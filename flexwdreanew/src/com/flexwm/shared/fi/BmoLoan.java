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
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author smuniz
 *
 */

public class BmoLoan extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name,	description, amount, rate, commissionPayment, commission, startDate, endDate, disbursed, progress, supplierId, developmentPhaseId, 
	balance, disbursedAmount, capitalPayment, capitalBalance, companyId, revolving;
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();

	public static String CODE_PREFIX = "CR-";

	public static final String ACTION_TOTAL = "ACTION_TOTAL";
	public static final String ACTION_PAYMENTS = "ACTION_PAYMENT";

	public BmoLoan(){
		super("com.flexwm.server.fi.PmLoan", "loans", "loanid", "LOAN", "Financiamiento");

		//Campo de Datos		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "", "Monto Crédito", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		rate = setField("rate", "", "Tasa", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		//downPayment = setField("downpayment", "", "Anticipo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		commissionPayment = setField("commissionpayment", "", "Pagos Comision", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		commission = setField("commission", "", "Comisión", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		startDate = setField("startdate", "", "Fecha Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fecha Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		disbursed = setField("disbursed", "", "Por Disponer", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		progress = setField("progress", "", "Avance", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		supplierId = setField("supplierid", "", "Acreedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		developmentPhaseId = setField("developmentphaseid", "", "Etapa D.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		balance = setField("balance", "", "Ministraciones", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		disbursedAmount = setField("disbursedamount", "", "Monto Dispuesto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		capitalPayment = setField("capitalpayment", "", "Pago Capital", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		capitalBalance = setField("capitalbalance", "", "Saldo Capital", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		revolving = setField("revolving", "", "Revolvente", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				bmoSupplier.getCode(),
				bmoDevelopmentPhase.getCode(),
				getProgress(),
				getAmount(),
				getDisbursed()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getProgress(),
				getAmount(),
				getDisbursed()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
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
	 * @return the rate
	 */
	public BmField getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(BmField rate) {
		this.rate = rate;
	}

	public BmField getCommissionPayment() {
		return commissionPayment;
	}

	public void setCommissionPayment(BmField commissionPayment) {
		this.commissionPayment = commissionPayment;
	}

	/**
	 * @return the commission
	 */
	public BmField getCommission() {
		return commission;
	}

	/**
	 * @param commission the commission to set
	 */
	public void setCommission(BmField commission) {
		this.commission = commission;
	}

	/**
	 * @return the startDate
	 */
	public BmField getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public BmField getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the disbursed
	 */
	public BmField getDisbursed() {
		return disbursed;
	}

	/**
	 * @param disbursed the disbursed to set
	 */
	public void setDisbursed(BmField disbursed) {
		this.disbursed = disbursed;
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
	 * @return the supplierId
	 */
	public BmField getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the developmentPhaseId
	 */
	public BmField getDevelopmentPhaseId() {
		return developmentPhaseId;
	}

	/**
	 * @param developmentPhaseId the developmentPhaseId to set
	 */
	public void setDevelopmentPhaseId(BmField developmentPhaseId) {
		this.developmentPhaseId = developmentPhaseId;
	}

	/**
	 * @return the bmoSupplier
	 */
	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	/**
	 * @param bmoSupplier the bmoSupplier to set
	 */
	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	/**
	 * @return the bmoDevelopmentPhase
	 */
	public BmoDevelopmentPhase getBmoDevelopmentPhase() {
		return bmoDevelopmentPhase;
	}

	/**
	 * @param bmoDevelopmentPhase the bmoDevelopmentPhase to set
	 */
	public void setBmoDevelopmentPhase(BmoDevelopmentPhase bmoDevelopmentPhase) {
		this.bmoDevelopmentPhase = bmoDevelopmentPhase;
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
	 * @return the disbursedAmount
	 */
	public BmField getDisbursedAmount() {
		return disbursedAmount;
	}

	/**
	 * @param disbursedAmount the disbursedAmount to set
	 */
	public void setDisbursedAmount(BmField disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	/**
	 * @return the capitalPayment
	 */
	public BmField getCapitalPayment() {
		return capitalPayment;
	}

	/**
	 * @param capitalPayment the capitalPayment to set
	 */
	public void setCapitalPayment(BmField capitalPayment) {
		this.capitalPayment = capitalPayment;
	}

	/**
	 * @return the capitalBalance
	 */
	public BmField getCapitalBalance() {
		return capitalBalance;
	}

	/**
	 * @param capitalBalance the capitalBalance to set
	 */
	public void setCapitalBalance(BmField capitalBalance) {
		this.capitalBalance = capitalBalance;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getRevolving() {
		return revolving;
	}

	public void setRevolving(BmField revolving) {
		this.revolving = revolving;
	}

}

