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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.ac.PmOrderSession;
import com.flexwm.server.ac.PmOrderSessionTypePackage;
import com.flexwm.server.ac.PmSessionSale;
import com.flexwm.server.ar.PmPropertyRental;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmOpportunity;
import com.flexwm.server.cm.PmOpportunityDetail;
import com.flexwm.server.cm.PmProject;
//import com.flexwm.server.cm.PmProjectStep;
import com.flexwm.server.cm.PmQuote;
import com.flexwm.server.cm.PmQuoteGroup;
import com.flexwm.server.cm.PmQuoteItem;
import com.flexwm.server.co.PmOrderProperty;
import com.flexwm.server.co.PmOrderPropertyModelExtra;
import com.flexwm.server.co.PmOrderPropertyTax;
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.cr.PmCredit;
import com.flexwm.server.fi.PmBankMovConcept;
import com.flexwm.server.fi.PmBankMovType;
import com.flexwm.server.fi.PmBankMovement;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.server.fi.PmRaccountItem;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowDocument;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.server.wf.PmWFlowUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.cm.BmoProject;
//import com.flexwm.shared.cm.BmoProjectStep;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.co.BmoOrderPropertyModelExtra;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountItem;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoExtraOrderProfile;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDetail;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmFormat;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFormat;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowUser;


public class PmOrder extends PmObject {
	BmoOrder bmoOrder;

	public PmOrder(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrder = new BmoOrder();
		setBmObject(bmoOrder);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrder.getOrderTypeId(), bmoOrder.getBmoOrderType()),
				new PmJoin(bmoOrder.getCurrencyId(), bmoOrder.getBmoCurrency()),
				new PmJoin(bmoOrder.getCustomerId(), bmoOrder.getBmoCustomer()),
				new PmJoin(bmoOrder.getBmoCustomer().getSalesmanId(), bmoOrder.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoOrder.getBmoCustomer().getBmoUser().getAreaId(),
						bmoOrder.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoOrder.getBmoCustomer().getBmoUser().getLocationId(),
						bmoOrder.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoOrder.getBmoCustomer().getTerritoryId(), bmoOrder.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoOrder.getBmoCustomer().getReqPayTypeId(), bmoOrder.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoOrder.getWFlowId(), bmoOrder.getBmoWFlow()),
				new PmJoin(bmoOrder.getBmoWFlow().getWFlowPhaseId(), bmoOrder.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoOrder.getWFlowTypeId(), bmoOrder.getBmoWFlowType()),
				new PmJoin(bmoOrder.getBmoWFlowType().getWFlowCategoryId(),
						bmoOrder.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoOrder.getBmoWFlow().getWFlowFunnelId(), bmoOrder.getBmoWFlow().getBmoWFlowFunnel()))));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de Oportunidads
		if (getSFParams().restrictData(bmoOrder.getProgramCode())) {

			filters = "( orde_userid in (" + " SELECT user_userid FROM users " + " WHERE " + " user_userid = "
					+ loggedUserId + " or user_userid in ( " + " 	select u2.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u3.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u4.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
					+ " left join users u4 on (u4.user_parentid = u3.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u5.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
					+ " left join users u4 on (u4.user_parentid = u3.user_userid) "
					+ " left join users u5 on (u5.user_parentid = u4.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " ) " + " OR " + " ( " + " orde_wflowid IN ( "
					+ " SELECT wflw_wflowid FROM wflowusers  " + " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) "
					+ " WHERE wflu_userid = " + loggedUserId + " AND wflw_callercode = 'ORDE' " + "   ) " + " ) "
					+ " ) ";
		}

		// Filtro de pedidos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += "( orde_companyid in (" + " select uscp_companyid from usercompanies " + " where "
					+ " uscp_userid = " + loggedUserId + " )" + ") ";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += " orde_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrder = (BmoOrder) autoPopulate(pmConn, new BmoOrder());

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		if (pmConn.getInt(bmoOrderType.getIdFieldName()) > 0)
			bmoOrder.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else
			bmoOrder.setBmoOrderType(bmoOrderType);

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0)
			bmoOrder.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else
			bmoOrder.setBmoCurrency(bmoCurrency);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		if (pmConn.getInt(bmoCustomer.getIdFieldName()) > 0)
			bmoOrder.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else
			bmoOrder.setBmoCustomer(bmoCustomer);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if (pmConn.getInt(bmoWFlow.getIdFieldName()) > 0)
			bmoOrder.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else
			bmoOrder.setBmoWFlow(bmoWFlow);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		if (pmConn.getInt(bmoWFlowType.getIdFieldName()) > 0)
			bmoOrder.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else
			bmoOrder.setBmoWFlowType(bmoWFlowType);

		return bmoOrder;
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmUpdateResult = super.save(bmObject, bmUpdateResult);

		if (!bmUpdateResult.hasErrors()) {
			bmoOrder = (BmoOrder) bmUpdateResult.getBmObject();
			// Guardar bitacora si esta autorizada la cotizacion
			if ((bmoOrder.getWFlowId().toInteger() > 0)
					&& bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
				addDataLog(bmoOrder, "Autorización del Pedido", bmUpdateResult);

				// Regresar el objeto de la Cotizacion
				bmUpdateResult.setBmObject(bmoOrder);
				bmUpdateResult.setId(bmoOrder.getId());
				
			}
			// Revisar si el tipo de Pedido maneja IVA
			if (bmoOrder.getBmoOrderType().getHasTaxes().toBoolean())
				bmoOrder.getTaxApplies().setValue(true);
		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoOrder bmoOrder = (BmoOrder)bmObject;
		BmoOrder bmoOrderPrevious = bmoOrder;

		boolean newRecord = false;
		if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			getMonth(bmoOrder, bmUpdateResult);
			getMonthContract(bmoOrder,bmUpdateResult);
			
		}
		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoOrder.setBmoOrderType((BmoOrderType)pmOrderType.get(pmConn, bmoOrder.getOrderTypeId().toInteger()));

		// Datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoOrder.getCustomerId().toInteger());

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoOrder.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoOrder, bmUpdateResult);

			bmoOrder.setId(bmUpdateResult.getId());

			if (!getSFParams().isProduction()) 
				System.out.println("Se guardo nuevo pedido: " + bmoOrder.getId() + " resultado id: " + bmUpdateResult.getId());

			//Obtener la moneda del sistema
			int currencyFlexId = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger();
			PmCurrency pmCurrency = new PmCurrency(getSFParams());
			BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, currencyFlexId);
			if (!(bmoOrder.getCurrencyId().toInteger() > 0)) 
				bmoOrder.getCurrencyId().setValue(bmoCurrency.getId());

			if (!(bmoOrder.getCurrencyParity().toDouble() > 0))	
				bmoOrder.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());

			// Si no esta asignada ya la clave por algun objeto foraneo, crear nueva
			if (!(bmoOrder.getCode().toString().length() > 0)) {
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, "REN-");			
					bmoOrder.getCode().setValue(code);
				} else {
					// La clave debe tener 4 digitos
					String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoOrder.CODE_PREFIX);			
					bmoOrder.getCode().setValue(code);
				}
			}	

			// Asigna IVA dependiendo tipo de pedido si viene de una renovacion no asigna IVA de tipo de pedido
			if (bmoOrder.getBmoOrderType().getHasTaxes().toBoolean() &&
					!(bmoOrder.getRenewOrderId().toInteger() > 0))
				bmoOrder.getTaxApplies().setValue(1);
			

			// Si es de tipo de renta y es nuevo, revisar que no haya problemas de bloqueo de fechas
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				PmOrderBlockDate pmOrderBlockDate = new PmOrderBlockDate(getSFParams());
				if (pmOrderBlockDate.orderInBlockedDates(pmConn, bmoOrder.getLockStart().toString(), bmoOrder.getLockEnd().toString(), bmUpdateResult)) {
					bmUpdateResult.addError(bmoOrder.getLockStart().getName(), "Las Fechas no están Disponibles para abrir nuevos Pedidos.");
				}
			}
			// Si es nuevo, y es de tipo venta o sesiones, asigna fecha default
			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {

				
				// Asigna fecha de bloqueo inicio por default
				// Si viene de una oportunidad
				if (bmoOrder.getOpportunityId().toInteger() > 0) {
					bmoOrder.getLockStart().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

					// Si Fecha fin de la op-ped es anterior a hoy, se asgina hoy mas un dia solo si es de tipo de Venta
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
								bmoOrder.getLockEnd().toString(), bmoOrder.getLockStart().toString())) {
							bmoOrder.getLockEnd().setValue(SFServerUtil.addDays(getSFParams().getDateTimeFormat(), 
									SFServerUtil.nowToString(getSFParams(),
											getSFParams().getDateTimeFormat()), 1));
						}
					}
				} else {
					if (bmoOrder.getLockStart().toString().equals(""))
						bmoOrder.getLockStart().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				}

				// Crea un grupo de items por default, si no existe alguno
				PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());				
				BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
				bmoOrderGroup.getOrderId().setValue(bmoOrder.getId());
				bmoOrderGroup.getName().setValue("Items");
				bmoOrderGroup.getShowQuantity().setValue(true);
				bmoOrderGroup.getShowAmount().setValue(true);
				bmoOrderGroup.getShowPrice().setValue(true);
				bmoOrderGroup.getPayConditionId().setValue(bmoOrder.getPayConditionId().toInteger());
				pmOrderGroup.saveSimple(pmConn, bmoOrderGroup, bmUpdateResult);	
			}

			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusRevision().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusAuthorized().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusFinished().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusCancelled().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			
		} else {
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusRevision().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusAuthorized().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusFinished().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED) && (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrdeStatusCancelled().toBoolean())) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(), " Este estatus esta desactivado ");
			}
			PmOrder pmOrderPrevious = new PmOrder(getSFParams());
			bmoOrderPrevious = (BmoOrder)pmOrderPrevious.get(bmoOrder.getId());
		}
		
		if(bmoOrderPrevious.getStatus().equals(""+BmoOrder.STATUS_FINISHED) && 
				(!bmoOrder.getStatus().equals(""+BmoOrder.STATUS_FINISHED))
				&& bmoOrderPrevious.getBmoOrderType().getType().equals(""+BmoOrderType.TYPE_LEASE)) {
			boolean hasRenewOrder = false;
			
			String sql = "SELECT * FROM orders "
					+ "	WHERE orde_reneworderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			hasRenewOrder= pmConn.next();
			
			//Manda error si tiene pedidos renovados
			if(hasRenewOrder) {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(),
						" No es posible cambiar el estatus. Tiene un pedido renovado");
			}
			else {
				if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUSUNFINISHED)) {
					bmUpdateResult.addError(bmoOrder.getStatus().getName(),
							" <b>No cuenta con permisos para Finalizar el Pedido</b>");
				}
			}
		}
		
		if (!newRecord){
			if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				BmoProperty bmoProperty = new BmoProperty();
				PmProperty pmProperty = new PmProperty(getSFParams());
				
				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				
				bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(pmConn, bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());

				bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());
				if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED || bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {
					bmoProperty.getAvailable().setValue(true);
					pmProperty.saveSimple(pmConn, bmoProperty, bmUpdateResult);
				}
				else {
					bmoProperty.getAvailable().setValue(false);
					pmProperty.saveSimple(pmConn, bmoProperty, bmUpdateResult);
				}
				//Si el pedido fue cancelado regresar la f.1er.Renta e incremento renta del contrato del pedido origen del que se esta cancelando 
				if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED) {
					if(bmoOrder.getRenewOrderId().toInteger() > 0) {
						BmoOrder bmoRenewOrderId = (BmoOrder) this.get(bmoOrder.getRenewOrderId().toInteger());
					
						bmoPropertyRental.getRentalScheduleDate().setValue(SFServerUtil.formatDate(getSFParams(), 
							getSFParams().getDateTimeFormat(), getSFParams().getDateFormat(), bmoRenewOrderId.getLockStart().toString()));
						bmoPropertyRental.getRentIncrease().setValue(SFServerUtil.formatDate(getSFParams(), 
							getSFParams().getDateTimeFormat(), getSFParams().getDateFormat(), bmoRenewOrderId.getLockEnd().toString()));
						pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);	
					}
				}
				
			}
		}
		
		//Si es de tipo arrendamiento 
//		if (!newRecord) {
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
//				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
//				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
//				
//				bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(pmConn, bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
//				if(!(bmoOrderPrevious.getStatus().equals(bmoOrder.getStatus()))) {		
//					// Asigna el estatus al contrato
//					if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) {
//						bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_CANCEL);
//						pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//					} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
//						bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_FINISHED);
//						pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//					} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
//						bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_REVISION);
//						pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//					} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
//						bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_AUTHORIZED);
//						pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//					}
//				}
//				if(!(bmoOrderPrevious.getLockStart().equals(bmoOrder.getLockStart()))) {		
//					// Asigna fecha inicio
//					bmoPropertyRental.getStartDate().setValue(bmoOrder.getLockStart().toString());	
//					pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//				}
//				
//				if(!(bmoOrderPrevious.getLockEnd().equals(bmoOrder.getLockEnd()))) {		
//					// Asigna fecha fin
//					bmoPropertyRental.getEndDate().setValue(bmoOrder.getLockEnd().toString());	
//					pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
//				}
//				
//			}
//		}
		
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			// Cambia estatus de pago a NORMAL si el pedido esta autorizado
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {

				BmoCredit bmoCredit = new BmoCredit();
				PmCredit pmCredit = new PmCredit(getSFParams());
				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());
				bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_NORMAL);

				pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
			}
		}
		// Si esta asignada la renovacion, pero no el origen, la iguala
		if (bmoOrder.getRenewOrderId().toInteger() > 0) {
			if (!(bmoOrder.getOriginRenewOrderId().toInteger() > 0)) {
				bmoOrder.getOriginRenewOrderId().setValue(bmoOrder.getRenewOrderId().toInteger());
			}
		}

		// Crear detalle del pedido si no existe
		PmOrderDetail pmOrderDetail = new PmOrderDetail(getSFParams());
		if (!pmOrderDetail.orderDetailExists(pmConn, bmoOrder.getId())) {
			BmoOrderDetail bmoOrderDetail = new BmoOrderDetail();
			bmoOrderDetail.getCloseDate().setValue(bmoOrder.getDateCreate().toString());
			bmoOrderDetail.getOrderDate().setValue(bmoOrder.getLockStart().toString());
			bmoOrderDetail.getOrderId().setValue(bmoOrder.getId());
			if(bmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger() > 0) {
				bmoOrderDetail.getAreaId().setValue(bmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger());
			}
			if(!bmoOrder.getBmoOrderType().getStatusDefaultDetail().equals("")) {
				bmoOrderDetail.getStatus().setValue(bmoOrder.getBmoOrderType().getStatusDefaultDetail().toChar());
			}
			pmOrderDetail.save(pmConn, bmoOrderDetail, bmUpdateResult);
		}

		// Validar partida presupuestal
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0)) 
				bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "Seleccione una Partida.");
		}
		
		// Validar que si esta cambiando el mercado, verifique si hay lineas con productos
		if (bmoOrderPrevious.getMarketId().toInteger() > 0) {
			if (bmoOrder.getMarketId().toInteger() != bmoOrderPrevious.getMarketId().toInteger()) {
				if (existProducts(pmConn, bmoOrder.getId()))
					bmUpdateResult.addError(bmoOrder.getMarketId().getName(), "El Pedido tiene Productos con Precios asignados al(la) " + getSFParams().getFieldFormTitle(bmoOrder.getMarketId()) + " actual. "
									+ "Debe eliminarlos para poder cambiar el(la) " + getSFParams().getFieldFormTitle(bmoOrder.getMarketId()) + "." );
			}
		}

		// Actualizar WFlow
		if (!bmUpdateResult.hasErrors()) {

			// Asigna el estatus al flujo
			char wFlowStatus = BmoWFlow.STATUS_ACTIVE;
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED) || 
					bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED))
				wFlowStatus = BmoWFlow.STATUS_INACTIVE;

			// Crea el WFlow y asigna el ID recien creado
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			bmoOrder.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoOrder.getWFlowTypeId().toInteger(), bmoOrder.getWFlowId().toInteger(), 
					bmoOrder.getProgramCode(), bmoOrder.getId(), bmoOrder.getUserId().toInteger(), bmoOrder.getCompanyId().toInteger(), bmoOrder.getCustomerId().toInteger(),
					bmoOrder.getCode().toString(), bmoOrder.getName().toString(), bmoOrder.getDescription().toString(), 
					bmoOrder.getLockStart().toString(), bmoOrder.getLockEnd().toString(), wFlowStatus, bmUpdateResult).getId());
		}

		// En caso de estar cancelado el pedido
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED) {
			// Cancelamos el credito
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Se esta cambiando de autorizada a cancelada, mandar mensaje
				if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED
						|| bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {

					if (!bmoOrder.getDeliveryStatus().equals(BmoOrder.DELIVERYSTATUS_PENDING))
						bmUpdateResult.addError(bmoOrder.getStatus().getName(), "El monto del crédito ya fue entregado");
				}	
			} else {
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION) 
						|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
					//Si es tipo de sesion cancelar la venta
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
						BmoSessionSale bmoSessionSale = new BmoSessionSale();
						PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
						bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(bmoOrder.getId(), bmoSessionSale.getOrderId().getName());

						bmoSessionSale.getStatus().setValue(BmoSessionSale.STATUS_CANCELLED);
						super.save(pmConn, bmoSessionSale, bmUpdateResult);
					}

					//Eliminar el envio de pedido
					if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {						
						PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());					
						if (hasOrderItemWithProduct(pmConn, bmoOrder) > 0) {
							pmOrderDelivery.deleteOrderDelivery(pmConn, bmoOrder, bmUpdateResult);
						}

						bmUpdateResult = super.save(pmConn, bmoOrder, bmUpdateResult);

						updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);
					}
				}	
				// Se esta Cancelando colocar estatus de entrega a Pendiente
				// de Tipo Consultoria
				// TODO: SI APLICAR CONSULTORIA?
				else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
					bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_PENDING);

				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				
				// Validar que existan CXC autorizadas, si no tienes permiso
				if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CANCELLEDWITHCCAUT)) {
					// Detener si existen cxc Autorizadas
					if (pmRaccount.orderHasAuthorizedRaccounts(pmConn, bmoOrder.getId()))
						bmUpdateResult.addError(bmoOrder.getStatus().getName(), 
								"El Pedido no se puede cancelar, existen CxC Autorizadas.");
				} 
				
				// Eliminar las cxc en revision 
				pmRaccount.deleteRaccount(pmConn, bmoOrder, bmUpdateResult);
			}

			// Actualiza valores
			if (!bmUpdateResult.hasErrors()) 
				updateBalance(pmConn, bmoOrder, bmUpdateResult);

			// Guardar fecha de cancelacion y limpiar autorizacion/finalizacion
			bmoOrder.getCancelledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrder.getCancelledUser().setValue(getSFParams().getLoginInfo().getUserId());			
			bmoOrder.getAuthorizedDate().setValue("");
			bmoOrder.getAuthorizedUser().setValue("");
			bmoOrder.getFinishedDate().setValue("");
			bmoOrder.getFinishedUser().setValue("");

			// Generar bitacora al Cancelar
			if (!bmUpdateResult.hasErrors()) {
				if (bmoOrder.getWFlowId().toInteger() > 0)
					addDataLog(pmConn, bmoOrder, "Pedido Cancelado", bmUpdateResult);
			}			
		} else {

			// Si se esta cambiando el estatus a En Revision, se modifica para poder hacer el resto de procesos
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				//Validar que exista una venta
				if (newRecord) {
					super.save(pmConn, bmoOrder, bmUpdateResult);
				}	

				// Es de tipo credito
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
					if (bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) ||
							bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_FINISHED)) {

						if (pmRaccount.orderHasRaccount(pmConn, bmoOrder, bmUpdateResult) > 0) {
							bmUpdateResult.addError(bmoOrder.getStatus().getName(), "Existen CxC ligadas al crédito");		
						}
						//Ver si tiene proyectos
//						if(getSFParams().hasRead(new BmoProjectStep().getProgramCode())) {
//							if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//								if(getProyects(pmConn,bmoOrderPrevious.getId())) {
//									bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No puede cambiar el estatus, existe un Proyecto ligado");
//								}
//							}
//						}
					}	
					// Actualiza valores
					if (!bmUpdateResult.hasErrors()) 
						updateBalance(pmConn, bmoOrder, bmUpdateResult);
				}
				
				// Es de tipo Arrendamiento
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					if (bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) ||
							bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_FINISHED)) {

						if (pmRaccount.orderHasRaccount(pmConn, bmoOrder, bmUpdateResult) > 0) {							
								bmUpdateResult.addError(bmoOrder.getStatus().getName(), "Existen CxC ligadas al Pedido");
						}
						//Ver si tiene proyectos
//						if(getSFParams().hasRead(new BmoProjectStep().getProgramCode())) {
//							if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//								if(getProyects(pmConn,bmoOrderPrevious.getId())) {
//									bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No puede cambiar el estatus, existe un Proyecto ligado");
//								}
//							}
//						}
					}	
					// Actualiza valores
					if (!bmUpdateResult.hasErrors()) 
						updateBalance(pmConn, bmoOrder, bmUpdateResult);
				}

				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
					// Avanzar fase, solo si es de tipo session
					PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());
					pmOrderSession.advanceStepPhaseAssigned(pmConn, bmoOrder, false, bmUpdateResult);
					//Ver si tiene proyectos
//					if(getSFParams().hasRead(new BmoProjectStep().getProgramCode())) {
//						if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//							if(getProyects(pmConn,bmoOrderPrevious.getId())) {
//								bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No puede cambiar el estatus, existe un Proyecto ligado");
//							}
//						}
//					}
					//Eliminar las CxC que no esten pagadas
					//Revisar que las CxC no tengas pagos realizados
					pmRaccount.deleteRaccount(pmConn, bmoOrder, bmUpdateResult);
				}

				//Eliminar el Envio de Pedido
				if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
					if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {
						//Validar que no existan pagos
						if (bmoOrderPrevious.getPaymentStatus().equals(BmoOrder.PAYMENTSTATUS_PENDING)) {
							PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());					
							if (hasOrderItemWithProduct(pmConn, bmoOrder) > 0) {
								pmOrderDelivery.deleteOrderDelivery(pmConn, bmoOrder, bmUpdateResult);
							}

							bmUpdateResult = super.save(pmConn, bmoOrder, bmUpdateResult);

							updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);
						} else {
							bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No puede cambiar el estatus, existen pagos realizados");
						}
					}	
					//Ver si tiene proyectos
