/**
 * 
 */

package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;


/**
 * @author jhernandez
 *
 */

public class BmoWorkContractProperty extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmoProperty bmoProperty = new BmoProperty();
	private BmoWorkContract bmoWorkContract = new BmoWorkContract();
	private BmField propertyId, workContractId;

	public BmoWorkContractProperty() {
		super("com.flexwm.server.co.PmWorkContractProperty", "workcontractproperties", "workcontractpropertyid", "WCPR", "Viviendas del Contrato");
		propertyId = setField("propertyid", "", "Vivienda", 4, Types.INTEGER, false, BmFieldType.ID, false);
		workContractId = setField("workcontractid", "", "Contrato", 4, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProperty().getCode()
			//	getBmoWorkContract().getCode(),
			//	getBmoWorkContract().getName()								
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProperty().getCode() 
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	/**
	 * @return the bmoProperty
	 */
	public BmoProperty getBmoProperty() {
		return bmoProperty;
	}

	/**
	 * @param bmoProperty the bmoProperty to set
	 */
	public void setBmoProperty(BmoProperty bmoProperty) {
		this.bmoProperty = bmoProperty;
	}

	/**
	 * @return the bmoWorkContract
	 */
	public BmoWorkContract getBmoWorkContract() {
		return bmoWorkContract;
	}

	/**
	 * @param bmoWorkContract the bmoWorkContract to set
	 */
	public void setBmoWorkContract(BmoWorkContract bmoWorkContract) {
		this.bmoWorkContract = bmoWorkContract;
	}

	/**
	 * @return the propertyId
	 */
	public BmField getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(BmField propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * @return the workContractId
	 */
	public BmField getWorkContractId() {
		return workContractId;
	}

	/**
	 * @param workContractId the workContractId to set
	 */
	public void setWorkContractId(BmField workContractId) {
		this.workContractId = workContractId;
	}

}
