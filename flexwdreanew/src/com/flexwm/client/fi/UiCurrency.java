package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoCurrencyRate;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCurrency extends UiList {
	public UiCurrency(UiParams uiParams) {
		super(uiParams, new BmoCurrency());
	}

	@Override
	public void create() {
		UiCurrencyForm uiCurrencyForm = new UiCurrencyForm(getUiParams(), 0);
		uiCurrencyForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCurrencyForm uiCurrencyForm = new UiCurrencyForm(getUiParams(), bmObject.getId());
		uiCurrencyForm.show();
	}

	public class UiCurrencyForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox parityTextBox = new TextBox();
		BmoCurrency bmoCurrency;

		String historySection = "Historial";
		CurrencyUpdater currencyUpdater = new CurrencyUpdater();

		public UiCurrencyForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCurrency(), id);
		}

		@Override
		public void populateFields() {
			bmoCurrency = (BmoCurrency)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoCurrency.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoCurrency.getName());		
			formFlexTable.addField(3, 0, descriptionTextArea, bmoCurrency.getDescription());
			formFlexTable.addFieldReadOnly(4, 0, parityTextBox, bmoCurrency.getParity());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(5, 0, historySection, 2);
				BmoCurrencyRate bmoCurrencyRate = new BmoCurrencyRate();
				FlowPanel currencyRateFP = new FlowPanel();
				BmFilter filterCurrencyRate = new BmFilter();
				filterCurrencyRate.setValueFilter(bmoCurrencyRate.getKind(), bmoCurrencyRate.getCurrencyId(), bmoCurrency.getId());
				getUiParams().setForceFilter(bmoCurrencyRate.getProgramCode(), filterCurrencyRate);
				UiCurrencyRate uiCurrencyRate = new UiCurrencyRate(getUiParams(), currencyRateFP, bmoCurrency, bmoCurrency.getId(), currencyUpdater);
				setUiType(bmoCurrencyRate.getProgramCode(), UiParams.MINIMALIST);
				uiCurrencyRate.show();
				formFlexTable.addPanel(6, 0, currencyRateFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCurrency.setId(id);
			bmoCurrency.getCode().setValue(codeTextBox.getText());
			bmoCurrency.getName().setValue(nameTextBox.getText());		
			bmoCurrency.getDescription().setValue(descriptionTextArea.getText());
			bmoCurrency.getParity().setValue(parityTextBox.getText());
			return bmoCurrency;
		}

		@Override
		public void close() {
			UiCurrency uiCurrency = new UiCurrency(getUiParams());
			uiCurrency.show();
		}

		public class CurrencyUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}

}
