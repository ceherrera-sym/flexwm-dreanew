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

import com.flexwm.client.ac.UiSessionReview.UiSessionReviewForm.SessionReviewUpdater;
import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.flexwm.shared.ac.BmoProgramSessionSubLevel;
import com.flexwm.shared.ac.BmoSessionReview;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiProgramSessionSubLevel extends UiList {
	BmoProgramSessionSubLevel bmoProgramSessionSubLevel;

	int sessionReviewId;
	BmoSessionReview bmoSessionReview;
	SessionReviewUpdater sessionReviewUpdater;

	public UiProgramSessionSubLevel(UiParams uiParams, Panel defaultPanel, BmoSessionReview bmoSessionReview, int id, SessionReviewUpdater sessionReviewUpdater) {
		super(uiParams, defaultPanel, new BmoProgramSessionSubLevel());
		sessionReviewId = id;
		bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
		this.bmoSessionReview = bmoSessionReview;
		this.sessionReviewUpdater = sessionReviewUpdater;
	}
	
	@Override
	public void postShow() {
		newImage.setVisible(false);
	}

	@Override
	public void create() {
		UiProgramSessionSubLevelForm uiProgramSessionSubLevelForm = new UiProgramSessionSubLevelForm(getUiParams(), 0);
		uiProgramSessionSubLevelForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProgramSessionSubLevelForm uiProgramSessionSubLevelForm = new UiProgramSessionSubLevelForm(getUiParams(), bmObject.getId());
		uiProgramSessionSubLevelForm.show();
	}

	private class UiProgramSessionSubLevelForm extends UiFormDialog {
		TextBox sequenceTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea observationTextArea = new TextArea();
		UiListBox programSessionLevelUiListBox = new UiListBox(getUiParams(), new BmoProgramSessionLevel());
		CheckBox progressCheckBox = new CheckBox();

		public UiProgramSessionSubLevelForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProgramSessionSubLevel(), id);
		}

		@Override
		public void populateFields() {
			bmoProgramSessionSubLevel = (BmoProgramSessionSubLevel)getBmObject();
			formFlexTable.addField(1, 0, programSessionLevelUiListBox, bmoProgramSessionSubLevel.getProgramSessionLevelId());
			formFlexTable.addField(2, 0, sequenceTextBox, bmoProgramSessionSubLevel.getSequence());
			formFlexTable.addField(3, 0, nameTextBox, bmoProgramSessionSubLevel.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoProgramSessionSubLevel.getDescription());
			formFlexTable.addField(5, 0, progressCheckBox, bmoProgramSessionSubLevel.getProgress());
			formFlexTable.addField(6, 0, observationTextArea, bmoProgramSessionSubLevel.getObservation());
			
			statusEffect();
		}
		
		public void statusEffect () {
			sequenceTextBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			programSessionLevelUiListBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProgramSessionSubLevel.setId(id);
			bmoProgramSessionSubLevel.getSessionReviewId().setValue(sessionReviewId);
			bmoProgramSessionSubLevel.getProgramSessionLevelId().setValue(programSessionLevelUiListBox.getSelectedId());
			bmoProgramSessionSubLevel.getSequence().setValue(sequenceTextBox.getText());
			bmoProgramSessionSubLevel.getName().setValue(nameTextBox.getText());
			bmoProgramSessionSubLevel.getDescription().setValue(descriptionTextArea.getText());
			bmoProgramSessionSubLevel.getProgress().setValue(progressCheckBox.getValue());
			bmoProgramSessionSubLevel.getObservation().setValue(observationTextArea.getText());
			
			return bmoProgramSessionSubLevel;
		}

		@Override
		public void close() {
			list();

			if (sessionReviewUpdater != null)
				sessionReviewUpdater.update();
		}
	}
}
