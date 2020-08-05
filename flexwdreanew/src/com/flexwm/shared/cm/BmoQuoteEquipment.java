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
import com.flexwm.shared.op.BmoEquipment;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoQuoteEquipment extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, quantity, days, basePrice, price, amount, equipmentId, quoteId;
	BmoEquipment bmoEquipment;

	
	public BmoQuoteEquipment() {
		super("com.flexwm.server.cm.PmQuoteEquipment", "quoteequipments", "quoteequipmentid", "QOEQ", "Recurso de Cotizaciones");
		
		// Campo de datos
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);
		days = setField("days", "", "Días", 20, Types.FLOAT, true, BmFieldType.NUMBER, false);
		basePrice = setField("baseprice", "", "Precio Base", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		equipmentId = setField("equipmentid", "", "Recurso", 20, Types.INTEGER, true, BmFieldType.ID, false);
		quoteId = setField("quoteid", "", "Cotizacion", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		bmoEquipment = new BmoEquipment();
	}
	
	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getName(),
						getQuantity(), 
						getBmoEquipment().getCode(), 
						getBmoEquipment().getName()
						));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getName(),
						getBmoEquipment().getBrand(), 
						getBmoEquipment().getModel()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoEquipment().getCode())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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

	public BmField getDays() {
		return days;
	}

	public void setDays(BmField days) {
		this.days = days;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(BmField equipmentId) {
		this.equipmentId = equipmentId;
	}

	public BmoEquipment getBmoEquipment() {
		return bmoEquipment;
	}

	public void setBmoEquipment(BmoEquipment bmoEquipment) {
		this.bmoEquipment = bmoEquipment;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BmField basePrice) {
		this.basePrice = basePrice;
	}
}
