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

public class BmoConceptHeading extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;

	private BmField name, description, conceptGroupId;
	BmoConceptGroup bmoConceptGroup = new BmoConceptGroup();

	public BmoConceptHeading(){
		super("com.flexwm.server.co.PmConceptHeading", "conceptheadings", "conceptheadingid", "CPHD", "Rubro/Familia");

		//Campo de Datos		
		conceptGroupId = setField("conceptgroupid", "", "Partida", 8, Types.INTEGER, true, BmFieldType.ID, false);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoConceptGroup.getName(),
				getName(),				
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
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
	 * @return the conceptGroupId
	 */
	public BmField getConceptGroupId() {
		return conceptGroupId;
	}

	/**
	 * @param conceptGroupId the conceptGroupId to set
	 */
	public void setConceptGroupId(BmField conceptGroupId) {
		this.conceptGroupId = conceptGroupId;
	}

	/**
	 * @return the bmoConceptGroup
	 */
	public BmoConceptGroup getBmoConceptGroup() {
		return bmoConceptGroup;
	}

	/**
	 * @param bmoConceptGroup the bmoConceptGroup to set
	 */
	public void setBmoConceptGroup(BmoConceptGroup bmoConceptGroup) {
		this.bmoConceptGroup = bmoConceptGroup;
	}

}
