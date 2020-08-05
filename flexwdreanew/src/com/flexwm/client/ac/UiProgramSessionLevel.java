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

import com.flexwm.shared.ac.BmoProgramSession;
import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiProgramSessionLevel extends UiList {
	BmoProgramSessionLevel bmoProgramSessionLevel;

	public UiProgramSessionLevel(UiParams uiParams) {
		super(uiParams, new BmoProgramSessionLevel());
		bmoProgramSessionLevel = (BmoProgramSessionLevel)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoProgramSession()), bmoProgramSessionLevel.getBmoProgramSession());
	}

	@Override
	public void create() {
		UiProgramSessionLevelForm uiProgramSessionLevelForm = new UiProgramSessionLevelForm(getUiParams(), 0);
		uiProgramSessionLevelForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProgramSessionLevelForm uiProgramSessionLevelForm = new UiProgramSessionLevelForm(getUiParams(), bmObject.getId());
		uiProgramSessionLevelForm.show();
	}

	public class UiProgramSessionLevelForm extends UiFormDialog {

		BmoProgramSessionLevel bmoProgramSessionLevel;

		TextBox sequenceTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox programSessionUiListBox = new UiListBox(getUiParams(), new BmoProgramSession());

		String levelTypesSection = "Características del Nivel";

		public UiProgramSessionLevelForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProgramSessionLevel(), id);
		}

		@Override
		public void populateFields() {
			bmoProgramSessionLevel = (BmoProgramSessionLevel)getBmObject();
			formFlexTable.addField(1, 0, sequenceTextBox, bmoProgramSessionLevel.getSequence());
			formFlexTable.addField(2, 0, nameTextBox, bmoProgramSessionLevel.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoProgramSessionLevel.getDescription());
			formFlexTable.addField(4, 0, programSessionUiListBox, bmoProgramSessionLevel.getProgramSessionId());

			if (!newRecord) {
				formFlexTable.addSectionLabel(5, 0, levelTypesSection, 2);
				formFlexTable.addField(6, 0, new UiProgramSessionSubLevelTypeLabelList(getUiParams(), id));	
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProgramSessionLevel.setId(id);
			bmoProgramSessionLevel.getSequence().setValue(sequenceTextBox.getText());
			bmoProgramSessionLevel.getName().setValue(nameTextBox.getText());
			bmoProgramSessionLevel.getDescription().setValue(descriptionTextArea.getText());
			bmoProgramSessionLevel.getProgramSessionId().setValue(programSessionUiListBox.getSelectedId());
			return bmoProgramSessionLevel;
		}

		@Override
		public void close() {
			list();
		}
	}
}
