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

import com.flexwm.shared.co.BmoFSRS;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

/**
 * @author smuniz
 *
 */

public class PmFSRS extends PmObject{
	BmoFSRS bmoFSRS;
	
	
	public PmFSRS(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoFSRS = new BmoFSRS();
		setBmObject(bmoFSRS); 
		
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoFSRS());
	}
	
	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoFSRS bmoFSRS = (BmoFSRS)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoFSRS.getId() > 0)) {			
			// Establecer clave si no esta asignada
			if (bmoFSRS.getCode().toString().equals("")) bmoFSRS.getCode().setValue(bmoFSRS.getCodeFormat());
		}

		super.save(pmConn, bmoFSRS, bmUpdateResult);
		return bmUpdateResult;
	}
}

