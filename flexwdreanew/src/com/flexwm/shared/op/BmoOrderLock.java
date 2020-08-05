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
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderLock extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, lockStart, lockEnd, quantity, orderId, productId, requisitionId, whSectionId;

	BmoOrder bmoOrder = new BmoOrder();
	BmoProduct bmoProduct = new BmoProduct();
	BmoRequisition bmoRequisition = new BmoRequisition();
	BmoWhSection bmoWhSection = new BmoWhSection();

	public static char TYPE_WHSECTION = 'W';
	public static char TYPE_REQUISITION = 'R';

	public BmoOrderLock() {
		super("com.flexwm.server.op.PmOrderLock", "orderlocks", "orderlockid", "ORLK", "Bloqueo de Productos");

		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WHSECTION, "S. Almacén", "./icons/orlk_type_whsection.png"),
				new BmFieldOption(TYPE_REQUISITION, "O. Compra", "./icons/orlk_type_requisition.png")
				)));

		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);

		orderId = setField("orderid", "", "Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, false, BmFieldType.ID, false);
		requisitionId = setField("requisitionid", "", "Orden de Compra", 20, Types.INTEGER, true, BmFieldType.ID, false);
		whSectionId = setField("whsectionid", "", "S. Almacén", 20, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(), 
				getBmoProduct().getCode(), 
				getBmoProduct().getName(),
				getType(),
				getBmoWhSection().getName(),
				getBmoRequisition().getCode()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
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

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmField getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(BmField requisitionId) {
		this.requisitionId = requisitionId;
	}

	public BmField getWhSectionId() {
		return whSectionId;
	}

	public void setWhSectionId(BmField whSectionId) {
		this.whSectionId = whSectionId;
	}

	public BmoProduct getBmoProduct() {
		return bmoProduct;
	}

	public void setBmoProduct(BmoProduct bmoProduct) {
		this.bmoProduct = bmoProduct;
	}

	public BmoRequisition getBmoRequisition() {
		return bmoRequisition;
	}

	public void setBmoRequisition(BmoRequisition bmoRequisition) {
		this.bmoRequisition = bmoRequisition;
	}

	public BmoWhSection getBmoWhSection() {
		return bmoWhSection;
	}

	public void setBmoWhSection(BmoWhSection bmoWhSection) {
		this.bmoWhSection = bmoWhSection;
	}

	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}
}
