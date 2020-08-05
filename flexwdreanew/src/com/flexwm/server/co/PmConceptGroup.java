/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import com.flexwm.shared.co.BmoConceptGroup;
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

public class PmConceptGroup extends PmObject{
	BmoConceptGroup  bmoConceptGroup ;


	public PmConceptGroup(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoConceptGroup = new BmoConceptGroup();
		setBmObject(bmoConceptGroup); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoConceptGroup());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoConceptGroup bmoConceptGroup = (BmoConceptGroup)bmObject;

		super.save(pmConn, bmoConceptGroup, bmUpdateResult);

		return bmUpdateResult;
	}
}