//					if(getSFParams().hasRead(new BmoProjectStep().getProgramCode())) {
//						if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//							if(getProyects(pmConn,bmoOrderPrevious.getId())) {
//								bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No puede cambiar el estatus, existe un Proyecto ligado");
//							}
//						}
//					}
				}

				// Limpiar fecha/usuario de cancelacion-autorizacion-finalizacion
				bmoOrder.getCancelledDate().setValue("");
				bmoOrder.getCancelledUser().setValue("");			
				bmoOrder.getAuthorizedDate().setValue("");
				bmoOrder.getAuthorizedUser().setValue("");
				bmoOrder.getFinishedDate().setValue("");
				bmoOrder.getFinishedUser().setValue("");
				
				if (bmoOrder.getDiscount().toDouble() > 0 && bmoOrder.getDiscount().toDouble() != bmoOrderPrevious.getDiscount().toDouble()) {
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(),
								BmoWFlowLog.TYPE_OTHER, "Descuento aplicado: " + bmoOrder.getDiscount().toDouble());
				}
			}

			// Revisa fechas
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoOrder.getLockEnd().toString(), bmoOrder.getLockStart().toString())) {
				bmUpdateResult.addError(bmoOrder.getLockEnd().getName(), 
						"No puede ser Anterior a " + bmoOrder.getLockStart().getLabel());
			}
			if (!bmUpdateResult.hasErrors()) {
				// Si no tiene una sección de almacén asignada, crearla si es de tipo renta
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {

					if (bmoOrder.getLockStart().toString().equals("")) 
						bmUpdateResult.addError(bmoOrder.getLockStart().getName(), "No se ha capturado la Fecha de Inicio.");

					if (bmoOrder.getLockEnd().toString().equals(""))
						bmUpdateResult.addError(bmoOrder.getLockEnd().getName() , "No se ha capturado la Fecha de Fin.");

					PmWhSection pmWhSection = new PmWhSection(getSFParams());
					int warehouseId = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrderWarehouseId().toInteger();
					if (warehouseId > 0 && !pmWhSection.orderSectionExists(pmConn, bmoOrder.getId())) {
						// No existe, crear la seccion
						BmoWhSection bmoWhSection = new BmoWhSection();
						bmoWhSection.getName().setValue(bmoOrder.getName().toString());
						bmoWhSection.getWarehouseId().setValue(warehouseId);
						bmoWhSection.getOrderId().setValue(bmoOrder.getId());
						pmWhSection.save(pmConn, bmoWhSection, bmUpdateResult);
					}

					// Actualiza fechas de bloqueo
					updateItemsDatesLockStatus(pmConn, bmoOrder, bmUpdateResult);

					// Actualiza estatus de bloqueo
					if (!bmUpdateResult.hasErrors()) updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

				} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
					// TODO: SI APLICAR CONSULTORIA?	
					if (bmoOrder.getLockStart().toString().equals("")) 
						bmUpdateResult.addError(bmoOrder.getLockStart().getName(), "No se ha capturado la Fecha de Inicio.");

					if (bmoOrder.getLockEnd().toString().equals(""))
						bmUpdateResult.addError(bmoOrder.getLockEnd().getName() , "No se ha capturado la Fecha de Fin.");
				} 

				// Si se cambio el estatus a Revision
				if ((bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION)
						&& (!newRecord)
						&& (!bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_REVISION))
						&& (!bmUpdateResult.hasErrors())
						&& (bmoOrder.getWFlowId().toInteger() > 0)) {
					super.save(pmConn, bmoOrder, bmUpdateResult);
					addDataLog(pmConn, bmoOrder, "Pedido en Revisión", bmUpdateResult);

					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
						BmoSessionSale bmoSessionSale = new BmoSessionSale();
						PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
						bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(pmConn, bmoOrder.getId(), bmoSessionSale.getOrderId().getName());

						bmoSessionSale.getStatus().setValue(BmoSessionSale.STATUS_REVISION);

						super.save(pmConn, bmoSessionSale, bmUpdateResult);
					}

					// Cambiar estatus del credito a en revision
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
						// Lanzar error, no se puede cambiar de autorizada en revision porque ya se crearon las cxc, 
						// el envio y mb de salida de dinero
						if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED ) {
							if (!bmoOrder.getDeliveryStatus().equals(BmoOrder.DELIVERYSTATUS_PENDING))								
								bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El monto del crédito ya fue entregado.");

						} else if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {
							bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El crédito ya finalizó");
						} 
					}
					// Se esta cambiando a en Revision regresar estatus de entrega a Pendiente
					// de Tipo Servicio
					// TODO: SI APLICAR CONSULTORIA?
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
						bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_PENDING);
					}
				}

				// Actualiza valores
				if (!bmUpdateResult.hasErrors()) 			
					updateBalance(pmConn, bmoOrder, bmUpdateResult);

				// Proceso de autorizar pedido, revisar bloqueos
				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED
						|| bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
					String logStatus = "";
					if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
						logStatus = "Autorización del Pedido";

					if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
						logStatus = "Pedido Finalizado";

					// No generar bitacora si ya estaba autorizada o finalizada
					if (!bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) || 
							!bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_FINISHED)) {

						// Se esta autorizando, generar bitacora con nuevos cambios
						if (!bmUpdateResult.hasErrors()) {
							// Guardar fecha y usuario de autorizacion 
							if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
								bmoOrder.getAuthorizedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
								bmoOrder.getAuthorizedUser().setValue(getSFParams().getLoginInfo().getUserId());
								bmoOrder.getFinishedDate().setValue("");
								bmoOrder.getFinishedUser().setValue("");
								//Crear Proyecto
//								if(getSFParams().hasRead(new BmoProjectStep().getProgramCode())) {
//									if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//										if(createProjectStep(bmoOrder)) {						
//											action(bmoOrder, bmUpdateResult, BmoOrder.ACTION_CREATEPROJ, "");
//										}
//									}
//								}
							
								// Se esta cambiando de Finalizado a en Autorizado regresar estatus de entrega a Pendiente
								// de Tipo Consultoria
								// TODO: SI APLICAR CONSULTORIA?
								if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
									if (bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_FINISHED)) 
										bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_PENDING);

							} else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {
								if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUSFINISHED))
									bmUpdateResult.addError(bmoOrder.getStatus().getName() , "No cuenta con permisos para Finalizar el Pedido");
								else {
									//Cambiar el credito a finalizado si el pedido esta pagado
									if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {										
										if (bmoOrder.getPaymentStatus().equals(BmoOrder.PAYMENTSTATUS_TOTAL)) {
											PmRaccount pmRaccount = new PmRaccount(getSFParams());
											//Validar que la falla este pagada
											if (pmRaccount.failureIsPaid(pmConn, bmoOrder.getId(), bmUpdateResult)) {
												BmoCredit bmoCredit = new BmoCredit();
												PmCredit pmCredit = new PmCredit(getSFParams());
												bmoCredit = (BmoCredit)pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());
												bmoCredit.getStatus().setValue(BmoCredit.STATUS_FINISHED);

												pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
											} else {
												bmUpdateResult.addError(bmoOrder.getStatus().getName(), "La penalización del crédito no esta pagada");
											}
										} else {
											bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El pedido debe estar pagado");
										}
									}
									// Se esta Finalizando colocar estatus de entrega a Total
									// de Tipo Consultoria
									// TODO: SI APLICAR CONSULTORIA?
									else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
										bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_TOTAL);
								}
								// Guardar fecha y usuario de finalizacion, claramente no se limpia la autorizacion
								bmoOrder.getFinishedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
								bmoOrder.getFinishedUser().setValue(getSFParams().getLoginInfo().getUserId());

								if (bmoOrder.getWFlowId().toInteger() > 0) {
									addDataLog(pmConn, bmoOrder, logStatus, bmUpdateResult);
								}
							}
							// Limpiar fecha de cancelacion
							bmoOrder.getCancelledDate().setValue("");
							bmoOrder.getCancelledUser().setValue("");

							// Si recien se esta autorizando el pedido, reviar que no existan Ordenes de Compra pendientes de Autorizar
							if (bmoOrderPrevious.getStatus().equals(BmoOrder.STATUS_REVISION)) {
								// No aplicar esto, porque si ya le quitaste manual el check, este lo volvia a poner
								// Establece si se va a renovar el pedido, en caso que tenga productos renovables
								//								if (hasRenewableProducts(pmConn, bmoOrder)) {
								//									bmoOrder.getWillRenew().setValue(1);
								//								}
								// Existen conflictos, no se puede dar el cambio de estatus
								if (bmoOrder.getLockStatus().toChar() == BmoOrder.LOCKSTATUS_CONFLICT) 
									bmUpdateResult.addMsg("No se puede Autorizar - existen Conflictos.");

								// Avanzar fase, solo si es de tipo session
								if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
									PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());
									pmOrderSession.advanceStepPhaseAssigned(pmConn, bmoOrder, true, bmUpdateResult);
								}
							}
						}

						// Revisar saldo de cuentas x cobrar
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnsureProcessCxC().toBoolean()) {

							// El Pedido es de tipo Credito
							if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
								// Autorizar/Finalizar el credito
								// De en revison a autorizado, crea dispersion y saca dinero de bancos
								// De en revision a finalizo lanza error, primero debe salir dinero (autorizar el credito)
								// De autorizado a finalizado, solo cambia el estatus

								// Revisa si antes estaba en Revision y ahora se está autorizando
								if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_REVISION) {

									// Revisa que el pedido este autorizado
									if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
										// Revisar el estatus del crédito
										PmCredit pmCredit = new PmCredit(getSFParams());
										BmoCredit bmoCredit = new BmoCredit();
										bmoCredit = (BmoCredit)pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

										if (bmoCredit.getStatus().equals(BmoCredit.STATUS_AUTHORIZED)) {
											// Revisar si el ejecutivo tiene credito										
											this.checkCreditLimit(pmConn, bmoOrder, bmUpdateResult);

											// Revisar los avales que este presentes
											checkGuarantee(pmConn, bmoOrder, bmUpdateResult);

											// Crear las CxC
											printDevLog(this.getClass().getName() + "save(): Iniciando ensureOrderCredit ");
											PmRaccount pmRaccount = new PmRaccount(getSFParams());	
											// Validar que no existan CxC Creadas
											if (!(pmRaccount.orderHasRaccount(pmConn, bmoOrder, bmUpdateResult) > 0)) {
												pmRaccount.ensureOrderCredit(pmConn, bmoOrder, bmUpdateResult);
											}
											else {
												bmUpdateResult.addError(bmoOrder.getStatus().getName(), "<b>Existen CxC ligadas.</b>");
											}
											
											//pmRaccount.ensureOrderCredit(pmConn, bmoOrder, bmUpdateResult);

											// Crear la dispersion del pedido
											printDevLog(this.getClass().getName() + "save(): Iniciando createOrderDelivery ");
											PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
											pmOrderDelivery.createOrderDelivery(pmConn, bmoOrder, bmUpdateResult);

											printDevLog(this.getClass().getName() + "save(): Finalizo createOrderDelivery ");

											// Se asgina al pedido el estatus de entrega una vez creado el envio.
											// Esto lo hace en el metodo updateDeliveryStatus cuando se crea el envio
											// pero se lo pasamos a la variable del pedido para que al guardar lo haga con el cambio correcto y no con el valor anterior
											if (!bmUpdateResult.hasErrors()) 
												bmoOrder.getDeliveryStatus().setValue(BmoOrder.DELIVERYSTATUS_TOTAL);

										} 
										else {
											bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El Crédito no esta Autorizado.");
										}

									} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
										bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El Pedido no está Autorizado.");
									}		
								} 
								// Manda errores de "ayuda?", los estatus van en escalera; revision,autorizado,terminado
								// El Pedido estaba cancelado
								else if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_CANCELLED) {
									if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) 
										bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El Crédito no está Autorizado.");
									if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
										bmUpdateResult.addError(bmoOrder.getStatus().getName() , "El Crédito debe estar en Revisión para ser Autorizado");								
								}								
							} 
							// Si es de tipo session, y si autoriza el pedido se autoriza la venta de la sesion
							else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
								BmoSessionSale bmoSessionSale = new BmoSessionSale();
								PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
								bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(pmConn, bmoOrder.getId(), bmoSessionSale.getOrderId().getName());

								bmoSessionSale.getStatus().setValue(BmoSessionSale.STATUS_AUTHORIZED);

								super.save(pmConn, bmoSessionSale, bmUpdateResult);

								// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
								PmRaccount pmRaccount = new PmRaccount(getSFParams());
								pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
							} 
							// Generar las cuentas por cobrar
							else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
								if (bmoOrder.getStatus().equals(BmoCredit.STATUS_AUTHORIZED)) {
									// Crear las CxC para arrendamiento
									printDevLog(this.getClass().getName() + "save(): Iniciando ensureOrderCredit ");
									PmRaccount pmRaccount = new PmRaccount(getSFParams());		
									pmRaccount.ensureOrderCredit(pmConn, bmoOrder, bmUpdateResult);
									
									// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
									pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
								}
							}
							// Todo los tipos de pedido
							else {
								// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
								PmRaccount pmRaccount = new PmRaccount(getSFParams());
								pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
							}
						}	

						// Realizar el envio de pedido
						if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {
							printDevLog(this.getClass().getName() + "save(): Iniciando Envio de Pedido Automatico ");

							PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
							if (bmoOrderPrevious.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
								// Validar que existan items
								if (hasOrderItemWithProduct(pmConn, bmoOrder) > 0) {									
									pmOrderDelivery.createOrderDelivery(pmConn, bmoOrder, bmUpdateResult);
								}	

								bmUpdateResult = super.save(pmConn, bmoOrder, bmUpdateResult);

								updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);
							} 
						} 	
					}
				} 
			}
		}

		if (!bmUpdateResult.hasErrors()) {
			// Aplicar si esta activo en el tipo de pedido y no esta cancelado
			if (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0
					&& !bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) {
				// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
			}

			// Actualiza estatus del cliente			
			pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			
			// Mandar correo, SI esta creando el pedido directo y si la notificacion esta activada en el Tipo de Pedido
			// notificar a los usuarios del WFlow del pedido (ya no mandar notificacion si viene de una oportunidad)
			if (newRecord && !(bmoOrder.getOpportunityId().toInteger() > 0)
					&& !(bmoOrder.getQuoteId().toInteger() > 0)
					&& bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
				if (bmoOrder.getBmoOrderType().getEmailRemindersOrderStart().toBoolean() && !bmUpdateResult.hasErrors()) {
					sendMailReminderOrderStart(pmConn, bmoOrder);
				}					
			}
			
			// Si esta guadando el pedido de una consultoria sincronizar datos
			if (!newRecord && !bmUpdateResult.hasErrors() && bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
				BmoConsultancy bmoConsultancy = new BmoConsultancy();
				PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
				
				bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
				bmoConsultancy.getName().setValue(bmoOrder.getName().toString());
				bmoConsultancy.getUserId().setValue(bmoOrder.getUserId().toInteger());				
				bmoConsultancy.getStartDate().setValue(bmoOrder.getLockStart().toString());
				bmoConsultancy.getEndDate().setValue(bmoOrder.getLockEnd().toString());
				bmoConsultancy.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
				bmoConsultancy.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
				bmoConsultancy.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
				bmoConsultancy.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());				
				bmoConsultancy.getMarketId().setValue(bmoOrder.getMarketId().toInteger());		
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoConsultancy.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());				
					bmoConsultancy.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());	
				}
				bmoConsultancy.getStatus().setValue(bmoOrder.getStatus().toString());
				bmoConsultancy.getAmount().setValue(bmoOrder.getAmount().toDouble());
				bmoConsultancy.getTax().setValue(bmoOrder.getTax().toDouble());
				bmoConsultancy.getTotal().setValue(bmoOrder.getTotal().toDouble());
				bmoConsultancy.getPayments().setValue(bmoOrder.getPayments().toDouble());
				bmoConsultancy.getBalance().setValue(bmoOrder.getBalance().toDouble());
				bmoConsultancy.getTags().setValue(bmoOrder.getTags().toString());
				bmoConsultancy.getCustomerContactId().setValue(bmoOrder.getCustomerContactId().toString());
				bmoConsultancy.getCustomerRequisition().setValue(bmoOrder.getCustomerRequisition().toString());
				
				pmConsultancy.saveSimple(pmConn,bmoConsultancy, bmUpdateResult);			
			} else if (!newRecord && !bmUpdateResult.hasErrors() && bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) ) {
				//Validar que no es pedido extra ya que el pedido extra no tiene projectId
				if (!(bmoOrder.getRenewOrderId().toInteger() > 0) && !(bmoOrder.getOriginRenewOrderId().toInteger() > 0)) {
					BmoProject bmoProject = new BmoProject();
					PmProject pmProject = new PmProject(getSFParams());
					bmoProject = (BmoProject)pmProject.getBy(bmoOrder.getId(), bmoProject.getOrderId().getName());
					bmoProject.getName().setValue(bmoOrder.getName().toString());
					bmoProject.getUserId().setValue(bmoOrder.getUserId().toInteger());				
					bmoProject.getStartDate().setValue(bmoOrder.getLockStart().toString());
					bmoProject.getEndDate().setValue(bmoOrder.getLockEnd().toString());
					bmoProject.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
					bmoProject.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
					bmoProject.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
					bmoProject.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());				
					bmoProject.getMarketId().setValue(bmoOrder.getMarketId().toInteger());		
					bmoProject.getStatus().setValue(bmoOrder.getStatus().toString());
					bmoProject.getTags().setValue(bmoOrder.getTags().toString());
					bmoProject.getTotal().setValue(bmoOrder.getTotal().toDouble());
					pmProject.saveSimple(pmConn, bmoProject, bmUpdateResult);	
				//Es un pedido extra y se esta autorizando	
				} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
					//Mandar notificacion(Pedido extra)
					//Si esta marcado el envio en tipo de pedido
					if (bmoOrder.getBmoOrderType().getSendExtraMail().toBoolean()) {
						BmoExtraOrderProfile bmoExtraOrderProfile = new BmoExtraOrderProfile();
						PmExtraOrderProfile pmExtraOrderProfile = new PmExtraOrderProfile(getSFParams());
						BmFilter orderTypeFilter = new BmFilter();
						boolean hasProfiles = false;
						orderTypeFilter.setValueFilter(bmoExtraOrderProfile.getKind(), bmoExtraOrderProfile.getOrderTypeId(), bmoOrder.getOrderTypeId().toInteger());
						Iterator<BmObject> profileList = pmExtraOrderProfile.list(orderTypeFilter).iterator();
						//Construir consulta para usaruios a mandar notificación
						String sql = "SELECT pfus_userid FROM profileusers WHERE ";
						int i = 0;
						while (profileList.hasNext()) {
							hasProfiles = true;
							BmoExtraOrderProfile nextProfile = (BmoExtraOrderProfile)profileList.next();
							//Agrega nuevo un OR y el id de perfil
							if (i > 0)sql += " OR ";						
							sql += "pfus_profileid = " + nextProfile.getProfileId().toInteger();

							i++;
						}
						// se agrupa por usuarios para no repetir emails
						sql += " GROUP BY pfus_userid ";
						System.err.println(sql);
						if (hasProfiles) {
							//Notificar que se autorizo un Pedido Extra
							sendEmailAutorized(pmConn,bmoOrder,sql);
						}

					}
				}
			}			
			// La ultima accion debe ser save
			if (!bmUpdateResult.hasErrors())
				super.save(pmConn, bmoOrder, bmUpdateResult);
		}

		return bmUpdateResult; 
	}
	public void sendEmailAutorized(PmConn pmConn,BmoOrder bmoOrder,String sql) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		PmUser pmUser = new PmUser(getSFParams());
		String msg = "";
		String msgBody = "";
		pmConn.doFetch(sql);
		
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(getSFParams());
		
		while (pmConn.next() ) {
			BmoUser bmoUser = (BmoUser)pmUser.get( pmConn.getInt("pfus_userid"));
			
			if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
				mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
						bmoUser.getFirstname().toString() 
						+ " " + bmoUser.getFatherlastname().toString()));
			}
		}
		BmoUser userCreator = (BmoUser)pmUser.get(pmConn, bmoOrder.getUserCreateId().toInteger());
		bmoProject = (BmoProject)pmProject.getBy(pmConn, bmoOrder.getOriginRenewOrderId().toInteger(), bmoProject.getOrderId().getName());
		
		String subject = "Autorización Pedido Extra " + bmoOrder.getCode().toString();

		msg = " <p style=\"font-size:12px\"> " 
				+ " <b>Pedido:</b> " + bmoOrder.getCode().toHtml() + " " + bmoOrder.getName().toHtml() 
				+ "<br>"
				+ " <b>Proyecto: </b> " + bmoProject.getCode().toHtml() + " " + bmoProject.getName().toHtml()
				+ "<br>"
				+ " <b>Creador de Pedido Extra: </b> " + userCreator.getFirstname().toHtml() + " " + userCreator.getMotherlastname().toHtml() + " " + userCreator.getFatherlastname().toHtml()
				+ "</p>";
		msg += "	<p align=\"left\" style=\"font-size:12px\"> "
				+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
				+ " y bórralo sin retener copia alguna." + "	</p> ";

		msgBody = HtmlUtil.mailBodyFormat(getSFParams(), subject, msg);


		if (getSFParams().isProduction()) {
			try {
				SFSendMail.send(getSFParams(),
						mailList, 
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getBmoSFConfig().getAppTitle().toString(), 
						subject, 
						msgBody);
			} catch (Exception e) {
				throw new SFException(this.getClass().getName() + " - sendMailReminder() - Error al enviar email: " + e.toString());
			}
		}
	}
	// Validar que exista una venta de sesión ligada al pedido
	public boolean hasSessionSale(BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int items = 0;
		PmConn pmConn = new PmConn(getSFParams());
		try {

			pmConn.open();
			sql = " SELECT COUNT(sesa_orderid) AS items FROM sessionsales " + " WHERE sesa_orderid = "
					+ bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				items = pmConn.getInt("items");
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "hasSessionSale(): " + e.toString());
		} finally {
			pmConn.close();
		}

		printDevLog("Items " + items);
		if (items > 0)
			return true;
		else
			return false;

	}

	// Validar los Avales
	private void checkGuarantee(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		BmoCredit bmoCredit = new BmoCredit();
		PmCredit pmCredit = new PmCredit(getSFParams());
		bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getWFlowId().toInteger(),
				bmoCredit.getWFlowId().getName());

		// Obtener el tipo de credito
		if (!pmCredit.hasGuarantee(pmConn, bmoCredit, bmUpdateResult))
			bmUpdateResult.addError(bmoOrder.getStatus().getName(), "Los Avales no estan Asignados.");
	}

	public void checkCreditLimit(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int userId = getUserByGroup(pmConn, bmoOrder, bmUpdateResult);

		// Promotor
		if (userId > 0) {
			// bmoUser = (BmoUser)pmUser.get(pmConn, userId);
			// Validar que tiene saldo el ejecutivo en su cuenta de banco
			sql = " SELECT * FROM bankaccounts " + " WHERE bkac_responsibleid = " + userId;
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				// Obtener el saldo de la cuenta de banco
				double balance = pmConn.getDouble("bkac_balance");
				if (bmoOrder.getAmount().toDouble() > balance) {
					bmUpdateResult.addError(bmoOrder.getStatus().getName(),
							"El ejecutivo no tiene saldo suficiente en la cuenta de banco");
				}
			} else {
				bmUpdateResult.addError(bmoOrder.getStatus().getName(),
						"El ejecutivo no tiene una cuenta de banco asignada");
			}
		} else {
			bmUpdateResult.addError(bmoOrder.getStatus().getName(), "El crédito no tiene un ejecutivo asignado");
		}

	}

	// Obtener el ejecutivo
	public int getUserByGroup(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		int userId = 0;
		String sql = "";
		// Obtener el usuario ejecutivo desde usuarios de grupo
		sql = " SELECT wflu_userid FROM wflowusers " + " WHERE wflu_profileid = "
				+ bmoOrder.getBmoOrderType().getDispersionProfileId().toInteger() + " AND wflu_wflowid = "
				+ bmoOrder.getWFlowId().toInteger();

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			userId = pmConn.getInt("wflu_userid");
		}

		return userId;
	}

	// Crear los items de una cotización
	public void createOrderItemsFromQuote(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {

		// Obtener cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = new BmoQuote();
		bmoQuote = (BmoQuote) pmQuote.get(pmConn, bmoOrder.getQuoteId().toInteger());

		// Filtro de items de la cotización por grupo de cotización
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		BmFilter byQuoteGroupFilter = new BmFilter();
		byQuoteGroupFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(),
				bmoQuote.getId());

		// Crear los grupos del pedido
		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		Iterator<BmObject> quoteGroupIterator = pmQuoteGroup.list(byQuoteGroupFilter).iterator();
		while (quoteGroupIterator.hasNext()) {
			bmoQuoteGroup = (BmoQuoteGroup) quoteGroupIterator.next();
			BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
			bmoOrderGroup.getName().setValue(bmoQuoteGroup.getName().toString());
			bmoOrderGroup.getDescription().setValue(bmoQuoteGroup.getDescription().toString());
			bmoOrderGroup.getAmount().setValue(bmoQuoteGroup.getAmount().toDouble());
			bmoOrderGroup.getIsKit().setValue(bmoQuoteGroup.getIsKit().toBoolean());
			bmoOrderGroup.getShowQuantity().setValue(bmoQuoteGroup.getShowQuantity().toBoolean());
			bmoOrderGroup.getShowAmount().setValue(bmoQuoteGroup.getShowAmount().toBoolean());
			bmoOrderGroup.getShowPrice().setValue(bmoQuoteGroup.getShowPrice().toBoolean());
			bmoOrderGroup.getShowItems().setValue(bmoQuoteGroup.getShowItems().toBoolean());
			bmoOrderGroup.getImage().setValue(bmoQuoteGroup.getImage().toString());
			bmoOrderGroup.getShowGroupImage().setValue(bmoQuoteGroup.getShowGroupImage().toBoolean());
			bmoOrderGroup.getShowProductImage().setValue(bmoQuoteGroup.getShowProductImage().toBoolean());
			bmoOrderGroup.getPayConditionId().setValue(bmoQuoteGroup.getPayConditionId().toInteger());
			bmoOrderGroup.getIndex().setValue(bmoQuoteGroup.getIndex().toInteger());
			bmoOrderGroup.getOrderId().setValue(bmoOrder.getId());
			
			bmoOrderGroup.getDiscountApplies().setValue(bmoQuoteGroup.getDiscountApplies().toInteger());
			bmoOrderGroup.getDiscountRate().setValue(bmoQuoteGroup.getDiscountRate().toDouble());
			bmoOrderGroup.getFeeProductionApply().setValue(bmoQuoteGroup.getFeeProductionApply().toInteger());
			bmoOrderGroup.getFeeProductionRate().setValue(bmoQuoteGroup.getFeeProductionRate().toDouble());
			bmoOrderGroup.getFeeProduction().setValue(bmoQuoteGroup.getFeeProduction().toDouble());
			bmoOrderGroup.getCommissionApply().setValue(bmoQuoteGroup.getCommissionApply().toInteger());
			bmoOrderGroup.getCommissionRate().setValue(bmoQuoteGroup.getCommissionRate().toDouble());
			bmoOrderGroup.getCommissionAmount().setValue(bmoQuoteGroup.getCommissionAmount().toDouble());

			pmOrderGroup.saveSimple(pmConn, bmoOrderGroup, bmUpdateResult);

			// Obten el ultimo ID generado, que es el del grupo de pedido
			int orderGroupId = bmUpdateResult.getId();

			// Filtro de items de la cotización por Grupo de Cotizacion
			PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
			BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
			BmFilter byQuoteFilter = new BmFilter();
			byQuoteFilter.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(),
					bmoQuoteGroup.getId());

			PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
			Iterator<BmObject> quoteItemIterator = pmQuoteItem.list(byQuoteFilter).iterator();
			while (quoteItemIterator.hasNext()) {
				bmoQuoteItem = (BmoQuoteItem) quoteItemIterator.next();
				BmoOrderItem bmoOrderItem = new BmoOrderItem();
				bmoOrderItem.getProductId().setValue(bmoQuoteItem.getProductId().toInteger());
				bmoOrderItem.getName().setValue(bmoQuoteItem.getName().toString());
				bmoOrderItem.getDescription().setValue(bmoQuoteItem.getDescription().toString());
				bmoOrderItem.getQuantity().setValue(bmoQuoteItem.getQuantity().toDouble());
				bmoOrderItem.getDays().setValue(bmoQuoteItem.getDays().toDouble());
				bmoOrderItem.getBasePrice().setValue(bmoQuoteItem.getBasePrice().toDouble());
				bmoOrderItem.getPrice().setValue(bmoQuoteItem.getPrice().toDouble());
				bmoOrderItem.getAmount().setValue(bmoQuoteItem.getAmount().toDouble());
				bmoOrderItem.getOrderGroupId().setValue(orderGroupId);
				bmoOrderItem.getCommission().setValue(bmoQuoteItem.getCommission().toInteger());
				bmoOrderItem.getIndex().setValue(bmoQuoteItem.getIndex().toInteger());
				
				bmoOrderItem.getDiscountApplies().setValue(bmoQuoteItem.getDiscountApplies().toInteger());
				bmoOrderItem.getDiscount().setValue(bmoQuoteItem.getDiscount().toDouble());

				// Pasar datos de control presupuestal
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoOrderItem.getBudgetItemId().setValue(bmoQuoteItem.getBudgetItemId().toInteger());
					bmoOrderItem.getAreaId().setValue(bmoQuoteItem.getAreaId().toInteger());
				}

				pmOrderItem.createFromQuote(pmConn, bmoOrderItem, bmoOrder, bmUpdateResult);

				// Viene de un producto
				if (bmoOrderItem.getProductId().toInteger() > 0) {
					// Traer el Producto para validar de que tipo es

					BmoProduct bmoProduct = new BmoProduct();
					PmProduct pmProduct = new PmProduct(getSFParams());
					bmoProduct = (BmoProduct) pmProduct.get(bmoOrderItem.getProductId().toInteger());

					// Crear subProductos si el producto es uno de tipo Compuesto
					if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED) {
						// Primero agrega el ultimo valor
						// super.save(pmConn, bmoOrderItem, bmUpdateResult);
						// crear/actualizar subproductos
						pmOrderItem.createItemsComposed(pmConn, bmoOrderItem, bmoOrder, true, bmUpdateResult);
					}
				}
			}
		}

		// Lista de recursos
//		PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(getSFParams());
//		PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(getSFParams());
//		BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
//		BmFilter byQuoteFilter = new BmFilter();
//		byQuoteFilter.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(),
//				bmoQuote.getId());
//		Iterator<BmObject> quoteEquipmentIterator = pmQuoteEquipment.list(byQuoteFilter).iterator();
//		while (quoteEquipmentIterator.hasNext()) {
//			bmoQuoteEquipment = (BmoQuoteEquipment) quoteEquipmentIterator.next();
//			BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
//			bmoOrderEquipment.getName().setValue(bmoQuoteEquipment.getName().toString());
//			bmoOrderEquipment.getQuantity().setValue(bmoQuoteEquipment.getQuantity().toInteger());
//			bmoOrderEquipment.getDays().setValue(bmoQuoteEquipment.getDays().toDouble());
//			bmoOrderEquipment.getBasePrice().setValue(bmoQuoteEquipment.getBasePrice().toDouble());
//			bmoOrderEquipment.getPrice().setValue(bmoQuoteEquipment.getPrice().toDouble());
//			bmoOrderEquipment.getLockStart().setValue(bmoOrder.getLockStart().toString());
//			bmoOrderEquipment.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//			bmoOrderEquipment.getAmount().setValue(bmoQuoteEquipment.getAmount().toDouble());
//			bmoOrderEquipment.getEquipmentId().setValue(bmoQuoteEquipment.getEquipmentId().toInteger());
//			bmoOrderEquipment.getOrderId().setValue(bmoOrder.getId());
//
//			pmOrderEquipment.createFromQuote(pmConn, bmoOrderEquipment, bmoOrder, bmUpdateResult);
//		}

		// Lista de personal
//		PmOrderStaff pmOrderStaff = new PmOrderStaff(getSFParams());
//		PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(getSFParams());
//		BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
//		byQuoteFilter.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(), bmoQuote.getId());
//		Iterator<BmObject> quoteStaffIterator = pmQuoteStaff.list(byQuoteFilter).iterator();
//		while (quoteStaffIterator.hasNext()) {
//			bmoQuoteStaff = (BmoQuoteStaff) quoteStaffIterator.next();
//
//			if (bmoQuoteStaff.getProfileId().toInteger() > 0) {
//				// No tiene grupo asignado
//				BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
//				bmoOrderStaff.getName().setValue(bmoQuoteStaff.getName().toString());
//				bmoOrderStaff.getDescription().setValue(bmoQuoteStaff.getDescription().toString());
//				bmoOrderStaff.getQuantity().setValue(bmoQuoteStaff.getQuantity().toInteger());
//				bmoOrderStaff.getDays().setValue(bmoQuoteStaff.getDays().toDouble());
//				bmoOrderStaff.getBasePrice().setValue(bmoQuoteStaff.getBasePrice().toDouble());
//				bmoOrderStaff.getPrice().setValue(bmoQuoteStaff.getPrice().toDouble());
//				bmoOrderStaff.getLockStart().setValue(bmoOrder.getLockStart().toString());
//				bmoOrderStaff.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//				bmoOrderStaff.getAmount().setValue(bmoQuoteStaff.getAmount().toDouble());
//				bmoOrderStaff.getOrderId().setValue(bmoOrder.getId());
//				bmoOrderStaff.getProfileId().setValue(bmoQuoteStaff.getProfileId().toInteger());
//
//				pmOrderStaff.createFromQuote(pmConn, bmoOrderStaff, bmoOrder, bmUpdateResult);
//
//			} else {
//				// No tiene grupo asignado
//				BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
//				bmoOrderStaff.getName().setValue(bmoQuoteStaff.getName().toString());
//				bmoOrderStaff.getDescription().setValue(bmoQuoteStaff.getDescription().toString());
//				bmoOrderStaff.getQuantity().setValue(bmoQuoteStaff.getQuantity().toInteger());
//				bmoOrderStaff.getDays().setValue(bmoQuoteStaff.getDays().toDouble());
//				bmoOrderStaff.getPrice().setValue(bmoQuoteStaff.getPrice().toDouble());
//				bmoOrderStaff.getLockStart().setValue(bmoOrder.getLockStart().toString());
//				bmoOrderStaff.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//				bmoOrderStaff.getAmount().setValue(bmoQuoteStaff.getAmount().toDouble());
//				bmoOrderStaff.getOrderId().setValue(bmoOrder.getId());
//
//				pmOrderStaff.createFromQuote(pmConn, bmoOrderStaff, bmoOrder, bmUpdateResult);
//			}
//		}

		// Traer descuento
		bmoOrder.getDiscount().setValue(bmoQuote.getDiscount().toDouble());

		// Aplica IVA
		if (bmoQuote.getTax().toDouble() > 0)
			bmoOrder.getTaxApplies().setValue(true);
		else
			bmoOrder.getTaxApplies().setValue(false);

		// Otros valores de mostrar valores
		bmoOrder.getShowEquipmentQuantity().setValue(bmoQuote.getShowEquipmentQuantity().getValue());
		bmoOrder.getShowEquipmentPrice().setValue(bmoQuote.getShowEquipmentPrice().getValue());
		bmoOrder.getShowStaffQuantity().setValue(bmoQuote.getShowStaffQuantity().getValue());
		bmoOrder.getShowStaffPrice().setValue(bmoQuote.getShowStaffPrice().getValue());

		// Actualizar valor del pedido
		this.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		// Actualizar status de apartado del pedido
		this.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

		// Se guarda en bitácora primer creación, mostrando la cotizacion autorizada
		pmQuote.addDataLog(pmConn, bmoQuote, bmoOrder.getWFlowId().toInteger(), bmUpdateResult);
	}

	// Crear las cuentas x cobrar del pedido
	public void createOrderRaccountsAuto(PmConn pmConn, BmoOrder bmoOrder, int opportunityId,
			BmUpdateResult bmUpdateResult) throws SFException {
		PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
		BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
		bmoOpportunityDetail = (BmoOpportunityDetail) pmOpportunityDetail.getBy(pmConn, opportunityId,
				bmoOpportunityDetail.getOpportunityId().getName());

		// Obtener el tipo de cuenta por cobrar default
		int raccountTypeId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRaccountTypeId().toInteger();

		// Establecer el monto total a crear
		double amount = bmoOrder.getTotal().toDouble();
		PmRaccount pmRaccount = new PmRaccount(getSFParams());

		// Crear el anticipo
		if (bmoOpportunityDetail.getDownPayment().toDouble() > 0) {
			BmoRaccount bmoRaccount = new BmoRaccount();

			// double downPayment = bmoQuote.getDownPayment().toDouble();
			double downPayment = bmoOpportunityDetail.getDownPayment().toDouble();

			bmoRaccount.getCode().setValue("");
			bmoRaccount.getInvoiceno().setValue("Anticipo");
			bmoRaccount.getReceiveDate()
					.setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoRaccount.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoRaccount.getDescription().setValue("Anticipo");
			bmoRaccount.getReference().setValue("Anticipo");
			bmoRaccount.getAmount().setValue(downPayment);
			bmoRaccount.getBalance().setValue(downPayment);
			bmoRaccount.getRaccountTypeId().setValue(raccountTypeId);
			bmoRaccount.getOrderId().setValue(bmoOrder.getId());
			bmoRaccount.getAutoCreate().setValue(0);
			bmoRaccount.getLinked().setValue(0);
			bmoRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toDouble());
			if (bmoOrder.getCompanyId().toInteger() > 0)
				bmoRaccount.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger());
			else
				bmoRaccount.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger());

			bmoRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoRaccount.getStatus().setValue(BmoRaccount.STATUS_REVISION);

			// Obtener la paridad y la moneda
			bmoRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
			bmoRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());

			// Colocar partida y departamento del pedido
			if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
					bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(),
							"Seleccione una Partida presp. para el Pedido.");
				else {
					bmoRaccount.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
					bmoRaccount.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
				}
			}

			// Maneja Iva, Desglosar el anticipo
			if (bmoOrder.getTaxApplies().toBoolean()) {
				double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
				downPayment = downPayment / (taxRate + 1);
				bmoRaccount.getTaxApplies().setValue(1);
			}

			// Se guarda simple para que no lance mas procesos antes de tener los items
			// creados
			pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
			bmoRaccount.setId(bmUpdateResult.getId());
			PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
			String code = "";
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoRaccount.getCompanyId().toInteger(),
					bmoRaccount.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoRaccount.CODE_PREFIX
					);
			bmoRaccount.getCode().setValue(code);
