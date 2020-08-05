
package com.flexwm.server.fi;

import java.util.StringTokenizer;

import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoCurrencyRate;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmCurrencyRate extends PmObject {
	BmoCurrencyRate bmoCurrencyRate;

	public PmCurrencyRate(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCurrencyRate = new BmoCurrencyRate();
		setBmObject(bmoCurrencyRate);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCurrencyRate());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCurrencyRate = (BmoCurrencyRate)bmObject;
		// Actualiza fecha y hora
		if (bmoCurrencyRate.getDateTime().toString().equals(""))
			bmoCurrencyRate.getDateTime().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

		//Validar que no exista el Tipo de Cambio
		if (exitCurrencyRate(bmoCurrencyRate.getCurrencyId().toInteger(), bmoCurrencyRate.getDateTime().toString().substring(0,10),bmoCurrencyRate.getId())) {
			bmUpdateResult.addError(bmoCurrencyRate.getDateTime().getName(), "Ya existe un Tipo de Cambio para " + bmoCurrencyRate.getDateTime().toString().substring(0,10));
		}

		if (countDecimals(""+bmoCurrencyRate.getRate().toDouble(), 4)) {
			bmUpdateResult.addError(bmoCurrencyRate.getRate().getName(), "Maximo 4 decimales");
		}


		super.save(pmConn, bmoCurrencyRate, bmUpdateResult);

		//Obten el registro de la moneda
		PmCurrency pmCurrency = new PmCurrency(getSFParams());
		BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(bmoCurrencyRate.getCurrencyId().toInteger());
		
		// Actualiza el tipo de cambio default de la moneda
		pmCurrency.save(pmConn, bmoCurrency, bmUpdateResult);
		
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCurrencyRate = (BmoCurrencyRate)bmObject;

		super.delete(pmConn, bmoCurrencyRate, bmUpdateResult);

		//Obten el registro de la moneda
		PmCurrency pmCurrency = new PmCurrency(getSFParams());
		BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(bmoCurrencyRate.getCurrencyId().toInteger());

		// Actualiza el tipo de cambio default de la moneda
		pmCurrency.save(pmConn, bmoCurrency, bmUpdateResult);

		return bmUpdateResult;
	}
	private boolean exitCurrencyRate(int currencyId,String date,int currencyRateId) throws SFPmException {
		PmConn pmConn = new PmConn(getSFParams());
		String sql = "";		
		boolean result = false;
		pmConn.open();
		sql = "SELECT cura_currencyid FROM currencyrates  WHERE SUBSTRING(cura_datetime,1,10)  = '" + date  + "' AND cura_currencyid = " + currencyId;
		if (currencyRateId > 0)sql += " AND cura_currencyrateid <> " + currencyRateId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			result = true;
		}		
		pmConn.close();
		return result;		
	}
	private boolean countDecimals(String number,int limitDecimals) {
		@SuppressWarnings("unused")
		String firstNumber = "";
		String decimals = "";
		boolean result = false;
		StringTokenizer tabs = new StringTokenizer(number,".");		
		while (tabs.hasMoreTokens()) {
			firstNumber = tabs.nextToken();
			decimals = tabs.nextToken();
		}
		if (decimals.length() > 4)
			result = true;
		return result;
	}
	

}
