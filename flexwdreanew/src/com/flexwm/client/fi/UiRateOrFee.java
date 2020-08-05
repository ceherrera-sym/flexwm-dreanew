package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoFactorType;
import com.flexwm.shared.fi.BmoRateOrFee;
import com.flexwm.shared.fi.BmoTax;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;

public class UiRateOrFee extends UiList {

	public UiRateOrFee(UiParams uiParams) {
		super(uiParams, new BmoRateOrFee());		
	}
	@Override
	public void create() {
		UiRateOrFeeForm uiRateOrFeeForm = new UiRateOrFeeForm(getUiParams(), 0);
		uiRateOrFeeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRateOrFeeForm uiRateOrFeeForm = new UiRateOrFeeForm(getUiParams(), bmObject.getId());
		uiRateOrFeeForm.show();
	}
	
	
	public class UiRateOrFeeForm extends UiFormDialog {
		BmoRateOrFee bmoRateOrFee;
		TextBox codTextBox = new TextBox();
		TextBox maxvalueTextBox = new TextBox();
		UiListBox taxtIdBox = new UiListBox(getUiParams(), new BmoTax());
		UiListBox factorTypeId = new UiListBox(getUiParams(), new BmoFactorType());
		UiListBox companyId = new UiListBox(getUiParams(), new BmoCompany());
		
		CheckBox tranferCheckBox = new CheckBox();
		CheckBox retentionCheckBox = new CheckBox();
		
		public UiRateOrFeeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRateOrFee(), id);			
		}
		
		
		@Override
		public void populateFields() {
			bmoRateOrFee = (BmoRateOrFee)getBmObject();
			formFlexTable.addFieldReadOnly(1, 0, codTextBox,bmoRateOrFee.getCode());
			formFlexTable.addField(2, 0, maxvalueTextBox,bmoRateOrFee.getMaxValue());
			formFlexTable.addField(3, 0, taxtIdBox,bmoRateOrFee.getTaxId());
			formFlexTable.addField(4, 0, factorTypeId,bmoRateOrFee.getFactorTypeId());
			formFlexTable.addField(5, 0, companyId,bmoRateOrFee.getCompanyId());
			formFlexTable.addField(6, 0, tranferCheckBox, bmoRateOrFee.getTransfer());
			formFlexTable.addField(7, 0, retentionCheckBox, bmoRateOrFee.getRetention());
			
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoRateOrFee.setId(id);
			bmoRateOrFee.getCode().setValue(codTextBox.getText());
			bmoRateOrFee.getMaxValue().setValue(maxvalueTextBox.getText());
			bmoRateOrFee.getTaxId().setValue(taxtIdBox.getSelectedId());
			bmoRateOrFee.getFactorTypeId().setValue(factorTypeId.getSelectedId());
			bmoRateOrFee.getCompanyId().setValue(companyId.getSelectedId());
			bmoRateOrFee.getTransfer().setValue(tranferCheckBox.getValue());
			bmoRateOrFee.getRetention().setValue(retentionCheckBox.getValue());
			return bmoRateOrFee;
		}
		@Override
		public void close() {
			list();
		}
	
	}
}
