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


import com.flexwm.shared.in.BmoPolicyStatus;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiPolicyStatusList extends UiList {

	public UiPolicyStatusList(UiParams uiParams) {
		super(uiParams, new BmoPolicyStatus());
	}
	
	public UiPolicyStatusList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPolicyStatus());
	}
	
	@Override
	public void create() {
		UiPolicyStatusForm uiPolicyStatusForm = new UiPolicyStatusForm(getUiParams(), 0);
		uiPolicyStatusForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiPolicyStatusForm uiPolicyStatusForm = new UiPolicyStatusForm(getUiParams(), bmObject.getId());
		uiPolicyStatusForm.show();
	}
}


