/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
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


public class BmoBudgetItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField budgetId, amount, comments, budgetItemTypeId, provisioned, payments, balance,currencyId,currencyParity; 
	
	BmoBudget bmoBudget = new BmoBudget();	
	BmoBudgetItemType bmoBudgetItemType = new BmoBudgetItemType();
	BmoCurrency bmoCurrency = new BmoCurrency();
	
	public BmoBudgetItem() {
		super("com.flexwm.server.fi.PmBudgetItem", "budgetitems", "budgetitemid", "BGIT", "Item Presupuesto");
		
		comments = setField("comments", "", "Comentarios", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		provisioned = setField("provisioned", "", "Provisionado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		budgetId = setField("budgetid", "", "Presupuesto", 20, Types.INTEGER, false, BmFieldType.ID, false);
		budgetItemTypeId = setField("budgetitemtypeid", "", "Tipo", 20, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, true, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", " Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}
		
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(	
				getBmoBudget().getName(),
				getBmoBudgetItemType().getName(),
				getBmoCurrency().getCode(),
				getAmount(),
				getProvisioned(),
				getPayments(),
				getBalance()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoBudget().getName(),
				getBmoBudgetItemType().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoBudgetItemType().getName()),
				new BmSearchField(getBmoBudget().getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(),getBmoBudget().getName(), BmOrder.ASC),
				new BmOrder(getKind(), getBmoBudgetItemType().getName(), BmOrder.ASC)
				));
	}

	/**
	 * @return the budgetId
	 */
	public BmField getBudgetId() {
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(BmField budgetId) {
		this.budgetId = budgetId;
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

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	/**
	 * @return the bmoBudget
	 */
	public BmoBudget getBmoBudget() {
		return bmoBudget;
	}

	/**
	 * @param bmoBudget the bmoBudget to set
	 */
	public void setBmoBudget(BmoBudget bmoBudget) {
		this.bmoBudget = bmoBudget;
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
	 * @return the payments
	 */
	public BmField getPayments() {
		return payments;
	}

	/**
	 * @param payments the payments to set
	 */
	public void setPayments(BmField payments) {
		this.payments = payments;
	}

	public BmField getBudgetItemTypeId() {
		return budgetItemTypeId;
	}

	public void setBudgetItemTypeId(BmField budgetItemTypeId) {
		this.budgetItemTypeId = budgetItemTypeId;
	}

	public BmoBudgetItemType getBmoBudgetItemType() {
		return bmoBudgetItemType;
	}

	public void setBmoBudgetItemType(BmoBudgetItemType bmoBudgetItemType) {
		this.bmoBudgetItemType = bmoBudgetItemType;
	}

	public BmField getProvisioned() {
		return provisioned;
	}

	public void setProvisioned(BmField provisioned) {
		this.provisioned = provisioned;
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

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}
	
	
	
	
	
	
}
