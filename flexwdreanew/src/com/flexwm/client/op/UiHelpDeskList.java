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
import com.flexwm.shared.op.BmoHelpDesk;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */
public class UiHelpDeskList extends UiList {
	BmoHelpDesk bmoHelpDesk;

	public UiHelpDeskList(UiParams uiParams) {
		super(uiParams, new BmoHelpDesk());
		bmoHelpDesk = (BmoHelpDesk)getBmObject();
	}
	
	@Override
	public void postShow(){
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoHelpDesk.getStatus()), bmoHelpDesk, bmoHelpDesk.getStatus());
		addFilterListBox(new UiListBox(getUiParams(), bmoHelpDesk.getBmoHelpDeskType()) , bmoHelpDesk.getBmoHelpDeskType());
	}
	
	@Override
	public void create() {
		UiHelpDeskForm uiHelpDeskForm = new UiHelpDeskForm(getUiParams(), 0);
		uiHelpDeskForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiHelpDeskForm uiHelpDeskForm = new UiHelpDeskForm(getUiParams(), bmObject.getId());
		uiHelpDeskForm.show();
	}
}
