/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.in;

import com.flexwm.shared.in.BmoInsuranceCategory;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsuranceCategory extends PmObject {
	BmoInsuranceCategory bmoInsuranceCategory = new BmoInsuranceCategory();
	
	public PmInsuranceCategory(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsuranceCategory);
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoInsuranceCategory());
	}
}
