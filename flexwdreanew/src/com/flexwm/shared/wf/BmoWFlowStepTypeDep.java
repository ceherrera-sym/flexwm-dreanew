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
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.symgae.shared.BmField;

public class BmoWFlowStepTypeDep extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmoWFlowStepType bmoWFlowStepType;
	private BmField childStepTypeId, wFlowStepTypeId;

	public BmoWFlowStepTypeDep() {
		super("com.flexwm.server.wf.PmWFlowStepTypeDep", "wflowsteptypedeps", "wflowsteptypedepid", "WFSD", "Depend. Tareas");
		
		childStepTypeId = setField("childid", "", "Tipo de Flujo Rama", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowStepTypeId = setField("wflowsteptypeid", "", "Tipo de Flujo Raíz", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bmoWFlowStepType = new BmoWFlowStepType();
		
		// Campos de ordenamiento
		//setOrderFields(new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getSequence(), BmOrder.ASC))));
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {		
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlowStepType().getBmoWFlowPhase().getSequence(),
				getBmoWFlowStepType().getSequence(),
				getBmoWFlowStepType().getName(),
				getBmoWFlowStepType().getBmoWFlowPhase().getName()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {		
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlowStepType().getBmoWFlowPhase().getSequence(),
				getBmoWFlowStepType().getSequence(),
				getBmoWFlowStepType().getName(),
				getBmoWFlowStepType().getBmoWFlowPhase().getName()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getBmoWFlowStepType().getBmoWFlowPhase().getKind(), getBmoWFlowStepType().getBmoWFlowPhase().getSequence(), BmOrder.ASC),
				new BmOrder(getKind(), getBmoWFlowStepType().getSequence(), BmOrder.ASC)
				));
	}

	public BmoWFlowStepType getBmoWFlowStepType() {
		return bmoWFlowStepType;
	}

	public void setBmoWFlowStepType(BmoWFlowStepType bmoWFlowStepType) {
		this.bmoWFlowStepType = bmoWFlowStepType;
	}

	public BmField getChildStepTypeId() {
		return childStepTypeId;
	}

	public void setChildStepTypeId(BmField childStepTypeId) {
		this.childStepTypeId = childStepTypeId;
	}

	public BmField getWFlowStepTypeId() {
		return wFlowStepTypeId;
	}

	public void setWFlowStepTypeId(BmField wFlowStepTypeId) {
		this.wFlowStepTypeId = wFlowStepTypeId;
	}
	
	
}
