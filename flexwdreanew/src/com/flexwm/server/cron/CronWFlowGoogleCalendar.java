package com.flexwm.server.cron;

import java.util.Iterator;
import com.symgae.server.PmConn;
import com.symgae.server.cron.ICronJob;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowUser;


public class CronWFlowGoogleCalendar implements ICronJob {

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}

	private String execute(SFParams sFParams) throws SFException {
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		PmWFlow pmWFlow = new PmWFlow(sFParams);
		BmoWFlow bmoWFlow = new BmoWFlow();

		BmFilter activeWFlowFilter = new BmFilter();
		activeWFlowFilter.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getStatus(), "" + BmoWFlow.STATUS_ACTIVE);
		Iterator<BmObject> itWFlow = pmWFlow.list(pmConn, activeWFlowFilter).iterator();			

		// Revisar cada flujo
		while (itWFlow.hasNext()) {
			bmoWFlow = (BmoWFlow)itWFlow.next();
			pmWFlow.updateGoogleCalendar(pmConn, bmoWFlow, bmUpdateResult);
		}

		// Revisar cada usuario de flujo, en flujo activo
		PmWFlowUser pmWFlowUser = new PmWFlowUser(sFParams);
		Iterator<BmObject> itWFlowUser = pmWFlowUser.list(pmConn).iterator();
		while (itWFlowUser.hasNext()) {
			BmoWFlowUser bmoWFlowUser = (BmoWFlowUser)itWFlowUser.next();
			bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowUser.getWFlowId().toInteger());
			if (bmoWFlow.getStatus().equals(BmoWFlow.STATUS_ACTIVE)
					&& (bmoWFlowUser.getUserId().toInteger() > 0)
					&& (bmoWFlowUser.getLockStart().toString().length() > 0)) 
				pmWFlowUser.updateGoogleCalendar(pmConn, bmoWFlow, bmoWFlowUser, bmUpdateResult);
		}

		pmConn.close();
		return this.getClass().getName() + "-execute(): OK";
	}
}
