/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;

import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.co.PmPropertyModel;
import com.flexwm.server.co.PmPropertySale;
import com.flexwm.server.ev.PmVenue;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.op.PmConsultancy;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderBlockDate;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.op.PmRequisition;
//import com.flexwm.server.op.PmProduct;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCategoryForecast;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerEmail;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoEstimationGroup;
//import com.flexwm.shared.cm.BmoEstimationRFQItem;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoQuote;
//import com.flexwm.shared.cm.BmoQuoteGroup;
//import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.cm.BmoQuoteProperty;
//import com.flexwm.shared.cm.BmoRFQU;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
//import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFormat;

import com.symgae.shared.sf.BmoUser;


public class PmOpportunity extends PmObject {
	BmoOpportunity bmoOpportunity;

	public PmOpportunity(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOpportunity = new BmoOpportunity();
		setBmObject(bmoOpportunity);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOpportunity.getUserId(), bmoOpportunity.getBmoUser()),
				new PmJoin(bmoOpportunity.getOrderTypeId(), bmoOpportunity.getBmoOrderType()),
				new PmJoin(bmoOpportunity.getCurrencyId(), bmoOpportunity.getBmoCurrency()),
				new PmJoin(bmoOpportunity.getCustomerId(), bmoOpportunity.getBmoCustomer()),
				new PmJoin(bmoOpportunity.getBmoUser().getAreaId(), bmoOpportunity.getBmoUser().getBmoArea()),
				new PmJoin(bmoOpportunity.getBmoUser().getLocationId(), bmoOpportunity.getBmoUser().getBmoLocation()),
				new PmJoin(bmoOpportunity.getBmoCustomer().getTerritoryId(), bmoOpportunity.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoOpportunity.getBmoCustomer().getReqPayTypeId(), bmoOpportunity.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoOpportunity.getWFlowTypeId(), bmoOpportunity.getBmoWFlowType()),
				new PmJoin(bmoOpportunity.getBmoWFlowType().getWFlowCategoryId(), bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoOpportunity.getWFlowId(), bmoOpportunity.getBmoWFlow()),
				new PmJoin(bmoOpportunity.getBmoWFlow().getWFlowPhaseId(), bmoOpportunity.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoOpportunity.getBmoWFlow().getWFlowFunnelId(), bmoOpportunity.getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoOpportunity.getVenueId(), bmoOpportunity.getBmoVenue()),
				new PmJoin(bmoOpportunity.getBmoVenue().getCityId(), bmoOpportunity.getBmoVenue().getBmoCity()),
				new PmJoin(bmoOpportunity.getBmoVenue().getBmoCity().getStateId(), bmoOpportunity.getBmoVenue().getBmoCity().getBmoState()),
				new PmJoin(bmoOpportunity.getBmoVenue().getBmoCity().getBmoState().getCountryId(), bmoOpportunity.getBmoVenue().getBmoCity().getBmoState().getBmoCountry())
				))); 
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de Oportunidads 
		if (getSFParams().restrictData(bmoOpportunity.getProgramCode())) {

			filters = "( oppo_userid IN (" +
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
					" oppo_opportunityid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					" AND wflw_callercode = 'OPPO' " + 
					"   ) " +
					" ) " +
					" ) ";
		}

		// Filtro de oportunidades de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( oppo_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " oppo_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoOpportunity bmoOpportunity = (BmoOpportunity) autoPopulate(pmConn, new BmoOpportunity());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (customerId > 0) bmoOpportunity.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoCustomer(bmoCustomer);

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
		if (orderTypeId > 0) bmoOpportunity.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoOrderType(bmoOrderType);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoOpportunity.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoUser(bmoUser);

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoOpportunity.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoCurrency(bmoCurrency);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int wFlowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (wFlowTypeId > 0) bmoOpportunity.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoWFlowType(bmoWFlowType);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoOpportunity.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoWFlow(bmoWFlow);
		
		//BmoVenue
		BmoVenue bmoVenue = new BmoVenue();
		int venueId = (int)pmConn.getInt(bmoVenue.getIdFieldName());
		if (venueId > 0) bmoOpportunity.setBmoVenue((BmoVenue) new PmVenue(getSFParams()).populate(pmConn));
		else bmoOpportunity.setBmoVenue(bmoVenue);

		return bmoOpportunity;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		
		BmoOpportunity bmoOpportunity = (BmoOpportunity)bmObject;
		BmoOpportunity bmoOpportunityPrev = (BmoOpportunity)bmObject;	
		PmOpportunity pmOpportunityPrev = new PmOpportunity(getSFParams());
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";

		boolean newRecord = false;
		// Obtiene datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOpportunity.getCustomerId().toInteger());

		// Obtiene datos del tipo de flujo
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoOpportunity.getWFlowTypeId().toInteger());

		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		BmoWFlow bmoWFlow = new BmoWFlow();

		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoOpportunity.setBmoOrderType((BmoOrderType)pmOrderType.get(pmConn, bmoOpportunity.getOrderTypeId().toInteger()));
	
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			if (!bmoOpportunity.getBmoOrderType().getRequiredPropertyModel().toBoolean()) {
				if (bmoOpportunity.getName().toString().equals(""))
					bmUpdateResult.addError(bmoOpportunity.getName().getName(), "El Nombre no debe estar vacío.");
			}
		}
		
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoOpportunity.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoOpportunity, bmUpdateResult);
			bmoOpportunity.setId(bmUpdateResult.getId());
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoOpportunity.getCompanyId().toInteger(),
					bmoOpportunity.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoOpportunity.CODE_PREFIX
					);
			bmoOpportunity.getCode().setValue(code);

//			//La clave debe tener 4 digitos
//			String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoOpportunity.CODE_PREFIX);			
//			bmoOpportunity.getCode().setValue(code);

			//Fecha lead no sea mayor a la fecha cierre 
			if (!bmUpdateResult.hasErrors())
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoOpportunity.getSaleDate().toString(), bmoOpportunity.getLeadDate().toString()))

				bmUpdateResult.addError(bmoOpportunity.getLeadDate().getName(), 
						"No puede ser mayor a la  " + bmoOpportunity.getLeadDate().getLabel());
			
			// Asigna fecha de expiración, si esta activado
			if (bmoWFlowType.getBmoWFlowCategory().getExpires().toBoolean()) {
				bmoOpportunity.getExpireDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), 
						bmoWFlowType.getBmoWFlowCategory().getExpireDays().toInteger()));
			}

			
			// Si es de tipo de renta y es nuevo, revisar que no haya problemas de bloqueo de fechas
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				PmOrderBlockDate pmOrderBlockDate = new PmOrderBlockDate(getSFParams());

				if (bmoOpportunity.getStartDate().toString().equals("")) 
					bmUpdateResult.addError(bmoOpportunity.getStartDate().getName(), "No se ha capturado la Fecha de Inicio.");

				if (bmoOpportunity.getEndDate().toString().equals(""))
					bmUpdateResult.addError(bmoOpportunity.getEndDate().getName() , "No se ha capturado la Fecha de Fin.");

				if (pmOrderBlockDate.orderInBlockedDates(pmConn, bmoOpportunity.getStartDate().toString(), bmoOpportunity.getEndDate().toString(), bmUpdateResult)) {
					bmUpdateResult.addError(bmoOpportunity.getStartDate().getName(), "Las Fechas no están Disponibles para abrir nuevos Pedidos.");
				}
			} else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
				// Es de tipo compra de inmueble, se asigna fecha y hora actual
				bmoOpportunity.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				bmoOpportunity.getEndDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), 
						bmoWFlowType.getBmoWFlowCategory().getExpireDays().toInteger()));

				if (bmoOpportunity.getBmoOrderType().getRequiredPropertyModel().toBoolean()) {
					if (bmoOpportunity.getPropertyModelId().toInteger() > 0) {
						PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
						BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmConn, bmoOpportunity.getPropertyModelId().toInteger());
						bmoOpportunity.getName().setValue(bmoPropertyModel.getBmoDevelopment().getCode() + " - " + bmoPropertyModel.getName().toString());

						bmoOpportunity.getCompanyId().setValue(bmoPropertyModel.getBmoDevelopment().getCompanyId().toInteger());
					} else {
						bmUpdateResult.addError(bmoOpportunity.getPropertyModelId().getName(), "Debe Seleccionar el Modelo del Inmueble.");
					}
				} else {
					
				}
				
				
			} else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
				// Es de tipo venta, se asigna fecha y hora actual
				if (bmoOpportunity.getStartDate().toString().equals("")) {
					bmoOpportunity.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				}

				// Si esta activado la expiracion coloca los dias, Si no esta activado por defecto pone 1 dia
				if (bmoWFlowType.getBmoWFlowCategory().getExpires().toBoolean()) {
					bmoOpportunity.getEndDate().setValue(SFServerUtil.addDays(getSFParams().getDateTimeFormat(), 
							bmoOpportunity.getStartDate().toString(), 
							bmoWFlowType.getBmoWFlowCategory().getExpireDays().toInteger()));
				} else bmoOpportunity.getEndDate().setValue(SFServerUtil.addDays(getSFParams().getDateTimeFormat(), 
						bmoOpportunity.getStartDate().toString(), 1));

			} 
