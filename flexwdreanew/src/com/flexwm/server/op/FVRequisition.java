package com.flexwm.server.op;

import com.flexwm.shared.op.BmoRequisition;
import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;


public class FVRequisition implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		try {
			PmRequisition pmRequisition = new PmRequisition(sFParams);
			BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(Integer.parseInt(value));

			if (bmoRequisition.getStatus().toChar() != BmoRequisition.STATUS_AUTHORIZED) 
				bmUpdateResult.addMsg("No se puede Imprimir/Enviar: No está Autorizada.");

		} catch (SFException e) {
			bmUpdateResult.addError("", e.toString());
		}
		return bmUpdateResult;
	}
}
