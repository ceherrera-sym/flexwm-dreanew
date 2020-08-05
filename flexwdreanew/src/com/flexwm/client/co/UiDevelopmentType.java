/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoDevelopmentType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiDevelopmentType extends UiList {
	BmoDevelopmentType bmoDevelopmentType;

	public UiDevelopmentType(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentType());
		bmoDevelopmentType = (BmoDevelopmentType)getBmObject();
	}

	@Override
	public void create() {
		UiDevelopmentTypeForm uiDevelopmentTypeForm = new UiDevelopmentTypeForm(getUiParams(), 0);
		uiDevelopmentTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopmentType = (BmoDevelopmentType)bmObject;
		UiDevelopmentTypeForm uiDevelopmentTypeForm = new UiDevelopmentTypeForm(getUiParams(), bmoDevelopmentType.getId());
		uiDevelopmentTypeForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiDevelopmentTypeForm uiDevelopmentTypeForm = new UiDevelopmentTypeForm(getUiParams(), bmObject.getId());
		uiDevelopmentTypeForm.show();
	}

	public class UiDevelopmentTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();

		BmoDevelopmentType bmoDevelopmentType;

		public UiDevelopmentTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDevelopmentType(), id); 
		}

		@Override
		public void populateFields() {
			bmoDevelopmentType = (BmoDevelopmentType)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoDevelopmentType.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoDevelopmentType.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoDevelopmentType.getDescription());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDevelopmentType.setId(id);
			bmoDevelopmentType.getCode().setValue(codeTextBox.getText());
			bmoDevelopmentType.getName().setValue(nameTextBox.getText());
			bmoDevelopmentType.getDescription().setValue(descriptionTextArea.getText());
			return bmoDevelopmentType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
