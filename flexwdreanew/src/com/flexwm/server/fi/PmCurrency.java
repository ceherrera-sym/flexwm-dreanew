
package com.flexwm.server.fi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.flexwm.server.DataSerie;
import com.flexwm.server.Response;
import com.flexwm.server.Serie;
import com.flexwm.server.Series;
import com.flexwm.server.YahooCurrencyExchange;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoCurrencyRate;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmCurrency extends PmObject {
	BmoCurrency bmoCurrency;

	public PmCurrency(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCurrency = new BmoCurrency();
		setBmObject(bmoCurrency);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCurrency());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCurrency = (BmoCurrency)bmObject;

		// Actualiza la ultima moneda
		PmCurrencyRate pmCurrencyRate = new PmCurrencyRate(getSFParams());
		BmoCurrencyRate bmoCurrencyRate = new BmoCurrencyRate();
		BmFilter filterByCurrency = new BmFilter();
		filterByCurrency.setValueFilter(bmoCurrencyRate.getKind(), bmoCurrencyRate.getCurrencyId(), bmoCurrency.getId());
		Iterator<BmObject> firstCurrencyRate = pmCurrencyRate.list(pmConn, filterByCurrency).iterator();
		if (firstCurrencyRate.hasNext())
			bmoCurrencyRate = (BmoCurrencyRate)firstCurrencyRate.next();

		bmoCurrency.getUpdateTime().setValue(bmoCurrencyRate.getDateTime().toString());
		bmoCurrency.getParity().setValue(bmoCurrencyRate.getRate().toDouble());

		super.save(pmConn, bmoCurrency, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateCurrencyExchanges(BmUpdateResult bmUpdateResult) throws SFException{
		Iterator<BmObject> currencyIterator = list().iterator();
		boolean success = true;
		String errors = "";

		// Obtiene moneda base de sistema 
		int systemCurrencyId = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger();
		BmoCurrency bmoSystemCurrency = (BmoCurrency)get(systemCurrencyId);

		PmCurrencyRate pmCurrencyRate = new PmCurrencyRate(getSFParams());

		while (currencyIterator.hasNext()) {
			BmoCurrency bmoCurrency = (BmoCurrency)currencyIterator.next();

			// No recalcula la moneda base
			if (bmoCurrency.getId() != systemCurrencyId) {

				printDevLog("Recuperando informacion de Monedas: " + bmoCurrency.getCode().toString() + 
						", Base Moneda Default: " + bmoSystemCurrency.getCode().toString());

				BmoCurrencyRate bmoCurrencyRate = new BmoCurrencyRate();

				// Si la conversion es entre USD y MXN, utiliza Banxico
				if ((bmoSystemCurrency.getCode().toString().equalsIgnoreCase("MXN")
						&& bmoCurrency.getCode().toString().equalsIgnoreCase("USD"))
						||
						(bmoSystemCurrency.getCode().toString().equalsIgnoreCase("USD")
								&& bmoCurrency.getCode().toString().equalsIgnoreCase("MXN")) ) {
					//					BanxicoExchange be = new BanxicoExchange();
					double usdMxnRate = 0;

					try {
						Response response = Series.readSeries();
						Serie serie = response.getBmx().getSeries().get(0);
						printDevLog("Serie: "+serie.getTitulo());
						for (DataSerie data:serie.getDatos()) {
							//Se omiten las observaciones sin dato (N/E)
							if(data.getDato().equals("N/E")) continue; 
							printDevLog("Fecha: "+ data.getFecha());
							printDevLog("Dato: "+ data.getDato());
							usdMxnRate = Double.parseDouble(data.getDato());
						}

					} catch(Exception e) {
						printDevLog("ERROR: No se obtuvo una respuesta de la API. Buscando el último Tipo de cambio en Flex.");
						// En caso de no encontrar un valor a retornar, buscar el ultimo registrado en el catalogo de monedas
						PmConn pmConn = new PmConn(getSFParams());
						pmConn.open();
						String sql = "SELECT " + bmoCurrencyRate.getRate().getName() 
								+ " FROM " + formatKind(bmoCurrencyRate.getKind())
								+ " ORDER BY " + bmoCurrencyRate.getDateTime().getName() + " " + BmOrder.DESC + " LIMIT 1;";
						printDevLog("sql:"+sql);

						pmConn.doFetch(sql);
						if (pmConn.next()) {
							usdMxnRate = pmConn.getDouble(bmoCurrencyRate.getRate().getName()) ;
						} else {
							success = false;
							errors += "\n No se obtuvo una respuesta de la API. No se encontro ningún Tipo de Cambio disponible.";
							printDevLog("ERROR: " + errors);
						}
						pmConn.close();
					}

					// La conversion es de MXN a USD
					if (bmoSystemCurrency.getCode().toString().equalsIgnoreCase("MXN")
							&& bmoCurrency.getCode().toString().equalsIgnoreCase("USD")) {
						bmoCurrencyRate.getRate().setValue(new DecimalFormat("#.####").format(usdMxnRate));
					} else {
						bmoCurrencyRate.getRate().setValue(new DecimalFormat("#.####").format(1 / usdMxnRate));
					}
				} else {
					try {
						// Toma valores de yahoo? // No esta probado de Yahoo
						bmoCurrencyRate.getRate().setValue(
								YahooCurrencyExchange.currencyExchange(
										bmoCurrency.getCode().toString(), 
										bmoSystemCurrency.getCode().toString(),
										1));
					} catch(Exception e) {
						success = false;
						errors += "\n" + this.getClass().getName() + " - updateCurrencyExchanges() - " + e.getMessage();
					}

					printDevLog("Tipo de Cambio recuperado: " + bmoCurrencyRate.getRate().toString());
				}

				bmoCurrencyRate.getCurrencyId().setValue(bmoCurrency.getId());

				// Solo la modifica si el resultado es mayor a 0
				if (bmoCurrencyRate.getRate().toDouble() > 0)  {
					pmCurrencyRate.save(bmoCurrencyRate, bmUpdateResult);
				}

				if (bmUpdateResult.hasErrors()) {
					success = false;
					errors += "\n" + bmUpdateResult.errorsToString();
				}

				if (!success) {
					printDevLog("Errores acumulados: "+ errors);
					sendEmailErrorLog(getSFParams(), errors);
				}
			}
		}
	}

	public static void sendEmailErrorLog(SFParams sFParams, String errors) throws SFException {

		// Enviar correo solicitando autorizacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String emailSupport = ""; 
		emailSupport = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getEmailFailCron().toString();
		// Existe un correo
		if (!emailSupport.equals("")) {
			mailList.add(new SFMailAddress(emailSupport, "Soporte"));

			String subject = "Error al Ejecutar Trabajo Cron: " + "CronCurrencyExchange";

			String msgBody = HtmlUtil.mailBodyFormat(
					sFParams, 
					"CronCurrencyExchange.updateCurrencyExchanges()", 
					"<b><i>Detalle del Error</i></b>: " + errors
					);

			SFSendMail.send(sFParams,
					mailList,  
					sFParams.getBmoSFConfig().getEmail().toString(), 
					sFParams.getBmoSFConfig().getAppTitle().toString(), 
					subject, 
					msgBody);
		}
		// Regresar con error
		throw new SFException("" + errors);
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		if (action.equals(BmoCurrency.ACTION_GETCURRENCYPARITY)) {			
			bmUpdateResult.setMsg("" + getParityFromCurrency(value, bmUpdateResult));
		}

		return bmUpdateResult;
	}

	//Obtener la paridad de la moneda
	public String getParityFromCurrency(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		int currencyId = 0;
		String startDate = "", sql = "";
		double parity = 0;

		try {

			StringTokenizer tabs = new StringTokenizer(value, "|");		
			while (tabs.hasMoreTokens()) {
				currencyId = Integer.parseInt(tabs.nextToken());
				startDate = tabs.nextToken();
			}

			//Si la moneda es difenrente a la del sistema obtener la paridad
			if (currencyId != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger()) {

				sql = " SELECT cura_rate from currencyrates " +
						" WHERE cura_currencyid = " + currencyId + 
						" AND cura_datetime >= '" + startDate.substring(0,10) + " 00:00'" + 
						" AND cura_datetime <= '" + startDate.substring(0,10) + " 23:59'";	

				pmConn.doFetch(sql);
				if (pmConn.next()) {
					parity = pmConn.getDouble("cura_rate");
				} else {
					sql = " SELECT cura_rate from currencyrates " +
							" WHERE cura_currencyid = " + currencyId +
							" AND cura_datetime < '" + startDate.substring(0,10) + " 00:00' " +
							" ORDER BY cura_datetime DESC";

					if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + 
							": getParityFromCurrency() - sql_DESC: " + sql);
					pmConn.doFetch(sql);					
					if (pmConn.next()) {
						parity = pmConn.getDouble("cura_rate");						
					} else {
						sql = " SELECT cura_rate from currencyrates " +
								" WHERE cura_currencyid = " + currencyId +
								" AND cura_datetime > '" + startDate.substring(0,10) + " 00:00' " +
								" ORDER BY cura_datetime ASC";

						if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + 
								": getParityFromCurrency() - sql_ASC: " + sql);
						pmConn.doFetch(sql);
						if (pmConn.next()) { 
							parity = pmConn.getDouble("cura_rate");							
						}	
					}
				}
			} else {
				//Si la moneda es igual a la del sistema la paridad es 1
				parity = 1;
			}
			printDevLog(this.getClass().getName() + 
					": getParityFromCurrency() - El Tipo de Cambio es: " + parity);

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error al traer el tipo de cambio de la moneda " + e.toString());
		} finally {
			pmConn.close();
		}	

		return "" + parity;		
	}

	// Conversion de una moneda a otra
	public double currencyExchange(double amount, int currencyIdOrigin, double currencyParityOrigin, int currencyIdDestiny, double currencyParityDestiny) {
		double currencyExchange = 0;
		// Si es la misma moneda regresar el mismo monto, en caso contrario se hace la conversion
		if (currencyIdOrigin == currencyIdDestiny) {
			currencyExchange = amount;
			//				currencyExchange = ((amount * currencyParityOrigin) / currencyParityOrigin);
		} else {
			currencyExchange = ((amount * currencyParityOrigin) / currencyParityDestiny);
		}
		return currencyExchange;
	}
}
