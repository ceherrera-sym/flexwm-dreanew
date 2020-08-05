/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.flexwm.client.fi.UiCurrency.UiCurrencyForm.CurrencyUpdater;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoCurrencyRate;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiCurrencyRate extends UiList {

	BmoCurrencyRate bmoCurrencyRate;
	TextBox rateTextBox = new TextBox();
	UiDateTimeBox dateTimeBox = new UiDateTimeBox();
	int currencyId;
	protected CurrencyUpdater currencyUpdater;
	BmoCurrency bmoCurrency;

	public UiCurrencyRate(UiParams uiParams, Panel defaultPanel, BmoCurrency bmoCurrency, int id, CurrencyUpdater currencyUpdater) {
		super(uiParams, defaultPanel, new BmoCurrencyRate());
		currencyId = id;
		bmoCurrencyRate = new BmoCurrencyRate();
		this.bmoCurrency = bmoCurrency;
		this.currencyUpdater = currencyUpdater;
	}


	@Override
	public void create() {
		UiCurrencyRateForm uiCurrencyRateForm = new UiCurrencyRateForm(getUiParams(), 0);
		uiCurrencyRateForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCurrencyRateForm uiCurrencyRateForm = new UiCurrencyRateForm(getUiParams(), bmObject.getId());
		uiCurrencyRateForm.show();
	}

	private class UiCurrencyRateForm extends UiFormDialog {

		public UiCurrencyRateForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCurrencyRate(), id);
		}

		@Override
		public void populateFields() {
			bmoCurrencyRate = (BmoCurrencyRate)getBmObject();
			formFlexTable.addField(1, 0, dateTimeBox,bmoCurrencyRate.getDateTime());
			formFlexTable.addField(2, 0, rateTextBox, bmoCurrencyRate.getRate());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCurrencyRate = new BmoCurrencyRate();
			bmoCurrencyRate.setId(id);
			bmoCurrencyRate.getDateTime().setValue(dateTimeBox.getDateTime());
			bmoCurrencyRate.getRate().setValue(rateTextBox.getText());
			bmoCurrencyRate.getCurrencyId().setValue(currencyId);		
			return bmoCurrencyRate;
		}

		@Override
		public void close() {
			list();

			if (currencyUpdater != null)
				currencyUpdater.update();
		}
	}
}
