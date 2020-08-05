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
import java.util.ListIterator;

import com.flexwm.server.FlexUtil;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmOpportunity;
import com.flexwm.server.cm.PmQuote;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowStep;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionReview;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
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
import com.symgae.shared.sf.BmoCompany;

import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowType;


public class PmSessionSale extends PmObject {
	BmoSessionSale bmoSessionSale;

	public PmSessionSale(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSessionSale = new BmoSessionSale();
		setBmObject(bmoSessionSale);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSessionSale.getCustomerId(), bmoSessionSale.getBmoCustomer()),
				new PmJoin(bmoSessionSale.getBmoCustomer().getSalesmanId(), bmoSessionSale.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoSessionSale.getBmoCustomer().getBmoUser().getAreaId(), bmoSessionSale.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoSessionSale.getBmoCustomer().getBmoUser().getLocationId(), bmoSessionSale.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoSessionSale.getBmoCustomer().getTerritoryId(), bmoSessionSale.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoSessionSale.getBmoCustomer().getReqPayTypeId(), bmoSessionSale.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoSessionSale.getWFlowTypeId(), bmoSessionSale.getBmoWFlowType()),
				new PmJoin(bmoSessionSale.getBmoWFlowType().getWFlowCategoryId(), bmoSessionSale.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoSessionSale.getWFlowId(), bmoSessionSale.getBmoWFlow()),
				new PmJoin(bmoSessionSale.getBmoWFlow().getWFlowPhaseId(), bmoSessionSale.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoSessionSale.getSessionTypePackageId(), bmoSessionSale.getBmoSessionTypePackage()),
				new PmJoin(bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId(), bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType()),
				new PmJoin(bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getSessionDisciplineId(), bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getBmoSessionDiscipline()),
				new PmJoin(bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getCurrencyId(), bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getBmoCurrency()),
				new PmJoin(bmoSessionSale.getBmoWFlow().getWFlowFunnelId(), bmoSessionSale.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoSessionSale bmoSessionSale = (BmoSessionSale) autoPopulate(pmConn, new BmoSessionSale());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoSessionSale.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoSessionSale.setBmoCustomer(bmoCustomer);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoSessionSale.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoSessionSale.setBmoWFlowType(bmoWFlowType);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoSessionSale.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoSessionSale.setBmoWFlow(bmoWFlow);
		
		// BmoSessionTypePackage
		BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
		if (pmConn.getInt(bmoSessionTypePackage.getIdFieldName()) > 0) 
			bmoSessionSale.setBmoSessionTypePackage((BmoSessionTypePackage) new PmSessionTypePackage(getSFParams()).populate(pmConn));
		else 
			bmoSessionSale.setBmoSessionTypePackage(bmoSessionTypePackage);

		return bmoSessionSale;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		if (getSFParams().restrictData(bmoSessionSale.getProgramCode())) {

			// Filtro por asignacion de venta propiedads
			filters = " ( sesa_salesuserid IN (" +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid in ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" left join users u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" left join users u2 ON (u2.user_parentid = u1.user_userid) " +
					" left join users u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" left join users u2 ON (u2.user_parentid = u1.user_userid) " +
					" left join users u3 ON (u3.user_parentid = u2.user_userid) " +
					" left join users u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid in ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" left join users u2 ON (u2.user_parentid = u1.user_userid) " +
					" left join users u3 ON (u3.user_parentid = u2.user_userid) " +
					" left join users u4 ON (u4.user_parentid = u3.user_userid) " +
					" left join users u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" sesa_orderid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					" AND (wflw_callercode = '" + new BmoSessionSale().getProgramCode() + "' " +
					" OR wflw_callercode = '" + new BmoOrder().getProgramCode() + "') " + 
					" ) " +
					" ) " +
					" ) ";
		}
		
		// Filtro de pedidos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( sesa_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " sesa_companyid = " + getSFParams().getSelectedCompanyId();
		}
					
