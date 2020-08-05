package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowProjectFinalize implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se autoriza el proyecto dependiendo avance de la tarea
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		bmoProject = (BmoProject)pmProject.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoProject.getWFlowId().toInteger());

		// Si el proyecto no esta finalizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoProject.getStatus().toChar() != BmoProject.STATUS_FINISHED) {
			bmoProject.getStatus().setValue(BmoProject.STATUS_FINISHED);
			pmProject.saveSimple(pmConn, bmoProject, bmUpdateResult);		

			// Deshabilitar flujo
			bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
			pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
		}
		// Si el avance es menor a 100 y el proyecto esta finalizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoProject.getStatus().toChar() == BmoProject.STATUS_FINISHED) {
			bmoProject.getStatus().setValue(BmoProject.STATUS_AUTHORIZED);
			pmProject.save(pmConn, bmoProject, bmUpdateResult);	

			// Habilitar flujo
			bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_ACTIVE);
			pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
		}
	}
}
