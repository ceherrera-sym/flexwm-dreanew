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

public class BmoDevelopmentBlock extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, isOpen, endDate, startDate, readyDate, section, isTemporally, statusProcess, 
	processPercentage, habitabilityHistory, developmentPhaseId, blueprint;

	BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();

	public static String ACTION_BATCHUPDATE = "BATCHUPDATE";

	public static String CODE_PREFIX = "Mz-";

	public BmoDevelopmentBlock(){
		super("com.flexwm.server.co.PmDevelopmentBlock", "developmentblocks", "developmentblockid", "DVBL", "Manzanas/Torres");

		//Campo de Datos		
		code = setField("code", "", "Clave M/T", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		isOpen = setField("isopen", "", "Mza. Abierta", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		startDate = setField("startdate", "", "Programación Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Programación Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		readyDate = setField("readydate", "", "Fecha Terminación", 20, Types.DATE, true, BmFieldType.DATE, false);
		section = setField("section", "", "Sección", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		isTemporally = setField("istemporally", "", "Mza. Temporal?", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		statusProcess = setField("statusprocess", "", "En Proceso?", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		processPercentage = setField("processpercentage", "0", "% Avance", 11, Types.INTEGER, false, BmFieldType.PERCENTAGE, false);
		habitabilityHistory = setField("habitabilityhistory", "", "Historial", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		developmentPhaseId = setField("developmentphaseid", "", "Etapa Des.", 8, Types.INTEGER, false, BmFieldType.ID, false);
		blueprint = setField("blueprint", "", "Plano", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),		
				getBmoDevelopmentPhase().getCode(),
				getReadyDate(),
				getBlueprint(),
				getIsOpen(),
				getStatusProcess(),
				getProcessPercentage()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoDevelopmentPhase().getCode(),
				getCode(),		
				getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription()),
				new BmSearchField(getBmoDevelopmentPhase().getCode()), 
				new BmSearchField(getBmoDevelopmentPhase().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoDevelopmentPhase().getCode(), BmOrder.ASC),
				new BmOrder(getKind(), getCode(), BmOrder.ASC)
				));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}


	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}


	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
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
	 * @return the isOpen
	 */
	public BmField getIsOpen() {
		return isOpen;
	}


	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(BmField isOpen) {
		this.isOpen = isOpen;
	}


	/**
	 * @return the endDate
	 */
	public BmField getEndDate() {
		return endDate;
	}


	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}


	/**
	 * @return the startDate
	 */
	public BmField getStartDate() {
		return startDate;
	}


	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}


	/**
	 * @return the readyDate
	 */
	public BmField getReadyDate() {
		return readyDate;
	}


	/**
	 * @param readyDate the readyDate to set
	 */
	public void setReadyDate(BmField readyDate) {
		this.readyDate = readyDate;
	}


	/**
	 * @return the section
	 */
	public BmField getSection() {
		return section;
	}


	/**
	 * @param section the section to set
	 */
	public void setSection(BmField section) {
		this.section = section;
	}


	/**
	 * @return the isTemporally
	 */
	public BmField getIsTemporally() {
		return isTemporally;
	}


	/**
	 * @param isTemporally the isTemporally to set
	 */
	public void setIsTemporally(BmField isTemporally) {
		this.isTemporally = isTemporally;
	}


	/**
	 * @return the statusProcess
	 */
	public BmField getStatusProcess() {
		return statusProcess;
	}


	/**
	 * @param statusProcess the statusProcess to set
	 */
	public void setStatusProcess(BmField statusProcess) {
		this.statusProcess = statusProcess;
	}


	/**
	 * @return the processPercentage
	 */
	public BmField getProcessPercentage() {
		return processPercentage;
	}


	/**
	 * @param processPorcent the processPercentage to set
	 */
	public void setProcessPercentage(BmField processPercentage) {
		this.processPercentage = processPercentage;
	}


	/**
	 * @return the habitabilityHistory
	 */
	public BmField getHabitabilityHistory() {
		return habitabilityHistory;
	}


	/**
	 * @param habitabilityHistory the habitabilityHistory to set
	 */
	public void setHabitabilityHistory(BmField habitabilityHistory) {
		this.habitabilityHistory = habitabilityHistory;
	}


	/**
	 * @return the developmentPhaseId
	 */
	public BmField getDevelopmentPhaseId() {
		return developmentPhaseId;
	}


	/**
	 * @param developmentPhaseId the developmentPhaseId to set
	 */
	public void setDevelopmentPhaseId(BmField developmentPhaseId) {
		this.developmentPhaseId = developmentPhaseId;
	}

	public BmoDevelopmentPhase getBmoDevelopmentPhase() {
		return bmoDevelopmentPhase;
	}

	public void setBmoDevelopmentPhase(BmoDevelopmentPhase bmoDevelopmentPhase) {
		this.bmoDevelopmentPhase = bmoDevelopmentPhase;
	}

	/**
	 * @return the blueprint
	 */
	public BmField getBlueprint() {
		return blueprint;
	}

	/**
	 * @param blueprint the blueprint to set
	 */
	public void setBlueprint(BmField blueprint) {
		this.blueprint = blueprint;
	}

}
