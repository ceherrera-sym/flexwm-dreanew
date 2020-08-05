package com.flexwm.server.cron;

import com.symgae.server.cron.ICronJob;
import com.flexwm.server.cv.PmActivity;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronActivity implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmActivity pmActivity = new PmActivity(sFParams);
		pmActivity.prepareReminders();
		return this.getClass().getName() + "-execute(): OK";
	}
}
