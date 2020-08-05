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

public class BmoContractConceptGroup extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField workContractId, conceptGroupId;

	private BmoWorkContract bmoWorkContract = new BmoWorkContract();
	private BmoConceptGroup bmoConceptGroup = new BmoConceptGroup();

	public BmoContractConceptGroup(){
		super("com.flexwm.server.co.PmContractConceptGroup", "contractconceptgroups", "contractconceptgroupid", "COCG", "Grupo de Conceptos de Contrato");

		//Campo de Datos		
		workContractId = setField("workcontractid", "", "Contrato", 8, Types.INTEGER, false, BmFieldType.ID, false);
		conceptGroupId = setField("conceptgroupid", "", "Concepto", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoConceptGroup.getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField( bmoConceptGroup.getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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

	/**
	 * @return the conceptGroupId
	 */
	public BmField getConceptGroupId() {
		return conceptGroupId;
	}

	/**
	 * @param conceptGroupId the conceptGroupId to set
	 */
	public void setConceptGroupId(BmField conceptGroupId) {
		this.conceptGroupId = conceptGroupId;
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
	 * @return the bmoConceptGroup
	 */
	public BmoConceptGroup getBmoConceptGroup() {
		return bmoConceptGroup;
	}

	/**
	 * @param bmoConceptGroup the bmoConceptGroup to set
	 */
	public void setBmoConceptGroup(BmoConceptGroup bmoConceptGroup) {
		this.bmoConceptGroup = bmoConceptGroup;
	}

}
