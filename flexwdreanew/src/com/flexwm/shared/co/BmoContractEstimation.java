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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author smuniz
 *
 */

public class BmoContractEstimation extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;

	BmoWorkContract bmoWorkContract;


	private BmField workContractId, consecutive, code, startDate, endDate, amount, tax, downPayment, warrantyFund, total,
	status, othersExpenses, paymentStatus, descriptionOthersExpenses, totalPercentage, subTotal;

	public static final char STATUS_PENDING = 'P';
	public static final char STATUS_AUTHORIZED = 'A';

	public static final char PAYMENTSTATUS_PENDING = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T'; 

	public static final String ACCESS_CHANGESTATUS = "COESCHS";
	public static String CODE_PREFIX = "COES-";

	public BmoContractEstimation(){
		super("com.flexwm.server.co.PmContractEstimation", "contractestimations", "contractestimationid", "COES", "Estimaciones");

		//Campo de Datos	
		workContractId = setField("workcontractid", "", "Contrato de la Obra", 8, Types.INTEGER, false, BmFieldType.ID, false);
		code = setField("code", "", "Clave Est. de Contrato", 10, 0, true, BmFieldType.CODE, true);
		consecutive = setField("consecutive", "", "Consecutivo", 11, Types.INTEGER, false, BmFieldType.NUMBER, false);
		startDate = setField("startdate", "", "Fecha de Inicio", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fecha de Termino", 20, Types.DATE, false, BmFieldType.DATE, false);
		amount = setField("amount", "", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		subTotal = setField("subtotal", "", "SubTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		downPayment = setField("downpayment", "", "Amort. Ant.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		warrantyFund = setField("warrantyfund", "", "Fondo de Garatía", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		totalPercentage = setField("totalpercentage", "", "% Acum", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);
		total = setField("total", "", "Neto Pagar", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		status = setField("status", "" + STATUS_PENDING, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_PENDING, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png")				
				)));
		othersExpenses = setField("othersexpenses", "", "Otros Cargos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),				
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pagada", "./icons/paymentstatus_total.png")			
				)));
		descriptionOthersExpenses = setField("descriptionotherexpenses", "", "Desc. otros cargos", 250, Types.VARCHAR, true, BmFieldType.STRING, false);

		bmoWorkContract = new BmoWorkContract();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(				
				getConsecutive(),
				getStartDate(),
				getEndDate(),
				getAmount(),
				getTax(),
				getSubTotal(),				
				getDownPayment(),
				getWarrantyFund(),
				getOthersExpenses(),
				getTotal(),				
				getStatus(),
				getPaymentStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getConsecutive()
						)));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(getConsecutive(), bmoWorkContract.getName(), getTotal()));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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
	 * @return the startDate
	 */
	public BmField getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public BmField getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
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
	 * @return the tax
	 */
	public BmField getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BmField tax) {
		this.tax = tax;
	}

	/**
	 * @return the downPayment
	 */
	public BmField getDownPayment() {
		return downPayment;
	}

	/**
	 * @param downPayment the downPayment to set
	 */
	public void setDownPayment(BmField downPayment) {
		this.downPayment = downPayment;
	}

	/**
	 * @return the warrantyFund
	 */
	public BmField getWarrantyFund() {
		return warrantyFund;
	}

	/**
	 * @param warrantyFund the warrantyFund to set
	 */
	public void setWarrantyFund(BmField warrantyFund) {
		this.warrantyFund = warrantyFund;
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
	 * @return the status
	 */
	public BmField getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BmField status) {
		this.status = status;
	}

	/**
	 * @return the othersExpenses
	 */
	public BmField getOthersExpenses() {
		return othersExpenses;
	}

	/**
	 * @param othersExpenses the othersExpenses to set
	 */
	public void setOthersExpenses(BmField othersExpenses) {
		this.othersExpenses = othersExpenses;
	}



	/**
	 * @return the paymentStatus
	 */
	public BmField getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(BmField paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * @return the descriptionOthersExpenses
	 */
	public BmField getDescriptionOthersExpenses() {
		return descriptionOthersExpenses;
	}

	/**
	 * @param descriptionOthersExpenses the descriptionOthersExpenses to set
	 */
	public void setDescriptionOthersExpenses(BmField descriptionOthersExpenses) {
		this.descriptionOthersExpenses = descriptionOthersExpenses;
	}

	/**
	 * @return the totalPercentage
	 */
	public BmField getTotalPercentage() {
		return totalPercentage;
	}

	/**
	 * @param totalPercentage the totalPercentage to set
	 */
	public void setTotalPercentage(BmField totalPercentage) {
		this.totalPercentage = totalPercentage;
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


}
