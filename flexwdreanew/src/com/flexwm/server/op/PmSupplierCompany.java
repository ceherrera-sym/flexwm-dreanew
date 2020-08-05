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

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCompany;


public class PmSupplierCompany extends PmObject {
	BmoSupplierCompany bmoSupplierCompany;

	public PmSupplierCompany(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierCompany = new BmoSupplierCompany();
		setBmObject(bmoSupplierCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplierCompany.getCompanyId(), bmoSupplierCompany.getBmoCompany()), 
				new PmJoin(bmoSupplierCompany.getSupplierId(), bmoSupplierCompany.getBmoSupplier()),
				new PmJoin(bmoSupplierCompany.getBmoSupplier().getSupplierCategoryId(), bmoSupplierCompany.getBmoSupplier().getBmoSupplierCategory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoSupplierCompany bmoSupplierCompany = (BmoSupplierCompany)autoPopulate(pmConn, new BmoSupplierCompany());

		// BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		if ((int)pmConn.getInt(bmoSupplier.getIdFieldName()) > 0) 
			bmoSupplierCompany.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else 
			bmoSupplierCompany.setBmoSupplier(bmoSupplier);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoSupplierCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoSupplierCompany.setBmoCompany(bmoCompany);

		return bmoSupplierCompany;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserCompanyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();

		// Filtro de proveedores de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		sucp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR sucp_companyid = " + loggedUserCompanyId
					+ " )";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		sucp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR sucp_companyid = " + loggedUserCompanyId
					+ " )";
		}

		return filters;
	}

	// Determina si el proveedor pertenece a una empresa
	public boolean supplierInCompany(PmConn pmConn, int companyId, int supplierId) throws SFException {
		pmConn.doFetch("SELECT * FROM suppliercompanies WHERE sucp_supplierid = " + supplierId + ""
				+ " AND sucp_companyid = " + companyId);
		return pmConn.next();
	}
}
