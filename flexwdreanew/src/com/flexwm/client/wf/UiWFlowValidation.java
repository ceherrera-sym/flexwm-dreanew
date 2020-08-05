/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.wf.BmoWFlowValidation;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;

public class UiWFlowValidation extends UiList {

	public UiWFlowValidation(UiParams uiParams) {
		super(uiParams, new BmoWFlowValidation());
	}

	@Override
	public void create() {
		UiWFlowValidationForm uiWFlowValidationForm = new UiWFlowValidationForm(getUiParams(), 0);
		uiWFlowValidationForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowValidationForm uiWFlowValidationForm = new UiWFlowValidationForm(getUiParams(), bmObject.getId());
		uiWFlowValidationForm.show();	
	}

	public class UiWFlowValidationForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox classNameTextBox = new TextBox();
		BmoWFlowValidation bmoWFlowValidation;

		public UiWFlowValidationForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowValidation(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowValidation = (BmoWFlowValidation)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWFlowValidation.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowValidation.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowValidation.getDescription());
			formFlexTable.addField(4, 0, classNameTextBox, bmoWFlowValidation.getClassName());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowValidation.setId(id);
			bmoWFlowValidation.getCode().setValue(codeTextBox.getText());
			bmoWFlowValidation.getName().setValue(nameTextBox.getText());
			bmoWFlowValidation.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowValidation.getClassName().setValue(classNameTextBox.getText());
			return bmoWFlowValidation;
		}

		@Override
		public void close() {
			list();
		}
	}
}
