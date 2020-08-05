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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoLoanDisbursement;
import com.flexwm.shared.fi.BmoLoanPayment;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiLoan extends UiList {
	BmoLoan bmoLoan;

	public UiLoan(UiParams uiParams) {
		super(uiParams, new BmoLoan());
		bmoLoan = (BmoLoan)getBmObject();
	}

	public UiLoan(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoLoan());
		bmoLoan = (BmoLoan)getBmObject();
	}

	@Override
	public void postShow() {
		if(isMinimalist()) {
			if (!isMobile()) {
				addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoLoan.getSupplierId());
				addFilterSuggestBox(new UiSuggestBox(new BmoDevelopment()), new BmoDevelopment(), bmoLoan.getBmoDevelopmentPhase().getDevelopmentId());
			}
		} else {
			addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoLoan.getSupplierId());
			addFilterSuggestBox(new UiSuggestBox(new BmoDevelopment()), new BmoDevelopment(), bmoLoan.getBmoDevelopmentPhase().getDevelopmentId());
		}
	}

	@Override
	public void create() {
		UiLoanForm uiLoanForm = new UiLoanForm(getUiParams(), 0);
		uiLoanForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiLoanForm uiLoanForm = new UiLoanForm(getUiParams(), bmObject.getId());
		uiLoanForm.show();
	}

	public class UiLoanForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox amountTextBox = new TextBox();
		TextBox rateTextBox = new TextBox();
		TextBox comissionPaymentTextBox = new TextBox();
		TextBox commisionTextBox = new TextBox();
		UiDateBox startDateDateBox = new UiDateBox();
		UiDateBox endDateDateBox = new UiDateBox();
		TextBox disbursedTextBox = new TextBox();
		TextBox progressTextBox = new TextBox();
		TextBox balanceTextBox = new TextBox();
		TextBox disbursedAmountTextBox = new TextBox();
		TextBox capitalPaymentTextBox = new TextBox();
		TextBox capitalBalanceTextBox = new TextBox();
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiSuggestBox supplierIdSuggestBox = new UiSuggestBox(new BmoSupplier());
		UiSuggestBox developmentPhaseIdSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
		CheckBox revolvingCheckBox = new CheckBox();

		String generalSection = "Datos Generales";
		String amountSection = "Montos";
		String detailsSection = "Detalles y Fechas";
		String itemsSection = "Items";

		LoanUpdater loanUpdater = new LoanUpdater();
		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();


		BmoLoan bmoLoan;

		public UiLoanForm(UiParams uiParams, int id) {
			super(uiParams, new BmoLoan(), id);
		}

		public void updateAmount(int id) {
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
				}

				public void onSuccess(BmObject result) {
					stopLoading();				
					setAmount((BmoLoan)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoLoan.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}

		public void reset() {
			updateAmount(id);
		}

		@Override
		public void populateFields() {
			bmoLoan = (BmoLoan)getBmObject();

			if (newRecord) {
				try {
					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoLoan.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR al asignar Empresa: " + e.toString());
				}			
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoLoan.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoLoan.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoLoan.getDescription());

			formFlexTable.addSectionLabel(5, 0, detailsSection, 2);
			formFlexTable.addField(6, 0, startDateDateBox, bmoLoan.getStartDate());
			formFlexTable.addField(7, 0, endDateDateBox, bmoLoan.getEndDate());
			formFlexTable.addField(8, 0, supplierIdSuggestBox, bmoLoan.getSupplierId());
			formFlexTable.addField(9, 0, developmentPhaseIdSuggestBox, bmoLoan.getDevelopmentPhaseId());
			formFlexTable.addField(10, 0, companyListBox, bmoLoan.getCompanyId());
			formFlexTable.addField(11, 0, revolvingCheckBox, bmoLoan.getRevolving());

			formFlexTable.addSectionLabel(12, 0, amountSection, 2);
			formFlexTable.addField(13, 0, rateTextBox, bmoLoan.getRate());
			formFlexTable.addField(14, 0, commisionTextBox, bmoLoan.getCommission());
			formFlexTable.addFieldReadOnly(15, 0, progressTextBox, bmoLoan.getProgress());
			formFlexTable.addField(16, 0, amountTextBox, bmoLoan.getAmount());
			formFlexTable.addField(17, 0, comissionPaymentTextBox, bmoLoan.getCommissionPayment());
			formFlexTable.addFieldReadOnly(18, 0, balanceTextBox, bmoLoan.getBalance());
			formFlexTable.addFieldReadOnly(19, 0, disbursedTextBox, bmoLoan.getDisbursed());
			formFlexTable.addFieldReadOnly(20, 0, capitalPaymentTextBox, bmoLoan.getCapitalPayment());
			formFlexTable.addFieldReadOnly(21, 0, capitalBalanceTextBox, bmoLoan.getCapitalBalance());
			formFlexTable.addFieldReadOnly(22, 0, disbursedAmountTextBox, bmoLoan.getDisbursedAmount());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(23, 0, itemsSection, 2);
				BmoLoanDisbursement bmoLoanDisbursement = new BmoLoanDisbursement();
				FlowPanel loanDisbursementFP = new FlowPanel();
				BmFilter filterLoanDisbursement = new BmFilter();
				filterLoanDisbursement.setValueFilter(bmoLoanDisbursement.getKind(), bmoLoanDisbursement.getLoanId(), bmoLoan.getId());
				getUiParams().setForceFilter(bmoLoanDisbursement.getProgramCode(), filterLoanDisbursement);
				UiLoanDisbursement uiLoanDisbursement = new UiLoanDisbursement(getUiParams(), loanDisbursementFP, bmoLoan, bmoLoan.getId(), loanUpdater);
				setUiType(bmoLoanDisbursement.getProgramCode(), UiParams.MINIMALIST);
				uiLoanDisbursement.show();
				formFlexTable.addPanel(24, 0, loanDisbursementFP, 2);

				BmoLoanPayment bmoLoanPayment = new BmoLoanPayment();
				FlowPanel loanPaymentFP = new FlowPanel();
				BmFilter filterLoanPayment = new BmFilter();
				filterLoanPayment.setValueFilter(bmoLoanPayment.getKind(), bmoLoanPayment.getLoanId(), bmoLoan.getId());
				getUiParams().setForceFilter(bmoLoanPayment.getProgramCode(), filterLoanPayment);
				UiLoanPayment uiLoanPayment = new UiLoanPayment(getUiParams(), loanPaymentFP, bmoLoan, bmoLoan.getId(), loanUpdater);
				setUiType(bmoLoanPayment.getProgramCode(), UiParams.MINIMALIST);
				uiLoanPayment.show();
				formFlexTable.addPanel(25, 0, loanPaymentFP, 2);
			}
			if (!newRecord)
				formFlexTable.hideSection(detailsSection);

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoLoan.setId(id);
			bmoLoan.getCode().setValue(codeTextBox.getText());
			bmoLoan.getName().setValue(nameTextBox.getText());
			bmoLoan.getDescription().setValue(descriptionTextArea.getText());
			bmoLoan.getAmount().setValue(amountTextBox.getText());
			bmoLoan.getRate().setValue(rateTextBox.getText());
			bmoLoan.getCommissionPayment().setValue(comissionPaymentTextBox.getText());
			bmoLoan.getCommission().setValue(commisionTextBox.getText());
			bmoLoan.getBalance().setValue(balanceTextBox.getText());
			bmoLoan.getStartDate().setValue(startDateDateBox.getTextBox().getText());
			bmoLoan.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoLoan.getDisbursed().setValue(disbursedTextBox.getText());
			bmoLoan.getProgress().setValue(progressTextBox.getText());
			bmoLoan.getSupplierId().setValue(supplierIdSuggestBox.getSelectedId());
			bmoLoan.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoLoan.getDevelopmentPhaseId().setValue(developmentPhaseIdSuggestBox.getSelectedId());
			bmoLoan.getDisbursedAmount().setValue(disbursedAmountTextBox.getText());
			bmoLoan.getCapitalPayment().setValue(capitalPaymentTextBox.getText());
			bmoLoan.getCapitalBalance().setValue(capitalBalanceTextBox.getText());
			bmoLoan.getRevolving().setValue(revolvingCheckBox.getValue());

			return bmoLoan;
		}

		public void statusEffect() {	
			setAmount(bmoLoan);
		}

		public void setAmount(BmoLoan bmoLoan) {	
			numberFormat = NumberFormat.getCurrencyFormat();
//			balanceTextBox.setText(numberFormat.format(bmoLoan.getBalance().toDouble()));
//			amountTextBox.setText(numberFormat.format(bmoLoan.getAmount().toDouble()));
//			comissionPaymentTextBox.setText(numberFormat.format(bmoLoan.getCommissionPayment().toDouble()));
			disbursedTextBox.setText(numberFormat.format(bmoLoan.getDisbursed().toDouble()));
			capitalPaymentTextBox.setText(numberFormat.format(bmoLoan.getCapitalPayment().toDouble()));
			capitalBalanceTextBox.setText(numberFormat.format(bmoLoan.getCapitalBalance().toDouble()));
			disbursedAmountTextBox.setText(numberFormat.format(bmoLoan.getDisbursedAmount().toDouble()));
			setAmount("" + bmoLoan.getBalance().toDouble(), "" + bmoLoan.getAmount().toDouble(), "" + bmoLoan.getCommissionPayment().toDouble());
		}

		private void setAmount(String balance, String amount, String downPayment) {		
			balanceTextBox.setText(numberFormat.format(Double.parseDouble(balance)));
			amountTextBox.setText(numberFormat.format(Double.parseDouble(amount)));
			comissionPaymentTextBox.setText(numberFormat.format(Double.parseDouble(downPayment)));

		}

		protected class LoanUpdater {
			public void update() {
				stopLoading();
				reset();
			}		
		}

		@Override
		public void close() {
			
//			if (isSlave()) {
//				//
//			} else {
//				if (getBmObject().getId() > 0) {
////					showSystemMessage("1111");
////					UiRequisitionForm UiRequisitionForm = new UiRequisitionForm(getUiParams(), getBmObject().getId());
////					UiRequisitionForm.show();
//				} else {
//					//showSystemMessage("2222");
//					list();
//				}
//			}

		}

	}
}


