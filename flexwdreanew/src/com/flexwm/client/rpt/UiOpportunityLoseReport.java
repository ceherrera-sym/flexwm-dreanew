package com.flexwm.client.rpt;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiOpportunityLoseReport extends UiReport {

	BmoOpportunity bmoOpportunity;
	UiListBox reportTypeListBox;
	UiListBox loseMotiveListBox;
	UiListBox wflowTypeListBox;
	UiListBox refeListBox;
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
	UiListBox orderTypeListBox;
	UiListBox currencyListBox;

	String generalSection = "Filtros Generales";

	public UiOpportunityLoseReport(UiParams uiParams) {
		super(uiParams, new BmoOpportunity(), "/rpt/cm/oppo_lose_motive_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoOpportunity()) + " Perdidas");
		this.bmoOpportunity = (BmoOpportunity)getBmObject();

		try {	
			bmoOpportunity.getStatus().setValue(""+BmoOpportunity.STATUS_LOST);
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
		}
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Oportunidades Perdidas Agrupadas por Motivo de Perder", "/rpt/cm/oppo_lose_motive_report.jsp");
		reportTypeListBox.addItem("Oportunidades Perdidas Agrupadas por Tipo de Flujo", "/rpt/cm/oppo_lose_type_report.jsp");
		reportTypeListBox.addItem("Oportunidades Perdidas Agrupadas por Vendedor", "/rpt/cm/oppo_lose_user_report.jsp");
		reportTypeListBox.addItem("Oportunidades Perdidas Agrupadas por Cliente", "/rpt/cm/oppo_lose_customer_report.jsp");

		orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		loseMotiveListBox = new UiListBox(getUiParams(), new BmoLoseMotive());
		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

		// Filtros en los listbox
		// Tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

		// Tipo de pedido
		try {
			// Si no esta asignado el tipo, buscar por el default
			if (!(bmoOpportunity.getOrderTypeId().toInteger() > 0))
				bmoOpportunity.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Tipo Pedido por default : " + e.toString());
		}

		try {
			bmoOpportunity.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, orderTypeListBox, bmoOpportunity.getOrderTypeId());
		formFlexTable.addField(3, 0, wflowTypeListBox, bmoOpportunity.getBmoWFlow().getWFlowTypeId());
		formFlexTable.addField(4, 0, loseMotiveListBox, bmoOpportunity.getLoseMotiveId());
		formFlexTable.addField(5, 0, salesmanSuggestBox, bmoOpportunity.getUserId());
		formFlexTable.addField(6, 0, customersSuggestBox, bmoOpportunity.getCustomerId());
		formFlexTable.addField(7, 0, refeListBox, bmoOpportunity.getBmoCustomer().getReferralId());
		formFlexTable.addField(8, 0, startDateBox, bmoOpportunity.getStartDate());
		formFlexTable.addField(9, 0, endDateBox, bmoOpportunity.getEndDate());
		formFlexTable.addField(10, 0, currencyListBox, bmoOpportunity.getCurrencyId());
		formFlexTable.addField(11, 0, venueSuggestBox, bmoOpportunity.getVenueId());	
		formFlexTable.addLabelField(12, 0, bmoOpportunity.getStatus());	

		formFlexTable.hideField(venueSuggestBox);
		statusEffect();
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == orderTypeListBox) {
			statusEffect();
		}

		//Se pierde la url, se manda a llamar de nuevo
		formValueChange("");
	}

	private void statusEffect() {
		// Obtener tipo de pedido
		BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
		if (bmoOrderType == null) 
			bmoOrderType = bmoOpportunity.getBmoOrderType();

		// Si es de Tipo Renta mostrar el filtro de Salones, sino ocultarlo
		if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
			formFlexTable.showField(venueSuggestBox);
		} else {
			venueSuggestBox.clear();
			formFlexTable.hideField(venueSuggestBox);
		}
		
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoOpportunity.getBmoWFlow().getWFlowTypeId(), wflowTypeListBox);
		addFilter(bmoOpportunity.getLoseMotiveId(), loseMotiveListBox);
		addFilter(bmoOpportunity.getStatus(), bmoOpportunity.getStatus().toChar() + " : ");
		addFilter(bmoOpportunity.getUserId(), salesmanSuggestBox);
		addFilter(bmoOpportunity.getCustomerId(), customersSuggestBox);
		addFilter(bmoOpportunity.getStartDate(), startDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getEndDate(), endDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getBmoCustomer().getReferralId(), refeListBox);
		addFilter(bmoOpportunity.getVenueId(), venueSuggestBox);
		addFilter(bmoOpportunity.getOrderTypeId(), orderTypeListBox);
		addFilter(bmoOpportunity.getCurrencyId(), currencyListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
