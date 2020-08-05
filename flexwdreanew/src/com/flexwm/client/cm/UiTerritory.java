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
import com.flexwm.shared.cm.BmoTerritory;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiTerritory extends UiList {

	public UiTerritory(UiParams uiParams) {
		super(uiParams, new BmoTerritory());
	}

	@Override
	public void create() {
		UiTerritoryForm uiTerritoryForm = new UiTerritoryForm(getUiParams(), 0);
		uiTerritoryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiTerritoryForm uiTerritoryForm = new UiTerritoryForm(getUiParams(), bmObject.getId());
		uiTerritoryForm.show();
	}

	public class UiTerritoryForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoTerritory bmoTerritory;

		public UiTerritoryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoTerritory(), id);
		}

		@Override
		public void populateFields(){
			bmoTerritory = (BmoTerritory)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoTerritory.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoTerritory.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoTerritory.setId(id);
			bmoTerritory.getName().setValue(nameTextBox.getText());
			bmoTerritory.getDescription().setValue(descriptionTextArea.getText());
			return bmoTerritory;
		}

		@Override
		public void close() {
			list();
		}
	}
}