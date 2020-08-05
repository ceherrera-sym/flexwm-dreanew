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
import com.flexwm.shared.cm.BmoLoseMotive;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiLoseMotive extends UiList {

	public UiLoseMotive(UiParams uiParams) {
		super(uiParams, new BmoLoseMotive());
	}

	@Override
	public void create() {
		UiLoseMotiveForm uiLoseMotiveForm = new UiLoseMotiveForm(getUiParams(), 0);
		uiLoseMotiveForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiLoseMotiveForm uiLoseMotiveForm = new UiLoseMotiveForm(getUiParams(), bmObject.getId());
		uiLoseMotiveForm.show();
	}

	public class UiLoseMotiveForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoLoseMotive bmoLoseMotive;

		public UiLoseMotiveForm(UiParams uiParams, int id) {
			super(uiParams, new BmoLoseMotive(), id);
		}

		@Override
		public void populateFields(){
			bmoLoseMotive = (BmoLoseMotive)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoLoseMotive.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoLoseMotive.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoLoseMotive.setId(id);
			bmoLoseMotive.getName().setValue(nameTextBox.getText());
			bmoLoseMotive.getDescription().setValue(descriptionTextArea.getText());
			return bmoLoseMotive;
		}

		@Override
		public void close() {
			list();
		}
	}
}