//			bmoRaccount.getCode().setValue(bmoRaccount.getCodeFormat());
			pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);

			// Crear el item con el monto del anticipo
			PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
			BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
			bmoRaccItemNew.getName().setValue("Anticipo");
			bmoRaccItemNew.getQuantity().setValue("1");
			bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(downPayment));
			bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(downPayment));
			bmoRaccItemNew.getRaccountId().setValue(bmoRaccount.getId());
			if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				bmoRaccItemNew.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
				bmoRaccItemNew.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
			}

			pmRaccItemNew.save(pmConn, bmoRaccItemNew, bmUpdateResult);

			pmRaccount.save(pmConn, bmoRaccount, bmUpdateResult);
			
			if (!bmUpdateResult.hasErrors()) {
				// Actualizar id de claves del programa por empresa
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoRaccount.getCompanyId().toInteger(), 
						bmoRaccount.getProgramCode().toString());
			}

			amount = amount - downPayment;
		}

		// Revisar saldo
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnsureProcessCxC().toBoolean()) {
			pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
		}
	}

	// Crea el pedido a partir de una oportunidad
	public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity,
			BmUpdateResult bmUpdateResult) throws SFException {
		BmoOrder bmoOrder = new BmoOrder();
		bmoOrder.getName().setValue(bmoOpportunity.getName().toString());
		bmoOrder.getLockStart().setValue(bmoOpportunity.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoOpportunity.getEndDate().toString());

		bmoOrder.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoOrder.getUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoOrder.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoOrder.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoOrder.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
		bmoOrder.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoOrder.getCustomerContactId().setValue(bmoOpportunity.getCustomerContactId().toString());
		// Si esta habilitado el Control presp. pasar por defecto la partida y el dpto.
		// del tipo de pedido
		if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
			if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0))
				bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(),
						"Seleccione una Partida presp. en la Oportunidad.");
			else {
				bmoOrder.getDefaultBudgetItemId().setValue(bmoOpportunity.getBudgetItemId().toInteger());
				bmoOrder.getDefaultAreaId().setValue(bmoOpportunity.getAreaId().toInteger());
			}
		}

		// Obtener cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = new BmoQuote();
		bmoQuote = (BmoQuote) pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

		bmoOrder.getQuoteId().setValue(bmoQuote.getId());
		bmoOrder.getTax().setValue(bmoQuote.getTax().toDouble());
		bmoOrder.getTaxApplies().setValue(bmoQuote.getTaxApplies().toString());
		bmoOrder.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
		bmoOrder.getCurrencyParity().setValue(bmoQuote.getCurrencyParity().toDouble());
		bmoOrder.getCompanyId().setValue(bmoQuote.getCompanyId().toInteger());
		bmoOrder.getMarketId().setValue(bmoQuote.getMarketId().toInteger());
		bmoOrder.getCustomerRequisition().setValue(bmoQuote.getCustomerRequisition().toString());

		if (bmoQuote.getDescription().toString().length() > 0)
			bmoOrder.getDescription().setValue(bmoQuote.getDescription().toString());
		else
			bmoOrder.getDescription().setValue(bmoOpportunity.getDescription().toString());

		bmoOrder.getComments().setValue(bmoQuote.getComments().toString());
		bmoOrder.getCoverageParity().setValue(bmoQuote.getCoverageParity().toBoolean());
		bmoOrder.getPayConditionId().setValue(bmoQuote.getPayConditionId().toInteger());

		// Almacena pedido preliminar
		this.save(pmConn, bmoOrder, bmUpdateResult);

		if (!bmUpdateResult.hasErrors()) {
			// Elimina los grupos creados
			pmConn.doUpdate("DELETE FROM orderitems " + " WHERE ordi_ordergroupid IN ("
					+ " SELECT ordg_ordergroupid FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId() + ")");
			pmConn.doUpdate("DELETE FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId());
		}
		// Obtener los items de las cotizaciones
		if (!bmUpdateResult.hasErrors())
			createOrderItemsFromQuote(pmConn, bmoOrder, bmUpdateResult);

		// Crear las CxC necesarias para sustentar el saldo automaticamente
		if (!bmUpdateResult.hasErrors())
			createOrderRaccountsAuto(pmConn, bmoOrder, bmoOpportunity.getId(), bmUpdateResult);

		// Obtener los usuarios de WFlow de la oportunidad
		if (!bmUpdateResult.hasErrors())
			createWFlowUsersFromOpportunity(pmConn, bmoOrder, bmUpdateResult);

		// Si la notificacion esta activada en el Tipo de Pedido
		// notificar a los usuarios del WFlow del pedido
		if (!bmUpdateResult.hasErrors()) {
			if (bmoOrder.getBmoOrderType().getEmailRemindersOrderStart().toBoolean())
				sendMailReminderOrderStart(pmConn, bmoOrder);
		}

		// Obtener los documentos de WFlow de la oportunidad
		if (!bmUpdateResult.hasErrors())
			createWFlowDocumentsFromOpportunity(pmConn, bmoOrder, bmUpdateResult);

		new PmOpportunity(getSFParams()).updateRequisitions(pmConn, bmoOpportunity.getId(), bmoOrder, bmUpdateResult);
		
		return bmUpdateResult;
	}

	// Crea los usuarios a partir de la oportunidad
	public void createWFlowUsersFromOpportunity(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		// Obtener los usuarios de WFlow de la oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = (BmoOpportunity) pmOpportunity.get(pmConn,
				bmoOrder.getOpportunityId().toInteger());

		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(),
				bmoOpportunity.getWFlowId().toInteger());
		Iterator<BmObject> userList = pmWFlowUser.list(bmFilter).iterator();

		// Crear los usuarios en el nuevo pedido
		while (userList.hasNext()) {
			bmoWFlowUser = (BmoWFlowUser) userList.next();
			BmoWFlowUser orderWFlowUser = new BmoWFlowUser();

			// Revisar si ya existe el grupo como usuarios agregados
			if (groupExists(pmConn, bmoOrder.getWFlowId().toInteger(), bmoWFlowUser.getProfileId().toInteger())) {
				// Revisar si NO esta asignado el usuario, para asignarlo
				orderWFlowUser = pmWFlowUser.getUnassignedByGroup(pmConn, bmoOrder.getWFlowId().toInteger(),
						bmoWFlowUser.getProfileId().toInteger());
				if (orderWFlowUser.getId() > 0) {
					orderWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());

					// Si no es igual al vendedor del proyecto, asignarlo, para no duplicar
					// asignaciones
					if (orderWFlowUser.getUserId().toInteger() != bmoOrder.getUserId().toInteger())
						pmWFlowUser.save(pmConn, orderWFlowUser, bmUpdateResult);
				}
			} else {
				// Es nuevo, asignar valores
				orderWFlowUser.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
				orderWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
				orderWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);
				orderWFlowUser.getLockStart().setValue(bmoOrder.getLockStart().toString());
				orderWFlowUser.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
				orderWFlowUser.getRequired().setValue(bmoWFlowUser.getRequired().toString());
				orderWFlowUser.getAutoDate().setValue(bmoWFlowUser.getAutoDate().toString());
				orderWFlowUser.getProfileId().setValue(bmoWFlowUser.getProfileId().toInteger());

				// Si no es igual al vendedor del proyecto, asignarlo, para no duplicar
				// asignaciones
				if (orderWFlowUser.getUserId().toInteger() != bmoOrder.getUserId().toInteger())
					pmWFlowUser.save(pmConn, orderWFlowUser, bmUpdateResult);
			}
		}
	}

	public boolean groupExists(PmConn pmConn, int wFlowId, int profileId) throws SFException {
		pmConn.doFetch("SELECT wflu_wflowuserid FROM wflowusers " + " WHERE wflu_wflowid = " + wFlowId
				+ " AND wflu_profileid = " + profileId);
		return pmConn.next();
	}

	// Crea los documentos de una oportunidad; si coincide clave, no lo crea
	public void createWFlowDocumentsFromOpportunity(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		// Obtener los documentos de WFlow de la oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = (BmoOpportunity) pmOpportunity.get(pmConn,
				bmoOrder.getOpportunityId().toInteger());

		PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(getSFParams());
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(),
				bmoOpportunity.getWFlowId().toInteger());
		Iterator<BmObject> documentList = pmWFlowDocument.list(pmConn, bmFilter).iterator();

		// Crear los documentos en el nuevo proyecto
		while (documentList.hasNext()) {
			// Obtiene documento de la oportunidad
			bmoWFlowDocument = (BmoWFlowDocument) documentList.next();

			// Crea nuevo en la venta de inmueble
			BmoWFlowDocument propertySaleWFlowDocument = new BmoWFlowDocument();
			propertySaleWFlowDocument.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
			propertySaleWFlowDocument.getCode().setValue(bmoWFlowDocument.getCode().toString());
			propertySaleWFlowDocument.getName().setValue(bmoWFlowDocument.getName().toString());
			propertySaleWFlowDocument.getFile().setValue(bmoWFlowDocument.getFile().toString());
			propertySaleWFlowDocument.getIsUp().setValue(bmoWFlowDocument.getIsUp().toString());
			propertySaleWFlowDocument.getRequired().setValue(bmoWFlowDocument.getRequired().toString());
			propertySaleWFlowDocument.getFileTypeId().setValue(bmoWFlowDocument.getFileTypeId().toString());
			propertySaleWFlowDocument.getFileLink().setValue(bmoWFlowDocument.getFileLink().toString());

			// Busca el ID creado en el flujo nuevo, segun clave de documento, en caso
			// afirmativo asigna el ID para que se sobreescriba
			try {
				BmoWFlowDocument existingPropertySaleWFlowDocument = pmWFlowDocument.getByCode(pmConn,
						bmoOrder.getWFlowId().toInteger(), bmoWFlowDocument.getCode().toString());
				if (existingPropertySaleWFlowDocument.getId() > 0)
					propertySaleWFlowDocument.setId(existingPropertySaleWFlowDocument.getId());
			} catch (SFException e) {
				System.out.println(this.getClass().getName()
						+ " - createWFlowDocumentsFromOpportunity(): Error anticipado: " + e.toString());
			}

			pmWFlowDocument.save(pmConn, propertySaleWFlowDocument, bmUpdateResult);
		}
	}

	// Genera nuevos contratos dependiendo si hay renovaciones de pedidos pendientes
	public void batchContractRenew() throws SFException {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(getSFParams());

		try {
			// Revisa una fecha X dias menos un mes
			String checkDate = SFServerUtil.addDays(getSFParams().getDateFormat(),
					SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()),
					((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRenewDays().toInteger());

			String sql = " SELECT orde_orderid FROM orders "
					+ " LEFT JOIN propertiesrent ON (orde_orderid = prrt_orderid) " + " WHERE orde_status <> '"
					+ BmoOrder.STATUS_CANCELLED + "' " + " AND orde_status <> '" + BmoOrder.STATUS_REVISION + "' "
					+ " AND orde_willrenew = 1 " + " AND prrt_rentincrease <= '" + checkDate + "' "
					+ " AND orde_orderid NOT IN "
					+ "		(SELECT orde_reneworderid FROM orders WHERE orde_reneworderid IS NOT NULL)"
					+ " AND prrt_contractterm > 1 "; // Es mayor a 1 porque no debe renovar si el contrato dura solo un
														// año

			if (!getSFParams().isProduction())
				System.out.println("Consulta de Pedidos a Renovar: " + sql);

			pmConn2.doFetch(sql);

			// Revisa cada pedido que no tenga pedido ligado
			while (pmConn2.next()) {
				bmoOrder = (BmoOrder) this.get(pmConn, pmConn2.getInt("orde_orderid"));
				if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
					BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
					PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
					bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(bmoOrder.getId(),
							bmoPropertyRental.getOrderId().getName());

					bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());

					// valida si el inmueble esta abierto y disponible
					if (bmoProperty.getCansell().toInteger() > 0 && bmoProperty.getAvailable().toInteger() > 0) {
						if (bmoProperty.getRenewOrder().toInteger() != 1)
							bmUpdateResult.addError(bmoPropertyRental.getPropertyId().getName(),
									"  El inmueble no está configurado para renovar.");
						else
						// Revisar que no sobrepase los pedidos renovados al plazo anual
						// Si el pedido viene de un pedido anterior
						if (bmoOrder.getOriginRenewOrderId().toInteger() > 0
								&& bmoOrder.getRenewOrderId().toInteger() > 0) {
							if (countRenewOrder(bmoOrder.getOriginRenewOrderId().toInteger()) >= bmoPropertyRental
									.getContractTerm().toInteger()) {
								Date endDate2 = SFServerUtil.stringToDate(getSFParams().getDateFormat(),
										bmoPropertyRental.getEndDate().toString());
								if (SFServerUtil.stringToDate(getSFParams().getDateFormat(), bmoPropertyRental.getRentIncrease().toString())
										.compareTo(endDate2) == 0) {
									bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_FINISHED);
									pmPropertyRental.save(bmoPropertyRental, new BmUpdateResult());
									bmUpdateResult.addError(bmoPropertyRental.getEndDate().getName(),
											"No es posible renovar, ya fue renovado al limite de la fecha fin del contrato");

								}
							} 
								printDevLog("Por Renovar Pedido.: " + bmoOrder.getCode().toString());
							// Genera pedidos de renovacion
							this.createRenewOrderPropertyRental(pmConn, bmoOrder, bmUpdateResult);

						} 
							printDevLog("Por Renovar Pedido: " + bmoOrder.getCode().toString());
						
						// Genera pedidos de renovacion
						this.createRenewOrderPropertyRental(pmConn, bmoOrder, bmUpdateResult);

					} else
						bmUpdateResult.addError(bmoPropertyRental.getPropertyId().getName(),
								"El inmueble debe estar abierto y disponible");
				}
			}

			BmFilter filterByAuthorized = new BmFilter();
			filterByAuthorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getStatus(),
					"" + BmoOrder.STATUS_AUTHORIZED);
			Iterator<BmObject> authorizedOrderIterator = this.list(filterByAuthorized).iterator();

			while (authorizedOrderIterator.hasNext()) {
				bmoOrder = (BmoOrder) authorizedOrderIterator.next();

				// Actualiza estatus de pedidos
				this.updateStatus(pmConn, bmoOrder, bmUpdateResult);
			}

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "batchOrderRenew(): " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
		}
	}

	// Genera nuevos pedidos dependiendo si hay renovaciones de pedidos pendientes
	public void batchOrderRenew() throws SFException {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();
		PmConn pmConn3 = new PmConn(getSFParams());

		try {
			// Revisa una fecha X dias despues de la fecha actual
			String checkDate = SFServerUtil.addDays(getSFParams().getDateFormat(),
					SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()),
					((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRenewDays().toInteger());

			String sql = "SELECT user_userid FROM users "
					+ " WHERE user_userid IN ( SELECT orde_userid FROM orders "
					+ " WHERE orde_status <> '" + BmoOrder.STATUS_CANCELLED + "'  AND orde_status <> '" + BmoOrder.STATUS_REVISION + "'  AND orde_willrenew = 1 "
					+ " AND orde_lockend <= '" + checkDate + "'  " + 
					" AND orde_orderid NOT IN (SELECT orde_reneworderid FROM orders WHERE orde_reneworderid IS NOT NULL))";

			pmConn2.doFetch(sql);

			System.err.println("Consulta de Pedidos a Renovar: " + sql);
			 while (pmConn2.next()) {
				 sql = "SELECT orde_orderid FROM orders  " + 
				 		"WHERE orde_status <> '\" + BmoOrder.STATUS_CANCELLED + \"'  AND orde_status <> '" + BmoOrder.STATUS_CANCELLED + "'  AND orde_willrenew = 1 " + 
				 		"AND orde_lockend <= '" + checkDate + "'  " + 
				 		"AND orde_orderid NOT IN (SELECT orde_reneworderid FROM orders" + 
				 		"WHERE orde_reneworderid IS NOT NULL) AND orde_userid = " + pmConn2.getInt("user_userid");
			 }
			// Revisa cada pedido que no tenga pedido ligado
//			while (pmConn2.next()) {
//				PmConn pmConn = new PmConn(getSFParams());
//				pmConn.open();
//				pmConn.disableAutoCommit();
//				
//				bmoOrder = (BmoOrder) this.get(pmConn, pmConn2.getInt("orde_orderid"));
//
//				System.err.println("Por Renovar Pedido: " + bmoOrder.getCode().toString());
//
//				// Genera pedidos de renovacion
//				this.createRenewOrderProducts(pmConn, bmoOrder, bmUpdateResult);
//				
//				if (!bmUpdateResult.hasErrors()) {
//					// Añadir cambios
//					pmConn.commit();
//				} else {
//					// Regresar cambios
//					pmConn.rollback();
//					throw new SFException(this.getClass().getName() + " - " + bmUpdateResult.errorsToString() + " : " + bmoOrder.getCode().toString());
//				}
//				
//				pmConn.close();
//			}

			BmFilter filterByAuthorized = new BmFilter();
			filterByAuthorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getStatus(),
					"" + BmoOrder.STATUS_AUTHORIZED);
			Iterator<BmObject> authorizedOrderIterator = this.list(filterByAuthorized).iterator();

			pmConn3.open();
			while (authorizedOrderIterator.hasNext()) {
				bmoOrder = (BmoOrder) authorizedOrderIterator.next();

				// Actualiza estatus de pedidos
				this.updateStatus(pmConn3, bmoOrder, bmUpdateResult);
			}

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + " - batchOrderRenew(): " + e.toString());
		} finally {
			pmConn3.close();
			pmConn2.close();
		}
	}

	// Revisa si tiene productos con renovacion
	// private boolean hasRenewableProducts(PmConn pmConn, BmoOrder bmoOrder) throws
	// SFException {
	// String sql = " SELECT * FROM " + formatKind("orderitems")
	// + " LEFT JOIN " + formatKind("products") + " ON (ordi_productid =
	// prod_productid) "
	// + " LEFT JOIN " + formatKind("ordergroups") + " ON (ordi_ordergroupid =
	// ordg_ordergroupid) "
	// + " LEFT JOIN " + formatKind("orders") + " ON (ordg_orderid = orde_orderid) "
	// + " where prod_reneworder = 1 "
	// + " and orde_orderid = " + bmoOrder.getId();
	//
	// pmConn.doFetch(sql);
	// return pmConn.next();
	// }

	// Envia recordatorio de pedido renovado
	private void sendReminder(BmoOrder bmoOrder) throws SFException {
		// Obtiene el usuario a enviar correo
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();

		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		PmProperty pmProperty = new PmProperty(getSFParams());
		BmoProperty bmoProperty = new BmoProperty();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomerArrendador = new BmoCustomer();
		BmoCustomer bmoCustomerArrendatario = new BmoCustomer();
		BmoCustomer bmoCustomer = new BmoCustomer();

		bmoUser = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());
		String orderType = "";
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			orderType = "Contrato";
			// INADICO: Obtener Arrendador y contrato
			bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(),
					bmoPropertyRental.getOrderId().getName());
			bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());
			bmoCustomerArrendador = (BmoCustomer) pmCustomer
					.get(bmoPropertyRental.getBmoProperty().getCustomerId().toInteger());
			bmoCustomerArrendatario = (BmoCustomer) pmCustomer.get(bmoPropertyRental.getCustomerId().toInteger());
			String msgBody = "", msg = "";
			
			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
			mailList.add(new SFMailAddress(bmoUser.getEmail().toString(),
					bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));
			
			String subject = getSFParams().getAppCode() + " Renovación de " + orderType + " - Renovado: "
					+ bmoOrder.getCode().toString() + "-" + bmoOrder.getName();
			
			msg = " <p style=\"font-size:12px\"> " + " <b>Contrato:</b> " + bmoPropertyRental.getCode().toString()
					+ " " + bmoPropertyRental.getName().toString() + " <br> " + " <b>Arrendador:</b> "
					+ bmoCustomerArrendador.getDisplayName().toString() + " <br> " + " <b>Arrendatarío:</b> "
					+ bmoCustomerArrendatario.getDisplayName().toString() + " <br> " + " <b>Inmueble:</b> "
					+ bmoProperty.getCode() + " " + bmoProperty.getDescription().toString() + " <br> "
					+ " <b>Renta Vigente:</b> " + bmoPropertyRental.getCurrentIncome().toDouble() + " <br> "
					+ "	</p> ";
			msg += "	<p align=\"left\" style=\"font-size:12px\"> "
					+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
					+ " y bórralo sin retener copia alguna." + "	</p> ";

			msgBody = HtmlUtil.mailBodyFormat(getSFParams(),
					"Renovación ", msg);
			
			if(getSFParams().isProduction()) {
				SFSendMail.send(getSFParams(), mailList, getSFParams().getBmoSFConfig().getEmail().toString(),
						getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
			}
		
		} else {
			orderType = "Pedido";
			bmoCustomer = bmoOrder.getBmoCustomer();
			// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar
			// notificacion
			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
			// Si esta activo el usuario prepara info para enviar por correo
			if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
				mailList.add(new SFMailAddress(bmoUser.getEmail().toString(),
						bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));
	
				String subject = getSFParams().getAppCode() + " Renovación de " + orderType + " - Renovado: "
						+ bmoOrder.getCode().toString() + "-" + bmoOrder.getName();
	
				String msgBody = "", msg = "";
				// Campos Inadico				
				
					msg = " <p style=\"font-size:12px\"> " + " <b>Pedido:</b> " + bmoOrder.getCode().toString() + " "
							+ bmoOrder.getName().toString() + " <br> " + " <b>Cliente:</b> "
							+ bmoCustomer.getDisplayName().toString() + " <br> " + " <b>Monto:</b> "
							+ bmoOrder.getTotal().toDouble() + " <br> " + " <b>Moneda:</b> "
							+ bmoOrder.getBmoCurrency().getCode() + " <br> " + "	</p> ";				
	
				msg += "	<p align=\"left\" style=\"font-size:12px\"> "
						+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
						+ " y bórralo sin retener copia alguna." + "	</p> ";
				
					msgBody = HtmlUtil.mailBodyFormat(getSFParams(),
							"Renovación de " + getSFParams().getProgramTitle(bmoOrder.getProgramCode()), msg);
			// Quitar emails repetidos
			ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
			for (SFMailAddress event : mailList) {
			    boolean isFound = false;
			    // Revisar si el email existe en noRepeat
			    for (SFMailAddress e : mailListNoRepeat) {
			        if (e.getName().equals(event.getName()) || (e.equals(event))) {
			            isFound = true;        
			            break;
			        }
			    }
			    // Si no encontro ninguno añadirlo a la nueva lista
			    if (!isFound) mailListNoRepeat.add(event);
			}	
				if(getSFParams().isProduction()) {
					SFSendMail.send(getSFParams(), mailListNoRepeat, getSFParams().getBmoSFConfig().getEmail().toString(),
							getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
				}
			}
		}
	}
	//Copiar los items de otra cotización
	public void copyOrderItemsFromOrder(PmConn pmConn, int fromOrderId, BmoOrder toBmoOrder, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener pedido base
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder fromBmoOrder = new BmoOrder();
		fromBmoOrder = (BmoOrder)pmOrder.get(pmConn, fromOrderId);

		// Filtro de items del pedido
		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		BmoOrderGroup fromBmoOrderGroup = new BmoOrderGroup();
		BmFilter byOrderGroupFilter = new BmFilter();
		byOrderGroupFilter.setValueFilter(fromBmoOrderGroup.getKind(), fromBmoOrderGroup.getOrderId().getName(), fromOrderId);

		// Crear los grupos del pedido
		Iterator<BmObject> orderGroupIterator = pmOrderGroup.list(pmConn, byOrderGroupFilter).iterator();
		while (orderGroupIterator.hasNext()) {
			fromBmoOrderGroup = (BmoOrderGroup)orderGroupIterator.next();

			BmoOrderGroup toBmoOrderGroup = new BmoOrderGroup();
			toBmoOrderGroup.getName().setValue(fromBmoOrderGroup.getName().toString());
			toBmoOrderGroup.getDescription().setValue(fromBmoOrderGroup.getDescription().toString());
			toBmoOrderGroup.getAmount().setValue(fromBmoOrderGroup.getAmount().toDouble());
			toBmoOrderGroup.getIsKit().setValue(fromBmoOrderGroup.getIsKit().toBoolean());
			toBmoOrderGroup.getOrderId().setValue(toBmoOrder.getId());

			pmOrderGroup.saveSimple(pmConn, toBmoOrderGroup, bmUpdateResult);
			// Obten el ultimo ID generado, que es el del grupo de pedido
			int toOrderGroupId = bmUpdateResult.getId();

			// Filtro de items de la cotización por Grupo de Cotizacion
			PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
			BmoOrderItem fromBmoOrderItem = new BmoOrderItem();
			BmFilter byOrderFilter = new BmFilter();
			byOrderFilter.setValueFilter(fromBmoOrderItem.getKind(), fromBmoOrderItem.getOrderGroupId().getName(), fromBmoOrderGroup.getId());

			Iterator<BmObject> orderItemIterator = pmOrderItem.list(pmConn, byOrderFilter).iterator();
			while (orderItemIterator.hasNext()) {
				fromBmoOrderItem = (BmoOrderItem)orderItemIterator.next();

				BmoOrderItem toBmoOrderItem = new BmoOrderItem();
				toBmoOrderItem.getProductId().setValue(fromBmoOrderItem.getProductId().toInteger());
				toBmoOrderItem.getName().setValue(fromBmoOrderItem.getName().toString());
				toBmoOrderItem.getDescription().setValue(fromBmoOrderItem.getDescription().toString());
				toBmoOrderItem.getQuantity().setValue(fromBmoOrderItem.getQuantity().toDouble());
				toBmoOrderItem.getDays().setValue(fromBmoOrderItem.getDays().toDouble());
				toBmoOrderItem.getBasePrice().setValue(fromBmoOrderItem.getBasePrice().toDouble());
				toBmoOrderItem.getPrice().setValue(fromBmoOrderItem.getPrice().toDouble());
				toBmoOrderItem.getAmount().setValue(fromBmoOrderItem.getAmount().toDouble());
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					toBmoOrderItem.getBudgetItemId().setValue(fromBmoOrderItem.getBudgetItemId().toInteger());
					toBmoOrderItem.getAreaId().setValue(fromBmoOrderItem.getAreaId().toInteger());
				}
				toBmoOrderItem.getOrderGroupId().setValue(toOrderGroupId);

				pmOrderItem.saveSimple(pmConn, toBmoOrderItem, bmUpdateResult);
			}
		}

		// Lista de recursos
//		PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(getSFParams());
//		BmoOrderEquipment fromBmoOrderEquipment = new BmoOrderEquipment();
//		BmFilter byOrderFilter = new BmFilter();
//		byOrderFilter.setValueFilter(fromBmoOrderEquipment.getKind(), fromBmoOrderEquipment.getOrderId().getName(), fromOrderId);
//		Iterator<BmObject> orderEquipmentIterator = pmOrderEquipment.list(pmConn, byOrderFilter).iterator();
//		while (orderEquipmentIterator.hasNext()) {
//			fromBmoOrderEquipment = (BmoOrderEquipment)orderEquipmentIterator.next();
//
//			BmoOrderEquipment toBmoOrderEquipment = new BmoOrderEquipment();
//			toBmoOrderEquipment.getName().setValue(fromBmoOrderEquipment.getName().toString());
//			toBmoOrderEquipment.getQuantity().setValue(fromBmoOrderEquipment.getQuantity().toInteger());
//			toBmoOrderEquipment.getDays().setValue(fromBmoOrderEquipment.getDays().toDouble());
//			toBmoOrderEquipment.getBasePrice().setValue(fromBmoOrderEquipment.getBasePrice().toDouble());
//			toBmoOrderEquipment.getPrice().setValue(fromBmoOrderEquipment.getPrice().toDouble());
//			toBmoOrderEquipment.getAmount().setValue(fromBmoOrderEquipment.getAmount().toDouble());
//			toBmoOrderEquipment.getEquipmentId().setValue(fromBmoOrderEquipment.getEquipmentId().toInteger());
//			toBmoOrderEquipment.getOrderId().setValue(toBmoOrder.getId());
//
//			pmOrderEquipment.saveSimple(pmConn, toBmoOrderEquipment, bmUpdateResult);
//		}

		// Lista de personal
