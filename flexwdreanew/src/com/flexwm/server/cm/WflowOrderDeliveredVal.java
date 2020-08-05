package com.flexwm.server.cm;

import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.server.wf.IWFlowValidate;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WflowOrderDeliveredVal implements IWFlowValidate {

	@Override
	public void validate(SFParams sFParams, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Se obtiene el proyecto
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		bmoProject = (BmoProject)pmProject.getBy(bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		// Obtener el pedido del proyecto
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());

		// Si el pedido no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOrder.getDeliveryStatus().toChar() != BmoOrder.DELIVERYSTATUS_TOTAL) {
			bmUpdateResult.addError(bmoWFlowStep.getName().getName(), "No se ha entregado el Pedido Completo.");		
		}	
	}

}
