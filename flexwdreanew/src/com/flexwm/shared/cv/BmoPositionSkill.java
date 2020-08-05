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

public class BmoPositionSkill extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField positionId, skillId;
	BmoPosition bmoPosition = new BmoPosition();
	BmoSkill bmoSkill = new BmoSkill();
		
	public BmoPositionSkill(){
		super("com.flexwm.server.cv.PmPositionSkill", "positionskills", "positionskillid", "PSSK", "Catálogo de Puestos y Habilidades");
		
		//Campo de Datos
		positionId = setField("positionid", "", "Puesto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		skillId = setField("skillid", "", "Concepto", 8, Types.INTEGER, false, BmFieldType.ID, false);

	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoSkill.getName(),
				bmoSkill.getType(),
				bmoSkill.getDescription()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(bmoSkill.getName()), 
				new BmSearchField(bmoSkill.getType()),
				new BmSearchField(bmoSkill.getDescription())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the positionId
	 */
	public BmField getPositionId() {
		return positionId;
	}

	/**
	 * @param positionId the positionId to set
	 */
	public void setPositionId(BmField positionId) {
		this.positionId = positionId;
	}

	/**
	 * @return the skillId
	 */
	public BmField getSkillId() {
		return skillId;
	}

	/**
	 * @param skillId the skillId to set
	 */
	public void setSkillId(BmField skillId) {
		this.skillId = skillId;
	}

	/**
	 * @return the bmoPosition
	 */
	public BmoPosition getBmoPosition() {
		return bmoPosition;
	}

	/**
	 * @param bmoPosition the bmoPosition to set
	 */
	public void setBmoPosition(BmoPosition bmoPosition) {
		this.bmoPosition = bmoPosition;
	}

	/**
	 * @return the bmoSkill
	 */
	public BmoSkill getBmoSkill() {
		return bmoSkill;
	}

	/**
	 * @param bmoSkill the bmoSkill to set
	 */
	public void setBmoSkill(BmoSkill bmoSkill) {
		this.bmoSkill = bmoSkill;
	}


	
	
}
