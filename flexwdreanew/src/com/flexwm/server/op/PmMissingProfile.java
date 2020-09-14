package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.op.BmoMissingProfile;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;

public class PmMissingProfile extends PmObject {
	BmoMissingProfile bmoMissingProfile;
	
	public PmMissingProfile(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoMissingProfile = new BmoMissingProfile();
		setBmObject(bmoMissingProfile);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(				
				new PmJoin(bmoMissingProfile.getProfileId(), bmoMissingProfile.getBmoProfile())
				)));
	}	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoMissingProfile bmoMissingProfile = (BmoMissingProfile) autoPopulate(pmConn, new BmoMissingProfile());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoMissingProfile.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoMissingProfile.setBmoProfile(bmoProfile);

		return bmoMissingProfile;
	}	
}
