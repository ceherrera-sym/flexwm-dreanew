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
import java.util.ListIterator;

import com.flexwm.server.FlexUtil;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.PmWFlowStep;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class PmOrderSession extends PmObject{
	BmoOrderSession bmoOrderSession;


	public PmOrderSession(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoOrderSession = new BmoOrderSession();
		setBmObject(bmoOrderSession); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderSession.getOrderId(), bmoOrderSession.getBmoOrder()),
				new PmJoin(bmoOrderSession.getBmoOrder().getOrderTypeId(), bmoOrderSession.getBmoOrder().getBmoOrderType()),
				new PmJoin(bmoOrderSession.getBmoOrder().getCustomerId(), bmoOrderSession.getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoOrderSession.getBmoOrder().getBmoCustomer().getTerritoryId(), bmoOrderSession.getBmoOrder().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoOrderSession.getBmoOrder().getBmoCustomer().getReqPayTypeId(), bmoOrderSession.getBmoOrder().getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoOrderSession.getBmoOrder().getWFlowId(), bmoOrderSession.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoOrderSession.getBmoOrder().getBmoWFlow().getWFlowPhaseId(), bmoOrderSession.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoOrderSession.getBmoOrder().getWFlowTypeId(), bmoOrderSession.getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoOrderSession.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(), bmoOrderSession.getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoOrderSession.getBmoOrder().getBmoWFlow().getWFlowFunnelId(), bmoOrderSession.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel()),
				
				new PmJoin(bmoOrderSession.getSessionId(), bmoOrderSession.getBmoSession()),
				new PmJoin(bmoOrderSession.getBmoSession().getSessionTypeId(), bmoOrderSession.getBmoSession().getBmoSessionType()),
				new PmJoin(bmoOrderSession.getBmoSession().getBmoSessionType().getSessionDisciplineId(), bmoOrderSession.getBmoSession().getBmoSessionType().getBmoSessionDiscipline()),
				new PmJoin(bmoOrderSession.getBmoSession().getBmoSessionType().getCurrencyId(), bmoOrderSession.getBmoSession().getBmoSessionType().getBmoCurrency()),
				new PmJoin(bmoOrderSession.getBmoSession().getUserId(), bmoOrderSession.getBmoSession().getBmoUser()),
				new PmJoin(bmoOrderSession.getBmoSession().getBmoUser().getAreaId(), bmoOrderSession.getBmoSession().getBmoUser().getBmoArea()),
				new PmJoin(bmoOrderSession.getBmoSession().getBmoUser().getLocationId(), bmoOrderSession.getBmoSession().getBmoUser().getBmoLocation()),
				new PmJoin(bmoOrderSession.getBmoSession().getCompanyId(), bmoOrderSession.getBmoSession().getBmoCompany())
				
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderSession = (BmoOrderSession)autoPopulate(pmConn, new BmoOrderSession());

		// BmoOrder
		BmoOrder bmoOrder = new BmoOrder();
		if (pmConn.getInt(bmoOrder.getIdFieldName()) > 0) 
			bmoOrderSession.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else 
			bmoOrderSession.setBmoOrder(bmoOrder);	

		// BmoSession
		BmoSession bmoSession = new BmoSession();
		if (pmConn.getInt(bmoSession.getIdFieldName()) > 0) 
			bmoOrderSession.setBmoSession((BmoSession) new PmSession(getSFParams()).populate(pmConn));
		else 
			bmoOrderSession.setBmoSession(bmoSession);	

		return bmoOrderSession;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoOrderSession = (BmoOrderSession)bmObject;

		// Obtiene sesion
		PmSession pmSession = new PmSession(getSFParams());
		BmoSession bmoSession = (BmoSession)pmSession.get(pmConn, bmoOrderSession.getSessionId().toInteger());
		bmoOrderSession.setBmoSession(bmoSession);
		
		// Obtener la sesion vieja, si la cambiaron
		PmOrderSession pmOrderSessionOld = new PmOrderSession(getSFParams());
		BmoOrderSession bmoOrderSessionOld = new BmoOrderSession();
		if (bmoOrderSession.getId() > 0)
			bmoOrderSessionOld = (BmoOrderSession)pmOrderSessionOld.get(bmoOrderSession.getId());

		// Obtiene pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderSession.getOrderId().toInteger());
//		bmoOrderSession.setBmoOrder(bmoOrder);
		
		//Obtener la venta de sesión		
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
		bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(pmConn, bmoOrder.getId(), bmoSessionSale.getOrderId().getName());
		
		//Si la venta es demo, autorizar el pedido
		if (bmoSessionSale.getSessionDemo().toBoolean()) {
			bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		} 

		// Obtener el paquete del pedido
		PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
		BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
		bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)pmOrderSessionTypePackage.getBy(pmConn, bmoOrderSession.getOrderId().toInteger(), bmoOrderSessionTypePackage.getOrderId().getName());

		// Obtener el tipo de paquete
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoOrderSessionTypePackage.getSessionTypePackageId().toInteger());

		super.save(pmConn, bmoOrderSession, bmUpdateResult);
		
		// Calcular el precio de la venta si es la primera sesion
		if (!bmoSessionSale.getSessionDemo().toBoolean()) {
			calculateOrderPrice(pmConn, bmoOrderSession, bmoSessionTypePackage, bmUpdateResult);						
		}

		// Actualiza reservaciones de la sesion vieja
		if (bmoOrderSessionOld.getId() > 0 &&
				bmoOrderSessionOld.getSessionId().toInteger() != bmoOrderSession.getSessionId().toInteger())
			pmSession.updateReservations(pmConn, bmoOrderSessionOld.getBmoSession(), bmUpdateResult);
		
		// Actualiza reservaciones de la sesion nueva
		pmSession.updateReservations(pmConn, bmoSession, bmUpdateResult);

		// Valida que la sesion no este asignada ya al mismo pedido
		if (isDuplicateOrderSession(pmConn, bmoOrderSession, bmUpdateResult))
			bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "La Sesión ya está asignada a este Pedido.");			

		/*
		// Valida que no tenga sesion el mismo dia
		if (isDuplicateDay(pmConn, bmoOrderSession, bmUpdateResult))
			bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "El Pedido ya tiene Sesión asignada en ese Día.");			

		// Valida que las reservaciones no superen la capacidad
		if (bmoSession.getReservations().toInteger() > bmoSession.getBmoSessionType().getCapacity().toInteger())
			bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "La Sesión ya está completa.");

		// Valida que no sobrepase la cantidad de sesiones adquiridas en el pedido, si es de tipo sesiones
		if (weekSessions(pmConn, bmoOrderSession, bmUpdateResult) > bmoSessionTypePackage.getSessions().toInteger())
			bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "Ya se asignaron todas las Sesiones de la Semana.");
		
		if (isOutsideMonth(pmConn, bmoOrderSession, bmUpdateResult))
				bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "La Sesión debe estar en el mes del Pedido.");
		
		// Es de tipo semanal, valida que sea en la semana de la venta
		if (bmoSessionTypePackage.getType().equals(BmoSessionTypePackage.TYPE_WEEKLY)) {
			if (isOutsideWeek(pmConn, bmoOrderSession, bmUpdateResult))
				bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "La Sesión debe estar en la semana del Pedido.");
		}*/

		// Debe aplicar la serie completa
		if (bmoOrderSession.getSeriesApplyAll().toBoolean())
			createSessionSeries(pmConn, bmoSessionTypePackage, bmoOrder, bmoSession, bmoOrderSession, bmUpdateResult);

		// El Campo de Aplicar serie es temporal
		bmoOrderSession.getSeriesApplyAll().setValue(false);

		
		// Si las reservaciones esten completas(igual al #Ses/semana del paquete) en cada semana, avanzar fase
