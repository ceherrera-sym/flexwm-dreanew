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

import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoInsurance extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, comments, maxUnlimited, maxAmount, minUnlimited, minAmount, maxAge, minAge, payYears,
		insuranceCategoryId, goalId, currencyId;
	private BmoInsuranceCategory bmoInsuranceCategory = new BmoInsuranceCategory();
	private BmoGoal bmoGoal = new BmoGoal();
	private BmoCurrency bmoCurrency = new BmoCurrency();
	
	public static String CODE_PREFIX = "PS";
	
	public static String ACTION_COPY = "COPY";

	public BmoInsurance() {
		super("com.flexwm.server.in.PmInsurance", "insurances", "insuranceid", "INSU", "Productos/Seguros");
		code = setField("code", "", "Clave Prod/Seg.", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		maxUnlimited = setField("maxunlimited", "", "Sin Límite", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		maxAmount = setField("maxamount", "", "Suma Aseg. Máxima", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		minUnlimited = setField("minunlimited", "", "Sin Límite", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		minAmount = setField("minamount", "", "Suma Aseg. Mínima", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		maxAge = setField("maxage", "", "Edad Máxima", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		minAge = setField("minage", "", "Edad Mínima", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);

		payYears = setField("payyears", "", "Años de Pago", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		insuranceCategoryId = setField("insurancecategoryid", "", "Categoría", 8, Types.INTEGER, true, BmFieldType.ID, false);
		goalId = setField("goalid", "", "Objetivo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getCode(), 
						getName(), 
						getDescription(),
						getBmoInsuranceCategory().getCode(),
						getBmoGoal().getCode(),
						getBmoCurrency().getCode()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
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

	public BmField getInsuranceCategoryId() {
		return insuranceCategoryId;
	}

	public void setInsuranceCategoryId(BmField insuranceCategoryId) {
		this.insuranceCategoryId = insuranceCategoryId;
	}

	public BmField getGoalId() {
		return goalId;
	}

	public void setGoalId(BmField goalId) {
		this.goalId = goalId;
	}

	public BmoGoal getBmoGoal() {
		return bmoGoal;
	}

	public void setBmoGoal(BmoGoal bmoGoal) {
		this.bmoGoal = bmoGoal;
	}

	public BmField getMaxUnlimited() {
		return maxUnlimited;
	}

	public void setMaxUnlimited(BmField maxUnlimited) {
		this.maxUnlimited = maxUnlimited;
	}

	public BmField getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BmField maxAmount) {
		this.maxAmount = maxAmount;
	}

	public BmField getMinUnlimited() {
		return minUnlimited;
	}

	public void setMinUnlimited(BmField minUnlimited) {
		this.minUnlimited = minUnlimited;
	}

	public BmField getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BmField minAmount) {
		this.minAmount = minAmount;
	}

	public BmField getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(BmField maxAge) {
		this.maxAge = maxAge;
	}

	public BmField getMinAge() {
		return minAge;
	}

	public void setMinAge(BmField minAge) {
		this.minAge = minAge;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getPayYears() {
		return payYears;
	}

	public void setPayYears(BmField payYears) {
		this.payYears = payYears;
	}

	public BmoInsuranceCategory getBmoInsuranceCategory() {
		return bmoInsuranceCategory;
	}

	public void setBmoInsuranceCategory(BmoInsuranceCategory bmoInsuranceCategory) {
		this.bmoInsuranceCategory = bmoInsuranceCategory;
	}
	
}
