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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoProjectEquipment extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField code,projectId, name, description,equipmentId,startDate,endDate;
	BmoEquipment bmoEquipment = new BmoEquipment();


	public static String ACTION_CHANGEORDEREQUIPMENT = "CHANGEORDEREQUIPMENT";

	public BmoProjectEquipment() {
		super("com.flexwm.server.op.PmProjectEquipment", "projectequipment", "projectequipmentid", "PEQI", "Recurso del Proyecto");

		code = setField("code", "", "Clave Recurso", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre Recurso", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		projectId = setField("projectid", "", "Proyecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		equipmentId = setField("equipmentid", "", "Recurso", 8, Types.INTEGER, false, BmFieldType.ID, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(), 		
				getDescription(),
				getStartDate(),
				getEndDate()

				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getCode()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode()
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

	public BmField getProjectId() {
		return projectId;
	}

	public void setProject(BmField projectId) {
		this.projectId = projectId;
	}

	public BmField getEquipmentId() {
		return equipmentId;
	}

	public void setEquipment(BmField equipmentId) {
		this.equipmentId = equipmentId;
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





}
