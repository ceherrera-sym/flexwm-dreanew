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
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoCustomerPhone;


public class PmCustomerPhone extends PmObject {
	BmoCustomerPhone bmoCustomerPhone;

	public PmCustomerPhone(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerPhone = new BmoCustomerPhone();
		setBmObject(bmoCustomerPhone);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerPhone());
	}
}
