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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.cv.BmoCourse;
import com.flexwm.shared.cv.BmoTrainingSession;
import com.flexwm.shared.cv.BmoUserSession;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiTrainingSession extends UiList {
	BmoTrainingSession bmoTrainingSession;

	public UiTrainingSession(UiParams uiParams) {
		super(uiParams, new BmoTrainingSession());
		bmoTrainingSession = (BmoTrainingSession)getBmObject();
	}

	@Override
	public void postShow(){
		//
	}

	@Override
	public void create() {
		UiTrainingSessionForm uiTrainingSessionForm = new UiTrainingSessionForm(getUiParams(), 0);
		uiTrainingSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiTrainingSessionForm uiTrainingSessionForm = new UiTrainingSessionForm(getUiParams(), bmObject.getId());
		uiTrainingSessionForm.show();
	}

	public class UiTrainingSessionForm extends UiFormDialog {
		UiSuggestBox courseIdUiSuggestBox = new UiSuggestBox(new BmoCourse());
		TextArea locationNameTextArea = new TextArea();
		UiDateBox dateDateBox = new UiDateBox();
		TextBox timeTextBox = new TextBox();
		TextBox timeEndTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();

		BmoTrainingSession bmoTrainingSession;
		TrainingSessionUpdater trainingSessionUpdater = new TrainingSessionUpdater();
		String itemSection = "Items";

		public UiTrainingSessionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoTrainingSession(), id); 
		}

		@Override
		public void populateFields(){
			bmoTrainingSession = (BmoTrainingSession)getBmObject();
			formFlexTable.addField(1, 0, courseIdUiSuggestBox, bmoTrainingSession.getCourseId());
			formFlexTable.addField(2, 0, locationNameTextArea, bmoTrainingSession.getLocationName());
			formFlexTable.addField(3, 0, dateDateBox, bmoTrainingSession.getDate());
			formFlexTable.addField(4, 0, timeTextBox, bmoTrainingSession.getTime());
			formFlexTable.addField(5, 0, timeEndTextBox, bmoTrainingSession.getTimeEnd());
			formFlexTable.addField(6, 0, descriptionTextArea, bmoTrainingSession.getDescription());

			if (!newRecord) {

				// Items
				formFlexTable.addSectionLabel(7, 0, itemSection, 2);
				BmoUserSession bmoUserSession = new BmoUserSession();
				FlowPanel userSessionFP = new FlowPanel();
				BmFilter filterUserSession = new BmFilter();
				filterUserSession.setValueFilter(bmoUserSession.getKind(), bmoUserSession.getTrainingSessionId(), bmoTrainingSession.getId());
				getUiParams().setForceFilter(bmoUserSession.getProgramCode(), filterUserSession);
				UiUserSession uiUserSession = new UiUserSession(getUiParams(), userSessionFP, bmoTrainingSession, bmoTrainingSession.getId(), trainingSessionUpdater);
				setUiType(bmoUserSession.getProgramCode(), UiParams.MINIMALIST);
				uiUserSession.show();
				formFlexTable.addPanel(8, 0, userSessionFP, 2);

				//	SE DESHABILITA POR PROBLEMAS CON FOREIGN KEY DE USUARIOS PORQUE TRABAJA CON LA FUNCIONALIDAD
				//	HABILITAR CUANDO SE AGREGUE LA FUNCIONALIDAD
//				BmoUserSession bmoUserSessionAdditional = new BmoUserSession();
//				FlowPanel userSessionAdditionalFP = new FlowPanel();
//				BmFilter filterUserSessionAdditional = new BmFilter();
//				filterUserSessionAdditional.setValueFilter(bmoUserSessionAdditional.getKind(), bmoUserSessionAdditional.getTrainingSessionId(), bmoTrainingSession.getId());
//				getUiParams().setForceFilter(bmoUserSessionAdditional.getProgramCode(), filterUserSessionAdditional);
//				UiUserSessionAdditional uiUserSessionAdditional = new UiUserSessionAdditional(getUiParams(), userSessionAdditionalFP, bmoTrainingSession, bmoTrainingSession.getId(), trainingSessionUpdater);
//				setUiType(bmoUserSession.getProgramCode(), UiParams.MINIMALIST);
//				uiUserSessionAdditional.show();
//				
//				formFlexTable.addPanel(9, 0, userSessionAdditionalFP, 2);	
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoTrainingSession.setId(id);
			bmoTrainingSession.getCourseId().setValue(courseIdUiSuggestBox.getSelectedId()); 
			bmoTrainingSession.getLocationName().setValue(locationNameTextArea.getText());
			bmoTrainingSession.getDate().setValue(dateDateBox.getTextBox().getText());
			bmoTrainingSession.getTime().setValue(timeTextBox.getText());
			bmoTrainingSession.getDescription().setValue(descriptionTextArea.getText());
			bmoTrainingSession.getTimeEnd().setValue(timeEndTextBox.getText());

			return bmoTrainingSession;
		}

		@Override
		public void close() {
			list();
		}

		public class TrainingSessionUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
