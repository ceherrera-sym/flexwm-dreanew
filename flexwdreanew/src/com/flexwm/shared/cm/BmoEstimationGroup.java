///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.shared.cm;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//
//public class BmoEstimationGroup extends BmObject implements Serializable {
//	private static final long serialVersionUID = 1L;
//	private BmField code, estimationId, name, description, totalQuantity,  amount, isKit, image, showQuantity, showPrice,
//			showProductImage, showGroupImage, showAmount, index;
//
//	public static String ACTION_PRODUCTKIT = "productkit";
//	public static String ACTION_CHANGEINDEX = "ACTIONCHANGEINDEX";
//
//	public BmoEstimationGroup() {
//		super("com.flexwm.server.cm.PmEstimationGroup", "estimationgroups", "estimationgroupid", "ESGP",
//				"Grupo de Estimación");
//
//		// Campo de datos
//		code = setField("code", "", "Clave de Grupo", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
//		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
//		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
//		amount = setField("amount", "0", "Subtotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
//		isKit = setField("iskit", "", "Es Kit?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
//		image = setField("image", "", "Imagen Grupo", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
//		totalQuantity= setField("quantity", "", "Total", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
//		showQuantity = setField("showquantity", "", "Cantidad?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
//		showPrice = setField("showprice", "", "Precio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
//		showAmount = setField("showamount", "", "Subtotal?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
//		showProductImage = setField("showproductimage", "", "Img. Prod.?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN,
//				false);
//
//		index = setField("index", "", "Índice", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
//
//		estimationId = setField("estimationid", "", "Cotización", 20, Types.INTEGER, false, BmFieldType.ID, false);
//	}
//
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(getCode(), getName(), getDescription()));
//	}
//
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(new BmSearchField(getName())));
//	}
//
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIndex(), BmOrder.ASC)));
//	}
//
//	public BmField getCode() {
//		return code;
//	}
//
//	public void setCode(BmField code) {
//		this.code = code;
//	}
//
//	public BmField getName() {
//		return name;
//	}
//
//	public void setName(BmField name) {
//		this.name = name;
//	}
//
//	public BmField getDescription() {
//		return description;
//	}
//
//	public void setDescription(BmField description) {
//		this.description = description;
//	}
//
//	public BmField getAmount() {
//		return amount;
//	}
//
//	public void setAmount(BmField amount) {
//		this.amount = amount;
//	}
//
//	public BmField getIsKit() {
//		return isKit;
//	}
//
//	public void setIsKit(BmField isKit) {
//		this.isKit = isKit;
//	}
//
//	public BmField getShowQuantity() {
//		return showQuantity;
//	}
//
//	public void setShowQuantity(BmField showQuantity) {
//		this.showQuantity = showQuantity;
//	}
//
//	public BmField getShowPrice() {
//		return showPrice;
//	}
//
//	public void setShowPrice(BmField showPrice) {
//		this.showPrice = showPrice;
//	}
//
//	public BmField getImage() {
//		return image;
//	}
//
//	public void setImage(BmField image) {
//		this.image = image;
//	}
//
//	public BmField getShowProductImage() {
//		return showProductImage;
//	}
//
//	public void setShowProductImage(BmField showProductImage) {
//		this.showProductImage = showProductImage;
//	}
//
//	public BmField getShowGroupImage() {
//		return showGroupImage;
//	}
//
//	public void setShowGroupImage(BmField showGroupImage) {
//		this.showGroupImage = showGroupImage;
//	}
//
//	public BmField getShowAmount() {
//		return showAmount;
//	}
//
//	public void setShowAmount(BmField showAmount) {
//		this.showAmount = showAmount;
//	}
//
//	public BmField getIndex() {
//		return index;
//	}
//
//	public void setIndex(BmField index) {
//		this.index = index;
//	}
//
//	public BmField getEstimationId() {
//		return estimationId;
//	}
//
//	public void setEstimationId(BmField estimationId) {
//		this.estimationId = estimationId;
//	}
//
//	public BmField getTotalQuantity() {
//		return totalQuantity;
//	}
//
//	public void setTotalQuantity(BmField totalQuantity) {
//		this.totalQuantity = totalQuantity;
//	}
//	
//
//}
