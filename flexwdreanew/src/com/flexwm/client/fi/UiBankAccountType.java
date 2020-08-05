package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoBankAccountType;	
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiBankAccountType extends UiList {

	public UiBankAccountType(UiParams uiParams) {
		super(uiParams, new BmoBankAccountType());
	}

	@Override
	public void create() {
		UiBankAccountTypeForm uiBankAccountTypeForm = new UiBankAccountTypeForm(getUiParams(), 0);
		uiBankAccountTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiBankAccountTypeForm uiBankAccountTypeForm = new UiBankAccountTypeForm(getUiParams(), bmObject.getId());
		uiBankAccountTypeForm.show();
	}

	public class UiBankAccountTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoBankAccountType bmobankAccountType;

		public UiBankAccountTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBankAccountType(), id);
		}

		@Override
		public void populateFields() {
			bmobankAccountType = (BmoBankAccountType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmobankAccountType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmobankAccountType.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmobankAccountType.setId(id);
			bmobankAccountType.getName().setValue(nameTextBox.getText());
			bmobankAccountType.getDescription().setValue(descriptionTextArea.getText());
			return bmobankAccountType;
		}

		@Override
		public void close() {
			list();
		}
	}
}