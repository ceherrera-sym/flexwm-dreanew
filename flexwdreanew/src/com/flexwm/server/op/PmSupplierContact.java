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
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoSupplierContact;


public class PmSupplierContact extends PmObject {
	BmoSupplierContact bmoSupplierContact;

	public PmSupplierContact(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierContact = new BmoSupplierContact();
		setBmObject(bmoSupplierContact);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSupplierContact());
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoSupplierContact bmoSupplierContact = (BmoSupplierContact)bmObject;

		if((bmoSupplierContact.getNumber().toString().length() < 1 && bmoSupplierContact.getCellPhone().toString().length() < 1)){
			bmUpdateResult.addError(bmoSupplierContact.getNumber().getName(), "Debe introducir un Teléfono o un T.Móvil");
		}else{	
			if((bmoSupplierContact.getNumber().toString().length() < 7 && bmoSupplierContact.getCellPhone().toString().length() < 7)){
				bmUpdateResult.addError(bmoSupplierContact.getNumber().getName(), "Mínimo 7 caracteres(Tel./Móvil)");
			}
		}
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSupplierContact bmoSupplierContact = (BmoSupplierContact)bmObject;		
		super.save(pmConn, bmoSupplierContact, bmUpdateResult);
		return bmUpdateResult;
	}
}
