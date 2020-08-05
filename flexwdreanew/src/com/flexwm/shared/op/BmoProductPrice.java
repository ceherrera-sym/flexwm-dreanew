/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.sf.BmoCompany;

public class BmoProductPrice extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField price, startDate, productId, orderTypeId, wFlowTypeId, currencyId, marketId, companyId;
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoOrderType bmoOrderType = new BmoOrderType();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmoMarket bmoMarket = new BmoMarket();
	private BmoCompany bmoCompany = new BmoCompany();
	
	public static String ACTION_GETPRICE = "GETPRICE";

	public BmoProductPrice() {
		super("com.flexwm.server.op.PmProductPrice", "productprices", "productpriceid", "PRPC", "Precios Productos");
		
		price = setField("price", "0", "Precio", 20, Types.FLOAT, false, BmFieldType.CURRENCY, false);
		startDate = setField("startdate", "", "Vigencia", 20, Types.DATE, false, BmFieldType.DATE, false);
		productId = setField("productid", "", "Producto", 10, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 10, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 10, Types.INTEGER, true, BmFieldType.ID, false);
		marketId = setField("marketid", "", "Mercado", 10, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 10, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName(),
				getBmoOrderType().getName(),
				getBmoWFlowType().getName(),
				getBmoMarket().getName(),
				getPrice(),
				getBmoCurrency().getCode(),
				getStartDate()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getStartDate(), BmOrder.DESC),
				new BmOrder(getKind(), getPrice(), BmOrder.ASC)
				));
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getProductId() {
		return productId;
	}

	public void setProductId(BmField productId) {
		this.productId = productId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField CompanyId) {
		this.companyId = CompanyId;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
	}

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmoMarket getBmoMarket() {
		return bmoMarket;
	}

	public void setBmoMarket(BmoMarket bmoMarket) {
		this.bmoMarket = bmoMarket;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

}
