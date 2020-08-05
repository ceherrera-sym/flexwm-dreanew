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

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoOrderDeliveryItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, quantity, price, amount, serial, orderDeliveryId, orderItemId, orderCreditId, productId, 
	fromWhSectionId, toWhSectionId, quantityBalance, quantityReturned, days;
	
	BmoOrderItem bmoOrderItem = new BmoOrderItem();
	BmoProduct bmoProduct = new BmoProduct();
	private BmoWhSection bmoFromWhSection = new BmoWhSection();
	
	public static String ACTION_SEARCHBARCODE = "SEARCHBARCODE";
	
	public BmoOrderDeliveryItem() {
		super("com.flexwm.server.op.PmOrderDeliveryItem", "orderdeliveryitems", "orderdeliveryitemid", "ODYI", "Item Envío Ped.");
		
		name = setField("name", "", "Nombre", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		serial = setField("serial", "", "# Serie", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		days = setField("days", "", "Dias", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Costo", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderDeliveryId = setField("orderdeliveryid", "", "Envío Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		quantityBalance = setField("quantitybalance", "", "Pendiente", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantityReturned = setField("quantityreturned", "", "Devuelto", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		fromWhSectionId = setField("fromwhsectionid", "", "S. Alm. Origen", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		toWhSectionId = setField("towhsectionid", "", "S. Alm. Destino", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderItemId = setField("orderitemid", "", "Item Pedido", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderCreditId = setField("ordercreditid", "", "Item Credito", 20, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getQuantity(), 
						getName()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(),getBmoProduct().getType(),BmOrder.ASC),
				new BmOrder(getKind(), getName(), BmOrder.ASC)));
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

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	/**
	 * @return the quantityBalance
	 */
	public BmField getQuantityBalance() {
		return quantityBalance;
	}

	/**
	 * @param quantityBalance the quantityBalance to set
	 */
	public void setQuantityBalance(BmField quantityBalance) {
		this.quantityBalance = quantityBalance;
	}

	/**
	 * @return the days
	 */
	public BmField getDays() {
		return days;
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(BmField days) {
		this.days = days;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmField getSerial() {
		return serial;
	}

	public void setSerial(BmField serial) {
		this.serial = serial;
	}

	public BmField getOrderDeliveryId() {
		return orderDeliveryId;
	}

	public void setOrderDeliveryId(BmField orderDeliveryId) {
		this.orderDeliveryId = orderDeliveryId;
	}

	public BmField getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(BmField orderItemId) {
		this.orderItemId = orderItemId;
	}

	public BmoOrderItem getBmoOrderItem() {
		return bmoOrderItem;
	}

	public void setBmoOrderItem(BmoOrderItem bmoOrderItem) {
		this.bmoOrderItem = bmoOrderItem;
	}

	public BmField getQuantityReturned() {
		return quantityReturned;
	}

	public void setQuantityReturned(BmField quantityReturned) {
		this.quantityReturned = quantityReturned;
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

	public BmField getOrderCreditId() {
		return orderCreditId;
	}

	public void setOrderCreditId(BmField orderCreditId) {
		this.orderCreditId = orderCreditId;
	}

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
	}		
}
