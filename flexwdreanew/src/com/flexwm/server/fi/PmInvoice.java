/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.fi;

import java.util.Iterator;
import com.flexwm.server.op.PmOrderDelivery;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoInvoice;
import com.flexwm.shared.fi.BmoInvoiceOrderDelivery;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmInvoice extends PmObject{
	BmoInvoice bmoInvoice;


	public PmInvoice(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoInvoice = new BmoInvoice();
		setBmObject(bmoInvoice); 



	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {		
		bmoInvoice = (BmoInvoice) autoPopulate(pmConn, new BmoInvoice());

		return bmoInvoice;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoInvoice bmoInvoice = (BmoInvoice)bmObject;

		if (!(bmoInvoice.getId() > 0)) {

			super.save(pmConn, bmoInvoice, bmUpdateResult);

			//agregar los envios ligados al pedido
			createItemsFromOrder(pmConn, bmoInvoice, bmUpdateResult);
		}

		super.save(pmConn, bmoInvoice, bmUpdateResult);

		updateBalance(pmConn, bmoInvoice, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateBalance(PmConn pmConn, BmoInvoice bmoInvoice, BmUpdateResult bmUpdateResult) throws SFException {
		//Obtener la suma de los items
		double sumItems = sumItems(pmConn, bmoInvoice, bmUpdateResult);

		if (sumItems > 0) {
			double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
			double tax = sumItems * taxRate;

			bmoInvoice.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(sumItems));			
			bmoInvoice.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
			bmoInvoice.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(sumItems + tax));

		} else  {
			bmoInvoice.getAmount().setValue(0);
			bmoInvoice.getTax().setValue(0);
			bmoInvoice.getTotal().setValue(0);
		}

		super.save(pmConn, bmoInvoice, bmUpdateResult);

	}

	public double sumItems(PmConn pmConn, BmoInvoice bmoInvoice, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double sumItems = 0;

		sql = " SELECT SUM(inod_amount) AS sumItems  FROM invoiceorderdeliveries " +
				" WHERE inod_invoiceid = " + bmoInvoice.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) sumItems = pmConn.getDouble("sumItems");

		return sumItems;
	}

	public void createItemsFromOrder(PmConn pmConn, BmoInvoice bmoInvoice, BmUpdateResult bmUpdateResult) throws SFException {

		PmInvoiceOrderDelivery pmOInvoiceOrderDelivery = new PmInvoiceOrderDelivery(getSFParams());
		BmoInvoiceOrderDelivery bmoInvoiceOrderDelivery =  new BmoInvoiceOrderDelivery();

		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();

		BmFilter filterOrderDelivery = new BmFilter();
		filterOrderDelivery.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getOrderId(), bmoInvoice.getOrderId().toInteger());
		Iterator<BmObject> listOrderDelivery = new PmOrderDelivery(getSFParams()).list(pmConn, filterOrderDelivery).iterator();

		while (listOrderDelivery.hasNext()) {
			bmoOrderDelivery = (BmoOrderDelivery)listOrderDelivery.next();

			bmoInvoiceOrderDelivery =  new BmoInvoiceOrderDelivery();
			bmoInvoiceOrderDelivery.getCode().setValue(bmoOrderDelivery.getCode().toString());
			bmoInvoiceOrderDelivery.getInvoiceId().setValue(bmoInvoice.getId());
			bmoInvoiceOrderDelivery.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());
			bmoInvoiceOrderDelivery.getAmount().setValue(bmoOrderDelivery.getAmount().toDouble());

			pmOInvoiceOrderDelivery.save(pmConn, bmoInvoiceOrderDelivery, bmUpdateResult);

		}	

	}

}

