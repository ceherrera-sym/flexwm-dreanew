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

import com.flexwm.shared.cm.BmoCustomerBankAccount;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerBankAccountLabelList extends UiFormLabelList {
	BmoCustomerBankAccount bmoCustomerBankAccount;
	TextBox bankTextBox = new TextBox();
	TextBox clabeTextBox = new TextBox();
	TextBox accountNumberTextBox = new TextBox();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	int customerId;
	

	public UiCustomerBankAccountLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerBankAccount());
		customerId = id;
		bmoCustomerBankAccount = new BmoCustomerBankAccount();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerBankAccount.getKind(), 
				bmoCustomerBankAccount.getCustomerId().getName(), 
				bmoCustomerBankAccount.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerBankAccount.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerBankAccount = (BmoCustomerBankAccount)getBmObject();
		formFlexTable.addField(1, 0, bankTextBox, bmoCustomerBankAccount.getBank());
		formFlexTable.addField(2, 0, clabeTextBox, bmoCustomerBankAccount.getClabe());
		formFlexTable.addField(3, 0, accountNumberTextBox, bmoCustomerBankAccount.getAccountNumber());
		formFlexTable.addField(4, 0, currencyListBox, bmoCustomerBankAccount.getCurrencyId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerBankAccount = new BmoCustomerBankAccount();
		bmoCustomerBankAccount.setId(id);
		bmoCustomerBankAccount.getBank().setValue(bankTextBox.getText());
		bmoCustomerBankAccount.getClabe().setValue(clabeTextBox.getText());
		bmoCustomerBankAccount.getAccountNumber().setValue(accountNumberTextBox.getText());
		bmoCustomerBankAccount.getCustomerId().setValue(customerId);	
		bmoCustomerBankAccount.getCurrencyId().setValue(currencyListBox.getSelectedId());
		return bmoCustomerBankAccount;
	}
}
