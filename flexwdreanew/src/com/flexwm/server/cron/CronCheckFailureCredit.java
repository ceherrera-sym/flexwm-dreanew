package com.flexwm.server.cron;

import com.flexwm.server.fi.PmRaccount;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronCheckFailureCredit implements ICronJob {

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		//Revisar si existe falla
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		pmRaccount.checkFailure("");
		return this.getClass().getName() + "-execute(): OK";
	}
}
