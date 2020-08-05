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


import com.flexwm.shared.in.BmoCondition;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiConditionList extends UiList {

	public UiConditionList(UiParams uiParams) {
		super(uiParams, new BmoCondition());
	}
	
	public UiConditionList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoCondition());
	}
	
	@Override
	public void create() {
		UiConditionForm uiConditionForm = new UiConditionForm(getUiParams(), 0);
		uiConditionForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiConditionForm uiConditionForm = new UiConditionForm(getUiParams(), bmObject.getId());
		uiConditionForm.show();
	}
}


