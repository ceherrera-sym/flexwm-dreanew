package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoCFDI;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmCFDI extends PmObject {
	BmoCFDI bmoCFDI;

	public PmCFDI(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCFDI = new BmoCFDI();
		setBmObject(bmoCFDI);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCFDI());
	}

}
