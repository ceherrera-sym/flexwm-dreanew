/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;


public class BmoFlexConfig extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField tax, defaultSalesMan, sysMessage, quoteNotes, quoteDownPayment, 
	extraHour, salesProfileId, ytVideoId, enableOpportunityEffect, currencyId, systemCurrencyId,
	enableQuotes, defaultOrderTypeId, orderWarehouseId, orderRaccountTypeId, enableOrderLock, discountLimit, 
	defaultWarehouseId, defaultWhSectionId, defaultReqPayTypeId, enableWorkBudgetItem, negativeBankBalance, ensureProcessCxC,
	negativeBudget, customerProtection, comissionBudgetItemId, depositBudgetItemTypeId, defaultTypeCustomer, enableAutoFill,
	orderRenewDays, defaultBankAccountId, defaultFormatOpportunity, defaultFormatOrder, automaticPaymentCxC, sendEmailReconciled, 
	emailCustomerBirthday, authorizedBankMov, requestAuthReqi, requestAuthReqiAmount, collectProfileId, renewProducts,statusAuthorized,
	statusCancelled,companyInCustomer,statusReconciled,statusRevision, requiredPeriodFiscal,oppoStatusRevision, oppoStatusGanada, oppoStatusPerdida, oppoStatusExpirada, oppoStatusHold,
	ordeStatusRevision, ordeStatusAuthorized, ordeStatusFinished, ordeStatusCancelled, dayBeforeRemindRaccount, remaindRaccountInCustomer, enableEmailReminderComments, dayBeforeRemindRaccountTwo,
	showOrderInCustomer, sendEmailAuthorizedMB, emailFailCron, showOwnCustomer, duplicateAddress, duplicateAddressNumber, multiCompany, mobilOrMailCust, creditByLocation;

	// Tipo de cliente default
	public static char TYPE_PERSON = 'P';
	public static char TYPE_COMPANY = 'C';

	public static String UP = "UP";
	public static String DOWN = "DOWN";
	
	public static String ACTION_SEARCHPROFILESALESMAN = "ACTION_SEARCHPROFILESALESMAN";
	public static String ACTION_SEARCHPROFILECOORDINATOR = "ACTION_SEARCHPROFILECOORDINATOR";
	public static String ACTION_SEARCHCOLLECTPROFILE = "ACTION_SEARCHCOLLECTPROFILE";

	public BmoFlexConfig() {
		super("com.flexwm.server.PmFlexConfig", "flexconfig", "flexconfigid", "FLXC", "Configuración FLEX");

		ytVideoId = setField("ytvideoid", "", "ID Video YouTube", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		sysMessage = setField("sysmessage", "", "Mensaje Sistema", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		multiCompany = setField("multicompany", "", "Multi-Empresa?", 255, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		// ------ Comercial ------
		salesProfileId = setField("salesprofileid", "", "Perfil Vendedores", 8, Types.INTEGER, true, BmFieldType.ID, false);
		defaultSalesMan = setField("defaultsalesman", "", "Vendedor Predet.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		enableQuotes = setField("enablequotes", "", "Cotizaciones?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		quoteDownPayment = setField("quotedownpayment", "", "% Anticipo Cot.", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);
		quoteNotes = setField("quotenotes", "", "Notas Cotización", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		enableOpportunityEffect = setField("enableopportunityeffect", "", "Efecto Oportunidades", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		discountLimit = setField("discountlimit", "", "% Dcto. Límite", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		defaultOrderTypeId = setField("defaultordertypeid", "", "Tipo Pedido Default", 8, Types.INTEGER, true, BmFieldType.ID, false);
		extraHour = setField("extrahour", "", "Hora Extra", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		customerProtection = setField("customerprotection", "", "Protección Clientes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		defaultTypeCustomer = setField("defaulttypecustomer", "", "T. Cliente Default", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		defaultTypeCustomer.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PERSON, "Persona", "./icons/cust_type_person.png"),
				new BmFieldOption(TYPE_COMPANY, "Empresa", "./icons/cust_type_company.png")
				)));
		defaultFormatOpportunity = setField("defaultformatopportunity", "", "Default Formato Oportunidad", 8, Types.INTEGER, true, BmFieldType.ID, false);
		defaultFormatOrder = setField("defaultformatorder", "", "Default Formato Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		emailCustomerBirthday = setField("emailcustomerbirthday", "", "Notif. Cumpl. Clie.", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		enableEmailReminderComments = setField("enableemailremindercomments", "", "Activar Notf. Coment. Tareas?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showOwnCustomer = setField("showowncustomer", "", "Ver Clientes Propios", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		duplicateAddress = setField("duplicateaddress", "", "Validar Domicilio Duplicado?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		duplicateAddressNumber = setField("duplicateaddressnumber", "", "Núm. Dom. Duplicados?", 5, Types.INTEGER, true, BmFieldType.NUMBER, false);
		mobilOrMailCust = setField("mobilormailcust", "", "Mobil/Email en Cliente", 1, Types.INTEGER, true,BmFieldType.BOOLEAN, false);

		// control de estatus en oportunidades 
		oppoStatusRevision = setField("oppostatusrevision", "1", "Revisión?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		oppoStatusGanada = setField("oppostatusganada", "1", "Ganada?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		oppoStatusPerdida = setField("oppostatusperdida", "1", "Perdida?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		oppoStatusExpirada = setField("oppostatusexpirada", "1", "Expirada?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		oppoStatusHold = setField("oppostatushold", "1", "Detenido?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showOrderInCustomer = setField("showorderincustomer", "", "Mostrar Pedido dentro Clientes?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		emailFailCron = setField("emailfailcron", "", "Correo Soporte p/Cron", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);

		// ------ Pedidos ------
		orderWarehouseId = setField("orderwarehouseid", "", "Almacén Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		enableOrderLock = setField("enableorderlock", "", "Bloqueo Pedidos?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		orderRaccountTypeId = setField("orderraccounttypeid", "", "Tipo CxC Auto.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		renewProducts = setField("renewproducts", "0", "Renovación Productos?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		orderRenewDays = setField("orderrenewdays", "", "Dias Renov. Pedidos", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		// Empresa
		companyInCustomer = setField("statusdefaultcompany", "", "Empresa Default en Clientes", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		// ------ Operaciones ------
		defaultWarehouseId = setField("defaultwarehouseid", "", "Almacén Predet.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		defaultWhSectionId = setField("defaultwhsectionid", "", "Sección Normal", 8, Types.INTEGER, true, BmFieldType.ID, false);
		defaultReqPayTypeId = setField("defaultreqpaytypeid", "", "Término de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		ensureProcessCxC = setField("ensureprocesscxc", "", "Crear CxC Pedidos?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);	//Proceso de Creacion automatica de cxc en pedidos
		requestAuthReqi = setField("requestauthreqi", "", "Solic.Autorización O.C.", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		requestAuthReqiAmount = setField("requestauthreqiamount", "", "Monto O.C.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		// control de status de pedido
		ordeStatusAuthorized = setField("ordestatusauthorized", "1", "Autorizada?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		ordeStatusFinished =   setField("ordestatusfinished", "1", "Terminada?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		ordeStatusCancelled =  setField("ordestatuscancelled", "1", "Cancelada?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		ordeStatusRevision =   setField("ordestatusrevision", "1", "Revisión?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		// ------ Finanzas ------
		systemCurrencyId = setField("systemcurrencyid", "", "Moneda Sistema", 10, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda Predet.", 8, Types.INTEGER, false, BmFieldType.ID, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		enableAutoFill = setField("enableautofill", "", "Autollenado Finanzas", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		defaultBankAccountId = setField("defaultbankaccountid", "", "Cuenta Banco", 11, Types.INTEGER, true, BmFieldType.ID, false);
		negativeBankBalance = setField("negativebankbalance", "", "Saldo Negativo?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		automaticPaymentCxC = setField("automaticpaymentcxc", "", "Cobro CxC Crédito", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false); //Cobro Automatico CxC
		sendEmailReconciled = setField("sendemailreconciled", "", "Email al Conciliar MB", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		authorizedBankMov = setField("authorizedbankmov", "", "Solicitar Autorizar MB?", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		
		collectProfileId = setField("collectprofileid", "", "Perfil Cobranza", 8, Types.INTEGER, true, BmFieldType.ID, false);
		sendEmailAuthorizedMB = setField("sendemailauthorizedmb", "", "Email al Autorizar MB CxC", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		// envio de correos en pago de CxC	
		remaindRaccountInCustomer = setField("remaindraccountincustomer", "1", " Recordar Pago de CxC?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		dayBeforeRemindRaccount = setField("daybeforeremindraccount", "1", "Dias para recordar CxC ", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		dayBeforeRemindRaccountTwo = setField("daybeforeremindraccounttwo", "1", "Dias para recordar CxC 2", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		statusAuthorized = setField("statusauthorized", "1", "Autorizado?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		statusCancelled = setField("statuscancelled", "1", "Cancelado?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		statusReconciled = setField("statusreconciled", "1", "Conciliado?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		statusRevision = setField("statusrevision", "1", "En Revisión?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		requiredPeriodFiscal = setField("requiredperiodfiscal", "0", "Requiere Periodo Operativo?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		// tipo creditos
		creditByLocation = setField("creditbylocation", "", "Créditos x Ubicación", 20, Types.INTEGER, true, BmFieldType.ID, false);

		// ------ Control Presupuestal ------
		enableWorkBudgetItem = setField("enableworkbudgetitem", "", "Control Presupuestal?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		negativeBudget = setField("negativebudget", "", "Presup. Negativo?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		depositBudgetItemTypeId = setField("depositbudgetitemtypeid", "", "Partida Ingresos CxC", 8, Types.INTEGER, true, BmFieldType.ID, false);
		comissionBudgetItemId = setField("comissionbudgetitemid", "", "Partida Com. Bancaria", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmField getTax() {
		return tax;
	}

	public void setTax(BmField tax) {
		this.tax = tax;
	}

	public BmField getDefaultSalesMan() {
		return defaultSalesMan;
	}

	public void setDefaultSalesMan(BmField defaultSalesMan) {
		this.defaultSalesMan = defaultSalesMan;
	}

	public BmField getSysMessage() {
		return sysMessage;
	}

	public void setSysMessage(BmField sysMessage) {
		this.sysMessage = sysMessage;
	}

	public BmField getQuoteNotes() {
		return quoteNotes;
	}

	public void setQuoteNotes(BmField quoteNotes) {
		this.quoteNotes = quoteNotes;
	}

	public BmField getQuoteDownPayment() {
		return quoteDownPayment;
	}

	public void setQuoteDownPayment(BmField quoteDownPayment) {
		this.quoteDownPayment = quoteDownPayment;
	}

	public BmField getExtraHour() {
		return extraHour;
	}

	public void setExtraHour(BmField extraHour) {
		this.extraHour = extraHour;
	}

	public BmField getSalesProfileId() {
		return salesProfileId;
	}

	public void setSalesProfileId(BmField salesProfileId) {
		this.salesProfileId = salesProfileId;
	} 

	public BmField getYtVideoId() {
		return ytVideoId;
	}

	public void setYtVideoId(BmField ytVideoId) {
		this.ytVideoId = ytVideoId;
	}

	public BmField getEnableQuotes() {
		return enableQuotes;
	}

	public void setEnableQuotes(BmField enableQuotes) {
		this.enableQuotes = enableQuotes;
	}

	public BmField getEnableOpportunityEffect() {
		return enableOpportunityEffect;
	}

	public void setEnableOpportunityEffect(BmField enableOpportunityEffect) {
		this.enableOpportunityEffect = enableOpportunityEffect;
	}

	public BmField getOrderWarehouseId() {
		return orderWarehouseId;
	}

	public void setOrderWarehouseId(BmField orderWarehouseId) {
		this.orderWarehouseId = orderWarehouseId;
	}

	public BmField getOrderRaccountTypeId() {
		return orderRaccountTypeId;
	}

	public void setOrderRaccountTypeId(BmField orderRaccountTypeId) {
		this.orderRaccountTypeId = orderRaccountTypeId;
	}

	public BmField getDefaultWhSectionId() {
		return defaultWhSectionId;
	}

	public void setDefaultWhSectionId(BmField defaultWhSectionId) {
		this.defaultWhSectionId = defaultWhSectionId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getDefaultReqPayTypeId() {
		return defaultReqPayTypeId;
	}

	public void setDefaultReqPayTypeId(BmField defaultReqPayTypeId) {
		this.defaultReqPayTypeId = defaultReqPayTypeId;
	}

	public BmField getDefaultWarehouseId() {
		return defaultWarehouseId;
	}

	public void setDefaultWarehouseId(BmField defaultWarehouseId) {
		this.defaultWarehouseId = defaultWarehouseId;
	}

	public BmField getEnableOrderLock() {
		return enableOrderLock;
	}

	public void setEnableOrderLock(BmField enableOrderLock) {
		this.enableOrderLock = enableOrderLock;
	}

	public BmField getDefaultOrderTypeId() {
		return defaultOrderTypeId;
	}

	public void setDefaultOrderTypeId(BmField defaultOrderTypeId) {
		this.defaultOrderTypeId = defaultOrderTypeId;
	}

	public BmField getDiscountLimit() {
		return discountLimit;
	}

	public void setDiscountLimit(BmField discountLimit) {
		this.discountLimit = discountLimit;
	}

	/**
	 * @return the enableWorkBudgetItem
	 */
	public BmField getEnableWorkBudgetItem() {
		return enableWorkBudgetItem;
	}

	/**
	 * @param enableWorkBudgetItem the enableWorkBudgetItem to set
	 */
	public void setEnableWorkBudgetItem(BmField enableWorkBudgetItem) {
		this.enableWorkBudgetItem = enableWorkBudgetItem;
	}

	/**
	 * @return the negativeBankBalance
	 */
	public BmField getNegativeBankBalance() {
		return negativeBankBalance;
	}

	/**
	 * @param negativeBankBalance the negativeBankBalance to set
	 */
	public void setNegativeBankBalance(BmField negativeBankBalance) {
		this.negativeBankBalance = negativeBankBalance;
	}

	/**
	 * @return the ensureProcessCxC
	 */
	public BmField getEnsureProcessCxC() {
		return ensureProcessCxC;
	}

	/**
	 * @param ensureProcessCxC the ensureProcessCxC to set
	 */
	public void setEnsureProcessCxC(BmField ensureProcessCxC) {
		this.ensureProcessCxC = ensureProcessCxC;
	}

	public BmField getNegativeBudget() {
		return negativeBudget;
	}

	public void setNegativeBudget(BmField negativeBudget) {
		this.negativeBudget = negativeBudget;
	}

	public BmField getCustomerProtection() {
		return customerProtection;
	}

	public void setCustomerProtection(BmField customerProtection) {
		this.customerProtection = customerProtection;
	}	

	public BmField getComissionBudgetItemId() {
		return comissionBudgetItemId;
	}

	public void setComissionBudgetItemId(BmField comissionBudgetItemId) {
		this.comissionBudgetItemId = comissionBudgetItemId;
	}

	public BmField getDepositBudgetItemTypeId() {
		return depositBudgetItemTypeId;
	}

	public void setDepositBudgetItemTypeId(BmField depositBudgetItemTypeId) {
		this.depositBudgetItemTypeId = depositBudgetItemTypeId;
	}

	public BmField getDefaultTypeCustomer() {
		return defaultTypeCustomer;
	}

	public void setDefaultTypeCustomer(BmField defaultTypeCustomer) {
		this.defaultTypeCustomer = defaultTypeCustomer;
	}

	public BmField getEnableAutoFill() {
		return enableAutoFill;
	}

	public void setEnableAutoFill(BmField enableAutoFill) {
		this.enableAutoFill = enableAutoFill;
	}

	public BmField getSystemCurrencyId() {
		return systemCurrencyId;
	}

	public void setSystemCurrencyId(BmField systemCurrencyId) {
		this.systemCurrencyId = systemCurrencyId;
	}

	public BmField getOrderRenewDays() {
		return orderRenewDays;
	}

	public void setOrderRenewDays(BmField orderRenewDays) {
		this.orderRenewDays = orderRenewDays;
	}

	public BmField getDefaultBankAccountId() {
		return defaultBankAccountId;
	}

	public void setDefaultBankAccountId(BmField defaultBankAccountId) {
		this.defaultBankAccountId = defaultBankAccountId;
	}

	public BmField getDefaultFormatOpportunity() {
		return defaultFormatOpportunity;
	}

	public void setDefaultFormatOpportunity(BmField defaultFormatOpportunity) {
		this.defaultFormatOpportunity = defaultFormatOpportunity;
	}

	public BmField getDefaultFormatOrder() {
		return defaultFormatOrder;
	}

	public void setDefaultFormatOrder(BmField defaultFormatOrder) {
		this.defaultFormatOrder = defaultFormatOrder;
	}

	public BmField getAutomaticPaymentCxC() {
		return automaticPaymentCxC;
	}

	public void setAutomaticPaymentCxC(BmField automaticPaymentCxC) {
		this.automaticPaymentCxC = automaticPaymentCxC;
	}

	public BmField getSendEmailReconciled() {
		return sendEmailReconciled;
	}

	public void setSendEmailReconciled(BmField sendEmailReconciled) {
		this.sendEmailReconciled = sendEmailReconciled;
	}

	public BmField getEmailCustomerBirthday() {
		return emailCustomerBirthday;
	}

	public void setEmailCustomerBirthday(BmField emailCustomerBirthday) {
		this.emailCustomerBirthday = emailCustomerBirthday;
	}

	public BmField getAuthorizedBankMov() {
		return authorizedBankMov;
	}

	public BmField getRequestAuthReqi() {
		return requestAuthReqi;
	}

	public void setRequestAuthReqi(BmField requestAuthReqi) {
		this.requestAuthReqi = requestAuthReqi;
	}

	public BmField getRequestAuthReqiAmount() {
		return requestAuthReqiAmount;
	}

	public void setRequestAuthReqiAmount(BmField requestAuthReqiAmount) {
		this.requestAuthReqiAmount = requestAuthReqiAmount;
	}

	public BmField getCollectProfileId() {
		return collectProfileId;
	}

	public void setCollectProfileId(BmField collectProfileId) {
		this.collectProfileId = collectProfileId;
	}

	public BmField getRenewProducts() {
		return renewProducts;
	}

	public void setRenewProducts(BmField renewProducts) {
		this.renewProducts = renewProducts;
	}

	public BmField getStatusAuthorized() {
		return statusAuthorized;
	}

	public void setStatusAuthorized(BmField statusAuthorized) {
		this.statusAuthorized = statusAuthorized;
	}

	public BmField getStatusCancelled() {
		return statusCancelled;
	}

	public void setStatusCancelled(BmField statusCancelled) {
		this.statusCancelled = statusCancelled;
	}

	public BmField getStatusReconciled() {
		return statusReconciled;
	}

	public void setStatusReconciled(BmField statusReconciled) {
		this.statusReconciled = statusReconciled;
	}

	public BmField getStatusRevision() {
		return statusRevision;
	}

	public void setStatusRevision(BmField statusRevision) {
		this.statusRevision = statusRevision;
	}

	public BmField getRequiredPeriodFiscal() {
		return requiredPeriodFiscal;
	}

	public void setRequiredPeriodFiscal(BmField requiredPeriodFiscal) {
		this.requiredPeriodFiscal = requiredPeriodFiscal;
	}
	public BmField getOppoStatusRevision() {
		return oppoStatusRevision;
	}

	public void setOppoStatusRevision(BmField oppoStatusRevision) {
		this.oppoStatusRevision = oppoStatusRevision;
	}

	public BmField getOppoStatusGanada() {
		return oppoStatusGanada;
	}

	public void setOppoStatusGanada(BmField oppoStatusGanada) {
		this.oppoStatusGanada = oppoStatusGanada;
	}

	public BmField getOppoStatusPerdida() {
		return oppoStatusPerdida;
	}

	public void setOppoStatusPerdida(BmField oppoStatusPerdida) {
		this.oppoStatusPerdida = oppoStatusPerdida;
	}

	public BmField getOppoStatusExpirada() {
		return oppoStatusExpirada;
	}

	public void setOppoStatusExpirada(BmField oppoStatusExpirada) {
		this.oppoStatusExpirada = oppoStatusExpirada;
	}

	public BmField getOppoStatusHold() {
		return oppoStatusHold;
	}

	public void setOppoStatusHold(BmField oppoStatusHold) {
		this.oppoStatusHold = oppoStatusHold;
	}

	public BmField getCompanyInCustomer() {
		return companyInCustomer;
	}

	public void setCompanyInCustomer(BmField companyInCustomer) {
		this.companyInCustomer = companyInCustomer;
	}

	public BmField getOrdeStatusRevision() {
		return ordeStatusRevision;
	}

	public void setOrdeStatusRevision(BmField ordeStatusRevision) {
		this.ordeStatusRevision = ordeStatusRevision;
	}

	public BmField getOrdeStatusAuthorized() {
		return ordeStatusAuthorized;
	}

	public void setOrdeStatusAuthorized(BmField ordeStatusAuthorized) {
		this.ordeStatusAuthorized = ordeStatusAuthorized;
	}

	public BmField getOrdeStatusFinished() {
		return ordeStatusFinished;
	}

	public void setOrdeStatusFinished(BmField ordeStatusFinished) {
		this.ordeStatusFinished = ordeStatusFinished;
	}

	public BmField getOrdeStatusCancelled() {
		return ordeStatusCancelled;
	}

	public void setOrdeStatusCancelled(BmField ordeStatusCancelled) {
		this.ordeStatusCancelled = ordeStatusCancelled;
	}

	public BmField getDayBeforeRemindRaccount() {
		return dayBeforeRemindRaccount;
	}

	public void setDayBeforeRemindRaccount(BmField dayBeforeRemindRaccount) {
		this.dayBeforeRemindRaccount = dayBeforeRemindRaccount;
	}

	public BmField getRemaindRaccountInCustomer() {
		return remaindRaccountInCustomer;
	}

	public void setRemaindRaccountInCustomer(BmField remaindRaccountInCustomer) {
		this.remaindRaccountInCustomer = remaindRaccountInCustomer;
	}

	public BmField getEnableEmailReminderComments() {
		return enableEmailReminderComments;
	}

	public void setEnableEmailReminderComments(BmField enableEmailReminderComments) {
		this.enableEmailReminderComments = enableEmailReminderComments;
	}

	public BmField getShowOrderInCustomer() {
		return showOrderInCustomer;
	}

	public void setShowOrderInCustomer(BmField showOrderInCustomer) {
		this.showOrderInCustomer = showOrderInCustomer;
	}

	public BmField getDayBeforeRemindRaccountTwo() {
		return dayBeforeRemindRaccountTwo;
	}

	public void setDayBeforeRemindRaccountTwo(BmField dayBeforeRemindRaccountTwo) {
		this.dayBeforeRemindRaccountTwo = dayBeforeRemindRaccountTwo;
	}

	public BmField getSendEmailAuthorizedMB() {
		return sendEmailAuthorizedMB;
	}

	public void setSendEmailAuthorizedMB(BmField sendEmailAuthorizedMB) {
		this.sendEmailAuthorizedMB = sendEmailAuthorizedMB;
	}

	public BmField getEmailFailCron() {
		return emailFailCron;
	}

	public void setEmailFailCron(BmField emailFailCron) {
		this.emailFailCron = emailFailCron;
	}

	public BmField getShowOwnCustomer() {
		return showOwnCustomer;
	}

	public void setShowOwnCustomer(BmField showOwnCustomer) {
		this.showOwnCustomer = showOwnCustomer;
	}

	public BmField getDuplicateAddress() {
		return duplicateAddress;
	}

	public void setDuplicateAddress(BmField duplicateAddress) {
		this.duplicateAddress = duplicateAddress;
	}

	public BmField getDuplicateAddressNumber() {
		return duplicateAddressNumber;
	}

	public void setDuplicateAddressNumber(BmField duplicateAddressNumber) {
		this.duplicateAddressNumber = duplicateAddressNumber;
	}

	public BmField getMultiCompany() {
		return multiCompany;
	}

	public void setMultiCompany(BmField multiCompany) {
		this.multiCompany = multiCompany;
	}
	
	public BmField getMobilOrMailCust() {
		return mobilOrMailCust;
	}

	public void setMobilOrMailCust(BmField mobilOrMailCust) {
		this.mobilOrMailCust = mobilOrMailCust;
	}

	public BmField getCreditByLocation() {
		return creditByLocation;
	}

	public void setCreditByLocation(BmField creditByLocation) {
		this.creditByLocation = creditByLocation;
	}
}
