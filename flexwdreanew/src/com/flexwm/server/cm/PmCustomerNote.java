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

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoCustomerNote;


public class PmCustomerNote extends PmObject {
	BmoCustomerNote bmoCustomerNote;

	public PmCustomerNote(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerNote = new BmoCustomerNote();
		setBmObject(bmoCustomerNote);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerNote());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerNote bmoCustomerNote = (BmoCustomerNote)bmObject;		
		super.save(pmConn, bmoCustomerNote, bmUpdateResult);
		return bmUpdateResult;
	}

}
