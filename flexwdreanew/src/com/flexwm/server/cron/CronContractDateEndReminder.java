package com.flexwm.server.cron;

import com.flexwm.server.ar.PmPropertyRental;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronContractDateEndReminder implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
		pmPropertyRental.prepareRemindersEndContract();
		return this.getClass().getName() + "-execute(): OK";
	}
}
