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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoEquipment extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, brand, model, year, color, price, cost, userId, equipmentTypeId, status;
	BmoEquipmentType bmoEquipmentType;
	private BmoUser bmoUser = new BmoUser();

	public static char STATUS_ACTIVE = 'A';
	public static char STATUS_INACTIVE = 'I';

	public BmoEquipment() {
		super("com.flexwm.server.op.PmEquipment", "equipments", "equipmentid", "EQUI", "Recursos");

		// Campo de datos
		code = setField("code", "", "Clave Recurso", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Recurso", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);	
		brand = setField("brand", "", "Marca", 30, Types.VARCHAR, true, BmFieldType.STRING, false);	
		model = setField("model", "", "Modelo", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		year = setField("year", "", "Año", 4, Types.VARCHAR, true, BmFieldType.STRING, false);
		color = setField("color", "", "Color", 20, Types.VARCHAR, true, BmFieldType.STRING, false);	
		price = setField("price", "", "Precio Renta", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);	
		cost = setField("cost", "", "Costo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);	
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);
		equipmentTypeId = setField("equipmenttypeid", "", "Tipo Equipo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		status = setField("status", "" + BmoEquipment.STATUS_ACTIVE, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_ACTIVE, "Activo", "./icons/boolean_true.png"),
				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "./icons/boolean_false.png")
				)));
		bmoEquipmentType = new BmoEquipmentType();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(), 
				getDescription(),
				getBmoEquipmentType().getName(),
				getBmoUser().getCode(),
				getStatus()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getBrand(), 
				getModel()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoUser().getCode(),
				getStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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

	public BmField getBrand() {
		return brand;
	}

	public void setBrand(BmField brand) {
		this.brand = brand;
	}

	public BmField getModel() {
		return model;
	}

	public void setModel(BmField model) {
		this.model = model;
	}

	public BmField getYear() {
		return year;
	}

	public void setYear(BmField year) {
		this.year = year;
	}

	public BmField getColor() {
		return color;
	}

	public void setColor(BmField color) {
		this.color = color;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getEquipmentTypeId() {
		return equipmentTypeId;
	}

	public void setEquipmentTypeId(BmField equipmentTypeId) {
		this.equipmentTypeId = equipmentTypeId;
	}

	public BmoEquipmentType getBmoEquipmentType() {
		return bmoEquipmentType;
	}

	public void setBmoEquipmentType(BmoEquipmentType bmoEquipmentType) {
		this.bmoEquipmentType = bmoEquipmentType;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getCost() {
		return cost;
	}

	public void setCost(BmField cost) {
		this.cost = cost;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

}
