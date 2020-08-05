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

public class BmoEstimationItem extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField contractEstimationId, contractConceptItemId, quantity, consecutive, quantityLast, quantityReceipt, quantityTotal, price, subTotal;

	private BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
	private BmoContractConceptItem bmoContractConceptItem = new BmoContractConceptItem();


	public BmoEstimationItem(){
		super("com.flexwm.server.co.PmEstimationItem", "estimationitems", "estimationitemid", "ESTI", "Estimaciones del Contrato de Obra");

		//Campo de Datos		
		contractEstimationId = setField("contractestimationid", "", "Estimación de Contrato", 8, Types.INTEGER, false, BmFieldType.ID, false);
		contractConceptItemId = setField("contractconceptitemid", "", "Concepto de Contrato", 8, Types.INTEGER, false, BmFieldType.ID, false);
		quantityLast = setField("quantitylast", "", "Recibida", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantityReceipt = setField("quantityreceipt", "", "Acumulado", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quantityTotal = setField("quantitytotal", "", "Total", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		quantity = setField("quantity", "", "Avance", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		subTotal = setField("subtotal", "", "Importe", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		consecutive = setField("consecutive", "", "Orden", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				/*bmoContractConcept.getBmoConcept().getBmoUnitPrice().getCode(),
				bmoContractConcept.getBmoConcept().getBmoConceptHeading().getBmoConceptGroup().getCode(),
				bmoContractConcept.getBmoConcept().getBmoConceptHeading().getCode(),
				bmoContractConcept.getBmoConcept().getDescription(),
				bmoContractConcept.getBmoConcept().getBmoUnitPrice().getBmoUnit().getCode(),
				bmoContractConcept.getBmoConcept().getQuantity(), //paquete
				bmoContractConcept.getBmoConcept().getQuantity(), // cantidad. - faltaria multiplicar por woco_quantity*/
				getQuantity()//Cncpto
				//Ant - falta una funcion para este campo
				//getAmount(), // falta sumarle lo de la funcion anterior
				//bmoContractConcept.getBmoConcept().getBmoUnitPrice().getPrice(), //Le falta un if, y multiplicar por indirects de works
				//bmoContractConcept.getBmoConcept().getBmoUnitPrice().getPrice() ///Le falta un if, y multiplicar por amount(estimations)  e indirects(works)
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getConsecutive())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the contractEstimationId
	 */
	public BmField getContractEstimationId() {
		return contractEstimationId;
	}

	/**
	 * @param contractEstimationId the contractEstimationId to set
	 */
	public void setContractEstimationId(BmField contractEstimationId) {
		this.contractEstimationId = contractEstimationId;
	}


	/**
	 * @return the contractConceptItemId
	 */
	public BmField getContractConceptItemId() {
		return contractConceptItemId;
	}

	/**
	 * @param contractConceptItemId the contractConceptItemId to set
	 */
	public void setContractConceptItemId(BmField contractConceptItemId) {
		this.contractConceptItemId = contractConceptItemId;
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
	 * @return the consecutive
	 */
	public BmField getConsecutive() {
		return consecutive;
	}

	/**
	 * @param consecutive the consecutive to set
	 */
	public void setConsecutive(BmField consecutive) {
		this.consecutive = consecutive;
	}

	/**
	 * @return the bmoContractEstimation
	 */
	public BmoContractEstimation getBmoContractEstimation() {
		return bmoContractEstimation;
	}

	/**
	 * @param bmoContractEstimation the bmoContractEstimation to set
	 */
	public void setBmoContractEstimation(BmoContractEstimation bmoContractEstimation) {
		this.bmoContractEstimation = bmoContractEstimation;
	}

	/**
	 * @return the bmoContractConceptItem
	 */
	public BmoContractConceptItem getBmoContractConceptItem() {
		return bmoContractConceptItem;
	}

	/**
	 * @param bmoContractConceptItem the bmoContractConceptItem to set
	 */
	public void setBmoContractConceptItem(
			BmoContractConceptItem bmoContractConceptItem) {
		this.bmoContractConceptItem = bmoContractConceptItem;
	}

	/**
	 * @return the quantityReceipt
	 */
	public BmField getQuantityReceipt() {
		return quantityReceipt;
	}

	/**
	 * @param quantityReceipt the quantityReceipt to set
	 */
	public void setQuantityReceipt(BmField quantityReceipt) {
		this.quantityReceipt = quantityReceipt;
	}

	/**
	 * @return the quantityTotal
	 */
	public BmField getQuantityTotal() {
		return quantityTotal;
	}

	/**
	 * @param quantityTotal the quantityTotal to set
	 */
	public void setQuantityTotal(BmField quantityTotal) {
		this.quantityTotal = quantityTotal;
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
	 * @return the quantityLast
	 */
	public BmField getQuantityLast() {
		return quantityLast;
	}

	/**
	 * @param quantityLast the quantityLast to set
	 */
	public void setQuantityLast(BmField quantityLast) {
		this.quantityLast = quantityLast;
	}



}
