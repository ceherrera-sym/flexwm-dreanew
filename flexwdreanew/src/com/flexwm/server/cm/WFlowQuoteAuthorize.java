package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class WFlowQuoteAuthorize implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Autorizar Cotización 
		PmQuote pmQuote = new PmQuote(sFParams);
		BmoQuote bmoQuote = new BmoQuote();
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);
		//Traer Oportunidad
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoOpportunity.getIdFieldName());
		//Traer Cotización
		bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
		
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			bmoQuote.getStatus().setValue(BmoQuote.STATUS_AUTHORIZED);
			pmQuote.save(pmConn, bmoQuote, bmUpdateResult);		
			if (!bmUpdateResult.hasErrors())
				pmQuote.addDataLog(pmConn, bmoQuote, bmoOpportunity.getWFlowId().toInteger(), bmUpdateResult);
		
		} else if (bmoWFlowStep.getProgress().toInteger() < 100 &&  bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED ) {
			bmoQuote.getStatus().setValue(BmoQuote.STATUS_REVISION);
			pmQuote.save(pmConn, bmoQuote, bmUpdateResult);
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), 
					BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como En Revisión a través de la Tarea: " + bmoWFlowStep.getName().toString());
		}
		
		
	}

}
