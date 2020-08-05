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
import com.flexwm.shared.op.BmoSupplierEmail;


public class PmSupplierEmail extends PmObject {
	BmoSupplierEmail bmoSupplierEmail;

	public PmSupplierEmail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierEmail = new BmoSupplierEmail();
		setBmObject(bmoSupplierEmail);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSupplierEmail());
	}

	public int supplierByEmail(String email) throws SFException {
		int supplierId = 0;
		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();
			pmConn.doFetch("SELECT suem_customerid FROM supplieremails WHERE suem_email LIKE '" + email + "'");	

			if (pmConn.next()) {
				supplierId = pmConn.getInt(1);
			}

		} catch (SFPmException e) {
			System.out.println("ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return supplierId;
	}
}
