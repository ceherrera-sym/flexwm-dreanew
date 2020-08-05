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

import com.flexwm.shared.cm.BmoCustomerRelative;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerRelativeLabelList extends UiFormLabelList {
	BmoCustomerRelative bmoCustomerRelative;
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox fullNameTextBox = new TextBox();
	TextBox fatherLastNameTextBox = new TextBox();
	TextBox motherLastNameTextBox = new TextBox();
	TextBox emailTextBox = new TextBox();
	TextBox numberTextBox = new TextBox();
	TextBox cellPhoneTextBox = new TextBox();
	TextBox extensionTextBox = new TextBox();
	CheckBox responsibleCheckBox = new CheckBox();
	int userId;
	

	public UiCustomerRelativeLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerRelative());
		userId = id;
		bmoCustomerRelative = new BmoCustomerRelative();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerRelative.getKind(), 
				bmoCustomerRelative.getCustomerId().getName(), 
				bmoCustomerRelative.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerRelative.getCustomerId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoCustomerRelative = (BmoCustomerRelative)getBmObject();	
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerRelative.getType());
		formFlexTable.addField(2, 0, fullNameTextBox, bmoCustomerRelative.getFullName());
		formFlexTable.addField(3, 0, fatherLastNameTextBox, bmoCustomerRelative.getFatherLastName());
		formFlexTable.addField(4, 0, motherLastNameTextBox, bmoCustomerRelative.getMotherLastName());
		formFlexTable.addField(5, 0, numberTextBox, bmoCustomerRelative.getNumber());
		formFlexTable.addField(6, 0, extensionTextBox, bmoCustomerRelative.getExtension());
		formFlexTable.addField(7, 0, cellPhoneTextBox, bmoCustomerRelative.getCellPhone());
		formFlexTable.addField(8, 0, emailTextBox, bmoCustomerRelative.getEmail());
		formFlexTable.addField(9, 0, responsibleCheckBox, bmoCustomerRelative.getResponsible());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerRelative = new BmoCustomerRelative();
		bmoCustomerRelative.setId(id);
		bmoCustomerRelative.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerRelative.getFullName().setValue(fullNameTextBox.getText());
		bmoCustomerRelative.getFatherLastName().setValue(fatherLastNameTextBox.getText());
		bmoCustomerRelative.getMotherLastName().setValue(motherLastNameTextBox.getText());
		bmoCustomerRelative.getEmail().setValue(emailTextBox.getText());
		bmoCustomerRelative.getNumber().setValue(numberTextBox.getText());
		bmoCustomerRelative.getCellPhone().setValue(cellPhoneTextBox.getText());
		bmoCustomerRelative.getExtension().setValue(extensionTextBox.getText());
		bmoCustomerRelative.getCustomerId().setValue(userId);	
		bmoCustomerRelative.getResponsible().setValue(responsibleCheckBox.getValue());
		return bmoCustomerRelative;
	}
}
