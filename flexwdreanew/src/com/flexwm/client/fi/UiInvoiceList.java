/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoInvoice;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

/**
 * @author jhernandez
 *
 */
public class UiInvoiceList extends UiList {
	public UiInvoiceList(UiParams uiParams) {
		super(uiParams, new BmoInvoice());
	}
	
	@Override
	public void create() {
		UiInvoiceForm uiInvoiceForm = new UiInvoiceForm(getUiParams(), 0);
		uiInvoiceForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiInvoiceForm uiInvoiceForm = new UiInvoiceForm(getUiParams(), bmObject.getId());
		uiInvoiceForm.show();
	}
}
