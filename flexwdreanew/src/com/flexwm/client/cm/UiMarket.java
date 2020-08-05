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
import com.flexwm.shared.cm.BmoMarket;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiMarket extends UiList {

	public UiMarket(UiParams uiParams) {
		super(uiParams, new BmoMarket());
	}

	@Override
	public void create() {
		UiMarketForm uiMarketForm = new UiMarketForm(getUiParams(), 0);
		uiMarketForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiMarketForm uiMarketForm = new UiMarketForm(getUiParams(), bmObject.getId());
		uiMarketForm.show();
	}

	public class UiMarketForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoMarket bmoMarket;

		public UiMarketForm(UiParams uiParams, int id) {
			super(uiParams, new BmoMarket(), id);
		}

		@Override
		public void populateFields(){
			bmoMarket = (BmoMarket)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoMarket.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoMarket.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoMarket.setId(id);
			bmoMarket.getName().setValue(nameTextBox.getText());
			bmoMarket.getDescription().setValue(descriptionTextArea.getText());
			return bmoMarket;
		}

		@Override
		public void close() {
			list();
		}
	}
}