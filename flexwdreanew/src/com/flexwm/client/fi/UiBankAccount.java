/**
 * 
 */
package com.flexwm.client.fi;

import java.util.Iterator;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankAccountType;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
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
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class UiBankAccount extends UiList {
	BmoBankAccount bmoBankAccount;
	protected Label labelBankAccount;

	public UiBankAccount(UiParams uiParams) {
		super(uiParams, new BmoBankAccount());
		bmoBankAccount = (BmoBankAccount)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoBankAccount.getResponsibleId());
		
		// Boton para Cuentas de banco con saldo
		labelBankAccount = new Label("Ver Saldo");
		labelBankAccount.setTitle("Ver Saldo.");
		labelBankAccount.setStyleName("listAllImage");
		labelBankAccount.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				listBankAccounts();
			}
		});
		actionItems.add(labelBankAccount);
	}

	@Override
	public void create() {
		UiBankAccountForm uiBankAccountForm = new UiBankAccountForm(getUiParams(), 0);
		uiBankAccountForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiBankAccountForm uiBankAccountForm = new UiBankAccountForm(getUiParams(), bmObject.getId());
		uiBankAccountForm.show();
	}

	// Tomar Cuentas de Banco seleccionadas
	public void listBankAccounts() {
		String bankAccountId = "&" + bmoBankAccount.getIdFieldName() + "=";
		String bankAccountIdLabel = "&" + bmoBankAccount.getIdFieldName() + "Label=";
		String values = "", valuesLabels = "";
			Iterator<BmObject> listPayment = listFlexTable.getListCheckBoxSelectedBmObjectList().iterator();
			while (listPayment.hasNext()) {
				BmoBankAccount bmoBankAccount = (BmoBankAccount) listPayment.next();
				values +=  bmoBankAccount.getId() + " : ";
				valuesLabels += bmoBankAccount.getName().toString() + " ";
			}
			showListBankAccount(bankAccountId + values + bankAccountIdLabel + valuesLabels);
	}
	
	// Mandar datos al reporte
	public void showListBankAccount(String bankAccountId) {
		String url = "/rpt/fi/bkac_general_report.jsp?programId=" + bmObjectProgramId + bankAccountId;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}

	public class UiBankAccountForm extends UiFormDialog {	
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams(), new BmoBankAccountType());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		UiDateBox openDateDateBox = new UiDateBox();
		TextBox accountNoTextBox = new TextBox();
		TextBox clabeTextBox = new TextBox();	
		UiDateBox cutDateDateBox = new UiDateBox();
		TextBox initialBalanceTextBox = new TextBox();
		TextBox bankNameTextBox = new TextBox();
		TextBox branchTextBox = new TextBox();
		TextBox logoTextBox = new TextBox();
		TextBox repNameTextBox = new TextBox();
		TextBox phoneTextBox = new TextBox();
		TextBox checkNoTextBox = new TextBox();
		TextBox balanceTextBox = new TextBox();
		UiSuggestBox reponsibleSuggestBox = new UiSuggestBox(new BmoUser());
		TextBox bankRfcTextBox = new TextBox();
		Label balanceLabel = new Label("Calculando...");
		Button balanceButton = new Button("SALDO");
		String statusSection = "Estatus"; 
		UiListBox statusListBox = new UiListBox(getUiParams());
		BmoBankAccount bmoBankAccount;

		private String bankAccountId = "";
		private int balanceRpcAttempt = 0;
		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		
		public UiBankAccountForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBankAccount(), id);
			
			// Botón de saldo
			balanceButton.setStyleName("formSaveButton");
			balanceButton.setVisible(true);
			balanceButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getBalance("" + id);
				}
			});
		}
		
		@Override
		public void populateFields() {
			bmoBankAccount = (BmoBankAccount)getBmObject();

			if (newRecord) {
				if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
					try {
						bmoBankAccount.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					} catch (BmException e) {
						showSystemMessage(this.getClass().getName() + "-populate(): " + e.toString());
					}
			}

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoBankAccount.getCurrencyId().toInteger() > 0)) {
				try {
					bmoBankAccount.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				} catch (BmException e) {
					showSystemMessage("No se puede asignar moneda : " + e.toString());
				}
			}

			formFlexTable.addField(1, 0, nameTextBox, bmoBankAccount.getName());		
			formFlexTable.addField(2, 0, descriptionTextArea, bmoBankAccount.getDescription());
			  formFlexTable.addField(3, 0, typeListBox, bmoBankAccount.getBankAccountTypeId());
			formFlexTable.addField(4, 0, openDateDateBox, bmoBankAccount.getOpenDate());
			formFlexTable.addField(5, 0, cutDateDateBox, bmoBankAccount.getCutDate());
			formFlexTable.addField(6, 0, accountNoTextBox, bmoBankAccount.getAccountNo());
			formFlexTable.addField(7, 0, clabeTextBox, bmoBankAccount.getClabe());
			if (!newRecord)
				formFlexTable.addLabelField(8, 0, "Saldo:", balanceLabel);
