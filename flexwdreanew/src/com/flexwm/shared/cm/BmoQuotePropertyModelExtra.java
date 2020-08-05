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

import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoQuotePropertyModelExtra extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField quantity, comments, price, amount, propertyModelExtraId, quoteId;
	
	BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
	
	public static String ACTION_GETPROPERTYMODEL = "QUPX_GETPROPERTYMODEL";
	
	public BmoQuotePropertyModelExtra() {
		super("com.flexwm.server.cm.PmQuotePropertyModelExtra", "quotepropertymodelextras", "quotepropertymodelextraid", "QUPX", "Extras de la Propiedad");
		
		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		propertyModelExtraId = setField("propertymodelextraid", "", "Grupo", 20, Types.INTEGER, true, BmFieldType.ID, false);
		quoteId = setField("quoteid", "", "Cotización", 20, Types.INTEGER, false, BmFieldType.ID, false);
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
				new BmSearchField(getBmoPropertyModelExtra().getCode())
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

	public BmField getPropertyModelExtraId() {
		return propertyModelExtraId;
	}

	public void setPropertyModelExtraId(BmField propertyModelExtraId) {
		this.propertyModelExtraId = propertyModelExtraId;
	}

	public BmoPropertyModelExtra getBmoPropertyModelExtra() {
		return bmoPropertyModelExtra;
	}

	public void setBmoPropertyModelExtra(BmoPropertyModelExtra bmoPropertyModelExtra) {
		this.bmoPropertyModelExtra = bmoPropertyModelExtra;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}
}
