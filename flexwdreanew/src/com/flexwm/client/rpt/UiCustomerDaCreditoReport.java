package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoTerritory;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;


public class UiCustomerDaCreditoReport extends UiReport {

	BmoCustomer bmoCustomer;
	UiListBox reportTypeListBox;
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiListBox typeListBox;
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox refeListBox;
	UiListBox territoryListBox;
	UiListBox maritalStatusListBox = new UiListBox(getUiParams(), new BmoMaritalStatus());
	UiSuggestBox sicSuggestBox = new UiSuggestBox(new BmoIndustry());
	UiListBox locationListBox = new UiListBox(getUiParams(), new BmoLocation());
	UiListBox statusListBox;

	// Fecha nacimiento
	UiDateBox birthdateStartDateBox = new UiDateBox();	
	UiDateBox birthdateEndDateBox = new UiDateBox();
	BmField birthdateEnd;
	UiListBox birthdateByMonthListBox = new UiListBox(getUiParams());
	BmField birthdateByMonth;
	
	BmoCreditGuarantee bmoCreditGuarantee = new BmoCreditGuarantee();

	String generalSection = "Filtros Generales";
	String dateSection = "Filtros Fechas";

	public UiCustomerDaCreditoReport(UiParams uiParams) {
		super(uiParams, new BmoCustomer(), "/rpt/cm/cust_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoCustomer()));
		this.bmoCustomer = (BmoCustomer)getBmObject();
		
		birthdateByMonth = new BmField("birthdateByMonth", "", "F. Nac. por Mes", 10, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		birthdateEnd = new BmField("birthdateEnd", "", "F. Nacimiento Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de Clientes", "/rpt/cm/cust_general_report_dacredito.jsp");
		reportTypeListBox.addItem("Reporte Clientes Detallado", "/rpt/cm/cust_detailed_report_dacredito.jsp");
		
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

			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				bmoCustomer.getLocationId().setValue(getUiParams().getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger());
				
				setCustomersListBoxFilters(bmoCustomer.getLocationId().toInteger());
				setUserListBoxFilters(bmoCustomer.getLocationId().toInteger());
			}
		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Tipo Cliente por default : " + e.toString());
		}

		int r = 0, c = 0;
		formFlexTable.addSectionLabel(r, c, generalSection, 2); r++;
		formFlexTable.addField(r, c, reportTypeListBox, "Tipo de Reporte"); r++;
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
			if (getSFParams().isFieldEnabled(bmoCustomer.getLocationId()))
				formFlexTable.addField(r, c, locationListBox, bmoCustomer.getLocationId()); r++;
		}
		
		formFlexTable.addField(r, c, customerSuggestBox, bmoCustomer.getIdField());	r++;
		formFlexTable.addField(r, c, typeListBox, bmoCustomer.getCustomertype()); r++;	r++;
		formFlexTable.addField(r, c, salesmanSuggestBox, bmoCustomer.getSalesmanId());	r++;
		if (getSFParams().isFieldEnabled(bmoCustomer.getReferralId())) {
			formFlexTable.addField(r, c, refeListBox, bmoCustomer.getReferralId());	r++;
		}
		
		if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId())) {
			formFlexTable.addField(r, c, territoryListBox, bmoCustomer.getTerritoryId());	r++;
		}
		
		if (getSFParams().isFieldEnabled(bmoCustomer.getMaritalStatusId())) {
			formFlexTable.addField(r, c, maritalStatusListBox, bmoCustomer.getMaritalStatusId());	r++;
		}
		
		if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId())) {
			formFlexTable.addField(r, c, sicSuggestBox, bmoCustomer.getIndustryId());	r++;
		}
		
		formFlexTable.addField(r, c, statusListBox, bmoCustomer.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		if (getSFParams().isFieldEnabled(bmoCustomer.getBirthdate())) {
			formFlexTable.addSectionLabel(r, c, dateSection, 2);	r++;
			formFlexTable.addField(r, c, birthdateByMonthListBox, birthdateByMonth);	r++;
			formFlexTable.addField(r, c, birthdateStartDateBox, bmoCustomer.getBirthdate());	r++;
			formFlexTable.addField(r, c, birthdateEndDateBox, birthdateEnd);	r++;
			formFlexTable.hideSection(dateSection);
		}

		statusEffect();
	}

	private void statusEffect() {
		if (!getSFParams().hasSpecialAccess(BmoCustomer.TYPECHANGE))
			typeListBox.setEnabled(false);
		
		
		// Validar si tiene permiso de propios, en la ubicacion
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()) {
			if (getSFParams().restrictData(new BmoLocation().getProgramCode())) {
				locationListBox.setEnabled(false);
			} else {
				locationListBox.setEnabled(true);
			}
		}
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		// Filtrar los clientes y vendedores por ubicacion
		if (event.getSource() == locationListBox) {
			BmoLocation bmoLocation = (BmoLocation)locationListBox.getSelectedBmObject();
			if (bmoLocation != null) {
				populateCustomers(bmoLocation.getId());
				populateUsers(bmoLocation.getId());
			} else {
				customerSuggestBox.clear();
				salesmanSuggestBox.clear();
			}
		}
		
		formValueChange("");
	}
	
	// Actualiza combo de tipos de credito por UBICACION
	private void populateCustomers(int locationId) {
		customerSuggestBox.clear();
		setCustomersListBoxFilters(locationId);
	}

	// Asigna filtros al listado de tipos de credito por UBICACION
	private void setCustomersListBoxFilters(int locationId) {
		BmoCustomer bmoCustomer = new BmoCustomer();
		if (locationId > 0) {
			BmFilter bmFilterByLocation = new BmFilter();
			bmFilterByLocation.setValueFilter(bmoCustomer.getKind(), bmoCustomer.getLocationId(), locationId);
			customerSuggestBox.addFilter(bmFilterByLocation);
		} else {
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoCustomer.getKind(), bmoCustomer.getLocationId(), "-1");
			customerSuggestBox.addFilter(bmFilter);
		}
	}
	
	// Actualiza combo de usuarios por UBICACION
	private void populateUsers(int locationId) {
		salesmanSuggestBox.clear();
		setUserListBoxFilters(locationId);
	}
	
	// Filtrar usuarios por perfil de vendedores 
	private void setUserListBoxFilters(int locationId) {
		int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();

		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		
		BmFilter filterSalesmen = new BmFilter();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + salesProfileId);	
		salesmanSuggestBox.addFilter(filterSalesmen);

		// Filtrar por vendedores activos
		BmFilter filterSalesmenActive = new BmFilter();
		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		salesmanSuggestBox.addFilter(filterSalesmenActive);
		
		// Filtrar por vendedores de la ubicacion
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
			BmFilter filterByLocation= new BmFilter();
			filterByLocation.setValueFilter(bmoUser.getKind(), bmoUser.getLocationId(), "" + locationId);
			salesmanSuggestBox.addFilter(filterByLocation);
		}
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
		
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
			if (getSFParams().isFieldEnabled(bmoCustomer.getLocationId()))
				addFilter(bmoCustomer.getLocationId(), locationListBox);
		}

		if (getSFParams().isFieldEnabled(bmoCustomer.getBirthdate())) {
			addFilter(birthdateByMonth, birthdateByMonthListBox);
			addFilter(bmoCustomer.getBirthdate(), birthdateStartDateBox.getTextBox().getText());
			addFilter(birthdateEnd, birthdateEndDateBox.getTextBox().getText());
		}
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
