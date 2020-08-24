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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;


public class BmoOrderItem extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField name, description, quantity, lockedQuantity, conflictQuantity, days, basePrice, price, amount, 
	quantityDelivered, quantityReturned, lockStatus, orderGroupId, productId, baseCost, commission, index, budgetItemId, areaId,
	orderItemComposedId, discountApplies, discount;
	BmoProduct bmoProduct = new BmoProduct();
	BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
	BmoArea bmoArea = new BmoArea();
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	public static char LOCKSTATUS_OPEN = 'O';
	public static char LOCKSTATUS_LOCKED = 'L';
	public static char LOCKSTATUS_CONFLICT = 'C';

	public static String ACTION_CHANGEORDERITEM = "CHANGEORDERITEM";
	public static String ACTION_CHANGEINDEX = "ACTIONCHANGEINDEX";

	public BmoOrderItem() {
		super("com.flexwm.server.op.PmOrderItem", "orderitems", "orderitemid", "ORDI", "Item de Pedidos");

		name = setField("name", "", "Nombre", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 800, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "1", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		lockedQuantity = setField("lockedquantity", "", "C. Bloqueada", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		conflictQuantity = setField("conflictquantity", "", "C. Conflicto", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		days = setField("days", "1", "Días", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);		
		basePrice = setField("baseprice", "", "Precio Base", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		baseCost = setField("basecost", "", "Costo Base", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		quantityDelivered = setField("quantitydelivered", "", "Enviado", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantityReturned = setField("quantityreturned", "", "Devuelto", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);

		lockStatus = setField("lockstatus", "", "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		lockStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(LOCKSTATUS_OPEN, "Abierto", "./icons/lockstatus_open.png"),
				new BmFieldOption(LOCKSTATUS_LOCKED, "Apartado", "./icons/lockstatus_locked.png"),
				new BmFieldOption(LOCKSTATUS_CONFLICT, "Conflicto", "./icons/lockstatus_conflict.png")
				)));
		index = setField("index", "", "Índice", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		orderGroupId = setField("ordergroupid", "", "Grupo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		orderItemComposedId = setField("orderitemcomposedid", "", "Grupo Prod. Compuesto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		commission = setField("commission", "0", "Comisión", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
		
		discountApplies = setField("discountapplies", "0", "Aplica Descuento?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		discount = setField("discount", "0", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(), 
				getBmoProduct().getCode(), 
				getBmoProduct().getName(),						
				getPrice(),
				getDays(),
				getAmount(),
				getLockStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProduct().getCode()),
				new BmSearchField(getBmoProduct().getName()),
				new BmSearchField(getBmoProduct().getModel())
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(), 
				getBmoProduct().getCode(), 
				getBmoProduct().getName()
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIndex(), BmOrder.ASC)));
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

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
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

	public BmField getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(BmField lockStatus) {
		this.lockStatus = lockStatus;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getOrderGroupId() {
		return orderGroupId;
	}

	public void setOrderGroupId(BmField orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public BmoOrderGroup getBmoOrderGroup() {
		return bmoOrderGroup;
	}

	public void setBmoOrderGroup(BmoOrderGroup bmoOrderGroup) {
		this.bmoOrderGroup = bmoOrderGroup;
	}

	public BmField getQuantityDelivered() {
		return quantityDelivered;
	}

	public void setQuantityDelivered(BmField quantityDelivered) {
		this.quantityDelivered = quantityDelivered;
	}

	public BmField getQuantityReturned() {
		return quantityReturned;
	}

	public void setQuantityReturned(BmField quantityReturned) {
		this.quantityReturned = quantityReturned;
	}

	public BmField getLockedQuantity() {
		return lockedQuantity;
	}

	public void setLockedQuantity(BmField lockedQuantity) {
		this.lockedQuantity = lockedQuantity;
	}

	public BmField getConflictQuantity() {
		return conflictQuantity;
	}

	public void setConflictQuantity(BmField conflictQuantity) {
		this.conflictQuantity = conflictQuantity;
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

	public BmField getCommission() {
		return commission;
	}

	public void setCommission(BmField commission) {
		this.commission = commission;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmoArea getBmoArea() {
		return bmoArea;
	}

	public void setBmoArea(BmoArea bmoArea) {
		this.bmoArea = bmoArea;
	}

	public BmoBudgetItem getBmoBudgetItem() {
		return bmoBudgetItem;
	}

	public void setBmoBudgetItem(BmoBudgetItem bmoBudgetItem) {
		this.bmoBudgetItem = bmoBudgetItem;
	}

	public BmField getOrderItemComposedId() {
		return orderItemComposedId;
	}

	public void setOrderItemComposedId(BmField orderItemComposedId) {
		this.orderItemComposedId = orderItemComposedId;
	}

	public BmField getIndex() {
		return index;
	}

	public void setIndex(BmField index) {
		this.index = index;
	}

	public BmField getDiscountApplies() {
		return discountApplies;
	}

	public void setDiscountApplies(BmField discountApplies) {
		this.discountApplies = discountApplies;
	}

	public BmField getDiscount() {
		return discount;
	}

	public void setDiscount(BmField discount) {
		this.discount = discount;
	}
	
	

}
