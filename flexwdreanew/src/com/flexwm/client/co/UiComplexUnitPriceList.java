/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoComplexUnitPrice;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;

/**
 * @author smuniz
 *
 */
public class UiComplexUnitPriceList extends UiList {
	BmoComplexUnitPrice bmoComplexUnitPrice;

	public UiComplexUnitPriceList(UiParams uiParams) {
		super(uiParams, new BmoComplexUnitPrice());
		bmoComplexUnitPrice = (BmoComplexUnitPrice)getBmObject();
	}
	
	@Override
	public void postShow(){
		//addStaticFilterListBox(new UiListBox(bmoDevelopmentType.getCode()), bmoDevelopmentType, bmoDevelopmentType.getCode());
	}
	
	@Override
	public void create() {
		UiComplexUnitPriceForm uiComplexUnitPriceForm = new UiComplexUnitPriceForm(getUiParams(), 0);
		uiComplexUnitPriceForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiComplexUnitPriceForm uiComplexUnitPriceForm = new UiComplexUnitPriceForm(getUiParams(), bmObject.getId());
		uiComplexUnitPriceForm.show();
	}
}
