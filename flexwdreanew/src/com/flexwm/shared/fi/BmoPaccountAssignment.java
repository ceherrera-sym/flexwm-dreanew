/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.fi;

import java.io.Serializable;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmField;
import com.symgae.shared.BmSearchField;

public class BmoPaccountAssignment extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;	
	private BmField code, invoiceno, amount, paccountId, foreignPaccountId, currencyParity, amountConverted;
	
	private BmoPaccount bmoForeignPaccount = new BmoPaccount();
	
	public BmoPaccountAssignment() {
		super("com.flexwm.server.fi.PmPaccountAssignment", "paccountassignments", "paccountassignmentid", "PASS", "Asignaciones de Cuentas por Pagar");
		
		// Campo de datos
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		invoiceno = setField("invoiceno", "", "Factura", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		
		amount = setField("amount", "", "Monto Asignado", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		paccountId = setField("paccountid", "", "CxP", 20, Types.INTEGER, false, BmFieldType.ID, false);		
		foreignPaccountId = setField("foreignpaccountid", "", "CxP Asignada", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 30, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amountConverted = setField("amountconverted", "0", "Aplicar", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getInvoiceno(),
				getBmoForeignPaccount().getCode(), 
				getBmoForeignPaccount().getInvoiceno(),
				getBmoForeignPaccount().getAmount(), 
				getAmount()
			));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getForeignPaccountId())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the amount
	 */
	public BmField getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	/**
	 * @return the paccountId
	 */
	public BmField getPaccountId() {
		return paccountId;
	}

	/**
	 * @param paccountId the paccountId to set
	 */
	public void setPaccountId(BmField paccountId) {
		this.paccountId = paccountId;
	}


	public BmField getForeignPaccountId() {
		return foreignPaccountId;
	}

	public void setForeignPaccountId(BmField foreignPaccountId) {
		this.foreignPaccountId = foreignPaccountId;
	}

	public BmoPaccount getBmoForeignPaccount() {
		return bmoForeignPaccount;
	}

	public void setBmoForeignPaccount(BmoPaccount bmoForeignPaccount) {
		this.bmoForeignPaccount = bmoForeignPaccount;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(BmField invoiceno) {
		this.invoiceno = invoiceno;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getAmountConverted() {
		return amountConverted;
	}

	public void setAmountConverted(BmField amountConverted) {
		this.amountConverted = amountConverted;
	}
	
	
}
