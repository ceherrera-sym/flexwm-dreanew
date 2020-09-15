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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoUser;

import sun.print.PSPrinterJob.PluginPrinter;

import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;


public class PmCredit extends PmObject {
	BmoCredit bmoCredit;

	public PmCredit(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCredit = new BmoCredit();
		setBmObject(bmoCredit);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCredit.getCustomerId(), bmoCredit.getBmoCustomer()),
				new PmJoin(bmoCredit.getBmoCustomer().getSalesmanId(), bmoCredit.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoCredit.getBmoCustomer().getBmoUser().getAreaId(), bmoCredit.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoCredit.getBmoCustomer().getBmoUser().getLocationId(), bmoCredit.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoCredit.getBmoCustomer().getTerritoryId(), bmoCredit.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoCredit.getBmoCustomer().getReqPayTypeId(), bmoCredit.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoCredit.getWFlowTypeId(), bmoCredit.getBmoWFlowType()),
				new PmJoin(bmoCredit.getBmoWFlowType().getWFlowCategoryId(), bmoCredit.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoCredit.getWFlowId(), bmoCredit.getBmoWFlow()),
				new PmJoin(bmoCredit.getCreditTypeId(), bmoCredit.getBmoCreditType()),
				new PmJoin(bmoCredit.getBmoWFlow().getWFlowPhaseId(), bmoCredit.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoCredit.getBmoWFlow().getWFlowFunnelId(), bmoCredit.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCredit bmoCredit = (BmoCredit) autoPopulate(pmConn, new BmoCredit());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoCredit.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoCredit.setBmoCustomer(bmoCustomer);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoCredit.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoCredit.setBmoWFlowType(bmoWFlowType);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoCredit.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoCredit.setBmoWFlow(bmoWFlow);
		
		// BmoCreditType
		BmoCreditType BmoCreditType = new BmoCreditType();
		if (pmConn.getInt(BmoCreditType.getIdFieldName()) > 0) 
			bmoCredit.setBmoCreditType((BmoCreditType) new PmCreditType(getSFParams()).populate(pmConn));
		else 
			bmoCredit.setBmoCreditType(BmoCreditType);

		return bmoCredit;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int locationUserId = getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger();

		if (getSFParams().restrictData(bmoCredit.getProgramCode())) {

			// Filtro por asignacion de venta propiedads
			filters = "( cred_salesuserid in (" +
					" select user_userid from users " +
					" where " + 
					" user_userid = " + loggedUserId +
					" or user_userid in ( " +
					" select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" cred_orderid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					" AND (wflw_callercode = '" + new BmoCredit().getProgramCode() + 
							"' OR wflw_callercode = '" + new BmoOrder().getProgramCode() + "') " + 
					"   ) " +
					" ) " +
					" ) ";
		}
		
		// Filtro de cxc de empresas del usuario
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()
				&& getSFParams().restrictData(new BmoLocation().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " cred_locationid = " + locationUserId;
		}
		
		// Filtro de pedidos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( cred_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " cred_companyid = " + getSFParams().getSelectedCompanyId();
		}
				
