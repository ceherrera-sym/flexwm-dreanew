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

import com.flexwm.shared.cm.BmoIndustry;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiIndustry extends UiList {

	public UiIndustry(UiParams uiParams) {
		super(uiParams, new BmoIndustry());
	}

	@Override
	public void create() {
		UiIndustryForm uiIndustryForm = new UiIndustryForm(getUiParams(), 0);
		uiIndustryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiIndustryForm uiIndustryForm = new UiIndustryForm(getUiParams(), bmObject.getId());
		uiIndustryForm.show();
	}

	public class UiIndustryForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoIndustry bmoIndustry;

		public UiIndustryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoIndustry(), id);
		}

		@Override
		public void populateFields(){
			bmoIndustry = (BmoIndustry)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoIndustry.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoIndustry.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoIndustry.setId(id);
			bmoIndustry.getName().setValue(nameTextBox.getText());
			bmoIndustry.getDescription().setValue(descriptionTextArea.getText());
			return bmoIndustry;
		}

		@Override
		public void close() {
			list();
		}
	}
}