//			if (!newRecord)
//				formFlexTable.addButtonCell(8, 0, balanceButton);
			//formFlexTable.addFieldReadOnly(9, 0, balanceTextBox, bmoBankAccount.getBalance());
			formFlexTable.addField(10, 0, currencyListBox, bmoBankAccount.getCurrencyId());
			formFlexTable.addField(11, 0, bankNameTextBox, bmoBankAccount.getBankName());
			formFlexTable.addField(12, 0, bankRfcTextBox, bmoBankAccount.getBankRfc());
			formFlexTable.addField(13, 0, branchTextBox, bmoBankAccount.getBranch());
			formFlexTable.addField(14, 0, repNameTextBox, bmoBankAccount.getRepName());
			formFlexTable.addField(15, 0, phoneTextBox, bmoBankAccount.getPhone());
			formFlexTable.addField(16, 0, checkNoTextBox, bmoBankAccount.getCheckNo());
			formFlexTable.addField(17, 0, companyListBox, bmoBankAccount.getCompanyId());
			formFlexTable.addField(18, 0, profileListBox, bmoBankAccount.getProfileId());
			formFlexTable.addField(19, 0, reponsibleSuggestBox, bmoBankAccount.getResponsibleId());
			formFlexTable.addSectionLabel(20, 0, statusSection, 2);
			formFlexTable.addField(21, 0,statusListBox, bmoBankAccount.getStatus());	
			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoBankAccount.setId(id);
			bmoBankAccount.getName().setValue(nameTextBox.getText());		
			bmoBankAccount.getDescription().setValue(descriptionTextArea.getText());
			bmoBankAccount.getOpenDate().setValue(openDateDateBox.getTextBox().getText());
			bmoBankAccount.getCutDate().setValue(cutDateDateBox.getTextBox().getText());
			bmoBankAccount.getAccountNo().setValue(accountNoTextBox.getText());
			bmoBankAccount.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoBankAccount.getProfileId().setValue(profileListBox.getSelectedId());		
			bmoBankAccount.getBalance().setValue(balanceTextBox.getText());
			bmoBankAccount.getBankName().setValue(bankNameTextBox.getText());
			bmoBankAccount.getBranch().setValue(branchTextBox.getText());
			bmoBankAccount.getRepName().setValue(repNameTextBox.getText());
			bmoBankAccount.getPhone().setValue(phoneTextBox.getText());
			bmoBankAccount.getCheckNo().setValue(checkNoTextBox.getText());
			bmoBankAccount.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoBankAccount.getResponsibleId().setValue(reponsibleSuggestBox.getSelectedId());
			bmoBankAccount.getClabe().setValue(clabeTextBox.getText());
			bmoBankAccount.getBankRfc().setValue(bankRfcTextBox.getText());
			bmoBankAccount.getStatus().setValue(statusListBox.getSelectedCode());
			bmoBankAccount.getBankAccountTypeId().setValue(typeListBox.getSelectedId());
			return bmoBankAccount;
		}

		@Override
		public void close() {
			list();
		}

		public void statusEffect() {
			checkNoTextBox.setEnabled(false);

			if (getSFParams().hasSpecialAccess(BmoBankAccount.ACCESS_CHANGENOCHECK))
				checkNoTextBox.setEnabled(true);

			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);

			if (!newRecord) 
				getBalance("" + id);
		}

		public void getBalance(String bankAccountId) {
			getBalance(bankAccountId, 0);
		}

		public void getBalance(String bankAccountId, int balanceRpcAttempt) {
			if (balanceRpcAttempt < 5) {
				setBankAccountId(bankAccountId);
				setBalanceRpcAttempt(balanceRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {

						stopLoading();
						if (getBalanceRpcAttempt() < 5)
							getBalance(getBankAccountId(), getBalanceRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBalance() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						setBalanceRpcAttempt(0);
						setAmount(result.getMsg());					
					}
				};

				try {	
					if (!isLoading()) {				
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoBankAccount.getPmClass(), bmoBankAccount, BmoBankAccount.ACTION_GETBALANCE, "" + bmoBankAccount.getId(), callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBalance() ERROR: " + e.toString());
				}
			}
		}
		
		public void setAmount(String balance) {
			numberFormat = NumberFormat.getCurrencyFormat();
			balanceLabel.setText("" + numberFormat.format(Double.parseDouble(balance)));
		}

		// Varibles RPC
		public String getBankAccountId() {
			return bankAccountId;
		}

		public void setBankAccountId(String bankAccountId) {
			this.bankAccountId = bankAccountId;
		}

		public int getBalanceRpcAttempt() {
			return balanceRpcAttempt;
		}

		public void setBalanceRpcAttempt(int balanceRpcAttempt) {
			this.balanceRpcAttempt = balanceRpcAttempt;
		}
	} 
}
