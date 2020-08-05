/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;


public class BmoRaccountItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, quantity, price, amount, raccountId,orderEquipmentId, orderStaffId, orderItemId, budgetItemId, areaId;
	private BmoArea bmoArea = new BmoArea();
	private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	public BmoRaccountItem() {
		super("com.flexwm.server.fi.PmRaccountItem", "raccountitems", "raccountitemid", "RAIT", "Item de Cuentas por Cobrar");

		// Campo de datos
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		quantity = setField("quantity", "1", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		price = setField("price", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Total", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		raccountId = setField("raccountid", "", "Cuenta por Cobrar", 20, Types.INTEGER, false, BmFieldType.ID, false);
		orderItemId = setField("orderitemid", "", "Item Productos", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderEquipmentId = setField("orderEquipmentId", "", "Item Recursos", 20, Types.INTEGER, true, BmFieldType.ID, false);
		orderStaffId = setField("orderstaffid", "", "Item Staff", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
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

	public BmField getRaccountId() {
		return raccountId;
	}

	public void setRaccountId(BmField RaccountId) {
		this.raccountId = RaccountId;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getOrderEquipmentId() {
		return orderEquipmentId;
	}

	public void setOrderEquipmentId(BmField orderEquipmentId) {
		this.orderEquipmentId = orderEquipmentId;
	}

	public BmField getOrderStaffId() {
		return orderStaffId;
	}

	public void setOrderStaffId(BmField orderStaffId) {
		this.orderStaffId = orderStaffId;
	}

	public BmField getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(BmField orderItemId) {
		this.orderItemId = orderItemId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
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

}
