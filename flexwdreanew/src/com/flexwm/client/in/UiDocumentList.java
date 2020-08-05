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


import com.flexwm.shared.in.BmoDocument;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiDocumentList extends UiList {

	public UiDocumentList(UiParams uiParams) {
		super(uiParams, new BmoDocument());
	}
	
	public UiDocumentList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoDocument());
	}
	
	@Override
	public void create() {
		UiDocumentForm uiDocumentForm = new UiDocumentForm(getUiParams(), 0);
		uiDocumentForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiDocumentForm uiDocumentForm = new UiDocumentForm(getUiParams(), bmObject.getId());
		uiDocumentForm.show();
	}
}


