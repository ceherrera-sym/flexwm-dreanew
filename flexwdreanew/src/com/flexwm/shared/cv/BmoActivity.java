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
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoActivity extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, startdate, enddate, status, userId, wFlowTypeId, wFlowId,
	remindDate,emailReminders;
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();

	public static String CODE_PREFIX = "AC-";
	
	// Establecer estatus
	public static char STATUS_ACTIVE = 'A';
	public static char STATUS_INACTIVE = 'N';
	
	BmoUser bmoUser = new BmoUser();

	public BmoActivity(){
		super("com.flexwm.server.cv.PmActivity", "activities", "activityid", "ACTI", "Actividades");

		code = setField("code", "", "Clave Actividad", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		startdate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		enddate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		
		status = setField("status", "" + STATUS_ACTIVE, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_ACTIVE, "Activo", "/icons/boolean_true.png"),
				new BmFieldOption(STATUS_INACTIVE, "Finalizado", "/icons/boolean_false.png")
				)));
		
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		remindDate = setField("reminddate", "", "Recordar el", 20, Types.DATE, true, BmFieldType.DATE, false);
		emailReminders = setField("emailreminders", "", "Notificaciones", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),	
				getBmoUser().getCode(),
				getStartdate(),
				getEnddate(),
				getBmoWFlow().getProgress(),
				getStatus()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),	
				getBmoUser().getCode(),
				getStartdate(),
				getEnddate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getBmoUser().getCode()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getStartdate() {
		return startdate;
	}

	public void setStartdate(BmField startdate) {
		this.startdate = startdate;
	}

	public BmField getEnddate() {
		return enddate;
	}

	public void setEnddate(BmField enddate) {
		this.enddate = enddate;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(BmField remindDate) {
		this.remindDate = remindDate;
	}

	public BmField getEmailReminders() {
		return emailReminders;
	}

	public void setEmailReminders(BmField emailReminders) {
		this.emailReminders = emailReminders;
	}
	
}
