package com.flexwm.server;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.BmoCompanyCollectionProfile;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;


public class PmCompanyCollectionProfile extends PmObject {
	BmoCompanyCollectionProfile bmoCompanyCollectionProfile;

	public PmCompanyCollectionProfile(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoCompanyCollectionProfile = new BmoCompanyCollectionProfile();
		setBmObject(bmoCompanyCollectionProfile);
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCompanyCollectionProfile.getCollectProfileId(), bmoCompanyCollectionProfile.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCompanyCollectionProfile bmoCompanySalesman = (BmoCompanyCollectionProfile)autoPopulate(pmConn, new BmoCompanyCollectionProfile());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		int profileId = pmConn.getInt(bmoProfile.getIdFieldName());
		if (profileId > 0) bmoCompanySalesman.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoCompanySalesman.setBmoProfile(bmoProfile);

		return bmoCompanySalesman;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCompanyCollectionProfile = (BmoCompanyCollectionProfile)bmObject;
		boolean newRecord = false;
		if (!(bmoCompanyCollectionProfile.getId() > 0)) {
			newRecord = true;
		}
		
		if (newRecord && existProfileAssigned(pmConn, bmoCompanyCollectionProfile.getCompanyId().toInteger()))
			bmUpdateResult.addError(bmoCompanyCollectionProfile.getCompanyId().getName(), 
					"Ya existe un Perfil asignado para la empresa.");

		super.save(pmConn, bmoCompanyCollectionProfile, bmUpdateResult);

		return bmUpdateResult;
	}
	
	public boolean existProfileAssigned(PmConn pmConn, int companyId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoCompanyCollectionProfile.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoCompanyCollectionProfile.getKind() 
				+ " WHERE " + bmoCompanyCollectionProfile.getCompanyId().getName() + " = " + companyId + ";";
		pmConn.doFetch(sql);

		int countExist = 0;
		if(pmConn.next()) countExist = pmConn.getInt("countExist");
			printDevLog("countExist:"+countExist);
			
		if (countExist > 0) return true;
		else return false;
	}
	

}
