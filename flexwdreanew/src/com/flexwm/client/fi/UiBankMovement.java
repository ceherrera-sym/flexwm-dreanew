/**
 * 
 */
package com.flexwm.client.fi;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;


public class UiBankMovement extends UiList {
	BmoBankMovement bmoBankMovement;
	UiListBox bankAccountFilterListBox = new UiListBox(getUiParams(), new BmoBankAccount());
	UiListBox bankMovTypeFilterListBox = new UiListBox(getUiParams(), new BmoBankMovType());

	public UiBankMovement(UiParams uiParams) {
		super(uiParams, new BmoBankMovement());
		this.bmoBankMovement = (BmoBankMovement)getBmObject();
	}

	@Override
	public void postShow() {
		populateStatusListBox();
		if (isMaster()) {
			addFilterListBox(bankAccountFilterListBox, bmoBankMovement.getBmoBankAccount());
			addFilterListBox(bankMovTypeFilterListBox, bmoBankMovement.getBmoBankMovType());
			if (!isMobile()) {
				//addFilterSuggestBox(new UiSuggestBox(new BmoRequisition()), new BmoRequisition(), bmoBankMovement.getRequisitionId());
				addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoBankMovement.getCustomerId());
				addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoBankMovement.getSupplierId());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoBankMovement.getStatus()), bmoBankMovement, bmoBankMovement.getStatus());
				addDateRangeFilterListBox(bmoBankMovement.getDueDate(), GwtUtil.firstDayOfMonthToString(getUiParams().getSFParams().getDateFormat(), -2), "");
			}
			if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGESTATUS)) {
				addActionBatchListBox(new UiListBox(getUiParams(), new BmoBankMovement().getStatus()), bmoBankMovement);
			}	
		}
	}

	@Override
	public void create() {
		UiBankMovementForm uiBankMovementForm = new UiBankMovementForm(getUiParams(), 0);
		uiBankMovementForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoBankMovement = (BmoBankMovement)bmObject;
		UiBankMovementForm uiBankMovementForm = new UiBankMovementForm(getUiParams(), bmoBankMovement.getId());
		uiBankMovementForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiBankMovementForm uiBankMovementForm = new UiBankMovementForm(getUiParams(), bmObject.getId());
		uiBankMovementForm.show();
	}

	public class UiBankMovementForm extends UiFormDialog {
		UiSuggestBox bankMovementSuggestBox = new UiSuggestBox(new BmoBankMovement());	
		TextArea descriptionTextArea = new TextArea();
		UiDateBox inputDateBox = new UiDateBox();
		UiDateBox dueDateBox = new UiDateBox();
		TextBox withdrawTextBox = new TextBox();
		TextBox depositTextBox = new TextBox();
		TextBox referenceTextBox = new TextBox();
		TextBox codeTextBox = new TextBox();
		UiListBox bankMovTypeListBox = new UiListBox(getUiParams(), new BmoBankMovType());
		UiListBox bankAccountListBox = new UiListBox(getUiParams(), new BmoBankAccount());
		UiListBox bankAccDestListBox = new UiListBox(getUiParams(), new BmoBankAccount());
		UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());	
		//UiListBox requisitionListBox = new UiListBox(getUiParams(), new BmoRequisition());
		UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox bkmvCancelIdSuggestBox = new UiSuggestBox(new BmoBankMovement());	
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParity = new TextBox();
		UiListBox paymentTypeListBox = new UiListBox(getUiParams(), new BmoPaymentType());
		TextArea commentsTextArea = new TextArea();
		TextArea commentLogTextArea = new TextArea();

		TextBox amountConvertedTextBox = new TextBox();
		UiListBox loanListBox = new UiListBox(getUiParams(), new BmoLoan());
		TextBox noCheckTextBox = new TextBox();

		TextBox parityDestinyTextBox = new TextBox();
		BmField parityDestinyField;

		Label authorizedByLabel = new Label();
		Label authorizedDateLabel = new Label();
		Label reconciledByLabel = new Label();
		Label reconciledDateLabel = new Label();
		Label cancelledByLabel = new Label();
		Label cancelledDateLabel = new Label();

		String generalSection = "Datos Generales";
		String detailsSection = "Detalle y Fechas";
		String amountsSection = "Montos";
		String totalsSection = "Totales";
		String itemsSection = "Items";
		String statusSection = "Estatus";
		String logSection = "Bitácora";

		private int idUserChangeStatus = 0;
		private int userDataChangeStatusRpcAttempt = 0;
		private int bankAccountIdRPC = 0;
		private int balanceBankAccountRpcAttempt = 0;
		private int noCheckNowRpcAttempt = 0;
		private int updateAmountRpcAttempt = 0;
		private int bankMovementIdRPC = 0;
		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyId = "";
		
		boolean multiCompany = false; //multiEmpresa: G100

		Button balanceButton = new Button("VER SALDO CTA.");

		// Saldo Cta Banco
		private Label balanceBankAccountLabel = new Label();
		//private NumberFormat numberFormat = NumberFormat.getFormat("#######.##");
		protected FlowPanel formatPanel;
		protected UiDetailEastFlexTable eastTable = new UiDetailEastFlexTable(getUiParams());

		// Aplicaciones de pagos
		private UiBankMovConceptGrid uiBankMovConceptGrid;
		private FlowPanel uiBankMovConceptPanel = new FlowPanel();

		BmoBankMovement bmoBankMovement;
		BankMovementUpdater bankMovementUpdater = new BankMovementUpdater();


		public UiBankMovementForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBankMovement(), id);		

			// Manejo de cambios de textbox
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					formTextChange(event);
				}
			};
			formFlexTable.setTextChangeHandler(textChangeHandler);

			parityDestinyField = new BmField("parityDestinyTextBoxField", "", "Tipo de Cambio Destino", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);

			// Botón de saldo
			balanceButton.setStyleName("formSaveButton");
			balanceButton.setVisible(true);
			balanceButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getBalanceBankAccount(Integer.parseInt(bankAccountListBox.getSelectedId()));
				}
			});
		}

		@Override
		public void populateFields() {
			bmoBankMovement = (BmoBankMovement)getBmObject();			
			populateStatusListBox();
			
			// MultiEmpresa: g100
			multiCompany = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean();
			
			// Filtrar el tipo de deposito de categoria tranfer
			BmoBankMovType bmoBankMovType = new BmoBankMovType();		
			BmFilter filterNoTraspDep = new BmFilter();

			if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_TRANSFER && 
					bmoBankMovement.getBmoBankMovType().getType().toChar() == BmoBankMovType.TYPE_DEPOSIT) {
				if (newRecord)
					filterNoTraspDep.setValueFilter(bmoBankMovType.getKind(), bmoBankMovType.getVisible(), "" + BmoBankMovType.VISIBLE_FALSE);
				else
					filterNoTraspDep.setValueFilter(bmoBankMovType.getKind(), bmoBankMovType.getIdField(), bmoBankMovement.getBankMovTypeId().toString());
			} else {
				filterNoTraspDep.setValueFilter(bmoBankMovType.getKind(), bmoBankMovType.getVisible(), "" + BmoBankMovType.VISIBLE_TRUE);
			}
			bankMovTypeListBox.addFilter(filterNoTraspDep);

			// Es nuevo registro
			if (newRecord) {
				//filtrar cuentas de banco cbiertas
				BmoBankAccount bmoBankAccount = new BmoBankAccount();
				BmFilter bmFilterStatus = new BmFilter();
				bmFilterStatus.setValueFilter(bmoBankAccount.getKind(), bmoBankAccount.getStatus(), ""+BmoBankAccount.STATUS_OPEN);			
				bankAccountListBox.addBmFilter(bmFilterStatus);
				try {				
					bmoBankMovement.getInputDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));				
					bmoBankMovement.getDueDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));	

					// Asigna cuenta de banco si esta seleccionado el filtro
					//					if (Integer.parseInt(bankAccountFilterListBox.getSelectedId()) > 0)
					//						bmoBankMovement.getBankAccountId().setValue(bankAccountFilterListBox.getSelectedId());

				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}
			} 
			// Es registro existente
			else {
				formFlexTable.addField(0, 0, bankMovementSuggestBox, bmoBankMovement.getIdField());	
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			if (multiCompany)
				formFlexTable.addField(2, 0, bankAccountListBox, bmoBankMovement.getBankAccountId());
			formFlexTable.addFieldReadOnly(3, 0, codeTextBox, bmoBankMovement.getCode());		
			formFlexTable.addField(4, 0, bankMovTypeListBox, bmoBankMovement.getBankMovTypeId());
			formFlexTable.addField(5, 0, supplierSuggestBox, bmoBankMovement.getSupplierId());
			formFlexTable.addField(6, 0, customerSuggestBox, bmoBankMovement.getCustomerId());
			formFlexTable.addField(7, 0, budgetItemSuggestBox, bmoBankMovement.getBudgetItemId());
			setLoansListBoxFilters(bmoBankMovement.getBmoBankAccount().getCompanyId().toInteger());
			formFlexTable.addField(8, 0, loanListBox, bmoBankMovement.getLoanId());
			//setRequisitionsListBoxFilters(bmoBankMovement.getSupplierId().toInteger());
			//formFlexTable.addField(9, 0, requisitionListBox, bmoBankMovement.getRequisitionId());		
			if (!multiCompany)
				formFlexTable.addField(10, 0, bankAccountListBox, bmoBankMovement.getBankAccountId());
			setBankAccountDestinyListBoxFilters();
			formFlexTable.addField(11, 0, bankAccDestListBox, bmoBankMovement.getBankAccoTransId());	

			formFlexTable.addSectionLabel(12, 0, detailsSection, 2);
			formFlexTable.addField(13, 0, referenceTextBox, bmoBankMovement.getBankReference());
			formFlexTable.addField(14, 0, descriptionTextArea, bmoBankMovement.getDescription());
			formFlexTable.addField(15, 0, noCheckTextBox, bmoBankMovement.getNoCheck());
			formFlexTable.addField(16, 0, dueDateBox, bmoBankMovement.getDueDate());
			formFlexTable.addField(17, 0, paymentTypeListBox, bmoBankMovement.getPaymentTypeId());
			formFlexTable.addField(18, 0, commentsTextArea, bmoBankMovement.getComments());

			formFlexTable.addSectionLabel(19, 0, logSection, 2);
			formFlexTable.addField(20, 0, commentLogTextArea, bmoBankMovement.getCommentLog());
			commentLogTextArea.setEnabled(false);
			commentLogTextArea.setHeight("250px");

			if (newRecord) {
				formFlexTable.addSectionLabel(21, 0, amountsSection, 2);
				formFlexTable.addField(22, 0, withdrawTextBox, bmoBankMovement.getWithdraw());
				formFlexTable.addField(23, 0, depositTextBox, bmoBankMovement.getDeposit());
				formFlexTable.addField(24, 0, currencyParity, bmoBankMovement.getCurrencyParity());
				formFlexTable.addField(25, 0, amountConvertedTextBox, bmoBankMovement.getAmountConverted());
				formFlexTable.addLabelField(26, 0, "Saldo Cta. Banco", balanceBankAccountLabel);
				formFlexTable.addButtonCell(26, 0, balanceButton);

				formFlexTable.addSectionLabel(27, 0, statusSection, 2);				
				formFlexTable.addField(28, 0, statusListBox, bmoBankMovement.getStatus());

			} else {
				if (bmoBankMovement.getBkmvCancelId().toInteger() > 0)
					formFlexTable.addField(21, 0, bkmvCancelIdSuggestBox, bmoBankMovement.getBkmvCancelId());

				formFlexTable.addSectionLabel(22, 0, itemsSection, 2);
				formFlexTable.addPanel(23, 0, uiBankMovConceptPanel);
				uiBankMovConceptGrid = new UiBankMovConceptGrid(getUiParams(), uiBankMovConceptPanel, bmoBankMovement, bankMovementUpdater);
				uiBankMovConceptGrid.show();

				formFlexTable.addSectionLabel(24, 0, amountsSection, 2);
				formFlexTable.addField(25, 0, withdrawTextBox, bmoBankMovement.getWithdraw());
				formFlexTable.addField(26, 0, depositTextBox, bmoBankMovement.getDeposit());
				formFlexTable.addField(27, 0, currencyParity, bmoBankMovement.getCurrencyParity());
				formFlexTable.addField(28, 0, amountConvertedTextBox, bmoBankMovement.getAmountConverted());
				formFlexTable.addLabelField(29, 0, "Saldo Cta. Banco", balanceBankAccountLabel);
				formFlexTable.addButtonCell(29, 0, balanceButton);


				formFlexTable.addSectionLabel(30, 0, statusSection, 2);
				formFlexTable.addLabelField(31, 0, authorizedByLabel, bmoBankMovement.getAuthorizeUserId());
				formFlexTable.addLabelField(32, 0, authorizedDateLabel, bmoBankMovement.getAuthorizeDate());
				formFlexTable.addLabelField(33, 0, reconciledByLabel, bmoBankMovement.getReconciledUserId());
				formFlexTable.addLabelField(34, 0, reconciledDateLabel, bmoBankMovement.getReconciledDate());
				formFlexTable.addLabelField(35, 0, cancelledByLabel, bmoBankMovement.getCancelledUserId());
				formFlexTable.addLabelField(36, 0, cancelledDateLabel, bmoBankMovement.getCancelledDate());

				formFlexTable.addField(37, 0, statusListBox, bmoBankMovement.getStatus());
			}

			// Ocultar campos y secciones
			if (newRecord) {
				formFlexTable.hideField(bmoBankMovement.getSupplierId());
				formFlexTable.hideField(bmoBankMovement.getCustomerId());
				//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
				formFlexTable.hideField(bmoBankMovement.getLoanId());
				formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
				formFlexTable.hideField(bmoBankMovement.getWithdraw());
				formFlexTable.hideField(bmoBankMovement.getDeposit());
				formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
				formFlexTable.hideField(bmoBankMovement.getAmountConverted());
				if (!(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0))
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
			} else {
				formFlexTable.hideSection(detailsSection);

				// Actualiza datos de usuario que autorizo
				showUserDataChangeStatus();
			}
			formFlexTable.hideSection(logSection);

			statusEffect();
		}

		@Override
		public void postShow() {	

			//Si esta en el grupo poder desconciliar		
			if (!newRecord && getSFParams().userInProfile(bmoBankMovement.getBmoBankAccount().getProfileId().toString())) {
				saveButton.setVisible(true);
				if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) &&
						bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
					deleteButton.setVisible(false);
				} else {
					deleteButton.setVisible(true);
				}
			} else if (!newRecord && bmoBankMovement.getStatus().toChar() == BmoBankMovement.STATUS_RECONCILED) {
				if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGESTATUS)) {
					if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGEAUTHORIZED)) {
						statusListBox.setEnabled(true);
					} else {
						deleteButton.setVisible(false);
						saveButton.setVisible(false);
					}
				} else {
					deleteButton.setVisible(false);
					saveButton.setVisible(false);
				}	
			}		
		}

		private void statusEffect() {
			inputDateBox.setEnabled(false);
			dueDateBox.setEnabled(false);
			withdrawTextBox.setEnabled(false);
			depositTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			referenceTextBox.setEnabled(false);
			bankMovTypeListBox.setEnabled(false);
			bankAccountListBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);		
			supplierSuggestBox.setEnabled(false);		
			bankAccDestListBox.setEnabled(false);
			//requisitionListBox.setEnabled(false);
			statusListBox.setEnabled(false);
			budgetItemSuggestBox.setEnabled(false);		
			bkmvCancelIdSuggestBox.setEnabled(false);
			noCheckTextBox.setEnabled(false);
			loanListBox.setEnabled(false);
			currencyParity.setEnabled(false);
			paymentTypeListBox.setEnabled(false);

			if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
				formFlexTable.hideField(bmoBankMovement.getLoanId());
			}

			// Si esta el tipo de cuenta
			BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
			if (bmoBankMovType == null) 
				bmoBankMovType = bmoBankMovement.getBmoBankMovType();	

			if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
				noCheckTextBox.setEnabled(true);
				inputDateBox.setEnabled(true);
				dueDateBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				referenceTextBox.setEnabled(true);
				if (newRecord) {
					bankMovTypeListBox.setEnabled(true);
					bankAccountListBox.setEnabled(true);
				}
				paymentTypeListBox.setEnabled(true);
			}
			if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGEINFORMATION)) {
				inputDateBox.setEnabled(true);
				dueDateBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				referenceTextBox.setEnabled(true);
			}

			if (!(bmoBankMovement.getNoCheck().toString().length() > 0)) {
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC) ||
						bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
						bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					noCheckTextBox.setEnabled(true);
				}
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
					if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_DISPOSAL)) {
						// Mostrar campos
						formFlexTable.hideField(bmoBankMovement.getWithdraw());
						withdrawTextBox.setEnabled(true);
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							formFlexTable.showField(bmoBankMovement.getBudgetItemId());
							budgetItemSuggestBox.setEnabled(true);
						}

						// ocultar campos
						formFlexTable.hideField(bmoBankMovement.getCustomerId());
						formFlexTable.hideField(bmoBankMovement.getSupplierId());
						//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
						formFlexTable.hideField(bmoBankMovement.getLoanId());
						formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
						formFlexTable.hideField(bmoBankMovement.getDeposit());
						formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
						formFlexTable.hideField(bmoBankMovement.getAmountConverted());
					}	
				}
			}

			if (bmoBankMovement.getBmoBankMovType() != null) {
				// Si es de tipo cxc, devolucion cxc, entrega credito
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXC) || 
						bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC) || 
						bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) {

					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getSupplierId());
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) 
						formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
					formFlexTable.hideField(bmoBankMovement.getWithdraw());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());
					formFlexTable.hideField(bmoBankMovement.getDeposit());

					if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXC)) {
						// mostrar campos
						formFlexTable.showField(bmoBankMovement.getCustomerId());
						formFlexTable.showField(bmoBankMovement.getDeposit());
						customerSuggestBox.setEnabled(true);

						//ocultar campos
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
							budgetItemSuggestBox.setEnabled(false);
						}
					}

					if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) {
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							formFlexTable.showField(bmoBankMovement.getBudgetItemId());
							budgetItemSuggestBox.setEnabled(true);
							formFlexTable.hideField(bmoBankMovement.getCustomerId());
							customerSuggestBox.setEnabled(false);
						}
					}

					if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
						//mostrar campos
						formFlexTable.showField(bmoBankMovement.getCustomerId());
						formFlexTable.showField(bmoBankMovement.getWithdraw());
						customerSuggestBox.setEnabled(true);
						noCheckTextBox.setEnabled(true);

						// ocultar campos
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
							budgetItemSuggestBox.setEnabled(false);
						}
						formFlexTable.hideField(bmoBankMovement.getDeposit());
					}

					// Si es de tipo Comisiones
				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_CXP) {
					// mostrar campos
					formFlexTable.showField(bmoBankMovement.getSupplierId());
					supplierSuggestBox.setEnabled(true);
					formFlexTable.showField(bmoBankMovement.getWithdraw());

					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
					formFlexTable.hideField(bmoBankMovement.getDeposit());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());
				} 
				// Multiples CXC	
				else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC)) {
					// mostrar campos
					formFlexTable.showField(bmoBankMovement.getDeposit());

					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getSupplierId());
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					formFlexTable.hideField(bmoBankMovement.getWithdraw());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());

					// Tipo Anticipo	
				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					// mostrar campos
					formFlexTable.showField(bmoBankMovement.getSupplierId());
					//					formFlexTable.showField(bmoBankMovement.getWithdraw());
					//					formFlexTable.showField(bmoBankMovement.getRequisitionId());
					supplierSuggestBox.setEnabled(true);
					//
					//					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
					formFlexTable.hideField(bmoBankMovement.getDeposit());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());
					//
					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {

						withdrawTextBox.setEnabled(true);	
						noCheckTextBox.setEnabled(true);
						//requisitionListBox.setEnabled(true);
						//
						//						// si es nuevo; Compara la moneda del banco contra la moneda de la OC
						//						// muestra/oculta paridad y monto(aplicar) a sacar
						//						// si no es nuevo; si hay valor en campos los muestra
						//						if (newRecord) {
						//							BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
						//							BmoBankAccount bmoBankAccount = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
						//
						//							if (bmoBankAccount.getCurrencyId().toInteger() != bmoRequisition.getCurrencyId().toInteger()) {
						//								formFlexTable.showField(bmoBankMovement.getCurrencyParity());
						//								formFlexTable.showField(bmoBankMovement.getAmountConverted());
						//								currencyParity.setEnabled(true);
						//								currencyParity.setText("");
						//								amountConvertedTextBox.setEnabled(true);
						//								amountConvertedTextBox.setText("");
						//
						//							} else {
						//								formFlexTable.hideField(bmoBankMovement.getDeposit());
						//								formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
						//								formFlexTable.hideField(bmoBankMovement.getAmountConverted());
						//								depositTextBox.setEnabled(false);
						//								currencyParity.setEnabled(false);
						//								amountConvertedTextBox.setEnabled(false);
						//							}
						//						} else {
						//							if (bmoBankMovement.getCurrencyParity().toDouble() > 0)
						//								formFlexTable.showField(bmoBankMovement.getCurrencyParity());
						//							if (bmoBankMovement.getAmountConverted().toDouble() > 0)
						//								formFlexTable.showField(bmoBankMovement.getAmountConverted());
						//						}
					} else if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
						//						// mostrar campos
						formFlexTable.showField(bmoBankMovement.getSupplierId());
						supplierSuggestBox.setEnabled(true);
						formFlexTable.showField(bmoBankMovement.getDeposit());
						//
						//						// ocultar campos
						formFlexTable.hideField(bmoBankMovement.getCustomerId());
						formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
						//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
						formFlexTable.hideField(bmoBankMovement.getLoanId());
						formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
						formFlexTable.hideField(bmoBankMovement.getWithdraw());
						formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
						formFlexTable.hideField(bmoBankMovement.getAmountConverted());

						if (bmoBankMovement.getBkmvCancelId().toInteger() > 0)
							noCheckTextBox.setEnabled(true);
					}

					// Depositos Libres
				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_DEPOSITFREE) {
					// mostrar campos
					formFlexTable.showField(bmoBankMovement.getDeposit());
					depositTextBox.setEnabled(true);

					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getSupplierId());
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());

					formFlexTable.hideField(bmoBankMovement.getWithdraw());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());

				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_TRANSFER) {
					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getSupplierId());
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getBudgetItemId());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					formFlexTable.hideField(bmoBankMovement.getLoanId());
					formFlexTable.hideField(bmoBankMovement.getDeposit());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());

					if (newRecord) {
						//Mostrar el campo de paridad de acuerdo a moneda de la cuenta de banco	
						BmoBankAccount bmoBkacOrign = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
						BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();

						if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
							// Mostrar campos
							formFlexTable.showField(bmoBankMovement.getWithdraw());
							formFlexTable.showField(bmoBankMovement.getBankAccoTransId());
							withdrawTextBox.setEnabled(true);
							bankAccDestListBox.setEnabled(true);

							if (bmoBkacOrign.getCurrencyId().toInteger() != bmoBkacDestiny.getCurrencyId().toInteger()) {
								formFlexTable.showField(bmoBankMovement.getCurrencyParity());
								formFlexTable.showField(bmoBankMovement.getAmountConverted());
								currencyParity.setEnabled(true);
								amountConvertedTextBox.setEnabled(true);
								amountConvertedTextBox.setText("0");
								withdrawTextBox.setText("0");
								if (bmoBkacDestiny.getCurrencyId().toInteger() == ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger()
										&& (bmoBkacDestiny.getCurrencyId().toInteger() != bmoBkacOrign.getCurrencyId().toInteger())) {
									getParityFromCurrency("" + bmoBkacOrign.getCurrencyId().toInteger());
//									currencyParity.setText("" + bmoBkacOrign.getBmoCurrency().getParity().toDouble());
								} else {
									getParityFromCurrency("" + bmoBkacDestiny.getCurrencyId().toInteger());
								}
//									currencyParity.setText("" + bmoBkacDestiny.getBmoCurrency().getParity().toDouble());

							} else {
								currencyParity.setEnabled(false);
								currencyParity.setText("");
								amountConvertedTextBox.setEnabled(false);
								amountConvertedTextBox.setText("");
							}

						} else {
							deleteButton.setVisible(false);
						}
					} else {
						if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
							// mostrar campos
							formFlexTable.showField(bmoBankMovement.getWithdraw());

							// ocultar campos
							formFlexTable.hideField(bmoBankMovement.getDeposit());
						} else if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
							// mostrar campos
							formFlexTable.showField(bmoBankMovement.getDeposit());

							// ocultar campos
							formFlexTable.hideField(bmoBankMovement.getWithdraw());
						}
						if (bmoBankMovement.getCurrencyParity().toDouble() > 0)
							formFlexTable.showField(bmoBankMovement.getCurrencyParity());
						if (bmoBankMovement.getAmountConverted().toDouble() > 0)
							formFlexTable.showField(bmoBankMovement.getAmountConverted());
					}
				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_DISPOSALFREE) {		
					if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_DISPOSAL)) {
						// Mostrar campos
						formFlexTable.showField(bmoBankMovement.getWithdraw());
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							formFlexTable.showField(bmoBankMovement.getBudgetItemId());
							budgetItemSuggestBox.setEnabled(true);
						}
						withdrawTextBox.setEnabled(true);

						// ocultar campos
						formFlexTable.hideField(bmoBankMovement.getSupplierId());
						formFlexTable.hideField(bmoBankMovement.getCustomerId());
						//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
						formFlexTable.hideField(bmoBankMovement.getLoanId());
						formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
						formFlexTable.hideField(bmoBankMovement.getDeposit());
						formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
						formFlexTable.hideField(bmoBankMovement.getAmountConverted());
					}

				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_LOANDISPOSAL) {
					// mostrar campos
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						formFlexTable.showField(bmoBankMovement.getBudgetItemId());
						budgetItemSuggestBox.setEnabled(true);
					}
					formFlexTable.showField(bmoBankMovement.getLoanId());
					formFlexTable.showField(bmoBankMovement.getDeposit());
					loanListBox.setEnabled(true);						
					depositTextBox.setEnabled(true);

					// ocultar campos
					formFlexTable.hideField(bmoBankMovement.getCustomerId());
					formFlexTable.hideField(bmoBankMovement.getSupplierId());
					formFlexTable.hideField(bmoBankMovement.getWithdraw());
					//formFlexTable.hideField(bmoBankMovement.getRequisitionId());
					formFlexTable.hideField(bmoBankMovement.getBankAccoTransId());
					formFlexTable.hideField(bmoBankMovement.getCurrencyParity());
					formFlexTable.hideField(bmoBankMovement.getAmountConverted());

					//					if (newRecord)
					//						populateLoans(((BmoBankAccount)bankAccountListBox.getSelectedBmObject()).getCompanyId().toInteger());

				} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_CREDITDISPOSAL) {
					customerSuggestBox.setEnabled(true);
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						budgetItemSuggestBox.setEnabled(true);					
					}
				}
			}			
			//}

			//}
			if (!newRecord) {
				// Inactivar
				bankMovTypeListBox.setEnabled(false);
				bankAccountListBox.setEnabled(false);
				supplierSuggestBox.setEnabled(false);
				customerSuggestBox.setEnabled(false);
				//requisitionListBox.setEnabled(false);
				loanListBox.setEnabled(false);
				budgetItemSuggestBox.setEnabled(false);
				withdrawTextBox.setEnabled(false);
				depositTextBox.setEnabled(false);
				currencyParity.setEnabled(false);
				amountConvertedTextBox.setEnabled(false);
				statusListBox.setEnabled(false);

				// ultimas validaciones para cambio de estatus
				if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGESTATUS)) {
					statusListBox.setEnabled(true);
				} else if(getSFParams().userInProfile(bmoBankMovement.getBmoBankAccount().getProfileId().toString())) {
					statusListBox.setEnabled(true);
				}

				// Inactivar por estatus
				if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
					// especial para cada caso(tipo)
					if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_LOANDISPOSAL) {
						loanListBox.setEnabled(true);
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean())
							budgetItemSuggestBox.setEnabled(true);
					} else if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_DISPOSALFREE){
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean())
							budgetItemSuggestBox.setEnabled(true);
					}
				} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
					loanListBox.setEnabled(false);
					budgetItemSuggestBox.setEnabled(false);
					noCheckTextBox.setEnabled(false);
					inputDateBox.setEnabled(false);
					dueDateBox.setEnabled(false);
					descriptionTextArea.setEnabled(false);
					referenceTextBox.setEnabled(false);
				} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) { 
					if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGEINFOAUTHO)) {
						dueDateBox.setEnabled(true);
						noCheckTextBox.setEnabled(true);
					}
				} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
					loanListBox.setEnabled(false);
					budgetItemSuggestBox.setEnabled(false);
					noCheckTextBox.setEnabled(false);
					inputDateBox.setEnabled(false);
					dueDateBox.setEnabled(false);
					descriptionTextArea.setEnabled(false);
					referenceTextBox.setEnabled(false);
					statusListBox.setEnabled(false);

					if (bmoBankMovement.getBkmvCancelId().toInteger() > 0) {
						if (getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGECANCELLED)) {
							statusListBox.setEnabled(false);
						} else {
							statusListBox.setEnabled(false);
						}
					}
				}

				// Mostrar saldo de la cuenta de banco
				//				setBalanceBankAccountLabel("" + bmoBankMovement.getBmoBankAccount().getBalance().toDouble());
				//	getBalanceBankAccount(bmoBankAccount.getId());
			}

			if (bmoBankMovement.getWithdraw().toDouble() > 0 || bmoBankMovement.getDeposit().toDouble() > 0)
				setAmount(bmoBankMovement);
		}

		// Conversion de una moneda a otra
		public double currencyExchange(double amount, int currencyIdOrigin, double currencyParityOrigin, int currencyIdDestiny, double currencyParityDestiny) {
			double currencyExchange = 0;
			// Si es la misma moneda regresar el mismo monto, en caso contrario se hace la conversion
			if (currencyIdOrigin == currencyIdDestiny) {
				currencyExchange = amount;
				//				currencyExchange = ((amount * currencyParityOrigin) / currencyParityOrigin);
			} else {
				currencyExchange = ((amount * currencyParityOrigin) / currencyParityDestiny);
			}
			return currencyExchange;
		}

		public void populateStatusListBox() {

			ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusRevision().toBoolean())) {
				status.add(new BmFieldOption(BmoBankMovement.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusAuthorized().toBoolean())) {
				status.add(new BmFieldOption(BmoBankMovement.STATUS_AUTHORIZED, "Autorizado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusReconciled().toBoolean())) {
				status.add(new BmFieldOption(BmoBankMovement.STATUS_RECONCILED, "Conciliado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_reconciled.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusCancelled().toBoolean())) {
				status.add(new BmFieldOption(BmoBankMovement.STATUS_CANCELLED, "Cancelado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
			}

			bmoBankMovement.getStatus().setOptionList(status);
			//			statusListBox.clear();
			//
			//			statusListBox.addItem("< Estatus >", "");
			//			statusListBox.addItem("En Revisión", "" + BmoBankMovement.STATUS_REVISION);
			//			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getAuthorizedBankMov().toBoolean()) {
			//				statusListBox.addItem("Autorizado", "" + BmoBankMovement.STATUS_AUTHORIZED);
			//			}	
			//			statusListBox.addItem("Conciliado", "" + BmoBankMovement.STATUS_RECONCILED);
			//			statusListBox.addItem("Cancelado", "" + BmoBankMovement.STATUS_CANCELLED);
		}

		// Obtener saldo de CTA. BANCO, primer intento
		public void getBalanceBankAccount(int bankAccountIdRPC) {
			getBalanceBankAccount(bankAccountIdRPC, 0);
		}

		// Obtener saldo de CTA. BANCO
		public void getBalanceBankAccount(int bankAccountIdRPC, int balanceRpcAttempt) {
			if (balanceRpcAttempt < 5) {
				setBankAccountIdRPC(bankAccountIdRPC);
				setBalanceBankAccountRpcAttempt(balanceRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {

						stopLoading();
						if (getBalanceBankAccountRpcAttempt() < 5)
							getBalanceBankAccount(getBankAccountIdRPC(), getBalanceBankAccountRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBalanceBankAccount() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						setBalanceBankAccountRpcAttempt(0);
						setBalanceBankAccountLabel(result.getMsg());
					}
				};

				try {	
					BmoBankAccount bmoBankAccount = new BmoBankAccount();
					if (!isLoading()) {	
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoBankAccount.getPmClass(), bmoBankAccount, BmoBankAccount.ACTION_GETBALANCE, "" + bankAccountIdRPC, callback);
					} 
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBalanceBankAccount() ERROR: " + e.toString());
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoBankMovement.setId(id);		
			bmoBankMovement.getDescription().setValue(descriptionTextArea.getText());		
			bmoBankMovement.getBankAccountId().setValue(bankAccountListBox.getSelectedId());		
			bmoBankMovement.getBankAccoTransId().setValue(bankAccDestListBox.getSelectedId());
			bmoBankMovement.getBankReference().setValue(referenceTextBox.getText());				
			bmoBankMovement.getBankMovTypeId().setValue(bankMovTypeListBox.getSelectedId());
			bmoBankMovement.getSupplierId().setValue(supplierSuggestBox.getSelectedId());
			bmoBankMovement.getCustomerId().setValue(customerSuggestBox.getSelectedId());		
			bmoBankMovement.getDueDate().setValue(dueDateBox.getTextBox().getText());
			bmoBankMovement.getStatus().setValue(statusListBox.getSelectedCode());
			//bmoBankMovement.getRequisitionId().setValue(requisitionListBox.getSelectedId());		
			bmoBankMovement.getBkmvCancelId().setValue(bkmvCancelIdSuggestBox.getSelectedId());
			bmoBankMovement.getBudgetItemId().setValue(budgetItemSuggestBox.getSelectedId());
			bmoBankMovement.getLoanId().setValue(loanListBox.getSelectedId());
			bmoBankMovement.getNoCheck().setValue(noCheckTextBox.getText());
			bmoBankMovement.getCurrencyParity().setValue(currencyParity.getText());
			bmoBankMovement.getAmountConverted().setValue(amountConvertedTextBox.getText());
			bmoBankMovement.getComments().setValue(commentsTextArea.getText());
			bmoBankMovement.getCommentLog().setValue(commentLogTextArea.getText());
			if (newRecord) {
				bmoBankMovement.getWithdraw().setValue(withdrawTextBox.getText());
				bmoBankMovement.getDeposit().setValue(depositTextBox.getText());
			} else {
				bmoBankMovement.getWithdraw().setValue(bmoBankMovement.getWithdraw().toDouble());
				bmoBankMovement.getDeposit().setValue(bmoBankMovement.getDeposit().toDouble());
			}
			bmoBankMovement.getPaymentTypeId().setValue(paymentTypeListBox.getSelectedId());

			return bmoBankMovement;
		}

		@Override
		public void close() {
			if (deleted) list();
		}

		@Override
		public void saveNext() {		
			if (newRecord) { 
				UiBankMovementForm uiBankMovementForm = new UiBankMovementForm(getUiParams(), getBmObject().getId());
				uiBankMovementForm.show();
			} else {
				if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() != BmoBankMovType.CATEGORY_DISPOSALFREE) {
					list();
				}
			}		
		}

		public void formTextChange(ValueChangeEvent<String>  event) {
			NumberFormat numberFormat = NumberFormat.getFormat("#######.##");
			BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
			BmoRequisition bmoRequisition = new BmoRequisition();
			BmoBankAccount bmoBkacDestiny = new BmoBankAccount();

			int currencyIdOrigin = 0, currencyIdDestiny = 0;
			double parityOrigin = 0, parityDestiny = 0;

			if (bmoBankMovType == null) 
				bmoBankMovType = bmoBankMovement.getBmoBankMovType();

			// Cuenta de banco origen/destinop
			BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
					//bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
				}
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) 
				bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();


			if (event.getSource() == currencyParity) {
				// ******************** codigo de jv ********************
				//				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				//					BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//					BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();
				//					BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
				//					double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
				//					double parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
				//					double parity = Double.parseDouble(currencyParity.getText());
				//					double amount = Double.parseDouble(withdrawTextBox.getText());
				//					double amountConverted = 0;
				//
				//					if (parityDestiny > parityOrign)
				//						amountConverted = amount / parity;						
				//					else
				//						amountConverted = amount * parity;
				//
				//					//amountConverted = amount * factor;			
				//
				//					amountConvertedTextBox.setText("" + numberFormat.format(amountConverted));
				//
				//					try {
				//						bmoBankMovement.getAmountConverted().setValue(amountConvertedTextBox.getText());
				//					} catch (BmException e) {
				//						showSystemMessage(this.getClass().getName() + 
				//								"-formTextChange() ERROR: No se puedo asignar el Monto(Aplicar)" + e.toString());
				//					}
				//
				//					formFlexTable.showField(bmoBankMovement.getAmountConverted());
				// ******************** hasta aqui codigo javier ********************


				// Obtener origen/destino de las cuentas
				currencyIdOrigin = bmoBkacOrigin.getCurrencyId().toInteger();
				parityOrigin = bmoBkacOrigin.getBmoCurrency().getParity().toDouble();
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
						currencyIdDestiny = bmoRequisition.getCurrencyId().toInteger();
						parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
					}
				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
					currencyIdDestiny = bmoBkacDestiny.getCurrencyId().toInteger();
					parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
				}

				// Obtener paridad de la caja de texto
				double parity = Double.parseDouble(currencyParity.getText());

				// Si mi Cuenta de banco es en moneda sistema, cambiar paridades
				if (parityOrigin == 1 && currencyIdOrigin == 1) {						
					parityDestiny = parity;
					parity = parityOrigin;
				}

				amountConvertedTextBox.setText("" + numberFormat.format(currencyExchange(
						Double.parseDouble(withdrawTextBox.getText()), 
						currencyIdOrigin, 
						parity, 
						currencyIdDestiny, 
						parityDestiny)));

				//					showSystemMessage(
				//							" monto: " +withdrawTextBox.getText()
				//							+" |mOrigen: " +currencyIdOrigin
				//							+" |pOrigen: " +parity
				//							+" |mDest: " +currencyIdDestiny
				//							+" |pDest: " +parityDestiny
				//							+" | montoConFin: "+ Double.parseDouble(amountConvertedTextBox.getText()));
				// ******************** codigo de jv ********************
				//				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {

				//					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				//						BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
				//						BmoBankAccount bmoBkacOrign = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//						double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
				//						double parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
				//						double parity = Double.parseDouble(currencyParity.getText());
				//						double amount = Double.parseDouble(amountConvertedTextBox.getText());
				//
				//						if (parityOrign > parityDestiny) {
				//							amount = amount / parity;						
				//						} else {
				//							amount = amount * parity;
				//						}
				//
				//						withdrawTextBox.setText("" + numberFormat.format(amount));
				//					}
				//
				//				}
				// ******************** hasta aqui codigo de jv ********************
			} else if (event.getSource() == withdrawTextBox) {
				// ******************** codigo de jv ********************
				//if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				//					BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//					BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();
				//					BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();


				//					double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
				//					double parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
				//
				//					if (bmoBkacOrign.getCurrencyId().toInteger() != bmoBkacDestiny.getCurrencyId().toInteger()) {
				//
				//						double parity = Double.parseDouble(currencyParity.getText());
				//						double amount = Double.parseDouble(withdrawTextBox.getText());
				//
				//
				//						if (parityOrign > parityDestiny) {
				//							amount = amount * parity;
				//							amountConvertedTextBox.setText("" + numberFormat.format(amount));
				//						} else {					
				//							amount = amount / parity;
				//							amountConvertedTextBox.setText("" + numberFormat.format(amount));
				//						}
				//
				//						try {
				//							bmoBankMovement.getAmountConverted().setValue(amountConvertedTextBox.getText());
				//						} catch (BmException e) {
				//							showSystemMessage(this.getClass().getName() + 
				//									"-formTextChange() ERROR: No se puedo asignar el Monto(Aplicar)" + e.toString());
				//						}
				//
				//						formFlexTable.showField(bmoBankMovement.getAmountConverted());
				//					} else {
				//						formFlexTable.showField(bmoBankMovement.getDeposit());
				//					}
				// ******************** hasta aqui codigo javier ********************


				// Obtener origen/destino de las cuentas
				currencyIdOrigin = bmoBkacOrigin.getCurrencyId().toInteger();
				parityOrigin = bmoBkacOrigin.getBmoCurrency().getParity().toDouble();
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
						currencyIdDestiny = bmoRequisition.getCurrencyId().toInteger();
						parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
					}
				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
					currencyIdDestiny = bmoBkacDestiny.getCurrencyId().toInteger();
					parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
				}

				// Obtener paridad de la caja de texto
				double parity = Double.parseDouble(currencyParity.getText());

				// Si mi Cuenta de banco es en moneda sistema, cambiar paridades
				if (parityOrigin == 1 && currencyIdOrigin == 1) {						
					parityDestiny = parity;
					parity = parityOrigin;
				}

				amountConvertedTextBox.setText("" + numberFormat.format(currencyExchange(
						Double.parseDouble(withdrawTextBox.getText()), 
						currencyIdOrigin, 
						parity, 
						currencyIdDestiny, 
						parityDestiny)));

				//					showSystemMessage(
				//							" monto: " +withdrawTextBox.getText()
				//							+" |mOrigen: " +currencyIdOrigin
				//							+" |pOrigen: " +parity
				//							+" |mDest: " +currencyIdDestiny
				//							+" |pDest: " +parityDestiny
				//							+" | montoConFin: "+ Double.parseDouble(amountConvertedTextBox.getText()));

				// ******************** codigo de jv *******************
				//				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				//					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				//						BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
				//						BmoBankAccount bmoBkacOrign = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//						double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
				//						double parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
				//						double parity = Double.parseDouble(currencyParity.getText());
				//						double amount = Double.parseDouble(withdrawTextBox.getText());					
				//
				//						if (parityOrign > parityDestiny) {
				//							amount = amount * parity;
				//						} else {					
				//							amount = amount / parity;
				//						}
				//
				//						amountConvertedTextBox.setText("" + numberFormat.format(amount));
				//
				//						try {
				//							bmoBankMovement.getAmountConverted().setValue(amountConvertedTextBox.getText());
				//						} catch (BmException e) {
				//							showSystemMessage(this.getClass().getName() + 
				//									"-formTextChange() ERROR: No se puedo asignar el Monto(Aplicar)" + e.toString());
				//						}
				//					}
				//				}	
				// ******************** hasta aqui codigo de jv ********************
			} else if (event.getSource() == amountConvertedTextBox) {

				// ******************** codigo de jv ********************
				//				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				//					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				//						BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
				//						BmoBankAccount bmoBkacOrign = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//						double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
				//						double parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
				//						double parity = Double.parseDouble(currencyParity.getText());
				//
				//						double amount = Double.parseDouble(amountConvertedTextBox.getText());
				//
				//						if (parityOrign > parityDestiny) {
				//							amount = amount * parity;
				//						} else {					
				//							amount = amount / parity;
				//						}			
				//
				//						withdrawTextBox.setText("" + numberFormat.format(amount));
				//					}
				//				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				//formFlexTable.showField(bmoBankMovement.getAmountConverted());
				//					BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
				//					BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();
				//					BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();


				//					double parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();				
				//					double parity = Double.parseDouble(currencyParity.getText());
				//
				//					double amount = Double.parseDouble(amountConvertedTextBox.getText());
				//					double factor = parityDestiny / parity;
				//					amount = amount * factor;			
				//
				//					withdrawTextBox.setText("" + numberFormat.format(amount));
				//
				//
				//					try {
				//						bmoBankMovement.getAmountConverted().setValue(amountConvertedTextBox.getText());
				//					} catch (BmException e) {
				//						showSystemMessage(this.getClass().getName() + 
				//								"-formTextChange() ERROR: No se puedo asignar el Monto(Aplicar)" + e.toString());
				//					}
				// ******************** hasta aqui codigo javier ********************


				// Obtener origen/destino de las cuentas
				currencyIdOrigin = bmoBkacOrigin.getCurrencyId().toInteger();
				parityOrigin = bmoBkacOrigin.getBmoCurrency().getParity().toDouble();
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
						currencyIdDestiny = bmoRequisition.getCurrencyId().toInteger();
						parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 

					}
				} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
					currencyIdDestiny = bmoBkacDestiny.getCurrencyId().toInteger();
					parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
				}

				// Obtener paridad de la caja de texto
				double parity = Double.parseDouble(currencyParity.getText());

				// Si mi Cuenta de banco es en moneda sistema, cambiar paridades
				if (parityOrigin == 1 && currencyIdOrigin == 1) {						
					parityDestiny = parity;
					parity = parityOrigin;
				}
				// Se intercambia moneda/paridad ORIGEN por DESTINO, es el monto a convertir a la moneda de la cuenta de banco
				withdrawTextBox.setText("" + numberFormat.format(currencyExchange(
						Double.parseDouble(amountConvertedTextBox.getText()), 
						currencyIdDestiny, 
						parityDestiny, 
						currencyIdOrigin, 
						parity)));

				//					showSystemMessage(
				//							" monto: " +amountConvertedTextBox.getText()
				//							+" |mOrigen: " +currencyIdOrigin
				//							+" |pOrigen: " +parity
				//							+" |mDest: " +currencyIdDestiny
				//							+" |pDest: " +parityDestiny
				//							+" | montoConFin: "+ Double.parseDouble(withdrawTextBox.getText()));
				//					
				//				}	
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {

			BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
			if (bmoBankMovType == null) 
				bmoBankMovType = bmoBankMovement.getBmoBankMovType();

			if (event.getSource() == bankMovTypeListBox) {
				if (newRecord) {
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableAutoFill().toInteger() > 0) {
						referenceTextBox.setText(bmoBankMovType.getName().toString());
						descriptionTextArea.setText(bmoBankMovType.getName().toString());
					}
					cleanFields();
				}
				
				// Filtrar clientes por empresa
				if (multiCompany && Integer.parseInt(bankAccountListBox.getSelectedId()) > 0) {
					BmoBankAccount bmoBankAccount = ((BmoBankAccount)bankAccountListBox.getSelectedBmObject());
					populateCustomers(bmoBankAccount.getCompanyId().toInteger(), multiCompany);
				}
				
				statusEffect();
			} else if (event.getSource() == statusListBox) {	
				update("Desea cambiar el Estatus del Movimiento Bancario?");
				statusEffect();
			} 
			else if (event.getSource() == bankAccountListBox) {
				BmoBankAccount bmoBankAccount = new BmoBankAccount();
				if (Integer.parseInt(bankAccountListBox.getSelectedId()) > 0)
					bmoBankAccount = ((BmoBankAccount)bankAccountListBox.getSelectedBmObject());
				
				// Limpiar saldo de la cta. de banco
				if (Integer.parseInt(bankAccountListBox.getSelectedId()) > 0) 
					balanceBankAccountLabel.setText("");

				if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_LOANDISPOSAL) 
					populateLoans(((BmoBankAccount)bankAccountListBox.getSelectedBmObject()).getCompanyId().toInteger());
				
				populateCustomers(bmoBankAccount.getCompanyId().toInteger(), multiCompany);

				statusEffect();
				// Si esta seleccionada una oc comparar monedas para aplicar monto correspondiente
				//if (Integer.parseInt(requisitionListBox.getSelectedId()) > 0) populateWithdraw();
			} else if (event.getSource() == bankAccDestListBox) {
				statusEffect();
			} else if (event.getSource() == loanListBox) {
			} else if (event.getSource() == paymentTypeListBox) {
			} else {
				if (newRecord)
					cleanFields();
			}
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de requisiones
			if (uiSuggestBox == supplierSuggestBox) {
				BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();			
				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) { 
					//requisitionListBox.setEnabled(true);
					//populateRequisitions(supplierSuggestBox.getSelectedId());
				} 
				statusEffect();
			} else if (uiSuggestBox == bankMovementSuggestBox) {
				if (bankMovementSuggestBox.getSelectedBmObject() != null)
					changeBankMovement((BmoBankMovement)bankMovementSuggestBox.getSelectedBmObject());
			}
		}
		
		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			// Obtener el Tipo de cambio cuando cambian la fecha
			if (event.getSource() == dueDateBox) {
				// Si esta el tipo de cuenta
				BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
				if (bmoBankMovType == null) 
					bmoBankMovType = bmoBankMovement.getBmoBankMovType();
			
				if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_TRANSFER) {
					if (newRecord) {
						//Mostrar el campo de paridad de acuerdo a moneda de la cuenta de banco	
						BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
						BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();

						if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
							if (bmoBkacOrigin.getCurrencyId().toInteger() != bmoBkacDestiny.getCurrencyId().toInteger()) {
								if (bmoBkacDestiny.getCurrencyId().toInteger() == ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger()
										&& (bmoBkacDestiny.getCurrencyId().toInteger() != bmoBkacOrigin.getCurrencyId().toInteger())) {
									getParityFromCurrency("" + bmoBkacOrigin.getCurrencyId().toInteger());
								} else {
									getParityFromCurrency("" + bmoBkacDestiny.getCurrencyId().toInteger());
								}
							}
						}
					}
				}
			}
		}

		// Cambia el movimiento de banco
		private void changeBankMovement(BmoBankMovement newBmoBankMovement) {
			if (newBmoBankMovement.getId() != bmoBankMovement.getId()) {
				formDialogBox.hide();
				edit(newBmoBankMovement);
			}
		}

		//		public void populateWithdraw() {
		//			//Validar que la moneda de la OC y La Cta Banco
		//			BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
		//			BmoBankAccount bmoBankAccount = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
		//			formFlexTable.showField(bmoBankMovement.getWithdraw());
		//
		//			if (bmoBankAccount.getCurrencyId().toInteger() !=  bmoRequisition.getCurrencyId().toInteger()) {
		//				formFlexTable.showField(bmoBankMovement.getCurrencyParity());
		//				currencyParity.setEnabled(true);
		//				formFlexTable.showField(bmoBankMovement.getAmountConverted());
		//				amountConvertedTextBox.setEnabled(true);
		//				
		//				withdrawTextBox.setText("0");
		//				currencyParity.setText("" + bmoRequisition.getCurrencyParity().toDouble());
		//				amountConvertedTextBox.setText("" + bmoRequisition.getBalance().toDouble());
		//				
		//				// ******************** codigo de jv ********************
		////				currencyParity.setText("" + bmoRequisition.getCurrencyParity().toDouble());
		////				currencyParity.setEnabled(true);
		////				amountConvertedTextBox.setText("" + withdrawTextBox.getText());
		////				try {
		////					bmoBankMovement.getCurrencyParity().setValue(bmoRequisition.getCurrencyParity().toDouble());
		////					bmoBankMovement.getAmountConverted().setValue(withdrawTextBox.getText());
		////				} catch (BmException e) {
		////					showErrorMessage("Error en la paridad de la OC");
		////				}
		////
		////				formFlexTable.showField(bmoBankMovement.getCurrencyParity());
		////				formFlexTable.showField(bmoBankMovement.getAmountConverted());
		////
		////				//Convertir el saldo de la OC a la paridad de Cta Banco
		////				BmoBankAccount bmoBkacOrign = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
		////				double parityOrign = bmoBkacOrign.getBmoCurrency().getParity().toDouble();
		////				double parityDestiny = bmoRequisition.getBmoCurrency().getParity().toDouble();
		////				double parity = bmoRequisition.getCurrencyParity().toDouble();
		////				double amount = Double.parseDouble(withdrawTextBox.getText());
		////
		////
		////				if (parityOrign > parityDestiny) {			
		////					amount = amount  / parity;				
		////				} else {
		////					amount = amount  * parity;
		////				}
		////
		////				try {
		////					bmoBankMovement.getWithdraw().setValue(numberFormat.format(amount));
		////				} catch (BmException e) {
		////					showSystemMessage(this.getClass().getName() + 
		////							"-populateWithdraw() ERROR: No se puedo asignar el formato en el Monto" + e.toString());
		////				}
		////				formFlexTable.showField(bmoBankMovement.getWithdraw());
		//				// ******************** hasta aqui codigo javier ********************
		//				
		//				
		//				// Validar que esto ocurra cuando es el MB es Tipo Anticipo Proveedor
		//				BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
		//
		//				int currencyIdOrigin = 0, currencyIdDestiny = 0;
		//				double parityOrigin = 0, parityDestiny = 0;
		//				
		//				if (bmoBankMovType == null) 
		//					bmoBankMovType = bmoBankMovement.getBmoBankMovType();
		//				
		//				// Cuenta de banco origen/destinop
		//				BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
		//				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
		//					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
		//						bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
		//					}
		//				}
		//				// Obtener origen/destino de las cuentas
		//				currencyIdOrigin = bmoBkacOrigin.getCurrencyId().toInteger();
		//				parityOrigin = bmoBkacOrigin.getBmoCurrency().getParity().toDouble();
		//				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
		//					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
		//						currencyIdDestiny = bmoRequisition.getCurrencyId().toInteger();
		//						parityDestiny =  bmoRequisition.getBmoCurrency().getParity().toDouble(); 
		//					}
		//				}
		//				
		//				// Obtener paridad de la caja de texto
		//				double parity = Double.parseDouble(currencyParity.getText());
		//				
		//				// Si mi Cuenta de banco es en moneda sistema, cambiar paridades
		//				if (parityOrigin == 1 && currencyIdOrigin == 1) {						
		//					parityDestiny = parity;
		//					parity = parityOrigin;
		//				}
		//				// Se intercambia moneda/paridad ORIGEN por DESTINO, es el monto a convertir a la moneda de la cuenta de banco
		//				withdrawTextBox.setText("" + numberFormat.format(currencyExchange(
		//						Double.parseDouble(amountConvertedTextBox.getText()), 
		//						currencyIdDestiny, 
		//						parityDestiny, 
		//						currencyIdOrigin, 
		//						parity)));
		//				
		////				showSystemMessage(
		////						" monto: " +amountConvertedTextBox.getText()
		////						+" |mOrigen: " +currencyIdOrigin
		////						+" |pOrigen: " +parity
		////						+" |mDest: " +currencyIdDestiny
		////						+" |pDest: " +parityDestiny
		////						+" | montoConFin: "+ Double.parseDouble(withdrawTextBox.getText()));
		//			} else  {
		//				withdrawTextBox.setText("" + bmoRequisition.getBalance().toDouble());
		//				// Limpiar paridad/aplicar
		//				currencyParity.setText("");
		//				amountConvertedTextBox.setText("");
		//			}
		//		}
		
		public void getParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId, 0);
		}

		public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
			if (parityFromCurrencyRpcAttempt < 5) {
				setCurrencyId(currencyId);
				setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);

				BmoCurrency bmoCurrency = new BmoCurrency();
				String startDate = dueDateBox.getTextBox().getText();

				if (dueDateBox.getTextBox().getText().equals("")) {
					startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
				}
				String actionValues = currencyId + "|" + startDate;

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {

						stopLoading();
						if (getParityFromCurrencyRpcAttempt() < 5) 
							getParityFromCurrency(getCurrencyId(), getParityFromCurrencyRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {				
						stopLoading();	
						setParityFromCurrencyRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Tipo de Cambio.");
						else {
							currencyParity.setValue(result.getMsg());
							// Calcular montos con paridades
							postChangeDueDateCalculateParity();
						}
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
		
		// Es el mismo codigo de formTextChange para el widget de currencyParity
		public void postChangeDueDateCalculateParity() {
			NumberFormat numberFormat = NumberFormat.getFormat("#######.##");
			// Si esta el tipo de cuenta
			BmoBankMovType bmoBankMovType = (BmoBankMovType)bankMovTypeListBox.getSelectedBmObject();
			if (bmoBankMovType == null) 
				bmoBankMovType = bmoBankMovement.getBmoBankMovType();
		
			if (bmoBankMovType.getCategory().toChar() == BmoBankMovType.CATEGORY_TRANSFER) {
				if (newRecord) {
					//Mostrar el campo de paridad de acuerdo a moneda de la cuenta de banco	
					BmoBankAccount bmoBkacOrigin = (BmoBankAccount)bankAccountListBox.getSelectedBmObject();
					BmoBankAccount bmoBkacDestiny = (BmoBankAccount)bankAccDestListBox.getSelectedBmObject();

					if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
						if (bmoBkacOrigin.getCurrencyId().toInteger() != bmoBkacDestiny.getCurrencyId().toInteger()) {

							// Obtener origen/destino de las cuentas
							int currencyIdDestiny = 0;
							double parityDestiny = 0;
							int currencyIdOrigin = bmoBkacOrigin.getCurrencyId().toInteger();
							double parityOrigin = bmoBkacOrigin.getBmoCurrency().getParity().toDouble();
							if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
								currencyIdDestiny = bmoBkacDestiny.getCurrencyId().toInteger();
								parityDestiny = bmoBkacDestiny.getBmoCurrency().getParity().toDouble();
							}

							// Obtener paridad de la caja de texto
							double parity = Double.parseDouble(currencyParity.getText());
							// Si mi Cuenta de banco es en moneda sistema, cambiar paridades
							if (parityOrigin == 1 && currencyIdOrigin == 1) {
								parityDestiny = parity;
								parity = parityOrigin;
							}

							amountConvertedTextBox.setText("" + numberFormat.format(currencyExchange(
									Double.parseDouble(withdrawTextBox.getText()), 
									currencyIdOrigin, 
									parity, 
									currencyIdDestiny, 
									parityDestiny)));
						}
					}
				}
			}
		}

		// Obtiene el NoCheck, primer intento
		public void getNoCheckNow() {
			getNoCheckNow(0);
		}

		// Obtiene el NoCheck
		public void getNoCheckNow(int noCheckNowRpcAttempt) {
			if (noCheckNowRpcAttempt < 5) {
				setNoCheckNowRpcAttempt(noCheckNowRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						if (getNoCheckNowRpcAttempt() < 5)
							getNoCheckNow(getNoCheckNowRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getNoCheckNow() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						setNoCheckNowRpcAttempt(0);
						noCheckTextBox.setText(result.getMsg());				
					}
				};

				try {	
					getUiParams().getBmObjectServiceAsync().action(bmoBankMovement.getPmClass(), bmoBankMovement, BmoBankMovement.ACTION_CHECKNO, bankAccountListBox.getSelectedId(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getNoCheckNow() ERROR: " + e.toString());
				}
			}
		}

		private void populateLoans(int companyId) {
			// Filtros de ordenes de compra
			// Se quitan elementos y filtros
			loanListBox.clear();
			loanListBox.clearFilters();
			setLoansListBoxFilters(companyId);
			loanListBox.populate(bmoBankMovement.getLoanId());
		}

		private void setLoansListBoxFilters(int companyId) {
			BmoLoan bmoLoan = new BmoLoan();

			if (companyId > 0) {
				BmFilter bmFilterByComp = new BmFilter();
				bmFilterByComp.setValueFilter(bmoLoan.getKind(), bmoLoan.getCompanyId(), companyId);			
				loanListBox.addBmFilter(bmFilterByComp);
			} else {
				BmFilter bmFilterByComp = new BmFilter();
				bmFilterByComp.setValueFilter(bmoLoan.getKind(), bmoLoan.getIdField(), -1);			
				loanListBox.addBmFilter(bmFilterByComp);
			}
		}

		//		private void populateRequisitions(int supplierId) {		
		//			// Filtros de ordenes de compra
		//			// Se quitan elementos y filtros
		//			requisitionListBox.clear();
		//			requisitionListBox.clearFilters();
		//			setRequisitionsListBoxFilters(supplierId);
		//			requisitionListBox.populate(bmoBankMovement.getRequisitionId());
		//		}

		//		private void setRequisitionsListBoxFilters(int supplierId) {
		//			BmoRequisition bmoRequisition = new BmoRequisition();
		//
		//			if (newRecord && supplierId > 0) {
		//
		//				BmFilter bmFilterReqAuthorized = new BmFilter();					
		//				BmFilter bmFilterPaymentStatus = new BmFilter();
		//				BmFilter bmFilterReqBySupplier = new BmFilter();
		//
		//				bmFilterReqAuthorized.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus(),
		//						"" + BmoRequisition.STATUS_AUTHORIZED);			
		//
		//				bmFilterPaymentStatus.setValueOperatorFilter(bmoRequisition.getKind(), bmoRequisition.getPaymentStatus(), 
		//						BmFilter.NOTEQUALS, "" + BmoRequisition.PAYMENTSTATUS_TOTAL);			
		//
		//				bmFilterReqBySupplier.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getSupplierId(), supplierId);
		//
		//				requisitionListBox.addBmFilter(bmFilterReqBySupplier);			
		//				requisitionListBox.addBmFilter(bmFilterReqAuthorized);			
		//				requisitionListBox.addBmFilter(bmFilterPaymentStatus);
		//
		//			} else {
		//				BmFilter bmFilterReqById = new BmFilter();
		//				bmFilterReqById.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getIdFieldName(),  bmoBankMovement.getRequisitionId().toInteger());
		//				requisitionListBox.addBmFilter(bmFilterReqById);
		//			}
		//		}

		private void populateBankAccountDestiny() {
			bankAccDestListBox.clear();
			bankAccDestListBox.clearFilters();
			setBankAccountDestinyListBoxFilters();
			bankAccDestListBox.populate(bmoBankMovement.getBankAccoTransId());
		}
		private void setBankAccountDestinyListBoxFilters() {
			bankAccDestListBox.setDiscloseSetting(false);
		}
		
		// Filtrar clientes por empresa
		private void populateCustomers(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				customerSuggestBox.clear();
				setCustomerListBoxFilters(companyId, multiCompany);
			}
		}

		// Filtrar clientes por empresa 
		private void setCustomerListBoxFilters(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
				BmFilter filterCustomer = new BmFilter();
				BmoCustomer bmocustomer = new BmoCustomer();
				filterCustomer.setInFilter(bmoCustomerCompany.getKind(), 
						bmocustomer.getIdFieldName(),
						bmoCustomerCompany.getCustomerId().getName(),
						bmoCustomerCompany.getCompanyId().getName(),
						"" + companyId);	
				customerSuggestBox.addFilter(filterCustomer);
			}
		}

		// Obtener el saldo de la orden de compra
		//		private void getRequisitionBalance() {
		//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {			
		//				@Override
		//				public void onFailure(Throwable caught) {
		//					stopLoading();
		//					showErrorMessage(this.getClass().getName() + "-getRequisitionBalance() ERROR: " + caught.toString());
		//				}
		//
		//				@Override
		//				public void onSuccess(BmUpdateResult result) {
		//					stopLoading();				
		//					withdrawTextBox.setValue(result.getMsg());
		//					populateWithdraw();
		//				}
		//			};
		//
		//			try {	
		//				if (!isLoading()) {				 
		//					startLoading();				
		//					getUiParams().getBmObjectServiceAsync().action(bmoBankMovement.getPmClass(), bmoBankMovement, BmoBankMovement.ACTION_BALANCEREQI, requisitionListBox.getSelectedId(), callback);
		//				}
		//			} catch (SFException e) {
		//				stopLoading();
		//				showErrorMessage(this.getClass().getName() + "-getRequisitionBalance() ERROR: " + e.toString());
		//			}
		//		}	

		// Actualizar datos, primer intento
		private void updateAmount(int bankMovementIdRPC) {
			updateAmount(bankMovementIdRPC, 0);
		}

		// Actualizar datos
		private void updateAmount(int bankMovementIdRPC, int updateAmountRpcAttempt) {
			if (updateAmountRpcAttempt < 5) {
				setBankMovementIdRPC(bankMovementIdRPC);
				setUpdateAmountRpcAttempt(updateAmountRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUpdateAmountRpcAttempt() < 5)
							updateAmount(getBankMovementIdRPC(), getUpdateAmountRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUpdateAmountRpcAttempt(0);
						//getBalanceBankAccount(bmoBankMovement.getBankAccountId().toInteger());
						balanceBankAccountLabel.setText("");
						setAmount((BmoBankMovement)result);
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoBankMovement.getPmClass(), bankMovementIdRPC, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
				}
			}
		}

		// Actualiza usuario de cambio de estatus
		private void showUserDataChangeStatus() {
			formFlexTable.hideField(bmoBankMovement.getAuthorizeUserId());
			formFlexTable.hideField(bmoBankMovement.getAuthorizeDate());
			formFlexTable.hideField(bmoBankMovement.getReconciledUserId());
			formFlexTable.hideField(bmoBankMovement.getReconciledDate());
			formFlexTable.hideField(bmoBankMovement.getCancelledUserId());
			formFlexTable.hideField(bmoBankMovement.getCancelledDate());

			if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
				if (bmoBankMovement.getReconciledUserId().toInteger() > 0) {
					formFlexTable.showField(bmoBankMovement.getReconciledUserId());
					formFlexTable.showField(bmoBankMovement.getReconciledDate());
					getUserDataChangeStatus(bmoBankMovement.getReconciledUserId().toInteger());
				}
			} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) {
				if (bmoBankMovement.getAuthorizeUserId().toInteger() > 0) {
					formFlexTable.showField(bmoBankMovement.getAuthorizeUserId());
					formFlexTable.showField(bmoBankMovement.getAuthorizeDate());
					getUserDataChangeStatus(bmoBankMovement.getAuthorizeUserId().toInteger());
				}
			} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
				if (bmoBankMovement.getCancelledUserId() .toInteger() > 0) {
					formFlexTable.showField(bmoBankMovement.getCancelledUserId());
					formFlexTable.showField(bmoBankMovement.getCancelledDate());
					getUserDataChangeStatus(bmoBankMovement.getCancelledUserId().toInteger());
				}
			}
		}

		// Obtiene el usuario a desplegar de cambio estatus
		private void getUserDataChangeStatus(int idUserChangeStatus) {
			getUserDataChangeStatus(idUserChangeStatus,0);
		}

		// Obtiene el usuario a desplegar de cambio estatus
		private void getUserDataChangeStatus(int idUserChangeStatus, int userDataChangeStatusRpcAttempt) {
			BmoUser bmoUser = new BmoUser();

			if (userDataChangeStatusRpcAttempt < 5) {
				setIdUserChangeStatus(idUserChangeStatus);
				setUserDataChangeStatusRpcAttempt(userDataChangeStatusRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUserDataChangeStatusRpcAttempt() < 5)
							getUserDataChangeStatus(getIdUserChangeStatus(), getUserDataChangeStatusRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getUserDataChangeStatus() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUserDataChangeStatusRpcAttempt(0);
						setBmObject((BmObject)result);
						BmoUser bmoUser = (BmoUser)getBmObject();
						if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) {
							authorizedByLabel.setText(bmoUser.getCode().toString());
							authorizedDateLabel.setText(bmoBankMovement.getAuthorizeDate().toString());
						} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
							reconciledByLabel.setText(bmoUser.getCode().toString());
							reconciledDateLabel.setText(bmoBankMovement.getAuthorizeDate().toString());
						} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
							formFlexTable.addLabelField(16, 0, "Cancelado Por:", bmoUser.getCode().toString());
							formFlexTable.addLabelField(17, 0, bmoBankMovement.getCancelledDate());
						}
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoUser.getPmClass(), idUserChangeStatus, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getUserDataChangeStatus(): ERROR " + e.toString());
				}
			}
		}

		private void setBalanceBankAccountLabel(String balance) {
			NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
			balanceBankAccountLabel.setText(numberFormat.format(Double.parseDouble(balance)));
		}

		private void setAmount(BmoBankMovement bmoBankMovement) {
			NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
			withdrawTextBox.setText(numberFormat.format(bmoBankMovement.getWithdraw().toDouble()));
			depositTextBox.setText(numberFormat.format(bmoBankMovement.getDeposit().toDouble()));	
		}

		private void cleanFields() {
			noCheckTextBox.setValue("");
			withdrawTextBox.setValue("");
			depositTextBox.setValue("");
			currencyParity.setValue("");
			amountConvertedTextBox.setValue("");

			noCheckTextBox.setText("");
			withdrawTextBox.setText("");
			depositTextBox.setText("");
			currencyParity.setText("");
			amountConvertedTextBox.setText("");

			supplierSuggestBox.clear();
			supplierSuggestBox.setSelectedId("-1");
			customerSuggestBox.clear();
			budgetItemSuggestBox.clear();

			populateLoans(((BmoBankAccount)bankAccountListBox.getSelectedBmObject()).getCompanyId().toInteger());
			//populateRequisitions(supplierSuggestBox.getSelectedId());
			populateBankAccountDestiny();
		}

		public void reset() {
			updateAmount(id);
			uiBankMovConceptGrid.show();		
		}

		protected class BankMovementUpdater {
			public void changeBankMovement() {
				stopLoading();
				reset();
			}		
		}

		// Variables para llamadas RPC
		public int getUserDataChangeStatusRpcAttempt() {
			return userDataChangeStatusRpcAttempt;
		}

		public void setUserDataChangeStatusRpcAttempt(int userDataChangeStatusRpcAttempt) {
			this.userDataChangeStatusRpcAttempt = userDataChangeStatusRpcAttempt;
		}

		public int getIdUserChangeStatus() {
			return idUserChangeStatus;
		}

		public void setIdUserChangeStatus(int idUserChangeStatus) {
			this.idUserChangeStatus = idUserChangeStatus;
		}

		public int getBankAccountIdRPC() {
			return bankAccountIdRPC;
		}

		public void setBankAccountIdRPC(int bankAccountIdRPC) {
			this.bankAccountIdRPC = bankAccountIdRPC;
		}

		public int getBalanceBankAccountRpcAttempt() {
			return balanceBankAccountRpcAttempt;
		}

		public void setBalanceBankAccountRpcAttempt(int balanceBankAccountRpcAttempt) {
			this.balanceBankAccountRpcAttempt = balanceBankAccountRpcAttempt;
		}

		public int getNoCheckNowRpcAttempt() {
			return noCheckNowRpcAttempt;
		}

		public void setNoCheckNowRpcAttempt(int noCheckNowRpcAttempt) {
			this.noCheckNowRpcAttempt = noCheckNowRpcAttempt;
		}

		public int getUpdateAmountRpcAttempt() {
			return updateAmountRpcAttempt;
		}

		public void setUpdateAmountRpcAttempt(int updateAmountRpcAttempt) {
			this.updateAmountRpcAttempt = updateAmountRpcAttempt;
		}

		public int getBankMovementIdRPC() {
			return bankMovementIdRPC;
		}

		public void setBankMovementIdRPC(int bankMovementIdRPC) {
			this.bankMovementIdRPC = bankMovementIdRPC;
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
	public void populateStatusListBox() {


		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusAuthorized().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_AUTHORIZED, "Autorizado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusReconciled().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_RECONCILED, "Conciliado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_reconciled.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusCancelled().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_CANCELLED, "Cancelado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}

		bmoBankMovement.getStatus().setOptionList(status);

	}

}
