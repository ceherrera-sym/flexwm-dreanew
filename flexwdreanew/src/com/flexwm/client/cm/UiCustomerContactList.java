package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;


public class UiCustomerContactList extends UiList{
	private UiCustomerContactForm uiCustomerContactForm;
	BmoCustomerContact bmoCustomerContact;
	
	Image batchContactPricesImage;
	public UiCustomerContactList(UiParams uiParams) {
		super(uiParams, new BmoCustomerContact());
		bmoCustomerContact = (BmoCustomerContact)getBmObject();	
		
		batchContactPricesImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/batch.png"));
		batchContactPricesImage.setTitle("Actualizar contactos");
		batchContactPricesImage.setStyleName("listSearchImage");
		batchContactPricesImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					showBatch();
				}
			}
		});
						
	}
	public void showBatch() {
		String url = "/batch/batch_updatecontacts.jsp";
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}	
	
	public void create() {
		uiCustomerContactForm = new UiCustomerContactForm(getUiParams(), 0);
		uiCustomerContactForm.show();
	}
	@Override
	public void postShow() {
		if(isMaster()) {
			getUiParams().getUiProgramParams(bmoCustomerContact.getProgramCode()).clearFilters();
			localButtonPanel.add(batchContactPricesImage);
		}
		addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoUser(), bmoCustomerContact.getCustomerId());
		if(!isMobile()) {
			
		}
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiCustomerContactForm uiCustomerContactForm = new UiCustomerContactForm(getUiParams(), bmObject.getId());
		uiCustomerContactForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiCustomerContactForm uiCustomerContactForm = new UiCustomerContactForm(getUiParams(), bmObject.getId());
		uiCustomerContactForm.show();
	}

	public UiCustomerContactForm getUiCustomerContactForm() {
		uiCustomerContactForm = new UiCustomerContactForm(getUiParams(), 0);
		return uiCustomerContactForm;
	}
	public class UiCustomerContactForm extends UiFormDialog {
		BmoCustomerContact bmoCustomerContact;
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		TextBox fullNameTextBox = new TextBox();
		TextBox fatherLastNameTextBox = new TextBox();
		TextBox motherLastNameTextBox = new TextBox();
		TextBox positionTextBox = new TextBox();
		TextBox aliasTextBox = new TextBox();
		TextArea commentAliasTextArea = new TextArea();
		TextBox emailTextBox = new TextBox();
		TextBox numberTextBox = new TextBox();
		TextBox cellPhoneTextBox = new TextBox();
		TextBox extensionTextBox = new TextBox();
		CheckBox contactMainCheckBox = new CheckBox();
		
		public UiCustomerContactForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomerContact(), id);
			bmoCustomerContact = (BmoCustomerContact)getBmObject();
		}
		@Override
		public void populateFields(){
			bmoCustomerContact = (BmoCustomerContact)getBmObject();	

			if(!newRecord)customerSuggestBox.setEnabled(false);
			formFlexTable.addField(0, 0, customerSuggestBox, bmoCustomerContact.getCustomerId());
			formFlexTable.addField(1, 0, fullNameTextBox, bmoCustomerContact.getFullName());		
			formFlexTable.addField(2, 0, fatherLastNameTextBox, bmoCustomerContact.getFatherLastName());
			formFlexTable.addField(3, 0, motherLastNameTextBox, bmoCustomerContact.getMotherLastName());
			formFlexTable.addField(4, 0, aliasTextBox, bmoCustomerContact.getAlias());
			formFlexTable.addField(5, 0, positionTextBox, bmoCustomerContact.getPosition());
			formFlexTable.addField(6, 0, emailTextBox, bmoCustomerContact.getEmail());
			formFlexTable.addField(7, 0, numberTextBox, bmoCustomerContact.getNumber());
			formFlexTable.addField(8, 0, extensionTextBox, bmoCustomerContact.getExtension());
			formFlexTable.addField(9, 0, cellPhoneTextBox, bmoCustomerContact.getCellPhone());
			formFlexTable.addField(10, 0, contactMainCheckBox, bmoCustomerContact.getContactMain());
			formFlexTable.addField(11, 0, commentAliasTextArea, bmoCustomerContact.getCommentAlias());
		
		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomerContact = new BmoCustomerContact();
			bmoCustomerContact.setId(id);
			bmoCustomerContact.getFullName().setValue(fullNameTextBox.getText());
			bmoCustomerContact.getFatherLastName().setValue(fatherLastNameTextBox.getText());
			bmoCustomerContact.getMotherLastName().setValue(motherLastNameTextBox.getText());
			bmoCustomerContact.getEmail().setValue(emailTextBox.getText());
			bmoCustomerContact.getPosition().setValue(positionTextBox.getText());
			bmoCustomerContact.getNumber().setValue(numberTextBox.getText());
			bmoCustomerContact.getExtension().setValue(extensionTextBox.getText());
			bmoCustomerContact.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoCustomerContact.getAlias().setValue(aliasTextBox.getText());
			bmoCustomerContact.getCellPhone().setValue(cellPhoneTextBox.getText());
			bmoCustomerContact.getContactMain().setValue(contactMainCheckBox.getValue());
			bmoCustomerContact.getCommentAlias().setValue(commentAliasTextArea.getText());
			return bmoCustomerContact;
		}
		@Override
		public void close() {
			list();
		}
		
	}
	

}
