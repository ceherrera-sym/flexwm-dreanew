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


public class BmoCurrencyRate extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField rate, dateTime, currencyId;
	
	public BmoCurrencyRate() {
		super("com.flexwm.server.fi.PmCurrencyRate","currencyrates", "currencyrateid", "CURA", "Tipos de Cambio");
		 
		rate = setField("rate", "", "T. Cambio", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		dateTime = setField("datetime", "", "Fecha y Hora", 30, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		currencyId = setField("currencyid", "", "Moneda", 4, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getDateTime(),
				getRate()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getRate()),
				new BmSearchField(getDateTime())

				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDateTime(), BmOrder.DESC)));
	}

	public BmField getRate() {
		return rate;
	}

	public void setRate(BmField rate) {
		this.rate = rate;
	}

	public BmField getDateTime() {
		return dateTime;
	}

	public void setDateTime(BmField dateTime) {
		this.dateTime = dateTime;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}	
}
