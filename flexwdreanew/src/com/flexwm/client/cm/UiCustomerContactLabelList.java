/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.client.cm.UiCustomer.UiCustomerForm.CustomerUpdater;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoTitle;


public class UiCustomerContactLabelList extends UiFormLabelList {
	BmoCustomerContact bmoCustomerContact;
	
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
	UiListBox titleUiListBox = new UiListBox(getUiParams(), new BmoTitle());
	
	int customerId;
	protected CustomerUpdater customerUpdater;

	public UiCustomerContactLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerContact());
		customerId = id;
		bmoCustomerContact = new BmoCustomerContact();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerContact.getKind(), 
				bmoCustomerContact.getCustomerId().getName(), 
				bmoCustomerContact.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerContact.getCustomerId().getName());
	}
	
	public UiCustomerContactLabelList(UiParams uiParams, int id, CustomerUpdater customerUpdater) {
		super(uiParams, new BmoCustomerContact());
		customerId = id;
		bmoCustomerContact = new BmoCustomerContact();
		this.customerUpdater = customerUpdater;
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerContact.getKind(), 
				bmoCustomerContact.getCustomerId().getName(), 
				bmoCustomerContact.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerContact.getCustomerId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoCustomerContact = (BmoCustomerContact)getBmObject();	
		formFlexTable.addField(1, 0, titleUiListBox, bmoCustomerContact.getTitleId());		
		formFlexTable.addField(2, 0, fullNameTextBox, bmoCustomerContact.getFullName());		
		formFlexTable.addField(3, 0, fatherLastNameTextBox, bmoCustomerContact.getFatherLastName());
		formFlexTable.addField(4, 0, motherLastNameTextBox, bmoCustomerContact.getMotherLastName());
		formFlexTable.addField(5, 0, aliasTextBox, bmoCustomerContact.getAlias());
		formFlexTable.addField(6, 0, positionTextBox, bmoCustomerContact.getPosition());
		formFlexTable.addField(7, 0, emailTextBox, bmoCustomerContact.getEmail());
		formFlexTable.addField(8, 0, numberTextBox, bmoCustomerContact.getNumber());
		formFlexTable.addField(9, 0, extensionTextBox, bmoCustomerContact.getExtension());
		formFlexTable.addField(10, 0, cellPhoneTextBox, bmoCustomerContact.getCellPhone());
		formFlexTable.addField(11, 0, contactMainCheckBox, bmoCustomerContact.getContactMain());
		formFlexTable.addField(12, 0, commentAliasTextArea, bmoCustomerContact.getCommentAlias());
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
		bmoCustomerContact.getCustomerId().setValue(customerId);
		bmoCustomerContact.getAlias().setValue(aliasTextBox.getText());
		bmoCustomerContact.getCellPhone().setValue(cellPhoneTextBox.getText());
		bmoCustomerContact.getContactMain().setValue(contactMainCheckBox.getValue());
		bmoCustomerContact.getCommentAlias().setValue(commentAliasTextArea.getText());
		bmoCustomerContact.getTitleId().setValue(titleUiListBox.getSelectedId());
		return bmoCustomerContact;
	}
	
	@Override
	protected void reset() {
		saveButton.setText("AGREGAR");
		id = 0;
		deleteButton.setVisible(false);
		msgPanel.setVisible(false);
		try {
			getBmObject().reset();
		} catch (BmException e) {
			showFormMsg("Error en el Refrescado de la Forma", this.getClass() + "-reset() " + e.toString());
		}
		listPanel.clear();
		formFlexTable.clear();
		populateFields();
		formFlexTable.addButtonPanel(buttonPanel);
		list();

		formDialogBox.hide();
		
		// Actualizar datos
		updateCustomerForm();
	}
	
	// Actualizar datos del cliente
	public void updateCustomerForm() {
		if (customerUpdater != null)
			customerUpdater.update();
	}
}
