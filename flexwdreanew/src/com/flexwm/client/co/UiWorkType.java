package com.flexwm.client.co;

import com.flexwm.shared.co.BmoWorkType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiWorkType extends UiList {

	public UiWorkType(UiParams uiParams) {
		super(uiParams, new BmoWorkType());
	}

	@Override
	public void create() {
		UiWorkTypeForm uiWorkTypeForm = new UiWorkTypeForm(getUiParams(), 0);
		uiWorkTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWorkTypeForm uiWorkTypeForm = new UiWorkTypeForm(getUiParams(), bmObject.getId());
		uiWorkTypeForm.show();
	}

	public class UiWorkTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		BmoWorkType bmoWorkType;

		public UiWorkTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWorkType(), id);
		}

		@Override
		public void populateFields(){
			bmoWorkType = (BmoWorkType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoWorkType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoWorkType.getDescription());
			formFlexTable.addField(3, 0, typeListBox, bmoWorkType.getType());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWorkType.setId(id);
			bmoWorkType.getName().setValue(nameTextBox.getText());
			bmoWorkType.getDescription().setValue(descriptionTextArea.getText());
			bmoWorkType.getType().setValue(typeListBox.getSelectedCode());
			return bmoWorkType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
