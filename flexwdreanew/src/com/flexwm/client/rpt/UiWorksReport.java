package com.flexwm.client.rpt;

import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class UiWorksReport extends UiReport {

	BmoWork bmoWork;
	UiListBox developmentPhaseIdUiListBox;
	UiSuggestBox userIdUiSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox budgetItemIdUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiListBox companyIdUiListBox;
	UiListBox isMasterUiListBox;
	UiListBox statusUiListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public UiWorksReport(UiParams uiParams) {
		super(uiParams, new BmoWork(), "/rpt/co/work_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWork()));
		this.bmoWork = (BmoWork)getBmObject();				
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Obras General", "/rpt/co/work_general_report.jsp");	

		developmentPhaseIdUiListBox = new UiListBox(getUiParams(), new BmoDevelopmentPhase());
		companyIdUiListBox = new UiListBox(getUiParams(), new BmoCompany());
		isMasterUiListBox = new UiListBox(getUiParams());
		isMasterUiListBox.addItem("Todos", "-1");
		isMasterUiListBox.addItem("Si", "1");
		isMasterUiListBox.addItem("No", "0");

		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoWork.getCompanyId().setValue(getSFParams().getSelectedCompanyId());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, developmentPhaseIdUiListBox, bmoWork.getDevelopmentPhaseId());
		formFlexTable.addField(3, 0, userIdUiSuggestBox, bmoWork.getUserId());
		formFlexTable.addField(4, 0, budgetItemIdUiSuggestBox, bmoWork.getBudgetItemId());
		formFlexTable.addField(5, 0, companyIdUiListBox, bmoWork.getCompanyId());
		formFlexTable.addField(6, 0, isMasterUiListBox, bmoWork.getIsMaster());
		formFlexTable.addField(7, 0, statusUiListBox, bmoWork.getStatus(), true);
		statusUiListBox.setSelectedIndex(-1);

		statusEffect();
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoWork.getDevelopmentPhaseId(), developmentPhaseIdUiListBox);
		addFilter(bmoWork.getUserId(), userIdUiSuggestBox);
		addFilter(bmoWork.getBudgetItemId(), budgetItemIdUiSuggestBox);
		addFilter(bmoWork.getCompanyId(), companyIdUiListBox);
		addFilter(bmoWork.getIsMaster(), isMasterUiListBox);
		addFilter(bmoWork.getStatus(), statusUiListBox);
	}

	private void statusEffect() {
		if (getSFParams().getSelectedCompanyId() > 0)
			companyIdUiListBox.setEnabled(false);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

