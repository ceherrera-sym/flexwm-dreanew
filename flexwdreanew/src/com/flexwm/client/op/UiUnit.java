/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoUnit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiUnit extends UiList {

	public UiUnit(UiParams uiParams) {
		super(uiParams, new BmoUnit());
	}

	@Override
	public void create() {
		UiUnitForm uiUnitForm = new UiUnitForm(getUiParams(), 0);
		uiUnitForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiUnitForm uiUnitForm = new UiUnitForm(getUiParams(), bmObject.getId());
		uiUnitForm.show();
	}

	public class UiUnitForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		CheckBox fractionCheckBox = new CheckBox();
		BmoUnit bmoUnit;

		public UiUnitForm(UiParams uiParams, int id) {
			super(uiParams, new BmoUnit(), id);
		}

		@Override
		public void populateFields() {
			bmoUnit = (BmoUnit)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoUnit.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoUnit.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoUnit.getDescription());
			formFlexTable.addField(4, 0, fractionCheckBox, bmoUnit.getFraction());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoUnit.setId(id);
			bmoUnit.getCode().setValue(codeTextBox.getText());
			bmoUnit.getName().setValue(nameTextBox.getText());
			bmoUnit.getDescription().setValue(descriptionTextArea.getText());
			bmoUnit.getFraction().setValue(fractionCheckBox.getValue());
			return bmoUnit;
		}

		@Override
		public void close() {
			list();
		}
	}
}
