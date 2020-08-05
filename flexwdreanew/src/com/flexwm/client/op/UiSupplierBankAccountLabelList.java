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

import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoSupplierBankAccount;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiSupplierBankAccountLabelList extends UiFormLabelList {

	BmoSupplierBankAccount bmoSupplierBankAccount;
	TextBox bankTextBox = new TextBox();
	TextBox clabeTextBox = new TextBox();
	TextBox accountNumberTextBox = new TextBox();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	int customerId;


	public UiSupplierBankAccountLabelList(UiParams uiParams,  int id) {
		super(uiParams, new BmoSupplierBankAccount());
		customerId = id;
		bmoSupplierBankAccount = new BmoSupplierBankAccount();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierBankAccount.getKind(), 
				bmoSupplierBankAccount.getSupplierId().getName(), 
				bmoSupplierBankAccount.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierBankAccount.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierBankAccount = (BmoSupplierBankAccount)getBmObject();
		formFlexTable.addField(1, 0, bankTextBox, bmoSupplierBankAccount.getBank());
		formFlexTable.addField(2, 0, clabeTextBox, bmoSupplierBankAccount.getClabe());
		formFlexTable.addField(3, 0, accountNumberTextBox, bmoSupplierBankAccount.getAccountNumber());
		formFlexTable.addField(4, 0, currencyListBox, bmoSupplierBankAccount.getCurrencyId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierBankAccount = new BmoSupplierBankAccount();
		bmoSupplierBankAccount.setId(id);
		bmoSupplierBankAccount.getBank().setValue(bankTextBox.getText());
		bmoSupplierBankAccount.getClabe().setValue(clabeTextBox.getText());
		bmoSupplierBankAccount.getAccountNumber().setValue(accountNumberTextBox.getText());
		bmoSupplierBankAccount.getSupplierId().setValue(customerId);		
		bmoSupplierBankAccount.getCurrencyId().setValue(currencyListBox.getSelectedId());

		return bmoSupplierBankAccount;
	}
}
