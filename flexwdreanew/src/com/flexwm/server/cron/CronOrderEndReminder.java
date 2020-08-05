package com.flexwm.server.cron;

import com.flexwm.server.op.PmOrder;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronOrderEndReminder implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmOrder pmOrder = new PmOrder(sFParams);
		pmOrder.prepareRemindersOrderEnd();
		return this.getClass().getName() + "-execute(): OK";
	}
}
