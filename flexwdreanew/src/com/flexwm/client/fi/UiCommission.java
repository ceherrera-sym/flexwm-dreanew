/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.op.BmoProductGroup;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;


/**
 * @author jhernandez
 *
 */

public class UiCommission extends UiList {
	public UiCommission(UiParams uiParams) {
		super(uiParams, new BmoCommission());
	}

	@Override
	public void create() {
		UiCommissionForm uiCommissionForm = new UiCommissionForm(getUiParams(), 0);
		uiCommissionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCommissionForm uiCommissionForm = new UiCommissionForm(getUiParams(), bmObject.getId());
		uiCommissionForm.show();
	}

	public class UiCommissionForm extends UiFormDialog {	
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		DateBox dueDateBox = new DateBox();
		DateBox stampDatetimeDateBox = new DateBox();
		UiSuggestBox profileSuggestBox = new UiSuggestBox(new BmoProfile());	
		UiListBox productGroupListBox = new UiListBox(getUiParams(), new BmoProductGroup());
		TextBox commissionTextBox = new TextBox();
		BmoCommission bmoCommission;

		public UiCommissionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCommission(), id);
		}

		@Override
		public void populateFields() {
			bmoCommission = (BmoCommission)getBmObject();
			formFlexTable.addField(1, 0, productGroupListBox, bmoCommission.getProductGroupId());
			formFlexTable.addField(2, 0, profileSuggestBox, bmoCommission.getProfileId());		
			formFlexTable.addField(3, 0, commissionTextBox, bmoCommission.getPercentage());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCommission.setId(id);
			bmoCommission.getProfileId().setValue(profileSuggestBox.getSelectedId());
			bmoCommission.getProductGroupId().setValue(productGroupListBox.getSelectedId());		
			bmoCommission.getPercentage().setValue(commissionTextBox.getText());
			return bmoCommission;
		}

		@Override
		public void close() {
			list();
		}
	} 
}
