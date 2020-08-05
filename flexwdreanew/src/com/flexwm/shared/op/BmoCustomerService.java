/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.op;

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

public class BmoCustomerService extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, orderId, customerServiceTypeId, status, solution, active, registrationDate, 
	committalDate, solutionDate, file, userId, companyId;
	BmoCustomerServiceType bmoCustomerServiceType = new BmoCustomerServiceType();
	BmoOrder bmoOrder = new BmoOrder();

	public static final char STATUS_OPENED = 'O';
	public static final char STATUS_CLOSED = 'C';
	public static final char STATUS_FROZEN = 'F';

	public static String CODE_PREFIX = "AT-";

	public BmoCustomerService() {
		super("com.flexwm.server.op.PmCustomerService", "customerservices", "customerserviceid", "CUSE", "Atn.Clientes");

		//Campo de Datos		
		code = setField("code", "", "Clave Atención", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		orderId = setField("orderid", "", "Pedido", 11, Types.INTEGER, true, BmFieldType.ID, false);
		customerServiceTypeId = setField("customerservicetypeid", "", "Tipo Atención", 11, Types.INTEGER, false, BmFieldType.ID, false);
		solution = setField("solution", "", "Solución", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		status = setField("status", "" + STATUS_OPENED, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OPENED, "Abierta", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_CLOSED, "Cerrada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_FROZEN, "Congelada", "./icons/status_expired.png")
				)));
		active = setField("active", "", "Activo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		registrationDate = setField("registrationdate", "", "Fecha Registro", 12, Types.DATE, true, BmFieldType.DATE, false);
		committalDate = setField("committaldate", "", "Fecha Compromiso", 12, Types.DATE, true, BmFieldType.DATE, false);
		solutionDate = setField("solutiondate", "", "Fecha Solución", 12, Types.DATE, true, BmFieldType.DATE, false);
		file = setField("file", "", "Archivo", 500, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		userId = setField("userid", "", "Registra", 11, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),				
				bmoCustomerServiceType.getName(),
				bmoOrder.getBmoCustomer().getCode(),
				bmoOrder.getBmoCustomer().getDisplayName(),
				bmoOrder.getCode(),
				bmoOrder.getName(),
				getRegistrationDate(),
				getSolutionDate(),
				getStatus(),
				getActive()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription()), 
				new BmSearchField(bmoOrder.getCode()),
				new BmSearchField(bmoOrder.getBmoCustomer().getCode()),
				new BmSearchField(bmoOrder.getBmoCustomer().getDisplayName())
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
	 * @return the orderId
	 */
	public BmField getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the customerServiceTypeId
	 */
	public BmField getCustomerServiceTypeId() {
		return customerServiceTypeId;
	}

	/**
	 * @param customerServiceTypeId the customerServiceTypeId to set
	 */
	public void setCustomerServiceTypeId(BmField customerServiceTypeId) {
		this.customerServiceTypeId = customerServiceTypeId;
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
	 * @return the solution
	 */
	public BmField getSolution() {
		return solution;
	}

	/**
	 * @param solution the solution to set
	 */
	public void setSolution(BmField solution) {
		this.solution = solution;
	}

	/**
	 * @return the active
	 */
	public BmField getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(BmField active) {
		this.active = active;
	}

	/**
	 * @return the registrationDate
	 */
	public BmField getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(BmField registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @return the committalDate
	 */
	public BmField getCommittalDate() {
		return committalDate;
	}

	/**
	 * @param committalDate the committalDate to set
	 */
	public void setCommittalDate(BmField committalDate) {
		this.committalDate = committalDate;
	}

	/**
	 * @return the solutionDate
	 */
	public BmField getSolutionDate() {
		return solutionDate;
	}

	/**
	 * @param solutionDate the solutionDate to set
	 */
	public void setSolutionDate(BmField solutionDate) {
		this.solutionDate = solutionDate;
	}

	/**
	 * @return the bmoCustomerServiceType
	 */
	public BmoCustomerServiceType getBmoCustomerServiceType() {
		return bmoCustomerServiceType;
	}

	/**
	 * @param bmoCustomerServiceType the bmoCustomerServiceType to set
	 */
	public void setBmoCustomerServiceType(BmoCustomerServiceType bmoCustomerServiceType) {
		this.bmoCustomerServiceType = bmoCustomerServiceType;
	}

	/**
	 * @return the bmoOrder
	 */
	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	/**
	 * @param bmoOrder the bmoOrder to set
	 */
	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}
}
