/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.flexwm.client.cv.UiTrainingSession.UiTrainingSessionForm.TrainingSessionUpdater;
import com.flexwm.shared.cv.BmoTrainingSession;
import com.flexwm.shared.cv.BmoUserSession;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


/**
 * @author smuniz
 *
 */

public class UiUserSessionAdditional extends UiList {

	BmoTrainingSession bmoTrainingSession;
	BmoUserSession bmoUserSession;
	protected TrainingSessionUpdater trainingSessionUpdater;
	int trainingSessionId;

	public UiUserSessionAdditional(UiParams uiParams, Panel defaultPanel, BmoTrainingSession bmoTrainingSession, int trainingSessionId, TrainingSessionUpdater trainingSessionUpdater) {
		super(uiParams, defaultPanel, new BmoUserSession());
		bmoUserSession = (BmoUserSession)getBmObject();
		this.trainingSessionId = trainingSessionId;
		this.bmoTrainingSession = bmoTrainingSession;
		this.trainingSessionUpdater = trainingSessionUpdater;
	}

	@Override
	public void create() {
		UiUserSessionAdditionalForm uiUserSessionAdditionalForm = new UiUserSessionAdditionalForm(getUiParams(), 0);
		uiUserSessionAdditionalForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiUserSessionAdditionalForm uiUserSessionAdditionalForm = new UiUserSessionAdditionalForm(getUiParams(), bmObject.getId());
		uiUserSessionAdditionalForm.show();
	}

	public class UiUserSessionAdditionalForm extends UiFormDialog {
		UiSuggestBox additionalSessionUiSuggestBox = new UiSuggestBox(new BmoTrainingSession());


		public UiUserSessionAdditionalForm(UiParams uiParams, int id) {
			super(uiParams, new BmoUserSession(), id); 
		}
		@Override
		public void populateFields(){
			bmoUserSession = (BmoUserSession)getBmObject();
			formFlexTable.addField(1, 0, additionalSessionUiSuggestBox, bmoUserSession.getAdditionalSessionId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoUserSession.setId(id);
			if(trainingSessionId > 0) bmoUserSession.getTrainingSessionId().setValue(trainingSessionId);
			bmoUserSession.getAdditionalSessionId().setValue(additionalSessionUiSuggestBox.getSelectedId());

			return bmoUserSession;
		}
	}
}
