package com.flexwm.server.op;

import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;


public class FVRequisitionReceipt implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		try {
			PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
			BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(Integer.parseInt(value));

			if (bmoRequisitionReceipt.getStatus().toChar() != BmoRequisitionReceipt.STATUS_AUTHORIZED) 
				bmUpdateResult.addMsg("No se puede Imprimir/Enviar: No está Autorizada.");

		} catch (SFException e) {
			bmUpdateResult.addError("", e.toString());
		}
		return bmUpdateResult;
	}
}