		return filters;
	}	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCredit = (BmoCredit)bmObject;	
		boolean newRecord = false;
		BmoCredit bmoCreditPrevious = new BmoCredit();

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoCredit.getId() > 0)) {
			
			newRecord = true;
			
			if (!(bmoCredit.getCurrencyParity().toDouble() > 0)) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, bmoCredit.getCurrencyId().toInteger());
			
				bmoCredit.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
			}

			super.save(pmConn, bmoCredit, bmUpdateResult);
			bmoCredit.setId(bmUpdateResult.getId());
			bmoCredit.getCode().setValue(bmoCredit.getCodeFormat());

			//Validar que no tenga fallos
			
			//Validar que no rebase los limites de credito
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoCredit.getCustomerId().toInteger());
			
			//Validar el INE del Cliente
			if (bmoCustomer.getNss().toString().equals(""))
				bmUpdateResult.addError(bmoCredit.getCustomerId().getName(), "El Cliente no tiene capturado el INE");
			
			//Validar que no tenga un crédito vigente
			if (hasCreditNow(pmConn, bmoCredit, bmUpdateResult)) {
				bmUpdateResult.addError(bmoCredit.getCustomerId().getName(), "El cliente cuenta con un crédito activo");
			} 
			
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());

			PmCreditGuarantee pmCreditGuarantee = new PmCreditGuarantee(getSFParams());

			if (bmoCreditType.getGuarantees().toInteger() > 0) {
				//Si es una renovación obtener los avales
				if (bmoCredit.getParentId().toInteger() > 0) {
					BmoCreditGuarantee bmoCreditGuarantee = new BmoCreditGuarantee();				
					BmFilter bmFilter = new BmFilter();
					bmFilter.setValueFilter(bmoCreditGuarantee.getKind(), bmoCreditGuarantee.getCreditId(), bmoCredit.getParentId().toInteger());					
					Iterator<BmObject> listGuarantee = pmCreditGuarantee.list(pmConn, bmFilter).iterator();
					
					while (listGuarantee.hasNext()) {
						bmoCreditGuarantee = (BmoCreditGuarantee)listGuarantee.next();						
						pmCreditGuarantee.createGuarantee(pmConn, bmoCredit, bmoCreditGuarantee.getCustomerId().toInteger(), bmUpdateResult);
					}
					
				} else {				
					if (!(bmoCredit.getGuaranteeOneId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoCredit.getGuaranteeOneId().getName(), "Debe Capturar el Primer Aval");
					} else {
						pmCreditGuarantee.createGuarantee(pmConn, bmoCredit, bmoCredit.getGuaranteeOneId().toInteger(), bmUpdateResult);
					}
				}	
			}	

			//Validar los avales
			if (bmoCreditType.getGuarantees().toInteger() > 1) {			
				if (!(bmoCredit.getGuaranteeTwoId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoCredit.getGuaranteeTwoId().getName(), "Debe Capturar el Segundo Aval");
				} else {
					pmCreditGuarantee.createGuarantee(pmConn, bmoCredit, bmoCredit.getGuaranteeTwoId().toInteger(), bmUpdateResult);
				}
			}			

			//Validar que el monto sea mayor a cero
			if (bmoCredit.getAmount().toDouble() <= 0) {
				bmUpdateResult.addError(bmoCredit.getAmount().getName(), "El monto debe ser mayor a cero");
			} 
			
			//Validar las sumas de los creditos no sobre el limite de credito del promotor
			
			//Revisar el Limite de Credito del supervisor			
			//checkCreditLimit(pmConn, bmoCredit, bmUpdateResult);
		} else {		

			PmCredit pmCreditPrevious = new PmCredit(getSFParams());
			bmoCreditPrevious = (BmoCredit)pmCreditPrevious.get(pmConn, bmoCredit.getId());
		}

		// Asigna fecha de venta si no esta asignada
		if (bmoCredit.getStartDate().equals(""))
			bmoCredit.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		
		// Actualizacion de la clave de la venta
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoCredit.getWFlowTypeId().toInteger());

		// Datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoCredit.getCustomerId().toInteger());
		pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);

		// Asigna vendedor, desde el cliente si no viene de alguna oportunidad
		if (newRecord) {
			if(!(bmoCredit.getSalesUserId().toInteger() > 0))
				bmoCredit.getSalesUserId().setValue(bmoCustomer.getSalesmanId().toInteger());				
		} 

		PmCreditType pmCreditType = new PmCreditType(getSFParams());
		BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());
		if ((bmoCredit.getCustomerId().toInteger() == bmoCredit.getGuaranteeOneId().toInteger()) || (bmoCredit.getCustomerId().toInteger() == bmoCredit.getGuaranteeTwoId().toInteger()) )
			bmUpdateResult.addError(bmoCredit.getGuaranteeOneId().getName(), "El Cliente no puede ser aval de su propio Credito.");
		
		if (bmoCreditType.getGuarantees().toInteger() == 1) {
			if (bmoCredit.getGuaranteeOneId().toInteger() > 0) {
				if (!validateSalesManNotEquals(pmConn, bmoCredit.getCustomerId().toInteger(), 
						bmoCredit.getGuaranteeOneId().toInteger()))
					bmUpdateResult.addError(bmoCredit.getGuaranteeOneId().getName(), "El aval tiene un Promotor diferente al Cliente.");
			}
		} else if (bmoCreditType.getGuarantees().toInteger() == 2) {
			if (bmoCredit.getGuaranteeOneId().toInteger() > 0 && bmoCredit.getGuaranteeTwoId().toInteger() > 0) {
				if (!validateSalesManNotEquals(pmConn, bmoCredit.getCustomerId().toInteger(), 
						bmoCredit.getGuaranteeOneId().toInteger(), bmoCredit.getGuaranteeTwoId().toInteger()))
					bmUpdateResult.addError(bmoCredit.getGuaranteeOneId().getName(), "Uno de los avales tiene un Promotor diferente al Cliente.");
			}
		}

		if (!bmUpdateResult.hasErrors()) {
			
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			// Modificar pedido si ya existe
			if (bmoCredit.getOrderId().toInteger() > 0) {				

				// Actualiza info del pedido, porque es existente				
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoCredit.getOrderId().toInteger());
				
				//Validar que el pedido este en revisión
				if (!bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
					String status = "";
					if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
						status = "Autorizado";
					if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED))
						status = "Finalizado";
					if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED))
						status = "Cancelado";
						
						bmUpdateResult.addError(bmoCredit.getStatus().getName(), "El Pedido está " + status);
				}
				
				bmoOrder.getName().setValue(bmoWFlowType.getName().toString());
				bmoOrder.getCustomerId().setValue(bmoCredit.getCustomerId().toInteger());
				bmoOrder.getUserId().setValue(bmoCredit.getSalesUserId().toInteger());
				bmoOrder.getLockStart().setValue(bmoCredit.getStartDate().toString());
				bmoOrder.getLockEnd().setValue(bmoCredit.getEndDate().toString());
				bmoOrder.getCompanyId().setValue(bmoCredit.getCompanyId().toInteger());
				bmoOrder.getCurrencyId().setValue(bmoCredit.getCurrencyId().toInteger());
				bmoOrder.getCurrencyParity().setValue(bmoCredit.getCurrencyParity().toDouble());
				
				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
				
			} else {	

				// No existe, se crea
				createOrderFromCredit(pmConn, pmOrder, bmoOrder, bmoCredit, bmUpdateResult);
			}
			
			// Asignar el id del pedido
			//bmoCredit.getOrderId().setValue(bmoOrder.getId());
			
			// Asignar el wflowid del pedido
			bmoCredit.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());

			//El ejecutivo esta asignado
			/*if (!hasUserInWflowuser(pmConn, bmoOrder.getWFlowId().toInteger(), bmoOrder.getOrderTypeId().toInteger(), bmUpdateResult)) {
				//Asignar el ejecutivo
				updateUserInWflowuser(pmConn, bmoOrder.getWFlowId().toInteger(), bmoOrder.getOrderTypeId().toInteger(), bmUpdateResult);
			}*/
			
			super.save(pmConn, bmoCredit, bmUpdateResult);

		}
		
		//Finalizar el crédito
		/*if (bmoCredit.getStatus().equals(BmoCredit.STATUS_FINISHED)) {
			//Validar que el crédito este liquidado incluyendo las
		}*/
		
		PmOrderCredit pmOrderCredit = new PmOrderCredit(getSFParams());

		if (newRecord) {
			pmOrderCredit.create(pmConn, bmoCredit, bmUpdateResult);
		} else {			
			//Si viene de una renovacion crear el ordercredit 
			if (newRecord && bmoCredit.getParentId().toInteger() > 0)				
				pmOrderCredit.create(pmConn, bmoCredit, bmUpdateResult);
			else	
				pmOrderCredit.changeOrderCredit(pmConn, bmoCredit, bmUpdateResult);
			
	
		}

		// Validaciones adicionales
		if (!bmUpdateResult.hasErrors()) {
			if (!newRecord) {
				// Si no cuenta con permisos para autorizar NO deja avanzar
				if (bmoCredit.getStatus().toChar() == BmoCredit.STATUS_AUTHORIZED) {
					if (!getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGESTATUSAUTHORIZED)) {
						bmUpdateResult.addError(bmoCredit.getStatus().getName(), "No cuenta con permisos para Autorizar el Crédito");
					} else {
						BmoWFlow bmoWFlow = new BmoWFlow();
						PmWFlow pmWFlow = new PmWFlow(getSFParams());
						bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoCredit.getWFlowId().toInteger());
						// Revisar flujo
						if (!bmoWFlow.getHasDocuments().toBoolean())
							bmUpdateResult.addError(bmoCredit.getCode().getName(), "Los Documentos no están Completos.");
					}
				} 
				// Si no cuenta con permisos para finalizar NO deja avanzar
				else if (bmoCredit.getStatus().toChar() == BmoCredit.STATUS_FINISHED) {
					if (!getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGESTATUSFINISHED)) {
						bmUpdateResult.addError(bmoCredit.getStatus().getName(), "No cuenta con permisos para Finalizar el Crédito");
					}
				}
				//  Si estaba autorizado y estan cambiando a otro estatus y no tiene permiso de autorizar el credito, NO dejar cambiar 
				if ((bmoCreditPrevious.getStatus().toChar() == BmoCredit.STATUS_AUTHORIZED)
						&& (bmoCredit.getStatus().toChar() != BmoCredit.STATUS_AUTHORIZED)
						&& !getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGESTATUSAUTHORIZED)) {
					bmUpdateResult.addError(bmoCredit.getStatus().getName(), "No cuenta con permisos para quitar Autorización del Crédito");
				}
				//Si cambia el tipo de credito, recalcular los montos del pedido
				if(bmoCreditPrevious.getCreditTypeId() != bmoCredit.getCreditTypeId()) {
					BmoOrder bmoOrder = new BmoOrder();
					PmOrder pmOrder = new PmOrder(getSFParams());					
					bmoOrder = (BmoOrder)pmOrder.get(pmConn,bmoCreditPrevious.getOrderId().toInteger());
					pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
					printDevLog("Se cambio el tipo de credito " + bmoCreditPrevious.getCode());
				}

				// Generar bitacora
				String status = "En Revisión";
				/*if (bmoCredit.getStatus().equals(BmoCredit.STATUS_PREAUTHORIZED))
					status = "Pre-Autorizado";*/
				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_AUTHORIZED))
					status = "Autorizado";
				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_FINISHED))
					status = "Finalizado";
				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_CANCELLED))
					status = "Cancelado";
				
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoCredit.getWFlowId().toInteger(),
						BmoWFlowLog.TYPE_OTHER, "El Crédito se guardó como " +status + ".");

			}
			// Guarda la sesion
			super.save(pmConn, bmoCredit, bmUpdateResult);
		

		}

		return bmUpdateResult;
	}
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		//Obtener el saldo de la orden de compra
		if (action.equals(BmoCredit.ACTION_CREDITAUTORIZED)) {
			bmUpdateResult = getCreditAutorized(value, bmUpdateResult);			
		} else if (action.equals(BmoCredit.ACTION_RENEWCREDIT)) {
			bmUpdateResult = renewCredit(value, bmUpdateResult);			
		} else if (action.equals(BmoCredit.ACTION_GETRENEWCREDIT)) {
			bmUpdateResult = getRenewCredit(value, bmUpdateResult);	
		} else if (action.equals(BmoCredit.ACTION_CHANGECREDITDATE)) {
			bmUpdateResult = changeCreditDate(value, bmUpdateResult);
		} else if (action.equals(BmoCredit.ACTION_CHANGEUSERSALE)) {
			bmUpdateResult = changeUserSale(value, bmUpdateResult);
		} else if (action.equals(BmoCredit.ACTION_LASTMONDAYOFWEEK)) {
			Calendar mondayWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			mondayWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			mondayWeek.add(Calendar.WEEK_OF_YEAR, 0); 
			bmUpdateResult.setMsg(FlexUtil.calendarToString(getSFParams(), mondayWeek).substring(0,10));
		} else if(action.equals(BmoCredit.ACTION_CHANGECUSTOMERS)){

			bmUpdateResult = changeCustomer(value,bmObject, bmUpdateResult );
		}
		
		
		return bmUpdateResult;
	}
	
	
	//El ejecutivo esta asignado
