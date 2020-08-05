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

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoWhTrack extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField serial, inQuantity, outQuantity, datemov, productId, whMovItemId;
	
	private BmoProduct bmoProduct = new BmoProduct();
	
	public BmoWhTrack() {
		super("com.flexwm.server.op.PmWhTrack", "whtracks", "whtrackid", "WHTR", "Rastreo Productos");
		
		//Campo de Datos
		serial = setField("serial", "", "#Serie/Lote", 40, Types.VARCHAR, true, BmFieldType.STRING, true);		
		inQuantity = setField("inquantity", "", "En Alm.", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		outQuantity = setField("outquantity", "", "Fuera Alm.", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		datemov = setField("datemov", "", "Fecha Ult. Mov.", 30, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);		
		productId = setField("productid", "", "Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		whMovItemId = setField("whmovitemid", "", "Entrada Almacen", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getSerial(),
				getDatemov(),
				getInQuantity(),
				getOutQuantity(),
				getBmoProduct().getTrack()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getSerial()
				));
	}
	
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProduct().getName(),
				getSerial(),
				getInQuantity(),
				getOutQuantity()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProduct().getCode()),
				new BmSearchField(getSerial())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getDatemov(), BmOrder.DESC),
				new BmOrder(getKind(), getBmoProduct().getIdField(), BmOrder.ASC),
				new BmOrder(getKind(), getSerial(), BmOrder.ASC)
				));
	}

	public BmField getSerial() {
		return serial;
	}

	public void setSerial(BmField serial) {
		this.serial = serial;
	}

	public BmField getInQuantity() {
		return inQuantity;
	}

	public void setInQuantityy(BmField inQuantity) {
		this.inQuantity = inQuantity;
	}

	public BmField getOutQuantity() {
		return outQuantity;
	}

	public void setOutQuantity(BmField outQuantity) {
		this.outQuantity = outQuantity;
	}

	public BmField getDatemov() {
		return datemov;
	}

	public void setDatemov(BmField datemov) {
		this.datemov = datemov;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
	}

	public BmField getWhMovItemId() {
		return whMovItemId;
	}

	public void setWhMovItemId(BmField whMovItemId) {
		this.whMovItemId = whMovItemId;
	}
}
