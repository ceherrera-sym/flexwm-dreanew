/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.in;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;

public class BmoInsuranceFund extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoInsurance bmoInsurance;
	private BmoFund bmoFund;
	private BmField insuranceId, fundId;

	public BmoInsuranceFund() {
		super("com.flexwm.server.in.PmInsuranceFund", "insurancefunds", "insurancefundid", "INFU", "Fondos Prod/Seguro");
		insuranceId = setField("insuranceid", "", "Prod. Seguro", 8, Types.INTEGER, false, BmFieldType.ID, false);
		fundId = setField("fundid", "", "Cobertura", 8, Types.INTEGER, false, BmFieldType.ID, false);
		
		bmoInsurance = new BmoInsurance();
		bmoFund = new BmoFund();
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoInsurance().getCode(),
				getBmoInsurance().getName(), 
				getBmoInsurance().getDescription() 
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmoInsurance getBmoInsurance() {
		return bmoInsurance;
	}

	public void setBmoInsurance(BmoInsurance bmoInsurance) {
		this.bmoInsurance = bmoInsurance;
	}

	public BmoFund getBmoFund() {
		return bmoFund;
	}

	public void setBmoFund(BmoFund bmoFund) {
		this.bmoFund = bmoFund;
	}

	public BmField getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(BmField insuranceId) {
		this.insuranceId = insuranceId;
	}

	public BmField getFundId() {
		return fundId;
	}

	public void setFundId(BmField fundId) {
		this.fundId = fundId;
	}
}
