/**
 * 
 */
package com.flexwm.server.op;


import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.sf.BmoCompany;

import com.flexwm.shared.op.BmoWarehouse;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmWarehouse extends PmObject {
	BmoWarehouse bmoWarehouse;

	public PmWarehouse(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWarehouse = new BmoWarehouse();
		setBmObject(bmoWarehouse);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWarehouse.getCompanyId(), bmoWarehouse.getBmoCompany())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de almacenes de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "( ware_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ware_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWarehouse = (BmoWarehouse) autoPopulate(pmConn, new BmoWarehouse());		

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = (int)pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoWarehouse.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoWarehouse.setBmoCompany(bmoCompany);

		return bmoWarehouse;
	}
}
