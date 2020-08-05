package com.flexwm.server.cron;

import com.flexwm.server.cm.PmOpportunity;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class CronOppoExpiredReminder implements ICronJob{
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	private String execute(SFParams sFParams) throws SFException {
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		pmOpportunity.remindExpired();
		return this.getClass().getName() + "-execute(): OK";
	}
}
