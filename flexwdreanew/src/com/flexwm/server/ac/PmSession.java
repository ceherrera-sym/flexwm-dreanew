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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

import com.symgae.shared.sf.BmoUser;


public class PmSession extends PmObject{
	BmoSession bmoSession;


	public PmSession(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoSession = new BmoSession();
		setBmObject(bmoSession); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSession.getSessionTypeId(), bmoSession.getBmoSessionType()),
				new PmJoin(bmoSession.getBmoSessionType().getCurrencyId(), bmoSession.getBmoSessionType().getBmoCurrency()),
				new PmJoin(bmoSession.getBmoSessionType().getSessionDisciplineId(), bmoSession.getBmoSessionType().getBmoSessionDiscipline()),
				new PmJoin(bmoSession.getUserId(), bmoSession.getBmoUser()),
				new PmJoin(bmoSession.getBmoUser().getAreaId(), bmoSession.getBmoUser().getBmoArea()),
				new PmJoin(bmoSession.getBmoUser().getLocationId(), bmoSession.getBmoUser().getBmoLocation()),
				new PmJoin(bmoSession.getCompanyId(), bmoSession.getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSession = (BmoSession)autoPopulate(pmConn, new BmoSession());

		// BmoSessionType
		BmoSessionType bmoSessionType = new BmoSessionType();
		if (pmConn.getInt(bmoSessionType.getIdFieldName()) > 0) 
			bmoSession.setBmoSessionType((BmoSessionType) new PmSessionType(getSFParams()).populate(pmConn));
		else 
			bmoSession.setBmoSessionType(bmoSessionType);	

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName()) > 0) 
			bmoSession.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else 
			bmoSession.setBmoUser(bmoUser);	
		
		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if (pmConn.getInt(bmoCompany.getIdFieldName()) > 0)
			bmoSession.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else
			bmoSession.setBmoCompany(bmoCompany);	

		return bmoSession;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";

		if (getSFParams().restrictData(bmoSession.getProgramCode())) {
			int loggedUserId = getSFParams().getLoginInfo().getUserId();

			// Filtro por asignacion de venta propiedads
			filters = "( sess_userid in (" +
					" SELECT user_userid from users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid in ( " +
					" SELECT u2.user_userid from users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u3.user_userid from users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u4.user_userid from users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 on (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u5.user_userid from users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 on (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 on (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					//					" OR " +
					//					" ( " +
					//					" sess_orderid IN ( " +
					//					" SELECT wflw_callerid FROM wflowusers  " +
					//					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					//					" WHERE wflu_userid = " + loggedUserId +
					//					" AND (wflw_callercode = '" + new BmoSessionSale().getProgramCode() + 
					//							"' OR wflw_callercode = '" + new BmoOrder().getProgramCode() + "') " + 
					//					"   ) " +
					//					" ) " +
					" ) ";
		}				
		return filters;
	}	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoSession = (BmoSession)bmObject;

		boolean newRecord = true;		
		if (bmoSession.getId() > 0) {
			newRecord = false;	
		} 

		// Ajusta el fin de la session de acuerdo al tipo de sesion
		PmSessionType pmSessionType = new PmSessionType(getSFParams());
		BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSession.getSessionTypeId().toInteger());
		bmoSession.setBmoSessionType(bmoSessionType);

		// Obtiene datos del usuario instructor
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = (BmoUser)pmUser.get(pmConn, bmoSession.getUserId().toInteger());
		bmoSession.setBmoUser(bmoUser);

		if (!bmoSession.getStartDate().toString().equals("")) {
			calculateEndDate(bmoSessionType, bmoSession, bmUpdateResult);
		}

		// Actualiza reservaciones de la sesion
		this.updateReservations(pmConn, bmoSession, bmUpdateResult);

		super.save(pmConn, bmoSession, bmUpdateResult);

		if (hasDuplicateSessions(pmConn, bmoSession, bmUpdateResult)) {
			bmUpdateResult.addError(bmoSession.getUserId().getName(), "El instrucctor tiene ocupado el horario ");
			return bmUpdateResult;
		}

		// Si es nuevo, y tiene creacion de series, ejecutarla
		if (!bmUpdateResult.hasErrors() && bmoSession.getIsSeries().toBoolean()) {
			if (newRecord) {
				bmoSession.getSeriesParentId().setValue(bmoSession.getId());
				createSeries(pmConn, bmoSessionType, bmoSession, bmUpdateResult);
			} else if (bmoSession.getSeriesApplyAll().toBoolean()) 
				updateSeries(pmConn, bmoSessionType, bmoSession, bmUpdateResult); 
		}

		// El campo de aplicar a todos es temporal
		bmoSession.getSeriesApplyAll().setValue(false);

		// Actualiza calendario Google
		if (!bmUpdateResult.hasErrors()) {			
			// Calendario general del tipo de sesion
			updateGoogleCalendar(pmConn, bmoSession, bmUpdateResult);

			// Calendario google del instructor
			updateUserGEvent(pmConn, bmoSession, bmUpdateResult);
		}

		// Ultimo guardado de cambios
		super.save(pmConn, bmoSession, bmUpdateResult);

		return bmUpdateResult;
	}

	// Calcula en fin de la sesion de acuerdo al inicio y a la duracion del tipo de sesion
	private void calculateEndDate(BmoSessionType bmoSessionType, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
		try {
			Date startDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(bmoSession.getStartDate().toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.MINUTE, bmoSessionType.getDuration().toInteger());

			bmoSession.getEndDate().setValue(new SimpleDateFormat(getSFParams().getDateTimeFormat()).format(cal.getTime()));
		} catch (ParseException e) {
			bmUpdateResult.addError(bmoSession.getStartDate().getName(), "Error al Asignar Fecha: " + e.toString());
		}
	}

	//Actualizar el numero de viviendas de la etapa
	public void updateReservations(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
	String sql = "";
		printDevLog("updateReservations_bmoSession.getCompanyId() : "+ bmoSession.getCompanyId().toInteger());
		int reservations = 0;
		sql = " SELECT COUNT(*) AS reservations FROM ordersessions " +
				" WHERE orss_sessionid = " + bmoSession.getId();
		pmConn.doFetch(sql);
		if (pmConn.next())
			reservations = pmConn.getInt("reservations");

		bmoSession.getReservations().setValue(reservations);

		// Obten el tipo de sesion
		PmSessionType pmSessionType = new PmSessionType(getSFParams());
		BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSession.getSessionTypeId().toInteger());

		// Actualiza disponibilidad
		if (bmoSession.getReservations().toInteger() < bmoSessionType.getCapacity().toInteger()) {
			bmoSession.getAvailable().setValue("1");
		} else {			
			bmoSession.getAvailable().setValue("0");
		}	

		super.save(pmConn, bmoSession, bmUpdateResult);
	}

	// Revisa que el responsable no tenga ya una sesion en el mismo horario
	private boolean hasDuplicateSessions(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int sessionCount = 0;

		// La sentence SQL busca fechas duplicadas descontando los horarios iguales
		sql = "SELECT COUNT(sess_sessionid) as sessioncount FROM sessions"
				+ " WHERE sess_userid = " + bmoSession.getUserId().toInteger()
				+ " AND sess_sessionid <> " + bmoSession.getId()
				+ " AND ("
				+ "			("
				+ "				('" + bmoSession.getStartDate().toString() + "' > sess_startdate AND '" + bmoSession.getStartDate().toString() + "' < sess_enddate) "
				+ "				OR"
				+ "				('" + bmoSession.getEndDate().toString() + "' > sess_startdate AND '" + bmoSession.getEndDate().toString() + "' < sess_enddate) "
				+ "			)"
				+ " 		OR (	"
				+ "				sess_startdate  = '" + bmoSession.getStartDate().toString() + "' "
				+ "				AND "
				+ "				sess_enddate = '" + bmoSession.getEndDate().toString() + "' "
				+ "			)"
				+ " 		OR (	"
				+ "				sess_startdate  >= '" + bmoSession.getStartDate().toString() + "' "
				+ "				AND "
				+ "				sess_enddate <= '" + bmoSession.getEndDate().toString() + "' "
				+ "			)"
				+ "		) ";

		pmConn.doFetch(sql);
		if (pmConn.next())
			sessionCount = pmConn.getInt("sessioncount");

		// Valida conteo de sesiones
		if (sessionCount > 0) {			
			return true;
		} else { 
			return false;
		}	
	}

	//	private void usedDuplicateSessions(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
	//		String sql = "";
	//		//		int sessionCount = 0;
	//
	//		sql = "SELECT * FROM sessions"
	//				+ " WHERE sess_userid = " + bmoSession.getUserId().toInteger()
	//				+ " AND sess_sessionid <> " + bmoSession.getId()
	//				+ " AND ("
	//				+ "			("
	//				+ "				('" + bmoSession.getStartDate().toString() + "' > sess_startdate AND '" + bmoSession.getStartDate().toString() + "' < sess_enddate) "
	//				+ "				OR"
	//				+ "				('" + bmoSession.getEndDate().toString() + "' > sess_startdate AND '" + bmoSession.getEndDate().toString() + "' < sess_enddate) "
	//				+ "			)"
	//				+ " 		OR (	"
	//				+ "				sess_startdate  = '" + bmoSession.getStartDate().toString() + "' "
	//				+ "				AND "
	//				+ "				sess_enddate = '" + bmoSession.getEndDate().toString() + "' "
	//				+ "			)"
	//				+ " 		OR (	"
	//				+ "				sess_startdate  >= '" + bmoSession.getStartDate().toString() + "' "
	//				+ "				AND "
	//				+ "				sess_enddate <= '" + bmoSession.getEndDate().toString() + "' "
	//				+ "			)"
	//				+ "		) ";
	//
	//		pmConn.doFetch(sql);
	//		if (pmConn.next())  {
	//			if (pmConn.getInt("sess_available") > 0) {
	//				if (bmoSession.getSeriesParentId().toInteger() > 0) {				
	//					BmoSession bmoSessParent = (BmoSession)this.get(bmoSession.getSeriesParentId().toInteger());
	//					if (bmoSessParent.getAutoAssign().toBoolean()) {
	//						//autoAssignSession(value, bmUpdateResult);
	//					}
	//				}
	//			}	
	//		}
	//
	//	}

	// Crea o actualiza serie repetida de sesiones
	private void createSeries(PmConn pmConn, BmoSessionType bmoSessionType, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
		Date seriesStartDate, seriesEndDate, currentSeriesDate;

		try {		
			seriesStartDate = new SimpleDateFormat(getSFParams().getDateFormat()).parse(bmoSession.getSeriesStart().toString());
			seriesEndDate = new SimpleDateFormat(getSFParams().getDateFormat()).parse(bmoSession.getSeriesEnd().toString());
			currentSeriesDate = seriesStartDate;

		} catch (ParseException e) {
			throw new SFException(this.getClass().getName() + "-createSeries(): " + e.toString());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(seriesStartDate);

		// Mientras sea igual o menor
		while (currentSeriesDate.before(seriesEndDate) || currentSeriesDate.equals(seriesEndDate)) {

			Calendar c = Calendar.getInstance();
			c.setTime(currentSeriesDate);

			// Revisar si aplica el dia
			if (seriesDayApplies(bmoSession, c.get(Calendar.DAY_OF_WEEK))) {

				BmoSession newBmoSession = new BmoSession();
				newBmoSession.getDescription().setValue(bmoSession.getDescription().toString());
				newBmoSession.getReservations().setValue(0);
				newBmoSession.getAvailable().setValue(1);
				newBmoSession.getUserId().setValue(bmoSession.getUserId().toString());
				newBmoSession.getSessionTypeId().setValue(bmoSession.getSessionTypeId().toString());
				newBmoSession.getCompanyId().setValue(bmoSession.getCompanyId().toInteger());

				newBmoSession.getIsSeries().setValue(bmoSession.getIsSeries().toString());
				newBmoSession.getSeriesStart().setValue(bmoSession.getSeriesStart().toString());
				newBmoSession.getSeriesEnd().setValue(bmoSession.getSeriesEnd().toString());
				newBmoSession.getSeriesParentId().setValue(bmoSession.getId());
				newBmoSession.getSeriesMonday().setValue(bmoSession.getSeriesMonday().toString());
				newBmoSession.getSeriesTuesday().setValue(bmoSession.getSeriesTuesday().toString());
				newBmoSession.getSeriesWednesday().setValue(bmoSession.getSeriesWednesday().toString());
				newBmoSession.getSeriesThursday().setValue(bmoSession.getSeriesThursday().toString());
				newBmoSession.getSeriesFriday().setValue(bmoSession.getSeriesFriday().toString());
				newBmoSession.getSeriesSaturday().setValue(bmoSession.getSeriesSaturday().toString());
				newBmoSession.getSeriesSunday().setValue(bmoSession.getSeriesSunday().toString());

				// El campo de aplicar a todos es temporal
				newBmoSession.getSeriesApplyAll().setValue(false);

				// Calcula el inicio y fin 
				newBmoSession.getStartDate().setValue(mergeDates(bmoSession.getStartDate().toString(), currentSeriesDate));
				calculateEndDate(bmoSessionType, newBmoSession, bmUpdateResult);

				// Si no tiene duplicados, la genera
				if (!hasDuplicateSessions(pmConn, newBmoSession, bmUpdateResult))
					super.save(pmConn,  newBmoSession, bmUpdateResult);



			}

			// Agrega un dia
			cal.add(Calendar.MINUTE, 1440);
			currentSeriesDate = cal.getTime();
		}


	}

	// Crea o actualiza serie repetida de sesiones
	private void updateSeries(PmConn pmConn, BmoSessionType bmoSessionType, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {

		BmFilter filterByParent = new BmFilter();
		filterByParent.setValueFilter(bmoSession.getKind(), bmoSession.getSeriesParentId(), bmoSession.getSeriesParentId().toInteger());
		Iterator<BmObject> seriesIterator = this.list(pmConn, filterByParent).iterator();

		while (seriesIterator.hasNext()) {
			BmoSession currentBmoSession = (BmoSession)seriesIterator.next();

			// Solo modifica sesiones que nos sean igual a la actual
			if (currentBmoSession.getId() != bmoSession.getId()) {

				// Solo modifica la sesion si no tiene reservaciones
				if (!(currentBmoSession.getReservations().toInteger() > 0)) {

					// Obtiene fecha de inicio de la sesion actual
					Date currentSessionStartDate;
					try {
						currentSessionStartDate = new SimpleDateFormat(getSFParams().getDateFormat()).parse(currentBmoSession.getStartDate().toString());
					} catch (ParseException e) {
						throw new SFException(this.getClass().getName() + "-updateSeries(): " + e.toString());
					}
					Calendar c = Calendar.getInstance();
					c.setTime(currentSessionStartDate);

					// Revisar si aplica el dia
					if (seriesDayApplies(bmoSession, c.get(Calendar.DAY_OF_WEEK))) {

						currentBmoSession.getDescription().setValue(bmoSession.getDescription().toString());
						currentBmoSession.getUserId().setValue(bmoSession.getUserId().toString());
						currentBmoSession.getSessionTypeId().setValue(bmoSession.getSessionTypeId().toString());
						currentBmoSession.getCompanyId().setValue(bmoSession.getCompanyId().toInteger());						

						currentBmoSession.getIsSeries().setValue(bmoSession.getIsSeries().toString());
						currentBmoSession.getSeriesStart().setValue(bmoSession.getSeriesStart().toString());
						currentBmoSession.getSeriesEnd().setValue(bmoSession.getSeriesEnd().toString());
						currentBmoSession.getSeriesParentId().setValue(bmoSession.getId());
						currentBmoSession.getSeriesMonday().setValue(bmoSession.getSeriesMonday().toString());
						currentBmoSession.getSeriesTuesday().setValue(bmoSession.getSeriesTuesday().toString());
						currentBmoSession.getSeriesWednesday().setValue(bmoSession.getSeriesWednesday().toString());
						currentBmoSession.getSeriesThursday().setValue(bmoSession.getSeriesThursday().toString());
						currentBmoSession.getSeriesFriday().setValue(bmoSession.getSeriesFriday().toString());
						currentBmoSession.getSeriesSaturday().setValue(bmoSession.getSeriesSaturday().toString());
						currentBmoSession.getSeriesSunday().setValue(bmoSession.getSeriesSunday().toString());

						// El campo de aplicar a todos es temporal
						currentBmoSession.getSeriesApplyAll().setValue(false);

						// Calcula el inicio y fin 
						currentBmoSession.getStartDate().setValue(mergeDates(bmoSession.getStartDate().toString(), currentSessionStartDate));
						calculateEndDate(bmoSessionType, currentBmoSession, bmUpdateResult);

						// Si no tiene duplicados, la genera
						if (!hasDuplicateSessions(pmConn, currentBmoSession, bmUpdateResult))
							super.save(pmConn,  currentBmoSession, bmUpdateResult);

					} else {
						// Elimina la sesion, esta en un dia no seleccionado
						super.delete(pmConn, currentBmoSession, bmUpdateResult);
					}
				}
			}
		}
	}	

	// Calcula en fin de la sesion de acuerdo al inicio y a la duracion del tipo de sesion
	private String mergeDates(String fullDateString, Date date) throws SFException {
		String resultDateString;
		try {
			Date fullDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(fullDateString);
			Calendar fullCal = Calendar.getInstance();
			fullCal.setTime(fullDate);

			Calendar dateCal = Calendar.getInstance();
			dateCal.setTime(date);

			fullCal.set(dateCal.get(Calendar.YEAR), dateCal.get(Calendar.MONTH), dateCal.get(Calendar.DAY_OF_MONTH));

			resultDateString = new SimpleDateFormat(getSFParams().getDateTimeFormat()).format(fullCal.getTime());

			return resultDateString;
		} catch (ParseException e) {
			throw new SFException(this.getClass().getName() + "-mergeDates(): " + e.toString());
		}
	}

	// Revisa si aplica generar la sesion en el dia especifico
	private boolean seriesDayApplies(BmoSession bmoSession, int day) {

		if (day == 1 && bmoSession.getSeriesSunday().toBoolean()) return true; 
		else if (day == 2 && bmoSession.getSeriesMonday().toBoolean()) return true; 
		else if (day == 3 && bmoSession.getSeriesTuesday().toBoolean()) return true; 
		else if (day == 4 && bmoSession.getSeriesWednesday().toBoolean()) return true; 
		else if (day == 5 && bmoSession.getSeriesThursday().toBoolean()) return true; 
		else if (day == 6 && bmoSession.getSeriesFriday().toBoolean()) return true; 
		else if (day == 7 && bmoSession.getSeriesSaturday().toBoolean()) return true; 

		return false;
	}



	// Metodo para crear evento de calendario Google
	private void updateGoogleCalendar(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {

		System.out.println("Iniciando actualización del Calendario");

		/*
		// Actualiza calendario google
		try {
			
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
			bmoSession.getGoogleEventId().setValue(
					gCalendar.updateEvent(bmoSession.getBmoSessionType().getgCalendarId().toString(), 
							bmoSession.getGoogleEventId().toString(), 
							bmoSession.getBmoSessionType().getName().toString(), 
							bmoSession.getBmoSessionType().getName().toString() + ", " + bmoSession.getBmoUser().getCode().toString(), 
							"",
							bmoSession.getStartDate().toString(), 
							bmoSession.getEndDate().toString()));
			

		} catch (SFException e) {
			bmUpdateResult.addMsg(this.getClass().getName() + "-updateGoogleCalendar(): Revisar que este asignado el Usuario de Calendario Google a nivel Sistema en Configuración SYMGF: " + e.toString());
		}
		*/
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado."); 
	}

	// Actualiza el evento del instructor calendario google
	private void updateUserGEvent(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		// Actualiza calendario google personal del usuario si tiene id de google
		if (!bmoSession.getBmoUser().getGoogleid().toString().equals("")) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), bmoSession.getBmoUser().getGoogleid().toString());
			bmoSession.getUserGoogleEventId().setValue(
					gCalendar.updateEvent("",
							bmoSession.getUserGoogleEventId().toString(), 
							bmoSession.getBmoSessionType().getName().toString(), 
							bmoSession.getBmoSessionType().getName().toString() + ", " + bmoSession.getBmoUser().getCode().toString(), 
							"",
							bmoSession.getStartDate().toString(), 
							bmoSession.getEndDate().toString()));
		}
		*/
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		if (action.equals(BmoSession.ACTION_CHECKDATES)) {
			bmUpdateResult = checkDates(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_NEWSESSIONSALE)) {
			bmUpdateResult = createNewSessionSale((BmoSession)bmObject, value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_SESSNOTATTENDED)) {
			StringTokenizer tabs = new StringTokenizer(value, "|");
			String date = "";
			while (tabs.hasMoreTokens()) {
				value = tabs.nextToken();
				date = tabs.nextToken();
			}
			bmUpdateResult = sessionNotAttended(value, date, bmUpdateResult);		 
		} else if (action.equals(BmoSession.ACTION_NEWSESSIONCUST)) {
			bmUpdateResult = createNewSessionCust(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_GETORDER)) {
			bmUpdateResult = getOrder(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_CHECKSESSIONTIME)) {
			bmUpdateResult = checkSessionTime(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_CHECKSINGUP)) {
			bmUpdateResult = checkSingUp(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_GETSINGUP)) {
			bmUpdateResult = getSingUp(value, bmUpdateResult);
		} else if (action.equals(BmoSession.ACTION_NEWSESSIONWITHSALE)) {
			bmUpdateResult = createNewSessionWithSale((BmoSession)bmObject, value, bmUpdateResult);
		}

		return bmUpdateResult;
	}


	private BmUpdateResult getSingUp(String value, BmUpdateResult bmUpdateResult) throws SFException {
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String inscriptionDateStart = "";
		String inscriptionDateEnd = "";
		String inscriptionDate = "";
		try {
			//Obtener la fecha de inscripción venta de sesión
			sql = " SELECT sesa_inscriptiondate AS startdate FROM sessionsales " +
					" WHERE sesa_customerid = " + value +
					" ORDER BY sesa_startdate DESC LIMIT 1";
			pmConn.doFetch(sql);			
			if (pmConn.next()) {				
				inscriptionDateStart = pmConn.getString("startdate");
			}	

			//Sumar un año
			if (!inscriptionDateStart.equals("")) {
				Calendar now = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), inscriptionDateStart);
				now.add(Calendar.YEAR, 1);
				inscriptionDateEnd = FlexUtil.calendarToString(getSFParams(), now);
			}

			if(!(inscriptionDateStart.equals("") && inscriptionDateEnd.equals(""))) {
				String cal = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());

				sql = " SELECT sesa_inscriptiondate AS startdate FROM sessionsales " +
						" WHERE sesa_customerid = " + value +
						" AND '"+cal +"' between '"+inscriptionDateStart+"' AND '"+inscriptionDateEnd +"'"+
						" ORDER BY sesa_startdate DESC LIMIT 1";

				pmConn.doFetch(sql);
				if (pmConn.next()) {				
					inscriptionDate = pmConn.getString("startdate");
				}	
			}
			bmUpdateResult.addMsg(inscriptionDate);

		} catch (SFException e) {
			bmUpdateResult.addError(bmoSessionSale.getInscriptionDate().getName(), "Error al Obtener la inscripción: " + bmUpdateResult.errorsToString() + " " + e);
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	//Obtener la inscripcion
	private BmUpdateResult checkSingUp(String value, BmUpdateResult bmUpdateResult) throws SFException {
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String inscriptionDate = "";
		try {
			//Obtener la fecha de inscripción venta de sesión
			sql = " SELECT sesa_inscriptiondate AS startdate FROM sessionsales " +
					" WHERE sesa_customerid = " + value +
					" ORDER BY sesa_startdate DESC";
			pmConn.doFetch(sql);			
			if (pmConn.next()) {				
				inscriptionDate = pmConn.getString("startdate");
			}	



			//Sumar un año
			if (!inscriptionDate.equals("")) {
				Calendar now = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), inscriptionDate);
				now.add(Calendar.YEAR, 1);
				inscriptionDate = FlexUtil.calendarToString(getSFParams(), now);
			}

			bmUpdateResult.addMsg(inscriptionDate);

		} catch (SFException e) {
			bmUpdateResult.addError(bmoSessionSale.getInscriptionDate().getName(), "Error al Obtener la inscripción" + bmUpdateResult.errorsToString() + " " + e);
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	//Validar la fecha de la session
	private BmUpdateResult checkSessionTime(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		BmoOrderType bmoOrderType = new BmoOrderType();
		bmoOrderType = (BmoOrderType)pmOrderType.getBy("" + BmoOrderType.TYPE_SESSION, bmoOrderType.getType().getName());
		if (bmoOrderType != null) {

			Date startDate = null;
			Date checkDate = null;
			Date checkDateEnd = null;
			try {
				startDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(value);
				String checkDateString = value.substring(0,10) + " " + bmoOrderType.getScheduleStart().toInteger() + ":00";
				checkDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(checkDateString);

				checkDateString = value.substring(0,10) + " " + bmoOrderType.getScheduleEnd().toInteger() + ":00";
				checkDateEnd = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(checkDateString);

			} catch (ParseException e) {
				System.out.println(this.getClass().getName() + "-checkSessionTime() ERROR: " + e.toString());
			}
			Calendar dateSess = Calendar.getInstance();
			dateSess.setTime(startDate);

			Calendar checkStart = Calendar.getInstance();
			checkStart.setTime(checkDate);

			Calendar checkEnd = Calendar.getInstance();
			checkEnd.setTime(checkDateEnd);

			//Validar el horario

			if (dateSess.getTime().after(checkStart.getTime())) {				
				if (!dateSess.getTime().before(checkEnd.getTime())) {
					bmUpdateResult.setMsg("El horario finaliza a las " + bmoOrderType.getScheduleEnd().toInteger() + " Hrs.");
				}				
			} else {
				if (dateSess.compareTo(checkStart) != 0)
					bmUpdateResult.setMsg("El horario inicia a partir de las " + bmoOrderType.getScheduleStart().toInteger() + " Hrs.");
			} 


		} else {
			bmUpdateResult.setMsg("No existe el tipo de pedido de sesión");
		}


		return bmUpdateResult;
	}

	//Obtener el pedido mediante la venta de sessión
	private BmUpdateResult getOrder(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());		
		BmoSessionSale bmoSessionSale = (BmoSessionSale)pmSessionSale.get(Integer.parseInt(value));

		bmUpdateResult.setMsg(bmoSessionSale.getOrderId().toString());

		return bmUpdateResult;
	}


	// Crear venta de sesión
	private BmUpdateResult createNewSessionSale(BmoSession bmoSession, String value, BmUpdateResult bmUpdateResult) throws SFException {
		int customerId = 0;	
		int wflowTypeId = 0;
		String name = "";
		String startDateSessionSale = "";
		String inscriptionDateSessionSale = "";
		int sessionTypePackegeId = 0;
		int noSessions = 0;
		double costSessions = 0;
		int companyid = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());

		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {			
			customerId = Integer.parseInt(tabs.nextToken());
			name = tabs.nextToken();
			startDateSessionSale = tabs.nextToken();
			inscriptionDateSessionSale = tabs.nextToken();
			wflowTypeId = Integer.parseInt(tabs.nextToken());
			sessionTypePackegeId = Integer.parseInt(tabs.nextToken());
			noSessions = Integer.parseInt(tabs.nextToken());
			costSessions = Double.parseDouble(tabs.nextToken());
			companyid = Integer.parseInt(tabs.nextToken());
		}

		try {
			String sql = "";
			String inscriptionDate = inscriptionDateSessionSale;
			int enableSession = 0;

			pmConn.disableAutoCommit();

			//Obtener la fecha de inscripción venta de sesión
			sql = " SELECT sesa_sessionsaleid, sesa_inscriptiondate AS startdate, sesa_nosession FROM sessionsales " +
					" WHERE sesa_customerid = " + customerId +
					" ORDER BY sesa_startdate DESC";
			pmConn.doFetch(sql);				
			if (pmConn.next()) {				
				BmoSessionSale  bmoSessionSale = (BmoSessionSale)pmSessionSale.get(pmConn.getInt("sesa_sessionsaleid"));
				enableSession = this.sessionNotAttended(pmConn, bmoSessionSale, true, bmUpdateResult);
				noSessions += enableSession;
			}	

			PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
			BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(sessionTypePackegeId);

			BmoSessionSale bmoSessionSaleNew = new BmoSessionSale();
			bmoSessionSaleNew.getWFlowTypeId().setValue(wflowTypeId);
			bmoSessionSaleNew.getSessionTypePackageId().setValue(sessionTypePackegeId);
			bmoSessionSaleNew.getCustomerId().setValue(customerId);
			bmoSessionSaleNew.getName().setValue(name);
			bmoSessionSaleNew.getStartDate().setValue(startDateSessionSale);	
			bmoSessionSaleNew.getInscriptionDate().setValue(inscriptionDate);
			bmoSessionSaleNew.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
			// Las sesiones de mes, mas las pendientes de la venta anterior
			bmoSessionSaleNew.getMaxSessions().setValue(noSessions);
			bmoSessionSaleNew.getNoSession().setValue(noSessions);
			bmoSessionSaleNew.getCompanyId().setValue(companyid);
			// Obtener la Moneda y la paridad del Tipo
			bmoSessionSaleNew.getCurrencyId().setValue(bmoSessionTypePackage.getBmoSessionType().getCurrencyId().toInteger());
			bmoSessionSaleNew.getCurrencyParity().setValue(bmoSessionTypePackage.getBmoSessionType().getBmoCurrency().getParity().toDouble());

			pmSessionSale.save(pmConn, bmoSessionSaleNew, bmUpdateResult);
			int sessionSaleId = bmUpdateResult.getId();

			// Obtener Venta de Sesión
			bmoSessionSaleNew = (BmoSessionSale)pmSessionSale.get(pmConn, sessionSaleId);

			// Modificar el costo de la session
			PmOrderSessionTypePackage pmOrderSessTP = new PmOrderSessionTypePackage(getSFParams());
			BmoOrderSessionTypePackage bmoOrderSessTP = new BmoOrderSessionTypePackage();
			bmoOrderSessTP = (BmoOrderSessionTypePackage)pmOrderSessTP.getBy(pmConn, bmoSessionSaleNew.getOrderId().toInteger(), bmoOrderSessTP.getOrderId().getName());
			bmoOrderSessTP.getPrice().setValue(costSessions);
			pmOrderSessTP.save(pmConn, bmoOrderSessTP, bmUpdateResult);

			bmUpdateResult.setId(sessionSaleId);

			// Genera las asistencias a las sesiones existentes, o las crea
			createAllOrderSessions(pmConn, bmoSessionSaleNew, bmoSession, bmUpdateResult);

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

			bmUpdateResult.setId(sessionSaleId);

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addMsg("Error al Crear la Venta de Sesión. " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Crea todas las asistencias a sesiones, segun fechas elegidas
	public void createAllOrderSessions(PmConn pmConn, BmoSessionSale bmoSessionSale, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog("Iniciando recorrido de todos los dias de sesiones.");

		PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());

		PmSessionType pmSessionType = new PmSessionType(getSFParams());
		BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSession.getSessionTypeId().toInteger());

		// Recorre cada día de las sesiones, si no encuentra sesion, la crea
		Date currentDate = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(), bmoSession.getStartDate().toString());
		Date lastDate = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(), bmoSession.getStartDate().toString());

		Calendar c = Calendar.getInstance();
		c.setTime(lastDate);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		lastDate = c.getTime();

		printDevLog("Fecha actual : " + SFServerUtil.dateToString(currentDate, getSFParams().getDateTimeFormat()));
		printDevLog("Fecha final : " + SFServerUtil.dateToString(lastDate, getSFParams().getDateTimeFormat()));	

		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
		PmSession pmSession = new PmSession(getSFParams());
		int countSession = 0;
		while (currentDate.getTime() <= lastDate.getTime()) {
			printDevLog("Iniciando revisión de sesiones en el día: " + SFServerUtil.dateToString(currentDate, getSFParams().getDateTimeFormat()));
			printDevLog("countSession: "+countSession);
			printDevLog("MaxSessions: "+bmoSessionSale.getMaxSessions().toInteger() );

			// Crear sesiones de acuerdo a las que tiene la venta
			if (countSession < bmoSessionSale.getMaxSessions().toInteger() 
					&& bmoSessionSale.getMaxSessions().toInteger() > 0) {

				// Obtiene el dia de la semana de la fecha y revisa que este solicitado
				c = Calendar.getInstance();
				c.setTime(currentDate);
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				if ((dayOfWeek == 2 && bmoSession.getSeriesMonday().toBoolean())
						|| (dayOfWeek == 3 && bmoSession.getSeriesTuesday().toBoolean())
						|| (dayOfWeek == 4 && bmoSession.getSeriesWednesday().toBoolean())
						|| (dayOfWeek == 5 && bmoSession.getSeriesThursday().toBoolean())
						|| (dayOfWeek == 6 && bmoSession.getSeriesFriday().toBoolean())
						|| (dayOfWeek == 7 && bmoSession.getSeriesSaturday().toBoolean())
						|| (dayOfWeek == 1 && bmoSession.getSeriesSunday().toBoolean())) {

					// Revisa si hay sesion en la fecha 
					BmoSession newBmoSession = findSession(pmConn, bmoSession.getUserId().toInteger(), bmoSession.getSessionTypeId().toInteger(), currentDate,bmoSessionSale.getCompanyId().toInteger());

					// Existe Sesion? si no crearla
					if (!(newBmoSession.getId() > 0)) {
						printDevLog("No encontro sesion: ->" + newBmoSession.getStartDate().toString() + "<-");
						newBmoSession.getUserId().setValue(bmoSession.getUserId().toInteger());
						newBmoSession.getSessionTypeId().setValue(bmoSession.getSessionTypeId().toInteger());
						newBmoSession.getStartDate().setValue(SFServerUtil.dateToString(currentDate, getSFParams().getDateTimeFormat()));
						newBmoSession.getCompanyId().setValue(bmoSessionSale.getCompanyId().toInteger());
						calculateEndDate(bmoSessionType, newBmoSession, bmUpdateResult);
						this.saveSimple(pmConn, newBmoSession, bmUpdateResult);
					}

					// Crea la asistencia
					BmoOrderSession bmoOrderSession = new BmoOrderSession();
					bmoOrderSession.getOrderId().setValue(bmoSessionSale.getOrderId().toInteger());
					//valida que la empresa de la sesion sea igual a la empresa en la que pertence el niño
					if(newBmoSession.getCompanyId().toInteger() ==  bmoSessionSale.getCompanyId().toInteger()) 
						bmoOrderSession.getSessionId().setValue(newBmoSession.getId());
					else {
						bmUpdateResult.addError(bmoSessionSale.getCompanyId().getLabel(), "La empresa es diferente a la empresa de sesión");
						break;
					}
					if(!bmUpdateResult.hasErrors()) {
					bmoOrderSession.getAttended().setValue(1);
					pmOrderSession.saveSimple(pmConn,  bmoOrderSession, bmUpdateResult);
					// Actualiza cantidad de sesiones disponibles
					pmSessionSale.updateCountingSessions(pmConn, bmoSessionSale, bmUpdateResult);
					printDevLog("Vta actualizada: "+bmoSessionSale.getNoSession());
					countSession = bmoSessionSale.getNoSession().toInteger();

					// Actualiza reservaciones de la sesion
					pmSession.updateReservations(pmConn, newBmoSession, bmUpdateResult);
					}
				}
			}
			// Suma 1 dia
			c = Calendar.getInstance(); 
			c.setTime(currentDate); 
			c.add(Calendar.DATE, 1);
			currentDate = c.getTime();
		}
	}

	// Encuentra una sesion por usuario 
	private BmoSession findSession(PmConn pmConn, int userId, int sessionTypeId, Date date,int companyid) throws SFException {
		BmoSession newBmoSession;

		String sql = "SELECT * FROM " + formatKind("sessions") + " "
				+ " WHERE sess_userid = " + userId + " "
				+ " AND sess_startdate = '" + SFServerUtil.dateToString(date, getSFParams().getDateTimeFormat()) + "'"
				+ " AND sess_companyid = "+ companyid;

		printDevLog(sql);

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			newBmoSession = (BmoSession)get(pmConn.getInt("sess_sessionid"));

			if (newBmoSession.getSessionTypeId().toInteger() == sessionTypeId) {
				return newBmoSession;
			} else {
				throw new SFException("Error al Asignar Sesion: En La fecha " + SFServerUtil.dateToString(date, getSFParams().getDateTimeFormat()) + ""
						+ " ya existe otra Sesión del mismo Instructor, pero con distinto Tipo de Sesión");
			}
		}

		return new BmoSession();
	}

	private BmUpdateResult createNewSessionCust(String value, BmUpdateResult bmUpdateResult) throws SFException {
		StringTokenizer tabs = new StringTokenizer(value, "|");

		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());

		String sql = "", assist = "", startDateSession = "";
		int userId = 0, sessionSaleId = 0;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		while (tabs.hasMoreTokens()) {
			userId = Integer.parseInt(tabs.nextToken());
			startDateSession = tabs.nextToken();			
			assist = tabs.nextToken();
			sessionSaleId = Integer.parseInt(tabs.nextToken());
		}

		// Obtener la venta de session
		//		sql = " SELECT sesa_sessionsaleid FROM sessionsales " +
		//				" WHERE sesa_sesionsaleid > 0 " +
		//				" AND sesa_sessionsaleid = " + sessionSaleId +
		//				" ORDER BY sesa_startdate DESC";
		//		pmConn.doFetch(sql);
		//if (pmConn.next()) {

			try {
				
				// Viene de una venta
				if (sessionSaleId > 0) {
					if (userId > 0) {
						if (!startDateSession.equals("")) {
							bmoSessionSale = (BmoSessionSale)pmSessionSale.get(pmConn, sessionSaleId);
							
							pmConn.disableAutoCommit();
			
							// Existe una sesion con el intructor a la misma hora
							sql = " SELECT * FROM sessions " +
									" WHERE sess_userid = " + userId +
									" AND sess_sessiontypeid = " + bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger() +
									" AND sess_startdate = '" + startDateSession + "'";
							pmConn.doFetch(sql);				
							if (pmConn.next()) {
								// asignar el cliente a la sesion						
								BmoSession bmoSession = (BmoSession)this.get(pmConn, pmConn.getInt("sess_sessionid"));
			
								// Alumnos por clase
								PmSessionType pmSessionType = new PmSessionType(getSFParams());
								BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSession.getSessionTypeId().toInteger());
			
								// Numero de Alumnos
								if (bmoSession.getReservations().toInteger() >= bmoSessionType.getCapacity().toInteger()) {
									bmUpdateResult.addMsg("El cupo esta completo");
			
								} else {
									// Asignar el cliente a la sesion						
									bmUpdateResult = autoAssignSession(pmConn, bmoSession, bmoSessionSale, assist, bmUpdateResult);
								}	
			
							} else {
								// Crear la sesion
								PmSession pmSessionNew = new PmSession(getSFParams());
								BmoSession bmoSessionNew = new BmoSession();
								bmoSessionNew.getUserId().setValue(userId);
								bmoSessionNew.getSessionTypeId().setValue(bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger());						
								bmoSessionNew.getStartDate().setValue(startDateSession);
								bmoSessionNew.getCompanyId().setValue(bmoSessionSale.getCompanyId().toInteger());
								
								bmUpdateResult = pmSessionNew.save(pmConn, bmoSessionNew, bmUpdateResult);
								bmoSessionNew = (BmoSession)bmUpdateResult.getBmObject();
			
								// Asignar el cliente a la sesion						 
								autoAssignSession(pmConn, bmoSessionNew, bmoSessionSale, assist, bmUpdateResult);
							}
			
							if (!bmUpdateResult.hasErrors())
								pmConn.commit();

						} else 
							bmUpdateResult.addError(bmoSessionSale.getIdFieldName(), "Debe seleccionar la Fecha Inicio.");
					} else 
						bmUpdateResult.addError(bmoSessionSale.getIdFieldName(), "Debe seleccionar el Instructor.");
				} else 
					bmUpdateResult.addError(bmoSessionSale.getIdFieldName(), "Debe seleccionar la Venta de Sesión.");


			} catch (SFException e) {
				pmConn.rollback();
				bmUpdateResult.addMsg("Error al Crear/Asignar la Sesión " + e.toString());
			} finally {			
				pmConn.close();
			}
		//}	

		return bmUpdateResult;
	}

	// Asigna sesion sin asistencia
	private BmUpdateResult sessionNotAttended(String value, String pickDate, BmUpdateResult bmUpdateResult) throws SFPmException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();
		int sessionNotAttended = 0;
		try {
			// Obtener el fin de mes del mes seleccionado en el calendario
			String lastDateMonth = FlexUtil.getLastDateMonth(getSFParams(), pickDate);
			printDevLog("lastDateMonth:: "+lastDateMonth);

			// Obtener 1 mes atras de la fecha seleccionada
			Date firstDate = SFServerUtil.stringToDate(getSFParams().getDateFormat(), pickDate);
			// Restar 1 mes a la fecha
			Calendar c = Calendar.getInstance();
			c = Calendar.getInstance(); 
			c.setTime(firstDate); 
			c.add(Calendar.MONTH, -1);
			firstDate = c.getTime();
			// obtener el primer dia de la fecha del mes anterior
			String firstDateMonth = FlexUtil.getFirstDateMonth(getSFParams(), SFServerUtil.dateToString(firstDate, getSFParams().getDateFormat()));			

			printDevLog("firstDateMonth:: "+firstDateMonth);

			sql = " SELECT sesa_sessionsaleid FROM sessionsales " +
					" WHERE sesa_customerid = " + value +
					" AND sesa_startdate >= '" + firstDateMonth + "' " +
					" AND sesa_startdate <= '" +lastDateMonth + "' ";
			//" ORDER BY sesa_startdate DESC";
			printDevLog("sql sesa: "+sql);
			pmConn.doFetch(sql);	

			PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
			BmoSessionSale bmoSessionSale = new BmoSessionSale();
			int a = 1;
			while (pmConn.next()) {
				bmoSessionSale = (BmoSessionSale)pmSessionSale.get(pmConn.getInt("sesa_sessionsaleid"));	
				sessionNotAttended += sessionNotAttended(pmConn2, bmoSessionSale, false, bmUpdateResult);
				printDevLog("#"+a + " - vta: " +bmoSessionSale.getCode() + " - Disp: "+sessionNotAttended);
				a++;
			}	
			bmUpdateResult.addMsg("" + sessionNotAttended);

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error en sessionNotAttended " + e.toString());
		} finally {		
			pmConn.close();
			pmConn2.close();
		}
		return bmUpdateResult;
	}



	//@SuppressWarnings("static-access")
	private BmUpdateResult checkDates(String value, BmUpdateResult bmUpdateResult) throws SFPmException {		
		String saleDate = "";
		String sessDate = "";

		try {
			StringTokenizer tabs = new StringTokenizer(value, "|");		
			while (tabs.hasMoreTokens()) {
				saleDate = tabs.nextToken();
				sessDate = tabs.nextToken();				
			}


			Calendar saleCal = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), saleDate);
			Calendar sessCal = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), sessDate);

			int saleMonth = saleCal.get(Calendar.MONTH);
			int sessMonth = sessCal.get(Calendar.MONTH);

			if (saleMonth != sessMonth) {
				bmUpdateResult.addMsg("la fecha de venta es menor a la fecha de sesión");
			}

		} catch (SFException e) {			
			bmUpdateResult.addMsg("Error al autoasignar " + e.toString());
		}	

		return bmUpdateResult;	
	}	

	// Asigna sesion automaticamente
	private BmUpdateResult autoAssignSession(PmConn pmConn, BmoSession bmoSession, BmoSessionSale bmoSessionSale, String attended, BmUpdateResult bmUpdateResult) throws SFException {
		PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());
		BmoOrderSession bmoOrderSession = new BmoOrderSession();

		bmoOrderSession = new BmoOrderSession();
		bmoOrderSession.getSessionId().setValue(bmoSession.getId());
		bmoOrderSession.getOrderId().setValue(bmoSessionSale.getOrderId().toInteger());			
		if (attended.equals("true")) 
			bmoOrderSession.getAttended().setValue("1");
		else
			bmoOrderSession.getAttended().setValue("0");

		pmOrderSession.save(pmConn, bmoOrderSession, bmUpdateResult);

		return bmUpdateResult;		
	}	

	// Crea una sesion y genera la asistencia del pedido
	private BmUpdateResult createNewSessionWithSale(BmoSession bmoSession, String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		int sessionSaleId = 0;
		String description = "";
		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {			
			sessionSaleId = Integer.parseInt(tabs.nextToken());
			description = tabs.nextToken();
		}

		try {
			pmConn.disableAutoCommit();

			// Obtiene la venta de sesion del valor de la accion
			PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());					
			BmoSessionSale bmoSessionSale = (BmoSessionSale)pmSessionSale.get(sessionSaleId);

			printDevLog("En Accion crear Sesion con Venta, recuperada venta con id: " + sessionSaleId);

			// Asigna fecha y hora de fin de sesion calculada
			Date startDate = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(), bmoSession.getStartDate().toString());
			Date endDate = new Date(startDate.getTime() + (bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getDuration().toInteger() * 60000));
			bmoSession.getCompanyId().setValue(bmoSessionSale.getCompanyId().toInteger());
			bmoSession.getEndDate().setValue(SFServerUtil.dateToString(endDate, getSFParams().getDateTimeFormat()));
			save(pmConn, bmoSession, bmUpdateResult);
			printDevLog("En Accion crear Sesion con Venta, empresa: " + bmoSession.getCompanyId().toInteger());
			printDevLog("Se almacenó nueva sesion, id antes de commit" + bmoSession.getId());

			// Crea el Order Session
			BmoOrderSession bmoOrderSession = new BmoOrderSession();
			bmoOrderSession.getAttended().setValue(true);
			bmoOrderSession.getOrderId().setValue(bmoSessionSale.getOrderId().toInteger());
			bmoOrderSession.getSessionId().setValue(bmoSession.getId());
			bmoOrderSession.setBmoSession(bmoSession);
			bmoOrderSession.getDescription().setValue(description);
			PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());
			pmOrderSession.saveSimple(pmConn, bmoOrderSession, bmUpdateResult);
			// Actualiza cantidad de sesiones disponibles
			pmSessionSale.updateCountingSessions(pmConn, bmoSessionSale, bmUpdateResult);

			if (bmoSessionSale.getNoSession().toInteger() > bmoSessionSale.getMaxSessions().toInteger())
				bmUpdateResult.addError(bmoSession.getSessionTypeId().getName(), "El Cliente agotó sus sesiones");
			printDevLog("Se almacenó sesion de pedido, id antes de commit");

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

			bmUpdateResult.setId(bmoOrderSession.getId());
			bmUpdateResult.setBmObject(bmoOrderSession);

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addMsg("Error al crear la venta de sesión " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	//Número de sesiones que asistion en una venta de sesion
	public int sessionNotAttended(PmConn pmConn, BmoSessionSale bmoSessionSale, boolean attended, BmUpdateResult bmUpdateResult) throws SFException {
		int sessionAttended = 0;
		int countSession = 0;

		//Obtener del pedido el numero de clases
		PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
		BmoOrderSessionTypePackage bmoOrderSessTP = new BmoOrderSessionTypePackage();
		bmoOrderSessTP = (BmoOrderSessionTypePackage)pmOrderSessionTypePackage.getBy(pmConn, bmoSessionSale.getOrderId().toInteger(), bmoOrderSessTP.getOrderId().getName());

		String sql = "";
		//Obtener la sessiones que el cliente se ha presentado
		sql = " SELECT count(*) AS sessAttended FROM ordersessions " +
				" WHERE orss_orderid = " + bmoSessionSale.getOrderId().toInteger() + 
				((attended) ? " AND orss_attended = 1 " : "") +
				" AND orss_type <> '" + BmoOrderSession.TYPE_EXEMPT + "' ";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			sessionAttended = pmConn.getInt("sessAttended");
		}

		printDevLog("sessionAttended: "+sessionAttended);
		//Sesiones disponibles en el mes
		countSession = bmoSessionSale.getMaxSessions().toInteger() - sessionAttended;

		printDevLog("getNoSession(): "+bmoSessionSale.getNoSession().toInteger());
		printDevLog("getMaxSessions(): "+bmoSessionSale.getMaxSessions().toInteger());
		printDevLog("countSession: "+countSession);

		if (!(countSession > 0))
			countSession = 0;

		return countSession;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoSession = (BmoSession)bmObject;		

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoSession.getReservations().toInteger() > 0) {
			bmUpdateResult.addMsg("No se puede eliminar la Sesión: ya tiene Reservaciones.");
		} else {
			// Elimina serie
			if (bmoSession.getIsSeries().toBoolean() && bmoSession.getSeriesApplyAll().toBoolean())
				deleteSeries(pmConn, bmoSession, bmUpdateResult);

			// Elimina la sesion
			super.delete(pmConn, bmObject, bmUpdateResult);
		}
		return bmUpdateResult;
	}

	// Elimina toda la Serie
	private void deleteSeries(PmConn pmConn, BmoSession bmoSession, BmUpdateResult bmUpdateResult) throws SFException {

		BmFilter filterByParent = new BmFilter();
		filterByParent.setValueFilter(bmoSession.getKind(), bmoSession.getSeriesParentId(), bmoSession.getSeriesParentId().toInteger());
		Iterator<BmObject> seriesIterator = this.list(pmConn, filterByParent).iterator();

		while (seriesIterator.hasNext()) {
			BmoSession currentBmoSession = (BmoSession)seriesIterator.next();

			// Elimina la sesion si no tiene reservaciones
			if (!(currentBmoSession.getReservations().toInteger() > 0))
				super.delete(pmConn, currentBmoSession, bmUpdateResult);
		}

	}
}

