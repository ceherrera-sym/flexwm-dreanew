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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowValidation;


public class BmoWFlowStepType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, sequence, daysRemind, emailReminders, wFlowFunnelId, hours, billable, rate,
			wFlowTypeId, wFlowPhaseId, profileId, wFlowValidationId, wFlowActionId;
	
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
	private BmoProfile bmoProfile = new BmoProfile();
	private BmoWFlowValidation bmoWFlowValidation = new BmoWFlowValidation();
	private BmoWFlowAction bmoWFlowAction = new BmoWFlowAction();
	private BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();

	public BmoWFlowStepType() {
		super("com.flexwm.server.wf.PmWFlowStepType", "wflowsteptypes", "wflowsteptypeid", "WFST", "Tipos Tarea");
		
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		sequence = setField("sequence", "", "Sec. Tarea", 8, Types.INTEGER, false, BmFieldType.NUMBER, false);
		daysRemind = setField("daysremind", "10", "Recordar en (días)", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		emailReminders = setField("emailreminders", "", "Notificaciones", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		wFlowFunnelId = setField("wflowfunnelid", "", "Funnel", 8, Types.INTEGER, true, BmFieldType.ID, false);
		hours = setField("hours", "", "Horas", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		billable = setField("billable", "", "Facturable?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		rate = setField("rate", "", "Tarifa Hr.", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		wFlowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowPhaseId = setField("wflowphaseid", "", "Fase de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Perfil", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowValidationId = setField("wflowvalidationid", "", "Clase Validación", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowActionId = setField("wflowactionid", "", "Clase Acción", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlowPhase().getSequence(),
				getBmoWFlowPhase().getName(),
				getSequence(),
				getName(),
				getBmoProfile().getName(),
				getBmoWFlowFunnel().getName(),
				getBmoWFlowValidation().getCode(),
				getBmoWFlowAction().getCode()
				));
	}	
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlowPhase().getSequence(),
				getBmoWFlowPhase().getName(),
				getSequence(),
				getName()
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
				new BmOrder(getBmoWFlowPhase().getKind(), getBmoWFlowPhase().getSequence(), BmOrder.ASC),
				new BmOrder(getKind(), getSequence(), BmOrder.ASC)
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

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
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

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmField getWFlowPhaseId() {
		return wFlowPhaseId;
	}

	public void setWFlowPhaseId(BmField wFlowPhaseId) {
		this.wFlowPhaseId = wFlowPhaseId;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setprofileId(BmField profileId) {
		this.profileId = profileId;
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

	public BmoWFlowValidation getBmoWFlowValidation() {
		return bmoWFlowValidation;
	}

	public void setBmoWFlowValidation(BmoWFlowValidation bmoWFlowValidation) {
		this.bmoWFlowValidation = bmoWFlowValidation;
	}

	public BmoWFlowAction getBmoWFlowAction() {
		return bmoWFlowAction;
	}

	public void setBmoWFlowAction(BmoWFlowAction bmoWFlowAction) {
		this.bmoWFlowAction = bmoWFlowAction;
	}

	public BmField getDaysRemind() {
		return daysRemind;
	}

	public void setDaysRemind(BmField daysRemind) {
		this.daysRemind = daysRemind;
	}

	public BmField getEmailReminders() {
		return emailReminders;
	}

	public void setEmailReminders(BmField emailReminders) {
		this.emailReminders = emailReminders;
	}

	public BmField getWFlowFunnelId() {
		return wFlowFunnelId;
	}

	public void setWFlowFunnelId(BmField wFlowFunnelId) {
		this.wFlowFunnelId = wFlowFunnelId;
	}

	public BmField getRate() {
		return rate;
	}

	public void setRate(BmField rate) {
		this.rate = rate;
	}

	public BmField getBillable() {
		return billable;
	}

	public void setBillable(BmField billable) {
		this.billable = billable;
	}

	public BmField getHours() {
		return hours;
	}

	public void setHours(BmField hours) {
		this.hours = hours;
	}

	public BmoWFlowFunnel getBmoWFlowFunnel() {
		return bmoWFlowFunnel;
	}

	public void setBmoWFlowFunnel(BmoWFlowFunnel bmoWFlowFunnel) {
		this.bmoWFlowFunnel = bmoWFlowFunnel;
	}

}
