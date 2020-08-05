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
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


public class PmProductCompany extends PmObject {
	BmoProductCompany bmoProductCompany;

	public PmProductCompany(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductCompany = new BmoProductCompany();
		setBmObject(bmoProductCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProductCompany.getCompanyId(), bmoProductCompany.getBmoCompany()), 
				new PmJoin(bmoProductCompany.getProductId(), bmoProductCompany.getBmoProduct()),
				new PmJoin(bmoProductCompany.getBmoProduct().getProductFamilyId(), bmoProductCompany.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoProductCompany.getBmoProduct().getProductGroupId(), bmoProductCompany.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoProductCompany.getBmoProduct().getUnitId(), bmoProductCompany.getBmoProduct().getBmoUnit())
				)));	
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoProductCompany bmoCustomerCompany = (BmoProductCompany)autoPopulate(pmConn, new BmoProductCompany());

		// BmoCustomer
		BmoProduct bmoCustomer = new BmoProduct();
		if ((int)pmConn.getInt(bmoCustomer.getIdFieldName()) > 0) 
			bmoCustomerCompany.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else 
			bmoCustomerCompany.setBmoProduct(bmoCustomer);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoCustomerCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoCustomerCompany.setBmoCompany(bmoCompany);

		return bmoCustomerCompany;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserCompanyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();

		// Filtro de productos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		prcp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM " + formatKind("usercompanies") 
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR prcp_companyid = " + loggedUserCompanyId
					+ " )";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		prcp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM " + formatKind("usercompanies") 
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			)"
					+ " 	OR prcp_companyid = " + loggedUserCompanyId
					+ " )";			
		}

		return filters;
	}
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoProductCompany bmoProductCompany = (BmoProductCompany)bmObject;

		// Validar partida presupuestal
		//		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
		//			if (!(bmoProductCompany.getBudgetItemId().toInteger() > 0)) 
		//				bmUpdateResult.addError(bmoProductCompany.getBudgetItemId().getName(), "Seleccione una Partida.");
		//		}

		super.save(pmConn, bmoProductCompany, bmUpdateResult);
		return bmUpdateResult;
	}

	// Determina si el producto pertenece a la empresa
	public boolean productInCompany(PmConn pmConn, int companyId, int productId) throws SFException {
		pmConn.doFetch("SELECT * FROM " + formatKind("productcompanies") + " WHERE prcp_productid = " + productId + ""
				+ " AND prcp_companyid = " + companyId);
		return pmConn.next();
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		// Obten el precio vigente
		if (action.equals(BmoProductCompany.ACTION_GETPRODUCTCOMPANY)) {
			BmoProductCompany bmoProductCompany = (BmoProductCompany)bmObject;
			bmUpdateResult = getProductCompanySpecific(value, bmoProductCompany.getCompanyId().toInteger(), bmUpdateResult);
		} 

		return bmUpdateResult;
	}

	// Obtener un registro especifico, por el producto como por la empresa
	public BmUpdateResult getProductCompanySpecific(String productId, int companyId, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			int productCompanyId = 0;

			String sql = " SELECT prcp_productcompanyid FROM " + formatKind("productcompanies") +
					" WHERE prcp_productid = " + productId +
					" AND prcp_companyid = " + companyId;

			pmConn.doFetch(sql);
			if (pmConn.next()) productCompanyId = pmConn.getInt("prcp_productcompanyid");

			BmoProductCompany bmoProductCompany = new BmoProductCompany();
			PmProductCompany pmProductCompany = new PmProductCompany(getSFParams());
			if (productCompanyId > 0)
				bmoProductCompany = (BmoProductCompany)pmProductCompany.get(productCompanyId);

			bmUpdateResult.setBmObject(bmoProductCompany);
			return bmUpdateResult;

		} catch (SFPmException e) {
			throw new SFException("" + e.toString());
		} finally {
			pmConn.close();
		}
	}
	
	// Comparar que la empresa del producto sea de la empresa de la cotizacion,
	// o que el producto no tenga empresa
	public boolean compareProductCompany(PmConn pmConn, int productId, int companyId) throws SFException {
		boolean existProduct = false;
		String sql = "";
		// Busca producto que corresponda a la empresa
		sql = "SELECT prod_productid FROM " + formatKind("products") 
					+ " WHERE ( prod_productid IN ("
					+ "		SELECT prcp_productid FROM " + formatKind("productcompanies") 
					+ " 	WHERE prcp_companyid = " + companyId 
					+ " 	AND prcp_productid = " + productId + " ) " 			
					+ " ) ";
		printDevLog("sqlCompareCompany: "+sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) existProduct = true;
		
		// Si el producto no tiene empresas regresar verdadero
		if (!existProduct) {
			sql = "SELECT prod_productid FROM " + formatKind("products") 
					+ " WHERE ( prod_productid = " + productId 
					+ " 	AND NOT EXISTS (SELECT prcp_productid FROM " + formatKind("productcompanies")
					+ "		WHERE prcp_productid = prod_productid )"
					+ " ) ";
			printDevLog("sqlCompareCompanyNotExist: "+sql);
			pmConn.doFetch(sql);

			if (pmConn.next()) existProduct = true; 
		}

		return existProduct;
	}
}
