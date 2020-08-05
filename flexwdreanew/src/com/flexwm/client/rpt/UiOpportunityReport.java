package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiOpportunityReport extends UiReport {

	BmoOpportunity bmoOpportunity;
	UiListBox reportTypeListBox;
	UiListBox wflowPhaseListBox;
	UiListBox wflowTypeListBox;
	UiListBox refeListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiDateBox saleStartDateBox = new UiDateBox();
	UiDateBox saleEndDateBox = new UiDateBox();
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox wflowFunnelUiListBox;
	UiListBox orderTypeListBox;
	UiListBox productFamilyListBox;
	UiListBox productGroupListBox;
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	BmField dateStart;
	BmField dateEnd;

	String generalSection = "Filtros Generales";
	String productsSection = "Filtros Productos";
	String dateSection = "Filtros de Fecha";
	
	

	BmoProduct bmoProduct = new BmoProduct();
	BmoWFlow bmoWFlow = new BmoWFlow();	

	public UiOpportunityReport(UiParams uiParams) {
		super(uiParams, new BmoOpportunity(), "/rpt/cm/oppo_type_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoOpportunity()));
		this.bmoOpportunity = (BmoOpportunity)getBmObject();
		
		dateStart = new BmField("datestart", "", "Fecha Cierre Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		dateEnd = new BmField("dateend", "", "Fecha Cierre Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {
		populateStatusListBox();
		reportTypeListBox = new UiListBox(getUiParams()); 		
		reportTypeListBox.addItem("Oportunidades Agrupadas por Tipo de Flujo", "/rpt/cm/oppo_type_report.jsp");
		reportTypeListBox.addItem("Oportunidades Agrupado por flujo comercial", "/rpt/cm/oppo_type_code_report.jsp");
		reportTypeListBox.addItem("Oportunidades Agrupadas por Fase de Flujo", "/rpt/cm/oppo_phase_report.jsp");
		reportTypeListBox.addItem("Oportunidades Agrupadas por Vendedor", "/rpt/cm/oppo_user_report.jsp");
		reportTypeListBox.addItem("Oportunidades Agrupadas por Cliente", "/rpt/cm/oppo_customer_report.jsp");
		reportTypeListBox.addItem("Oportunidades Agrupadas por Estatus", "/rpt/cm/oppo_status_report.jsp");
		if (getSFParams().isFieldEnabled(bmoWFlow.getWFlowFunnelId()))
			reportTypeListBox.addItem("Oportunidades Por Funnel", "/rpt/cm/oppo_funnel_report.jsp");
		reportTypeListBox.addItem("Reporte de Productos en Cotizaciones", "/rpt/cm/qoit_product_report.jsp");
		//reportTypeListBox.addItem("Reporte Especial", "/rpt/cm/oppo_customer_payment.jsp");

		orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		productFamilyListBox = new UiListBox(getUiParams(), new BmoProductFamily());
		productGroupListBox = new UiListBox(getUiParams(), new BmoProductGroup());
		wflowFunnelUiListBox = new UiListBox(getUiParams(), new BmoWFlowFunnel());

		// Filtros en los listbox
		// Tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

		// Fase de flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(),
				"" + bmObjectProgramId);
		wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase);

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
		formFlexTable.addField(4, 0, wflowPhaseListBox, bmoOpportunity.getBmoWFlow().getWFlowPhaseId());
		if (getSFParams().isFieldEnabled(bmoWFlow.getWFlowFunnelId()))
			formFlexTable.addField(5, 0, wflowFunnelUiListBox, bmoOpportunity.getBmoWFlow().getWFlowFunnelId(), true);
		formFlexTable.addField(6, 0, salesmanSuggestBox, bmoOpportunity.getUserId());
		formFlexTable.addField(7, 0, customersSuggestBox, bmoOpportunity.getCustomerId());
		formFlexTable.addField(8, 0, refeListBox, bmoOpportunity.getBmoCustomer().getReferralId());
		
		formFlexTable.addField(11, 0, venueSuggestBox, bmoOpportunity.getVenueId());	
		formFlexTable.addField(12, 0, currencyListBox, bmoOpportunity.getCurrencyId());
		formFlexTable.addField(13, 0, statusListBox, bmoOpportunity.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		
		formFlexTable.addSectionLabel(14, 0, dateSection, 2);
		formFlexTable.addField(15, 0, startDateBox, bmoOpportunity.getStartDate());
		formFlexTable.addField(16, 0, endDateBox, bmoOpportunity.getEndDate());
		formFlexTable.addField(17, 0, saleStartDateBox, dateStart );
		formFlexTable.addField(18, 0, saleEndDateBox, dateEnd);

		formFlexTable.addSectionLabel(19, 0, productsSection, 2);
		formFlexTable.addField(20, 0, productSuggestBox, bmoProduct.getIdField());
		formFlexTable.addField(21, 0, productFamilyListBox, bmoProduct.getProductFamilyId(), true);
		formFlexTable.addField(22, 0, productGroupListBox, bmoProduct.getProductGroupId(), true);
		
		
		

		formFlexTable.hideSection(productsSection);
		formFlexTable.hideField(venueSuggestBox);
		formFlexTable.hideSection(dateSection);
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

	public void populateStatusListBox() {
		
		
		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();
		
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusGanada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_WON, "Ganada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_authorized.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusPerdida().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_LOST, "Perdida", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusExpirada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_EXPIRED, "Expirado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_expired.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusHold().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_HOLD, "Detenido", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_on_hold.png")));
		}
		
		bmoOpportunity.getStatus().setOptionList(status);
	}
	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoOpportunity.getBmoWFlow().getWFlowTypeId(), wflowTypeListBox);
		addFilter(bmoOpportunity.getBmoWFlow().getWFlowPhaseId(), wflowPhaseListBox);
		addFilter(bmoOpportunity.getStatus(), statusListBox);
		addFilter(bmoOpportunity.getUserId(), salesmanSuggestBox);
		addFilter(bmoOpportunity.getCustomerId(), customersSuggestBox);
		addFilter(bmoOpportunity.getStartDate(), startDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getEndDate(), endDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getBmoCustomer().getReferralId(), refeListBox);
		addFilter(bmoOpportunity.getVenueId(), venueSuggestBox);
		addFilter(bmoOpportunity.getOrderTypeId(), orderTypeListBox);
		addFilter(bmoProduct.getIdField(), productSuggestBox);
		addFilter(bmoProduct.getProductFamilyId(), productFamilyListBox);
		addFilter(bmoProduct.getProductGroupId(), productGroupListBox);
		addFilter(bmoProduct.getProductGroupId(), productGroupListBox);
		addFilter(bmoOpportunity.getCurrencyId(), currencyListBox);
		addFilter(dateStart,saleStartDateBox.getTextBox().getText());
		addFilter(dateEnd,saleEndDateBox.getTextBox().getText());
		
		if (getSFParams().isFieldEnabled(bmoWFlow.getWFlowFunnelId()))
			addFilter(bmoOpportunity.getBmoWFlow().getWFlowFunnelId(), wflowFunnelUiListBox);		
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
