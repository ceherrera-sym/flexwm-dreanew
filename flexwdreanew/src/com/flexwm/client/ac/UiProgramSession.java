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

import com.flexwm.shared.ac.BmoProgramSession;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiProgramSession extends UiList {

	public UiProgramSession(UiParams uiParams) {
		super(uiParams, new BmoProgramSession());
	}

	@Override
	public void create() {
		UiProgramSessionForm uiProgramSessionForm = new UiProgramSessionForm(getUiParams(), 0);
		uiProgramSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProgramSessionForm uiProgramSessionForm = new UiProgramSessionForm(getUiParams(), bmObject.getId());
		uiProgramSessionForm.show();
	}

	public class UiProgramSessionForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoProgramSession bmoProgramSession;

		public UiProgramSessionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProgramSession(), id);
		}

		@Override
		public void populateFields() {
			bmoProgramSession = (BmoProgramSession)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoProgramSession.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoProgramSession.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProgramSession.setId(id);
			bmoProgramSession.getName().setValue(nameTextBox.getText());
			bmoProgramSession.getDescription().setValue(descriptionTextArea.getText());
			return bmoProgramSession;
		}

		@Override
		public void close() {
			list();
		}
	}
}
