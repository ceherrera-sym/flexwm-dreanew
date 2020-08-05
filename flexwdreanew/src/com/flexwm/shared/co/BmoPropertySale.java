/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoPropertySale extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, type, code, description, startDate, endDate, tags, customerId, wFlowTypeId, wFlowId, 
	salesUserId, opportunityId, orderTypeId, orderId, propertyId, loseMotiveId, loseComments, cancellDate, companyId, hooking, deadLinePayment,
	payConditionId, dateContract, dateKeep;

	private BmoProperty bmoProperty = new BmoProperty();
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoUser bmoSalesUser = new BmoUser();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoOrder bmoOrder = new BmoOrder();

	public static char STATUS_REVISION = 'R';	
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_CANCELLED = 'C';

	public static final char TYPE_NEW = 'N';
	public static final char TYPE_RELOCATION = 'R';

	public static String ACCESS_CHANGESTATUS = "PRSCHS";
	public static String ACCESS_RELOCATE = "PRSARLCT";
	public static String ACCESS_NOSALEDIRECT = "PRSANSD";
	public static String ACCESS_CHANGEWFLOWTYPE = "PRSACHWFTY";
	public static String ACCESS_MODIFYSALESMAN = "PRSAMODSM";

	public static String ACTION_RELOCATE = "RELOCATE";
	public static String ACTION_CHANGEWFLOWTYPE = "CHANGEWFLOWTYPE";

	public static String CODE_PREFIX = "VI-";

	public BmoPropertySale() {
		super("com.flexwm.server.co.PmPropertySale", "propertysales", "propertysaleid", "PRSA", "Venta Inmuebles");

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));

		type = setField("type", "", "Tipo Venta", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_NEW, "Nueva", "./icons/prsa_type_new.png"),
				new BmFieldOption(TYPE_RELOCATION, "Reubicación", "./icons/prsa_type_relocation.png")
				)));

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		description = setField("description", "", "Comentarios", 700, Types.VARCHAR, true, BmFieldType.STRING, false);

		startDate = setField("startdate", "", "Fecha Venta", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fecha Entrega", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);

		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);

		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		propertyId = setField("propertyid", "", "Inmueble", 8, Types.INTEGER, false, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Medio Titulación", 8, Types.INTEGER, false, BmFieldType.ID, false);
		salesUserId = setField("salesuserid", "", "Vendedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		opportunityId = setField("opportunityid", "", "Oportunidad", 8, Types.INTEGER, true, BmFieldType.ID, false);	

		loseMotiveId = setField("losemotiveid", "", "Motivo Cancelación", 8, Types.INTEGER, true, BmFieldType.ID, false);
		loseComments = setField("losecomments", "", "Comentarios Cancelación", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		cancellDate = setField("cancelldate", "", "Fecha Cancelación", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		hooking = setField("hooking", "", "Enganche", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		deadLinePayment = setField("deadlinepayment", "", "Plazo de Pago", 20, Types.INTEGER, true, BmFieldType.NUMBER, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		dateContract = setField("datecontract", "", "Fecha Contrato", 12, Types.DATE, true, BmFieldType.DATE, false);
		dateKeep = setField("datekeep", "", "Fecha Apartado", 12, Types.DATE, true, BmFieldType.DATE, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getBmoCustomer().getNss(),
				getBmoProperty().getCode(),
				getBmoWFlow().getBmoWFlowType().getName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
				getBmoWFlow().getProgress(),
				getStatus(),
				getTags()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getBmoCustomer().getNss(),
				getBmoCustomer().getRfc(),
				getBmoCustomer().getCurp(),
				getBmoSalesUser().getCode(),
				getBmoWFlow().getBmoWFlowType().getName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
				getBmoProperty().getFinishDate(),
				getBmoWFlow().getProgress(),
				getType(),
				getTags()				
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoProperty().getCode(),
				getBmoCustomer().getDisplayName()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getTags()),
				new BmSearchField(getBmoProperty().getCode()),
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getBmoCustomer().getDisplayName())
				));
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

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
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

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
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

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmField getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(BmField opportunityId) {
		this.opportunityId = opportunityId;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getSalesUserId() {
		return salesUserId;
	}

	public void setSalesUserId(BmField salesUserId) {
		this.salesUserId = salesUserId;
	}

	public BmoUser getBmoSalesUser() {
		return bmoSalesUser;
	}

	public void setBmoSalesUser(BmoUser bmoSalesUser) {
		this.bmoSalesUser = bmoSalesUser;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}

	public BmoProperty getBmoProperty() {
		return bmoProperty;
	}

	public void setBmoProperty(BmoProperty bmoProperty) {
		this.bmoProperty = bmoProperty;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	/**
	 * @return the bmoOrder
	 */
	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	/**
	 * @param bmoOrder the bmoOrder to set
	 */
	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

	public BmField getLoseMotiveId() {
		return loseMotiveId;
	}

	public void setLoseMotiveId(BmField loseMotiveId) {
		this.loseMotiveId = loseMotiveId;
	}

	public BmField getLoseComments() {
		return loseComments;
	}

	public void setLoseComments(BmField loseComments) {
		this.loseComments = loseComments;
	}

	public BmField getCancellDate() {
		return cancellDate;
	}

	public void setcancellDateDate(BmField cancellDate) {
		this.cancellDate = cancellDate;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getHooking() {
		return hooking;
	}

	public void setHooking(BmField hooking) {
		this.hooking = hooking;
	}

	public BmField getDeadLinePayment() {
		return deadLinePayment;
	}

	public void setDeadLinePayment(BmField deadLinePayment) {
		this.deadLinePayment = deadLinePayment;
	}

	public BmField getPayConditionId() {
		return payConditionId;
	}

	public void setPayConditionId(BmField payConditionId) {
		this.payConditionId = payConditionId;
	}

	public BmField getDateContract() {
		return dateContract;
	}

	public void setDateContract(BmField dateContract) {
		this.dateContract = dateContract;
	}

	public BmField getDateKeep() {
		return dateKeep;
	}

	public void setDateKeep(BmField dateKeep) {
		this.dateKeep = dateKeep;
	}
	
	
	
	
}
