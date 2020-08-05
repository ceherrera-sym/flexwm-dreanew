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

import com.flexwm.shared.cm.BmoIndustry;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmIndustry extends PmObject {
	BmoIndustry bmoIndustry;

	public PmIndustry(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoIndustry = new BmoIndustry();
		setBmObject(bmoIndustry);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoIndustry());
	}
}
