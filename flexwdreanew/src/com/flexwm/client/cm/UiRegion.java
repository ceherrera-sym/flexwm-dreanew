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

import com.flexwm.shared.cm.BmoRegion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;

public class UiRegion extends UiList {

	public UiRegion(UiParams uiParams) {
		super(uiParams, new BmoRegion());
	}

	@Override
	public void create() {
		UiRegionForm uiRegionForm = new UiRegionForm(getUiParams(), 0);
		uiRegionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRegionForm uiRegionForm = new UiRegionForm(getUiParams(), bmObject.getId());
		uiRegionForm.show();
	}

	public class UiRegionForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoRegion bmoRegion;

		public UiRegionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRegion(), id);
		}

		@Override
		public void populateFields(){
			bmoRegion = (BmoRegion)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoRegion.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoRegion.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRegion.setId(id);
			bmoRegion.getName().setValue(nameTextBox.getText());
			bmoRegion.getDescription().setValue(descriptionTextArea.getText());
			return bmoRegion;
		}
		
		@Override
		public void saveNext() {
			showList();
		}
		
		@Override
		public void close() {
			if (deleted)
				list();
		}
	}
}