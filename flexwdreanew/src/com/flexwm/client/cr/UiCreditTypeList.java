/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.client.cr;

import com.flexwm.shared.cr.BmoCreditType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiCreditTypeList extends UiList {
	BmoCreditType bmoCreditType;

	public UiCreditTypeList(UiParams uiParams) {
		super(uiParams, new BmoCreditType());
		bmoCreditType = (BmoCreditType)getBmObject();
	}

	@Override
	public void postShow( ){
		//addFilterListBox(new UiListBox(getUiParams(), new bmoCreditTypeGroup()), bmoCreditType.getbmoCreditTypeGroup());
	}

	@Override
	public void create() {
		UiCreditTypeForm uiTermForm = new UiCreditTypeForm(getUiParams(), 0);
		uiTermForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCreditTypeForm uiTermForm = new UiCreditTypeForm(getUiParams(), bmObject.getId());
		uiTermForm.show();
	}

	public class UiCreditTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox deadLineTextBox = new TextBox();
		TextBox interestTextBox = new TextBox();
		TextBox creditLimitTextBox = new TextBox();
		TextBox guaranteesTextBox = new TextBox();
		TextBox failureTextBox = new TextBox();
		UiListBox typeListBox = new UiListBox(getUiParams());
		BmoCreditType bmoCreditType;

		public UiCreditTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCreditType(), id); 
		}

		@Override
		public void populateFields() {
			bmoCreditType = (BmoCreditType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoCreditType.getName());
			formFlexTable.addField(2, 0, deadLineTextBox, bmoCreditType.getDeadLine());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoCreditType.getDescription());
			formFlexTable.addField(4, 0, creditLimitTextBox, bmoCreditType.getCreditLimit());
			formFlexTable.addField(5, 0, interestTextBox, bmoCreditType.getInterest());
			formFlexTable.addField(6, 0, guaranteesTextBox, bmoCreditType.getGuarantees());
			formFlexTable.addField(7, 0, failureTextBox, bmoCreditType.getFailure());
			formFlexTable.addField(8, 0, typeListBox, bmoCreditType.getType());		
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCreditType.setId(id);
			bmoCreditType.getName().setValue(nameTextBox.getText());
			bmoCreditType.getDescription().setValue(descriptionTextArea.getText());
			bmoCreditType.getDeadLine().setValue(deadLineTextBox.getText());		
			bmoCreditType.getInterest().setValue(interestTextBox.getText());		
			bmoCreditType.getType().setValue(typeListBox.getSelectedCode());
			bmoCreditType.getCreditLimit().setValue(creditLimitTextBox.getText());
			bmoCreditType.getGuarantees().setValue(guaranteesTextBox.getText());
			bmoCreditType.getFailure().setValue(failureTextBox.getText());

			return bmoCreditType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
