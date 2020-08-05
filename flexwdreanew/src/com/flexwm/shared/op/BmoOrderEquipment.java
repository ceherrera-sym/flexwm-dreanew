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
import com.flexwm.shared.op.BmoEquipment;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderEquipment extends BmObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private BmField name, description, quantity, days, basePrice, price, amount, 
	lockStatus, lockStart, lockEnd, equipmentId, orderId, baseCost;
	BmoEquipment bmoEquipment = new BmoEquipment();

	public static char LOCKSTATUS_OPEN = 'O';
	public static char LOCKSTATUS_LOCKED = 'L';
	public static char LOCKSTATUS_CONFLICT = 'C';

	public static String ACTION_CHANGEORDEREQUIPMENT = "CHANGEORDEREQUIPMENT";

	public BmoOrderEquipment() {
		super("com.flexwm.server.op.PmOrderEquipment", "orderequipments", "orderequipmentid", "ORDQ", "Recurso del Pedido");

		name = setField("name", "", "Nombre", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 250, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);
		days = setField("days", "", "Días", 20, Types.FLOAT, false, BmFieldType.NUMBER, false);
		basePrice = setField("baseprice", "", "Precio Base", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		baseCost = setField("basecost", "", "Costo Base", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);

		lockStart = setField("lockstart", "", "Fecha Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		lockEnd = setField("lockend", "", "Fecha Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		lockStatus = setField("lockstatus", "" + LOCKSTATUS_OPEN, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		lockStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(LOCKSTATUS_OPEN, "Abierto", "./icons/lockstatus_open.png"),
				new BmFieldOption(LOCKSTATUS_LOCKED, "Apartado", "./icons/lockstatus_locked.png"),
				new BmFieldOption(LOCKSTATUS_CONFLICT, "Conflicto", "./icons/lockstatus_conflict.png")
				)));

		equipmentId = setField("equipmentid", "", "Recurso", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 20, Types.INTEGER, true, BmFieldType.ID, false);
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
				getName(),
				getQuantity(), 
				getBmoEquipment().getCode(), 
				getBmoEquipment().getName()
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

	public BmField getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(BmField lockStatus) {
		this.lockStatus = lockStatus;
	}

	public BmField getLockStart() {
		return lockStart;
	}

	public void setLockStart(BmField lockStart) {
		this.lockStart = lockStart;
	}

	public BmField getLockEnd() {
		return lockEnd;
	}

	public void setLockEnd(BmField lockEnd) {
		this.lockEnd = lockEnd;
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

	public BmField getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(BmField baseCost) {
		this.baseCost = baseCost;
	}
}
