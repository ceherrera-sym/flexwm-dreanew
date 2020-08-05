package com.flexwm.server.op;

import com.flexwm.shared.op.BmoEquipmentType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmEquipmentType extends PmObject {
	BmoEquipmentType bmoEquipmentType;

	public PmEquipmentType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoEquipmentType = new BmoEquipmentType();
		setBmObject(bmoEquipmentType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoEquipmentType());
	}

}
