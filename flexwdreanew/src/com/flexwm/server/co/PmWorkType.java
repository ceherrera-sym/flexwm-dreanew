package com.flexwm.server.co;

import com.flexwm.shared.co.BmoWorkType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWorkType extends PmObject {
	BmoWorkType bmoWorkType;

	public PmWorkType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWorkType = new BmoWorkType();
		setBmObject(bmoWorkType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoWorkType());
	}

}
