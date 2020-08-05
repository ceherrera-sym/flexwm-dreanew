package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

public class PmWFlowTypeCompany extends PmObject {
	BmoWFlowTypeCompany bmoWFlowTypeCompany;
	
	public PmWFlowTypeCompany(SFParams sFParams) throws SFPmException {
		super(sFParams);
		
		bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
		setBmObject(bmoWFlowTypeCompany);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowTypeCompany.getCompanyId(), bmoWFlowTypeCompany.getBmoCompany()),
				new PmJoin(bmoWFlowTypeCompany.getWflowTypeId(), bmoWFlowTypeCompany.getBmoWFlowType()),
				new PmJoin(bmoWFlowTypeCompany.getBmoWFlowType().getWFlowCategoryId(), bmoWFlowTypeCompany.getBmoWFlowType().getBmoWFlowCategory())
				)));
	}	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( " +					
					"		 wftc_companyid IN " + 
					"		(" + 
					"			SELECT uscp_companyid from usercompanies WHERE uscp_userid = " + loggedUserId +
					"        ) " +
					"   ) ";
		}		
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
				filters += " ( " +
							"   wftc_companyid = " + getSFParams().getSelectedCompanyId() +
							" )";			
		}
		
		return filters;
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowTypeCompany bmoWFlowTypeCompany = (BmoWFlowTypeCompany)autoPopulate(pmConn, new BmoWFlowTypeCompany());
	
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoWFlowTypeCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoWFlowTypeCompany.setBmoCompany(bmoCompany);
	
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		if ((int)pmConn.getInt(bmoWFlowType.getIdFieldName()) > 0)
			bmoWFlowTypeCompany.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else
			bmoWFlowTypeCompany.setBmoWFlowType(bmoWFlowType);
		
		return bmoWFlowTypeCompany;
	}
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)
			throws SFException {
		BmoWFlowType bmoWFlowType = (BmoWFlowType)bmObject;
		
		
		if ( action.equals( BmoWFlowTypeCompany.ACTION_EXISTCOMPANY ) ) {
			bmUpdateResult.setMsg(""+existCompany(bmoWFlowType));
		}
		
		return bmUpdateResult;
	}

	private int existCompany(BmoWFlowType bmoWFlowType) throws SFPmException {
		int result = 0;
		PmConn pmConn = new PmConn(getSFParams());

		pmConn.open();

		String sql = "SELECT wfcc_wflowcategorycompanyid FROM  wflowcategorycompanies WHERE wfcc_wflowcategoryid = " + bmoWFlowType.getWFlowCategoryId().toInteger();
		
		pmConn.doFetch(sql);

		if(pmConn.next())result = 1;

		pmConn.close();

		return result;
	}
}
