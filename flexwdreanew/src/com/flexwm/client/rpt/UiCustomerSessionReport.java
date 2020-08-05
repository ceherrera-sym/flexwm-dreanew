package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoTerritory;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;


public class UiCustomerSessionReport extends UiReport {

	BmoCustomer bmoCustomer;
	UiListBox reportTypeListBox;
	UiDateBox startDateBox = new UiDateBox();	
	UiDateBox endDateBox = new UiDateBox();
	UiListBox typeListBox;
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox refeListBox;
	UiListBox territoryListBox;
	UiListBox maritalStatusListBox = new UiListBox(getUiParams(),new BmoMaritalStatus());

	UiSuggestBox sicSuggestBox = new UiSuggestBox(new BmoIndustry());
	UiListBox statusListBox = new UiListBox(getUiParams());
	
	UiDateBox birthdateStartDateBox = new UiDateBox();	
	UiDateBox birthdateEndDateBox = new UiDateBox();
	BmField birthdateEnd;
	//BmField birthdayStart;
	
	//BmField startDateField;
	BmField dateCreateEndField;
	
	UiListBox birthdateByMonthListBox = new UiListBox(getUiParams());
	BmField birthdateByMonth;
	
	String generalSection = "Filtros Generales";
	String dateSection = "Filtros de Fechas";

	public UiCustomerSessionReport(UiParams uiParams) {
		super(uiParams, new BmoCustomer(), "/rpt/cm/cust_session_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoCustomer()));
		this.bmoCustomer = (BmoCustomer)getBmObject();

		//Field de fechas finales
		//startDateField = new BmField("startdate", "", "Fecha Inicial", 10, Types.DATE, false, BmFieldType.DATE, false);
		dateCreateEndField = new BmField("dateCreateEnd", "", "Fecha Creación Final", 10, Types.DATE, false, BmFieldType.DATE, false);
	
		birthdateByMonth = new BmField("birthdateByMonth", "", "F. Nac. por Mes", 10, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		birthdateEnd = new BmField("birthdateEnd", "", "F. Nacimiento Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reportes de Clientes", "/rpt/cm/cust_session_report.jsp");
		reportTypeListBox.addItem("Reporte Cumpleaños Clientes", "/rpt/cm/cust_birthday_report.jsp");
		reportTypeListBox.addItem("Reportes Clientes Detallado", "/rpt/cm/cust_detailed_report.jsp");

		birthdateByMonthListBox.addItem("No", "0"); 
		birthdateByMonthListBox.addItem("Si", "1");
		
		typeListBox = new UiListBox(getUiParams());
		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		territoryListBox = new UiListBox(getUiParams(), new BmoTerritory());
		
		// Tipo de cliente
		try {
			// Si no esta asignado el tipo, buscar por el default
			if (!(bmoCustomer.getCustomertype().toInteger() > 0))
				bmoCustomer.getCustomertype().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultTypeCustomer().toString());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Tipo Cliente por default : " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, typeListBox, bmoCustomer.getCustomertype());
		formFlexTable.addField(3, 0, salesmanSuggestBox, bmoCustomer.getSalesmanId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getReferralId()))
			formFlexTable.addField(4, 0, refeListBox, bmoCustomer.getReferralId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
			formFlexTable.addField(5, 0, territoryListBox, bmoCustomer.getTerritoryId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getMaritalStatusId()))
			formFlexTable.addField(6, 0, maritalStatusListBox, bmoCustomer.getMaritalStatusId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
			formFlexTable.addField(7, 0, sicSuggestBox, bmoCustomer.getIndustryId());
		formFlexTable.addField(8, 0, statusListBox, bmoCustomer.getStatus(), true);		
		statusListBox.setSelectedIndex(-1);
		
		formFlexTable.addSectionLabel(9, 0, dateSection, 2);
		formFlexTable.addField(10, 0, startDateBox, bmoCustomer.getDateCreate());
		formFlexTable.addField(11, 0, endDateBox, dateCreateEndField);
		formFlexTable.addField(12, 0, birthdateByMonthListBox, birthdateByMonth);
		formFlexTable.addField(13, 0, birthdateStartDateBox, bmoCustomer.getBirthdate());
		formFlexTable.addField(14, 0, birthdateEndDateBox, birthdateEnd);
		formFlexTable.hideSection(dateSection);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoCustomer.getCustomertype(), typeListBox);
		addFilter(bmoCustomer.getSalesmanId(), salesmanSuggestBox);
		addFilter(bmoCustomer.getReferralId(), refeListBox);
		// OJO: se pone validacion porque si se deshabilita el campo(desde detalle de modulo), NO muestra el reporte	
		if (getSFParams().isFieldEnabled(bmoCustomer.getReferralId()))
			addFilter(bmoCustomer.getReferralId(), refeListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
			addFilter(bmoCustomer.getTerritoryId(), territoryListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getMaritalStatusId()))
			addFilter(bmoCustomer.getMaritalStatusId(), maritalStatusListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
			addFilter(bmoCustomer.getIndustryId(), sicSuggestBox);

		addFilter(bmoCustomer.getStatus(), statusListBox);
		addFilter(bmoCustomer.getDateCreate(), startDateBox.getTextBox().getText());
		addFilter(dateCreateEndField, endDateBox.getTextBox().getText());
		addFilter(bmoCustomer.getBirthdate(), birthdateStartDateBox.getTextBox().getText());
		addFilter(birthdateByMonth, birthdateByMonthListBox);
		addFilter(birthdateEnd, birthdateEndDateBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
