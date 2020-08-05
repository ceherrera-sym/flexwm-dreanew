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

import com.flexwm.shared.co.BmoDevelopmentType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmDevelopmentType extends PmObject{
	BmoDevelopmentType bmoDevelopmentType;


	public PmDevelopmentType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoDevelopmentType = new BmoDevelopmentType();
		setBmObject(bmoDevelopmentType); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoDevelopmentType());
	}
}

