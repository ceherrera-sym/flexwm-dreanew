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
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentCompany;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


public class PmEquipmentCompany extends PmObject {
	BmoEquipmentCompany bmoEquipmentCompany;

	public PmEquipmentCompany(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoEquipmentCompany = new BmoEquipmentCompany();
		setBmObject(bmoEquipmentCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoEquipmentCompany.getCompanyId(), bmoEquipmentCompany.getBmoCompany()),
				new PmJoin(bmoEquipmentCompany.getEquipmentId(), bmoEquipmentCompany.getBmoEquipment()), 
				new PmJoin(bmoEquipmentCompany.getBmoEquipment().getUserId(), bmoEquipmentCompany.getBmoEquipment().getBmoUser()),
				new PmJoin(bmoEquipmentCompany.getBmoEquipment().getBmoUser().getAreaId(), bmoEquipmentCompany.getBmoEquipment().getBmoUser().getBmoArea()),
				new PmJoin(bmoEquipmentCompany.getBmoEquipment().getBmoUser().getLocationId(), bmoEquipmentCompany.getBmoEquipment().getBmoUser().getBmoLocation()),
				new PmJoin(bmoEquipmentCompany.getBmoEquipment().getEquipmentTypeId(), bmoEquipmentCompany.getBmoEquipment().getBmoEquipmentType())
				)));	
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoEquipmentCompany bmoEquipmentCompany = (BmoEquipmentCompany)autoPopulate(pmConn, new BmoEquipmentCompany());

		// BmoEquipment
		BmoEquipment bmoEquipment = new BmoEquipment();
		if ((int)pmConn.getInt(bmoEquipment.getIdFieldName()) > 0) 
			bmoEquipmentCompany.setBmoEquipment((BmoEquipment) new PmEquipment(getSFParams()).populate(pmConn));
		else 
			bmoEquipmentCompany.setBmoEquipment(bmoEquipment);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoEquipmentCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoEquipmentCompany.setBmoCompany(bmoCompany);

		return bmoEquipmentCompany;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserCompanyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();

		// Filtro de proveedores de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		eqcp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR eqcp_companyid = " + loggedUserCompanyId
					+ " )";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		eqcp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR eqcp_companyid = " + loggedUserCompanyId
					+ " )";
		}

		return filters;
	}

	// Determina si el recurso pertenece a la empresa
	public boolean equipmentInCompany(PmConn pmConn, int companyId, int equipmentId) throws SFException {
		pmConn.doFetch("SELECT * FROM equipmentcompanies WHERE eqcp_equipmentid = " + equipmentId + ""
				+ " AND eqcp_companyid = " + companyId);
		return pmConn.next();
	}
}
