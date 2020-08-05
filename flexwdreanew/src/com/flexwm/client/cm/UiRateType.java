package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoRateType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiRateType extends UiList {

	public UiRateType(UiParams uiParams) {
		super(uiParams, new BmoRateType());
	}

	@Override
	public void create() {
		UiRateTypeForm uiRateTypeForm = new UiRateTypeForm(getUiParams(), 0);
		uiRateTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRateTypeForm uiRateTypeForm = new UiRateTypeForm(getUiParams(), bmObject.getId());
		uiRateTypeForm.show();
	}

	public class UiRateTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoRateType bmoRateType;

		public UiRateTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRateType(), id);
		}

		@Override
		public void populateFields(){
			bmoRateType = (BmoRateType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoRateType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoRateType.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRateType.setId(id);
			bmoRateType.getName().setValue(nameTextBox.getText());
			bmoRateType.getDescription().setValue(descriptionTextArea.getText());
			return bmoRateType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
