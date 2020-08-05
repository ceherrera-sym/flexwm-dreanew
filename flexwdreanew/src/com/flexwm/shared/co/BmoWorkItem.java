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


public class BmoWorkItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, quantity, unitPriceId, workId, amount, price, description;

	BmoWork bmoWork = new BmoWork();
	BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();

	public BmoWorkItem() {
		super("com.flexwm.server.co.PmWorkItem", "workitems", "workitemid", "WKIT", "Items Obra");

		// Campo de datos		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);		
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		amount = setField("amount", "", "SubTotal", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		unitPriceId = setField("unitpriceid", "", "Precio Unitario", 20, Types.INTEGER, true, BmFieldType.ID, false);		
		workId = setField("workid", "", "Obra", 20, Types.INTEGER, false, BmFieldType.ID, false);				
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(), 
				getCode()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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
	 * @return the workId
	 */
	public BmField getWorkId() {
		return workId;
	}

	/**
	 * @param workId the workId to set
	 */
	public void setWorkId(BmField workId) {
		this.workId = workId;
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
	 * @return the price
	 */
	public BmField getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(BmField price) {
		this.price = price;
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the bmoWork
	 */
	public BmoWork getBmoWork() {
		return bmoWork;
	}

	/**
	 * @param bmoWork the bmoWork to set
	 */
	public void setBmoWork(BmoWork bmoWork) {
		this.bmoWork = bmoWork;
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


}
