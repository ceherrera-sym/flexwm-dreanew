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
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoPolicy extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, code, number, startDate, endDate, payFrequency, payType, tcDigits, tcExpiryDate, premium, amount, status,
			userId, customerId, insuredId, insuranceId, wFlowId, wFlowTypeId;
	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoUser bmoUser = new BmoUser();
	private BmoInsurance bmoInsurance = new BmoInsurance();
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	
	public static char TYPE_NORMAL = 'N';
	public static char TYPE_PREFERRED = 'P';
	public static char TYPE_GROUP = 'G';

	public static char PAYFREQUENCY_YEAR = 'Y';
	public static char PAYFREQUENCY_SEMESTER = 'S';
	public static char PAYFREQUENCY_QUARTER = 'Q';
	public static char PAYFREQUENCY_MONTH = 'M';
	
	public static char PAYTYPE_CREDIT = 'C';
	public static char PAYTYPE_DEBIT = 'D';
	public static char PAYTYPE_TRANSFER = 'T';
	public static char PAYTYPE_AGENT = 'A';
	
	public static char STATUS_PENDING = 'N';
	public static char STATUS_PAID = 'P';
	public static char STATUS_LATE = 'L';
	
	public static String CODE_PREFIX = "P-";
	
	
	public BmoPolicy() {
		super("com.flexwm.server.in.PmPolicy", "policies", "policyid", "POLI", "Pólizas");
		code = setField("code", "", "Clave Póliza", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_NORMAL, "Normal", "./icons/poli_type_normal.png"),
				new BmFieldOption(TYPE_PREFERRED, "Preferente", "./icons/poli_type_preferred.png"),
				new BmFieldOption(TYPE_GROUP, "Grupal", "./icons/poli_type_group.png")
				)));
		number = setField("number", "", "No. Póliza", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		startDate = setField("startdate", "", "Fecha Emisión", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fecha Vencimiento", 20, Types.DATE, false, BmFieldType.DATE, false);
		
		payFrequency = setField("payfrequency", "", "Forma Pago", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		payFrequency.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYFREQUENCY_YEAR, "Anual", "Anual", "./icons/poli_payfrequency_year.png"),
				new BmFieldOption(PAYFREQUENCY_SEMESTER, "Semestral", "Semest.", "./icons/poli_payfrequency_semester.png"),
				new BmFieldOption(PAYFREQUENCY_QUARTER, "Trimestral", "Trimest.", "./icons/poli_payfrequency_trimester.png"),
				new BmFieldOption(PAYFREQUENCY_MONTH, "Mensual", "Mens.", "./icons/poli_payfrequency_month.png")
				)));
		
		payType = setField("paytype", "", "Conducto Cobro", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		payType.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYTYPE_CREDIT, "Crédito", "Crédito", "./icons/poli_paytype_credit.png"),
				new BmFieldOption(PAYTYPE_DEBIT, "Débito", "Débito", "./icons/poli_paytype_debit.png"),
				new BmFieldOption(PAYTYPE_TRANSFER, "Transferencia", "Transf.", "./icons/poli_paytype_transfer.png"),
				new BmFieldOption(PAYTYPE_AGENT, "Agente", "Agente", "./icons/poli_paytype_agent.png")
				)));
		
		status = setField("status", "", "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_PENDING, "Pendiente", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_PAID, "Pagado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_LATE, "Vencido", "./icons/popa_status_late.png")
				)));
		
		tcDigits = setField("tcdigits", "", "4 Dígitos T/C", 4, Types.VARCHAR, true, BmFieldType.STRING, false);
		tcExpiryDate = setField("tcexpirydate", "", "Venc. T/C", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		premium = setField("premium", "", "Prima", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Suma Asegurada", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		userId = setField("userid", "", "Agente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		insuredId = setField("insuredid", "", "Asegurado", 8, Types.INTEGER, false, BmFieldType.ID, false);
		insuranceId = setField("insuranceId", "", "Plán Básico", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getNumber(), 
						getBmoCustomer().getDisplayName(), 
						getType(),
						getBmoInsurance().getName(),
						getBmoInsurance().getBmoCurrency().getName(),
						getPayType(),
						getPayFrequency(),
						getStatus(),
						getPremium()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoCustomer().getDisplayName()),
				new BmSearchField(getBmoCustomer().getCode()),
				new BmSearchField(getNumber())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoCustomer().getDisplayName(), BmOrder.ASC)));
	}
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}
	
	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getNumber() {
		return number;
	}

	public void setNumber(BmField number) {
		this.number = number;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getPayFrequency() {
		return payFrequency;
	}

	public void setPayFrequency(BmField payFrequency) {
		this.payFrequency = payFrequency;
	}

	public BmField getPayType() {
		return payType;
	}

	public void setPayType(BmField payType) {
		this.payType = payType;
	}

	public BmField getTcDigits() {
		return tcDigits;
	}

	public void setTcDigits(BmField tcDigits) {
		this.tcDigits = tcDigits;
	}

	public BmField getTcExpiryDate() {
		return tcExpiryDate;
	}

	public void setTcExpiryDate(BmField tcExpiryDate) {
		this.tcExpiryDate = tcExpiryDate;
	}

	public BmField getPremium() {
		return premium;
	}

	public void setPremium(BmField premium) {
		this.premium = premium;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getInsuredId() {
		return insuredId;
	}

	public void setInsuredId(BmField insuredId) {
		this.insuredId = insuredId;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(BmField insuranceId) {
		this.insuranceId = insuranceId;
	}

	public BmoInsurance getBmoInsurance() {
		return bmoInsurance;
	}

	public void setBmoInsurance(BmoInsurance bmoInsurance) {
		this.bmoInsurance = bmoInsurance;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}
}
