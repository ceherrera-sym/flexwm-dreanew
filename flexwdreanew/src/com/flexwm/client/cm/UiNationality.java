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
import com.flexwm.shared.cm.BmoNationality;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiNationality extends UiList {

	public UiNationality(UiParams uiParams) {
		super(uiParams, new BmoNationality());
	}

	@Override
	public void create() {
		UiNationalityForm uiNationalityForm = new UiNationalityForm(getUiParams(), 0);
		uiNationalityForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiNationalityForm uiNationalityForm = new UiNationalityForm(getUiParams(), bmObject.getId());
		uiNationalityForm.show();
	}

	public class UiNationalityForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoNationality bmoNationality;

		public UiNationalityForm(UiParams uiParams, int id) {
			super(uiParams, new BmoNationality(), id);
		}

		@Override
		public void populateFields(){
			bmoNationality = (BmoNationality)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoNationality.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoNationality.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoNationality.setId(id);
			bmoNationality.getName().setValue(nameTextBox.getText());
			bmoNationality.getDescription().setValue(descriptionTextArea.getText());
			return bmoNationality;
		}

		@Override
		public void close() {
			list();
		}
	}
}