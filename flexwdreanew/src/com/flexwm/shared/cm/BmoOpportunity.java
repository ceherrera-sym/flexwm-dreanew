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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;


public class BmoOpportunity extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, code, name, description, startDate, endDate, expireDate, customerId, orderTypeId, 
	wFlowTypeId, wFlowId, currencyId, currencyParity, marketId, userId, quoteId, saleDate, saleProbability, 
	foreignWFlowTypeId, amount, loseComments, loseMotiveId, tags, propertyModelId, companyId, customField1, 
	customField2, customField3, customField4, customerRequisition, customerAddressId, venueId, customerContactId, budgetItemId, areaId, 
	fiscalPeriod, fiscalYear, rfquId, amountService, estimationId, amountLicense, leadDate, compvspos, commercialTerms, 
	payConditionId, categoryForecastId;

	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoUser bmoUser = new BmoUser();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoCurrency bmoCurrency = new BmoCurrency();	
	private BmoOrderType bmoOrderType = new BmoOrderType();	
	private BmoVenue bmoVenue = new BmoVenue();
		
	public static char STATUS_REVISION = 'R';
	public static char STATUS_WON = 'W';
	public static char STATUS_LOST = 'L';
	public static char STATUS_EXPIRED = 'E';
	public static char STATUS_HOLD = 'H';

	public static char FORETCAST_UPSIDE = 'U';
	public static char FORETCAST_COMMIT = 'M';
	public static char FORETCAST_BESTCASE = 'B';
	public static char FORETCAST_FORESTCAST = 'F';
	public static char FORETCAST_ONHOLD = 'O';
	public static char FORETCAST_CLOSEDWON = 'W';
	public static char FORETCAST_CLOSEDLOST = 'L';
	public static char FORETCAST_OMITTED = 'I';
	public static char FORETCAST_PIPELINE = 'P';
	public static char FORETCAST_MOSTLIKELY = 'S';
	public static char FORETCAST_1_PIPELINE = '1';
	public static char FORETCAST_2_MOSTLIKELY = '2';
	public static char FORETCAST_3_COMMIT = '3';
	public static char FORETCAST_4_CLOSEDWON = '4';
	public static char FORETCAST_5_CLOSEDLOST = '5';
	public static char FORETCAST_6_ONHOLD= '6';
	// Permisos
	public static String ACCESS_CHANGESTATUS = "OPCHST"; // Permiso para cambiar el estatus
	public static String ACCESS_CHANGESALESMAN = "OPCHSL"; // Permiso para cambiar el vendedor
	public static String ACCESS_CHANGESALESMANINQUOTAUT = "OPCHSLINQA"; // Permiso para cambiar el vendedor cuando la cotizacion esta Autorizada
	public static String ACCESS_CHANGEEXPIREDATE = "OPCEXD"; // Permiso para cambiar la fecha de expiracion
	public static String ACCESS_CHANGECUSTOMER = "OPCHCUS"; // Permiso para cambia el cliente
	public static String ACCESS_COPYOPPORTUNITY = "OPCOPO"; // Permiso para copiar una oportunidad
	public static String ACCESS_CHANGEDATEINQUOTAUT = "OPCHDATINQ"; // Permiso para cambiar fechas cuando la cotizacion esta Autorizada
	public static String ACCESS_LOSEOPPORTUNITY = "OPLOSE";	// Permiso para perder la oportunidad
	public static String ACCESS_CHANGEWFLOWTYPE = "OPCHWFT"; // Permiso para cambiar workFlow(Seguimiento y Efecto)

	public static String ACTION_GETEFFECT = "ACTION_GETEFFECT";
	public static String ACTION_COPYOPPORTUNITY = "ACTION_COPYOPPORTUNITY";
	public static String ACTION_CHANGEWFLOWTYPE = "ACTION_CHANGEWFLOWTYPE";	
	public static String ACTION_GETSUMTOTAL = "ACTION_GETSUMTOTAL";	

	public static String CODE_PREFIX = "OP-";

	public BmoOpportunity() {
		super("com.flexwm.server.cm.PmOpportunity", "opportunities", "opportunityid", "OPPO", "Oportunidades");
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_WON, "Ganada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_LOST, "Perdida", "./icons/status_cancelled.png"),
				new BmFieldOption(STATUS_EXPIRED, "Expirada", "./icons/status_expired.png"),
				new BmFieldOption(STATUS_HOLD, "Detenido", "./icons/status_on_hold.png")
				)));

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		saleProbability = setField("saleprobability", "", "Probabilidad", 8, Types.INTEGER, true, BmFieldType.PERCENTAGE, false);
		saleDate = setField("saledate", "", "Fecha Cierre", 20, Types.DATE, true, BmFieldType.DATE, false);
		expireDate = setField("expiredate", "", "Expira", 20, Types.DATE, true, BmFieldType.DATE, false);
		amount = setField("amount", "", "Valor Cotizado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amountService = setField("amountservice", "0", "Valor Est. Servicios", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amountLicense = setField("amountlicense", "0", "Valor Est. Licencias", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		quoteId = setField("quoteid", "", "Cotización", 8, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);

		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Seguimiento", 8, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Vendedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		loseMotiveId = setField("losemotiveid", "", "Motivo Perder", 8, Types.INTEGER, true, BmFieldType.ID, false);
		loseComments = setField("losecomments", "", "Comentarios Perder", 500, Types.VARCHAR, true, BmFieldType.STRING, false);

		foreignWFlowTypeId = setField("foreignwflowtypeid", "", "Tipo Efecto", 8, Types.INTEGER, false, BmFieldType.ID, false);

		marketId = setField("marketid", "", "Mercado", 8, Types.INTEGER, true, BmFieldType.ID, false);
		propertyModelId = setField("propertymodelid", "", "Modelo Inmueble", 8, Types.INTEGER, true, BmFieldType.ID, false);

		venueId = setField("venueid", "", "Lugar", 8, Types.INTEGER, true, BmFieldType.ID, false);
		customerAddressId = setField("customeraddressid", "", "Dir. Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);

		customField1 = setField("customfield1", "", "Campo 1", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		customField2 = setField("customfield2", "", "Campo 2", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		customField3 = setField("customfield3", "", "Campo 3", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		customField4 = setField("customfield4", "", "Campo 4", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		customerRequisition = setField("customerrequisition", "", "O.C. del Cliente", 20, Types.VARCHAR, true, BmFieldType.STRING, false);

		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);

		fiscalPeriod = setField("fiscalperiod", "", "Periodo Fiscal", 2, Types.VARCHAR, true, BmFieldType.STRING, false);
		fiscalYear = setField("fiscalyear", "", "Año Fiscal", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		customerContactId = setField("customercontactid", "", "Contacto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		leadDate =setField("leaddate", "", "Fecha Lead", 20, Types.DATE, true, BmFieldType.DATETIME, false);
		compvspos = setField("compvspos", "", "Posición vs. Competencia", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		commercialTerms = setField("commercialterms", "", "Condiciones Comerciales", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
		estimationId = setField("estimationid", "", "Estimación", 8, Types.INTEGER, true, BmFieldType.ID, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		rfquId= setField("rfquid", "", "RFQ", 8, Types.INTEGER, true, BmFieldType.ID, false);
		categoryForecastId = setField("categoryforecastid", "", "Categoria Forecast", 11, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
//				getBmoOrderType().getType(),
				getBmoCustomer().getLogo(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
//				getBmoCustomer().getCustomertype(),
				getBmoVenue().getName(),
				getBmoUser().getCode(),				
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getBmoWFlow().getProgress(),
				getExpireDate(),
				getStartDate(),
				getStatus(),
				getBmoCurrency().getCode(),
				getAmount(),
				getTags()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getStartDate(),
				getEndDate(),
				getBmoCustomer().getDisplayName(),
				getBmoWFlow().getBmoWFlowPhase().getName(),
				getStatus(),
				getAmount()
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
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getName()), 
				new BmSearchField(getTags()), 
				new BmSearchField(getBmoCustomer().getDisplayName()),
				new BmSearchField(getBmoUser().getEmail()),
				new BmSearchField(getBmoUser().getFirstname()),
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

	public BmField getSaleProbability() {
		return saleProbability;
	}

	public void setSaleProbability(BmField saleProbability) {
		this.saleProbability = saleProbability;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getForeignWFlowTypeId() {
		return foreignWFlowTypeId;
	}

	public void setForeignWFlowTypeId(BmField foreignWFlowTypeId) {
		this.foreignWFlowTypeId = foreignWFlowTypeId;
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

	public BmField getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(BmField saleDate) {
		this.saleDate = saleDate;
	}

	public BmField getCustomField1() {
		return customField1;
	}

	public void setCustomField1(BmField customField1) {
		this.customField1 = customField1;
	}

	public BmField getCustomField2() {
		return customField2;
	}

	public void setCustomField2(BmField customField2) {
		this.customField2 = customField2;
	}

	public BmField getCustomField3() {
		return customField3;
	}

	public void setCustomField3(BmField customField3) {
		this.customField3 = customField3;
	}

	public BmField getCustomField4() {
		return customField4;
	}

	public void setCustomField4(BmField customField4) {
		this.customField4 = customField4;
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

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}

	public BmField getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(BmField expireDate) {
		this.expireDate = expireDate;
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

	public BmField getPropertyModelId() {
		return propertyModelId;
	}

	public void setPropertyModelId(BmField propertyModelId) {
		this.propertyModelId = propertyModelId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
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

	public BmField getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(BmField customerAddressId) {
		this.customerAddressId = customerAddressId;
	}

	public BmField getVenueId() {
		return venueId;
	}

	public void setVenueId(BmField venueId) {
		this.venueId = venueId;
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

	public BmField getFiscalPeriod() {
		return fiscalPeriod;
	}

	public void setFiscalPeriod(BmField fiscalPeriod) {
		this.fiscalPeriod = fiscalPeriod;
	}

	public BmField getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(BmField fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public BmField getCustomerContactId() {
		return customerContactId;
	}

	public void setCustomerContactId(BmField customerContactId) {
		this.customerContactId = customerContactId;
	}

	public BmField getAmountService() {
		return amountService;
	}

	public void setAmountService(BmField amountService) {
		this.amountService = amountService;
	}

	public BmField getAmountLicense() {
		return amountLicense;
	}

	public void setAmountLicense(BmField amountLicense) {
		this.amountLicense = amountLicense;
	}

	public BmField getLeadDate() {
		return leadDate;
	}

	public void setLeadDate(BmField leadDate) {
		this.leadDate = leadDate;
	}

	public BmField getCompvspos() {
		return compvspos;
	}

	public void setCompvspos(BmField compvspos) {
		this.compvspos = compvspos;
	}

	public BmField getCommercialTerms() {
		return commercialTerms;
	}

	public void setCommercialTerms(BmField commercialTerms) {
		this.commercialTerms = commercialTerms;
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

	public BmField getCategoryForecastId() {
		return categoryForecastId;
	}

	public void setCategoryForecastId(BmField categoryForecastId) {
		this.categoryForecastId = categoryForecastId;
	}

	public BmoVenue getBmoVenue() {
		return bmoVenue;
	}

	public void setBmoVenue(BmoVenue bmoVenue) {
		this.bmoVenue = bmoVenue;
	}
	
}
