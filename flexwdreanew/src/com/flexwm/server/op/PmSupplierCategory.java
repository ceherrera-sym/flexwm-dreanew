/**
 * 
 */
package com.flexwm.server.op;

import com.flexwm.shared.op.BmoSupplierCategory;
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

public class PmSupplierCategory extends PmObject {
	BmoSupplierCategory bmoSupplierCategory;

	public PmSupplierCategory(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierCategory = new BmoSupplierCategory();
		setBmObject(bmoSupplierCategory);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSupplierCategory());
	}
}
