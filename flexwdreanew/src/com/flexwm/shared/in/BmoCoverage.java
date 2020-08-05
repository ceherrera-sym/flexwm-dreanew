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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoCoverage extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, maxAmountApplies, maxUnlimited, maxAmount, minAmountApplies, minUnlimited, minAmount, 
	minAge, maxAge, restrictions, conditions;

	public BmoCoverage() {
		super("com.flexwm.server.in.PmCoverage", "coverages", "coverageid", "COVE", "Coberturas");
		code = setField("code", "", "Clave Cobertura", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		maxAmountApplies = setField("maxamountapplies", "", "No Aplica", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		maxUnlimited = setField("maxunlimited", "", "Sin Límite", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		maxAmount = setField("maxamount", "", "Monto Máximo", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		minAmountApplies = setField("minamountapplies", "", "No Aplica", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		minUnlimited = setField("minunlimited", "", "Sin Límite", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		minAmount = setField("minamount", "", "Monto Mínimo", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		maxAge = setField("maxage", "", "Edad Máxima", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		minAge = setField("minage", "", "Edad Mínima", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		restrictions = setField("restrictions", "", "Restricciones", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		conditions = setField("conditions", "", "Cond. Indemn.", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getCode(), 
						getName(), 
						getDescription(),
						getRestrictions()
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

	public BmField getMaxAmountApplies() {
		return maxAmountApplies;
	}

	public void setMaxAmountApplies(BmField maxAmountApplies) {
		this.maxAmountApplies = maxAmountApplies;
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

	public BmField getMinAmountApplies() {
		return minAmountApplies;
	}

	public void setMinAmountApplies(BmField minAmountApplies) {
		this.minAmountApplies = minAmountApplies;
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

	public BmField getMinAge() {
		return minAge;
	}

	public void setMinAge(BmField minAge) {
		this.minAge = minAge;
	}

	public BmField getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(BmField maxAge) {
		this.maxAge = maxAge;
	}

	public BmField getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(BmField restrictions) {
		this.restrictions = restrictions;
	}

	public BmField getConditions() {
		return conditions;
	}

	public void setConditions(BmField conditions) {
		this.conditions = conditions;
	}
}
