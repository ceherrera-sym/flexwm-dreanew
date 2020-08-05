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

public class BmoPolicyCoverage extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField amount, policyId, coverageId;
	private BmoCoverage bmoCoverage = new BmoCoverage();

	
	public BmoPolicyCoverage() {
		super("com.flexwm.server.in.PmPolicyCoverage", "policycoverages", "policycoverageid", "POCV", "Coberturas");
		
		amount = setField("amount", "", "Suma", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		
		policyId = setField("policyid", "", "Póliza", 8, Types.INTEGER, false, BmFieldType.ID, false);
		coverageId = setField("coverageid", "", "Cobertura", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCoverage().getCode(),
				getBmoCoverage().getName(),
				getBmoCoverage().getMinAmount(),
				getBmoCoverage().getMaxAmount(),			
				getAmount() 
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoCoverage().getCode(), BmOrder.ASC)));
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getPolicyId() {
		return policyId;
	}

	public void setPolicyId(BmField policyId) {
		this.policyId = policyId;
	}

	public BmField getCoverageId() {
		return coverageId;
	}

	public void setCoverageId(BmField coverageId) {
		this.coverageId = coverageId;
	}

	public BmoCoverage getBmoCoverage() {
		return bmoCoverage;
	}

	public void setBmoCoverage(BmoCoverage bmoCoverage) {
		this.bmoCoverage = bmoCoverage;
	}

	
}
