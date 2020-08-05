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
import com.flexwm.shared.fi.BmoBudgetItem;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class BmoWork extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, isMaster, userId, status, indirects, indirectsAmount, companyId, subTotal, total, budgetItemId, 
	developmentPhaseId, startDate;

	private BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
	private BmoUser bmoUser = new BmoUser();
	private BmoCompany bmoCompany  = new BmoCompany();

	private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();


	public static String CODE_PREFIX = "WO-";
	public static final String ACCESS_CHANGESTATUS = "WORKCHS";

	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static final char STATUS_CANCELLED = 'C'; 

	public static String ACTION_COPYWORK = "WORKCOPY";


	public BmoWork(){
		super("com.flexwm.server.co.PmWork", "works", "workid", "WORK", "Obras");

		//Campo de Datos		
		code = setField("code", "", "Clave", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		startDate = setField("startdate", "", "Fecha", 20, Types.DATE, false, BmFieldType.DATE, false);

		developmentPhaseId = setField("developmentphaseid", "", "Etapa Desarrollo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Presupuesto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//typeCostCenterId = setField("typecostcenterid", "", "CC Tipo", 20, Types.INTEGER, true, BmFieldType.ID, false);
		//workTypeId = setField("worktypeid", "", "Tipo", 8, Types.INTEGER, true, BmFieldType.ID, false);

		isMaster = setField("ismaster", "", "Maestro", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);


		status = setField("status", "", "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_CANCELLED, "Cancelado", "./icons/status_cancelled.png")
				)));
		indirects = setField("indirects", "", "Factor Indirectos", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		indirectsAmount = setField("indirectsamount", "", "Indirectos", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		subTotal = setField("subtotal", "", "SubTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getStartDate(),
				getDescription(),
				getIsMaster(),
				bmoDevelopmentPhase.getCode(),								
				bmoCompany.getName(),
				getStatus(),
				getTotal()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
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
	 * @return the isMaster
	 */
	public BmField getIsMaster() {
		return isMaster;
	}

	/**
	 * @param isMaster the isMaster to set
	 */
	public void setIsMaster(BmField isMaster) {
		this.isMaster = isMaster;
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
	 * @return the indirects
	 */
	public BmField getIndirects() {
		return indirects;
	}

	/**
	 * @param indirects the indirects to set
	 */
	public void setIndirects(BmField indirects) {
		this.indirects = indirects;
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
	 * @return the bmoDevelopmentPhase
	 */
	public BmoDevelopmentPhase getBmoDevelopmentPhase() {
		return bmoDevelopmentPhase;
	}

	/**
	 * @param bmoDevelopmentPhase the bmoDevelopmentPhase to set
	 */
	public void setBmoDevelopmentPhase(BmoDevelopmentPhase bmoDevelopmentPhase) {
		this.bmoDevelopmentPhase = bmoDevelopmentPhase;
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
	 * @return the developmentPhaseId
	 */
	public BmField getDevelopmentPhaseId() {
		return developmentPhaseId;
	}

	/**
	 * @param developmentPhaseId the developmentPhaseId to set
	 */
	public void setDevelopmentPhaseId(BmField developmentPhaseId) {
		this.developmentPhaseId = developmentPhaseId;
	}

	/**
	 * @return the dateCreate
	 */
	public BmField getStartDate() {
		return startDate;
	}

	/**
	 * @param dateCreate the dateCreate to set
	 */
	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the indirectsAmount
	 */
	public BmField getIndirectsAmount() {
		return indirectsAmount;
	}

	/**
	 * @param indirectsAmount the indirectsAmount to set
	 */
	public void setIndirectsAmount(BmField indirectsAmount) {
		this.indirectsAmount = indirectsAmount;
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

	/**
	 * @return the bmoBudgetItem
	 */
	public BmoBudgetItem getBmoBudgetItem() {
		return bmoBudgetItem;
	}

	/**
	 * @param bmoBudgetItem the bmoBudgetItem to set
	 */
	public void setBmoBudgetItem(BmoBudgetItem bmoBudgetItem) {
		this.bmoBudgetItem = bmoBudgetItem;
	}



}
