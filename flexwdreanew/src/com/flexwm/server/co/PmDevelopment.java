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

import com.flexwm.shared.co.BmoDevelopment;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author smuniz
 *
 */

public class PmDevelopment extends PmObject {
	BmoDevelopment bmoDevelopment;


	public PmDevelopment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoDevelopment = new BmoDevelopment();
		setBmObject(bmoDevelopment); 
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de empresas del usuario en Desarrollos
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters += 	" ( deve_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " ) " +
					" ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " deve_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoDevelopment = (BmoDevelopment)autoPopulate(pmConn, new BmoDevelopment());		
		return bmoDevelopment;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoDevelopment bmoDevelopment = (BmoDevelopment)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoDevelopment.getId() > 0)) {			
			// Establecer clave si no esta asignada
			if (bmoDevelopment.getCode().toString().equals("")) bmoDevelopment.getCode().setValue(bmoDevelopment.getCodeFormat());
		}

		super.save(pmConn, bmoDevelopment, bmUpdateResult);
		return bmUpdateResult;
	}
}

