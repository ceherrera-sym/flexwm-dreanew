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
import com.flexwm.shared.cm.BmoReferral;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiReferral extends UiList {

	public UiReferral(UiParams uiParams) {
		super(uiParams, new BmoReferral());
	}

	@Override
	public void create() {
		UiReferralForm uiReferralForm = new UiReferralForm(getUiParams(), 0);
		uiReferralForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiReferralForm uiReferralForm = new UiReferralForm(getUiParams(), bmObject.getId());
		uiReferralForm.show();
	}

	public class UiReferralForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoReferral boReferral;

		public UiReferralForm(UiParams uiParams, int id) {
			super(uiParams, new BmoReferral(), id);
		}

		@Override
		public void populateFields(){
			boReferral = (BmoReferral)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boReferral.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boReferral.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boReferral.setId(id);
			boReferral.getName().setValue(nameTextBox.getText());
			boReferral.getDescription().setValue(descriptionTextArea.getText());
			return boReferral;
		}

		@Override
		public void close() {
			list();
		}

	}
}