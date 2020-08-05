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
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoCustomerDate;


public class PmCustomerDate extends PmObject {
	BmoCustomerDate bmoCustomerDate;

	public PmCustomerDate(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerDate = new BmoCustomerDate();
		setBmObject(bmoCustomerDate);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerDate());
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoCustomerDate bmoCustomerDate = (BmoCustomerDate)bmObject;

		if (bmoCustomerDate.getRemindDate().toInteger() < 0)
			bmUpdateResult.addError(bmoCustomerDate.getRemindDate().getName(), "Debe colocar número igual/mayor a cero");		
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerDate bmoCustomerDate = (BmoCustomerDate)bmObject;		

		super.save(pmConn, bmoCustomerDate, bmUpdateResult);
		return bmUpdateResult;
	}
}
