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
//
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//import com.symgae.shared.sf.BmoUser;
//import com.flexwm.shared.wf.BmoWFlow;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldOption;
//
//
//
//public class BmoProjectStep extends BmObject implements Serializable {
//	private static final long serialVersionUID = 1L;
//	private BmField code,orderTypeId,name,description,currencyId,userId,lockStart,lockEnd,companyId
//	,defaultBudgetItemId,defaultAreaId,currencyParity,customerId,status,orderId, wFlowTypeId, wFlowId;
//	
//	private BmoCustomer bmoCustomer = new BmoCustomer();
//    private	BmoUser bmoUser = new BmoUser();
//	private BmoWFlow bmoWFlow = new BmoWFlow();
//	
//	public static char STATUS_REVISION = 'R';
//	public static char STATUS_AUTHORIZED = 'A';
//	public static char STATUS_CANCELLED = 'C';
//	public static char STATUS_FINISHED = 'F';
//	
//	public static String CODE_PREFIX = "PR-";
//	
//	public BmoProjectStep() {
//		super("com.flexwm.server.cm.PmProjectStep", "projectsstep", "projectstepid", "SPRO", "Proyectos");
//		customerId = setField("customerid", "", "Cliente", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		code = setField("code", "", "Clave Proy.", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
//		name = setField("name", "", "Nombre Proyecto", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
//		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
//		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, true, BmFieldType.ID, false);
//		userId = setField("userid", "", "Vendedor", 20, Types.INTEGER, false, BmFieldType.ID, false);
//		lockStart = setField("lockstart", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		lockEnd = setField("lockend", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);		
//		defaultBudgetItemId = setField("defaultbudgetitemid", "", "Partida Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
//		defaultAreaId = setField("defaultareaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);		
//		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
//		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
//		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
//				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
//				new BmFieldOption(STATUS_FINISHED, "Terminado", "./icons/status_finished.png"),
//				new BmFieldOption(STATUS_CANCELLED, "Cancelado", "./icons/status_cancelled.png")
//				)));
//		wFlowTypeId = setField("wflowtypeid", "", "Tipo WFlow", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
//	}
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCode(),
//				getName(),
//				getLockStart(),
//				getLockEnd(),
//				getStatus()				
//				));
//	}	
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(
//				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
//				));
//	}
//	
//	public ArrayList<BmField> getMobileFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCode(),
//				getName(),
//				getStatus()		
//				));
//	}
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getCode()), 				
//				new BmSearchField(getName())));
//	}
//	public String getCodeFormat() {
//		if (getId() > 0) return CODE_PREFIX + getId();
//		else return "";
//	}
//	
//	public BmoWFlow getBmoWFlow() {
//		return bmoWFlow;
//	}
//	public void setBmoWFlow(BmoWFlow bmoWFlow) {
//		this.bmoWFlow = bmoWFlow;
//	}
//	
//	public BmoCustomer getBmoCustomer() {
//		return bmoCustomer;
//	}
//
//
//	public void setBmoCustomer(BmoCustomer bmoCustomer) {
//		this.bmoCustomer = bmoCustomer;
//	}
//
//
//	public BmoUser getBmoUser() {
//		return bmoUser;
//	}
//
//
//	public void setBmoUser(BmoUser bmoUser) {
//		this.bmoUser = bmoUser;
//	}
//
//
//	public BmField getCode() {
//		return code;
//	}
//	
//	public void setCode(BmField code) {
//		this.code = code;
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
//	public BmField getCurrencyId() {
//		return currencyId;
//	}
//	
//	public void setCurrencyId(BmField currencyId) {
//		this.currencyId = currencyId;
//	}
//	
//	public BmField getUserId() {
//		return userId;
//	}
//	
//	public void setUserId(BmField userId) {
//		this.userId = userId;
//	}
//	
//	public BmField getLockStart() {
//		return lockStart;
//	}
//	
//	public void setLockStart(BmField lockStart) {
//		this.lockStart = lockStart;
//	}
//	
//	public BmField getLockEnd() {
//		return lockEnd;
//	}
//	
//	public void setLockEnd(BmField lockEnd) {
//		this.lockEnd = lockEnd;
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
//	public BmField getDefaultBudgetItemId() {
//		return defaultBudgetItemId;
//	}
//	
//	public void setDefaultBudgetItemId(BmField defaultBudgetItemId) {
//		this.defaultBudgetItemId = defaultBudgetItemId;
//	}
//	
//	public BmField getDefaultAreaId() {
//		return defaultAreaId;
//	}
//	
//	public void setDefaultAreaId(BmField defaultAreaId) {
//		this.defaultAreaId = defaultAreaId;
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
//	public BmField getStatus() {
//		return status;
//	}
//	
//	public void setStatus(BmField status) {
//		this.status = status;
//	}
//	
//	public BmField getCustomerId() {
//		return customerId;
//	}
//	
//	public void setCustomerId(BmField customerId) {
//		this.customerId = customerId;
//	}
//	public BmField getOrderId() {
//		return orderId;
//	}
//	public void setOrderId(BmField orderId) {
//		this.orderId = orderId;
//	}
//	public BmField getWFlowTypeId() {
//		return wFlowTypeId;
//	}
//	public void setWFlowTypeId(BmField wFlowTypeId) {
//		this.wFlowTypeId = wFlowTypeId;
//	}
//	public BmField getWFlowId() {
//		return wFlowId;
//	}
//	public void setWFlowId(BmField wFlowId) {
//		this.wFlowId = wFlowId;
//	}
//	
//}
