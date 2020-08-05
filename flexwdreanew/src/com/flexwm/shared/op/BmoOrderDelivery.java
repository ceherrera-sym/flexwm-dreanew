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


public class BmoOrderDelivery extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, deliveryDate, orderId, type, status, payment, companyId, 
	amount, discount, tax, total, customerId, toWhSectionId, currencyId, currencyParity, projectId,notes,userCreate;

	BmoOrder bmoOrder = new BmoOrder();

	public static final char TYPE_DELIVERY = 'D';
	public static final char TYPE_RETURN = 'R';

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';	

	public static final char PAYMENT_PROVISIONED = 'P'; 
	public static final char PAYMENT_NOTPROVISIONED = 'N'; 

	public static final String ACCESS_CHANGESTATUS = "ODYCHS";
	public static final String ACCESS_VIEWAMOUNT = "ODYVAMOUNT";
	public static String ACTION_WHBOX = "ODLBOX";

	public static String CODE_PREFIX = "PL-";

	public BmoOrderDelivery() {
		super("com.flexwm.server.op.PmOrderDelivery", "orderdeliveries", "orderdeliveryid", "ODLY", "Envío Pedidos");

		// Campo de datos
		code = setField("code", "", "Clave Envío", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Envío", 60, Types.VARCHAR, true, BmFieldType.STRING, false);
		deliveryDate = setField("deliverydate", "", "Fecha Envío", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);

		toWhSectionId = setField("towhsectionid", "", "Secc. Alm. Destino", 8, Types.INTEGER, true, BmFieldType.ID, false);

		type = setField("type", "" + TYPE_DELIVERY, "Tipo Envío", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_DELIVERY, "Envío", "./icons/odly_type_delivery.png"),
				new BmFieldOption(TYPE_RETURN, "Devolución", "./icons/odly_type_return.png")
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
		discount = setField("discount", "", "Descuento", 10, Types.FLOAT, true, BmFieldType.CURRENCY, false);		
		tax = setField("tax", "", "IVA", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 8, Types.VARCHAR, false, BmFieldType.STRING, false);

		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		projectId = setField("projectid", "", "Proyecto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		notes = setField("notes", "", "Notas", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		userCreate = setField("usercreate", "", "", 60, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoOrder().getCode(), 
				getBmoOrder().getName(), 
				getBmoOrder().getBmoOrderType().getType(),
				getDeliveryDate(),
				getType(),
				getStatus()
				//getPayment(),
				//getBmoOrder().getBmoCurrency().getCode()
				//getTotal()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoOrder().getCode(), 
				getTotal()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoOrder().getName(), 
				getTotal()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getBmoOrder().getCode()), 
				new BmSearchField(getBmoOrder().getName()),
				new BmSearchField(getBmoOrder().getCustomerId()),
				new BmSearchField(getName())));
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

	public BmField getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(BmField deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

	public BmField getToWhSectionId() {
		return toWhSectionId;
	}

	public void setToWhSectionId(BmField toWhSectionId) {
		this.toWhSectionId = toWhSectionId;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
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

	public BmField getProjectId() {
		return projectId;
	}

	public void setProjectId(BmField projectId) {
		this.projectId = projectId;
	}

	public BmField getNotes() {
		return notes;
	}

	public void setNotes(BmField notes) {
		this.notes = notes;
	}

	public BmField getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(BmField userCreateId) {
		this.userCreate = userCreateId;
	}
	
	
}
