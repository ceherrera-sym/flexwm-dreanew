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
import com.symgae.server.sf.PmUser;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentType;


public class PmEquipment extends PmObject {
	BmoEquipment bmoEquipment;

	public PmEquipment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoEquipment = new BmoEquipment();
		setBmObject(bmoEquipment);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoEquipment.getEquipmentTypeId(), bmoEquipment.getBmoEquipmentType()),
				new PmJoin(bmoEquipment.getUserId(), bmoEquipment.getBmoUser()),
				new PmJoin(bmoEquipment.getBmoUser().getAreaId(), bmoEquipment.getBmoUser().getBmoArea()),
				new PmJoin(bmoEquipment.getBmoUser().getLocationId(), bmoEquipment.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoEquipment bmoEquipment = (BmoEquipment)autoPopulate(pmConn, new BmoEquipment());

		// Tipo de Equipo
		BmoEquipmentType bmoEquipmentType = new BmoEquipmentType();
		int productGroupId = (int)pmConn.getInt(bmoEquipmentType.getIdFieldName());
		if (productGroupId > 0) bmoEquipment.setBmoEquipmentType((BmoEquipmentType) new PmEquipmentType(getSFParams()).populate(pmConn));
		else bmoEquipment.setBmoEquipmentType(bmoEquipmentType);

		// Responsable
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoEquipment.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoEquipment.setBmoUser(bmoUser);

		return bmoEquipment;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de productos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		( equi_equipmentid IN "
					+ "			(" 
					+ " 			SELECT eqcp_equipmentid FROM equipmentcompanies " 
					+ " 			WHERE eqcp_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + loggedUserId
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT eqcp_equipmentcompanyid FROM equipmentcompanies "
					+ "			WHERE eqcp_equipmentid = equi_equipmentid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";

			filters += " ( "
					+ "		( equi_equipmentid IN "
					+ "			("
					+ " 		SELECT eqcp_equipmentid FROM equipmentcompanies "
					+ " 		WHERE eqcp_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT eqcp_equipmentcompanyid FROM equipmentcompanies "
					+ "			WHERE eqcp_equipmentid = equi_equipmentid)"
					+ "		)"
					+ ")";
		}

		return filters;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoEquipment bmoEquipment = (BmoEquipment)bmObject;

		//Validar que el equipo no este en mantenimiento
		if (bmoEquipment.getStatus().equals(BmoEquipment.STATUS_ACTIVE)) {
			this.equipmentInMaintenace(pmConn, bmoEquipment, bmUpdateResult);
		}

		super.save(pmConn, bmoEquipment, bmUpdateResult);

		return bmUpdateResult;
	}

	private void equipmentInMaintenace(PmConn pmConn,BmoEquipment bmoEquipment, BmUpdateResult bmUpdateResult) throws SFException {

		PmEquipmentService pmEquipmentService = new PmEquipmentService(getSFParams());

		if (pmEquipmentService.equipmentInMantenace(pmConn, bmoEquipment.getId(), bmUpdateResult)) {
			bmUpdateResult.addError(bmoEquipment.getStatus().getName(), "El Equipo se encuentra en mantenimiento");
		}

	}
}
