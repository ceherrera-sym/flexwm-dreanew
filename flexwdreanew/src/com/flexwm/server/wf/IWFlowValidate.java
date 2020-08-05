package com.flexwm.server.wf;

import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.flexwm.shared.wf.BmoWFlowStep;

public interface IWFlowValidate {

	public void validate(SFParams sFParams, BmoWFlowStep bmWFlowStep, BmUpdateResult bmUpdateResult) throws SFException;
	
}
