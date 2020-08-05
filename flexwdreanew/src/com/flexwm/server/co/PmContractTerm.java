package com.flexwm.server.co;

import com.flexwm.shared.co.BmoContractTerm;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmContractTerm extends PmObject {
	BmoContractTerm bmoContractTerm;

	public PmContractTerm(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoContractTerm = new BmoContractTerm();
		setBmObject(bmoContractTerm);
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoContractTerm = (BmoContractTerm)autoPopulate(pmConn, new BmoContractTerm());		

		return bmoContractTerm;
	}
	
	

}
