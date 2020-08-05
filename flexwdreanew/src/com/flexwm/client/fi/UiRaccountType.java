package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoRaccountType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiRaccountType extends UiList {

	public UiRaccountType(UiParams uiParams) {
		super(uiParams, new BmoRaccountType());
	}

	@Override
	public void create() {
		UiRaccountTypeForm uiRaccountTypeForm = new UiRaccountTypeForm(getUiParams(), 0);
		uiRaccountTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRaccountTypeForm uiRaccountTypeForm = new UiRaccountTypeForm(getUiParams(), bmObject.getId());
		uiRaccountTypeForm.show();
	}

	public class UiRaccountTypeForm extends UiFormDialog {
		UiListBox visibleTextBox = new UiListBox(getUiParams());
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox categoryListBox = new UiListBox(getUiParams());
		TextBox folioZerosTextBox = new TextBox();

		BmoRaccountType boRaccountType;

		public UiRaccountTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRaccountType(), id);
		}

		@Override
		public void populateFields() {
			boRaccountType = (BmoRaccountType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boRaccountType.getName());
			formFlexTable.addField(2, 0, typeListBox, boRaccountType.getType());
			formFlexTable.addField(3, 0, descriptionTextArea, boRaccountType.getDescription());		
			formFlexTable.addField(4, 0, categoryListBox, boRaccountType.getCategory());
			formFlexTable.addField(5, 0, visibleTextBox, boRaccountType.getVisible());
			formFlexTable.addField(5, 0, folioZerosTextBox, boRaccountType.getFolioZeros());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boRaccountType.setId(id);
			boRaccountType.getName().setValue(nameTextBox.getText());
			boRaccountType.getDescription().setValue(descriptionTextArea.getText());
			boRaccountType.getType().setValue(typeListBox.getSelectedCode());
			boRaccountType.getCategory().setValue(categoryListBox.getSelectedCode());
			boRaccountType.getVisible().setValue(visibleTextBox.getSelectedCode());
			boRaccountType.getFolioZeros().setValue(folioZerosTextBox.getText());
			return boRaccountType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
