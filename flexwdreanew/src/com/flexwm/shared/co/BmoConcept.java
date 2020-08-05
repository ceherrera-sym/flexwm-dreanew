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
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author jhernandez
 *
 */

public class BmoConcept extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;

	private BmField workId, code, name, description, quantity, subTotal,  total, typeCostCenterId;
	BmoWork bmoWork = new BmoWork();
	BmoBudgetItemType bmoTypeCostCenter = new BmoBudgetItemType();

	public static String CODE_PREFIX = "CNCP-";

	public BmoConcept(){
		super("com.flexwm.server.co.PmConcept", "concepts", "conceptid", "CNCP", "Conceptos");

		//Campo de Datos
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		workId = setField("workid", "", "Obra", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		subTotal = setField("subtotal", "", "SubTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		typeCostCenterId = setField("typecostcenterid", "", "CC Tipo", 20, Types.INTEGER, false, BmFieldType.ID, false);
		description = setField("description", "", "Descripción", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getDescription(),
				getBmoTypeCostCenter().getName(),
				getQuantity(),
				getTotal()				
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()),
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}


	/**
	 * @return the workId
	 */
	public BmField getWorkId() {
		return workId;
	}

	/**
	 * @param workId the workId to set
	 */
	public void setWorkId(BmField workId) {
		this.workId = workId;
	}



	/**

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the bmowork
	 */
	public BmoWork getBmoWork() {
		return bmoWork;
	}

	/**
	 * @param bmowork the bmowork to set
	 */
	public void setBmoWork(BmoWork bmowork) {
		this.bmoWork = bmowork;
	}

	/**
	 * @return the total
	 */
	public BmField getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BmField total) {
		this.total = total;
	}

	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
	}



	/**
	 * @return the quantity
	 */
	public BmField getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the subTotal
	 */
	public BmField getSubTotal() {
		return subTotal;
	}

	/**
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(BmField subTotal) {
		this.subTotal = subTotal;
	}

	/**
	 * @return the typeCostCenterId
	 */
	public BmField getTypeCostCenterId() {
		return typeCostCenterId;
	}

	/**
	 * @param typeCostCenterId the typeCostCenterId to set
	 */
	public void setTypeCostCenterId(BmField typeCostCenterId) {
		this.typeCostCenterId = typeCostCenterId;
	}

	/**
	 * @return the bmoTypeCostCenter
	 */
	public BmoBudgetItemType getBmoTypeCostCenter() {
		return bmoTypeCostCenter;
	}

	/**
	 * @param bmoTypeCostCenter the bmoTypeCostCenter to set
	 */
	public void setBmoTypeCostCenter(BmoBudgetItemType bmoTypeCostCenter) {
		this.bmoTypeCostCenter = bmoTypeCostCenter;
	}



}
