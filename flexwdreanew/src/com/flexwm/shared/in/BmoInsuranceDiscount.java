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

public class BmoInsuranceDiscount extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField insuranceId, discountId;
	private BmoInsurance bmoInsurance = new BmoInsurance();
	private BmoDiscount bmoDiscount = new BmoDiscount();

	
	public BmoInsuranceDiscount() {
		super("com.flexwm.server.in.PmInsuranceDiscount", "insurancediscounts", "insurancediscountid", "INDI", "Descuentos Prod/Seguro");
		insuranceId = setField("insuranceid", "", "Prod. Seguro", 8, Types.INTEGER, false, BmFieldType.ID, false);
		discountId = setField("discountid", "", "Cobertura", 8, Types.INTEGER, false, BmFieldType.ID, false);
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

	public BmField getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(BmField insuranceId) {
		this.insuranceId = insuranceId;
	}

	public BmoInsurance getBmoInsurance() {
		return bmoInsurance;
	}

	public void setBmoInsurance(BmoInsurance bmoInsurance) {
		this.bmoInsurance = bmoInsurance;
	}

	public BmoDiscount getBmoDiscount() {
		return bmoDiscount;
	}

	public void setBmoDiscount(BmoDiscount bmoDiscount) {
		this.bmoDiscount = bmoDiscount;
	}

	public BmField getDiscountId() {
		return discountId;
	}

	public void setDiscountId(BmField discountId) {
		this.discountId = discountId;
	}
}
