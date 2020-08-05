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

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoRaccountAssignmentParent extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, invoiceno, amount, raccountId, foreignRaccountId, currencyParity, amountConverted;
	
	private BmoRaccount bmoForeignRaccount = new BmoRaccount();
	// IMPORTATE: SI SE VA A MODIFICA ESTE ARCHIVO SE DEBE CAMBIAR EN BmoRaccountAssignment.java TAMBIEN
	// Este archivo es para crear un join con diferente destino, ya que no se pueden agregar dos join de una misma tabla

	public BmoRaccountAssignmentParent() {
		super("com.flexwm.server.fi.PmRaccountAssignmentParent", "raccountassignments", "raccountassignmentid", "RASS", "Aplicaciones CxC");
		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		invoiceno = setField("invoiceno", "", "Factura", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		
		amount = setField("amount", "", "Monto Asignado", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);		
		raccountId = setField("raccountid", "", "CxC Origen", 20, Types.INTEGER, false, BmFieldType.ID, false);		
		foreignRaccountId = setField("foreignraccountid", "", "CxC Destino", 20, Types.INTEGER, true, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 30, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amountConverted = setField("amountconverted", "0", "Aplicar", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getInvoiceno(),
				getBmoForeignRaccount().getCode(), 
				getBmoForeignRaccount().getInvoiceno(),
				getBmoForeignRaccount().getTotal(), 
				getAmount()
			));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getRaccountId())
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
	 * @return the RaccountId
	 */
	public BmField getRaccountId() {
		return raccountId;
	}

	/**
	 * @param RaccountId the RaccountId to set
	 */
	public void setRaccountId(BmField raccountId) {
		this.raccountId = raccountId;
	}


	public BmField getForeignRaccountId() {
		return foreignRaccountId;
	}

	public void setForeignRaccountId(BmField foreignRaccountId) {
		this.foreignRaccountId = foreignRaccountId;
	}

	public BmoRaccount getBmoForeignRaccount() {
		return bmoForeignRaccount;
	}

	public void setBmoForeignRaccount(BmoRaccount bmoForeignRaccount) {
		this.bmoForeignRaccount = bmoForeignRaccount;
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
