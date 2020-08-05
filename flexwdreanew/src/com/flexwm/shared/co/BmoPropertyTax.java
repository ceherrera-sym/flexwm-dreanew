package com.flexwm.shared.co;

import java.io.Serializable;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.BmFieldType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

public class BmoPropertyTax extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField accountNo, propertyId,description,status;

	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T';
	


	public BmoPropertyTax() {
		super("com.flexwm.server.co.PmPropertyTax", "propertytax", "propertytaxid", "PRTX", " # Predial");
		
		// Campo de datos
		accountNo = setField("accountno", "", "No.Cuenta", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);		
		propertyId = setField("propertyid", "", "Inmueble", 8, Types.INTEGER, false, BmFieldType.ID, false);	
		status = setField("status", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Total", "./icons/paymentstatus_total.png")
				)));
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getAccountNo(), 
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getAccountNo()),
				new BmSearchField(getDescription())
				));
	
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}
	
	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(BmField accountNo) {
		this.accountNo = accountNo;
	}
	
	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}
}
