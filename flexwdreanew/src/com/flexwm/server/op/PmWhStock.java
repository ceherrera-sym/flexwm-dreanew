
package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.flexwm.shared.op.BmoWhStock;
import com.flexwm.server.FlexUtil;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



public class PmWhStock extends PmObject {
	BmoWhStock bmoWhStock;

	public PmWhStock(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhStock = new BmoWhStock();
		setBmObject(bmoWhStock);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhStock.getWhSectionId(), bmoWhStock.getBmoWhSection()),
				new PmJoin(bmoWhStock.getBmoWhSection().getWarehouseId(), bmoWhStock.getBmoWhSection().getBmoWarehouse()),
				new PmJoin(bmoWhStock.getBmoWhSection().getBmoWarehouse().getCompanyId(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getBmoCompany()),
				new PmJoin(bmoWhStock.getProductId(), bmoWhStock.getBmoProduct()),
				new PmJoin(bmoWhStock.getBmoProduct().getProductFamilyId(), bmoWhStock.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoWhStock.getBmoProduct().getProductGroupId(), bmoWhStock.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoWhStock.getBmoProduct().getUnitId(), bmoWhStock.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoWhStock.getWhTrackId(), bmoWhStock.getBmoWhTrack())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de existencias de empresas del usuario
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
		bmoWhStock = (BmoWhStock) autoPopulate(pmConn, new BmoWhStock());		

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productIdId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productIdId > 0) bmoWhStock.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoWhStock.setBmoProduct(bmoProduct);

		// BmoWhSection
		BmoWhSection bmoWhSection = new BmoWhSection();
		int whSectionId = (int)pmConn.getInt(bmoWhSection.getIdFieldName());
		if (whSectionId > 0) bmoWhStock.setBmoWhSection((BmoWhSection) new PmWhSection(getSFParams()).populate(pmConn));
		else bmoWhStock.setBmoWhSection(bmoWhSection);

		// BmoWhTrack
		BmoWhTrack bmoWhTrack = new BmoWhTrack();
		int whTrackId = (int)pmConn.getInt(bmoWhTrack.getIdFieldName());
		if (whTrackId > 0) bmoWhStock.setBmoWhTrack((BmoWhTrack) new PmWhTrack(getSFParams()).populate(pmConn));
		else bmoWhStock.setBmoWhTrack(bmoWhTrack);

