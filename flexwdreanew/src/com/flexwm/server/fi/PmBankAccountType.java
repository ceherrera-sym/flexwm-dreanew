package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoBankAccountType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmBankAccountType extends PmObject {
	BmoBankAccountType 	bmoBankAccountType;

	public PmBankAccountType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBankAccountType = new BmoBankAccountType();
		setBmObject(bmoBankAccountType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoBankAccountType());
	}

}