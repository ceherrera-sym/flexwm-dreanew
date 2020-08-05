/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.StringTokenizer;
import com.flexwm.server.FlexUtil;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmSessionTypePackage extends PmObject{
	BmoSessionTypePackage bmoSessionTypePackage;
	
	public PmSessionTypePackage(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoSessionTypePackage = new BmoSessionTypePackage();
		setBmObject(bmoSessionTypePackage); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSessionTypePackage.getSessionTypeId(), bmoSessionTypePackage.getBmoSessionType()),
				new PmJoin(bmoSessionTypePackage.getBmoSessionType().getCurrencyId(), bmoSessionTypePackage.getBmoSessionType().getBmoCurrency()),
				new PmJoin(bmoSessionTypePackage.getBmoSessionType().getSessionDisciplineId(), bmoSessionTypePackage.getBmoSessionType().getBmoSessionDiscipline())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSessionTypePackage = (BmoSessionTypePackage)autoPopulate(pmConn, new BmoSessionTypePackage());

		// BmoSessionType
		BmoSessionType bmoSessionType = new BmoSessionType();
		if (pmConn.getInt(bmoSessionType.getIdFieldName()) > 0) 
			bmoSessionTypePackage.setBmoSessionType((BmoSessionType) new PmSessionType(getSFParams()).populate(pmConn));
		else 
			bmoSessionTypePackage.setBmoSessionType(bmoSessionType);	
		
		return bmoSessionTypePackage;
	}
	
	
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		
		if (action.equals(BmoSessionTypePackage.ACTION_NOSESSION)) {
			bmUpdateResult = sessionByMonth(value, bmUpdateResult);
		} else if (action.equals(BmoSessionTypePackage.ACTION_COSTSESSION)) {
			bmUpdateResult = costSessionByMonth(value, bmUpdateResult);
		}
		
		return bmUpdateResult;
	}
	
	//Calcular las sesiones que tiene derecho por mes
	public BmUpdateResult sessionByMonth(String value, BmUpdateResult bmUpdateResult) throws SFException {
		double countSession = 0;
		int sessionByWeek = 0;
		int sessionTypePackageId = 0;
		String sesionSaleDate = "";
		printDevLog("Entro al metodo");
		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {			
			sessionTypePackageId = Integer.parseInt(tabs.nextToken());
			sesionSaleDate = tabs.nextToken();
		}	

		
		//Obtener las clases por semana
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(sessionTypePackageId);
		
		sessionByWeek = bmoSessionTypePackage.getSessions().toInteger();
		
		Calendar calToday = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), sesionSaleDate);
		Calendar lastDayMonth = Calendar.getInstance();
		
		//Obtener el día de hoy
		int today = calToday.get(Calendar.DAY_OF_MONTH); 
		
		//Obtener el ultimo dia del mes
		lastDayMonth = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), FlexUtil.getLastDateMonth(getSFParams(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()))); 
		int daysByMonth = lastDayMonth.get(Calendar.DAY_OF_MONTH);
		
		//Dias que restan del mes
		double remaining = daysByMonth - today;
		
		double weekByMonth = remaining / 7;
		//Redondear hacia arriba 
		weekByMonth = Math.ceil(weekByMonth);
		
		if (remaining > 0) {
			//Calcular las semanas por mes			
			if (weekByMonth == 1) {			
				//Numero de sesiones al mes
				if (sessionByWeek > remaining)
					countSession = remaining;
				else {					
					if (remaining > 0)
						countSession = sessionByWeek;					
					else
						countSession = 1;
				}	
			} else {
				//Numero de sesiones al mes		
				countSession = sessionByWeek * weekByMonth;
			}			
		} else {
			countSession = 1;
		}
		
		
		
		Double d = new Double(countSession);		
		bmUpdateResult.addMsg("" + d.intValue());		
		return  bmUpdateResult;
	}
	
	//Calcular el costo por mes
	public BmUpdateResult costSessionByMonth(String value, BmUpdateResult bmUpdateResult) throws SFException {
		int sessionTypePackageId = 0;
		String sesionSaleDate = "";
		
		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {			
			sessionTypePackageId = Integer.parseInt(tabs.nextToken());
			sesionSaleDate = tabs.nextToken();
		}
		
		//Obtener las clases por semana
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(sessionTypePackageId);
		
		Calendar calToday = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), sesionSaleDate);
		Calendar lastDayMonth = Calendar.getInstance();
		
		//Obtener el día de hoy
		int today = calToday.get(Calendar.DAY_OF_MONTH); 
		
		//Obtener el ultimo dia del mes
		lastDayMonth = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), FlexUtil.getLastDateMonth(getSFParams(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()))); 
		int daysByMonth = lastDayMonth.get(Calendar.DAY_OF_MONTH);
		
		//Dias que restan del mes
		double remaining = daysByMonth - today;
		
		double cost = bmoSessionTypePackage.getSalePrice().toDouble();
		
		if (today > 10)
			cost = remaining * (bmoSessionTypePackage.getSalePrice().toDouble() / 30);
		
		bmUpdateResult.addMsg("" + cost);
		
		return  bmUpdateResult;
	}
}

