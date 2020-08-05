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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;

public class BmoSupplierBankAccount extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmField supplierId, bank, clabe, accountNumber, currencyId;
	private BmoCurrency bmoCurrency = new BmoCurrency();

	public BmoSupplierBankAccount() {
		super("com.flexwm.server.op.PmSupplierBankAccount", "supplierbankaccounts", "supplierbankaccountid", "SUBA", "Cuentas de Banco Pv.");
		supplierId = setField("supplierid", "", "Proveedor", 10, Types.INTEGER, false, BmFieldType.ID, false);
		bank = setField("bank", "", "Banco", 70, Types.VARCHAR, false, BmFieldType.STRING, false);
		clabe = setField("clabe", "", "Clabe", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		accountNumber = setField("accountnumber", "", "No. Cuenta", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBank(),
				getClabe(),
				getAccountNumber(),
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

	public BmField getBank() {
		return bank;
	}

	public void setBank(BmField bank) {
		this.bank = bank;
	}

	public BmField getClabe() {
		return clabe;
	}

	public void setClabe(BmField clabe) {
		this.clabe = clabe;
	}

	public BmField getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(BmField accountNumber) {
		this.accountNumber = accountNumber;
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
