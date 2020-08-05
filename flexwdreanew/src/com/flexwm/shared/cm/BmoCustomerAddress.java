/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCity;


public class BmoCustomerAddress extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, street, number, neighborhood, zip, description, cityId, customerId;

	private BmoCity bmoCity = new BmoCity();

	public static char TYPE_WORK = 'W';
	public static char TYPE_PERSONAL = 'P';

	public BmoCustomerAddress() {
		super("com.flexwm.server.cm.PmCustomerAddress", "customeraddress", "customeraddressid", "CUAD", "Direcciones");

		// Campo de datos
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PERSONAL, "Personal", "./icons/home.png"),
				new BmFieldOption(TYPE_WORK, "Trabajo", "./icons/office.png")
				)));

		street = setField("street", "", "Calle", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		number = setField("number", "", "No", 60, Types.VARCHAR, false, BmFieldType.STRING, false);
		neighborhood = setField("neighborhood", "", "Colonia", 60, Types.VARCHAR, false, BmFieldType.STRING, false);
		zip = setField("zip", "", "C. P.", 7, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		cityId = setField("cityid", "", "Ciudad", 5, Types.INTEGER, false, BmFieldType.ID, false);			
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);	
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getType(), 
				getStreet(),
				getNumber(),
				getNeighborhood(),
				getZip(),
				getBmoCity().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getStreet().getName(), getStreet().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getStreet(), BmOrder.ASC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getStreet() {
		return street;
	}

	public void setStreet(BmField street) {
		this.street = street;
	}

	public BmField getNumber() {
		return number;
	}

	public void setNumber(BmField number) {
		this.number = number;
	}

	public BmField getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(BmField neighborhood) {
		this.neighborhood = neighborhood;
	}

	public BmField getZip() {
		return zip;
	}

	public void setZip(BmField zip) {
		this.zip = zip;
	}

	public BmField getCityId() {
		return cityId;
	}

	public void setCityId(BmField cityId) {
		this.cityId = cityId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmoCity getBmoCity() {
		return bmoCity;
	}

	public void setBmoCity(BmoCity bmoCity) {
		this.bmoCity = bmoCity;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

}
