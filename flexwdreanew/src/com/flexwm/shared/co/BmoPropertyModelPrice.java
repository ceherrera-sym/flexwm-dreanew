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

public class BmoPropertyModelPrice extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField price, meterprice, publicmeterprice, constructionmeterprice, startdate, propertymodelid;

	public BmoPropertyModelPrice(){
		super("com.flexwm.server.co.PmPropertyModelPrice", "propertymodelprices", "propertymodelpriceid", "PRMP", "Precios Modelos");

		//Campo de Datos
		price = setField("price", "", "Precio ", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		meterprice = setField("meterprice", "", "$m2 T. Privativo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		publicmeterprice = setField("publicmeterprice", "", "$m2. T. Comunal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		constructionmeterprice = setField("constructionmeterprice", "", "$m2 Constr.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		startdate = setField("startdate", "", "Vigencia", 20, Types.DATE, true, BmFieldType.DATE, false);
		propertymodelid = setField("propertymodelid", "", "Modelo", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}


	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getStartDate(),
				getMeterPrice(),
				getPublicMeterPrice(),
				getConstructionMeterPrice(),
				getPrice()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getPrice()), 
				new BmSearchField(getStartDate())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getStartDate(), BmOrder.ASC)));
	}

	/*
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}*/


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
	 * @return the meterprice
	 */
	public BmField getMeterPrice() {
		return meterprice;
	}


	/**
	 * @param meterprice the meterprice to set
	 */
	public void setMeterPrice(BmField meterprice) {
		this.meterprice = meterprice;
	}


	/**
	 * @return the publicmeterprice
	 */
	public BmField getPublicMeterPrice() {
		return publicmeterprice;
	}


	/**
	 * @param publicmeterprice the publicmeterprice to set
	 */
	public void setPublicMeterPrice(BmField publicmeterprice) {
		this.publicmeterprice = publicmeterprice;
	}


	/**
	 * @return the constructionmeterprice
	 */
	public BmField getConstructionMeterPrice() {
		return constructionmeterprice;
	}


	/**
	 * @param constructionmeterprice the constructionmeterprice to set
	 */
	public void setConstructionMeterPrice(BmField constructionmeterprice) {
		this.constructionmeterprice = constructionmeterprice;
	}


	/**
	 * @return the startdate
	 */
	public BmField getStartDate() {
		return startdate;
	}


	/**
	 * @param startdate the startdate to set
	 */
	public void setStartDate(BmField startdate) {
		this.startdate = startdate;
	}


	/**
	 * @return the propertymodelid
	 */
	public BmField getPropertyModelId() {
		return propertymodelid;
	}


	/**
	 * @param propertymodelid the propertymodelid to set
	 */
	public void setPropertyModelId(BmField propertymodelid) {
		this.propertymodelid = propertymodelid;
	}

}
