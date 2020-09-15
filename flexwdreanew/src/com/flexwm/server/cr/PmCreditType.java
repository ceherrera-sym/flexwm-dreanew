/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cr;

import java.util.Calendar;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cr.BmoCreditType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoLocation;


/**
 * @author jhernandez
 *
 */

public class PmCreditType extends PmObject{
	BmoCreditType bmoCreditType;

	public PmCreditType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCreditType = new BmoCreditType();
		setBmObject(bmoCreditType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCreditType = (BmoCreditType)autoPopulate(pmConn, new BmoCreditType());		

		return bmoCreditType;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int locationUserId = getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger();

		// Filtro de Ubicaciones
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()
				&& getSFParams().restrictData(new BmoLocation().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " crty_locationid = " + locationUserId;
		}

		return filters;
	}
	
	// Revisar si es dia cobrable
	public boolean isPaymentDay(int dayOfWeek, BmoCreditType bmoCreditType) {
		boolean isPaymentDay = false;
		
		// Sunday
		if (dayOfWeek == 1) { 
			if (bmoCreditType.getPaymentDaySunday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Monday
		} else if (dayOfWeek == 2) { 
			if (bmoCreditType.getPaymentDayMonday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Tuesday
		} else if (dayOfWeek == 3) { 
			if (bmoCreditType.getPaymentDayTuesday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Wednesday
		} else if (dayOfWeek == 4) { 
			if (bmoCreditType.getPaymentDayWednesday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Thursday
		} else if (dayOfWeek == 5) { 
			if (bmoCreditType.getPaymentDayThursday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Friday
		} else if (dayOfWeek == 6) { 
			if (bmoCreditType.getPaymentDayFriday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		// Saturday
		} else if (dayOfWeek == 7) { 
			if (bmoCreditType.getPaymentDaySaturday().toBoolean()) isPaymentDay = true;
			else isPaymentDay = false;
		}
		
		return isPaymentDay;
	}
	
	// Regresar el ultimo dia cobrable ANTES/DESPUES  de la fecha
	public String getLastPaymentDay(String date, int dayOfWeek, BmoCreditType bmoCreditType, boolean previous) throws SFException {
		
		try {
			int daysPayout = 0;
			String datePayout = "";
			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				daysPayout = 1;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
				daysPayout = 7;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_TWOWEEKS)) {
				daysPayout = 15;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_MONTHLY)) {
				daysPayout = 30;
			}
			
			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				// Revisar si es dia cobrable
				if (isPaymentDay(dayOfWeek, bmoCreditType)) {
//					date = date;
				} else {
					if (previous)
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), date, -daysPayout);
					else 
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), date, +daysPayout);

					// Revisar si el dia anterior/siguiente es un dia cobrable
					Calendar nextDate = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), datePayout);
					int dayOfWeekNew = nextDate.get(Calendar.DAY_OF_WEEK);

					if (isPaymentDay(dayOfWeekNew, bmoCreditType)) {
						date = datePayout;
					} else {
						if (previous)
							getLastPaymentDay(datePayout, dayOfWeekNew, bmoCreditType, true);
						else
							getLastPaymentDay(datePayout, dayOfWeekNew, bmoCreditType, false);

					}
				}
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "getLastPaymentDay(): " + e.toString());
		} finally {
		}
		
		
		return date;
	}
}