//		PmOrderStaff pmOrderStaff = new PmOrderStaff(getSFParams());
//		BmoOrderStaff fromBmoOrderStaff = new BmoOrderStaff();
//		byOrderFilter.setValueFilter(fromBmoOrderStaff.getKind(), fromBmoOrderStaff.getOrderId().getName(), fromOrderId);
//		Iterator<BmObject> OrderStaffIterator = pmOrderStaff.list(pmConn, byOrderFilter).iterator();
//		while (OrderStaffIterator.hasNext()) {
//			fromBmoOrderStaff = (BmoOrderStaff)OrderStaffIterator.next();
//
//			BmoOrderStaff toBmoOrderStaff = new BmoOrderStaff();
//			toBmoOrderStaff.getName().setValue(fromBmoOrderStaff.getName().toString());
//			toBmoOrderStaff.getDescription().setValue(fromBmoOrderStaff.getDescription().toString());
//			toBmoOrderStaff.getQuantity().setValue(fromBmoOrderStaff.getQuantity().toInteger());
//			toBmoOrderStaff.getDays().setValue(fromBmoOrderStaff.getDays().toDouble());
//			toBmoOrderStaff.getBasePrice().setValue(fromBmoOrderStaff.getBasePrice().toDouble());
//			toBmoOrderStaff.getPrice().setValue(fromBmoOrderStaff.getPrice().toDouble());
//			toBmoOrderStaff.getAmount().setValue(fromBmoOrderStaff.getAmount().toDouble());
//			toBmoOrderStaff.getOrderId().setValue(toBmoOrder.getId());
//			toBmoOrderStaff.getProfileId().setValue(fromBmoOrderStaff.getProfileId().toInteger());
//
//			pmOrderStaff.saveSimple(pmConn, toBmoOrderStaff, bmUpdateResult);
//		}

		// Aplica IVA
		if (fromBmoOrder.getTax().toDouble() > 0) 
			toBmoOrder.getTaxApplies().setValue(true);
		else 
			toBmoOrder.getTaxApplies().setValue(false);

		// Otros valores de mostrar valores
		toBmoOrder.getShowEquipmentQuantity().setValue(fromBmoOrder.getShowEquipmentQuantity().getValue());
		toBmoOrder.getShowEquipmentPrice().setValue(fromBmoOrder.getShowEquipmentPrice().getValue());
		toBmoOrder.getShowStaffQuantity().setValue(fromBmoOrder.getShowStaffQuantity().getValue());
		toBmoOrder.getShowStaffPrice().setValue(fromBmoOrder.getShowStaffPrice().getValue());
		
		// Actualizar valor del pedido
		this.updateBalance(pmConn, toBmoOrder, bmUpdateResult);
	}
	
	// Revisa si tiene pedidos activos
	private boolean hasActiveOrder(PmConn pmConn, BmoOrder bmoOrder) throws SFException {
		String sql = " SELECT * FROM orders" + " WHERE orde_originreneworderid= " + bmoOrder.getId()
				+ " AND orde_status != '" + BmoOrder.STATUS_AUTHORIZED + "'" + " AND orde_status != '"
				+ BmoOrder.STATUS_FINISHED + "'";

		pmConn.doFetch(sql);
		return pmConn.next();
	}

	// Crear pedido Extra
	private BmUpdateResult createExtraOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		bmoWFlowType = (BmoWFlowType) pmWFlowType.get(pmConn, bmoOrder.getBmoOrderType().getwFlowTypeId().toInteger());
		if (!(bmoWFlowType.getId() > 0)) {
			bmUpdateResult.addError("", "<b>No está Configurado el Tipo de Flujo para Pedidos Extras.</b>");
			return bmUpdateResult;
		}
		int count = 1;
		BmoOrder newBmoOrder = new BmoOrder();

		PmOrder pmOrder = new PmOrder(getSFParams());
		// CONSULTAR EL ULTIMO ORDER EXTRA PARA PONER EL NOMBRE
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(newBmoOrder.getKind(), newBmoOrder.getOriginRenewOrderId().getName(), bmoOrder.getId());
		Iterator<BmObject> listOrderOrigin = pmOrder.list(pmConn, bmFilter).iterator();
		if (listOrderOrigin.hasNext()) {
			while (listOrderOrigin.hasNext()) {
				
//				BmoOrder bmoOrderBefore = new BmoOrder();
//				bmoOrderBefore = (BmoOrder) listOrderOrigin.next();
				count++;
			}
		}

		newBmoOrder.getName().setValue(bmoOrder.getName().toString() + " _EXTRA " + count);
		newBmoOrder.getDescription().setValue(bmoOrder.getDescription().toString());

		newBmoOrder.getLockStart().setValue(bmoOrder.getLockStart().toString());
		newBmoOrder.getLockEnd().setValue(bmoOrder.getLockEnd().toString());

		newBmoOrder.getCustomerId().setValue(bmoOrder.getCustomerId().toString());
		newBmoOrder.getUserId().setValue(bmoOrder.getUserId().toString());
		newBmoOrder.getMarketId().setValue(bmoOrder.getMarketId().toInteger());
		newBmoOrder.getOrderTypeId().setValue(bmoOrder.getOrderTypeId().toInteger());

		// newBmoOrder.getTags().setValue(bmoOrder.getTags().toString());
		// newBmoOrder.getWFlowTypeId().setValue(bmoOrder.getWFlowTypeId().toInteger());
		newBmoOrder.getWFlowTypeId().setValue(bmoWFlowType.getId());
		newBmoOrder.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
		newBmoOrder.getTax().setValue(bmoOrder.getTax().toDouble());
		newBmoOrder.getTaxApplies().setValue(bmoOrder.getTaxApplies().toString());
		newBmoOrder.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
		newBmoOrder.getCustomerContactId().setValue(bmoOrder.getCustomerContactId().toString());
		newBmoOrder.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
		newBmoOrder.getWillRenew().setValue(bmoOrder.getWillRenew().toInteger());
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			newBmoOrder.getDefaultBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
			newBmoOrder.getDefaultAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
		}

		if (!getSFParams().isProduction())	
			System.out.println("Tipo de Cambio : " + bmoOrder.getCoverageParity().toDouble());

		newBmoOrder.getCoverageParity().setValue(bmoOrder.getCoverageParity().toInteger());
		newBmoOrder.getCustomerRequisition().setValue(bmoOrder.getCustomerRequisition().toString());

		// Asigna campos de pedido origen
		newBmoOrder.getRenewOrderId().setValue(bmoOrder.getId());
		newBmoOrder.getOriginRenewOrderId().setValue(bmoOrder.getId());
		// Deja el nuevo pedido en revision
		newBmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
	
		this.save(pmConn, newBmoOrder, bmUpdateResult);
		if (!bmUpdateResult.hasErrors()) {

			// Asigna los usuarios al nuevo pedido creado
			createWFlowUsersFromOrderExtra(pmConn, bmoOrder, newBmoOrder, bmUpdateResult);
			bmUpdateResult.setBmObject(newBmoOrder);
		} else {
			// Existen errores, no se puede proseguir
			throw new SFException(this.getClass().getName() + "-createExtraOrder(): ERROR: "
					+ bmUpdateResult.errorsToString());
		}
		return bmUpdateResult;
	}

	// Crea nuevos pedidos a partir de items de productos con auto renovacion
	private void createRenewOrderProducts(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {

		// Listar los Tipos de Pedido
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = new BmoWFlowType();

		// Obtener los productos del pedido
		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
		BmoOrderItem bmoOrderItem = new BmoOrderItem();

		// Obtener los productos del pedido de inmueble
		PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(getSFParams());
		BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();

		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		PmProduct pmProduct = new PmProduct(getSFParams());
//		PmProperty pmProperty = new PmProperty(getSFParams());
		
		PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
		BmoConsultancy bmoConsultancy = new BmoConsultancy();

		// Lista donde se agregan los items a renovar
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		ArrayList<BmObject> orderItemList = new ArrayList<BmObject>();
		ArrayList<BmObject> ordePropertyList = new ArrayList<BmObject>();

		Iterator<BmObject> wFlowTypeIterator = null;
		if (bmoOrder.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_LEASE)) {
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getIdField(),
					bmoOrder.getWFlowTypeId().toInteger());
			filterList.add(filterByOrder);
			wFlowTypeIterator = pmWFlowType.list(pmConn, filterList).iterator();
		} else if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
			wFlowTypeIterator = pmWFlowType.list(pmConn).iterator();
		}
		

		while (wFlowTypeIterator.hasNext()) {
			bmoWFlowType = (BmoWFlowType) wFlowTypeIterator.next();

			// Revisa que no haya ya un pedido del mismo tipo ligado ya creado
			if (!renewOrderExists(pmConn, bmoOrder.getId(), bmoWFlowType.getId())) {
				printDevLog("----------- revisa que no exista " + bmUpdateResult.errorsToString());

				filterList = new ArrayList<BmFilter>();

				if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
					// Filtra por items del pedido
					BmFilter filterByOrder = new BmFilter();
					filterByOrder.setValueFilter(bmoOrderPropertyTax.getKind(), bmoOrderPropertyTax.getOrderId(),
							bmoOrder.getId());
					filterList.add(filterByOrder);

					// Filtra por items en inmueble con renovacion
					BmFilter filterByRenewProperty = new BmFilter();
					filterByRenewProperty.setValueFilter(bmoOrderPropertyTax.getKind(),
							bmoOrderPropertyTax.getBmoProperty().getRenewOrder(), "1");
					filterList.add(filterByRenewProperty);

					ordePropertyList = pmOrderPropertyTax.list(pmConn, filterList);
				} else if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
					// Filtra por items del pedido
					BmFilter filterByOrder = new BmFilter();
					filterByOrder.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getBmoOrderGroup().getOrderId(),
							bmoOrder.getId());
					filterList.add(filterByOrder);

					// Filtra por items en productos con renovacion
					BmFilter filterByRenewProduct = new BmFilter();
					filterByRenewProduct.setValueFilter(bmoOrderItem.getKind(),
							bmoOrderItem.getBmoProduct().getRenewOrder(), "1");
					filterList.add(filterByRenewProduct);

					// Filtra productos con renovacion, del tipo de pedido actual
					BmFilter filterByRenewWFlowType = new BmFilter();
					filterByRenewWFlowType.setValueFilter(bmoOrderItem.getKind(),
							bmoOrderItem.getBmoProduct().getWFlowTypeId(), bmoWFlowType.getId());
					filterList.add(filterByRenewWFlowType);

					orderItemList = pmOrderItem.list(pmConn, filterList);
					bmoConsultancy = new BmoConsultancy();
				}
				// Si hay elementos, crear nuevo pedido y agregar items
				if (orderItemList.size() > 0 || ordePropertyList.size() > 0) {

					BmoOrder newBmoOrder = new BmoOrder();
					PmOrder newPmOrder = new PmOrder(getSFParams());
					
					// Crea/Actualiza datos
					newBmoOrder.getName().setValue(bmoOrder.getName().toString() + " *");
					newBmoOrder.getDescription().setValue(bmoOrder.getDescription().toString());

					// Suma los meses entre el inicio y fin, y renueva los mismos meses
					int months = SFServerUtil.monthsBetween(getSFParams().getDateTimeFormat(),
							bmoOrder.getLockStart().toString(), bmoOrder.getLockEnd().toString());
					newBmoOrder.getLockStart().setValue(SFServerUtil.addMonths(getSFParams().getDateTimeFormat(),
							bmoOrder.getLockStart().toString(), months));
					newBmoOrder.getLockEnd().setValue(SFServerUtil.addMonths(getSFParams().getDateTimeFormat(),
							bmoOrder.getLockEnd().toString(), months));

					newBmoOrder.getCustomerId().setValue(bmoOrder.getCustomerId().toString());
					newBmoOrder.getUserId().setValue(bmoOrder.getUserId().toString());
					BmoOrderItem firstBmoOrderItem = new BmoOrderItem();
					if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
						newBmoOrder.getOrderTypeId().setValue(bmoOrder.getOrderTypeId().toInteger());
					} else if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
						// Obtiene el OrderType del primer producto listado
						firstBmoOrderItem = (BmoOrderItem) orderItemList.iterator().next();
						newBmoOrder.getOrderTypeId()
								.setValue(firstBmoOrderItem.getBmoProduct().getOrderTypeId().toString());
					}
					
					newBmoOrder.getWFlowTypeId().setValue(bmoWFlowType.getId());
					// Colocar Tipo Cambio a dia de hoy
					newBmoOrder.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
					PmCurrency pmCurrency = new PmCurrency(getSFParams());
					String parityNow = "" + bmoOrder.getCurrencyId().toString() + "|"
							+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
					double partityNow = SFServerUtil
							.stringToDouble(pmCurrency.getParityFromCurrency(parityNow, bmUpdateResult));
					newBmoOrder.getCurrencyParity().setValue(partityNow);

					newBmoOrder.getTax().setValue(bmoOrder.getTax().toDouble());
					newBmoOrder.getTaxApplies().setValue(bmoOrder.getTaxApplies().toInteger());
					newBmoOrder.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0)
							newBmoOrder.getDefaultBudgetItemId()
									.setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						else
							bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(),
									"Debe asignar una Partida Presupuestal en el Pedido a  ("
											+ bmoOrder.getCode().toString() + ")");

						newBmoOrder.getDefaultAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
					}

					if (!getSFParams().isProduction())
						System.out.println("Tipo de Cambio : " + bmoOrder.getCoverageParity().toDouble());

					newBmoOrder.getCoverageParity().setValue(bmoOrder.getCoverageParity().toInteger());
					newBmoOrder.getMarketId().setValue(bmoOrder.getMarketId().toInteger());
					if (!bmUpdateResult.hasErrors()) {
						// Crear venta de consultoria
						if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
							BmoConsultancy bmoConsultancyPrev = new BmoConsultancy();
							PmConsultancy pmConsultancyPrev = new PmConsultancy(getSFParams());
							bmoConsultancyPrev = (BmoConsultancy)pmConsultancyPrev.getBy(pmConn, bmoOrder.getId(), bmoConsultancyPrev.getOrderId().getName());
							pmConsultancy.createConsultancyFromOrderRenew(pmConn, bmoConsultancy, newBmoOrder, bmUpdateResult);
							if (!bmUpdateResult.hasErrors()) 
								bmoConsultancy = (BmoConsultancy)bmUpdateResult.getBmObject();
							// Obtener el pedido de la consultoria
							if (bmoConsultancy.getOrderId().toInteger() > 0 && !bmUpdateResult.hasErrors() ) {
								newBmoOrder = (BmoOrder)newPmOrder.get(pmConn, bmoConsultancy.getOrderId().toInteger());
							}
							
							// Sobrescribir valores de datos venta
							bmoConsultancy.getCustomerRequisition().setValue(bmoConsultancyPrev.getCustomerRequisition().toString());
							bmoConsultancy.getCustomerContactId().setValue(bmoConsultancyPrev.getCustomerContactId().toString());
							
							// Sobrescribir valores de datos scrum
							bmoConsultancy.getCloseDate().setValue(newBmoOrder.getDateCreate().toString());
							bmoConsultancy.getOrderDate().setValue(newBmoOrder.getLockStart().toString().substring(0, 10));
							if(newBmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger() > 0) {
								bmoConsultancy.getAreaIdScrum().setValue(bmoOrder.getBmoOrderType().getAreaDefaultDetail().toInteger());
							}
							if(!newBmoOrder.getBmoOrderType().getStatusDefaultDetail().equals("")) {
								bmoConsultancy.getStatusScrum().setValue(bmoOrder.getBmoOrderType().getStatusDefaultDetail().toChar());
							}
							if (!bmUpdateResult.hasErrors()) 
								pmConsultancy.saveSimple(pmConn, bmoConsultancy, bmUpdateResult);
							
							// Forzar a colocar valores ya que se pierden si el pedido es consultoria
							newBmoOrder.getOrderTypeId().setValue(firstBmoOrderItem.getBmoProduct().getOrderTypeId().toString());
							
						}
						
						// Forzar a colocar valores ya que se pierden si el pedido es consultoria
						newBmoOrder.getWFlowTypeId().setValue(bmoWFlowType.getId());
						newBmoOrder.getWillRenew().setValue(bmoOrder.getWillRenew().toInteger());
						// Asigna campos de pedido origen
						newBmoOrder.getRenewOrderId().setValue(bmoOrder.getId());

						// Si el pedido que va a renovar tiene un pedido origen, pasarlo, sino el pedido de donde viene
						if (bmoOrder.getOriginRenewOrderId().toInteger() > 0)
							newBmoOrder.getOriginRenewOrderId().setValue(bmoOrder.getOriginRenewOrderId().toInteger());
						else
							newBmoOrder.getOriginRenewOrderId().setValue(bmoOrder.getId());
						
						
						// Guardar valores que se pudieron haber perdido
						if (!bmUpdateResult.hasErrors()) { 
							// Guardar y actualizar montos y estatus
							this.saveSimple(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateBalance(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updatePayments(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateStatus(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateDeliveryStatus(pmConn, newBmoOrder, bmUpdateResult);
						}
						printDevLog("---- Por crear usuarios del pedido: " + bmoOrder.getCode().toString()
									+ ", errores: " + bmUpdateResult.errorsToString());

						// Asigna los usuarios al nuevo pedido creado
						if (!bmUpdateResult.hasErrors()) 
							createWFlowUsersFromOrder(pmConn, bmoOrder, newBmoOrder, bmUpdateResult);

						printDevLog("----- Usuarios creados del nuevo pedido ID: " + newBmoOrder.getId()
									+ ", errores: " + bmUpdateResult.errorsToString());

						// Si el pedido es de tipo arrendamiento crear items de arrendamiento
						if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
							// Crear los items
							Iterator<BmObject> orderIterator = ordePropertyList.iterator();
							while (orderIterator.hasNext()) {

								BmoOrderPropertyTax nextBmoOrderItem = (BmoOrderPropertyTax) orderIterator.next();

								BmoOrderPropertyTax newBmoOrderItem = new BmoOrderPropertyTax();
								newBmoOrderItem.getPropertyId().setValue(nextBmoOrderItem.getPropertyId().toInteger());
								newBmoOrderItem.getOrderId().setValue(newBmoOrder.getId());

								if (nextBmoOrderItem.getPropertyId().toInteger() > 0) {
									// BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn,
									// nextBmoOrderItem.getPropertyId().toInteger());

									// Calcula incremento si esta asignado
									// if (bmoProperty.getRenewRate().toDouble() > 0) {
									double piceItem = 0;
									// piceItem = nextBmoOrderItem.getPrice().toDouble() +
									// bmoProperty.getRenewRate().toDouble();
									newBmoOrderItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(piceItem));
									newBmoOrderItem.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(piceItem));
									// }
								}
								if (!bmUpdateResult.hasErrors()) 
									pmOrderPropertyTax.save(pmConn, newBmoOrderItem, bmUpdateResult);
							}
						} else if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
							// Genera un grupo de items para agregar los nuevos
							BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
							BmoOrderGroup bmoOrderGroupOld = new BmoOrderGroup();
							bmoOrderGroupOld = (BmoOrderGroup) pmOrderGroup.getBy(pmConn, bmoOrder.getId(),
									bmoOrderGroup.getOrderId().getName());

							// Revisa si ya existe un grupo en el pedido
							if (pmOrderGroup.orderHasGroups(pmConn, newBmoOrder, bmUpdateResult)) {
								bmoOrderGroup = (BmoOrderGroup) pmOrderGroup.getBy(pmConn, newBmoOrder.getId(),
										bmoOrderGroup.getOrderId().getName());

								bmoOrderGroup.getName().setValue(bmoWFlowType.getName().toString());
								bmoOrderGroup.getShowQuantity()
										.setValue(bmoOrderGroupOld.getShowQuantity().toInteger());
								bmoOrderGroup.getShowAmount().setValue(bmoOrderGroupOld.getShowAmount().toInteger());
								bmoOrderGroup.getShowPrice().setValue(bmoOrderGroupOld.getShowPrice().toInteger());
								bmoOrderGroup.getShowProductImage()
										.setValue(bmoOrderGroupOld.getShowProductImage().toInteger());
								bmoOrderGroup.getShowGroupImage()
										.setValue(bmoOrderGroupOld.getShowGroupImage().toInteger());

								if (!bmUpdateResult.hasErrors()) 
									pmOrderGroup.saveSimple(pmConn, bmoOrderGroup, bmUpdateResult);
							} else {
								bmoOrderGroup.getName().setValue(bmoWFlowType.getName().toString());
								bmoOrderGroup.getOrderId().setValue(newBmoOrder.getId());
								bmoOrderGroup.getShowQuantity()
										.setValue(bmoOrderGroupOld.getShowQuantity().toInteger());
								bmoOrderGroup.getShowAmount().setValue(bmoOrderGroupOld.getShowAmount().toInteger());
								bmoOrderGroup.getShowPrice().setValue(bmoOrderGroupOld.getShowPrice().toInteger());
								bmoOrderGroup.getShowProductImage()
										.setValue(bmoOrderGroupOld.getShowProductImage().toInteger());
								bmoOrderGroup.getShowGroupImage()
										.setValue(bmoOrderGroupOld.getShowGroupImage().toInteger());

								if (!bmUpdateResult.hasErrors()) 
									pmOrderGroup.save(pmConn, bmoOrderGroup, bmUpdateResult);
							}

							printDevLog("Por crear items del nuevo pedido: " + newBmoOrder.getCode().toString()
												+ ", errores: " + bmUpdateResult.errorsToString());

							// Crear los items
							Iterator<BmObject> orderItemIterator = orderItemList.iterator();
							while (orderItemIterator.hasNext()) {
								BmoOrderItem nextBmoOrderItem = (BmoOrderItem) orderItemIterator.next();

								BmoOrderItem newBmoOrderItem = new BmoOrderItem();
								newBmoOrderItem.getProductId().setValue(nextBmoOrderItem.getProductId().toInteger());
								newBmoOrderItem.getName().setValue(nextBmoOrderItem.getName().toString());
								newBmoOrderItem.getDescription().setValue(nextBmoOrderItem.getDescription().toString());
								newBmoOrderItem.getDays().setValue(nextBmoOrderItem.getDays().toDouble());
								newBmoOrderItem.getOrderGroupId().setValue(bmoOrderGroup.getId());

								// Calcula precio e incremento
								newBmoOrderItem.getBasePrice().setValue(nextBmoOrderItem.getBasePrice().toDouble());
								newBmoOrderItem.getBaseCost().setValue(nextBmoOrderItem.getBaseCost().toDouble());
								newBmoOrderItem.getPrice().setValue(nextBmoOrderItem.getPrice().toDouble());
								newBmoOrderItem.getAmount().setValue(nextBmoOrderItem.getAmount().toDouble());
								// Pasar datos de control presupuestal
								if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem()
										.toBoolean()) {
									newBmoOrderItem.getBudgetItemId()
											.setValue(nextBmoOrderItem.getBudgetItemId().toInteger());
									newBmoOrderItem.getAreaId().setValue(nextBmoOrderItem.getAreaId().toInteger());
								}
								if (nextBmoOrderItem.getProductId().toInteger() > 0) {
									BmoProduct bmoProduct = (BmoProduct) pmProduct.get(pmConn,
											nextBmoOrderItem.getProductId().toInteger());
									// Calcula incremento si esta asignado
									if (bmoProduct.getRenewRate().toDouble() > 0) {
										double piceItem = 0, amountItem = 0;
										piceItem = nextBmoOrderItem.getPrice().toDouble()
												* (1 + (bmoProduct.getRenewRate().toDouble() / 100));
										newBmoOrderItem.getPrice()
												.setValue(SFServerUtil.roundCurrencyDecimals(piceItem));

										amountItem = newBmoOrderItem.getPrice().toDouble()
												* newBmoOrderItem.getQuantity().toDouble()
												* newBmoOrderItem.getDays().toDouble();
										newBmoOrderItem.getAmount()
												.setValue(SFServerUtil.roundCurrencyDecimals(amountItem));
									}
								}
								if (!bmUpdateResult.hasErrors()) 
									pmOrderItem.save(pmConn, newBmoOrderItem, bmUpdateResult);
							}
						}
						// Deja el nuevo pedido en revision
						newBmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
						printDevLog("Asignando estatus revision al nuevo pedido: " + newBmoOrder.getCode().toString());

						// Guardar ultimos cambios
						if (!bmUpdateResult.hasErrors()) {
							// Asigna IVA dependiendo tipo de pedido si viene de una renovacion no asigna IVA de tipo de pedido
							if (newBmoOrder.getBmoOrderType().getHasTaxes().toBoolean() &&
									!(newBmoOrder.getRenewOrderId().toInteger() > 0))
								newBmoOrder.getTaxApplies().setValue(1);
							
							// Guardar y actualizar montos y estatus
							this.saveSimple(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateBalance(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updatePayments(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateStatus(pmConn, newBmoOrder, bmUpdateResult);
							newPmOrder.updateDeliveryStatus(pmConn, newBmoOrder, bmUpdateResult);
						}
						
						// Si no hay errores, generar bitacora en el pedido origen y el
						// destino(renovado)
						if (!bmUpdateResult.hasErrors()) {
							String commentsFromRenew = "Pedido Renovado a: ", commentsToRenew = "Renovación del Pedido:";
							if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
								commentsFromRenew = "Venta Renovada a: ";
								commentsToRenew = "Renovación de la Venta:";
							}
							// Bitacora de la renovacion
							PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

							// Bitacora del renovado
							pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_SYS,
									commentsFromRenew + newBmoOrder.getCode().toString(), "");

							pmWFlowLog.addDataLog(pmConn, bmUpdateResult, newBmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_SYS,
									commentsToRenew + bmoOrder.getCode().toString(), "");

							// Actualiza estatus del pedido
							this.updateStatus(pmConn, bmoOrder, bmUpdateResult);

							// Mandar notificacion del pedido renovado
							if (!(bmUpdateResult.hasErrors()) && getSFParams().isProduction() 
									&& bmoOrder.getBmoOrderType().getEmailRemindersOrderStart().toBoolean())
								sendReminder(bmoOrder);
							
							// Finalizar el pedido una vez renovado
							if (!(bmUpdateResult.hasErrors()) && bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
								bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
								if (!bmUpdateResult.hasErrors()) {
									PmOrder newPmOrderFinished = new PmOrder(getSFParams());
									newPmOrderFinished.save(pmConn, bmoOrder, bmUpdateResult);
								}
							}
						}

					} else {
						// Existen errores, no se puede proseguir
						throw new SFException(this.getClass().getName() + " - createRenewOrderProducts(): ERROR: "
								+ bmUpdateResult.errorsToString());
					}
				}
			}
		}
	}

	// Crea nuevos contratospedidos a partir de pedidos con auto renovacion
	private void createRenewOrderPropertyRental(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {

		// Obtener el contrato del pedido
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		String rentIncrease = "";
		String rentIncrease2 = "";
//		String endDate = "";
		Boolean renew = false;
		String rentIncreaseNew = "";
		// validar que no pueda renovar

		bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(pmConn,
				bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
		Date lockStart = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(),
				bmoPropertyRental.getRentIncrease().toString() + " 00:00");
		rentIncrease = bmoPropertyRental.getRentIncrease().toString() + " 00:00";

		rentIncrease2 = SFServerUtil.addMonths(getSFParams().getDateFormat(), rentIncrease, 12); // fecha incremento
//		endDate = bmoPropertyRental.getEndDate().toString() + " 00:00"; // fecha fin del contrato

		// validar que la fecha incremento no sobre pase la fecha fin del contrato

		Date rentIncrease22 = SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentIncrease2);
		Date endDate2 = SFServerUtil.stringToDate(getSFParams().getDateFormat(),
				bmoPropertyRental.getEndDate().toString());

		rentIncreaseNew = SFServerUtil.addMonths(getSFParams().getDateFormat(),
				bmoPropertyRental.getRentIncrease().toString(), 12);

		// Valida si la proxima renovacion es mayor al f.fin del contrato
		if (SFServerUtil.stringToDate(getSFParams().getDateFormat(), bmoPropertyRental.getRentIncrease().toString())
				.compareTo(endDate2) == 0) {
			//Finalizar contrato si la proxima renovacion es mayor a la f. fin de contrato
			bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_FINISHED);
			pmPropertyRental.save(bmoPropertyRental, new BmUpdateResult());
			bmUpdateResult.addError(bmoPropertyRental.getEndDate().getName(),
					"No es posible renovar, ya fue renovado al limite de la fecha fin del contrato");

		}
		// Valida si la proxima renovacion es mayor al f.fin del contrato
		else {
			if (rentIncrease22.compareTo(endDate2) > 0) {
				rentIncreaseNew = bmoPropertyRental.getEndDate().toString();
				Date rentIncreaseNew2 = SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentIncreaseNew);

				if (rentIncreaseNew2.compareTo(endDate2) <= 0) {
					renew = true;
				} else {
					renew = false;
					bmUpdateResult.addError(bmoPropertyRental.getEndDate().getName(),
							"Error no es posible renovar,la fecha incremento sobrepasa la fecha fin del contrato");
				}
			} else {
				renew = true;
			}
		}

		if (renew) {
			if (bmoPropertyRental.getContractTerm().toDouble() > 0) {
				bmoPropertyRental.getRentalScheduleDate().setValue(bmoPropertyRental.getRentIncrease().toString());
				bmoPropertyRental.getRentIncrease().setValue(rentIncreaseNew);

				bmUpdateResult = pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);

				// BmoPropertyRental bmoPropertyRentalNew = new BmoPropertyRental();

				// setear campos a bmoPropertyRentalNew
				// bmoPropertyRentalNew.getStatus().setValue(BmoPropertyRental.STATUS_REVISION);
				// bmoPropertyRentalNew.getName().setValue(bmoPropertyRental.getName().toString());
				// bmoPropertyRentalNew.getDescription().setValue(bmoPropertyRental.getDescription().toString());
				// bmoPropertyRentalNew.getStartDate().setValue(bmoPropertyRental.getStartDate().toString());
				// bmoPropertyRentalNew.getEndDate().setValue(bmoPropertyRental.getEndDate().toString());
				// bmoPropertyRentalNew.getOrderTypeId().setValue(bmoPropertyRental.getOrderTypeId().toInteger());
				// bmoPropertyRentalNew.getOrderId().setValue(bmoOrder.getId());
				// bmoPropertyRentalNew.getCompanyId().setValue(bmoPropertyRental.getCompanyId().toInteger());
				// bmoPropertyRentalNew.getMarketId().setValue(bmoPropertyRental.getMarketId().toInteger());
				// bmoPropertyRentalNew.getCurrencyId().setValue(bmoPropertyRental.getCurrencyId().toInteger());
				// bmoPropertyRentalNew.getCurrencyParity().setValue(bmoPropertyRental.getCurrencyParity().toDouble());
				// bmoPropertyRentalNew.getTags().setValue(bmoPropertyRental.getTags().toString());
				// bmoPropertyRentalNew.getCustomerContactId().setValue(bmoPropertyRental.getCustomerContactId().toInteger());
				// bmoPropertyRentalNew.getPropertyId().setValue(bmoPropertyRental.getPropertyId().toInteger());
				// bmoPropertyRentalNew.getContractTerm().setValue(bmoPropertyRental.getContractTerm().toInteger());
				// bmoPropertyRentalNew.getInitialIconme().setValue(bmoPropertyRental.getInitialIconme().toDouble());
				// bmoPropertyRentalNew.getCurrentIncome().setValue(bmoPropertyRental.getCurrentIncome().toDouble());
				// bmoPropertyRentalNew.getRentalScheduleDate().setValue(bmoPropertyRental.getRentIncrease().toString());
				// bmoPropertyRentalNew.getUserId().setValue(bmoPropertyRental.getUserId().toInteger());
				// bmoPropertyRentalNew.getCustomerId().setValue(bmoPropertyRental.getCustomerId().toInteger());
				// bmoPropertyRentalNew.getOwnerProperty().setValue(bmoPropertyRental.getOwnerProperty().toString());
				// bmoPropertyRentalNew.getWFlowTypeId().setValue(bmoPropertyRental.getWFlowTypeId().toInteger());
				// bmoPropertyRentalNew.getRentIncrease().setValue(SFServerUtil.addMonths(getSFParams().getDateFormat(),
				// bmoPropertyRental.getRentIncrease().toString(), 12));
				// bmUpdateResult = pmPropertyRental.save(pmConn,bmoPropertyRentalNew,
				// bmUpdateResult);

				// int propertyRentalId = bmUpdateResult.getId();
				// Obtenemos el nuevo contrato renovado
				// bmoPropertyRentalNew =
				// (BmoPropertyRental)pmPropertyRental.get(propertyRentalId);

				// Crear el pedido
				BmoOrder bmoOrderNew = new BmoOrder();
				PmOrder pmOrder = new PmOrder(getSFParams());

				// PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				bmoOrderNew = pmPropertyRental.createOrderFromPropertyRental(pmConn, bmoPropertyRental, bmUpdateResult);
				// bmoOrderNew = bmoOrder;

				// Asigna campos de pedido origen
				bmoOrderNew.getRenewOrderId().setValue(bmoOrder.getId());
				// Calendar lockStart =
				// SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
				// bmoPropertyRental.getRentIncrease().toString()+ " 00:00",
				// getSFParams().getDateTimeFormat());

				bmoOrderNew.getLockStart()
						.setValue(SFServerUtil.dateToString(lockStart, getSFParams().getDateTimeFormat()));

				bmoOrderNew.getLockEnd().setValue(rentIncreaseNew);
				//
				// Si el pedido que va a renovar tiene un pedido origen, pasarlo, sino el pedido
				// de donde viene
				// if (bmoOrder.getOriginRenewOrderId().toInteger() > 0)
				bmoOrderNew.getOriginRenewOrderId().setValue(bmoOrder.getOriginRenewOrderId().toInteger());
				// else
				// bmoOrderNew.getOriginRenewOrderId().setValue(bmoOrder.getId());
				pmOrder.saveSimple(bmoOrderNew, bmUpdateResult);
				//

				// Si no hay errores, generar bitacora en el pedido origen y el
				// destino(renovado)
				if (!bmUpdateResult.hasErrors()) {

					// Bitacora de la renovacion
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
					pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_SYS,
							"Contrato Renovado a: " + bmoOrderNew.getCode().toString(), "");

					// Bitacora del renovado
					pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrderNew.getWFlowId().toInteger(), BmoWFlowLog.TYPE_SYS,
							"Renovación del Contrato: " + bmoOrder.getCode().toString(), "");
				
					if (!bmUpdateResult.hasErrors())
						sendReminder(bmoOrder);
				}
			}
		}
	}

	// Revisar que no exista ya un pedido renovado de este, del tipo de flujo
	// esperado
	private boolean renewOrderExists(PmConn pmConn, int orderId, int wFlowTypeId) throws SFPmException {
		String sql = "SELECT orde_orderid FROM orders " + "	WHERE orde_reneworderid = " + orderId + ""
				+ " AND orde_wflowtypeid = " + wFlowTypeId;

		pmConn.doFetch(sql);
		return pmConn.next();
	}

	// Total de pedidos renovados del pedido origen
	private int countRenewOrder(int orderId) throws SFPmException {
		int totalOrderRenewed = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String sql = "SELECT count(*) as total FROM orders" + " WHERE orde_originreneworderid = " + orderId;
		printDevLog("Total de renovados: " + sql);
		pmConn.doFetch(sql);
		if (pmConn.next())
			totalOrderRenewed = pmConn.getInt("total");
		pmConn.close();
		return totalOrderRenewed;
	}

	// Crea los usuarios a partir del pedido origen
	private void createWFlowUsersFromOrderExtra(PmConn pmConn, BmoOrder bmoOrder, BmoOrder newBmoOrder,
			BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(),
				newBmoOrder.getBmoOrderType().getwFlowTypeId().toInteger());
		Iterator<BmObject> userList = pmWFlowUser.list(bmFilter).iterator();

		// Crear los usuarios en el nuevo proyecto
		while (userList.hasNext()) {
			bmoWFlowUser = (BmoWFlowUser) userList.next();

			// Revisar si ya hay registro esperando asignacion de usuario
			BmoWFlowUser projectWFlowUser = pmWFlowUser.getByGroup(pmConn, newBmoOrder.getWFlowId().toInteger(), -1,
					bmoWFlowUser.getProfileId().toInteger());

			if (projectWFlowUser.getId() > 0) {
				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
			} else {
				// Es nuevo, asignar valores
				projectWFlowUser.getWFlowId().setValue(newBmoOrder.getWFlowId().toInteger());
				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
				projectWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);
				projectWFlowUser.getLockStart().setValue(newBmoOrder.getLockStart().toString());
				projectWFlowUser.getLockEnd().setValue(newBmoOrder.getLockEnd().toString());
				projectWFlowUser.getRequired().setValue(bmoWFlowUser.getRequired().toString());
				projectWFlowUser.getAutoDate().setValue(bmoWFlowUser.getAutoDate().toString());
				projectWFlowUser.getProfileId().setValue(bmoWFlowUser.getProfileId().toInteger());
			}

			// Si no es igual al vendedor del proyecto, asignarlo, para no duplicar
			// asignaciones
			if (projectWFlowUser.getUserId().toInteger() != bmoOrder.getUserId().toInteger())
				pmWFlowUser.save(pmConn, projectWFlowUser, bmUpdateResult);
		}
	}

	// Crea los usuarios a partir del pedido origen
	private void createWFlowUsersFromOrder(PmConn pmConn, BmoOrder bmoOrder, BmoOrder newBmoOrder,
			BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoOrder.getWFlowId().toInteger());
		Iterator<BmObject> userList = pmWFlowUser.list(bmFilter).iterator();

		// Crear los usuarios en el nuevo proyecto
		while (userList.hasNext()) {
			bmoWFlowUser = (BmoWFlowUser) userList.next();

			// Revisar si ya esta asignado el usuario con el mismo grupo
			BmoWFlowUser projectWFlowUser = pmWFlowUser.getByGroup(pmConn, newBmoOrder.getWFlowId().toInteger(),
					bmoWFlowUser.getUserId().toInteger(), bmoWFlowUser.getProfileId().toInteger());

			if (projectWFlowUser.getId() > 0
					&& projectWFlowUser.getUserId().toInteger() == bmoWFlowUser.getUserId().toInteger()) {
				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
			} else {
				// Es diferente el grupo del usuario, crear registro nuevo si no, modificarlo
				if (projectWFlowUser.getUserId().toInteger() != bmoWFlowUser.getUserId().toInteger())
					projectWFlowUser = new BmoWFlowUser();

				projectWFlowUser.getWFlowId().setValue(newBmoOrder.getWFlowId().toInteger());
				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
				projectWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);
				projectWFlowUser.getLockStart().setValue(newBmoOrder.getLockStart().toString());
				projectWFlowUser.getLockEnd().setValue(newBmoOrder.getLockEnd().toString());
				projectWFlowUser.getRequired().setValue(bmoWFlowUser.getRequired().toString());
				projectWFlowUser.getAutoDate().setValue(bmoWFlowUser.getAutoDate().toString());
				projectWFlowUser.getProfileId().setValue(bmoWFlowUser.getProfileId().toInteger());
			}

			// Si no es igual al vendedor del proyecto, asignarlo, para no duplicar
			// asignaciones
			if (projectWFlowUser.getUserId().toInteger() != bmoOrder.getUserId().toInteger())
				pmWFlowUser.save(pmConn, projectWFlowUser, bmUpdateResult);
		}
	}

	// El pedido fue renovado
	public int orderWasRenewed(int orderId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		int orderWasRenewed = -1;

		// El pedido fue renovado?
		String sql = " SELECT orde_orderid FROM " + formatKind("orders") + " WHERE orde_reneworderid = " + orderId
				+ ";";

		printDevLog("orderWasRenewed:: " + sql);
		pmConn.doFetch(sql);
		if (pmConn.next())
			orderWasRenewed = pmConn.getInt("orde_orderid");

		pmConn.close();

		return orderWasRenewed;
	}

	// Actualiza sumas y valores
	public BmUpdateResult updateBalance(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		double orderItemAmount = 0, orderEquipmentAmount = 0, orderStaffAmount = 0, orderPropertyAmount = 0,
				orderPropertyTaxAmount = 0, orderPropertyModelExtraAmount = 0, orderSessionExtra = 0,
				orderSessionTypePackageAmount = 0, amount = 0, orderCreditAmount = 0;
		
		//Drea
		double comission = 0.0,feeProduction = 0.0;
		String sql = "";

		// Obtener el tipo de pedido por default
		int orderTypeDefaultId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toInteger();
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		BmoOrderType bmoOrderTypeDefault = (BmoOrderType) pmOrderType.get(pmConn, orderTypeDefaultId);

		// Suma los items de los grupos de la cotizacion
		if ( bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			sql = "select sum(ordg_amount) from ordergroups " + " where ordg_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderItemAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + " - updateBalance() SQL: " + sql);

			if (bmoOrderTypeDefault.getType().equals(BmoOrderType.TYPE_SESSION)) {
				sql = "select sum(orsx_amount) from ordersessionextras " + " where orsx_orderid = " + bmoOrder.getId();
				pmConn.doFetch(sql);
				if (pmConn.next())
					orderSessionExtra = pmConn.getDouble(1);
				if (!getSFParams().isProduction())
					System.out.println(this.getClass().getName() + " - updateBalance() SQL: " + sql);
			}
		}
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {		
			pmConn.doFetch("SELECT ordg_amount AS sumAmount,ordg_total AS sumTotal,ordg_iskit "
					+ ",ordg_comission,ordg_feeproduction FROM ordergroups "
					+ " WHERE ordg_orderid = " + bmoOrder.getId() );
			while (pmConn.next()) {
				if (pmConn.getInt("ordg_iskit") > 0)
					orderItemAmount += pmConn.getDouble("sumTotal");
				else
					orderItemAmount += pmConn.getDouble("sumAmount");
				
				comission += pmConn.getDouble("ordg_comission");
				feeProduction += pmConn.getDouble("ordg_feeproduction");
			}		
			
			 System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}


		// Suma de recursos y equipos de renta
//		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//			sql = "SELECT sum(ordq_amount) FROM orderequipments " + " WHERE ordq_orderid = " + bmoOrder.getId();
//			pmConn.doFetch(sql);
//			if (pmConn.next())
//				orderEquipmentAmount = pmConn.getDouble(1);
//			if (!getSFParams().isProduction())
//				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
//
//			// Suma de personal
//			sql = "SELECT sum(ords_amount) FROM orderstaff " + " WHERE ords_orderid = " + bmoOrder.getId();
//			pmConn.doFetch(sql);
//			if (pmConn.next())
//				orderStaffAmount = pmConn.getDouble(1);
//			if (!getSFParams().isProduction())
//				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
//		}

		// Suma de inmuebles
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			sql = "SELECT sum(orpy_amount) FROM orderproperties " + " WHERE orpy_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderPropertyAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "SELECT sum(orpx_amount) FROM orderpropertymodelextras " + " WHERE orpx_orderid = "
					+ bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderPropertyModelExtraAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}

		// Suma de inmuebles
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			sql = "SELECT sum(orpt_amount) FROM orderpropertiestax " + " WHERE orpt_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderPropertyTaxAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "SELECT sum(orpx_amount) FROM orderpropertymodelextras " + " WHERE orpx_orderid = "
					+ bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderPropertyModelExtraAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}

		// Suma de sesiones
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {

			// Suma de paquetes de sesiones
			sql = "SELECT sum(orsp_amount) FROM ordersessiontypepackages " + " WHERE orsp_orderid = "
					+ bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderSessionTypePackageAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "select sum(ordg_amount) from ordergroups " + " where ordg_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderItemAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "select sum(orsx_amount) from ordersessionextras " + " where orsx_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderSessionExtra = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

		}

		// Suma de Credito
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			// Suma de paquetes de sesiones
			sql = "SELECT sum(orcr_amount) FROM ordercredits " + " WHERE orcr_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderCreditAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "select sum(ordg_amount) from ordergroups " + " where ordg_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderItemAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction())
				System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}

		// Suma todos los conceptos agregados
		amount = orderItemAmount + orderEquipmentAmount + orderStaffAmount + orderPropertyAmount
				+ orderPropertyTaxAmount + orderPropertyModelExtraAmount + orderSessionTypePackageAmount
				+ orderCreditAmount + orderSessionExtra;

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			amount = amount + feeProduction - comission;
		}
		// Calcular montos
		if (amount == 0) {
			bmoOrder.getAmount().setValue(0);
			bmoOrder.getDiscount().setValue(0);
			bmoOrder.getTax().setValue(0);
			bmoOrder.getTotal().setValue(0);
		} else {
			bmoOrder.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			if (bmoOrder.getDiscount().toString().equals(""))
				bmoOrder.getDiscount().setValue(0);

			double tax = 0;
			double total = 0;

			double taxRate = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
			if (bmoOrder.getTaxApplies().toBoolean())
				tax = (bmoOrder.getAmount().toDouble() - bmoOrder.getDiscount().toDouble()) * taxRate;
			total = bmoOrder.getAmount().toDouble() - bmoOrder.getDiscount().toDouble() + tax;

			bmoOrder.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
			bmoOrder.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));
		}
		
		// Copiar montos modificados a la venta de servicio
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
			if (pmConsultancy.consultancyOrderExists(pmConn, bmoOrder.getId()))
				pmConsultancy.copyAmountsFromOrder(pmConn, bmoOrder, bmUpdateResult);
		}
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) ) {
			PmProject pmProject = new PmProject(getSFParams());
			if(	pmProject.projectOrderExists1(pmConn, bmoOrder.getId())){
				BmoProject bmoProject = (BmoProject)pmProject.getBy(bmoOrder.getId(), new BmoProject().getOrderId().getName());
				bmoProject.getTotal().setValue(bmoOrder.getTotal().toDouble());
				pmProject.saveSimple(pmConn, bmoProject, bmUpdateResult);
			}
		}

		return super.save(pmConn, bmoOrder, bmUpdateResult);
	}

	// Actualiza estatus de bloqueo
	public BmUpdateResult updateLockStatus(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		// Default sin conflictos
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);

		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {

			// Revisar estatus de conflicto de los items,
			pmConn.doFetch("SELECT ordi_orderitemid FROM orderitems "
					+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) "
					+ " WHERE ordi_lockstatus = '" + BmoOrderItem.LOCKSTATUS_CONFLICT + "' AND ordg_orderid = "
					+ bmoOrder.getId());
			if (pmConn.next())
				bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_CONFLICT);

			// Revisar estatus de conflicto de los equipos
