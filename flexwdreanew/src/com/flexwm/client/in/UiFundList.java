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


import com.flexwm.shared.in.BmoFund;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiFundList extends UiList {

	public UiFundList(UiParams uiParams) {
		super(uiParams, new BmoFund());
	}
	
	public UiFundList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoFund());
	}
	
	@Override
	public void create() {
		UiFundForm uiFundForm = new UiFundForm(getUiParams(), 0);
		uiFundForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiFundForm uiFundForm = new UiFundForm(getUiParams(), bmObject.getId());
		uiFundForm.show();
	}
}


