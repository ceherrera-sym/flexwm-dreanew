package com.flexwm.client.ac;

import com.flexwm.shared.ac.BmoProgramSessionSubLevelType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiProgramSessionSubLevelTypeLabelList extends UiFormLabelList {

	BmoProgramSessionSubLevelType bmoProgramSessionSubLevelType;
	TextBox nameTextBox = new TextBox();
	TextBox sequenceTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	int programSessionLevelId;

	public UiProgramSessionSubLevelTypeLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoProgramSessionSubLevelType());
		programSessionLevelId = id;
		bmoProgramSessionSubLevelType = new BmoProgramSessionSubLevelType();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoProgramSessionSubLevelType.getKind(), 
				bmoProgramSessionSubLevelType.getProgramSessionLevelId().getName(),
				bmoProgramSessionSubLevelType.getProgramSessionLevelId().getLabel(), 
				BmFilter.EQUALS, 
				id, 
				bmoProgramSessionSubLevelType.getProgramSessionLevelId().getName());
	}

	@Override
	public void populateFields() {
		bmoProgramSessionSubLevelType = (BmoProgramSessionSubLevelType)getBmObject();
		formFlexTable.addField(1, 0, sequenceTextBox, bmoProgramSessionSubLevelType.getSequence());
		formFlexTable.addField(2, 0, nameTextBox, bmoProgramSessionSubLevelType.getName());
		formFlexTable.addField(3, 0, descriptionTextArea, bmoProgramSessionSubLevelType.getDescription());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoProgramSessionSubLevelType = (BmoProgramSessionSubLevelType)getBmObject();
		bmoProgramSessionSubLevelType.setId(id);
		bmoProgramSessionSubLevelType.getSequence().setValue(sequenceTextBox.getText());
		bmoProgramSessionSubLevelType.getName().setValue(nameTextBox.getText());
		bmoProgramSessionSubLevelType.getDescription().setValue(descriptionTextArea.getText());
		bmoProgramSessionSubLevelType.getProgramSessionLevelId().setValue(programSessionLevelId);

		return bmoProgramSessionSubLevelType;
	}
}
