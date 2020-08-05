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
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiBudget extends UiList {
	BmoBudget bmoBudget;

	public UiBudget(UiParams uiParams) {
		super(uiParams, new BmoBudget());
		bmoBudget = (BmoBudget)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoBudget.getUserId());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoBudget.getStatus()), bmoBudget, bmoBudget.getStatus());
	}

	@Override
	public void create() {
		UiBudgetForm uiBudgetForm = new UiBudgetForm(getUiParams(), 0);
		uiBudgetForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiBudgetForm uiBudgetForm = new UiBudgetForm(getUiParams(), bmObject.getId());
		uiBudgetForm.show();
	}

	public void edit(int budgetId) {
		UiBudgetForm uiBudgetForm = new UiBudgetForm(getUiParams(), budgetId);
		uiBudgetForm.show();
	}

	public class UiBudgetForm extends UiFormDialog {	
		BmoBudget bmoBudget;	
		BmoDevelopmentPhase bmoDevelopmentPhase;
		private FlowPanel uiWorkBudgetItemPanel = new FlowPanel();
		BudgetUpdater budgetUpdater = new BudgetUpdater();

//		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox statusListBox = new UiListBox(getUiParams());
		TextBox balanceTextBox = new TextBox();
		TextBox provisionedTextBox = new TextBox();
		TextBox paymentsTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		
		//Egresos
		TextBox provisionedWithdrawTextBox = new TextBox();
		TextBox paymentWithdrawTextBox = new TextBox();
		TextBox totalWithdrawTextBox = new TextBox();
		TextBox balanceWithdrawTextBox = new TextBox();

		//Ingresos
		TextBox provisionedDepositTextBox = new TextBox();
		TextBox paymentDepositTextBox = new TextBox();
		TextBox totalDepositTextBox = new TextBox();
		TextBox balanceDepositTextBox = new TextBox();

		UiDateBox startDateBox = new UiDateBox();
		UiDateBox endDateBox = new UiDateBox();
		UiSuggestBox userIdSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyId;		

		String generalSection = "Datos Generales";
		String itemSection = "Items";
		String depositSection = "Total Ingresos";
		String withdrawSection = "Total Egresos";
		String balanceSection = "Saldo";

		public UiBudgetForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBudget(), id);

			// Panel de staff
			uiWorkBudgetItemPanel.setWidth("100%");
		}

		@Override
		public void populateFields(){
			bmoBudget = (BmoBudget)getBmObject();	

			// Preparar campos
			if (newRecord) {
				try {
					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoBudget.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}			
			}

			// Mostrar usuarios Activos
			BmoUser bmoUser = new BmoUser();
			BmFilter bmFilterUsersActives = new BmFilter();
			bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userIdSuggestBox.addFilter(bmFilterUsersActives);

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(3, 0, nameTextBox, bmoBudget.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoBudget.getDescription());		
			formFlexTable.addField(5, 0, userIdSuggestBox, bmoBudget.getUserId());
			formFlexTable.addField(6, 0, startDateBox, bmoBudget.getStartDate());
			formFlexTable.addField(8, 0, endDateBox, bmoBudget.getEndDate());
			formFlexTable.addField(9, 0, companyListBox, bmoBudget.getCompanyId());
			formFlexTable.addField(10, 0, currencyListBox, bmoBudget.getCurrencyId());			
			formFlexTable.addField(11, 0, currencyParityTextBox, bmoBudget.getCurrencyParity());
			formFlexTable.addField(12, 0, statusListBox, bmoBudget.getStatus());

			if (!newRecord) {	
				// Items
				formFlexTable.addSectionLabel(13, 0, itemSection, 2);
				BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
				FlowPanel budgetItemFP = new FlowPanel();
				BmFilter filterBudgetItems = new BmFilter();
				filterBudgetItems.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId(), bmoBudget.getId());
				getUiParams().setForceFilter(bmoBudgetItem.getProgramCode(), filterBudgetItems);
				UiBudgetItem uiBudgetItem = new UiBudgetItem(getUiParams(), budgetItemFP, bmoBudget, bmoBudget.getId(), budgetUpdater);
				setUiType(bmoBudgetItem.getProgramCode(), UiParams.MINIMALIST);
				uiBudgetItem.show();
				formFlexTable.addPanel(14, 0, budgetItemFP, 2);

				formFlexTable.addSectionLabel(15, 0, depositSection, 2);
				formFlexTable.addField(16, 0, totalDepositTextBox, bmoBudget.getTotalDeposit());
				formFlexTable.addField(17, 0, provisionedDepositTextBox, bmoBudget.getProvisionedDeposit());
				formFlexTable.addField(18, 0, paymentDepositTextBox, bmoBudget.getPaymentDeposit());
				formFlexTable.addField(19, 0, balanceDepositTextBox, bmoBudget.getBalanceDeposit());

				formFlexTable.addSectionLabel(20, 0, withdrawSection, 2);
				formFlexTable.addField(21, 0, totalWithdrawTextBox, bmoBudget.getTotalWithdraw());
				formFlexTable.addField(22, 0, provisionedWithdrawTextBox, bmoBudget.getProvisionedWithdraw());
				formFlexTable.addField(23, 0, paymentWithdrawTextBox, bmoBudget.getPaymentWithdraw());
				formFlexTable.addField(24, 0, balanceWithdrawTextBox, bmoBudget.getBalanceWithdraw());

				formFlexTable.addSectionLabel(25, 0, balanceSection, 2);
				formFlexTable.addField(26, 0, totalTextBox, bmoBudget.getTotal());
				formFlexTable.addField(27, 0, provisionedTextBox, bmoBudget.getProvisioned());
				formFlexTable.addField(28, 0, paymentsTextBox, bmoBudget.getPayments());			
				formFlexTable.addField(29, 0, balanceTextBox, bmoBudget.getBalance());
			} 

			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {		
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status del Presupuesto?");
			}
			else if (event.getSource() == currencyListBox) {
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
						String startDate = startDateBox.getTextBox().getText();
						if (startDateBox.getTextBox().getText().equals("")) {
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

		public void statusEffect() {		
			totalTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
//			codeTextBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			//currencyListBox.setEnabled(false);
			//currencyParityTextBox.setEnabled(false);
			//Totales
			provisionedTextBox.setEnabled(false);
			paymentsTextBox.setEnabled(false);
			balanceTextBox.setEnabled(false);		
			//Egresos
			provisionedWithdrawTextBox.setEnabled(false);
			paymentWithdrawTextBox.setEnabled(false);
			totalWithdrawTextBox.setEnabled(false);
			balanceWithdrawTextBox.setEnabled(false);
			//Ingresos
			provisionedDepositTextBox.setEnabled(false);
			paymentDepositTextBox.setEnabled(false);
			totalDepositTextBox.setEnabled(false);
			balanceDepositTextBox.setEnabled(false);

			if (!isSlave()) {
//				codeTextBox.setEnabled(true);
				nameTextBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				userIdSuggestBox.setEnabled(true);
				startDateBox.setEnabled(true);
				endDateBox.setEnabled(true);
				companyListBox.setEnabled(true);
			} 

			if (bmoBudget.getStatus().equals(BmoBudget.STATUS_AUTHORIZED)
					|| bmoBudget.getStatus().equals(BmoBudget.STATUS_CLOSED)) {
//				codeTextBox.setEnabled(false);
				nameTextBox.setEnabled(false);
				descriptionTextArea.setEnabled(false);
				userIdSuggestBox.setEnabled(false);
				startDateBox.setEnabled(false);
				endDateBox.setEnabled(false);
				companyListBox.setEnabled(false);
				deleteButton.setVisible(false);
				saveButton.setVisible(false);
			}

			if (!newRecord) {
				if (getSFParams().hasSpecialAccess(BmoBudget.ACCESS_CHANGESTATUS)) {					
					statusListBox.setEnabled(true);
				}
			}
			
			if (currencyListBox.getSelectedId() != ((BmoFlexConfig) getSFParams().getBmoAppConfig())
					.getSystemCurrencyId().toString()) {
				// currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(true);
			} else {
				// currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(false);
			}
			

			setAmount(bmoBudget);

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoBudget.setId(id);			
			bmoBudget.getName().setValue(nameTextBox.getText());
			bmoBudget.getDescription().setValue(descriptionTextArea.getText());
			bmoBudget.getTotal().setValue(totalTextBox.getText());
			bmoBudget.getStatus().setValue(statusListBox.getSelectedCode());
			bmoBudget.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoBudget.getEndDate().setValue(endDateBox.getTextBox().getText());
			bmoBudget.getUserId().setValue(userIdSuggestBox.getSelectedId());
			bmoBudget.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoBudget.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoBudget.getCurrencyParity().setValue(currencyParityTextBox.getText());
			
			//Egresos
			bmoBudget.getProvisionedWithdraw();
			bmoBudget.getPaymentWithdraw();		
			bmoBudget.getTotalWithdraw();
			bmoBudget.getBalanceWithdraw();		
			//Ingresos
			bmoBudget.getProvisionedDeposit();
			bmoBudget.getPaymentDeposit();
			bmoBudget.getTotalDeposit();
			bmoBudget.getBalanceDeposit();

			return bmoBudget;
		}

		@Override
		public void close() {
			
//			if (isSlave()) {
//				//
//			} else {
//				if (getBmObject().getId() > 0) {
//					UiBudgetForm uiBudgetForm = new UiBudgetForm(getUiParams(), getBmObject().getId());
//					uiBudgetForm.show();
//				} else {
//					list();
//				}
//			}
		}

		@Override
		public void saveNext() {	
			if (getUiType() == UiParams.MASTER) {
				if (newRecord) {
					UiBudgetForm uiBudgetForm = new UiBudgetForm(getUiParams(), getBmObject().getId());
					uiBudgetForm.show();
				} else {
					UiBudget uiBudgetList = new UiBudget(getUiParams());
					uiBudgetList.show();
				}		
			} else {
				showFormMsg("Cambios almacenados exitosamente.", "Cambios almacenados exitosamente.");
			}
		}

		public void reset(){
			updateAmount(id);
		}

		public void updateAmount(int id){
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
				}

				public void onSuccess(BmObject result) {
					stopLoading();				
					setAmount((BmoBudget)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoBudget.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}

		public void setAmount(BmoBudget bmoBudget) {
			numberFormat = NumberFormat.getCurrencyFormat();
			provisionedTextBox.setText(numberFormat.format(bmoBudget.getProvisioned().toDouble()));
			paymentsTextBox.setText(numberFormat.format(bmoBudget.getPayments().toDouble()));
			balanceTextBox.setText(numberFormat.format(bmoBudget.getBalance().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoBudget.getTotal().toDouble()));

			//Egresos
			provisionedWithdrawTextBox.setText(numberFormat.format(bmoBudget.getProvisionedWithdraw().toDouble()));
			paymentWithdrawTextBox.setText(numberFormat.format(bmoBudget.getPaymentWithdraw().toDouble()));
			totalWithdrawTextBox.setText(numberFormat.format(bmoBudget.getTotalWithdraw().toDouble()));
			balanceWithdrawTextBox.setText(numberFormat.format(bmoBudget.getBalanceWithdraw().toDouble()));

			//Ingresos
			provisionedDepositTextBox.setText(numberFormat.format(bmoBudget.getProvisionedDeposit().toDouble()));
			paymentDepositTextBox.setText(numberFormat.format(bmoBudget.getPaymentDeposit().toDouble()));
			totalDepositTextBox.setText(numberFormat.format(bmoBudget.getTotalDeposit().toDouble()));
			balanceDepositTextBox.setText(numberFormat.format(bmoBudget.getBalanceDeposit().toDouble()));
		}

		public class BudgetUpdater {
			public void update() {
				stopLoading();
				reset();
			}		
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
