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


public class BmoWhMovItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField serial, amount, quantity, productId, whMovementId, fromWhSectionId, toWhSectionId,
			requisitionReceiptItemId, orderDeliveryItemId;
	
	private BmoProduct bmoProduct = new BmoProduct();
	private BmoWhSection bmoFromWhSection = new BmoWhSection();
	
	public static String ACTION_SEARCHBARCODE = "SEARCHBARCODE";
	
	public BmoWhMovItem() {
		super("com.flexwm.server.op.PmWhMovItem", "whmovitems", "whmovitemid", "WHMI", "Item Tx. Invent.");
		
		serial = setField("serial", "", "No. Serie", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		whMovementId = setField("whmovementid", "", "Movimiento", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		fromWhSectionId = setField("fromwhsectionid", "", "S. Alm. Origen", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		toWhSectionId = setField("towhsectionid", "", "S. Alm. Destino", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		
		productId = setField("productid", "", "Producto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionReceiptItemId = setField("requisitionreceiptitemid", "", "Item R. O. C.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderDeliveryItemId = setField("orderdeliveryitemid", "", "Item Envío P.", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(),
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getSerial(),
				getBmoProduct().getCost(),
				getAmount()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProduct().getCode())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoProduct().getIdField(), BmOrder.ASC)
				));
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmField getWhMovementId() {
		return whMovementId;
	}

	public void setWhMovementId(BmField whMovementId) {
		this.whMovementId = whMovementId;
	}

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
	}

	public BmField getSerial() {
		return serial;
	}

	public void setSerial(BmField serial) {
		this.serial = serial;
	}

	public BmField getRequisitionReceiptItemId() {
		return requisitionReceiptItemId;
	}

	public void setRequisitionReceiptItemId(BmField requisitionReceiptItemId) {
		this.requisitionReceiptItemId = requisitionReceiptItemId;
	}

	public BmField getOrderDeliveryItemId() {
		return orderDeliveryItemId;
	}

	public void setOrderDeliveryItemId(BmField orderDeliveryItemId) {
		this.orderDeliveryItemId = orderDeliveryItemId;
	}

	public BmField getFromWhSectionId() {
		return fromWhSectionId;
	}

	public void setFromWhSectionId(BmField fromWhSectionId) {
		this.fromWhSectionId = fromWhSectionId;
	}

	public BmoWhSection getBmoFromWhSection() {
		return bmoFromWhSection;
	}

	public void setBmoFromWhSection(BmoWhSection bmoFromWhSection) {
		this.bmoFromWhSection = bmoFromWhSection;
	}

	public BmField getToWhSectionId() {
		return toWhSectionId;
	}

	public void setToWhSectionId(BmField toWhSectionId) {
		this.toWhSectionId = toWhSectionId;
	}
}
