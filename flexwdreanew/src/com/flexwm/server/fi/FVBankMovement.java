package com.flexwm.server.fi;

import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;


public class FVBankMovement implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		//		try {
		////			PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
		////			BmoBankMovement bmoBankMovement = (BmoBankMovement)pmBankMovement.get(Integer.parseInt(value));
		//			/*
		//			if (bmoBankMovement.getStatus().toChar() != BmoBankMovement.STATUS_RECONCILED) 
		//				bmUpdateResult.addMsg("No se puede Imprimir/Enviar: Está En Revisión.");
		//			*/
		//			
		//		} catch (SFException e) {
		//			bmUpdateResult.addError("", e.toString());
		//		}
		return bmUpdateResult;
	}
}
