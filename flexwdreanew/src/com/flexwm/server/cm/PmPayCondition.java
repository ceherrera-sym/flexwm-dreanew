package com.flexwm.server.cm;

import com.flexwm.shared.cm.BmoPayCondition;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmPayCondition extends PmObject {
	BmoPayCondition bmoPayCondition;

	public PmPayCondition(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPayCondition= new BmoPayCondition();
		setBmObject(bmoPayCondition);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPayCondition());
	}

}
