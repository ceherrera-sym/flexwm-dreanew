/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.client.op.UiProductKit.UiProductKitForm.ProductKitUpdater;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiProductKitItem extends UiList {

	BmoProductKitItem bmoProductKitItem;
	BmoProductKit bmoProductKit;
	protected ProductKitUpdater productKitUpdater;
	int productKitId;

	public UiProductKitItem(UiParams uiParams, Panel defaultPanel, BmoProductKit bmoProductKit, int id, ProductKitUpdater productKitUpdater) {
		super(uiParams, defaultPanel, new BmoProductKitItem());
		productKitId = id;
		bmoProductKitItem = new BmoProductKitItem();
		this.bmoProductKit = bmoProductKit;
		this.productKitUpdater = productKitUpdater;
	}

	@Override
	public void create() {
		UiProductKitItemForm uiProductKitItemForm = new UiProductKitItemForm(getUiParams(), 0);
		uiProductKitItemForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProductKitItemForm uiProductKitItemForm = new UiProductKitItemForm(getUiParams(), bmObject.getId());
		uiProductKitItemForm.show();
	}

	private class UiProductKitItemForm extends UiFormDialog {
		UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		TextBox quantityTextBox = new TextBox();
		TextBox daysTextBox = new TextBox();

		public UiProductKitItemForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductKitItem(), id);
		}

		@Override
		public void populateFields() {
			bmoProductKitItem = (BmoProductKitItem)getBmObject();
			formFlexTable.addField(1, 0, productSuggestBox, bmoProductKitItem.getProductId());
			formFlexTable.addField(2, 0, daysTextBox, bmoProductKitItem.getDays());
			formFlexTable.addField(3, 0, quantityTextBox, bmoProductKitItem.getQuantity());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProductKitItem = new BmoProductKitItem();
			bmoProductKitItem.setId(id);
			bmoProductKitItem.getProductKitId().setValue(productKitId);	
			bmoProductKitItem.getQuantity().setValue(quantityTextBox.getText());
			bmoProductKitItem.getDays().setValue(daysTextBox.getText());
			bmoProductKitItem.getProductId().setValue(productSuggestBox.getSelectedId());
			return bmoProductKitItem;
		}

		@Override
		public void close() {
			list();

			if (productKitUpdater != null)
				productKitUpdater.update();
		}
	}
}
