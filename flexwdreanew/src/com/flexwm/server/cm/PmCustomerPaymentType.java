/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.fi.PmPaymentType;
import com.flexwm.shared.cm.BmoCustomerPaymentType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaymentType;
import java.util.ArrayList;
import java.util.Arrays;


public class PmCustomerPaymentType extends PmObject {
	BmoCustomerPaymentType bmoCustomerPaymentType;

	public PmCustomerPaymentType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerPaymentType = new BmoCustomerPaymentType();
		setBmObject(bmoCustomerPaymentType);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerPaymentType.getCurrencyId(), bmoCustomerPaymentType.getBmoCurrency()),
				new PmJoin(bmoCustomerPaymentType.getPaymentTypeId(), bmoCustomerPaymentType.getBmoPaymentType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCustomerPaymentType bmoCustomerPaymentType = (BmoCustomerPaymentType) autoPopulate(pmConn, new BmoCustomerPaymentType());

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0) 
			bmoCustomerPaymentType.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else 
			bmoCustomerPaymentType.setBmoCurrency(bmoCurrency);

		// BmoPaymentType
		BmoPaymentType bmoPaymentType = new BmoPaymentType();
		if (pmConn.getInt(bmoPaymentType.getIdFieldName()) > 0) 
			bmoCustomerPaymentType.setBmoPaymentType((BmoPaymentType) new PmPaymentType(getSFParams()).populate(pmConn));
		else 
			bmoCustomerPaymentType.setBmoPaymentType(bmoPaymentType);

		return bmoCustomerPaymentType;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerPaymentType bmoCustomerPaymentType = (BmoCustomerPaymentType)bmObject;

		// Valida que no se duplique el tipo de pago y moneda en el cliente
		if (customerPaymentTypeExist(pmConn, bmoCustomerPaymentType.getCustomerId().toInteger(),
				bmoCustomerPaymentType.getPaymentTypeId().toInteger(), 
				bmoCustomerPaymentType.getCurrencyId().toInteger(), bmUpdateResult))
			bmUpdateResult.addError(bmoCustomerPaymentType.getPaymentTypeId().getName(), "El Método de Pago ya existe en este cliente");	

		super.save(pmConn, bmoCustomerPaymentType, bmUpdateResult);

		return bmUpdateResult;
	}

	public boolean customerPaymentTypeExist(PmConn pmConn, int customerId, int paymentTypeId, int currencyId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;
		sql = " SELECT COUNT(cupt_customerpaymenttypeid) as items FROM customerpaymenttypes " +
				" WHERE cupt_paymenttypeid = " + paymentTypeId + 
				" AND cupt_currencyid = " + currencyId +
				" AND cupt_customerid = " + customerId;
		pmConn.doFetch(sql);

		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;		
	}
}
