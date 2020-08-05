package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoOrderFlowUserGrup;
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


public class PmOrderFlowUserGrup extends PmObject{
	BmoOrderFlowUserGrup bmoOrderFlowUserGrup;

	public PmOrderFlowUserGrup(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoOrderFlowUserGrup = new BmoOrderFlowUserGrup();
		setBmObject(bmoOrderFlowUserGrup);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderFlowUserGrup.getProfileId(), bmoOrderFlowUserGrup.getBmoProfile())

				)));
	}
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoOrderFlowUserGrup bmoOrderFlowUserGrups = (BmoOrderFlowUserGrup) autoPopulate(pmConn, new BmoOrderFlowUserGrup());

		bmoOrderFlowUserGrups.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));

		return bmoOrderFlowUserGrups;

	}
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderFlowUserGrup = (BmoOrderFlowUserGrup)bmObject;
		if(existGroup(pmConn, bmoOrderFlowUserGrup.getProfileId().toInteger())) {
			bmUpdateResult.addError(bmoOrderFlowUserGrup.getProfileId().getName(), "El Grupo ya existe.");
		}
		super.save(pmConn, bmoOrderFlowUserGrup, bmUpdateResult);
		return bmUpdateResult;
	}
	public boolean existGroup(PmConn pmConn,int profileId) throws SFPmException {
		pmConn.doFetch("SELECT * FROM orderflowusergroup WHERE ofug_profileid = " + profileId );
		return pmConn.next();
	}

}
