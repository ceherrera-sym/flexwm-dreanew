package com.flexwm.server.cron;

import com.flexwm.server.co.PmPropertyTax;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class CronPropertyTaxReminder  implements ICronJob{

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmPropertyTax pmPropertyTax = new PmPropertyTax(sFParams);
		pmPropertyTax.statusReminders();
		return this.getClass().getName() + "-execute(): OK";
	}

}
