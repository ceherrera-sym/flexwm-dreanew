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

public class BmoDevelopmentPhase extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, isactive, enddate, startdate, blueprint, 
	orderCommissionId, userId, developmentId, wFlowTypeId, wFlowId, ventureCostCenterId, electronicFolio, fideicomisoNumber, budgetId,
	numberProperties, maintenanceCost, feeDueDate, companyId, bankAccountId;;

	public static String CODE_PREFIX = "DP-";

	public BmoDevelopmentPhase(){
		super("com.flexwm.server.co.PmDevelopmentPhase", "developmentphases", "developmentphaseid", "DVPH", "Etapas Desarrollos");

		//Campo de Datos		
		code = setField("code", "", "Clave Etapa", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		isactive = setField("isactive", "", "Activo", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		startdate = setField("startdate", "", "Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		enddate = setField("enddate", "", "Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		blueprint = setField("blueprint", "", "Plano", 512, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		orderCommissionId = setField("ordercommissionid", "", "Tabulador Comisión", 8, Types.INTEGER, true, BmFieldType.ID, false);
		userId = setField("userid", "", "Responsable", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Seguimiento", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		developmentId = setField("developmentid", "", "Desarrollo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		ventureCostCenterId = setField("venturecostcenterid", "", "C. Costos P.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		electronicFolio = setField("electronicfolio", "", "Folio Mercantil Electrónico", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		fideicomisoNumber = setField("fideicomisonumber", "", "Núm. de Fideicomiso", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		budgetId = setField("budgetid", "", "Presupuesto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		numberProperties = setField("numberproperties", "", "No.Viviendas", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		maintenanceCost = setField("maintenancecost", "", "Costo de mtto.", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		feeDueDate = setField("feeduedate", "", "Vigencia costo", 20, Types.DATE, true, BmFieldType.DATE, false);
		companyId = setField("companyId", "", "Empresa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bankAccountId = setField("bankaccountid", "", "Cuenta de Banco", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),	
				getNumberProperties(),
				getBlueprint(),
				getStartDate(),
				getEndDate(),
				getIsActive()
				));
	}

	@Override
	public ArrayList<BmField> getExtendedDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),	
				getDescription(),
				getStartDate(),
				getEndDate(),
				getIsActive()
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

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getNumberProperties(),
				getIsActive()
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
	 * @return the isactive
	 */
	public BmField getIsActive() {
		return isactive;
	}

	/**
	 * @param isactive the isactive to set
	 */
	public void setIsActive(BmField isactive) {
		this.isactive = isactive;
	}

	/**
	 * @return the enddate
	 */
	public BmField getEndDate() {
		return enddate;
	}

	/**
	 * @param enddate the enddate to set
	 */
	public void setEndDate(BmField enddate) {
		this.enddate = enddate;
	}

	/**
	 * @return the startdate
	 */
	public BmField getStartDate() {
		return startdate;
	}

	/**
	 * @param startdate the startdate to set
	 */
	public void setStartDate(BmField startdate) {
		this.startdate = startdate;
	}

	/**
	 * @return the developmentid
	 */
	public BmField getDevelopmentId() {
		return developmentId;
	}

	/**
	 * @param developmentid the developmentid to set
	 */
	public void setDevelopmentId(BmField developmentid) {
		this.developmentId = developmentid;
	}

	public BmField getBlueprint() {
		return blueprint;
	}

	public void setBlueprint(BmField blueprint) {
		this.blueprint = blueprint;
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

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getOrderCommissionId() {
		return orderCommissionId;
	}

	public void setOrderCommissionId(BmField orderCommissionId) {
		this.orderCommissionId = orderCommissionId;
	}

	public BmField getVentureCostCenterId() {
		return ventureCostCenterId;
	}

	public void setVentureCostCenterId(BmField ventureCostCenterId) {
		this.ventureCostCenterId = ventureCostCenterId;
	}

	/**
	 * @return the electronicFolio
	 */
	public BmField getElectronicFolio() {
		return electronicFolio;
	}

	/**
	 * @param electronicFolio the electronicFolio to set
	 */
	public void setElectronicFolio(BmField electronicFolio) {
		this.electronicFolio = electronicFolio;
	}

	/**
	 * @return the fideicomisoNumber
	 */
	public BmField getFideicomisoNumber() {
		return fideicomisoNumber;
	}

	/**
	 * @param fideicomisoNumber the fideicomisoNumber to set
	 */
	public void setFideicomisoNumber(BmField fideicomisoNumber) {
		this.fideicomisoNumber = fideicomisoNumber;
	}


	/**
	 * @return the budgetId
	 */
	public BmField getBudgetId() {
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(BmField budgetId) {
		this.budgetId = budgetId;
	}

	/**
	 * @return the numberProperties
	 */
	public BmField getNumberProperties() {
		return numberProperties;
	}

	/**
	 * @param numberProperties the numberProperties to set
	 */
	public void setNumberProperties(BmField numberProperties) {
		this.numberProperties = numberProperties;
	}

	/**
	 * @return the maintenanceCost
	 */
	public BmField getMaintenanceCost() {
		return maintenanceCost;
	}

	/**
	 * @param maintenanceCost the maintenanceCost to set
	 */
	public void setMaintenanceCost(BmField maintenanceCost) {
		this.maintenanceCost = maintenanceCost;
	}

	public BmField getFeeDueDate() {
		return feeDueDate;
	}

	public void setFeeDueDate(BmField feeDueDate) {
		this.feeDueDate = feeDueDate;
	}

	/**
	 * @return the companyId
	 */
	public BmField getCompanyId() {
		return companyId;
	}

	/**
	 * @return the bankAccountId
	 */
	public BmField getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	/**
	 * @param bankAccountId the bankAccountId to set
	 */
	public void setBankAccountId(BmField bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
}
