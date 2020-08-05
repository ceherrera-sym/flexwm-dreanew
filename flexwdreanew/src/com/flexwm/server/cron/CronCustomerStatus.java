package com.flexwm.server.cron;

import com.flexwm.server.cm.PmCustomer;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronCustomerStatus implements ICronJob {

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}
	
	private String execute(SFParams sFParams) throws SFException {
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		pmCustomer.batchUpdateStatus();
		return this.getClass().getName() + "-execute(): OK";
	}
}
