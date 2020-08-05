package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoPayMethod;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPayMethod extends UiList {

	public UiPayMethod(UiParams uiParams) {
		super(uiParams, new BmoPayMethod());
	}

	@Override
	public void create() {
		UiPayMerhodForm uiPayMethodForm = new UiPayMerhodForm(getUiParams(), 0);
		uiPayMethodForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPayMerhodForm uiPayMethodForm = new UiPayMerhodForm(getUiParams(), bmObject.getId());
		uiPayMethodForm.show();
	}

	public class UiPayMerhodForm extends UiFormDialog {
		TextBox codeSatTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoPayMethod bmoPayMethod;

		public UiPayMerhodForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPayMethod(), id);
		}

		@Override
		public void populateFields() {
			bmoPayMethod = (BmoPayMethod)getBmObject();
			formFlexTable.addField(1, 0, codeSatTextBox, bmoPayMethod.getCodeSAT());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoPayMethod.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPayMethod.setId(id);
			bmoPayMethod.getCodeSAT().setValue(codeSatTextBox.getText());
			bmoPayMethod.getDescription().setValue(descriptionTextArea.getText());
			return bmoPayMethod;
		}

		@Override
		public void close() {
			list();
		}
	}
}
