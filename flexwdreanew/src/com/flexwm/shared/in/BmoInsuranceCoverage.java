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

public class BmoInsuranceCoverage extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoInsurance bmoInsurance = new BmoInsurance();
	private BmoCoverage bmoCoverage = new BmoCoverage();
	private BmField insuranceId, coverageId;

	public BmoInsuranceCoverage() {
		super("com.flexwm.server.in.PmInsuranceCoverage", "insurancecoverages", "insurancecoverageid", "INCV", "Coberturas Prod/Seguro");
		insuranceId = setField("insuranceid", "", "Prod. Seguro", 8, Types.INTEGER, false, BmFieldType.ID, false);
		coverageId = setField("coverageid", "", "Cobertura", 8, Types.INTEGER, false, BmFieldType.ID, false);
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

	public BmoCoverage getBmoCoverage() {
		return bmoCoverage;
	}

	public void setBmoCoverage(BmoCoverage bmoCoverage) {
		this.bmoCoverage = bmoCoverage;
	}

	public BmField getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(BmField insuranceId) {
		this.insuranceId = insuranceId;
	}

	public BmField getCoverageId() {
		return coverageId;
	}

	public void setCoverageId(BmField coverageId) {
		this.coverageId = coverageId;
	}
}
