/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmOpportunity;
import com.flexwm.server.cm.PmQuote;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;


public class PmConsultancy extends PmObject {
	BmoConsultancy bmoConsultancy;

	public PmConsultancy(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoConsultancy = new BmoConsultancy();
		setBmObject(bmoConsultancy);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoConsultancy.getOrderTypeId(), bmoConsultancy.getBmoOrderType()),
				new PmJoin(bmoConsultancy.getCustomerId(), bmoConsultancy.getBmoCustomer()),
				new PmJoin(bmoConsultancy.getBmoCustomer().getSalesmanId(), bmoConsultancy.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoConsultancy.getBmoCustomer().getBmoUser().getAreaId(), bmoConsultancy.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoConsultancy.getBmoCustomer().getBmoUser().getLocationId(), bmoConsultancy.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoConsultancy.getBmoCustomer().getTerritoryId(), bmoConsultancy.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoConsultancy.getBmoCustomer().getReqPayTypeId(), bmoConsultancy.getBmoCustomer().getBmoReqPayType()),

				new PmJoin(bmoConsultancy.getWFlowId() , bmoConsultancy.getBmoWFlow()),
				new PmJoin(bmoConsultancy.getBmoWFlow().getWFlowPhaseId(), bmoConsultancy.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoConsultancy.getBmoWFlow().getWFlowFunnelId(), bmoConsultancy.getBmoWFlow().getBmoWFlowFunnel()),

				new PmJoin(bmoConsultancy.getWFlowTypeId(), bmoConsultancy.getBmoWFlowType()),
				new PmJoin(bmoConsultancy.getBmoWFlowType().getWFlowCategoryId(), bmoConsultancy.getBmoWFlowType().getBmoWFlowCategory()),

				new PmJoin(bmoConsultancy.getCurrencyId(), bmoConsultancy.getBmoCurrency())

				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de Oportunidads 
		if (getSFParams().restrictData(bmoConsultancy.getProgramCode())) {

			filters = "( cons_userid IN (" +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" cons_orderid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					" AND wflw_callercode = '" + bmoConsultancy.getProgramCode() + "' " + 
					"   ) " +
					" ) " +
					" ) ";
		}

		// Filtro de pedidos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += "( cons_companyid IN (" 
					+ " SELECT uscp_companyid FROM " + formatKind("usercompanies") 
					+  " WHERE uscp_userid = " + loggedUserId + " ) " 
					+ ") ";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += " cons_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoConsultancy = (BmoConsultancy) autoPopulate(pmConn, new BmoConsultancy());

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		if (pmConn.getInt(bmoOrderType.getIdFieldName()) > 0)
			bmoConsultancy.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else
			bmoConsultancy.setBmoOrderType(bmoOrderType);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		if (pmConn.getInt(bmoCustomer.getIdFieldName()) > 0)
			bmoConsultancy.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else
			bmoConsultancy.setBmoCustomer(bmoCustomer);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoConsultancy.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoConsultancy.setBmoWFlowType(bmoWFlowType);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoConsultancy.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoConsultancy.setBmoWFlow(bmoWFlow);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int UserId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (UserId > 0) bmoConsultancy.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoConsultancy.setBmoUser(bmoUser);

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoConsultancy.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoConsultancy.setBmoCurrency(bmoCurrency);

