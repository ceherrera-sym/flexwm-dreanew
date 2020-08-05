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

import com.flexwm.shared.cm.BmoCustomerPhone;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerPhoneLabelList extends UiFormLabelList {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoCustomerPhone bmoCustomerPhone;
	
	TextBox numberTextBox = new TextBox();
	TextBox extensionTextBox = new TextBox();
	int customerId;
	

	public UiCustomerPhoneLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerPhone());
		customerId = id;
		bmoCustomerPhone = new BmoCustomerPhone();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerPhone.getKind(), 
				bmoCustomerPhone.getCustomerId().getName(), 
				bmoCustomerPhone.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerPhone.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerPhone = (BmoCustomerPhone)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerPhone.getType());
		formFlexTable.addField(2, 0, numberTextBox, bmoCustomerPhone.getNumber());
		
		formFlexTable.addField(3, 0, extensionTextBox, bmoCustomerPhone.getExtension());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerPhone = new BmoCustomerPhone();
		bmoCustomerPhone.setId(id);
		bmoCustomerPhone.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerPhone.getNumber().setValue(numberTextBox.getText());
		bmoCustomerPhone.getExtension().setValue(extensionTextBox.getText());
		bmoCustomerPhone.getCustomerId().setValue(customerId);		
		return bmoCustomerPhone;
	}
}