//	private boolean hasUserInWflowuser(PmConn pmConn, int wFlowId, int orderTypeId, BmUpdateResult bmUpdateResult) throws SFException {
//		
//		//Obtener el grupo
//		PmOrderType pmOrderType = new PmOrderType(getSFParams());
//		BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, orderTypeId);
//		
//		pmConn.doFetch("SELECT wflu_wflowuserid FROM wflowusers "
//				+ " WHERE wflu_wflowid = " + wFlowId 
//				+ " AND wflu_userid > 0 "
//				+ " AND wflu_profileid = " + bmoOrderType.getDispersionGroupId().toInteger());
//		if (pmConn.next()) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	//Asignar el ejecutivo
//	private void updateUserInWflowuser(PmConn pmConn, int wFlowId, int orderTypeId, BmUpdateResult bmUpdateResult) throws SFException {		
//		String sql = "";
//		
//		PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
//		
//		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
//		
//		//Obtener el grupo
//		PmOrderType pmOrderType = new PmOrderType(getSFParams());
//		BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, orderTypeId);
//		
//		sql = " SELECT wflu_userid FROM wflowusers " +
//		      " WHERE wflu_wflowid = " + wFlowId +
//		      " ORDER BY wflu_profileid";
//		pmConn.doFetch(sql);
//		while(pmConn.next()) {
//			int userId = pmConn.getInt("wflu_userid");
//			//Existe el usuario en el grupo
//			if (pmProfileUser.userInGroup(pmConn, bmoOrderType.getDispersionGroupId().toInteger(), userId)) {
//				//Asignar el usuario al grupo
//				pmWFlowUser.updateUnassignedWithUser(pmConn, wFlowId, userId, bmUpdateResult);
//				break;
//			}
//		}
//		
//	}
	
	//Obtener el crédito renovado
	private BmUpdateResult getRenewCredit(String value, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		
		try {
			
			sql = " SELECT cred_code FROM credits " +
				  " WHERE cred_parentid = " + value;
			pmConn.doFetch(sql);
			if (pmConn.next()) bmUpdateResult.addMsg(pmConn.getString("cred_code"));
			
			
		} catch (SFPmException e) {
			bmUpdateResult.addMsg("Error al renovar el crédito " + e.toString());
		} finally {
			pmConn.close();
		}
	                 
		return bmUpdateResult;
	}
	
	
	
	
	//Renovacion del Crédito
	private BmUpdateResult renewCredit(String value, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
				
		PmOrder pmOrder = new PmOrder(getSFParams());
		
		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCreditLast = (BmoCredit)pmCredit.get(Integer.parseInt(value));
		
		try {
			
			//Validar que el credito este finalizado y liquidado			
			BmoOrder bmoOrderLast = (BmoOrder)pmOrder.get(bmoCreditLast.getOrderId().toInteger());
			
			
			if (!bmoOrderLast.getPaymentStatus().equals(BmoOrder.PAYMENTSTATUS_TOTAL)) {				
				bmUpdateResult.addMsg("El crédito tiene adeudo");
			} else {
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				
				//Si la falla esta pagada poder hacer la renovación
				if (pmRaccount.failureIsPaid(pmConn, bmoOrderLast.getId(), bmUpdateResult)) {
					int items = 0; 
					//Validar que no exista otro credito renovado
					sql = " SELECT count(*) as items from credits " + 
					      " WHERE cred_parentid = " + value;
					pmConn.doFetch(sql);
					if (pmConn.next()) items = pmConn.getInt("items");
					
					if (items > 0)
						bmUpdateResult.addMsg("El credito ya cuenta con una renovación");
					else {
						//Validar que no tenga mas de 2 pagos atrazado
						if (bmoCredit.getPaymentStatus().equals(BmoCredit.PAYMENTSTATUS_INPROBLEM)) {
							bmUpdateResult.addMsg("El credito no puede ser renovado ");
						} else if (bmoCredit.getPaymentStatus().equals(BmoCredit.PAYMENTSTATUS_PENALTY)) {
							items = 0;
							//Validar que la CxC de penalizacion fue pagada
							sql = " SELECT count(*) as items FROM raccounts " +
							      " WHERE racc_failure = 1 " +
								  " AND racc_orderid = " + bmoCreditLast.getOrderId().toInteger() +
								  " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'";
							pmConn.doFetch(sql);
							if (pmConn.next()) items = pmConn.getInt("items");
							
							if (!(items > 0)) bmUpdateResult.addMsg("La penalización no ha sido pagada");
						}
						
						//Si no tiene errores crear la credito nuevo
						
						if (!bmUpdateResult.hasErrors()) {
							BmoCredit bmoNewCredit = new BmoCredit();
							bmoNewCredit.getWFlowTypeId().setValue(bmoCreditLast.getWFlowTypeId().toInteger());
							bmoNewCredit.getOrderTypeId().setValue(bmoCreditLast.getOrderTypeId().toInteger());
							bmoNewCredit.getCreditTypeId().setValue(bmoCreditLast.getCreditTypeId().toInteger());
							bmoNewCredit.getCustomerId().setValue(bmoCreditLast.getCustomerId().toInteger());
							bmoNewCredit.getSalesUserId().setValue(bmoCreditLast.getSalesUserId().toInteger());
							//bmoNewCredit.getGuaranteeOneId().setValue(bmoCreditLast.getGuaranteeOneId().toInteger());
							//bmoNewCredit.getGuaranteeTwoId().setValue(bmoCreditLast.getGuaranteeTwoId().toInteger());
							bmoNewCredit.getParentId().setValue(bmoCreditLast.getId());
							bmoNewCredit.getCurrencyId().setValue(bmoCreditLast.getCurrencyId().toInteger());
							bmoNewCredit.getCurrencyParity().setValue(bmoCreditLast.getCurrencyParity().toDouble());
							bmoNewCredit.getCompanyId().setValue(bmoCreditLast.getCompanyId().toInteger());
							bmoNewCredit.getAmount().setValue(bmoCreditLast.getAmount().toDouble());						
							pmCredit.save(bmoNewCredit, bmUpdateResult);
							
						}
						/*
						//Mover los documentos
						BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
						BmFilter bmFilter = new BmFilter();
						bmFilter.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId().getName(), bmoCreditLast.getwFlowId().toInteger());
						PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(getSFParams());
						Iterator<BmObject> wFlowDocumentIterator = pmWFlowDocument.list(pmConn, bmFilter).iterator();
						
						//Obtener el wflow nuevo
						BmoCredit bmoCreditNew = (BmoCredit)pmCredit.get(bmUpdateResult.getId());
						bmUpdateResult.addMsg("Credito " + bmoCreditNew.getCode().toString());
						
						BmoWFlowDocument bmoWFlowDocumentNew = new BmoWFlowDocument();
						
						/*while (wFlowDocumentIterator.hasNext()) {
							bmoWFlowDocument = (BmoWFlowDocument)wFlowDocumentIterator.next();
							
							bmoWFlowDocumentNew = new BmoWFlowDocument();
							//bmoWFlowDocumentNew.getWFlowId().setValue();
						}*/
						
						if (!bmUpdateResult.hasErrors()) 
							pmConn.enableAutoCommit();
					}	
					
				} else {
					bmUpdateResult.addMsg("La penalización del crédito no esta pagada");
				}
			}	
			
		} catch (SFPmException e) {
			bmUpdateResult.addMsg("Error al renovar el crédito " + bmUpdateResult.errorsToString());
		} finally {
			pmConn.close();
		}
	
		return bmUpdateResult;
	}
	
	//Autorizar el crédito
	private BmUpdateResult getCreditAutorized(String value, BmUpdateResult bmUpdateResult) throws SFException {				
		int creditId = 0;
		int guaranteeOneId = 0;
		int guaranteeTwoId = 0;
		
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			
			
			pmConn.disableAutoCommit();
			
			StringTokenizer tabs = new StringTokenizer(value, "|");		
			while (tabs.hasMoreTokens()) {
				creditId = Integer.parseInt(tabs.nextToken());
				guaranteeOneId = Integer.parseInt(tabs.nextToken());
				guaranteeTwoId = Integer.parseInt(tabs.nextToken());				
			}

			//Obtener el pedido del crédito			
			bmoCredit = (BmoCredit)this.get(pmConn, creditId);
			
			//Obtener el tipo de Crédito
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());
			
			PmCreditGuarantee pmCreditGuarantee = new PmCreditGuarantee(getSFParams());
			
			//Validar los avales
			if (!(guaranteeOneId > 0)) {
				bmUpdateResult.addMsg("Debe de Capturar el 1er.Aval");
			} else {
				pmCreditGuarantee.createGuarantee(pmConn, bmoCredit, guaranteeOneId, bmUpdateResult);
			}
			
			if (bmoCreditType.getGuarantees().toInteger() > 1) {
				if (!(guaranteeTwoId > 0)) {
					bmUpdateResult.addMsg("Debe de Capturar el 2do.Aval");
				} else {
					pmCreditGuarantee.createGuarantee(pmConn, bmoCredit, guaranteeTwoId, bmUpdateResult);
				}
			}
			
			//Obtener el Pedido
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoCredit.getOrderId().toInteger());
			
			bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
			
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
			
			if (!bmUpdateResult.hasErrors())
				pmConn.commit();
			


		} catch (SFPmException e) {
			bmUpdateResult.addMsg("Error Autorizar Crédito " + e.toString());
		} finally {
			pmConn.close();
		}
		
		return bmUpdateResult;
		
	}
	
	// Cambiar las fecha en credito-pedidos-cxc
	private BmUpdateResult changeCreditDate(String value, BmUpdateResult bmUpdateResult) throws SFException {
		StringTokenizer tabs = new StringTokenizer(value, "|");
		int creditId = 0;
		String startDate = "";
		while (tabs.hasMoreTokens()) {
			creditId = Integer.parseInt(tabs.nextToken());
			startDate = tabs.nextToken();
		}

		// Obtener credito
		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCredit = (BmoCredit)pmCredit.get(creditId);
		bmoCredit.getStartDate().setValue(startDate);

		// Obtener pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoCredit.getOrderId().toInteger());
		bmoOrder.getLockStart().setValue(startDate);

		// Validar que no tengas pagos las cxc
		if (bmoOrder.getPayments().toDouble() > 0)
			bmUpdateResult.addError(bmoCredit.getStartDate().getName(), "<b>No se pueden cambiar las Fechas de las CxC, existen pagos.</b>");

		if (!bmUpdateResult.hasErrors()) {
			pmOrder.saveSimple(bmoOrder, bmUpdateResult);
			pmCredit.saveSimple(bmoCredit, bmUpdateResult);

			// Modificar la fecha de las CxC
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			pmRaccount.recalculatedRaccDate(bmoCredit, bmUpdateResult);
		}
		return bmUpdateResult;
	}
	
	//Cambiar el cliente 
		private BmUpdateResult changeCustomer(String value,BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			String sql = "";
			try {
			StringTokenizer tabs = new StringTokenizer(value, "|");
			int creditId = 0;
			int customerId = 0;
			while (tabs.hasMoreTokens()) {
				creditId = Integer.parseInt(tabs.nextToken());
				customerId = Integer.parseInt(tabs.nextToken());
			
			}
			PmCredit pmCredit = new PmCredit(getSFParams());
			BmoCredit bmoCredit = (BmoCredit)pmCredit.get(creditId);
			
			BmoOrder bmoOrder = new BmoOrder();
			PmOrder pmOrder = new PmOrder(getSFParams());
//			bmoCredit = (BmoCredit)this.get(pmConn, creditId);
			bmoOrder = (BmoOrder)pmOrder.get(bmoCredit.getOrderId().toInteger());

				if (bmoCredit.getCustomerId().toInteger() != customerId) {
					bmoCredit.getCustomerId().setValue(customerId);
					
					pmCredit.saveSimple(bmoCredit, bmUpdateResult);
				
				if((bmoOrder.getPayments().toDouble()>0)) {
					bmUpdateResult.addError(bmoCredit.getCustomerId().getName(), "No se puede cambiar el cliente por que existen pagos.");
				}else {
					sql = " UPDATE orders set orde_customerid = " + bmoCredit.getCustomerId() + 
						  " WHERE orde_orderid = " + bmoCredit.getOrderId().toInteger();
					pmConn.doUpdate(sql);
					
					sql = " UPDATE wflows set wflw_customerid = " + bmoCredit.getCustomerId() + 
						  " WHERE wflw_wflowid = " + bmoCredit.getWFlowId().toInteger();
					pmConn.doUpdate(sql);
					
					sql = " UPDATE raccounts set racc_customerid= " + bmoCredit.getCustomerId() + 
						  " WHERE racc_orderid = " + bmoCredit.getOrderId().toInteger();
					pmConn.doUpdate(sql);
			
				}
		}
			}catch (SFPmException e) {
			bmUpdateResult.addMsg("Error al cambiar el cliente en el crédito " + e.toString());
		} finally {
			pmConn.close();
		}	
		
		return bmUpdateResult;
	}
	//userlol
	private BmUpdateResult changeUserSale(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		
		String sql = "";
		
		try {
		
			StringTokenizer tabs = new StringTokenizer(value, "|");
			int creditId = 0;
			int userId = 0;
			while (tabs.hasMoreTokens()) {
				creditId = Integer.parseInt(tabs.nextToken());
				userId = Integer.parseInt(tabs.nextToken());
			}
			
			PmCredit pmCredit = new PmCredit(getSFParams());
			BmoCredit bmoCredit = (BmoCredit)pmCredit.get(creditId);
			
			if (bmoCredit.getSalesUserId().toInteger() != userId) {
				bmoCredit.getSalesUserId().setValue(userId);
				
				pmCredit.saveSimple(bmoCredit, bmUpdateResult);
				
				//Cambiar el usuario en el pedido, el flujo y las CxC
				sql = " UPDATE orders set orde_userid = " + userId + 
					  " WHERE orde_orderid = " + bmoCredit.getOrderId().toInteger();
				pmConn.doUpdate(sql);
				
				sql = " UPDATE wflows set wflw_userid = " + userId + 
					  " WHERE wflw_wflowid = " + bmoCredit.getWFlowId().toInteger();
				pmConn.doUpdate(sql);
				
				sql = " UPDATE raccounts set racc_userid = " + userId + 
					  " WHERE racc_orderid = " + bmoCredit.getOrderId().toInteger();
				pmConn.doUpdate(sql);
			}
			
		} catch (SFPmException e) {
			bmUpdateResult.addMsg("Error cambiar el vendedor del crédito " + e.toString());
		} finally {
			pmConn.close();
		}	
		
		return bmUpdateResult;
	}


	/*public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, int propertyId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoCredit bmoCredit = new BmoCredit();
		bmoCredit.getDescription().setValue(bmoOpportunity.getDescription().toString());
		bmoCredit.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
		bmoCredit.getEndDate().setValue(bmoOpportunity.getEndDate().toString());		
		bmoCredit.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoCredit.getSalesUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoCredit.getwFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoCredit.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoCredit.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoCredit.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());

		return this.save(pmConn, bmoCredit, bmUpdateResult);
	}*/
	
	
	
	public void checkCreditLimit(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		//Promotor
		BmoUser bmoUser = new BmoUser();
		PmUser pmUser = new PmUser(getSFParams());
		bmoUser = (BmoUser)pmUser.get(pmConn, bmoCredit.getSalesUserId().toInteger());
		
		if (bmoUser.getParentId().toInteger() > 0) {
			
			//Validar que tiene saldo el ejecutivo en su cuenta de banco 
			sql = " SELECT * FROM bankaccounts " + 
			      " WHERE bkac_responsibleid = " + bmoUser.getParentId().toInteger();
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				//Obtener el saldo de la cuenta de banco
				double balance = pmConn.getDouble("bkac_balance");
				if (bmoCredit.getAmount().toDouble() > balance) {
					bmUpdateResult.addError(bmoCredit.getSalesUserId().getName(), "El ejecutivo no tiene saldo suficiente en la cuenta de banco");
				}
			} else {
				bmUpdateResult.addError(bmoCredit.getSalesUserId().getName(), "El ejecutivo no tiene una cuenta de banco asignada");
			}
		} else { 
			bmUpdateResult.addError(bmoCredit.getSalesUserId().getName(), "El promotor no tiene un ejecutivo asignado");
		}		
		
		
		
	}

	private void createOrderFromCredit(PmConn pmConn, PmOrder pmOrder, BmoOrder bmoOrder, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		
		// Crear pedido a partir de venta propiedad
		bmoOrder.getCode().setValue(bmoCredit.getCode().toString());
		bmoOrder.getName().setValue(bmoCredit.getCode().toString());
		bmoOrder.getDescription().setValue(bmoCredit.getComments().toString());
		bmoOrder.getAmount().setValue(bmoCredit.getAmount().toDouble());
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoCredit.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoCredit.getEndDate().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoCredit.getCustomerId().toInteger());
		bmoOrder.getOrderTypeId().setValue(bmoCredit.getOrderTypeId().toInteger());
		bmoOrder.getUserId().setValue(bmoCredit.getSalesUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoCredit.getWFlowTypeId().toInteger());
		bmoOrder.getCompanyId().setValue(bmoCredit.getCompanyId().toInteger());
		bmoOrder.getCurrencyId().setValue(bmoCredit.getCurrencyId().toInteger());
		bmoOrder.getCurrencyParity().setValue(bmoCredit.getCurrencyParity().toDouble());		

		pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		
		bmoCredit.getOrderId().setValue(bmoOrder.getId());
	}

	public boolean hasCreditNow(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;
		//Buscar creditos que no esten finalizados
		sql = " SELECT COUNT(cred_creditid) AS creditNow FROM credits " +
		      " WHERE cred_customerid = " + bmoCredit.getCustomerId().toInteger() +
		      " AND (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')" +
		      " AND cred_creditid <> " + bmoCredit.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("creditNow");
		}
		
		if (items > 0) result = true;
		
		return result;
	}
	
	public boolean hasFailure(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;		
//		int raccounts = 0;
		//Buscar creditos que no esten finalizados
		sql = " SELECT cred_credittypeid FROM credits " +
		      " WHERE cred_customerid = " + bmoCredit.getCustomerId().toInteger() +
		      //" AND (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')" +
		      " AND cred_creditid <> " + bmoCredit.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());
			
			items = bmoCreditType.getDeadLine().toInteger() + 1;
		}
		
		sql = " SELECT COUNT(racc_raccountid) AS raccounts FROM raccounts " +
			  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
			  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +	
			  " AND racc_orderid IN ( " +
			  	" SELECT cred_orderid FROM credits WHERE cred_customerid = " + bmoCredit.getCustomerId().toInteger() +
			  	" AND cred_creditid <> " + bmoCredit.getId() +  
			  	")"; 
		pmConn.doFetch(sql);
		if (pmConn.next()) 
		if (items > 0) result = true;
		
		return result;
	}
	
	public boolean hasGuarantee(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = true;
		String sql = "";
		int items = 0;		
		//Obtener en numero de avales
		PmCreditType pmCreditType = new PmCreditType(getSFParams());
		BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());
				
		
		//Buscar creditos que no esten finalizados
		sql = " SELECT COUNT(crgu_creditid) AS guaranteeNow FROM creditguarantees " +
		      " WHERE crgu_creditid = " + bmoCredit.getId();		      
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("guaranteeNow");
		}
		
		if (items < bmoCreditType.getGuarantees().toInteger()) result = false;
		
		return result;
	}
	
	public double sumCreditNow(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		double result = 0;
		String sql = "";
		
		//Buscar creditos que no esten finalizados
		sql = " SELECT SUM(cred_amount) AS sumCredit FROM credits " +
		      " WHERE cred_salesuserid = " + bmoCredit.getSalesUserId().toInteger() +
		      " AND (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			result = pmConn.getInt("sumCredit");
		}
		
		return result;
	}
	
	public BmoCredit getByWFlowId(int wFlowId) throws SFException {
		return (BmoCredit)super.getBy(wFlowId, bmoCredit.getWFlowId().getName());
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCredit = (BmoCredit)bmObject;
//		BmFilter filterByCredit = new BmFilter();

		// Revisar si esta en revision para proceder a eliminar
		if (bmoCredit.getStatus().toChar() == BmoCredit.STATUS_REVISION) {	
			
			//Eliminar los avales
			deleteGuarantee(pmConn, bmoCredit, bmUpdateResult);

			super.delete(pmConn, bmoCredit, bmUpdateResult);

			// Eliminar pedidos
			if (!bmUpdateResult.hasErrors()) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				if (bmoCredit.getOrderId().toInteger() > 0) {
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoCredit.getOrderId().toInteger());

					pmOrder.delete(pmConn,  bmoOrder, bmUpdateResult);
				}
				
//				// Eliminar flujos
//				PmWFlow pmWFlow = new PmWFlow(getSFParams());
//				BmoWFlow bmoWFlow = new BmoWFlow();
//				// Busca el WFlow por PEDIDO
//				filterByCredit.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoCredit.getOrderId().toInteger());			
//				BmFilter filterWFlowCategory = new BmFilter();
//				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoCredit.getProgramCode());
//				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
//				filterList.add(filterByCredit);
//				filterList.add(filterWFlowCategory);
//				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
//				while (wFlowList.hasNext()) {
//					bmoWFlow = (BmoWFlow)wFlowList.next();
//					pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
//				}
			}

		} else {
			bmUpdateResult.addError(bmoCredit.getStatus().getName(), "No se puede eliminar el credito - no está En Revisión.");
		}

		return bmUpdateResult;
	}
	
	public void deleteGuarantee(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmCreditGuarantee pmCreditGuarantee = new PmCreditGuarantee(getSFParams());
		BmoCreditGuarantee bmoCreditGuarantee = new BmoCreditGuarantee();
		bmFilter.setValueFilter(bmoCreditGuarantee.getKind(), bmoCreditGuarantee.getCreditId(), bmoCredit.getId());
		ListIterator<BmObject> listCreditGuarantee = pmCreditGuarantee.list(pmConn, bmFilter).listIterator();
		while (listCreditGuarantee.hasNext()) {
			bmoCreditGuarantee = (BmoCreditGuarantee)listCreditGuarantee.next();
			pmCreditGuarantee.delete(pmConn,  bmoCreditGuarantee, bmUpdateResult);
		}
	}
	public boolean validateSalesManNotEquals(PmConn pmConn,int customer,int creditGuaratiOne,int  creditGuaratiTwo) throws SFPmException {
		int customerSalesmanId = 0,creditGuaratiOneSalesmanId = 0,creditGuaratiTwoSalesmanId = 0 ;
		boolean result = false;
		String sql = "SELECT cust_salesmanid FROM customers WHERE cust_customerid = " + customer;		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			customerSalesmanId = pmConn.getInt("cust_salesmanid");
		}
		
		sql = "SELECT cust_salesmanid FROM customers WHERE cust_customerid = " + creditGuaratiOne;		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			creditGuaratiOneSalesmanId = pmConn.getInt("cust_salesmanid");
		}
		
		sql = "SELECT cust_salesmanid FROM customers WHERE cust_customerid = " + creditGuaratiTwo;		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			creditGuaratiTwoSalesmanId = pmConn.getInt("cust_salesmanid");
		}
		
		if((customerSalesmanId == creditGuaratiOneSalesmanId) && (creditGuaratiOneSalesmanId == creditGuaratiTwoSalesmanId)) {
			result = true;
		}
		return result;
	}
	public boolean validateSalesManNotEquals(PmConn pmConn,int customer,int creditGuaratiOne) throws SFPmException {
		int customerSalesmanId = 0,creditGuaratiOneSalesmanId = 0;
		boolean result = false;
		String sql = "SELECT cust_salesmanid FROM customers WHERE cust_customerid = " + customer;		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			customerSalesmanId = pmConn.getInt("cust_salesmanid");
		}
		
		sql = "SELECT cust_salesmanid FROM customers WHERE cust_customerid = " + creditGuaratiOne;		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			creditGuaratiOneSalesmanId = pmConn.getInt("cust_salesmanid");
		}		
		
		
		if(customerSalesmanId == creditGuaratiOneSalesmanId) {
			result = true;
		}
		return result;
	}
	
}
