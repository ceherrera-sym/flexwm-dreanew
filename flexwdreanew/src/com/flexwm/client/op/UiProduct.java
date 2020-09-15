/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.flexwm.shared.op.BmoProductLink;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoUnit;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiProduct extends UiList {
	BmoProduct bmoProduct;

	Image batchProductPricesImage;

	public UiProduct(UiParams uiParams) {
		super(uiParams, new BmoProduct());
		bmoProduct = (BmoProduct)getBmObject();

		batchProductPricesImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/batch.png"));
		batchProductPricesImage.setTitle("Carga Masiva de Precios de Productos...");
		batchProductPricesImage.setStyleName("listSearchImage");
		batchProductPricesImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					showBatch();
				}
			}
		});
	}

	@Override
	public void postShow() {
		if (getSFParams().hasWrite(getBmObject().getProgramCode()))
			localButtonPanel.add(batchProductPricesImage);

		addFilterListBox(new UiListBox(getUiParams(), new BmoProductFamily()), bmoProduct.getBmoProductFamily());
		addFilterListBox(new UiListBox(getUiParams(), new BmoProductGroup()), bmoProduct.getBmoProductGroup());
		if (isMaster()) {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoProduct.getTrack()), bmoProduct, bmoProduct.getTrack());
			if (!isMobile()) 
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoProduct.getType()), bmoProduct, bmoProduct.getType());
		}
	}

	public void showBatch() {
		String url = "/batch/prpc_batch.jsp";
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}

	@Override
	public void create() {
		UiProductForm uiProductForm = new UiProductForm(getUiParams(), 0);
		uiProductForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProduct = (BmoProduct)bmObject;
		UiProductForm uiProductForm = new UiProductForm(getUiParams(), bmoProduct.getId());
		uiProductForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiProductForm uiProductForm = new UiProductForm(getUiParams(), bmObject.getId());
		uiProductForm.show();
	}

	public class UiProductForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextBox displayNameTextBox = new TextBox();
		TextBox costTextBox = new TextBox();
		TextBox rentalCostTextBox = new TextBox();
		TextBox reorderTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox brandTextBox = new TextBox();
		TextBox modelTextBox = new TextBox();	
		UiListBox productFamilyListBox = new UiListBox(getUiParams(), new BmoProductFamily());
		UiListBox productGroupListBox = new UiListBox(getUiParams(), new BmoProductGroup());
		UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
		UiListBox unitListBox = new UiListBox(getUiParams(), new BmoUnit());
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox trackListBox = new UiListBox(getUiParams());
		UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox stockMaxTextBox = new TextBox();
		TextBox stockMinTextBox = new TextBox();
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		UiSuggestBox budgetItemUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());

		CheckBox inventoryCheckBox = new CheckBox();
		CheckBox enabledCheckBox = new CheckBox();
		CheckBox renewOrderCheckBox = new CheckBox();
		CheckBox commisionCheckBox = new CheckBox();
		TextBox renewRateTextBox = new TextBox();
		CheckBox consumableCheckBox = new CheckBox();
		
		TextBox weightTextBox = new TextBox();
		TextBox cubicMeterTextBox = new TextBox();
		TextBox dimensionLengthTextBox = new TextBox();
		TextBox dimensionHeightTextBox = new TextBox();
		TextBox dimensionWidthTextBox = new TextBox();
		TextBox amperage110TextBox = new TextBox();
		TextBox amperage220TextBox = new TextBox();
		TextBox quantityForCaseTextBox = new TextBox();
		TextBox weightCaseTextBox = new TextBox();
		TextBox caseLengthTextBox = new TextBox();
		TextBox caseHeightTextBox = new TextBox();
		TextBox caseseWidthTextBox = new TextBox();
		TextBox caseCubicMeterTextBox = new TextBox();
		CheckBox useCaseCheckBox = new CheckBox();

		String generalSection = "Datos Generales";
		String pricesSection = "Valores";
		String warehouseManagement = "Manejo de Almacén";
		String renewOrderSection = "Renovación Automática de Pedidos";
		String budgetItemSection = "Datos Control Presupuestal";
		String productLinksSection = "Sub-Productos";
		String extrasSection = "Datos Adicionales";
		String specificationsSection = "Especificaciones";

		BmoProduct bmoProduct;

		public UiProductForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProduct(), id);
		}

		@Override
		public void populateFields() {
			bmoProduct = (BmoProduct)getBmObject();		
			
			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoProduct.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoProduct.getName());
			formFlexTable.addField(4, 0, displayNameTextBox, bmoProduct.getDisplayName());
			formFlexTable.addField(5, 0, typeListBox, bmoProduct.getType());
			formFlexTable.addField(6, 0, descriptionTextArea, bmoProduct.getDescription());
			formFlexTable.addField(7, 0, brandTextBox, bmoProduct.getBrand());
			formFlexTable.addField(8, 0, modelTextBox, bmoProduct.getModel());
			formFlexTable.addField(9, 0, imageFileUpload, bmoProduct.getImage());	
			formFlexTable.addField(10, 0, productFamilyListBox, bmoProduct.getProductFamilyId());
			formFlexTable.addField(11, 0, productGroupListBox, bmoProduct.getProductGroupId());
			formFlexTable.addField(12, 0, supplierSuggestBox, bmoProduct.getSupplierId());
			formFlexTable.addField(13, 0, enabledCheckBox, bmoProduct.getEnabled());
			formFlexTable.addField(14, 0, commisionCheckBox, bmoProduct.getCommision());
			formFlexTable.addField(15, 0, consumableCheckBox, bmoProduct.getConsumable());

			// Precios
			formFlexTable.addSectionLabel(16, 0, pricesSection, 2);
			formFlexTable.addField(17, 0, costTextBox, bmoProduct.getCost());
			formFlexTable.addField(18, 0, rentalCostTextBox, bmoProduct.getRentalCost());
			if (!newRecord) {
				BmoProductPrice bmoProductPrice = new BmoProductPrice();
				FlowPanel productPriceFP = new FlowPanel();
				BmFilter filterProductPrices = new BmFilter();
				filterProductPrices.setValueFilter(bmoProductPrice.getKind(), bmoProductPrice.getProductId(), bmoProduct.getId());
				getUiParams().setForceFilter(bmoProductPrice.getProgramCode(), filterProductPrices);
				UiProductPrice uiProductPrice = new UiProductPrice(getUiParams(), productPriceFP, bmoProduct);
				setUiType(bmoProductPrice.getProgramCode(), UiParams.MINIMALIST);
				uiProductPrice.show();
				formFlexTable.addPanel(19, 0, productPriceFP, 2);
			}

			if (!newRecord) {
				if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED) {
					formFlexTable.addSectionLabel(20, 0, productLinksSection, 2);
					BmoProductLink bmoProductLink = new BmoProductLink();
					FlowPanel productLinkFP = new FlowPanel();
					BmFilter filterProductLink = new BmFilter();
					filterProductLink.setValueFilter(bmoProductLink.getKind(), bmoProductLink.getProductId(), bmoProduct.getId());
					getUiParams().setForceFilter(bmoProductLink.getProgramCode(), filterProductLink);
					UiProductLink uiProductLink = new UiProductLink(getUiParams(), productLinkFP, bmoProduct);
					setUiType(bmoProductLink.getProgramCode(), UiParams.MINIMALIST);
					uiProductLink.show();
					formFlexTable.addPanel(21, 0, productLinkFP, 2);
				}
			}

			formFlexTable.addSectionLabel(22, 0, warehouseManagement, 2);
			formFlexTable.addField(23, 0, trackListBox, bmoProduct.getTrack());
			setUnitListBoxFilters(bmoProduct.getTrack().toString());
			formFlexTable.addField(24, 0, unitListBox, bmoProduct.getUnitId());
			formFlexTable.addField(25, 0, inventoryCheckBox, bmoProduct.getInventory());
			formFlexTable.addField(26, 0, reorderTextBox, bmoProduct.getReorder());
			formFlexTable.addField(27, 0, stockMaxTextBox, bmoProduct.getStockMax());
			formFlexTable.addField(28, 0, stockMinTextBox, bmoProduct.getStockMin());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRenewProducts().toBoolean())) {	
				formFlexTable.addSectionLabel(29, 0, renewOrderSection, 2);
				formFlexTable.addField(30, 0, renewOrderCheckBox, bmoProduct.getRenewOrder());
				formFlexTable.addField(31, 0, renewRateTextBox, bmoProduct.getRenewRate());
				formFlexTable.addField(32, 0, orderTypeListBox, bmoProduct.getOrderTypeId());
				formFlexTable.addField(33, 0, wFlowTypeListBox, bmoProduct.getWFlowTypeId());
			}
			if (!newRecord) {
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					formFlexTable.addSectionLabel(34, 0, budgetItemSection, 2);
					formFlexTable.addField(35, 0, budgetItemUiSuggestBox, bmoProduct.getBudgetItemId());
					formFlexTable.addField(36, 0, areaUiListBox, bmoProduct.getAreaId());
				}

				// Items
				formFlexTable.addSectionLabel(37, 0, extrasSection, 2);
				// Empresas
				formFlexTable.addField(38, 0, new UiProductCompanyLabelList(getUiParams(), id));
			}
			formFlexTable.addSectionLabel(39, 0, specificationsSection, 2);
			formFlexTable.addField(40, 0, weightTextBox,bmoProduct.getWeight());
			formFlexTable.addField(41, 0, dimensionLengthTextBox,bmoProduct.getDimensionLength());
			formFlexTable.addField(42, 0, dimensionHeightTextBox,bmoProduct.getDimensionHeight());
			formFlexTable.addField(43, 0, dimensionWidthTextBox,bmoProduct.getDimensionWidth());
			formFlexTable.addField(44, 0, cubicMeterTextBox,bmoProduct.getCubicMeter());
			formFlexTable.addField(45, 0, amperage110TextBox,bmoProduct.getAmperage110());
			formFlexTable.addField(46, 0, amperage220TextBox,bmoProduct.getAmperage220());
			// si esta habilitado el campo de usar check muestra los campos relacionados a este
			if (getUiParams().getSFParams().isFieldEnabled(bmoProduct.getUseCase())) {
				formFlexTable.addField(47, 0, useCaseCheckBox,bmoProduct.getUseCase());
				formFlexTable.addField(48, 0, quantityForCaseTextBox,bmoProduct.getQuantityForCase());
				formFlexTable.addField(49, 0, weightCaseTextBox,bmoProduct.getWeightCase());
				formFlexTable.addField(50, 0, caseLengthTextBox,bmoProduct.getCaseLength());
				formFlexTable.addField(51, 0, caseHeightTextBox,bmoProduct.getCaseHeight());
				formFlexTable.addField(52, 0, caseseWidthTextBox,bmoProduct.getCaseWidth());
				formFlexTable.addField(53, 0, caseCubicMeterTextBox,bmoProduct.getCaseCubicMeter());
			}
			if(weightTextBox.getText().equals(""))weightTextBox.setText("0");
			if(dimensionLengthTextBox.getText().equals(""))dimensionLengthTextBox.setText("0");
			if(dimensionHeightTextBox.getText().equals(""))dimensionHeightTextBox.setText("0");
			if(dimensionWidthTextBox.getText().equals(""))dimensionWidthTextBox.setText("0");
			if(cubicMeterTextBox.getText().equals(""))cubicMeterTextBox.setText("0");
			if(amperage110TextBox.getText().equals(""))amperage110TextBox.setText("0");
			if(amperage220TextBox.getText().equals(""))amperage220TextBox.setText("0");
			if(quantityForCaseTextBox.getText().equals(""))quantityForCaseTextBox.setText("0");
			if(weightCaseTextBox.getText().equals(""))weightCaseTextBox.setText("0");
			if(caseLengthTextBox.getText().equals(""))caseLengthTextBox.setText("0");
			if(caseHeightTextBox.getText().equals(""))caseHeightTextBox.setText("0");
			if(caseseWidthTextBox.getText().equals(""))caseseWidthTextBox.setText("0");
			if(caseCubicMeterTextBox.getText().equals(""))caseCubicMeterTextBox.setText("0");
			statusEffect();

			if (!newRecord) {
				formFlexTable.hideSection(renewOrderSection);
				formFlexTable.hideSection(extrasSection);
			}			
		}

		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == typeListBox) {
				// Los productos de tipo compuesto no afectan almacen
				if (typeListBox.getSelectedCode().toString().equalsIgnoreCase("" + BmoProduct.TYPE_COMPOSED)) {
					inventoryCheckBox.setValue(false);
					inventoryCheckBox.setEnabled(false);
					consumableCheckBox.setValue(false);
				}
				else inventoryCheckBox.setEnabled(true);			
			} else if(event.getSource() == trackListBox) {
				populateUnit(trackListBox.getSelectedCode().toString());
			}
		}

		// Actualiza combo de almacenes
		private void populateUnit(String track) {
			unitListBox.clear();
			unitListBox.clearFilters();
			setUnitListBoxFilters(track);
			unitListBox.populate(bmoProduct.getUnitId());
		}

		// Asigna filtros al listado de almacenes
		private void setUnitListBoxFilters(String track) {
			BmoUnit bmoUnit = new BmoUnit();
			BmFilter bmFilterByTrack= new BmFilter();
			if (track.equals("" + BmoProduct.TRACK_SERIAL)) {
				// Filtrar que no sea fraccion
				bmFilterByTrack.setValueFilter(bmoUnit.getKind(), bmoUnit.getFraction(), 0);
			} else if (track.equals("" + BmoProduct.TRACK_BATCH) || track.equals("" + BmoProduct.TRACK_NONE) ) { 
				// Filtrar todos
				bmFilterByTrack.setValueOperatorFilter(bmoUnit.getKind(), bmoUnit.getIdField(), BmFilter.MAYOR, 0);
			} else if (track.equals("") ) {
				// Filtrar ninguno
				bmFilterByTrack.setValueFilter(bmoUnit.getKind(), bmoUnit.getIdField(), -1);
			}
			unitListBox.addBmFilter(bmFilterByTrack);		
		}

		public void statusEffect() {
			cubicMeterTextBox.setEnabled(false);
			caseCubicMeterTextBox.setEnabled(false);
			if (renewOrderCheckBox.getValue()) {
				orderTypeListBox.setEnabled(true);
				wFlowTypeListBox.setEnabled(true);
			} else {
				orderTypeListBox.setEnabled(false);
				orderTypeListBox.setSelectedIndex(0);
				orderTypeListBox.setSelectedId("-1");

				wFlowTypeListBox.setEnabled(false);
				wFlowTypeListBox.setSelectedIndex(0);
				wFlowTypeListBox.setSelectedId("-1");
			}
			if (useCaseCheckBox.getValue()) {

				formFlexTable.showField(quantityForCaseTextBox);
				formFlexTable.showField(weightCaseTextBox);
				formFlexTable.showField(caseLengthTextBox);
				formFlexTable.showField(caseHeightTextBox);
				formFlexTable.showField(caseseWidthTextBox);
				formFlexTable.showField(caseCubicMeterTextBox);
			} else {
				formFlexTable.hideField(quantityForCaseTextBox);
				formFlexTable.hideField(weightCaseTextBox);
				formFlexTable.hideField(caseLengthTextBox);
				formFlexTable.hideField(caseHeightTextBox);
				formFlexTable.hideField(caseseWidthTextBox);
				formFlexTable.hideField(caseCubicMeterTextBox);
				quantityForCaseTextBox.setText("0");
				weightCaseTextBox.setText("0");
				caseLengthTextBox.setText("0");
				caseHeightTextBox.setText("0");
				caseseWidthTextBox.setText("0");
				caseCubicMeterTextBox.setText("0");		
				
			}
			
			if (typeListBox.getSelectedCode().toString().equalsIgnoreCase("" + BmoProduct.TYPE_COMPOSED)) {
				inventoryCheckBox.setEnabled(false);
				
			} else {
				inventoryCheckBox.setEnabled(true);
			}
			if (consumableCheckBox.getValue()) {
				inventoryCheckBox.setValue(true);
				inventoryCheckBox.setEnabled(false);
			} 
		}
		@Override
		public void formTextChange(ValueChangeEvent<String> event) {
			double dimensionLength = Double.parseDouble(dimensionLengthTextBox.getText());			
			double dimensionWidth = Double.parseDouble(dimensionWidthTextBox.getText());	
			double dimensionHeight = Double.parseDouble(dimensionHeightTextBox.getText());
			double caseDimensionLength = Double.parseDouble(caseLengthTextBox.getText());			
			double caseDimensionWidth = Double.parseDouble(caseseWidthTextBox.getText());	
			double caseDimensionHeight = Double.parseDouble(caseHeightTextBox.getText());
			
			if ((event.getSource() == dimensionLengthTextBox) || (event.getSource() == dimensionHeightTextBox)
					|| (event.getSource() == dimensionWidthTextBox)) {
				cubicMeterTextBox.setText("" +(dimensionLength * dimensionWidth * dimensionHeight ));			
			} else if((event.getSource() == caseLengthTextBox) || (event.getSource() == caseHeightTextBox)
					|| (event.getSource() == caseseWidthTextBox)) {
				caseCubicMeterTextBox.setText(""+ (caseDimensionLength * caseDimensionWidth * caseDimensionHeight));
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProduct.setId(id);
			bmoProduct.getCode().setValue(codeTextBox.getText());
			bmoProduct.getName().setValue(nameTextBox.getText());
			bmoProduct.getDescription().setValue(descriptionTextArea.getText());
			bmoProduct.getDisplayName().setValue(displayNameTextBox.getText());
			bmoProduct.getImage().setValue(imageFileUpload.getBlobKey());
			bmoProduct.getBrand().setValue(brandTextBox.getText());
			bmoProduct.getModel().setValue(modelTextBox.getText());
			bmoProduct.getCost().setValue(costTextBox.getText());
			bmoProduct.getRentalCost().setValue(rentalCostTextBox.getText());
			bmoProduct.getType().setValue(typeListBox.getSelectedCode());
			bmoProduct.getTrack().setValue(trackListBox.getSelectedCode());
			bmoProduct.getUnitId().setValue(unitListBox.getSelectedId());
			bmoProduct.getSupplierId().setValue(supplierSuggestBox.getSelectedId());
			bmoProduct.getProductFamilyId().setValue(productFamilyListBox.getSelectedId());
			bmoProduct.getProductGroupId().setValue(productGroupListBox.getSelectedId());
			bmoProduct.getReorder().setValue(reorderTextBox.getText());
			bmoProduct.getInventory().setValue(inventoryCheckBox.getValue());
			bmoProduct.getEnabled().setValue(enabledCheckBox.getValue());
			bmoProduct.getConsumable().setValue(consumableCheckBox.getValue());			
			bmoProduct.getStockMax().setValue(stockMaxTextBox.getText());
			bmoProduct.getStockMin().setValue(stockMinTextBox.getText());
			bmoProduct.getRenewOrder().setValue(renewOrderCheckBox.getValue());
			bmoProduct.getRenewRate().setValue(renewRateTextBox.getValue());
			bmoProduct.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoProduct.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoProduct.getCommision().setValue(commisionCheckBox.getValue());
			bmoProduct.getBudgetItemId().setValue(budgetItemUiSuggestBox.getSelectedId());
			bmoProduct.getAreaId().setValue(areaUiListBox.getSelectedId());
			bmoProduct.getWeight().setValue(weightTextBox.getText());
			bmoProduct.getDimensionLength().setValue(dimensionLengthTextBox.getText());
			bmoProduct.getDimensionHeight().setValue(dimensionHeightTextBox.getText());
			bmoProduct.getDimensionWidth().setValue(dimensionWidthTextBox.getText());
			bmoProduct.getCubicMeter().setValue(cubicMeterTextBox.getText());
			bmoProduct.getAmperage110().setValue(amperage110TextBox.getText());
			bmoProduct.getAmperage220().setValue(amperage220TextBox.getText());
			bmoProduct.getUseCase().setValue(useCaseCheckBox.getValue());
			bmoProduct.getQuantityForCase().setValue(quantityForCaseTextBox.getText());
			bmoProduct.getWeightCase().setValue(weightCaseTextBox.getText());
			bmoProduct.getCaseLength().setValue(caseLengthTextBox.getText());
			bmoProduct.getCaseHeight().setValue(caseHeightTextBox.getText());
			bmoProduct.getCaseWidth().setValue(caseseWidthTextBox.getText());
			bmoProduct.getCaseCubicMeter().setValue(caseCubicMeterTextBox.getText());

			return bmoProduct;
		}

		@Override
		public void close() {
			list();
		}
		@Override
		public void saveNext() {
			if(newRecord)
				open(bmoProduct);
			else 
				showList();
		}
	}
}
