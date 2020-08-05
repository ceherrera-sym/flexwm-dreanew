/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.ac.BmoSessionDiscipline;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiSessionDiscipline extends UiList {

	public UiSessionDiscipline(UiParams uiParams) {
		super(uiParams, new BmoSessionDiscipline());
	}

	@Override
	public void create() {
		UiSessionDisciplineForm uiSessionDisciplineForm = new UiSessionDisciplineForm(getUiParams(), 0);
		uiSessionDisciplineForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSessionDisciplineForm uiSessionDisciplineForm = new UiSessionDisciplineForm(getUiParams(), bmObject.getId());
		uiSessionDisciplineForm.show();
	}

	public class UiSessionDisciplineForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoSessionDiscipline boSessionDiscipline;

		public UiSessionDisciplineForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionDiscipline(), id);
		}

		@Override
		public void populateFields() {
			boSessionDiscipline = (BmoSessionDiscipline)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boSessionDiscipline.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boSessionDiscipline.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boSessionDiscipline.setId(id);
			boSessionDiscipline.getName().setValue(nameTextBox.getText());
			boSessionDiscipline.getDescription().setValue(descriptionTextArea.getText());
			return boSessionDiscipline;
		}

		@Override
		public void close() {
			list();
		}
	}

}