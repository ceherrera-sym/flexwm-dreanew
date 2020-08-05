/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla Guerra
 * @version 2013-10
 */

package com.flexwm.server.ar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import com.flexwm.server.FlexUtil;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.co.PmOrderPropertyTax;
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFormat;
import com.symgae.shared.sf.BmoUser;


public class PmPropertyRental extends PmObject{
	BmoPropertyRental bmoPropertyRental;

	public PmPropertyRental(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPropertyRental = new BmoPropertyRental();
		setBmObject(bmoPropertyRental);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertyRental.getPropertyId(), bmoPropertyRental.getBmoProperty()),
				new PmJoin(bmoPropertyRental.getBmoProperty().getDevelopmentBlockId(), bmoPropertyRental.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoPropertyRental.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoPropertyRental.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase()),
				new PmJoin(bmoPropertyRental.getUserId(), bmoPropertyRental.getBmoUser()),
				new PmJoin(bmoPropertyRental.getOrderTypeId(), bmoPropertyRental.getBmoOrderType()),
				new PmJoin(bmoPropertyRental.getCustomerId(), bmoPropertyRental.getBmoCustomer()),
				new PmJoin(bmoPropertyRental.getBmoCustomer().getTerritoryId(), bmoPropertyRental.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoPropertyRental.getBmoCustomer().getReqPayTypeId(), bmoPropertyRental.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoPropertyRental.getBmoUser().getAreaId(), bmoPropertyRental.getBmoUser().getBmoArea()),
				new PmJoin(bmoPropertyRental.getBmoUser().getLocationId(), bmoPropertyRental.getBmoUser().getBmoLocation()),
				new PmJoin(bmoPropertyRental.getWFlowTypeId(), bmoPropertyRental.getBmoWFlowType()),
				new PmJoin(bmoPropertyRental.getBmoWFlowType().getWFlowCategoryId(), bmoPropertyRental.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoPropertyRental.getWFlowId(), bmoPropertyRental.getBmoWFlow()),
				new PmJoin(bmoPropertyRental.getBmoWFlow().getWFlowPhaseId(), bmoPropertyRental.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoPropertyRental.getBmoWFlow().getWFlowFunnelId(), bmoPropertyRental.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de proyectos
		if (getSFParams().restrictData(bmoPropertyRental.getProgramCode())) {

			filters = "( prrt_userid in (" +
					" select user_userid from users " +
					" where " + 
					" user_userid = " + loggedUserId +
					" or user_userid in ( " +
					" 	select u2.user_userid from users u1 " +
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
					" prrt_orderid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId + 
					" AND (wflw_callercode = 'PRRT' OR wflw_callercode = 'ORDE') " + 
					"   ) " +
					" ) " +
					" ) ";
		}				

		// Filtro de proyectos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( prrt_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " prrt_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPropertyRental bmoPropertyRental = (BmoPropertyRental) autoPopulate(pmConn, new BmoPropertyRental());

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
		if (orderTypeId > 0) bmoPropertyRental.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else bmoPropertyRental.setBmoOrderType(bmoOrderType);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoPropertyRental.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoPropertyRental.setBmoCustomer(bmoCustomer);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int UserId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (UserId > 0) bmoPropertyRental.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoPropertyRental.setBmoUser(bmoUser);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoPropertyRental.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoPropertyRental.setBmoWFlow(bmoWFlow);

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoPropertyRental.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoPropertyRental.setBmoProperty(bmoProperty);

		return bmoPropertyRental;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertyRental bmoPropertyRental = (BmoPropertyRental)bmObject;	
		boolean isNewRecord = false;
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoPropertyRental.getId() > 0)) {		
			isNewRecord = true;
			super.save(pmConn, bmoPropertyRental, bmUpdateResult);

			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());

			if (bmoPropertyRental.getName().toString().equals(""))
				bmoPropertyRental.getName().setValue(bmoProperty.getDescription().toString());
			bmoPropertyRental.setId(bmUpdateResult.getId());
			String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoPropertyRental.CODE_PREFIX);			
			bmoPropertyRental.getCode().setValue(code);

			if (!(bmoPropertyRental.getCompanyId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoPropertyRental.getCompanyId().getName(), "Debe Seleccionar una Empresa.");
			}
			if(!(bmoPropertyRental.getOriginRenewContractId().toInteger() > 0))
				bmoPropertyRental.getStatus().setValue(BmoPropertyRental.STATUS_AUTHORIZED);
		}

		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoPropertyRental.setBmoOrderType((BmoOrderType)pmOrderType.get(pmConn, bmoPropertyRental.getOrderTypeId().toInteger()));

		// Si se esta cambiando el estatus a En Revision, se modifica para poder hacer el resto de procesos
		if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_REVISION)
			super.save(pmConn, bmoPropertyRental, bmUpdateResult);

		// Revisa fechas
		if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
				bmoPropertyRental.getEndDate().toString(), bmoPropertyRental.getStartDate().toString()))
			bmUpdateResult.addError(bmoPropertyRental.getEndDate().getName(), 
					"No puede ser Anterior a " + bmoPropertyRental.getStartDate().getLabel());

		// Otras validaciones una vez asignado el tipo de contrato
		if (!bmUpdateResult.hasErrors() && bmoPropertyRental.getWFlowTypeId().toInteger() > 0) {

			//Validar que la difeferencia de f.1er renta menos incremento renta no sobrepase los 12 meses
			// Calcular meses entre fechas
			String rentalScheduleDate = bmoPropertyRental.getRentalScheduleDate().toString();
			String rentalIncrease = bmoPropertyRental.getRentIncrease() .toString();

			Calendar dateStart = Calendar.getInstance();
			Calendar dateEnd= Calendar.getInstance();

			dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalScheduleDate));
			dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalIncrease));

			int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);
			if(diffMonth > 12) {
				bmUpdateResult.addError(bmoPropertyRental.getRentIncrease().getName(), "Error: La Diferencia de 1er.Renta e Incremento Renta Exceden a Doce Meses");
			}
			
			// Crear pedido si no existe
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			int wflowId = 0;
			// Si no existe el pedido, crearlo
			if (!(bmoPropertyRental.getOrderId().toInteger() > 0)) {
				bmoOrder = createOrderFromPropertyRental(pmConn, bmoPropertyRental, bmUpdateResult);
				if (!bmUpdateResult.hasErrors()) {
					//Actualiza disponibilidad del inmueble
					if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
						BmoProperty bmoProperty = new BmoProperty();
						PmProperty pmProperty = new PmProperty(getSFParams());
						bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());
						if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED || bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED) {
							bmoProperty.getAvailable().setValue(true);
							pmProperty.saveSimple(pmConn, bmoProperty, bmUpdateResult);
						}
						else {
							bmoProperty.getAvailable().setValue(false);
							pmProperty.saveSimple(pmConn, bmoProperty, bmUpdateResult);
						}
					}
					//Si es nuevo contarto asigna en Codigo del contrato
					if (isNewRecord) {	
						bmoOrder.getCode().setValue(bmoPropertyRental.getCode().toString());
						pmOrder.saveSimple(pmConn,bmoOrder, bmUpdateResult);
					}
					
					
				}
			} else {
				// Actualiza info del pedido, porque es existente
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertyRental.getOrderId().toInteger());
				wflowId = bmoOrder.getWFlowId().toInteger();

				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					bmoOrder.getName().setValue(bmoPropertyRental.getName().toString());
					bmoOrder.getUserId().setValue(bmoPropertyRental.getUserId().toInteger());				
					bmoOrder.getCurrencyId().setValue(bmoPropertyRental.getCurrencyId().toInteger());
					bmoOrder.getCurrencyParity().setValue(bmoPropertyRental.getCurrencyParity().toDouble());
					bmoOrder.getCustomerId().setValue(bmoPropertyRental.getCustomerId().toInteger());
					bmoOrder.getCompanyId().setValue(bmoPropertyRental.getCompanyId().toInteger());				

					pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);

					
				} else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {

					if (!bmoOrder.getName().toString().equals(bmoPropertyRental.getName().toString())) {
						bmUpdateResult.addError(bmoPropertyRental.getName().getName() , "No se puede cambiar el Nombre de la Contrato, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCustomerId().toInteger() != bmoPropertyRental.getCustomerId().toInteger()) {
						bmUpdateResult.addError(bmoPropertyRental.getCustomerId().getName() , "No se puede cambiar el Cliente/Prospecto, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getUserId().toInteger() != bmoPropertyRental.getUserId().toInteger()) {
						bmUpdateResult.addError(bmoPropertyRental.getUserId().getName() , "No se puede cambiar el Vendedor, el Pedido ya está Autorizado");
					} else if (bmoOrder.getLockStart().toString().substring(0, 10).compareTo(bmoPropertyRental.getStartDate().toString()) != 0){
						bmUpdateResult.addError(bmoPropertyRental.getStartDate().getName() , "No se puede cambiar la Fecha Inicio, el Pedido ya está Autorizado");
					} else if (!bmoOrder.getLockEnd().toString().substring(0, 10).equals(bmoPropertyRental.getEndDate().toString())) {
						bmUpdateResult.addError(bmoPropertyRental.getEndDate().getName() , "No se puede cambiar la Fecha Fin, el Pedido ya está Autorizado");
					} else if (bmoOrder.getCurrencyId().toInteger() == bmoPropertyRental.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() != bmoPropertyRental.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoPropertyRental.getCurrencyParity().getName() , "No se puede cambiar el Tipo de Cambio, el Pedido ya está Autorizado");
					} else if (bmoOrder.getCurrencyId().toInteger() != bmoPropertyRental.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() == bmoPropertyRental.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoPropertyRental.getCurrencyId().getName() , "No se puede cambiar la Moneda, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCurrencyId().toInteger() != bmoPropertyRental.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() != bmoPropertyRental.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoPropertyRental.getCurrencyId().getName() , "No se puede cambiar la Moneda/Tipo de Cambio, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCompanyId().toInteger() != bmoPropertyRental.getCompanyId().toInteger()) {
						bmUpdateResult.addError(bmoPropertyRental.getCompanyId().getName() , "No se puede cambiar la Empresa, v");
					} 
				}
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

				// Asigna el estatus al pedido
				if (bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_CANCEL)) {
					//bmoOrder.getStatus().setValue(BmoOrder.STATUS_CANCELLED);
					if (bmoOrder.getWFlowId().toInteger() > 0)
						pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Contrato se guardó como Cancelado.");
					//pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

				} else if (bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_FINISHED)) {
					//bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
					if (bmoOrder.getWFlowId().toInteger() > 0)
						pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Contrato se guardó como Finalizado.");
					//pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

				} else if (bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_REVISION)) {
					//bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Contrato se guardó como Revisión.");
					//pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
				} else if (bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_AUTHORIZED)) {
					//bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Contrato se guardó como Autorizado.");
					//pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
				}
			}

			// Asigna valor del pedido
			bmoPropertyRental.getOrderId().setValue(bmoOrder.getId());

			// Asigna ID del flujo recien creado
			if (wflowId > 0)
				bmoPropertyRental.getWFlowId().setValue(wflowId);
			else
				bmoPropertyRental.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());

			super.save(pmConn, bmoPropertyRental, bmUpdateResult);

		}

		// Actualiza estatus del cliente
		if (!bmUpdateResult.hasErrors() && bmoPropertyRental.getWFlowTypeId().toInteger() > 0) {
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoPropertyRental.getCustomerId().toInteger());
			pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
		}

		// Si se esta autorizando el proyecto, pero tiene ordenes de compra no autorizadas, evitar autorización
		if (!bmUpdateResult.hasErrors()) {
			if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_AUTHORIZED) {
				if (bmoPropertyRental.getOrderId().toInteger() > 0) {
					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					if (pmRequisition.hasPendingRequisitionsByOrder(pmConn, bmoPropertyRental.getOrderId().toInteger()))
						bmUpdateResult.addMsg("No se puede Autorizar: Tiene Órdenes de Compra En Revisión.");
				}
			}
		}

		// Se almacena el proyecto
		if (!bmUpdateResult.hasErrors()) {
			
			if(hasRenew(pmConn, bmoPropertyRental.getId())) {
				bmoPropertyRental.getEnabled().setValue(BmoPropertyRental.DISABLED);
			}else {
				bmoPropertyRental.getEnabled().setValue(BmoPropertyRental.ENABLED);
			}
				
		
			// Guarda el proyecto
			super.save(pmConn, bmoPropertyRental, bmUpdateResult);
			
			// Forzar el ID del resultado que sea del proyecto y no de otra clase
			bmUpdateResult.setId(bmoPropertyRental.getId());

		}

		return bmUpdateResult;
	}

	public BmoOrder createOrderFromPropertyRental(PmConn pmConn, BmoPropertyRental bmoPropertyRental, BmUpdateResult bmUpdateResult) 
			throws SFException {
		BmoOrder bmoOrder = new BmoOrder();
		
		// Crear pedido a partir de contrato
		PmOrder pmOrder = new PmOrder(getSFParams());
		//bmoOrder.getCode().setValue(bmoPropertyRental.getCode().toString());
		bmoOrder.getName().setValue(bmoPropertyRental.getName().toString());
		bmoOrder.getDescription().setValue(bmoPropertyRental.getDescription().toString());
		bmoOrder.getAmount().setValue(0);
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getOrderTypeId().setValue(bmoPropertyRental.getOrderTypeId().toInteger());
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoPropertyRental.getRentalScheduleDate().toString());
		bmoOrder.getLockEnd().setValue(bmoPropertyRental.getRentIncrease().toString());
		bmoOrder.getTags().setValue(bmoPropertyRental.getTags().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoPropertyRental.getCustomerId().toInteger());
		bmoOrder.getUserId().setValue(bmoPropertyRental.getUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoPropertyRental.getWFlowTypeId().toInteger());
		//bmoOrder.getWFlowId().setValue(bmoPropertyRental.getWFlowId().toInteger());
		bmoOrder.getCompanyId().setValue(bmoPropertyRental.getCompanyId().toInteger());
		bmoOrder.getMarketId().setValue(bmoPropertyRental.getMarketId().toInteger());
		bmoOrder.getCurrencyId().setValue(bmoPropertyRental.getCurrencyId().toInteger());
		bmoOrder.getCurrencyParity().setValue(bmoPropertyRental.getCurrencyParity().toDouble());		
		if(bmoPropertyRental.getContractTerm().toInteger() > 1)
			bmoOrder.getWillRenew().setValue(1);

		// Si esta habilitado el Control presp. pasar por defecto la partida y el dpto. del tipo de pedido
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
			PmOrderType pmOrderType = new PmOrderType(getSFParams());
			BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoPropertyRental.getOrderTypeId().toInteger());

			if (!(bmoOrderType.getDefaultBudgetItemId().toInteger() > 0)) 
				bmUpdateResult.addError(bmoOrderType.getDefaultBudgetItemId().getName(), "Seleccione una Partida Presupuestal en el Tipo de Pedido.");
			else {
				bmoOrder.getDefaultBudgetItemId().setValue(bmoOrderType.getDefaultBudgetItemId().toInteger());
				bmoOrder.getDefaultAreaId().setValue(bmoOrderType.getDefaultAreaId().toInteger());
			}
		}
		//}
		 pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		 bmoOrder.getOriginRenewOrderId().setValue(bmoOrder.getId());
		 pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
		//Si es de tipo arrendamiento 
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			PmProperty pmProperty = new PmProperty(getSFParams());
			BmoProperty bmoProperty = new BmoProperty();
			bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());
			//valida si el inmueble esta abierto y disponible
			//if (bmoProperty.getCansell().toInteger() > 0 && bmoProperty.getAvailable().toInteger() > 0) {
				//si el pedido no viene de una renovacion guarda el inmueble del pedido
				if(!(bmoOrder.getRenewOrderId().toInteger() > 0)) {

					// Calcular meses entre fechas
					String rentalScheduleDate = bmoPropertyRental.getRentalScheduleDate().toString();
					String rentalIncrease = bmoPropertyRental.getRentIncrease() .toString();

					Calendar dateStart = Calendar.getInstance();
					Calendar dateEnd= Calendar.getInstance();

					dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalScheduleDate));
					dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalIncrease));

					int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
					int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);
										
					PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(getSFParams());
					BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
					//bmoOrderPropertyTax.getPrice().setValue(bmoProperty.getExtraPrice().toDouble());
					bmoOrderPropertyTax.getPrice().setValue(bmoPropertyRental.getCurrentIncome().toDouble());
					printDevLog("precio pedido: "+ bmoPropertyRental.getCurrentIncome().toDouble() * diffMonth);
					bmoOrderPropertyTax.getAmount().setValue(bmoPropertyRental.getCurrentIncome().toDouble() * diffMonth);
					bmoOrderPropertyTax.getPropertyId().setValue(bmoPropertyRental.getPropertyId().toInteger());
					bmoOrderPropertyTax.getOrderId().setValue(bmoOrder.getId());
					bmoOrderPropertyTax.getQuantity().setValue(diffMonth);
					pmOrderPropertyTax.create(pmConn, bmoOrderPropertyTax, bmoOrder, bmUpdateResult);
					//actualiza el campo disponible en inmueble
					bmoProperty.getAvailable().setValue(0);
					pmProperty.saveSimple(pmConn, bmoProperty, bmUpdateResult);
				}
