/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;


import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoFormat;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.sf.BmoUser;


public class UiFlexConfigForm extends UiForm {
	TextBox taxTextBox = new TextBox();
	TextArea sysMessageTextArea = new TextArea();
	TextArea quoteNotesTextArea = new TextArea();
	TextBox quoteDownPaymentTextBox = new TextBox();
	TextBox extraHourTextBox = new TextBox();
	TextBox discountLimitTextBox = new TextBox();

	TextBox ytVideoIdTextBox = new TextBox();
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox salesProfileSuggestBox = new UiSuggestBox(new BmoProfile());
	UiSuggestBox collectProfileSuggestBox = new UiSuggestBox(new BmoProfile());
	UiListBox defaultOrderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());

	CheckBox enableQuotesCheckBox = new CheckBox();
	CheckBox enableOpportunityEffectCheckBox = new CheckBox();

	CheckBox enableNegativeBankBalance = new CheckBox();
	CheckBox enableNegativeBudget = new CheckBox();

	UiListBox orderWarehouseListBox = new UiListBox(getUiParams(), new BmoWarehouse());
	UiListBox orderRaccountTypeListBox = new UiListBox(getUiParams(), new BmoRaccountType());
	CheckBox enableOrderLockCheckBox = new CheckBox();
	UiListBox defaultWarehouseListBox = new UiListBox(getUiParams(), new BmoWarehouse());
	UiListBox defaultWhSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox systemCurrencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox defaultReqPayTypeListBox = new UiListBox(getUiParams(), new BmoReqPayType());

	CheckBox enableWorkBudgetItemCheckBox = new CheckBox();
	CheckBox enableEnsureProcessCheckBox = new CheckBox();
	CheckBox customerProtectionCheckBox = new CheckBox();
	CheckBox mobilOrMailCustCheckBox = new CheckBox();

	UiSuggestBox commisionBudgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiSuggestBox depositBudgetItemTypeSuggestBox = new UiSuggestBox(new BmoBudgetItemType());
	UiListBox defaultTypeCustomerListBox = new UiListBox(getUiParams());

	CheckBox enableAutoFillCheckBox = new CheckBox();	
	TextBox orderRenewDaysTextBox = new TextBox();

	UiSuggestBox defaultBankAccountSuggestBox = new UiSuggestBox(new BmoBankAccount()); 

	UiListBox defaultFormatOpportunityListBox = new UiListBox(getUiParams(), new BmoFormat());
	UiListBox defaultFormatOrderListBox = new UiListBox(getUiParams(), new BmoFormat());

	//Cobro de CxC Automatico
	CheckBox enableAutomaticPaymentCxCCheckBox = new CheckBox();

	//Envio de Correo al conciliar MB
	CheckBox enableSendEmailReconciledCheckBox = new CheckBox();

	CheckBox sendEmailCustomerBirthdayCheckBox = new CheckBox();

	//Estatus de autorizado en bancos
	CheckBox showStatusAuthorizeBkmv = new CheckBox();

	//Pedir autorizaición de una OC
	CheckBox requestAuthReqiCheckBox = new CheckBox();
	TextBox requestAuthReqiAmountTextBox = new TextBox();

	//Activar Envio de Pedido
	CheckBox enableSendDeliveryCheckBox = new CheckBox();

	// Activar envio de pedido en caso de que se autorize el MB
	CheckBox sendEmailAuthorizedMBCheckBox = new CheckBox();
	
	//Activar renovación
	CheckBox enableRenewProduct = new CheckBox();

	//Status de MB
	CheckBox statusRevisionCheckBox = new CheckBox();
	CheckBox statusAuthorizedCheckBox = new CheckBox();
	CheckBox statusReconciledCheckBox = new CheckBox();
	CheckBox statusCancelledCheckBox = new CheckBox();
	CheckBox statusDefaultCompanyCheckBox = new CheckBox();
	//Status de Opportunidades
	CheckBox oppoStatusRevisionCheckBox = new CheckBox();
	CheckBox oppoStatusGanadaCheckBox = new CheckBox();
	CheckBox oppoStatusPerdidanCheckBox = new CheckBox();
	CheckBox oppoStatusExpiradanCheckBox = new CheckBox();
	CheckBox oppoStatusHoldCheckBox = new CheckBox();
	//Status de pedido
	CheckBox ordeStatusAuthorized = new CheckBox();
	CheckBox ordeStatusFinished = new CheckBox();
	CheckBox ordeStatusCancelled = new CheckBox();
	CheckBox ordeStatusRevision = new CheckBox();

	// envio de correo al cliente, recordando el dia de programación de la  CxC
	CheckBox remindRaccountCheckBox = new CheckBox();
	TextBox daysBeforeRemaindRaccount = new TextBox();
	TextBox daysBeforeRemaindRaccountTwo = new TextBox();

	CheckBox enableEmailReminderCommentsCheckBox = new CheckBox();
	CheckBox showOrderInCustomerCheckBox = new CheckBox();
	CheckBox showOwmCustomerCheckBox = new CheckBox();
	TextBox emailFailCronTextBox = new TextBox();
	CheckBox duplicateAddressCheckBox = new CheckBox();
	TextBox duplicateAddressNumberTextBox = new TextBox();
	CheckBox multiCompanyCheckBox = new CheckBox();
	CheckBox creditByLocationCheckBox = new CheckBox();

	protected ValueChangeHandler<Boolean> booleanChangeHandler;

	BmoFlexConfig bmoFlexConfig;

	BmoOpportunity bmoOpportunity;
	BmoOrder bmoOrder = new BmoOrder();
	BmoProgram bmoProgramOrder = new BmoProgram();
	BmoProgram bmoProgramOpportunity = new BmoProgram();

	public UiFlexConfigForm(UiParams uiParams) {
		super(uiParams, new BmoFlexConfig(), 0);
		setUiType(UiParams.SINGLE);
		initialize();
	}

	private void initialize() {
		// Filtrar tipos de cuentas x cobrar de tipo cargo
		BmoRaccountType bmoRaccountType = new BmoRaccountType();
		BmFilter filterRaccountsWithdraw = new BmFilter();
		filterRaccountsWithdraw.setValueFilter(bmoRaccountType.getKind(), bmoRaccountType.getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
		orderRaccountTypeListBox.addFilter(filterRaccountsWithdraw);

		// Filtrar secciones de almacen default, que sean de tipo normal (no de pedidos)
		BmoWhSection bmoWhSection = new BmoWhSection();
		BmFilter bmFilterWhSections = new BmFilter();
		bmFilterWhSections.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
		defaultWhSectionListBox.addFilter(bmFilterWhSections);

		// Buscar el modulo de pedido
		try {
			if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
				bmoOpportunity = new BmoOpportunity();
				bmoProgramOpportunity = (BmoProgram)getSFParams().getBmoProgram(bmoOpportunity.getProgramCode());
			}
			bmoProgramOrder = (BmoProgram)getSFParams().getBmoProgram(bmoOrder.getProgramCode());
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-Error al obtener el Módulo(): ERROR " + e.toString());
		}

		// Filtrar formatos de Oportunidades
		BmoFormat bmoFormatOppo = new BmoFormat();
		BmFilter filterDefaultFormatOppo = new BmFilter();
		filterDefaultFormatOppo.setValueFilter(bmoFormatOppo.getKind(), bmoFormatOppo.getProgramId(), bmoProgramOpportunity.getId());
		defaultFormatOpportunityListBox.addFilter(filterDefaultFormatOppo);

		// Filtrar formatos de Pedidos
		BmoFormat bmoFormatOrde = new BmoFormat();
		BmFilter filterDefaultFormatOrder = new BmFilter();
		filterDefaultFormatOrder.setValueFilter(bmoFormatOrde.getKind(), bmoFormatOrde.getProgramId(), bmoProgramOrder.getId());
		defaultFormatOrderListBox.addFilter(filterDefaultFormatOrder);
		
		// Añadir controlador de eventos
		booleanChangeHandler = new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				formBooleanChange(event);
			}
		};
		formFlexTable.setBooleanChangeHandler(booleanChangeHandler);
		
	}

	@Override
	public void populateFields() {
		bmoFlexConfig = (BmoFlexConfig)getBmObject();
		int i = 1; // Incrementar posicion
		
		formFlexTable.addField(i, 0, ytVideoIdTextBox, bmoFlexConfig.getYtVideoId());	i++;
		formFlexTable.addField(i, 0, sysMessageTextArea, bmoFlexConfig.getSysMessage());	i++;
		formFlexTable.addField(i, 0, multiCompanyCheckBox, bmoFlexConfig.getMultiCompany());	i++;

		formFlexTable.addSectionLabel(i, 0, "Comercial", 2);	i++; // posicion: 4
		formFlexTable.addField(i, 0, salesProfileSuggestBox, bmoFlexConfig.getSalesProfileId());	i++;
		formFlexTable.addField(i, 0, new UiCompanySalesmanLabelList(getUiParams(), id));	i++;
		formFlexTable.addField(i, 0, salesmanSuggestBox, bmoFlexConfig.getDefaultSalesMan());	i++;
		formFlexTable.addField(i, 0, enableQuotesCheckBox, bmoFlexConfig.getEnableQuotes());	i++;
		formFlexTable.addField(i, 0, quoteDownPaymentTextBox, bmoFlexConfig.getQuoteDownPayment());	i++;
		formFlexTable.addField(i, 0, quoteNotesTextArea, bmoFlexConfig.getQuoteNotes());	i++;
		formFlexTable.addField(i, 0, enableOpportunityEffectCheckBox, bmoFlexConfig.getEnableOpportunityEffect());	i++;
		formFlexTable.addField(i, 0, showOwmCustomerCheckBox, bmoFlexConfig.getShowOwnCustomer());	i++;
		formFlexTable.addField(i, 0, duplicateAddressCheckBox, bmoFlexConfig.getDuplicateAddress());	i++;
		formFlexTable.addField(i, 0, duplicateAddressNumberTextBox, bmoFlexConfig.getDuplicateAddressNumber());	i++;
		formFlexTable.addField(i, 0, creditByLocationCheckBox, bmoFlexConfig.getCreditByLocation()); i++; // para tipo de pedido credito
		
		//	ESTATUS DE OPORTUNIDADES	
		FlowPanel oppocheckBoxPanel = new FlowPanel();
		oppocheckBoxPanel.setWidth("100%");
		oppocheckBoxPanel.add(formFlexTable.getCheckBoxPanel(oppoStatusRevisionCheckBox, bmoFlexConfig.getOppoStatusRevision()));
		oppocheckBoxPanel.add(formFlexTable.getCheckBoxPanel(oppoStatusGanadaCheckBox, bmoFlexConfig.getOppoStatusGanada()));
		oppocheckBoxPanel.add(formFlexTable.getCheckBoxPanel(oppoStatusPerdidanCheckBox, bmoFlexConfig.getOppoStatusPerdida()));
		oppocheckBoxPanel.add(formFlexTable.getCheckBoxPanel(oppoStatusExpiradanCheckBox, bmoFlexConfig.getOppoStatusExpirada()));
		oppocheckBoxPanel.add(formFlexTable.getCheckBoxPanel(oppoStatusHoldCheckBox, bmoFlexConfig.getOppoStatusHold()));
		
		formFlexTable.addLabelCell(i, 0, "Estatus de Opportunidades:"); // NO Incrementar posicion
		formFlexTable.addPanel(i, 1, oppocheckBoxPanel, 2);	i++;
		
		formFlexTable.addField(i, 0, defaultTypeCustomerListBox, bmoFlexConfig.getDefaultTypeCustomer());	i++;
		formFlexTable.addField(i, 0, defaultFormatOpportunityListBox, bmoFlexConfig.getDefaultFormatOpportunity());	i++;
		formFlexTable.addField(i, 0, defaultFormatOrderListBox, bmoFlexConfig.getDefaultFormatOrder());	i++;
		formFlexTable.addField(i, 0, sendEmailCustomerBirthdayCheckBox, bmoFlexConfig.getEmailCustomerBirthday());	i++;
		// validar que se pueda enviar recordatorio de cXc desde clientes
		formFlexTable.addField(i, 0, enableEmailReminderCommentsCheckBox, bmoFlexConfig.getEnableEmailReminderComments());	i++;
		formFlexTable.addField(i, 0, showOrderInCustomerCheckBox, bmoFlexConfig.getShowOrderInCustomer());	i++;
		formFlexTable.addField(i, 0, emailFailCronTextBox, bmoFlexConfig.getEmailFailCron());	i++;
		formFlexTable.addField(i, 0, mobilOrMailCustCheckBox, bmoFlexConfig.getMobilOrMailCust());	i++;
		
		formFlexTable.addSectionLabel(i, 0, "Pedidos", 2);	i++; // posicion: 23
		formFlexTable.addField(i, 0, orderWarehouseListBox, bmoFlexConfig.getOrderWarehouseId());	i++;
		formFlexTable.addField(i, 0, enableOrderLockCheckBox, bmoFlexConfig.getEnableOrderLock());	i++;
		formFlexTable.addField(i, 0, orderRaccountTypeListBox, bmoFlexConfig.getOrderRaccountTypeId());	i++;
		formFlexTable.addField(i, 0, enableRenewProduct,bmoFlexConfig.getRenewProducts());	i++;
		formFlexTable.addField(i, 0, orderRenewDaysTextBox, bmoFlexConfig.getOrderRenewDays());	i++;

		// ESTATUS DEL PEDIDO
		FlowPanel ordeCheckBoxPanel = new FlowPanel();
		ordeCheckBoxPanel.setWidth("100%");
		ordeCheckBoxPanel.add(formFlexTable.getCheckBoxPanel(ordeStatusRevision, bmoFlexConfig.getOrdeStatusRevision()));
		ordeCheckBoxPanel.add(formFlexTable.getCheckBoxPanel(ordeStatusAuthorized, bmoFlexConfig.getOrdeStatusAuthorized()));
		ordeCheckBoxPanel.add(formFlexTable.getCheckBoxPanel(ordeStatusFinished, bmoFlexConfig.getOrdeStatusFinished()));
		ordeCheckBoxPanel.add(formFlexTable.getCheckBoxPanel(ordeStatusCancelled, bmoFlexConfig.getOrdeStatusCancelled()));
		formFlexTable.addLabelCell(i, 0, "Estatus de Pedido"); // NO Incrementar posicion
		formFlexTable.addPanel(i, 1, ordeCheckBoxPanel);	i++;

		formFlexTable.addField(i, 0, discountLimitTextBox, bmoFlexConfig.getDiscountLimit());	i++;
		formFlexTable.addField(i, 0, defaultOrderTypeListBox, bmoFlexConfig.getDefaultOrderTypeId());	i++;	
		formFlexTable.addField(i, 0, extraHourTextBox, bmoFlexConfig.getExtraHour());	i++;
		formFlexTable.addField(i, 0, customerProtectionCheckBox, bmoFlexConfig.getCustomerProtection());	i++;
		formFlexTable.addField(i, 0, statusDefaultCompanyCheckBox, bmoFlexConfig.getCompanyInCustomer());	i++;

		formFlexTable.addSectionLabel(i, 0, "Operaciones", 2);	i++; // posicion: 35
		formFlexTable.addField(i, 0, defaultWarehouseListBox, bmoFlexConfig.getDefaultWarehouseId());	i++;
		formFlexTable.addField(i, 0, defaultWhSectionListBox, bmoFlexConfig.getDefaultWhSectionId());	i++;
		formFlexTable.addField(i, 0, defaultReqPayTypeListBox, bmoFlexConfig.getDefaultReqPayTypeId());	i++;
		formFlexTable.addField(i, 0, enableEnsureProcessCheckBox, bmoFlexConfig.getEnsureProcessCxC());	i++;
		formFlexTable.addField(i, 0, requestAuthReqiCheckBox, bmoFlexConfig.getRequestAuthReqi());	i++;
		formFlexTable.addField(i, 0, requestAuthReqiAmountTextBox, bmoFlexConfig.getRequestAuthReqiAmount());	i++;

		formFlexTable.addSectionLabel(i, 0, "Finanzas", 2);	i++; // posicion: 42
		formFlexTable.addField(i, 0, systemCurrencyListBox, bmoFlexConfig.getSystemCurrencyId());	i++;
		formFlexTable.addField(i, 0, currencyListBox, bmoFlexConfig.getCurrencyId());	i++;
		formFlexTable.addField(i, 0, taxTextBox, bmoFlexConfig.getTax());	i++;
		formFlexTable.addField(i, 0, enableAutoFillCheckBox, bmoFlexConfig.getEnableAutoFill());	i++;
		formFlexTable.addField(i, 0, defaultBankAccountSuggestBox, bmoFlexConfig.getDefaultBankAccountId());	i++;
		formFlexTable.addField(i, 0, enableNegativeBankBalance, bmoFlexConfig.getNegativeBankBalance());	i++;
		formFlexTable.addField(i, 0, enableAutomaticPaymentCxCCheckBox, bmoFlexConfig.getAutomaticPaymentCxC());	i++;
		formFlexTable.addField(i, 0, enableSendEmailReconciledCheckBox, bmoFlexConfig.getSendEmailReconciled());	i++;
		formFlexTable.addField(i, 0, showStatusAuthorizeBkmv, bmoFlexConfig.getAuthorizedBankMov());	i++;
		
		formFlexTable.addField(i, 0, new UiCompanyCollectionProfileLabelList(getUiParams(), id));	i++;
		formFlexTable.addField(i, 0, collectProfileSuggestBox, bmoFlexConfig.getCollectProfileId());	i++;
		formFlexTable.addField(i, 0, sendEmailAuthorizedMBCheckBox, bmoFlexConfig.getSendEmailAuthorizedMB());	i++;
		formFlexTable.addField(i, 0, remindRaccountCheckBox, bmoFlexConfig.getRemaindRaccountInCustomer());	i++;
		formFlexTable.addField(i, 0, daysBeforeRemaindRaccount, bmoFlexConfig.getDayBeforeRemindRaccount());	i++;
		formFlexTable.addField(i, 0, daysBeforeRemaindRaccountTwo, bmoFlexConfig.getDayBeforeRemindRaccountTwo());	i++;
		
		FlowPanel checkBoxPanel = new FlowPanel();
		checkBoxPanel.setWidth("100%");
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(statusRevisionCheckBox, bmoFlexConfig.getStatusRevision()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(statusAuthorizedCheckBox, bmoFlexConfig.getStatusAuthorized()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(statusReconciledCheckBox, bmoFlexConfig.getStatusReconciled()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(statusCancelledCheckBox, bmoFlexConfig.getStatusCancelled()));

		formFlexTable.addLabelCell(i, 0, "Estatus MB:"); // NO Incrementar posicion
		formFlexTable.addPanel(i, 1, checkBoxPanel, 1);	i++;

		formFlexTable.addSectionLabel(i, 0, "Control Presupuestal", 2);	i++; // posicion: 59
		formFlexTable.addField(i, 0, enableWorkBudgetItemCheckBox, bmoFlexConfig.getEnableWorkBudgetItem());	i++;
		formFlexTable.addField(i, 0, enableNegativeBudget, bmoFlexConfig.getNegativeBudget());	i++;
		formFlexTable.addField(i, 0, commisionBudgetItemSuggestBox, bmoFlexConfig.getComissionBudgetItemId());	i++;
		formFlexTable.addField(i, 0, depositBudgetItemTypeSuggestBox, bmoFlexConfig.getDepositBudgetItemTypeId());	i++;
		
		statusEffect();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoFlexConfig.setId(id);
		bmoFlexConfig.getTax().setValue(taxTextBox.getText());
		bmoFlexConfig.getDefaultSalesMan().setValue(salesmanSuggestBox.getSelectedId());
		bmoFlexConfig.getSalesProfileId().setValue(salesProfileSuggestBox.getSelectedId());
		bmoFlexConfig.getSysMessage().setValue(sysMessageTextArea.getText());
		bmoFlexConfig.getEnableQuotes().setValue(enableQuotesCheckBox.getValue());
		bmoFlexConfig.getQuoteNotes().setValue(quoteNotesTextArea.getText());
		bmoFlexConfig.getQuoteDownPayment().setValue(quoteDownPaymentTextBox.getText());
		bmoFlexConfig.getExtraHour().setValue(extraHourTextBox.getText());
		bmoFlexConfig.getDiscountLimit().setValue(discountLimitTextBox.getText());
		bmoFlexConfig.getShowOwnCustomer().setValue(showOwmCustomerCheckBox.getValue());

		bmoFlexConfig.getYtVideoId().setValue(ytVideoIdTextBox.getText());
		bmoFlexConfig.getDefaultOrderTypeId().setValue(defaultOrderTypeListBox.getSelectedId());
		bmoFlexConfig.getEnableOpportunityEffect().setValue(enableOpportunityEffectCheckBox.getValue());
		bmoFlexConfig.getOrderRaccountTypeId().setValue(orderRaccountTypeListBox.getSelectedId());
		bmoFlexConfig.getOrderWarehouseId().setValue(orderWarehouseListBox.getSelectedId());

		bmoFlexConfig.getEnableOrderLock().setValue(enableOrderLockCheckBox.getValue());
		bmoFlexConfig.getDefaultReqPayTypeId().setValue(defaultReqPayTypeListBox.getSelectedId());
		bmoFlexConfig.getDefaultWhSectionId().setValue(defaultWhSectionListBox.getSelectedId());
		bmoFlexConfig.getDefaultWarehouseId().setValue(defaultWarehouseListBox.getSelectedId());
		bmoFlexConfig.getSystemCurrencyId().setValue(systemCurrencyListBox.getSelectedId());
		bmoFlexConfig.getCurrencyId().setValue(currencyListBox.getSelectedId());
		bmoFlexConfig.getEnableWorkBudgetItem().setValue(enableWorkBudgetItemCheckBox.getValue());		
		bmoFlexConfig.getEnsureProcessCxC().setValue(enableEnsureProcessCheckBox.getValue());

		bmoFlexConfig.getNegativeBankBalance().setValue(enableNegativeBankBalance.getValue());
		bmoFlexConfig.getCustomerProtection().setValue(customerProtectionCheckBox.getValue());
		bmoFlexConfig.getNegativeBudget().setValue(enableNegativeBudget.getValue());
		bmoFlexConfig.getComissionBudgetItemId().setValue(commisionBudgetItemSuggestBox.getSelectedId());
		bmoFlexConfig.getDepositBudgetItemTypeId().setValue(depositBudgetItemTypeSuggestBox.getSelectedId());

		bmoFlexConfig.getDefaultTypeCustomer().setValue(defaultTypeCustomerListBox.getSelectedCode());		
		bmoFlexConfig.getEnableAutoFill().setValue(enableAutoFillCheckBox.getValue());		
		bmoFlexConfig.getOrderRenewDays().setValue(orderRenewDaysTextBox.getText());
		bmoFlexConfig.getDefaultBankAccountId().setValue(defaultBankAccountSuggestBox.getSelectedId());
		bmoFlexConfig.getDefaultFormatOpportunity().setValue(defaultFormatOpportunityListBox.getSelectedId());
		bmoFlexConfig.getDefaultFormatOrder().setValue(defaultFormatOrderListBox.getSelectedId());
		bmoFlexConfig.getAutomaticPaymentCxC().setValue(enableAutomaticPaymentCxCCheckBox.getValue());
		bmoFlexConfig.getSendEmailReconciled().setValue(enableSendEmailReconciledCheckBox.getValue());
		bmoFlexConfig.getEmailCustomerBirthday().setValue(sendEmailCustomerBirthdayCheckBox.getValue());
		bmoFlexConfig.getAuthorizedBankMov().setValue(showStatusAuthorizeBkmv.getValue());
		bmoFlexConfig.getRequestAuthReqi().setValue(requestAuthReqiCheckBox.getValue());
		bmoFlexConfig.getRequestAuthReqiAmount().setValue(requestAuthReqiAmountTextBox.getText());
		bmoFlexConfig.getRenewProducts().setValue(enableRenewProduct.getValue());
		bmoFlexConfig.getCollectProfileId().setValue(collectProfileSuggestBox.getSelectedId());

		bmoFlexConfig.getStatusAuthorized().setValue(statusAuthorizedCheckBox.getValue());
		bmoFlexConfig.getStatusRevision().setValue(statusRevisionCheckBox.getValue());
		bmoFlexConfig.getStatusCancelled().setValue(statusCancelledCheckBox.getValue());
		bmoFlexConfig.getStatusReconciled().setValue(statusReconciledCheckBox.getValue());

		bmoFlexConfig.getOppoStatusExpirada().setValue(oppoStatusExpiradanCheckBox.getValue());
		bmoFlexConfig.getOppoStatusGanada().setValue(oppoStatusGanadaCheckBox.getValue());
		bmoFlexConfig.getOppoStatusHold().setValue(oppoStatusHoldCheckBox.getValue());
		bmoFlexConfig.getOppoStatusPerdida().setValue(oppoStatusPerdidanCheckBox.getValue());
		bmoFlexConfig.getOppoStatusRevision().setValue(oppoStatusRevisionCheckBox.getValue());
		bmoFlexConfig.getCompanyInCustomer().setValue(statusDefaultCompanyCheckBox.getValue());

		bmoFlexConfig.getOrdeStatusRevision().setValue(ordeStatusRevision.getValue());
		bmoFlexConfig.getOrdeStatusAuthorized().setValue(ordeStatusAuthorized.getValue());
		bmoFlexConfig.getOrdeStatusFinished().setValue(ordeStatusFinished.getValue());
		bmoFlexConfig.getOrdeStatusCancelled().setValue(ordeStatusCancelled.getValue());
		bmoFlexConfig.getDayBeforeRemindRaccount().setValue(daysBeforeRemaindRaccount.getText());
		bmoFlexConfig.getRemaindRaccountInCustomer().setValue(remindRaccountCheckBox.getValue());
		bmoFlexConfig.getDayBeforeRemindRaccountTwo().setValue(daysBeforeRemaindRaccountTwo.getValue());
		bmoFlexConfig.getEnableEmailReminderComments().setValue(enableEmailReminderCommentsCheckBox.getValue());
		bmoFlexConfig.getShowOrderInCustomer().setValue(showOrderInCustomerCheckBox.getValue());
		bmoFlexConfig.getEnableEmailReminderComments().setValue(enableEmailReminderCommentsCheckBox.getValue());
		bmoFlexConfig.getSendEmailAuthorizedMB().setValue(sendEmailAuthorizedMBCheckBox.getValue());
		bmoFlexConfig.getEmailFailCron().setValue(emailFailCronTextBox.getText());
		bmoFlexConfig.getDuplicateAddress().setValue(duplicateAddressCheckBox.getValue());
		bmoFlexConfig.getDuplicateAddressNumber().setValue(duplicateAddressNumberTextBox.getText());
		bmoFlexConfig.getMultiCompany().setValue(multiCompanyCheckBox.getValue());
		bmoFlexConfig.getMobilOrMailCust().setValue(mobilOrMailCustCheckBox.getValue());
		bmoFlexConfig.getCreditByLocation().setValue(creditByLocationCheckBox.getValue());

		return bmoFlexConfig;
	}
	
	private void statusEffect() {
		validateDuplicateAddress();
		validateMultiCompany();
	}
	
	public void formBooleanChange(ValueChangeEvent<Boolean> event) {
		// Validar check de configuracion de Domicilio duplicado
		if (event.getSource() == duplicateAddressCheckBox) {
			validateDuplicateAddress();
		}
		else if (event.getSource() == multiCompanyCheckBox) {
			validateMultiCompany();
		}
	}
	
	private void validateDuplicateAddress() {
		// Validar check de configuracion de Domicilio duplicado
		if (duplicateAddressCheckBox.getValue()) 
			duplicateAddressNumberTextBox.setEnabled(true);
		else 
			duplicateAddressNumberTextBox.setEnabled(false);
	}
	
	// Validar si esta activo la multiempresa
	private void validateMultiCompany() {
		// Activar campos multiempresa
		if (multiCompanyCheckBox.getValue()) {
			formFlexTable.getWidget(6, 0).setVisible(true); // UiCompanySalesmanLabelList
			formFlexTable.getWidget(6, 1).setVisible(true); // UiCompanySalesmanLabelList
			formFlexTable.getWidget(52, 0).setVisible(true); //UiCompanyCollectionProfileLabelList
			formFlexTable.getWidget(52, 1).setVisible(true); //UiCompanyCollectionProfileLabelList
			salesProfileSuggestBox.setEnabled(false);
			formFlexTable.hideField(salesProfileSuggestBox);
			collectProfileSuggestBox.setEnabled(false);
			formFlexTable.hideField(collectProfileSuggestBox);
			formFlexTable.hideField(sendEmailAuthorizedMBCheckBox);
			formFlexTable.hideField(daysBeforeRemaindRaccount);
			formFlexTable.hideField(remindRaccountCheckBox);
			formFlexTable.hideField(daysBeforeRemaindRaccountTwo);
		} else {
			formFlexTable.getWidget(6, 0).setVisible(false); // UiCompanySalesmanLabelList
			formFlexTable.getWidget(6, 1).setVisible(false); // UiCompanySalesmanLabelList
			formFlexTable.getWidget(52, 0).setVisible(false); //UiCompanyCollectionProfileLabelList
			formFlexTable.getWidget(52, 1).setVisible(false); //UiCompanyCollectionProfileLabelList
			salesProfileSuggestBox.setEnabled(true);
			formFlexTable.showField(salesProfileSuggestBox);
			collectProfileSuggestBox.setEnabled(true);
			formFlexTable.showField(collectProfileSuggestBox);
			formFlexTable.showField(sendEmailAuthorizedMBCheckBox);
			formFlexTable.showField(remindRaccountCheckBox);
			formFlexTable.showField(daysBeforeRemaindRaccount);
			formFlexTable.showField(daysBeforeRemaindRaccountTwo);
		}
	}

	@Override
	public void close() {

	}
}
