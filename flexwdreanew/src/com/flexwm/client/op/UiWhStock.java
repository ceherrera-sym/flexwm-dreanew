package com.flexwm.client.op;

import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhStock;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;


public class UiWhStock extends UiList {
	BmoWhStock bmoWhStock;

	public UiWhStock(UiParams uiParams) {
		super(uiParams, new BmoWhStock());
		bmoWhStock = (BmoWhStock)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoWarehouse()), bmoWhStock.getBmoWhSection().getBmoWarehouse());
		addFilterSuggestBox(new UiSuggestBox(new BmoWhSection()), new BmoWhSection(), bmoWhStock.getWhSectionId());
		addFilterSuggestBox(new UiSuggestBox(new BmoProduct()), new BmoProduct(), bmoWhStock.getProductId());

		//		newButton
		newImage.setVisible(false);
		deleteImage.setVisible(false);
	}

	@Override
	public void create() {
		UiWhStockForm uiWhStockForm = new UiWhStockForm(getUiParams(), 0);
		uiWhStockForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWhStock = (BmoWhStock)bmObject;
		UiWhStockForm uiWhStockForm = new UiWhStockForm(getUiParams(), bmoWhStock.getId());
		uiWhStockForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWhStockForm uiWhStockForm = new UiWhStockForm(getUiParams(), bmObject.getId());
		uiWhStockForm.show();
	}

	public class UiWhStockForm extends UiFormDialog {
		UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		UiSuggestBox whTrackSuggestBox = new UiSuggestBox(new BmoWhTrack());
		UiListBox whSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
		BmoWhStock bmoWhStock;

		public UiWhStockForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhStock(), id);
		}

		@Override
		public void populateFields() {
			bmoWhStock = (BmoWhStock)getBmObject();
			formFlexTable.addField(1, 0, productSuggestBox, bmoWhStock.getProductId());
			formFlexTable.addField(2, 0, whSectionListBox, bmoWhStock.getWhSectionId());	
			formFlexTable.addField(3, 0, whTrackSuggestBox, bmoWhStock.getWhTrackId());
			formFlexTable.addLabelField(4, 0, bmoWhStock.getQuantity());
			formFlexTable.addLabelField(5, 0, bmoWhStock.getAmount());
		}

		@Override
		public void postShow() {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
			productSuggestBox.setEnabled(false);
			whSectionListBox.setEnabled(false);
			whTrackSuggestBox.setEnabled(false);
		}

		@Override
		public void close() {
			list();
		}
	}
}
