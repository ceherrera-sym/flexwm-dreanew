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
import com.flexwm.shared.co.BmoConceptGroup;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiConceptGroup extends UiList {
	BmoConceptGroup bmoConceptGroup;

	public UiConceptGroup(UiParams uiParams) {
		super(uiParams, new BmoConceptGroup());
		bmoConceptGroup = (BmoConceptGroup)getBmObject();
	}

	@Override
	public void create() {
		UiConceptGroupForm uiConceptGroupForm = new UiConceptGroupForm(getUiParams(), 0);
		uiConceptGroupForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiConceptGroupForm uiConceptGroupForm = new UiConceptGroupForm(getUiParams(), bmObject.getId());
		uiConceptGroupForm.show();
	}

	public class UiConceptGroupForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoConceptGroup bmoConceptGroup;

		public UiConceptGroupForm(UiParams uiParams, int id) {
			super(uiParams, new BmoConceptGroup(), id); 
		}

		@Override
		public void populateFields(){
			bmoConceptGroup = (BmoConceptGroup)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoConceptGroup.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoConceptGroup.getDescription());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoConceptGroup.setId(id);
			bmoConceptGroup.getName().setValue(nameTextBox.getText());
			bmoConceptGroup.getDescription().setValue(descriptionTextArea.getText());
			return bmoConceptGroup;
		}

		@Override
		public void close() {
			list();
		}
	}

}
