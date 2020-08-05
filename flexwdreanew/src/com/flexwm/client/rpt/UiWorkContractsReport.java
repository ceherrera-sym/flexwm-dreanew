package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoCompany;


public class UiWorkContractsReport extends UiReport {

	BmoWorkContract bmoWorkContract;
	UiSuggestBox workIdUiSuggestBox = new UiSuggestBox(new BmoWork());
	UiListBox companyIdUiListBox;
	UiSuggestBox supplierIdUiSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiDateBox startDateDateBox = new UiDateBox();
	UiDateBox endDateDateBox = new UiDateBox();
	UiSuggestBox budgetItemIdUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiDateBox startDatePaymentDateDateBox = new UiDateBox();
	UiDateBox endDatePaymentDateDateBox = new UiDateBox();
	BmField endDatePayment;
	UiDateBox startDateContracDateDateBox = new UiDateBox();
	UiDateBox endDateContractDateDateBox = new UiDateBox();
	BmField endDateContract;
	UiListBox statusUiListBox = new UiListBox(getUiParams());
	UiListBox statusPaymentUiListBox = new UiListBox(getUiParams());
	UiSuggestBox developmentPhaseIdUiSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiSuggestBox budgetIdUiSuggestBox = new UiSuggestBox(new BmoBudget());	

	UiListBox reportTypeListBox;
	BmField budgetId;

	String generalSection = "Filtros Generales";

	public UiWorkContractsReport(UiParams uiParams) {
		super(uiParams, new BmoWorkContract(), "/rpt/co/woco_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWorkContract()));
		this.bmoWorkContract = (BmoWorkContract)getBmObject();

		endDatePayment = new BmField("endDatePayment", "", "F. Pago Anticipo Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		endDateContract = new BmField("endDateContract", "", "F. Contrato Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		budgetId = new BmField("budgetId", "", "Presupuesto", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Contratos de Obra General", "/rpt/co/woco_general_report.jsp");	
		reportTypeListBox.addItem("Contratos de Obra Detallado", "/rpt/co/woco_detail_report.jsp");

		companyIdUiListBox = new UiListBox(getUiParams(), new BmoCompany());

		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoWorkContract.getCompanyId().setValue(getSFParams().getSelectedCompanyId());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, workIdUiSuggestBox, bmoWorkContract.getWorkId());
		formFlexTable.addField(3, 0, companyIdUiListBox, bmoWorkContract.getCompanyId());
		formFlexTable.addField(4, 0, supplierIdUiSuggestBox, bmoWorkContract.getSupplierId());
		formFlexTable.addField(5, 0, developmentPhaseIdUiSuggestBox, bmoWorkContract.getBmoWork().getDevelopmentPhaseId());		
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			formFlexTable.addField(6, 0, budgetIdUiSuggestBox, budgetId);
			formFlexTable.addField(7, 0, budgetItemIdUiSuggestBox, bmoWorkContract.getBudgetItemId());
		}
		formFlexTable.addField(8, 0, startDateDateBox, bmoWorkContract.getStartDate());
		formFlexTable.addField(9, 0, endDateDateBox, bmoWorkContract.getEndDate());
		formFlexTable.addField(10, 0, startDatePaymentDateDateBox, bmoWorkContract.getPaymentDate());
		formFlexTable.addField(11, 0, endDatePaymentDateDateBox, endDatePayment);
		formFlexTable.addField(12, 0, startDateContracDateDateBox, bmoWorkContract.getDateContract());
		formFlexTable.addField(13, 0, endDateContractDateDateBox, endDateContract);
		formFlexTable.addField(14, 0, statusUiListBox, bmoWorkContract.getStatus(), true);
		statusUiListBox.setSelectedIndex(-1);
		formFlexTable.addField(15, 0, statusPaymentUiListBox, bmoWorkContract.getPaymentStatus(), true);
		statusPaymentUiListBox.setSelectedIndex(-1);

		statusEffect();
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoWorkContract.getWorkId(), workIdUiSuggestBox);
		addFilter(bmoWorkContract.getCompanyId(), companyIdUiListBox);
		addFilter(bmoWorkContract.getSupplierId(), supplierIdUiSuggestBox);
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			addFilter(bmoWorkContract.getBudgetItemId(), budgetItemIdUiSuggestBox);
			addFilter(budgetId, budgetIdUiSuggestBox);
		}
		addFilter(bmoWorkContract.getStartDate(), startDateDateBox.getTextBox().getText());
		addFilter(bmoWorkContract.getEndDate(), endDateDateBox.getTextBox().getText());
		addFilter(bmoWorkContract.getPaymentDate(), startDatePaymentDateDateBox.getTextBox().getText());
		addFilter(endDatePayment, endDatePaymentDateDateBox.getTextBox().getText());
		addFilter(bmoWorkContract.getDateContract(), startDateContracDateDateBox.getTextBox().getText());
		addFilter(endDateContract, endDateContractDateDateBox.getTextBox().getText());
		addFilter(bmoWorkContract.getStatus(), statusUiListBox);
		addFilter(bmoWorkContract.getPaymentStatus(), statusPaymentUiListBox);
		addFilter(bmoWorkContract.getBmoWork().getDevelopmentPhaseId(), developmentPhaseIdUiSuggestBox);
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

