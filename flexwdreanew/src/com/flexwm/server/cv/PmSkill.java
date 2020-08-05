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

import com.flexwm.shared.cv.BmoSkill;
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

public class PmSkill extends PmObject {
	BmoSkill bmoSkill;

	public PmSkill(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoSkill = new BmoSkill();
		setBmObject(bmoSkill); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSkill());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSkill bmoSkill = (BmoSkill)bmObject;
		super.save(pmConn, bmoSkill, bmUpdateResult);

		return bmUpdateResult;
	}
}

