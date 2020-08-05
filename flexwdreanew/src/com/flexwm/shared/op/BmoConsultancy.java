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
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;


public class BmoConsultancy extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, orderTypeId, wFlowTypeId, wFlowId,	customerId, userId, companyId, 
	startDate, endDate, status, marketId, currencyId, currencyParity, amount, tax, total, payments, balance, 
	orderId, opportunityId, budgetItemId, areaId, tags, customerRequisition, customerContactId, 
	// viene del programa de orderDetail
	statusScrum, closeDate, orderDate, desireDate, startDateScrum, deliveryDate, 
	leaderUserId, assignedUserId, areaIdScrum;

	private BmoOrderType bmoOrderType = new BmoOrderType();
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoUser bmoUser = new BmoUser();
	private BmoCurrency bmoCurrency = new BmoCurrency();

	public static char STATUS_REVISION = 'R';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_CANCELLED = 'C';
	public static char STATUS_FINISHED = 'F';

	public static char STATUSSCRUM_INITIAL = 'I';
	public static char STATUSSCRUM_DOING = 'N';
	public static char STATUSSCRUM_DONE = 'D';
	public static char STATUSSCRUM_HOLD = 'H';
	
	// Accesos datos generales
	public static String ACCESS_CHANGESALESMAN = "ORSECHSL"; // Cambiar vendedor

	// Accesos datos para scrum
	public static String ACCESS_CHANGEDATA = "ORSECHDATA"; // Cambiar datos
	public static String ACCESS_CHANGEDESIREDATE = "ORSEDESIRE"; // Cambiar fecha deseada
	public static String ACCESS_CHANGESTARTDATE = "ORSESTARTD"; // Cambiar fecha inicio
	public static String ACCESS_CHANGEDELIVERYDATE = "ORSEDELIVD"; // Cambiar fecha pactada
	public static String ACCESS_CHANGESTATUSSCRUM = "ORSECHSTAT"; // Cambiar estatus scrum

	public static String CODE_PREFIX = "VSE-";

	public static String ACCESS_CHANGESTATUS = "ORSECHST"; // Cambiar estatus

	public BmoConsultancy() {
		super("com.flexwm.server.op.PmConsultancy", "consultancies", "consultancyid", "CONS", "Venta de Consultoría");

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);	
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);

		wFlowTypeId = setField("wflowtypeid", "", "Tipo Flujo", 20, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 20, Types.INTEGER, true, BmFieldType.ID, false);

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));

		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		opportunityId = setField("opportunityid", "", "Oportunidad", 20, Types.INTEGER, true, BmFieldType.ID, false);

		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		amount = setField("amount", "0", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		tax = setField("tax", "0", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "0", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "0", "Saldo Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		customerId = setField("customerid", "", "Cliente", 20, Types.INTEGER, false, BmFieldType.ID, false);
		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);

		userId = setField("userid", "", "Vendedor", 20, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);

		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);

		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);

		// OrderDetail
		statusScrum = setField("statusscrum", "" + STATUSSCRUM_INITIAL, "Estatus SCRUM", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		statusScrum.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUSSCRUM_INITIAL, "Por Iniciar", "./icons/status_revision.png"),
				new BmFieldOption(STATUSSCRUM_DOING, "En Proceso", "./icons/status_authorized.png"),
				new BmFieldOption(STATUSSCRUM_DONE, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUSSCRUM_HOLD, "Retenido", "./icons/status_cancelled.png")
				)));

		orderDate = setField("orderdate", "", "Fecha Pedido", 20, Types.DATE, true, BmFieldType.DATE, false);
		desireDate = setField("desiredate", "", "Fecha Deseada", 20, Types.DATE, true, BmFieldType.DATE, false);
		closeDate = setField("closedate", "", "Fecha Cierre", 20, Types.DATE, true, BmFieldType.DATE, false);
		startDateScrum = setField("startdatescrum", "", "Fecha Inicio SCRUM", 20, Types.DATE, true, BmFieldType.DATE, false);
		deliveryDate = setField("deliverydate", "", "Fecha Pactada", 20, Types.DATE, true, BmFieldType.DATE, false);

		leaderUserId = setField("leaderuserid", "", "Responsable", 8, Types.INTEGER, true, BmFieldType.ID, false);
		assignedUserId = setField("assigneduserid", "", "Consultor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		areaIdScrum = setField("areaidscrum", "", "Departamento SCRUM", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		customerRequisition = setField("customerrequisition", "", "O.C. del Cliente", 20, Types.VARCHAR, true, BmFieldType.STRING, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoOrderType().getType(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getStartDate(),
				getStatus(),
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
				getStatus()
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
				new BmSearchField(getCustomerRequisition()),
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

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
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

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
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

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}
	
	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
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

	public BmField getPayments() {
		return payments;
	}

	public void setPayments(BmField payments) {
		this.payments = payments;
	}

	public BmField getBalance() {
		return balance;
	}

	public void setBalance(BmField balance) {
		this.balance = balance;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(BmField opportunityId) {
		this.opportunityId = opportunityId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getStatusScrum() {
		return statusScrum;
	}

	public void setStatusScrum(BmField statusScrum) {
		this.statusScrum = statusScrum;
	}

	public BmField getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(BmField closeDate) {
		this.closeDate = closeDate;
	}

	public BmField getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(BmField orderDate) {
		this.orderDate = orderDate;
	}

	public BmField getDesireDate() {
		return desireDate;
	}

	public void setDesireDate(BmField desireDate) {
		this.desireDate = desireDate;
	}

	public BmField getStartDateScrum() {
		return startDateScrum;
	}

	public void setStartDateScrum(BmField startDateScrum) {
		this.startDateScrum = startDateScrum;
	}

	public BmField getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(BmField deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public BmField getLeaderUserId() {
		return leaderUserId;
	}

	public void setLeaderUserId(BmField leaderUserId) {
		this.leaderUserId = leaderUserId;
	}

	public BmField getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(BmField assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public BmField getAreaIdScrum() {
		return areaIdScrum;
	}

	public void setAreaIdScrum(BmField areaIdScrum) {
		this.areaIdScrum = areaIdScrum;
	}

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

//	public BmoCurrency getBmoCurrency() {
//		return bmoCurrency;
//	}
//
//	public void setBmoCurrency(BmoCurrency bmoCurrency) {
//		this.bmoCurrency = bmoCurrency;
//	}
//
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

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}
	
	public BmField getCustomerRequisition() {
		return customerRequisition;
	}

	public void setCustomerRequisition(BmField customerRequisition) {
		this.customerRequisition = customerRequisition;
	}
	
	
	public BmField getCustomerContactId() {
		return customerContactId;
	}

	public void setCustomerContactId(BmField customerContactId) {
		this.customerContactId = customerContactId;
	}

}
