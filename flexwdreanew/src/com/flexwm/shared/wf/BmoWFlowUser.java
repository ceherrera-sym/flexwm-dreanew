/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.wf;

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
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;

public class BmoWFlowUser extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField required, autoDate, lockStatus, lockStart, lockEnd, assignSteps, 
		commission, hours, billable, rate,
		profileId, userId, wflowId, userGEventId, publicGEventId, areaGEventId;
	
	BmoProfile bmoProfile = new BmoProfile();
	BmoUser bmoUser = new BmoUser();
	BmoWFlow bmoWFlow = new BmoWFlow();

	public static char LOCKSTATUS_OPEN = 'O';
	public static char LOCKSTATUS_LOCKED = 'L';
	public static char LOCKSTATUS_CONFLICT = 'C';
	
	public BmoWFlowUser() {
		super("com.flexwm.server.wf.PmWFlowUser", "wflowusers", "wflowuserid", "WFLU", "Usuarios WFlow");

		required = setField("required", "", "Requerido?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		autoDate = setField("autodate", "1", "Fechas Autom.?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		lockStart = setField("lockstart", "", "Fecha Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		lockEnd = setField("lockend", "", "Fecha Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		
		lockStatus = setField("lockstatus", "" + LOCKSTATUS_OPEN, "Bloqueo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		lockStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(LOCKSTATUS_OPEN, "Libre", "./icons/wflu_lockstatus_open.png"),
				new BmFieldOption(LOCKSTATUS_LOCKED, "Bloqueado", "./icons/wflu_lockstatus_locked.png"),
				new BmFieldOption(LOCKSTATUS_CONFLICT, "Conflicto", "./icons/wflu_lockstatus_conflict.png")
				)));
		
		assignSteps = setField("assignsteps", "1", "Asignar a Tareas", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		commission = setField("commission", "0", "Comisión ?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);		
		hours = setField("hours", "", "Horas", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		billable = setField("billable", "", "Facturable?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		rate = setField("rate", "", "Tarifa Hr.", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		userGEventId = setField("usergeventid", "", "G. Cal. Usuario", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		publicGEventId = setField("publicgeventid", "", "G. Cal. Público", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		areaGEventId = setField("areageventid", "", "G. Cal. Depto.", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		profileId = setField("profileid", "", "Perfil", 20, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Colaborador", 20, Types.INTEGER, true, BmFieldType.ID, false);
		wflowId = setField("wflowid", "", "Flujo", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getBmoUser().getCode(),
						getBmoProfile().getName(),
						getRequired(),
						getCommission(),
						getLockStart(),
						getLockStatus()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoUser().getEmail())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoUser().getEmail(), BmOrder.ASC)));
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getWFlowId() {
		return wflowId;
	}

	public void setWFlowId(BmField projectId) {
		this.wflowId = projectId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getLockStart() {
		return lockStart;
	}

	public void setLockStart(BmField lockStart) {
		this.lockStart = lockStart;
	}

	public BmField getLockEnd() {
		return lockEnd;
	}

	public void setLockEnd(BmField lockEnd) {
		this.lockEnd = lockEnd;
	}

	public BmField getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(BmField lockStatus) {
		this.lockStatus = lockStatus;
	}

	public BmField getPublicGEventId() {
		return publicGEventId;
	}

	public void setPublicGEventId(BmField publicGEventId) {
		this.publicGEventId = publicGEventId;
	}

	public BmField getProfileId() {
		return profileId;
	}


	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}


	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}


	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}


	public BmField getRequired() {
		return required;
	}


	public void setRequired(BmField required) {
		this.required = required;
	}


	public BmField getAutoDate() {
		return autoDate;
	}


	public void setAutoDate(BmField autoDate) {
		this.autoDate = autoDate;
	}


	public BmField getUserGEventId() {
		return userGEventId;
	}


	public void setUserGEventId(BmField userGEventId) {
		this.userGEventId = userGEventId;
	}


	public BmField getAreaGEventId() {
		return areaGEventId;
	}


	public void setAreaGEventId(BmField areaGEventId) {
		this.areaGEventId = areaGEventId;
	}


	public BmField getAssignSteps() {
		return assignSteps;
	}


	public void setAssignSteps(BmField assignSteps) {
		this.assignSteps = assignSteps;
	}


	public BmField getCommission() {
		return commission;
	}


	public void setCommission(BmField commission) {
		this.commission = commission;
	}


	public BmField getHours() {
		return hours;
	}


	public void setHours(BmField hours) {
		this.hours = hours;
	}


	public BmField getBillable() {
		return billable;
	}


	public void setBillable(BmField billable) {
		this.billable = billable;
	}


	public BmField getRate() {
		return rate;
	}


	public void setRate(BmField rate) {
		this.rate = rate;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}
}
