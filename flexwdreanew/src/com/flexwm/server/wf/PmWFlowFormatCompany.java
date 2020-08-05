package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;

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
import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowFormatCompany;


public class PmWFlowFormatCompany  extends PmObject{
	BmoWFlowFormatCompany bmoWFlowFormatCompany;
	
	public PmWFlowFormatCompany(SFParams sFParams) throws SFPmException {
		super(sFParams);
		
		bmoWFlowFormatCompany = new BmoWFlowFormatCompany();
		setBmObject(bmoWFlowFormatCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowFormatCompany.getCompanyId(), bmoWFlowFormatCompany.getBmoCompany()) ,
				new PmJoin(bmoWFlowFormatCompany.getWflowformatId(), bmoWFlowFormatCompany.getBmoWFlowFormat()),
				new PmJoin(bmoWFlowFormatCompany.getBmoWFlowFormat().getWflowTypeId(), bmoWFlowFormatCompany.getBmoWFlowFormat().getBmoWFlowType()),
				new PmJoin(bmoWFlowFormatCompany.getBmoWFlowFormat().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowFormatCompany.getBmoWFlowFormat().getBmoWFlowType().getBmoWFlowCategory())
				)));
	}
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( " +					
					"		 wffc_companyid IN " + 
					"		(" + 
					"			SELECT uscp_companyid from usercompanies WHERE uscp_userid = " + loggedUserId +
					"        ) " +
					"   ) ";
		}
		
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
				filters += " ( " +
							"   wffc_companyid = " + getSFParams().getSelectedCompanyId() +
							" )";
			
		}
		
		return filters;
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowFormatCompany bmoWFlowFormatCompany = (BmoWFlowFormatCompany)autoPopulate(pmConn, new BmoWFlowFormatCompany());
	
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoWFlowFormatCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoWFlowFormatCompany.setBmoCompany(bmoCompany);
		
	
	
			
		return bmoWFlowFormatCompany;
	}
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)
			throws SFException {
		BmoWFlowFormat bmoWFlowFormat = (BmoWFlowFormat)bmObject;
		
		if ( action.equals(BmoWFlowFormatCompany.ACTION_EXISTCOMPANY) ) {
			bmUpdateResult.setMsg(existCompany(bmoWFlowFormat));
		}
		
		return bmUpdateResult;
	}
	
	private String existCompany(BmoWFlowFormat bmoWFlowFormat) throws SFPmException {
		String result = "";
		PmConn pmConn = new PmConn(getSFParams());

		pmConn.open();

		String sql = "SELECT wftc_wflowtypecompanyid FROM wflowtypecompany WHERE wftc_wflowtypeid = " + bmoWFlowFormat.getWflowTypeId().toInteger();
		
		pmConn.doFetch(sql);

		if(pmConn.next())result = BmoWFlowFormatCompany.ACTION_FILTERBYTYPECOMP;
		else {
			 sql = "SELECT wfcc_wflowcategorycompanyid FROM  wflowcategorycompany WHERE wfcc_wflowcategoryid = " + bmoWFlowFormat.getBmoWFlowType().getWFlowCategoryId().toInteger();
			 
			 pmConn.doFetch(sql);
			 if(pmConn.next())result = BmoWFlowFormatCompany.ACTION_FILTERBYCATEGORYCOMP;
		}

		pmConn.close();

		return result;
	}

}
