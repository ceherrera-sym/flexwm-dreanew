package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoRegion;
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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;


public class UiCreditReport extends UiReport {

	BmoCredit bmoCredit;
	BmoOrder bmoOrder = new BmoOrder();
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox ;
	UiDateBox creditStartDateBox = new UiDateBox();
	UiDateBox creditEndDateBox = new UiDateBox();
	UiListBox creditTypeListBox;
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox regionSuggestBox = new UiSuggestBox(new BmoRegion());
	UiListBox statusOrderListBox = new UiListBox(getUiParams());
	UiListBox profileListBox;
	BmField profileId;
	BmField salesUserId;
	UiChart uiChart;

	BmField creditEndDateField;
	UiDateBox raccReceiveDateStartDateBox = new UiDateBox();
	BmField raccDueDateStart;
	UiDateBox raccReceiveDateEndDateBox = new UiDateBox();
	BmField raccDueDateEnd;

	String generalSection = "Filtros Generales";
	String statusSection = "Filtros de Estatus";
	String raccDueDateSection = "Fechas de Prog. solo aplica al Reporte 'Créditos Fallas'";

	public UiCreditReport(UiParams uiParams) {
		super(uiParams, new BmoCredit(), "/rpt/cr/cred_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoCredit()));		
		this.bmoCredit = (BmoCredit)getBmObject();		
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Créditos General", "/rpt/cr/cred_general_report.jsp");
		reportTypeListBox.addItem("Crédito Debe", "/rpt/cr/cred_user_report.jsp");
		reportTypeListBox.addItem("Créditos Fallas", "/rpt/cr/cred_failure_report.jsp");
		reportTypeListBox.addItem("Hoja de Pagos", "/rpt/cr/cred_paysheet_report.jsp");
		reportTypeListBox.addItem("Reporte Avales", "/rpt/cr/cust_creditguarantees_report.jsp");

		profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		creditTypeListBox = new UiListBox(getUiParams(), new BmoCreditType());

		creditEndDateField = new BmField("cred_enddate", "", "Fecha Final", 10, Types.DATE, false, BmFieldType.DATE, false);
		profileId = new BmField("profileid", "", "Perfil", 10, Types.INTEGER, true, BmFieldType.ID, false);
		salesUserId = new BmField("salesUserId", "", "Usuario", 10, Types.INTEGER, true, BmFieldType.ID, false);
		raccDueDateStart = new BmField("racc_dueDateStart", "", "F. Prog. Inicio", 10, Types.DATE, true, BmFieldType.DATE, false);
		raccDueDateEnd = new BmField("racc_dueDateEnd", "", "F. Prog. Fin", 10, Types.DATE, true, BmFieldType.DATE, false);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, creditTypeListBox, bmoCredit.getCreditTypeId(),true);
		formFlexTable.addField(3, 0, customerSuggestBox, bmoCredit.getCustomerId());		
		formFlexTable.addField(4, 0, salesmanSuggestBox, salesUserId);
		formFlexTable.addField(5, 0, regionSuggestBox, bmoCredit.getBmoCustomer().getRegionId());
		formFlexTable.addField(6, 0, creditStartDateBox, bmoCredit.getStartDate());
		formFlexTable.addField(7, 0, creditEndDateBox, creditEndDateField);
		formFlexTable.addField(8, 0, profileListBox, profileId);
		profileListBox.setSelectedId(""+ ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger() );

		formFlexTable.addSectionLabel(9, 0, statusSection, 2);
		formFlexTable.addField(10, 0, statusListBox, bmoCredit.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		formFlexTable.addField(11, 0, statusOrderListBox, bmoOrder.getStatus(), true);
		statusOrderListBox.setSelectedIndex(-1);
		// Se fuerza el label de la lista externa del modulo (no coincide la clave del modulo con la del bmField)
		//formFlexTable.addLabelField(11, 0, bmoCredit.getProgramCode().toString(), bmoOrder.getStatus());
		
		formFlexTable.addSectionLabel(12, 0, raccDueDateSection, 2);
	
		formFlexTable.addField(13, 0, raccReceiveDateStartDateBox, raccDueDateStart);
		formFlexTable.addField(14, 0, raccReceiveDateEndDateBox, raccDueDateEnd);

		formFlexTable.hideSection(statusSection);
		formFlexTable.hideSection(raccDueDateSection);
		

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoCredit.getCreditTypeId(), creditTypeListBox);		
		addFilter(bmoCredit.getCustomerId(), customerSuggestBox);
		addFilter(salesUserId, salesmanSuggestBox);
		addFilter(bmoCredit.getStartDate(), creditStartDateBox.getTextBox().getText());				
		addFilter(creditEndDateField, creditEndDateBox.getTextBox().getText());
		addFilter(bmoCredit.getBmoCustomer().getRegionId(), regionSuggestBox);
		addFilter(bmoCredit.getStatus(), statusListBox);
		addFilter(bmoOrder.getStatus(), statusOrderListBox);
		addFilter(profileId, profileListBox);
		addFilter(raccDueDateStart, raccReceiveDateStartDateBox.getTextBox().getText());
		addFilter(raccDueDateEnd, raccReceiveDateEndDateBox.getTextBox().getText());
	}

	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode().equals("/rpt/cr/cust_creditguarantees_report.jsp")){
				formFlexTable.hideField(profileId);				
			}else {
				formFlexTable.showField(profileId);
			}
			
		}
		formValueChange("");
	}
	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

