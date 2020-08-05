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

public class BmoInsuranceValuable extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoInsurance bmoInsurance = new BmoInsurance();
	private BmoValuable bmoValuable = new BmoValuable();
	private BmField insuranceId, valuableId;

	public BmoInsuranceValuable() {
		super("com.flexwm.server.in.PmInsuranceValuable", "insurancevaluables", "insurancevaluableid", "INVA", "Valores Prod/Seguro");
		insuranceId = setField("insuranceid", "", "Prod. Seguro", 8, Types.INTEGER, false, BmFieldType.ID, false);
		valuableId = setField("valuableid", "", "Cobertura", 8, Types.INTEGER, false, BmFieldType.ID, false);
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

	public BmoValuable getBmoValuable() {
		return bmoValuable;
	}

	public void setBmoValuable(BmoValuable bmoValuable) {
		this.bmoValuable = bmoValuable;
	}

	public BmField getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(BmField insuranceId) {
		this.insuranceId = insuranceId;
	}

	public BmField getValuableId() {
		return valuableId;
	}

	public void setValuableId(BmField valuableId) {
		this.valuableId = valuableId;
	}
}
