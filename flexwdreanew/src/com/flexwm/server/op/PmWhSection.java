
package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author jhernandez
 *
 */

public class PmWhSection extends PmObject {
	BmoWhSection bmoWhSection;

	public PmWhSection(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhSection = new BmoWhSection();
		setBmObject(bmoWhSection);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhSection.getWarehouseId(), bmoWhSection.getBmoWarehouse()),
				new PmJoin(bmoWhSection.getBmoWarehouse().getCompanyId(), bmoWhSection.getBmoWarehouse().getBmoCompany())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de secciones de empresas del usuario
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
		bmoWhSection = (BmoWhSection) autoPopulate(pmConn, new BmoWhSection());		

		// BmoWarehouse
		BmoWarehouse bmoWarehouse = new BmoWarehouse();
		int warehouseIdId = (int)pmConn.getInt(bmoWarehouse.getIdFieldName());
		if (warehouseIdId > 0) bmoWhSection.setBmoWarehouse((BmoWarehouse) new PmWarehouse(getSFParams()).populate(pmConn));
		else bmoWhSection.setBmoWarehouse(bmoWarehouse);

		return bmoWhSection;
	}

	public boolean orderSectionExists(PmConn pmConn, int orderId) throws SFException{
		boolean exists = false;
		pmConn.doFetch("SELECT * FROM whsections WHERE whse_orderid = " + orderId);
		exists = pmConn.next();
		return exists;
	}
}
