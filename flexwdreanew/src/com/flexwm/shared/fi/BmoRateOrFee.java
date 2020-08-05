package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.fi.BmoFactorType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;

public class BmoRateOrFee extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code,maxValue,taxId,factorTypeId,companyId,transfer,retention;
	private BmoTax bmoTax = new BmoTax();
	private BmoFactorType bmoFactorType = new BmoFactorType();
	private BmoCompany bmoCompany = new BmoCompany();
	public static String CODE_PREFIX = "RF-";
	
	public BmoRateOrFee() {
		super("com.flexwm.server.fi.PmRateOrFee","rateorfees","rateorfeesid","RAFE","Taso o Cuota");
		code = setField("code", "", "Codigo", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		maxValue = setField("maxvalue", "", "Valor Máximo", 10, Types.DOUBLE,false , BmFieldType.NUMBER, false);
		taxId = setField("taxid", "", "Impuesto", 10, Types.INTEGER, false, BmFieldType.ID, false);
		factorTypeId = setField("factortypeid", "", "Tipo de Factor", 10, Types.INTEGER, false,  BmFieldType.ID, false);
		transfer = setField("transfer", "", "Traslado", 1, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		retention = setField("retention", "", "Retención", 1, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		companyId = setField("companyid", "", "Empresa", 10, Types.INTEGER, false,  BmFieldType.ID, false);		
	}
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getMaxValue(),
				getBmoTax().getDescrition(),
				getBmoFactorType().getCode(),
				getBmoCompany().getName()
				));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getBmoTax().getDescrition())				
				));
	}
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}

	public BmoTax getBmoTax() {
		return bmoTax;
	}
	public void setBmoTax(BmoTax bmoTax) {
		this.bmoTax = bmoTax;
	}
	public BmoFactorType getBmoFactorType() {
		return bmoFactorType;
	}
	public void setBmoFactorType(BmoFactorType bmoFactorType) {
		this.bmoFactorType = bmoFactorType;
	}
	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}
	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}
	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BmField maxValue) {
		this.maxValue = maxValue;
	}

	public BmField getTaxId() {
		return taxId;
	}

	public void setTaxId(BmField taxId) {
		this.taxId = taxId;
	}

	public BmField getFactorTypeId() {
		return factorTypeId;
	}

	public void setFactorTypeId(BmField factorTypeId) {
		this.factorTypeId = factorTypeId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}
	public BmField getTransfer() {
		return transfer;
	}
	public void setTransfer(BmField transfer) {
		this.transfer = transfer;
	}
	public BmField getRetention() {
		return retention;
	}
	public void setRetention(BmField retention) {
		this.retention = retention;
	}	
	

}
