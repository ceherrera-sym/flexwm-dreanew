package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoUnit;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;


public class UiProductsReport extends UiReport {

	BmoProduct bmoProduct;
	BmoProductPrice bmoProductPrice;
	
	UiListBox reportTypeListBox ;	
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiListBox trackListBox = new UiListBox(getUiParams());
	UiListBox unitListBox = new UiListBox(getUiParams(), new BmoUnit());
	UiListBox familyListBox = new UiListBox(getUiParams(), new BmoProductFamily());
	UiListBox grupListBox = new UiListBox(getUiParams(), new BmoProductGroup());	
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());	
	UiDateBox dateStartDateBox = new UiDateBox();
	UiDateBox dateEndDateBox = new UiDateBox();
	BmField dateStart;
	BmField dateEnd;
	UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
	UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
	UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
	UiListBox companiesListBox = new UiListBox(getUiParams(), new BmoCompany());

	String generalSection = "Filtros Generales";

	public UiProductsReport(UiParams uiParams) {
		super(uiParams, new BmoProduct(), "/rpt/op/prod_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoProduct()));
		this.bmoProduct = (BmoProduct)getBmObject();
		
		this.bmoProductPrice = new BmoProductPrice();
		

		dateStart = new BmField("datestart", "", "Fecha Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		dateEnd = new BmField("dateend", "", "Fecha Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Productos General", "/rpt/op/prod_general_report.jsp");
		reportTypeListBox.addItem("Productos por Proveedor", "/rpt/op/prod_bysupplier_report.jsp");
		reportTypeListBox.addItem("Precios por Producto", "/rpt/op/prod_byprices_report.jsp");
		//reportTypeListBox.addItem("Utilización por Producto", "/rpt/op/prod_byuse_report.jsp");
		reportTypeListBox.addItem("Máximos y Mínimos por Producto", "/rpt/op/prod_maxminprod_report.jsp");
		reportTypeListBox.addItem("Mantenimiento Producto", "/rpt/op/prod_maintenance_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, supplierSuggestBox, bmoProduct.getSupplierId());
		formFlexTable.addField(3, 0, typeListBox, bmoProduct.getType());		
		formFlexTable.addField(4, 0, familyListBox, bmoProduct.getProductFamilyId(), true);
		formFlexTable.addField(5, 0, grupListBox, bmoProduct.getProductGroupId(), true);
		formFlexTable.addField(6, 0, unitListBox, bmoProduct.getUnitId());
		formFlexTable.addField(7, 0, trackListBox, bmoProduct.getTrack());
		formFlexTable.addField(8, 0, dateStartDateBox, dateStart);
		formFlexTable.addField(9, 0, dateEndDateBox, dateEnd);
		formFlexTable.addField(10, 0, orderTypeListBox, bmoProductPrice.getOrderTypeId());
		setWFlowTypeListBoxFilters(bmoProductPrice.getOrderTypeId().toInteger());
		formFlexTable.addField(11, 0, wFlowTypeListBox, bmoProductPrice.getWFlowTypeId());
		formFlexTable.addField(12, 0, marketListBox, bmoProductPrice.getMarketId());
		formFlexTable.addField(13, 0, companiesListBox, bmoProductPrice.getCompanyId());
		
		formFlexTable.hideField(dateEndDateBox);
		formFlexTable.hideField(orderTypeListBox);
		formFlexTable.hideField(wFlowTypeListBox);
		formFlexTable.hideField(marketListBox);
		formFlexTable.hideField(companiesListBox);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		
		if (getSFParams().isFieldEnabled(bmoProduct.getSupplierId()))
			addFilter(bmoProduct.getSupplierId(), supplierSuggestBox);
		if (getSFParams().isFieldEnabled(bmoProduct.getType()))
			addFilter(bmoProduct.getType(), typeListBox);
		if (getSFParams().isFieldEnabled(bmoProduct.getProductFamilyId()))
			addFilter(bmoProduct.getProductFamilyId(), familyListBox);
		if (getSFParams().isFieldEnabled(bmoProduct.getProductGroupId()))
			addFilter(bmoProduct.getProductGroupId(), grupListBox);
		if (getSFParams().isFieldEnabled(bmoProduct.getUnitId()))
			addFilter(bmoProduct.getUnitId(), unitListBox);				
		if (getSFParams().isFieldEnabled(bmoProduct.getTrack()))
			addFilter(bmoProduct.getTrack(), trackListBox);
		if (getSFParams().isFieldEnabled(bmoProductPrice.getStartDate()))
			addFilter(dateStart, dateStartDateBox.getTextBox().getText());
			addFilter(dateEnd, dateEndDateBox.getTextBox().getText());
		if (getSFParams().isFieldEnabled(bmoProductPrice.getOrderTypeId()))
			addFilter(bmoProductPrice.getOrderTypeId(), orderTypeListBox);
		if (getSFParams().isFieldEnabled(bmoProductPrice.getMarketId()))
			addFilter(bmoProductPrice.getMarketId(), marketListBox);
		if (getSFParams().isFieldEnabled(bmoProductPrice.getCompanyId()))
			addFilter(bmoProductPrice.getCompanyId(), companiesListBox);
		if (getSFParams().isFieldEnabled(bmoProductPrice.getWFlowTypeId()))
			addFilter(bmoProductPrice.getWFlowTypeId(), wFlowTypeListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode().equals("/rpt/op/prod_byprices_report.jsp")) {
				formFlexTable.showField(dateEndDateBox);
				formFlexTable.showField(orderTypeListBox);
				formFlexTable.showField(wFlowTypeListBox);
				formFlexTable.showField(marketListBox);
				formFlexTable.showField(companiesListBox);
				
				formFlexTable.addLabelCell(8, 0, "Vigencia Inicio");
				formFlexTable.addLabelCell(9, 0, "Vigencia Fin");
//				dateStart.setLabel("Vigencia Inicio");
//				dateEnd.setLabel("Vigencia Fin");

			} else {
				formFlexTable.hideField(dateEndDateBox);
				formFlexTable.hideField(orderTypeListBox);
				formFlexTable.hideField(wFlowTypeListBox);
				formFlexTable.hideField(marketListBox);
				formFlexTable.hideField(companiesListBox);
				formFlexTable.addLabelCell(8, 0, "Fecha Inicio");
				formFlexTable.addLabelCell(9, 0, "Fecha Fin");
			}
		}
		// Si el movimiento es en el lista de tipos de flujo de la oportunidad, modificar el de los efectos
		else if (event.getSource() == orderTypeListBox) {
			BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
			if (bmoOrderType != null) {
				changeOrderType(bmoOrderType);
			}
		}

	//Se pierde la url, se manda a llamar de nuevo
	formValueChange("");
	}
	
	// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
	private void changeOrderType(BmoOrderType bmoOrderType) {
		wFlowTypeListBox.clear();
		wFlowTypeListBox.clearFilters();
		if (bmoOrderType.getId() > 0)
			setWFlowTypeListBoxFilters(bmoOrderType.getId());
		wFlowTypeListBox.populate("" + bmoProductPrice.getWFlowTypeId(), false);

	}
	
	// Filtrar tipos de flujo por empresa
	private void setWFlowTypeListBoxFilters(int orderTypeId) {
		BmoProject bmoProject = new BmoProject();
		int bmProjectProgramId = 0;

		try {
			// Filtro de tipos de flujo en categorias del modulo Proyectos
			bmProjectProgramId = getSFParams().getProgramId(bmoProject.getProgramCode());
			BmFilter bmFilterByProgramId = new BmFilter();
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			bmFilterByProgramId.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), "" + bmProjectProgramId);

			// Filtros de tipos de flujos activos
			BmFilter bmFilterByStatus = new BmFilter();
			bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);

			// Filtro de flujos en categoria agregada al tipo de pedido
			BmFilter bmFilterByWFlowCategories = new BmFilter();
			BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			if (orderTypeId > 0) {
				bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
						bmoWFlowCategory.getIdFieldName(), 
						bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
						bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
						"" + orderTypeId);
			}
			// Agregar filtros al tipo de flujo
			wFlowTypeListBox.addFilter(bmFilterByProgramId); 
			wFlowTypeListBox.addFilter(bmFilterByStatus);
			if (orderTypeId > 0) {
				wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
			}

		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-setWFlowTypeListBoxFilters() ERROR: " + e.toString());
		}
	}

			

}
