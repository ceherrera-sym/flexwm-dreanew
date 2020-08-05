package com.flexwm.client.op;

import com.flexwm.shared.op.BmoReqPayType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiReqPayType extends UiList {

	public UiReqPayType(UiParams uiParams) {
		super(uiParams, new BmoReqPayType());
	}

	@Override
	public void create() {
		UiReqPayTypeForm uiReqPayTypeForm = new UiReqPayTypeForm(getUiParams(), 0);
		uiReqPayTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiReqPayTypeForm uiReqPayTypeForm = new UiReqPayTypeForm(getUiParams(), bmObject.getId());
		uiReqPayTypeForm.show();
	}

	public class UiReqPayTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox daysTextBox = new TextBox();
		BmoReqPayType boReqPayType;

		public UiReqPayTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoReqPayType(), id);
		}

		@Override
		public void populateFields() {
			boReqPayType = (BmoReqPayType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boReqPayType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boReqPayType.getDescription());
			formFlexTable.addField(3, 0, daysTextBox, boReqPayType.getDays());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boReqPayType.setId(id);
			boReqPayType.getName().setValue(nameTextBox.getText());
			boReqPayType.getDescription().setValue(descriptionTextArea.getText());
			boReqPayType.getDays().setValue(daysTextBox.getText());
			return boReqPayType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
