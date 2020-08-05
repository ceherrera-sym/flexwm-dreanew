/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import com.flexwm.shared.ac.BmoProgramSession;
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

public class PmProgramSession extends PmObject {
	BmoProgramSession bmoProgramSession;

	public PmProgramSession(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoProgramSession = new BmoProgramSession();
		setBmObject(bmoProgramSession); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoProgramSession());
	}
}
