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

import java.util.Date;

import com.flexwm.client.fi.UiBudget.UiBudgetForm.BudgetUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiBudgetItem extends UiList {
	BmoBudgetItem bmoBudgetItem;
	protected BudgetUpdater budgetUpdater;
	BmoBudget bmoBudget;
	int budgetId;

	public UiBudgetItem(UiParams uiParams, Panel defaultPanel, BmoBudget bmoBudget, int id, BudgetUpdater budgetUpdater) {
		super(uiParams, defaultPanel, new BmoBudgetItem());
		budgetId = id;
		bmoBudgetItem = new BmoBudgetItem();
		this.bmoBudget = bmoBudget;
		this.budgetUpdater = budgetUpdater;
	}

	@Override
	public void create() {
		UiBudgetItemForm uiBudgetItemForm = new UiBudgetItemForm(getUiParams(), 0);
		uiBudgetItemForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiBudgetItemForm uiBudgetItemForm = new UiBudgetItemForm(getUiParams(), bmObject.getId());
		uiBudgetItemForm.show();
	}

	private class UiBudgetItemForm extends UiFormDialog {
		private TextArea commentsTextBox = new TextArea();
		private UiSuggestBox budgetItemTypeSuggestBox = new UiSuggestBox(new BmoBudgetItemType());
		private TextBox amountTextBox = new TextBox();
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyId;

		public UiBudgetItemForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBudgetItem(), id);
		}

		@Override
		public void populateFields(){
			bmoBudgetItem = (BmoBudgetItem)getBmObject();
			formFlexTable.addField(1, 0, budgetItemTypeSuggestBox, bmoBudgetItem.getBudgetItemTypeId());
			formFlexTable.addField(2, 0, commentsTextBox, bmoBudgetItem.getComments());
			formFlexTable.addField(3, 0, amountTextBox, bmoBudgetItem.getAmount());	
			formFlexTable.addField(4, 0, currencyListBox, bmoBudgetItem.getCurrencyId());
			formFlexTable.addField(5, 0, currencyParityTextBox, bmoBudgetItem.getCurrencyParity());
			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {		

			if (event.getSource() == currencyListBox) {
				getParityFromCurrency(currencyListBox.getSelectedId());
			}
			statusEffect();

		}

		// Obtiene paridad de la moneda, primer intento
		public void getParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId, 0);
		}

		// Obtiene paridad de la moneda
		public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
			if (parityFromCurrencyRpcAttempt < 5) {
				setCurrencyId(currencyId);
				setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);

				BmoCurrency bmoCurrency = new BmoCurrency();

				String startDate = bmoBudget.getStartDate().getValue();
				if (startDate.equals("")) {
					startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
				}

				String actionValues = currencyId + "|" + startDate;

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getParityFromCurrencyRpcAttempt() < 5) {
							getParityFromCurrency(getCurrencyId(), getParityFromCurrencyRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {				
						stopLoading();				
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Tipo de Cambio Cambiaria.");
						else
							currencyParityTextBox.setValue(result.getMsg());
					}
				};

				try {	
					if (!isLoading()) {				
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoCurrency.getPmClass(), bmoCurrency, BmoCurrency.ACTION_GETCURRENCYPARITY, "" + actionValues, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + e.toString());
				}
			}
		}


		@Override
		public BmObject populateBObject() throws BmException {
			bmoBudgetItem = new BmoBudgetItem();
			bmoBudgetItem.setId(id);
			bmoBudgetItem.getBudgetId().setValue(budgetId);				
			bmoBudgetItem.getComments().setValue(commentsTextBox.getText());
			bmoBudgetItem.getBudgetItemTypeId().setValue(budgetItemTypeSuggestBox.getSelectedId());
			bmoBudgetItem.getAmount().setValue(amountTextBox.getText());
			bmoBudgetItem.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoBudgetItem.getCurrencyParity().setValue(currencyParityTextBox.getText());

			return bmoBudgetItem;
		}

		public void statusEffect() {		
			saveButton.setVisible(true);
			deleteButton.setVisible(true);
			//currencyListBox.setEnabled(false);
			//currencyParityTextBox.setEnabled(false);

			if (bmoBudget.getStatus().equals(BmoBudget.STATUS_AUTHORIZED)) {
				commentsTextBox.setEnabled(false);
				amountTextBox.setEnabled(false);
				budgetItemTypeSuggestBox.setEnabled(false);
				saveButton.setVisible(false);
				deleteButton.setVisible(false);
			}


			if (currencyListBox.getSelectedId() != ((BmoFlexConfig) getSFParams().getBmoAppConfig())
					.getSystemCurrencyId().toString()) {
				// currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(true);
			} else {
				// currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(false);
			}


		}

		@Override
		public void close() {
			list();

			if (budgetUpdater != null)
				budgetUpdater.update();
		}

		public int getParityFromCurrencyRpcAttempt() {
			return parityFromCurrencyRpcAttempt;
		}

		public void setParityFromCurrencyRpcAttempt(int parityFromCurrencyRpcAttempt) {
			this.parityFromCurrencyRpcAttempt = parityFromCurrencyRpcAttempt;
		}

		public String getCurrencyId() {
			return currencyId;
		}

		public void setCurrencyId(String currencyId) {
			this.currencyId = currencyId;
		}
	}
}
