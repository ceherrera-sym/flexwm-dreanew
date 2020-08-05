package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoTax;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmTax extends PmObject {
	BmoTax bmoTax;
	public PmTax(SFParams sFParams) throws SFPmException{
		super(sFParams);
		bmoTax = new BmoTax();
		setBmObject(bmoTax);
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoTax());
	}
	
}
