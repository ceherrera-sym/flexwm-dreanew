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
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoOrderGroup extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, amount, isKit, image, showQuantity, showPrice, 
	showProductImage, showGroupImage, showAmount, createRaccount, index, orderId, payConditionId,showItems;
	
	public static String ACTION_PRODUCTKIT = "PRODUCTKIT";
	public static String ACTION_CHANGEINDEX = "ACTIONCHANGEINDEX";
	
	public BmoOrderGroup() {
		super("com.flexwm.server.op.PmOrderGroup", "ordergroups", "ordergroupid", "ORDG", "Grupo de Pedido");
		
		// Campo de datos
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "0", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		isKit = setField("iskit", "", "Es Kit?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		image = setField("image", "", "Imagen Grupo", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		showQuantity = setField("showquantity", "", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showPrice = setField("showprice", "", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showAmount = setField("showamount", "", "Subtotal?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showProductImage = setField("showproductimage", "", "Img. Prod.?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		showGroupImage = setField("showgroupimage", "", "Img. Grupo?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		createRaccount = setField("createraccount", "", "Crear CxC Grupo", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		index = setField("index", "", "Índice", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		orderId = setField("orderid", "", "Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		showItems = setField("showitems", "", "Mostrar Ítems?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
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

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
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

	public BmField getShowItems() {
		return showItems;
	}

	public void setShowItems(BmField showItems) {
		this.showItems = showItems;
	}

	public BmField getImage() {
		return image;
	}

	public void setImage(BmField image) {
		this.image = image;
	}

	public BmField getCreateRaccount() {
		return createRaccount;
	}

	public void setCreateRaccount(BmField createRaccount) {
		this.createRaccount = createRaccount;
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
	
}