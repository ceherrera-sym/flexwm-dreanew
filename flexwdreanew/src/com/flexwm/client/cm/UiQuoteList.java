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

import com.symgae.shared.BmObject;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;

public class UiQuoteList extends UiList {
	BmoQuote bmoQuote;

	public UiQuoteList(UiParams uiParams) {
		super(uiParams, new BmoQuote());
		bmoQuote = (BmoQuote)getBmObject();
	}
	
	@Override
	public void postShow(){
		addFilterListBox(new UiListBox(getUiParams(), new BmoOrderType()), new BmoOrderType());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoQuote.getStatus()), bmoQuote, bmoQuote.getStatus());
		addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoQuote.getCustomerId());
	}
	
	@Override
	public void create() {
		UiQuoteForm uiQuoteForm = new UiQuoteForm(getUiParams(), 0);
		uiQuoteForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiQuoteForm uiQuoteForm = new UiQuoteForm(getUiParams(), bmObject.getId());
		uiQuoteForm.show();
	}
}


