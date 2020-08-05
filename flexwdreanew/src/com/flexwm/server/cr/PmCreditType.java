/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cr;

import com.flexwm.shared.cr.BmoCreditType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmCreditType extends PmObject{
	BmoCreditType bmoCreditType;


	public PmCreditType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCreditType = new BmoCreditType();
		setBmObject(bmoCreditType);

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCreditType = (BmoCreditType)autoPopulate(pmConn, new BmoCreditType());		

		return bmoCreditType;

	}
}

