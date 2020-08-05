/**
 * 
 */
package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoBankMovement extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, bankMovTypeId, code, description, inputDate, dueDate, withdraw, deposit, supplierId, customerId,
	parentId, autorizedId, logAuthorizired, bankReference, bankAccountId, bankAccoTransId,
	activeStatus, bkmvCancelId, budgetItemId, loanId, noCheck, currencyParity, amountConverted, reconciledUserId, reconciledDate,
	authorizeUserId, authorizeDate, cancelledUserId, cancelledDate,paymentTypeId,comments,commentLog, file;
	
	private BmoBankMovType bmoBankMovType = new BmoBankMovType();
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoBankAccount bmoBankAccount = new BmoBankAccount();

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static final char STATUS_RECONCILED = 'C';
	public static final char STATUS_CANCELLED = 'N';

	public static final String ACCESS_CHANGESTATUS = "BMVCHS";
	public static final String ACCESS_DISPOSAL = "BKMVDISP";
	public static final String ACCESS_CHANGEINFORMATION = "BKMVINFO"; // Habilita f registro-pago-desc.-referencia
	public static final String ACCESS_CHANGEINFOAUTHO = "BKAUTINFO";// Habilita f.pago-NoCheque
	public static final String ACCESS_CHANGEAUTHORIZED = "BKMVAUTO";
	public static final String ACCESS_CHANGERECONCILED = "BKMVRECO";
	public static final String ACCESS_CHANGECANCELLED  = "BKMVCANC";
	public static final String ACCESS_ADDMBCALLCOMPANY  = "BKMVAMBCAC"; // Quitar filtro de empresa de las O.C./CxP/CxC, con este permiso puedes pagar/cobrar de otra empresa

	public static String ACTION_BALANCEREQI = "BALANCEREQI"; 	
	public static String ACTION_CHECKNO = "CHECKNO";
	public static String ACTION_BALACEBANKACCOUNT = "BLANBKAC";
	public static String ACTION_GETCUREBYDESTYNY = "ACTCURDEST";
	public static String ACTION_BALANCEREQIADVANCE = "BALREQADV";
	public static String ACTION_SHOWBANKMOVEMENTCP = "SHOWBKMVCP";
	public static String ACTION_SHOWBANKMOVEMENTCC = "SHOWBKMVCC";
	public static String ACTION_SHOWBANKMOVEMENT = "SHOWBKMV";

	public static String CODE_PREFIX = "MB-";


	public BmoBankMovement() {
		super("com.flexwm.server.fi.PmBankMovement", "bankmovements", "bankmovementid", "BKMV", "Mov. Banco");

		//Campo de Datos
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_RECONCILED, "Conciliado", "./icons/status_reconciled.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelado", "./icons/status_cancelled.png")
				)));

		bankMovTypeId = setField("bankmovtypeid", "", "Tipo Movimiento", 8, Types.INTEGER, false, BmFieldType.ID, false);
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);		
		description = setField("description", "", "Descripción",500, Types.VARCHAR, true, BmFieldType.STRING, false);		
		inputDate = setField("inputdate", "", "F. Registro", 12, Types.DATE, true, BmFieldType.DATE, false);
		dueDate = setField("duedate", "", "Pago", 12, Types.DATE, false, BmFieldType.DATE, false);
		withdraw = setField("withdraw","", "Cargo", 20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		deposit = setField("deposit","", "Abono", 20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		customerId = setField("customerid", "", "Clientes", 8, Types.INTEGER, true, BmFieldType.ID, false);
		parentId = setField("parentid", "", "Padre", 8, Types.INTEGER, true, BmFieldType.ID, false);
		autorizedId = setField("authorizedid", "", "Autorizado", 8, Types.INTEGER, true, BmFieldType.ID, false);
		logAuthorizired = setField("logAuthorized", "", "Bitacora",512, 0, true, BmFieldType.STRING, false);
		bankReference = setField("bankreference","", "Referencia", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		bankAccountId = setField("bankaccountid", "", "Cuenta de Banco", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bankAccoTransId = setField("bankaccotransid", "", "Cta.Banco Destino", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//requisitionId = setField("requisitionId", "", "Orden de Compra", 8, Types.INTEGER, true, BmFieldType.ID, false);
		bkmvCancelId = setField("bkmvcancelid", "", "MB Cancelado", 8, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		loanId = setField("loanid", "", "Crédito", 8, Types.INTEGER, true, BmFieldType.ID, false);
		noCheck = setField("nocheck","", "No.Cheque", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amountConverted = setField("amountconverted", "", "Aplicar", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		reconciledUserId = setField("reconcileduserid", "", "Conciliado por", 8, Types.INTEGER, true, BmFieldType.ID, false);
		reconciledDate = setField("reconcileddate", "", "Fecha Conciliado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		authorizeUserId = setField("authorizeuserid", "", "Autorizado por", 8, Types.INTEGER, true, BmFieldType.ID, false);
		authorizeDate = setField("authorizedate", "", "Fecha Autorizado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		cancelledUserId = setField("cancelledUserid", "", "Cancelado por", 8, Types.INTEGER, true, BmFieldType.ID, false);
		cancelledDate = setField("cancelleddate", "", "Fecha Cancelado", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		paymentTypeId = setField("paymenttypeid", "", "Método de Pago", 11, Types.INTEGER, true, BmFieldType.ID, false);
		comments = setField("comments", "", "Comentarios", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
		commentLog = setField("commentlog", "", "Bitácora Mov.Bancos", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
		file = setField("file", "0", "Archivos", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDescription(),
				getNoCheck(),
				getBmoBankAccount().getName(),				
				getBmoSupplier().getName(),				
				getBmoCustomer().getCode(),	
				getDueDate(),
				getFile(),
				getBmoBankMovType().getCategory(),
				getStatus(),
				getBmoBankMovType().getType(),
				getWithdraw(),
				getDeposit()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getBmoBankMovType().getType(),
				getWithdraw(),
				getDeposit()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getWithdraw()),				
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getBmoSupplier().getName()),
				new BmSearchField(getNoCheck()),
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) {
			return CODE_PREFIX + getId();
		}
		return "";
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
	 * @return the bankMovTypeId
	 */
	public BmField getBankMovTypeId() {
		return bankMovTypeId;
	}

	/**
	 * @param bankMovTypeId the bankMovTypeId to set
	 */
	public void setBankMovTypeId(BmField bankMovTypeId) {
		this.bankMovTypeId = bankMovTypeId;
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
	 * @return the inputDate
	 */
	public BmField getInputDate() {
		return inputDate;
	}

	/**
	 * @param inputDate the inputDate to set
	 */
	public void setInputDate(BmField inputDate) {
		this.inputDate = inputDate;
	}

	/**
	 * @return the dueDate
	 */
	public BmField getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}


	/**
	 * @return the supplierId
	 */
	public BmField getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}


	/**
	 * @return the customerId
	 */
	public BmField getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the parentId
	 */
	public BmField getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(BmField parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the autorizedId
	 */
	public BmField getAutorizedId() {
		return autorizedId;
	}

	/**
	 * @param autorizedId the autorizedId to set
	 */
	public void setAutorizedId(BmField autorizedId) {
		this.autorizedId = autorizedId;
	}

	/**
	 * @return the logAuthorizired
	 */
	public BmField getLogAuthorizired() {
		return logAuthorizired;
	}

	/**
	 * @param logAuthorizired the logAuthorizired to set
	 */
	public void setLogAuthorizired(BmField logAuthorizired) {
		this.logAuthorizired = logAuthorizired;
	}

	/**
	 * @return the bankReference
	 */
	public BmField getBankReference() {
		return bankReference;
	}

	/**
	 * @param bankReference the bankReference to set
	 */
	public void setBankReference(BmField bankReference) {
		this.bankReference = bankReference;
	}

	/**
	 * @return the bmoBankMovType
	 */
	public BmoBankMovType getBmoBankMovType() {
		return bmoBankMovType;
	}

	/**
	 * @param bmoBankMovType the bmoBankMovType to set
	 */
	public void setBmoBankMovType(BmoBankMovType bmoBankMovType) {
		this.bmoBankMovType = bmoBankMovType;
	}

	/**
	 * @return the bmoSupplier
	 */
	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	/**
	 * @param bmoSupplier the bmoSupplier to set
	 */
	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	/**
	 * @return the bmoCustomer
	 */
	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	/**
	 * @param bmoCustomer the bmoCustomer to set
	 */
	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	/**
	 * @return the bankAccountId
	 */
	public BmField getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * @param bankAccountId the bankAccountId to set
	 */
	public void setBankAccountId(BmField bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * @return the bmoBankAccount
	 */
	public BmoBankAccount getBmoBankAccount() {
		return bmoBankAccount;
	}

	/**
	 * @param bmoBankAccount the bmoBankAccount to set
	 */
	public void setBmoBankAccount(BmoBankAccount bmoBankAccount) {
		this.bmoBankAccount = bmoBankAccount;
	}

	/**
	 * @return the withdraw
	 */
	public BmField getWithdraw() {
		return withdraw;
	}

	/**
	 * @param withdraw the withdraw to set
	 */
	public void setWithdraw(BmField withdraw) {
		this.withdraw = withdraw;
	}

	/**
	 * @return the deposit
	 */
	public BmField getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(BmField deposit) {
		this.deposit = deposit;
	}



	/**
	 * @return the bankAccoTransId
	 */
	public BmField getBankAccoTransId() {
		return bankAccoTransId;
	}

	/**
	 * @param bankAccoTransId the bankAccoTransId to set
	 */
	public void setBankAccoTransId(BmField bankAccoTransId) {
		this.bankAccoTransId = bankAccoTransId;
	}

	/**
	 * @return the requisitionId
	 */
//	public BmField getRequisitionIdd() {
//		return requisitionId;
//	}

	/**
	 * @param requisitionId the requisitionId to set
	 */
//	public void setRequisitionIdd(BmField requisitionId) {
//		this.requisitionId = requisitionId;
//	}

	/**
	 * @return the activeStatus
	 */
	public BmField getActiveStatus() {
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(BmField activeStatus) {
		this.activeStatus = activeStatus;
	}

	public BmField getBkmvCancelId() {
		return bkmvCancelId;
	}

	public void setBkmvCancelId(BmField bkmvCancelId) {
		this.bkmvCancelId = bkmvCancelId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getLoanId() {
		return loanId;
	}

	public void setLoanId(BmField loanId) {
		this.loanId = loanId;
	}

	public BmField getNoCheck() {
		return noCheck;
	}

	public void setNoCheck(BmField noCheck) {
		this.noCheck = noCheck;
	}	


	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmField getAmountConverted() {
		return amountConverted;
	}

	public void setAmountConverted(BmField amountConverted) {
		this.amountConverted = amountConverted;
	}	

	/**
	 * @return the reconciledUserId
	 */
	public BmField getReconciledUserId() {
		return reconciledUserId;
	}

	/**
	 * @param reconciledUserId the reconciledUserId to set
	 */
	public void setReconciledUserId(BmField reconciledUserId) {
		this.reconciledUserId = reconciledUserId;
	}

	/**
	 * @return the reconciledDate
	 */
	public BmField getReconciledDate() {
		return reconciledDate;
	}

	/**
	 * @param reconciledDate the reconciledDate to set
	 */
	public void setReconciledDate(BmField reconciledDate) {
		this.reconciledDate = reconciledDate;
	}

	/**
	 * @return the authorizeUserId
	 */
	public BmField getAuthorizeUserId() {
		return authorizeUserId;
	}

	/**
	 * @param authorizeUserId the authorizeUserId to set
	 */
	public void setAuthorizeUserId(BmField authorizeUserId) {
		this.authorizeUserId = authorizeUserId;
	}

	/**
	 * @return the authorizeDate
	 */
	public BmField getAuthorizeDate() {
		return authorizeDate;
	}

	/**
	 * @param authorizeDate the authorizeDate to set
	 */
	public void setAuthorizeDate(BmField authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	/**
	 * @return the cancelledUserId
	 */
	public BmField getCancelledUserId() {
		return cancelledUserId;
	}

	/**
	 * @param cancelledUserId the cancelledUserId to set
	 */
	public void setCancelledUserId(BmField cancelledUserId) {
		this.cancelledUserId = cancelledUserId;
	}

	/**
	 * @return the cancelledDate
	 */
	public BmField getCancelledDate() {
		return cancelledDate;
	}

	/**
	 * @param cancelledDate the cancelledDate to set
	 */
	public void setCancelledDate(BmField cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public BmField getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(BmField paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getCommentLog() {
		return commentLog;
	}

	public void setCommentLog(BmField commentLog) {
		this.commentLog = commentLog;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

}
