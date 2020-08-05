/**
 * 
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


public class BmoBankMovConcept extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, amount, paccountId, raccountId, bankMovementId, foreignId, total, depositPaccountItemId, 
	                budgetItemId, currencyParity, amountCoverted, orderDeliveryId, requisitionId;
	private BmoBankMovement bmoBankMovement = new BmoBankMovement();
	private BmoRaccount bmoRaccount = new BmoRaccount();
	
	public static final char GETTOTAL = 'T';
	public static final char GETPARITY = 'P';
	
	public static final String ACCESS_CHANGEPARITY = "BKMCCHCUREP";
 	public static final String ACTION_DELETEPACCOUNTID = "DELETEPACCID";
 
	public BmoBankMovConcept() {
		super("com.flexwm.server.fi.PmBankMovConcept","bankmovconcepts", "bankmovconceptid", "BKMC","Conceptos Bancarios"); 
		 
		code = setField("code", "", "Clave", 40 ,0,true, BmFieldType.CODE, false);
		amount = setField("amount", "0", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		bankMovementId = setField("bankmovementid", "", "Mov. Banco", 8, Types.INTEGER, true, BmFieldType.ID, false);
	    paccountId = setField("paccountid", "", "CxP", 8, Types.INTEGER, true, BmFieldType.ID, false);	    
	    raccountId = setField("raccountid", "", "CxC", 8, Types.INTEGER, true, BmFieldType.ID, false);
	    foreignId = setField("foreignid", "", "Cta. Relacionada", 8, Types.INTEGER, true, BmFieldType.ID, false);
	    depositPaccountItemId = setField("depositpaccountitemid", "", "Item CxP Rel.", 8, Types.INTEGER, true, BmFieldType.ID, false);
//	    depositRaccountItemId = setField("depositraccountitemid", "", "Item CxC Rel.", 8, Types.INTEGER, true, BmFieldType.ID, false);
	    budgetItemId = setField("budgetitemid", "", "Presupuesto", 4, Types.INTEGER, true, BmFieldType.ID, false);
	    orderDeliveryId = setField("orderdeliveryid", "", "Envio Pedido", 4, Types.INTEGER, true, BmFieldType.ID, false);
	    currencyParity = setField("currencyparity", "0", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	    amountCoverted = setField("amountconverted", "0", "Aplicar", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	    requisitionId = setField("requisitionid", "", "O.C.", 8, Types.INTEGER, true, BmFieldType.ID, false);	    

	}
	
	/**
	 * @return the raccountId
	 */
	public BmField getRaccountId() {
		return raccountId;
	}

	/**
	 * @param raccountId the raccountId to set
	 */
	public void setRaccountId(BmField raccountId) {
		this.raccountId = raccountId;
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getAmount()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode().getName(), getCode().getLabel()), 
				new BmSearchField(getAmount().getName(), getAmount().getLabel())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBankMovementId(), BmOrder.ASC)));
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
	
	/**
	 * @return the bankMovementId
	 */
	public BmField getBankMovementId() {
		return bankMovementId;
	}

	/**
	 * @param bankMovementId the bankMovementId to set
	 */
	public void setBankMovementId(BmField bankMovementId) {
		this.bankMovementId = bankMovementId;
	}

	/**
	 * @return the bmoBankMovement
	 */
	public BmoBankMovement getBmoBankMovement() {
		return bmoBankMovement;
	}

	/**
	 * @param bmoBankMovement the bmoBankMovement to set
	 */
	public void setBmoBankMovement(BmoBankMovement bmoBankMovement) {
		this.bmoBankMovement = bmoBankMovement;
	}

	public BmField getForeignId() {
		return foreignId;
	}

	public void setForeignId(BmField foreignId) {
		this.foreignId = foreignId;
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
	 * @return the depositPaccountItemId
	 */
	public BmField getDepositPaccountItemId() {
		return depositPaccountItemId;
	}

	/**
	 * @param depositPaccountItemId the depositPaccountItemId to set
	 */
	public void setDepositPaccountItemId(BmField depositPaccountItemId) {
		this.depositPaccountItemId = depositPaccountItemId;
	}
//
//	/**
//	 * @return the depositRaccountItemId
//	 */
//	public BmField getDepositRaccountItemId() {
//		return depositRaccountItemId;
//	}
//
//	/**
//	 * @param depositRaccountItemId the depositRaccountItemId to set
//	 */
//	public void setDepositRaccountItemId(BmField depositRaccountItemId) {
//		this.depositRaccountItemId = depositRaccountItemId;
//	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getAmountCoverted() {
		return amountCoverted;
	}

	public void setAmountCoverted(BmField amountCoverted) {
		this.amountCoverted = amountCoverted;
	}

	public BmField getOrderDeliveryId() {
		return orderDeliveryId;
	}

	public void setOrderDeliveryId(BmField orderDeliveryId) {
		this.orderDeliveryId = orderDeliveryId;
	}

	public BmoRaccount getBmoRaccount() {
		return bmoRaccount;
	}

	public void setBmoRaccount(BmoRaccount bmoRaccount) {
		this.bmoRaccount = bmoRaccount;
	}

	public BmField getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(BmField requisitionId) {
		this.requisitionId = requisitionId;
	}

}
