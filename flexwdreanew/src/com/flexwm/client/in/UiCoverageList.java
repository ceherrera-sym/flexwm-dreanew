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


import com.flexwm.shared.in.BmoCoverage;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiCoverageList extends UiList {

	public UiCoverageList(UiParams uiParams) {
		super(uiParams, new BmoCoverage());
	}
	
	public UiCoverageList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoCoverage());
	}
	
	@Override
	public void create() {
		UiCoverageForm uiCoverageForm = new UiCoverageForm(getUiParams(), 0);
		uiCoverageForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiCoverageForm uiCoverageForm = new UiCoverageForm(getUiParams(), bmObject.getId());
		uiCoverageForm.show();
	}
}


