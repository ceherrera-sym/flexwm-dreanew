package com.flexwm.client.rpt;

import java.sql.Types;

import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoUserTimeClock;


public class UiUserTimeClockReport extends UiReport {

	BmoUserTimeClock bmoUserTimeClock;
	BmoUser bmoUser = new BmoUser();
	UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	BmField endDate;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public UiUserTimeClockReport(UiParams uiParams) {
		super(uiParams, new BmoUserTimeClock(), "/rpt/cv/ustc_usertimeclock_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoUserTimeClock()));
		this.bmoUserTimeClock = (BmoUserTimeClock)getBmObject();

		endDate = new BmField("enddate", "", "Fecha Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte Reloj Checador", "/rpt/cv/ustc_usertimeclock_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, userSuggestBox, bmoUserTimeClock.getUserId());
		formFlexTable.addField(3, 0, areaListBox, bmoUser.getAreaId());
		formFlexTable.addField(4, 0, startDateBox, bmoUserTimeClock.getDateTime());		
		formFlexTable.addField(5, 0, endDateBox, endDate);
		formFlexTable.addField(6, 0, statusListBox, bmoUser.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		statusListBox.setItemSelected(1, true);

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoUserTimeClock.getUserId(), userSuggestBox);
		addFilter(bmoUser.getAreaId(), areaListBox);
		addFilter(bmoUserTimeClock.getDateTime(), startDateBox.getTextBox().getText());
		addFilter(endDate, endDateBox.getTextBox().getText());
		addFilter(bmoUser.getStatus(), statusListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