		return bmoConsultancy;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoConsultancy bmoConsultancy = (BmoConsultancy)bmObject;
		// Validar partida presupuestal
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (!(bmoConsultancy.getBudgetItemId().toInteger() > 0)) 
				bmUpdateResult.addError(bmoConsultancy.getBudgetItemId().getName(), "Seleccione una Partida.");

//			if (!(bmoConsultancy.getAreaId().toInteger() > 0)) 
//				bmUpdateResult.addError(bmoConsultancy.getAreaId().getName(), "Seleccione una Departamento.");
		}

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoConsultancy bmoConsultancy = (BmoConsultancy)bmObject;
		BmoConsultancy bmoConsultancyPrev = (BmoConsultancy)bmObject;
		PmConsultancy  pmConsultancyPrev = new PmConsultancy(getSFParams());
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";
		boolean newRecord  = false;

		// Datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoConsultancy.getCustomerId().toInteger());

		// Revisa fechas
		if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
				bmoConsultancy.getEndDate().toString(), bmoConsultancy.getStartDate().toString()))
			bmUpdateResult.addError(bmoConsultancy.getEndDate().getName(), 
					"No puede ser Anterior a " + bmoConsultancy.getStartDate().getLabel());

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoConsultancy.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoConsultancy, bmUpdateResult);

			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoConsultancy.getCompanyId().toInteger(),
					bmoConsultancy.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoConsultancy.CODE_PREFIX
					);
			bmoConsultancy.getCode().setValue(code);
		} else {
			bmoConsultancyPrev = (BmoConsultancy)pmConsultancyPrev.get(bmoConsultancy.getId());
		}

		// Crear pedido si no existe
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = new BmoOrder();
		int wflowId = 0;

		if (!(bmoConsultancy.getOrderId().toInteger() > 0)) {
			// Si no existe el pedido, crearlo
			createOrderFromConsultancy(pmConn, pmOrder, bmoOrder, bmoConsultancy, bmUpdateResult);
		} else {
			// Actualiza info del pedido, porque es existente
			bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoConsultancy.getOrderId().toInteger());
			wflowId = bmoOrder.getWFlowId().toInteger();
			
			// Validar que si esta cambiando el mercado, verifique si hay lineas con productos
			if (bmoConsultancyPrev.getMarketId().toInteger() > 0) {
				if (bmoOrder.getMarketId().toInteger() != bmoConsultancyPrev.getMarketId().toInteger()) {
					if (pmOrder.existProducts(pmConn, bmoOrder.getId()))
						bmUpdateResult.addError(bmoConsultancy.getMarketId().getName(), "El Pedido tiene Productos con Precios asignados al(la) " + getSFParams().getFieldFormTitle(bmoConsultancy.getMarketId()) + " actual. "
										+ "Debe eliminarlos para poder cambiar el(la) " + getSFParams().getFieldFormTitle(bmoConsultancy.getMarketId()) + "." );
				}
			}

			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
				// Sincronizar datos
				bmoOrder.getName().setValue(bmoConsultancy.getName().toString());
				bmoOrder.getUserId().setValue(bmoConsultancy.getUserId().toInteger());				
				bmoOrder.getLockStart().setValue(bmoConsultancy.getStartDate().toString());
				bmoOrder.getLockEnd().setValue(bmoConsultancy.getEndDate().toString());
				bmoOrder.getCurrencyId().setValue(bmoConsultancy.getCurrencyId().toInteger());
				bmoOrder.getCurrencyParity().setValue(bmoConsultancy.getCurrencyParity().toDouble());
				bmoOrder.getCustomerId().setValue(bmoConsultancy.getCustomerId().toInteger());
				bmoOrder.getCompanyId().setValue(bmoConsultancy.getCompanyId().toInteger());				
				bmoOrder.getMarketId().setValue(bmoConsultancy.getMarketId().toInteger());
				bmoOrder.getCustomerContactId().setValue(bmoConsultancy.getCustomerContactId().toString());
				bmoOrder.getCustomerRequisition().setValue(bmoConsultancy.getCustomerRequisition().toString());
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoOrder.getDefaultBudgetItemId().setValue(bmoConsultancy.getBudgetItemId().toInteger());
					bmoOrder.getDefaultAreaId().setValue(bmoConsultancy.getAreaId().toInteger());	
				}
				bmoOrder.getStatus().setValue(bmoConsultancy.getStatus().toString());				
				bmoOrder.getAmount().setValue(bmoConsultancy.getAmount().toDouble());
				bmoOrder.getTax().setValue(bmoConsultancy.getTax().toDouble());
				bmoOrder.getTotal().setValue(bmoConsultancy.getTotal().toDouble());
				bmoOrder.getPayments().setValue(bmoConsultancy.getPayments().toDouble());
				bmoOrder.getBalance().setValue(bmoConsultancy.getBalance().toDouble());
				bmoOrder.getTags().setValue(bmoConsultancy.getTags().toString());
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
				//  Si la cotizacion ya esta autorizada, no dejar cambiar los mismos campos de la venta y el pedido
			} else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
				if (!bmoOrder.getName().toString().equals(bmoConsultancy.getName().toString())) {
					bmUpdateResult.addError(bmoConsultancy.getName().getName() , "No se puede cambiar el " + getSFParams().getFieldFormTitle(bmoConsultancy.getName()) + ", el Pedido ya está Autorizado.");
				} else if  (bmoOrder.getCustomerId().toInteger() != bmoConsultancy.getCustomerId().toInteger()) {
					bmUpdateResult.addError(bmoConsultancy.getCustomerId().getName() , "No se puede cambiar el " + getSFParams().getFieldFormTitle(bmoConsultancy.getCustomerId()) + ", el Pedido ya está Autorizado.");
				} else if  (bmoOrder.getUserId().toInteger() != bmoConsultancy.getUserId().toInteger()) {
					bmUpdateResult.addError(bmoConsultancy.getUserId().getName() , "No se puede cambiar el " + getSFParams().getFieldFormTitle(bmoConsultancy.getUserId()) + ", el Pedido ya está Autorizado.");
				} else if (!bmoOrder.getLockStart().toString().equals(bmoConsultancy.getStartDate().toString())) {
					bmUpdateResult.addError(bmoConsultancy.getStartDate().getName() , "No se puede cambiar la " + getSFParams().getFieldFormTitle(bmoConsultancy.getStartDate()) + ", el Pedido ya está Autorizado.");
				} else if (!bmoOrder.getLockEnd().toString().equals(bmoConsultancy.getEndDate().toString())) {
					bmUpdateResult.addError(bmoConsultancy.getEndDate().getName() , "No se puede cambiar la " + getSFParams().getFieldFormTitle(bmoConsultancy.getEndDate()) + ", el Pedido ya está Autorizado.");
				} else if (bmoOrder.getCurrencyId().toInteger() == bmoConsultancy.getCurrencyId().toInteger()
						&& bmoOrder.getCurrencyParity().toDouble() != bmoConsultancy.getCurrencyParity().toDouble()) {
					bmUpdateResult.addError(bmoConsultancy.getCurrencyParity().getName() , "No se puede cambiar el " + getSFParams().getFieldFormTitle(bmoConsultancy.getCurrencyParity()) + ", el Pedido ya está Autorizado.");
				} else if (bmoOrder.getCurrencyId().toInteger() != bmoConsultancy.getCurrencyId().toInteger()
						&& bmoOrder.getCurrencyParity().toDouble() == bmoConsultancy.getCurrencyParity().toDouble()) {
					bmUpdateResult.addError(bmoConsultancy.getCurrencyId().getName() , "No se puede cambiar la " + getSFParams().getFieldFormTitle(bmoConsultancy.getCurrencyId()) + ", el Pedido ya está Autorizado.");
				} else if  (bmoOrder.getCurrencyId().toInteger() != bmoConsultancy.getCurrencyId().toInteger()
						&& bmoOrder.getCurrencyParity().toDouble() != bmoConsultancy.getCurrencyParity().toDouble()) {
					bmUpdateResult.addError(bmoConsultancy.getCurrencyId().getName() , "No se puede cambiar la " + getSFParams().getFieldFormTitle(bmoConsultancy.getCurrencyId()) + "/" + getSFParams().getFieldFormTitle(bmoConsultancy.getCurrencyParity()) + ", el Pedido ya está Autorizado.");
				} else if  (bmoOrder.getCompanyId().toInteger() != bmoConsultancy.getCompanyId().toInteger()) {
					bmUpdateResult.addError(bmoConsultancy.getCompanyId().getName() , "No se puede cambiar la " + getSFParams().getFieldFormTitle(bmoConsultancy.getCompanyId()) + ", el Pedido ya está Autorizado.");
				} 
			}
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			// Asigna el estatus al pedido
			if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_CANCELLED )) {
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_CANCELLED);
				if (bmoOrder.getWFlowId().toInteger() > 0)
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "La " + getSFParams().getProgramFormTitle(bmoConsultancy) + " se guardó como Cancelada.");
				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

			} else if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_FINISHED)) {
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);

				if (bmoOrder.getWFlowId().toInteger() > 0)						
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "La " + getSFParams().getProgramFormTitle(bmoConsultancy) + " se guardó como Finalizada.");

				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

			} else if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_REVISION)) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "La " + getSFParams().getProgramFormTitle(bmoConsultancy) + " se guardó como Revisión.");
			} else if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_AUTHORIZED)) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "La " + getSFParams().getProgramFormTitle(bmoConsultancy) + " se guardó como Autorizada.");
			}
		}
		// Asigna valores del pedido
		bmoConsultancy.getOrderId().setValue(bmoOrder.getId());
		if (newRecord) {
			bmoConsultancy.getAmount().setValue(bmoOrder.getAmount().toDouble());
			bmoConsultancy.getTax().setValue(bmoOrder.getTax().toDouble());
			bmoConsultancy.getTotal().setValue(bmoOrder.getTotal().toDouble());
			bmoConsultancy.getPayments().setValue(bmoOrder.getPayments().toDouble());
			bmoConsultancy.getBalance().setValue(bmoOrder.getBalance().toDouble());
		}
		// Asigna ID del flujo recien creado
		if (wflowId > 0)
			bmoConsultancy.getWFlowId().setValue(wflowId);
		else
			bmoConsultancy.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
		
