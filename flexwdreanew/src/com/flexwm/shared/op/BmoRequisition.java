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

import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;

public class BmoRequisition extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, requestDate, paymentDate, deliveryDate, status, deliveryStatus, 
	amount, discount, taxApplies, tax, total, payments, balance, paymentStatus, supplierId, requestedBy, 
	authorizedUser, authorizedDate,	orderCommissionAmountId, areaId, reqPayTypeId, orderId, warehouseId, 
	companyId, requisitionTypeId, contractEstimationId,	budgetItemId, holdBack, currencyId, currencyParity,
	loanId,	propertyId, file, wFlowTypeId, wFlowId,oportunityId;

	private BmoCompany bmoCompany = new BmoCompany();
	private BmoSupplier bmoSupplier = new BmoSupplier();	
	private BmoReqPayType bmoReqPayType = new BmoReqPayType();	
	private BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
	private BmoCurrency bmoCurrency = new BmoCurrency();

	// Status
	public static final char STATUS_EDITION = 'E';
	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static final char STATUS_CANCELLED = 'C';

	// Delivery Status
	public static final char DELIVERYSTATUS_REVISION = 'R';
	public static final char DELIVERYSTATUS_PARTIAL = 'P';
	public static final char DELIVERYSTATUS_TOTAL = 'T';

	// Payment Status
	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_AUTHORIZED = 'A';
	public static final char PAYMENTSTATUS_TOTAL = 'T';
	public static final char PAYMENTSTATUS_CANCEL='C';

	public static String CODE_PREFIX = "OC-";

	// Permisos especiales
	public static final String ACCESS_CHANGESTATUS = "REQCHS";
	public static final String ACCESS_AUTHORIZE = "REQAUT";
	public static final String ACCESS_CHANGEBUDGETITEM = "REQCBI";
	public static final String ACCESS_CHANGEITEMPRICE = "REQCHIP";
	public static final String ACCESS_CHANGETAX = "REQCHTAX";
	public static final String ACCESS_REQTRAVEXP = "REQTRAVEXP";
	public static final String ACCESS_AUTMIN = "RQAUTMIN";
	public static final String ACCESS_AUTMAX = "RQAUTMAX";
	public static final String ACCESS_REQCHSERVICE = "RQCHSERV"; // Modificar Items de oc de tipo servicio

	// pagar de mas/modificar items

	// Acciones
	public static final String ACTION_REQIAMOUNT = "REQIAMOUNT";
	public static final String ACTION_PROFIT = "PROFIT";
	public static final String ACTION_COPYREQUISITION = "REQICOPY";
	public static final String ACTION_CHEECKINGCOSTS = "CHEECKINGCOSTS";

	public static String ACTION_GETORDERPROPERTY = "GETORDERPROPERTY";
	public static String ACTION_GETORDERPROPERTYTAX = "GETORDERPROPERTYTAX";
	public static String ACCESS_ENABLEORDERTOFINISHEDSALE = "EOOFS";
	public static String ACCESS_CREATEPURCHASE = "OCCREATPUT";
	public static String ACCESS_CREATEVIATICS = "OCCREATVIA";
	

	public BmoRequisition() {
		super("com.flexwm.server.op.PmRequisition", "requisitions", "requisitionid", "REQI", "Ord.Compra");

		requisitionTypeId = setField("requisitiontypeid", "", "Tipo Orden Compra", 8, Types.INTEGER, false, BmFieldType.ID, false);

		code = setField("code", "", "Clave O.C.", 10, 0, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre O.C.", 30, 0, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, 0, true, BmFieldType.STRING, false);		
		requestDate = setField("requestdate", "", "Fecha Solicitud", 20, 0, false, BmFieldType.DATE, false);
		deliveryDate = setField("deliverydate", "", "Fecha Entrega", 20, 0, false, BmFieldType.DATE, false);
		paymentDate = setField("paymentdate", "", "Fecha Pago", 20, 0, false, BmFieldType.DATE, false);

		status = setField("status", "" + STATUS_EDITION, "Status", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_EDITION, "En Edición", "./icons/status_edition.png"),
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED , "Cancelada", "./icons/status_cancelled.png")
				)));
		requestedBy = setField("requestedby", "", "Solicitado Por", 8, Types.INTEGER, true, BmFieldType.ID, false);

		deliveryStatus = setField("deliverystatus", "" + DELIVERYSTATUS_REVISION, "Recepción", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		deliveryStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(DELIVERYSTATUS_REVISION, "Pendiente", "./icons/deliverystatus_pending.png"),
				new BmFieldOption(DELIVERYSTATUS_PARTIAL, "Parcial", "./icons/deliverystatus_partial.png"),
				new BmFieldOption(DELIVERYSTATUS_TOTAL, "Total", "./icons/deliverystatus_total.png")				
				)));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Total", "./icons/paymentstatus_total.png")
				)));

		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true,  BmFieldType.CURRENCY, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		taxApplies = setField("taxapplies", "1", "Aplica IVA", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		holdBack = setField("holdback", "", "Retenciones", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		authorizedUser = setField("authorizeduser", "", "Autorizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedDate = setField("authorizeddate", "", "Fecha de Autorización", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		orderCommissionAmountId = setField("ordercommissionamountid", "", "Tabulador Comisión", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		areaId = setField("areaid", "", "Departamento", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		reqPayTypeId = setField("reqpaytypeid", "", "Términos Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		warehouseId = setField("warehouseid", "", "Almacén", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		contractEstimationId = setField("contractestimationid", "", "Estimación", 8, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 8, Types.VARCHAR, true, BmFieldType.STRING, false);
		oportunityId = setField("oportunityid", "", "Oportunidad", 8, Types.INTEGER, true, BmFieldType.ID, false);
		loanId = setField("loanid", "", "Crédito", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//campo inadico 
		propertyId = setField("propertyid", "", "Inmueble", 8, Types.INTEGER, true, BmFieldType.ID, false);
		file = setField("file", "0", "Archivos", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		wFlowTypeId = setField("wflowtypeid", "", "Tipo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getBmoRequisitionType().getName(),
				getRequestDate(),				
				getBmoSupplier().getName(),	
				getBmoRequisitionType().getType(),
				getBmoCurrency().getCode(),
				getCurrencyParity(),
				getStatus(),
				getDeliveryStatus(),				
				getPaymentStatus(),	
				getFile(),
				getTotal(),
				getPayments(),
				getBalance()				
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getBmoRequisitionType().getName()				
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getBmoSupplier().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getName()),
				new BmSearchField(getBmoSupplier().getCode()),
				new BmSearchField(getBmoSupplier().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) {
			return CODE_PREFIX + getId();
		}
		return "";
	}

	/**
	 * @return the supplierId
	 */
	public BmField getSupplierId() {
		return supplierId;
	}


	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}


	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}


	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}


	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
	}


	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}


	/**
	 * @return the requestDate
	 */
	public BmField getRequestDate() {
		return requestDate;
	}


	/**
	 * @param requestDate the requestDate to set
	 */
	public void setRequestDate(BmField requestDate) {
		this.requestDate = requestDate;
	}


	/**
	 * @return the deliveryDate
	 */
	public BmField getDeliveryDate() {
		return deliveryDate;
	}


	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(BmField deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	/**
	 * @return the requestedBy
	 */
	public BmField getRequestedBy() {
		return requestedBy;
	}


	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(BmField requestedBy) {
		this.requestedBy = requestedBy;
	}


	/**
	 * @return the deliveryStatus
	 */
	public BmField getDeliveryStatus() {
		return deliveryStatus;
	}


	/**
	 * @param deliveryStatus the deliveryStatus to set
	 */
	public void setDeliveryStatus(BmField deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}


	/**
	 * @return the amount
	 */
	public BmField getAmount() {
		return amount;
	}


	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BmField amount) {
		this.amount = amount;
	}


	/**
	 * @return the paymentStatus
	 */
	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * @return the bmoSupplier
	 */
	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	/**
	 * @param bmoSupplier the bmoSupplier to set
	 */
	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(BmField warehouseId) {
		this.warehouseId = warehouseId;
	}

	public BmField getDiscount() {
		return discount;
	}

	public void setDiscount(BmField discount) {
		this.discount = discount;
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

	public BmField getReqPayTypeId() {
		return reqPayTypeId;
	}

	public void setReqPayTypeId(BmField reqPayTypeId) {
		this.reqPayTypeId = reqPayTypeId;
	}

	public BmoReqPayType getBmoReqPayType() {
		return bmoReqPayType;
	}

	public void setBmoReqPayType(BmoReqPayType bmoReqPayType) {
		this.bmoReqPayType = bmoReqPayType;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(BmField paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BmField getTaxApplies() {
		return taxApplies;
	}

	public void setTaxApplies(BmField taxApplies) {
		this.taxApplies = taxApplies;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getOrderCommissionAmountId() {
		return orderCommissionAmountId;
	}

	public void setOrderCommissionAmountId(BmField orderCommissionAmountId) {
		this.orderCommissionAmountId = orderCommissionAmountId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the requisitionTypeId
	 */
	public BmField getRequisitionTypeId() {
		return requisitionTypeId;
	}

	/**
	 * @param requisitionTypeId the requisitionTypeId to set
	 */
	public void setRequisitionTypeId(BmField requisitionTypeId) {
		this.requisitionTypeId = requisitionTypeId;
	}

	/**
	 * @return the bmoRequisitionType
	 */
	public BmoRequisitionType getBmoRequisitionType() {
		return bmoRequisitionType;
	}

	/**
	 * @param bmoRequisitionType the bmoRequisitionType to set
	 */
	public void setBmoRequisitionType(BmoRequisitionType bmoRequisitionType) {
		this.bmoRequisitionType = bmoRequisitionType;
	}

	/**
	 * @return the contractEstimationId
	 */
	public BmField getContractEstimationId() {
		return contractEstimationId;
	}

	/**
	 * @param contractEstimationId the contractEstimationId to set
	 */
	public void setContractEstimationId(BmField contractEstimationId) {
		this.contractEstimationId = contractEstimationId;
	}

	/**
	 * @return the budgetItemId
	 */
	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	/**
	 * @param budgetItemId the budgetItemId to set
	 */
	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getHoldBack() {
		return holdBack;
	}

	public void setHoldBack(BmField holdBack) {
		this.holdBack = holdBack;
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

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
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

	public BmField getLoanId() {
		return loanId;
	}

	public void setLoanId(BmField loanId) {
		this.loanId = loanId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getAuthorizedUser() {
		return authorizedUser;
	}

	public void setAuthorizedUser(BmField authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	public BmField getAuthorizedDate() {
		return authorizedDate;
	}

	public void setAuthorizedDate(BmField authorizedDate) {
		this.authorizedDate = authorizedDate;
	}

	public BmField getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
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

	public BmField getOportunityId() {
		return oportunityId;
	}

	public void setOportunityId(BmField oportunityId) {
		this.oportunityId = oportunityId;
	}	
	

}