//			} else {
//				bmUpdateResult.addError(bmoPropertyRental.getPropertyId().getName(), "El inmueble debe estar abierto y disponible");
//			}
		}
		return bmoOrder;
	}

	public BmoPropertyRental getByWFlowId(int wFlowId) throws SFException {
		return (BmoPropertyRental)super.getBy(wFlowId, bmoPropertyRental.getWFlowId().getName());
	}

	// Revisar si existe la orden de algún proyecto
	public boolean projectOrderExists(PmConn pmConn, int projectId) throws SFPmException {
		pmConn.doFetch("SELECT orde_orderid FROM orders WHERE orde_projectid = " + projectId);
		return pmConn.next();
	}

	// Se encuentra liberado el Pedido
	public BmUpdateResult isProjectReleased(BmoFormat bmoFormat, BmoPropertyRental bmoPropertyRental, BmUpdateResult bmUpdateResult) throws SFException{
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			// Revisa si existe proyecto autorizado
			if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_AUTHORIZED) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = new BmoOrder();
				bmoOrder = (BmoOrder)pmOrder.get(bmoPropertyRental.getOrderId().toInteger());
				if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED)
					bmUpdateResult.addMsg("El Pedido NO está Autorizado.");
			} else {
				bmUpdateResult.addMsg("El Proyecto NO está Autorizado.");
			}

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		if (bmObject.getId() > 0) 
			bmoPropertyRental = (BmoPropertyRental)this.get(bmObject.getId());

		if (action.equals(BmoOrder.ACTION_GETDATAOWNERPROPERTY)) {
			String ownerProperty = "";
			ownerProperty = this.getDataOwnerProperty(value);

			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoProperty = (BmoProperty) pmProperty.get(Integer.parseInt(value));

			bmUpdateResult.setBmObject(bmoProperty);
			bmUpdateResult.setMsg("" + ownerProperty);
		}//Copiar contrato
		if(action.equals(BmoPropertyRental.ACTION_COPYCONTRATC)) {
			PmConn pmConnCopy = new PmConn(getSFParams());	
			
			try {
				pmConnCopy.open();
				pmConn.disableAutoCommit();
				
				
				PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
				BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
				bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.get(bmObject.getId());
				if(isNotRenew(pmConnCopy, bmoPropertyRental.getId())) {
					if(propertyAvailable(pmConnCopy, bmoPropertyRental.getPropertyId().toInteger())) {
						BmoPropertyRental bmoPropertyRentalNew = new BmoPropertyRental();

						bmoPropertyRentalNew.getName().setValue(bmoPropertyRental.getName().toString());			
						bmoPropertyRentalNew.getStartDate().setValue(bmoPropertyRental.getEndDate().toString());			
						bmoPropertyRentalNew.getContractTerm().setValue(bmoPropertyRental.getContractTerm().toInteger());			
						bmoPropertyRentalNew.getEndDate().setValue(
								SFServerUtil.addMonths(getSFParams().getDateFormat(), bmoPropertyRental.getEndDate().toString(), 
										bmoPropertyRental.getContractTerm().toInteger() * 12) );			
						bmoPropertyRentalNew.getOrderTypeId().setValue(bmoPropertyRental.getOrderTypeId().toInteger());		
						bmoPropertyRentalNew.getCompanyId().setValue(bmoPropertyRental.getCompanyId().toInteger());			
						bmoPropertyRentalNew.getCurrencyId().setValue(bmoPropertyRental.getCurrencyId().toInteger());			
						bmoPropertyRentalNew.getPropertyId().setValue(bmoPropertyRental.getPropertyId().toInteger());		
						bmoPropertyRentalNew.getCustomerId().setValue(bmoPropertyRental.getCustomerId().toInteger());			
						bmoPropertyRentalNew.getUserId().setValue(bmoPropertyRental.getUserId().toInteger());			
						bmoPropertyRentalNew.getWFlowTypeId().setValue(bmoPropertyRental.getWFlowTypeId().toInteger());			
						bmoPropertyRentalNew.getRentIncrease().setValue(SFServerUtil.addMonths(
								getSFParams().getDateFormat(), bmoPropertyRental.getEndDate().toString(), 1));			
						bmoPropertyRentalNew.getStatus().setValue(BmoPropertyRental.STATUS_REVISION);		
						bmoPropertyRentalNew.getRentalScheduleDate().setValue(bmoPropertyRental.getEndDate().toString());			
						bmoPropertyRentalNew.getCurrentIncome().setValue(bmoPropertyRental.getCurrentIncome().toString());
						bmoPropertyRentalNew.getInitialIconme().setValue(bmoPropertyRental.getInitialIconme().toString());
						bmoPropertyRentalNew.getOriginRenewContractId().setValue(bmoPropertyRental.getId());
						bmoPropertyRentalNew.getOwnerProperty().setValue(bmoPropertyRental.getOwnerProperty().toString());
						bmoPropertyRentalNew.getEnabled().setValue(BmoPropertyRental.ENABLED);
						pmPropertyRental.save(pmConnCopy,bmoPropertyRentalNew, bmUpdateResult);		
						
						bmoPropertyRental.getEnabled().setValue(BmoPropertyRental.DISABLED);
						pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);
					}else {
						bmUpdateResult.addMsg("El Inmueble no esta disponible");
					}
				}else {
					bmUpdateResult.addMsg("El Contrato ya fue renovado anteriormente");
				}
			}catch (Exception e) {
				pmConnCopy.close();
			}
			finally {
				pmConnCopy.close();
			}
		
		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException{
		bmoPropertyRental = (BmoPropertyRental)bmObject;
		String sql = "";
		PmConn pmConn2 = new PmConn(getSFParams());
		try {
		
		if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_REVISION) {
			//Revisar que no tenga pedidos pendientes renovados si tiene pedidos pendientes mandar error 
			//SOLO SE PUEDE ELIMINAR EL CONTRATO SI EL CONTRATO ESTA EN REVISION Y TIENE SOLO UN PEDIDO EN REVISION 
			sql = "";
			//obtener el pedido del contrato
			sql = " SELECT orde_orderid FROM orders WHERE orde_orderid = " + bmoPropertyRental.getOrderId().toInteger();
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				String originreneworderid = pmConn.getString("orde_orderid");
				//Si tiene más de un contrato no dejar eliminar ya que tiene contratos renovados
				sql = " SELECT count(*) FROM orders where orde_originreneworderid = " + originreneworderid;
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					if (pmConn.getInt(1) <= 1) {
						PmOrder pmOrder = new PmOrder(getSFParams());
						BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertyRental.getOrderId().toInteger());
						if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
							bmUpdateResult.addError(bmoPropertyRental.getName().getName(), "No se puede eliminar, el contrato tiene pedidos autorizados");
						}
						else {
							//liberar el inmueble 
							PmProperty pmProperty = new PmProperty(getSFParams());
							BmoProperty bmoProperty = new BmoProperty();
							bmoProperty = (BmoProperty) pmProperty.get(pmConn,bmoPropertyRental.getPropertyId().toInteger());
							
							
							if(bmoPropertyRental.getOriginRenewContractId().toInteger() > 0) {
								BmoPropertyRental bmoPropertyRentalOrigin = new BmoPropertyRental();
								bmoPropertyRentalOrigin = (BmoPropertyRental)this.get(bmoPropertyRental.getOriginRenewContractId().toInteger());
								bmoPropertyRentalOrigin.getEnabled().setValue(BmoPropertyRental.ENABLED);
								super.saveSimple(bmoPropertyRentalOrigin, bmUpdateResult);
							}
							
							//eliminar el pedido  y el contrato porque tiene solo un pedido y es diferente de autorizado
							super.delete(pmConn, bmoPropertyRental, bmUpdateResult);
							
							
							
							if(!bmUpdateResult.hasErrors()) {	
								
								
								pmConn2.open();
								pmConn2.doUpdate("update orders set orde_originreneworderid = NULL where orde_orderid = "+ bmoPropertyRental.getOrderId().toInteger());
								pmConn2.close();						
								pmOrder.delete(pmConn, bmoOrder, bmUpdateResult);
														
								//liberamos el inmueble
								bmoProperty.getAvailable().setValue(true);
								super.save(pmConn, bmoProperty, bmUpdateResult);

							}else {
								printDevLog("Errores PRRT ::: " + bmUpdateResult.errorsToString());
							}
						}
					}
					else {
						bmUpdateResult.addError(bmoPropertyRental.getName().getName(), "No se puede eliminar, el contrato tiene renovados");
					}
				}
			}
		} else {
			String status = "Autorizado";
			if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_CANCEL)
				status = "Cancelado";
			else if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_FINISHED)
				status = "Finalizado";
			bmUpdateResult.addError(bmoPropertyRental.getStatus().getName(), "No se puede eliminar, el contrato está " + status);
		}
		
			 pmConn2.close();
		
		return bmUpdateResult;
		}
		catch (SFException e) {
			pmConn.rollback();
			pmConn2.rollback();
			bmUpdateResult.addMsg("Error al Crear la Venta de Sesión. " + e.toString());
		} finally {
			pmConn2.close();
		}
		return bmUpdateResult;
	}

	// Obtener el Propiertario del inmueble del pedido
	public String getDataOwnerProperty(String propertyId) throws SFException {
		String sql = "", customer = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		// Sumar los ivas 
		sql = " SELECT cust_code, cust_customertype, cust_displayname, cust_legalname " +
				" FROM " + formatKind("properties") + 
				" LEFT JOIN " + formatKind("customers") + " ON (cust_customerid = prty_customerid) " +
				" WHERE prty_propertyid = " + Integer.parseInt(propertyId);
		printDevLog("sql_getDataOwnerProperty: "+ sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			if (pmConn.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				if(!(pmConn.getString("cust_legalname").equals("")))
					customer = pmConn.getString("cust_code") + " " + pmConn.getString("cust_legalname");
				else 
					customer = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
			else
				customer = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
		}
		pmConn.close();

		return customer;
	}
	
	public int getMoths(BmoPropertyRental bmoPropertyRental, BmUpdateResult bmUpdateResult) throws SFException {
		// Calcular meses entre fechas
		int moths = 0;

		String rentalScheduleDate = bmoPropertyRental.getRentalScheduleDate().toString();
		String rentalIncrease = bmoPropertyRental.getRentIncrease() .toString();

		Calendar dateStart = Calendar.getInstance();
		Calendar dateEnd= Calendar.getInstance();

		dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalScheduleDate));
		dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalIncrease));

		int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
		int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);

		moths = diffMonth;
		
		return moths;
	}
