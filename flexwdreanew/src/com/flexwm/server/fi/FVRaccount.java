package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoRaccount;
import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;


public class FVRaccount implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		try {
			PmRaccount pmRaccount = new PmRaccount(sFParams);
			BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(Integer.parseInt(value));

			if (bmoRaccount.getStatus().toChar() != BmoRaccount.STATUS_AUTHORIZED) 
				bmUpdateResult.addMsg("No se puede Imprimir/Enviar: No está Autorizada.");

		} catch (SFException e) {
			bmUpdateResult.addError("", e.toString());
		}
		return bmUpdateResult;
	}
}
