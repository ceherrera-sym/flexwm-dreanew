package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.cm.BmoCustomer;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;


public class UiSessionReport extends UiReport {

	BmoSession bmoSession;
	UiListBox reportTypeListBox;	
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiListBox sessionTypeListBox;
	BmField customerField;

	String generalSection = "Filtros Generales";

	public UiSessionReport(UiParams uiParams) {
		super(uiParams, new BmoSession(), "/rpt/ac/sess_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoSession()));
		this.bmoSession = (BmoSession)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte de Sesiones General", "/rpt/ac/sess_general_report.jsp");
		reportTypeListBox.addItem("Reporte de Sesiones Prueba", "/rpt/ac/sess_prospects_report.jsp");
		reportTypeListBox.addItem("Reporte de Asistencias", "/rpt/ac/sess_attented_report.jsp");
		reportTypeListBox.addItem("Reporte Semanal", "/frm/sess_weekly_report.jsp");

		sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
		customerField = new BmField("customerid", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, salesmanSuggestBox, bmoSession.getUserId());
		formFlexTable.addField(3, 0, sessionTypeListBox, bmoSession.getSessionTypeId());
		formFlexTable.addField(4, 0, customersSuggestBox, customerField);
		formFlexTable.addField(5, 0, startDateBox, bmoSession.getStartDate());
		formFlexTable.addField(6, 0, endDateBox, bmoSession.getEndDate());
		
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);		
		addFilter(customerField, customersSuggestBox);
		addFilter(bmoSession.getUserId(), salesmanSuggestBox);	
		addFilter(bmoSession.getSessionTypeId(), sessionTypeListBox);
		addFilter(bmoSession.getStartDate(), startDateBox.getTextBox().getText());
		addFilter(bmoSession.getEndDate(), endDateBox.getTextBox().getText());		
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}	
}
