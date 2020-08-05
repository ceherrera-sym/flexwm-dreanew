package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoPaccountType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPaccountType extends UiList {

	public UiPaccountType(UiParams uiParams) {
		super(uiParams, new BmoPaccountType());
	}

	@Override
	public void create() {
		UiPaccountTypeForm uiPaccountTypeForm = new UiPaccountTypeForm(getUiParams(), 0);
		uiPaccountTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPaccountTypeForm uiPaccountTypeForm = new UiPaccountTypeForm(getUiParams(), bmObject.getId());
		uiPaccountTypeForm.show();
	}

	public class UiPaccountTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox visibleListBox = new UiListBox(getUiParams());
		UiListBox categoryListBox = new UiListBox(getUiParams());
		TextBox uientryclassTextBox = new TextBox();
		BmoPaccountType boPaccountType;

		public UiPaccountTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPaccountType(), id);
		}

		@Override
		public void populateFields() {
			boPaccountType = (BmoPaccountType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boPaccountType.getName());
			formFlexTable.addField(2, 0, typeListBox, boPaccountType.getType());
			formFlexTable.addField(3, 0, descriptionTextArea, boPaccountType.getDescription());		
			formFlexTable.addField(4, 0, categoryListBox, boPaccountType.getCategory());
			formFlexTable.addField(5, 0, visibleListBox, boPaccountType.getVisible());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boPaccountType.setId(id);
			boPaccountType.getName().setValue(nameTextBox.getText());
			boPaccountType.getDescription().setValue(descriptionTextArea.getText());
			boPaccountType.getType().setValue(typeListBox.getSelectedCode());
			boPaccountType.getCategory().setValue(categoryListBox.getSelectedCode());
			boPaccountType.getVisible().setValue(visibleListBox.getSelectedCode());
			return boPaccountType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
