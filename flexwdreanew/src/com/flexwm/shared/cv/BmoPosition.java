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

/**
 * @author smuniz
 *
 */

public class BmoPosition extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name, description, objective, isActived, experience;
		
	public BmoPosition(){
		super("com.flexwm.server.cv.PmPosition", "positions", "positionid", "POSI", " Catálogo de Puestos");
		
		//Campo de Datos
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Funciones Específicas", 3000, Types.VARCHAR, true, BmFieldType.STRING, false);
		objective = setField("objective", "", "Objetivo del Puesto", 3000, Types.VARCHAR, true, BmFieldType.STRING, false);
		isActived = setField("isactived", "true", "Activo", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		experience = setField("experience", "", "Experiencia Requerida", 512, Types.VARCHAR, true, BmFieldType.STRING, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),		
				getIsActived()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName())));
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
	 * @return the objective
	 */
	public BmField getObjective() {
		return objective;
	}

	/**
	 * @param objective the objective to set
	 */
	public void setObjective(BmField objective) {
		this.objective = objective;
	}

	/**
	 * @return the isActived
	 */
	public BmField getIsActived() {
		return isActived;
	}

	/**
	 * @param isActived the isActived to set
	 */
	public void setIsActived(BmField isActived) {
		this.isActived = isActived;
	}

	/**
	 * @return the experience
	 */
	public BmField getExperience() {
		return experience;
	}

	/**
	 * @param experience the experience to set
	 */
	public void setExperience(BmField experience) {
		this.experience = experience;
	}
	
}
