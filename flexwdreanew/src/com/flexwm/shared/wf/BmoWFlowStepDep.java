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
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.shared.BmField;

public class BmoWFlowStepDep extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmoWFlowStep bmoWFlowStep;
	private BmField childStepId, wFlowStepId;

	
	public BmoWFlowStepDep() {
		super("com.flexwm.server.wf.PmWFlowStepDep", "wflowstepdeps", "wflowstepdepid", "WFSZ", "Depend. Tareas");
		
		childStepId = setField("childid", "", "Tarea Rama", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowStepId = setField("wflowstepid", "", "Tarea Raíz", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bmoWFlowStep = new BmoWFlowStep();		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
					bmoWFlowStep.getSequence(),
					bmoWFlowStep.getName(),
					bmoWFlowStep.getBmoWFlowPhase().getName()
						));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmoWFlowStep getBmoWFlowStep() {
		return bmoWFlowStep;
	}

	public void setBmoWFlowStep(BmoWFlowStep bmoWFlowStep) {
		this.bmoWFlowStep = bmoWFlowStep;
	}

	public BmField getChildStepId() {
		return childStepId;
	}

	public void setChildStepId(BmField childStepId) {
		this.childStepId = childStepId;
	}

	public BmField getWFlowStepId() {
		return wFlowStepId;
	}

	public void setWFlowStepId(BmField wFlowStepId) {
		this.wFlowStepId = wFlowStepId;
	}
}
