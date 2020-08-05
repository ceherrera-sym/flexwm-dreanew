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
import com.flexwm.shared.co.BmoConcept;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiConceptList extends UiList {
	BmoConcept bmoConcept;

	public UiConceptList(UiParams uiParams) {
		super(uiParams, new BmoConcept());
		bmoConcept = (BmoConcept)getBmObject();
	}

	@Override
	public void postShow() {
		//addFilterListBox(new UiListBox(getUiParams(), new BmoConceptGroup()), bmoConcept.getBmoConceptGroup());
	}

	@Override
	public void create() {
		UiConceptForm uiConceptForm = new UiConceptForm(getUiParams(), 0);
		uiConceptForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiConceptForm uiConceptForm = new UiConceptForm(getUiParams(), bmObject.getId());
		uiConceptForm.show();
	}
}
