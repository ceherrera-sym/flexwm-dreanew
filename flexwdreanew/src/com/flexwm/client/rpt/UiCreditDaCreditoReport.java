package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.chart.UiChart;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;


public class UiCreditDaCreditoReport extends UiReport {

	BmoCredit bmoCredit;
	BmoOrder bmoOrder = new BmoOrder();
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox ;
	UiDateBox creditStartDateBox = new UiDateBox();
	UiDateBox creditEndDateBox = new UiDateBox();
	UiListBox creditTypeListBox;
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox statusOrderListBox = new UiListBox(getUiParams());
	UiListBox locationListBox = new UiListBox(getUiParams(), new BmoLocation());
	UiTextBox amountStartTextBox = new UiTextBox();
	UiTextBox amountEndTextBox = new UiTextBox();
	UiListBox profileListBox;
	BmField profileId;
	BmField salesUserId;
	UiChart uiChart;

	BmField creditEndDateField;
	UiDateBox raccReceiveDateStartDateBox = new UiDateBox();
	BmField raccDueDateStart;
	UiDateBox raccReceiveDateEndDateBox = new UiDateBox();
	BmField raccDueDateEnd;
	
	BmField amountStartField;
	BmField amountEndField;

	String generalSection = "Filtros Generales";
	String statusSection = "Filtros de Estatus";
	String raccDueDateSection = "Fechas de Prog. solo aplica al Reporte 'Créditos Fallas'";

	public UiCreditDaCreditoReport(UiParams uiParams) {
		super(uiParams, new BmoCredit(), "/rpt/cr/cred_general_report_dacredito.jsp", uiParams.getSFParams().getProgramTitle(new BmoCredit()));		
		this.bmoCredit = (BmoCredit)getBmObject();		
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Créditos General", "/rpt/cr/cred_general_report_dacredito.jsp");
		reportTypeListBox.addItem("Crédito Debe", "/rpt/cr/cred_user_report_dacredito.jsp");
		reportTypeListBox.addItem("Créditos Fallas", "/rpt/cr/cred_failure_report_dacredito.jsp");
		reportTypeListBox.addItem("Hoja de Pagos", "/rpt/cr/cred_paysheet_report_dacredito.jsp");
		reportTypeListBox.addItem("Reporte Avales", "/rpt/cr/cust_creditguarantees_report_dacredito.jsp");

		profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		creditTypeListBox = new UiListBox(getUiParams(), new BmoCreditType());

		creditEndDateField = new BmField("cred_enddate", "", "Fecha Final", 10, Types.DATE, false, BmFieldType.DATE, false);
		profileId = new BmField("profileid", "", "Perfil", 10, Types.INTEGER, true, BmFieldType.ID, false);
		salesUserId = new BmField("salesUserId", "", "Usuario", 10, Types.INTEGER, true, BmFieldType.ID, false);
		raccDueDateStart = new BmField("racc_dueDateStart", "", "F. Prog. Inicio", 10, Types.DATE, true, BmFieldType.DATE, false);
		raccDueDateEnd = new BmField("racc_dueDateEnd", "", "F. Prog. Fin", 10, Types.DATE, true, BmFieldType.DATE, false);
		
		amountStartField = new BmField("cred_amountstart", "", "Rango Monto Inicio", 10, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amountEndField = new BmField("cred_amountend", "", "Rango Monto Fin", 10, Types.DOUBLE, true, BmFieldType.NUMBER, false);

		try {
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				bmoCredit.getLocationId().setValue(getUiParams().getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger());
				setCreditTypeListBoxFilters(bmoCredit.getLocationId().toInteger());
				setCustomersListBoxFilters(bmoCredit.getLocationId().toInteger());
				setUserListBoxFilters(bmoCredit.getLocationId().toInteger());
			}
		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar los datos por default : " + e.toString());
		}

		int r = 0, c = 0;
		formFlexTable.addSectionLabel(r, 0, generalSection, 2);	r++;
		formFlexTable.addField(r, 0, reportTypeListBox, "Tipo de Reporte");	r++;
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
			if (getSFParams().isFieldEnabled(bmoCredit.getLocationId()))
				formFlexTable.addField(r, c, locationListBox, bmoCredit.getLocationId());	r++;
		}
		
		formFlexTable.addField(r, c, creditTypeListBox, bmoCredit.getCreditTypeId(), true);	r++;
		formFlexTable.addField(r, c, customerSuggestBox, bmoCredit.getCustomerId());	r++;	
		formFlexTable.addField(r, c, salesmanSuggestBox, salesUserId);	r++;
		formFlexTable.addField(r, c, creditStartDateBox, bmoCredit.getStartDate());	r++;
		formFlexTable.addField(r, c, creditEndDateBox, creditEndDateField);	r++;
		formFlexTable.addField(r, c, profileListBox, profileId);	r++;
		profileListBox.setSelectedId(""+ ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger() );

