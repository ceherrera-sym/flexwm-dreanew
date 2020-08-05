/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowPhase;

public class PmWFlowPhase extends PmObject {
	BmoWFlowPhase bmoWFlowPhase;
	
	public PmWFlowPhase(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowPhase = new BmoWFlowPhase();
		setBmObject(bmoWFlowPhase);
	}
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) {
			if (getSFParams().getSelectedCompanyId() > 0) {
				filters = "( "+
						" 	wfph_wflowcategoryid IN " +
						" 	(SELECT wfcc_wflowcategoryid FROM wflowcategorycompanies WHERE wfcc_companyid = " + getSFParams().getSelectedCompanyId() + ")  " +
						" )";
			}
			
			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
				if (filters.length() > 0) filters += " AND ";
				filters += " (" +
						"		(" + 
						"			wfph_wflowcategoryid IN  " + 
						"            ( " + 
						"				SELECT wfcc_wflowcategoryid FROM wflowcategorycompanies WHERE wfcc_companyid IN " +
						"					(" +
						" 						SELECT uscp_companyid FROM usercompanies where uscp_userid = " + getSFParams().getLoginInfo().getUserId() +
						" 					) " + 
						"			) " + 
						"   	) "
						+ "	 ) ";
			}
		}
		return filters;
	}
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoWFlowPhase());
	}
	
	public int getFirstPhaseId(PmConn pmConn, int wFlowCategoryId) throws SFPmException {
		int wFlowPhaseId = 0;
		
		String sql = "SELECT wfph_wflowphaseid FROM wflowphases "
				+ " WHERE "
				+ " wfph_wflowcategoryid = " + wFlowCategoryId 
				+ " ORDER BY wfph_sequence ASC ";
		
		pmConn.doFetch(sql);
		
		System.out.println("\n" + sql + "\n");
		
		// Si es rama de otro paso, revisar el avance de los anteriores
		if (pmConn.next()) {
			wFlowPhaseId = pmConn.getInt(1);
		}
		return wFlowPhaseId;
	}
}