//		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoConsultancy, bmUpdateResult);
			// Forzar el ID del resultado que sea de la consultoria, no de otra clase
			bmUpdateResult.setId(bmoConsultancy.getId());
//		}
		
		// No hay errores guardar
		if (!bmUpdateResult.hasErrors()) {
			// Actualizar id de claves del programa por empresa
			if (newRecord) {
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoConsultancy.getCompanyId().toInteger(), 
						bmoConsultancy.getProgramCode().toString());
			}

			// Actualiza estatus del cliente
			if(pmCustomer.validateDataCustomer(pmConn, bmoOrder.getCustomerId().toInteger(),bmoOrder.getCustomerId(), bmUpdateResult)) {
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			}
		}

		// Guardar cambios por ultima vez
		super.save(pmConn, bmoConsultancy, bmUpdateResult);

		return bmUpdateResult;
	}
	
	@Override
	public BmUpdateResult saveSimple(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoConsultancy bmoConsultancy = (BmoConsultancy)bmObject;
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(getSFParams());
		
		if (bmoConsultancy.getOrderId().toInteger() > 0) {
			bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoConsultancy.getOrderId().toInteger());
			
			bmoOrder.getName().setValue(bmoConsultancy.getName().toString());
			bmoOrder.getUserId().setValue(bmoConsultancy.getUserId().toInteger());				
			bmoOrder.getLockStart().setValue(bmoConsultancy.getStartDate().toString());
			bmoOrder.getLockEnd().setValue(bmoConsultancy.getEndDate().toString());
			bmoOrder.getCurrencyId().setValue(bmoConsultancy.getCurrencyId().toInteger());
			bmoOrder.getCurrencyParity().setValue(bmoConsultancy.getCurrencyParity().toDouble());
			bmoOrder.getCustomerId().setValue(bmoConsultancy.getCustomerId().toInteger());
			bmoOrder.getCompanyId().setValue(bmoConsultancy.getCompanyId().toInteger());				
			bmoOrder.getMarketId().setValue(bmoConsultancy.getMarketId().toInteger());		
			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				bmoOrder.getDefaultBudgetItemId().setValue(bmoConsultancy.getBudgetItemId().toInteger());				
				bmoOrder.getDefaultAreaId().setValue(bmoConsultancy.getAreaId().toInteger());	
			}
			bmoOrder.getStatus().setValue(bmoConsultancy.getStatus().toString());
			bmoOrder.getAmount().setValue(bmoConsultancy.getAmount().toDouble());
			bmoOrder.getTax().setValue(bmoConsultancy.getTax().toDouble());
			bmoOrder.getTotal().setValue(bmoConsultancy.getTotal().toDouble());
			bmoOrder.getPayments().setValue(bmoConsultancy.getPayments().toDouble());
			bmoOrder.getBalance().setValue(bmoConsultancy.getBalance().toDouble());
			bmoOrder.getTags().setValue(bmoConsultancy.getTags().toString());
			bmoOrder.getCustomerContactId().setValue(bmoConsultancy.getCustomerContactId().toString());
			bmoOrder.getCustomerRequisition().setValue(bmoConsultancy.getCustomerRequisition().toString());
			pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
			
		} else {
			bmUpdateResult.addError(bmoConsultancy.getCode().getName(), "No se encontró un Pedido ligado a la Venta.");
		}
		// Guardar cambios por ultima vez
		super.save(pmConn, bmoConsultancy, bmUpdateResult);
		
		return bmUpdateResult;
	}
		// Crear pedido a partir de venta de servicio
	private void createOrderFromConsultancy(PmConn pmConn, PmOrder pmOrder, BmoOrder bmoOrder, BmoConsultancy bmoConsultancy, BmUpdateResult bmUpdateResult) throws SFException {
		
		bmoOrder.getCode().setValue(bmoConsultancy.getCode().toString());
		bmoOrder.getName().setValue(bmoConsultancy.getName().toString());
		bmoOrder.getDescription().setValue(bmoConsultancy.getDescription().toString());
		bmoOrder.getAmount().setValue(0);
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getOrderTypeId().setValue(bmoConsultancy.getOrderTypeId().toInteger());
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoConsultancy.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoConsultancy.getEndDate().toString());
		bmoOrder.getTags().setValue(bmoConsultancy.getTags().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoConsultancy.getCustomerId().toInteger());
		bmoOrder.getUserId().setValue(bmoConsultancy.getUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoConsultancy.getWFlowTypeId().toInteger());
		bmoOrder.getCompanyId().setValue(bmoConsultancy.getCompanyId().toInteger());
		bmoOrder.getMarketId().setValue(bmoConsultancy.getMarketId().toInteger());
		bmoOrder.getCustomerContactId().setValue(bmoConsultancy.getCustomerContactId().toString());
		bmoOrder.getCustomerRequisition().setValue(bmoConsultancy.getCustomerRequisition().toString());

		// Obtener la moneda de la oportunidad si existe, en caso contrario de la configuracion
		if (bmoConsultancy.getOpportunityId().toInteger() > 0) {
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoConsultancy.getOpportunityId().toInteger());

			bmoOrder.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
			bmoOrder.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
			bmoOrder.getQuoteId().setValue(bmoOpportunity.getQuoteId().toInteger());
			bmoOrder.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
			bmoOrder.getOpportunityId().setValue(bmoOpportunity.getId());
			bmoOrder.getPayConditionId().setValue(bmoOpportunity.getPayConditionId().toInteger());

			// Obtener cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = new BmoQuote();
			bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

			bmoOrder.getTax().setValue(bmoQuote.getTax().toDouble());
			bmoOrder.getTaxApplies().setValue(bmoQuote.getTaxApplies().toString());
			bmoOrder.getCompanyId().setValue(bmoQuote.getCompanyId().toInteger());
			bmoOrder.getMarketId().setValue(bmoQuote.getMarketId().toInteger());

			bmoOrder.getComments().setValue(bmoQuote.getComments().toString());
			bmoOrder.getCoverageParity().setValue(bmoQuote.getCoverageParity().toBoolean());
			bmoOrder.getCustomerContactId().setValue(bmoOpportunity.getCustomerContactId().toString());
			bmoOrder.getCustomerRequisition().setValue(bmoQuote.getCustomerRequisition().toString());

			// Si esta habilitado el Control presp. pasar por defecto la partida y el dpto. del tipo de pedido
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				if (!(bmoQuote.getBudgetItemId().toInteger() > 0))
					bmUpdateResult.addError(bmoQuote.getBudgetItemId().getName(), "Debe seleccionar una Partida Presupuestal en la Oportunidad.");
				else {
					bmoOrder.getDefaultBudgetItemId().setValue(bmoQuote.getBudgetItemId().toInteger());
					bmoOrder.getDefaultAreaId().setValue(bmoQuote.getAreaId().toInteger());
				}
			}

		} else {
			bmoOrder.getCurrencyId().setValue(bmoConsultancy.getCurrencyId().toInteger());
			bmoOrder.getCurrencyParity().setValue(bmoConsultancy.getCurrencyParity().toDouble());

			
			// Validar partida presupuestal
			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				bmoOrder.getDefaultBudgetItemId().setValue(bmoConsultancy.getBudgetItemId().toInteger());
				bmoOrder.getDefaultAreaId().setValue(bmoConsultancy.getAreaId().toInteger());
				
				if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "Seleccione una Partida.");
			}

		}
		if (!bmUpdateResult.hasErrors()) {
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		}
		
		// Si el proyecto proviene de una oportunidad
		if (bmoConsultancy.getOpportunityId().toInteger() > 0) {
			// Obtener los items de las cotizaciones
			if (!bmUpdateResult.hasErrors()) {
				pmOrder.createOrderItemsFromQuote(pmConn, bmoOrder, bmUpdateResult);
			}

			// Crear las CxC necesarias para sustentar el saldo automaticamente
			if (!bmUpdateResult.hasErrors()) {
				pmOrder.createOrderRaccountsAuto(pmConn, bmoOrder, bmoConsultancy.getOpportunityId().toInteger(), bmUpdateResult);
			}

			// Obtener los usuarios de WFlow de la oportunidad
			if (!bmUpdateResult.hasErrors())
				pmOrder.createWFlowUsersFromOpportunity(pmConn, bmoOrder, bmUpdateResult);

			// Obtener los documentos de WFlow de la oportunidad
			if (!bmUpdateResult.hasErrors())
				pmOrder.createWFlowDocumentsFromOpportunity(pmConn, bmoOrder, bmUpdateResult);
			
			new PmOpportunity(getSFParams()).updateRequisitions(pmConn, bmoConsultancy.getOpportunityId().toInteger(), bmoOrder, bmUpdateResult);
		}
