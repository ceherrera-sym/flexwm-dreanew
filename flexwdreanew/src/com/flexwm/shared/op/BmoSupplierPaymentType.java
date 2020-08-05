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
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaymentType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;


public class BmoSupplierPaymentType extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmField supplierId, currencyId, paymentTypeId;
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoPaymentType bmoPaymentType = new BmoPaymentType();

	public BmoSupplierPaymentType() {
		super("com.flexwm.server.op.PmSupplierPaymentType", "supplierpaymenttypes", "supplierpaymenttypeid", "SUPT", "Métodos de Pago Pv.");
		supplierId = setField("supplierid", "", "Proveedor", 10, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
		paymentTypeId= setField("paymenttypeid", "", "Método de Pago", 2, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoPaymentType().getName(),
				getBmoCurrency().getCode()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	public BmField getPaymentTypeId() {
		return paymentTypeId;
	}

	public BmoPaymentType getBmoPaymentType() {
		return bmoPaymentType;
	}

	public void setPaymentTypeId(BmField paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public void setBmoPaymentType(BmoPaymentType bmoPaymentType) {
		this.bmoPaymentType = bmoPaymentType;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

}
