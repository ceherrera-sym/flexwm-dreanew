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

import com.flexwm.shared.co.BmoDevelopmentRegistry;
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

public class PmDevelopmentRegistry extends PmObject{
	BmoDevelopmentRegistry bmoDevelopmentRegistry;

	public PmDevelopmentRegistry(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoDevelopmentRegistry = new BmoDevelopmentRegistry();
		setBmObject(bmoDevelopmentRegistry); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoDevelopmentRegistry = (BmoDevelopmentRegistry)autoPopulate(pmConn, new BmoDevelopmentRegistry());		

		return bmoDevelopmentRegistry;
	}

	public boolean existCodeDvrg(PmConn pmConn, String code) throws SFException {
		String sql = "";
		boolean result = false;

		sql = " SELECT dvrg_code FROM developmentregistry " +
				" WHERE dvrg_code LIKE '" + code + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) result = true;

		return result;
	}
}
