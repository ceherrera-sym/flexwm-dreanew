package com.flexwm.server.cm;

import com.flexwm.shared.cm.BmoDelegation;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmDelegation extends PmObject {

	BmoDelegation bmoDelegation;

	public PmDelegation(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoDelegation = new BmoDelegation();
		setBmObject(bmoDelegation);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoDelegation());
	}
}
