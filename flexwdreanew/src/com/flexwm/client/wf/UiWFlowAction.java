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
import com.flexwm.shared.wf.BmoWFlowAction;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;

public class UiWFlowAction extends UiList {

	public UiWFlowAction(UiParams uiParams) {
		super(uiParams, new BmoWFlowAction());
	}

	@Override
	public void create() {
		UiWFlowActionForm uiWFlowActionForm = new UiWFlowActionForm(getUiParams(), 0);
		uiWFlowActionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowActionForm uiWFlowActionForm = new UiWFlowActionForm(getUiParams(), bmObject.getId());
		uiWFlowActionForm.show();	
	}

	public class UiWFlowActionForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox classNameTextBox = new TextBox();
		BmoWFlowAction bmoWFlowAction;

		public UiWFlowActionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowAction(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowAction = (BmoWFlowAction)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWFlowAction.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowAction.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowAction.getDescription());
			formFlexTable.addField(4, 0, classNameTextBox, bmoWFlowAction.getClassName());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowAction.setId(id);
			bmoWFlowAction.getCode().setValue(codeTextBox.getText());
			bmoWFlowAction.getName().setValue(nameTextBox.getText());
			bmoWFlowAction.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowAction.getClassName().setValue(classNameTextBox.getText());
			return bmoWFlowAction;
		}

		@Override
		public void close() {
			list();
		}
	}
}
