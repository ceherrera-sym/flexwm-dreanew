/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.shared.co;

import java.io.Serializable;


import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoUnitPriceItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField unitPriceId, quantity, amount, total, unitPriceParentId, code;
	BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
	
	public BmoUnitPriceItem() {
		super("com.flexwm.server.co.PmUnitPriceItem", "unitpriceitems", "unitpriceitemid", "UNPI", "Item de Precios Unitarios");
		
		// Campo de datos
		code = setField("code", "", "Clave", 20, Types.VARCHAR, false, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		unitPriceParentId = setField("unitpriceparentid", "", "PU Padre", 20, Types.INTEGER, false, BmFieldType.ID, false);
		unitPriceId = setField("unitpriceid", "", "Precios Unitarios", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getQuantity()
						
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getQuantity())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getQuantity(), BmOrder.ASC)));
	}

	/**
	 * @return the unitPriceId
	 */
	public BmField getUnitPriceId() {
		return unitPriceId;
	}

	/**
	 * @param unitPriceId the unitPriceId to set
	 */
	public void setUnitPriceId(BmField unitPriceId) {
		this.unitPriceId = unitPriceId;
	}

	/**
	 * @return the quantity
	 */
	public BmField getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the amount
	 */
	public BmField getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	/**
	 * @return the unitPriceParentId
	 */
	public BmField getUnitPriceParentId() {
		return unitPriceParentId;
	}

	/**
	 * @param unitPriceParentId the unitPriceParentId to set
	 */
	public void setUnitPriceParentId(BmField unitPriceParentId) {
		this.unitPriceParentId = unitPriceParentId;
	}

	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}



	/**
	 * @return the bmoUnitPrice
	 */
	public BmoUnitPrice getBmoUnitPrice() {
		return bmoUnitPrice;
	}



	/**
	 * @param bmoUnitPrice the bmoUnitPrice to set
	 */
	public void setBmoUnitPrice(BmoUnitPrice bmoUnitPrice) {
		this.bmoUnitPrice = bmoUnitPrice;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}
	
}
