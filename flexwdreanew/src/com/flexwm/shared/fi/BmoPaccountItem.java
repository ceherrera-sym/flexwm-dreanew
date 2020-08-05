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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;


public class BmoPaccountItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, quantity, price, amount, paccountId, requisitionReceiptItemId, budgetItemId, areaId,discount,tax,sumRetention;
	private BmoArea bmoArea = new BmoArea();
	private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	public BmoPaccountItem() {
		super("com.flexwm.server.fi.PmPaccountItem", "paccountitems", "paccountitemid", "PAIT", "Item CxP");

		name = setField("name", "", "Nombre", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		paccountId = setField("paccountid", "", "Cuenta por Pagar", 20, Types.INTEGER, false, BmFieldType.ID, false);
		requisitionReceiptItemId = setField("requisitionreceiptitemid", "", "Item Roc", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		sumRetention = setField("sumretention", "", "Retenciones", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
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

	public BmField getPaccountId() {
		return paccountId;
	}

	public void setPaccountId(BmField paccountId) {
		this.paccountId = paccountId;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getRequisitionReceiptItemId() {
		return requisitionReceiptItemId;
	}

	public void setRequisitionReceiptItemId(BmField requisitionReceiptItemId) {
		this.requisitionReceiptItemId = requisitionReceiptItemId;
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

	public BmField getDiscount() {
		return discount;
	}

	public void setDiscount(BmField discount) {
		this.discount = discount;
	}

	public BmField getTax() {
		return tax;
	}

	public void setTax(BmField tax) {
		this.tax = tax;
	}	
	
	public BmField getSumRetention() {
		return sumRetention;
	}

	public void setSumRetention(BmField sumRetention) {
		this.sumRetention = sumRetention;
	}	
	

}
