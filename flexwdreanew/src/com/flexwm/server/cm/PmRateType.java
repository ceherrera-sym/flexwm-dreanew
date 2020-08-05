package com.flexwm.server.cm;

import com.flexwm.shared.cm.BmoRateType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmRateType extends PmObject {
	BmoRateType bmoRateType;

	public PmRateType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoRateType = new BmoRateType();
		setBmObject(bmoRateType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoRateType());
	}
}
