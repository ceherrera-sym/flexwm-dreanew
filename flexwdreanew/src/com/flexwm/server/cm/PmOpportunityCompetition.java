/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoCompetition;
import com.flexwm.shared.cm.BmoOpportunityCompetition;
import java.util.ArrayList;
import java.util.Arrays;


public class PmOpportunityCompetition extends PmObject {
	BmoOpportunityCompetition bmoOpportunityCompetition;

	public PmOpportunityCompetition(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOpportunityCompetition = new BmoOpportunityCompetition();
		setBmObject(bmoOpportunityCompetition);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOpportunityCompetition.getCompetitionId(), bmoOpportunityCompetition.getBmoCompetition())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {

BmoOpportunityCompetition bmoOpportunityCompetition = (BmoOpportunityCompetition) autoPopulate(pmConn, new BmoOpportunityCompetition());
		// Bmooppocompetition
		BmoCompetition bmoCompetition = new BmoCompetition();
		if (pmConn.getInt(bmoCompetition.getIdFieldName()) > 0) 
			bmoOpportunityCompetition.setBmoCompetition((BmoCompetition) new PmCompetition(getSFParams()).populate(pmConn));
		else 
			bmoOpportunityCompetition.setBmoCompetition(bmoCompetition);

		return bmoOpportunityCompetition;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoOpportunityCompetition bmoOpportunityCompetition = (BmoOpportunityCompetition)bmObject;
		if(bmoOpportunityCompetition.getCompetitionId().toInteger()>0) {
		if (competitionExist(pmConn, bmoOpportunityCompetition.getOpportunityId().toInteger(),
				bmoOpportunityCompetition.getCompetitionId().toInteger(), bmUpdateResult))
			bmUpdateResult.addError(bmoOpportunityCompetition.getCompetitionId().getName(), "La competencia ya existe en la oportunidad.");	

		
		}else {
			bmUpdateResult.addError(bmoOpportunityCompetition.getCompetitionId().getName(), "Seleccione una competencia");	

		}
		super.save(pmConn, bmoOpportunityCompetition, bmUpdateResult);
		return bmUpdateResult;
	}

	public boolean competitionExist(PmConn pmConn, int opportunityId, int competitionId,  BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;
		sql = " SELECT COUNT(opcm_compid) as items FROM opportunitycompetition " +
				" WHERE opcm_opportunityid = " + opportunityId + 
				" AND opcm_competitionid  = " + competitionId ;
		pmConn.doFetch(sql);

		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;		
	}
}
