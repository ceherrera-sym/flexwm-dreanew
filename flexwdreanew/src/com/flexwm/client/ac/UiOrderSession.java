/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.flexwm.client.ac.UiSession.UiSessionForm.SessionUpdater;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiOrderSession extends UiList {
	private UiOrderSessionForm uiOrderSessionForm;
	
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	BmoOrderSession bmoOrderSession;
	BmoSession bmoSession;
	TextBox accountTextBox = new TextBox();
	CheckBox attendedCheckBox = new CheckBox();
	int sessionId;
	protected SessionUpdater sessionUpdater;

	public UiOrderSession(UiParams uiParams, Panel defaultPanel, BmoSession bmoSession, int id, SessionUpdater sessionUpdater) {
		super(uiParams, defaultPanel, new BmoOrderSession());
		sessionId = id;
		bmoOrderSession = new BmoOrderSession();
		this.bmoSession = bmoSession;
		this.sessionUpdater = sessionUpdater;
	}
	
	

	@Override
	public void create() {
		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), 0);
		uiOrderSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoOrderSession = (BmoOrderSession)bmObject;
		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), bmoOrderSession.getId());
		uiOrderSessionForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {		
		uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), bmObject.getId());
		uiOrderSessionForm.show();
	}
	
	private class UiOrderSessionForm extends UiFormDialog {

		public UiOrderSessionForm(UiParams uiParams, int id) {			
			super(uiParams, new BmoOrderSession(), id);			
		}
		
		@Override
		public void populateFields() {
			bmoOrderSession = (BmoOrderSession)getBmObject();
			formFlexTable.addField(1, 0, orderSuggestBox, bmoOrderSession.getOrderId());
			formFlexTable.addField(2, 0, attendedCheckBox, bmoOrderSession.getAttended());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderSession = new BmoOrderSession();
			bmoOrderSession.setId(id);
			bmoOrderSession.getOrderId().setValue(orderSuggestBox.getSelectedId());
			bmoOrderSession.getSessionId().setValue(sessionId);	
			bmoOrderSession.getAttended().setValue(attendedCheckBox.getValue());
			return bmoOrderSession;
		}		

		@Override
		public void close() {
			list();

			if (sessionUpdater != null)
				sessionUpdater.update();
		}

	}
}
