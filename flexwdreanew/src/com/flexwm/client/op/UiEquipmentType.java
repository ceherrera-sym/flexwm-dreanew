package com.flexwm.client.op;

import com.flexwm.shared.op.BmoEquipmentType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiEquipmentType extends UiList {

	public UiEquipmentType(UiParams uiParams) {
		super(uiParams, new BmoEquipmentType());
	}

	@Override
	public void create() {
		UiEquipmentTypeForm uiEquipmentTypeForm = new UiEquipmentTypeForm(getUiParams(), 0);
		uiEquipmentTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiEquipmentTypeForm uiEquipmentTypeForm = new UiEquipmentTypeForm(getUiParams(), bmObject.getId());
		uiEquipmentTypeForm.show();
	}

	public class UiEquipmentTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoEquipmentType boEquipmentType;

		public UiEquipmentTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoEquipmentType(), id);
		}

		@Override
		public void populateFields() {
			boEquipmentType = (BmoEquipmentType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boEquipmentType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boEquipmentType.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boEquipmentType.setId(id);
			boEquipmentType.getName().setValue(nameTextBox.getText());
			boEquipmentType.getDescription().setValue(descriptionTextArea.getText());
			return boEquipmentType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
