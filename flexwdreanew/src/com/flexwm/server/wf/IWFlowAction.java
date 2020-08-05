package com.flexwm.server.wf;

import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.flexwm.shared.wf.BmoWFlowStep;

public interface IWFlowAction {

	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmWFlowStep, BmUpdateResult bmUpdateResult) throws SFException;
	
}
