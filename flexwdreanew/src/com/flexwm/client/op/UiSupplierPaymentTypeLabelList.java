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
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.op.BmoSupplierPaymentType;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiSupplierPaymentTypeLabelList extends UiFormLabelList {

	BmoSupplierPaymentType bmoSupplierPaymentType;
	UiListBox currencyUiListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox paymentTypeUiListBox = new UiListBox(getUiParams(), new BmoPaymentType());
	int supplierId;

	public UiSupplierPaymentTypeLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierPaymentType());
		supplierId = id;
		bmoSupplierPaymentType = new BmoSupplierPaymentType();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierPaymentType.getKind(), 
				bmoSupplierPaymentType.getSupplierId().getName(), 
				bmoSupplierPaymentType.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierPaymentType.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierPaymentType = (BmoSupplierPaymentType)getBmObject();
		formFlexTable.addField(1, 0, paymentTypeUiListBox, bmoSupplierPaymentType.getPaymentTypeId());
		formFlexTable.addField(2, 0, currencyUiListBox, bmoSupplierPaymentType.getCurrencyId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierPaymentType = new BmoSupplierPaymentType();
		bmoSupplierPaymentType.setId(id);
		bmoSupplierPaymentType.getPaymentTypeId().setValue(paymentTypeUiListBox.getSelectedId());
		bmoSupplierPaymentType.getCurrencyId().setValue(currencyUiListBox.getSelectedId());
		bmoSupplierPaymentType.getSupplierId().setValue(supplierId);		
		return bmoSupplierPaymentType;
	}
}
