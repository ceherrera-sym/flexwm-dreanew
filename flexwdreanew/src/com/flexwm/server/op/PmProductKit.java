/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoProductKit;


public class PmProductKit extends PmObject {
	BmoProductKit bmoProductKit;

	public PmProductKit(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductKit = new BmoProductKit();
		setBmObject(bmoProductKit);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProductKit = (BmoProductKit)autoPopulate(pmConn, new BmoProductKit());

		return bmoProductKit;
	}
}
