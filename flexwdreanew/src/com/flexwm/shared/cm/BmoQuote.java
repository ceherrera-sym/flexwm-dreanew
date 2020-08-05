/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoQuote extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, status, startDate, endDate, amount, discount, taxApplies, tax, total, downPayment, authNum,
		showEquipmentQuantity, showEquipmentPrice, showStaffQuantity, showStaffPrice, orderTypeId, currencyId, currencyParity, coverageParity, marketId, userId, customerId, wFlowId, companyId, 
		comments, authorizedUser,rfquId, authorizedDate,estimationId, cancelledDate, cancelledUser, customerRequisition, budgetItemId, areaId, payConditionId;

	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoUser bmoUser = new BmoUser();
	private BmoOrderType bmoOrderType = new BmoOrderType();
	
	public static char STATUS_REVISION = 'P';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_CANCELLED = 'C';
	
	public static final char DELIVERYSTATUS_PENDING = 'A';
	public static final char DELIVERYSTATUS_PARTIAL = 'P';
	public static final char DELIVERYSTATUS_TOTAL = 'T';
	
    //paymentStatus
 	public static final char PAYMENTSTATUS_REVISION = 'R';
 	public static final char PAYMENTSTATUS_AUTHORIZED = 'A';
 	public static final char PAYMENTSTATUS_PARTIAL = 'P';
 	public static final char PAYMENTSTATUS_TOTAL = 'T';
 	public static final char PAYMENTSTATUS_CANCEL='C';
 	
	public static String ACCESS_CHANGESTATUS = "QTCHST";
	public static String ACCESS_SENDQUOTEEMAIL = "QUOTSE";
	public static String ACCESS_LIMITEDDISCOUNT = "QUOTDI";
	public static String ACCESS_UNLIMITEDDISCOUNT = "QUODUN";
	public static String ACCESS_CHANGEITEMPRICE = "QUOCHP";
	public static String ACCESS_CHANGEITEMNAME = "QUOCHN";
	public static String ACCESS_CHANGEKITPRICE = "QUOCKP";
	public static String ACCESS_NOPRODUCTITEM = "QUONPI";
	
	public static String ACTION_SENDQUOTEEMAIL = "QUOTSE";
	public static String ACTION_LOGQUOTE = "QUOTLG";
	public static String ACTION_COPYQUOTE = "QUOTCOPY";
	public static String ACTION_LOCKEDQUANTITY = "LOCKEDQUANTITY";
	public static String ACTION_QUOTESTAFFQUANTITY = "QUOTESTAFFQUANTITY";

	public static String CODE_PREFIX = "CT-";

	public BmoQuote() {
		super("com.flexwm.server.cm.PmQuote", "quotes", "quoteid", "QUOT", "Cotizaciones");
		
		code = setField("code", "", "Clave Cot.", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre Cot.", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Notas", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		taxApplies = setField("taxapplies", "1", "Aplica IVA", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		downPayment = setField("downPayment", "", "Anticipo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		authNum = setField("authnum", "", "# Autorización", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));
		showEquipmentQuantity = setField("showequipmentquantity", "1", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showEquipmentPrice = setField("showequipmentprice", "1", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showStaffQuantity = setField("showstaffquantity", "1", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showStaffPrice = setField("showstaffprice", "1", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
	
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Vendedor", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		coverageParity = setField("coverageparity", "0" , "Cobertura Tipo Cambio", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		customerId = setField("customerId", "", "Cliente", 20, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 20, Types.INTEGER, true, BmFieldType.ID, false);
		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedUser = setField("authorizeduser", "", "Autorizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedDate = setField("authorizeddate", "", "Fecha de Autorización", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		cancelledDate = setField("cancelleddate", "", "Fecha de Cancelación", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		cancelledUser = setField("cancelleduser", "", "Cancelado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		customerRequisition = setField("customerrequisition", "", "O.C. del Cliente", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
//		estimationId = setField("estimationid", "", "Estimación.", 11, Types.INTEGER, true, BmFieldType.ID, false);
//		rfquId = setField("rfquid", "", "RFQ.", 11, Types.INTEGER, true, BmFieldType.ID, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getCode(), 
						getName(),
						getBmoOrderType().getType(),
						getBmoCustomer().getCode(),
						getBmoCustomer().getDisplayName(),
						getBmoUser().getEmail(),
						getStartDate(),
						getStatus(),
						getBmoCurrency().getCode(),
						getTotal()
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

	public BmField getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BmField downPayment) {
		this.downPayment = downPayment;
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

	public BmField getTaxApplies() {
		return taxApplies;
	}

	public void setTaxApplies(BmField taxApplies) {
		this.taxApplies = taxApplies;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}
	
	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
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

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getAuthNum() {
		return authNum;
	}

	public void setAuthNum(BmField authNum) {
		this.authNum = authNum;
	}

	public BmField getComments() {
		return comments;
	}
	
	public void setComments(BmField comments) {
		this.comments = comments;
	}
	
//	public BmField getCustomerContactId() {
//		return customerContactId;
//	}
//
//	public void setCustomerContactId(BmField customerContactId) {
//		this.customerContactId = customerContactId;
//	}
//	
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
	
	public BmField getCoverageParity() {
		return coverageParity;
	}
	
	public void setCoverageParity(BmField coverageParity) {
		this.coverageParity = coverageParity;
	}
	
	public BmField getCustomerRequisition() {
		return customerRequisition;
	}
	
	public void setCustomerRequisition(BmField customerRequisition) {
		this.customerRequisition = customerRequisition;
	}

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
	}
	
	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}
	
	public BmField getPayConditionId() {
		return payConditionId;
	}

	public void setPayConditionId(BmField payConditionId) {
		this.payConditionId = payConditionId;
	}

	public BmField getEstimationId() {
		return estimationId;
	}

	public void setEstimationId(BmField estimationId) {
		this.estimationId = estimationId;
	}

	public BmField getRfquId() {
		return rfquId;
	}

	public void setRfquId(BmField rfquId) {
		this.rfquId = rfquId;
	}
	
	
}