//			else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SERVICE)) {
//				if (bmoOpportunity.getStartDate().toString().equals("")) 
//					bmUpdateResult.addError(bmoOpportunity.getStartDate().getName(), "No se ha capturado la Fecha de Inicio.");
//
//				if (bmoOpportunity.getEndDate().toString().equals(""))
//					bmUpdateResult.addError(bmoOpportunity.getEndDate().getName() , "No se ha capturado la Fecha de Fin.");
//
//			}
			
			// Si existe oportunidad con el mismo cliente pero diferente vendedor, no deja hacer oportunidad.
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCustomerProtection().toBoolean()){
				if(getCustomerProtection(bmoOpportunity.getUserId().toInteger(), bmoOpportunity.getCustomerId().toInteger()))
					bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El Cliente tiene una Oportunidad Activa");
			}
			
			
		} else {
				if (SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
						bmoOpportunity.getSaleDate().toString(), bmoOpportunity.getLeadDate().toString()))
					bmUpdateResult.addError(bmoOpportunity.getLeadDate().getName(), 
							"No puede ser mayor a la  " + bmoOpportunity.getSaleDate().getLabel());

			bmoOpportunityPrev = (BmoOpportunity)pmOpportunityPrev.get(pmConn, bmoOpportunity.getId());
			if(bmoOpportunityPrev.getStatus().equals(BmoOpportunity.STATUS_WON)) {
				if(!bmoOpportunityPrev.getStatus().equals(bmoOpportunity.getStatus())) {
					if(existOrder(pmConn, bmoOpportunityPrev)) {
						bmUpdateResult.addError(bmoOpportunity.getStatus().getName(),"No se puede cambiar el Estatus, la Oportunidad tiene un Pedido");
					}
				}
			}

			//Verificar que sea de tipo venta de inmueble
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {

				if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION)||(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD))) {
					//Verificar que su estatus es expirada
					//					PmOpportunity pmOpportunityPrev = new PmOpportunity(getSFParams());
					//					BmoOpportunity bmoOpportunityPrev = (BmoOpportunity)pmOpportunityPrev.get(pmConn, bmoOpportunity.getId());

					if (bmoOpportunityPrev.getStatus().equals(BmoOpportunity.STATUS_EXPIRED)) {
						// Es de tipo compra de inmueble, se asigna fecha y hora actual
						bmoOpportunity.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
						bmoOpportunity.getEndDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
								SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), 
								bmoWFlowType.getBmoWFlowCategory().getExpireDays().toInteger()));
						
						bmoOpportunity.getExpireDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
								SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), 
								bmoWFlowType.getBmoWFlowCategory().getExpireDays().toInteger()));
					}
				}	
			}	
			

			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOppoStatusHold().toBoolean())) {
				bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOppoStatusRevision().toBoolean())) {
				bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_EXPIRED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOppoStatusExpirada().toBoolean())) {
				bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_LOST) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOppoStatusPerdida().toBoolean())) {
				bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOppoStatusGanada().toBoolean())) {
				bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), " Este estatus esta desactivado ");
			}
	
		}


		// Validar partidas presupuestales
		// Si es Venta inmueble, no validar, ya que se debe tomar cuando se agrega la viv. en la cotizacion
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (!bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
				if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), "Seleccione una Partida.");
			}
		}

		// Si no esta asignado el cliente, asignarlo
		if (!(bmoOpportunity.getUserId().toInteger() > 0)) {
			// Asigna el vendedor de la oportunidad
			if (bmoCustomer.getSalesmanId().toInteger() > 0)
				bmoOpportunity.getUserId().setValue(bmoCustomer.getSalesmanId().toInteger());
		}

		// Actualizar WFlow
		if (!bmUpdateResult.hasErrors()) {
			// Asigna el estatus al flujo
			char wFlowStatus = BmoWFlow.STATUS_ACTIVE;
			if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_LOST) 
					|| bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)
					|| bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_EXPIRED))
				wFlowStatus = BmoWFlow.STATUS_INACTIVE;

			// Crea o actualiza el WFlow y asigna el ID recien creado
			bmoWFlow = pmWFlow.updateWFlow(pmConn, bmoOpportunity.getWFlowTypeId().toInteger(), bmoOpportunity.getWFlowId().toInteger(), 
					bmoOpportunity.getProgramCode(), bmoOpportunity.getId(), bmoOpportunity.getUserId().toInteger(), bmoOpportunity.getCompanyId().toInteger(), bmoOpportunity.getCustomerId().toInteger(),
					bmoOpportunity.getCode().toString(), bmoOpportunity.getName().toString(), bmoOpportunity.getDescription().toString(), 
					bmoOpportunity.getStartDate().toString(), bmoOpportunity.getEndDate().toString(), wFlowStatus, bmUpdateResult);
			bmoOpportunity.getWFlowId().setValue(bmoWFlow.getId());
		}

		// Crear detalle del proyecto si no existe
		PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
		if (!pmOpportunityDetail.opportunityDetailExists(pmConn, bmoOpportunity.getId())) {
			BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
			bmoOpportunityDetail.getExtraHour().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getExtraHour().toString());

			bmoOpportunityDetail.getOpportunityId().setValue(bmoOpportunity.getId());
			pmOpportunityDetail.save(pmConn,  bmoOpportunityDetail, bmUpdateResult);
		}

		// Otras validaciones una vez asignado el tipo de oportunidad
		if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
			BmoQuote bmoQuote = new BmoQuote();
			PmQuote pmQuote = new PmQuote(getSFParams());
			if (!newRecord) {
				// No dejar cambiar el valor estimado de la oportunidad cuando ya esta autorizado la cotizacion
				
				bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

				if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {
					if (bmoQuote.getAmount().toDouble() != bmoOpportunity.getAmount().toDouble())
						bmUpdateResult.addError(bmoOpportunity.getAmount().getName(), "El Valor estimado no se puede cambiar, la Cotización está Autorizada");
				}
			}
			
			// Validar que si esta cambiando el mercado, verifique si hay lineas con productos
			if (bmoOpportunityPrev.getMarketId().toInteger() > 0) {
				if (bmoOpportunity.getMarketId().toInteger() != bmoOpportunityPrev.getMarketId().toInteger()) {
					if (pmQuote.existProducts(pmConn, bmoQuote.getId()))
						bmUpdateResult.addError(bmoOpportunity.getMarketId().getName(), "La Cotización tiene Productos con Precios asignados al(la) " + getSFParams().getFieldFormTitle(bmoOpportunity.getMarketId()) + " actual. "
										+ "Debe eliminarlos para poder cambiar el(la) " + getSFParams().getFieldFormTitle(bmoOpportunity.getMarketId()) + "." );
				}
			}
			
			super.save(pmConn, bmoOpportunity, bmUpdateResult);

			// Crear / Actualizar cotización
			updateQuote(pmConn, bmoOpportunity, bmoWFlowType, bmUpdateResult);

			// Funcionalidad de ganar la oportunidad
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_WON) {
				
				winOpportunity(pmConn, bmoOpportunity, bmoWFlow, pmWFlowLog, bmUpdateResult);
			} 
			
		}
	

		// Actualiza estatus del cliente
		if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
			pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
		}

		// En caso de establecer como perdida la oportunidad, forzar ingreso del motivo
		if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST) {
			if (bmoOpportunityPrev.getStatus().toChar() == BmoOpportunity.STATUS_WON) {
				bmUpdateResult.addError(bmoOpportunity.getLoseMotiveId().getName(), "La Oportunidad esta Ganada");
			} else {
				if (!(bmoOpportunity.getLoseMotiveId().toInteger() > 0))
					bmUpdateResult.addError(bmoOpportunity.getLoseMotiveId().getName(), "Debe asignarse el Motivo de Perder la Oportunidad.");
				
				if (bmoOpportunity.getBmoOrderType().getRequiredLoseComments().toBoolean()) {
					if (bmoOpportunity.getLoseComments().toString().equals(""))
						bmUpdateResult.addError(bmoOpportunity.getLoseComments().getName(), "Debe asignarse el Comentario del Motivo de Perder.");
				}
			}
		} 
		
		// Asignar perido y fecha fiscal de acuerdo a la Fecha de Cierre de la oportunidad
		if (!bmoOpportunity.getSaleDate().toString().equals("")) {
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(getSFParams());
			bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());

			if (!bmoCompany.getFiscalPeriodType().toString().equals("") && bmoCompany.getFiscalStartMonth().toInteger() > 0) {
				bmoOpportunity.getFiscalPeriod().setValue("" + FlexUtil.getFiscalPeriod(getSFParams().getDateFormat(), 
								bmoCompany.getFiscalStartMonth().toInteger(), 
								bmoCompany.getFiscalPeriodType().toString(), 
								bmoOpportunity.getSaleDate().toString()));
				
				bmoOpportunity.getFiscalYear().setValue(FlexUtil.getFiscalYear(getSFParams().getDateFormat(), 
								bmoCompany.getFiscalStartMonth().toInteger(),
								bmoOpportunity.getSaleDate().toString()));
			}
		}
		
		// Se almacena el Oportunidad
		if (!bmUpdateResult.hasErrors()) {
			// Crear bitacoras 
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_WON) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad se guardó como Ganada.");
				// Asignar fecha del dia de autorizacion en la Fecha Cierre
				bmoOpportunity.getSaleDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			} else if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST) {
				String motives = "";
				if (bmoOpportunity.getLoseMotiveId().toInteger() > 0) {
					PmLoseMotive pmLoseMotive = new PmLoseMotive(getSFParams());
					BmoLoseMotive bmoLoseMotive = (BmoLoseMotive)pmLoseMotive.get(bmoOpportunity.getLoseMotiveId().toInteger());
					motives = bmoLoseMotive.getName().toString() + " | "+ bmoOpportunity.getLoseComments().toString();
				}
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad se guardó como Perdida. Motivo: " + motives);

				//Cancelar cotización
				PmQuote pmQuote = new PmQuote(getSFParams());
				BmoQuote bmoQuote = new BmoQuote();
				bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());
				if (!bmoQuote.getStatus().equals(BmoQuote.STATUS_CANCELLED)) {
					if (bmoQuote.getWFlowId().toInteger() > 0) {
						bmoQuote.getStatus().setValue(BmoQuote.STATUS_CANCELLED);
						// Guardar fecha de cancelacion y limpia autorizacion
						bmoQuote.getCancelledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
						bmoQuote.getCancelledUser().setValue(getSFParams().getLoginInfo().getUserId());
						bmoQuote.getAuthorizedDate().setValue("");
						bmoQuote.getAuthorizedUser().setValue("");
						pmQuote.saveSimple(pmConn, bmoQuote, bmUpdateResult);
						pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como Cancelada.");
					}
				}
			} else if  (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_EXPIRED) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad se guardó como Expirada.");
				//Cancelar Cotización peticion de Drea(Pedidos de tipo renta)
				if (!bmUpdateResult.hasErrors()  && !newRecord) {					
					if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						BmoQuote bmoQuote = new BmoQuote();
						PmQuote pmQuote = new PmQuote(getSFParams());							
						bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
						if (bmoQuote.getWFlowId().toInteger() > 0) {
							bmoQuote.getStatus().setValue(BmoQuote.STATUS_CANCELLED);
							pmQuote.saveSimple(pmConn, bmoQuote, bmUpdateResult);
							pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como Cancelada.");
						}
					}
					
				}

			} else if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_REVISION || bmoOpportunity.getStatus().toChar() == (BmoOpportunity.STATUS_HOLD)) {
				if (!newRecord)
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad se guardó como En Revisión.");
				
				if (!bmUpdateResult.hasErrors() && !newRecord) {
					if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						BmoQuote bmoQuote = new BmoQuote();
						PmQuote pmQuote = new PmQuote(getSFParams());							
						bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
						if (bmoQuote.getWFlowId().toInteger() > 0) {
							bmoQuote.getStatus().setValue(BmoQuote.STATUS_REVISION);
							pmQuote.save(pmConn, bmoQuote, bmUpdateResult);
						}
					}
				}
			}

			// Si la notificacion esta activada en el Tipo de Pedido y se esta cambiando el estatus a uno diferente
			// notificar a los usuarios del WFlow de la oportunidad, sino hay, al usuario del WFlow
			if (!newRecord) {
				if (bmoOpportunityPrev.getStatus().toChar() != bmoOpportunity.getStatus().toChar()){
					if (bmoOpportunity.getBmoOrderType().getEmailChangeStatusOppo().toBoolean())
						sendMailChangeStatusOpportunity(pmConn, bmoOpportunity);
				}
			}
			
			// Borrar Domicilio particular en caso de el salon no tenga check de casa particular
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				BmoVenue bmoVenue = new BmoVenue();
				PmVenue pmVenue = new PmVenue(getSFParams());
				if (bmoOpportunity.getVenueId().toInteger() > 0 ) {
					bmoVenue = (BmoVenue)pmVenue.get(bmoOpportunity.getVenueId().toInteger());
					if (!(bmoVenue.getHomeAddress().toInteger() > 0))
						bmoOpportunity.getCustomField4().setValue("");
				}
			}
			
			// Si se modifico el cliente a la oportunidad
			if(bmoOpportunityPrev.getCustomerId().toInteger() != bmoOpportunity.getCustomerId().toInteger() ) {

				// Obtiene cliente actual
				String customerNew = "";
				if (bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY)) {
					if (bmoCustomer.getLegalname().toString().equals(""))
						customerNew ="'" + bmoCustomer.getCode() + " "+  bmoCustomer.getDisplayName().toString() + "'";
					else
						customerNew = "'" + bmoCustomer.getCode() + " "+  bmoCustomer.getLegalname().toString() + "'";
				} else 
					customerNew = "'" + bmoCustomer.getCode() + " "+  bmoCustomer.getDisplayName().toString() + "'";

				// Obtiene cliente anterior
				String customerPrev = "";
				if (bmoOpportunityPrev.getBmoCustomer().getCustomertype().equals(BmoCustomer.TYPE_COMPANY)) {
					if (bmoCustomer.getLegalname().toString().equals(""))
						customerPrev = "'" + bmoOpportunityPrev.getBmoCustomer().getCode() + " "+  bmoOpportunityPrev.getBmoCustomer().getDisplayName().toString() + "'";
					else 
						customerPrev = "'" + bmoOpportunityPrev.getBmoCustomer().getCode() + " "+  bmoOpportunityPrev.getBmoCustomer().getLegalname().toString() + "'";

				} else 
					customerPrev = "'" + bmoOpportunityPrev.getBmoCustomer().getCode() + " "+  bmoOpportunityPrev.getBmoCustomer().getDisplayName().toString() + "'";

				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "El Cliente se modificó de "+customerPrev +"  a  " + customerNew);

				// Acuatlizar estatus del Cliente anterior
				pmCustomer.updateStatus(pmConn, bmoOpportunityPrev.getBmoCustomer() , bmUpdateResult);
			}
