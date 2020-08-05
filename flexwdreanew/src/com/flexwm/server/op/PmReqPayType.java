package com.flexwm.server.op;

import com.flexwm.shared.op.BmoReqPayType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmReqPayType extends PmObject {
	BmoReqPayType bmoReqPayType;

	public PmReqPayType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoReqPayType = new BmoReqPayType();
		setBmObject(bmoReqPayType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoReqPayType());
	}

}