//	// Enviar recordatorios al vendedor, al vencer el contrato
	public void prepareRemindersEndContract() throws SFException {
		
		
		ArrayList<BmFilter> filterPropertyRental = new ArrayList<BmFilter>();
		
		
			// Obtener las fechas del Contarto dias antes del fin del Contrato, que tengan
			// activada la notificación
			BmFilter filterEmailReminder = new BmFilter();
			filterEmailReminder.setValueFilter(bmoPropertyRental.getKind(),bmoPropertyRental.getBmoOrderType().getEmailReminderContractEnd().getName(),1	);
			filterPropertyRental.add(filterEmailReminder);
		
			// Mandar a solo Contratos Autorizados
//			BmFilter filterStatus = new BmFilter();
//			filterStatus.setValueFilter(bmoPropertyRental.getKind(), bmoPropertyRental.getStatus().getName(), ""+BmoPropertyRental.STATUS_AUTHORIZED);
//			filterPropertyRental.add(filterStatus);
		 
			
			Iterator<BmObject> propertyRentalIterator = this.list(filterPropertyRental).iterator();
			while(propertyRentalIterator.hasNext()) {
				bmoPropertyRental = (BmoPropertyRental) propertyRentalIterator.next();
				
				// Si no tiene fecha fin de contarto, no mandar nada
				if(!bmoPropertyRental.getEndDate().toString().equalsIgnoreCase("")) {		
					if((!bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDate().toString().equals("")) 
							|| (!bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDateTwo().toString().equals(""))) {
					
						//Vendedor 
						BmoUser bmoUser = new BmoUser();
						PmUser pmUser = new PmUser(getSFParams());
						bmoUser = (BmoUser) pmUser.get(bmoPropertyRental.getUserId().toInteger());
					
						//Arendador
						BmoCustomer bmoCustomer = new BmoCustomer();
						PmCustomer pmCustomer = new PmCustomer(getSFParams());
						bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertyRental.getBmoProperty().getCustomerId().toInteger());
						
						String Calnow = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
						
						int previousDate =  SFServerUtil.daysBetween(getSFParams().getDateFormat(), Calnow, bmoPropertyRental.getEndDate()
								.toString());
					
					
					
						
			
						if ((previousDate == bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDate().toInteger())
								|| previousDate == bmoPropertyRental.getBmoOrderType().getRemindDaysBeforeEndContractDateTwo().toInteger()) {
						
							
							String comments = "";
							this.sendMailRemindersContractEnd(bmoPropertyRental, bmoUser,bmoCustomer,comments);
						}				
					
					}
				}
			}
		
		
	}
	public void sendMailRemindersContractEnd(BmoPropertyRental bmoPropertyRental, BmoUser bmoSalesman, BmoCustomer bmoCustumer, String comments) throws SFException {
		if(bmoSalesman.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
			
			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
			
			mailList.add(new SFMailAddress(bmoSalesman.getEmail().toString(),
					bmoSalesman.getFirstname().toString() + " " + bmoSalesman.getFatherlastname().toString()));
			
			String subject = getSFParams().getAppCode() + " Recordatorio Fin de Contrato: "
					+ bmoPropertyRental.getCode().toString() + " " + bmoPropertyRental.getName().toString() ;
			
			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio  Fin de Contrato",
					" 	<p style=\"font-size:12px\"> " + " <b>Contrato:</b> " + bmoPropertyRental.getCode().toString()
							+ " " + bmoPropertyRental.getName().toString() 
							+ " <br> " + "  <b>Arrendador:</b> " + bmoCustumer.getDisplayName().toString()
							+ "	<br> " + "	<b>F. Inicio:</b> " + bmoPropertyRental.getStartDate().toString()
							+ "	<br> " + "	<b>F. Fin:</b> " + bmoPropertyRental.getEndDate().toString()
							+ "	<br> " + "	<b>Inmueble:</b> " + bmoPropertyRental.getBmoProperty().getCode().toString() + " " + bmoPropertyRental.getBmoProperty().getDescription().toString()
							+ "	<br> " + "	<b>Arrendatario:</b> "
							+ bmoPropertyRental.getBmoCustomer().getCode().toString() + " "
							+ bmoPropertyRental.getBmoCustomer().getDisplayName().toString() 							
							+ "	<br> " + "	<b>Correo:</b> " + bmoPropertyRental.getBmoCustomer().getEmail()
							+ "	<br> "	+ "	<b>Tel./M&oacute;vil del cliente:</b> "
							+ bmoPropertyRental.getBmoCustomer().getPhone().toString() + " / "
							+ bmoPropertyRental.getBmoCustomer().getMobile().toString() 
							+ "	<br> "
							+ "	<br> "
							+ "	<p align=\"left\" style=\"font-size:12px\"> "
							+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
							+ " y bórralo sin retener copia alguna." + "	</p> "
							);
					
			SFSendMail.send(getSFParams(),mailList,getSFParams().getBmoSFConfig().getEmail().toString(),
					getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
			
		}
		
	}
	//Para saber si un contrato ya fue renovado anteriormente
	public boolean isNotRenew(PmConn pmConn,int id) throws SFPmException {
		String sql = "SELECT prrt_propertiesrentid FROM propertiesrent where prrt_originrenewcontractid = " + id;
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			return false;
		}else {		
			return true;
		}
		
	}
	//Para saber si el inmueble esta disponible
	public boolean propertyAvailable(PmConn pmConn,int id) throws SFPmException {
		String sql = "SELECT prty_available FROM properties WHERE prty_propertyid = "+ id + " AND prty_available > 0";
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			return true;
		}else {
			return false;
		}
	}
	public boolean hasRenew(PmConn pmConn,int propertyRentId) throws SFPmException {
		String sql = "SELECT prrt_propertiesrentid FROM propertiesrent WHERE prrt_originrenewcontractid = " + propertyRentId;
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			return true;
		}else {
			return false;
		}
		
	}
	

}
