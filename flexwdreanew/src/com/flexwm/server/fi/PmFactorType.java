package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoFactorType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmFactorType extends PmObject{
	BmoFactorType bmoFactorType;
	public PmFactorType(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoFactorType = new BmoFactorType();
		setBmObject(bmoFactorType);
		
	}
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoFactorType());
	}
}
