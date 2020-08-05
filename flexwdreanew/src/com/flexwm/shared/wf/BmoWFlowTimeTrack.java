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
import com.symgae.shared.BmOrder;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.BmField;

public class BmoWFlowTimeTrack extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private BmField description, minutes, startDate, endDate, 
		userId, wFlowId, wFlowStepId;

	private BmoUser bmoUser = new BmoUser();
	private BmoWFlow bmoWFlow = new BmoWFlow();

	public static String ACTION_LASTWFLOWTRACK = "LASTWFLOWTRACK";
	
	public BmoWFlowTimeTrack() {
		super("com.flexwm.server.wf.PmWFlowTimeTrack", "wflowtimetracks", "wflowtimetrackid", "WFTT", "Rastreo WFlow");

		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		minutes = setField("minutes", "", "Duración (Min)", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		userId = setField("userid", "", "Usuario", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowStepId = setField("wflowstepid", "", "Tarea", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlow().getName(),
				getDescription(),
				getBmoUser().getCode(),
				getStartDate(),
				getMinutes()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getMinutes() {
		return minutes;
	}

	public void setMinutes(BmField minutes) {
		this.minutes = minutes;
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

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getWFlowStepId() {
		return wFlowStepId;
	}

	public void setWFlowStepId(BmField wFlowStepId) {
		this.wFlowStepId = wFlowStepId;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}
}
