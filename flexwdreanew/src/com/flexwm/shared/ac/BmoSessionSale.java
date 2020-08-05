/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.ac;

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


public class BmoSessionSale extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, code, name, description, startDate, endDate, tags,
	customerId, wFlowTypeId, wFlowId, salesUserId, opportunityId, 
	sessionTypePackageId, orderTypeId, orderId, sessionDemo, sessionDateDemo, 
	signLetter, takePhoto, inscriptionDate, maxSessions, noSession, currencyId, currencyParity, companyId, paymentStatus;

	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoOrder bmoOrder = new BmoOrder();
	private BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();	

	private BmoSession bmoSession;

	public static char STATUS_REVISION = 'R';	
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_CANCELLED = 'C';
	public static char STATUS_FINISHED = 'F';

	public static String ACCESS_CHANGESTATUS = "PRSCHS";
	public static String ACCESS_CREATEDEMO = "PRSDEMO";

	public static String ACTION_CREATEDEMO = "CREATEDEMO";
	public static String ACTION_COUNTORSS = "COUNTORSS";
	public static String ACTION_HASORSS = "HASORSS";
	public static String ACTION_GETVALID = "VALID";
	
	public static final char PAYMENTSTATUS_PENDING = 'P';
 	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public static String CODE_PREFIX = "VS-";

	public BmoSessionSale() {
		super("com.flexwm.server.ac.PmSessionSale", "sessionsales", "sessionsaleid", "SESA", "Venta Sesiones");

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Finalizado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")				
				)));

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Comentarios", 700, Types.VARCHAR, true, BmFieldType.STRING, false);

		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "WFlow", 8, Types.INTEGER, false, BmFieldType.ID, false);
		salesUserId = setField("salesuserid", "", "Vendedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		opportunityId = setField("opportunityid", "", "Oportunidad", 8, Types.INTEGER, true, BmFieldType.ID, false);
		sessionTypePackageId = setField("sessiontypepackageid", "", "Paquete Sesiones", 8, Types.INTEGER, false, BmFieldType.ID, false);
		
		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Total", "./icons/paymentstatus_total.png")
				)));

		//Sesion Demo
		sessionDemo = setField("sessiondemo", "", "Clase Prueba", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		sessionDateDemo = setField("sessiondatedemo", "", "Fecha Prueba", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		//Firmar carta y tomar fotos
		signLetter = setField("signletter", "", "Firmo Carta", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		takePhoto = setField("takephoto", "", "Tomar Fotos", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		//Fecha de Inscripción
		inscriptionDate = setField("inscriptiondate", "", "Inscripción", 20, Types.DATE, true, BmFieldType.DATE, false);

		//Numero de Sesiones que tiene acceso
		maxSessions = setField("maxsessions", "", "Sesiones Max.", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		noSession = setField("nosession", "", "# Sesiones", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		//Empresa
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		
		//Moneda
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 8, Types.VARCHAR, true, BmFieldType.STRING, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getBmoSessionTypePackage().getBmoSessionType().getName(),
				getBmoSessionTypePackage().getName(),
				getStartDate(),
				getBmoSessionTypePackage().getBmoSessionType().getDuration(),
				getMaxSessions(),
				getNoSession(),
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
				getBmoWFlow().getProgress(),
				getStatus(),
				getPaymentStatus()
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

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
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

	public BmField getSessionTypePackageId() {
		return sessionTypePackageId;
	}

	public void setSessionTypePackageId(BmField sessionTypePackageId) {
		this.sessionTypePackageId = sessionTypePackageId;
	}

	public BmoSessionTypePackage getBmoSessionTypePackage() {
		return bmoSessionTypePackage;
	}

	public void setBmoSessionTypePackage(BmoSessionTypePackage bmoSessionTypePackage) {
		this.bmoSessionTypePackage = bmoSessionTypePackage;
	}

	public BmField getSessionDemo() {
		return sessionDemo;
	}

	public void setSessionDemo(BmField sessionDemo) {
		this.sessionDemo = sessionDemo;
	}

	public BmField getSignLetter() {
		return signLetter;
	}

	public void setSignLetter(BmField signLetter) {
		this.signLetter = signLetter;
	}

	public BmField getTakePhoto() {
		return takePhoto;
	}

	public void setTakePhoto(BmField takePhoto) {
		this.takePhoto = takePhoto;
	}

	public BmField getSessionDateDemo() {
		return sessionDateDemo;
	}

	public void setSessionDateDemo(BmField sessionDateDemo) {
		this.sessionDateDemo = sessionDateDemo;
	}

	public BmField getInscriptionDate() {
		return inscriptionDate;
	}

	public void setInscriptionDate(BmField inscriptionDate) {
		this.inscriptionDate = inscriptionDate;
	}

	public BmField getNoSession() {
		return noSession;
	}

	public void setNoSession(BmField noSession) {
		this.noSession = noSession;
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

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	public BmoSession getBmoSession() {
		return bmoSession;
	}

	public void setBmoSession(BmoSession bmoSession) {
		this.bmoSession = bmoSession;
	}

	public BmField getMaxSessions() {
		return maxSessions;
	}

	public void setMaxSessions(BmField maxSessions) {
		this.maxSessions = maxSessions;
	}
}
