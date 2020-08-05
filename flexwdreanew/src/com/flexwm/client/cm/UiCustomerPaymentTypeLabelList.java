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

import com.flexwm.shared.cm.BmoCustomerPaymentType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaymentType;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerPaymentTypeLabelList extends UiFormLabelList {
	BmoCustomerPaymentType bmoCustomerPaymentType;
	
	UiListBox currencyUiListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox paymentTypeUiListBox = new UiListBox(getUiParams(), new BmoPaymentType());

	int customerId;
	
	public UiCustomerPaymentTypeLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerPaymentType());
		customerId = id;
		bmoCustomerPaymentType = new BmoCustomerPaymentType();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerPaymentType.getKind(), 
				bmoCustomerPaymentType.getCustomerId().getName(), 
				bmoCustomerPaymentType.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerPaymentType.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerPaymentType = (BmoCustomerPaymentType)getBmObject();
		formFlexTable.addField(1, 0, paymentTypeUiListBox, bmoCustomerPaymentType.getPaymentTypeId());
		formFlexTable.addField(2, 0, currencyUiListBox, bmoCustomerPaymentType.getCurrencyId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerPaymentType = new BmoCustomerPaymentType();
		bmoCustomerPaymentType.setId(id);
		bmoCustomerPaymentType.getPaymentTypeId().setValue(paymentTypeUiListBox.getSelectedId());
		bmoCustomerPaymentType.getCurrencyId().setValue(currencyUiListBox.getSelectedId());
		bmoCustomerPaymentType.getCustomerId().setValue(customerId);		
		return bmoCustomerPaymentType;
	}
}