//			createOpportunityItemsFromQuote(pmConn, bmoOpportunity, bmUpdateResult);

			// Tipo Consultoria - Cambia categoria forecast de acuerdo al estatus de la oportunidad(Ejecutar "accion")
			if (bmoOpportunity.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_CONSULTANCY)) {
				BmoCategoryForecast bmoCategoryForecast = new BmoCategoryForecast();
				PmCategoryForecast pmCategoryForecast = new PmCategoryForecast(getSFParams());

				// Si existe un registro en la tabla de Categorías Forecast, obtenerla para asignarla a la oportunidad
				if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION)) {
					if (pmCategoryForecast.existStatusOpportunity(pmConn, "" + BmoCategoryForecast.STATUS_OP_REVISION, -1)) {
						bmoCategoryForecast = (BmoCategoryForecast)pmCategoryForecast.getBy(pmConn, "" + BmoCategoryForecast.STATUS_OP_REVISION, bmoCategoryForecast.getStatusOpportunity().getName());
						bmoOpportunity.getCategoryForecastId().setValue(bmoCategoryForecast.getId());
					}
				}else if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)) {
					if (pmCategoryForecast.existStatusOpportunity(pmConn, "" + BmoCategoryForecast.STATUS_OP_WON, -1)) {
						bmoCategoryForecast = (BmoCategoryForecast)pmCategoryForecast.getBy(pmConn, "" + BmoCategoryForecast.STATUS_OP_WON, bmoCategoryForecast.getStatusOpportunity().getName());
						bmoOpportunity.getCategoryForecastId().setValue(bmoCategoryForecast.getId());
					}
				} else if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_LOST)) {
					if (pmCategoryForecast.existStatusOpportunity(pmConn, "" + BmoCategoryForecast.STATUS_OP_LOST, -1)) {
						bmoCategoryForecast = (BmoCategoryForecast)pmCategoryForecast.getBy(pmConn, "" + BmoCategoryForecast.STATUS_OP_LOST, bmoCategoryForecast.getStatusOpportunity().getName());
						bmoOpportunity.getCategoryForecastId().setValue(bmoCategoryForecast.getId());
					}
				} else if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD)) {
					if (pmCategoryForecast.existStatusOpportunity(pmConn, "" + BmoCategoryForecast.STATUS_OP_HOLD, -1)) {
						bmoCategoryForecast = (BmoCategoryForecast)pmCategoryForecast.getBy(pmConn, "" + BmoCategoryForecast.STATUS_OP_HOLD, bmoCategoryForecast.getStatusOpportunity().getName());
						bmoOpportunity.getCategoryForecastId().setValue(bmoCategoryForecast.getId());
					}
				} else if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_EXPIRED)) {
					if (pmCategoryForecast.existStatusOpportunity(pmConn, "" + BmoCategoryForecast.STATUS_OP_EXPIRED, -1)) {
						bmoCategoryForecast = (BmoCategoryForecast)pmCategoryForecast.getBy(pmConn, "" + BmoCategoryForecast.STATUS_OP_EXPIRED, bmoCategoryForecast.getStatusOpportunity().getName());
						bmoOpportunity.getCategoryForecastId().setValue(bmoCategoryForecast.getId());
					}
				}
			}
			
			// Actualizar id de claves del programa por empresa
			if (newRecord && !bmUpdateResult.hasErrors()) {
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoOpportunity.getCompanyId().toInteger(), 
						bmoOpportunity.getProgramCode().toString());
			}

			// Guarda el Oportunidad
			super.save(pmConn, bmoOpportunity, bmUpdateResult);
			
			// Forzar el ID del resultado que sea del Oportunidad y no de otra clase
			bmUpdateResult.setId(bmoOpportunity.getId());
		}
		return bmUpdateResult;
	}

	private void updateQuote(PmConn pmConn, BmoOpportunity bmoOpportunity, BmoWFlowType bmoWFlowType, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear cotizacion si estan habilitadas por sistema
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableQuotes().toBoolean()) {
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = new BmoQuote();

			if (!(bmoOpportunity.getQuoteId().toInteger() > 0)) {
				bmoQuote.getCode().setValue(bmoOpportunity.getCode().toString());
				bmoQuote.getName().setValue(bmoOpportunity.getName().toString());
				// Se asignan notas de Flex Config y Comentarios del Tipo de Flujo
				bmoQuote.getDescription().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getQuoteNotes().toString() + " \n" + 
						bmoWFlowType.getComments().toString());
				bmoQuote.getDownPayment().setValue(0);
				bmoQuote.getAmount().setValue(0);
				bmoQuote.getDiscount().setValue(0);
				bmoQuote.getTaxApplies().setValue(bmoOpportunity.getBmoOrderType().getHasTaxes().toBoolean());
				bmoQuote.getTax().setValue(0);
				bmoQuote.getTotal().setValue(0);
				bmoQuote.getStatus().setValue(BmoQuote.STATUS_REVISION);
				bmoQuote.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
				bmoQuote.getEndDate().setValue(bmoOpportunity.getEndDate().toString());
				bmoQuote.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
				bmoQuote.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
				bmoQuote.getCustomerId().setValue(bmoOpportunity.getCustomerId().toInteger());
				bmoQuote.getUserId().setValue(bmoOpportunity.getUserId().toInteger());
				bmoQuote.getWFlowId().setValue(bmoOpportunity.getWFlowId().toInteger());
				bmoQuote.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toChar());
				bmoQuote.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
				bmoQuote.getMarketId().setValue(bmoOpportunity.getMarketId().toInteger());
				bmoQuote.getCustomerRequisition().setValue(bmoOpportunity.getCustomerRequisition().toString());
				bmoQuote.getPayConditionId().setValue(bmoOpportunity.getPayConditionId().toString());
				
				// Validar partida que viene de la OP
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					//if (!bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
						if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0)) 
							bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), "Seleccione una Partida.");
						bmoQuote.getBudgetItemId().setValue(bmoOpportunity.getBudgetItemId().toInteger());
						bmoQuote.getAreaId().setValue(bmoOpportunity.getAreaId().toInteger());
					//}
				}

				
				
//				PmCustomer pmCustomer = new PmCustomer(getSFParams());
//				BmoCustomer bmoCustomer = new BmoCustomer();
//				bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOpportunity.getCustomerId().toInteger());

				// Asignar por defecto el contacto principal si es cliente de tipo empresa
