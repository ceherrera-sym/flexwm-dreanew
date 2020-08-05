package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;


public class UiDevelopmentBlocksReport extends UiReport {

	BmoDevelopmentBlock bmoDevelopmentBlock;
	UiSuggestBox developmentBlockUiSuggestBox = new UiSuggestBox(new BmoDevelopmentBlock());
	UiSuggestBox developmentPhaseIdUiSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiSuggestBox developmentUiSuggestBox = new UiSuggestBox(new BmoDevelopment());
	UiDateBox startDateDateBox = new UiDateBox();
	UiDateBox endDateDateBox = new UiDateBox();
	UiDateBox readyDateStartDateBox = new UiDateBox();
	UiDateBox readyDateEndDateBox = new UiDateBox();
	BmField readyDateEnd;

	UiListBox statusProcessUiListBox;
	UiListBox isOpenUiListBox;
	UiListBox isTemporallyUiListBox;
	UiListBox processUiListBox;
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public UiDevelopmentBlocksReport(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentBlock(), "/rpt/co/dvbl_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoDevelopmentBlock()));
		this.bmoDevelopmentBlock = (BmoDevelopmentBlock)getBmObject();	

		readyDateEnd = new BmField("readyDateEnd", "", "Fecha Term. Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Manzana/Torre General", "/rpt/co/dvbl_general_report.jsp");
		reportTypeListBox.addItem("Por Etapa", "/rpt/co/dvbl_bydevelopmentphase_report.jsp");	

		statusProcessUiListBox = new UiListBox(getUiParams());
		isOpenUiListBox = new UiListBox(getUiParams());
		isTemporallyUiListBox = new UiListBox(getUiParams());
		processUiListBox = new UiListBox(getUiParams());

		statusProcessUiListBox.addItem("Todos", "2");
		statusProcessUiListBox.addItem("Si", "1");
		statusProcessUiListBox.addItem("No", "0");

		isOpenUiListBox.addItem("Todos", "2");
		isOpenUiListBox.addItem("Si", "1");
		isOpenUiListBox.addItem("No", "0");

		isTemporallyUiListBox.addItem("Todos", "2");
		isTemporallyUiListBox.addItem("Si", "1");
		isTemporallyUiListBox.addItem("No", "0");

		processUiListBox.addItem("Todos", "3");
		processUiListBox.addItem("0", "0");
		processUiListBox.addItem("<100", "1");
		processUiListBox.addItem("100", "2");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, developmentUiSuggestBox, bmoDevelopmentBlock.getBmoDevelopmentPhase().getDevelopmentId());
		formFlexTable.addField(3, 0, developmentPhaseIdUiSuggestBox, bmoDevelopmentBlock.getDevelopmentPhaseId());
		formFlexTable.addField(4, 0, developmentBlockUiSuggestBox, bmoDevelopmentBlock.getIdField());
		formFlexTable.addField(5, 0, statusProcessUiListBox, bmoDevelopmentBlock.getStatusProcess());
		formFlexTable.addField(6, 0, isOpenUiListBox, bmoDevelopmentBlock.getIsOpen());
		formFlexTable.addField(7, 0, isTemporallyUiListBox, bmoDevelopmentBlock.getIsTemporally());
		formFlexTable.addField(8, 0, startDateDateBox, bmoDevelopmentBlock.getStartDate());
		formFlexTable.addField(9, 0, endDateDateBox, bmoDevelopmentBlock.getEndDate());
		formFlexTable.addField(10, 0, readyDateStartDateBox, bmoDevelopmentBlock.getReadyDate());
		formFlexTable.addField(11, 0, readyDateEndDateBox, readyDateEnd);
		formFlexTable.addField(12, 0, processUiListBox, bmoDevelopmentBlock.getProcessPercentage());

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoDevelopmentBlock.getIdField(), developmentBlockUiSuggestBox);
		addFilter(bmoDevelopmentBlock.getBmoDevelopmentPhase().getDevelopmentId(), developmentUiSuggestBox);
		addFilter(bmoDevelopmentBlock.getDevelopmentPhaseId(), developmentPhaseIdUiSuggestBox);
		addFilter(bmoDevelopmentBlock.getStartDate(), startDateDateBox.getTextBox().getText());
		addFilter(bmoDevelopmentBlock.getEndDate(), endDateDateBox.getTextBox().getText());
		addFilter(bmoDevelopmentBlock.getReadyDate(), readyDateStartDateBox.getTextBox().getText());
		addFilter(readyDateEnd, readyDateEndDateBox.getTextBox().getText());
		addFilter(bmoDevelopmentBlock.getStatusProcess(), statusProcessUiListBox);
		addFilter(bmoDevelopmentBlock.getIsOpen(), isOpenUiListBox);
		addFilter(bmoDevelopmentBlock.getIsTemporally(), isTemporallyUiListBox);
		addFilter(bmoDevelopmentBlock.getProcessPercentage(), processUiListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
