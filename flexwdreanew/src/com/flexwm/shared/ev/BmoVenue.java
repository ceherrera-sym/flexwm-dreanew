/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.ev;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCity;


public class BmoVenue extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, street, number, neighborhood, zip,
	cityId, contactName, contactEmail, contactPhone, contactPhoneExt, contactPhone2, contactPhoneExt2, 
	www, latitude, longitude, location,logo, homeAddress, delegationId,
	bluePrint;
	BmoCity bmoCity;

	public static String CODE_PREFIX = "L-";
	
	public static String ACTION_EXITSHOMEADDRESS = "EHAD";

	public BmoVenue() {
		super("com.flexwm.server.ev.PmVenue", "venues", "venueid", "VENU", "Salones / Lugares");

		// Campo de datos
		code = setField("code", "", "Clave Lugar", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre Lugar", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		street = setField("street", "", "Calle", 80, Types.VARCHAR, true, BmFieldType.STRING, false);
		number = setField("number", "", "No", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		neighborhood = setField("neighborhood", "", "Colonia", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		zip = setField("zip", "", "C. P.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		delegationId = setField("delegationid", "", "Delegación", 5, Types.INTEGER, true, BmFieldType.ID, false);		
		cityId = setField("cityid", "", "Ciudad", 5, Types.INTEGER, true, BmFieldType.ID, false);		
		contactName = setField("contactname", "", "Contacto", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		contactEmail = setField("contactemail", "", "Email", 100, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		contactPhone = setField("contactphone", "", "Teléfono", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		contactPhoneExt = setField("contactphoneext", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.PHONE, false);
		contactPhone2 = setField("contactphone2", "", "Teléfono 2", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		contactPhoneExt2 = setField("contactphoneext2", "", "Ext", 5, Types.VARCHAR, true, BmFieldType.PHONE, false);
		www = setField("www", "", "Sitio Web", 50, Types.VARCHAR, true, BmFieldType.WWW, false);

		latitude = setField("latitude", "", "Latitud", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		longitude = setField("longitude", "", "Longitud", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		location = setField("location", "", "Datos Ubicación", 255, Types.VARCHAR, true, BmFieldType.STRING, false);	
		logo = setField("logo", "", "Logo", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		homeAddress = setField("homeaddress", "0", "Domicilio Particular?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		bluePrint = setField("blueprint", "", "Liga para Planos", 400, Types.VARCHAR, true, BmFieldType.WWW, false);

		bmoCity = new BmoCity();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getLogo(),
				getContactName(),
				getContactEmail(),
				getContactPhone(),
				getContactPhone2(),
				getWww(),
				getHomeAddress()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getContactEmail(),
				getContactPhone()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getBmoCity().getName()), 
				new BmSearchField(getStreet())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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

	public BmField getContactName() {
		return contactName;
	}

	public void setContactName(BmField contactName) {
		this.contactName = contactName;
	}

	public BmField getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(BmField contactEmail) {
		this.contactEmail = contactEmail;
	}

	public BmField getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(BmField contactPhone) {
		this.contactPhone = contactPhone;
	}

	public BmField getLocation() {
		return location;
	}

	public void setLocation(BmField location) {
		this.location = location;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
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

	public BmoCity getBmoCity() {
		return bmoCity;
	}

	public void setBmoCity(BmoCity bmoCity) {
		this.bmoCity = bmoCity;
	}

	public BmField getLatitude() {
		return latitude;
	}

	public void setLatitude(BmField latitude) {
		this.latitude = latitude;
	}

	public BmField getLongitude() {
		return longitude;
	}

	public void setLongitude(BmField longitude) {
		this.longitude = longitude;
	}

	public BmField getContactPhoneExt() {
		return contactPhoneExt;
	}

	public void setContactPhoneExt(BmField contactPhoneExt) {
		this.contactPhoneExt = contactPhoneExt;
	}

	public BmField getContactPhone2() {
		return contactPhone2;
	}

	public void setContactPhone2(BmField contactPhone2) {
		this.contactPhone2 = contactPhone2;
	}

	public BmField getContactPhoneExt2() {
		return contactPhoneExt2;
	}

	public void setContactPhoneExt2(BmField contactPhoneExt2) {
		this.contactPhoneExt2 = contactPhoneExt2;
	}

	public BmField getWww() {
		return www;
	}

	public void setWww(BmField www) {
		this.www = www;
	}

	public BmField getLogo() {
		return logo;
	}

	public void setLogo(BmField logo) {
		this.logo = logo;
	}

	public BmField getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(BmField homeAddress) {
		this.homeAddress = homeAddress;
	}

	public BmField getDelegationId() {
		return delegationId;
	}

	public void setDelegationId(BmField delegationId) {
		this.delegationId = delegationId;
	}

	public BmField getBluePrint() {
		return bluePrint;
	}

	public void setBluePrint(BmField bluePrint) {
		this.bluePrint = bluePrint;
	}
	
	
}
