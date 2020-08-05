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
import com.flexwm.shared.op.BmoProductFamily;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiProductFamily extends UiList {

	public UiProductFamily(UiParams uiParams) {
		super(uiParams, new BmoProductFamily());
	}

	@Override
	public void create() {
		UiProductFamilyForm uiProductFamilyForm = new UiProductFamilyForm(getUiParams(), 0);
		uiProductFamilyForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProductFamilyForm uiProductFamilyForm = new UiProductFamilyForm(getUiParams(), bmObject.getId());
		uiProductFamilyForm.show();
	}

	public class UiProductFamilyForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoProductFamily boProductFamily;

		public UiProductFamilyForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductFamily(), id);
		}

		@Override
		public void populateFields() {
			boProductFamily = (BmoProductFamily)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boProductFamily.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boProductFamily.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boProductFamily.setId(id);
			boProductFamily.getName().setValue(nameTextBox.getText());
			boProductFamily.getDescription().setValue(descriptionTextArea.getText());
			return boProductFamily;
		}

		@Override
		public void close() {
			list();
		}
	}
}
