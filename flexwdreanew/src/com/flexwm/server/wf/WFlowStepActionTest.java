package com.flexwm.server.wf;

import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.server.wf.IWFlowAction;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class WFlowStepActionTest implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		
		// Si el proyecto no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100) {
			System.out.println("La tarea fue activada al 100%");			
		}
		// Si el avance es menor a 100 y el proyecto esta autorizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100) {
			System.out.println("La tarea fue activada a menos del 100%");	
		}
	}
}
