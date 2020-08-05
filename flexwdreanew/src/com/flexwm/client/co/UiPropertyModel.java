/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.flexwm.shared.co.BmoPropertyType;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiPropertyModel extends UiList {
	BmoPropertyModel bmoPropertyModel;
	public int propertyModelId;
	public int updateAmountRpcAttempt = 0;
	
	public UiPropertyModel(UiParams uiParams) {
		super(uiParams, new BmoPropertyModel());
		bmoPropertyModel = (BmoPropertyModel)getBmObject();
	}
	
	public int getPropertyModelId() {
		return propertyModelId;
	}

	public void setPropertyModelId(int propertyModelId) {
		this.propertyModelId = propertyModelId;
	}

	public int getUpdateAmountRpcAttempt() {
		return updateAmountRpcAttempt;
	}
	
	public void setUpdateAmountRpcAttempt(int updateAmountRpcAttempt) {
		this.updateAmountRpcAttempt = updateAmountRpcAttempt;
	}

	@Override
	public void postShow() {
		if (getSFParams().hasRead((new BmoDevelopment()).getProgramCode().toString() )) 
		 addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopment()), bmoPropertyModel.getBmoDevelopment());
	}

	@Override
	public void create() {
		UiPropertyModelForm uiPropertyModelForm = new UiPropertyModelForm(getUiParams(), 0);
		uiPropertyModelForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertyModel = (BmoPropertyModel)bmObject;
		UiPropertyModelForm uiPropertyModelForm = new UiPropertyModelForm(getUiParams(), bmObject.getId());
		uiPropertyModelForm.show();

	}

	@Override
	public void edit(BmObject bmObject) {
		UiPropertyModelForm uiPropertyModelForm = new UiPropertyModelForm(getUiParams(), bmObject.getId());	
		uiPropertyModelForm.show();
		
	}

	public class UiPropertyModelForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox propertyTypeListBox = new UiListBox(getUiParams(), new BmoPropertyType());
		TextBox roomsTextBox = new TextBox();
		TextBox dormsTextBox = new TextBox();
		TextBox landSizeTextBox = new TextBox();
		TextBox constructionSizeTextBox = new TextBox();
		TextArea highLightsTextArea = new TextArea();
		TextArea detailsTextArea = new TextArea();
		TextArea finishingTextArea = new TextArea();
		TextArea otherTextArea = new TextArea();
		TextBox priceTextBox = new TextBox();
		TextBox valuedPriceTextBox = new TextBox();
		TextBox meterPriceTextBox = new TextBox();
		TextBox publicLandSizeTextBox = new TextBox();
		TextBox publicMeterPriceTextBox = new TextBox();
		TextBox constructionMeterPriceTextBox = new TextBox();
		TextBox marketSegmentIdTextBox = new TextBox();
		TextBox monthlyGoalTextBox = new TextBox();
		UiListBox developmentListBox = new UiListBox(getUiParams(), new BmoDevelopment());
		UiFileUploadBox blueprintFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox garageFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox roofGardenFileUpload = new UiFileUploadBox(getUiParams());
		TextBox bonusRGTextBox = new TextBox();
		BmoPropertyModel bmoPropertyModel;
		BmoOrderType bmoOrderType = new BmoOrderType();
		String generalSection = "Datos Generales";
		String measurementSection = "Dimensiones";
		String detailsSection = "Detalles";
		String itemsSection = "Items";
		
		
		PropertyModelUpdater propertyModelUpdater = new PropertyModelUpdater();

		public UiPropertyModelForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertyModel(), id); 
		}
		

		@Override
		public void populateFields(){
			bmoPropertyModel = (BmoPropertyModel)getBmObject();

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, codeTextBox, bmoPropertyModel.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoPropertyModel.getName());
			if (getSFParams().hasRead((new BmoDevelopment()).getProgramCode().toString() )) 
				formFlexTable.addField(4, 0, developmentListBox, bmoPropertyModel.getDevelopmentId());
			formFlexTable.addField(5, 0, propertyTypeListBox, bmoPropertyModel.getPropertyTypeId());
			formFlexTable.addField(6, 0, descriptionTextArea, bmoPropertyModel.getDescription());
			formFlexTable.addField(7, 0, monthlyGoalTextBox, bmoPropertyModel.getMonthlyGoal());
			formFlexTable.addField(8, 0, roomsTextBox, bmoPropertyModel.getRooms());
			formFlexTable.addField(9, 0, dormsTextBox, bmoPropertyModel.getDorms());
			formFlexTable.addField(10, 0, bonusRGTextBox, bmoPropertyModel.getBonusRG());

			formFlexTable.addSectionLabel(11, 0, measurementSection, 2);
			formFlexTable.addField(12, 0, landSizeTextBox, bmoPropertyModel.getLandSize());
			formFlexTable.addField(13, 0, constructionSizeTextBox, bmoPropertyModel.getConstructionSize());
			formFlexTable.addField(14, 0, publicLandSizeTextBox, bmoPropertyModel.getPublicLandSize());

			formFlexTable.addSectionLabel(15, 0, detailsSection, 2);
			formFlexTable.addField(16, 0, highLightsTextArea, bmoPropertyModel.getHighLights());
			formFlexTable.addField(17, 0, detailsTextArea, bmoPropertyModel.getDetails());
			formFlexTable.addField(18, 0, finishingTextArea, bmoPropertyModel.getFinishing());
			formFlexTable.addField(19, 0, otherTextArea, bmoPropertyModel.getOther());

			if (!newRecord) {
				formFlexTable.addField(19, 0, blueprintFileUpload, bmoPropertyModel.getBlueprint());	
				formFlexTable.addField(20, 0, imageFileUpload, bmoPropertyModel.getImage());
				formFlexTable.addField(21, 0, garageFileUpload, bmoPropertyModel.getGarage());	
				formFlexTable.addField(22, 0, roofGardenFileUpload, bmoPropertyModel.getRoofGarden());	

				// Items
				formFlexTable.addSectionLabel(23, 0, itemsSection, 2);
				
				BmoPropertyModelPrice bmoPropertyModelPrice = new BmoPropertyModelPrice();
				if(getSFParams().hasRead(bmoPropertyModelPrice.getProgramCode())){
					FlowPanel propertyModelPriceFP = new FlowPanel();
					BmFilter filterPropertyModelPrice = new BmFilter();
					filterPropertyModelPrice.setValueFilter(bmoPropertyModelPrice.getKind(),
							bmoPropertyModelPrice.getPropertyModelId(), bmoPropertyModel.getId());
					getUiParams().setForceFilter(bmoPropertyModelPrice.getProgramCode(), filterPropertyModelPrice);
					UiPropertyModelPrice uiPropertyModelPrice = new UiPropertyModelPrice(getUiParams(),
							propertyModelPriceFP, bmoPropertyModel, bmoPropertyModel.getId(), propertyModelUpdater);
					setUiType(bmoPropertyModelPrice.getProgramCode(), UiParams.MINIMALIST);
					uiPropertyModelPrice.show();
					formFlexTable.addPanel(24, 0, propertyModelPriceFP, 2);
				}
				
				BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
				if(getSFParams().hasRead(bmoPropertyModelExtra.getProgramCode())){
					FlowPanel propertyModelExtraFP = new FlowPanel();
					BmFilter filterPropertyModelExtra = new BmFilter();
					filterPropertyModelExtra.setValueFilter(bmoPropertyModelExtra.getKind(),
							bmoPropertyModelExtra.getPropertyModelId(), bmoPropertyModel.getId());
					getUiParams().setForceFilter(bmoPropertyModelExtra.getProgramCode(), filterPropertyModelExtra);
					UiPropertyModelExtra uiPropertyModelExtra = new UiPropertyModelExtra(getUiParams(),
							propertyModelExtraFP, bmoPropertyModel, bmoPropertyModel.getId(), propertyModelUpdater);
					setUiType(bmoPropertyModelExtra.getProgramCode(), UiParams.MINIMALIST);
					uiPropertyModelExtra.show();
					formFlexTable.addPanel(25, 0, propertyModelExtraFP, 2);
				}
			}

			statusEffect();

			if (!newRecord)
				formFlexTable.hideSection(detailsSection);
			
		}
		
		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == propertyTypeListBox ) {
				BmoPropertyType bmoPropertyType = (BmoPropertyType)propertyTypeListBox.getSelectedBmObject();
				getOrderType(bmoPropertyType.getOrderTypeId().toInteger());
			}
		}
		
		public void getPropertyType(int id) {
			BmoPropertyType  bmoPropertyType= new BmoPropertyType();
					AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
							
								showErrorMessage(this.getClass().getName() + "-getPropertyType() ERROR: " + caught.toString());
							
						}
		
						@Override
						public void onSuccess(BmObject result) {
							stopLoading();
							BmoPropertyType  bmoPropertyType= (BmoPropertyType) result;
							getOrderType(bmoPropertyType.getOrderTypeId().toInteger());
							
						}
					};
					try {
						if (!isLoading()) {
							startLoading();
							getUiParams().getBmObjectServiceAsync().get(bmoPropertyType.getPmClass(), id, callback);
						}
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-getPropertyType(): ERROR " + e.toString());
					}
				
			
		}
		
		public void getOrderType(int id) {		
					AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
								showErrorMessage(this.getClass().getName() + "-getOrderType() ERROR: " + caught.toString());
						}
		
						@Override
						public void onSuccess(BmObject result) {
							stopLoading();
							setUpdateAmountRpcAttempt(0);
							
							bmoOrderType = (BmoOrderType) result;
							if(bmoOrderType.getType().equals(""+BmoOrderType.TYPE_LEASE)) {
								formFlexTable.hideField(roomsTextBox);
								formFlexTable.hideField(dormsTextBox);
								formFlexTable.hideField(formFlexTable);
								formFlexTable.hideField(bmoPropertyModel.getGarage());
								formFlexTable.hideField(bmoPropertyModel.getRoofGarden());
								formFlexTable.hideField(bmoPropertyModel.getBonusRG());
							}
							else {
								formFlexTable.showField(roomsTextBox);
								formFlexTable.showField(dormsTextBox);
								formFlexTable.showField(formFlexTable);
								formFlexTable.showField(bmoPropertyModel.getGarage());
								formFlexTable.showField(bmoPropertyModel.getRoofGarden());
								formFlexTable.showField(bmoPropertyModel.getBonusRG());
							}
							
						}
					};
					try {
						if (!isLoading()) {
							startLoading();
							getUiParams().getBmObjectServiceAsync().get(bmoOrderType.getPmClass(), id, callback);
						}
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
					}
				
			}

		private void statusEffect(){
			if (!newRecord) {
				codeTextBox.setEnabled(true);				
				getPropertyType(bmoPropertyModel.getPropertyTypeId().toInteger());
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertyModel.setId(id);
			bmoPropertyModel.getCode().setValue(codeTextBox.getText());
			bmoPropertyModel.getName().setValue(nameTextBox.getText());
			bmoPropertyModel.getDescription().setValue(descriptionTextArea.getText());
			bmoPropertyModel.getPropertyTypeId().setValue(propertyTypeListBox.getSelectedId());
			bmoPropertyModel.getRooms().setValue(roomsTextBox.getText());
			bmoPropertyModel.getDorms().setValue(dormsTextBox.getText());
			bmoPropertyModel.getLandSize().setValue(landSizeTextBox.getText());
			bmoPropertyModel.getConstructionSize().setValue(constructionSizeTextBox.getText());
			bmoPropertyModel.getHighLights().setValue(highLightsTextArea.getText());
			bmoPropertyModel.getDetails().setValue(detailsTextArea.getText());
			bmoPropertyModel.getFinishing().setValue(finishingTextArea.getText());
			bmoPropertyModel.getOther().setValue(otherTextArea.getText());
			bmoPropertyModel.getPublicLandSize().setValue(publicLandSizeTextBox.getText());
			bmoPropertyModel.getMonthlyGoal().setValue(monthlyGoalTextBox.getText());
			if (getSFParams().hasRead((new BmoDevelopment()).getProgramCode().toString() )) 
				bmoPropertyModel.getDevelopmentId().setValue(developmentListBox.getSelectedId());
			bmoPropertyModel.getBlueprint().setValue(blueprintFileUpload.getBlobKey());
			bmoPropertyModel.getImage().setValue(imageFileUpload.getBlobKey());
			bmoPropertyModel.getGarage().setValue(garageFileUpload.getBlobKey());
			bmoPropertyModel.getRoofGarden().setValue(roofGardenFileUpload.getBlobKey());
			bmoPropertyModel.getBonusRG().setValue(bonusRGTextBox.getText());

			return bmoPropertyModel;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiPropertyModelForm uiPropertyModelForm = new UiPropertyModelForm(getUiParams(), getBmObject().getId());
				uiPropertyModelForm.show();
			} else {
				UiPropertyModel uiPropertyModel = new UiPropertyModel(getUiParams());
				uiPropertyModel.show();
			}
		}

		public class PropertyModelUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
