package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowOpportunityLoose implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se gana la oportunidad dependiendo del avance de la tarea
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoWFlowStep.getBmoWFlow().getCallerId().toInteger());

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoOpportunity.getWFlowId().toInteger());
		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);

		// Obtiene datos del cliente
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOpportunity.getCustomerId().toInteger());

		// Si la oportunidad no esta ganada o perdida y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOpportunity.getStatus().toChar() != BmoOpportunity.STATUS_LOST
				|| bmoOpportunity.getStatus().toChar() != BmoOpportunity.STATUS_WON) {
			bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_LOST);
			pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);	
			String motives = "";

			//if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			// En caso de establecer como perdida la oportunidad, forzar ingreso del motivo
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST) {
				if (!(bmoOpportunity.getLoseMotiveId().toInteger() > 0))
					bmUpdateResult.addError(bmoOpportunity.getLoseMotiveId().getName(), "Debe asigarse el Motivo de Perder en la Oportunidad.");
				else{
					PmLoseMotive pmLoseMotive = new PmLoseMotive(sFParams);
					BmoLoseMotive bmoLoseMotive = (BmoLoseMotive)pmLoseMotive.get(bmoOpportunity.getLoseMotiveId().toInteger());
					motives = bmoLoseMotive.getName().toString() + " | "+ bmoOpportunity.getLoseComments().toString();
				}
			}
			//}
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Oportunidad Perdida a través de la Tarea: " + bmoWFlowStep.getName().toString() + ", Motivo: " + motives);

			//Cancelar cotización
			PmQuote pmQuote = new PmQuote(sFParams);
			BmoQuote bmoQuote = new BmoQuote();
			bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());
			if (bmoQuote.getWFlowId().toInteger() > 0){
				bmoQuote.getStatus().setValue(BmoQuote.STATUS_CANCELLED);
				pmQuote.saveSimple(pmConn, bmoQuote, bmUpdateResult);
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como Cancelada.");
			}

			// Deshabilitar flujo
			bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
			pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

			//Actualizar estatus del cliente
			pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
		}
		// Si el avance es menor a 100 y la oportunidad esta autorizada, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST) {
			//Si tiene permiso para cambiar estatus de la oportunidad cambia estatus desde la tarea
			if (sFParams.hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESTATUS)) {
				bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_REVISION);
				pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);	

				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Oportunidad En Revisión a través de la Tarea: " + bmoWFlowStep.getName().toString());

				// Habilitar flujo
				bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_ACTIVE);
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

				//Actualizar estatus del cliente
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			}
		}
	}
}
