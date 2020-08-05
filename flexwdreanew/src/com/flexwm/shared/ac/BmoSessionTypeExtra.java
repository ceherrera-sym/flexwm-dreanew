/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.ac;

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
public class BmoSessionTypeExtra extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name, description, price, fixedPrice, startDate, endDate, sessionTypeId;
	
	BmoSessionType bmoSessionType = new BmoSessionType();
		
	public BmoSessionTypeExtra() {
		super("com.flexwm.server.ac.PmSessionTypeExtra", "sessiontypeextras", "sessiontypeextraid", "SETX", "Extras de Tipos Sesión");
		
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		price = setField("price", "", "Precio ", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		fixedPrice = setField("fixedprice", "", "Precio Fijo", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		
		startDate = setField("startdate", "", "Vigencia Inicio", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Vigencia Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		sessionTypeId = setField("sessiontypeid", "", "Tipo de Sesión", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getBmoSessionType().getName(),
				getStartDate(),
				getEndDate(),
				getFixedPrice(),
				getPrice()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getSessionTypeId(), BmOrder.ASC),
				new BmOrder(getKind(), getStartDate(), BmOrder.ASC),
				new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getSessionTypeId() {
		return sessionTypeId;
	}

	public void setSessionTypeId(BmField sessionTypeId) {
		this.sessionTypeId = sessionTypeId;
	}

	public BmoSessionType getBmoSessionType() {
		return bmoSessionType;
	}

	public void setBmoSessionType(BmoSessionType bmoSessionType) {
		this.bmoSessionType = bmoSessionType;
	}

	public BmField getFixedPrice() {
		return fixedPrice;
	}

	public void setFixedPrice(BmField fixedPrice) {
		this.fixedPrice = fixedPrice;
	}
	
}