//				if (bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY)) {
//					PmCustomerContact pmCustomerContact = new PmCustomerContact(getSFParams());
//					int contactMain = pmCustomerContact.getCustomerContactMain(bmoCustomer.getId());
//					if (contactMain > 0)
//						bmoOpportunity.getCustomerContactId().setValue(contactMain);
//				}
				pmQuote.save(pmConn, bmoQuote, bmUpdateResult);

			} else {
				// Actualiza datos de la cotizacion
				bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

				if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
					bmoQuote.getName().setValue(bmoOpportunity.getName().toString());
					bmoQuote.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
					bmoQuote.getEndDate().setValue(bmoOpportunity.getEndDate().toString());
					bmoQuote.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
					bmoQuote.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
					bmoQuote.getCustomerId().setValue(bmoOpportunity.getCustomerId().toInteger());
					bmoQuote.getUserId().setValue(bmoOpportunity.getUserId().toInteger());
					bmoQuote.getWFlowId().setValue(bmoOpportunity.getWFlowId().toInteger());
					bmoQuote.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toChar());
					bmoQuote.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
					bmoQuote.getMarketId().setValue(bmoOpportunity.getMarketId().toInteger());
					bmoQuote.getCustomerRequisition().setValue(bmoOpportunity.getCustomerRequisition().toString());
					bmoQuote.getPayConditionId().setValue(bmoOpportunity.getPayConditionId().toString());

					// Validar partida que viene de la OP
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						//if (!bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
							if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0)) 
								bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), "Seleccione una Partida en la Oportunidad");
							bmoQuote.getBudgetItemId().setValue(bmoOpportunity.getBudgetItemId().toInteger());
							bmoQuote.getAreaId().setValue(bmoOpportunity.getAreaId().toInteger());
						//}
					}

					pmQuote.saveSimple(pmConn, bmoQuote, bmUpdateResult);
				}
				//  Si la cotizacion ya esta autorizada, no dejar cambiar los mismos campos de la oportunidad y cotizacion
				else if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {

					//if (!bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {

						if (!bmoQuote.getName().toString().equals(bmoOpportunity.getName().toString())) {
							bmUpdateResult.addError(bmoOpportunity.getName().getName() , "No se puede cambiar el Nombre de la Oportunidad, la Cotización ya está Autorizada");
						} else if  (bmoQuote.getCustomerId().toInteger() != bmoOpportunity.getCustomerId().toInteger()) {
							bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName() , "No se puede cambiar el Cliente/Prospecto, la Cotización está Autorizada");
						} 
						// Mandar error; si es diferente y no tiene permisos para cambiar vendedor con cotizacion aut.
						else if  (bmoQuote.getUserId().toInteger() != bmoOpportunity.getUserId().toInteger()
								&& !getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT)) {
							bmUpdateResult.addError(bmoOpportunity.getUserId().getName() , "No se puede cambiar el Vendedor, la Cotización está Autorizada");
						}						
						// Mandar error; si es igual y no tiene permisos para cambiar la fecha con cotizacion aut.
						else if (!bmoQuote.getStartDate().toString().equals(bmoOpportunity.getStartDate().toString())
								&& !getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT)) {
							bmUpdateResult.addError(bmoOpportunity.getStartDate().getName() , "No se puede cambiar la Fecha Inicio, la Cotización está Autorizada");
						} else if (!bmoQuote.getEndDate().toString().equals(bmoOpportunity.getEndDate().toString())
								&& !getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT)) {
							bmUpdateResult.addError(bmoOpportunity.getEndDate().getName() , "No se puede cambiar la Fecha Fin, la Cotización está Autorizada");
						}
						else if (bmoQuote.getCurrencyId().toInteger() == bmoOpportunity.getCurrencyId().toInteger()
								&& bmoQuote.getCurrencyParity().toDouble() != bmoOpportunity.getCurrencyParity().toDouble()
								&& !getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT)) {
							bmUpdateResult.addError(bmoOpportunity.getCurrencyParity().getName() , "No se puede cambiar el Tipo de Cambio, la Cotización está Autorizada");
						} else if (bmoQuote.getCurrencyId().toInteger() != bmoOpportunity.getCurrencyId().toInteger()
								&& bmoQuote.getCurrencyParity().toDouble() == bmoOpportunity.getCurrencyParity().toDouble()) {
							bmUpdateResult.addError(bmoOpportunity.getCurrencyId().getName() , "No se puede cambiar la Moneda, la Cotización está Autorizada");
						} else if  (bmoQuote.getCurrencyId().toInteger() != bmoOpportunity.getCurrencyId().toInteger()
								&& bmoQuote.getCurrencyParity().toDouble() != bmoOpportunity.getCurrencyParity().toDouble()) {
							bmUpdateResult.addError(bmoOpportunity.getCurrencyId().getName() , "No se puede cambiar la Moneda/Tipo de Cambio, la Cotización está Autorizada");
						} else if  (bmoQuote.getCompanyId().toInteger() != bmoOpportunity.getCompanyId().toInteger()) {
							bmUpdateResult.addError(bmoOpportunity.getCompanyId().getName() , "No se puede cambiar la Empresa, la Cotización está Autorizada");
						} 

					//}
						
					// Deja guardar si los mismos campos de la oportunidad y cotizacion son igual
					if (bmoQuote.getName().toString().equals(bmoOpportunity.getName().toString())
							&& bmoQuote.getCurrencyId().toInteger() == bmoOpportunity.getCurrencyId().toInteger()
							&& (bmoQuote.getCurrencyParity().toDouble() == bmoOpportunity.getCurrencyParity().toDouble()
								|| (bmoQuote.getCurrencyParity().toString() != (bmoOpportunity.getCurrencyParity().toString())	
								&& getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT))
							)
							&& bmoQuote.getCustomerId().toInteger() == bmoOpportunity.getCustomerId().toInteger()
							// si es igual o es diferente pero con permisos de cambiar vendedor con cotizacion aut.
							&& (bmoQuote.getUserId().toInteger() == bmoOpportunity.getUserId().toInteger()
								|| (bmoQuote.getUserId().toInteger() != bmoOpportunity.getUserId().toInteger() 
									&& getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT)))
							&& bmoQuote.getCompanyId().toInteger() == bmoOpportunity.getCompanyId().toInteger()
							// si es igual o es diferente pero con permisos de cambiar fecha inicio con cotizacion aut.
							&& (bmoQuote.getStartDate().toString().equals(bmoOpportunity.getStartDate().toString())
								 || (bmoQuote.getStartDate().toString() != (bmoOpportunity.getStartDate().toString())	
									 && getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT) )
							)
							// si es igual o es diferente pero con permisos de cambiar fecha fin con cotizacion aut.
							&& (bmoQuote.getEndDate().toString().equals(bmoOpportunity.getEndDate().toString())
									|| (bmoQuote.getEndDate().toString() != (bmoOpportunity.getEndDate().toString())	
									 && getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT) )
								)
							){
						bmoQuote.getName().setValue(bmoOpportunity.getName().toString());
						bmoQuote.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
						bmoQuote.getEndDate().setValue(bmoOpportunity.getEndDate().toString());
						bmoQuote.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
						bmoQuote.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
						bmoQuote.getCustomerId().setValue(bmoOpportunity.getCustomerId().toInteger());
						bmoQuote.getUserId().setValue(bmoOpportunity.getUserId().toInteger());
						bmoQuote.getWFlowId().setValue(bmoOpportunity.getWFlowId().toInteger());
						bmoQuote.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toChar());
						bmoQuote.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
						bmoQuote.getCustomerRequisition().setValue(bmoOpportunity.getCustomerRequisition().toString());
						bmoQuote.getPayConditionId().setValue(bmoOpportunity.getPayConditionId().toString());

						pmQuote.saveSimple(pmConn, bmoQuote, bmUpdateResult);
					}
				} 
			}
			bmoOpportunity.getQuoteId().setValue(bmoQuote.getId());
		}
	}

	// Ganar oportunidad
	private void winOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, BmoWFlow bmoWFlow, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
		if (!bmUpdateResult.hasErrors()) {

			// Revisar si esta habilitado el crear los efectos de la oportunidad
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOpportunityEffect().toBoolean()){

				// Revisar que este asignado el tipo de proyecto
				if (bmoOpportunity.getForeignWFlowTypeId().toInteger() > 0)  {

					// Obtener cotizacion
					PmQuote pmQuote = new PmQuote(getSFParams());
					BmoQuote bmoQuote = new BmoQuote();
					bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());
					if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED)
						bmUpdateResult.addError(bmoOpportunity.getCode().getName(), "Debe Autorizarse la Cotización de la Oportunidad.");

					// Revisar flujo
					if (!bmoWFlow.getHasDocuments().toBoolean())
						bmUpdateResult.addError(bmoOpportunity.getCode().getName(), "Los Documentos no están Completos.");

					// Es de tipo Renta, crear Proyecto
					if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						winRental(pmConn, bmoOpportunity, pmWFlowLog, bmUpdateResult);
					} 
					// Es de tipo Compra
					else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						winSale(pmConn, bmoOpportunity, pmWFlowLog, bmUpdateResult);
					}
					// Es de tipo Servicio
					else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
						winConsultancy(pmConn, bmoOpportunity, pmWFlowLog, bmUpdateResult);
					}
					// Es de tipo Inmueble
					else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
						winPropertySale(pmConn, bmoOpportunity, pmWFlowLog, bmUpdateResult);
					}
				} else {
					// No se asigno el tipo de proyecto a crear, enviar error
					bmUpdateResult.addError(bmoOpportunity.getForeignWFlowTypeId().getName(), "Debe asigarse el tipo de Proyecto a crear.");
				}
			}
		}
	}

	// Se gana oportunidad de tipo renta
	private void winRental(PmConn pmConn, BmoOpportunity bmoOpportunity, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
		// Revisar que se haya creado el detalle de la oportunidad
		PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
		if (!pmOpportunityDetail.opportunityDetailExists(pmConn, bmoOpportunity.getId()))
			bmUpdateResult.addError(bmoOpportunity.getCode().getName(), "Debe crearse el registro de Detalle de la Oportunidad.");
		// Revisar que se hayan asignado las fechas de la oportunidad
		if (bmoOpportunity.getStartDate().toString().equals(""))
			bmUpdateResult.addError(bmoOpportunity.getStartDate().getName(), "Debe asignarse la Fecha de Inicio de la Oportunidad.");
		if (bmoOpportunity.getEndDate().toString().equals(""))
			bmUpdateResult.addError(bmoOpportunity.getEndDate().getName(), "Debe asignarse la Fecha de Fin de la Oportunidad.");
		// Revisa fechas
		if (!bmUpdateResult.hasErrors()) {
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoOpportunity.getEndDate().toString(), bmoOpportunity.getStartDate().toString())) {
				bmUpdateResult.addError(bmoOpportunity.getEndDate().getName(), 
						"No puede ser Anterior a " + bmoOpportunity.getStartDate().getLabel());
			}
		}
		// Revisar que no haya errores, crear Proyecto
		if (!bmUpdateResult.hasErrors()) {
			PmProject pmProject = new PmProject(getSFParams());
			// Si no existe el proyecto de la oportunidad, crearlo
			if (!pmProject.projectOpportunityExists(pmConn, bmoOpportunity.getId())) {
				pmProject.createFromOpportunity(pmConn, bmoOpportunity, bmUpdateResult);
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Se creó el Proyecto a partir de la Oportunidad.");
			}
		} 
	}

	// Se gana oportunidad de tipo venta
	private void winSale(PmConn pmConn, BmoOpportunity bmoOpportunity, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
		// Revisar que no haya errores, crear pedido
		if (!bmUpdateResult.hasErrors()) {
			PmOrder pmOrder = new PmOrder(getSFParams());
			// Si no existe el pedido de la oportunidad, crearlo
			if (!pmOrder.orderOpportunityExists(pmConn, bmoOpportunity.getId())) {
				pmOrder.createFromOpportunity(pmConn, bmoOpportunity, bmUpdateResult);
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Se creó el Pedido a partir de la Oportunidad.");
			}
		} 
	}
	
	// Se gana oportunidad de tipo venta
	private void winConsultancy(PmConn pmConn, BmoOpportunity bmoOpportunity, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
		// Revisar que no haya errores, crear pedido
		if (!bmUpdateResult.hasErrors()) {
			PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
			// Si no existe el pedido de la oportunidad, crearlo
			if (!pmConsultancy.consultancyOpportunityExists(pmConn, bmoOpportunity.getId())) {
				
				pmConsultancy.createFromOpportunity(pmConn, bmoOpportunity, bmUpdateResult);
				
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Se creó la " + getSFParams().getProgramFormTitle(new BmoConsultancy()) + " a partir de la Oportunidad.");
			}
		} 
	}

	// Se gana oportunidad de tipo inmueble
	private void winPropertySale(PmConn pmConn, BmoOpportunity bmoOpportunity, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {

		// Revisar que se haya asignado una propiedad a la cotización
		PmQuoteProperty pmQuoteProperty = new PmQuoteProperty(getSFParams());
		BmoQuoteProperty bmoQuoteProperty = new BmoQuoteProperty();
		try {
			bmoQuoteProperty = (BmoQuoteProperty)pmQuoteProperty.getBy(bmoOpportunity.getQuoteId().toInteger(), bmoQuoteProperty.getQuoteId().getName());
		} catch (SFException e) {
			bmUpdateResult.addError(bmoOpportunity.getName().getName(), "No se puede Ganar: La Cotización no tiene asignado Inmueble.");
		}

		// Revisar que no haya errores, crear Venta Inmueble
		if (!bmUpdateResult.hasErrors()) {

			// Revisar disponibilidad del inmueble
			PmProperty pmProperty = new PmProperty(getSFParams());
			BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoQuoteProperty.getPropertyId().toInteger());
			if (!bmoProperty.getCansell().toBoolean()) 
				bmUpdateResult.addError(bmoOpportunity.getName().getName(), "No se puede Ganar: El Inmueble de la Cotización no se puede Vender.");

			if (!bmoProperty.getAvailable().toBoolean()) 
				bmUpdateResult.addError(bmoOpportunity.getName().getName(), "No se puede Ganar: El Inmueble de la Cotización ya no está Disponible.");

			PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());
			// Si no existe el la venta de la oportunidad, crearlo
			if (!pmPropertySale.propertySaleOpportunityExists(pmConn, bmoOpportunity.getId())) {

				pmPropertySale.createFromOpportunity(pmConn, bmoOpportunity, bmoQuoteProperty.getPropertyId().toInteger(), bmUpdateResult);

				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "Se creó la Venta Inmueble a partir de la Oportunidad.");
			}
		}
	}

	public boolean getCustomerProtection(int userId, int customerId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		boolean result = getCustomerProtection(pmConn, userId, customerId);		
		pmConn.close();
		return result;
	}

	// Si existe oportunidad con el mismo cliente pero diferente vendedor, devuelve verdadero
	private boolean getCustomerProtection(PmConn pmConn, int userId, int customerId) throws SFException {	
		boolean existCustomer = false;

		String sql = "SELECT COUNT(oppo_opportunityid) AS countOppo FROM opportunities " +
				" WHERE oppo_userid <> " + userId + " " +
				" AND (oppo_customerid = " + customerId  +
				" AND oppo_status = '" + BmoOpportunity.STATUS_REVISION + "') "+
				" OR (oppo_customerid = " + customerId  +
				" AND oppo_status = '" + BmoOpportunity.STATUS_HOLD + "') ";
		pmConn.doFetch(sql);
		if (pmConn.next()){
			if(pmConn.getInt("countOppo") > 0)
				existCustomer = true;
		}

		return existCustomer;
	}

	public void createFromEmail(String from, String fromFirstname, String fromFatherlastname, String fromMotherlastname, String subject, String body) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Buscar si el cliente existe
			PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(getSFParams());
			int customerId = pmCustomerEmail.customerByEmail(from);

			if (!(customerId > 0)) {
				// No hay cliente registrado, crear nuevo
				// Crear cliente
				BmoCustomer bmoCustomer = new BmoCustomer();
				bmoCustomer.getFirstname().setValue(fromFirstname);
				bmoCustomer.getFatherlastname().setValue(fromFatherlastname);
				bmoCustomer.getMotherlastname().setValue(fromMotherlastname);
				bmoCustomer.getCustomertype().setValue(BmoCustomer.TYPE_PERSON);
				bmoCustomer.getRfc().setValue(SFServerUtil.rfcGenerator(fromFatherlastname,
						fromMotherlastname,
						fromFirstname,
						new Date()));
				bmoCustomer.getSalesmanId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultSalesMan().toInteger());
				PmCustomer pmCustomer = new PmCustomer(getSFParams());
				pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
				customerId = bmUpdateResult.getId();

				// Crear correo del cliente
				BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
				bmoCustomerEmail.getCustomerId().setValue(customerId);
				bmoCustomerEmail.getType().setValue(BmoCustomerEmail.TYPE_PERSONAL);
				bmoCustomerEmail.getEmail().setValue(from);

				pmCustomerEmail.save(pmConn, bmoCustomerEmail, bmUpdateResult);
			} 

			// Crear Oportunidad si se tiene un cliente asignado
			DateFormat dfName = new SimpleDateFormat("yyyyMMdd");
			String oppoName = "E" + dfName.format(new Date()) + customerId;

			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_REVISION);
			bmoOpportunity.getName().setValue(oppoName);

			// Revisar que el body no sea mayor al campo description
			String description = subject + ": " + body;
			if (description.length() > bmoOpportunity.getDescription().getSize()) {
				description = description.substring(0, bmoOpportunity.getDescription().getSize() - 2);
			}

			bmoOpportunity.getDescription().setValue(description);
			bmoOpportunity.getUserId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultSalesMan().toInteger());

			DateFormat df = new SimpleDateFormat(getSFParams().getDateTimeFormat());
			String startDate = df.format(new Date());			
			bmoOpportunity.getStartDate().setValue(startDate);
			bmoOpportunity.getEndDate().setValue(startDate);

			bmoOpportunity.getCustomerId().setValue(customerId);

			this.save(pmConn, bmoOpportunity, bmUpdateResult);

			// Obtener ID para generar mas datos del Oportunidad
			bmoOpportunity.setId(bmUpdateResult.getId());

			// No hay errores, envia correo
			if (!bmUpdateResult.hasErrors()) {
				pmConn.commit();

				// Direcciones del correo de confirmación
				ArrayList<SFMailAddress> toList = new ArrayList<SFMailAddress>();
				SFMailAddress customerAddress = new SFMailAddress(from, fromFirstname + " " + fromFatherlastname);
				toList.add(customerAddress);

				PmUser pmUser = new PmUser(getSFParams());
				BmoUser bmoUser = (BmoUser)pmUser.get(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultSalesMan().toInteger());

				// El usuario esta activo enviar correo
				if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
					SFMailAddress salesmanAddress = new SFMailAddress(bmoUser.getEmail().toString(), 
							bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString());
					toList.add(salesmanAddress);
	
					// Enviar
					SFSendMail.send(getSFParams(),
							toList, 
							getSFParams().getBmoSFConfig().getEmail().toString(), 
							getSFParams().getMainAppTitle(), 
							"#" + getSFParams().getAppCode()  + bmoOpportunity.getCode().toString() + " Correo procesado exitosamente", 
							HtmlUtil.mailBodyFormat(getSFParams(), 
									"#" + getSFParams().getAppCode()  + bmoOpportunity.getCode().toString() + " Correo procesado exitosamente", 
									"El correo para la creacion del Evento fue procesado exitosamente; usted sera atendido a la brevedad.")
							);
				}
			}
			else {
				SFSendMail.send(getSFParams(),
						from, 
						fromFirstname + " " + fromFatherlastname, 
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getMainAppTitle(), 
						"Error al procesar correo", 
						HtmlUtil.mailBodyFormat(getSFParams(), 
								"Error al procesar correo",
								"El correo para la creacion de Oportunidad no pudo ser procesado. Se pondran en contacto con usted para dar atencion a su peticion."
								));
				SFSendMail.send(getSFParams(),
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getMainAppTitle(), 
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getMainAppTitle(), 
						"Error al procesar correo de " + from, 
						HtmlUtil.mailBodyFormat(getSFParams(), 
								"Error al procesar correo de " + from,
								"Error al procesar correo: " + bmUpdateResult.errorsToString()
								));
			}

		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
	}

	public BmoOpportunity getByWFlowId(int wFlowId) throws SFException {
		return (BmoOpportunity)super.getBy(wFlowId, bmoOpportunity.getWFlowId().getName());
	}

	public BmUpdateResult canPublishQuote(BmoFormat bmoFormat, BmoOpportunity bmoOpportunity, BmUpdateResult bmUpdateResult) throws SFException{
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();

			// Revisa si existe cotizacion autorizada
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableQuotes().toBoolean()) {
				PmQuote pmQuote = new PmQuote(getSFParams());
				BmoQuote bmoQuote = new BmoQuote();
				if (bmoOpportunity.getQuoteId().toInteger() > 0) {
					bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());
					if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) 
						bmUpdateResult.addMsg("La Cotización no está Autorizada.");

					if (!(bmoOpportunity.getForeignWFlowTypeId().toInteger() > 0))
						bmUpdateResult.addMsg("No se ha asignado el Tipo de Proyecto en la Oportunidad.");

				} else {
					bmUpdateResult.addMsg("La Cotización no está Creada.");
				}

			} 	

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	public BmUpdateResult canPublishContract(BmoFormat bmoFormat, BmoOpportunity bmoOpportunity, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();

			// Revisar la cotización autorizada
			bmUpdateResult = this.canPublishQuote(bmoFormat, bmoOpportunity, bmUpdateResult);

			// Revisa si hay datos de detalle capturados
			PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
			if (!pmOpportunityDetail.opportunityDetailExists(pmConn, bmoOpportunity.getId())) 
				bmUpdateResult.addMsg("No se ha capturado el Detalle de la Oportunidad.");

			//			if (!bmUpdateResult.hasErrors()) {
			//				// Agrega bitacora de intento de impresión
			//				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			//				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad se almacenó como Autorizada.");
			//			}

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	// Actualizar estatus de las oportunidades si es que expiran
	public void updateBatchStatus() throws SFException{
		// Si esta configurada la expiracion de oportunidades, proseguir

		BmoOpportunity bmoOpportunity = new BmoOpportunity();

		// Obtener oportunidades en revision
		BmFilter filterByStatus = new BmFilter();
		filterByStatus.setValueFilter(bmoOpportunity.getKind(), bmoOpportunity.getStatus().getName(), "" + BmoOpportunity.STATUS_REVISION);
		Iterator<BmObject> opportunityIterator = this.list(filterByStatus).iterator();

		while (opportunityIterator.hasNext()) {
			bmoOpportunity = (BmoOpportunity)opportunityIterator.next();

			// Revisa si aplica la expiracion en el tipo de flujo de la oportunidad
			if (bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getExpires().toBoolean()) {
				if (SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
						bmoOpportunity.getExpireDate().toString(),
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()))) {
					
					bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_EXPIRED);

					PmOpportunity pmUpdateOpportunity = new PmOpportunity(getSFParams());
					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					pmUpdateOpportunity.saveSimple(bmoOpportunity, bmUpdateResult);
					
					// Inactivar el flujo
					PmWFlow pmWFlow = new PmWFlow(getSFParams());
					BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(bmoOpportunity.getWFlowId().toInteger());
					bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
					pmWFlow.saveSimple(bmoWFlow, bmUpdateResult);
					
					// Generar Bitacora
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());						
					pmWFlowLog.addLog(bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Oportunidad Expiró.");
				}
			}
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		if (!action.equals(BmoOpportunity.ACTION_GETSUMTOTAL))
			bmoOpportunity = (BmoOpportunity)this.get(bmObject.getId());

		// Obtiene el ID del modulo de effecto effecto
		if (action.equals(BmoOpportunity.ACTION_GETEFFECT)) {

			printDevLog("Ingreso a revisar tipos de Pedidos");

			// Es de tipo Renta
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				printDevLog("Es de tipo Renta");
				PmProject pmProject = new PmProject(getSFParams());
				BmoProject bmoProject = new BmoProject();
				bmoProject = (BmoProject)pmProject.getBy(bmoOpportunity.getId(), bmoProject.getOpportunityId().getName());
				bmUpdateResult.setBmObject(bmoProject);
			}
			// Es de tipo Venta
			else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
				printDevLog("Es de tipo Venta");
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = new BmoOrder();
				bmoOrder = (BmoOrder)pmOrder.getBy(bmoOpportunity.getId(), bmoOrder.getOpportunityId().getName());
				bmUpdateResult.setBmObject(bmoOrder);
			}	
			// Es de tipo Consultoria
			else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
				printDevLog("Es de tipo Consultoria");
				PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
				BmoConsultancy bmoConsultancy = new BmoConsultancy();
				bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(bmoOpportunity.getId(), bmoConsultancy.getOpportunityId().getName());
				bmUpdateResult.setBmObject(bmoConsultancy);
			}	
			// Es de tipo venta de Propiedad
			else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
				printDevLog("Es de tipo Inmuebles");
				PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());
				BmoPropertySale bmoPropertySale = new BmoPropertySale();
				bmoPropertySale = (BmoPropertySale)pmPropertySale.getBy(bmoOpportunity.getId(), bmoPropertySale.getOpportunityId().getName());
				bmUpdateResult.setBmObject(bmoPropertySale);
			}	
			
		} else if (action.equals(BmoOpportunity.ACTION_COPYOPPORTUNITY)) {
			if(bmoOpportunity.getId() > 0) {
				if(value.isEmpty() || Integer.parseInt(value) < 0)
					bmUpdateResult.addError(bmoOpportunity.getIdFieldName(), "Debe seleccionar un cliente");
				else
				bmUpdateResult = copyOpportunity( bmoOpportunity, Integer.parseInt(value), bmUpdateResult);
			}
			else
				bmUpdateResult.addError(bmoOpportunity.getIdFieldName(), "Debe seleccionar una oportunidad");
		} else if (action.equals(BmoOpportunity.ACTION_CHANGEWFLOWTYPE)) {
			// Obtener datos a cambiar
			StringTokenizer tabs = new StringTokenizer(value, "|");
			String wflowTypeId = "", effectWflowTypeId = "";
			while (tabs.hasMoreTokens()) {
				wflowTypeId = tabs.nextToken();
				effectWflowTypeId = tabs.nextToken();
			}
			
			// Obtener ID de flujos viejos/nuevos
			int oldWFlowTypeId = bmoOpportunity.getWFlowTypeId().toInteger();
			int oldEffectWFlowTypeId = bmoOpportunity.getForeignWFlowTypeId().toInteger();
			int newWFlowTypeId = Integer.parseInt(wflowTypeId);
			int newEffectWFlowTypeId = Integer.parseInt(effectWflowTypeId);
			
			// Obtener nombre de los flujos viejos/nuevos
			PmWFlowType pmEffectWFlowType = new PmWFlowType(getSFParams());
			String oldWFlowTypeName = bmoOpportunity.getBmoWFlowType().getName().toString();
			String oldEffectWFlowTypeName = ((BmoWFlowType)pmEffectWFlowType.get(bmoOpportunity.getForeignWFlowTypeId().toInteger())).getName().toString();
			String newWFlowTypeName = ((BmoWFlowType)pmEffectWFlowType.get(newWFlowTypeId)).getName().toString();
			String newEffectWFlowTypeName = ((BmoWFlowType)pmEffectWFlowType.get(newEffectWFlowTypeId)).getName().toString();
			
			// Volver a Validar que exista un Seguimiento/Efecto
			if (!(newWFlowTypeId > 0) || !(newEffectWFlowTypeId > 0))
				bmUpdateResult.addError("", "<b>Debe seleccionar un "
						+ "" + getSFParams().getFieldFormTitle(bmoOpportunity.getWFlowTypeId()) + ""
						+ "/" + getSFParams().getFieldFormTitle(bmoOpportunity.getForeignWFlowTypeId()) + ""
						+ " para cambiar el Workflow.</b>");
	
			// Validar que no sea el mismo
			if (oldWFlowTypeId == newWFlowTypeId
					&& oldEffectWFlowTypeId == newEffectWFlowTypeId) {
				bmUpdateResult.addError("", "<b>El WorkFlow debe ser distinto al actual.</b>");
			} else {
				PmConn pmConn = new PmConn(getSFParams());

				try {
					pmConn.open();
					pmConn.disableAutoCommit();
					
					// Obtener flujo
					PmWFlow pmWFlow = new PmWFlow(getSFParams());
					BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoOpportunity.getWFlowId().toInteger());
					
					printDevLog("old wflowType: "+bmoOpportunity.getWFlowTypeId().toString() 
							+ "  old wflowTypeEffect: "+bmoOpportunity.getForeignWFlowTypeId().toString());
					
					printDevLog("new wflowType: "+newWFlowTypeId
							+ "  new wflowTypeEffect: "+newEffectWFlowTypeId);
					
					// Renueva el WorkFlow
					pmWFlow.changeWorkWFlowType(pmConn, bmoWFlow, newWFlowTypeId, bmUpdateResult);

					// Guardar nuevo workFlow
					bmoOpportunity.getWFlowTypeId().setValue(newWFlowTypeId);
					bmoOpportunity.getForeignWFlowTypeId().setValue(newEffectWFlowTypeId);
					super.save(pmConn, bmoOpportunity, bmUpdateResult);					
					
					// Generar Bitacora
					String modifyWFlowType = "", modifyEffectWFlowType = "";
					if (oldWFlowTypeId != newWFlowTypeId)
						modifyWFlowType = getSFParams().getFieldFormTitle(bmoOpportunity.getWFlowTypeId()) 
						+ " de \"" +  oldWFlowTypeName + "\" a \"" + newWFlowTypeName + "\"";
					
					if (oldEffectWFlowTypeId != newWFlowTypeId)
						modifyEffectWFlowType = getSFParams().getFieldFormTitle(bmoOpportunity.getForeignWFlowTypeId())
						+ " de \"" +  oldEffectWFlowTypeName + "\" a \"" + newEffectWFlowTypeName+ "\"";
					
					// Arreglo coma
					String coma = ", ";
					if (modifyWFlowType.equals("")) coma = "";
					else if (modifyEffectWFlowType.equals("")) coma = "";
	
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOpportunity.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
							"Cambio de: " + modifyWFlowType + coma + modifyEffectWFlowType + ".");
					
					if (!bmUpdateResult.hasErrors())
						pmConn.commit();
	
				} catch (SFException e) {
					pmConn.rollback();
					throw new SFException(this.getClass() + " - action() ChangeWFlowType: " + e.toString());
				} finally {
					pmConn.close();
				}
			}
		} else if (action.equals(BmoOpportunity.ACTION_GETSUMTOTAL)) {
			bmUpdateResult.addMsg(getSumTotal(Integer.parseInt(value)));
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOpportunity = (BmoOpportunity)bmObject;

		if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_REVISION || bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_HOLD) {
			// Eliminar datos adicionales
			PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
			BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
			BmFilter filterByOpportunity = new BmFilter();
			filterByOpportunity.setValueFilter(bmoOpportunityDetail.getKind(), bmoOpportunityDetail.getOpportunityId(), bmoOpportunity.getId());
			ListIterator<BmObject> projectDetailList = pmOpportunityDetail.list(pmConn, filterByOpportunity).listIterator();
			while (projectDetailList.hasNext()) {
				bmoOpportunityDetail = (BmoOpportunityDetail)projectDetailList.next();
				pmOpportunityDetail.delete(pmConn,  bmoOpportunityDetail, bmUpdateResult);
			}	

			// Eliminar oportunidad
			super.delete(pmConn, bmoOpportunity, bmUpdateResult);

			// Eliminar cotizaciones
			if (!bmUpdateResult.hasErrors()) {
				PmQuote pmQuote = new PmQuote(getSFParams());
				if (bmoOpportunity.getQuoteId().toInteger() > 0) {
					BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

					pmQuote.delete(pmConn,  bmoQuote, bmUpdateResult);
				}
			}

			// Actualizar Estatus Cliente
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOpportunity.getCustomerId().toInteger());
			if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			}

			if (!bmUpdateResult.hasErrors()) {
				// Eliminar flujos
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				BmoWFlow bmoWFlow = new BmoWFlow();
				filterByOpportunity.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoOpportunity.getId());			
				BmFilter filterWFlowCategory = new BmFilter();
				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoOpportunity.getProgramCode());
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				filterList.add(filterByOpportunity);
				filterList.add(filterWFlowCategory);
				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
				while (wFlowList.hasNext()) {
					bmoWFlow = (BmoWFlow)wFlowList.next();
					pmWFlow.delete(pmConn, bmoWFlow, bmUpdateResult);
				}
			}

		} else {
			bmUpdateResult.addError(bmoOpportunity.getStatus().getName(), "No se puede eliminar la Oportunidad - está " + bmoOpportunity.getStatus().getSelectedOption().getLabel() + ".");
		}
		
