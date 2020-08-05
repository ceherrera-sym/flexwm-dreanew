package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoRaccountType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmRaccountType extends PmObject {
	BmoRaccountType bmoRaccountType;

	public PmRaccountType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountType = new BmoRaccountType();
		setBmObject(bmoRaccountType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoRaccountType());
	}

}
