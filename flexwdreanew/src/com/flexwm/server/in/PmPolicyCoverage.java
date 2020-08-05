/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.in;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.in.BmoCoverage;
import com.flexwm.shared.in.BmoPolicyCoverage;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmPolicyCoverage extends PmObject {
	BmoPolicyCoverage bmoPolicyCoverage = new BmoPolicyCoverage();
	
	public PmPolicyCoverage(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoPolicyCoverage);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPolicyCoverage.getCoverageId(), bmoPolicyCoverage.getBmoCoverage())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPolicyCoverage bmoPolicyCoverage = (BmoPolicyCoverage)autoPopulate(pmConn, new BmoPolicyCoverage());

		// BmoCoverage
		BmoCoverage bmoCoverage = new BmoCoverage();
		int coverageId = (int)pmConn.getInt(bmoCoverage.getIdFieldName());
		if (coverageId > 0) bmoPolicyCoverage.setBmoCoverage((BmoCoverage) new PmCoverage(getSFParams()).populate(pmConn));
		else bmoPolicyCoverage.setBmoCoverage(bmoCoverage);
		
		return bmoPolicyCoverage;
	}
}