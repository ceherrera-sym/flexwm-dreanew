/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;

public class BmoOrder extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, status, deliveryStatus, paymentStatus, amount, discount, taxApplies, 
	tax, total, lockStatus, lockStart, lockEnd, 	totalRaccounts, totalCreditNotes, payments, balance, showEquipmentQuantity, 
	showEquipmentPrice, showStaffQuantity, showStaffPrice, tags,	marketId, orderTypeId, orderCommissionId, currencyId, 
	currencyParity, coverageParity, quoteId, wFlowTypeId, wFlowId, customerId, userId, companyId, opportunityId, renewOrderId, 
	originRenewOrderId, willRenew, comments, authorizedUser, authorizedDate, customerContactId, customerRequisition,
	cancelledDate, cancelledUser, finishedDate, finishedUser,defaultBudgetItemId,defaultAreaId, statusDetail, payConditionId;

	private BmoOrderType bmoOrderType = new BmoOrderType();
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();

	public static char STATUS_REVISION = 'R';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_CANCELLED = 'C';
	public static char STATUS_FINISHED = 'F';

	public static char STATUSDETAIL_INITIAL = 'I';
	public static char STATUSDETAIL_DOING = 'N';
	public static char STATUSDETAIL_DONE = 'D';
	public static char STATUSDETAIL_HOLD = 'H';

	public static final char DELIVERYSTATUS_PENDING = 'N';
	public static final char DELIVERYSTATUS_PARTIAL = 'P';
	public static final char DELIVERYSTATUS_TOTAL = 'T';

	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public static char LOCKSTATUS_LOCKED = 'L';
	public static char LOCKSTATUS_CONFLICT = 'C';

	public static String ACCESS_CHANGESTATUS = "ORDHST";
	public static String ACCESS_CHANGESTATUSFINISHED = "ORDHSTF";
	public static String ACCESS_CHANGESTATUSUNFINISHED = "ORDHSTUNF";

	public static String ACCESS_SENDEMAIL = "ORDTSE";
	public static String ACCESS_CHANGEITEM = "ORDCHI";
	public static String ACCESS_CHANGEITEMNAME = "ORDCHN";
	public static String ACCESS_CHANGEITEMPRICE = "ORDCHP";
	public static String ACCESS_LIMITEDDISCOUNT = "ORDLID";
	public static String ACCESS_UNLIMITEDDISCOUNT = "ORDEUND";
	public static String ACCESS_CHANGEKITPRICE = "ORDCKP";
	public static String ACCESS_NOPRODUCTITEM = "ORDNPI";
	public static String ACCESS_CHANGESALESMAN = "ORDCHSL";
	public static String ACCESS_OVERDRAFTCREDITNOTES = "ORDODC";
	public static String ACCESS_CHANGECOMPANY = "ORDCHC";
	public static String ACCESS_CANCELLEDWITHCCAUT = "ORDECWCCA"; // Cancelar pedido a pesar de que tenga CC autorizadas

	public static String ACTION_SENDEMAIL = "ORDTSE";
	public static String ACTION_LOCKEDQUANTITY = "LOCKEDQUANTITY";
	public static String ACTION_ORDERBALANCE = "ORDERBALANCE";
	public static String ACTION_ORDERITEMPENDING = "ORDERITEMPENDING";
	public static String ACTION_QUOTESTAFFQUANTITY = "QUOTESTAFFQUANTITY";
	public static String ACTION_ORDERSTAFFQUANTITY = "ORDERSTAFFQUANTITY";
	public static String ACTION_CREATENEWORDER = "NEWORDER";
	public static String ACTION_TOTALREQUISITIONS = "TOTALREQUISITIONS";
	public static String ACTION_EXTRAORDER = "EXTRAORDER";
	public static String ACTION_GETTAXRACCS = "ACTION_GETTAXRACCS";
	public static String ACTION_GETTAXRACCSPAYMENTS = "ACTION_GETTAXRACCSPAYMENTS";
	public static String ACTION_GETTAXRACCSPENDING = "ACTION_GETTAXRACCSPENDING";
	public static String ACTION_VALIDATESTATUS = "VALIDATESTATUS";
	public static String ACTION_GETDATAOWNERPROPERTY = "GETDATAOWNERPROPERTY";
	public static String ACTION_SHOWBUTTONRENEWORDER = "SHOWBUTTONRENEWORDER";
	public static String ACTION_CREATERENEWORDER = "CREATERENEWORDER";
	public static String ACTION_PAYORDER = "PAYORDER"; // Pagar un pedido
	public static String ACTION_GETMONTHS  = "GETMONTHS"; // Obtener meses entre fecha inicio y fecha fin 
	public static String ACTION_CREATEPROJ  = "CREPROJ";
	public static String ACTION_GETEFFECT = "ACTION_GETEFFECT";
	public static String ACTION_DELETECONSULTANCYFROMORDER= "DELETECONSULTANCYFROMORDER";
	
	public static String CODE_PREFIX = "PED-";

	public BmoOrder() {
		super("com.flexwm.server.op.PmOrder", "orders", "orderid", "ORDE", "Pedidos");

		code = setField("code", "", "Clave Pedido", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre Pedido", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Notas", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);	
		comments = setField("comments", "", "Comentarios", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);		
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		taxApplies = setField("taxapplies", "", "Aplica IVA", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		totalRaccounts = setField("totalraccounts", "", "Sub Total CxC", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		totalCreditNotes = setField("totalcreditnotes", "", "Total N.C.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "", "Saldo Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));

		statusDetail = setField("statusdetail", "" , "Estatus Detalle", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		statusDetail.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUSDETAIL_INITIAL, "Por Iniciar", "./icons/status_revision.png"),
				new BmFieldOption(STATUSDETAIL_DOING, "En Proceso", "./icons/status_authorized.png"),
				new BmFieldOption(STATUSDETAIL_DONE, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUSDETAIL_HOLD, "Retenido", "./icons/status_cancelled.png")
				)));

		showEquipmentQuantity = setField("showequipmentquantity", "1", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showEquipmentPrice = setField("showequipmentprice", "1", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showStaffQuantity = setField("showstaffquantity", "1", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showStaffPrice = setField("showstaffprice", "1", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);

		deliveryStatus = setField("deliverystatus", "" + DELIVERYSTATUS_PENDING, "Entrega", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		deliveryStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(DELIVERYSTATUS_PENDING, "Pendiente", "./icons/deliverystatus_pending.png"),
				new BmFieldOption(DELIVERYSTATUS_PARTIAL, "Parcial", "./icons/deliverystatus_partial.png"),
				new BmFieldOption(DELIVERYSTATUS_TOTAL, "Entregada", "./icons/deliverystatus_total.png")
				)));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Total", "./icons/paymentstatus_total.png")
				)));

		lockStart = setField("lockstart", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		lockEnd = setField("lockend", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		lockStatus = setField("lockstatus", "" + LOCKSTATUS_LOCKED, "Apartado", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		lockStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(LOCKSTATUS_LOCKED, "Apartado", "./icons/lockstatus_locked.png"),
				new BmFieldOption(LOCKSTATUS_CONFLICT, "Conflicto", "./icons/lockstatus_conflict.png")
				)));

		opportunityId = setField("opportunityid", "", "Oportunidad", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		orderCommissionId = setField("ordercommissionid", "", "Tab. Comisión", 20, Types.INTEGER, true, BmFieldType.ID, false);

		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		coverageParity = setField("coverageparity", "0", "Cobertura T/C", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		customerId = setField("customerid", "", "Cliente", 20, Types.INTEGER, false, BmFieldType.ID, false);
		quoteId = setField("quoteid", "", "Cotización", 20, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Flujo", 20, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 20, Types.INTEGER, true, BmFieldType.ID, false);
		userId = setField("userid", "", "Vendedor", 20, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		renewOrderId = setField("reneworderid", "", "Renov. Pedido", 20, Types.INTEGER, true, BmFieldType.ID, false);
		originRenewOrderId = setField("originreneworderid", "", "Ped. Origen Renov.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		willRenew = setField("willrenew", "0", "Renovar?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);

		authorizedUser = setField("authorizeduser", "", "Autorizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedDate = setField("authorizeddate", "", "Fecha Autorizado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		cancelledDate = setField("cancelleddate", "", "Fecha Cancelado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		cancelledUser = setField("cancelleduser", "", "Cancelado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		finishedDate = setField("finisheddate", "", "Fecha Finalizado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		finishedUser = setField("finisheduser", "", "Finalizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);

		// Partida presupuestal defauilt
		defaultBudgetItemId = setField("defaultbudgetitemid", "", "Partida Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		defaultAreaId = setField("defaultareaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		customerRequisition = setField("customerrequisition", "", "O.C. del Cliente", 20, Types.VARCHAR, true, BmFieldType.STRING, false);

	}

	public BmField getRenewOrderId() {
		return renewOrderId;
	}

	public void setRenewOrderId(BmField renewOrderId) {
		this.renewOrderId = renewOrderId;
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoOrderType().getType(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getLockStart(),
				getStatus(),
				getDeliveryStatus(),	
				getPaymentStatus(),
				getBmoCurrency().getCode(),
				getTotal(),
				getTags()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoOrderType().getType(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getStatus(),
				getLockStatus(),
				getDeliveryStatus(),	
				getPaymentStatus(),
				getBmoCurrency().getCode(),
				getTotal()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getBmoCustomer().getDisplayName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getDiscount() {
		return discount;
	}

	public void setDiscount(BmField discount) {
		this.discount = discount;
	}

	public BmField getTax() {
		return tax;
	}

	public void setTax(BmField tax) {
		this.tax = tax;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	public BmField getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(BmField deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BmField getTaxApplies() {
		return taxApplies;
	}

	public void setTaxApplies(BmField taxApplies) {
		this.taxApplies = taxApplies;
	}

	public BmField getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(BmField lockStatus) {
		this.lockStatus = lockStatus;
	}

	public BmField getLockStart() {
		return lockStart;
	}

	public void setLockStart(BmField lockStart) {
		this.lockStart = lockStart;
	}

	public BmField getLockEnd() {
		return lockEnd;
	}

	public void setLockEnd(BmField lockEnd) {
		this.lockEnd = lockEnd;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	public BmField getShowEquipmentQuantity() {
		return showEquipmentQuantity;
	}

	public void setShowEquipmentQuantity(BmField showEquipmentQuantity) {
		this.showEquipmentQuantity = showEquipmentQuantity;
	}

	public BmField getShowEquipmentPrice() {
		return showEquipmentPrice;
	}

	public void setShowEquipmentPrice(BmField showEquipmentPrice) {
		this.showEquipmentPrice = showEquipmentPrice;
	}

	public BmField getShowStaffQuantity() {
		return showStaffQuantity;
	}

	public void setShowStaffQuantity(BmField showStaffQuantity) {
		this.showStaffQuantity = showStaffQuantity;
	}

	public BmField getShowStaffPrice() {
		return showStaffPrice;
	}

	public void setShowStaffPrice(BmField showStaffPrice) {
		this.showStaffPrice = showStaffPrice;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmField getOrderCommissionId() {
		return orderCommissionId;
	}

	public void setOrderCommissionId(BmField orderCommissionId) {
		this.orderCommissionId = orderCommissionId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(BmField opportunityId) {
		this.opportunityId = opportunityId;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
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

	public BmField getCancelledDate() {
		return cancelledDate;
	}

	public void setCancelledDate(BmField cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public BmField getCancelledUser() {
		return cancelledUser;
	}

	public void setCancelledUser(BmField cancelledUser) {
		this.cancelledUser = cancelledUser;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(BmField finishedDate) {
		this.finishedDate = finishedDate;
	}

	public BmField getFinishedUser() {
		return finishedUser;
	}

	public void setFinishedUser(BmField finishedUser) {
		this.finishedUser = finishedUser;
	}

	public BmField getCoverageParity() {
		return coverageParity;
	}

	public void setCoverageParity(BmField coverageParity) {
		this.coverageParity = coverageParity;
	}

	public BmField getOriginRenewOrderId() {
		return originRenewOrderId;
	}

	public void setOriginRenewOrderId(BmField originRenewOrderId) {
		this.originRenewOrderId = originRenewOrderId;
	}

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
	}

	public BmField getWillRenew() {
		return willRenew;
	}

	public void setWillRenew(BmField willRenew) {
		this.willRenew = willRenew;
	}

	public BmField getTotalRaccounts() {
		return totalRaccounts;
	}

	public void setTotalRaccounts(BmField totalRaccounts) {
		this.totalRaccounts = totalRaccounts;
	}

	public BmField getTotalCreditNotes() {
		return totalCreditNotes;
	}

	public void setTotalCreditNotes(BmField totalCreditNotes) {
		this.totalCreditNotes = totalCreditNotes;
	}

	public BmField getBalance() {
		return balance;
	}

	public void setBalance(BmField balance) {
		this.balance = balance;
	}

	public BmField getPayments() {
		return payments;
	}

	public void setPayments(BmField payments) {
		this.payments = payments;
	}

	public BmField getDefaultBudgetItemId() {
		return defaultBudgetItemId;
	}

	public void setDefaultBudgetItemId(BmField defaultBudgetItemId) {
		this.defaultBudgetItemId = defaultBudgetItemId;
	}

	public BmField getDefaultAreaId() {
		return defaultAreaId;
	}

	public void setDefaultAreaId(BmField defaultAreaId) {
		this.defaultAreaId = defaultAreaId;
	}

	public BmField getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(BmField statusDetail) {
		this.statusDetail = statusDetail;
	}
	
	public BmField getPayConditionId() {
		return payConditionId;
	}

	public void setPayConditionId(BmField payConditionId) {
		this.payConditionId = payConditionId;
	}

	public BmField getCustomerContactId() {
		return customerContactId;
	}

	public void setCustomerContactId(BmField customerContactId) {
		this.customerContactId = customerContactId;
	}
	
	public BmField getCustomerRequisition() {
		return customerRequisition;
	}

	public void setCustomerRequisition(BmField customerRequisition) {
		this.customerRequisition = customerRequisition;
	}
}
