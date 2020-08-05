package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoTax;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;

public class UiTax extends UiList {
	
	public UiTax(UiParams uiParams) {
		super(uiParams, new BmoTax());		
	}
	@Override
	public void postShow() {
		if(!getUiParams().getSFParams().hasSpecialAccess(BmoTax.ACCESS_SAVE))			
			newImage.setVisible(false);
	}
	@Override
	public void create() {
		UiTaxForm uiTaxForm = new UiTaxForm(getUiParams(), 0);
		uiTaxForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiTaxForm uiTaxForm = new UiTaxForm(getUiParams(), bmObject.getId());
		uiTaxForm.show();
	}
	
	public class UiTaxForm extends UiFormDialog {
		BmoTax bmoTax;
		TextBox codeTextBox = new TextBox();
		TextBox descriptionTextBox = new TextBox();
		
		public UiTaxForm(UiParams uiParams, int id) {
			super(uiParams, new BmoTax(), id);			
		}
		@Override
		public void postShow() {
			if(!getUiParams().getSFParams().hasSpecialAccess(BmoTax.ACCESS_SAVE))
				saveButton.setVisible(false);
		}
		@Override
		public void populateFields(){
			bmoTax = (BmoTax)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoTax.getCode());
			formFlexTable.addField(2, 0, descriptionTextBox, bmoTax.getDescrition());
			if(!getUiParams().getSFParams().hasSpecialAccess(BmoTax.ACCESS_SAVE)) {
				codeTextBox.setEnabled(false);
				descriptionTextBox.setEnabled(false);
			}
		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoTax.setId(id);
			bmoTax.getCode().setValue(codeTextBox.getText());
			bmoTax.getDescrition().setValue(descriptionTextBox.getText());
			return bmoTax;
		}

		@Override
		public void close() {
			list();
		}
		
	}
}
