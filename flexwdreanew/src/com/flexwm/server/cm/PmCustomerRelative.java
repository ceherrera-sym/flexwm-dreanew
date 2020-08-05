/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import com.flexwm.shared.cm.BmoCustomerRelative;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmCustomerRelative extends PmObject {
	BmoCustomerRelative bmoCustomerRelative;

	public PmCustomerRelative(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerRelative = new BmoCustomerRelative();
		setBmObject(bmoCustomerRelative);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerRelative());
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoCustomerRelative bmoCustomerRelative = (BmoCustomerRelative)bmObject;

		if((bmoCustomerRelative.getNumber().toString().length() < 1 && bmoCustomerRelative.getCellPhone().toString().length() < 1)){
			bmUpdateResult.addError(bmoCustomerRelative.getType().getName(), "Debe introducir un Teléfono o un T.Celular");
		}else{	
			if((bmoCustomerRelative.getNumber().toString().length() < 7 && bmoCustomerRelative.getCellPhone().toString().length() < 7)){
				bmUpdateResult.addError(bmoCustomerRelative.getType().getName(), "Mínimo 7 caracteres(Tel/Cel)");
			}
		}
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerRelative bmoCustomerRelative = (BmoCustomerRelative)bmObject;		
		super.save(pmConn, bmoCustomerRelative, bmUpdateResult);
		return bmUpdateResult;
	}
}
