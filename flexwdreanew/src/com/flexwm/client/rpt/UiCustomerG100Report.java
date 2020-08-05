package com.flexwm.client.rpt;



import java.sql.Types;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoRegion;
import com.flexwm.shared.cm.BmoTerritory;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;


public class UiCustomerG100Report extends UiReport {

	BmoCustomer bmoCustomer;
	UiListBox reportTypeListBox;
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiListBox typeListBox;
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox refeListBox;
	UiListBox territoryListBox;
	UiListBox maritalStatusListBox = new UiListBox(getUiParams(),new BmoMaritalStatus());
	UiSuggestBox sicSuggestBox = new UiSuggestBox(new BmoIndustry());
	UiSuggestBox regionSuggestBox = new UiSuggestBox(new BmoRegion());
	UiListBox statusListBox;

	// Fecha nacimiento
	UiDateBox birthdateStartDateBox = new UiDateBox();	
	UiDateBox birthdateEndDateBox = new UiDateBox();
	BmField birthdateEnd;
	UiListBox birthdateByMonthListBox = new UiListBox(getUiParams());
	BmField birthdateByMonth;
	
	UiDateBox dateCreateDateBox = new UiDateBox();
	UiDateBox dateCreateEndDateBox = new UiDateBox();
	BmField dateCreateEnd;
	
	BmoCreditGuarantee bmoCreditGuarantee = new BmoCreditGuarantee();

	String generalSection = "Filtros Generales";
	String dateSection = "Filtros Fechas";

	public UiCustomerG100Report(UiParams uiParams) {
		super(uiParams, new BmoCustomer(), "/rpt/cm/cust_general_report_g100.jsp", uiParams.getSFParams().getProgramTitle(new BmoCustomer()));
		this.bmoCustomer = (BmoCustomer)getBmObject();
		
		birthdateByMonth = new BmField("birthdateByMonth", "", "Fecha Nac. por Mes", 10, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		birthdateEnd = new BmField("birthdateEnd", "", "Fin Fecha Nac.", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		dateCreateEnd = new BmField("dateCreateEnd", "", "Fin Fecha Creación ", 10, Types.VARCHAR, true, BmFieldType.DATE, false);

	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de Clientes", "/rpt/cm/cust_general_report_g100.jsp");
		reportTypeListBox.addItem("Reporte Clientes Detallado", "/rpt/cm/cust_detailed_report.jsp");
//		reportTypeListBox.addItem("Estatus de Pedidos del Cliente", "/rpt/cm/cust_activeordertypes_report_g100.jsp");
		
		typeListBox = new UiListBox(getUiParams());
		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		territoryListBox = new UiListBox(getUiParams(), new BmoTerritory());
		statusListBox = new UiListBox(getUiParams());

		birthdateByMonthListBox.addItem("No", "0"); 
		birthdateByMonthListBox.addItem("Si", "1");

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
		formFlexTable.addField(2, 0, customerSuggestBox, bmoCustomer.getIdField());
		formFlexTable.addField(3, 0, typeListBox, bmoCustomer.getCustomertype());
		formFlexTable.addField(4, 0, salesmanSuggestBox, bmoCustomer.getSalesmanId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getReferralId()))
			formFlexTable.addField(5, 0, refeListBox, bmoCustomer.getReferralId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
			formFlexTable.addField(6, 0, territoryListBox, bmoCustomer.getTerritoryId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getMaritalStatusId()))
			formFlexTable.addField(7, 0, maritalStatusListBox, bmoCustomer.getMaritalStatusId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
			formFlexTable.addField(8, 0, sicSuggestBox, bmoCustomer.getIndustryId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getRegionId()))
			formFlexTable.addField(9, 0, regionSuggestBox, bmoCustomer.getRegionId());
		formFlexTable.addField(10, 0, statusListBox, bmoCustomer.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

			formFlexTable.addSectionLabel(11, 0, dateSection, 2);
			 bmoCustomer.getDateCreate().setLabel("Inicio Fecha Creación");
			formFlexTable.addField(12, 0, dateCreateDateBox, bmoCustomer.getDateCreate());
			formFlexTable.addField(13, 0, dateCreateEndDateBox, dateCreateEnd);
			if (getSFParams().isFieldEnabled(bmoCustomer.getBirthdate())) {
				formFlexTable.addField(14, 0, birthdateByMonthListBox, birthdateByMonth);
				 bmoCustomer.getBirthdate().setLabel("Inicio Fecha Nac.");
				formFlexTable.addField(15, 0, birthdateStartDateBox, bmoCustomer.getBirthdate());
				formFlexTable.addField(16, 0, birthdateEndDateBox, birthdateEnd);
			}

			formFlexTable.hideSection(dateSection);

		statusEffect();
	}

	private void statusEffect() {
		if (!getSFParams().hasSpecialAccess(BmoCustomer.TYPECHANGE))
			typeListBox.setEnabled(false);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoCustomer.getIdField(), customerSuggestBox);
		addFilter(bmoCustomer.getCustomertype(), typeListBox);
		addFilter(bmoCustomer.getSalesmanId(), salesmanSuggestBox);

		// OJO: se pone validacion porque si se deshabilita el campo(desde detalle de modulo), NO muestra el reporte
		if (getSFParams().isFieldEnabled(bmoCustomer.getReferralId()))
			addFilter(bmoCustomer.getReferralId(), refeListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
			addFilter(bmoCustomer.getTerritoryId(), territoryListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getMaritalStatusId()))
			addFilter(bmoCustomer.getMaritalStatusId(), maritalStatusListBox);
		addFilter(bmoCustomer.getStatus(), statusListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
			addFilter(bmoCustomer.getIndustryId(), sicSuggestBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getRegionId()))
			addFilter(bmoCustomer.getRegionId(), regionSuggestBox);

		if (getSFParams().isFieldEnabled(bmoCustomer.getBirthdate())) {
			addFilter(birthdateByMonth, birthdateByMonthListBox);
			addFilter(bmoCustomer.getBirthdate(), birthdateStartDateBox.getTextBox().getText());
			addFilter(birthdateEnd, birthdateEndDateBox.getTextBox().getText());
		}

		addFilter(bmoCustomer.getDateCreate(), dateCreateDateBox.getTextBox().getText());
		addFilter(dateCreateEnd, dateCreateEndDateBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
