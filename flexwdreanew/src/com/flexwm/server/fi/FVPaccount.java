package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoPaccount;
import com.symgae.server.sf.IFormatPublishValidator;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoFormat;


public class FVPaccount implements IFormatPublishValidator {

	@Override
	public BmUpdateResult canPublish(SFParams sFParams, BmoFormat bmoFormat, String value, BmUpdateResult bmUpdateResult) {
		try {
			PmPaccount pmPaccount = new PmPaccount(sFParams);
			BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(Integer.parseInt(value));

			if (bmoPaccount.getStatus().toChar() != BmoPaccount.STATUS_AUTHORIZED) 
				bmUpdateResult.addMsg("No se puede Imprimir/Enviar: No está Autorizada.");

		} catch (SFException e) {
			bmUpdateResult.addError("", e.toString());
		}
		return bmUpdateResult;
	}
}
