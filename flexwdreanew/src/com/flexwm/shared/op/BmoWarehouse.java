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
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author jhernandez
 *
 */

public class BmoWarehouse extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField userId, type, name, description, street, number, 
	neighborhood, cityId, officePhone, companyId;
	private BmoCompany bmoCompany;

	public static final char TYPE_NORMAL = 'N';
	public static final char TYPE_RENTAL = 'R';

	public BmoWarehouse() {
		super("com.flexwm.server.op.PmWarehouse","warehouses", "warehouseid", "WARE","Almacenes");

		type = setField("type", "", "Tipo Almacén", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_NORMAL, "Normal", "./icons/ware_type_normal.png"),
				new BmFieldOption(TYPE_RENTAL, "Pedido Renta", "./icons/ware_type_rental.png")
				)));
		name = setField("name", "", "Nombre Almacén", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		street = setField("street", "", "Calle", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		number = setField("number", "", "Número", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		neighborhood = setField("neighborhood", "", "Colonia", 30, Types.VARCHAR, true, BmFieldType.STRING, false);				
		officePhone = setField("officephone", "", "Teléfono", 20, Types.VARCHAR, true, BmFieldType.PHONE, false);
		cityId = setField("cityid", "", "Ciudad", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);		

		bmoCompany = new BmoCompany();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getOfficePhone(),
				getBmoCompany().getName(),
				getType()
				));
	}
	
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getOfficePhone(),
				getType()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()),
				new BmSearchField(getDescription().getName(), getDescription().getLabel())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	/**
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
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
	 * @return the street
	 */
	public BmField getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(BmField street) {
		this.street = street;
	}

	/**
	 * @return the number
	 */
	public BmField getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(BmField number) {
		this.number = number;
	}

	/**
	 * @return the neighborhood
	 */
	public BmField getNeighborhood() {
		return neighborhood;
	}

	/**
	 * @param neighborhood the neighborhood to set
	 */
	public void setNeighborhood(BmField neighborhood) {
		this.neighborhood = neighborhood;
	}

	/**
	 * @return the cityId
	 */
	public BmField getCityId() {
		return cityId;
	}

	/**
	 * @param cityId the cityId to set
	 */
	public void setCityId(BmField cityId) {
		this.cityId = cityId;
	}

	/**
	 * @return the officePhone
	 */
	public BmField getOfficePhone() {
		return officePhone;
	}

	/**
	 * @param officePhone the officePhone to set
	 */
	public void setOfficePhone(BmField officePhone) {
		this.officePhone = officePhone;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}
}
