/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.fi;

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

/**
 * @author jhernandez
 *
 */
public class BmoInvoice extends BmObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private BmField code,name,description,dueDate, currencyId, userId, customerId, amount,tax,total,stampDatetime, folio,
	certString, cfDiseal, satSeal, orderId, status, paymentStatus;
	
	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';	
	
	public static final char PAYMENTSTATUS_REVISION = 'R'; 
	public static final char PAYMENTSTATUS_PARTIAL = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T';
	
	public BmoInvoice() {
		super("com.flexwm.server.fi.PmInvoice","invoices", "invoiceid", "INVO","Facturas");
		 
		//Campo de Datos		
		code = setField("code", "", "Clave Factura.", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre.", 30, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		dueDate = setField("duedate", "", "Fecha Pago", 12, Types.DATE, false, BmFieldType.DATE, false);		
		stampDatetime = setField("stampdatetime", "", "Fecha Timbre", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png")
				)));
		
		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_REVISION, "Estatus Pago", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_REVISION, "Pendiente", "./icons/pacc_paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_PARTIAL, "Pago Parcial", "./icons/pacc_paymentstatus_partialpayment.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", "./icons/pacc_paymentstatus_totalpayment.png")				
				)));
		
		amount = setField("amount","", "Monto", 20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		tax = setField("tax","", "Iva", 20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		total = setField("total","", "Total", 20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		
		userId = setField("userid", "", "Creado por", 8, Types.INTEGER, true, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		folio = setField("folio", "", "Folio", 100, Types.VARCHAR, true, BmFieldType.STRING, true);
		certString = setField("certstring", "", "Certificado", 255, Types.VARCHAR, true, BmFieldType.STRING, true);
		cfDiseal = setField("cfdiseal", "", "CFDI", 255, Types.VARCHAR, true, BmFieldType.STRING, true);
		satSeal = setField("satseal", "", "SAT", 255, Types.VARCHAR, true, BmFieldType.STRING, true);
		
		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode().getName(), getCode().getLabel()), 
				new BmSearchField(getName().getName(), getName().getLabel())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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

	public BmField getDueDate() {
		return dueDate;
	}

	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
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

	public BmField getStampDatetime() {
		return stampDatetime;
	}

	public void setStampDatetime(BmField stampDatetime) {
		this.stampDatetime = stampDatetime;
	}

	public BmField getFolio() {
		return folio;
	}

	public void setFolio(BmField folio) {
		this.folio = folio;
	}

	public BmField getCertString() {
		return certString;
	}

	public void setCertString(BmField certString) {
		this.certString = certString;
	}

	public BmField getCfDiseal() {
		return cfDiseal;
	}

	public void setCfDiseal(BmField cfDiseal) {
		this.cfDiseal = cfDiseal;
	}
		

	public BmField getSatSeal() {
		return satSeal;
	}

	public void setSatSeal(BmField satSeal) {
		this.satSeal = satSeal;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}

	