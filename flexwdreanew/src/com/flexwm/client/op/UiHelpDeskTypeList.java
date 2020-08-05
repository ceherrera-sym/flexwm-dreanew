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

import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoHelpDeskType;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;

/**
 * @author smuniz
 *
 */
public class UiHelpDeskTypeList extends UiList {
	BmoHelpDeskType bmoHelpDeskType;

	public UiHelpDeskTypeList(UiParams uiParams) {
		super(uiParams, new BmoHelpDeskType());
		bmoHelpDeskType = (BmoHelpDeskType)getBmObject();
	}
	
	@Override
	public void postShow(){
		//
	}
	
	@Override
	public void create() {
		UiHelpDeskTypeForm uiHelpDeskTypeForm = new UiHelpDeskTypeForm(getUiParams(), 0);
		uiHelpDeskTypeForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiHelpDeskTypeForm uiHelpDeskTypeForm = new UiHelpDeskTypeForm(getUiParams(), bmObject.getId());
		uiHelpDeskTypeForm.show();
	}
}
