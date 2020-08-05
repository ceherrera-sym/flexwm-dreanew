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

import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author jhernandez
 *
 */

public class PmSupplier extends PmObject {
	BmoSupplier bmoSupplier;

	public PmSupplier(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplier = new BmoSupplier();
		setBmObject(bmoSupplier);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplier.getSupplierCategoryId(), bmoSupplier.getBmoSupplierCategory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoSupplier bmoSupplier = (BmoSupplier) autoPopulate(pmConn, new BmoSupplier());		

		// BmoSupplierCategory
		BmoSupplierCategory bmoSupplierCategory = new BmoSupplierCategory();
		int reqPayTypeId = (int)pmConn.getInt(bmoSupplierCategory.getIdFieldName());
		if (reqPayTypeId > 0) bmoSupplier.setBmoSupplierCategory((BmoSupplierCategory) new PmSupplierCategory(getSFParams()).populate(pmConn));
		else bmoSupplier.setBmoSupplierCategory(bmoSupplierCategory);

		return bmoSupplier;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de proveedores de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "("
					+ "		( supl_supplierid IN "
					+ "			(" 
					+ " 			SELECT sucp_supplierid FROM suppliercompanies " 
					+ " 			WHERE sucp_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + loggedUserId
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT sucp_suppliercompanyid FROM suppliercompanies "
					+ "			WHERE sucp_supplierid = supl_supplierid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";

			filters += "("
					+ "		( supl_supplierid IN "
					+ "			("
					+ " 		SELECT sucp_supplierid FROM suppliercompanies "
					+ " 		WHERE sucp_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT sucp_suppliercompanyid FROM suppliercompanies "
					+ "			WHERE sucp_supplierid = supl_supplierid)"
					+ "		)"
					+ ")";
		}

		return filters;
	}	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSupplier bmoSupplier = (BmoSupplier)bmObject;
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		boolean newRecord = false;
		int companyId = 0;
		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoSupplier.getId() > 0)) {
			super.save(pmConn, bmoSupplier, bmUpdateResult);
			bmoSupplier.setId(bmUpdateResult.getId());
			String code = "";
			newRecord = true;

			companyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();
			
			if (!(companyId > 0))
				companyId = getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger();				
			if (getSFParams().getSelectedCompanyId() > 0)
				companyId = getSFParams().getSelectedCompanyId();
			// Establecer clave si no esta asignada
			if (bmoSupplier.getCode().toString().equals("")) {
					code = pmCompanyNomenclature.getCodeCustom(pmConn,
							companyId,
							bmoSupplier.getProgramCode().toString(),
							bmUpdateResult.getId(),
							BmoSupplier.CODE_PREFIX
							);
					bmoSupplier.getCode().setValue(code);
			}
			// Establecer clave si no esta asignada
			//if (bmoSupplier.getCode().toString().equals("")) bmoSupplier.getCode().setValue(bmoSupplier.getCodeFormat());
		}

		//Al activar la casilla de enviar correo debe existir un email
		if (bmoSupplier.getSendEmail().toBoolean()) {
			if (bmoSupplier.getEmail().toString().equals("")) {
				bmUpdateResult.addError(bmoSupplier.getEmail().getName(), "Capturar el correo del proveedor");				
			}
		}

		// Actualizar id de claves del programa por empresa
		if (newRecord && !bmUpdateResult.hasErrors()) {
			pmCompanyNomenclature.updateConsecutiveByCompany(pmConn,companyId, 
			bmoSupplier.getProgramCode().toString());
		}
		super.save(pmConn, bmoSupplier, bmUpdateResult);

		return bmUpdateResult;
	}
}
