package com.flexwm.server.op;

import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowOrderFinalize implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se finaliza el pedido dependiendo avance de la tarea
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder = (BmoOrder)pmOrder.getBy(pmConn, bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(), bmoOrder.getIdFieldName());

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoOrder.getWFlowId().toInteger());

		// Bitacora WFlow
		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);

		// Obtiene datos del cliente
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOrder.getCustomerId().toInteger());
		
		// Consultoria
		PmConsultancy pmConsultancy = new PmConsultancy(sFParams);
		BmoConsultancy bmoConsultancy = new BmoConsultancy();

		// Si el pedido no esta finalizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOrder.getStatus().toChar() != BmoOrder.STATUS_FINISHED) {
			if (sFParams.hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUSFINISHED)) {
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);		

				// Consultoria
				bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
				bmoConsultancy.getStatus().setValue(BmoConsultancy.STATUS_FINISHED);
				pmConsultancy.saveSimple(pmConn, bmoConsultancy, bmUpdateResult);
				
				// Deshabilitar flujo
				bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

				// Colocar bitácora
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
						"Pedido Finalizado a través de la Tarea: " + bmoWFlowStep.getName().toString());

				//Actualizar estatus del cliente
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			} else
				bmUpdateResult.addMsg("<b>No tiene permisos para cambiar el estatus a Finalizado.</b>");
		}
		// Si el avance es menor a 100 y el pedido esta finalizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {
			if (sFParams.hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUSUNFINISHED)) {
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
				
				// Consultoria
				bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
				bmoConsultancy.getStatus().setValue(BmoConsultancy.STATUS_AUTHORIZED);
				pmConsultancy.saveSimple(pmConn, bmoConsultancy, bmUpdateResult);

				// Habilitar flujo
				bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_ACTIVE);
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

				// Colocar bitácora
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
						"Se regresa el estatus de Pedido a Autorizado a través de la Tarea: " + bmoWFlowStep.getName().toString());

				//Actualizar estatus del cliente
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			} else
				bmUpdateResult.addMsg("<b>No tiene permisos para quitar el estatus de Finalizado.</b>");
		}
	}
}
