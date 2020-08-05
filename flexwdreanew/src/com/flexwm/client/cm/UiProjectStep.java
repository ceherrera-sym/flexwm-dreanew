///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.client.cm;
//
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.sf.BmoArea;
//import com.symgae.shared.sf.BmoCompany;
//import com.symgae.shared.sf.BmoUser;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.flexwm.shared.fi.BmoBudgetItem;
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.op.BmoOrderType;
//import com.flexwm.shared.wf.BmoWFlowType;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.symgae.client.ui.UiDateTimeBox;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//
//
//
//public class UiProjectStep extends UiList {
//	private UiProjectStepForm uiProjectStepForm;
//	BmoProjectStep bmoProjectStep;	
//	public UiProjectStep(UiParams uiParams) {
//		super(uiParams, new BmoProjectStep());
//		bmoProjectStep = (BmoProjectStep)getBmObject();	
//		
//	}
//	@Override
//	public void postShow() {
//		newImage.setVisible(false);
//	}
//	@Override
//	public void create() {
//		uiProjectStepForm = new UiProjectStepForm(getUiParams(), 0);
//		uiProjectStepForm.show();
//	}
//	@Override
//	public void open(BmObject bmObject) {
//		UiProjectStepDetail uiStepProjectDetail = new UiProjectStepDetail(getUiParams(), bmObject.getId());
//		uiStepProjectDetail.show();
//	}
//
//	@Override
//	public void edit(BmObject bmObject) {
//		uiProjectStepForm = new UiProjectStepForm(getUiParams(), bmObject.getId());
//		uiProjectStepForm.show();
//	}
//
//	public UiProjectStepForm getUiExternalSalesForm() {
//		uiProjectStepForm = new UiProjectStepForm(getUiParams(), 0);
//		return uiProjectStepForm;
//	}
//
//	public class UiProjectStepForm extends UiFormDialog {
//		String generalSection = "Datos Generales";	
////		String currencySection = "Moneda y Paridad";
////		String statusSection = "Estatus";
//		TextBox codeTextBox = new TextBox();
//		TextBox parityTextBox = new TextBox();
//		TextBox nameTextBox = new TextBox();
//		UiListBox wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
//		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
//		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
//		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
//		UiDateTimeBox lockStartDateTimeBox = new UiDateTimeBox();
//		UiDateTimeBox lockEndDateTimeBox = new UiDateTimeBox();	
//		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
//		UiListBox defaultBudgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
//		UiListBox defaultAreaUiListBox = new UiListBox(getUiParams(), new BmoArea());
//		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
//		UiListBox statusListBox = new UiListBox(getUiParams());
//		TextArea descriptionTextArea = new TextArea();
//
//		
//		BmoProjectStep bmoProjectStep;		
//	
//			
//		public UiProjectStepForm(UiParams uiParams, int id) {
//			super(uiParams, new BmoProjectStep(), id);
//			bmoProjectStep = (BmoProjectStep)getBmObject();			
//			
//		}
//
//		@Override
//		public void populateFields(){
//			bmoProjectStep = (BmoProjectStep)getBmObject();
//			codeTextBox.setEnabled(false);
//			formFlexTable.addSectionLabel(0, 0, generalSection, 1);
//			formFlexTable.addField(1, 0, codeTextBox,bmoProjectStep.getCode());
//			formFlexTable.addField(2, 0, nameTextBox,bmoProjectStep.getName());	
//			formFlexTable.addField(3, 0, descriptionTextArea, bmoProjectStep.getDescription());
//			formFlexTable.addField(4, 0, customerSuggestBox,bmoProjectStep.getCustomerId());
//			formFlexTable.addField(5, 0, userSuggestBox,bmoProjectStep.getUserId());
//			formFlexTable.addField(7, 0, lockStartDateTimeBox,bmoProjectStep.getLockStart());
//			formFlexTable.addField(8, 0, lockEndDateTimeBox,bmoProjectStep.getLockEnd());
//			formFlexTable.addField(9, 0, companyListBox, bmoProjectStep.getCompanyId());
////			formFlexTable.addField(10, 0, defaultBudgetItemUiListBox, bmoProjectStep.getDefaultBudgetItemId());
////			formFlexTable.addField(11, 0, defaultAreaUiListBox, bmoProjectStep.getDefaultAreaId());			
////			formFlexTable.addSectionLabel(15, 0, currencySection, 1);
////			formFlexTable.addField(16, 0, currencyListBox, bmoProjectStep.getCurrencyId());
////			formFlexTable.addField(17, 0, parityTextBox,bmoProjectStep.getCurrencyParity());
//			if((bmoProjectStep.getId() > 0)) {
////				formFlexTable.addSectionLabel(18, 0, statusSection, 1);
//				formFlexTable.addField(19, 0, statusListBox, bmoProjectStep.getStatus());
//			}
//		
//			statusEffect();
//		}
//		
//
//		@Override
//		public BmObject populateBObject() throws BmException {
//			bmoProjectStep.setId(id);
//			bmoProjectStep.getName().setValue(nameTextBox.getText());
//			bmoProjectStep.getCustomerId().setValue(customerSuggestBox.getSelectedId());
//			bmoProjectStep.getUserId().setValue(userSuggestBox.getSelectedId());
//			bmoProjectStep.getLockStart().setValue(lockStartDateTimeBox.getDateTime());
//			bmoProjectStep.getLockEnd().setValue(lockEndDateTimeBox.getDateTime());
//			bmoProjectStep.getCompanyId().setValue(companyListBox.getSelectedId());
////			bmoProjectStep.getDefaultBudgetItemId().setValue(defaultBudgetItemUiListBox.getSelectedId());
////			bmoProjectStep.getDefaultAreaId().setValue(defaultAreaUiListBox.getSelectedId());
////			bmoProjectStep.getCurrencyId().setValue(currencyListBox.getSelectedId());
//			bmoProjectStep.getStatus().setValue(statusListBox.getSelectedCode());	
////			bmoProjectStep.getDescription().setValue(descriptionTextArea.getText());
////			bmoProjectStep.getCurrencyParity().setValue(parityTextBox.getText());		
//			return bmoProjectStep;
//			
//		}
//		@Override
//		public void formListChange(ChangeEvent event) {
//			if (event.getSource() == statusListBox)
//				update("¿Desea cambiar el Estatus?");
//		}
//		public void statusEffect() {
//			if(bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_CANCELLED) || bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_AUTHORIZED) 
//					|| bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_FINISHED)) {
//				nameTextBox.setEnabled(false);
//				customerSuggestBox.setEnabled(false);
//				userSuggestBox.setEnabled(false);
//				lockStartDateTimeBox.setEnabled(false);
//				lockEndDateTimeBox.setEnabled(false);
//				companyListBox.setEnabled(false);
//			}else {
//				nameTextBox.setEnabled(true);
//				customerSuggestBox.setEnabled(true);
//				userSuggestBox.setEnabled(true);
//				lockStartDateTimeBox.setEnabled(true);
//				lockEndDateTimeBox.setEnabled(true);
//				companyListBox.setEnabled(true);
//			}
//			
//		}
//		@Override
//		public void close() {
//			if(deleted) new UiProjectStep(getUiParams()).show();		
//		}	
//		@Override
//		public void saveNext() {
//			if(newRecord) {
//				edit(bmoProjectStep);
//			}else {
//				UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), bmoProjectStep.getId());
//				uiProjectStepDetail.show();
//			}
//		}		
//		@Override
//		public void updateNext() {
//			get(id);
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), bmoProjectStep.getId());
//			uiProjectStepDetail.show();			
//		}
//	}
//}
//
//