//		if(bmoOpportunity.getRfquId().toInteger()>0) {
//			PmRFQU pmRFQU = new PmRFQU(getSFParams());
//			BmoRFQU bmoRFQU = new BmoRFQU();
//			
//			bmoRFQU = (BmoRFQU)pmRFQU.get(pmConn, bmoOpportunity.getRfquId().toInteger());
//			bmoRFQU.getStatusRFQU().setValue(BmoRFQU.STATUS_PROCESSING);
//			pmRFQU.save(pmConn, bmoRFQU, bmUpdateResult);
//		}
		
		return bmUpdateResult;
	}

	// Enviar correo al Cambiar Estatus de la Oportunidad
	public void sendMailChangeStatusOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity) throws SFException {

		if (!getSFParams().isProduction()) 
			System.out.println("Enviar correo Cambio estatus OP: "+ bmoOpportunity.getCode().toString());

		// Vendedor
		BmoUser bmoSalesman = new BmoUser();
		PmUser pmUser = new PmUser(getSFParams());
		bmoSalesman = (BmoUser)pmUser.get(bmoOpportunity.getUserId().toInteger());

		// Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOpportunity.getCustomerId().toInteger());

		// Lista de correos de los usuarios del Flujo de la Oportunidad, que se le va a enviar notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		mailList = getWFlowUsersMailList(pmConn, bmoOpportunity.getWFlowId().toInteger());

		String subject = getSFParams().getMainAppTitle()
				+ " Cambio de Estatus de la Oportunidad: " 
				+ bmoOpportunity.getCode().toString() 
				+ " " + bmoOpportunity.getName().toString();

		String msgBody = HtmlUtil.mailBodyFormat(
				getSFParams(), 
				"Cambio de Estatus de la Oportunidad",
				" 	<p style=\"font-size:12px\"> "
						+ " <b>Oportunidad:</b> " + bmoOpportunity.getCode().toString() + " " + bmoOpportunity.getName().toString()
						+ "	<br> "
						+ "	<b>Cliente:</b> " + bmoCustomer.getCode().toString() 
						+ 				" " + bmoCustomer.getDisplayName().toString()  
						+ 				" " + ((bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY)) 
								? " ("+ bmoCustomer.getLegalname().toString() +")" : "")
						+ "	<br> "
						+ "	<b>Estatus:</b> " 
						+ " " + bmoOpportunity.getStatus().getSelectedOption().getLabeltoHtml()
						+ "	<br> "
						+ "	<b>Fecha Cierre:</b> " 
						+ " " + bmoOpportunity.getSaleDate().toString()
						+ "	<br> "
						+ "	<b>Fecha Expiración:</b> " 
						+ " " + bmoOpportunity.getExpireDate().toString()
						+ "	<br> "
						+ "	<b>Vendedor:</b> " + bmoSalesman.getFirstname().toString()
						+ " " + bmoSalesman.getFatherlastname().toString()
						+ " " + bmoSalesman.getMotherlastname().toString()
						+ "	<p align=\"center\" style=\"font-size:12px\"> "
						+ "		Favor de dar Seguimiento al Pedido <a target=\"_blank\" href=\""
						+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoOpportunity.getProgramCode() +"&foreignid=" + bmoOpportunity.getId() + "\">Aqu&iacute;</a>. "
						//+ getSFParams().getAppURL() + "symgf_start.jsp" + "\">Aqu&iacute;</a>. "
						+ "	</p> "
				);

		SFSendMail.send(getSFParams(),
				mailList,  
				getSFParams().getBmoSFConfig().getEmail().toString(), 
				getSFParams().getBmoSFConfig().getAppTitle().toString(), 
				subject, 
				msgBody);

	}
	
	// Crea el pedido a partir de una oportunidad
