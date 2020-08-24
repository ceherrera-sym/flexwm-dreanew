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

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoQuoteGroup extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, amount, isKit, image, showQuantity, showPrice,total, 
	showProductImage, showGroupImage, showAmount, index, quoteId, payConditionId,showItems,days, mainGroupId,
	discountApplies,discount,discountRate,feeProductionApply,feeProductionRate,commissionApply,commissionRate,feeProduction,commissionAmount
	,price;
	
	public static String ACTION_PRODUCTKIT = "productkit";
	public static String ACTION_CHANGEINDEX = "ACTIONCHANGEINDEX";

	public BmoQuoteGroup() {
		super("com.flexwm.server.cm.PmQuoteGroup", "quotegroups", "quotegroupid", "QOGR", "Grupo de Cotización");
		
		// Campo de datos
		code = setField("code", "", "Clave de Grupo", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "0", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		isKit = setField("iskit", "", "Es Kit?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		image = setField("image", "", "Imagen Grupo", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		days = setField("days", "1", "Días", 20, Types.FLOAT, true, BmFieldType.NUMBER, false);
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		discount = setField("discount", "0", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		feeProduction = setField("feeproduction", "0", "Fee Producción", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		commissionAmount = setField("comission", "0", "Comisión", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		showQuantity = setField("showquantity", "", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showPrice = setField("showprice", "", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showAmount = setField("showamount", "", "Subtotal?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showProductImage = setField("showproductimage", "", "Img. Prod.?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showGroupImage = setField("showgroupimage", "", "Img. Grupo?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showItems = setField("showitems", "", "Mostrar Ítems?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		index = setField("index", "", "Índice", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		quoteId = setField("quoteid", "", "Cotización", 20, Types.INTEGER, false, BmFieldType.ID, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		mainGroupId = setField("maingroupid", "", "Grupo", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		discountApplies = setField("discountapplies", "0", "Aplica Descuento?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		discountRate = setField("discountrate", "0", "Descuento %", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);
		feeProductionApply = setField("feeproductionapply", "0", "Fee Producción?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		feeProductionRate = setField("feeproductionrate", "0", "Fee Producción %", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);
		commissionApply = setField("comissionapply", "0", "Comisión?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		commissionRate = setField("commissionrate", "0", "Comisión%", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);
		price = setField("price", "0", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getCode(), 
						getName(), 
						getDescription()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIndex(), BmOrder.ASC)));
	}
	
	public BmField getShowItems() {
		return showItems;
	}

	public void setShowItems(BmField showItems) {
		this.showItems = showItems;
	}

	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
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

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getIsKit() {
		return isKit;
	}

	public void setIsKit(BmField isKit) {
		this.isKit = isKit;
	}

	public BmField getShowQuantity() {
		return showQuantity;
	}

	public void setShowQuantity(BmField showQuantity) {
		this.showQuantity = showQuantity;
	}

	public BmField getShowPrice() {
		return showPrice;
	}

	public void setShowPrice(BmField showPrice) {
		this.showPrice = showPrice;
	}

	public BmField getImage() {
		return image;
	}

	public void setImage(BmField image) {
		this.image = image;
	}

	public BmField getShowProductImage() {
		return showProductImage;
	}

	public void setShowProductImage(BmField showProductImage) {
		this.showProductImage = showProductImage;
	}

	public BmField getShowGroupImage() {
		return showGroupImage;
	}

	public void setShowGroupImage(BmField showGroupImage) {
		this.showGroupImage = showGroupImage;
	}

	public BmField getShowAmount() {
		return showAmount;
	}

	public void setShowAmount(BmField showAmount) {
		this.showAmount = showAmount;
	}

	public BmField getIndex() {
		return index;
	}

	public void setIndex(BmField index) {
		this.index = index;
	}
	
	public BmField getPayConditionId() {
		return payConditionId;
	}

	public void setPayConditionId(BmField payConditionId) {
		this.payConditionId = payConditionId;
	}

	public BmField getDays() {
		return days;
	}

	public void setDays(BmField days) {
		this.days = days;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	public BmField getMainGroupId() {
		return mainGroupId;
	}

	public void setMainGroupId(BmField mainGroupId) {
		this.mainGroupId = mainGroupId;
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

	public BmField getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BmField discountRate) {
		this.discountRate = discountRate;
	}

	public BmField getFeeProductionApply() {
		return feeProductionApply;
	}

	public void setFeeProductionApply(BmField feeProductionApply) {
		this.feeProductionApply = feeProductionApply;
	}

	public BmField getFeeProductionRate() {
		return feeProductionRate;
	}

	public void setFeeProductionRate(BmField feeProductionRate) {
		this.feeProductionRate = feeProductionRate;
	}

	public BmField getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BmField commissionRate) {
		this.commissionRate = commissionRate;
	}

	public BmField getCommissionApply() {
		return commissionApply;
	}

	public void setCommissionApply(BmField commissionApply) {
		this.commissionApply = commissionApply;
	}

	public BmField getFeeProduction() {
		return feeProduction;
	}

	public void setFeeProduction(BmField feeProduction) {
		this.feeProduction = feeProduction;
	}

	public BmField getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BmField commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}	
	
}
