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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoUser;


/**
 * @author smuniz
 *
 */

public class UiUserSession extends UiList {

	BmoUserSession bmoUserSession;
	BmoTrainingSession bmoTrainingSession;
	protected TrainingSessionUpdater trainingSessionUpdater;
	int trainingSessionId;


	public UiUserSession(UiParams uiParams, Panel defaultPanel, BmoTrainingSession bmoTrainingSession, int trainingSessionId, TrainingSessionUpdater trainingSessionUpdater) {
		super(uiParams, defaultPanel, new BmoUserSession());
		bmoUserSession = (BmoUserSession)getBmObject();
		this.trainingSessionId = trainingSessionId;
		this.bmoTrainingSession = bmoTrainingSession;
		this.trainingSessionUpdater = trainingSessionUpdater;
	}

	@Override
	public void create() {
		UiUserSessionForm uiUserSessionForm = new UiUserSessionForm(getUiParams(), 0);
		uiUserSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiUserSessionForm uiUserSessionForm = new UiUserSessionForm(getUiParams(), bmObject.getId());
		uiUserSessionForm.show();
	}

	public class UiUserSessionForm extends UiFormDialog {
		UiSuggestBox userUiSuggestBox = new UiSuggestBox(new BmoUser());
		CheckBox attendedCheckBox = new CheckBox();

		public UiUserSessionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoUserSession(), id); 
		}

		@Override
		public void populateFields(){
			bmoUserSession = (BmoUserSession)getBmObject();

			// Filtrar por usuarios activos
			BmoUser bmoUser = new BmoUser();
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userUiSuggestBox.addFilter(filterUserActive);
			formFlexTable.addField(1, 0, userUiSuggestBox, bmoUserSession.getUserId());
			formFlexTable.addField(2, 0, attendedCheckBox, bmoUserSession.getAttended());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoUserSession.setId(id);
			if(trainingSessionId > 0) bmoUserSession.getTrainingSessionId().setValue(trainingSessionId);
			bmoUserSession.getUserId().setValue(userUiSuggestBox.getSelectedId());
			bmoUserSession.getAttended().setValue(attendedCheckBox.getValue());	

			return bmoUserSession;
		}

		@Override
		public void close() {
			list();

			if (trainingSessionUpdater != null)
				trainingSessionUpdater.update();
		}
	}
}
