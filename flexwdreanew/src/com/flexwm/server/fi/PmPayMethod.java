package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoPayMethod;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmPayMethod extends PmObject {
	BmoPayMethod bmoPayMethod;

	public PmPayMethod(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPayMethod = new BmoPayMethod();
		setBmObject(bmoPayMethod);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPayMethod());
	}

}