		return filters;
	}	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoSessionSale = (BmoSessionSale)bmObject;	
		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoSessionSale.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoSessionSale, bmUpdateResult);
			bmoSessionSale.setId(bmUpdateResult.getId());
			bmoSessionSale.getCode().setValue(bmoSessionSale.getCodeFormat());
						
		}
		
		// Asigna fecha de venta si no esta asignada
		if (bmoSessionSale.getStartDate().equals(""))
			bmoSessionSale.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		
		// Actualizacion de la clave de la venta
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoSessionSale.getWFlowTypeId().toInteger());

		// Datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoSessionSale.getCustomerId().toInteger());
		pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
		
		// Asigna vendedor, desde el cliente si no viene de alguna oportunidad
		if (newRecord) {
			if(!(bmoSessionSale.getSalesUserId().toInteger() > 0))
				bmoSessionSale.getSalesUserId().setValue(bmoCustomer.getSalesmanId().toInteger());
		}

		// Otras validaciones una vez asignado el tipo de venta propiedad
		if (!bmUpdateResult.hasErrors()) {
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();
			
			// Modificar pedido si ya existe
			if (bmoSessionSale.getOrderId().toInteger() > 0) {
				
				// Actualiza info del pedido, porque es existente
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoSessionSale.getOrderId().toInteger());
				bmoOrder.getName().setValue(bmoWFlowType.getName().toString());
				bmoOrder.getCustomerId().setValue(bmoSessionSale.getCustomerId().toInteger());
				bmoOrder.getUserId().setValue(bmoSessionSale.getSalesUserId().toInteger());
				bmoOrder.getLockStart().setValue(bmoSessionSale.getStartDate().toString());
				bmoOrder.getLockEnd().setValue(bmoSessionSale.getEndDate().toString());				
				
				//pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
				
				//Si cambio el Tipo de paquete actualizar el precio
				PmSessionSale pmSessionSalePrev = new PmSessionSale(getSFParams());
				BmoSessionSale bmoSessionSalePrev = (BmoSessionSale)pmSessionSalePrev.get(pmConn, bmoSessionSale.getId());
				
				if (bmoSessionSalePrev.getSessionTypePackageId().toInteger() != bmoSessionSale.getSessionTypePackageId().toInteger()) {
					//eliminar el pedido
					BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
					PmOrderSessionTypePackage pmOrderSessionTypePackagePrev = new PmOrderSessionTypePackage(getSFParams());
					bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)pmOrderSessionTypePackagePrev.getBy(pmConn, bmoSessionSale.getOrderId().toInteger(), bmoOrderSessionTypePackage.getOrderId().getName());
					pmOrderSessionTypePackagePrev.delete(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);
					
					bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
					bmoOrderSessionTypePackage.getOrderId().setValue(bmoOrder.getId());
					bmoOrderSessionTypePackage.getSessionTypePackageId().setValue(bmoSessionSale.getSessionTypePackageId().toInteger());
					pmOrderSessionTypePackagePrev.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);
				}
				
				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
				
			} else {
				// No existe, se crea
				createOrderFromSessionSale(pmConn, pmOrder, bmoOrder, bmoSessionSale, bmUpdateResult);
			}
			
			// Asignar el id del pedido
			bmoSessionSale.getOrderId().setValue(bmoOrder.getId());
			
			// Asignar el wflowid del pedido
			bmoSessionSale.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
		
			// Almacena venta para vincular pedido
			super.save(pmConn, bmoSessionSale, bmUpdateResult);
			
			// Genera el tipo de paquete de sesion al pedido
			if (newRecord) {
				PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
				BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
				bmoOrderSessionTypePackage.getOrderId().setValue(bmoOrder.getId());
				bmoOrderSessionTypePackage.getSessionTypePackageId().setValue(bmoSessionSale.getSessionTypePackageId().toInteger());
				pmOrderSessionTypePackage.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);
				
				if (bmoSessionSale.getSessionDemo().toBoolean()) {
					//Crear sessionDemo
					createSessionDemo(pmConn, bmoSessionSale, bmUpdateResult);
					
					pmConn.doUpdate("update ordersessiontypepackages set orsp_price = 0, orsp_amount = 0 where orsp_orderid = " + bmoOrder.getId());
					pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
				}	
				
				// Avanzar la primera tarea en automático
				advanceFirstStep(pmConn, bmoSessionSale, bmUpdateResult);
			}
		}

		// Validaciones adicionales
		if (!bmUpdateResult.hasErrors()) {

			// Guarda la sesion
			super.save(pmConn, bmoSessionSale, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, int propertyId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		bmoSessionSale.getDescription().setValue(bmoOpportunity.getDescription().toString());
		bmoSessionSale.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
		bmoSessionSale.getEndDate().setValue(bmoOpportunity.getEndDate().toString());		
		bmoSessionSale.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoSessionSale.getSalesUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoSessionSale.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoSessionSale.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoSessionSale.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoSessionSale.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());

		return this.save(pmConn, bmoSessionSale, bmUpdateResult);
	}

	private void createOrderFromSessionSale(PmConn pmConn, PmOrder pmOrder, BmoOrder bmoOrder, BmoSessionSale bmoSessionSale, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear pedido a partir de venta propiedad
		bmoOrder.getCode().setValue(bmoSessionSale.getCode().toString());
		bmoOrder.getName().setValue(bmoSessionSale.getCode().toString());
		bmoOrder.getDescription().setValue(bmoSessionSale.getDescription().toString());
		bmoOrder.getAmount().setValue(0);
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoSessionSale.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoSessionSale.getEndDate().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoSessionSale.getCustomerId().toInteger());
		bmoOrder.getOrderTypeId().setValue(bmoSessionSale.getOrderTypeId().toInteger());
		bmoOrder.getUserId().setValue(bmoSessionSale.getSalesUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoSessionSale.getWFlowTypeId().toInteger());
		
		
		
		// Obtener la empresa
		if (bmoSessionSale.getCompanyId().toInteger() > 0 )
			bmoOrder.getCompanyId().setValue(bmoSessionSale.getCompanyId().toInteger());
		else
			bmoOrder.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger());
		
		// Obtener la moneda de la oportunidad si existe, en caso contrario de la configuracion
		if (bmoSessionSale.getOpportunityId().toInteger() > 0) {
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoSessionSale.getOpportunityId().toInteger());
			bmoOrder.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
			bmoOrder.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
			bmoOrder.getOpportunityId().setValue(bmoOpportunity.getId());
			
			// Obtener cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = new BmoQuote();
			bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());
			
			bmoOrder.getTax().setValue(bmoQuote.getTax().toDouble());
			bmoOrder.getTaxApplies().setValue(bmoQuote.getTaxApplies().toString());
		} else {			
			//Obtener la moneda del tipo de session}
			PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
			BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoSessionSale.getSessionTypePackageId().toInteger());
			
			
			PmSessionType pmSessionType = new PmSessionType(getSFParams());
			BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSessionTypePackage.getSessionTypeId().toInteger());
								
			if (bmoSessionType.getCurrencyId().toInteger() > 0)
				bmoOrder.getCurrencyId().setValue(bmoSessionType.getCurrencyId().toInteger());
			else
				bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		}
		
		 		
		pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		
		
		// Si el venta propiedad proviene de una oportunidad
		if (bmoSessionSale.getOpportunityId().toInteger() > 0) {

			// Obtener los usuarios de WFlow de la oportunidad
			pmOrder.createWFlowUsersFromOpportunity(pmConn, bmoOrder, bmUpdateResult);

			// Obtener los documentos de WFlow de la oportunidad
			pmOrder.createWFlowDocumentsFromOpportunity(pmConn, bmoOrder, bmUpdateResult);
		}
		
	}
	
	
	private void createSessionDemo(PmConn pmConn, BmoSessionSale bmoSessionSale, BmUpdateResult bmUpdateResult) throws SFException {
			
			PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
			BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoSessionSale.getSessionTypePackageId().toInteger());
			
			PmSessionType pmSessionType = new PmSessionType(getSFParams());
			BmoSessionType bmoSessionType = (BmoSessionType)pmSessionType.get(pmConn, bmoSessionTypePackage.getSessionTypeId().toInteger());
			
			PmSession newPmSession = new PmSession(getSFParams());
			
			BmoSession newBmoSession = new BmoSession();
			newBmoSession.getDescription().setValue("Clase de Prueba");
			newBmoSession.getReservations().setValue(1);
			newBmoSession.getAvailable().setValue(1);
			newBmoSession.getUserId().setValue(bmoSessionSale.getSalesUserId().toString());
			newBmoSession.getSessionTypeId().setValue(bmoSessionType.getId());
			newBmoSession.getAutoAssign().setValue(1);
			newBmoSession.getStartDate().setValue(bmoSessionSale.getSessionDateDemo().toString());
			newBmoSession.getCompanyId().setValue(bmoSessionSale.getCompanyId().toInteger());
			newPmSession.save(pmConn, newBmoSession, bmUpdateResult);
			
				
	}	
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		
		if (action.equals(BmoSessionSale.ACTION_COUNTORSS)) {
			bmUpdateResult = countOrderSession(value, bmUpdateResult);			
		} else {
			bmUpdateResult = hasOrderSession(value, bmUpdateResult);
		}
		
		return bmUpdateResult;
	}
	
	private BmUpdateResult hasOrderSession(String value, BmUpdateResult bmUpdateResult) throws SFPmException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		
		try {
			//Calular las sessiones del mes
			int totalORSS = 0;
			//Obtener el no de ordersessions
			sql = " SELECT COUNT(orss_ordersessionid) AS countOrdeSession FROM ordersessions " +
			      " WHERE orss_orderid = " + value;
			pmConn.doFetch(sql);			
			if(pmConn.next()) {
				totalORSS = pmConn.getInt("countOrdeSession");
			}
			
			bmUpdateResult.setMsg("" + totalORSS);
		} catch (Exception e) {
			bmUpdateResult.addMsg("Existe errores " + e.toString());
		} finally {
			pmConn.close();
		}	
		
		return bmUpdateResult;		
	}
	
	private BmUpdateResult countOrderSession(String value, BmUpdateResult bmUpdateResult) throws SFPmException {
		
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		
		try { 
			String sql = "";
			//Obtener la venta
			BmoSessionSale bmoSessionSale = new BmoSessionSale();
			PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
			bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(value, bmoSessionSale.getOrderId().getName());
			
			//Obtener el paquete
			PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
			BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(bmoSessionSale.getSessionTypePackageId().toInteger());
			
			//Calular las sessiones del mes
			int totalORSS = 0;
			//Obtener el no de ordersessions
			sql = " SELECT COUNT(orss_ordersessionid) AS countOrdeSession FROM ordersessions " +
			      " WHERE orss_orderid = " + value;
			pmConn.doFetch(sql);
			
			if(pmConn.next()) {
				totalORSS = pmConn.getInt("countOrdeSession");
			}
			
			String startDate = "";
			String endDate = "";
			sql = " SELECT * FROM ordersessions " +
			      " LEFT JOIN sessions ON (sess_sessionid = orss_sessionid) " +
			      " WHERE orss_orderid = " + value;
			pmConn.doFetch(sql);			
			if(pmConn.next()) {
				startDate = pmConn.getString("sess_startdate");
			}
			
			//Obtener la fecha del primer apartado
			if (startDate.equals(""))
				startDate = bmoSessionSale.getStartDate().toString();
			
			//Obtener la sesiones a las que tiene derecho			
			int sesaWeek = bmoSessionTypePackage.getSessions().toInteger();
			
			Calendar sesaCalWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), startDate);
			double dayOfWeek = 0;
			
			//Obtener la fecha final
			sesaCalWeek.set(Calendar.DAY_OF_YEAR, sesaCalWeek.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate = FlexUtil.calendarToString(getSFParams(), sesaCalWeek);
			dayOfWeek = FlexUtil.getWorkingDaysDiff(getSFParams(), startDate, endDate);
			
			
			System.out.println("startDate " + startDate + " endDate " + endDate);
			
			double sessTotal = (dayOfWeek / sesaWeek) * sesaWeek;
			int total = (int)sessTotal; 
			
			bmUpdateResult.addMsg(totalORSS + "/" + (total - totalORSS));
			
			System.out.println("dayOfWeek " + dayOfWeek + " sesaWeek " + sesaWeek + " total " + total);
			
			
			
			/*
			if (sesaCalWeek.get(sesaCalWeek.DAY_OF_WEEK) == sesaCalWeek.MONDAY) dayOfWeek = 0;
			else if (sesaCalWeek.get(sesaCalWeek.DAY_OF_WEEK)== sesaCalWeek.TUESDAY) dayOfWeek = 1;
			else if (sesaCalWeek.get(sesaCalWeek.DAY_OF_WEEK) == sesaCalWeek.WEDNESDAY) dayOfWeek = 2;
			else if (sesaCalWeek.get(sesaCalWeek.DAY_OF_WEEK) == sesaCalWeek.THURSDAY) dayOfWeek = 3;
			else if (sesaCalWeek.get(sesaCalWeek.DAY_OF_WEEK) == sesaCalWeek.FRIDAY) dayOfWeek = 4;
			
			if (sesaWeek < 5) dayOfWeek = 0; 
			
			System.out.println("dayOfWeek " + dayOfWeek);
			
			//Obtener las semanas a partir de la venta
			int startWeek = sesaCalWeek.get(Calendar.WEEK_OF_YEAR);
			
			//Obtener el último día del mes
			sesaCalWeek.set(sesaCalWeek.DAY_OF_YEAR, sesaCalWeek.getActualMaximum(sesaCalWeek.DAY_OF_MONTH));
			int endWeek = sesaCalWeek.get(Calendar.WEEK_OF_YEAR);
			
			System.out.println("endDate " + sesaCalWeek.getTime());
			System.out.println(" startWeek " + startWeek + " endWeek " + endWeek);
			int totalWeek = (endWeek - startWeek);
			
			System.out.println("totalORSS " + totalORSS + " totalWeek " + totalWeek + " sesaWeek " + sesaWeek + " dayOfWeek " + dayOfWeek);
			
			
			*/
			
			
			
					
			
		} catch (Exception e) {
			bmUpdateResult.addMsg("Existe errores " + e.toString());
		} finally {
			pmConn.close();
		}	
		
		return bmUpdateResult;	
	}	
	
	// Calcula el total de asistencias que cuentan
	public void updateCountingSessions(PmConn pmConn, BmoSessionSale bmoSessionSale, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		
		sql = " SELECT COUNT(*) AS c FROM " + formatKind("ordersessions") + " " + 
		      " WHERE orss_orderid = " + bmoSessionSale.getOrderId().toInteger() +
		      " AND orss_type <> '" + BmoOrderSession.TYPE_EXEMPT + "' ";				
		pmConn.doFetch(sql);			
		
		if (pmConn.next()) {
			bmoSessionSale.getNoSession().setValue(pmConn.getInt("c"));
		}
		
		super.saveSimple(pmConn, bmoSessionSale, bmUpdateResult);
	}
	
	// Avanza el primer paso de la venta
	private void advanceFirstStep(PmConn pmConn, BmoSessionSale bmoSessionSale, BmUpdateResult bmUpdateResult) throws SFException {
		// Buscar la primer tarea
		PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		
		// Filtro wflujo
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoSessionSale.getWFlowId().toInteger());
		
		// Filtro primera fase
		BmFilter filterFirstPhase = new BmFilter();
		filterFirstPhase.setValueOperatorFilter(bmoWFlowStep.getKind(),  bmoWFlowStep.getWFlowPhaseId() , BmFilter.EQUALS, 1);
		
		// Filtro tarea activa
		BmFilter filterStepEnabled = new BmFilter();
		filterStepEnabled.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getEnabled(), 1);
		
		// Filtro tarea < 100
		BmFilter filterStepMinor = new BmFilter();
		filterStepMinor.setValueOperatorFilter(bmoWFlowStep.getKind(),  bmoWFlowStep.getProgress(), BmFilter.MINOR, 100);
		
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		filterList.add(filterWFlow);
		filterList.add(filterFirstPhase);
		filterList.add(filterStepEnabled);
		filterList.add(filterStepMinor);

		ListIterator<BmObject> stepList = pmWFlowStep.list(pmConn, filterList).listIterator();
		
		if (stepList.hasNext()) {
			bmoWFlowStep = (BmoWFlowStep)stepList.next();
			bmoWFlowStep.getProgress().setValue(100);
			bmoWFlowStep.getComments().setValue("Avance automático");
			pmWFlowStep.save(pmConn, bmoWFlowStep, bmUpdateResult);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoSessionSale = (BmoSessionSale)bmObject;
		BmFilter filterBySessionSale = new BmFilter();

		// Revisar si esta en revision para proceder a eliminar
		if (bmoSessionSale.getStatus().toChar() == BmoSessionSale.STATUS_REVISION) {		

			super.delete(pmConn, bmoSessionSale, bmUpdateResult);

			// Eliminar Evaluaciones de la venta de sesion
			PmSessionReview pmSessionReview = new PmSessionReview(getSFParams());
			BmoSessionReview bmoSessionReview = new BmoSessionReview();
			BmFilter filterBySessionSaleId = new BmFilter();
			filterBySessionSaleId.setValueFilter(bmoSessionReview.getKind(), bmoSessionReview.getSessionSaleId(), bmoSessionSale.getId());
			ListIterator<BmObject> sessionReviewList = pmSessionReview.list(pmConn, filterBySessionSaleId).listIterator();
			while (sessionReviewList.hasNext()) {
				bmoSessionReview = (BmoSessionReview)sessionReviewList.next();
				pmSessionReview.delete(pmConn,  bmoSessionReview, bmUpdateResult);
			}
						
			// Eliminar pedidos
			if (!bmUpdateResult.hasErrors()) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				if (bmoSessionSale.getOrderId().toInteger() > 0) {
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoSessionSale.getOrderId().toInteger());

					// Eliminar ordenes de compra ligadas al pedido
					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					BmoRequisition bmoRequisition = new BmoRequisition();
					filterBySessionSale.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(), bmoOrder.getId());
					ListIterator<BmObject> requisitionList = pmRequisition.list(pmConn, filterBySessionSale).listIterator();
					while (requisitionList.hasNext()) {
						bmoRequisition = (BmoRequisition)requisitionList.next();
						pmRequisition.delete(pmConn,  bmoRequisition, bmUpdateResult);
					}

					pmOrder.delete(pmConn,  bmoOrder, bmUpdateResult);
				}

//				// Eliminar flujos
//				PmWFlow pmWFlow = new PmWFlow(getSFParams());
//				BmoWFlow bmoWFlow = new BmoWFlow();
//				// Busca el WFlow por PEDIDO
//				filterBySessionSale.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoSessionSale.getOrderId().toInteger());			
//				BmFilter filterWFlowCategory = new BmFilter();
//				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoSessionSale.getProgramCode());
//				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
//				filterList.add(filterBySessionSale);
//				filterList.add(filterWFlowCategory);
//				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
//				while (wFlowList.hasNext()) {
//					bmoWFlow = (BmoWFlow)wFlowList.next();
//					pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
//				}
			}

		} else {
			bmUpdateResult.addError(bmoSessionSale.getStatus().getName(), "No se puede eliminar la Venta - no está En Revisión.");
		}

		return bmUpdateResult;
	}
	
	
	
	

}
