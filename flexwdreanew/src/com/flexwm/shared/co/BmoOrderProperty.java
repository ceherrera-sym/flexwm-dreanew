/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderProperty extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField price, extraLand, extraConstruction, extraOther, amount, propertyId, orderId;

	BmoProperty bmoProperty = new BmoProperty();

	public static String ACTION_CHANGEORDERSTAFF = "CHANGEORDERSTAFF";

	public BmoOrderProperty() {
		super("com.flexwm.server.co.PmOrderProperty", "orderproperties", "orderpropertyid", "ORPY", "Inmuebles Pedido");

		price = setField("price", "", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		extraLand = setField("extraLand", "", "Terreno Ex.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		extraConstruction = setField("extraconstruction", "", "Const. Ex.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		extraOther = setField("extraother", "", "Otros Ex.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		propertyId = setField("propertyid", "", "Inmueble", 20, Types.INTEGER, true, BmFieldType.ID, false);
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
				getBmoProperty().getCode(),
				getPrice()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProperty().getCode())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}

	public BmoProperty getBmoProperty() {
		return bmoProperty;
	}

	public void setBmoProperty(BmoProperty bmoProperty) {
		this.bmoProperty = bmoProperty;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getExtraLand() {
		return extraLand;
	}

	public void setExtraLand(BmField extraLand) {
		this.extraLand = extraLand;
	}

	public BmField getExtraConstruction() {
		return extraConstruction;
	}

	public void setExtraConstruction(BmField extraConstruction) {
		this.extraConstruction = extraConstruction;
	}

	public BmField getExtraOther() {
		return extraOther;
	}

	public void setExtraOther(BmField extraOther) {
		this.extraOther = extraOther;
	}
}
