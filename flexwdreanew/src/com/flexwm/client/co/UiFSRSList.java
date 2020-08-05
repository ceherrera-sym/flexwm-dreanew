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
import com.flexwm.shared.co.BmoFSRS;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */
public class UiFSRSList extends UiList {
	BmoFSRS bmoFSRS;

	public UiFSRSList(UiParams uiParams) {
		super(uiParams, new BmoFSRS());
		bmoFSRS = (BmoFSRS)getBmObject();
	}
	
	@Override
	public void postShow(){
		//addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopment()), bmoFSRS.getBmoDevelopment());
	}
	
	@Override
	public void create() {
		UiFSRSForm uiFSRSForm = new UiFSRSForm(getUiParams(), 0);
		uiFSRSForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiFSRSForm uiFSRSForm = new UiFSRSForm(getUiParams(), bmObject.getId());
		uiFSRSForm.show();
	}
}
