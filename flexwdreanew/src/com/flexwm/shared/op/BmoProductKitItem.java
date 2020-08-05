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

public class BmoProductKitItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField quantity, days, productKitId, productId;
	BmoProduct bmoProduct;

	public BmoProductKitItem() {
		super("com.flexwm.server.op.PmProductKitItem", "productkititems", "productkititemid", "PRKI", "Item de Kit");

		// Campo de datos
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		days = setField("days", "", "Días", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		productKitId = setField("productkitid", "", "Tipo Kit", 20, Types.INTEGER, false, BmFieldType.ID, false);
		productId = setField("productid", "", "Producto", 20, Types.INTEGER, false, BmFieldType.ID, false);

		bmoProduct = new BmoProduct();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProduct().getCode(), 
				getBmoProduct().getName(),
				getQuantity()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoProduct().getCode(), BmOrder.ASC)));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getProductKitId() {
		return productKitId;
	}

	public void setProductKitId(BmField productKitId) {
		this.productKitId = productKitId;
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

	public BmField getDays() {
		return days;
	}

	public void setDays(BmField days) {
		this.days = days;
	}
}
