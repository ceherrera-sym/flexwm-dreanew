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
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.op.BmoSupplierPaymentType;
import java.util.ArrayList;
import java.util.Arrays;


public class PmSupplierPaymentType extends PmObject {
	BmoSupplierPaymentType bmoSupplierPaymentType;

	public PmSupplierPaymentType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierPaymentType = new BmoSupplierPaymentType();
		setBmObject(bmoSupplierPaymentType);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplierPaymentType.getCurrencyId(), bmoSupplierPaymentType.getBmoCurrency()),
				new PmJoin(bmoSupplierPaymentType.getPaymentTypeId(), bmoSupplierPaymentType.getBmoPaymentType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoSupplierPaymentType bmoSupplierPaymentType = (BmoSupplierPaymentType) autoPopulate(pmConn, new BmoSupplierPaymentType());

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0) 
			bmoSupplierPaymentType.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else 
			bmoSupplierPaymentType.setBmoCurrency(bmoCurrency);

		// BmoPaymentType
		BmoPaymentType bmoPaymentType = new BmoPaymentType();
		if (pmConn.getInt(bmoPaymentType.getIdFieldName()) > 0) 
			bmoSupplierPaymentType.setBmoPaymentType((BmoPaymentType) new PmPaymentType(getSFParams()).populate(pmConn));
		else 
			bmoSupplierPaymentType.setBmoPaymentType(bmoPaymentType);

		return bmoSupplierPaymentType;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSupplierPaymentType bmoSupplierPaymentType = (BmoSupplierPaymentType)bmObject;

		// Valida que no se duplique el tipo de pago y moneda en el proveedor
		if (supplierPaymentTypeExist(pmConn, bmoSupplierPaymentType.getSupplierId().toInteger(),
				bmoSupplierPaymentType.getPaymentTypeId().toInteger(), 
				bmoSupplierPaymentType.getCurrencyId().toInteger(),	bmUpdateResult))
			bmUpdateResult.addError(bmoSupplierPaymentType.getPaymentTypeId().getName(), "El Método de Pago ya existe en este proveedor");	

		super.save(pmConn, bmoSupplierPaymentType, bmUpdateResult);

		return bmUpdateResult;
	}

	public boolean supplierPaymentTypeExist(PmConn pmConn, int supplierId, int paymentTypeId, int currencyId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;
		sql = " SELECT COUNT(supt_supplierpaymenttypeid) as items FROM supplierpaymenttypes " +
				" WHERE supt_paymenttypeid = " + paymentTypeId  +
				" AND supt_currencyid = " + currencyId +
				" AND supt_supplierid = " + supplierId;
		pmConn.doFetch(sql);

		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;		
	}
}
