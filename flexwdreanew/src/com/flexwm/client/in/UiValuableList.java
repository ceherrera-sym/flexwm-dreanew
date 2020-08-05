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


import com.flexwm.shared.in.BmoValuable;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiValuableList extends UiList {

	public UiValuableList(UiParams uiParams) {
		super(uiParams, new BmoValuable());
	}
	
	public UiValuableList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoValuable());
	}
	
	@Override
	public void create() {
		UiValuableForm uiValuableForm = new UiValuableForm(getUiParams(), 0);
		uiValuableForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiValuableForm uiValuableForm = new UiValuableForm(getUiParams(), bmObject.getId());
		uiValuableForm.show();
	}
}


