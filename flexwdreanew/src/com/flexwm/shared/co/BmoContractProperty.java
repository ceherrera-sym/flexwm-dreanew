/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
/**
 * @author smuniz
 *
 */
public class BmoContractProperty extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private BmField workContractid, propertyId;
	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoProperty bmoProperty = new BmoProperty();
	
	public BmoContractProperty(){
		super("com.flexwm.server.co.PmContractProperty", "contractproperties", "contractpropertyid", "WCPR", "Viviendas del Contrato");
		
		//Campo de Datos	
		workContractid = setField("workcontractid", "", "Contrato", 8, Types.INTEGER, false, BmFieldType.ID, false); 
		propertyId = setField("propertyid", "", "Vivienda", 8, Types.INTEGER, false, BmFieldType.ID, false); 

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoWorkContract.getCode(),
				bmoProperty.getCode()				
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(bmoWorkContract.getCode()), 
				new BmSearchField(bmoProperty.getCode())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the workContractid
	 */
	public BmField getWorkContractid() {
		return workContractid;
	}

	/**
	 * @param workContractid the workContractid to set
	 */
	public void setWorkContractid(BmField workContractid) {
		this.workContractid = workContractid;
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
	
	
}