		formFlexTable.addField(r, c, amountStartTextBox, amountStartField);	r++;
		formFlexTable.addField(r, c, amountEndTextBox, amountEndField);	r++;
		
		formFlexTable.addSectionLabel(r, c, statusSection, 2);	r++;
		formFlexTable.addField(r, c, statusListBox, bmoCredit.getStatus(), true);	r++;
		statusListBox.setSelectedIndex(-1);
		formFlexTable.addField(r, c, statusOrderListBox, bmoOrder.getStatus(), true);	r++;
		statusOrderListBox.setSelectedIndex(-1);
		// Se fuerza el label de la lista externa del modulo (no coincide la clave del modulo con la del bmField)
		//formFlexTable.addLabelField(r, c, bmoCredit.getProgramCode().toString(), bmoOrder.getStatus());	r++;
		
		formFlexTable.addSectionLabel(r, c, raccDueDateSection, 2);	r++;
		formFlexTable.addField(r, c, raccReceiveDateStartDateBox, raccDueDateStart);	r++;
		formFlexTable.addField(r, c, raccReceiveDateEndDateBox, raccDueDateEnd);	r++;

		formFlexTable.hideSection(statusSection);
		formFlexTable.hideSection(raccDueDateSection);
		formFlexTable.hideField(raccReceiveDateStartDateBox);
		formFlexTable.hideField(raccReceiveDateEndDateBox);
		statusEffect();
	}
	
	private void statusEffect() {
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
		if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode().equals("/rpt/cr/cred_failure_report_dacredito.jsp")) {
				formFlexTable.showField(raccReceiveDateStartDateBox);
				formFlexTable.showField(raccReceiveDateEndDateBox);
				formFlexTable.showSection(raccDueDateSection);
			} else {
				raccReceiveDateStartDateBox.getTextBox().setText("");
				raccReceiveDateEndDateBox.getTextBox().setText("");
				formFlexTable.hideField(raccReceiveDateStartDateBox);
				formFlexTable.hideField(raccReceiveDateEndDateBox);
				formFlexTable.hideSection(raccDueDateSection);
			}
			
		}
		// Filtrar los clientes y vendedores por ubicacion
		if (event.getSource() == locationListBox) {
			BmoLocation bmoLocation = (BmoLocation)locationListBox.getSelectedBmObject();
			if (bmoLocation != null) {
				populateCreditType(bmoLocation.getId());
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
	private void populateCreditType(int locationId) {
		creditTypeListBox.clear();
		creditTypeListBox.clearFilters();
		setCreditTypeListBoxFilters(locationId);
		creditTypeListBox.populate(bmoCredit.getCreditTypeId());
	}

	// Asigna filtros al listado de tipos de credito por UBICACION
	private void setCreditTypeListBoxFilters(int locationId) {
		BmoCreditType bmoCreditType = new BmoCreditType();

		if (locationId > 0) {
			BmFilter bmFilterByLocation = new BmFilter();
			bmFilterByLocation.setValueFilter(bmoCreditType.getKind(), bmoCreditType.getLocationId(), locationId);
			creditTypeListBox.addBmFilter(bmFilterByLocation);
		} else {
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoCreditType.getKind(), bmoCreditType.getIdField(), "-1");
			creditTypeListBox.addBmFilter(bmFilter);
		}
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
		addFilter(bmoCredit.getCreditTypeId(), creditTypeListBox);		
		addFilter(bmoCredit.getCustomerId(), customerSuggestBox);
		addFilter(salesUserId, salesmanSuggestBox);
		addFilter(bmoCredit.getStartDate(), creditStartDateBox.getTextBox().getText());				
		addFilter(creditEndDateField, creditEndDateBox.getTextBox().getText());
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
			addFilter(bmoCredit.getLocationId(), locationListBox);
		}
		addFilter(bmoCredit.getStatus(), statusListBox);
		addFilter(bmoOrder.getStatus(), statusOrderListBox);
		addFilter(profileId, profileListBox);
		addFilter(amountStartField, amountStartTextBox.getText());
		addFilter(amountEndField, amountEndTextBox.getText());
		
		addFilter(raccDueDateStart, raccReceiveDateStartDateBox.getTextBox().getText());
		addFilter(raccDueDateEnd, raccReceiveDateEndDateBox.getTextBox().getText());
	}
	
	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

