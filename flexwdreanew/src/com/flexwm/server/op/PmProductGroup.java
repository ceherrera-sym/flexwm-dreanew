package com.flexwm.server.op;

import com.flexwm.shared.op.BmoProductGroup;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmProductGroup extends PmObject {
	BmoProductGroup bmoProductGroup;

	public PmProductGroup(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductGroup = new BmoProductGroup();
		setBmObject(bmoProductGroup);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoProductGroup());
	}

}
