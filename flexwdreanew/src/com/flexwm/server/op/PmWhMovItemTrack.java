
package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoWhMovItemTrack;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWhMovItemTrack extends PmObject {
	BmoWhMovItemTrack bmoWhMovItemTrack;

	public PmWhMovItemTrack(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhMovItemTrack = new BmoWhMovItemTrack();
		setBmObject(bmoWhMovItemTrack);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhMovItemTrack.getWhTrackId(), bmoWhMovItemTrack.getBmoWhTrack()),
				new PmJoin(bmoWhMovItemTrack.getBmoWhTrack().getProductId(), bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct()),
				new PmJoin(bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getProductFamilyId(), bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getProductGroupId(), bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getUnitId(), bmoWhMovItemTrack.getBmoWhTrack().getBmoProduct().getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWhMovItemTrack = (BmoWhMovItemTrack) autoPopulate(pmConn, new BmoWhMovItemTrack());		

		// BmoWhTrack
		BmoWhTrack bmoWhTrack = new BmoWhTrack();
		int whTrackIdId = (int)pmConn.getInt(bmoWhTrack.getIdFieldName());
		if (whTrackIdId > 0) bmoWhMovItemTrack.setBmoWhTrack((BmoWhTrack) new PmWhTrack(getSFParams()).populate(pmConn));
		else bmoWhMovItemTrack.setBmoWhTrack(bmoWhTrack);

		return bmoWhMovItemTrack;
	}
}
