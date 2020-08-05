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
import com.symgae.shared.sf.BmoCity;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.client.co.UiPropertyTaxLabelList;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoDevelopmentRegistry;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyTax;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;

/**
 * @author smuniz
 *
 */

public class UiProperty extends UiList {
	BmoProperty bmoProperty;	
	BmoOrderType bmoOrderType;
	private int propertyId;
	private int updateAmountRpcAttempt = 0;
	private UiPropertyForm uiPropertyForm;

	public int getUpdateAmountRpcAttempt() {
		return updateAmountRpcAttempt;
	}

	public void setUpdateAmountRpcAttempt(int updateAmountRpcAttempt) {
		this.updateAmountRpcAttempt = updateAmountRpcAttempt;
	}

	public int getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

	public UiProperty(UiParams uiParams) {
		super(uiParams, new BmoProperty());
		bmoProperty = (BmoProperty)getBmObject();
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			if (getSFParams().hasRead((new BmoDevelopmentPhase()).getProgramCode().toString() )) 
				addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopmentPhase()), bmoProperty.getBmoDevelopmentBlock().getBmoDevelopmentPhase());
			if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
				addFilterSuggestBox(new UiSuggestBox(new BmoDevelopmentBlock()), new BmoDevelopmentBlock(), bmoProperty.getDevelopmentBlockId());
		}
		addTagFilterListBox(bmoProperty.getTags());
	}

	@Override
	public void create() {
		UiPropertyForm uiPropertyForm = new UiPropertyForm(getUiParams(), 0);
		uiPropertyForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProperty = (BmoProperty)bmObject;
		UiPropertyForm uiPropertyForm = new UiPropertyForm(getUiParams(), bmoProperty.getId());
		uiPropertyForm.show();
	}
	
	public UiPropertyForm getUiPropertyForm() {
		uiPropertyForm = new UiPropertyForm(getUiParams(), bmoProperty.getId());
		return uiPropertyForm;
	}

	@Override
	public void edit(BmObject bmObject) {
		uiPropertyForm = new UiPropertyForm(getUiParams(), bmObject.getId());
		uiPropertyForm.show();
	}

	public class UiPropertyForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox lotTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea streetTextArea = new TextArea();
		TextBox numberTextBox = new TextBox();
		TextBox neighborhoodTextBox = new TextBox();
		TextArea betweenStreetsTextArea = new TextArea();
		TextBox zipTextBox = new TextBox();
		UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());

		TextBox progressTextBox = new TextBox();
		CheckBox cansellCheckBox = new CheckBox();
		CheckBox habitabilityCheckBox = new CheckBox();
		UiDateBox finishDateDateBox = new UiDateBox();
		UiDateBox dueDateDateBox = new UiDateBox();
		TextBox landSizeTextBox = new TextBox();
		TextBox publicLandSizeTextBox = new TextBox();
		TextBox constructionSizeTextBox = new TextBox();
		TextBox registryNumberTextBox = new TextBox();
		TextArea adjoinsTextArea = new TextArea();
		TextBox extraPriceTextBox = new TextBox();
		UiSuggestBox propertyModelSuggestBox = new UiSuggestBox(new BmoPropertyModel());
		UiSuggestBox developmentBlockSuggestBox  = new UiSuggestBox(new BmoDevelopmentBlock());
		UiSuggestBox developmentRegistrySuggestBox = new UiSuggestBox(new BmoDevelopmentRegistry());
		UiFileUploadBox facadeFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox blueprintFileUpload = new UiFileUploadBox(getUiParams());
		//Campos Inadico 
		CheckBox renewOrderCheckBox = new CheckBox();
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());		

		UiFileUploadBox contractFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox propertyReceiptFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox propertyTitleFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox constitutiveActFileUpload = new UiFileUploadBox(getUiParams());
		
		UiFileUploadBox certifiedWritingFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox demarcationFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox notaryQuotationFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox appraiseFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox debtCertificateFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox taxCertificateFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox waterBillFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox electricityBillFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox otherDocumentsFileUpload = new UiFileUploadBox(getUiParams());
		UiTagBox tagBox = new UiTagBox(getUiParams());

		TextBox coordinatesTextBox = new TextBox();
		UiListBox companyUiListBox = new UiListBox(getUiParams(), new BmoCompany());

		BmoProperty bmoProperty;
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();

		String documentsSection = "Documentos";
		String generalSection = "Datos Generales";
		String progressSection = "Avances";
		String measurementSection = "Dimensiones";
		String addressSection = "Direccion";
		String renewOrderSection = "Renovación Automática de Inmueble";
		String requisistionSection = "Ordenes Compra";
		boolean isLease = false;
		BmoCustomer bmoCustomer;
		private int getPropertyModelRpcAttemp = 0;
		private int propertyModelId;
		
		public UiPropertyForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProperty(), id); 
			initialize();
		}

		private void initialize() {
			// Filtrar por cliente Arrendador 
			bmoCustomer = new BmoCustomer();
			BmFilter filterLesse = new BmFilter();
			filterLesse.setValueFilter(bmoCustomer.getKind(),bmoCustomer.getCustomercategory(),""+ BmoCustomer.CATEGORY_LESSEE);
			customerSuggestBox.addFilter(filterLesse);

		}

		@Override
		public void saveNext() {
			if (isMaster()) {
				if (newRecord && getBmObject().getId() > 0) {
					// El inmueble es recientemente creado
					edit(bmoProperty);
					list();
				} else list();
			}
		}

		@Override
		public void populateFields() {
			bmoProperty = (BmoProperty)getBmObject();

			formFlexTable.addSectionLabel(0, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoProperty.getCode());
			formFlexTable.addField(2, 0, propertyModelSuggestBox, bmoProperty.getPropertyModelId());
			if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
				formFlexTable.addField(3, 0, developmentBlockSuggestBox, bmoProperty.getDevelopmentBlockId());

			formFlexTable.addField(4, 0, lotTextBox, bmoProperty.getLot());
			formFlexTable.addField(5, 0, developmentRegistrySuggestBox, bmoProperty.getDevelopmentRegistryId());	
			formFlexTable.addField(6, 0, descriptionTextArea, bmoProperty.getDescription());
			formFlexTable.addField(7, 0, streetTextArea, bmoProperty.getStreet());
			formFlexTable.addField(8, 0, numberTextBox, bmoProperty.getNumber());

			formFlexTable.addField(9, 0, neighborhoodTextBox, bmoProperty.getNeighborhood());
			formFlexTable.addField(10, 0, betweenStreetsTextArea, bmoProperty.getBetweenStreets());
			formFlexTable.addField(11, 0, zipTextBox, bmoProperty.getZip());
			formFlexTable.addField(12, 0, citySuggestBox, bmoProperty.getCityId());

			formFlexTable.addField(13, 0, registryNumberTextBox, bmoProperty.getRegistryNumber());
			if (!newRecord) {
				if (getSFParams().hasRead((new BmoPropertyTax()).getProgramCode().toString() )) {
					//if (getSFParams().hasSpecialAccess(BmoProperty.ACCESS_ADDPROPERTYTA)) 
					formFlexTable.addField(14, 0, new UiPropertyTaxLabelList(getUiParams(), id));
				}
			}
			formFlexTable.addField(15, 0, extraPriceTextBox, bmoProperty.getExtraPrice());
			formFlexTable.addField(16, 0, coordinatesTextBox, bmoProperty.getCoordinates());
			formFlexTable.addField(17, 0, customerSuggestBox, bmoProperty.getCustomerId());
			formFlexTable.addField(18, 0, companyUiListBox, bmoProperty.getCompanyId());	

			formFlexTable.addSectionLabel(19, 0, documentsSection, 2);
			formFlexTable.addField(20, 0, facadeFileUpload, bmoProperty.getFacade());	
			formFlexTable.addField(21, 0, blueprintFileUpload, bmoProperty.getBlueprint());				
			formFlexTable.addField(22, 0, contractFileUpload, bmoProperty.getContract());	
			formFlexTable.addField(23, 0, propertyReceiptFileUpload, bmoProperty.getPropertyReceipt());	
			formFlexTable.addField(24, 0, propertyTitleFileUpload, bmoProperty.getPropertyTitle());	
			formFlexTable.addField(25, 0, constitutiveActFileUpload, bmoProperty.getConstitutiveAct());	
			if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
				formFlexTable.addField(26, 0, certifiedWritingFileUpload, bmoProperty.getCertifiedWriting());
				formFlexTable.addField(27, 0, demarcationFileUpload, bmoProperty.getDemarcation());
				formFlexTable.addField(28, 0, notaryQuotationFileUpload, bmoProperty.getNotaryQuotation());
				formFlexTable.addField(29, 0, appraiseFileUpload, bmoProperty.getAppraise());
				formFlexTable.addField(30, 0, debtCertificateFileUpload, bmoProperty.getDebtCertificate());
				formFlexTable.addField(31, 0, taxCertificateFileUpload, bmoProperty.getTaxCertificate());
				formFlexTable.addField(32, 0, waterBillFileUpload, bmoProperty.getWaterBill());
				formFlexTable.addField(33, 0, electricityBillFileUpload, bmoProperty.getElectricityBill());
				formFlexTable.addField(34, 0, otherDocumentsFileUpload, bmoProperty.getOtherDocuments());			
			}
			if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
				formFlexTable.addSectionLabel(35, 0, renewOrderSection, 2);
				formFlexTable.addField(36, 0, renewOrderCheckBox, bmoProperty.getRenewOrder());
			}

			formFlexTable.addSectionLabel(37, 0, progressSection, 2); 
			formFlexTable.addField(38, 0, dueDateDateBox, bmoProperty.getDueDate());
			formFlexTable.addField(39, 0, progressTextBox, bmoProperty.getProgress());
			formFlexTable.addField(40, 0, finishDateDateBox, bmoProperty.getFinishDate()); 
			formFlexTable.addField(41, 0, cansellCheckBox, bmoProperty.getCansell()); 
			formFlexTable.addField(42, 0, habitabilityCheckBox, bmoProperty.getHabitability());			
			formFlexTable.addField(43, 0, tagBox, bmoProperty.getTags());
			if (!newRecord) {
				BmoRequisition bmoRequisition = new BmoRequisition();
				FlowPanel requisitionFP = new FlowPanel();
				BmFilter filterRequisition = new BmFilter();
				filterRequisition.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getPropertyId(), bmoProperty.getId());
				getUiParams().setForceFilter(bmoRequisition.getProgramCode(), filterRequisition);
				UiRequisition uiRequisition = new UiRequisition(getUiParams(), requisitionFP, bmoProperty);
				setUiType(bmoRequisition.getProgramCode(), UiParams.MINIMALIST);
				uiRequisition.show();
				formFlexTable.addPanel(44, 0, requisitionFP, 2);
			}
			

			formFlexTable.addSectionLabel(45, 0, measurementSection, 2);
			formFlexTable.addField(46, 0, landSizeTextBox, bmoProperty.getLandSize());
			formFlexTable.addField(47, 0, publicLandSizeTextBox, bmoProperty.getPublicLandSize());
			formFlexTable.addField(48, 0, constructionSizeTextBox, bmoProperty.getConstructionSize());
			formFlexTable.addField(49, 0, adjoinsTextArea, bmoProperty.getAdjoins());

			statusEffect();
		}

		public void statusEffect() {
			if (!newRecord) 
				getPropertyModel(bmoProperty.getPropertyModelId().toInteger());
			else {
				if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
					formFlexTable.hideField(developmentBlockSuggestBox);
				formFlexTable.hideField(lotTextBox);
				formFlexTable.hideField(developmentRegistrySuggestBox);
				formFlexTable.hideField(registryNumberTextBox);
				formFlexTable.hideField(habitabilityCheckBox);
				formFlexTable.hideField(customerSuggestBox);
				formFlexTable.hideField(dueDateDateBox);
				formFlexTable.hideField(finishDateDateBox);
				formFlexTable.hideField(progressTextBox);
				formFlexTable.hideField(publicLandSizeTextBox);
				if(getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger() > 0)
					companyUiListBox.setSelectedId(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());
			}

			companyUiListBox.setEnabled(false);
			if (getSFParams().hasSpecialAccess(BmoProperty.ACCESS_CHANGECOMPANY)) 
				companyUiListBox.setEnabled(true);
		}

		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == developmentBlockSuggestBox) {
				companyUiListBox.setSelectedId(((BmoDevelopmentBlock)developmentBlockSuggestBox.getSelectedBmObject()).getBmoDevelopmentPhase().getCompanyId().toString());
			}
			else if(uiSuggestBox == propertyModelSuggestBox) {
				BmoPropertyModel bmoPropertyModel = ((BmoPropertyModel) propertyModelSuggestBox.getSelectedBmObject());
				// IMPORTANTE: colocar lo mismo (ocultar/mostrar) en el asincrono getPropertyModel
				if (bmoPropertyModel.getBmoPropertyType().getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
					// Ocultar
					if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
						formFlexTable.hideField(developmentBlockSuggestBox);
					formFlexTable.hideField(lotTextBox);
					formFlexTable.hideField(developmentRegistrySuggestBox);
					formFlexTable.hideField(registryNumberTextBox);
					formFlexTable.hideField(habitabilityCheckBox);
					formFlexTable.hideField(dueDateDateBox);
					formFlexTable.hideField(finishDateDateBox);
					formFlexTable.hideField(progressTextBox);
					formFlexTable.hideField(publicLandSizeTextBox);
					// Mostrar
					formFlexTable.showField(customerSuggestBox);
					formFlexTable.showField(bmoProperty.getFacade());
					formFlexTable.showField(bmoProperty.getBlueprint());
					formFlexTable.showField(bmoProperty.getContract());
					formFlexTable.showField(bmoProperty.getPropertyReceipt());
					formFlexTable.showField(bmoProperty.getPropertyTitle());
					formFlexTable.showField(bmoProperty.getConstitutiveAct());
					if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
						formFlexTable.showSection(renewOrderSection);
						formFlexTable.showField(renewOrderCheckBox);
						renewOrderCheckBox.setValue(true);
					}
					cansellCheckBox.setValue(true);
				} else {
					// Mostrar
					if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
						formFlexTable.showField(developmentBlockSuggestBox);
					formFlexTable.showField(lotTextBox);
					formFlexTable.showField(developmentRegistrySuggestBox);
					formFlexTable.showField(registryNumberTextBox);
					formFlexTable.showField(habitabilityCheckBox);
					formFlexTable.showField(dueDateDateBox);
					formFlexTable.showField(finishDateDateBox);
					formFlexTable.showField(progressTextBox);
					formFlexTable.showField(publicLandSizeTextBox);
					// Ocultar
					formFlexTable.hideField(customerSuggestBox);
					formFlexTable.hideField(bmoProperty.getFacade());
					formFlexTable.hideField(bmoProperty.getBlueprint());
					formFlexTable.hideField(bmoProperty.getContract());
					formFlexTable.hideField(bmoProperty.getPropertyReceipt());
					formFlexTable.hideField(bmoProperty.getPropertyTitle());
					formFlexTable.hideField(bmoProperty.getConstitutiveAct());
					if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
						formFlexTable.hideSection(renewOrderSection);
						formFlexTable.hideField(renewOrderCheckBox);
						renewOrderCheckBox.setValue(false);
					}
					cansellCheckBox.setValue(false);
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProperty.setId(id);
			bmoProperty.getCode().setValue(codeTextBox.getText());
			bmoProperty.getLot().setValue(lotTextBox.getText());
			bmoProperty.getDescription().setValue(descriptionTextArea.getText());
			bmoProperty.getStreet().setValue(streetTextArea.getText());
			bmoProperty.getNumber().setValue(numberTextBox.getText());
			bmoProperty.getNeighborhood().setValue(neighborhoodTextBox.getText());
			bmoProperty.getZip().setValue(zipTextBox.getText());
			bmoProperty.getBetweenStreets().setValue(betweenStreetsTextArea.getText());
			bmoProperty.getCityId().setValue(citySuggestBox.getSelectedId());
			bmoProperty.getProgress().setValue(progressTextBox.getText());
			bmoProperty.getCansell().setValue(cansellCheckBox.getValue());
			bmoProperty.getHabitability().setValue(habitabilityCheckBox.getValue());
			bmoProperty.getFinishDate().setValue(finishDateDateBox.getTextBox().getText());
			bmoProperty.getDueDate().setValue(dueDateDateBox.getTextBox().getText());
			bmoProperty.getLandSize().setValue(landSizeTextBox.getText());
			bmoProperty.getPublicLandSize().setValue(publicLandSizeTextBox.getText());
			bmoProperty.getConstructionSize().setValue(constructionSizeTextBox.getText());
			bmoProperty.getRegistryNumber().setValue(registryNumberTextBox.getText());
			bmoProperty.getAdjoins().setValue(adjoinsTextArea.getText());
			bmoProperty.getExtraPrice().setValue(extraPriceTextBox.getText());
			bmoProperty.getPropertyModelId().setValue(propertyModelSuggestBox.getSelectedId());
			if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
				bmoProperty.getDevelopmentBlockId().setValue(developmentBlockSuggestBox.getSelectedId());
			bmoProperty.getDevelopmentRegistryId().setValue(developmentRegistrySuggestBox.getSelectedId());
			bmoProperty.getCoordinates().setValue(coordinatesTextBox.getText());
			bmoProperty.getFacade().setValue(facadeFileUpload.getBlobKey());
			bmoProperty.getBlueprint().setValue(blueprintFileUpload.getBlobKey());
			bmoProperty.getCompanyId().setValue(companyUiListBox.getSelectedId());

			bmoProperty.getContract().setValue(contractFileUpload.getBlobKey());
			bmoProperty.getPropertyReceipt().setValue(propertyReceiptFileUpload.getBlobKey());
			bmoProperty.getPropertyTitle().setValue(propertyTitleFileUpload.getBlobKey());
			bmoProperty.getConstitutiveAct().setValue(constitutiveActFileUpload.getBlobKey());

			bmoProperty.getRenewOrder().setValue(renewOrderCheckBox.getValue());
			bmoProperty.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoProperty.getCertifiedWriting().setValue(certifiedWritingFileUpload.getBlobKey());
			bmoProperty.getDemarcation().setValue(demarcationFileUpload.getBlobKey());
			bmoProperty.getNotaryQuotation().setValue(notaryQuotationFileUpload.getBlobKey());
			bmoProperty.getAppraise().setValue(appraiseFileUpload.getBlobKey());
			bmoProperty.getDebtCertificate().setValue(debtCertificateFileUpload.getBlobKey());
			bmoProperty.getTaxCertificate().setValue(taxCertificateFileUpload.getBlobKey());
			bmoProperty.getWaterBill().setValue(waterBillFileUpload.getBlobKey());
			bmoProperty.getElectricityBill().setValue(electricityBillFileUpload.getBlobKey());
			bmoProperty.getOtherDocuments().setValue(otherDocumentsFileUpload.getBlobKey());
			bmoProperty.getTags().setValue(tagBox.getTagList());
			return bmoProperty;
		}

		@Override
		public void close() {
//			showSystemMessage("tipo: "+ getUiParams().getUiType(getBmObject().getProgramCode()) + " |ob;"+getBmObject().getProgramCode());

//			if (isSlave()) {
//				//dialogClose();
//			} else
//				list();
		}

		public void getPropertyModel(int propertyModelId) {
			getPropertyModel(0, propertyModelId);
		}

		public void getPropertyModel(int getPropertyModelRpcAttemp, int propertyModelId) {
			if (getPropertyModelRpcAttemp < 5) {
				setPropertyModelId(propertyModelId);
				setGetPropertyModelRpcAttemp(getPropertyModelRpcAttemp + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getGetPropertyModelRpcAttemp() < 5) 
							getPropertyModel(getGetPropertyModelRpcAttemp(), getPropertyModelId());
						else 
							showErrorMessage(this.getClass().getName() + "-getPropertyModel() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setGetPropertyModelRpcAttemp(0);
						bmoPropertyModel = (BmoPropertyModel)result;
						if (bmoPropertyModel.getBmoPropertyType().getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
							// Ocultar
							if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
								formFlexTable.hideField(developmentBlockSuggestBox);
							formFlexTable.hideField(lotTextBox);
							formFlexTable.hideField(developmentRegistrySuggestBox);
							formFlexTable.hideField(registryNumberTextBox);
							formFlexTable.hideField(habitabilityCheckBox);
							formFlexTable.hideField(dueDateDateBox);
							formFlexTable.hideField(finishDateDateBox);
							formFlexTable.hideField(progressTextBox);
							formFlexTable.hideField(publicLandSizeTextBox);

							// Mostrar
							formFlexTable.showField(customerSuggestBox);
							formFlexTable.showField(bmoProperty.getContract());
							formFlexTable.showField(bmoProperty.getPropertyReceipt());
							formFlexTable.showField(bmoProperty.getPropertyTitle());
							formFlexTable.showField(bmoProperty.getConstitutiveAct());
							if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
								formFlexTable.showSection(renewOrderSection);
								formFlexTable.showField(renewOrderCheckBox);
								renewOrderCheckBox.setValue(true);
							}
						} else {
							// Mostrar
							if (getSFParams().hasRead((new BmoDevelopmentBlock()).getProgramCode().toString() ))
								formFlexTable.showField(developmentBlockSuggestBox);
							formFlexTable.showField(lotTextBox);
							formFlexTable.showField(developmentRegistrySuggestBox);
							formFlexTable.showField(registryNumberTextBox);
							formFlexTable.showField(habitabilityCheckBox);
							formFlexTable.showField(dueDateDateBox);
							formFlexTable.showField(finishDateDateBox);
							formFlexTable.showField(progressTextBox);
							formFlexTable.showField(publicLandSizeTextBox);
							// Ocultar
							formFlexTable.hideField(customerSuggestBox);
							formFlexTable.hideField(bmoProperty.getContract());
							formFlexTable.hideField(bmoProperty.getPropertyReceipt());
							formFlexTable.hideField(bmoProperty.getPropertyTitle());
							formFlexTable.hideField(bmoProperty.getConstitutiveAct());
							if (getSFParams().isFieldEnabled(bmoProperty.getRenewOrder())) {
								formFlexTable.hideSection(renewOrderSection);
								formFlexTable.hideField(renewOrderCheckBox);
								renewOrderCheckBox.setValue(false);
							}
						}
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoPropertyModel.getPmClass(), propertyModelId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getPropertyModel(): ERROR " + e.toString());
				}
			}
		}

		public int getGetPropertyModelRpcAttemp() {
			return getPropertyModelRpcAttemp;
		}

		public void setGetPropertyModelRpcAttemp(int getPropertyModelRpcAttemp) {
			this.getPropertyModelRpcAttemp = getPropertyModelRpcAttemp;
		}

		public int getPropertyModelId() {
			return propertyModelId;
		}

		public void setPropertyModelId(int propertyModelId) {
			this.propertyModelId = propertyModelId;
		}
	}
}
