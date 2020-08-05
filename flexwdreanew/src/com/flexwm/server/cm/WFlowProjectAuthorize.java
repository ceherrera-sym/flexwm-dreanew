package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowProjectAuthorize implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se autoriza el proyecto dependiendo avance de la tarea
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);
		bmoProject = (BmoProject)pmProject.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		// Si el proyecto no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoProject.getStatus().toChar() != BmoProject.STATUS_AUTHORIZED) {
			bmoProject.getStatus().setValue(BmoProject.STATUS_AUTHORIZED);
			pmProject.save(pmConn, bmoProject, bmUpdateResult);
		}
		// Si el avance es menor a 100 y el proyecto esta autorizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoProject.getStatus().toChar() == BmoProject.STATUS_AUTHORIZED) {
			bmoProject.getStatus().setValue(BmoProject.STATUS_REVISION);
			pmProject.saveSimple(pmConn, bmoProject, bmUpdateResult);	
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoProject.getWFlowId().toInteger(), 
					BmoWFlowLog.TYPE_OTHER, "El Proyecto se guardó como En Revisión a través de la Tarea: " + bmoWFlowStep.getName().toString());
		}

	}
}
