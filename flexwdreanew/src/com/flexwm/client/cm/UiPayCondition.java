package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoPayCondition;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPayCondition extends UiList {

	public UiPayCondition(UiParams uiParams) {
		super(uiParams, new BmoPayCondition());
	}

	@Override
	public void create() {
		UiPayConditionForm uiPayConditionForm = new UiPayConditionForm(getUiParams(), 0);
		uiPayConditionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPayConditionForm uiPayConditionForm = new UiPayConditionForm(getUiParams(), bmObject.getId());
		uiPayConditionForm.show();
	}

	public class UiPayConditionForm extends UiFormDialog {
		TextBox codeSatTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoPayCondition bmoPayCondition;

		public UiPayConditionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPayCondition(), id);
		}

		@Override
		public void populateFields() {
			bmoPayCondition = (BmoPayCondition)getBmObject();
			formFlexTable.addField(1, 0, codeSatTextBox, bmoPayCondition.getCode());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoPayCondition.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPayCondition.setId(id);
			bmoPayCondition.getCode().setValue(codeSatTextBox.getText());
			bmoPayCondition.getDescription().setValue(descriptionTextArea.getText());
			return bmoPayCondition;
		}

		@Override
		public void close() {
			list();
		}
	}
}