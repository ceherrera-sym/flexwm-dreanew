/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cv;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoArea;


/**
 * @author smuniz
 *
 */

public class BmoCourseProgram extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name, description, areaId;
	BmoArea bmoArea = new BmoArea();

	public BmoCourseProgram(){
		super("com.flexwm.server.cv.PmCourseProgram", "courseprograms", "programid", "CPRO", "Programas");

		//Campo de Datos
		areaId = setField("areaid", "", "Área Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);
		name = setField("name", "", "Nombre Programa", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),				
				getDescription(),
				bmoArea.getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription()),
				new BmSearchField(bmoArea.getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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
	 * @return the areaId
	 */
	public BmField getAreaId() {
		return areaId;
	}

	/**
	 * @param areaId the areaId to set
	 */
	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	/**
	 * @return the bmoArea
	 */
	public BmoArea getBmoArea() {
		return bmoArea;
	}

	/**
	 * @param bmoArea the bmoArea to set
	 */
	public void setBmoArea(BmoArea bmoArea) {
		this.bmoArea = bmoArea;
	}



}
