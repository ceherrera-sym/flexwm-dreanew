package com.flexwm.server.cron;

import com.symgae.server.cron.ICronJob;
import com.flexwm.server.wf.PmWFlowStep;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronWFlowStep implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
		pmWFlowStep.prepareReminders();
		return this.getClass().getName() + "-execute(): OK";
	}
}
