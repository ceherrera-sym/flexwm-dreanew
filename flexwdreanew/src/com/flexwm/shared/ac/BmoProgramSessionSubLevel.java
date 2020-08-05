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

public class BmoProgramSessionSubLevel extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField sequence, name, description, observation, progress, programSessionLevelId, sessionReviewId;

	public BmoProgramSessionSubLevel() {
		super("com.flexwm.server.ac.PmProgramSessionSubLevel", "programsessionsublevels", "programsessionsublevelid", "PSSL", "Características Nivel Prog.");

		//Campo de Datos
		sequence = setField("sequence", "", "Sub Nivel", 2, Types.INTEGER, false, BmFieldType.NUMBER, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		observation = setField("observation", "", "Observación", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		progress = setField("progress", "", "Avance", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		sessionReviewId = setField("sessionreviewid", "", "Evaluación", 8, Types.INTEGER, false, BmFieldType.ID, false);
		programSessionLevelId = setField("programsessionlevelid", "", "Nivel Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getSequence(),
				getName(),
				getProgress()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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

	/**
	 * @return the observation
	 */
	public BmField getObservation() {
		return observation;
	}

	/**
	 * @param observation the observation to set
	 */
	public void setObservation(BmField observation) {
		this.observation = observation;
	}

	/**
	 * @return the progress
	 */
	public BmField getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(BmField progress) {
		this.progress = progress;
	}

	/**
	 * @return the sessionReviewId
	 */
	public BmField getSessionReviewId() {
		return sessionReviewId;
	}

	/**
	 * @param sessionReviewId the sessionReviewId to set
	 */
	public void setSessionReviewId(BmField sessionReviewId) {
		this.sessionReviewId = sessionReviewId;
	}

}
