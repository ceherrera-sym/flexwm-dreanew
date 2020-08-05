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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoWhStock extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField amount, quantity, productId, whSectionId, whTrackId;

	private BmoProduct bmoProduct = new BmoProduct();
	private BmoWhSection bmoWhSection = new BmoWhSection();
	private BmoWhTrack bmoWhTrack = new BmoWhTrack();

	public static String ACTION_STOCKQUANTITY = "STOCKQUANTITY";
	public static String ACTION_SEARCHBARCODE = "SEARCHBARCODE";

	public BmoWhStock() {
		super("com.flexwm.server.op.PmWhStock", "whstocks", "whstockid", "WHST", "Existencias");

		//Campo de Datos
		amount = setField("amount", "", "Monto", 30, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		whSectionId = setField("whsectionid", "", "Sección A.", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		productId = setField("productid", "", "Producto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		whTrackId = setField("whtrackid", "", "Rastreo", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWhSection().getBmoWarehouse().getName(),
				getBmoWhSection().getName(),
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getBmoProduct().getModel(),
				getBmoProduct().getBmoUnit().getCode(),
				getBmoWhTrack().getSerial(),
				getQuantity()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWhSection().getName(),
				getBmoProduct().getName(),
				getBmoWhTrack().getSerial(),
				getQuantity()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProduct().getCode()),
				new BmSearchField(getBmoProduct().getName()),
				new BmSearchField(getBmoWhSection().getBmoWarehouse().getName()),
				new BmSearchField(getBmoWhSection().getName()),
				new BmSearchField(getBmoWhTrack().getSerial())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoWhSection().getBmoWarehouse().getName(), BmOrder.ASC),
				new BmOrder(getKind(), getBmoWhSection().getName(), BmOrder.ASC),
				new BmOrder(getKind(), getBmoProduct().getCode(), BmOrder.ASC)
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProduct().getCode(),
				getBmoProduct().getName(),
				getBmoWhSection().getName(),
//				getBmoWhTrack().getSerial(),
				getQuantity()
				));
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
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

	public BmField getWhSectionId() {
		return whSectionId;
	}

	public void setWhSectionId(BmField whSectionId) {
		this.whSectionId = whSectionId;
	}

	public BmoWhSection getBmoWhSection() {
		return bmoWhSection;
	}

	public void setBmoWhSection(BmoWhSection bmoWhSection) {
		this.bmoWhSection = bmoWhSection;
	}

	public BmField getWhTrackId() {
		return whTrackId;
	}

	public void setWhTrackId(BmField whTrackId) {
		this.whTrackId = whTrackId;
	}

	public BmoWhTrack getBmoWhTrack() {
		return bmoWhTrack;
	}

	public void setBmoWhTrack(BmoWhTrack bmoWhTrack) {
		this.bmoWhTrack = bmoWhTrack;
	}
}
