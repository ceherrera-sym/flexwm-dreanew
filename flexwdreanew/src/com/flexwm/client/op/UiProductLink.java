/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author smuniz
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductLink;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiProductLink extends UiList {
	BmoProductLink bmoProductLink;
	BmoProduct bmoProduct;

	public UiProductLink(UiParams uiParams, Panel defaultPanel, BmoProduct bmoProduct) {
		super(uiParams, defaultPanel, new BmoProductLink());
		bmoProductLink = (BmoProductLink)getBmObject();
		this.bmoProduct = bmoProduct;
	}

	@Override
	public void create() {
		UiProductLinkForm uiProductLinkForm = new UiProductLinkForm(getUiParams(), 0);
		uiProductLinkForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProductLinkForm uiProductLinkForm = new UiProductLinkForm(getUiParams(), bmObject.getId());
		uiProductLinkForm.show(); 
	}

	private class UiProductLinkForm extends UiFormDialog {
		UiSuggestBox productLinkedSuggestBox = new UiSuggestBox(new BmoProduct());

		public UiProductLinkForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductLink(), id);
		}

		@Override
		public void populateFields() {
			bmoProductLink = (BmoProductLink)getBmObject();
			
			// Mostrar productos de Tipo SubProducto
			BmoProduct bmoProduct = new BmoProduct();
			BmFilter filterByType = new BmFilter();
			filterByType.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS,""+ BmoProduct.TYPE_COMPOSED);
			productLinkedSuggestBox.addFilter(filterByType);
			
			formFlexTable.addField(1, 0, productLinkedSuggestBox, bmoProductLink.getProductLinkedId());	
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProductLink = new BmoProductLink();
			bmoProductLink.setId(id);
			bmoProductLink.getProductId().setValue(bmoProduct.getId());
			bmoProductLink.getProductLinkedId() .setValue(productLinkedSuggestBox.getSelectedId());
			return bmoProductLink;
		}

		@Override
		public void close() {
			list();
		}
	}
}
