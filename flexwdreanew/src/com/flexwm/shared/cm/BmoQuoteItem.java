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

import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProduct;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;

public class BmoQuoteItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, quantity, days, basePrice, price, amount, index, quoteGroupId, productId, commission, budgetItemId, areaId
	,discountApplies, discount;
	BmoProduct bmoProduct;
	BmoQuoteGroup bmoQuoteGroup;
	BmoArea bmoArea = new BmoArea();
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	public static String ACTION_CHANGEINDEX = "ACTIONCHANGEINDEX";
	
	public BmoQuoteItem() {
		super("com.flexwm.server.cm.PmQuoteItem", "quoteitems", "quoteitemid", "QOIT", "Item de Cotizaciones");

		name = setField("name", "", "Nombre", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 800, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "1", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		days = setField("days", "1", "Días", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		basePrice = setField("baseprice", "", "Precio Base", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		quoteGroupId = setField("quotegroupid", "", "Grupo Cotización", 20, Types.INTEGER, false, BmFieldType.ID, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, true, BmFieldType.ID, false);
		commission = setField("commission", "0", "Comisión", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		index = setField("index", "", "Índice", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
		
		discountApplies = setField("discountapplies", "0", "Aplica Descuento?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		discount = setField("discount", "0", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		
		bmoProduct = new BmoProduct();
		bmoQuoteGroup = new BmoQuoteGroup();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getQuantity(), 
				getBmoProduct().getCode(), 
				getBmoProduct().getName()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIndex(), BmOrder.ASC)));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getQuoteGroupId() {
		return quoteGroupId;
	}

	public void setQuoteGroupId(BmField quoteId) {
		this.quoteGroupId = quoteId;
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

	public BmoQuoteGroup getBmoQuoteGroup() {
		return bmoQuoteGroup;
	}

	public void setBmoQuoteGroup(BmoQuoteGroup bmoQuoteGroup) {
		this.bmoQuoteGroup = bmoQuoteGroup;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
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