//	public BmUpdateResult createFromRFQU(PmConn pmConn, BmoRFQU bmoRFQU,
//		BmUpdateResult bmUpdateResult) throws SFException {
//		PmCurrency pmCurrency = new PmCurrency(getSFParams());
//		BmoCurrency bmoCurrency= new BmoCurrency();
//		PmOrderType pmOrderType = new PmOrderType(getSFParams());
//		BmoOrderType bmoOrderType= new BmoOrderType();
////		PmQuote pmQuote = new PmQuote(getSFParams());
////		BmoQuote bmoQuote= new BmoQuote();
//		
//	
//	int currencyId =((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger();
//	bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, currencyId);
//	bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoRFQU.getOrderTypeId().toInteger());
//	
////		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
//		BmoOpportunity bmoOpportunity = new BmoOpportunity();
//		bmoOpportunity.getName().setValue(bmoRFQU.getAffair().toString());
//		bmoOpportunity.getUserId().setValue(bmoRFQU.getUserSale().toInteger());
//		bmoOpportunity.getCustomerId().setValue(bmoRFQU.getCustomerId().toInteger());
//		bmoOpportunity.getCustomerContactId().setValue(bmoRFQU.getCustomerContactId().toInteger());
//		bmoOpportunity.getStartDate().setValue(bmoRFQU.getDateRFQU().toString());
//		bmoOpportunity.getEndDate().setValue(bmoRFQU.getDateRFQU().toString());
//		bmoOpportunity.getWFlowTypeId().setValue(bmoRFQU.getwFlowTypeId().toInteger());
//		bmoOpportunity.getOrderTypeId().setValue(bmoRFQU.getOrderTypeId().toInteger());
//		bmoOpportunity.getDescription().setValue(bmoRFQU.getObjectiveRFQU().toString());
//		bmoOpportunity.getCompanyId().setValue(bmoRFQU.getCompanyId().toInteger());
//		bmoOpportunity.getCurrencyId().setValue(currencyId);
//		bmoOpportunity.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
//		bmoOpportunity.getForeignWFlowTypeId().setValue(bmoRFQU.getForeignWFlowTypeId().toInteger());
//		bmoOpportunity.getBudgetItemId().setValue(bmoOrderType.getDefaultBudgetItemId().toInteger());
//		bmoOpportunity.getEstimationId().setValue(bmoRFQU.getEstimationId().toInteger());
//		bmoOpportunity.getRfquId().setValue(bmoRFQU.getId());
//		
//		// Almacena pedido preliminar
//		this.save(pmConn, bmoOpportunity, bmUpdateResult);
//			printDevLog("Acabo de guardar la Opportunidad y corre el metodo createOpportunityItemsFromQuote ************* ");
//		
//		// Obtener los items de las cotizaciones
//		createOpportunityItemsFromQuote(pmConn, bmoOpportunity, bmUpdateResult);
//
//		return bmUpdateResult;
//	}
	
	// Crear los items de una estimacion
