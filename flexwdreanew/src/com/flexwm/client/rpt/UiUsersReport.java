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
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.sf.BmoUser;


public class UiUsersReport extends UiReport {

	BmoUser bmoUser;
	UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());
	UiListBox locationListBox = new UiListBox(getUiParams(), new BmoLocation());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiSuggestBox parentSuggestBox = new UiSuggestBox(new BmoUser());
	UiDateBox birthdateDateBox = new UiDateBox();
	UiDateBox birthdateEndDateBox = new UiDateBox();
	BmField birthdateEnd;
	UiSuggestBox programSuggestBox = new UiSuggestBox(new BmoProgram());
	UiListBox birthdateByMonthListBox = new UiListBox(getUiParams());
	BmField birthdateByMonth;
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public UiUsersReport(UiParams uiParams) {
		super(uiParams, new BmoUser(), "/rpt/cv/user_users_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoUser()));
		this.bmoUser = (BmoUser)getBmObject();

		birthdateEnd = new BmField("birthdateEnd", "", "F. Nacimiento Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		birthdateByMonth = new BmField("birthdateByMonth", "", "F. Nac. por Mes", 10, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
	}

	@Override
	public void populateFields(){

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte de Usuarios", "/rpt/cv/user_users_report.jsp");
		reportTypeListBox.addItem("Reporte de Usuarios Detallado", "/rpt/cv/user_detailed_report.jsp");

		birthdateByMonthListBox.addItem("No", "0"); 
		birthdateByMonthListBox.addItem("Si", "1"); 

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, areaListBox, bmoUser.getAreaId(), true);
		//areaListBox.setSelectedIndex(-1);
		formFlexTable.addField(3, 0, locationListBox, bmoUser.getLocationId(), true);
		//locationListBox.setSelectedIndex(-1);
		formFlexTable.addField(4, 0, companyListBox, bmoUser.getCompanyId());
		formFlexTable.addField(5, 0, parentSuggestBox, bmoUser.getParentId());
		formFlexTable.addField(6, 0, programSuggestBox, bmoUser.getStartProgramId());
		formFlexTable.addField(7, 0, birthdateByMonthListBox, birthdateByMonth);
		formFlexTable.addField(8, 0, birthdateDateBox, bmoUser.getBirthdate());		
		formFlexTable.addField(9, 0, birthdateEndDateBox, birthdateEnd);
		formFlexTable.addField(10, 0, statusListBox, bmoUser.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoUser.getAreaId(), areaListBox);
		addFilter(bmoUser.getLocationId(), locationListBox);
		addFilter(bmoUser.getCompanyId(), companyListBox);
		addFilter(bmoUser.getParentId(), parentSuggestBox);
		addFilter(bmoUser.getBirthdate(), birthdateDateBox.getTextBox().getText());
		addFilter(birthdateEnd, birthdateEndDateBox.getTextBox().getText());
		addFilter(bmoUser.getStartProgramId(), programSuggestBox);
		addFilter(bmoUser.getStatus(), statusListBox);
		addFilter(birthdateByMonth, birthdateByMonthListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
