package com.flexwm.server;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.BmoCompanySalesman;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfile;


public class PmCompanySalesman extends PmObject {
	BmoCompanySalesman bmoCompanySalesman;

	public PmCompanySalesman(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoCompanySalesman = new BmoCompanySalesman();
		setBmObject(bmoCompanySalesman);
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCompanySalesman.getProfileId(), bmoCompanySalesman.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCompanySalesman bmoCompanySalesman = (BmoCompanySalesman)autoPopulate(pmConn, new BmoCompanySalesman());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		int profileId = pmConn.getInt(bmoProfile.getIdFieldName());
		if (profileId > 0) bmoCompanySalesman.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoCompanySalesman.setBmoProfile(bmoProfile);

		return bmoCompanySalesman;
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
//		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de usuarios propios en jerarquia
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {


//			filters = " ( user_userid in (" +
//			
//					 	")) ";
		}		

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
//			if (filters.length() > 0) filters += " AND ";
//			filters += " user_userid IN (SELECT uscp_userid FROM usercompanies WHERE uscp_companyid = " + getSFParams().getSelectedCompanyId() + ")";
		}

		return filters;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {

		bmoCompanySalesman = (BmoCompanySalesman)bmObject;
		boolean newRecord = false;
		if (!(bmoCompanySalesman.getId() > 0)) {
			newRecord = true;
		}

		if (newRecord && existProfileAssigned(pmConn, bmoCompanySalesman.getCompanyId().toInteger()))
			bmUpdateResult.addError(bmoCompanySalesman.getCompanyId().getName(), 
					"Ya existe un Perfil asignado para la empresa.");
		
		super.save(pmConn, bmoCompanySalesman, bmUpdateResult);

		return bmUpdateResult;

	}
	
	public boolean existProfileAssigned(PmConn pmConn, int companyId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoCompanySalesman.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoCompanySalesman.getKind() 
				+ " WHERE " + bmoCompanySalesman.getCompanyId().getName() + " = " + companyId + ";";
		pmConn.doFetch(sql);

		int countExist = 0;
		if(pmConn.next()) countExist = pmConn.getInt("countExist");
			printDevLog("countExist:"+countExist);
			
		if (countExist > 0) return true;
		else return false;
	}

}
