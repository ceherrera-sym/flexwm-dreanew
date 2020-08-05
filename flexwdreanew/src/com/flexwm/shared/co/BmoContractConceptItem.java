/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
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


public class BmoContractConceptItem extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, quantity, amount, price, workContractId, name, workItemId,description;

	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoWorkItem bmoWorkItem = new BmoWorkItem();

	public BmoContractConceptItem() {
		super("com.flexwm.server.co.PmContractConceptItem", "contractconceptitems", "contractconceptitemid", "CCIT", "Item de Contractos");

		// Campo de datos		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amount = setField("amount", "", "SubTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		workItemId = setField("workitemid", "", "Contrato", 20, Types.INTEGER, false, BmFieldType.ID, false);
		workContractId = setField("workcontractid", "", "Contrato", 20, Types.INTEGER, false, BmFieldType.ID, false);				
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(), 
				getCode()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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
	 * @return the unitPriceId
	 */


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
	 * @return the amount
	 */
	public BmField getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	/**
	 * @return the price
	 */
	public BmField getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(BmField price) {
		this.price = price;
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
	 * @return the workItemId
	 */
	public BmField getWorkItemId() {
		return workItemId;
	}

	/**
	 * @param workItemId the workItemId to set
	 */
	public void setWorkItemId(BmField workItemId) {
		this.workItemId = workItemId;
	}

	/**
	 * @return the bmoWorkItem
	 */
	public BmoWorkItem getBmoWorkItem() {
		return bmoWorkItem;
	}

	/**
	 * @param bmoWorkItem the bmoWorkItem to set
	 */
	public void setBmoWorkItem(BmoWorkItem bmoWorkItem) {
		this.bmoWorkItem = bmoWorkItem;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}
	



}
