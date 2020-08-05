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
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoUnit;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoUnitPrice extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, date, type, category, subTotal,total, baseFsr, materialTypeId, genRequisition,
	deleteHistory, quantityByHouse, status, workId, unitId, currencyId, indirects,totalIndirects;
	BmoSupplier bmoSupplier = new BmoSupplier();
	BmoWork bmoWork = new BmoWork();
	BmoUnit bmoUnit = new BmoUnit();
	BmoCurrency bmoCurrency = new BmoCurrency();

	public static final char TYPE_MATERIAL = 'M';
	public static final char TYPE_WORK = 'W';
	public static final char TYPE_TOOL = 'T';
	public static final char TYPE_EQUIPMENT = 'E';
	public static final char TYPE_AUXILIARY = 'A';
	public static final char TYPE_CONCEPT = 'C';
	public static final char TYPE_INTERMEDIATE = 'I';

	public static final char CATEGORY_BASIC = 'B';
	public static final char CATEGORY_COMPLEX = 'C';
	public static final char CATEGORY_HOURLY = 'H';
	public static final char CATEGORY_SUPPLIES = 'S';

	public static final char STATUS_OBSOLETE = '1'; 
	public static final char STATUS_ACTIVES = '2';

	public static String CODE_PREFIX = "PU-";

	public BmoUnitPrice() {
		super("com.flexwm.server.co.PmUnitPrice", "unitprices", "unitpriceid", "UNPR", "Precios Unitarios");

		// Campo de datos
		code = setField("code", "", "Clave P. Unitario", 30, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);	
		date = setField("date", "", "Fecha", 768, Types.DATE, true, BmFieldType.DATE, false);	
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_MATERIAL, "Material", "./icons/unpr_type_material.png"),
				new BmFieldOption(TYPE_WORK, "Mano de Obra", "./icons/unpr_type_work.png"),
				new BmFieldOption(TYPE_TOOL, "Herramienta", "./icons/unpr_type_tool.png"),
				new BmFieldOption(TYPE_EQUIPMENT, "Equipo", "./icons/unpr_type_equipment.png"),
				new BmFieldOption(TYPE_AUXILIARY, "Auxiliares", "./icons/unpr_type_auxiliary.png"),
				new BmFieldOption(TYPE_CONCEPT, "Concepto", "./icons/unpr_type_concept.png"),
				new BmFieldOption(TYPE_INTERMEDIATE, "Mando Intermedio", "./icons/unpr_type_intermediate.png")
				)));
		category = setField("category", "", "Categoría", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		category.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(CATEGORY_SUPPLIES, "Insumo", "./icons/unpr_category_supplies.png"),
				new BmFieldOption(CATEGORY_BASIC, "Básico", "./icons/unpr_category_basic.png"),
				new BmFieldOption(CATEGORY_COMPLEX, "Compuesto", "./icons/unpr_category_complex.png"),
				new BmFieldOption(CATEGORY_HOURLY, "Horario", "./icons/unpr_category_hourly.png")
				)));
		subTotal = setField("subtotal", "", "subTotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		total = setField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		indirects = setField("indirects", "", "% Indirectos", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		totalIndirects = setField("totalindirects", "", "Indirectos", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		baseFsr = setField("basefsr", "", "FSR", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);		
		materialTypeId = setField("materialtypeid", "", "Familias", 8, Types.INTEGER, true, BmFieldType.ID, false);		

		genRequisition = setField("genrequisition", "", "Orden de Compra Requerida", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		deleteHistory = setField("deletehistory", "", "Historia Borrada-", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);		

		quantityByHouse = setField("quantitybyhouse", "", "Cantidad por vivienda-", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		status = setField("status", "", "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OBSOLETE, "Obsoleto", "./icons/unpr_status_absolete.png"),
				new BmFieldOption(STATUS_ACTIVES, "Activo", "./icons/unpr_status_actives.png")
				)));
		workId = setField("workid", "", "Obra", 8, Types.INTEGER, true, BmFieldType.ID, false);
		unitId = setField("unitid", "", "Unidad", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(), 
				getDescription(),
				bmoUnit.getCode(),
				getType(),				
				getCategory(),				
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

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the date
	 */
	public BmField getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(BmField date) {
		this.date = date;
	}

	/**
	 * @return the type
	 */
	public BmField getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BmField type) {
		this.type = type;
	}

	/**
	 * @return the category
	 */
	public BmField getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(BmField category) {
		this.category = category;
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
	 * @return the baseFsr
	 */
	public BmField getBaseFsr() {
		return baseFsr;
	}

	/**
	 * @param baseFsr the baseFsr to set
	 */
	public void setBaseFsr(BmField baseFsr) {
		this.baseFsr = baseFsr;
	}


	/**
	 * @return the materialTypeId
	 */
	public BmField getMaterialTypeId() {
		return materialTypeId;
	}

	/**
	 * @param materialTypeId the materialTypeId to set
	 */
	public void setMaterialTypeId(BmField materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	/**
	 * @return the genRequisition
	 */
	public BmField getGenRequisition() {
		return genRequisition;
	}

	/**
	 * @param genRequisition the genRequisition to set
	 */
	public void setGenRequisition(BmField genRequisition) {
		this.genRequisition = genRequisition;
	}

	/**
	 * @return the deleteHistory
	 */
	public BmField getDeleteHistory() {
		return deleteHistory;
	}

	/**
	 * @param deleteHistory the deleteHistory to set
	 */
	public void setDeleteHistory(BmField deleteHistory) {
		this.deleteHistory = deleteHistory;
	}

	/**
	 * @return the quantityByHouse
	 */
	public BmField getQuantityByHouse() {
		return quantityByHouse;
	}

	/**
	 * @param quantityByHouse the quantityByHouse to set
	 */
	public void setQuantityByHouse(BmField quantityByHouse) {
		this.quantityByHouse = quantityByHouse;
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
	 * @return the unitId
	 */
	public BmField getUnitId() {
		return unitId;
	}

	/**
	 * @param unitId the unitId to set
	 */
	public void setUnitId(BmField unitId) {
		this.unitId = unitId;
	}

	/**
	 * @return the currencyId
	 */
	public BmField getCurrencyId() {
		return currencyId;
	}

	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
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
	 * @return the bmoUnit
	 */
	public BmoUnit getBmoUnit() {
		return bmoUnit;
	}

	/**
	 * @param bmoUnit the bmoUnit to set
	 */
	public void setBmoUnit(BmoUnit bmoUnit) {
		this.bmoUnit = bmoUnit;
	}

	/**
	 * @return the bmoCurrency
	 */
	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	/**
	 * @param bmoCurrency the bmoCurrency to set
	 */
	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
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
	 * @return the totalIndirects
	 */
	public BmField getTotalIndirects() {
		return totalIndirects;
	}

	/**
	 * @param totalIndirects the totalIndirects to set
	 */
	public void setTotalIndirects(BmField totalIndirects) {
		this.totalIndirects = totalIndirects;
	}


}
