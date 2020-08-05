package com.flexwm.server.cm;

import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.cm.BmoOpportunity;
import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;
import com.flexwm.shared.wf.BmoWFlowLog;


public class FVOpportunityQuote implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		try {
			PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
			BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(Integer.parseInt(value));
			bmUpdateResult = pmOpportunity.canPublishQuote(bmoFormat, bmoOpportunity, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()){
				PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);
				pmWFlowLog.addLog(new BmUpdateResult(), bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Se Visualizó para Impresión el formato: " + bmoFormat.getName());
			}
		} catch (SFException e) {
			bmUpdateResult.addError("", e.toString());
		}
		return bmUpdateResult;
	}

}