//			pmConn.doFetch("SELECT ordq_orderequipmentid FROM orderequipments " + " WHERE ordq_lockstatus = '"
//					+ BmoOrderItem.LOCKSTATUS_CONFLICT + "' AND ordq_orderid = " + bmoOrder.getId());
//			if (pmConn.next())
//				bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_CONFLICT);

			// Revisar estatus de conflicto del personal
//			pmConn.doFetch("SELECT ords_orderstaffid FROM orderstaff " + " WHERE ords_lockstatus = '"
//					+ BmoOrderItem.LOCKSTATUS_CONFLICT + "' AND ords_orderid = " + bmoOrder.getId());
//			if (pmConn.next())
//				bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_CONFLICT);
		}

		return super.save(pmConn, bmoOrder, bmUpdateResult);
	}

	// Actualiza fechas de los items del pedido
	private void updateItemsDatesLockStatus(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		boolean forceCalculate = false;
		try {
			PmOrder prevPmOrder = new PmOrder(getSFParams());
			BmoOrder prevBmoOrder = (BmoOrder) prevPmOrder.get(bmoOrder.getId());
			if (!prevBmoOrder.getLockStart().toString().equals(bmoOrder.getLockStart().toString()))
				forceCalculate = true;

			if (!prevBmoOrder.getLockEnd().toString().equals(bmoOrder.getLockEnd().toString()))
				forceCalculate = true;
		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-updateItemDatesLock() " + e.toString());
		}

		// Actualiza bloqueo de Items
		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
		BmoOrderItem bmoOrderItem = new BmoOrderItem();
		BmFilter itemsByOrder = new BmFilter();
		itemsByOrder.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getBmoOrderGroup().getOrderId(),
				bmoOrder.getId());
		Iterator<BmObject> orderItemIterator = pmOrderItem.list(pmConn, itemsByOrder).iterator();
		while (orderItemIterator.hasNext() && !bmUpdateResult.hasErrors()) {
			BmoOrderItem bmoModOrderItem = (BmoOrderItem) orderItemIterator.next();
			pmOrderItem.updateLockStatus(pmConn, bmoOrder, bmoModOrderItem, forceCalculate, bmUpdateResult);
		}

		// Actualiza bloqueo de Recursos
//		PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(getSFParams());
//		BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
//		BmFilter equipmentsByOrder = new BmFilter();
//		equipmentsByOrder.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId(), bmoOrder.getId());
//		Iterator<BmObject> orderEquipmentIterator = pmOrderEquipment.list(pmConn, equipmentsByOrder).iterator();
//		while (orderEquipmentIterator.hasNext() && !bmUpdateResult.hasErrors()) {
//			BmoOrderEquipment bmoModOrderEquipment = (BmoOrderEquipment) orderEquipmentIterator.next();
//			bmoModOrderEquipment.getLockStart().setValue(bmoOrder.getLockStart().toString());
//			bmoModOrderEquipment.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//			pmOrderEquipment.updateLockStatus(pmConn, bmoModOrderEquipment, bmUpdateResult);
//		}

		// Actualiza bloqueo de Personal
