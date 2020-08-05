package com.flexwm.client.rpt;

import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;


public class UiDevelopmentPhaseReport extends UiReport {

	BmoDevelopmentPhase bmoDevelopmentPhase;
	UiSuggestBox developmentPhaseSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiListBox reportTypeListBox;
	String generalSection = "Filtros Generales";

	public UiDevelopmentPhaseReport(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentPhase(), "/rpt/fi/flex_budget_bydevephase_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoDevelopmentPhase()));
		this.bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());		
		reportTypeListBox.addItem("Reporte de Etapa", "/rpt/fi/flex_budget_bydevephase_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, developmentPhaseSuggestBox, bmoDevelopmentPhase.getIdField());
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoDevelopmentPhase.getIdField(), developmentPhaseSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
