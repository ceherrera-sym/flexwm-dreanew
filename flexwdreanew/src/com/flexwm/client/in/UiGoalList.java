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


import com.flexwm.shared.in.BmoGoal;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiGoalList extends UiList {

	public UiGoalList(UiParams uiParams) {
		super(uiParams, new BmoGoal());
	}
	
	public UiGoalList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoGoal());
	}
	
	@Override
	public void create() {
		UiGoalForm uiGoalForm = new UiGoalForm(getUiParams(), 0);
		uiGoalForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiGoalForm uiGoalForm = new UiGoalForm(getUiParams(), bmObject.getId());
		uiGoalForm.show();
	}
}


