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
import com.symgae.server.SFServerUtil;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.flexwm.shared.op.BmoUnit;


public class PmProduct extends PmObject {
	BmoProduct bmoProduct;

	public PmProduct(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProduct = new BmoProduct();
		setBmObject(bmoProduct);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProduct.getProductFamilyId(), bmoProduct.getBmoProductFamily()),
				new PmJoin(bmoProduct.getProductGroupId(), bmoProduct.getBmoProductGroup()),
				new PmJoin(bmoProduct.getUnitId(), bmoProduct.getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProduct = (BmoProduct)autoPopulate(pmConn, new BmoProduct());

		// BmoProductFamily
		BmoProductFamily bmoProductFamily = new BmoProductFamily();
		int productFamilyId = (int)pmConn.getInt(bmoProductFamily.getIdFieldName());
		if (productFamilyId > 0) bmoProduct.setBmoProductFamily((BmoProductFamily) new PmProductFamily(getSFParams()).populate(pmConn));
		else bmoProduct.setBmoProductFamily(bmoProductFamily);

		// BmoProductGroup
		BmoProductGroup bmoProductGroup = new BmoProductGroup();
		int productGroupId = (int)pmConn.getInt(bmoProductGroup.getIdFieldName());
		if (productGroupId > 0) bmoProduct.setBmoProductGroup((BmoProductGroup) new PmProductGroup(getSFParams()).populate(pmConn));
		else bmoProduct.setBmoProductGroup(bmoProductGroup);

		// BmoUnit
		BmoUnit bmoUnit = new BmoUnit();
		int initId = (int)pmConn.getInt(bmoUnit.getIdFieldName());
		if (initId > 0) bmoProduct.setBmoUnit((BmoUnit) new PmUnit(getSFParams()).populate(pmConn));
		else bmoProduct.setBmoUnit(bmoUnit);

		return bmoProduct;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de productos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		( prod_productid IN "
					+ "			(" 
					+ " 			SELECT prcp_productid FROM productcompanies " 
					+ " 			WHERE prcp_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + loggedUserId
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT prcp_productcompanyid FROM productcompanies "
					+ "			WHERE prcp_productid = prod_productid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";

			filters += " ( "
					+ "		( prod_productid IN "
					+ "			("
					+ " 		SELECT prcp_productid FROM productcompanies "
					+ " 		WHERE prcp_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT prcp_productcompanyid FROM productcompanies "
					+ "			WHERE prcp_productid = prod_productid)"
					+ "		)"
					+ ")";
		}

		return filters;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoProduct bmoProduct = (BmoProduct)bmObject;
		BmoProduct bmoProductPrev = (BmoProduct)bmObject;
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";
		int companyId = 0;
		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoProduct.getId() > 0)) {
			super.save(pmConn, bmoProduct, bmUpdateResult);
			bmoProduct.setId(bmUpdateResult.getId());
			newRecord = true;
			companyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();
			
			if (!(companyId > 0))
				companyId = getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger();				
			if (getSFParams().getSelectedCompanyId() > 0)
				companyId = getSFParams().getSelectedCompanyId();
			// Establecer clave si no esta asignada
			if (bmoProduct.getCode().toString().equals("")) {
					code = pmCompanyNomenclature.getCodeCustom(pmConn,
							companyId,
							bmoProduct.getProgramCode().toString(),
							bmUpdateResult.getId(),
							BmoProduct.CODE_PREFIX
							);
				bmoProduct.getCode().setValue(code);
			}
		} else {
			PmProduct pmProductPrev = new PmProduct(getSFParams());
			bmoProductPrev = (BmoProduct)pmProductPrev.get(bmoProduct.getId());
		}

		if((bmoProduct.getType().equals(BmoProduct.TYPE_COMPLEMENTARY)) && bmoProduct.getTrack().equals(BmoProduct.TRACK_NONE)) {
			bmUpdateResult.addError(bmoProduct.getTrack().getName(), "El Complememtario/Auxiliar debe de llevar un tipo de rastreo (#Serie/Lote)");
		}
		if((bmoProduct.getType().equals(BmoProduct.TYPE_COMPLEMENTARY)) && bmoProduct.getConsumable().toBoolean()) {
			bmUpdateResult.addError(bmoProduct.getConsumable().getName(),"El Complememtario/Auxiliar no puede ser consumible ");
		}
		if((bmoProduct.getType().equals(BmoProduct.TYPE_COMPLEMENTARY)) && !(bmoProduct.getInventory().toBoolean())) {
			bmUpdateResult.addError(bmoProduct.getInventory().getName(),"El Complementario/Auxiliar afecta almacen ");
		}
		if(bmoProduct.getConsumable().toBoolean() && (!bmoProduct.getType().equals(BmoProduct.TYPE_PRODUCT))) {
			bmUpdateResult.addError(bmoProduct.getType().getName(),bmoProduct.getType().getSelectedOption().getLabel()+" no puede ser consumible");
		}
		if(bmoProduct.getConsumable().toBoolean() && !(bmoProduct.getInventory().toBoolean())) {
			bmUpdateResult.addError(bmoProduct.getInventory().getName(), "Si el producto es consumible, afecta almacen");

		}

		// Si esta cambiando de Unidad validar que no existan transacciones con el producto
		if (bmoProduct.getUnitId().toInteger() != bmoProductPrev.getUnitId().toInteger()) {
			if (productInWhMovItems(pmConn, bmoProduct.getId(), bmUpdateResult))
				bmUpdateResult.addError(bmoProduct.getUnitId().getName(), 
						"No se puede cambiar la Unidad, existen movimientos con el producto.");
		}
		
		bmoProduct.getWeight().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getWeight().toDouble()));
		bmoProduct.getCubicMeter().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getCubicMeter().toDouble()));
		bmoProduct.getDimensionLength().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getDimensionLength().toDouble()));
		bmoProduct.getDimensionHeight().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getDimensionHeight().toDouble()));
		bmoProduct.getDimensionWidth().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getDimensionWidth().toDouble()));
		bmoProduct.getAmperage110().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getAmperage110().toDouble()));
		bmoProduct.getAmperage220().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getAmperage220().toDouble()));
		bmoProduct.getWeightCase().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getWeightCase().toDouble()));
		bmoProduct.getCaseLength().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getCaseLength().toDouble()));
		bmoProduct.getCaseHeight().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getCaseHeight().toDouble()));
		bmoProduct.getCaseWidth().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getCaseWidth().toDouble()));
		bmoProduct.getCaseCubicMeter().setValue(SFServerUtil.roundCurrencyDecimals(bmoProduct.getCaseCubicMeter().toDouble()));
		//Validar el producto se haya utilizado anteriormente antes de desactivarlo
		/*if (!(bmoProduct.getEnabled().toInteger() > 0)) {
			//Revisar que el producto no exista en las cotizaciones
			if (this.productInQuote(pmConn, bmoProduct, bmUpdateResult)) {
				bmUpdateResult.addError(bmoProduct.getEnabled().getName(), "El producto esta siendo utilizado en una cotización activa");
			} else if (this.productInOrder(pmConn, bmoProduct, bmUpdateResult)) {
				bmUpdateResult.addError(bmoProduct.getEnabled().getName(), "El producto esta siendo utilizado en un pedido activo");
			} else if (this.productInRequisition(pmConn, bmoProduct, bmUpdateResult)) {
				bmUpdateResult.addError(bmoProduct.getEnabled().getName(), "El producto esta siento utilizado en una order de compra activa");
			}
		}*/
		// Validación de campos drea, si el producto usa case , todos los campos relacionados son obligatorios
		if (getSFParams().isFieldEnabled(bmoProduct.getUseCase())) {
			if (bmoProduct.getUseCase().toBoolean()) {
//				if (!(bmoProduct.getWeightCase().toDouble() > 0))
//					bmUpdateResult.addError(bmoProduct.getWeightCase().getName(), "El campo no debe estar vacio");
				if (!(bmoProduct.getQuantityForCase().toDouble() > 0))
					bmUpdateResult.addError(bmoProduct.getQuantityForCase().getName(), "El campo no debe estar vacio");
				if (!(bmoProduct.getCaseLength().toDouble() > 0))
					bmUpdateResult.addError(bmoProduct.getCaseLength().getName(), "El campo no debe estar vacio");
				if (!(bmoProduct.getCaseHeight().toDouble() > 0))
					bmUpdateResult.addError(bmoProduct.getCaseHeight().getName(), "El campo no debe estar vacio");
				if (!(bmoProduct.getCaseWidth().toDouble() > 0))
					bmUpdateResult.addError(bmoProduct.getCaseWidth().getName(), "El campo no debe estar vacio");
			}
		}

		// Actualizar id de claves del programa por empresa
		if (newRecord && !bmUpdateResult.hasErrors()) {
			pmCompanyNomenclature.updateConsecutiveByCompany(pmConn,companyId, 
					bmoProduct.getProgramCode().toString());
		}

		super.save(pmConn, bmoProduct, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoProduct = (BmoProduct)bmObject;

		// Accion de encontrar producto
		if (action.equals(BmoProduct.ACTION_GETPRODUCT)) {
			// Busca producto
			bmoProduct = getProduct(pmConn, value);

			if (bmoProduct.getId() > 0) 
				bmUpdateResult.setBmObject(bmoProduct);
			else 
				bmUpdateResult.addMsg("No fue encontrado el producto");

		} 

		return bmUpdateResult;
	}

	// Busca por clave de producto
	private BmoProduct getProduct(PmConn pmConn, String productId) throws SFException {
		bmoProduct = new BmoProduct();

		// Busca producto
		String sql = "SELECT prod_productid FROM products " +
				" WHERE prod_productid = " + Integer.parseInt(productId);
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			int product = pmConn.getInt("prod_productid");
			bmoProduct = (BmoProduct)this.get(pmConn, product);	
		}

		return bmoProduct;
	}

	// Si el producto es #SERIE, Validar que la cantidad no sea fraccion 
	// Si es #Lote/Sin Rastreo, Validar que la cantidad no sea fraccion POR la Unidad del producto
	public boolean applyFraction(BmoProduct bmoProduct, double quantity) {
		boolean applyFraction = true;
		if (bmoProduct.getTrack().toString().equals("" + BmoProduct.TRACK_SERIAL)) {
			if (quantity % 1 != 0) 
				applyFraction = false;
		} else if (bmoProduct.getTrack().toString().equals("" + BmoProduct.TRACK_BATCH) 
				|| bmoProduct.getTrack().toString().equals("" + BmoProduct.TRACK_NONE)) {
			if (!bmoProduct.getBmoUnit().getFraction().toBoolean()) {
				if (quantity % 1 != 0) 
					applyFraction = false;
			}
		} else applyFraction = false;

		return applyFraction;
	}

	// Validar si el producto  existe en el Inventario
	private boolean productInWhMovItems (PmConn pmConn, int productId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;

		pmConn.doFetch("SELECT * FROM whmovitems WHERE whmi_productid = " +productId);
		if (pmConn.next()) result = true;

		return result;
	}

	//Validar que el producto exista en una cotizacion
	//	private boolean productInQuote(PmConn pmConn, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
	//		boolean result = false;
	//		String sql = "";
	//		int prods = 0;
	//
	//		sql = " SELECT COUNT(qoit_productid) AS prods FROM quoteitems " +
	//			  " LEFT JOIN quotegroups ON (qoit_quotegroupid = qogr_quotegroupid) " +
	//			  " LEFT JOIN quotes ON (qogr_quoteid = quot_quoteid) " +	
	//			  " WHERE qoit_productid = " + bmoProduct.getId() +
	//			  " AND quot_status = '" + BmoQuote.STATUS_AUTHORIZED +"'";
	//		pmConn.doFetch(sql);
	//		
	//		if (pmConn.next()) {
	//			prods = pmConn.getInt("prods");
	//		}
	//		
	//		if (prods > 0) {
	//			result = true;
	//		}
	//
	//		return result;			
	//	}

	//Validar que el producto exista en un pedido
	//	private boolean productInOrder(PmConn pmConn, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
	//		boolean result = false;
	//		String sql = "";
	//		int prods = 0;
	//
	//		sql = " SELECT COUNT(ordi_productid) AS prods FROM orderitems " +
	//			  " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) " +
	//			  " LEFT JOIN orders ON (ordg_orderid = orde_orderid) " +	
	//			  " WHERE ordi_productid = " + bmoProduct.getId() +
	//			  " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED +"'";
	//		pmConn.doFetch(sql);
	//		
	//		if (pmConn.next()) {
	//			prods = pmConn.getInt("prods");
	//		}
	//		
	//		if (prods > 0) {
	//			result = true;
	//		}
	//
	//		return result;			
	//	}

	//Validar que el producto exista en una order de compra
	//	private boolean productInRequisition(PmConn pmConn, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
	//		boolean result = false;
	//		String sql = "";
	//		int prods = 0;
	//
	//		sql = " SELECT COUNT(rqit_productid) AS prods FROM requisitionitems " +			  
	//			  " LEFT JOIN requisitions ON (rqit_requisitionid = reqi_requisitionid) " +	
	//			  " WHERE rqit_productid = " + bmoProduct.getId() +
	//			  " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED +"'";
	//		pmConn.doFetch(sql);
	//		
	//		if (pmConn.next()) {
	//			prods = pmConn.getInt("prods");
	//		}
	//		
	//		if (prods > 0) {
	//			result = true;
	//		}
	//
	//		return result;			
	//	}

}
