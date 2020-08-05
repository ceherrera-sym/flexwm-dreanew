/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cr;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;


public class BmoCredit extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, code, comments, startDate, endDate, tags,
	customerId, wFlowTypeId, wFlowId, salesUserId, creditTypeId, 
	orderTypeId, orderId, amount, progress, bond, collectorUserId, paymentStatus, guaranteeOneId, guaranteeTwoId,
	parentId, companyId, currencyId, currencyParity;

	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoOrder bmoOrder = new BmoOrder();	
	private BmoCreditType bmoCreditType = new BmoCreditType();

	public static char STATUS_REVISION = 'R';
	//public static char STATUS_PREAUTHORIZED = 'P';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_FINISHED = 'F';
	public static char STATUS_CANCELLED = 'C';

	public static char PAYMENTSTATUS_REVISION = 'R';
	public static char PAYMENTSTATUS_NORMAL = 'N';
	public static char PAYMENTSTATUS_PENALTY = 'P';
	public static char PAYMENTSTATUS_INPROBLEM = 'I';

	public static String ACCESS_CHANGESTATUS = "CREDCHS";
	public static String ACCESS_CHANGESTATUSAUTHORIZED = "CREDCHSA";
	public static String ACCESS_CHANGESTATUSFINISHED = "CREDCHSF";
	public static String ACCESS_CHANGECUSTOMER = "CUSTCHANGE";
	

	public static String ACTION_CREDITAUTORIZED = "CREDAUT";
	public static String ACTION_RENEWCREDIT = "RENEWCRED";
	public static String ACTION_GETRENEWCREDIT = "GETRENEWCRED";
	public static String ACTION_LASTMONDAYOFWEEK = "LASTMONDAYOFWEE";
	public static String ACTION_CHANGECREDITDATE = "ACTION_CHANGECREDATE";
	public static String ACTION_CHANGEUSERSALE = "ACTION_CHANGECREDUSER";
	public static String ACTION_CHANGECUSTOMERS = "ACTION_CHANGECUSTOMERS";

	//Permiso para modificar la fecha del crédito
	public static final String ACCESS_CHANGEINFO = "CHSINFO";
	public static final String ACCESS_CHANGEUSER = "CHSUSER";


	public static String CODE_PREFIX = "CR-";

	public BmoCredit() {
		super("com.flexwm.server.cr.PmCredit", "credits", "creditid", "CRED", "MicroCreditos");

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				//new BmFieldOption(STATUS_PREAUTHORIZED, "Pre-Autorización", "./icons/woco_open.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Finalizado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")				
				)));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_REVISION, "Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_REVISION, "En Revision", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_NORMAL, "Normal", "./icons/paymentstatus_normal.png"),
				new BmFieldOption(PAYMENTSTATUS_PENALTY, "Penalización", "./icons/paymentstatus_penalty.png"),
				new BmFieldOption(PAYMENTSTATUS_INPROBLEM, "En Problemas", "./icons/paymentstatus_inproblem.png")				
				)));

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		comments = setField("comments", "", "Comentarios", 700, Types.VARCHAR, true, BmFieldType.STRING, false);

		amount = setField("amount", "", " Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		progress = setField("progress", "", " Avance", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);

		startDate = setField("startdate", "", "Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fin", 20, Types.DATE, true, BmFieldType.DATE, false);

		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);

		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);

		creditTypeId = setField("credittypeid", "", "Tipo Crédito", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);		

		wFlowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		salesUserId = setField("salesuserid", "", "Ejecutivo", 8, Types.INTEGER, true, BmFieldType.ID, false);

		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		bond = setField("bond", "", "#Pagare", 10, Types.VARCHAR, true, BmFieldType.STRING, false);

		collectorUserId = setField("collectoruserid", "", "Gestor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		guaranteeOneId = setField("guaranteeoneid", "", "Primer Aval", 8, Types.INTEGER, true, BmFieldType.ID, false);
		guaranteeTwoId = setField("guaranteetwoid", "", "Segundo Aval", 8, Types.INTEGER, true, BmFieldType.ID, false);

		parentId = setField("parentid", "", "Renovado", 8, Types.INTEGER, true, BmFieldType.ID, false);

		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),				
				getStartDate(),
				getBmoCreditType().getName(),
				getAmount(),
				getStatus(),
				getPaymentStatus()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getBmoWFlow().getBmoWFlowType().getName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
				getBmoCreditType().getName(),
				getAmount(),
				getStatus(),
				getPaymentStatus()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoCustomer().getDisplayName(),
				getStartDate(),
				getAmount()
				));
	}			


	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getTags()),
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

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
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

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
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

	public BmField getSalesUserId() {
		return salesUserId;
	}

	public void setSalesUserId(BmField salesUserId) {
		this.salesUserId = salesUserId;
	}



	public BmField getCreditTypeId() {
		return creditTypeId;
	}

	public void setCreditTypeId(BmField creditTypeId) {
		this.creditTypeId = creditTypeId;
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getProgress() {
		return progress;
	}

	public void setProgress(BmField progress) {
		this.progress = progress;
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

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

	public BmoCreditType getBmoCreditType() {
		return bmoCreditType;
	}

	public void setBmoCreditType(BmoCreditType bmoCreditType) {
		this.bmoCreditType = bmoCreditType;
	}

	public BmField getBond() {
		return bond;
	}

	public void setBond(BmField bond) {
		this.bond = bond;
	}

	public BmField getCollectorUserId() {
		return collectorUserId;
	}

	public void setCollectorUserId(BmField collectorUserId) {
		this.collectorUserId = collectorUserId;
	}

	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BmField getGuaranteeOneId() {
		return guaranteeOneId;
	}

	public void setGuaranteeOneId(BmField guaranteeOneId) {
		this.guaranteeOneId = guaranteeOneId;
	}

	public BmField getGuaranteeTwoId() {
		return guaranteeTwoId;
	}

	public void setGuaranteeTwoId(BmField guaranteeTwoId) {
		this.guaranteeTwoId = guaranteeTwoId;
	}

	public BmField getParentId() {
		return parentId;
	}

	public void setParentId(BmField parentId) {
		this.parentId = parentId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}
}