//		if (bmUpdateResult.hasErrors()) {
//			bmUpdateResult.addError(bmoOrder.getIdFieldName(), "Existen Errores: " + bmUpdateResult.errorsToString());
//		}
	}

	public boolean consultancyOpportunityExists(PmConn pmConn, int opportunityId) throws SFPmException {
		pmConn.doFetch("SELECT cons_opportunityid FROM " + formatKind("consultancies") + " WHERE cons_opportunityid = " + opportunityId);
		return pmConn.next();
	}

	public boolean consultancyOrderExists(PmConn pmConn, int orderId) throws SFPmException {
		pmConn.doFetch("SELECT cons_orderid FROM " + formatKind("consultancies") + " WHERE cons_orderid= " + orderId);
		return pmConn.next();
	}

	// Crea el pedido a partir de una oportunidad
	public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, BmUpdateResult bmUpdateResult) throws SFException {
		BmoConsultancy bmoConsultancy = new BmoConsultancy();
		bmoConsultancy.getName().setValue(bmoOpportunity.getName().toString());
		bmoConsultancy.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
		bmoConsultancy.getEndDate().setValue(bmoOpportunity.getEndDate().toString());
		bmoConsultancy.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoConsultancy.getUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoConsultancy.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoConsultancy.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoConsultancy.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
		bmoConsultancy.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoConsultancy.getCustomerRequisition().setValue(bmoOpportunity.getCustomerRequisition().toString());
		bmoConsultancy.getCustomerContactId().setValue(bmoOpportunity.getCustomerContactId().toInteger());

		// Si esta habilitado el Control presp. pasar por defecto la partida y el dpto.
		if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
			if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0))
				bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(),
						"Seleccione una Partida Presp. en la Oportunidad.");
			else {
				bmoConsultancy.getBudgetItemId().setValue(bmoOpportunity.getBudgetItemId().toInteger());
				bmoConsultancy.getAreaId().setValue(bmoOpportunity.getAreaId().toInteger());
			}
		}

		// Obtener cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = new BmoQuote();
		bmoQuote = (BmoQuote) pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

		bmoConsultancy.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
		bmoConsultancy.getCurrencyParity().setValue(bmoQuote.getCurrencyParity().toDouble());
		bmoConsultancy.getCompanyId().setValue(bmoQuote.getCompanyId().toInteger());
		bmoConsultancy.getMarketId().setValue(bmoQuote.getMarketId().toInteger());
		if (bmoQuote.getDescription().toString().length() > 0)
			bmoConsultancy.getDescription().setValue(bmoQuote.getDescription().toString());
		else
			bmoConsultancy.getDescription().setValue(bmoOpportunity.getDescription().toString());

		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		BmoOrderType bmoOrderType = new BmoOrderType();
		bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoOpportunity.getOrderTypeId().toInteger());
		if(bmoOrderType.getAreaDefaultDetail().toInteger() > 0) {
			bmoConsultancy.getAreaIdScrum().setValue(bmoOrderType.getAreaDefaultDetail().toInteger());
		}
		if(!bmoOrderType.getStatusDefaultDetail().equals("")) {
			bmoConsultancy.getStatusScrum().setValue(bmoOrderType.getStatusDefaultDetail().toChar());
		}

		bmoConsultancy.getCloseDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoConsultancy.getOrderDate().setValue(bmoConsultancy.getStartDate().toString());

		// Almacena venta preliminar
		this.save(pmConn, bmoConsultancy, bmUpdateResult);

		return bmUpdateResult;
	}
	
	// Crear venta de servicio a partir de pedido (se usa en renovacion de pedidos)
	public BmUpdateResult createConsultancyFromOrderRenew(PmConn pmConn, BmoConsultancy bmoConsultancy, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		
//		bmoConsultancy.getCode().setValue(bmoOrder.getCode().toString());
		bmoConsultancy.getName().setValue(bmoOrder.getName().toString());
		bmoConsultancy.getDescription().setValue(bmoOrder.getDescription().toString());
		bmoConsultancy.getOrderTypeId().setValue(bmoOrder.getOrderTypeId().toInteger());
		bmoConsultancy.getWFlowTypeId().setValue(bmoOrder.getWFlowTypeId().toInteger());
//		bmoConsultancy.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
		bmoConsultancy.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
		bmoConsultancy.getUserId().setValue(bmoOrder.getUserId().toInteger());
		bmoConsultancy.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
		bmoConsultancy.getStartDate().setValue(bmoOrder.getLockStart().toString());
		bmoConsultancy.getEndDate().setValue(bmoOrder.getLockEnd().toString());
		bmoConsultancy.getStatus().setValue(bmoOrder.getStatus().toString());
		bmoConsultancy.getMarketId().setValue(bmoOrder.getMarketId().toInteger());
		bmoConsultancy.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
		bmoConsultancy.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
		bmoConsultancy.getCustomerRequisition().setValue(bmoOrder.getCustomerRequisition().toDouble());
		bmoConsultancy.getCustomerContactId().setValue(bmoOrder.getCustomerContactId().toDouble());
		// Si existe ya la venta, asignar el pedido
		if (bmoConsultancy.getId() > 0)
			bmoConsultancy.getOrderId().setValue(bmoOrder.getId());
		
		bmoConsultancy.getAmount().setValue(0);
		bmoConsultancy.getTax().setValue(0);
		bmoConsultancy.getTotal().setValue(0);
		bmoConsultancy.getPayments().setValue(0);
		bmoConsultancy.getBalance().setValue(0);
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
			bmoConsultancy.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
			bmoConsultancy.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
		}
