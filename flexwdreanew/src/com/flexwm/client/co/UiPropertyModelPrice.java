/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.flexwm.client.co.UiPropertyModel.UiPropertyModelForm.PropertyModelUpdater;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPropertyModelPrice extends UiList {

	BmoPropertyModelPrice bmoPropertyModelPrice;
	BmoPropertyModel bmoPropertyModel;
	int propertymodelid;
	protected PropertyModelUpdater propertyModelUpdater;

	public UiPropertyModelPrice(UiParams uiParams, Panel defaultPanel, BmoPropertyModel bmoPropertyModel, int propertymodelid, PropertyModelUpdater propertyModelUpdater) {
		super(uiParams, defaultPanel, new BmoPropertyModelPrice());
		bmoPropertyModelPrice = (BmoPropertyModelPrice)getBmObject();
		this.propertymodelid = propertymodelid;
		this.bmoPropertyModel = bmoPropertyModel;
		this.propertyModelUpdater = propertyModelUpdater;
	}

	@Override
	public void create() {
		UiPropertyModelPriceForm uiPropertyModelPriceForm = new UiPropertyModelPriceForm(getUiParams(), 0);
		uiPropertyModelPriceForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertyModelPrice = (BmoPropertyModelPrice)bmObject;
		UiPropertyModelPriceForm uiPropertyModelPriceForm = new UiPropertyModelPriceForm(getUiParams(), bmoPropertyModelPrice.getId());
		uiPropertyModelPriceForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {
		UiPropertyModelPriceForm uiPropertyModelPriceForm = new UiPropertyModelPriceForm(getUiParams(), bmObject.getId());
		uiPropertyModelPriceForm.show();
	}

	private class UiPropertyModelPriceForm extends UiFormDialog {
		TextBox priceTextBox = new TextBox();
		TextBox meterpriceTextBox = new TextBox();
		TextBox publicmeterpriceTextBox = new TextBox();
		TextBox constructionmeterpriceTextBox = new TextBox();
		UiDateBox dateBox = new UiDateBox();

		public UiPropertyModelPriceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertyModelPrice(), id);
		}

		@Override
		public void populateFields() {
			bmoPropertyModelPrice = (BmoPropertyModelPrice)getBmObject();
			formFlexTable.addField(1, 0, priceTextBox, bmoPropertyModelPrice.getPrice());
			formFlexTable.addField(2, 0, meterpriceTextBox, bmoPropertyModelPrice.getMeterPrice());
			formFlexTable.addField(3, 0, publicmeterpriceTextBox, bmoPropertyModelPrice.getPublicMeterPrice());
			formFlexTable.addField(4, 0, constructionmeterpriceTextBox, bmoPropertyModelPrice.getConstructionMeterPrice());
			formFlexTable.addField(5, 0, dateBox, bmoPropertyModelPrice.getStartDate());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertyModelPrice = new BmoPropertyModelPrice();
			bmoPropertyModelPrice.setId(id);

			bmoPropertyModelPrice.getPrice().setValue(priceTextBox.getText());
			bmoPropertyModelPrice.getMeterPrice().setValue(meterpriceTextBox.getText());
			bmoPropertyModelPrice.getPublicMeterPrice().setValue(publicmeterpriceTextBox.getText());
			bmoPropertyModelPrice.getConstructionMeterPrice().setValue(constructionmeterpriceTextBox.getText());
			bmoPropertyModelPrice.getStartDate().setValue(dateBox.getTextBox().getText());
			if (propertymodelid > 0) bmoPropertyModelPrice.getPropertyModelId().setValue(propertymodelid);	
			return bmoPropertyModelPrice;
		}
		
		@Override
		public void close() {
			list();
			if (propertyModelUpdater != null)
				propertyModelUpdater.update();
		}
	}
}
