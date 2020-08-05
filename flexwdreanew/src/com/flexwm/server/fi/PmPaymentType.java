/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoPaymentType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmPaymentType extends PmObject {
	BmoPaymentType bmoPaymentType;

	public PmPaymentType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPaymentType = new BmoPaymentType();
		setBmObject(bmoPaymentType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPaymentType());
	}
}
