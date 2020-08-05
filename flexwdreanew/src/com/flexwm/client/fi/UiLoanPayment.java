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

import com.flexwm.client.fi.UiLoan.UiLoanForm.LoanUpdater;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoLoanPayment;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiLoanPayment extends UiList {
	UiDateBox dateDateBox = new UiDateBox();
	TextBox amountTextBox = new TextBox();
	UiSuggestBox propertySuggestBox = new UiSuggestBox(new BmoProperty());
	//UiListBox bankMovConceptListBox = new UiListBox(getUiParams(), new BmoBankMovConcept());
	private Label capitalPaymentLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	BmoLoanPayment  bmoLoanPayment;
	BmoLoan bmoLoan;
	protected LoanUpdater loanUpdater;		

	int loanId;


	public UiLoanPayment(UiParams uiParams, Panel defaultPanel, BmoLoan bmoLoan, int id, LoanUpdater loanUpdater) {
		super(uiParams, defaultPanel, new BmoLoanPayment());
		loanId = id;
		bmoLoanPayment = new BmoLoanPayment();
		this.bmoLoan = bmoLoan;
		this.loanUpdater = loanUpdater;
	}
	@Override
	public void create() {
		UiLoanPaymentForm uiLoanPaymentForm = new UiLoanPaymentForm(getUiParams(), 0);
		uiLoanPaymentForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiLoanPaymentForm uiLoanPaymentForm = new UiLoanPaymentForm(getUiParams(), bmObject.getId());
		uiLoanPaymentForm.show();
	}	


	private class UiLoanPaymentForm extends UiFormDialog {

		public UiLoanPaymentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoLoanPayment(), id);
		}

		@Override
		public void populateFields() {
			bmoLoanPayment = (BmoLoanPayment)getBmObject();

			formFlexTable.addField(1, 0, dateDateBox, bmoLoanPayment.getDate());
			formFlexTable.addField(2, 0, amountTextBox, bmoLoanPayment.getAmount());
			formFlexTable.addField(3, 0, propertySuggestBox, bmoLoanPayment.getPropertyId());
			formFlexTable.addLabelField(4, 0, "Pagos", capitalPaymentLabel);
			//formFlexTable.addField(5, 0, bankMovConceptListBox, bmoLoanPayment.getBankMovConceptId());

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoLoanPayment = new BmoLoanPayment();
			bmoLoanPayment.setId(id);
			bmoLoanPayment.getDate().setValue(dateDateBox.getTextBox().getText());
			bmoLoanPayment.getAmount().setValue(amountTextBox.getText());
			bmoLoanPayment.getPropertyId().setValue(propertySuggestBox.getSelectedId());	
			bmoLoanPayment.getLoanId().setValue(loanId);
			//bmoLoanPayment.getBankMovConceptId().setValue(bankMovConceptListBox.getSelectedId());

			return bmoLoanPayment;
		}

		public void statusEffect() {
			//bankMovConceptListBox.setVisible(false);
			getLoanCapitalBalance();
			setAmount(bmoLoanPayment);
		}

		// Obtiene el valor del servidor prespuesto
		public void getLoanCapitalBalance() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					showErrorMessage(this.getClass().getName() + "-getLoanCapitalBalance() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {		
					capitalPaymentLabel.setText(result.getMsg());
					setAmount();
				}
			};

			try {	
				getUiParams().getBmObjectServiceAsync().action(bmoLoan.getPmClass(), bmoLoan, BmoLoan.ACTION_PAYMENTS, "" + loanId, callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getLoanCapitalBalance() ERROR: " + e.toString());
			}
		} 


		private void setAmount() {
			numberFormat = NumberFormat.getCurrencyFormat();
			capitalPaymentLabel.setText(numberFormat.format(Double.parseDouble(capitalPaymentLabel.getText())));
		}


		private void setAmount(BmoLoanPayment bmoLoanPayment) {
			numberFormat = NumberFormat.getCurrencyFormat();		
			amountTextBox.setText(numberFormat.format(bmoLoanPayment.getAmount().toDouble()));
		}

		@Override
		public void close() {
			list();

			if (loanUpdater != null)
				loanUpdater.update();
		}
	}
}
