///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//package com.flexwm.shared.cm;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.op.BmoOrderType;
//import com.flexwm.shared.wf.BmoWFlow;
//import com.flexwm.shared.wf.BmoWFlowType;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldOption;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//import com.symgae.shared.sf.BmoUser;
//
//
//
//public class BmoRFQU extends BmObject implements Serializable {
//
//	private BmoCustomer bmoCustomer = new BmoCustomer();
//	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
//	private BmoUser bmoUser = new BmoUser();
//	private BmoWFlow bmoWFlow = new BmoWFlow();
//	private BmoCurrency bmoCurrency = new BmoCurrency();	
//	private BmoOrderType bmoOrderType = new BmoOrderType();	
//	
//	private static final long serialVersionUID = 1L;
//	private BmField codeRFQU, estimationId,customerId, budgetItemId, foreignWFlowTypeId,currencyId, userSale,companyId,customerContactId,affair, dateRFQU, orderTypeId, wFlowTypeId, objectiveRFQU, statusRFQU,wFlowId;
//	
//	public static char STATUS_NOTSTARTING = 'N';
//	public static char STATUS_PROCESSING = 'P';
//	public static char STATUS_CANCELED= 'C';
//	public static char STATUS_COMPLET= 'F';
//	public static String ACTION_GETEFFECT = "ACTION_GETEFFECT";
//
//	public static String CODE_PREFIX = "RFQ-";
//	public static String ACCESS_STATUSRFQCHANG = "STATUSRFQCHANG"; 		// Cambiar el estatus del RFQ
//	public BmoRFQU() {
//		super("com.flexwm.server.cm.PmRFQU", "rfqu", "rfquid", "RFQU", "RFQ");
//		codeRFQU = setField("code", "", "RFQ:", 15, Types.VARCHAR, true, BmFieldType.CODE, true);
//		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		userSale = setField("userid", "", "Vendedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		customerContactId = setField("customercontactid", "", "Contacto", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		affair = setField("affair", "", "Asunto", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
//		dateRFQU = setField("date", "", "Fecha RFQ", 16, Types.DATE, true, BmFieldType.DATETIME, false);
//		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		objectiveRFQU = setField("objective", "", "Objectivo RFQ", 1500
//				, Types.VARCHAR, true, BmFieldType.STRING, false);
//		statusRFQU = setField("status", "" + STATUS_NOTSTARTING, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		statusRFQU.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(STATUS_NOTSTARTING, "Sin Iniciar", "./icons/status_revision.png"),
//				new BmFieldOption(STATUS_PROCESSING, "En Proceso", "./icons/ware_type_normal.png"),
//				new BmFieldOption(STATUS_CANCELED, "Cancelado", "./icons/status_cancelled.png"),
//				new BmFieldOption(STATUS_COMPLET, "Completado", "./icons/true.png")
//				)));
//		wFlowTypeId = setField("wflowtypeid", "", "Tipo Seguimiento", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		estimationId = setField("estimationid", "", "Estimación ", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		foreignWFlowTypeId = setField("foreignwflowtypeid", "", "Tipo Efecto", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
//
//
//	}
//
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCodeRFQU(),
//				getBmoCustomer().getCode(),
//				getBmoCustomer().getDisplayName(),
//				getAffair(),
//				getBmoUser().getCode(),
//				getDateRFQU(),
//				getStatusRFQU()
//				));
//	}
//
//	@Override
//	public ArrayList<BmField> getListBoxFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCodeRFQU(),
//				getAffair()
//				));
//	}
//	
//	@Override
//	public ArrayList<BmField> getMobileFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCodeRFQU(),
//				getAffair()				
//				));
//	}
//
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getCodeRFQU()), 
//				new BmSearchField(getAffair())));
//	}
//
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCodeRFQU(), BmOrder.ASC)));
//	}
//
//	public BmField getCodeRFQU() {
//		return codeRFQU;
//	}
//
//	public void setCodeRFQU(BmField codeRFQU) {
//		this.codeRFQU = codeRFQU;
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
//	public BmField getUserSale() {
//		return userSale;
//	}
//
//	public void setUserSale(BmField userSale) {
//		this.userSale = userSale;
//	}
//
//	public BmField getCustomerContactId() {
//		return customerContactId;
//	}
//
//	public void setCustomerContactId(BmField customerContactId) {
//		this.customerContactId = customerContactId;
//	}
//
//	public BmField getAffair() {
//		return affair;
//	}
//
//	public void setAffair(BmField affair) {
//		this.affair = affair;
//	}
//
//	public BmField getDateRFQU() {
//		return dateRFQU;
//	}
//
//	public void setDateRFQU(BmField dateRFQU) {
//		this.dateRFQU = dateRFQU;
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
//	public BmField getwFlowTypeId() {
//		return wFlowTypeId;
//	}
//
//	public void setwFlowTypeId(BmField wFlowTypeId) {
//		this.wFlowTypeId = wFlowTypeId;
//	}
//
//	public BmField getObjectiveRFQU() {
//		return objectiveRFQU;
//	}
//
//	public void setObjectiveRFQU(BmField objectiveRFQU) {
//		this.objectiveRFQU = objectiveRFQU;
//	}
//
//	public BmField getStatusRFQU() {
//		return statusRFQU;
//	}
//
//	public void setStatusRFQU(BmField statusRFQU) {
//		this.statusRFQU = statusRFQU;
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
//	public BmoWFlowType getBmoWFlowType() {
//		return bmoWFlowType;
//	}
//
//	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
//		this.bmoWFlowType = bmoWFlowType;
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
//	public BmoWFlow getBmoWFlow() {
//		return bmoWFlow;
//	}
//
//	public void setBmoWFlow(BmoWFlow bmoWFlow) {
//		this.bmoWFlow = bmoWFlow;
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
//	public BmoOrderType getBmoOrderType() {
//		return bmoOrderType;
//	}
//
//	public void setBmoOrderType(BmoOrderType bmoOrderType) {
//		this.bmoOrderType = bmoOrderType;
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
//	public BmField getEstimationId() {
//		return estimationId;
//	}
//
//	public void setEstimationId(BmField estimationId) {
//		this.estimationId = estimationId;
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
//	public BmField getForeignWFlowTypeId() {
//		return foreignWFlowTypeId;
//	}
//
//	public void setForeignWFlowTypeId(BmField foreignWFlowTypeId) {
//		this.foreignWFlowTypeId = foreignWFlowTypeId;
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
//	
//}
