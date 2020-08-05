/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;


import com.flexwm.shared.in.BmoDiscount;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiDiscountList extends UiList {

	public UiDiscountList(UiParams uiParams) {
		super(uiParams, new BmoDiscount());
	}
	
	public UiDiscountList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoDiscount());
	}
	
	@Override
	public void create() {
		UiDiscountForm uiDiscountForm = new UiDiscountForm(getUiParams(), 0);
		uiDiscountForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiDiscountForm uiDiscountForm = new UiDiscountForm(getUiParams(), bmObject.getId());
		uiDiscountForm.show();
	}
}


