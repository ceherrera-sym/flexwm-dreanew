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

import com.flexwm.shared.op.BmoSupplierContact;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiSupplierContactLabelList extends UiFormLabelList {

	BmoSupplierContact bmoSupplierContact;
	TextBox firstNameTextBox = new TextBox();
	TextBox fatherLastNameTextBox = new TextBox();
	TextBox motherLastNameTextBox = new TextBox();
	TextBox positionTextBox = new TextBox();
	TextBox aliasTextBox = new TextBox();
	TextArea commentsTextArea = new TextArea();
	TextBox emailTextBox = new TextBox();
	TextBox numberTextBox = new TextBox();
	TextBox cellPhoneTextBox = new TextBox();
	TextBox extensionTextBox = new TextBox();
	int supplierId;

	public UiSupplierContactLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierContact());
		supplierId = id;
		bmoSupplierContact = new BmoSupplierContact();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierContact.getKind(), 
				bmoSupplierContact.getSupplierId().getName(), 
				bmoSupplierContact.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierContact.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierContact = (BmoSupplierContact)getBmObject();	
		formFlexTable.addField(1, 0, firstNameTextBox, bmoSupplierContact.getFirstName());		
		formFlexTable.addField(2, 0, fatherLastNameTextBox, bmoSupplierContact.getFatherLastName());
		formFlexTable.addField(3, 0, motherLastNameTextBox, bmoSupplierContact.getMotherLastName());
		formFlexTable.addField(4, 0, aliasTextBox, bmoSupplierContact.getAlias());
		formFlexTable.addField(5, 0, positionTextBox, bmoSupplierContact.getPosition());
		formFlexTable.addField(6, 0, emailTextBox, bmoSupplierContact.getEmail());
		formFlexTable.addField(7, 0, numberTextBox, bmoSupplierContact.getNumber());
		formFlexTable.addField(8, 0, extensionTextBox, bmoSupplierContact.getExtension());
		formFlexTable.addField(9, 0, cellPhoneTextBox, bmoSupplierContact.getCellPhone());
		formFlexTable.addField(10, 0, commentsTextArea, bmoSupplierContact.getComments());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierContact = new BmoSupplierContact();
		bmoSupplierContact.setId(id);
		bmoSupplierContact.getFirstName().setValue(firstNameTextBox.getText());
		bmoSupplierContact.getFatherLastName().setValue(fatherLastNameTextBox.getText());
		bmoSupplierContact.getMotherLastName().setValue(motherLastNameTextBox.getText());
		bmoSupplierContact.getEmail().setValue(emailTextBox.getText());
		bmoSupplierContact.getPosition().setValue(positionTextBox.getText());
		bmoSupplierContact.getNumber().setValue(numberTextBox.getText());
		bmoSupplierContact.getExtension().setValue(extensionTextBox.getText());
		bmoSupplierContact.getSupplierId().setValue(supplierId);
		bmoSupplierContact.getAlias().setValue(aliasTextBox.getText());
		bmoSupplierContact.getCellPhone().setValue(cellPhoneTextBox.getText());
		bmoSupplierContact.getComments().setValue(commentsTextArea.getText());
		return bmoSupplierContact;
	}
}
