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

import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoRequisitionReceipt extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, receiptDate, requisitionId, type, status, payment, companyId, 
	amount, discount, tax, total, whSectionId, supplierId, quality, punctuality, service, reliability, handling,
	budgetItemId, currencyId, currencyParity, areaId;

	BmoRequisition bmoRequisition = new BmoRequisition();

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';

	public static final char TYPE_RECEIPT = 'C';
	public static final char TYPE_RETURN = 'R';

	public static final char PAYMENT_PROVISIONED = 'P'; 
	public static final char PAYMENT_NOTPROVISIONED = 'N'; 

	// Accesos
	public static final String ACCESS_CHANGESTATUS = "PERCHS";
	public static final String CHANGE_ITEMRECEIPTS = "REQICHG";
	public static final String ACCESS_CHANGEEVALUATEPV = "REQEVPV";

	// Accesos 
	public static final String ACCESS_PURCHASE = "REQPUR";	
	public static final String ACCESS_RENTAL = "REQREN";
	public static final String ACCESS_SERVICE = "REQSER";
	public static final String ACCESS_TRAVELEXPENSE = "REQTRA";
	public static final String ACCESS_COMMISION = "REQCOM";
	public static final String CHANGE_ADDNEWITEMREC = "ADDITREC";
	public static final String ACCESS_REQTRAVELEXPENSE = "RQTRAEX";

	public static String CODE_PREFIX = "RC-";

	public BmoRequisitionReceipt() {
		super("com.flexwm.server.op.PmRequisitionReceipt", "requisitionreceipts", "requisitionreceiptid", "RERC", "Recibos Ordenes de Compra");

		// Campo de datos
		code = setField("code", "", "Clave Recibo", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Recibo", 60, Types.VARCHAR, true, BmFieldType.STRING, false);
		receiptDate = setField("receiptdate", "", "Fecha Recibo", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		requisitionId = setField("requisitionid", "", "Order de Compra", 8, Types.INTEGER, false, BmFieldType.ID, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, false, BmFieldType.ID, false);

		whSectionId = setField("whsectionid", "", "Sección Almacén", 8, Types.INTEGER, true, BmFieldType.ID, false);

		type = setField("type", "" + TYPE_RECEIPT, "Tipo Recibo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_RECEIPT, "Recibo", "./icons/rerc_type_receipt.png"),
				new BmFieldOption(TYPE_RETURN, "Devolución", "./icons/rerc_type_return.png")
				)));
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png")
				)));

		payment = setField("payment", "" + PAYMENT_NOTPROVISIONED, "Pago", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		payment.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENT_NOTPROVISIONED, "No Provisionado", "./icons/boolean_false.png"),
				new BmFieldOption(PAYMENT_PROVISIONED, "Provisionado", "./icons/boolean_true.png")								
				)));

		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true,  BmFieldType.CURRENCY, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);		
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		//Valores para Calidad
		quality = setField("quality", "", "Calidad", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		punctuality = setField("punctuality", "", "Puntualidad", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		service = setField("service", "", "Servicio", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		reliability = setField("reliability", "", "Veracidad", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		handling = setField("handling", "", "Manejo", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 4, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 8, Types.INTEGER, true, BmFieldType.ID, false);		

		currencyId = setField("currencyid", "", "Moneda", 4, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 8, Types.VARCHAR, true, BmFieldType.STRING, false);

	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoRequisition().getCode(),
				getBmoRequisition().getName(),	
				getBmoRequisition().getBmoRequisitionType().getType(),
				getBmoRequisition().getBmoSupplier().getName(),
				getReceiptDate(),
				getType(),
				getStatus(),
				getPayment(),
				getTotal()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoRequisition().getCode(),
				getTotal()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoRequisition().getName(),
				getBmoRequisition().getBmoSupplier().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getBmoRequisition().getCode()),
				new BmSearchField(getBmoRequisition().getName()),  
				new BmSearchField(getBmoRequisition().getBmoSupplier().getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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
	 * @return the receiptDate
	 */
	public BmField getReceiptDate() {
		return receiptDate;
	}

	/**
	 * @param receiptDate the receiptDate to set
	 */
	public void setReceiptDate(BmField receiptDate) {
		this.receiptDate = receiptDate;
	}

	/**
	 * @return the requisitionId
	 */
	public BmField getRequisitionId() {
		return requisitionId;
	}

	/**
	 * @param requisitionId the requisitionId to set
	 */
	public void setRequisitionId(BmField requisitionId) {
		this.requisitionId = requisitionId;
	}

	/**
	 * @return the bmoRequisition
	 */
	public BmoRequisition getBmoRequisition() {
		return bmoRequisition;
	}

	/**
	 * @param bmoRequisition the bmoRequisition to set
	 */
	public void setBmoRequisition(BmoRequisition bmoRequisition) {
		this.bmoRequisition = bmoRequisition;
	}

	/**
	 * @return the status
	 */
	public BmField getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BmField status) {
		this.status = status;
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
	 * @return the discount
	 */
	public BmField getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BmField discount) {
		this.discount = discount;
	}

	/**
	 * @return the tax
	 */
	public BmField getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BmField tax) {
		this.tax = tax;
	}

	/**
	 * @return the total
	 */
	public BmField getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BmField total) {
		this.total = total;
	}

	/**
	 * @return the payment
	 */
	public BmField getPayment() {
		return payment;
	}

	/**
	 * @param payment the payment to set
	 */
	public void setPayment(BmField payment) {
		this.payment = payment;
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

	public BmField getWhSectionId() {
		return whSectionId;
	}

	public void setWhSectionId(BmField whSectionId) {
		this.whSectionId = whSectionId;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	/**
	 * @return the quality
	 */
	public BmField getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(BmField quality) {
		this.quality = quality;
	}

	/**
	 * @return the punctuality
	 */
	public BmField getPunctuality() {
		return punctuality;
	}

	/**
	 * @param punctuality the punctuality to set
	 */
	public void setPunctuality(BmField punctuality) {
		this.punctuality = punctuality;
	}

	/**
	 * @return the service
	 */
	public BmField getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(BmField service) {
		this.service = service;
	}

	/**
	 * @return the reliability
	 */
	public BmField getReliability() {
		return reliability;
	}

	/**
	 * @param reliability the reliability to set
	 */
	public void setReliability(BmField reliability) {
		this.reliability = reliability;
	}

	/**
	 * @return the handling
	 */
	public BmField getHandling() {
		return handling;
	}

	/**
	 * @param handling the handling to set
	 */
	public void setHandling(BmField handling) {
		this.handling = handling;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
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

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

}
