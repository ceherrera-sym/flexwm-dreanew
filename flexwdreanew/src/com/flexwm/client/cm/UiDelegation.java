package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoDelegation;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCity;

public class UiDelegation extends UiList {

	BmoDelegation bmoDelegation;

	public UiDelegation(UiParams uiParams) {
		super(uiParams, new BmoDelegation());
		this.bmoDelegation = (BmoDelegation)getBmObject();
	}

	@Override
	public void create() {
		UiDelegationForm uiDelegationForm = new UiDelegationForm(getUiParams(), 0);
		uiDelegationForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiDelegationForm uiDelegationForm = new UiDelegationForm(getUiParams(), bmObject.getId());
		uiDelegationForm.show();
	}

	public class UiDelegationForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextBox ladaTextBox = new TextBox();
		TextBox populationTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();	
		UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());

		public UiDelegationForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDelegation(), id);
		}

		@Override
		public void populateFields(){
			bmoDelegation = (BmoDelegation)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoDelegation.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoDelegation.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoDelegation.getDescription());
			formFlexTable.addField(6, 0, citySuggestBox, bmoDelegation.getCityId());	
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDelegation.setId(id);
			bmoDelegation.getCode().setValue(codeTextBox.getText());
			bmoDelegation.getName().setValue(nameTextBox.getText());
			bmoDelegation.getDescription().setValue(descriptionTextArea.getText());
			bmoDelegation.getCityId().setValue(citySuggestBox.getSelectedId());
			return bmoDelegation;
		}

		@Override
		public void close() {
			list();
		}	
	}
}
