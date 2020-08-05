package com.flexwm.server.ac;

import com.flexwm.shared.ac.BmoSessionDiscipline;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmSessionDiscipline extends PmObject {
	BmoSessionDiscipline bmoSessionDiscipline;
	
	public PmSessionDiscipline(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSessionDiscipline = new BmoSessionDiscipline();
		setBmObject(bmoSessionDiscipline);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSessionDiscipline());
	}
	
}
