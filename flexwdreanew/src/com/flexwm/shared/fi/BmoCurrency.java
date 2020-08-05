/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

/**
 * @author jhernandez
 *
 */
public class BmoCurrency extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, parity, updateTime;
	
	
	public static String ACTION_GETCURRENCYPARITY = "CURRENCYPARITY";
	
	public BmoCurrency() {
		super("com.flexwm.server.fi.PmCurrency","currencies", "currencyid", "CURE","Monedas");
		 
		code = setField("code", "", "Clave Moneda", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Moneda", 30, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		parity = setField("parity", "", "Tipo de Cambio", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		updateTime = setField("updatetime", "", "Ult. Actual.", 30, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getDescription(),
				getParity(),
				getUpdateTime()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getParity()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
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

	public BmField getParity() {
		return parity;
	}

	public void setParity(BmField parity) {
		this.parity = parity;
	}

	public BmField getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(BmField updateTime) {
		this.updateTime = updateTime;
	}
}
