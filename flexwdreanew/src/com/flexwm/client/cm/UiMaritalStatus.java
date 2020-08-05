/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiMaritalStatus extends UiList {

	public UiMaritalStatus(UiParams uiParams) {
		super(uiParams, new BmoMaritalStatus());
	}

	@Override
	public void create() {
		UiMaritalStatusForm uiMaritalStatusForm = new UiMaritalStatusForm(getUiParams(), 0);
		uiMaritalStatusForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiMaritalStatusForm uiMaritalStatusForm = new UiMaritalStatusForm(getUiParams(), bmObject.getId());
		uiMaritalStatusForm.show();
	}

	public class UiMaritalStatusForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoMaritalStatus bmoMaritalStatus;

		public UiMaritalStatusForm(UiParams uiParams, int id) {
			super(uiParams, new BmoMaritalStatus(), id);
		}

		@Override
		public void populateFields(){
			bmoMaritalStatus = (BmoMaritalStatus)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoMaritalStatus.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoMaritalStatus.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoMaritalStatus.setId(id);
			bmoMaritalStatus.getName().setValue(nameTextBox.getText());
			bmoMaritalStatus.getDescription().setValue(descriptionTextArea.getText());
			return bmoMaritalStatus;
		}

		@Override
		public void close() {
			list();
		}
	}
}

