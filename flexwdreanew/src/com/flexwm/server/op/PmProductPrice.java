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
import com.symgae.server.sf.PmCompany;
import com.flexwm.server.cm.PmMarket;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


public class PmProductPrice extends PmObject {
	BmoProductPrice bmoProductPrice;

	public PmProductPrice(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductPrice = new BmoProductPrice();
		setBmObject(bmoProductPrice);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProductPrice.getCompanyId(), bmoProductPrice.getBmoCompany()), 
				new PmJoin(bmoProductPrice.getOrderTypeId(), bmoProductPrice.getBmoOrderType()),
				new PmJoin(bmoProductPrice.getWFlowTypeId(), bmoProductPrice.getBmoWFlowType()),
				new PmJoin(bmoProductPrice.getBmoWFlowType().getWFlowCategoryId(), bmoProductPrice.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoProductPrice.getMarketId(), bmoProductPrice.getBmoMarket()),
				new PmJoin(bmoProductPrice.getCurrencyId(), bmoProductPrice.getBmoCurrency())
				)));	
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoProductPrice bmoCustomerCompany = (BmoProductPrice)autoPopulate(pmConn, new BmoProductPrice());

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if ((int)pmConn.getInt(bmoCurrency.getIdFieldName()) > 0) 
			bmoProductPrice.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else 
			bmoProductPrice.setBmoCurrency(bmoCurrency);

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		if ((int)pmConn.getInt(bmoOrderType.getIdFieldName()) > 0) 
			bmoProductPrice.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else 
			bmoProductPrice.setBmoOrderType(bmoOrderType);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		if ((int)pmConn.getInt(bmoWFlowType.getIdFieldName()) > 0) 
			bmoProductPrice.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else 
			bmoProductPrice.setBmoWFlowType(bmoWFlowType);

		// BmoMarket
		BmoMarket bmoMarket = new BmoMarket();
		if ((int)pmConn.getInt(bmoMarket.getIdFieldName()) > 0) 
			bmoProductPrice.setBmoMarket((BmoMarket) new PmMarket(getSFParams()).populate(pmConn));
		else 
			bmoProductPrice.setBmoMarket(bmoMarket);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoCustomerCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoCustomerCompany.setBmoCompany(bmoCompany);

		return bmoCustomerCompany;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoProductPrice = (BmoProductPrice)bmObject;

		if(bmoProductPrice.getCompanyId().toInteger() > 0) {

			if(!compareCompanyproductPrice(pmConn, bmoProductPrice.getProductId().toInteger(), bmoProductPrice.getCompanyId().toInteger())) {
				//bmUpdateResult.addMsg("La Empresa no es válida para el Producto");
				bmUpdateResult.addError(bmoProductPrice.getCompanyId().getName(), "La Empresa no es válida para el Producto");
			}else {
				bmoProductPrice.getCompanyId().setValue(bmoProductPrice.getCompanyId().toInteger());
			}
		}

		super.save(pmConn, bmoProductPrice, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		// Obten el precio vigente
		if (action.equals(BmoProductPrice.ACTION_GETPRICE)) {
			BmoProductPrice bmoProductPrice = (BmoProductPrice)bmObject;
			bmoProductPrice.getPrice().setValue(getCurrentPrice(bmoProductPrice.getStartDate().toString(),
					bmoProductPrice.getProductId().toInteger(), bmoProductPrice.getCurrencyId().toInteger(),
					bmoProductPrice.getOrderTypeId().toInteger(), bmoProductPrice.getWFlowTypeId().toInteger(),
					bmoProductPrice.getMarketId().toInteger(), bmoProductPrice.getCompanyId().toInteger())); 
			bmUpdateResult.setBmObject(bmoProductPrice);
		} 

		return bmUpdateResult;
	}

	// Calcula y obtiene el precio vigente
	public Double getCurrentPrice(String startDate, int productId, int currencyId, int orderTypeId, int wflowTypeId, int marketId, int companyId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();

			return getCurrentPrice(pmConn, startDate, productId, currencyId, orderTypeId, wflowTypeId, marketId, companyId);

		} catch (SFPmException e) {
			throw new SFException("" + e.toString());
		} finally {
			pmConn.close();
		}
	}

	// Calcula el precio vigente actual
	public Double getCurrentPrice(PmConn pmConn, int productId, int currencyId, int orderTypeId, int wflowTypeId, int marketId, int companyId) throws SFException {
		return getCurrentPrice(pmConn, SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), productId, currencyId, orderTypeId, wflowTypeId, marketId, companyId);
	}

	// Calcula y obtiene el precio vigente
	public Double getCurrentPrice(PmConn pmConn, String startDate, int productId, int currencyId, int orderTypeId, int wflowTypeId, int marketId, int companyId) throws SFException {
		double price = 0;
		boolean requiresOrderType = false, requiresMarket = false, requiresCompany = false, requiresWFlowType = false;

		// Revisar fecha de inicio que no sea menor al dia actual
		String priceDate = startDate;
		if (SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), startDate, SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()))) {
			priceDate = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
		}

		// SQL Base
		String baseSql = "SELECT * FROM " + formatKind("productprices") 
		+ " WHERE prpc_startdate <= '" + priceDate  + "' "
		+ " AND prpc_productid = " + productId
		+ " AND prpc_currencyid = " + currencyId;

		// Determina si requiere especificar tipo de pedido
		String sql = baseSql + " AND prpc_ordertypeid = " + orderTypeId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			requiresOrderType = true;
		}

		// Determina si requiere especificar tipo de flujo
		sql = baseSql + " AND prpc_wflowtypeid = " + wflowTypeId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			requiresWFlowType = true;
		}

		// Determina si requiere especificar mercado
		sql = baseSql + " AND prpc_marketid =" + marketId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			requiresMarket = true;
		}

		// Determina si requiere especificar empresa
		sql = baseSql + " AND prpc_companyid = " + companyId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			requiresCompany = true;
		}

		// Genera el SQL a utilizar
		sql = baseSql;
		if (requiresOrderType) sql += " AND prpc_ordertypeid = " + orderTypeId;
		else sql += " AND prpc_ordertypeid IS NULL";

		if (requiresWFlowType) sql += " AND prpc_wflowtypeid = " + wflowTypeId;
		else sql += " AND prpc_wflowtypeid IS NULL";

		if (requiresMarket) sql += " AND prpc_marketid = " + marketId;
		else sql += " AND prpc_marketid IS NULL";

		if (requiresCompany) sql += " AND prpc_companyid = " + companyId;
		else sql += " AND prpc_companyid IS NULL";

		sql +=  " ORDER BY prpc_startdate DESC";

		printDevLog("SQL_getCurrentPrice:" + sql);

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			price = pmConn.getDouble("prpc_price");
		}	

		return price;
	}

	//validar si la empresa esta asiganada al producto
	public boolean compareCompanyproductPrice( PmConn pmConn,int productId, int companyId) throws SFException {
		boolean exist = false;

		String sql = "SELECT * FROM productcompanies WHERE (prcp_productid = " + productId + ") "
				+ " AND (prcp_companyid = " + companyId + ")";

		pmConn.doFetch(sql);
		if (pmConn.next())
			exist = true;

		return exist;
	}
}
