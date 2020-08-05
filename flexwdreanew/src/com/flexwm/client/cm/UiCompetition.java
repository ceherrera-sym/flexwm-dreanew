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

import com.flexwm.shared.cm.BmoCompetition;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiCompetition extends UiList {

	public UiCompetition(UiParams uiParams) {
		super(uiParams, new BmoCompetition());
	}

	@Override
	public void create() {
		UiPaymentTypeForm uiPaymentTypeForm = new UiPaymentTypeForm(getUiParams(), 0);
		uiPaymentTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPaymentTypeForm uiPaymentTypeForm = new UiPaymentTypeForm(getUiParams(), bmObject.getId());
		uiPaymentTypeForm.show();
	}

	public class UiPaymentTypeForm extends UiFormDialog {

		TextBox nameTextBox = new TextBox();
		BmoCompetition bmoCompetition;

		public UiPaymentTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCompetition(), id);
		}

		@Override
		public void populateFields() {
			bmoCompetition = (BmoCompetition)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoCompetition.getName());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCompetition.setId(id);
			bmoCompetition.getName().setValue(nameTextBox.getText());
			return bmoCompetition;
		}

		@Override
		public void close() {
			list();
		}
	}
}
