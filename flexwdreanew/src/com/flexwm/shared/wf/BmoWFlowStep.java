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
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowPhase;

public class BmoWFlowStep extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type, sequence, progress, comments, commentLog,
		daysRemind, remindDate, emailReminders, emailReminderComments, enabled, startdate, enddate, file, fileLink, pending, 
		hours, billable, rate, wFlowFunnelId, status,
		wFlowId, userId, profileId, wFlowPhaseId, wFlowValidationId, wFlowActionId;
	
	private BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoProfile bmoProfile = new BmoProfile();
	private BmoUser bmoUser = new BmoUser();
	private BmoWFlowAction bmoWFlowAction = new BmoWFlowAction();
	private BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoWFlowValidation bmoWFlowValidation = new BmoWFlowValidation();
	
	
	
	public static char TYPE_WFLOW = 'W';
	public static char TYPE_USER = 'U';
	public static char STATUS_EXPIRED = 'E';
	public static char STATUS_TOEXPIRED = 'T';	
	public static char STATUS_FINALIZED = 'F';
	
	
	public static String ACCESS_CHANGESENDDATE = "WSCHED";
	
	
	public BmoWFlowStep() {
		super("com.flexwm.server.wf.PmWFlowStep", "wflowsteps", "wflowstepid", "WFSP", "Tareas WFlow");
		
		// Campos de datos
		name = setField("name", "", "Nombre Tarea", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WFLOW, "WFlow", "./icons/vaadin/cogs.png"),
				new BmFieldOption(TYPE_USER, "Usuario", "./icons/vaadin/user.png")
				)));
		status = setField("status", "", "Estatus", 50, Types.VARCHAR, true, BmFieldType.OPTIONS, false);
		status.setOptionList(new  ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_EXPIRED,"Vencida","./icons/status_expired.png"),
				new BmFieldOption(STATUS_TOEXPIRED,"Por Vencer","./icons/status_revision.png"),
				new BmFieldOption(STATUS_FINALIZED,"Finalizada","./icons/paymentstatus_authorized.png")
				)));
		
		sequence = setField("sequence", "", "Secuencia", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		progress = setField("progress", "", "Avance", 8, Types.INTEGER, false, BmFieldType.PERCENTAGE, false);
		comments = setField("comments", "", "Comentarios", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		commentLog = setField("commentlog", "", "Bitácora Tarea", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
		enabled = setField("enabled", "", "Activo", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		startdate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		enddate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		daysRemind = setField("daysremind", "0", "Recordar en (días)", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDate = setField("reminddate", "", "Recordar el", 20, Types.DATE, true, BmFieldType.DATE, false);
		emailReminders = setField("emailreminders", "", "Notificaciones", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		file = setField("file", "", "Archivo", 512, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		fileLink = setField("filelink", "", "Liga Ext.", 500, Types.VARCHAR, true, BmFieldType.WWW, false);
		pending = setField("pending", "", "Pendientes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		hours = setField("hours", "", "Horas", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		billable = setField("billable", "", "Facturable?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		rate = setField("rate", "", "Tarifa Hr.", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		profileId = setField("profileid", "", "Perfil", 8, Types.INTEGER, true, BmFieldType.ID, false);
		userId = setField("userid", "", "Usuario", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowPhaseId = setField("wflowphaseid", "", "Fase", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowValidationId = setField("wflowvalidationid", "", "Clase Validación", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowActionId = setField("wflowactionid", "", "Clase Acción", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowFunnelId = setField("wflowfunnelid", "", "Funnel", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		emailReminderComments = setField("emailremindercomments", "", "Notificación de comentarios", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getSequence(),
				getName(),
				getType(),
				getBmoWFlow().getCode(),
				getBmoWFlow().getName(),
				getBmoProfile().getName(),
				getBmoUser().getCode(),
				getStartdate(),
				getFile(),
				getFileLink(),
				getRemindDate(),
				getPending(),
				getProgress(),
				getStatus(),
				getEnabled()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlow().getCode(),
				getName(),
				getRemindDate(),
				getProgress()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoWFlow().getCode(), BmOrder.ASC),
				new BmOrder(getBmoWFlowPhase().getKind(), getBmoWFlowPhase().getSequence(), BmOrder.ASC),
				new BmOrder(getKind(), getSequence(), BmOrder.ASC),
				new BmOrder(getKind(), getProgress(), BmOrder.ASC)
				));
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

	public BmField getSequence() {
		return sequence;
	}

	public void setSequence(BmField sequence) {
		this.sequence = sequence;
	}

	public BmoWFlowPhase getBmoWFlowPhase() {
		return bmoWFlowPhase;
	}

	public void setBmoWFlowPhase(BmoWFlowPhase bmoWFlowPhase) {
		this.bmoWFlowPhase = bmoWFlowPhase;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setprofileId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmField getWFlowPhaseId() {
		return wFlowPhaseId;
	}

	public void setWFlowPhaseId(BmField wFlowPhaseId) {
		this.wFlowPhaseId = wFlowPhaseId;
	}

	public BmField getProgress() {
		return progress;
	}

	public void setProgress(BmField progress) {
		this.progress = progress;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getEnabled() {
		return enabled;
	}

	public void setEnabled(BmField enabled) {
		this.enabled = enabled;
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

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getDaysRemind() {
		return daysRemind;
	}

	public void setDaysRemind(BmField daysRemind) {
		this.daysRemind = daysRemind;
	}

	public BmField getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(BmField remindDate) {
		this.remindDate = remindDate;
	}
	public BmField getWFlowValidationId() {
		return wFlowValidationId;
	}

	public void setWFlowValidationId(BmField wFlowValidationId) {
		this.wFlowValidationId = wFlowValidationId;
	}

	public BmField getWFlowActionId() {
		return wFlowActionId;
	}

	public void setWFlowActionId(BmField wFlowActionId) {
		this.wFlowActionId = wFlowActionId;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

	public BmField getEmailReminders() {
		return emailReminders;
	}

	public void setEmailReminders(BmField emailReminders) {
		this.emailReminders = emailReminders;
	}

	public BmField getCommentLog() {
		return commentLog;
	}

	public void setCommentLog(BmField commentLog) {
		this.commentLog = commentLog;
	}

	public BmoWFlowAction getBmoWFlowAction() {
		return bmoWFlowAction;
	}

	public void setBmoWFlowAction(BmoWFlowAction bmoWFlowAction) {
		this.bmoWFlowAction = bmoWFlowAction;
	}

	public BmField getPending() {
		return pending;
	}

	public void setPending(BmField pending) {
		this.pending = pending;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
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

	public BmField getWFlowFunnelId() {
		return wFlowFunnelId;
	}

	public void setWFlowFunnelId(BmField wFlowFunnelId) {
		this.wFlowFunnelId = wFlowFunnelId;
	}

	public BmoWFlowFunnel getBmoWFlowFunnel() {
		return bmoWFlowFunnel;
	}

	public void setBmoWFlowFunnel(BmoWFlowFunnel bmoWFlowFunnel) {
		this.bmoWFlowFunnel = bmoWFlowFunnel;
	}

	public BmField getFileLink() {
		return fileLink;
	}

	public void setFileLink(BmField fileLink) {
		this.fileLink = fileLink;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmoWFlowValidation getBmoWFlowValidation() {
		return bmoWFlowValidation;
	}

	public void setBmoWFlowValidation(BmoWFlowValidation bmoWFlowValidation) {
		this.bmoWFlowValidation = bmoWFlowValidation;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getEmailReminderComments() {
		return emailReminderComments;
	}

	public void setEmailReminderComments(BmField emailReminderComments) {
		this.emailReminderComments = emailReminderComments;
	}

}
