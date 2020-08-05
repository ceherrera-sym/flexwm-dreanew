package com.flexwm.server.cm;

import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowProjectOrderRelease implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Se obtiene el proyecto
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		System.out.println(" wflow " + bmoWFlowStep.getBmoWFlow().getCallerId().toInteger());
		String comment = " a través de la Tarea: " + bmoWFlowStep.getName().toString();
		bmoProject = (BmoProject)pmProject.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		// Obtener el pedido del proyecto
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());

		// Si el pedido no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			if (bmoOrder.getLockStatus().toChar() == BmoOrder.LOCKSTATUS_CONFLICT)
				bmUpdateResult.addError(bmoWFlowStep.getName().getName(), "No se puede Autorizar el Pedido. Existen Conflictos.");
			else {
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
				pmOrder.addDataLog(pmConn, bmoOrder, "Autorización del Pedido" + comment, bmUpdateResult);
			}
		}

		// Si el avance es menor a 100 y el pedido esta autorizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
			pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
			pmOrder.addDataLog(pmConn, bmoOrder, "Pedido En Revisión" + comment, bmUpdateResult);
		}
	}
}
