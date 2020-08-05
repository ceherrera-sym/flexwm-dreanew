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
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;

public class BmoWFlow extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, code, name, description, startDate, endDate, progress, hasDocuments, wFlowFunnelId, 
	hours, billable, googleEventId, callerCode, callerId, userId, wFlowTypeId, wFlowPhaseId, companyId, customerId;

	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
	private BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();

	public static final char STATUS_ACTIVE = 'A';
	public static final char STATUS_INACTIVE = 'N';

	public BmoWFlow() {
		super("com.flexwm.server.wf.PmWFlow", "wflows", "wflowid", "WFLW", "Flujos");

		status = setField("status", "" + STATUS_ACTIVE, "Estatus", 1, 0,false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_ACTIVE, "Activo", "./icons/wflw_status_active.png"),
				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "./icons/wflw_status_inactive.png")
				)));

		code = setField("code", "", "Clave Flujo", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre Flujo", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		progress = setField("progress", "0", "Avance", 8, Types.INTEGER, false, BmFieldType.PERCENTAGE, false);
		wFlowFunnelId = setField("wflowfunnelid", "", "Funnel", 8, Types.INTEGER, true, BmFieldType.ID, false);
		hours = setField("hours", "", "Horas", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		billable = setField("billable", "", "Facturable?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		googleEventId = setField("googleeventid", "", "ID Evento G.", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		hasDocuments = setField("hasdocuments", "", "Docs. Req.", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		callerCode = setField("callercode", "", "Clave Cliente", 6, Types.VARCHAR, true, BmFieldType.STRING, false);
		callerId = setField("callerid", "", "ID Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		userId = setField("userid", "", "Creado Por", 8, Types.INTEGER, true, BmFieldType.ID, false);

		wFlowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowPhaseId = setField("wflowphaseid", "", "Fase de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);

		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente/Prospecto", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(), 
				getDescription(),
				getBmoWFlowType().getName(),
				getBmoWFlowPhase().getName(),
				getBmoWFlowFunnel().getName(),
				getProgress()
				));
	}	

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode().getName(), getCode().getLabel()),
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
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

	public BmoWFlowPhase getBmoWFlowPhase() {
		return bmoWFlowPhase;
	}

	public void setBmoWFlowPhase(BmoWFlowPhase bmoWFlowPhase) {
		this.bmoWFlowPhase = bmoWFlowPhase;
	}

	public BmField getProgress() {
		return progress;
	}

	public void setProgress(BmField progress) {
		this.progress = progress;
	}

	public BmField getCallerCode() {
		return callerCode;
	}

	public void setCallerCode(BmField callerCode) {
		this.callerCode = callerCode;
	}

	public BmField getCallerId() {
		return callerId;
	}

	public void setCallerId(BmField callerId) {
		this.callerId = callerId;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getGoogleEventId() {
		return googleEventId;
	}

	public void setGoogleEventId(BmField googleEventId) {
		this.googleEventId = googleEventId;
	}

	public BmField getHasDocuments() {
		return hasDocuments;
	}

	public void setHasDocuments(BmField hasDocuments) {
		this.hasDocuments = hasDocuments;
	}

	public BmField getWFlowFunnelId() {
		return wFlowFunnelId;
	}

	public void setWFlowFunnelId(BmField wFlowFunnelId) {
		this.wFlowFunnelId = wFlowFunnelId;
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

	public BmoWFlowFunnel getBmoWFlowFunnel() {
		return bmoWFlowFunnel;
	}

	public void setBmoWFlowFunnel(BmoWFlowFunnel bmoWFlowFunnel) {
		this.bmoWFlowFunnel = bmoWFlowFunnel;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

}
