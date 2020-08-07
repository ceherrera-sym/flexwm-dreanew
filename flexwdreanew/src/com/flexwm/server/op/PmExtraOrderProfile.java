package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.op.BmoExtraOrderProfile;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;

public class PmExtraOrderProfile  extends PmObject {
	BmoExtraOrderProfile bmoExtraOrderProfile;
	public PmExtraOrderProfile(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoExtraOrderProfile = new BmoExtraOrderProfile();
		setBmObject(bmoExtraOrderProfile);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(				
				new PmJoin(bmoExtraOrderProfile.getProfileId(), bmoExtraOrderProfile.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoExtraOrderProfile bmoExtraOrderProfile = (BmoExtraOrderProfile) autoPopulate(pmConn, new BmoExtraOrderProfile());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoExtraOrderProfile.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoExtraOrderProfile.setBmoProfile(bmoProfile);

		return bmoExtraOrderProfile;
	}
}
