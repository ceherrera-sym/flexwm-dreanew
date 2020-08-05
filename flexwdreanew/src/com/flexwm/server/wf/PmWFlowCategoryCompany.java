package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

public class PmWFlowCategoryCompany extends PmObject {
	BmoWFlowCategoryCompany bmoWFlowCategoryCompany;

	public PmWFlowCategoryCompany(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();
		setBmObject(bmoWFlowCategoryCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowCategoryCompany.getCompanyId(), bmoWFlowCategoryCompany.getBmoCompany())
				)));
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( " +					
					"		 wfcc_companyid IN " + 
					"		(" + 
					"			SELECT uscp_companyid from usercompanies WHERE uscp_userid = " + loggedUserId +
					"        ) " +
					"   ) ";
		}
		
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
				filters += " ( " +
							"   wfcc_companyid = " + getSFParams().getSelectedCompanyId() +
							" )";
			
		}
		
		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowCategoryCompany bmoWFlowCategoryCompany = (BmoWFlowCategoryCompany)autoPopulate(pmConn, new BmoWFlowCategoryCompany());

		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoWFlowCategoryCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoWFlowCategoryCompany.setBmoCompany(bmoCompany);


		return bmoWFlowCategoryCompany;
	}

}