		return bmoWhStock;
	}

	public BmUpdateResult updateStock(PmConn pmConn, BmUpdateResult bmUpdateResult, double quantity, int productId, double cost, int whSectionId, int whTrackId) throws SFException {
		int whStockId = 0;
		String stockSql = "SELECT whst_whstockid, whst_quantity FROM whstocks "
				+ " WHERE whst_productid = " + productId + " "
				+ " AND whst_whsectionid =  " + whSectionId;
		// Si esta asignada la seccion aplicar filtro
		if (whTrackId > 0) 
			stockSql += " AND whst_whtrackid = " + whTrackId;

		printDevLog("Esta modificando un registro de Stock: " + stockSql);

		pmConn.doFetch(stockSql);
		if (pmConn.next()) {
			whStockId = pmConn.getInt(1);
			quantity += pmConn.getDouble(2);
		}
		quantity = FlexUtil.roundDouble(quantity, 4);

		// Se actualizo la cantidad en inventario, almacenar
		BmoWhStock bmoWhStock = new BmoWhStock();
		bmoWhStock.setId(whStockId);
		bmoWhStock.getProductId().setValue(productId);
		bmoWhStock.getWhSectionId().setValue(whSectionId);
		bmoWhStock.getQuantity().setValue(quantity);
		bmoWhStock.getAmount().setValue(quantity * cost);
		// Solo si esta enviado un id, asignarlo
		if (whTrackId > 0)
			bmoWhStock.getWhTrackId().setValue(whTrackId);

		super.save(pmConn, bmoWhStock, bmUpdateResult);

		// Revisa si la existencia es 0 cero, en ese caso elimina el registro de existencia
		if (bmoWhStock.getQuantity().toDouble() == 0)
			super.delete(pmConn, bmoWhStock, bmUpdateResult);

		return bmUpdateResult;
	}

	// Recuperar existencias en almacen
	public double getStockQuantity(BmoWhStock bmoWhStock, int companyId) throws SFException{		
		double stockQuantity = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String stockSql = "SELECT SUM(whst_quantity) "
				+ " FROM whstocks "
				+ " LEFT JOIN whsections ON (whse_whsectionid = whst_whsectionid) " 
				+ " LEFT JOIN warehouses ON (ware_warehouseid = whse_warehouseid) "
				+ " WHERE whst_productid = " + bmoWhStock.getProductId().toInteger();
				if (companyId > 0)
					stockSql += " AND ware_companyid = " + companyId;

		// Si esta asignada la seccion aplicar filtro
		if (bmoWhStock.getWhSectionId().toInteger() > 0) 
			stockSql += " AND whst_whsectionid = " + bmoWhStock.getWhSectionId().toInteger();

		printDevLog("sql " + stockSql);

		pmConn.doFetch(stockSql);
		pmConn.next();
		stockQuantity = FlexUtil.roundDouble(pmConn.getDouble(1), 4);
		pmConn.close();
		return stockQuantity;
	}

	// Recuperar existencias en almacen /SI se habilita COLOCAR FILTRO DE EMPRESA
	public double stockQuantityInWhSectionn(int whSectionId, int whTrackId) throws SFException{		
		double stockQuantity = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String stockSql = " SELECT SUM(whst_quantity) " +
				" FROM whstocks " +
				" WHERE whst_whsectionid = " + whSectionId + 
				" AND whst_whtrackid = " + whTrackId;

		pmConn.doFetch(stockSql);
		pmConn.next();
		stockQuantity = FlexUtil.roundDouble(pmConn.getDouble(1), 4);
		pmConn.close();

		return stockQuantity;
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoWhStock = (BmoWhStock)bmObject;

		// Accion de encontrar el codigo de barras
		if (action.equals(BmoWhStock.ACTION_SEARCHBARCODE)) {
			String barcode = value;

			// Busca primero dentro de las existencias, la clave de producto
			bmoWhStock = searchProductCodeByBarcode(pmConn, barcode);

			// Si no fue encontrado, buscar como numero de serie
			if (!(bmoWhStock.getId() > 0)) 
				bmoWhStock = searchProductBySerial(pmConn, barcode);

			if (bmoWhStock.getId() > 0) {
				bmUpdateResult.setBmObject(bmoWhStock);
			} else {
				bmUpdateResult.addMsg("No fue encontrado el Código de Barras dentro de las Existencias de Almacén.");
			}
		} 
		// Acción de revisar existencias
		else if (action.equals(BmoWhStock.ACTION_STOCKQUANTITY)) {
			StringTokenizer tabs = new StringTokenizer(value, "|");
			String productId = ""; 
			int companyId = 0;
			while (tabs.hasMoreTokens()) {
				productId = tabs.nextToken();
				companyId = Integer.parseInt(tabs.nextToken());
			}
			
			bmoWhStock.getProductId().setValue(productId);
			bmoWhStock.getQuantity().setValue(getStockQuantity(bmoWhStock, companyId));
			bmUpdateResult.setBmObject(bmoWhStock);
		}

		return bmUpdateResult;
	}

	// Busca por clave de producto
	private BmoWhStock searchProductCodeByBarcode(PmConn pmConn, String barcode) throws SFException {
		bmoWhStock = new BmoWhStock();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT whst_whstockid FROM whstocks " +
				" LEFT JOIN products ON (whst_productid = prod_productid) " + 
				" LEFT JOIN whsections ON(whse_whsectionid = whst_whsectionid) " +
				" LEFT JOIN warehouses ON(ware_warehouseid = whse_warehouseid) " +
				" WHERE prod_code LIKE '" + barcode + "' " +
				((getDisclosureFilters().length() > 0) ? " AND " + getDisclosureFilters() : "");

		pmConn.doFetch(sql);

		System.out.println("Busqueda de producto por clave: " + sql);

		if (pmConn.next()) {
			int whStockId = pmConn.getInt("whst_whstockid");
			bmoWhStock = (BmoWhStock)this.get(pmConn, whStockId);	
		}

		return bmoWhStock;
	}


	// Busca producto por numero de serie
	private BmoWhStock searchProductBySerial(PmConn pmConn, String barcode) throws SFException {
		bmoWhStock = new BmoWhStock();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT whst_whstockid FROM whstocks " +
				" LEFT JOIN whtracks ON (whst_whtrackid = whtr_whtrackid) " + 
				" LEFT JOIN whsections ON(whse_whsectionid = whst_whsectionid) " +
				" LEFT JOIN warehouses ON(ware_warehouseid = whse_warehouseid) " +
				" WHERE whtr_serial LIKE '" + barcode + "' " +
				((getDisclosureFilters().length() > 0) ? " AND " + getDisclosureFilters() : "");

		pmConn.doFetch(sql);

		System.out.println("Busqueda de producto por serial: " + sql);

		if (pmConn.next()) {
			int whStockId = pmConn.getInt("whst_whstockid");
			bmoWhStock = (BmoWhStock)this.get(pmConn, whStockId);	
		}

		return bmoWhStock;
	}

	// Regresa la seccion del elemento segun numero de serie
	public int getWhSectionByWhTrackId(PmConn pmConn, int whTrackId) throws SFException {
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT whst_whsectionid FROM whstocks " +
				" WHERE whst_whtrackid = " + whTrackId + "";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return pmConn.getInt("whst_whsectionid");
		} else {
			return 0;
		}
	}
}
