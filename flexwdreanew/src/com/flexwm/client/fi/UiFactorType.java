package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoFactorType;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;

public class UiFactorType extends UiList {

	public UiFactorType(UiParams uiParams) {
		super(uiParams, new BmoFactorType());
		
	}
	@Override
	public void postShow() {
		if(!getUiParams().getSFParams().hasSpecialAccess(BmoFactorType.ACCESS_SAVE))			
			newImage.setVisible(false);
	}
	@Override
	public void create() {
		UiFactorTypeForm uiFactorTypeForm = new UiFactorTypeForm(getUiParams(), 0);
		uiFactorTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiFactorTypeForm uiFactorTypeForm = new UiFactorTypeForm(getUiParams(), bmObject.getId());
		uiFactorTypeForm.show();
	}

	public class UiFactorTypeForm extends UiFormDialog {
		BmoFactorType bmoFactorType;
		TextBox codeTextBox = new TextBox();
		
		public UiFactorTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoFactorType(), id);			
		}
		@Override
		public void postShow() {
			if(!getUiParams().getSFParams().hasSpecialAccess(BmoFactorType.ACCESS_SAVE))
				saveButton.setVisible(false);
		}
		@Override
		public void populateFields(){
			bmoFactorType = (BmoFactorType)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoFactorType.getCode());
			if(!getUiParams().getSFParams().hasSpecialAccess(BmoFactorType.ACCESS_SAVE))
				codeTextBox.setEnabled(false);
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoFactorType.setId(id);
			bmoFactorType.getCode().setValue(codeTextBox.getText());
			return bmoFactorType;
		}

		@Override
		public void close() {
			list();
		}
		
	}
}
