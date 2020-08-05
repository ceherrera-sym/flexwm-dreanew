package com.flexwm.server.op;

import com.flexwm.shared.op.BmoOrderType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmOrderType extends PmObject {
	BmoOrderType bmoOrderType;

	public PmOrderType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderType = new BmoOrderType();
		setBmObject(bmoOrderType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoOrderType());
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoOrderType bmoOrderType = (BmoOrderType)bmObject;

		if (bmoOrderType.getEmailRemindersOrderEnd().toBoolean()) {
			if (!bmoOrderType.getRemindDaysBeforeEndOrder().toString().equals("")) {
				if (bmoOrderType.getRemindDaysBeforeEndOrder().toInteger() < 0)
					bmUpdateResult.addError(bmoOrderType.getRemindDaysBeforeEndOrder().getName(), "El valor no debe ser negativo");
			} else 
				bmUpdateResult.addError(bmoOrderType.getRemindDaysBeforeEndOrder().getName(), "El campo no debe estar vacío");


			if (bmoOrderType.getProfileId().toInteger() < 0)
				bmUpdateResult.addError(bmoOrderType.getProfileId().getName(), "El Grupo no debe estar vacío");
		}
		
		if(bmoOrderType.getEnableExtraOrder().toInteger() > 0) {
			if(!(bmoOrderType.getwFlowTypeId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoOrderType.getwFlowTypeId().getName(), "El campo no debe estar vacío");
			}
		}
	}

}
