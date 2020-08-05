package com.flexwm.server.cron;

import com.symgae.server.cron.ICronJob;
import com.flexwm.server.fi.PmBankAccount;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronBankAccountBalance implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		pmBankAccount.batchUpdateBalance();
		return this.getClass().getName() + "-execute(): OK";
	}
}
