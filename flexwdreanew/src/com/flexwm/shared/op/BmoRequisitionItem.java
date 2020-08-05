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

import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProduct;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;

public class BmoRequisitionItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, quantity, days, price, amount, requisitionId, productId, quantityReceipt, quantityReturned, estimationItemId,areaId,budgetItemId;
	
	private BmoProduct bmoProduct = new BmoProduct();
	private BmoArea bmoArea = new BmoArea();
	private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	
	public BmoRequisitionItem() {
		super("com.flexwm.server.op.PmRequisitionItem", "requisitionitems", "requisitionitemid", "RQIT", "Item de Orden de Compra");
		
		name = setField("name", "", "Nombre", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		quantityReceipt = setField("quantityreceipt", "", "Recibido", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantityReturned = setField("quantityreturned", "", "Devuelto", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		days = setField("days", "", "Días", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionId = setField("requisitionid", "", "Orden de Compra", 20, Types.INTEGER, false, BmFieldType.ID, false);
		estimationItemId = setField("estimationitemid", "", "Item Estimación", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
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
				new BmSearchField(getBmoProduct().getCode())
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

	public BmField getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(BmField requisitionId) {
		this.requisitionId = requisitionId;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDays() {
		return days;
	}

	public void setDays(BmField days) {
		this.days = days;
	}

	/**
	 * @return the quantityReceipt
	 */
	public BmField getQuantityReceipt() {
		return quantityReceipt;
	}

	/**
	 * @param quantityReceipt the quantityReceipt to set
	 */
	public void setQuantityReceipt(BmField quantityReceipt) {
		this.quantityReceipt = quantityReceipt;
	}

	public BmField getQuantityReturned() {
		return quantityReturned;
	}

	public void setQuantityReturned(BmField quantityReturned) {
		this.quantityReturned = quantityReturned;
	}

	/**
	 * @return the estimationItemId
	 */
	public BmField getEstimationItemId() {
		return estimationItemId;
	}

	/**
	 * @param estimationItemId the estimationItemId to set
	 */
	public void setEstimationItemId(BmField estimationItemId) {
		this.estimationItemId = estimationItemId;
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
