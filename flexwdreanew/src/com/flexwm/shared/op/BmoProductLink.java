/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author smuniz
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoProductLink extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField productId, productLinkedId;
	private BmoProduct bmoProductLinked = new BmoProduct();
	
	public BmoProductLink() {
		super("com.flexwm.server.op.PmProductLink", "productlinks", "productlinkid", "PRLK", "Sub Productos");

		productLinkedId = setField("productlinkedid", "", "Sub Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		productId = setField("productid", "", "Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProductLinked().getCode(),
				getBmoProductLinked().getName(),
				getBmoProductLinked().getModel()
		));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProductLinked().getCode()),
				new BmSearchField(getBmoProductLinked().getName()),
				new BmSearchField(getBmoProductLinked().getBrand()),
				new BmSearchField(getBmoProductLinked().getModel()),
				new BmSearchField(getBmoProductLinked().getDescription())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoProductLinked().getCode(), BmOrder.ASC),
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmField getProductLinkedId() {
		return productLinkedId;
	}

	public void setProductLinkedId(BmField productLinkedId) {
		this.productLinkedId = productLinkedId;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmoProduct getBmoProductLinked() {
		return bmoProductLinked;
	}

	public void setBmoProductLinked(BmoProduct bmoProductLinked) {
		this.bmoProductLinked = bmoProductLinked;
	}
}
