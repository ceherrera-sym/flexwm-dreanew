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

import com.flexwm.shared.cm.BmoConsultingService;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiConsultingService extends UiList {

	public UiConsultingService(UiParams uiParams) {
		super(uiParams, new BmoConsultingService());
	}

	@Override
	public void create() {
		UiConsultingServiceForm uiConsultingServiceForm = new UiConsultingServiceForm(getUiParams(), 0);
		uiConsultingServiceForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiConsultingServiceForm uiConsultingServiceForm = new UiConsultingServiceForm(getUiParams(), bmObject.getId());
		uiConsultingServiceForm.show();
	}

	public class UiConsultingServiceForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoConsultingService bmoConsultingService;

		public UiConsultingServiceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoConsultingService(), id);
		}

		@Override
		public void populateFields(){
			bmoConsultingService = (BmoConsultingService)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoConsultingService.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoConsultingService.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoConsultingService.setId(id);
			bmoConsultingService.getName().setValue(nameTextBox.getText());
			bmoConsultingService.getDescription().setValue(descriptionTextArea.getText());
			return bmoConsultingService;
		}

		@Override
		public void close() {
			list();
		}
	}
}


