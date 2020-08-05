package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowOpportunityWin implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se gana la oportunidad dependiendo del avance de la tarea
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoWFlowStep.getBmoWFlow().getCallerId().toInteger());

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoOpportunity.getWFlowId().toInteger());

		// Si la oportunidad no esta ganada y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoOpportunity.getStatus().toChar() != BmoOpportunity.STATUS_WON) {
			bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_WON);
			pmOpportunity.save(pmConn, bmoOpportunity, bmUpdateResult);

			// Deshabilitar flujo
			bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
			pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
		}
		// Si el avance es menor a 100 y la oportunidad esta ganada, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_WON) {
			bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_REVISION);
			pmOpportunity.save(pmConn, bmoOpportunity, bmUpdateResult);

			// Habilitar flujo
			bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_ACTIVE);
			pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
		}
	}
}
