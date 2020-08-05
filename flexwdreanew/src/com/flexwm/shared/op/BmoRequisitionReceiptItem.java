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
import com.flexwm.shared.fi.BmoBudgetItem;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;

public class BmoRequisitionReceiptItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, quantity, price, amount, serial, requisitionReceiptId, requisitionItemId, quantityBalance, quantityReturned, days,
	areaId, budgetItemId;
	BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
	private BmoArea bmoArea = new BmoArea();
	private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	
	public static String ACTION_SEARCHBARCODE = "SEARCHBARCODE";
	
	public BmoRequisitionReceiptItem() {
		super("com.flexwm.server.op.PmRequisitionReceiptItem", "requisitionreceiptitems", "requisitionreceiptitemid", "REIT", "Item R. O. C.");
		
		name = setField("name", "", "Nombre", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		serial = setField("serial", "", "# Serie/Lote", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		days = setField("days", "", "Dias", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);		
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);		
		requisitionItemId = setField("requisitionItemId", "", "Item O.C.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionReceiptId = setField("requisitionreceiptid", "", "Recibo O.C.", 20, Types.INTEGER, false, BmFieldType.ID, false);
		quantityBalance = setField("quantitybalance", "", "Pendiente", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		quantityReturned = setField("quantityreturned", "", "Devuelto", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);

		areaId = setField("areaid", "", "Departamento", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 8, Types.INTEGER, true, BmFieldType.ID, false);
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getRequisitionItemId() {
		return requisitionItemId;
	}

	public void setRequisitionItemId(BmField requisitionItemId) {
		this.requisitionItemId = requisitionItemId;
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
	
	/**
	 * @return the requisitionReceiptId
	 */
	public BmField getRequisitionReceiptId() {
		return requisitionReceiptId;
	}

	/**
	 * @param requisitionReceiptId the requisitionReceiptId to set
	 */
	public void setRequisitionReceiptId(BmField requisitionReceiptId) {
		this.requisitionReceiptId = requisitionReceiptId;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmoRequisitionItem getBmoRequisitionItem() {
		return bmoRequisitionItem;
	}

	public void setBmoRequisitionItem(BmoRequisitionItem bmoRequisitionItem) {
		this.bmoRequisitionItem = bmoRequisitionItem;
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

	public BmField getSerial() {
		return serial;
	}

	public void setSerial(BmField serial) {
		this.serial = serial;
	}

	public BmField getQuantityReturned() {
		return quantityReturned;
	}

	public void setQuantityReturned(BmField quantityReturned) {
		this.quantityReturned = quantityReturned;
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

}
