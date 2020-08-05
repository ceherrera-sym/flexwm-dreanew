/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
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
/**
 * @author smuniz
 *
 */
public class BmoComplexUnitPrice extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField parentUnitPriceId, childUnitPriceId, quantity;
	BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
	//BmoUnitPrice bmoUnitPriceP = new BmoUnitPrice();
	//BmoUnitPrice bmoUnitPriceCh = new BmoUnitPrice();
	
	public BmoComplexUnitPrice(){
		super("com.flexwm.server.co.PmComplexUnitPrice", "complexunitprices", "complexunitpriceid", "COUP", "Precios Unitarios Complejos-");
		
		//Campo de Datos		
		parentUnitPriceId = setField("parentunitpriceid", "", "Padre Precio Unitario", 8, Types.INTEGER, false, BmFieldType.ID, false);
		childUnitPriceId = setField("childunitpriceid", "", "Hijo Precio Unitario", 8, Types.INTEGER, false, BmFieldType.ID, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getParentUnitPriceId(),
				getChildUnitPriceId(),				
				getQuantity()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getParentUnitPriceId()), 
				new BmSearchField(getChildUnitPriceId()), 
				new BmSearchField(getQuantity())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}
	

	/**
	 * @return the parentUnitPriceId
	 */
	public BmField getParentUnitPriceId() {
		return parentUnitPriceId;
	}
	/**
	 * @param parentUnitPriceId the parentUnitPriceId to set
	 */
	public void setParentUnitPriceId(BmField parentUnitPriceId) {
		this.parentUnitPriceId = parentUnitPriceId;
	}
	/**
	 * @return the childUnitPriceId
	 */
	public BmField getChildUnitPriceId() {
		return childUnitPriceId;
	}
	/**
	 * @param childUnitPriceId the childUnitPriceId to set
	 */
	public void setChildUnitPriceId(BmField childUnitPriceId) {
		this.childUnitPriceId = childUnitPriceId;
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
	 * @return the bmoUnitPrice
	 */
	public BmoUnitPrice getBmoUnitPrice() {
		return bmoUnitPrice;
	}

	/**
	 * @param bmoUnitPrice the bmoUnitPriceCh to set
	 */
	public void setBmoUnitPrice(BmoUnitPrice bmoUnitPrice) {
		this.bmoUnitPrice = bmoUnitPrice;
	}
	
}
