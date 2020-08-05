/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoReqPayType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class BmoRaccount extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField raccountTypeId, supplierId,status,
	code, invoiceno, reference, receiveDate, dueDate, remindDate, description, comments, commentLog,
	paymentDate, userId, companyId, customerId, orderId, balance, paymentStatus, payments, amount,total, autoCreate,
	currencyId, currencyParity, budgetItemId, failure, relatedRaccountId, taxApplies, tax, paymentTypeId,
	orderGroupId, linked, authorizedUser, authorizedDate, serie, folio, collectorUserId, dueDateStart, reqPayTypeId, areaId, file, statusCharge;

	private BmoPaymentType bmoPaymentType = new BmoPaymentType();
	private BmoRaccountType bmoRaccountType = new BmoRaccountType();
	private BmoCustomer bmoCustomer = new BmoCustomer();	
	private BmoCompany bmoCompany = new BmoCompany();
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoReqPayType bmoReqPayType = new BmoReqPayType();
	private BmoUser bmoUser = new BmoUser();
	

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static final char STATUS_CANCELLED = 'C';

	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public static final String ACCESS_CHANGESTATUS = "RACCHS";
	public static final String ACCESS_LINKED = "ACCLINKED";
	public static final String ACCESS_CHANGECOLLECTORUSER = "RACCCU";
	public static final String ACCESS_PAYMENTRACC = "RACCPAY";

	public static String ACTION_ORDERBALANCE = "RACC_ORDERBALANCE";
	public static String ACTION_ORDERPROVISION = "RACC_ORDERPROVISION";
	public static String ACTION_ORDERPENDINGPROVISION = "RACC_ORDERPENDINGPROVISION";
	public static String ACTION_AMOUNTAUTOCREATE = "RACC_AMOUNTAUTOCREATE";
	public static String ACTION_GETCUSTOMERBYRACC = "RACC_GETCUSTOMERBYRACC";
	public static String ACTION_GETBUDGETITEM = "RACC_GETBUDGETITEM";
	public static String ACTION_SUMCCPENDINGNOLINKED = "RACC_CCPENDINGNOLINKED";

	//Pedido Tipo Creditos
	public static String ACTION_ORDERPAYMENTS = "RACC_ORDERPAYMENTS";
	public static String ACTION_ORDERFAILURE = "RACC_ORDERFAILURE";
	public static String ACTION_MULTIPLERACCOUNT = "RACC_MULTIPLERACCOUNT";

	//Pagos desde la App
	public static String ACTION_PAYMENTAPP = "RACC_PAYMENTAPP";
	public static String ACTION_UPDATEPAYMENTAPP = "UPDATEPAYMENTAPP";
	public static String ACTION_GETBANKACCOUNT = "GETBANKACCOUNT";
	public static String ACTION_GETPAYMENTTYPE = "GETPAYMENTTYPE";
	public static String ACTION_DELETEPAYORDER = "DELETEPAYORDER";

	public static final String ACTION_PAYDATE = "PAYDATE";
	public static String ACTION_MULTIPLEPAYMENT = "MULTIPLEPAYMENT";

	//Obtener la fecha de Pago (Lunes) en Cobi
	public static String ACTION_DUEDATEAPP = "RACC_DUEDATE";

	public static final int PAYMENTDAYS = 30;
	public static String CODE_PREFIX = "CC-";

	public BmoRaccount() {
		super("com.flexwm.server.fi.PmRaccount","raccounts", "raccountid", "RACC","CxC");

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", "./icons/paymentstatus_total.png")				
				)));
		invoiceno = setField("invoiceno", "", "Factura", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		serie = setField("serie", "", "Serie", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		folio = setField("folio", "", "Folio", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		reference = setField("reference", "", "Referencia", 60, Types.VARCHAR, true, BmFieldType.STRING, false);
		receiveDate = setField("receivedate", "", "F. Registro", 10, Types.DATE, false, BmFieldType.DATE, false);
		dueDate = setField("duedate", "", "Programación", 10, Types.DATE, false, BmFieldType.DATE, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		commentLog = setField("commentlog", "", "Bitácora CxC", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
		statusCharge = setField("statuscharge", "", "Estatus de Cobranza", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		//Relacionar una cuenta por cobrar
		relatedRaccountId = setField("relatedraccountid", "", "CxC Relacionada", 30, Types.INTEGER, true, BmFieldType.ID, false);
		paymentDate = setField("paymentdate", "", "Cobro en Bancos", 10, Types.DATE, true, BmFieldType.DATE, false);
		remindDate = setField("reminddate", "", "Recordar el", 10, Types.DATE, true, BmFieldType.DATE, false);
		raccountTypeId = setField("raccounttypeid", "", "Tipo CxC", 11, Types.INTEGER, false, BmFieldType.ID, false);		
		orderId = setField("orderid", "", "Pedido", 11, Types.INTEGER, true, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);				
		userId = setField("userid", "", "Vendedor", 11, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 11, Types.INTEGER, true, BmFieldType.ID, false);
		balance = setField("balance", "0", "Saldo", 30, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "0", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "0", "SubTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		//Manejo de Iva
		taxApplies = setField("taxapplies", "", "Aplica IVA", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		currencyId = setField("currencyid", "", "Moneda", 11, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
		autoCreate = setField("autocreate", "", "Cxc Automatica", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		//Marcar las CxC que son fallos en COBI
		failure = setField("failure", "", "Penalidad", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		linked = setField("linked", "0", "Externa", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentTypeId = setField("paymenttypeid", "", "Método de Pago", 11, Types.INTEGER, true, BmFieldType.ID, false);
		orderGroupId = setField("ordergroupid", "", "Grupo Pedido", 11, Types.INTEGER, true, BmFieldType.ID, false);
		//Autorización
		authorizedUser = setField("authorizeduser", "", "Autorizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedDate = setField("authorizeddate", "", "Fecha de Autorización", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		//Cobranza
		collectorUserId = setField("collectuserid", "", "Cobranza", 11, Types.INTEGER, true, BmFieldType.ID, false);

		dueDateStart = setField("duedatestart", "", "Vencimiento", 10, Types.DATE, true, BmFieldType.DATE, false);
		reqPayTypeId = setField("reqpaytypeid", "", "Término de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		file = setField("file", "0", "Archivos", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getInvoiceno(),	
				getReference(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getDueDate(),
				getBmoReqPayType().getName(),
				getStatus(),
				getPaymentStatus(),
				getFile(),
				getBmoCurrency().getCode(),
				getCurrencyParity(),
				getBmoRaccountType().getCategory(),
				getTotal(),				
				getPayments(),
				getBalance()				
				));
	}

	@Override	
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getReceiveDate(), 
				getInvoiceno(), 
				getBmoCustomer().getCode(), 
				getTotal()
				));
	}

	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getInvoiceno(),
				getDueDate(),
				getTotal(),
				getBalance()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getDescription()),
				new BmSearchField(getInvoiceno()),
				new BmSearchField(getReference()),
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getBmoCustomer().getDisplayName())				
				));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String statusToCaption(char status) {
		switch (status){
		case STATUS_REVISION: return "En Revision";
		case STATUS_AUTHORIZED: return "Autorizado";
		default: return "Tipo de Elemento Invalido";
		}
	}	


	/**
	 * @return the raccountTypeId
	 */
	public BmField getRaccountTypeId() {
		return raccountTypeId;
	}

	/**
	 * @param raccountTypeId the raccountTypeId to set
	 */
	public void setRaccountTypeId(BmField raccountTypeId) {
		this.raccountTypeId = raccountTypeId;
	}

	/**
	 * @return the supplierId
	 */
	public BmField getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the status
	 */
	public BmField getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BmField status) {
		this.status = status;
	}

	/**
	 * @return the invoiceno
	 */
	public BmField getInvoiceno() {
		return invoiceno;
	}

	/**
	 * @param invoiceno the invoiceno to set
	 */
	public void setInvoiceno(BmField invoiceno) {
		this.invoiceno = invoiceno;
	}


	/**
	 * @return the receiveDate
	 */
	public BmField getReceiveDate() {
		return receiveDate;
	}

	/**
	 * @param receiveDate the receiveDate to set
	 */
	public void setReceiveDate(BmField receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * @return the dueDate
	 */
	public BmField getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}



	/**
	 * @return the paymentDate
	 */
	public BmField getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(BmField paymentDate) {
		this.paymentDate = paymentDate;
	}


	/**
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	/**
	 * @return the companyId
	 */
	public BmField getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}



	/**
	 * @return the bmoRaccountType
	 */
	public BmoRaccountType getBmoRaccountType() {
		return bmoRaccountType;
	}

	/**
	 * @param bmoRaccountType the bmoRaccountType to set
	 */
	public void setBmoRaccountType(BmoRaccountType bmoRaccountType) {
		this.bmoRaccountType = bmoRaccountType;
	}

	/**
	 * @return the customerId
	 */
	public BmField getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}



	/**
	 * @return the bmoCustomer
	 */
	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}



	/**
	 * @param bmoCustomer the bmoCustomer to set
	 */
	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	/**
	 * @return the bmoCompany
	 */
	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	/**
	 * @param bmoCompany the bmoCompany to set
	 */
	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}



	public BmField getOrderId() {
		return orderId;
	}



	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}



	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}



	/**
	 * @return the balance
	 */
	public BmField getBalance() {
		return balance;
	}



	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BmField balance) {
		this.balance = balance;
	}

	/**
	 * @return the paymentStatus
	 */
	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BmField getReference() {
		return reference;
	}

	public void setReference(BmField reference) {
		this.reference = reference;
	}

	/**
	 * @return the payments
	 */
	public BmField getPayments() {
		return payments;
	}

	/**
	 * @param payments the payments to set
	 */
	public void setPayments(BmField payments) {
		this.payments = payments;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	/**
	 * @return the autoCreate
	 */
	public BmField getAutoCreate() {
		return autoCreate;
	}

	/**
	 * @param autoCreate the autoCreate to set
	 */
	public void setAutoCreate(BmField autoCreate) {
		this.autoCreate = autoCreate;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getFailure() {
		return failure;
	}

	public void setFailure(BmField failure) {
		this.failure = failure;
	}

	public BmField getRelatedRaccountId() {
		return relatedRaccountId;
	}

	public void setRelatedRaccountId(BmField relatedRaccountId) {
		this.relatedRaccountId = relatedRaccountId;
	}

	public BmField getTaxApplies() {
		return taxApplies;
	}

	public void setTaxApplies(BmField taxApplies) {
		this.taxApplies = taxApplies;
	}

	public BmField getTax() {
		return tax;
	}

	public void setTax(BmField tax) {
		this.tax = tax;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getOrderGroupId() {
		return orderGroupId;
	}

	public void setOrderGroupId(BmField orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public BmField getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(BmField paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getLinked() {
		return linked;
	}

	public void setLinked(BmField linked) {
		this.linked = linked;
	}

	public BmField getAuthorizedUser() {
		return authorizedUser;
	}

	public void setAuthorizedUser(BmField authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	public BmField getAuthorizedDate() {
		return authorizedDate;
	}

	public void setAuthorizedDate(BmField authorizedDate) {
		this.authorizedDate = authorizedDate;
	}

	public BmoPaymentType getBmoPaymentType() {
		return bmoPaymentType;
	}

	public void setBmoPaymentType(BmoPaymentType bmoPaymentType) {
		this.bmoPaymentType = bmoPaymentType;
	}

	public BmField getSerie() {
		return serie;
	}

	public void setSerie(BmField serie) {
		this.serie = serie;
	}

	public BmField getFolio() {
		return folio;
	}

	public void setFolio(BmField folio) {
		this.folio = folio;
	}

	public BmField getCollectorUserId() {
		return collectorUserId;
	}

	public void setCollectorUserId(BmField collectorUserId) {
		this.collectorUserId = collectorUserId;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getCommentLog() {
		return commentLog;
	}

	public void setCommentLog(BmField commentLog) {
		this.commentLog = commentLog;
	}

	public BmField getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(BmField remindDate) {
		this.remindDate = remindDate;
	}

	public BmField getDueDateStart() {
		return dueDateStart;
	}

	public void setDueDateStart(BmField dueDateStart) {
		this.dueDateStart = dueDateStart;
	}

	public BmField getReqPayTypeId() {
		return reqPayTypeId;
	}

	public void setReqPayTypeId(BmField reqPayTypeId) {
		this.reqPayTypeId = reqPayTypeId;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

	public BmField getStatusCharge() {
		return statusCharge;
	}

	public void setStatusCharge(BmField statusCharge) {
		this.statusCharge = statusCharge;
	}

	public BmoReqPayType getBmoReqPayType() {
		return bmoReqPayType;
	}

	public void setBmoReqPayType(BmoReqPayType bmoReqPayType) {
		this.bmoReqPayType = bmoReqPayType;
	}
	

}
