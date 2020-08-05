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
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.wf.BmoWFlow;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;


/**
 * @author smuniz
 *
 */

public class BmoWorkContract extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField workId, supplierId, code, name,	description, startDate, endDate, downPayment, percentDownPayment,
	hasTax, tax, dailySanction,	observations, quantity,	subTotal, total, totalReal, companyId,	paymentDate, status, estimationType,
	guaranteeFund, percentGuaranteeFund, downPaymentStatus,	comments, dateContract,	auxStatus, history, 
	descriptionIsclosed, worktype, lotDescription, finished, totalWarranty, totalConcepts, paymentStatus,
	budgetItemId, hasSanction, wFlowTypeId, wFlowId;

	BmoWork bmoWork = new BmoWork();
	BmoSupplier bmoSupplier = new BmoSupplier();
	BmoCompany bmoCompany = new BmoCompany();
	BmoConceptGroup bmoConceptGroup = new BmoConceptGroup();
	private BmoWFlow bmoWFlow = new BmoWFlow();

	public static String CODE_PREFIX = "WOCO-";

	public static final String ACCESS_CHANGESTATUS = "WOCOCHS";	

	public static final char ESTIMATIONTYPE_CONCEPTHEADINGS = 'H';
	public static final char ESTIMATIONTYPE_CONCEPTS = 'C';

	//Estatus
	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static final char STATUS_CLOSED = 'D';
	public static final char STATUS_CANCEL = 'C';

	//Estatus de pago
	public static final char PAYMENTSTATUS_PENDING = 'R';
	public static final char PAYMENTSTATUS_PARTIALPAYMENT = 'P'; 
	public static final char PAYMENTSTATUS_TOTALPAYMENT = 'T'; 
	public static final char PAYMENTSTATUS_DEVOLUTION = 'D'; 
	public static final char PAYMENTSTATUS_CANCELLED = 'C';

	public static final char DOWNPAYMENTE_STATUS_PENDING = 'R';
	public static final char DOWNPAYMENTE_STATUS_AUTHORIZED = 'A'; 
	public static final char DOWNPAYMENTE_STATUS_CANCELLED = 'C';



	public BmoWorkContract(){
		super("com.flexwm.server.co.PmWorkContract", "workcontracts", "workcontractid", "WOCO", "Contratos de la Obra");

		//Campo de Datos	
		workId = setField("workid", "", " Obras Autorizadas", 8, Types.INTEGER, false, BmFieldType.ID, false); 
		supplierId = setField("supplierid", "", "Contratista", 8, Types.INTEGER, false, BmFieldType.ID, false);
		code = setField("code", "", "Clave de Contrato", 12, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Núm. de Contrato", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Fecha de Inicio", 20, Types.DATE, true, BmFieldType.DATE, false); 
		endDate = setField("enddate", "", "Fecha de Término", 20, Types.DATE, true, BmFieldType.DATE, false); 
		//Anticipo
		downPayment = setField("downpayment", "", "Monto Anticipo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false); 
		percentDownPayment  = setField("percentdownpayment", "", "% Anticipo", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);


		dailySanction = setField("dailysanction", "", "Sanción Diaria ", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		hasSanction = setField("hassanction", "", "¿Aplica Sanción?", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		observations = setField("observations", "", "Observaciones", 500, Types.VARCHAR, true, BmFieldType.STRING, false);

		//Conceptos
		totalConcepts =setField("totalConcepts", "", "Total Conceptos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		//Totales
		quantity =setField("quantity", "", "Cantidad Paquetes", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false); 
		subTotal = setField("subtotal", "", "Sub-Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false); 
		total = setField("total", "", "Total Inicial", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		totalReal = setField("totalreal", "", "Total Real", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		hasTax = setField("hastax", "", "¿Aplica IVA?", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false); 
		tax = setField("tax", "", "Monto IVA", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);


		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false); 
		paymentDate = setField("paymentdate", "", "Fecha de Pago Anticipo", 20, Types.DATE, true, BmFieldType.DATE, false);

		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false); 
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revision", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CLOSED, "Cerrado", "./icons/status_closed.png"),
				new BmFieldOption(STATUS_CANCEL, "Cancelado", "./icons/status_cancelled.png"))));

		paymentStatus = setField("paymentstatus", "" + PAYMENTSTATUS_PENDING, "Pago", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_PENDING, "En Revisión", "./icons/paymentstatus_revision.png"),
				new BmFieldOption(PAYMENTSTATUS_PARTIALPAYMENT, "Pago Parcial", "./icons/paymentstatus_partial.png"),
				new BmFieldOption(PAYMENTSTATUS_TOTALPAYMENT, "Pagada", "./icons/paymentstatus_total.png"),
				new BmFieldOption(PAYMENTSTATUS_DEVOLUTION, "Devolución", "./icons/paymentstatus_penalty.png"),
				new BmFieldOption(PAYMENTSTATUS_CANCELLED, "Cancelada", "./icons/paymentstatus_cancel.png")
				)));

		estimationType = setField("estimationtype", "", "Tipo de Estimación", 1, Types.CHAR, true, BmFieldType.OPTIONS, false); 
		estimationType.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(ESTIMATIONTYPE_CONCEPTHEADINGS, "Por Familias del Concepto", "./icons/woco_estiType_conceptHeading.png"),
				new BmFieldOption(ESTIMATIONTYPE_CONCEPTS, "Concepto por Concepto", "./icons/woco_estiType_concepts.png")
				)));

		guaranteeFund = setField("guaranteefund", "", "Fondo de Garantía", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false); 
		percentGuaranteeFund = setField("percentguaranteefund", "", "% Fondo de Garantía", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);

		downPaymentStatus = setField("downpaymentstatus", "", "Estatus del Anticipo", 1, Types.CHAR, true, BmFieldType.OPTIONS, false); 
		downPaymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(DOWNPAYMENTE_STATUS_PENDING, "En Revisión", "./icons/woco_downPaymStatus_pending.png"),
				new BmFieldOption(DOWNPAYMENTE_STATUS_AUTHORIZED, "Autorizado", "./icons/woco_downPaymStatus_authorized.png"),
				new BmFieldOption(DOWNPAYMENTE_STATUS_CANCELLED, "Cancelado", "./icons/woco_downPaymStatus_cancelled.png")
				)));
		comments = setField("comments", "", "Comentarios", 500, Types.VARCHAR, true, BmFieldType.STRING, false); 
		dateContract = setField("datecontract", "", "Fecha de Contrato", 255, Types.DATE, true, BmFieldType.DATE, false); 
		auxStatus = setField("auxstatus", "", "Estatus Aux", 1, Types.CHAR, true, BmFieldType.OPTIONS, false); 
		auxStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(DOWNPAYMENTE_STATUS_PENDING, "En Revisión", "./icons/woco_auxStatus_pending.png"),
				new BmFieldOption(DOWNPAYMENTE_STATUS_AUTHORIZED, "Autorizado", "./icons/woco_auxStatus_authorized.png"),
				new BmFieldOption(DOWNPAYMENTE_STATUS_CANCELLED, "Cancelado", "./icons/woco_auxStatus_cancelled.png")
				)));

		history = setField("history", "", "Historial", 1000, Types.VARCHAR, true, BmFieldType.STRING, false); 
		descriptionIsclosed  = setField("descriptionisclosed", "", "Descripción de Cerrado-", 50, Types.VARCHAR, true, BmFieldType.STRING, false); 
		worktype =setField("worktype", "", "Partida/Tipo de Obra-", 8, Types.INTEGER, true, BmFieldType.ID, false); 
		lotDescription  = setField("lotdescription", "", "Descripción del Lote", 50, Types.VARCHAR, true, BmFieldType.STRING, false); 
		finished = setField("finished", "", "Finiquitado", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false); 
		totalWarranty = setField("totalwarranty", "", "Garantía Total-", 20, Types.DOUBLE, true, BmFieldType.NUMBER , false);

		budgetItemId = setField("budgetitemid", "", "Part. Presup.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo WFlow", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(				
				getName(),				
				getDescription(),
				bmoCompany.getName(),
				bmoSupplier.getCode(),
				getStartDate(),
				getEndDate(),				
				getStatus(),				
				getTotal(),
				getTotalReal()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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
	 * @return the percentDownPayment
	 */
	public BmField getPercentDownPayment() {
		return percentDownPayment;
	}

	/**
	 * @param percentDownPayment the percentDownPayment to set
	 */
	public void setPercentDownPayment(BmField percentDownPayment) {
		this.percentDownPayment = percentDownPayment;
	}

	/**
	 * @return the hasTax
	 */
	public BmField getHasTax() {
		return hasTax;
	}

	/**
	 * @param hasTax the hasTax to set
	 */
	public void setHasTax(BmField hasTax) {
		this.hasTax = hasTax;
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
	 * @return the dailySanction
	 */
	public BmField getDailySanction() {
		return dailySanction;
	}

	/**
	 * @param dailySanction the dailySanction to set
	 */
	public void setDailySanction(BmField dailySanction) {
		this.dailySanction = dailySanction;
	}

	/**
	 * @return the observations
	 */
	public BmField getObservations() {
		return observations;
	}

	/**
	 * @param observations the observations to set
	 */
	public void setObservations(BmField observations) {
		this.observations = observations;
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
	 * @return the paymentDate
	 */
	public BmField getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(BmField paymentDate) {
		this.paymentDate = paymentDate;
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
	 * @return the estimationType
	 */
	public BmField getEstimationType() {
		return estimationType;
	}

	/**
	 * @param estimationType the estimationType to set
	 */
	public void setEstimationType(BmField estimationType) {
		this.estimationType = estimationType;
	}

	/**
	 * @return the guaranteeFund
	 */
	public BmField getGuaranteeFund() {
		return guaranteeFund;
	}

	/**
	 * @param guaranteeFund the guaranteeFund to set
	 */
	public void setGuaranteeFund(BmField guaranteeFund) {
		this.guaranteeFund = guaranteeFund;
	}

	/**
	 * @return the percentGuaranteeFund
	 */
	public BmField getPercentGuaranteeFund() {
		return percentGuaranteeFund;
	}

	/**
	 * @param percentGuaranteeFund the percentGuaranteeFund to set
	 */
	public void setPercentGuaranteeFund(BmField percentGuaranteeFund) {
		this.percentGuaranteeFund = percentGuaranteeFund;
	}

	/**
	 * @return the downPaymentStatus
	 */
	public BmField getDownPaymentStatus() {
		return downPaymentStatus;
	}

	/**
	 * @param downPaymentStatus the downPaymentStatus to set
	 */
	public void setDownPaymentStatus(BmField downPaymentStatus) {
		this.downPaymentStatus = downPaymentStatus;
	}

	/**
	 * @return the comments
	 */
	public BmField getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(BmField comments) {
		this.comments = comments;
	}

	/**
	 * @return the dateContract
	 */
	public BmField getDateContract() {
		return dateContract;
	}

	/**
	 * @param dateContract the dateContract to set
	 */
	public void setDateContract(BmField dateContract) {
		this.dateContract = dateContract;
	}

	/**
	 * @return the auxStatus
	 */
	public BmField getAuxStatus() {
		return auxStatus;
	}

	/**
	 * @param auxStatus the auxStatus to set
	 */
	public void setAuxStatus(BmField auxStatus) {
		this.auxStatus = auxStatus;
	}


	/**
	 * @return the history
	 */
	public BmField getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(BmField history) {
		this.history = history;
	}

	/**
	 * @return the descriptionIsclosed
	 */
	public BmField getDescriptionIsclosed() {
		return descriptionIsclosed;
	}

	/**
	 * @param descriptionIsclosed the descriptionIsclosed to set
	 */
	public void setDescriptionIsclosed(BmField descriptionIsclosed) {
		this.descriptionIsclosed = descriptionIsclosed;
	}

	/**
	 * @return the worktype
	 */
	public BmField getWorktype() {
		return worktype;
	}

	/**
	 * @param worktype the worktype to set
	 */
	public void setWorktype(BmField worktype) {
		this.worktype = worktype;
	}

	/**
	 * @return the lotDescription
	 */
	public BmField getLotDescription() {
		return lotDescription;
	}

	/**
	 * @param lotDescription the lotDescription to set
	 */
	public void setLotDescription(BmField lotDescription) {
		this.lotDescription = lotDescription;
	}

	/**
	 * @return the finished
	 */
	public BmField getFinished() {
		return finished;
	}

	/**
	 * @param finished the finished to set
	 */
	public void setFinished(BmField finished) {
		this.finished = finished;
	}

	/**
	 * @return the totalWarranty
	 */
	public BmField getTotalWarranty() {
		return totalWarranty;
	}

	/**
	 * @param totalWarranty the totalWarranty to set
	 */
	public void setTotalWarranty(BmField totalWarranty) {
		this.totalWarranty = totalWarranty;
	}

	/**
	 * @return the bmoWork
	 */
	public BmoWork getBmoWork() {
		return bmoWork;
	}

	/**
	 * @param bmoWork the bmoWork to set
	 */
	public void setBmoWork(BmoWork bmoWork) {
		this.bmoWork = bmoWork;
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
	 * @return the bmoCompany
	 */
	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	/**
	 * @param BmoConceptGroup the BmoConceptGroup to set
	 */
	public void setBmoConceptGroup(BmoConceptGroup bmoConceptGroup) {
		this.bmoConceptGroup = bmoConceptGroup;
	}

	/**
	 * @return the bmoCompany
	 */
	public BmoConceptGroup getBmoConceptGroup() {
		return bmoConceptGroup;
	}

	/**
	 * @param bmoCompany the bmoCompany to set
	 */
	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	/**
	 * @return the totalConcepts
	 */
	public BmField getTotalConcepts() {
		return totalConcepts;
	}

	/**
	 * @param totalConcepts the totalConcepts to set
	 */
	public void setTotalConcepts(BmField totalConcepts) {
		this.totalConcepts = totalConcepts;
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
	 * @return the budgetItemId
	 */
	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	/**
	 * @param budgetItemId the budgetItemId to set
	 */
	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmField getHasSanction() {
		return hasSanction;
	}

	public void setHasSanction(BmField hasSanction) {
		this.hasSanction = hasSanction;
	}

	public BmField getTotalReal() {
		return totalReal;
	}

	public void setTotalReal(BmField totalReal) {
		this.totalReal = totalReal;
	}
	
	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
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
	
}
