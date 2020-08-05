/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.shared.ac;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderSessionExtra extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField quantity, comments, price, amount, sessionTypeExtraId, orderId;

	BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();

	public BmoOrderSessionExtra() {
		super("com.flexwm.server.ac.PmOrderSessionExtra", "ordersessionextras", "ordersessionextraid", "ORSX", "Extras");

		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);

		sessionTypeExtraId = setField("sessiontypeextraid", "", "Extra", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(),
				getPrice()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoSessionTypeExtra().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getSessionTypeExtraId() {
		return sessionTypeExtraId;
	}

	public void setSessionTypeExtraId(BmField sessionTypeExtraId) {
		this.sessionTypeExtraId = sessionTypeExtraId;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmoSessionTypeExtra getBmoSessionTypeExtra() {
		return bmoSessionTypeExtra;
	}

	public void setBmoSessionTypeExtra(BmoSessionTypeExtra bmoSessionTypeExtra) {
		this.bmoSessionTypeExtra = bmoSessionTypeExtra;
	}

}