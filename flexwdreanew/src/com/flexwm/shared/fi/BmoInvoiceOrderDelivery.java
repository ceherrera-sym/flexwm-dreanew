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

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
/**
 * @author jhernandez
 *
 */
public class BmoInvoiceOrderDelivery extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, invoiceId, orderDeliveryId, amount;
		
	
	public BmoInvoiceOrderDelivery(){
		super("com.flexwm.server.fi.PmInvoiceOrderDelivery", "invoiceorderdeliveries", "invoiceorderdeliveryid", "INOD", "Concepto de Factura");
		
		//Campo de Datos		
		code = setField("code", "", "Clave", 10,  Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);		
		invoiceId = setField("invoiceid", "", "Factura", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		orderDeliveryId = setField("orderdeliveryid", "", "Envio", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),				
				getAmount()				
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getAmount())));
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

	public BmField getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(BmField invoiceId) {
		this.invoiceId = invoiceId;
	}

	public BmField getOrderDeliveryId() {
		return orderDeliveryId;
	}

	public void setOrderDeliveryId(BmField orderDeliveryId) {
		this.orderDeliveryId = orderDeliveryId;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

		
}
