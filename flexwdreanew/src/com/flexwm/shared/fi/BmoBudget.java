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

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoBudget extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;	
	private BmField name, description, total, startDate, endDate, status, balance, provisioned, payments, userId, companyId,currencyId,
					currencyParity,totalWithdraw, provisionedWithdraw, paymentWithdraw, balanceWithdraw, totalDeposit, 
	                provisionedDeposit, paymentDeposit, balanceDeposit; 
	
	BmoCurrency bmoCurrency = new BmoCurrency();
	
	public static String CODE_PREFIX = "B-";
	public static final String ACCESS_CHANGESTATUS = "WKBGCHS";
	
	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';	
	public static final char STATUS_CLOSED = 'C';
	
	public static final String ACTION_TOTAL = "ACTION_TOTAL";
	
	public BmoBudget(){
		super("com.flexwm.server.fi.PmBudget", "budgets", "budgetid", "BUDG", "Presupuestos");
		
		name = setField("name", "", "Nombre Presup.", 60, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		provisioned = setField("provisioned", "", "Provisionado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		// Egresos
		provisionedWithdraw = setField("provisionedwithdraw", "", "Provisionado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		paymentWithdraw = setField("paymentwithdraw", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		totalWithdraw = setField("totalwithdraw", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balanceWithdraw = setField("balancewithdraw", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		// Ingresos
		provisionedDeposit = setField("provisioneddeposit", "", "Provisionado", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		paymentDeposit = setField("paymentdeposit", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		totalDeposit = setField("totaldeposit", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balanceDeposit = setField("balancedeposit", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		startDate = setField("startdate", "", "Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CLOSED, "Cerrado", "./icons/status_expired.png")	
				)));
		
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 20, Types.INTEGER, true, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);


	}
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(				
				getName(),
				getStartDate(),
				getEndDate(),
				getStatus(),
				getBmoCurrency().getCode(),
				getTotal(),
				getProvisioned(),
				getPayments(),
				getBalance()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getTotal(),
				getProvisioned(),
				getPayments(),
				getBalance()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList( 
				new BmSearchField(getName()),
				new BmSearchField(getDescription())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}
	
	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the total
	 */
	public BmField getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BmField total) {
		this.total = total;
	}

	/**
	 * @return the status
	 */
	public BmField getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
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

	/**
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getProvisioned() {
		return provisioned;
	}

	public void setProvisioned(BmField provisioned) {
		this.provisioned = provisioned;
	}

	public BmField getTotalWithdraw() {
		return totalWithdraw;
	}

	public void setTotalWithdraw(BmField totalWithdraw) {
		this.totalWithdraw = totalWithdraw;
	}

	public BmField getProvisionedWithdraw() {
		return provisionedWithdraw;
	}

	public void setProvisionedWithdraw(BmField provisionedWithdraw) {
		this.provisionedWithdraw = provisionedWithdraw;
	}

	public BmField getPaymentWithdraw() {
		return paymentWithdraw;
	}

	public void setPaymentWithdraw(BmField paymentWithdraw) {
		this.paymentWithdraw = paymentWithdraw;
	}

	public BmField getBalanceWithdraw() {
		return balanceWithdraw;
	}

	public void setBalanceWithdraw(BmField balanceWithdraw) {
		this.balanceWithdraw = balanceWithdraw;
	}

	public BmField getTotalDeposit() {
		return totalDeposit;
	}

	public void setTotalDeposit(BmField totalDeposit) {
		this.totalDeposit = totalDeposit;
	}

	public BmField getProvisionedDeposit() {
		return provisionedDeposit;
	}

	public void setProvisionedDeposit(BmField provisionedDeposit) {
		this.provisionedDeposit = provisionedDeposit;
	}

	public BmField getPaymentDeposit() {
		return paymentDeposit;
	}

	public void setPaymentDeposit(BmField paymentDeposit) {
		this.paymentDeposit = paymentDeposit;
	}

	public BmField getBalanceDeposit() {
		return balanceDeposit;
	}

	public void setBalanceDeposit(BmField balanceDeposit) {
		this.balanceDeposit = balanceDeposit;
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
