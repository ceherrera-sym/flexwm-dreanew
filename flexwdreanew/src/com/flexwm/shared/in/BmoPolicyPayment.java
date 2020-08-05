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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;

public class BmoPolicyPayment extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField status, amount, dueDate, payDate, comments, policyId;
	private BmoPolicy bmoPolicy = new BmoPolicy();
	
	public static char STATUS_PENDING = 'N';
	public static char STATUS_PAID = 'P';
	public static char STATUS_LATE = 'L';
	
	public BmoPolicyPayment() {
		super("com.flexwm.server.in.PmPolicyPayment", "policypayments", "policypaymentid", "POPA", "Pagos de Pólizas");
		
		status = setField("status", "", "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_PENDING, "Pendiente", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_PAID, "Pagado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_LATE, "Vencido", "./icons/popa_status_late.png")
				)));
		
		amount = setField("amount", "", "Suma", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		dueDate = setField("duedate", "", "Fecha Prog.", 20, Types.DATE, false, BmFieldType.DATE, false);
		payDate = setField("paydate", "", "Fecha Pago", 20, Types.DATE, true, BmFieldType.DATE, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		policyId = setField("policyid", "", "Póliza", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoPolicy.getNumber(),
				bmoPolicy.getBmoCustomer().getCode(),
				getDueDate(),
				getPayDate(),
				getComments(),
				getStatus(),
				getAmount()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDueDate(), BmOrder.ASC)));
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getPolicyId() {
		return policyId;
	}

	public void setPolicyId(BmField policyId) {
		this.policyId = policyId;
	}

	public BmField getDueDate() {
		return dueDate;
	}

	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}

	public BmField getPayDate() {
		return payDate;
	}

	public void setPayDate(BmField payDate) {
		this.payDate = payDate;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmoPolicy getBmoPolicy() {
		return bmoPolicy;
	}

	public void setBmoPolicy(BmoPolicy bmoPolicy) {
		this.bmoPolicy = bmoPolicy;
	}
}
