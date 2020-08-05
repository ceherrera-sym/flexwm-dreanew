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

/**
 * @author smuniz
 *
 */

public class BmoHelpDesk extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name, description, userId, consecutive, date, status, prioridad, statusDate, solution, helpDeskTypeId, dateAttended, equipmentId, ownerId;
	BmoUser bmoUser = new BmoUser();
	BmoEquipment bmoEquipment = new BmoEquipment();
	BmoHelpDeskType bmoHelpDeskType = new BmoHelpDeskType();
	
	public static final char STATUS_OPENED = 'O';
	public static final char STATUS_CANCELLED = 'K';
	public static final char STATUS_CLOSED = 'C';
	public static final char STATUS_ATTENDED = 'A';
	public static final char STATUS_FROZEN = 'F';
	
	public static final char PRIORITY_DOWN = '3';
	public static final char PRIORITY_NORMAL = '2';
	public static final char PRIORITY_HIGH  = '1';
	public static final char PRIORITY_CRITICAL  = '0'; 
	
	public BmoHelpDesk(){
		super("com.flexwm.server.op.PmHelpDesk", "helpdesks", "helpdeskid", "HPDK", "Helpdesk");
		
		//Campo de Datos
		userId = setField("userid", "", "Usuario", 8, Types.INTEGER, false, BmFieldType.ID, true);
		helpDeskTypeId = setField("helpdesktypeid", "", "Tipo de Soporte", 8, Types.INTEGER, false, BmFieldType.ID, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		consecutive = setField("consecutive", "", "Folio", 11, Types.INTEGER, false, BmFieldType.NUMBER, true);
		date = setField("date", "", "Fecha de Solución", 11, Types.DATE, true, BmFieldType.DATE, true);
		status = setField("status", "", "Estatus" , 1,  Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OPENED, "Abierta", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_ATTENDED, "Atendida", "./icons/deliverystatus_total.png"),
				new BmFieldOption(STATUS_FROZEN, "Congelada", "./icons/deliverystatus_partial.png"),
				new BmFieldOption(STATUS_CLOSED, "Cerrada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
			)));
		prioridad = setField("prioridad", "", "Prioridad" , 11,  Types.INTEGER, false, BmFieldType.OPTIONS, false);
		prioridad.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PRIORITY_DOWN, "Baja", "./icons/hpdk_priority_down.png"),
				new BmFieldOption(PRIORITY_NORMAL, "Normal", "./icons/hpdk_priority_normal.png"),
				new BmFieldOption(PRIORITY_HIGH, "Alta", "./icons/hpdk_priority_high.png"),
				new BmFieldOption(PRIORITY_CRITICAL, "Crítica", "./icons/hpdk_priority_critical.png")
			)));
		statusDate = setField("statusdate", "", "Fecha de Petición", 11, Types.DATE, true, BmFieldType.DATE, true);
		solution = setField("solution", "", "Solución", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		dateAttended = setField("dateattended", "", "Fecha de Atención", 11, Types.DATE, true, BmFieldType.DATE, true);
		equipmentId = setField("equipmentid", "", "Recurso", 8, Types.INTEGER, true, BmFieldType.ID, true);
		ownerId = setField("ownerid", "", "Técnico", 8, Types.INTEGER, true, BmFieldType.ID, true);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getConsecutive(),
				bmoUser.getEmail(),
				getName(),				
				getStatusDate(),
				bmoHelpDeskType.getName(),
				getPrioridad(),
				getStatus()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getConsecutive()),
				new BmSearchField(getPrioridad())));
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
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	/**
	 * @return the consecutive
	 */
	public BmField getConsecutive() {
		return consecutive;
	}

	/**
	 * @param consecutive the consecutive to set
	 */
	public void setConsecutive(BmField consecutive) {
		this.consecutive = consecutive;
	}

	/**
	 * @return the date
	 */
	public BmField getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(BmField date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public BmField getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BmField status) {
		this.status = status;
	}

	/**
	 * @return the prioridad
	 */
	public BmField getPrioridad() {
		return prioridad;
	}

	/**
	 * @param prioridad the prioridad to set
	 */
	public void setPrioridad(BmField prioridad) {
		this.prioridad = prioridad;
	}

	/**
	 * @return the statusDate
	 */
	public BmField getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(BmField statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the solution
	 */
	public BmField getSolution() {
		return solution;
	}

	/**
	 * @param solution the solution to set
	 */
	public void setSolution(BmField solution) {
		this.solution = solution;
	}

	/**
	 * @return the helpDeskTypeId
	 */
	public BmField getHelpDeskTypeId() {
		return helpDeskTypeId;
	}

	/**
	 * @param helpDeskTypeId the helpDeskTypeId to set
	 */
	public void setHelpDeskTypeId(BmField helpDeskTypeId) {
		this.helpDeskTypeId = helpDeskTypeId;
	}

	/**
	 * @return the dateAttended
	 */
	public BmField getDateAttended() {
		return dateAttended;
	}

	/**
	 * @param dateAttended the dateAttended to set
	 */
	public void setDateAttended(BmField dateAttended) {
		this.dateAttended = dateAttended;
	}

	/**
	 * @return the equipmentId
	 */
	public BmField getEquipmentId() {
		return equipmentId;
	}

	/**
	 * @param equipmentId the equipmentId to set
	 */
	public void setEquipmentId(BmField equipmentId) {
		this.equipmentId = equipmentId;
	}

	/**
	 * @return the ownerId
	 */
	public BmField getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(BmField ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the bmoUser
	 */
	public BmoUser getBmoUser() {
		return bmoUser;
	}

	/**
	 * @param bmoUser the bmoUser to set
	 */
	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	/**
	 * @return the bmoEquipment
	 */
	public BmoEquipment getBmoEquipment() {
		return bmoEquipment;
	}

	/**
	 * @param bmoEquipment the bmoEquipment to set
	 */
	public void setBmoEquipment(BmoEquipment bmoEquipment) {
		this.bmoEquipment = bmoEquipment;
	}

	/**
	 * @return the bmoHelpDeskType
	 */
	public BmoHelpDeskType getBmoHelpDeskType() {
		return bmoHelpDeskType;
	}

	/**
	 * @param bmoHelpDeskType the bmoHelpDeskType to set
	 */
	public void setBmoHelpDeskType(BmoHelpDeskType bmoHelpDeskType) {
		this.bmoHelpDeskType = bmoHelpDeskType;
	}
		
	
}
