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
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoReqPayType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;


public class BmoExternalSales extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private BmField code,externalsalesid,customerid,reference,date,
	quantity,price,total,productid;

	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoUser bmoUser = new BmoUser();
	BmoProduct bmoProduct = new BmoProduct();
	BmoTerritory bmoTerritory = new BmoTerritory();
	BmoReqPayType bmoReqPaytype = new BmoReqPayType();
	BmoArea bmoArea = new BmoArea();
	BmoProductFamily bmoproductFamily = new BmoProductFamily();	

	public static String CODE_PREFIX = "VE-";

	public BmoExternalSales() {
		super("com.flexwm.server.cm.PmExternalSales", "externalsales", "externalsalesid", "EXTS", "Ventas Externas");

		code = setField("code","","Clave",10,Types.VARCHAR, true, BmFieldType.CODE, true);
		customerid = setField("customerid","","Cliente",10,Types.INTEGER,false,BmFieldType.ID,false);
		reference = setField("extsreference","","Referencia", 30, Types.VARCHAR, true, BmFieldType.CHAR, false);		
		date = setField("date","","Fecha",12,Types.DATE, true,BmFieldType.DATE, false);	
		quantity = setField("quantity","","Cantidad",20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		productid = setField("productid", "", "Producto", 20, Types.INTEGER, true, BmFieldType.ID, false);		

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getReference(),
				getDate(),
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getQuantity(),
				getPrice(),
				getTotal()))	;
	}		

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
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

	public BmField getExternalSalesId() {
		return externalsalesid;
	}
	public void setExternalSalesId(BmField externalsalesid) {
		this.externalsalesid = externalsalesid;
	}
	public BmField getCustomerId() {
		return customerid;
	}

	public void setCustomerId(BmField customerid) {
		this.customerid = customerid;
	}

	public BmField getReference() {
		return reference;
	}

	public void setReference(BmField reference) {
		this.reference = reference;
	}

	public BmField getDate() {
		return date;
	}

	public void setDate(BmField date) {
		this.date = date;
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	public BmField getProductId() {
		return productid;
	}

	public void setProductId(BmField productid) {
		this.productid = productid;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}	
	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
	}

	public BmoTerritory getBmoTerritory() {
		return bmoTerritory;
	}

	public void setBmoTerritory(BmoTerritory bmoTerritory) {
		this.bmoTerritory = bmoTerritory;
	}

	public BmoReqPayType getBmoReqPaytype() {
		return bmoReqPaytype;
	}

	public void setBmoReqPaytype(BmoReqPayType bmoReqPaytype) {
		this.bmoReqPaytype = bmoReqPaytype;
	}

	public BmoArea getBmoArea() {
		return bmoArea;
	}

	public void setBmoArea(BmoArea bmoArea) {
		this.bmoArea = bmoArea;
	}

	public BmoProductFamily getBmoproductFamily() {
		return bmoproductFamily;
	}

	public void setBmoproductFamily(BmoProductFamily bmoproductFamily) {
		this.bmoproductFamily = bmoproductFamily;
	}


}
