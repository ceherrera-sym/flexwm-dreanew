package com.flexwm.client.op;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiCustomerServiceType extends UiList {
	BmoCustomerServiceType bmoCustomerServiceType;

	public UiCustomerServiceType(UiParams uiParams) {
		super(uiParams, new BmoCustomerServiceType());
		bmoCustomerServiceType = (BmoCustomerServiceType)getBmObject();
	}

	@Override
	public void postShow() {
		//addStaticFilterListBox(new UiListBox(bmoDevelopmentType.getCode()), bmoDevelopmentType, bmoDevelopmentType.getCode());
	}

	@Override
	public void create() {
		UiCustomerServiceTypeForm uiCustomerServiceTypeForm = new UiCustomerServiceTypeForm(getUiParams(), 0);
		uiCustomerServiceTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCustomerServiceTypeForm uiCustomerServiceTypeForm = new UiCustomerServiceTypeForm(getUiParams(), bmObject.getId());
		uiCustomerServiceTypeForm.show();
	}

	public class UiCustomerServiceTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox userIdSuggestBox = new UiSuggestBox(new BmoUser());
		BmoCustomerServiceType bmoCustomerServiceType;

		public UiCustomerServiceTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomerServiceType(), id); 
		}

		@Override
		public void populateFields() {
			bmoCustomerServiceType = (BmoCustomerServiceType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoCustomerServiceType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoCustomerServiceType.getDescription());
			formFlexTable.addField(3, 0, userIdSuggestBox, bmoCustomerServiceType.getUserId()); 
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomerServiceType.setId(id);
			bmoCustomerServiceType.getName().setValue(nameTextBox.getText());
			bmoCustomerServiceType.getDescription().setValue(descriptionTextArea.getText());
			bmoCustomerServiceType.getUserId().setValue(userIdSuggestBox.getSelectedId());
			return bmoCustomerServiceType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