//		PmOrderStaff pmOrderStaff = new PmOrderStaff(getSFParams());
//		BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
//		BmFilter staffByOrder = new BmFilter();
//		staffByOrder.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId(), bmoOrder.getId());
//		Iterator<BmObject> orderStaffIterator = pmOrderStaff.list(pmConn, staffByOrder).iterator();
//		while (orderStaffIterator.hasNext() && !bmUpdateResult.hasErrors()) {
//			BmoOrderStaff bmoModOrderStaff = (BmoOrderStaff) orderStaffIterator.next();
//			bmoModOrderStaff.getLockStart().setValue(bmoOrder.getLockStart().toString());
//			bmoModOrderStaff.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//			pmOrderStaff.updateLockStatus(pmConn, bmoModOrderStaff, bmUpdateResult);
//		}
	}

	public void updateDeliveryStatus(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		char deliveryStatus = BmoOrder.DELIVERYSTATUS_PENDING;
		String sql = "";
		double sumItemOrder = 0, sumItemDel = 0;

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {

			// Sumar los items de los pedidos
			sql = " SELECT SUM(ordi_quantity) as sumOrdi FROM orderitems "
					+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) " 
					+ " LEFT JOIN products ON (prod_productid = ordi_productid) " 
					+ " WHERE ordg_orderid = " + bmoOrder.getId() 
					+ " AND ordi_productid > 0 "	
					+ " AND prod_type <> '" + BmoProduct.TYPE_COMPOSED + "'";
			pmConn.doFetch(sql);
			if (pmConn.next())
				sumItemOrder = FlexUtil.roundDouble(pmConn.getDouble("sumOrdi"), 4);

			printDevLog("updateDeliveryStatus - sumItemOrder: " + sumItemOrder);

			// Sumar los items de la envio
			sql = " SELECT SUM(odyi_quantity) as sumOdyi FROM orderdeliveryitems "
					+ " LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid)"
					+ " WHERE odly_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				sumItemDel = FlexUtil.roundDouble(pmConn.getDouble("sumOdyi"), 4);

			printDevLog("updateDeliveryStatus - sumItemDel: " + sumItemDel);

			// Comparar los items
			if (sumItemDel == 0)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_PENDING;
			else if (sumItemDel < sumItemOrder)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_PARTIAL;
			else if (sumItemDel >= sumItemOrder)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_TOTAL;

		} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			double creditAmount = 0;
			sql = " SELECT SUM(orcr_price) as amount from ordercredits " + " WHERE orcr_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				creditAmount = pmConn.getDouble("amount");

			double dispercion = 0;
			sql = " SELECT SUM(odly_total) as total from orderdeliveries " + " WHERE odly_orderid = "
					+ bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				dispercion = pmConn.getDouble("total");

			if (dispercion >= creditAmount)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_TOTAL;
			else if (dispercion > 0)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_PARTIAL;
			else if (dispercion == 0)
				deliveryStatus = BmoOrder.DELIVERYSTATUS_PENDING;
		}

		// Actualizar el status de entrega de la oc
		sql = " UPDATE orders SET orde_deliverystatus = '" + deliveryStatus + "'" + " WHERE orde_orderid = "
				+ bmoOrder.getId();
		pmConn.doUpdate(sql);

		bmoOrder.getDeliveryStatus().setValue(deliveryStatus);
		super.save(pmConn, bmoOrder, bmUpdateResult);

		printDevLog("delivery " + deliveryStatus);
	}

	// Actualiza estatus y montos de pagos
	public void updatePayments(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {

		// Asigna montos a campos de sumas de pagos del Pedido
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = new BmoRaccount();
		ArrayList<BmFilter> filters = new ArrayList<BmFilter>();

		// Filtro de pedido
		BmFilter filterByOrder = new BmFilter();
		filterByOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
		filters.add(filterByOrder);

		Iterator<BmObject> raccountList = pmRaccount.list(pmConn, filters).iterator();

		double totalRaccounts = 0, totalCreditNotes = 0, totalBalance = 0, totalPayments = 0;

		while (raccountList.hasNext()) {
			BmoRaccount bmoNextRaccount = (BmoRaccount) raccountList.next();
			printDevLog(" --- " + bmoNextRaccount.getCode().toString() + " ---");

			// Notas de Credito
			if (bmoNextRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
				double creditNoteInOrderCurrency = 0;
				if (bmoOrder.getCurrencyId().toInteger() == bmoNextRaccount.getCurrencyId().toInteger()) {
					creditNoteInOrderCurrency = bmoNextRaccount.getAmount().toDouble();
				} else {
					creditNoteInOrderCurrency = ((bmoNextRaccount.getAmount().toDouble()
							* bmoNextRaccount.getCurrencyParity().toDouble())
							/ bmoOrder.getCurrencyParity().toDouble());
				}
				totalCreditNotes += creditNoteInOrderCurrency;

				printDevLog("Nota de Credito " + bmoNextRaccount.getCode().toString() + " en moneda pedido : "
						+ creditNoteInOrderCurrency);
			}
			// Cuentas x Cobrar
			else if ((bmoNextRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_ORDER)
					|| bmoNextRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER))
					&& bmoNextRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
				// Cifras CxC
				double raccountInOrderCurrency = 0, balanceInOrderCurrency = 0, paymentInOrderCurrency = 0;
				if (bmoOrder.getCurrencyId().toInteger() == bmoNextRaccount.getCurrencyId().toInteger()) {
					raccountInOrderCurrency = bmoNextRaccount.getAmount().toDouble();
					balanceInOrderCurrency = bmoNextRaccount.getBalance().toDouble();
					paymentInOrderCurrency = bmoNextRaccount.getTotal().toDouble()
							- bmoNextRaccount.getBalance().toDouble();
				} else {
					raccountInOrderCurrency = ((bmoNextRaccount.getAmount().toDouble()
							* bmoNextRaccount.getCurrencyParity().toDouble())
							/ bmoOrder.getCurrencyParity().toDouble());
					balanceInOrderCurrency = ((bmoNextRaccount.getBalance().toDouble()
							* bmoNextRaccount.getCurrencyParity().toDouble())
							/ bmoOrder.getCurrencyParity().toDouble());
					paymentInOrderCurrency = (((bmoNextRaccount.getTotal().toDouble()
							- bmoNextRaccount.getBalance().toDouble()) * bmoNextRaccount.getCurrencyParity().toDouble())
							/ bmoOrder.getCurrencyParity().toDouble());
				}
				totalRaccounts += raccountInOrderCurrency;
				totalBalance += balanceInOrderCurrency;
				totalPayments += paymentInOrderCurrency;

				printDevLog("CxC " + bmoNextRaccount.getCode().toString() + " en moneda pedido : "
						+ raccountInOrderCurrency);
				printDevLog("Pagos en CxC " + bmoNextRaccount.getCode().toString() + " en moneda pedido : "
						+ paymentInOrderCurrency);
				printDevLog("Saldo en CxC " + bmoNextRaccount.getCode().toString() + " en moneda pedido : "
						+ balanceInOrderCurrency);
			}
		}

		bmoOrder.getTotalRaccounts().setValue(totalRaccounts);
		bmoOrder.getTotalCreditNotes().setValue(totalCreditNotes);
		bmoOrder.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));
		bmoOrder.getBalance().setValue(totalBalance);
		// Si no hubo pagos el saldo se iguala al total
		if (totalBalance == 0 && totalPayments == 0)
			bmoOrder.getBalance().setValue(bmoOrder.getTotal().toDouble());

		printDevLog(" --- Totales ---");
		printDevLog("Total CxC Moneda del pedido: " + SFServerUtil.roundCurrencyDecimals(totalRaccounts));
		printDevLog("Total Notas Credito Moneda del pedido: " + SFServerUtil.roundCurrencyDecimals(totalCreditNotes));
		printDevLog("Total Pagos Moneda del pedido: " + SFServerUtil.roundCurrencyDecimals(totalPayments));
		printDevLog("Total Saldo Moneda del pedido: " + SFServerUtil.roundCurrencyDecimals(totalBalance));
		// Si no hay saldo, el estatus es pagado
		if (totalBalance == 0 && bmoOrder.getTotal().toDouble() > 0 && bmoOrder.getPayments().toDouble() > 0) {
			bmoOrder.getPaymentStatus().setValue(BmoOrder.PAYMENTSTATUS_TOTAL);

			// Es de tipo credito
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Obtener credito
				BmoCredit bmoCredit = new BmoCredit();
				PmCredit pmCredit = new PmCredit(getSFParams());
				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

				// Finalizar el Credito
				bmoCredit.getStatus().setValue(BmoCredit.STATUS_FINISHED);

				/// Bitacora de finalizacion
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoCredit.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER,
						"El Crédito Finalizó, Pago Total del Pedido.");

				super.save(pmConn, bmoCredit, bmUpdateResult);

				// Finalizar Pedido
				bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
			}
			// Es de tipo arrendamiento
			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Obtener contrato
				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(pmConn,
						bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());

				// Finalizar el Contrato
				// bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_FINISHED);

				/// Bitacora de finalizacion
				// PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
				// pmWFlowLog.addLog(pmConn, bmUpdateResult,
				/// bmoPropertyRental.getWFlowId().toInteger(),
				/// bmoPropertyRental.getCustomerId().toInteger(),
				// BmoWFlowLog.TYPE_OTHER, "El Contrato Finalizó, Pago Total del Contrato.");

				// super.save(pmConn, bmoPropertyRental, bmUpdateResult);

				// Si el total no es igual a la suma de pagos, no cambiar los estatus
				if (bmoOrder.getTotal().toDouble() != bmoOrder.getPayments().toDouble()) {
					bmoOrder.getPaymentStatus().setValue(BmoOrder.PAYMENTSTATUS_PENDING);
				} else {
					// Finalizar Pedido
					bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
				}
				this.saveSimple(pmConn, bmoOrder, bmUpdateResult);
				// Actualiza estatus propiedad
				PmProperty pmProperty = new PmProperty(getSFParams());
				// BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn,
				// bmoPropertyRental.getPropertyId().toInteger());
				pmProperty.updateAvailability(pmConn, bmoPropertyRental, bmUpdateResult);
			}
			// Es de tipo sesion
			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
				BmoSessionSale bmoSessionSale = new BmoSessionSale();
				PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
				bmoSessionSale = (BmoSessionSale) pmSessionSale.getBy(pmConn, bmoOrder.getId(),
						bmoSessionSale.getOrderId().getName());

				// Actualizar el estatus de pago de la venta de session
				bmoSessionSale.getPaymentStatus().setValue(BmoSessionSale.PAYMENTSTATUS_TOTAL);
				bmoSessionSale.getStatus().setValue(BmoSessionSale.STATUS_FINISHED);
				pmSessionSale.saveSimple(pmConn, bmoSessionSale, bmUpdateResult);
			}
		} else {
			bmoOrder.getPaymentStatus().setValue(BmoOrder.PAYMENTSTATUS_PENDING);

			// Es de tipo sesion
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
				BmoSessionSale bmoSessionSale = new BmoSessionSale();
				PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
				bmoSessionSale = (BmoSessionSale) pmSessionSale.getBy(pmConn, bmoOrder.getId(),
						bmoSessionSale.getOrderId().getName());

				// Actualizar el estatus de pago de la venta de session
				bmoSessionSale.getPaymentStatus().setValue(BmoSessionSale.PAYMENTSTATUS_PENDING);
				super.save(pmConn, bmoSessionSale, bmUpdateResult);

			}
			// Si no esta pagado totalmente, regresar a Autorizado el Credito y el Pedido
			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Obtener credito
				BmoCredit bmoCredit = new BmoCredit();
				PmCredit pmCredit = new PmCredit(getSFParams());
				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

				bmoCredit.getStatus().setValue(BmoCredit.STATUS_AUTHORIZED);
				pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);

				bmoOrder.getStatus().setValue(BmoCredit.STATUS_AUTHORIZED);
			}
			// Si no esta pagado totalmente, regresar a Autorizado el Contrato y el Pedido
			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Obtener crontrato
				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(pmConn,
						bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
					bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
				}
				// Actualiza estatus propiedad
				PmProperty pmProperty = new PmProperty(getSFParams());
				pmProperty.updateAvailability(pmConn, bmoPropertyRental, bmUpdateResult);
			}
		}
		// Copiar montos modificados a la venta de servicio
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
			if (pmConsultancy.consultancyOrderExists(pmConn, bmoOrder.getId())) {
				pmConsultancy.copyAmountsFromOrder(pmConn, bmoOrder, bmUpdateResult);
			}
		}
		// Almacena cambios
		super.save(pmConn, bmoOrder, bmUpdateResult);
	}

	// Actualiza el estatus de todos los clientes
	public void batchUpdateStatus() throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmConn.open();

		try {
			BmoOrder bmoOrder = new BmoOrder();

			BmFilter filterByAuthorized = new BmFilter();
			filterByAuthorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getStatus(),
					"" + BmoOrder.STATUS_AUTHORIZED);
			Iterator<BmObject> customerIterator = this.list(filterByAuthorized).iterator();

			while (customerIterator.hasNext()) {
				bmoOrder = (BmoOrder) customerIterator.next();

				this.updateStatus(pmConn, bmoOrder, bmUpdateResult);
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "batchUpdateStatus(): " + e.toString());
		} finally {
			pmConn.close();
		}
	}

	// Actualiza estatus del pedido-venta segun la fecha fin
	public void updateStatus(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
//		String sql = "";

		// Puede cambiar el estatus de autorizado, segun fechas
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
			
			// SE QUITO POR ORDEN aibarra
			// Determinar si tiene pedidos activos de tipo Servicio
			// TODO: SI APLICAR CONSULTORIA?
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
//				sql = " SELECT * FROM orders " + " WHERE orde_orderid = " + bmoOrder.getId() + " AND orde_lockend < '"
//						+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' ";
//				// " OR '" + SFServerUtil.nowToString(getSFParams(),
//				// getSFParams().getDateFormat()) + "' " +
//				// " BETWEEN orde_lockstart AND orde_lockend ";
//				pmConn.doFetch(sql);
//
//				if (pmConn.next()) {
//					// Venta
//					BmoConsultancy bmoConsultancy = new BmoConsultancy();
//					PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
//					bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
//					bmoConsultancy.getStatus().setValue(BmoConsultancy.STATUS_FINISHED);
//					super.save(pmConn, bmoConsultancy, bmUpdateResult);
//
//					// Pedido
//					addDataLog(pmConn, bmoOrder, "Finalizó la Vigencia de la Venta. ", bmUpdateResult);
//					bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
//					super.save(pmConn, bmoOrder, bmUpdateResult);
//					
//					// Flujo
//					BmoWFlow bmoWFlow = new BmoWFlow();
//					PmWFlow pmWFlow = new PmWFlow(getSFParams());
//					bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoConsultancy.getWFlowId().toInteger());
//					bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
//					super.save(pmConn, bmoWFlow, bmUpdateResult);
//				}
//			}
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)
			throws SFException {
		
		// Actualiza datos de la cotización
		if (bmObject.getId() > 0)
			bmoOrder = (BmoOrder) this.get(bmObject.getId());

		// Revisar cantidad de items apartados
		if (action.equals(BmoOrder.ACTION_LOCKEDQUANTITY)) {
			BmoOrderItem bmoOrderItem = new BmoOrderItem();
			bmoOrderItem.getQuantity().setValue(getLockedQuantity(bmoOrder, value));
			bmoOrderItem.getProductId().setValue(value);
			bmUpdateResult.setBmObject(bmoOrderItem);
		} else if (action.equals(BmoOrder.ACTION_ORDERITEMPENDING)) {
			BmoOrderItem bmoOrderItem = new BmoOrderItem();
			bmoOrderItem.getQuantity().setValue(orderItemPending(bmoOrder, value));
			bmoOrderItem.getProductId().setValue(value);
			bmUpdateResult.setBmObject(bmoOrderItem);
		} else if (action.equals(BmoOrder.ACTION_ORDERBALANCE)) {
			double result = getOrderBalance(bmoOrder.getId(), bmUpdateResult);
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoOrder.ACTION_QUOTESTAFFQUANTITY)) {
			double result = getQuoteStaffQuantity(bmoOrder, value);
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoOrder.ACTION_ORDERSTAFFQUANTITY)) {
			double result = getOrderStaffQuantity(bmoOrder.getId(), value);
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoOrder.ACTION_PAYORDER)) {
			bmUpdateResult = paymentSessionSale(value, bmUpdateResult);
		} else if (action.equals(BmoOrder.ACTION_CREATENEWORDER)) {
			bmUpdateResult = createNewOrder(value, bmUpdateResult);
		} else if (action.equals(BmoOrder.ACTION_TOTALREQUISITIONS)) {
			bmUpdateResult.addMsg("" + getTotalRequisitions(bmoOrder));
		} else if (action.equals(BmoOrder.ACTION_SHOWBUTTONRENEWORDER)) {
			int result = 0;
			result = orderWasRenewed(bmoOrder.getId());
			if (result < 0)
				result = 0;
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoOrder.ACTION_CREATERENEWORDER)) {
			if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
				BmoProperty bmoProperty = new BmoProperty();
				PmProperty pmProperty = new PmProperty(getSFParams());

				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				bmoPropertyRental = (BmoPropertyRental) pmPropertyRental
						.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());

				bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());

				// valida si el inmueble esta abierto y disponible
				if (bmoProperty.getCansell().toInteger() > 0 && bmoProperty.getAvailable().toInteger() > 0) {
					if (bmoProperty.getRenewOrder().toInteger() != 1)
						bmUpdateResult.addError(bmoPropertyRental.getPropertyId().getName(),
								" <b> El inmueble no está configurado para renovar. </b>");
					else
					// Revisar que no sobrepase los pedidos renovados al plazo anual
					// Si el pedido viene de un pedido anterior
					if (bmoOrder.getOriginRenewOrderId().toInteger() > 0
							&& bmoOrder.getRenewOrderId().toInteger() > 0) {
						if (countRenewOrder(bmoOrder.getOriginRenewOrderId().toInteger()) >= bmoPropertyRental
								.getContractTerm().toInteger()) {
							bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_FINISHED);
							pmPropertyRental.save(bmoPropertyRental, new BmUpdateResult());
							bmUpdateResult.addError(bmoPropertyRental.getContractTerm().getName(),
									"<b>  El inmueble ya fue renovado al limite del plazo del contrato.</b>");
						} else
							bmUpdateResult = this.manualCreateRenewOrderProperty(bmoOrder, bmUpdateResult);

					} else
						bmUpdateResult = this.manualCreateRenewOrderProperty(bmoOrder, bmUpdateResult);

				} else
					bmUpdateResult.addError(bmoPropertyRental.getPropertyId().getName(),
							"El inmueble debe estar abierto y disponible");
			} else
				bmUpdateResult = this.manualCreateRenewOrder(bmoOrder, bmUpdateResult);
		} else if (action.equals(BmoOrder.ACTION_EXTRAORDER)) {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			if (bmoOrder.getId() > 0)
				bmUpdateResult = this.createExtraOrder(pmConn, bmoOrder, bmUpdateResult);
			pmConn.close();
		} else if (action.equals(BmoOrder.ACTION_GETTAXRACCS)) {
			double sumTaxRaccs = 0;
			if (bmoOrder.getId() > 0)
				sumTaxRaccs = this.getTaxRaccs(Integer.parseInt(value));
			bmUpdateResult.setMsg("" + sumTaxRaccs);
		} else if (action.equals(BmoOrder.ACTION_GETTAXRACCSPAYMENTS)) {
			double sumTaxRaccsPayments = 0;
			if (bmoOrder.getId() > 0)
				sumTaxRaccsPayments = this.getTaxRaccsPayment(Integer.parseInt(value));
			bmUpdateResult.setMsg("" + sumTaxRaccsPayments);
		} else if (action.equals(BmoOrder.ACTION_GETTAXRACCSPENDING)) {
			double sumTaxRaccsPending = 0;
			if (bmoOrder.getId() > 0)
				sumTaxRaccsPending = this.getTaxRaccsPending(Integer.parseInt(value));
			bmUpdateResult.setMsg("" + sumTaxRaccsPending);
		} else if (action.equals(BmoOrder.ACTION_VALIDATESTATUS)) {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			int result = 0;
			if (bmoOrder.getId() > 0)
				if (hasActiveOrder(pmConn, bmoOrder)) {
					result = 1;
					bmUpdateResult.setMsg("" + result);
				}
			pmConn.close();
		} else if (action.equals(BmoOrder.ACTION_GETDATAOWNERPROPERTY)) {
			String ownerProperty = "";
			ownerProperty = this.getDataOwnerProperty(value);

			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoProperty = (BmoProperty) pmProperty.get(Integer.parseInt(value));

			bmUpdateResult.setBmObject(bmoProperty);
			bmUpdateResult.setMsg("" + ownerProperty);
		}

		else if (action.equals(BmoOrder.ACTION_GETMONTHS)) {
			bmoOrder = (BmoOrder) bmObject;
			getMonthContract(bmoOrder, bmUpdateResult);
			getMonth(bmoOrder, bmUpdateResult);
		}else if (action.equals(BmoOrder.ACTION_CREATEPROJ)) {//Crear projecto Visual
//			PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//			PmConn pmConn = new PmConn(getSFParams());
//			//Buscar si ya se creo proyecto
//			if(searchOrderProjectStep(pmConn, bmoOrder.getId())) {
//				bmUpdateResult.addError(bmoOrder.getStatus().getName(),"El pedido ya tiene un Proyecto");
//			}else {
//				pmProjectStep.createProject(pmConn, bmoOrder, bmUpdateResult);		
//				
//			}
		}else if (action.equals(BmoOrder.ACTION_GETEFFECT)) {
//			// Es de tipo Renta
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//				
//				PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//				BmoProjectStep bmoProjectStep = new BmoProjectStep();
//				try {
//					bmoProjectStep = (BmoProjectStep)pmProjectStep.getBy(bmoOrder.getId(), bmoProjectStep.getOrderId().getName());				
//					bmUpdateResult.setBmObject(bmoProjectStep);
//				}catch (Exception e) {
//					bmUpdateResult.addMsg("<b> El Pedido no tiene Proyecto</b>");
//				}
//			}
//			// Es de tipo Venta
//			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
//				
//				PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//				BmoProjectStep bmoProjectStep = new BmoProjectStep();
//				try {
//					bmoProjectStep = (BmoProjectStep)pmProjectStep.getBy(bmoOrder.getId(), bmoProjectStep.getOrderId().getName());				
//					bmUpdateResult.setBmObject(bmoProjectStep);
//				}catch (Exception e) {
//					bmUpdateResult.addMsg("<b> El Pedido no tiene Proyecto</b>");
//				}
//			}	
//			// Es de Tipo Consultoria
//			// TODO: SI APLICAR CONSULTORIA?
//			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
//				
//				PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//				BmoProjectStep bmoProjectStep = new BmoProjectStep();
//				try {
//					bmoProjectStep = (BmoProjectStep)pmProjectStep.getBy(bmoOrder.getId(), bmoProjectStep.getOrderId().getName());				
//					bmUpdateResult.setBmObject(bmoProjectStep);
//				}catch (Exception e) {
//					bmUpdateResult.addMsg("<b> El Pedido no tiene Proyecto</b>");
//				}
//			}	
//			// Es de tipo venta de Propiedad
//			else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
//				
//				PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//				BmoProjectStep bmoProjectStep = new BmoProjectStep();
//				try {
//					bmoProjectStep = (BmoProjectStep)pmProjectStep.getBy(bmoOrder.getId(), bmoProjectStep.getOrderId().getName());				
//					bmUpdateResult.setBmObject(bmoProjectStep);
//				}catch (Exception e) {
//					bmUpdateResult.addMsg("<b> El Pedido no tiene Proyecto</b>");
//				}
//			}	
//			
//			
		} else if (action.equals(BmoOrder.ACTION_DELETECONSULTANCYFROMORDER)) {
			deleteConsultancyFromOrder(bmoOrder, bmUpdateResult);
		} else if (action.equals(BmoOrder.ACTION_GETEXTRAORDER)) {		
			try {
				BmoOrder ordeExtra = (BmoOrder)this.getBy(bmoOrder.getId(), bmoOrder.getOriginRenewOrderId().getName());
				if (ordeExtra.getId() > 0)
					bmUpdateResult.setId(1);
				else
					bmUpdateResult.setId(-1);
			}catch (Exception e) {
				bmUpdateResult.setId(-1);
			}
		}
		return bmUpdateResult;
	}
		
	// Borrar consultoria desde el pedido
	public BmUpdateResult deleteConsultancyFromOrder(BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		BmoConsultancy bmoConsultancy = new BmoConsultancy();
		PmConsultancy pmConsultancy = new PmConsultancy(getSFParams());
		if (pmConsultancy.consultancyOrderExists(pmConn, bmoOrder.getId()))
			bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
		else bmUpdateResult.addError(bmoOrder.getCode().getName(), "No se encontró una Venta ligada al Pedido.");
		
		if (!bmUpdateResult.hasErrors())
			pmConsultancy.delete(pmConn, bmoConsultancy, bmUpdateResult);
		pmConn.close();

		return bmUpdateResult;
	}
	
	// Obtener suma de ivas de las cxc del pedido
	public double getTaxRaccs(int orderId) throws SFException {
		String sql = "";
		Double totalTax = 0.0;
		PmConn pmConn = new PmConn(getSFParams());
		PmCurrency pmCurrency = new PmCurrency(getSFParams());

		pmConn.open();
		// Sumar los ivas
		sql = " SELECT racc_tax, racc_currencyid, racc_currencyparity, orde_currencyid, orde_currencyparity "
				+ " FROM " + formatKind("raccounts") 
				+ " LEFT JOIN " + formatKind("orders") + " ON (orde_orderid = racc_orderid) " 
				+ " WHERE racc_orderid = " + orderId;
		pmConn.doFetch(sql);
		while (pmConn.next()) {	

			//Conversion a la moneda destino(del pedido)
		   	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		   	double parityOrigin = 0, parityDestiny = 0;
		   	currencyIdOrigin = pmConn.getInt("racc_currencyid") ;
		   	parityOrigin = pmConn.getDouble("racc_currencyparity");
		   	currencyIdDestiny = pmConn.getInt("orde_currencyid");
		   	parityDestiny = pmConn.getDouble("orde_currencyparity");

		   	// Conversion a moneda pedido
			if (currencyIdOrigin == currencyIdDestiny) {
				totalTax += pmConn.getDouble("racc_tax");
			} else {
				totalTax += pmCurrency.currencyExchange(pmConn.getDouble("racc_tax"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			}
		}
		pmConn.close();

		return totalTax;
	}

	// Obtener suma de ivas de las cxc parcial/total pagadas del pedido
	public double getTaxRaccsPayment(int orderId) throws SFException {
		String sql = "";
		double totalTax = 0.0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		PmCurrency pmCurrency = new PmCurrency(getSFParams());

//		double tax = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble();
		
		double totalRacc = 0, paymentsRacc = 0, taxRacc = 0, taxRait = 0;

		// Sumar los ivas
		sql = " SELECT racc_code, racc_total, racc_payments, racc_tax, rait_amount, racc_currencyid, racc_currencyparity, "
				+ " orde_currencyid, orde_currencyparity "
				+ " FROM " + formatKind("raccountitems") 
				+ " LEFT JOIN " + formatKind("raccounts") + " ON (racc_raccountid = rait_raccountid) " 
				+ " LEFT JOIN " + formatKind("orders") + " ON (orde_orderid = racc_orderid) " 
				+ " WHERE racc_orderid = " + orderId
				+ " AND racc_payments > 0 ";

		pmConn.doFetch(sql);
		while (pmConn.next()) {
//			printDevLog("\n --- PAGOS --- ");
//			printDevLog("clave:"+ pmConn.getString("racc_code") );
//			printDevLog("racc_mon:"+ pmConn.getInt("racc_currencyid") );
//			printDevLog("racc_paridad:"+ pmConn.getDouble("racc_currencyparity") );
//			printDevLog("racc_total:"+ pmConn.getDouble("racc_total") );
//			printDevLog("racc_pagos:"+ pmConn.getDouble("racc_payments") );
//
//			
//			printDevLog("orde_mon:"+ pmConn.getInt("orde_currencyid") );
//			printDevLog("orde_paridad:"+ pmConn.getDouble("orde_currencyparity") );
			
			taxRacc = pmConn.getDouble("racc_tax");
			
			//Conversion a la moneda destino(del pedido)
		   	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		   	double parityOrigin = 0, parityDestiny = 0;
		   	currencyIdOrigin = pmConn.getInt("racc_currencyid") ;
		   	parityOrigin = pmConn.getDouble("racc_currencyparity");
		   	currencyIdDestiny = pmConn.getInt("orde_currencyid");
		   	parityDestiny = pmConn.getDouble("orde_currencyparity");
			
		   	// Conversion a moneda pedido
			if (currencyIdOrigin == currencyIdDestiny) {
				totalRacc = pmConn.getDouble("racc_total");
			   	paymentsRacc = pmConn.getDouble("racc_payments");
			} else {
				totalRacc = pmConn.getDouble("racc_total");
			   	paymentsRacc = pmConn.getDouble("racc_payments");
				totalRacc = pmCurrency.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				paymentsRacc = pmCurrency.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			}

			// La CC tiene iva, sacar iva del item
		   	double percentage = paymentsRacc / totalRacc;
//		   	printDevLog("percentage:"+percentage);
		   	
		   	taxRait = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(taxRacc * percentage));
//		   	printDevLog("rait_tax:"+taxRait);

		   	totalTax += taxRait;
//		   	printDevLog("sum:"+totalTax);
		}
		pmConn.close();

		return totalTax;
	}
	
	// Obtener suma de ivas de las cxc parcial/total pagadas del pedido
	public double getTaxRaccsPending(int orderId) throws SFException {
		String sql = "";
		double totalTaxPending = 0.0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		PmCurrency pmCurrency = new PmCurrency(getSFParams());

//			double tax = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble();
		
		double totalRacc = 0, paymentsRacc = 0, taxRacc = 0, taxRait = 0;

		// Sumar los ivas
		sql = " SELECT racc_code, racc_total, racc_payments, racc_tax, rait_amount, racc_currencyid, racc_currencyparity, "
				+ " orde_currencyid, orde_currencyparity "
				+ " FROM " + formatKind("raccountitems") 
				+ " LEFT JOIN " + formatKind("raccounts") + " ON (racc_raccountid = rait_raccountid) " 
				+ " LEFT JOIN " + formatKind("orders") + " ON (orde_orderid = racc_orderid) " 
				+ " WHERE racc_orderid = " + orderId
				+ " AND racc_balance > 0 ";

		pmConn.doFetch(sql);
		while (pmConn.next()) {
//			printDevLog("--- PENDIENTES --- ");
//			printDevLog(" clave:"+ pmConn.getString("racc_code") );
//			printDevLog("racc_mon:"+ pmConn.getInt("racc_currencyid") );
//			printDevLog("racc_paridad:"+ pmConn.getDouble("racc_currencyparity") );
//			printDevLog("racc_total:"+ pmConn.getDouble("racc_total") );
//			printDevLog("racc_pagos:"+ pmConn.getDouble("racc_payments") );

//			printDevLog("orde_mon:"+ pmConn.getInt("orde_currencyid") );
//			printDevLog("orde_paridad:"+ pmConn.getDouble("orde_currencyparity") );
			
			taxRacc = pmConn.getDouble("racc_tax");
			
			//Conversion a la moneda destino(del pedido)
		   	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		   	double parityOrigin = 0, parityDestiny = 0;
		   	currencyIdOrigin = pmConn.getInt("racc_currencyid") ;
		   	parityOrigin = pmConn.getDouble("racc_currencyparity");
		   	currencyIdDestiny = pmConn.getInt("orde_currencyid");
		   	parityDestiny = pmConn.getDouble("orde_currencyparity");
			
		   	// Conversion a moneda pedido
			if (currencyIdOrigin == currencyIdDestiny) {
				totalRacc = pmConn.getDouble("racc_total");
			   	paymentsRacc = pmConn.getDouble("racc_payments");
			} else {
				totalRacc = pmConn.getDouble("racc_total");
			   	paymentsRacc = pmConn.getDouble("racc_payments");
				totalRacc = pmCurrency.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				paymentsRacc = pmCurrency.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			}

			// La CC tiene iva, sacar iva del item
		   	double percentage = paymentsRacc / totalRacc;
//		   	printDevLog("percentage:"+percentage);

		   	taxRait = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(taxRacc * percentage));
//		   	printDevLog("racc_tax:"+taxRait);
		   	
		   	totalTaxPending += pmConn.getDouble("racc_tax") - taxRait;
//		   	printDevLog("sum:"+totalTaxPending);
		}
		pmConn.close();

		return totalTaxPending;
	}

	// Obtener el Saldo en cuentas x cobrar del pedido
	public Double getOrderBalance(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog("Entra  getOrderBalance");
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		BmoOrder bmoOrder = (BmoOrder) get(orderId);

		double ordeTotal = bmoOrder.getTotal().toDouble();

		try {
			// Obtener los pagos ligados al cargo
			double totalPayments = 0;
			String sql = "";

			// Sumar los conceptos de banco
			sql = " SELECT * FROM bankmovconcepts "
					+ " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) "
					+ " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) "
					+ " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) "
					+ " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) "
					+ " LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " + " WHERE racc_orderid = "
					+ bmoOrder.getId() + " AND bkmt_category <> '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				if (pmConn.getDouble("bkmc_amountconverted") > 0)
					totalPayments += pmConn.getDouble("bkmc_amountconverted");
				else
					totalPayments += pmConn.getDouble("bkmc_amount");
			}

			// Sumar las devoluciones de banco
			sql = " SELECT * FROM bankmovconcepts "
					+ " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) "
					+ " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) "
					+ " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) "
					+ " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) "
					+ " LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " + " WHERE racc_orderid = "
					+ bmoOrder.getId() + " AND bkmt_category = '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				if (pmConn.getDouble("bkmc_amountconverted") > 0)
					totalPayments -= pmConn.getDouble("bkmc_amountconverted");
				else
					totalPayments -= pmConn.getDouble("bkmc_amount");
			}

			// Notas de Crédito
			sql = " SELECT * FROM raccountassignments " + " LEFT JOIN raccounts ON (rass_raccountid = racc_raccountid) "
					+ " LEFT JOIN currencies ON (racc_currencyid = cure_currencyid) "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'" + " AND ract_type = '"
					+ BmoRaccountType.TYPE_DEPOSIT + "'" + " AND rass_foreignraccountid IN ( "
					+ " SELECT racc_raccountid FROM raccounts "
					+ "	LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ "	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + "	AND racc_orderid = "
					+ bmoOrder.getId() +
					// " AND racc_currencyid = " + bmoOrder.getCurrencyId().toInteger() +
					"  ) ";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				if (pmConn.getDouble("rass_amountconverted") > 0) {
					totalPayments += pmConn.getDouble("bkmc_amountconverted");
				} else {
					totalPayments += pmConn.getDouble("rass_amount");
				}
			}

			BmoCredit bmoCredit = new BmoCredit();
			// Obtener el monto de la asignacion
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Obtener el costo del credito
				PmCredit pmCredit = new PmCredit(getSFParams());

				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

				// Restar el monto del credito de los pagos
				totalPayments -= bmoCredit.getAmount().toDouble();

			}

			// Aumentar la penalizacion si existe al total del pedido
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

				BmoRaccount bmoRaccount = new BmoRaccount();
				ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
				BmFilter filterOrder = new BmFilter();
				BmFilter filterCxCFail = new BmFilter();

				filterOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoOrder.getId());
				filterListD.add(filterOrder);
				filterCxCFail.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getFailure().getName(), "1");
				filterListD.add(filterCxCFail);

				Iterator<BmObject> listOrderRacc = new PmRaccount(getSFParams()).list(pmConn, filterListD).iterator();
				if (listOrderRacc.hasNext()) {
					bmoRaccount = (BmoRaccount) listOrderRacc.next();

					ordeTotal = ordeTotal + bmoRaccount.getTotal().toDouble();

				}
			}

			ordeTotal -= totalPayments;

		} catch (Exception e) {
			bmUpdateResult.addError(bmoOrder.getStatus().getName(), "Error " + bmUpdateResult.errorsToString());
		} finally {
			pmConn.close();
		}

		return ordeTotal;
	}

	// Compara monto de pedido vs. provisiones
	public boolean validateOrderVsRaccounts(PmConn pmConn, int orderId) throws SFException {
		boolean valid = true;
		double orderTotal = 0, raccountTotal = 0, raccountCreditNoteTotal = 0;

		// Total del pedido
		String sql = "SELECT orde_total FROM " + formatKind("orders") + " WHERE orde_orderid = " + orderId;
		pmConn.doFetch(sql);
		if (pmConn.next())
			orderTotal = pmConn.getDouble("orde_total");

		// Total de CxC cargo del pedido
		sql = "SELECT SUM(racc_total) as total FROM " + formatKind("raccounts") + " LEFT JOIN "
				+ formatKind("raccounttypes") + " ON (racc_raccounttypeid = ract_raccounttypeid) "
				+ " WHERE ract_category <> '" + BmoRaccountType.CATEGORY_CREDITNOTE + "' " + " AND racc_orderid = "
				+ orderId;
		pmConn.doFetch(sql);
		if (pmConn.next())
			raccountTotal = pmConn.getDouble("total");

		// Total de Notas de Credito
		sql = "SELECT SUM(racc_total) as total FROM " + formatKind("raccounts") + " LEFT JOIN "
				+ formatKind("raccounttypes") + " ON (racc_raccounttypeid = ract_raccounttypeid) "
				+ " WHERE ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "' " + " AND racc_orderid = "
				+ orderId;
		pmConn.doFetch(sql);
		if (pmConn.next())
			raccountCreditNoteTotal = pmConn.getDouble("total");

		// Si el Pedido es mayor a las CxC menos las Notas de Credito, regresa que NO es
		// valido
		if (orderTotal > (raccountTotal - raccountCreditNoteTotal))
			valid = false;

		return valid;
	}

	// Crear Renovacion del pedido manual desde boton
	private BmUpdateResult manualCreateRenewOrder(BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		try {
			// Crea nuevos pedidos a partir de items de productos con auto renovacion
			this.createRenewOrderProducts(pmConn, bmoOrder, bmUpdateResult);
			BmoConsultancy bmoConsultancy = new BmoConsultancy();
			PmConsultancy pmConsultancy =  new PmConsultancy(getSFParams());
			bmoConsultancy = (BmoConsultancy)pmConsultancy.getBy(pmConn, bmoOrder.getId(), bmoConsultancy.getOrderId().getName());
			bmUpdateResult.setBmObject(bmoConsultancy);

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + " - manualCreateRenewOrder(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Crear Renovacion del pedido manual desde boton
	private BmUpdateResult manualCreateRenewOrderProperty(BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		try {
			// Crea nuevos contratos a partir de items de inmueble con auto renovacion
			this.createRenewOrderPropertyRental(pmConn, bmoOrder, bmUpdateResult);
			BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
			PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
			bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(),
					bmoPropertyRental.getOrderId().getName());
			bmUpdateResult.setBmObject(bmoOrder);
			bmUpdateResult.setMsg("" + bmoPropertyRental.getId());

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + " -manualCreateRenewOrder(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Crear un pedido nuevo (flotis)
	private BmUpdateResult createNewOrder(String value, BmUpdateResult bmUpdateResult) throws SFPmException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			pmConn.disableAutoCommit();
			// obtener el cliente del pedido
			int customerId = Integer.parseInt(value);
			int salesmanId = getSFParams().getLoginInfo().getUserId();
			int companyid = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();
			int orderTypeId = 0;
			int wflowTypeId = 0;

			// Obtener el tipo de pedido
			sql = "SELECT ortp_ordertypeid FROM ordertypes " + "WHERE ortp_type = '" + BmoOrderType.TYPE_SALE + "'";
			pmConn.doFetch(sql);
			if (pmConn.next())
				orderTypeId = pmConn.getInt("ortp_ordertypeid");

			// Obtener el tipo de flujo Venta utilizando la categoria
			sql = "SELECT wfty_wflowtypeid FROM wflowtypes "
					+ "LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
					+ "LEFT JOIN programs ON (wfca_programid = prog_programid) " 
					+ "WHERE prog_code like 'ORDE'";
			pmConn.doFetch(sql);
			if (pmConn.next())
				wflowTypeId = pmConn.getInt("wfty_wflowtypeid");

			// Datos del pedido
			BmoOrder bmoNewOrder = new BmoOrder();
			bmoNewOrder.getName().setValue("Venta CL-" + customerId);
			bmoNewOrder.getCustomerId().setValue(customerId);
			bmoNewOrder.getUserId().setValue(salesmanId);
			bmoNewOrder.getCompanyId().setValue(companyid);
			bmoNewOrder.getOrderTypeId().setValue(orderTypeId);
			bmoNewOrder.getWFlowTypeId().setValue(wflowTypeId);
			bmoNewOrder.getCurrencyId()
					.setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
			bmoNewOrder.getAmount().setValue(0);
			bmoNewOrder.getDiscount().setValue(0);
			bmoNewOrder.getTaxApplies().setValue(false);
			bmoNewOrder.getTax().setValue(0);
			bmoNewOrder.getTotal().setValue(0);

			this.save(bmoNewOrder, bmUpdateResult);

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

		} catch (Exception e) {
			pmConn.rollback();
			bmUpdateResult.addMsg("Error al crear el pedido " + bmUpdateResult.errorsToString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Pagar el pedido
	private BmUpdateResult paymentSessionSale(String value, BmUpdateResult bmUpdateResult) throws SFPmException {

		StringTokenizer tabs = new StringTokenizer(value, "|");
		int orderId = 0;
		int bankAccountId = 0;
		int raccountId = 0;
		int paymentTypeId = 0;
		double amount = 0;

		while (tabs.hasMoreTokens()) {
			orderId = Integer.parseInt(tabs.nextToken());
			bankAccountId = Integer.parseInt(tabs.nextToken());
			raccountId = Integer.parseInt(tabs.nextToken());
			paymentTypeId = Integer.parseInt(tabs.nextToken());
			amount = Double.parseDouble(tabs.nextToken());
		}

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {

			pmConn.disableAutoCommit();

			// Obtener el pedido
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, orderId);

			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn, raccountId);

			// No pagar mas del monto
			if (amount <= bmoRaccount.getBalance().toDouble()) {

				// Crear el MB para pagar el pedido
				// Obtener el tipo pago en bancos
				PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
				BmoBankMovType bmoBankMovType = new BmoBankMovType();
				bmoBankMovType = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_CXC,
						bmoBankMovType.getCategory().getName());

				// Crear el Mov de Banco de Tipo Pago a Proveedor
				PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
				BmoBankMovement bmoBkmvNew = new BmoBankMovement();

				// Obtener la Cuenta de Banco desde configuración

				if (bankAccountId > 0) {

					bmoBkmvNew.getBankAccountId().setValue(bankAccountId);
					bmoBkmvNew.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
					bmoBkmvNew.getBankReference().setValue("Pago Recibido ");
					bmoBkmvNew.getDescription().setValue("Pago Recibido " + bmoOrder.getCode().toString());
					bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovType.getId());
					bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_AUTHORIZED);
					bmoBkmvNew.getInputDate()
							.setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
					bmoBkmvNew.getDueDate()
							.setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
					bmoBkmvNew.getPaymentTypeId().setValue(paymentTypeId);

					pmBkmvNew.save(pmConn, bmoBkmvNew, bmUpdateResult);

					// Crear el concepto de Banco
					PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
					BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
					bmoBankMovConcept.getBankMovementId().setValue(bmoBkmvNew.getId());
					bmoBankMovConcept.getRaccountId().setValue(raccountId);
					bmoBankMovConcept.getAmount().setValue(amount);

					bmUpdateResult = super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

					// bmUpdateResult = super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

					// Obtener la Id del concepto
					int bankMovConceptId = bmUpdateResult.getId();

					// int bankMovConceptId = bmUpdateResult.getId();

					pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);

					updatePayments(pmConn, bmoOrder, bmUpdateResult);
					if (!bmUpdateResult.hasErrors()) {
						pmConn.commit();

						// Obtener la Cuenta por Cobrar creada
						bmoBankMovConcept = (BmoBankMovConcept) pmBankMovConcept.get(bankMovConceptId);
						BmoRaccount bmoDeposit = (BmoRaccount) pmRaccount.get(pmConn,
								bmoBankMovConcept.getRaccountId().toInteger());
						bmoDeposit.getPaymentTypeId().setValue(paymentTypeId);
						pmRaccount.saveSimple(bmoDeposit, bmUpdateResult);
					}
					// bmoBankMovConcept =
					// (BmoBankMovConcept)pmBankMovConcept.get(bankMovConceptId);

					// BmoRaccount bmoDeposit = (BmoRaccount)pmRaccount.get(pmConn,
					// bmoBankMovConcept.getForeignId().toInteger());
					// bmoDeposit.getPaymentTypeId().setValue(paymentTypeId);
					// pmRaccount.saveSimple(bmoDeposit, bmUpdateResult);

					updatePayments(pmConn, bmoOrder, bmUpdateResult);

				} else {
					bmUpdateResult.addMsg("No existe una cuenta de banco definida");
				}

			} else {
				bmUpdateResult.addMsg("El monto del cobro excede el saldo");
			}

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addMsg("Error al pagar el pedido " + bmUpdateResult.errorsToString() + " " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Pagar el pedido
	private BmUpdateResult getMonth(BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFPmException {

		// Calcular meses entre fechas
		int moths = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		try {
			String lockStart = bmoOrder.getLockStart().toString();
			String lockEnd = bmoOrder.getLockEnd().toString();

			Calendar dateStart = Calendar.getInstance();
			Calendar dateEnd = Calendar.getInstance();

			dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), lockStart));
			dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), lockEnd));

			int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);

			moths = diffMonth;
			if (moths > 12) {
				bmUpdateResult.addError(bmoOrder.getLockEnd().getName(),
						"Error: la diferencia de fechas exceden a doce meses ");
			} else {
				// Actulizar f.primera renta y fecha incremento del contrato
				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());

				Date lockStarDate = SFServerUtil.stringToDate(getSFParams().getDateFormat(),
						bmoOrder.getLockStart().toString());
				Date lockEndDate = SFServerUtil.stringToDate(getSFParams().getDateFormat(),
						bmoOrder.getLockEnd().toString());

				bmoPropertyRental = (BmoPropertyRental) pmPropertyRental
						.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
				bmoPropertyRental.getRentalScheduleDate()
						.setValue(SFServerUtil.dateToString(lockStarDate, getSFParams().getDateFormat()));
				bmoPropertyRental.getRentIncrease()
						.setValue(SFServerUtil.dateToString(lockEndDate, getSFParams().getDateFormat()));
				pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);

				// Actualizar el item actualizar balance
				BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
				PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(getSFParams());
				bmoOrderPropertyTax = (BmoOrderPropertyTax) pmOrderPropertyTax.getBy(bmoOrder.getId(),
						bmoOrderPropertyTax.getOrderId().getName());
				bmoOrderPropertyTax.getQuantity().setValue(moths);
				pmOrderPropertyTax.create(pmConn, bmoOrderPropertyTax, bmoOrder, bmUpdateResult);
			}

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error al obtenner meses " + bmUpdateResult.errorsToString() + " " + e.toString());
		} finally {
			pmConn.close();
		}
		//////////
		// StringTokenizer tabs = new StringTokenizer(value, "|");
		// int orderId = 0;
		// int bankAccountId = 0;
		// int raccountId = 0;
		// int paymentTypeId = 0;
		// double amount = 0;
		//
		// while (tabs.hasMoreTokens()) {
		// orderId = Integer.parseInt(tabs.nextToken());
		// bankAccountId = Integer.parseInt(tabs.nextToken());
		// raccountId = Integer.parseInt(tabs.nextToken());
		// paymentTypeId = Integer.parseInt(tabs.nextToken());
		// amount = Double.parseDouble(tabs.nextToken());
		// }
		//
		// PmConn pmConn = new PmConn(getSFParams());
		// pmConn.open();
		//
		// try {
		//
		// pmConn.disableAutoCommit();
		//
		// //Obtener el pedido
		// PmOrder pmOrder = new PmOrder(getSFParams());
		// BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);
		//
		// PmRaccount pmRaccount = new PmRaccount(getSFParams());
		// BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, raccountId);
		//
		// //No pagar mas del monto
		// if (amount <= bmoRaccount.getBalance().toDouble()) {
		//
		// //Crear el MB para pagar el pedido
		// // Obtener el tipo pago en bancos
		// PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		// BmoBankMovType bmoBankMovType = new BmoBankMovType();
		// bmoBankMovType = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" +
		// BmoBankMovType.CATEGORY_CXC, bmoBankMovType.getCategory().getName());
		//
		// //Crear el Mov de Banco de Tipo Pago a Proveedor
		// PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
		// BmoBankMovement bmoBkmvNew = new BmoBankMovement();
		//
		// //Obtener la Cuenta de Banco desde configuración
		//
		// if (bankAccountId > 0) {
		//
		// bmoBkmvNew.getBankAccountId().setValue(bankAccountId);
		// bmoBkmvNew.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
		// bmoBkmvNew.getBankReference().setValue("Pago Recibido " );
		// bmoBkmvNew.getDescription().setValue("Pago Recibido " +
		// bmoOrder.getCode().toString());
		// bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovType.getId());
		// bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_AUTHORIZED);
		// bmoBkmvNew.getInputDate().setValue(SFServerUtil.nowToString(getSFParams(),
		// getSFParams().getDateFormat()));
		// bmoBkmvNew.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(),
		// getSFParams().getDateFormat()));
		// bmoBkmvNew.getPaymentTypeId().setValue(paymentTypeId);
		//
		// pmBkmvNew.save(pmConn, bmoBkmvNew, bmUpdateResult);
		//
		// //Crear el concepto de Banco
		// PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
		// BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		// bmoBankMovConcept.getBankMovementId().setValue(bmoBkmvNew.getId());
		// bmoBankMovConcept.getRaccountId().setValue(raccountId);
		// bmoBankMovConcept.getAmount().setValue(amount);
		//
		// bmUpdateResult = super.save(pmConn, bmoBankMovConcept, bmUpdateResult);
		//
		// //bmUpdateResult = super.save(pmConn, bmoBankMovConcept, bmUpdateResult);
		//
		// //Obtener la Id del concepto
		// int bankMovConceptId = bmUpdateResult.getId();
		//
		// //int bankMovConceptId = bmUpdateResult.getId();
		//
		// pmBankMovConcept.save(pmConn, bmoBankMovConcept,bmUpdateResult);
		//
		// updatePayments(pmConn, bmoOrder, bmUpdateResult);
		// if (!bmUpdateResult.hasErrors()) {
		// pmConn.commit();
		//
		// //Obtener la Cuenta por Cobrar creada
		// bmoBankMovConcept =
		// (BmoBankMovConcept)pmBankMovConcept.get(bankMovConceptId);
		// BmoRaccount bmoDeposit = (BmoRaccount)pmRaccount.get(pmConn,
		// bmoBankMovConcept.getRaccountId().toInteger());
		// bmoDeposit.getPaymentTypeId().setValue(paymentTypeId);
		// pmRaccount.saveSimple(bmoDeposit, bmUpdateResult);
		// }
		// //bmoBankMovConcept =
		// (BmoBankMovConcept)pmBankMovConcept.get(bankMovConceptId);
		//
		// //BmoRaccount bmoDeposit = (BmoRaccount)pmRaccount.get(pmConn,
		// bmoBankMovConcept.getForeignId().toInteger());
		// //bmoDeposit.getPaymentTypeId().setValue(paymentTypeId);
		// //pmRaccount.saveSimple(bmoDeposit, bmUpdateResult);
		//
		// updatePayments(pmConn, bmoOrder, bmUpdateResult);
		//
		// } else {
		// bmUpdateResult.addMsg("No existe una cuenta de banco definida");
		// }
		//
		// } else {
		// bmUpdateResult.addMsg("El monto del cobro excede el saldo");
		// }
		//
		//
		// } catch (SFException e) {
		// pmConn.rollback();
		// bmUpdateResult.addMsg("Error al pagar el pedido " +
		// bmUpdateResult.errorsToString() + " " + e.toString());
		// } finally {
		// pmConn.close();
		// }
		
		return bmUpdateResult;
	}

	// Obtener utilizacion en intervalo de fechas
	private double getLockedQuantity(BmoOrder bmoOrder, String productId) throws SFException {
		double lockedQuantity = 0;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		// Revisar asignaciones en las fechas establecidas de los items en pedidos
		String lockedSql = "SELECT SUM(ordi_quantity) FROM orderitems"
				+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) "
				+ " LEFT JOIN orders ON (ordg_orderid = orde_orderid)" 
				+ " WHERE ordi_productid = " + productId
				+ " AND (	('" + bmoOrder.getLockStart().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				+ "	OR ('" + bmoOrder.getLockEnd().toString() + "' BETWEEN orde_lockstart AND orde_lockend)	)" 
				+ " AND orde_companyid = " + bmoOrder.getCompanyId().toInteger();
		pmConn.doFetch(lockedSql);
		pmConn.next();
		lockedQuantity = FlexUtil.roundDouble(pmConn.getDouble(1), 4);
		pmConn.close();

		return lockedQuantity;
	}

	// Obtener los items de un pedido que tengan un producto seleccionado
	private double hasOrderItemWithProduct(PmConn pmConn, BmoOrder bmoOrder) throws SFException {
		double orderQuantity = 0;

		// Obtener total de items de el producto en el pedido
		String orderQuantitySql = "SELECT SUM(ordi_quantity) FROM orderitems"
				+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) "
				+ " LEFT JOIN orders ON (ordg_orderid = orde_orderid) " + " WHERE ordg_orderid = " + bmoOrder.getId()
				+ " AND ordi_productid > 0";
		pmConn.doFetch(orderQuantitySql);
		if (pmConn.next())
			orderQuantity = FlexUtil.roundDouble(pmConn.getDouble(1), 4);

		return orderQuantity;
	}

	// Obtener los items de un pedido faltanes de entrega
	private double orderItemPending(BmoOrder bmoOrder, String productId) throws SFException {
		double orderQuantity = 0, quantityDelivered = 0, quantityPending = 0;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		// Obtener total de items de el producto en el pedido
		String orderQuantitySql = "SELECT SUM(ordi_quantity) FROM orderitems"
				+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) "
				+ " LEFT JOIN orders ON (ordg_orderid = orde_orderid)" + " WHERE " + " ordi_productid = " + productId
				+ " AND ordg_orderid = " + bmoOrder.getId();
		pmConn.doFetch(orderQuantitySql);
		if (pmConn.next())
			orderQuantity = pmConn.getDouble(1);

		// Obtener total de surtido de un producto
		String orderDeliveredSql = "SELECT SUM(whmi_quantity) FROM whmovitems "
				+ " LEFT JOIN whmovements ON (whmi_whmovementid = whmv_whmovementid) "
				+ " LEFT JOIN orders ON (whmv_orderid = orde_orderid) " + " WHERE " + " whmi_productid = " + productId
				+ " AND (whmv_type = '" + BmoWhMovement.TYPE_OUT + "' " + " OR whmv_type = '"
				+ BmoWhMovement.TYPE_RENTAL_OUT + "') " + " AND orde_orderid = " + bmoOrder.getId();
		pmConn.doFetch(orderDeliveredSql);
		if (pmConn.next())
			quantityDelivered = pmConn.getDouble(1);
		pmConn.close();

		// Calculo de faltante
		quantityPending = orderQuantity - quantityDelivered;

		return quantityPending;
	}

	// Obtener el Saldo en cuentas x cobrar del pedido
	public Double getOrderBalance(int orderId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		double ordeTotal = 0;

		BmoOrder bmoOrder = (BmoOrder) this.get(orderId);

		try {
			// Obtener los pagos ligados al cargo
			double totalPayments = 0;
			String sql = "";

			// Pagos con la moneda del pedido
			sql = " SELECT SUM(racc_total) AS totalpayments FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT
					+ "'" + " AND racc_currencyid = " + bmoOrder.getCurrencyId().toInteger();
			// " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_REVISION + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				totalPayments = pmConn.getDouble("totalpayments");
			}

			double amountDiffCure = 0;
			double orderParity = 0;

			if (bmoOrder.getCurrencyParity().toDouble() > 0)
				orderParity = bmoOrder.getCurrencyParity().toDouble();
			else
				orderParity = bmoOrder.getBmoCurrency().getParity().toDouble();

			// Pagos con diferente moneda del pedido
			sql = " SELECT * FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT
					+ "'" +
					// " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_REVISION + "'" +
					" AND racc_currencyid <> " + bmoOrder.getCurrencyId().toInteger();
			pmConn.doFetch(sql);
			while (pmConn.next()) {

				// Covertir a la moneda
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency) pmCurrency.get(pmConn.getInt("racc_currencyid"));

				if (pmConn.getDouble("racc_currencyparity") > 0)
					amountDiffCure += (pmConn.getDouble("racc_total") * pmConn.getDouble("racc_currencyparity"))
							/ orderParity;
				else
					amountDiffCure += (pmConn.getDouble("racc_total") * bmoCurrency.getParity().toDouble())
							/ orderParity;

				totalPayments += amountDiffCure;
			}

			BmoCredit bmoCredit = new BmoCredit();
			// Obtener el monto de la asignacion
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Obtener el costo del credito
				PmCredit pmCredit = new PmCredit(getSFParams());

				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

				// Restar el monto del credito de los pagos
				totalPayments -= bmoCredit.getAmount().toDouble();

			}

			ordeTotal = bmoOrder.getTotal().toDouble();

			// Aumentar la penalizacion si existe al total del pedido
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

				BmoRaccount bmoRaccount = new BmoRaccount();
				ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
				BmFilter filterOrder = new BmFilter();
				BmFilter filterCxCFail = new BmFilter();

				filterOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoOrder.getId());
				filterListD.add(filterOrder);
				filterCxCFail.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getFailure().getName(), "1");
				filterListD.add(filterCxCFail);

				Iterator<BmObject> listOrderRacc = new PmRaccount(getSFParams()).list(pmConn, filterListD).iterator();
				if (listOrderRacc.hasNext()) {
					bmoRaccount = (BmoRaccount) listOrderRacc.next();

					ordeTotal = ordeTotal + bmoRaccount.getTotal().toDouble();

				}
			}

			ordeTotal -= totalPayments;

		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " - getOrderBalance() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return ordeTotal;
	}

	// Obtener el Saldo en cuentas x cobrar del pedido
	private int getQuoteStaffQuantity(BmoOrder bmoOrder, String profileId) throws SFException {
		int quantity = 0;

		// Revisar si la orden proviene de cotizacion
		if (bmoOrder.getQuoteId().toInteger() > 0) {
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = new BmoQuote();
			bmoQuote = (BmoQuote) pmQuote.get(bmoOrder.getQuoteId().toInteger());

			quantity = pmQuote.getQuoteStaffQuantity(bmoQuote.getId(), profileId);
		}
		return quantity;
	}

	private int getOrderStaffQuantity(int orderId, String profileId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		int quantity = 0;

		try {
			pmConn.open();

			quantity = getOrderStaffQuantity(pmConn, orderId, profileId);

		} catch (SFException e) {
			System.out.println(this.getClass().getName() + " - orderStaffQuantity() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}
		return quantity;
	}

	// Obtene la cantidad de staff de un grupo de un pedido
	private int getOrderStaffQuantity(PmConn pmConn, int orderId, String profileId) throws SFException {
		int quantity = 0;

		String sql = "SELECT SUM(ords_quantity) FROM orderstaff "
				+ " LEFT JOIN profiles ON (prof_profileid = ords_profileid) " + " WHERE ords_orderid = " + orderId + " "
				+ " AND ords_profileid = " + profileId;
		pmConn.doFetch(sql);

		if (pmConn.next())
			quantity = pmConn.getInt(1);

		return quantity;
	}

	public boolean orderOpportunityExists(PmConn pmConn, int opportunityId) throws SFPmException {
		pmConn.doFetch("SELECT orde_opportunityid FROM orders WHERE orde_opportunityid = " + opportunityId);
		return pmConn.next();
	}

	// Obtener de la etapa el presupuesto
	public int getBudgetItemByOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		int budgetItemId = -1;
		int budgetId = 0;
		String sql = "";

		// Si es de tipo propiedad, obtiene el item del budget del presupuesto de la
		// etapa de desarrollo
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			sql = " SELECT dvph_budgetid FROM propertysales "
					+ " LEFT JOIN properties ON (prsa_propertyid = prty_propertyid) "
					+ " LEFT JOIN developmentblocks ON(dvbl_developmentblockid = prty_developmentblockid) "
					+ " LEFT JOIN developmentphases ON(dvph_developmentphaseid = dvbl_developmentphaseid) "
					+ " WHERE prsa_orderid = " + bmoOrder.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				budgetId = pmConn.getInt("dvph_budgetid");

			// Obtener la partida de ingresos
			sql = " SELECT bgit_budgetitemid FROM budgetitems " + " WHERE bgit_budgetid = " + budgetId
					+ " AND bgit_budgetitemtypeid = "
					+ ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger();
			pmConn.doFetch(sql);
			if (pmConn.next())
				budgetItemId = pmConn.getInt("bgit_budgetitemid");
		} else {
			// Obtiene la partida default del sistema
			// int defaultBudgetItemTypeId =
			// ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger();
			sql = " SELECT ortp_defaultbudgetitemid FROM ordertypes " + " WHERE ortp_ordertypeid = "
					+ bmoOrder.getOrderTypeId().toInteger();
			pmConn.doFetch(sql);
			if (pmConn.next())
				budgetItemId = pmConn.getInt("ortp_defaultbudgetitemid");
		}
		return budgetItemId;
	}

	// Obtiene total de OC ligadas al pedido
	private double getTotalRequisitions(BmoOrder bmoOrder) throws SFException {
		double totalRequisitions = 0;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		// Revisar asignaciones en las fechas establecidas de los items en pedidos
		String sql = "SELECT SUM(reqi_total) FROM requisitions" + " WHERE " + " reqi_orderid = " + bmoOrder.getId();
		if (!getSFParams().isProduction())
			System.out.println(sql);
		pmConn.doFetch(sql);
		if (pmConn.next())
			totalRequisitions = pmConn.getDouble(1);
		pmConn.close();

		return totalRequisitions;
	}

	// Obtener el Propiertario del inmueble del pedido
	public String getDataOwnerProperty(String propertyId) throws SFException {
		String sql = "", customer = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		// Sumar los ivas
		sql = " SELECT cust_code, cust_customertype, cust_displayname, cust_legalname " + " FROM "
				+ formatKind("properties") + " LEFT JOIN " + formatKind("customers")
				+ " ON (cust_customerid = prty_customerid) " + " WHERE prty_propertyid = "
				+ Integer.parseInt(propertyId);
		printDevLog("sql_getDataOwnerProperty: " + sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			if (pmConn.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
				customer = pmConn.getString("cust_code") + " " + pmConn.getString("cust_legalname");
			else
				customer = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
		}
		pmConn.close();

		return customer;
	}

	// Agrega bitacora
	private void addDataLog(BmoOrder bmoOrder, String comment, BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		// Moneda con la que se esta autorizando
		BmoCurrency bmoCurrencyAuthorized = new BmoCurrency();
		PmCurrency pmCurrencyAuthorized = new PmCurrency(getSFParams());
		bmoCurrencyAuthorized = (BmoCurrency) pmCurrencyAuthorized.get(bmoOrder.getCurrencyId().toInteger());

		// Formato de Pedido por defecto
		BmoFormat bmoFormat = new BmoFormat();
		PmFormat pmFormat = new PmFormat(getSFParams());
		String link = "";
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDefaultFormatOrder().toInteger() > 0) {
			bmoFormat = (BmoFormat) pmFormat
					.get(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDefaultFormatOrder().toInteger());
			link = bmoFormat.getLink().toString();
		} else
			link = "/frm/flex_baseorder.jsp";

		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
			// Obtener html del formato

			String url = GwtUtil.getProperUrl(getSFParams(), link + "?h=" + new Date().getTime() + "format&w=EXT&z="
					+ GwtUtil.encryptId(bmoOrder.getId()) + "&resource=oppo" + (new Date().getTime() * 456)
					+ "&log=true");

			printDevLog("PmOrder-addDataLog()-getAppUrl(): " + getSFParams().getAppURL());
			printDevLog("PmOrder-addDataLog()-getAppUrl(): URL: " + url);

//			String data = SFServerUtil.fetchUrlToString(url);
			String data = "";
			pmWFlowLog.addDataLog(bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA,
					comment + ", Valor Total: " + SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) + " "
							+ bmoCurrencyAuthorized.getCode().toString(),
					data);
		} else {
			pmWFlowLog.addDataLog(bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, comment, "");
		}
	}

	// Agrega bitacora
	public void addDataLog(PmConn pmConn, BmoOrder bmoOrder, String comment, BmUpdateResult bmUpdateResult)
			throws SFException {
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		// Moneda con la que se esta autorizando
		BmoCurrency bmoCurrencyAuthorized = new BmoCurrency();
		PmCurrency pmCurrencyAuthorized = new PmCurrency(getSFParams());
		bmoCurrencyAuthorized = (BmoCurrency) pmCurrencyAuthorized.get(bmoOrder.getCurrencyId().toInteger());

		// Formato de Pedido por defecto
		BmoFormat bmoFormat = new BmoFormat();
		PmFormat pmFormat = new PmFormat(getSFParams());
		String link = "";
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDefaultFormatOrder().toInteger() > 0) {
			bmoFormat = (BmoFormat) pmFormat
					.get(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDefaultFormatOrder().toInteger());
			link = bmoFormat.getLink().toString();
		} else
			link = "/frm/flex_baseorder.jsp";

		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
			String url = GwtUtil.getProperUrl(getSFParams(), link + "?h=" + new Date().getTime() + "format&w=EXT&z="
					+ GwtUtil.encryptId(bmoOrder.getId()) + "&resource=oppo" + (new Date().getTime() * 456)
					+ "&log=true");

			printDevLog("PmOrder-addDataLog()-getAppUrl(): " + getSFParams().getAppURL());
			printDevLog("PmOrder-addDataLog()-getAppUrl(): URL: " + url);

			String data = SFServerUtil.fetchUrlToString(url);

			pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA,
					comment + ", Valor Total: " + SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) + " "
							+ bmoCurrencyAuthorized.getCode().toString(),
					data);
		} else {
			pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, comment, "");
		}
	}

	public String getParityFromCurrency(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmCurrency pmCurrency = new PmCurrency(getSFParams());

		return pmCurrency.getParityFromCurrency(value, bmUpdateResult);
	}

	// Enviar recordatorios al grupo, al finalizar un pedido
	public void prepareRemindersOrderEnd() throws SFException {

		BmoOrder bmoOrder = new BmoOrder();
		ArrayList<BmFilter> filterOrders = new ArrayList<BmFilter>();

		// Obtener las fechas del pedido dias antes del fin del pedido, que tengan
		// activada la notificación
		BmFilter filterEmailReminder = new BmFilter();
		filterEmailReminder.setValueFilter(bmoOrder.getKind(),
				bmoOrder.getBmoOrderType().getEmailRemindersOrderEnd().getName(), 1);
		filterOrders.add(filterEmailReminder);

		// Mandar a solo pedidos En revision y Autorizadas
		BmFilter filterOrderStatus = new BmFilter();
		filterOrderStatus.setValueOrFilter(bmoOrder.getKind(), bmoOrder.getStatus().getName(),
				"" + BmoOrder.STATUS_REVISION, "" + BmoOrder.STATUS_AUTHORIZED);
		filterOrders.add(filterOrderStatus);

		PmOrder pmOrder = new PmOrder(getSFParams());
		Iterator<BmObject> orderIterator = pmOrder.list(filterOrders).iterator();
		while (orderIterator.hasNext()) {
			bmoOrder = (BmoOrder) orderIterator.next();

			// Si no tiene fecha fin de pedido, no mandar nada
			if (!bmoOrder.getLockEnd().toString().equalsIgnoreCase("")) {

				// Vendedor del pedido
				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(getSFParams());
				bmoUser = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());

				// Se quitan los dias al pedido
				String previousDate = SFServerUtil.addDays(getSFParams().getDateFormat(),
						bmoOrder.getLockEnd().toString(),
						-bmoOrder.getBmoOrderType().getRemindDaysBeforeEndOrder().toInteger());

				// Fecha previa de fin de pedido
				Calendar calDatePrev = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), previousDate);
				Calendar calDatePrevTwo = null;

				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					String previousDateTwo = SFServerUtil.addDays(getSFParams().getDateFormat(),
							bmoOrder.getLockEnd().toString(),
							-bmoOrder.getBmoOrderType().getRemindDaysBeforeEndOrderTwo().toInteger());
					calDatePrevTwo = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), previousDateTwo);
				}
				// Fecha fin de pedido
				Calendar calSaleDate = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
						bmoOrder.getLockEnd().toString());

				// Fecha actual
				Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));

				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					if (calDatePrev.equals(calNow) || calDatePrevTwo.equals(calNow)) {

						String comments = "";
						sendMailRemindersOrderEnd(bmoOrder, bmoUser, comments);
					}
				} else {
					// Valida que la fecha previa y fecha fin de pedido sea igual a hoy
					if (calDatePrev.equals(calNow) || calSaleDate.equals(calNow)) {

						String comments = "";
						sendMailRemindersOrderEnd(bmoOrder, bmoUser, comments);
					}
				}
			}
		}
	}

	public void sendMailReminderOrderStart(PmConn pmConn, BmoOrder bmoOrder) throws SFException {

		if (!getSFParams().isProduction())
			System.out.println("Enviar correo Inicio Pedido: " + bmoOrder.getCode().toString());

		// Tipo de Flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		bmoWFlowType = (BmoWFlowType) pmWFlowType.get(bmoOrder.getWFlowTypeId().toInteger());
		
		// Vendedor
		BmoOrderType bmoOrderType = new BmoOrderType();
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoOrderType = (BmoOrderType)pmOrderType.get(bmoOrder.getOrderTypeId().toInteger());

		// Vendedor
		BmoUser bmoSalesman = new BmoUser();
		PmUser pmUser = new PmUser(getSFParams());
		bmoSalesman = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());

		// Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		bmoCustomer = (BmoCustomer) pmCustomer.get(bmoOrder.getCustomerId().toInteger());

		// Lista de correos de los usuarios del Flujo del Pedido, que se le va a enviar
		// notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		mailList = getWFlowUsersMailList(pmConn, bmoOrder.getWFlowId().toInteger());
		
		// Quitar emails repetidos
		ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
		for (SFMailAddress event : mailList) {
		    boolean isFound = false;
		    // Revisar si el email existe en noRepeat
		    for (SFMailAddress e : mailListNoRepeat) {
		        if (e.getEmail().equals(event.getEmail()) || (e.equals(event))) {
		            isFound = true;        
		            break;
		        }
		    }
		    // Si no encontro ninguno añadirlo a la nueva lista
		    if (!isFound) mailListNoRepeat.add(event);
		}
		
		String typeIndustry = "Pedido";
		if (bmoOrderType.getType().toChar() == BmoOrderType.TYPE_PROPERTY) {
			typeIndustry = getSFParams().getProgramFormTitle(new BmoPropertySale());
		}

		String subject = getSFParams().getAppCode() + " Recordatorio de Inicio de " + typeIndustry + ": "
				+ bmoOrder.getCode().toString() + " " + bmoOrder.getName().toString() + " ("
				+ bmoCustomer.getCode().toString() + " " + bmoCustomer.getDisplayName().toString() + ")";

		String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio de Inicio de " + typeIndustry + "",
				" 	<p style=\"font-size:12px\"> " + " <b>" + typeIndustry + ":</b> " + bmoOrder.getCode().toString() + " "
						+ bmoOrder.getName().toString() + " <br> " + "	<b>Tipo de Flujo:</b> "
						+ bmoWFlowType.getName().toString() + "	<br> " + "	<b>Fechas:</b> "
						+ ((!bmoOrder.getLockStart().toString().equals(""))
								? SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
										getSFParams().getBmoSFConfig().getPrintDateTimeFormat().toString(),
										bmoOrder.getLockStart().toString())
								: "")
						+ " - "
						+ ((!bmoOrder.getLockEnd().toString().equals(""))
								? SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
										getSFParams().getBmoSFConfig().getPrintDateTimeFormat().toString(),
										bmoOrder.getLockEnd().toString())
								: "")
						+ "	<br> " + "	<b>Cliente:</b> " + bmoCustomer.getCode().toString() + " "
						+ bmoCustomer.getDisplayName().toString() + " "
						+ ((bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY))
								? " (" + bmoCustomer.getLegalname().toString() + ")"
								: "")
						+ "	<br> " + "	<b>Correo del cliente:</b> " + bmoCustomer.getEmail().toString() + "	<br> "
						+ "	<b>Tel./M&oacute;vil del cliente:</b> " + bmoCustomer.getPhone().toString() + " / "
						+ bmoCustomer.getMobile().toString() + "	<br> " + "	<b>Vendedor:</b> "
						+ bmoSalesman.getFirstname().toString() + " " + bmoSalesman.getFatherlastname().toString() + " "
						+ bmoSalesman.getMotherlastname().toString()
						+ "	<p align=\"center\" style=\"font-size:12px\"> "
						+ "		Favor de dar Seguimiento a " + typeIndustry + " <a target=\"_blank\" href=\""
						+ GwtUtil.getProperUrl(getSFParams(), "start.jsp?startprogram=" + bmoOrder.getProgramCode()
						+ "&foreignid=" + bmoOrder.getId()) + "\">Aqu&iacute;</a>. "
						// + getSFParams().getAppURL() + "symgf_start.jsp" + "\">Aqu&iacute;</a>. "
						+ "	</p> ");

		SFSendMail.send(getSFParams(), mailListNoRepeat, getSFParams().getBmoSFConfig().getEmail().toString(),
				getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);

	}

	// Enviar recordatorios al vendedor, al vencer el contrato
	public void prepareRemindersRentIncrease() throws SFException {

		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		ArrayList<BmFilter> filterPropertyRental = new ArrayList<BmFilter>();

		// Obtener las fechas del contrato dias antes del fin del pedido, que tengan
		// activada la notificación
		BmFilter filterEmailReminder = new BmFilter();
		filterEmailReminder.setValueFilter(bmoPropertyRental.getKind(),
				bmoPropertyRental.getBmoOrderType().getEmailRemindersOrderEnd().getName(), 1);
		filterPropertyRental.add(filterEmailReminder);

		// Mandar a solo pedidos En revision y Autorizadas
		BmFilter filterPropertyRentalStatus = new BmFilter();
		filterPropertyRentalStatus.setValueOrFilter(bmoPropertyRental.getKind(),
				bmoPropertyRental.getStatus().getName(), "" + BmoPropertyRental.STATUS_REVISION,
				"" + BmoPropertyRental.STATUS_AUTHORIZED);
		filterPropertyRental.add(filterPropertyRentalStatus);

		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		Iterator<BmObject> propertyRentalIterator = pmPropertyRental.list(filterPropertyRental).iterator();
		while (propertyRentalIterator.hasNext()) {
			bmoPropertyRental = (BmoPropertyRental) propertyRentalIterator.next();

			// Si no tiene fecha fin de pedido, no mandar nada
			if (!bmoPropertyRental.getRentIncrease().toString().equalsIgnoreCase("")) {
				if ((!bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncrease().toString().equals(""))
						|| (!bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncreaseTwo().toString().equals(""))
						|| (!bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDateThree().toString().equals(""))) {
					// Vendedor del pedido
					BmoUser bmoUser = new BmoUser();
					PmUser pmUser = new PmUser(getSFParams());
					bmoUser = (BmoUser) pmUser.get(bmoPropertyRental.getUserId().toInteger());
					String Calnow = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
					//Dias entre 2 fechas
					int previousDate =  SFServerUtil.daysBetween(getSFParams().getDateFormat(), Calnow, bmoPropertyRental.getRentIncrease()
							.toString());
					
					if ((previousDate == bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncrease().toInteger())
							|| previousDate == bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncreaseTwo().toInteger()
							|| previousDate == bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDateThree().toInteger()) {
						String comments = "";
						sendMailRemindersContractEnd(bmoPropertyRental, bmoUser, comments);
					}
					
					//Se desfasa por un día revizar codigo
					// Se quitan los dias al pedido
//					String previousDate = SFServerUtil.addDays(getSFParams().getDateFormat(),
//							bmoPropertyRental.getRentIncrease().toString(),
//							-bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncrease().toInteger());
//
//					String previousDateTwo = SFServerUtil.addDays(getSFParams().getDateFormat(),
//							bmoPropertyRental.getRentIncrease().toString(),
//							-bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeRentIncreaseTwo().toInteger());
//
//					// Fecha previa de fin de pedido
//					Calendar calDatePrev = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), previousDate);
//					Calendar calDatePrevTwo = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
//							previousDateTwo);
//
//					// Fecha actual
//					Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
//							SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
//
//					// Valida que la fecha previa y fecha fin de pedido sea igual a hoy
//					if (calDatePrev.equals(calNow) || calDatePrevTwo.equals(calNow)) {
//						String comments = "";
//						sendMailRemindersContractEnd(bmoPropertyRental, bmoUser, comments);
//					}
				}
			}
		}
	}

	public void sendMailRemindersContractEnd(BmoPropertyRental bmoPropertyRental, BmoUser bmoSalesman, String comments)
			throws SFException {
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		if (bmoSalesman.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			//Arrendador
			bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertyRental.getBmoProperty().getCustomerId().toInteger());
			// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar
			
			// notificacion
			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

			mailList.add(new SFMailAddress(bmoSalesman.getEmail().toString(),
					bmoSalesman.getFirstname().toString() + " " + bmoSalesman.getFatherlastname().toString()));

			String subject = getSFParams().getAppCode() + " Recordatorio de Incremento de Renta: "
					+ bmoPropertyRental.getCode().toString() + " " + bmoPropertyRental.getName().toString() + " ("
					+ bmoPropertyRental.getBmoCustomer().getCode().toString() + " "
					+ bmoPropertyRental.getBmoCustomer().getDisplayName().toString() + ")";

			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio de Incremento de Renta",
					" 	<p style=\"font-size:12px\"> " 
							+ " <b>Contrato:</b> " + bmoPropertyRental.getCode().toString()
							+ " " + bmoPropertyRental.getName().toString() 
							+ " <br> " + " <b>Arrendador:</b> " + bmoCustomer.getDisplayName().toString()
							+ " <br> "
							+ " <br> " + "	<b>Fecha Incremento:</b> "
							+ ((!bmoPropertyRental.getRentIncrease().toString().equals(""))
									? SFServerUtil.formatDate(getSFParams(), getSFParams().getDateFormat(),
											getSFParams().getBmoSFConfig().getPrintDateFormat().toString(),
											bmoPropertyRental.getRentIncrease().toString())
									: "")
							
							+ "	<br> " + "	<b>Arrendatario:</b> "
							+ bmoPropertyRental.getBmoCustomer().getCode().toString() + " "
							+ bmoPropertyRental.getBmoCustomer().getDisplayName().toString() + " "
							+ ((bmoPropertyRental.getBmoCustomer().getCustomertype().equals(BmoCustomer.TYPE_COMPANY))
									? " (" + bmoPropertyRental.getBmoCustomer().getLegalname().toString() + ")"
									: "")
							+ "	<br> " + "	<b>Correo del Arrendatario:</b> "
							+ bmoPropertyRental.getBmoCustomer().getEmail().toString() + "	<br> "
							+ "	<b>Tel./M&oacute;vil del cliente:</b> "
							+ bmoPropertyRental.getBmoCustomer().getPhone().toString() + " / "
							+ bmoPropertyRental.getBmoCustomer().getMobile().toString() + "	<br> ");

			SFSendMail.send(getSFParams(), mailList, getSFParams().getBmoSFConfig().getEmail().toString(),
					getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
		}
	}

	public void sendMailRemindersOrderEnd(BmoOrder bmoOrder, BmoUser bmoSalesman, String comments) throws SFException {
		// Si esta activo el usuario prepara info para enviar por correo
		if (bmoSalesman.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {

			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				mailList.add(new SFMailAddress(bmoSalesman.getEmail().toString(),
						bmoSalesman.getFirstname().toString() + " " + bmoSalesman.getFatherlastname().toString()));
			} else {
				// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar
				// notificacion
				mailList = getGroupMailList(bmoOrder.getWFlowId().toInteger(),
						bmoOrder.getBmoOrderType().getProfileId().toInteger());
			}
			String subject = getSFParams().getAppCode() + " Recordatorio de Fin del Pedido: "
					+ bmoOrder.getCode().toString() + " " + bmoOrder.getName().toString() + " ("
					+ bmoOrder.getBmoCustomer().getCode().toString() + " "
					+ bmoOrder.getBmoCustomer().getDisplayName().toString() + ")";
			String msgBody = "";
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				msgBody = HtmlUtil
						.mailBodyFormat(getSFParams(), "Recordatorio de Fin del Pedido",
								" 	<p style=\"font-size:12px\"> " + " <b>Contrato:</b> "
										+ bmoOrder.getCode().toString() + " " + bmoOrder.getName().toString() + " <br> "
										+ "	<b>Fechas:</b> "
										+ ((!bmoOrder.getLockStart().toString().equals("")) ? SFServerUtil
												.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
														getSFParams().getBmoSFConfig().getPrintDateTimeFormat()
																.toString(),
														bmoOrder.getLockStart().toString())
												: "")
										+ " - "
										+ ((!bmoOrder.getLockEnd().toString().equals(""))
												? SFServerUtil.formatDate(getSFParams(),
														getSFParams().getDateTimeFormat(),
														getSFParams()
																.getBmoSFConfig().getPrintDateTimeFormat().toString(),
														bmoOrder.getLockEnd().toString())
												: "")
										+ "	<br> " + "	<b>Cliente:</b> "
										+ bmoOrder.getBmoCustomer().getCode().toString() + " "
										+ bmoOrder.getBmoCustomer().getDisplayName().toString() + " "
										+ ((bmoOrder.getBmoCustomer().getCustomertype()
												.equals(BmoCustomer.TYPE_COMPANY))
														? " (" + bmoOrder.getBmoCustomer().getLegalname().toString()
																+ ")"
														: "")
										+ "	<br> " + "	<b>Correo del cliente:</b> "
										+ bmoOrder.getBmoCustomer().getEmail().toString() + "	<br> "
										+ "	<b>Tel./M&oacute;vil del cliente:</b> "
										+ bmoOrder.getBmoCustomer().getPhone().toString() + " / "
										+ bmoOrder.getBmoCustomer().getMobile().toString() + "	<br> "
										+ "	<p align=\"center\" style=\"font-size:12px\"> "
										+ "		Favor de dar Seguimiento al Pedido <a target=\"_blank\" href=\""
										+ GwtUtil.getProperUrl(getSFParams(), "start.jsp?startprogram="
										+ bmoOrder.getProgramCode() + "&foreignid=" + bmoOrder.getId())
										+ "\">Aqu&iacute;</a>. "
										// + getSFParams().getAppURL() + "symgf_start.jsp" + "\">Aqu&iacute;</a>. "
										+ "	</p> ");
			} else {
				msgBody = HtmlUtil
						.mailBodyFormat(getSFParams(), "Recordatorio de Fin del Pedido",
								" 	<p style=\"font-size:12px\"> " + " <b>Pedido:</b> " + bmoOrder.getCode().toString()
										+ " " + bmoOrder.getName().toString() + " <br> " + "	<b>Tipo de Flujo:</b> "
										+ bmoOrder.getBmoWFlowType().getName().toString() + "	<br> "
										+ "	<b>Fechas:</b> "
										+ ((!bmoOrder.getLockStart().toString().equals("")) ? SFServerUtil
												.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
														getSFParams().getBmoSFConfig().getPrintDateTimeFormat()
																.toString(),
														bmoOrder.getLockStart().toString())
												: "")
										+ " - "
										+ ((!bmoOrder.getLockEnd().toString().equals(""))
												? SFServerUtil.formatDate(getSFParams(),
														getSFParams().getDateTimeFormat(),
														getSFParams()
																.getBmoSFConfig().getPrintDateTimeFormat().toString(),
														bmoOrder.getLockEnd().toString())
												: "")
										+ "	<br> " + "	<b>Cliente:</b> "
										+ bmoOrder.getBmoCustomer().getCode().toString() + " "
										+ bmoOrder.getBmoCustomer().getDisplayName().toString() + " "
										+ ((bmoOrder.getBmoCustomer().getCustomertype()
												.equals(BmoCustomer.TYPE_COMPANY))
														? " (" + bmoOrder.getBmoCustomer().getLegalname().toString()
																+ ")"
														: "")
										+ "	<br> " + "	<b>Correo del cliente:</b> "
										+ bmoOrder.getBmoCustomer().getEmail().toString() + "	<br> "
										+ "	<b>Tel./M&oacute;vil del cliente:</b> "
										+ bmoOrder.getBmoCustomer().getPhone().toString() + " / "
										+ bmoOrder.getBmoCustomer().getMobile().toString() + "	<br> "
										+ "	<b>Vendedor:</b> " + bmoSalesman.getFirstname().toString() + " "
										+ bmoSalesman.getFatherlastname().toString() + " "
										+ bmoSalesman.getMotherlastname().toString()
										+ "	<p align=\"center\" style=\"font-size:12px\"> "
										+ "		Favor de dar Seguimiento al Pedido <a target=\"_blank\" href=\""
										+ GwtUtil.getProperUrl(getSFParams(), "start.jsp?startprogram="
										+ bmoOrder.getProgramCode() + "&foreignid=" + bmoOrder.getId())
										+ "\">Aqu&iacute;</a>. "
										// + getSFParams().getAppURL() + "symgf_start.jsp" + "\">Aqu&iacute;</a>. "
										+ "	</p> ");
			}

			SFSendMail.send(getSFParams(), mailList, getSFParams().getBmoSFConfig().getEmail().toString(),
					getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
		}
	}

	// Prepara lista de destinatarios del correo
	private ArrayList<SFMailAddress> getGroupMailList(int wflowId, int profileId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String mailString = "", sql = "";

		// Revisar usuarios asignados al grupo del flujo
		sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid "
				+ " FROM profileusers "
				+ " LEFT JOIN users ON (pfus_userid = user_userid) " 
				+ " WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " 
				+ " AND (pfus_profileid IN (SELECT prof_profileid FROM profiles WHERE prof_profileid = " + profileId + ") "
				+ " AND pfus_userid IN (SELECT wflu_userid FROM wflowusers WHERE wflu_wflowid = " + wflowId + "))"
				+ " GROUP BY user_userid " + " ORDER BY user_email ASC";

		if (!getSFParams().isProduction())
			System.out.println("Buscar usuarios asignados al Grupo del Flujo: SQL: " + sql);

		try {
			pmConn.open();
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

				if (!getSFParams().isProduction()) {
					System.out.println(
							"No hay destinatarios del correo - se busca enviar a todos los usuarios del grupo del Flujo.");
					System.out.println("Buscar usuarios asignados al Grupo - SQL: " + sql);
				}

				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid "
						+ " FROM profileusers " + " LEFT JOIN users ON (pfus_userid = user_userid) "
						+ " WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " + " AND pfus_profileid = " + profileId
						+ " GROUP BY user_userid " + " ORDER BY user_email ASC";

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
				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname " + " FROM wflows "
						+ " LEFT JOIN users ON (wflw_userid = user_userid) " + " WHERE user_status = '"
						+ BmoUser.STATUS_ACTIVE + "' " + " AND wflw_wflowid = " + wflowId + " ORDER BY user_email ASC";

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
				System.out.println(
						"PmOrder-getGroupMailList(): No hay destinatarios del correo - no hay usuarios asignados al grupo del Flujo.");

			if (!getSFParams().isProduction())
				System.out.println(
						this.getClass().getName() + "-getGroupMailList(): La lista de correos es: " + mailString);

		} catch (SFPmException e) {
			throw new SFException("PmOrder-getGroupMailList() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return mailList;
	}

	// Prepara lista de destinatarios del correo
	private ArrayList<SFMailAddress> getWFlowUsersMailList(PmConn pmConn, int wflowId) throws SFException {

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String mailString = "", sql = "";

		// Revisar usuarios asignados al flujo
		sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid FROM wflowusers "
				+ " LEFT JOIN users ON (wflu_userid = user_userid) " + " WHERE user_status = '" + BmoUser.STATUS_ACTIVE
				+ "' " + " AND wflu_wflowid = " + wflowId + " GROUP BY user_userid " + " ORDER BY user_email ASC";

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
					System.out.println(
							"No hay destinatarios del correo - se busca enviar a todos los usuarios del grupo del Flujo.");

				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname, user_userid "
						+ " FROM profileusers " + " LEFT JOIN users ON (pfus_userid = user_userid) "
						+ " WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' "
						+ " AND pfus_profileid IN (SELECT wflu_profileid FROM wflowusers WHERE wflu_wflowid = " + wflowId + ") " 
						+ " GROUP BY user_userid " + " ORDER BY user_email ASC";

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
				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname " + " FROM wflows "
						+ " LEFT JOIN users ON (wflw_userid = user_userid) " + " WHERE user_status = '"
						+ BmoUser.STATUS_ACTIVE + "' " + " AND wflw_wflowid = " + wflowId + " ORDER BY user_email ASC";

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
				System.out.println(
						"PmOrder-getWFlowUsersMailList(): No hay destinatarios del correo - no hay usuarios asignados al grupo del Flujo.");

			if (!getSFParams().isProduction())
				System.out.println(
						this.getClass().getName() + "-getWFlowUsersMailList(): La lista de correos es: " + mailString);

		} catch (SFPmException e) {
			throw new SFException("PmOrder-getWFlowUsersMailList() ERROR: " + e.toString());
		}

		return mailList;
	}
	
	// Existen lineas de PRODUCTOS en el Pedido?
	public boolean existProducts(PmConn pmConn, int orderId) throws SFException {
		boolean existProducts = false;
		String sql = "SELECT ordi_orderitemid FROM orderitems "
				+ " LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) "
				+ " WHERE ordg_orderid = " + orderId
				+ " AND ordi_productid > 0 ";
		pmConn.doFetch(sql);
		if (pmConn.next() ) existProducts = true;
		
		return existProducts;
	}

	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrder = (BmoOrder) bmObject;
		
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			
			// Revisar si hay cuentas x cobrar ligadas
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			if (pmRaccount.orderHasAuthorizedRaccounts(pmConn, bmoOrder.getId())) {
				bmUpdateResult.addMsg("No se puede Eliminar Pedido -  tiene Cuentas x Cobrar Autorizadas.");
				
			} else {
				pmConn.doUpdate("DELETE FROM raccountitems " + " WHERE rait_raccountid IN ("
						+ " SELECT racc_raccountid FROM raccounts WHERE racc_orderid = " + bmoOrder.getId() + ")");
				pmConn.doUpdate("DELETE FROM raccounts WHERE racc_orderid = " + bmoOrder.getId());
			}

			// Revisar si hay ordenes de compra ligadas
			BmoRequisition bmoRequisition = new BmoRequisition();
			PmRequisition pmRequisition = new PmRequisition(getSFParams());
			BmFilter filterByPropertyRental = new BmFilter();
			
			filterByPropertyRental.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(),
					bmoOrder.getId());
			ListIterator<BmObject> requisitionList = pmRequisition.list(pmConn, filterByPropertyRental).listIterator();
			// Si existen OC lazar error
			if (requisitionList.hasNext()) {
				bmUpdateResult.addMsg("No se puede Eliminar el Pedido -  tiene Ordenes de Compra.");
				// bmoRequisition = (BmoRequisition)requisitionList.next();
				// pmRequisition.delete(pmConn, bmoRequisition, bmUpdateResult);
			}

			if (!bmUpdateResult.hasErrors()) {

				// Si es de tipo venta o renta, elimina registros ligados
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
						|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
						|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
					pmConn.doUpdate("DELETE FROM orderitems "
							+ " WHERE ordi_ordergroupid IN (SELECT ordg_ordergroupid FROM ordergroups WHERE ordg_orderid = "
							+ bmoOrder.getId() + ")");
					pmConn.doUpdate("DELETE FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId());
					pmConn.doUpdate("DELETE FROM whsections WHERE whse_orderid = " + bmoOrder.getId());
				}
				// Si es de tipo renta, elimina registros ligados
				else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					pmConn.doUpdate("DELETE FROM orderequipments WHERE ordq_orderid = " + bmoOrder.getId());
					pmConn.doUpdate("DELETE FROM orderstaff WHERE ords_orderid = " + bmoOrder.getId());
				}
				// Si es de tipo inmuebles, elimina registros ligados
				else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
					deleteOrderProperty(pmConn, bmoOrder, bmUpdateResult);
					deleteOrderPropertyModelExtra(pmConn, bmoOrder, bmUpdateResult);
				}
				// Si es de tipo sesiones, elimina registros ligados
				else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
					pmConn.doUpdate("DELETE FROM orderitems " + " WHERE ordi_ordergroupid IN ("
							+ " SELECT ordg_ordergroupid FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId()
							+ ")");
					pmConn.doUpdate("DELETE FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId());
					deleteOrderSessionTypePackage(pmConn, bmoOrder, bmUpdateResult);
					deleteOrderSession(pmConn, bmoOrder, bmUpdateResult);
				} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
					pmConn.doUpdate("DELETE FROM ordercredits WHERE orcr_orderid = " + bmoOrder.getId());
				} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					// pmConn.doUpdate("update orders set orde_originreneworderid = null where
					// orde_orderid = "+ bmoOrder.getId());
					deleteOrderPropertyTax(pmConn, bmoOrder, bmUpdateResult);
					deleteOrderPropertyModelExtra(pmConn, bmoOrder, bmUpdateResult);
					
				}

				// Eliminar detalle del pedido
				pmConn.doUpdate("DELETE FROM orderdetails WHERE ordt_orderid = " + bmoOrder.getId());

				super.delete(pmConn, bmoOrder, bmUpdateResult);

				// if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Actualiza estatus propiedad
				// PmProperty pmProperty = new PmProperty(getSFParams());
				// //obtener el contrato del pedido
				// BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				// PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				// bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(pmConn,
				// bmoOrder.getId(),bmoPropertyRental.getOrderId().getName());
				// pmProperty.updateAvailability(pmConn, bmoPropertyRental, bmUpdateResult);
				// }
				// Actualizar Estatus Cliente
				PmCustomer pmCustomer = new PmCustomer(getSFParams());
				BmoCustomer bmoCustomer = (BmoCustomer) pmCustomer.get(pmConn, bmoOrder.getCustomerId().toInteger());
				if (bmoOrder.getWFlowTypeId().toInteger() > 0) {
					pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
				}

				// Eliminar flujos
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				BmoWFlow bmoWFlow = new BmoWFlow();
				BmFilter filterByOrder = new BmFilter();
				filterByOrder.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoOrder.getId());
				BmFilter filterWFlowCategory = new BmFilter();
				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(),
						bmoOrder.getProgramCode());
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				filterList.add(filterByOrder);
				filterList.add(filterWFlowCategory);
				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
				while (wFlowList.hasNext()) {
					bmoWFlow = (BmoWFlow) wFlowList.next();
					pmWFlow.delete(pmConn, bmoWFlow, bmUpdateResult);
				}
			}
		} else {
			String status = "";
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				status = "Autorizado";
			else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				status = "Finalizado";
			else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				status = "Cancelado";

			bmUpdateResult.addMsg("No se puede Eliminar Pedido -  está " + status);
		}
		if (!bmUpdateResult.hasErrors()) {
			if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				if(bmoOrder.getOriginRenewOrderId().toInteger() > 0) {
					updateRenewDateContract(bmoOrder,bmUpdateResult);
				}				
			}
		}

		return bmUpdateResult;
	}
	//Actualizar fecha de incremento de renta al borrar un renovado
	public void updateRenewDateContract(BmoOrder bmoOrder,BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmproProperty = new PmProperty(getSFParams());
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		BmoOrder bmoOrderOrigin = new BmoOrder();
		PmOrder pmOrder = new PmOrder(getSFParams());
		
		
		if(bmoOrder.getRenewOrderId().toInteger() > 0) {	
					
			bmoOrderOrigin = (BmoOrder)pmOrder.get(bmoOrder.getRenewOrderId().toInteger());				
			bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrderOrigin.getOriginRenewOrderId().toInteger(),bmoPropertyRental.getOrderId().getName());
			bmoPropertyRental.getRentIncrease().setValue(bmoOrderOrigin.getLockEnd().toString());
			bmoProperty = (BmoProperty)pmproProperty.get(bmoPropertyRental.getPropertyId().toInteger());
			bmoProperty.getCansell().setValue(1);
			bmoProperty.getAvailable().setValue(1);			
			pmproProperty.saveSimple(bmoProperty,bmUpdateResult);
			pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);
			
		}else {		

			bmoOrderOrigin = (BmoOrder)pmOrder.get(bmoOrder.getOriginRenewOrderId().toInteger());	
			if(bmoOrderOrigin.getOriginRenewOrderId().toInteger() > 0) {
				bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrderOrigin.getOriginRenewOrderId().toInteger(),bmoPropertyRental.getOrderId().getName());
			
			}else {
				bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrderOrigin.getId(),bmoPropertyRental.getOrderId().getName());
			}
			bmoPropertyRental.getRentIncrease().setValue(bmoOrderOrigin.getLockEnd().toString());
			bmoProperty = (BmoProperty)pmproProperty.get(bmoPropertyRental.getPropertyId().toInteger());
			bmoProperty.getCansell().setValue(1);
			bmoProperty.getAvailable().setValue(1);
		}
		pmproProperty.saveSimple(bmoProperty,bmUpdateResult);
		if(bmoOrderOrigin.getOriginRenewOrderId().toInteger() > 0) {
			pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);
		}
	}
		

	// Elimina los inmuebles del pedido
	public void deleteOrderProperty(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmOrderProperty pmOrderProperty = new PmOrderProperty(getSFParams());
		BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		bmFilter.setValueFilter(bmoOrderProperty.getKind(), bmoOrderProperty.getOrderId(), bmoOrder.getId());
		ListIterator<BmObject> listOrderProperty = pmOrderProperty.list(pmConn, bmFilter).listIterator();
		while (listOrderProperty.hasNext()) {
			bmoOrderProperty = (BmoOrderProperty) listOrderProperty.next();
			pmOrderProperty.delete(pmConn, bmoOrderProperty, bmUpdateResult);
		}
	}

	// Elimina los extras de inmuebles del pedido
	public void deleteOrderPropertyModelExtra(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmOrderPropertyModelExtra pmOrderPropertyModelExtra = new PmOrderPropertyModelExtra(getSFParams());
		BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
		bmFilter.setValueFilter(bmoOrderPropertyModelExtra.getKind(), bmoOrderPropertyModelExtra.getOrderId(),
				bmoOrder.getId());
		ListIterator<BmObject> listOrderPropertyModelExtra = pmOrderPropertyModelExtra.list(pmConn, bmFilter)
				.listIterator();
		while (listOrderPropertyModelExtra.hasNext()) {
			bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra) listOrderPropertyModelExtra.next();
			pmOrderPropertyModelExtra.delete(pmConn, bmoOrderPropertyModelExtra, bmUpdateResult);
		}
	}

	// Elimina el tipo de paquetes de sesion del pedido
	public void deleteOrderSessionTypePackage(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmOrderSessionTypePackage pmOrderSessionTypePackage = new PmOrderSessionTypePackage(getSFParams());
		BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
		bmFilter.setValueFilter(bmoOrderSessionTypePackage.getKind(), bmoOrderSessionTypePackage.getOrderId(),
				bmoOrder.getId());
		ListIterator<BmObject> listOrderSessionTypePackage = pmOrderSessionTypePackage.list(pmConn, bmFilter)
				.listIterator();
		while (listOrderSessionTypePackage.hasNext()) {
			bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage) listOrderSessionTypePackage.next();
			pmOrderSessionTypePackage.delete(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);
		}
	}

	// Elimina las sesiones ligadas al pedido
	public void deleteOrderSession(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmOrderSession pmOrderSession = new PmOrderSession(getSFParams());
		BmoOrderSession bmoOrderSession = new BmoOrderSession();
		bmFilter.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getOrderId(), bmoOrder.getId());
		ListIterator<BmObject> listOrderSession = pmOrderSession.list(pmConn, bmFilter).listIterator();
		while (listOrderSession.hasNext()) {
			bmoOrderSession = (BmoOrderSession) listOrderSession.next();
			pmOrderSession.delete(pmConn, bmoOrderSession, bmUpdateResult);
		}
	}

	// Elimina los inmuebles de arrendamiento del pedido
	public void deleteOrderPropertyTax(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmFilter bmFilter = new BmFilter();
		PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(getSFParams());
		BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
		bmFilter.setValueFilter(bmoOrderPropertyTax.getKind(), bmoOrderPropertyTax.getOrderId(), bmoOrder.getId());
		ListIterator<BmObject> listOrderPropertyTax = pmOrderPropertyTax.list(pmConn, bmFilter).listIterator();
		while (listOrderPropertyTax.hasNext()) {
			bmoOrderPropertyTax = (BmoOrderPropertyTax) listOrderPropertyTax.next();
			pmOrderPropertyTax.delete(pmConn, bmoOrderPropertyTax, bmUpdateResult);
		}
	}
	//Validar si no hay projectos para el pedido
	public boolean searchOrderProjectStep(PmConn pmConn,int orderId) throws SFPmException {
		String sql = "SELECT * FROM projectsstep WHERE spro_orderid = " + orderId;
		pmConn.open();
		
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			pmConn.close();
			return true;
		}else {
			pmConn.close();
			return false;
		}
		
	}
	//Validar si tiene projetStep
	public boolean getProyects(PmConn pmConn, int orderId) throws SFPmException {
		
		String sql = "SELECT * FROM projectsstep WHERE spro_orderid = " + orderId;
		boolean result = false;
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			result = true;
		}
		return result;
	}
	//Valida que la fecha fin del pedido no supere la fecha fin del contrato
	public BmUpdateResult getMonthContract(BmoOrder bmoOrder ,BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog("getMonthContarct() - Pedido Obtenido " + bmoOrder.getCode());
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
		
		if(!SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
				bmoOrder.getLockEnd().toString(), 
				SFServerUtil.addDays(getSFParams().getDateFormat(), bmoPropertyRental.getEndDate().toString(), 1))) {
			bmUpdateResult.addError(bmoOrder.getLockEnd().getName(),"La fecha fin del Renovado supera la fecha fin del Contrato");
		}
		
		
		return bmUpdateResult;
	}
	//Validar si se creara proyecto
	public boolean createProjectStep(BmoOrder bmoOrder) throws SFPmException {
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		boolean result = false;
		String sql = "SELECT ordg_ordergroupid FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId();
		
		pmConn.open();
		pmConn2.open();
		
		pmConn.doFetch(sql);
		
		while (pmConn.next()) {
			sql = "SELECT * FROM orderitems WHERE ordi_ordergroupid = "+ pmConn.getInt("ordg_ordergroupid") +" AND ordi_createproject = 1";
			pmConn2.doFetch(sql);	
			if(pmConn2.next())result = true;
		}
		
		pmConn.close();
		pmConn2.close();
		return result;
		
	}
}
