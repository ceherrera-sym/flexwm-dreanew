package com.flexwm.client.op;

import com.flexwm.shared.op.BmoProductGroup;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiProductGroup extends UiList {

	public UiProductGroup(UiParams uiParams) {
		super(uiParams, new BmoProductGroup());
	}

	@Override
	public void create() {
		UiProductGroupForm uiProductGroupForm = new UiProductGroupForm(getUiParams(), 0);
		uiProductGroupForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProductGroupForm uiProductGroupForm = new UiProductGroupForm(getUiParams(), bmObject.getId());
		uiProductGroupForm.show();
	}

	public class UiProductGroupForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoProductGroup boProductGroup;

		public UiProductGroupForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductGroup(), id);
		}

		@Override
		public void populateFields(){
			boProductGroup = (BmoProductGroup)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boProductGroup.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boProductGroup.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boProductGroup.setId(id);
			boProductGroup.getName().setValue(nameTextBox.getText());
			boProductGroup.getDescription().setValue(descriptionTextArea.getText());
			return boProductGroup;
		}

		@Override
		public void close() {
			list();
		}
	}

}
