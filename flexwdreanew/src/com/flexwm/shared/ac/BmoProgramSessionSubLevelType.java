/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.ac;

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

public class BmoProgramSessionSubLevelType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField sequence, name, description, programSessionLevelId;

	public BmoProgramSessionSubLevelType() {
		super("com.flexwm.server.ac.PmProgramSessionSubLevelType", "programsessionsubleveltypes", "programsessionsubleveltypeid", "PSLT", "Tipos Caract. Nivel Prog.");

		//Campo de Datos
		sequence = setField("sequence", "", "Sub Nivel", 2, Types.INTEGER, false, BmFieldType.NUMBER, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		programSessionLevelId = setField("programsessionlevelid", "", "Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getSequence(),
				getName(),				
				getDescription()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getSequence(),
				getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getSequence(), BmOrder.ASC),
				new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	/**
	 * @return the sequence
	 */
	public BmField getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(BmField sequence) {
		this.sequence = sequence;
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
	 * @return the programSessionLevelId
	 */
	public BmField getProgramSessionLevelId() {
		return programSessionLevelId;
	}

	/**
	 * @param programSessionLevelId the programSessionLevelId to set
	 */
	public void setProgramSessionLevelId(BmField programSessionLevelId) {
		this.programSessionLevelId = programSessionLevelId;
	}
}
