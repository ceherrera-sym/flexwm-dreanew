/**
 * 
 */

package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */


public class BmoPaccount extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, invoiceno, paccountTypeId, supplierId, status,
	receiveDate, dueDate, description, paymentDate, companyId, requisitionId, reqiCode, requisitionReceiptId,
	balance, paymentStatus, userId, payments, amount, budgetItemId, currencyId, currencyParity, taxApplies, tax, total,
	authorizedUser, authorizedDate, requisitionType, xmlFileUpload, areaId, file,discount,isXml,sumRetention;

	private BmoPaccountType bmoPaccountType = new BmoPaccountType();	
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmoCompany bmoCompany = new BmoCompany();
	private BmoUser bmoUser = new BmoUser();
	private BmoCurrency bmoCurrency = new BmoCurrency();

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
//	public static final char STATUS_CANCELLED = 'C';

	public static final char PAYMENTSTATUS_PENDING = 'P'; 
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public static final String ACCESS_CHANGESTATUS = "PACCHS";
	public static final String ACCESS_CHANGEROC = "PACCCHSROC";
	public static final String ACCESS_UPXML = "PACCUPXML"; // Poder cargar facturas xml

	public static String CODE_PREFIX = "CP-";

	public static final char ACTION_BALANCE = 'B';
	public static final char ACTION_PAYMENT = 'P';
	
	public static String ACTION_NEWPACCOUNT = "NEWPACCOUNT";
	public static String MORE_REQUISITIONRECEIPTS = "Y";

	public BmoPaccount() {
		super("com.flexwm.server.fi.PmPaccount","paccounts", "paccountid", "PACC","Cuentas x Pagar");

		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png")
				//new BmFieldOption(STATUS_CANCELLED, "Cancelada", "./icons/status_cancelled.png")
				)));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Estatus Pago", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "Pendiente", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", "./icons/paymentstatus_total.png")				
				)));
		invoiceno = setField("invoiceno", "", "No. Factura", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		receiveDate = setField("receivedate", "", "F. Registro", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		dueDate = setField("duedate", "", "Fecha Prog.", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);

		//Manejo de Iva
		taxApplies = setField("taxapplies", "", "Aplica IVA", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		tax = setField("tax", "", "IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		//Monto
		amount = setField("amount", "0", "Monto", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		payments = setField("payments", "0", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		balance = setField("balance", "0", "Saldo", 30, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		discount = setField("discount", "", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		paymentDate = setField("paymentdate", "", "Fecha Pago", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		paccountTypeId = setField("paccounttypeid", "", "Tipo", 11, Types.INTEGER, false, BmFieldType.ID, false);		
		supplierId = setField("supplierid", "", "Proveedor", 11, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionId = setField("requisitionid", "", "Orden de Compra",11, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionReceiptId = setField("requisitionreceiptid", "", "Recibo O.C", 11, Types.INTEGER, true, BmFieldType.ID, false);


		userId = setField("userid", "", "Vendedor", 11, Types.INTEGER, true, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 11, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 4, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 11, Types.INTEGER, false, BmFieldType.ID, false);
		currencyParity = setField("currencyparity", "", "Tipo de Cambio", 30, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		reqiCode = setField("reqicode", "", "Clave OC", 10, Types.VARCHAR, true, BmFieldType.CODE, false);

		authorizedUser = setField("authorizeduser", "", "Autorizado por", 20, Types.INTEGER, true, BmFieldType.ID, false);
		authorizedDate = setField("authorizeddate", "", "Fecha de Autorización", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		
		//Llenar el tipo de OC, para realizar busquedas
		requisitionType = setField("requisitiontype", "", "Tipo OC", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		xmlFileUpload = setField("xmlfileupload", "", "Archivo XML", 500, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		file = setField("file", "0", "Archivos", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		isXml = setField("isxml", "0", "Es XML?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		sumRetention = setField("sumretention", "", "Total Retenciones", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getInvoiceno(),
				getBmoSupplier().getCode(),
				getBmoSupplier().getName(),
				getBmoPaccountType().getName(),
				getReqiCode(),				
				getRequisitionReceiptId(),
				getDueDate(),
				getPaymentDate(),
				getStatus(),				
				getPaymentStatus(),
				getFile(),
				getBmoCurrency().getCode(),
				getCurrencyParity(),
				getBmoPaccountType().getCategory(),
				getTotal(),
				getPayments(),
				getBalance()				
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getReceiveDate(), 
				getInvoiceno(), 
				getBmoSupplier().getName(), 
				getTotal()
				));
	}

	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getInvoiceno(),
				getTotal(),
				getBalance()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getInvoiceno()),
				new BmSearchField(getCode()),
				new BmSearchField(getReqiCode()),
				new BmSearchField(getDescription()),
				new BmSearchField(getBmoSupplier().getName()),
				new BmSearchField(getRequisitionReceiptId())				
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}


	public BmField getPaccountTypeId() {
		return paccountTypeId;
	}

	public void setPaccountTypeId(BmField paccountTypeId) {
		this.paccountTypeId = paccountTypeId;
	}



	public BmoPaccountType getBmoPaccountType() {
		return bmoPaccountType;
	}

	public void setBmoPaccountType(BmoPaccountType bmoPaccountType) {
		this.bmoPaccountType = bmoPaccountType;
	}



	public BmField getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(BmField invoiceno) {
		this.invoiceno = invoiceno;
	}


	public BmField getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(BmField receiveDate) {
		this.receiveDate = receiveDate;
	}

	public BmField getDueDate() {
		return dueDate;
	}

	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(BmField paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the companyId
	 */
	public BmField getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the bmoCompany
	 */
	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	/**
	 * @param bmoCompany the bmoCompany to set
	 */
	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(BmField requisitionId) {
		this.requisitionId = requisitionId;
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
	 * @return the balance
	 */
	public BmField getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BmField balance) {
		this.balance = balance;
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
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	/**
	 * @return the bmoUser
	 */
	public BmoUser getBmoUser() {
		return bmoUser;
	}

	/**
	 * @param bmoUser the bmoUser to set
	 */
	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	/**
	 * @return the requisitionReceiptId
	 */
	public BmField getRequisitionReceiptId() {
		return requisitionReceiptId;
	}

	/**
	 * @param requisitionReceiptId the requisitionReceiptId to set
	 */
	public void setRequisitionReceiptId(BmField requisitionReceiptId) {
		this.requisitionReceiptId = requisitionReceiptId;
	}

	/**
	 * @return the payments
	 */
	public BmField getPayments() {
		return payments;
	}

	/**
	 * @param payments the payments to set
	 */
	public void setPayments(BmField payments) {
		this.payments = payments;
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

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getCurrencyParity() {
		return currencyParity;
	}

	public void setCurrencyParity(BmField currencyParity) {
		this.currencyParity = currencyParity;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getReqiCode() {
		return reqiCode;
	}

	public void setReqiCode(BmField reqiCode) {
		this.reqiCode = reqiCode;
	}

	public BmField getTaxApplies() {
		return taxApplies;
	}

	public void setTaxApplies(BmField taxApplies) {
		this.taxApplies = taxApplies;
	}

	public BmField getTax() {
		return tax;
	}

	public void setTax(BmField tax) {
		this.tax = tax;
	}

	public BmField getTotal() {
		return total;
	}

	public void setTotal(BmField total) {
		this.total = total;
	}

	public BmField getAuthorizedUser() {
		return authorizedUser;
	}

	public void setAuthorizedUser(BmField authorizedUser) {
		this.authorizedUser = authorizedUser;
	}

	public BmField getAuthorizedDate() {
		return authorizedDate;
	}

	public void setAuthorizedDate(BmField authorizedDate) {
		this.authorizedDate = authorizedDate;
	}

	public BmField getRequisitionType() {
		return requisitionType;
	}

	public void setRequisitionType(BmField requisitionType) {
		this.requisitionType = requisitionType;
	}	
	
	public BmField getXmlFileUpload() {
		return xmlFileUpload;
	}

	public void setXmlFileUpload(BmField xmlFileUpload) {
		this.xmlFileUpload = xmlFileUpload;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

	public BmField getDiscount() {
		return discount;
	}

	public void setDiscount(BmField discount) {
		this.discount = discount;
	}

	public BmField getIsXml() {
		return isXml;
	}

	public void setIsXml(BmField isXml) {
		this.isXml = isXml;
	}
	
	public BmField getSumRetention() {
		return sumRetention;
	}

	public void setSumRetention(BmField sumRetention) {
		this.sumRetention = sumRetention;
	}
	

}
