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

public class BmoUnitPriceHistory extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField price, date, unitPriceId, comments;
	BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();

	
	public BmoUnitPriceHistory() {
		super("com.flexwm.server.co.PmUnitPriceHistory", "unitpricehistories", "unitpricehistoryid", "UNPH", "Historial de Precios Unitarios");
		
		// Campo de datos
		price = setField("price", "", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		date = setField("date", "", "Fecha", 768, Types.DATE, true, BmFieldType.DATE, false);	
		unitPriceId = setField("unitpriceid", "", "Precio Unitario", 8, Types.INTEGER, true, BmFieldType.ID, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);	
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getPrice(), 
				getDate(),
				getComments()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getPrice()), 
				new BmSearchField(getDate()), 
				new BmSearchField(getComments())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDate(), BmOrder.ASC)));
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
	 * @return the date
	 */
	public BmField getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(BmField date) {
		this.date = date;
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
	 * @return the comments
	 */
	public BmField getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(BmField comments) {
		this.comments = comments;
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
