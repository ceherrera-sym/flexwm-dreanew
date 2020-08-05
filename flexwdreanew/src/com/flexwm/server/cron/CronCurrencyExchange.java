package com.flexwm.server.cron;

import com.flexwm.server.fi.PmCurrency;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronCurrencyExchange implements ICronJob {
	
	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmCurrency pmCurrency = new PmCurrency(sFParams);
		boolean success = true;
		String errors="";
		try {
			pmCurrency.updateCurrencyExchanges(new BmUpdateResult());
		} catch (SFException e) {
			errors += e.getMessage();
			success = false;
		}
		
		if (success) 
			return this.getClass().getName() + "-execute(): OK";
		else 
			return this.getClass().getName() + "-execute() - ERROR :" +errors;

	}
}