//		bmoConsultancy.getTags().setValue(bmoOrder.getTags().toString());
		
		bmoConsultancy.getOrderDate().setValue(bmoOrder.getLockStart().toString());
		
		if (bmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger() > 0) {
			bmoConsultancy.getAreaIdScrum().setValue(bmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger());
		}
		if (!bmoOrder.getBmoOrderType().getStatusDefaultDetail().equals("")) {
			bmoConsultancy.getStatusScrum().setValue(bmoOrder.getBmoOrderType().getStatusDefaultDetail().toChar());
		}

		// Almacena venta preliminar
		this.save(pmConn, bmoConsultancy, bmUpdateResult);

		return bmUpdateResult;
	}

	// SOLO Actualiza montos desde el pedido
	public BmUpdateResult copyAmountsFromOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {

		PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
		// Valida que exista la orden de servicio
		if (consultancyOrderExists(pmConn, bmoOrder.getId())) {
			bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
			bmoConsultancy.getAmount().setValue(bmoOrder.getAmount().toDouble());
			bmoConsultancy.getTax().setValue(bmoOrder.getTax().toDouble());
			bmoConsultancy.getTotal().setValue(bmoOrder.getTotal().toDouble());
			bmoConsultancy.getPayments().setValue(bmoOrder.getPayments().toDouble());
			bmoConsultancy.getBalance().setValue(bmoOrder.getBalance().toDouble());

			return super.save(pmConn, bmoConsultancy, bmUpdateResult);
		} else  {
			return bmUpdateResult;
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoConsultancy = (BmoConsultancy)bmObject;

		if (bmoConsultancy.getStatus().toChar() == BmoConsultancy.STATUS_REVISION) {

			// Eliminar proyecto
			super.delete(pmConn, bmoConsultancy, bmUpdateResult);

			// Eliminar pedidos
			if (!bmUpdateResult.hasErrors()) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				if (bmoConsultancy.getOrderId().toInteger() > 0) {
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoConsultancy.getOrderId().toInteger());

					pmOrder.delete(pmConn,  bmoOrder, bmUpdateResult);
				}
			}

		} else {
			bmUpdateResult.addError(bmoConsultancy.getStatus().getName(), "No se puede eliminar, el/la " + getSFParams().getProgramFormTitle(bmoConsultancy) + " está " + bmoConsultancy.getStatus().getSelectedOption().getLabel());
		}

		return bmUpdateResult;
	}
	
	// Armado de sentencias SQL
//	public String getInsertSql(BmoConsultancy bmoConsultancy, ArrayList<BmField> fields) {
//		String sql = "";
//		Iterator<BmField> fieldIterator = fields.iterator();
//		int fieldCount = fields.size();
//
//		sql = "INSERT INTO " + formatKind(bmoConsultancy.getKind()) + " ( ";
//
//		int i = 1;
//		while (fieldIterator.hasNext()) {
//			BmField bmField = (BmField) fieldIterator.next();
//			sql += bmField.getName();
//			if (i < fieldCount) sql += ", ";
//			i++;
//		}
//
//		sql += ") VALUES ( ";
//
//		for (int j = 1; j <= fieldCount; j++) {
//			sql += "?";
//			if (j < fieldCount) sql += ",";
//		}
//		sql += ")";
//
//		return sql;
//	}	

}
