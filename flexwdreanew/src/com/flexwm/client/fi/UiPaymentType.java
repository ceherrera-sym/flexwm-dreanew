/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoPaymentType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPaymentType extends UiList {

	public UiPaymentType(UiParams uiParams) {
		super(uiParams, new BmoPaymentType());
	}

	@Override
	public void create() {
		UiPaymentTypeForm uiPaymentTypeForm = new UiPaymentTypeForm(getUiParams(), 0);
		uiPaymentTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPaymentTypeForm uiPaymentTypeForm = new UiPaymentTypeForm(getUiParams(), bmObject.getId());
		uiPaymentTypeForm.show();
	}

	public class UiPaymentTypeForm extends UiFormDialog {

		TextBox codeSATTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();	
		BmoPaymentType bmoPaymentType;

		public UiPaymentTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPaymentType(), id);
		}

		@Override
		public void populateFields() {
			bmoPaymentType = (BmoPaymentType)getBmObject();
			formFlexTable.addField(1, 0, codeSATTextBox, bmoPaymentType.getCodeSAT());
			formFlexTable.addField(2, 0, nameTextBox, bmoPaymentType.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoPaymentType.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPaymentType.setId(id);
			bmoPaymentType.getCodeSAT().setValue(codeSATTextBox.getText());
			bmoPaymentType.getName().setValue(nameTextBox.getText());
			bmoPaymentType.getDescription().setValue(descriptionTextArea.getText());
			return bmoPaymentType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
