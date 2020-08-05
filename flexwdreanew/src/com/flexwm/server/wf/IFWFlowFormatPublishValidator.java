package com.flexwm.server.wf;

import com.flexwm.shared.wf.BmoWFlowFormat;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFParams;

public interface IFWFlowFormatPublishValidator {
	public BmUpdateResult canPublish(SFParams sFParams, BmoWFlowFormat bmoWFlowFormat, String value, BmUpdateResult bmUpdateResult);
}
