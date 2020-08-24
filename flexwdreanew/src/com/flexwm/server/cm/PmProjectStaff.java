package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.cm.BmoProjectStaff;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;

public class PmProjectStaff extends PmObject {
	BmoProjectStaff bmoProjectStaff;
	public PmProjectStaff(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoProjectStaff = new BmoProjectStaff();
		setBmObject(bmoProjectStaff);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProjectStaff.getUserId(), bmoProjectStaff.getBmoUser()),
				new PmJoin(bmoProjectStaff.getBmoUser().getAreaId(), bmoProjectStaff.getBmoUser().getBmoArea()),
				new PmJoin(bmoProjectStaff.getBmoUser().getLocationId(), bmoProjectStaff.getBmoUser().getBmoLocation()),
				new PmJoin(bmoProjectStaff.getProfileId(), bmoProjectStaff.getBmoProfile())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoProjectStaff bmoProjectStaff = (BmoProjectStaff)autoPopulate(pmConn, new BmoProjectStaff());
		
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName())  > 0) 
			bmoProjectStaff.setBmoUser((BmoUser)new PmUser(getSFParams()).populate(pmConn));
		else
			bmoProjectStaff.setBmoUser(bmoUser);
		
		BmoProfile bmoProfile = new BmoProfile();
		
		
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0)
			bmoProjectStaff.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else
			bmoProjectStaff.setBmoProfile(bmoProfile);
			
			
		return bmoProjectStaff;
	}

}
