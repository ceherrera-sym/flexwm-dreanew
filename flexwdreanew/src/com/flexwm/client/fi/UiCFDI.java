package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoCFDI;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiCFDI extends UiList {

	public UiCFDI(UiParams uiParams) {
		super(uiParams, new BmoCFDI());
	}

	@Override
	public void create() {
		UiCFDIForm uiCFDIForm = new UiCFDIForm(getUiParams(), 0);
		uiCFDIForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCFDIForm uiCFDIForm = new UiCFDIForm(getUiParams(), bmObject.getId());
		uiCFDIForm.show();
	}

	public class UiCFDIForm extends UiFormDialog {
		TextBox codeSatTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoCFDI bmoCFDI;

		public UiCFDIForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCFDI(), id);
		}

		@Override
		public void populateFields() {
			bmoCFDI = (BmoCFDI)getBmObject();
			formFlexTable.addField(1, 0, codeSatTextBox, bmoCFDI.getCodeSAT());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoCFDI.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCFDI.setId(id);
			bmoCFDI.getCodeSAT().setValue(codeSatTextBox.getText());
			bmoCFDI.getDescription().setValue(descriptionTextArea.getText());
			return bmoCFDI;
		}

		@Override
		public void close() {
			list();
		}
	}
}
