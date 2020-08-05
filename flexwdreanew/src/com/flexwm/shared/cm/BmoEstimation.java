///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.shared.cm;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.op.BmoOrderType;
//import com.symgae.shared.BmFieldOption;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class BmoEstimation extends BmObject implements Serializable {
//	private static final long serialVersionUID = 1L;
//	private BmField code, name, description, status, startDate, endDate, total, 
//		   orderTypeId, currencyId, currencyParity, userId, customerId, wFlowId, companyId, 
//		comments, authorizedUser,rfquId, authorizedDate, cancelledDate, cancelledUser, customerRequisition, budgetItemId, areaId;
//
//	private BmoCurrency bmoCurrency = new BmoCurrency();
//	private BmoCustomer bmoCustomer = new BmoCustomer();
//	private BmoUser bmoUser = new BmoUser();
//	private BmoOrderType bmoOrderType = new BmoOrderType();
//	
//	public static char STATUS_AUTHORIZED= 'A';
//	public static char STATUS_PROCESSING = 'P';
//	
//	public static final char DELIVERYSTATUS_PENDING = 'A';
//	public static final char DELIVERYSTATUS_PARTIAL = 'P';
//	public static final char DELIVERYSTATUS_TOTAL = 'T';
//	public static String ACTION_LOCKEDQUANTITY = "LOCKEDQUANTITY";
//	public static String ACCESS_STATUSESTCHANG = "STATUSESTCHANG"; // CAMBIAR El estatus de la estimación
//   
//
//
//	public static String CODE_PREFIX = "CT-";
//
//	public BmoEstimation() {
//		super("com.flexwm.server.cm.PmEstimation", "estimations", "estimationid", "ESTS", "Estimación");
//		
//		code = setField("code", "", "Clave Cot.", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
//		name = setField("name", "", "Nombre Cot.", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
//		description = setField("description", "", "Notas", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
//		comments = setField("comments", "", "Comentarios", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
//		status = setField("status", "" + STATUS_PROCESSING, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(STATUS_PROCESSING, "En proceso", "./icons/status_revision.png"),
//				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png")
//				)));
//		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		
//		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		userId = setField("userid", "", "Vendedor", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
//		customerId = setField("customerId", "", "Cliente", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		wFlowId = setField("wflowid", "", "Flujo", 20, Types.INTEGER, true, BmFieldType.ID, false);
////		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);
//		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
//		rfquId = setField("rfquid", "", "RFQ", 20, Types.INTEGER, true, BmFieldType.ID, false);
//		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
//
//	}
//	
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//						getCode(), 
//						getName(),
//						getBmoOrderType().getType(),
//						getBmoCustomer().getCode(),
//						getBmoCustomer().getDisplayName(),
//						getBmoUser().getEmail(),
//						getStartDate(),
//						getStatus(),
//						getBmoCurrency().getCode(),
//						getTotal()
//						));
//	}
//
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getCode()), 
//				new BmSearchField(getName()), 
//				new BmSearchField(getDescription())));
//	}
//	
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
//	}
//	
//	public String getCodeFormat() {
//		if (getId() > 0) return CODE_PREFIX + getId();
//		else return "";
//	}
//
//	public BmField getCode() {
//		return code;
//	}
//
//	public void setCode(BmField code) {
//		this.code = code;
//	}
//
//	public BmField getName() {
//		return name;
//	}
//
//	public void setName(BmField name) {
//		this.name = name;
//	}
//
//	public BmField getDescription() {
//		return description;
//	}
//
//	public void setDescription(BmField description) {
//		this.description = description;
//	}
//
//	public BmField getStatus() {
//		return status;
//	}
//
//	public void setStatus(BmField status) {
//		this.status = status;
//	}
//
//	public BmField getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(BmField startDate) {
//		this.startDate = startDate;
//	}
//
//	public BmField getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(BmField endDate) {
//		this.endDate = endDate;
//	}
//	public BmField getTotal() {
//		return total;
//	}
//
//	public void setTotal(BmField total) {
//		this.total = total;
//	}
//
//	public BmField getOrderTypeId() {
//		return orderTypeId;
//	}
//
//	public void setOrderTypeId(BmField orderTypeId) {
//		this.orderTypeId = orderTypeId;
//	}
//
//	public BmField getCurrencyId() {
//		return currencyId;
//	}
//
//	public void setCurrencyId(BmField currencyId) {
//		this.currencyId = currencyId;
//	}
//
//	public BmField getCurrencyParity() {
//		return currencyParity;
//	}
//
//	public void setCurrencyParity(BmField currencyParity) {
//		this.currencyParity = currencyParity;
//	}
//
//
//
//	public BmField getUserId() {
//		return userId;
//	}
//
//	public void setUserId(BmField userId) {
//		this.userId = userId;
//	}
//
//	public BmField getCustomerId() {
//		return customerId;
//	}
//
//	public void setCustomerId(BmField customerId) {
//		this.customerId = customerId;
//	}
//
//	public BmField getwFlowId() {
//		return wFlowId;
//	}
//
//	public void setwFlowId(BmField wFlowId) {
//		this.wFlowId = wFlowId;
//	}
//
//	public BmField getCompanyId() {
//		return companyId;
//	}
//
//	public void setCompanyId(BmField companyId) {
//		this.companyId = companyId;
//	}
//
//	public BmField getComments() {
//		return comments;
//	}
//
//	public void setComments(BmField comments) {
//		this.comments = comments;
//	}
//
//	public BmField getAuthorizedUser() {
//		return authorizedUser;
//	}
//
//	public void setAuthorizedUser(BmField authorizedUser) {
//		this.authorizedUser = authorizedUser;
//	}
//
//	public BmField getRfquId() {
//		return rfquId;
//	}
//
//	public void setRfquId(BmField rfquId) {
//		this.rfquId = rfquId;
//	}
//
//	public BmField getAuthorizedDate() {
//		return authorizedDate;
//	}
//
//	public void setAuthorizedDate(BmField authorizedDate) {
//		this.authorizedDate = authorizedDate;
//	}
//
//	public BmField getCancelledDate() {
//		return cancelledDate;
//	}
//
//	public void setCancelledDate(BmField cancelledDate) {
//		this.cancelledDate = cancelledDate;
//	}
//
//	public BmField getCancelledUser() {
//		return cancelledUser;
//	}
//
//	public void setCancelledUser(BmField cancelledUser) {
//		this.cancelledUser = cancelledUser;
//	}
//
//	public BmField getCustomerRequisition() {
//		return customerRequisition;
//	}
//
//	public void setCustomerRequisition(BmField customerRequisition) {
//		this.customerRequisition = customerRequisition;
//	}
//
//	public BmField getBudgetItemId() {
//		return budgetItemId;
//	}
//
//	public void setBudgetItemId(BmField budgetItemId) {
//		this.budgetItemId = budgetItemId;
//	}
//
//	public BmField getAreaId() {
//		return areaId;
//	}
//
//	public void setAreaId(BmField areaId) {
//		this.areaId = areaId;
//	}
//
//	public BmoCurrency getBmoCurrency() {
//		return bmoCurrency;
//	}
//
//	public void setBmoCurrency(BmoCurrency bmoCurrency) {
//		this.bmoCurrency = bmoCurrency;
//	}
//
//	public BmoCustomer getBmoCustomer() {
//		return bmoCustomer;
//	}
//
//	public void setBmoCustomer(BmoCustomer bmoCustomer) {
//		this.bmoCustomer = bmoCustomer;
//	}
//
//	public BmoUser getBmoUser() {
//		return bmoUser;
//	}
//
//	public void setBmoUser(BmoUser bmoUser) {
//		this.bmoUser = bmoUser;
//	}
//
//	public BmoOrderType getBmoOrderType() {
//		return bmoOrderType;
//	}
//
//	public void setBmoOrderType(BmoOrderType bmoOrderType) {
//		this.bmoOrderType = bmoOrderType;
//	}
//
//	
//	
//}
