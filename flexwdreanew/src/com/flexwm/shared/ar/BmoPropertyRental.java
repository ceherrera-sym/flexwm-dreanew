/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla Guerra
 * @version 2018-08
 */
package com.flexwm.shared.ar;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoPropertyRental  extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status,code, name, description, startDate, endDate,orderTypeId,orderId,
	companyId, marketId, currencyId, currencyParity,tags,userId,wFlowId,customerContactId,	
	propertyId, contractTerm, initialIconme, currentIncome, rentalScheduleDate,wFlowTypeId,customerId, rentIncrease,ownerProperty,
	originRenewContractId,enabled, paymentStatus;

	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoUser bmoUser = new BmoUser();
	private BmoWFlow bmoWFlow = new BmoWFlow();	
	private BmoOrderType bmoOrderType = new BmoOrderType();	
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoProperty bmoProperty = new BmoProperty();

	public static char STATUS_REVISION = 'R';
	public static char STATUS_AUTHORIZED = 'A';
	public static char STATUS_FINISHED = 'F';
	public static char STATUS_CANCEL = 'C';
	
	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_BEATEN = 'B';
	public static final char PAYMENTSTATUS_TOTAL = 'T';
	
	public static char ENABLED = 'E';
	public static char DISABLED = 'D';

	public static String CODE_PREFIX = "CON-";
	
	public static String ACCESS_CHANGESTATUS = "PRCHST";
	
	public static String ACTION_COPYCONTRATC = "CPRRT";
	
	public BmoPropertyRental() {
		super("com.flexwm.server.ar.PmPropertyRental", "propertiesrent", "propertiesrentid", "PRRT", "Contratos");

		// Campo de datos
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", ".portalmobil/icons/btn_green.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FINISHED, "Finalizado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_CANCEL, "Cancelado", "./icons/status_cancelled.png")
				)));
		enabled = setField("enabled", ""+ ENABLED, "Activo", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		enabled.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(ENABLED, "Activo", ""),
				new BmFieldOption(DISABLED, "Inactivo", "")
				)));
		
		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pago Pendiente", "portalmobil/icons/btn_yellow.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago al corriente", "portalmobil/icons/btn_green.png"),
				new BmFieldOption(PAYMENTSTATUS_BEATEN, "Pago Vencido", "portalmobil/icons/btn_red.png")
				)));
		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		// Campos INADICO
		propertyId = setField("propertyid", "", "Inmueble", 8, Types.INTEGER, true, BmFieldType.ID, false);
		contractTerm = setField("contractterm", "", "Plazo Anual", 20, Types.INTEGER, true, BmFieldType.NUMBER, false);		
		initialIconme = setField("initialIconme", "", "Renta Inicial", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		currentIncome = setField("currentIncome", "", "Renta Vigente", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		rentalScheduleDate = setField("rentalscheduledate", "", "Programación Renta", 20, Types.DATE, true, BmFieldType.DATE, false);
		userId = setField("userid", "", "Vendedor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		rentIncrease = setField("rentincrease", "", "Incremento Renta", 20, Types.DATE, false, BmFieldType.DATE, false);
		// Propiertario del inmueble
		ownerProperty = setField("ownerproperty", "", "Arrendador", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		originRenewContractId = setField("originrenewcontractid", "", "Contrato Origen", 8,  Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getBmoProperty().getCode(),
				getOwnerProperty(),
				getEndDate(),
				getBmoCustomer().getLogo(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getPaymentStatus(),
				getBmoUser().getCode(),
//				getBmoWFlow().getBmoWFlowType().getName(),
//				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStartDate(),
//				getBmoWFlow().getProgress(),
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
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
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

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getCustomerContactId() {
		return customerContactId;
	}

	public void setCustomerContactId(BmField customerContactId) {
		this.customerContactId = customerContactId;
	}

	public BmField getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}

	public BmField getContractTerm() {
		return contractTerm;
	}

	public void setContractTerm(BmField contractTerm) {
		this.contractTerm = contractTerm;
	}

	public BmField getInitialIconme() {
		return initialIconme;
	}

	public void setInitialIconme(BmField initialIconme) {
		this.initialIconme = initialIconme;
	}

	public BmField getCurrentIncome() {
		return currentIncome;
	}

	public void setCurrentIncome(BmField currentIncome) {
		this.currentIncome = currentIncome;
	}

	public BmField getRentalScheduleDate() {
		return rentalScheduleDate;
	}

	public void setRentalScheduleDate(BmField rentalScheduleDate) {
		this.rentalScheduleDate = rentalScheduleDate;
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

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getRentIncrease() {
		return rentIncrease;
	}

	public void setRentIncrease(BmField rentIncrease) {
		this.rentIncrease = rentIncrease;
	}

	public BmoProperty getBmoProperty() {
		return bmoProperty;
	}

	public void setBmoProperty(BmoProperty bmoProperty) {
		this.bmoProperty = bmoProperty;
	}

	public BmField getOwnerProperty() {
		return ownerProperty;
	}

	public void setOwnerProperty(BmField ownerProperty) {
		this.ownerProperty = ownerProperty;
	}

	public BmField getOriginRenewContractId() {
		return originRenewContractId;
	}

	public void setOriginRenewContractId(BmField originRenewContractId) {
		this.originRenewContractId = originRenewContractId;
	}

	public BmField getEnabled() {
		return enabled;
	}

	public void setEnabled(BmField enabled) {
		this.enabled = enabled;
	}

	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

}
