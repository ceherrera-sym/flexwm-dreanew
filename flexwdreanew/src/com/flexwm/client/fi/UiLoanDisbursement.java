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
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoLoanDisbursement;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiLoanDisbursement extends UiList {
	TextBox progressTextBox = new TextBox();
	TextBox amountTextBox = new TextBox();
	UiDateBox dateDateBox = new UiDateBox();
	TextBox balanceTextBox = new TextBox();
	//UiListBox bankMovConceptListBox = new UiListBox(getUiParams(), new BmoBankMovConcept());
	private Label balanceLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	BmoLoanDisbursement bmoLoanDisbursement;
	BmoLoan bmoLoan;
	protected LoanUpdater loanUpdater;		
	int loanId;

	public UiLoanDisbursement(UiParams uiParams, Panel defaultPanel, BmoLoan bmoLoan, int id, LoanUpdater loanUpdater) {
		super(uiParams, defaultPanel, new BmoLoanDisbursement());
		loanId = id;
		bmoLoanDisbursement = new BmoLoanDisbursement();
		this.bmoLoan = bmoLoan;
		this.loanUpdater = loanUpdater;
	}

	@Override
	public void create() {
		UiLoanDisbursementForm uiLoanDisbursementForm = new UiLoanDisbursementForm(getUiParams(), 0);
		uiLoanDisbursementForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiLoanDisbursementForm uiLoanDisbursementForm = new UiLoanDisbursementForm(getUiParams(), bmObject.getId());
		uiLoanDisbursementForm.show();
	}	

	private class UiLoanDisbursementForm extends UiFormDialog {

		public UiLoanDisbursementForm(UiParams uiParams, int id) {
			super(uiParams, new BmoLoanDisbursement(), id);
		}

		@Override
		public void populateFields() {
			bmoLoanDisbursement = (BmoLoanDisbursement)getBmObject();
			formFlexTable.addField(1, 0, dateDateBox, bmoLoanDisbursement.getDate());
			formFlexTable.addField(2, 0, amountTextBox, bmoLoanDisbursement.getAmount());
			formFlexTable.addField(3, 0, progressTextBox, bmoLoanDisbursement.getProgress());
			formFlexTable.addLabelField(4, 0, "Balance", balanceLabel);
			//formFlexTable.addField(5, 0, bankMovConceptListBox, bmoLoanDisbursement.getBankMovConceptId());

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoLoanDisbursement = new BmoLoanDisbursement();
			bmoLoanDisbursement.setId(id);
			bmoLoanDisbursement.getDate().setValue(dateDateBox.getTextBox().getText());
			bmoLoanDisbursement.getAmount().setValue(amountTextBox.getText());
			bmoLoanDisbursement.getBalance().setValue(balanceTextBox.getText());
			bmoLoanDisbursement.getProgress().setValue(progressTextBox.getText());	
			bmoLoanDisbursement.getLoanId().setValue(loanId);
			//bmoLoanDisbursement.getBankMovConceptId().setValue(bankMovConceptListBox.getSelectedId());

			return bmoLoanDisbursement;
		}

		public void statusEffect() {
			deleteButton.setVisible(true);
			//bankMovConceptListBox.setVisible(false);		

			//Si viene de una disposicion desde bancos, no poder modificar
			if (bmoLoanDisbursement.getBankMovConceptId().toInteger() > 0) {
				dateDateBox.setEnabled(false);
				amountTextBox.setEnabled(false);
				deleteButton.setVisible(false);
			}

			getLoanBalance();
			setAmount(bmoLoanDisbursement);
		}

		// Obtiene el valor del servidor prespuesto
		public void getLoanBalance(){
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					showErrorMessage(this.getClass().getName() + "-getLoanBalance() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {		
					balanceLabel.setText(result.getMsg());
					setAmount();
				}
			};

			try {	
				getUiParams().getBmObjectServiceAsync().action(bmoLoan.getPmClass(), bmoLoan, BmoLoan.ACTION_TOTAL,  "" + loanId, callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getLoanBalance() ERROR: " + e.toString());
			}
		} 


		private void setAmount() {
			numberFormat = NumberFormat.getCurrencyFormat();
			balanceLabel.setText(numberFormat.format(Double.parseDouble(balanceLabel.getText())));
		}

		private void setAmount(BmoLoanDisbursement bmoLoanDisbursement) {
			numberFormat = NumberFormat.getCurrencyFormat();		
			amountTextBox.setText(numberFormat.format(bmoLoanDisbursement.getAmount().toDouble()));
		}

		@Override
		public void close() {
			list();

			if (loanUpdater != null)
				loanUpdater.update();
		}
	}
}