//		public void createOpportunityItemsFromQuote(PmConn pmConn, BmoOpportunity bmoOpportunity, BmUpdateResult bmUpdateResult)
//				throws SFException {
//
//			printDevLog("ENTRO A CREATE OPPORTUNITY ITEMS FORM QUOTE LEL *******");
//
//			// Obtener Estimación
//		PmEstimation pmEstimation = new PmEstimation(getSFParams());
//		BmoEstimation bmoEstimation = new BmoEstimation();
//		bmoEstimation = (BmoEstimation) pmEstimation.get(pmConn, bmoOpportunity.getEstimationId().toInteger());
//		
//		// Filtro de items de la esitmacino por grupo de cotización
//		PmEstimationGroup pmEstimationGroup = new PmEstimationGroup(getSFParams());
//		BmoEstimationGroup bmoEstimationGroup = new BmoEstimationGroup();
//		BmFilter byEstimationGroupFilter = new BmFilter();
//		byEstimationGroupFilter.setValueFilter(bmoEstimationGroup.getKind(), bmoEstimationGroup.getEstimationId().getName(),
//				bmoEstimation.getId());
//		// Crear los grupos del pedido
//		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
//		Iterator<BmObject> estimationGroupIterator = pmEstimationGroup.list(byEstimationGroupFilter).iterator();
//		while (estimationGroupIterator.hasNext()) {
//			bmoEstimationGroup = (BmoEstimationGroup) estimationGroupIterator.next();
//			BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
//			bmoQuoteGroup.getName().setValue(bmoEstimationGroup.getName().toString());
//			bmoQuoteGroup.getDescription().setValue(bmoEstimationGroup.getDescription().toString());
//			bmoQuoteGroup.getAmount().setValue(bmoEstimationGroup.getAmount().toDouble());
//			bmoQuoteGroup.getIsKit().setValue(bmoEstimationGroup.getIsKit().toBoolean());
//			bmoQuoteGroup.getShowQuantity().setValue(bmoEstimationGroup.getShowQuantity().toBoolean());
//			bmoQuoteGroup.getQuoteId().setValue(bmoOpportunity.getQuoteId().toInteger());
//			pmQuoteGroup.save(pmConn, bmoQuoteGroup, bmUpdateResult);
//
//			// Obten el ultimo ID generado, que es el del grupo de pedido
//			int quoteGroupId = bmoQuoteGroup.getId();
//			// Filtro de items de la estimacion por Grupo de estimacion
//			PmEstimationRFQItem pmEstimationRFQItem = new PmEstimationRFQItem(getSFParams());
//			BmoEstimationRFQItem bmoEstimationRFQItem = new BmoEstimationRFQItem();
//			BmFilter byQuoteFilter = new BmFilter();
//			byQuoteFilter.setValueFilter(bmoEstimationRFQItem.getKind(), bmoEstimationRFQItem.getEstimationGroupId().getName(),
//					bmoEstimationGroup.getId());
//
////			PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
//			Iterator<BmObject> estimationItemIterator = pmEstimationRFQItem.list(byQuoteFilter).iterator();
//			if(!(bmoEstimationRFQItem.getProductId().toInteger()>0)) {
//
//			while (estimationItemIterator.hasNext()) {
//				bmoEstimationRFQItem = (BmoEstimationRFQItem) estimationItemIterator.next();
//				BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
//				bmoQuoteItem.getProductId().setValue(bmoEstimationRFQItem.getProductId().toInteger());
//				bmoQuoteItem.getName().setValue(bmoEstimationRFQItem.getName().toString());
//				bmoQuoteItem.getQuantity().setValue(bmoEstimationRFQItem.getQuantity().toDouble());
//				bmoQuoteItem.getAmount().setValue(0);
//				bmoQuoteItem.getPrice().setValue(0);
//				bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupId);
//				bmoQuoteItem.getBudgetItemId().setValue(bmoEstimationRFQItem.getBudgetItemId().toInteger());
//				bmoQuoteItem.getAreaId().setValue(bmoEstimationRFQItem.getAreaId().toInteger());
//				super.save(pmConn, bmoQuoteItem, bmUpdateResult);
//			}
//			
//				}else {
//					while (estimationItemIterator.hasNext()) {
//					PmProduct pmProduct = new PmProduct(getSFParams());
//					BmoProduct bmoProduct = new BmoProduct();
//					bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoEstimationRFQItem.getProductId().toInteger());
//					bmoEstimationRFQItem = (BmoEstimationRFQItem) estimationItemIterator.next();
//					BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
//					bmoQuoteItem.getProductId().setValue(bmoProduct.getId());
//					bmoQuoteItem.getName().setValue(bmoProduct.getName().toString());
//					bmoQuoteItem.getDescription().setValue(bmoProduct.getDescription().toString());
//					bmoQuoteItem.getQuantity().setValue(bmoEstimationRFQItem.getQuantity().toDouble());
//					bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupId);
//					bmoQuoteItem.getAmount().setValue(0);
//					bmoQuoteItem.getPrice().setValue(0);
//					// Pasar datos de control presupuestal
//					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//						bmoQuoteItem.getBudgetItemId().setValue(bmoProduct.getBudgetItemId().toInteger());
//						bmoQuoteItem.getAreaId().setValue(bmoProduct.getAreaId().toInteger());
//					}
//					super.save(pmConn, bmoQuoteItem, bmUpdateResult);
//
//				}
//				// Primero agrega el ultimo valor
//
//				// Viene de un producto
////				if (bmoQuoteItem.getProductId().toInteger() > 0) {
//				// Traer el Producto para validar de que tipo es
//	
////					BmoProduct bmoProduct = new BmoProduct();
////					PmProduct pmProduct = new PmProduct(getSFParams());
////					bmoProduct = (BmoProduct) pmProduct.get(bmoOrderItem.getProductId().toInteger());
//
//					// Crear subProductos si el producto es uno de tipo Compuesto
////					if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED) {
////						// Primero agrega el ultimo valor
////						// super.save(pmConn, bmoOrderItem, bmUpdateResult);
////						// crear/actualizar subproductos
////						pmOrderItem.createItemsComposed(pmConn, bmoOrderItem, bmoOrder, true, bmUpdateResult);
////					}
////				}
//			}
//		}
//		}
	public BmUpdateResult copyOpportunity(BmoOpportunity bmoOpportunity, int idCustomer, BmUpdateResult bmUpdateResult)
			throws SFException {

		BmoCustomer bmoCustomer = new BmoCustomer();
		BmoQuote bmoQuoteOld = new BmoQuote();
		try {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			BmoOpportunity newBmoOpportunity = new BmoOpportunity();
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			
			PmCustomer pmCustomer = new PmCustomer(getSFParams());		
			bmoCustomer = (BmoCustomer) pmCustomer.get(idCustomer);			
			PmQuote pmQuote = new PmQuote(getSFParams());
			bmoQuoteOld = (BmoQuote) pmQuote.get(bmoOpportunity.getQuoteId().toInteger());

			newBmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_REVISION);
			newBmoOpportunity.getName().setValue(bmoOpportunity.getName().toString());
			newBmoOpportunity.getDescription().setValue(bmoOpportunity.getDescription().toString() + "-COPIA_"+bmoOpportunity.getCode().toString());
			newBmoOpportunity.getSaleProbability().setValue(bmoOpportunity.getSaleProbability().toString());
			newBmoOpportunity.getSaleDate().setValue(bmoOpportunity.getSaleDate().toString());
			newBmoOpportunity.getExpireDate().setValue(bmoOpportunity.getExpireDate().toString());
			newBmoOpportunity.getAmount().setValue(bmoOpportunity.getAmount().toDouble());
			newBmoOpportunity.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			newBmoOpportunity.getEndDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			newBmoOpportunity.getCustomerId().setValue(bmoCustomer.getId());
			newBmoOpportunity.getCompanyId().setValue(bmoOpportunity.getCompanyId().toString());
			newBmoOpportunity.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
			newBmoOpportunity.getWFlowTypeId().setValue(bmoOpportunity.getWFlowTypeId().toString());

			newBmoOpportunity.getUserId().setValue(bmoOpportunity.getUserId().toInteger());
			newBmoOpportunity.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
			newBmoOpportunity.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toString());
			newBmoOpportunity.getLoseMotiveId().setValue(bmoOpportunity.getLoseMotiveId().toInteger());
			newBmoOpportunity.getLoseComments().setValue(bmoOpportunity.getLoseComments().toString());
			newBmoOpportunity.getForeignWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toInteger());
			newBmoOpportunity.getMarketId().setValue(bmoOpportunity.getMarketId().toString());
			newBmoOpportunity.getPropertyModelId().setValue(bmoOpportunity.getPropertyModelId().toInteger());
			newBmoOpportunity.getVenueId().setValue(bmoOpportunity.getVenueId().toString());

			newBmoOpportunity.getCustomerAddressId().setValue(bmoOpportunity.getCustomerAddressId().toInteger());
			newBmoOpportunity.getCustomField1().setValue(bmoOpportunity.getCustomField1().toString());
			newBmoOpportunity.getCustomField2().setValue(bmoOpportunity.getCustomField2().toInteger());
			newBmoOpportunity.getCustomField3().setValue(bmoOpportunity.getCustomField3().toString());
			newBmoOpportunity.getCustomField4().setValue(bmoOpportunity.getCustomField4().toInteger());

			newBmoOpportunity.getCustomerRequisition().setValue(bmoOpportunity.getCustomerRequisition().toString());
			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				newBmoOpportunity.getAreaId().setValue(bmoOpportunity.getAreaId().toString());
				newBmoOpportunity.getBudgetItemId().setValue(bmoOpportunity.getBudgetItemId().toString());
			}
			pmOpportunity.save(pmConn, newBmoOpportunity, bmUpdateResult);
			BmoQuote bmoQuote = (BmoQuote) pmQuote.get(newBmoOpportunity.getQuoteId().toInteger());
			pmQuote.copyQuoteItemsFromQuote(pmConn, bmoQuoteOld.getId(), bmoQuote, bmUpdateResult);

			bmUpdateResult.setBmObject(pmOpportunity.get(newBmoOpportunity.getId()));
			pmConn.close();
		} catch (SFException e) {
			throw new SFException("PmOpportunity-copyOpportunity() ERROR: " + e.toString());
		}
		return bmUpdateResult;
	}

	// Prepara lista de destinatarios del correo
	private ArrayList<SFMailAddress> getWFlowUsersMailList(PmConn pmConn, int wflowId) throws SFException {

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String mailString = "", sql = "";

		// Revisar usuarios asignados al flujo
		sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid FROM wflowusers " +
				" LEFT JOIN users ON (wflu_userid = user_userid) " +
				" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " +
				" AND wflu_wflowid = " + wflowId + 
				" GROUP BY user_userid " +
				" ORDER BY user_email ASC";

		if (!getSFParams().isProduction()) 
			System.out.println("Buscar usuarios asignados al Flujo: " + wflowId + " SQL: " + sql);

		try {
			pmConn.doFetch(sql);

			int mailCount = 0;
			// Si existen colaboradores en el flujo
			while (pmConn.next()) {
				String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
				SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
				mailList.add(mail);
				mailCount++;

				mailString += pmConn.getString(1) + ", "; 
			}

			// Si no hay colaboradores en el flujo, tomar los colaboradores de los grupos
			if (mailCount == 0) {

				if (!getSFParams().isProduction()) 
					System.out.println("No hay destinatarios del correo - se busca enviar a todos los usuarios del grupo del Flujo.");

				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid " +
						" FROM profileusers " +
						" LEFT JOIN users ON (pfus_userid = user_userid) " +
						" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " +
						" AND pfus_profileid IN (SELECT wflu_profileid FROM wflowusers WHERE wflu_wflowid = " + wflowId + ") " +
						" GROUP BY user_userid " +
						" ORDER BY user_email ASC";

				if (!getSFParams().isProduction()) 
					System.out.println("Buscar usuarios asignados a los Grupos del flujo - SQL: " + sql);

				pmConn.doFetch(sql);
				while (pmConn.next()) {
					String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
					SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
					mailList.add(mail);
					mailCount++;
					mailString += pmConn.getString(1) + ", "; 
				}
			}

			// Si no hay colaboradores, mandar correo al vendedor del wflow
			if (mailCount == 0) {
				if (!getSFParams().isProduction()) 
					System.out.println("Mandar correo al vendedor del wflow");
				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname " +
						" FROM wflows " +
						" LEFT JOIN users ON (wflw_userid = user_userid) " +
						" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " +
						" AND wflw_wflowid = " + wflowId +
						" ORDER BY user_email ASC";

				pmConn.doFetch(sql);
				while (pmConn.next()) {
					String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
					SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
					mailList.add(mail);
					mailCount++;
					mailString += pmConn.getString(1) + ", "; 
				}
			}

			// No hay ni usuarios del wflow ni del grupo asignado
			if (mailCount == 0) 
				System.out.println("PmOrder-getWFlowUsersMailList(): No hay destinatarios del correo - no hay usuarios asignados al grupo del Flujo.");

			if (!getSFParams().isProduction()) 
				System.out.println(this.getClass().getName() + "-getWFlowUsersMailList(): La lista de correos es: " + mailString);

		} catch (SFPmException e) {
			throw new SFException("PmOrder-getWFlowUsersMailList() ERROR: " + e.toString());
		}

		return mailList;
	}
	private boolean existOrder(PmConn pmConn,BmoOpportunity bmoOpportunity) throws SFPmException {
		String sql = "SELECT * FROM orders where orde_opportunityid = " + bmoOpportunity.getId();
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			return true;
		}else {
			return false;
		}
		
	}
	// recordatorio expiracion 
	public void remindExpired() throws SFException {
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		BmFilter statusFilter = new BmFilter();
		
		statusFilter.setValueFilter(bmoOpportunity.getKind(), bmoOpportunity.getStatus(), "" + BmoOpportunity.STATUS_REVISION);
		
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		Iterator<BmObject> opportunityIterator = pmOpportunity.list(statusFilter).iterator();
		while (opportunityIterator.hasNext()) {
			bmoOpportunity = (BmoOpportunity)opportunityIterator.next();	
			if (bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()
					&& (bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getDaysRemindExpired().toInteger() > 0) 
					&& !bmoOpportunity.getExpireDate().toString().equalsIgnoreCase("")) {
				
				String Calnow = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
				//Dias entre 2 fechas (Fecha hoy y fecha expiración)
				int previousDate =  SFServerUtil.daysBetween(getSFParams().getDateFormat(), Calnow, bmoOpportunity.getExpireDate().toString());
				
				if (previousDate == bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getDaysRemindExpired().toInteger()) {
					
					sendMailRemindExpired(bmoOpportunity);
				}
				
			}
			
		}
	}
	public void sendMailRemindExpired(BmoOpportunity bmoOpportunity) throws SFPmException, SFException {
		BmoUser bmoSalesman = new BmoUser();
		bmoSalesman = (BmoUser)new PmUser(getSFParams()).get(bmoOpportunity.getUserId().toInteger());
		
		if (bmoSalesman.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
			mailList.add(new SFMailAddress(bmoSalesman.getEmail().toString(), bmoSalesman.getFirstname().toString() + " " + bmoSalesman.getFatherlastname().toString()));
			
			String subject = getSFParams().getAppCode() + " Recordatorio de Expiración de Oportunidad: "
					+ bmoOpportunity.getCode().toString() + " " + bmoOpportunity.getName().toString();
			
			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio de Expiración de Oportunidad", 
					" <p style=\"font-size:12px\">"
					+ " <b>Oportunidad:</b> " + bmoOpportunity.getCode().toHtml() + " " + bmoOpportunity.getName().toHtml()
					+ " <br> <b>Fecha de Expiración:</b> " + bmoOpportunity.getExpireDate().toString()
					);
			if (getSFParams().isProduction()) {
				SFSendMail.send(getSFParams(), mailList,  getSFParams().getBmoSFConfig().getEmail().toString(),
					getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
			} 
		}
		
	}
	public BmUpdateResult updateRequisitions(PmConn pmConn,int oportunityId,BmoOrder bmoOrder,BmUpdateResult bmUpdateResult) throws SFException {
		BmFilter OpoFilter = new BmFilter();
		BmoRequisition bmoRequisition = new BmoRequisition();
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		OpoFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOportunityId(),oportunityId);
		Iterator<BmObject> requisitionIterator = pmRequisition.list(pmConn ,OpoFilter).iterator();
		while (requisitionIterator.hasNext()) {
			BmoRequisition nextBmoRequisition = (BmoRequisition)requisitionIterator.next();
			nextBmoRequisition.getOrderId().setValue(bmoOrder.getId());
			pmRequisition.saveSimple(pmConn, nextBmoRequisition, bmUpdateResult);
		}
		
		return bmUpdateResult;
	}
	
	private String getSumTotal(int userId) throws SFPmException {
		String sql = "SELECT sum( oppo_amount) as total FROM opportunities " + 
				"WHERE (  ( oppo_status = '" + BmoOpportunity.STATUS_REVISION + "'  )  )  ";
		if (userId > 0)sql += "  AND oppo_userid = " + userId;
		String result = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			result = pmConn.getString("total");
		}else {
			result = "0.0";
		}
		pmConn.close();
		return result;
		
	}
	
}
	
	
//}
