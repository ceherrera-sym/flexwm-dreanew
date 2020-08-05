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
import com.flexwm.shared.cm.BmoCustomerEmail;


public class PmCustomerEmail extends PmObject {
	BmoCustomerEmail bmoCustomerEmail;

	public PmCustomerEmail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerEmail = new BmoCustomerEmail();
		setBmObject(bmoCustomerEmail);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerEmail());
	}

	public int customerByEmail(String email) throws SFException {
		int customerId = 0;
		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();
			pmConn.doFetch("SELECT cuem_customerid FROM customeremails WHERE cuem_email LIKE '" + email + "'");	

			if (pmConn.next()) {
				customerId = pmConn.getInt(1);
			}

		} catch (SFPmException e) {
			System.out.println("ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return customerId;
	}
}
