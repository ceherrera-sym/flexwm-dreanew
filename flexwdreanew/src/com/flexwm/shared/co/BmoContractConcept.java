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
public class BmoContractConcept extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;


	private BmField workContractId, conceptId;
	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoConcept bmoConcept = new BmoConcept();
	
	public BmoContractConcept(){
		super("com.flexwm.server.co.PmContractConcept", "contractconcepts", "contractconceptid", "COCO", "Conceptos");
		
		//Campo de Datos	
		workContractId = setField("workcontractid", "", "Contrato", 8, Types.INTEGER, false, BmFieldType.ID, false); 
		conceptId = setField("conceptid", "", "Concepto-", 8, Types.INTEGER, false, BmFieldType.ID, false); 

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getWorkContractid()
				/*bmoConcept.getBmoUnitPrice().getCode(),
				bmoConcept.getBmoUnitPrice().getName(),
				bmoConcept.getBmoUnitPrice().getPrice(), //falta multiplicarlos por otros campos
				bmoConcept.getQuantity()*/
				//getTotal()- falta multiplicaciones para sacar el total
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(bmoWorkContract.getCode())
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
		return workContractId;
	}

	/**
	 * @param workContractid the workContractid to set
	 */
	public void setWorkContractid(BmField workContractid) {
		this.workContractId = workContractid;
	}

	/**
	 * @return the conceptId
	 */
	public BmField getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId the conceptId to set
	 */
	public void setConceptId(BmField conceptId) {
		this.conceptId = conceptId;
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
	public BmoConcept getBmoConcept() {
		return bmoConcept;
	}

	/**
	 * @param bmoProperty the bmoProperty to set
	 */
	public void setBmoConcept(BmoConcept bmoConcept) {
		this.bmoConcept = bmoConcept;
	}


	
	
}
