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

public class BmoDevelopment extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, zip, address, landsize, isactivate,
	blueprint, logo, developmentTypeId, cityId, companyId, satic, municipalAgency, municipalAgencyWater;

	public static String CODE_PREFIX = "DE-";

	public BmoDevelopment() {
		super("com.flexwm.server.co.PmDevelopment", "developments", "developmentid", "DEVE", "Desarrollos");

		//Campo de Datos		
		code = setField("code", "", "Clave Desarrollo", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		zip = setField("zip", "", "Código Postal", 7, Types.VARCHAR, true, BmFieldType.STRING, false);
		address = setField("address", "", "Dirección", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		landsize = setField("landsize", "", "m2 Breña", 500, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		isactivate = setField("isactivate", "", "Des. Activo", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		blueprint = setField("blueprint", "", "Plano General", 512, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		logo = setField("logo", "", "Logo", 255, Types.VARCHAR, true, BmFieldType.IMAGE, false);

		developmentTypeId = setField("developmenttypeid", "", "Tipo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyId", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		cityId = setField("cityid", "", "Ciudad", 8, Types.INTEGER, false, BmFieldType.ID, false);

		satic = setField("satic", "", "SATIC", 40, Types.VARCHAR, true, BmFieldType.STRING, false);

		municipalAgency = setField("municipalagency", "", "Organismo Municipal", 8, Types.INTEGER, true, BmFieldType.ID, false);
		municipalAgencyWater = setField("municipalagencywater", "", "Org. Municipal de Agua", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getLogo(),
				getBlueprint(),
				getZip(),
				getLandSize(),
				getIsActivate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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
	 * @return the zip
	 */
	public BmField getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(BmField zip) {
		this.zip = zip;
	}

	/**
	 * @return the address
	 */
	public BmField getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(BmField address) {
		this.address = address;
	}

	/**
	 * @return the landsize
	 */
	public BmField getLandSize() {
		return landsize;
	}

	/**
	 * @param landsize the landsize to set
	 */
	public void setLandSize(BmField landsize) {
		this.landsize = landsize;
	}

	/**
	 * @return the isactivate
	 */
	public BmField getIsActivate() {
		return isactivate;
	}

	/**
	 * @param isactivate the isactivate to set
	 */
	public void setIsActivate(BmField isactivate) {
		this.isactivate = isactivate;
	}

	/**
	 * @return the developmentTypeId
	 */
	public BmField getDevelopmentTypeId() {
		return developmentTypeId;
	}

	/**
	 * @param developmentTypeId the developmentTypeId to set
	 */
	public void setDevelopmentTypeId(BmField developmentTypeId) {
		this.developmentTypeId = developmentTypeId;
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


	public BmField getLogo() {
		return logo;
	}


	public void setLogo(BmField logo) {
		this.logo = logo;
	}


	public BmField getBlueprint() {
		return blueprint;
	}


	public void setBlueprint(BmField blueprint) {
		this.blueprint = blueprint;
	}


	/**
	 * @return the satic
	 */
	public BmField getSatic() {
		return satic;
	}


	/**
	 * @param satic the satic to set
	 */
	public void setSatic(BmField satic) {
		this.satic = satic;
	}


	public BmField getCompanyId() {
		return companyId;
	}


	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getMunicipalAgency() {
		return municipalAgency;
	}

	public void setMunicipalAgency(BmField municipalAgency) {
		this.municipalAgency = municipalAgency;
	}

	public BmField getMunicipalAgencyWater() {
		return municipalAgencyWater;
	}

	public void setMunicipalAgencyWater(BmField municipalAgencyWater) {
		this.municipalAgencyWater = municipalAgencyWater;
	}

}
