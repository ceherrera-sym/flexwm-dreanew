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


public class WFlowProjectOrderDelivered implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Se obtiene el proyecto
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		bmoProject = (BmoProject)pmProject.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		// Obtener el pedido del proyecto
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());

		// Si el pedido no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOrder.getDeliveryStatus().toChar() != BmoOrder.DELIVERYSTATUS_TOTAL) {
			bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);			
		}

		// Si el avance es menor a 100 y el pedido esta autorizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);	
		}
	}
}
