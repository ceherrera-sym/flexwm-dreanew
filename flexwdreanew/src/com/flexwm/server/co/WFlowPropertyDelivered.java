package com.flexwm.server.co;

import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowPropertyDelivered implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Coloca Fechas de entrega en la venta de inmueble dependiendo del avance de la tarea
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		bmoPropertySale = (BmoPropertySale)pmPropertySale.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoPropertySale.getOrderId().getName());

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoPropertySale.getWFlowId().toInteger());

		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);

		// Obtener el pedido de la venta
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());

		// Si la venta de inmueble no esta cancelada y la tarea esta al 100, cambia status y fechas
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoPropertySale.getStatus().toChar() != BmoPropertySale.STATUS_CANCELLED) {
			if(bmoOrder.getStatus().toChar() ==  BmoOrder.STATUS_AUTHORIZED){
				bmoPropertySale.getEndDate().setValue(bmoWFlowStep.getEnddate().toString());
				pmPropertySale.saveSimple(pmConn, bmoPropertySale, bmUpdateResult);

				bmoOrder.getLockEnd().setValue(bmoWFlowStep.getEnddate().toString());
				bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_TOTAL);
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);	

				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
						"Pedido Finalizado a través de la Tarea: " + bmoWFlowStep.getName().toString());

				// Deshabilitar flujo
				bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
			}else{
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No se puede Finalizar el Pedido, no está Autorizado");
			}
		}
		// Si el avance es menor a 100 y la venta no esta cancelada, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoPropertySale.getStatus().toChar() != BmoPropertySale.STATUS_CANCELLED) {
			//Si el pedido estaba terminado, regresarlo al estatus anterior(autorizado)
			if(bmoOrder.getStatus().toChar() ==  BmoOrder.STATUS_FINISHED){
				bmoPropertySale.getEndDate().setValue("");
				pmPropertySale.saveSimple(pmConn, bmoPropertySale, bmUpdateResult);

				bmoOrder.getLockEnd().setValue("");
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
				bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_PENDING);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);	

				// Habilitar flujo
				bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_ACTIVE);
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
			}
		}
	}
}
