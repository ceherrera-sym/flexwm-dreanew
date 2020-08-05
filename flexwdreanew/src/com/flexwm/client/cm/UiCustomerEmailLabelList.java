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

import com.flexwm.shared.cm.BmoCustomerEmail;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerEmailLabelList extends UiFormLabelList {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoCustomerEmail bmoCustomerEmail;
	
	TextBox emailTextBox = new TextBox();
	int customerId;
	

	public UiCustomerEmailLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerEmail());
		customerId = id;
		bmoCustomerEmail = new BmoCustomerEmail();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerEmail.getKind(), 
				bmoCustomerEmail.getCustomerId().getName(), 
				bmoCustomerEmail.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerEmail.getCustomerId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoCustomerEmail = (BmoCustomerEmail)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerEmail.getType());
		formFlexTable.addField(2, 0, emailTextBox, bmoCustomerEmail.getEmail());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerEmail = new BmoCustomerEmail();
		bmoCustomerEmail.setId(id);
		bmoCustomerEmail.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerEmail.getEmail().setValue(emailTextBox.getText());
		bmoCustomerEmail.getCustomerId().setValue(customerId);		
		return bmoCustomerEmail;
	}
}
