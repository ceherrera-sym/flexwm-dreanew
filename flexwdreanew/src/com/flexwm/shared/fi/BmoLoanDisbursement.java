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
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
/**
 * @author smuniz
 *
 */
public class BmoLoanDisbursement extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField date, amount, progress, loanId, balance, bankMovConceptId;
	BmoLoan bmoLoan = new BmoLoan();	
	
	public BmoLoanDisbursement(){
		super("com.flexwm.server.fi.PmLoanDisbursement", "loandisbursements", "loandisbursementid", "LODI", "Disposición de Préstamos");
		
		//Campo de Datos		
		date = setField("date", "", "Fecha", 20, Types.DATE, false, BmFieldType.DATE, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		progress = setField("progress", "", "Avance", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		balance = setField("balance", "", "Saldo Ministrado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		loanId = setField("loanid", "", "Préstamo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		bankMovConceptId = setField("bankmovconceptid", "", "concepto en banco", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getDate(),
				getProgress(),
				getAmount()
				//getBalance()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getDate()), 
				new BmSearchField(getAmount())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDate(), BmOrder.ASC)));
	}

	/**
	 * @return the date
	 */
	public BmField getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(BmField date) {
		this.date = date;
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
	 * @return the progress
	 */
	public BmField getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(BmField progress) {
		this.progress = progress;
	}

	/**
	 * @return the loanId
	 */
	public BmField getLoanId() {
		return loanId;
	}

	/**
	 * @param loanId the loanId to set
	 */
	public void setLoanId(BmField loanId) {
		this.loanId = loanId;
	}

	/**
	 * @return the balance
	 */
	public BmField getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BmField balance) {
		this.balance = balance;
	}
	
	/**
	 * @return the bmoLoan
	 */
	public BmoLoan getBmoLoan() {
		return bmoLoan;
	}

	/**
	 * @param bmoBudget the bmoBudget to set
	 */
	public void setBmoLoan(BmoLoan bmoLoan) {
		this.bmoLoan = bmoLoan;
	}

	public BmField getBankMovConceptId() {
		return bankMovConceptId;
	}

	public void setBankMovConceptId(BmField bankMovConceptId) {
		this.bankMovConceptId = bankMovConceptId;
	}
	
	
}
