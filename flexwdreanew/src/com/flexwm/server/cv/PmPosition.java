/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cv;

import com.flexwm.shared.cv.BmoPosition;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmPosition extends PmObject {
	BmoPosition bmoPosition;

	public PmPosition(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPosition = new BmoPosition();
		setBmObject(bmoPosition); 

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPosition());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoPosition bmoPosition = (BmoPosition)bmObject;

		super.save(pmConn, bmoPosition, bmUpdateResult);

		return bmUpdateResult;
	}
}
