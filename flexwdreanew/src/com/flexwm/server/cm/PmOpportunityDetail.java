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
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoOpportunityDetail;


public class PmOpportunityDetail extends PmObject {
	BmoOpportunityDetail bmoOpportunityDetail;

	public PmOpportunityDetail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOpportunityDetail = new BmoOpportunityDetail();
		setBmObject(bmoOpportunityDetail);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoOpportunityDetail bmoOpportunityDetail = (BmoOpportunityDetail) autoPopulate(pmConn, new BmoOpportunityDetail());

		return bmoOpportunityDetail;		
	}

	public boolean opportunityDetailExists(PmConn pmConn, int opportunityId) throws SFPmException {
		pmConn.doFetch("SELECT opde_opportunitydetailid FROM opportunitydetails WHERE opde_opportunityid = " + opportunityId);
		return pmConn.next();
	}

}