//		if (countOrderSessionMonth(pmConn, bmoOrderSession, bmUpdateResult)) {
//			advanceStepPhaseAssigned(pmConn, bmoOrder, true, bmUpdateResult);
//		}		
		
		// Actualiza cantidad de sesiones disponibles
		pmSessionSale.updateCountingSessions(pmConn, bmoSessionSale, bmUpdateResult);
		
		// Valida que no supere sus sesiones disponibles?
		if (bmoSessionSale.getNoSession().toInteger() > bmoSessionSale.getMaxSessions().toInteger())
			bmUpdateResult.addError(bmoOrderSession.getSessionId().getName(), "El Cliente agotó sus sesiones");
		
		// Almacena por ultima vez
		super.save(pmConn, bmoOrderSession, bmUpdateResult);

		return bmUpdateResult;
	}

	// Revisa que la sesion no este ya asignada al pedido
	private boolean isDuplicateOrderSession(PmConn pmConn, BmoOrderSession bmoOrderSession, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int duplicates = 0;
		sql = " SELECT COUNT(*) AS duplicates FROM ordersessions " +
				" WHERE orss_sessionid = " + bmoOrderSession.getSessionId().toInteger() +
				" AND orss_orderid = " + bmoOrderSession.getOrderId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next())
			duplicates = pmConn.getInt("duplicates");

		if (duplicates > 1)
			return true;
		else 
			return false;
	}

	// Revisa que no tenga citas en ese dia
	private boolean isDuplicateDay(PmConn pmConn, BmoOrderSession bmoOrderSession, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int sessionCount = 0;
		sql = " SELECT COUNT(*) AS sessioncount FROM ordersessions " +
				" LEFT JOIN sessions ON (orss_sessionid = sess_sessionid) " +
				" WHERE orss_orderid = " + bmoOrderSession.getOrderId().toInteger() + 
				" AND sess_startdate LIKE '" + SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(), getSFParams().getDateFormat(), bmoOrderSession.getBmoSession().getStartDate().toString()) + "%'";
		pmConn.doFetch(sql);
		if (pmConn.next())
			sessionCount = pmConn.getInt("sessioncount");

		if (sessionCount > 1)
			return true;
		else 
			return false;
	}
	
	private void calculateOrderPrice(PmConn pmConn, BmoOrderSession bmoOrderSession, BmoSessionTypePackage bmoSessionTypePackage, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "", startDate = "", endDate = "";
		//Obtener la fecha de la primera asignación
		
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn,bmoOrderSession.getOrderId().toInteger());
		
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			sql = " SELECT sess_startdate FROM ordersessions  " +
				  " LEFT JOIN sessions ON (orss_sessionid = sess_sessionid) " +	 
				  " WHERE orss_orderid = " + bmoOrderSession.getOrderId().toInteger() +
		          " ORDER BY sess_startdate";
		    pmConn.doFetch(sql);
		    if (pmConn.next()) {
		    	startDate = pmConn.getString("sess_startdate");
		    }
		    
		    //Calular el precio mensual
		    if (!startDate.equals("")) {
		    	//Obtener el costo mensual
		    	double salePrice = bmoSessionTypePackage.getSalePrice().toDouble();
		    	//Obtener los dias restantes del mes
		    	Calendar lastDateCal = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), startDate);
		    	lastDateCal.set(Calendar.DAY_OF_YEAR, lastDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		    	endDate = FlexUtil.calendarToString(getSFParams(), lastDateCal);
		    	//int days = SFServerUtil.daysBetween(getSFParams().getDateFormat(), startDate, endDate);
		    	int days = FlexUtil.daysDiff(getSFParams(), startDate, endDate) + 1;
		    	
		    	//Calcular el precio por dia		    	
		    	double priceDay = salePrice / lastDateCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		    	
		    	double totalPrice = days * priceDay;
		    	
		    	PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
		    	BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
		    	bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)pmOrderSessionTypePackage.getBy(pmConn, bmoOrderSession.getOrderId().toInteger(), bmoOrderSessionTypePackage.getOrderId().getName());
		    	
		    	bmoOrderSessionTypePackage.getPrice().setValue(SFServerUtil.formatCurrency(totalPrice));
		    	
		    	pmOrderSessionTypePackage.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);
		    	
		    }
		}    
 		
	}

	// Revisa la cantidad de reservaciones de una sesion vs la capacidad
	private int weekSessions(PmConn pmConn, BmoOrderSession bmoOrderSession, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int sessionCount = 0;
		
		Date sessionStartDate;
		try {
			sessionStartDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(bmoOrderSession.getBmoSession().getStartDate().toString());
		} catch (ParseException e) {
			throw new SFException(this.getClass().getName() + "-hasExceededWeekSessions(): " + e.toString());
		}
		Calendar sessionStartCalendar = Calendar.getInstance();
		sessionStartCalendar.setTime(sessionStartDate);
		
		sql = 	" SELECT COUNT(*) AS sessioncount FROM ordersessions " +
				" LEFT JOIN sessions ON (orss_sessionid = sess_sessionid) " +
				" WHERE orss_orderid = " + bmoOrderSession.getOrderId().toInteger() + 
				" AND WEEK(sess_startdate) = " + (sessionStartCalendar.get(Calendar.WEEK_OF_YEAR) - 1);
		pmConn.doFetch(sql);
		
		if (pmConn.next())
			sessionCount = pmConn.getInt("sessioncount");
		
		return sessionCount;
	}

	// Crea la asignacion de una serie de sesiones
	public void createSessionSeries(PmConn pmConn, BmoSessionTypePackage bmoSessionTypePackage, BmoOrder bmoOrder, BmoSession bmoSession, BmoOrderSession bmoOrderSession, BmUpdateResult bmUpdateResult) throws SFException {
		PmSession pmSession = new PmSession(getSFParams());
		ArrayList<BmFilter> sessionFilters = new ArrayList<BmFilter>();
		
		// Fechas necesarias
		Date sessionStartDate, orderStartDate;
		try {
			sessionStartDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(bmoOrderSession.getBmoSession().getStartDate().toString());
			orderStartDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(bmoOrder.getLockStart().toString());
		} catch (ParseException e) {
			throw new SFException(this.getClass().getName() + "-createSessionSeries(): " + e.toString());
		}
		Calendar sessionStartCalendar = Calendar.getInstance();
		sessionStartCalendar.setTime(sessionStartDate);
		
		Calendar orderStartCalendar = Calendar.getInstance();
		orderStartCalendar.setTime(orderStartDate);

		// Filtra por mismo instructor
		BmFilter filterByUser = new BmFilter();
		filterByUser.setValueFilter(bmoSession.getKind(), bmoSession.getUserId(), bmoOrderSession.getBmoSession().getUserId().toInteger());
		sessionFilters.add(filterByUser);

		// Filtra por fecha posterior a la sesion seleccionada
		BmFilter filterByStartDate = new BmFilter();
		filterByStartDate.setValueOperatorFilter(bmoSession.getKind(), bmoSession.getStartDate(), BmFilter.MAJOREQUAL, bmoSession.getStartDate().toString());
		sessionFilters.add(filterByStartDate);
		
		// Filtra por mismo horario arranque
		BmFilter filterByStartTime = new BmFilter();
		String time = sessionStartCalendar.get(Calendar.HOUR_OF_DAY) + ":" + sessionStartCalendar.get(Calendar.MINUTE);
		filterByStartTime.setContainsLabelFilter(bmoSession.getKind(), bmoSession.getStartDate().getName(), 
				bmoSession.getStartDate().getLabel(), time, time);
		sessionFilters.add(filterByStartTime);

		// Filtra por mismo tipo de sesion
		BmFilter filterBySessionType = new BmFilter();
		filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(), bmoOrderSession.getBmoSession().getSessionTypeId().toInteger());
		sessionFilters.add(filterBySessionType);
		
		// Filtra por mes del pedido
		BmFilter filterByOrderMonth = new BmFilter();
		filterByOrderMonth.setMonthFilter(bmoSession.getKind(), bmoSession.getStartDate().getName(), "" + (orderStartCalendar.get(Calendar.MONTH) + 1));
		sessionFilters.add(filterByOrderMonth);
		
		Iterator<BmObject> seriesIterator = pmSession.list(pmConn, sessionFilters).iterator();
		
		System.out.println("SQL Antes");
		
		while (seriesIterator.hasNext()) {
			BmoSession currentBmoSession = (BmoSession)seriesIterator.next();

			// Revisa que la sesion sea distina a la actual
			if (bmoOrderSession.getSessionId().toInteger() != currentBmoSession.getId()) {
				
				// Obtiene fecha de inicio de la sesion actual
				Date currentSessionStartDate;
				try {
					currentSessionStartDate = new SimpleDateFormat(getSFParams().getDateFormat()).parse(currentBmoSession.getStartDate().toString());
				} catch (ParseException e) {
					throw new SFException(this.getClass().getName() + "-createSessionSeries(): " + e.toString());
				}
				Calendar c = Calendar.getInstance();
				c.setTime(currentSessionStartDate);

				// Revisar si aplica el dia
				if (seriesDayApplies(bmoOrderSession, c.get(Calendar.DAY_OF_WEEK))) {
					
					BmoOrderSession newBmoOrderSession = new BmoOrderSession();
					newBmoOrderSession.getSessionId().setValue(currentBmoSession.getId());
					newBmoOrderSession.getOrderId().setValue(bmoOrderSession.getOrderId().toInteger());
					newBmoOrderSession.setBmoSession(currentBmoSession);

					// El campo de aplicar a todos es temporal
					newBmoOrderSession.getSeriesApplyAll().setValue(false);

					// Validacion antes de crear la sesion
					if (!isDuplicateOrderSession(pmConn, newBmoOrderSession, bmUpdateResult)
							&& !isDuplicateDay(pmConn, newBmoOrderSession, bmUpdateResult)
							&& (weekSessions(pmConn, newBmoOrderSession, bmUpdateResult) < bmoSessionTypePackage.getSessions().toInteger())) {
						
						super.save(pmConn, newBmoOrderSession, bmUpdateResult);
					}
				} 
			}
		}
		
		
		System.out.println("Termino secuencia");
	}	

	// Revisa si aplica generar la sesion en el dia especifico
	private boolean seriesDayApplies(BmoOrderSession bmoOrderSession, int day) {

		if (day == 1 && bmoOrderSession.getSeriesSunday().toBoolean()) return true; 
		else if (day == 2 && bmoOrderSession.getSeriesMonday().toBoolean()) return true; 
		else if (day == 3 && bmoOrderSession.getSeriesTuesday().toBoolean()) return true; 
		else if (day == 4 && bmoOrderSession.getSeriesWednesday().toBoolean()) return true; 
		else if (day == 5 && bmoOrderSession.getSeriesThursday().toBoolean()) return true; 
		else if (day == 6 && bmoOrderSession.getSeriesFriday().toBoolean()) return true; 
		else if (day == 7 && bmoOrderSession.getSeriesSaturday().toBoolean()) return true; 

		return false;
	}
	
	// Revisa la cantidad de sesiones asginadas en la semana de el mes del pedido para avanzar fase
	private boolean countOrderSessionMonth(PmConn pmConn, BmoOrderSession bmoOrderSession, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int  countOrderSession = 0;
		boolean countOrderSessionMonth = false,	week1 = false, week2 = false, week3 = false, week4 = false,	week5 = false, week6 = false;
		
		// Obtener el paquete del pedido
		PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
		BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
		bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)pmOrderSessionTypePackage.getBy(pmConn, bmoOrderSession.getOrderId().toInteger(), bmoOrderSessionTypePackage.getOrderId().getName());
		
		// Obtener el tipo de paquete
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoOrderSessionTypePackage.getSessionTypePackageId().toInteger());

		// Obtiene pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderSession.getOrderId().toInteger());
		
		// obtenemos el # de semana del inicio del pedido, y el # de semana de la ultima semana del mes del pedido
		sql = " SELECT WEEK('" + bmoOrder.getLockStart() + "') as numSemMesPedido, " +
					" WEEK(LAST_DAY('" + bmoOrder.getLockStart() + "')) as numUltSemMesPedido";
		pmConn.doFetch(sql);
		int numSemMesPedido = 0, numUltSemMesPedido = 0, numCicle = 0;
		if (pmConn.next()) {
			numSemMesPedido = pmConn.getInt("numSemMesPedido");
			numUltSemMesPedido = pmConn.getInt("numUltSemMesPedido");
		}

		// Contar las asignaciones en todas las semanas del mes desde que inicio pedido(hasta fin de mes)
		for (int i = numSemMesPedido; i <= numUltSemMesPedido; i++) {
			numCicle++;
			sql = 	" SELECT COUNT(*) AS countORSS FROM ordersessions " +
					" LEFT JOIN orders ON (orde_orderid = orss_orderid) " +
					" LEFT JOIN sessions ON (sess_sessionid = orss_sessionid) " +
					" WHERE orss_orderid = " + bmoOrderSession.getOrderId().toInteger() + 
//						" AND WEEK(sess_startdate) = " + (sessionStartCalendar.get(Calendar.WEEK_OF_YEAR) - 1) +
					" AND WEEK(sess_startdate) = " + i +
					" AND orde_status <> '" + BmoOrder.STATUS_CANCELLED + "' ";
			pmConn.doFetch(sql);
			
			if (pmConn.next()) countOrderSession = pmConn.getInt("countORSS");
			
			// las reservaciones en la semana son las mismas que el paquete, se pone la semana como completa(true)
			// numCicle: control de cuantas semanas fueron en el mes del pedido
			if (countOrderSession == bmoSessionTypePackage.getSessions().toInteger()) {
				if (numCicle == 1) week1 = true;
				if (numCicle == 2) week2 = true;
				if (numCicle == 3) week3 = true;
				if (numCicle == 4) week4 = true;
				if (numCicle == 5) week5 = true;
				if (numCicle == 6) week6 = true;
			}
		}
		
		// si en alguna semana del mes del pedido, no estuvo completa, regresa false 
		for (int y = 1; y <= numCicle; y++) {
			
			if (y == 1) {
				if (week1) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;
				
			} else if (y == 2) {
				if (week1 && week2) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;

			} else if (y == 3) {
				if (week1 && week2 && week3) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;

			} else if (y == 4) {
				if (week1 && week2 && week3 && week4) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;

			} else if (y == 5) {
				if (week1 && week2 && week3 && week4 && week5) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;

			} else if (y == 6) {
				if (week1 && week2 && week3 && week4 &&week6) countOrderSessionMonth = true;
				else countOrderSessionMonth = false;

			} else countOrderSessionMonth = false;
		}
		
		return countOrderSessionMonth ;
	}
	
	
	public void advanceStepPhaseAssigned(PmConn pmConn, BmoOrder bmoOrder, boolean advance, BmUpdateResult bmUpdateResult) throws SFException {
		// Buscar la primer tarea
		PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		
		// Filtro wflujo
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoOrder.getWFlowId().toInteger());
		
		// Filtro segunda fase
		BmFilter filterFirstPhase = new BmFilter();
		filterFirstPhase.setValueOperatorFilter(bmoWFlowStep.getKind(),  bmoWFlowStep.getWFlowPhaseId() , BmFilter.EQUALS, 2);
		
		// Filtro tarea activa
		BmFilter filterStepEnabled = new BmFilter();
		filterStepEnabled.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getEnabled(), 1);
		
		// Filtro tarea avance(<100 o =100)
		BmFilter filterStepAdvance = new BmFilter();
		
		if (advance)
			filterStepAdvance.setValueOperatorFilter(bmoWFlowStep.getKind(),  bmoWFlowStep.getProgress(), BmFilter.MINOR, 100);
		else 
			filterStepAdvance.setValueOperatorFilter(bmoWFlowStep.getKind(),  bmoWFlowStep.getProgress(), BmFilter.EQUALS, 100);

		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		filterList.add(filterWFlow);
		filterList.add(filterFirstPhase);
		filterList.add(filterStepEnabled);
		filterList.add(filterStepAdvance);

		ListIterator<BmObject> stepList = pmWFlowStep.list(pmConn, filterList).listIterator();
		if (stepList.hasNext()) {
			bmoWFlowStep = (BmoWFlowStep)stepList.next();
			if (advance) {
				bmoWFlowStep.getProgress().setValue(100);
				bmoWFlowStep.getComments().setValue("Avance automático; Todas las Sesiones fueron Asignadas");
			} else {
				bmoWFlowStep.getProgress().setValue(0);
				bmoWFlowStep.getComments().setValue("Avance automático; Faltan Sesiones por Asignar");
			}

			pmWFlowStep.save(pmConn, bmoWFlowStep, bmUpdateResult);
		}
	}
	
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoOrderSession = (BmoOrderSession)bmObject;
		
		// Obtiene sesion
		PmSession pmSession = new PmSession(getSFParams());
		BmoSession bmoSession = (BmoSession)pmSession.get(pmConn, bmoOrderSession.getSessionId().toInteger());

		// Obtiene pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderSession.getOrderId().toInteger());

		
		super.delete(pmConn, bmoOrderSession, bmUpdateResult);
		
		// Si las reservaciones NO estan completas(igual al #Ses/semana del paquete) en cada semana, regresar fase a 0
		if (!countOrderSessionMonth(pmConn, bmoOrderSession, bmUpdateResult)) {
			advanceStepPhaseAssigned(pmConn, bmoOrder, false, bmUpdateResult);
		}
		
		//Obtener la venta de sesión		
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
		bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(pmConn, bmoOrder.getId(), bmoSessionSale.getOrderId().getName());
				
		// Actualiza cantidad de sesiones disponibles
		pmSessionSale.updateCountingSessions(pmConn, bmoSessionSale, bmUpdateResult);
						
		// Actualiza reservaciones de la sesion
		pmSession.updateReservations(pmConn, bmoSession, bmUpdateResult);
		
		return bmUpdateResult;
	}
}

