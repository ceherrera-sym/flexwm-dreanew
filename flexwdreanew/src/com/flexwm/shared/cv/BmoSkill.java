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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author smuniz
 *
 */

public class BmoSkill extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type;

	public static final char TYPE_SKILL = 'S';
	public static final char TYPE_KNOWLEDGE = 'K';
	public static final char TYPE_COMPETENCE = 'C';

	public BmoSkill() {
		super("com.flexwm.server.cv.PmSkill", "skills", "skillid", "SKIL", "Habilidades");

		//Campo de Datos		
		name = setField("name", "", "Nombre", 60, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, true);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_SKILL, "Habilidad", "./icons/skil_type_skill.png"),
				new BmFieldOption(TYPE_KNOWLEDGE, "Conocimiento", "./icons/skil_type_knowledge.png"),
				new BmFieldOption(TYPE_COMPETENCE, "Competencia", "./icons/skil_type_competence.png")
				)));
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),				
				getDescription(),
				getType()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getType()), 
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
	 * @return the type
	 */
	public BmField getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BmField type) {
		this.type = type;
	}

}
