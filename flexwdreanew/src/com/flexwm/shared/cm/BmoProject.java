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
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoProject extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, code, name, description, startDate, endDate, geventId, guests, tags, dateCreateProject, 
		customerId, wFlowTypeId, wFlowId, userId, orderTypeId, orderId, comments, customerAddressId, venueId, 
		opportunityId, companyId, marketId, currencyId, currencyParity, warehouseManagerId, homeAddress, dateContract,total;
	
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoUser bmoUser = new BmoUser();
	private BmoVenue bmoVenue = new BmoVenue();	
	private BmoWFlow bmoWFlow = new BmoWFlow();	
	private BmoOrderType bmoOrderType = new BmoOrderType();	
	private BmoCurrency bmoCurrency = new BmoCurrency();

	public static char STATUS_REVISION = 'R';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_FINISHED = 'F';
	public static char STATUS_CANCEL = 'C';
	
	public static String ACCESS_CHANGECUSTOMER = "CUSTCHANG";
	public static String ACCESS_COPYPROJECT = "PRCOPY";
	//Crear proyecto manual sin oportunidad
	public static String ACCESS_CREATEPROJECT = "CRPROJ";
	public static String ACTION_SENDPOLL = "SENDPOLL";
	public static String ACTION_GETSUMTOTAL = "ACTION_GETSUMTOTAL";	
	public static String ACCESS_CHANGESTATUS = "PJCHST";
	public static String CODE_PREFIX = "PR-";

	public BmoProject() {
		super("com.flexwm.server.cm.PmProject", "projects", "projectid", "PROJ", "Proyectos");

		// Campo de datos
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Finalizado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCEL, "Cancelado", "./icons/status_cancelled.png")
				)));
		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		dateCreateProject = setField("datecreateproject", "", "Fecha Creación Proyecto", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		geventId = setField("geventid", "", "Evento Google", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		guests = setField("guests", "", "Invitados", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
		
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Proyecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Vendedor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		orderId = setField("orderId", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		venueId = setField("venueid", "", "Lugar", 8, Types.INTEGER, true, BmFieldType.ID, false);
		customerAddressId = setField("customeraddressid", "", "Dir. Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		homeAddress = setField("homeaddress", "", "Domicilio", 500, Types.VARCHAR, true, BmFieldType.STRING, false);

		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		opportunityId = setField("opportunityid", "", "Oportunidad", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		warehouseManagerId = setField("warehousemanagerid", "", "Team Leader", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		dateContract = setField("datecontract", "", "Fecha Contrato", 20, Types.DATE, true, BmFieldType.DATE, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getBmoCustomer().getCustomertype(),
				getBmoUser().getCode(),
				getBmoVenue().getCode(),
				getBmoWFlow().getBmoWFlowType().getName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
				getBmoWFlow().getProgress(),
				getTotal(),
				getStatus(),
				getTags()
				));
	}
	
	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getCustomertype(),
				getBmoVenue().getCode(),
				getBmoWFlow().getBmoWFlowType().getName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
				getBmoWFlow().getProgress(),
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
				new BmSearchField(getTags()),
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel() ),
				new BmSearchField(getBmoVenue().getCode()),
				new BmSearchField(getBmoVenue().getName())
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

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
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

	public BmField getGeventId() {
		return geventId;
	}

	public void setGeventId(BmField geventId) {
		this.geventId = geventId;
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

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
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

	public BmoVenue getBmoVenue() {
		return bmoVenue;
	}

	public void setBmoVenue(BmoVenue bmoVenue) {
		this.bmoVenue = bmoVenue;
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

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getVenueId() {
		return venueId;
	}

	public void setVenueId(BmField venueId) {
		this.venueId = venueId;
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

	public BmField getGuests() {
		return guests;
	}

	public void setGuests(BmField guests) {
		this.guests = guests;
	}

	public BmField getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(BmField customerAddressId) {
		this.customerAddressId = customerAddressId;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
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

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmField getDateCreateProject() {
		return dateCreateProject;
	}

	public void setDateCreateProject(BmField dateCreateProject) {
		this.dateCreateProject = dateCreateProject;
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

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
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

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
	}

	public BmField getWarehouseManagerId() {
		return warehouseManagerId;
	}

	public void setWarehouseManagerId(BmField warehouseManagerId) {
		this.warehouseManagerId = warehouseManagerId;
	}

	public BmField getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(BmField homeAddress) {
		this.homeAddress = homeAddress;
	}

	public BmField getDateContract() {
		return dateContract;
	}

	public void setDateContract(BmField dateContract) {
		this.dateContract = dateContract;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	
}
