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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.ev.PmVenue;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.op.PmWhMovItem;
import com.flexwm.server.op.PmWhMovement;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectDetail;
import com.flexwm.shared.cm.BmoProjectGuideline;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFormat;

import com.symgae.shared.sf.BmoUser;


public class PmProject extends PmObject {
	BmoProject bmoProject;

	public PmProject(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProject = new BmoProject();
		setBmObject(bmoProject);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProject.getUserId(), bmoProject.getBmoUser()),
				new PmJoin(bmoProject.getOrderTypeId(), bmoProject.getBmoOrderType()),
				new PmJoin(bmoProject.getVenueId(), bmoProject.getBmoVenue()),
				new PmJoin(bmoProject.getBmoVenue().getCityId(), bmoProject.getBmoVenue().getBmoCity()),
				new PmJoin(bmoProject.getBmoVenue().getBmoCity().getStateId(), bmoProject.getBmoVenue().getBmoCity().getBmoState()),
				new PmJoin(bmoProject.getBmoVenue().getBmoCity().getBmoState().getCountryId(), bmoProject.getBmoVenue().getBmoCity().getBmoState().getBmoCountry()),
				new PmJoin(bmoProject.getCustomerId(), bmoProject.getBmoCustomer()),
				new PmJoin(bmoProject.getBmoCustomer().getTerritoryId(), bmoProject.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoProject.getBmoCustomer().getReqPayTypeId(), bmoProject.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoProject.getBmoUser().getAreaId(), bmoProject.getBmoUser().getBmoArea()),
				new PmJoin(bmoProject.getBmoUser().getLocationId(), bmoProject.getBmoUser().getBmoLocation()),
				new PmJoin(bmoProject.getWFlowTypeId(), bmoProject.getBmoWFlowType()),
				new PmJoin(bmoProject.getBmoWFlowType().getWFlowCategoryId(), bmoProject.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoProject.getWFlowId(), bmoProject.getBmoWFlow()),
				new PmJoin(bmoProject.getBmoWFlow().getWFlowPhaseId(), bmoProject.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoProject.getBmoWFlow().getWFlowFunnelId(), bmoProject.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de proyectos
		if (getSFParams().restrictData(bmoProject.getProgramCode())) {

			filters = "( proj_userid in (" +
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
					" proj_orderid IN ( " +
					" SELECT wflw_callerid FROM wflowusers  " +
					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId + 
					" AND (wflw_callercode = 'PROJ' OR wflw_callercode = 'ORDE') " + 
					"   ) " +
					" ) " +
					" ) ";
		}				

		// Filtro de proyectos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( proj_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " proj_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoProject bmoProject = (BmoProject) autoPopulate(pmConn, new BmoProject());

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
		if (orderTypeId > 0) bmoProject.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else bmoProject.setBmoOrderType(bmoOrderType);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoProject.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoProject.setBmoCustomer(bmoCustomer);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int UserId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (UserId > 0) bmoProject.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoProject.setBmoUser(bmoUser);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoProject.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoProject.setBmoWFlowType(bmoWFlowType);

		// BmoVenue
		BmoVenue bmoVenue = new BmoVenue();
		int VenueId = (int)pmConn.getInt(bmoVenue.getIdFieldName());
		if (VenueId > 0) bmoProject.setBmoVenue((BmoVenue) new PmVenue(getSFParams()).populate(pmConn));
		else bmoProject.setBmoVenue(bmoVenue);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoProject.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoProject.setBmoWFlow(bmoWFlow);

		return bmoProject;
	}

	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoProject bmoProject = (BmoProject)bmObject;	
		BmoProject bmoProjectPrev = new BmoProject();	
		PmProject pmProjectPrev = new PmProject(getSFParams());
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoProject.getId() > 0)) {
			super.save(pmConn, bmoProject, bmUpdateResult);
			bmoProject.setId(bmUpdateResult.getId());
			bmoProject.getCode().setValue(bmoProject.getCodeFormat());
			bmoProject.getDateCreateProject().setValue("" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

			if (!(bmoProject.getCompanyId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoProject.getCompanyId().getName(), "Debe Seleccionar una Empresa.");
			}

		} else {

			BmoOrder bmoOrder = new BmoOrder();
			bmoProjectPrev = (BmoProject)pmProjectPrev.get(bmoProject.getId());
			
			PmOrder pmOrder = new PmOrder(getSFParams());
			bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());
			PmConn pmconupdate = new PmConn(getSFParams());
			pmconupdate.open();
			PmConn pmcon = new PmConn(getSFParams());
			pmcon.open();
			
			if (!(bmoProject.getCustomerId().toInteger() == bmoProjectPrev.getCustomerId().toInteger())) {
				if ((bmoOrder.getPayments().toDouble()>0)) {
					bmUpdateResult.addError(bmoProject.getCustomerId().getName(), "No se puede cambiar el cliente,  existen pagos.");
				} else {
					String sql = " SELECT racc_raccountid FROM raccounts WHERE racc_orderid = "+bmoProjectPrev.getOrderId()+"";

					pmcon.doFetch(sql);
					while(pmcon.next()) {
						String update="UPDATE raccounts set racc_customerid = "+bmoProject.getCustomerId()+""
								+ " WHERE racc_raccountid = "+pmcon.getInt("racc_raccountid")+" ;";
						pmconupdate.doUpdate(update);
					}
				}
			}
		}

		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoProject.setBmoOrderType((BmoOrderType)pmOrderType.get(pmConn, bmoProject.getOrderTypeId().toInteger()));

		// Revisa fechas
		if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
				bmoProject.getEndDate().toString(), bmoProject.getStartDate().toString()))
			bmUpdateResult.addError(bmoProject.getEndDate().getName(), 
					"No puede ser Anterior a " + bmoProject.getStartDate().getLabel());

		// Revisa al menos una direccion seleccionada
		if (!(bmoProject.getVenueId().toInteger() > 0) && !(bmoProject.getCustomerAddressId().toInteger() > 0)) {
			bmUpdateResult.addError(bmoProject.getVenueId().getName(), 
					"Debe seleccionar un Lugar.");
		}
		
		// Si se esta cambiando el estatus a En Revision, se modifica para poder hacer el resto de procesos
		if (bmoProject.getStatus().toChar() == BmoProject.STATUS_REVISION)
			super.save(pmConn, bmoProject, bmUpdateResult);

		// Actualizar WFlow
		//		if (!bmUpdateResult.hasErrors()) {
		//			// Asigna el estatus al flujo
		//			char wFlowStatus = BmoWFlow.STATUS_ACTIVE;
		//			if (bmoProject.getStatus().equals(BmoProject.STATUS_FINISHED) ||
		//					bmoProject.getStatus().equals(BmoProject.STATUS_CANCEL))
		//				wFlowStatus = BmoWFlow.STATUS_INACTIVE;
		//			
		//			// Crea el WFlow y asigna el ID recien creado
		//			PmWFlow pmWFlow = new PmWFlow(getSFParams());
		//			bmoProject.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoProject.getWFlowTypeId().toInteger(), bmoProject.getWFlowId().toInteger(), 
		//					bmoProject.getProgramCode(), bmoProject.getId(), bmoProject.getUserId().toInteger(), 
		//					bmoProject.getCode().toString(), bmoProject.getName().toString(), bmoProject.getDescription().toString(), 
		//					bmoProject.getStartDate().toString(), bmoProject.getEndDate().toString(), wFlowStatus, bmUpdateResult).getId());
		//		}

		// Otras validaciones una vez asignado el tipo de proyecto
		if (!bmUpdateResult.hasErrors() && bmoProject.getWFlowTypeId().toInteger() > 0) {
			// Crear detalle del proyecto si no existe
			PmProjectDetail pmProjectDetail = new PmProjectDetail(getSFParams());
			if (!pmProjectDetail.projectDetailExists(pmConn, bmoProject.getId())) {
				BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
				bmoProjectDetail.getDeliveryDate().setValue(bmoProject.getStartDate().toString());
				bmoProjectDetail.getPrepDate().setValue(bmoProject.getStartDate().toString());
				bmoProjectDetail.getLoadStartDate().setValue(bmoProject.getStartDate().toString());
				bmoProjectDetail.getExitDate().setValue(bmoProject.getStartDate().toString());

				bmoProjectDetail.getUnloadStartDate().setValue(bmoProject.getEndDate().toString());
				bmoProjectDetail.getReturnDate().setValue(bmoProject.getEndDate().toString());

				bmoProjectDetail.getProjectId().setValue(bmoProject.getId());
				pmProjectDetail.save(pmConn,  bmoProjectDetail, bmUpdateResult);
			}

			// Crear orden del dia del proyecto si no existe
			PmProjectGuideline pmProjectGuideline = new PmProjectGuideline(getSFParams());
			if (!pmProjectGuideline.projectGuidelineExists(pmConn, bmoProject.getId())) {
				BmoProjectGuideline bmoProjectGuideline = new BmoProjectGuideline();
				bmoProjectGuideline.getGuidelines().setValue("");
				bmoProjectGuideline.getProjectId().setValue(bmoProject.getId());
				pmProjectGuideline.save(pmConn, bmoProjectGuideline, bmUpdateResult);
			}

			// Crear pedido si no existe
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			int wflowId = 0;


			if (!(bmoProject.getOrderId().toInteger() > 0)) {
				// Si no existe el pedido, crearlo
				createOrderFromProject(pmConn, pmOrder, bmoOrder, bmoProject, bmUpdateResult);
				bmoProject.getTotal().setValue(bmoOrder.getTotal().toString());
			} else {
				// Actualiza info del pedido, porque es existente
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());
				wflowId = bmoOrder.getWFlowId().toInteger();
				
				// Validar que si esta cambiando el mercado, verifique si hay lineas con productos
				if (bmoProjectPrev.getMarketId().toInteger() > 0) {
					if (bmoProject.getMarketId().toInteger() != bmoProjectPrev.getMarketId().toInteger()) {
						if (pmOrder.existProducts(pmConn, bmoOrder.getId()))
							bmUpdateResult.addError(bmoProject.getMarketId().getName(), "El Pedido tiene Productos con Precios asignados al(la) " + getSFParams().getFieldFormTitle(bmoProject.getMarketId()) + " actual. "
											+ "Debe eliminarlos para poder cambiar el(la) " + getSFParams().getFieldFormTitle(bmoProject.getMarketId()) + "." );
					}
				}

				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					bmoOrder.getName().setValue(bmoProject.getName().toString());
					bmoOrder.getUserId().setValue(bmoProject.getUserId().toInteger());				
					bmoOrder.getLockStart().setValue(bmoProject.getStartDate().toString());
					bmoOrder.getLockEnd().setValue(bmoProject.getEndDate().toString());
					bmoOrder.getCurrencyId().setValue(bmoProject.getCurrencyId().toInteger());
					bmoOrder.getCurrencyParity().setValue(bmoProject.getCurrencyParity().toDouble());
					bmoOrder.getCustomerId().setValue(bmoProject.getCustomerId().toInteger());
					bmoOrder.getCompanyId().setValue(bmoProject.getCompanyId().toInteger());				
	
					pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
					
				//  Si la cotizacion ya esta autorizada, no dejar cambiar los mismos campos de la oportunidad y cotizacion
				} else if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {

					if (!bmoOrder.getName().toString().equals(bmoProject.getName().toString())) {
						bmUpdateResult.addError(bmoProject.getName().getName() , "No se puede cambiar el Nombre de la Oportunidad, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCustomerId().toInteger() != bmoProject.getCustomerId().toInteger()) {
						bmUpdateResult.addError(bmoProject.getCustomerId().getName() , "No se puede cambiar el Cliente/Prospecto, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getUserId().toInteger() != bmoProject.getUserId().toInteger()) {
						bmUpdateResult.addError(bmoProject.getUserId().getName() , "No se puede cambiar el Vendedor, el Pedido ya está Autorizado");
					} else if (!bmoOrder.getLockStart().toString().equals(bmoProject.getStartDate().toString())) {
						bmUpdateResult.addError(bmoProject.getStartDate().getName() , "No se puede cambiar la Fecha Inicio, el Pedido ya está Autorizado");
					} else if (!bmoOrder.getLockEnd().toString().equals(bmoProject.getEndDate().toString())) {
						bmUpdateResult.addError(bmoProject.getEndDate().getName() , "No se puede cambiar la Fecha Fin, el Pedido ya está Autorizado");
					} else if (bmoOrder.getCurrencyId().toInteger() == bmoProject.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() != bmoProject.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoProject.getCurrencyParity().getName() , "No se puede cambiar el Tipo de Cambio, el Pedido ya está Autorizado");
					} else if (bmoOrder.getCurrencyId().toInteger() != bmoProject.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() == bmoProject.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoProject.getCurrencyId().getName() , "No se puede cambiar la Moneda, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCurrencyId().toInteger() != bmoProject.getCurrencyId().toInteger()
							&& bmoOrder.getCurrencyParity().toDouble() != bmoProject.getCurrencyParity().toDouble()) {
						bmUpdateResult.addError(bmoProject.getCurrencyId().getName() , "No se puede cambiar la Moneda/Tipo de Cambio, el Pedido ya está Autorizado");
					} else if  (bmoOrder.getCompanyId().toInteger() != bmoProject.getCompanyId().toInteger()) {
						bmUpdateResult.addError(bmoProject.getCompanyId().getName() , "No se puede cambiar la Empresa, v");
					} 
				}
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

				// Asigna el estatus al pedido
				if (bmoProject.getStatus().equals(BmoProject.STATUS_CANCEL)) {
					bmoOrder.getStatus().setValue(BmoOrder.STATUS_CANCELLED);
					if (bmoOrder.getWFlowId().toInteger() > 0)
						pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Proyecto se guardó como Cancelado.");
					pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

				} else if (bmoProject.getStatus().equals(BmoProject.STATUS_FINISHED)) {
					consumableActions(bmoOrder,getSFParams()); 
					bmoOrder.getStatus().setValue(BmoOrder.STATUS_FINISHED);
					
					if (bmoOrder.getWFlowId().toInteger() > 0)						
						pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Proyecto se guardó como Finalizado.");
					   
					pmOrder.save(pmConn, bmoOrder, bmUpdateResult);					
					
					
				} else if (bmoProject.getStatus().equals(BmoProject.STATUS_REVISION)) {
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Proyecto se guardó como Revisión.");

				} else if (bmoProject.getStatus().equals(BmoProject.STATUS_AUTHORIZED)) {
					pmWFlowLog.addLog(pmConn, bmUpdateResult, wflowId, BmoWFlowLog.TYPE_OTHER, "El Proyecto se guardó como Autorizado.");
				}
			}

			// Asigna valor del pedido
			bmoProject.getOrderId().setValue(bmoOrder.getId());			

			// Asigna ID del flujo recien creado
			if (wflowId > 0)
				bmoProject.getWFlowId().setValue(wflowId);
			else
				bmoProject.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());

			super.save(pmConn, bmoProject, bmUpdateResult);
			

		}

		// Actualiza estatus del cliente
		if (!bmUpdateResult.hasErrors() && bmoProject.getWFlowTypeId().toInteger() > 0) {
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoProject.getCustomerId().toInteger());
			pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
		}

		// Si se esta autorizando el proyecto, pero tiene ordenes de compra no autorizadas, evitar autorización
		if (!bmUpdateResult.hasErrors()) {
			if (bmoProject.getStatus().toChar() == BmoProject.STATUS_AUTHORIZED) {
				if (bmoProject.getOrderId().toInteger() > 0) {
					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					if (pmRequisition.hasPendingRequisitionsByOrder(pmConn, bmoProject.getOrderId().toInteger()))
						bmUpdateResult.addMsg("No se puede Autorizar: Tiene Órdenes de Compra En Revisión.");
				}
			}
		}

		// Se almacena el proyecto
		if (!bmUpdateResult.hasErrors()) {
			// Guarda el proyecto
			super.save(pmConn, bmoProject, bmUpdateResult);

			// Forzar el ID del resultado que sea del proyecto y no de otra clase
			bmUpdateResult.setId(bmoProject.getId());

			//Obtener el Pedido
			//			if (bmoProject.getOrderId().toInteger() > 0){
			//				PmOrder pmOrder = new PmOrder(getSFParams());
			//				BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());
			//			}
		}
		//Actualizar estatus de seccion de almacen
		if (!bmUpdateResult.hasErrors()) {
			updateWhSection(pmConn,bmoProject, bmUpdateResult);
		}
		// Forzar el ID del resultado que sea del proyecto y no de otra clase
		bmUpdateResult.setId(bmoProject.getId());

		return bmUpdateResult;
	}
	
	public BmUpdateResult updateWhSection(PmConn pmConn,BmoProject bmoProject,BmUpdateResult bmUpdateResult) throws SFException {
//		PmWhSection pmWhSection = new PmWhSection(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();
		String sql = "SELECT orde_orderid,whse_whsectionid FROM orders "
				+ " LEFT JOIN whsections ON (whse_orderid = orde_orderid) "
				+ " WHERE orde_orderid = " + bmoProject.getOrderId().toInteger() + " OR orde_originreneworderid = " + bmoProject.getOrderId().toInteger();
		pmConn2.doFetch(sql);
		System.err.println(sql);
		while (pmConn2.next()) {
			String status = "";
//			BmoWhSection nextBmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn, pmConn.getInt("orde_orderid"), new BmoWhSection().getOrderId().getName());
			
//			if (bmoProject.getStatus().equals(BmoProject.STATUS_FINISHED)) {
//				nextBmoWhSection.getStatus().setValue(BmoWhSection.STATUS_INACTIVE);
//			} else {
//				nextBmoWhSection.getStatus().setValue(BmoWhSection.STATUS_ACTIVE);
//			}
//			pmWhSection.save(pmConn, nextBmoWhSection, bmUpdateResult);
			//se hara un insert directo con (solo guarda la primera seccion y se detiene) se analisara
			
			if (bmoProject.getStatus().equals(BmoProject.STATUS_FINISHED)) {
				status = ""+BmoWhSection.STATUS_INACTIVE;
			} else {
				status = ""+BmoWhSection.STATUS_ACTIVE;
			}
			
			sql = "UPDATE whsections SET whse_status = '" + status + "' where whse_whsectionid = " + pmConn2.getInt("whse_whsectionid");
			
			pmConn.doUpdate(sql);
		}
		pmConn2.close();
		
		return bmUpdateResult;
	}

	public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, BmUpdateResult bmUpdateResult) throws SFException {
		BmoProject bmoProject = new BmoProject();
		bmoProject.getName().setValue(bmoOpportunity.getName().toString());
		bmoProject.getDescription().setValue(bmoOpportunity.getDescription().toString());
		bmoProject.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
		bmoProject.getEndDate().setValue(bmoOpportunity.getEndDate().toString());		
		bmoProject.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoProject.getUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoProject.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoProject.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoProject.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
		bmoProject.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
		bmoProject.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
		bmoProject.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
		bmoProject.getMarketId().setValue(bmoOpportunity.getMarketId().toInteger());
		bmoProject.getCustomerAddressId().setValue(bmoOpportunity.getCustomerAddressId().toInteger());
		
		// Pasar Domicilio particular en caso de el salon tenga check de casa particular
		BmoVenue bmoVenue = new BmoVenue();
		PmVenue pmVenue = new PmVenue(getSFParams());
		if (bmoOpportunity.getVenueId().toInteger() > 0 ) {
			bmoProject.getVenueId().setValue(bmoOpportunity.getVenueId().toInteger());
			bmoVenue = (BmoVenue)pmVenue.get(bmoOpportunity.getVenueId().toInteger());
			if ((bmoVenue.getHomeAddress().toInteger() > 0))
				bmoProject.getHomeAddress().setValue(bmoOpportunity.getCustomField4().toString());
		}		

		// Si no esta asignado el lugar, utilizar el de la oportunidad de detalle
		PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
		BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
		bmoOpportunityDetail = (BmoOpportunityDetail)pmOpportunityDetail.getBy(pmConn, bmoOpportunity.getId(), bmoOpportunityDetail.getOpportunityId().getName());
		
		bmoProject.getGuests().setValue(bmoOpportunityDetail.getGuests().toInteger());

		bmoProject.getOpportunityId().setValue(bmoOpportunity.getId());

	
		return this.save(pmConn, bmoProject, bmUpdateResult);
	}

	//	private void createWFlowUsersFromOpportunity(PmConn pmConn, BmoProject bmoProject, BmUpdateResult bmUpdateResult) throws SFException {
	//		// Obtener los usuarios de WFlow de la oportunidad
	//		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
	//		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(bmoProject.getOpportunityId().toInteger());
	//		
	//		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
	//		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
	//		BmFilter bmFilter = new BmFilter();
	//		bmFilter.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
	//		Iterator<BmObject> userList = pmWFlowUser.list(bmFilter).iterator();
	//		
	//		// Crear los usuarios en el nuevo proyecto
	//		while (userList.hasNext()) {
	//			bmoWFlowUser = (BmoWFlowUser)userList.next();
	//			
	//			// Revisar si ya hay registro esperando asignacion de usuario
	//			BmoWFlowUser projectWFlowUser = pmWFlowUser.getUnassignedByGroup(pmConn, bmoProject.getWFlowId().toInteger(), bmoWFlowUser.getProfileId().toInteger());
	//			if (projectWFlowUser.getId() > 0) {
	//				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
	//			} else {
	//				// Es nuevo, asignar valores
	//				projectWFlowUser.getWFlowId().setValue(bmoProject.getWFlowId().toInteger());
	//				projectWFlowUser.getUserId().setValue(bmoWFlowUser.getUserId().toInteger());
	//				projectWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);
	//				projectWFlowUser.getLockStart().setValue(bmoProject.getStartDate().toString());
	//				projectWFlowUser.getLockEnd().setValue(bmoProject.getEndDate().toString());
	//				projectWFlowUser.getRequired().setValue(bmoWFlowUser.getRequired().toString());
	//				projectWFlowUser.getAutoDate().setValue(bmoWFlowUser.getAutoDate().toString());
	//				projectWFlowUser.getProfileId().setValue(bmoWFlowUser.getProfileId().toInteger());
	//			}
	//			
	//			// Si no es igual al vendedor del proyecto, asignarlo, para no duplicar asignaciones
	//			if (projectWFlowUser.getUserId().toInteger() != bmoProject.getUserId().toInteger())
	//				pmWFlowUser.save(pmConn, projectWFlowUser, bmUpdateResult);
	//		}
	//	}

	//	private void createWFlowDocumentsFromOpportunity(PmConn pmConn, BmoProject bmoProject, BmUpdateResult bmUpdateResult) throws SFException {
	//		// Obtener los documentos de WFlow de la oportunidad
	//		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
	//		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(bmoProject.getOpportunityId().toInteger());
	//		
	//		PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(getSFParams());
	//		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
	//		BmFilter bmFilter = new BmFilter();
	//		bmFilter.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
	//		Iterator<BmObject> userList = pmWFlowDocument.list(bmFilter).iterator();
	//		
	//		// Crear los usuarios en el nuevo proyecto
	//		while (userList.hasNext()) {
	//			bmoWFlowDocument = (BmoWFlowDocument)userList.next();
	//			
	//			BmoWFlowDocument projectWFlowDocument = new BmoWFlowDocument();
	//			projectWFlowDocument.getWFlowId().setValue(bmoProject.getWFlowId().toInteger());
	//			projectWFlowDocument.getName().setValue(bmoWFlowDocument.getName().toString());
	//			projectWFlowDocument.getFile().setValue(bmoWFlowDocument.getFile().toString());
	//			projectWFlowDocument.getIsUp().setValue(bmoWFlowDocument.getIsUp().toString());
	//			projectWFlowDocument.getRequired().setValue(bmoWFlowDocument.getRequired().toString());
	//			
	//			pmWFlowDocument.save(pmConn, projectWFlowDocument, bmUpdateResult);
	//		}
	//	}

	private void createOrderFromProject(PmConn pmConn, PmOrder pmOrder, BmoOrder bmoOrder, BmoProject bmoProject, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear pedido a partir de proyecto
		bmoOrder.getCode().setValue(bmoProject.getCode().toString());
		bmoOrder.getName().setValue(bmoProject.getName().toString());
		bmoOrder.getDescription().setValue(bmoProject.getDescription().toString());
		bmoOrder.getAmount().setValue(0);
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getOrderTypeId().setValue(bmoProject.getOrderTypeId().toInteger());
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoProject.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoProject.getEndDate().toString());
		bmoOrder.getTags().setValue(bmoProject.getTags().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoProject.getCustomerId().toInteger());
		bmoOrder.getUserId().setValue(bmoProject.getUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoProject.getWFlowTypeId().toInteger());
		bmoOrder.getCompanyId().setValue(bmoProject.getCompanyId().toInteger());
		bmoOrder.getMarketId().setValue(bmoProject.getMarketId().toInteger());

		// Obtener la moneda de la oportunidad si existe, en caso contrario de la configuracion
		if (bmoProject.getOpportunityId().toInteger() > 0) {
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoProject.getOpportunityId().toInteger());

			bmoOrder.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
			bmoOrder.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().toDouble());
			bmoOrder.getQuoteId().setValue(bmoOpportunity.getQuoteId().toInteger());
			bmoOrder.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
			bmoOrder.getOpportunityId().setValue(bmoOpportunity.getId());

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
			bmoOrder.getCurrencyId().setValue(bmoProject.getCurrencyId().toInteger());
			bmoOrder.getCurrencyParity().setValue(bmoProject.getCurrencyParity().toDouble());
			
			// Si esta habilitado el Control presp. pasar por defecto la partida y el dpto. del tipo de pedido
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				PmOrderType pmOrderType = new PmOrderType(getSFParams());
				BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoProject.getOrderTypeId().toInteger());
				
				if (!(bmoOrderType.getDefaultBudgetItemId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoOrderType.getDefaultBudgetItemId().getName(), "Seleccione una Partida Presupuestal en el Tipo de Pedido.");
				else {
					bmoOrder.getDefaultBudgetItemId().setValue(bmoOrderType.getDefaultBudgetItemId().toInteger());
					bmoOrder.getDefaultAreaId().setValue(bmoOrderType.getDefaultAreaId().toInteger());
				}
			}
		}

		pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

		// Si el proyecto proviene de una oportunidad
		if (bmoProject.getOpportunityId().toInteger() > 0) {

			// Obtener los items de las cotizaciones
			pmOrder.createOrderItemsFromQuote(pmConn, bmoOrder, bmUpdateResult);

			// Crear las CxC necesarias para sustentar el saldo automaticamente
			pmOrder.createOrderRaccountsAuto(pmConn, bmoOrder, bmoProject.getOpportunityId().toInteger(), bmUpdateResult);

			// Obtener los usuarios de WFlow de la oportunidad
			pmOrder.createWFlowUsersFromOpportunity(pmConn, bmoOrder, bmUpdateResult);

			// Obtener los documentos de WFlow de la oportunidad
			pmOrder.createWFlowDocumentsFromOpportunity(pmConn, bmoOrder, bmUpdateResult);
			
		new PmOpportunity(getSFParams()).updateRequisitions(pmConn, bmoProject.getOpportunityId().toInteger(), bmoOrder, bmUpdateResult);
			
		}
	}

	public BmoProject getByWFlowId(int wFlowId) throws SFException {
		return (BmoProject)super.getBy(wFlowId, bmoProject.getWFlowId().getName());
	}

	// Revisar si existe la orden de algún proyecto
	public boolean projectOrderExists(PmConn pmConn, int orderId) throws SFPmException {
		pmConn.doFetch("SELECT proj_orderid from projects where proj_orderid = " + orderId);
		System.err.println("SELECT proj_orderid from projects where proj_orderid = " + orderId);
		return pmConn.next();
	}

	public boolean projectOpportunityExists(PmConn pmConn, int opportunityId) throws SFPmException {
		pmConn.doFetch("SELECT proj_opportunityid FROM projects WHERE proj_opportunityid = " + opportunityId);
		return pmConn.next();
	}

	// Se encuentra liberado el Pedido
	public BmUpdateResult isProjectReleased(BmoFormat bmoFormat, BmoProject bmoProject, BmUpdateResult bmUpdateResult) throws SFException{
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			// Revisa si existe proyecto autorizado
			if (bmoProject.getStatus().toChar() == BmoProject.STATUS_AUTHORIZED) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = new BmoOrder();
				bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());
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
			bmoProject = (BmoProject)this.get(bmObject.getId());
		if (action.equalsIgnoreCase(BmoProject.ACTION_SENDPOLL)) {
			sendPollCustomer(pmConn, bmoProject, bmUpdateResult);
		} 

		return bmUpdateResult;
	}

	// Enviar encuesta al cliente
	private void sendPollCustomer(PmConn pmConn, BmoProject bmoProject, BmUpdateResult bmUpdateResult) throws SFException {
		int countPollCustomer = 0;
		//Validar si existe una encuesta liga al proyecto.
		String sql = "SELECT COUNT(cupo_projectid) AS countprojects FROM customerpolls " +
				" WHERE cupo_projectid = " + bmoProject.getId();
		pmConn.doFetch(sql);
		if(pmConn.next())
			countPollCustomer = pmConn.getInt("countprojects");

		if(countPollCustomer > 0){
			bmUpdateResult.addError(bmoProject.getCode().getName(), "Ya existe una Encuesta ligada al Proyecto");
		}else{
			WFlowProjectPollEmail wFlowProjectPollEmail = new WFlowProjectPollEmail();
			wFlowProjectPollEmail.sendPollMail(getSFParams(), bmoProject);
		}
	}

	//Copiar un proyecto
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		if(!action.equals(BmoProject.ACTION_GETSUMTOTAL))
			bmoProject = (BmoProject)this.get(bmObject.getId());

		if (action.equals(BmoProject.ACCESS_COPYPROJECT)) {
			PmProject pmProject = new PmProject(getSFParams());
			BmoProject bmoProject = new BmoProject();
			bmoProject = (BmoProject)pmProject.getBy(this.bmoProject.getId(), this.bmoProject.getIdFieldName());
			bmUpdateResult.setBmObject(bmoProject);
			if(bmoProject.getId() > 0) {
				if(value.isEmpty() || Integer.parseInt(value) < 0)
					bmUpdateResult.addError(bmoProject.getIdFieldName(), "Debe seleccionar un cliente");
				else
					bmUpdateResult = copyProject( bmoProject, Integer.parseInt(value), bmUpdateResult);
			}
			else
				bmUpdateResult.addError(bmoProject.getIdFieldName(), "Debe seleccionar proyecto");
		}else if (action.equalsIgnoreCase(BmoProject.ACTION_GETSUMTOTAL)) {
			bmUpdateResult.addMsg(getSumTotal(Integer.parseInt(value)));
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoProject = (BmoProject)bmObject;

		if (bmoProject.getStatus().toChar() == BmoProject.STATUS_REVISION) {
			// Eliminar datos adicionales
			PmProjectDetail pmProjectDetail = new PmProjectDetail(getSFParams());
			BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
			BmFilter filterByProject = new BmFilter();
			filterByProject.setValueFilter(bmoProjectDetail.getKind(), bmoProjectDetail.getProjectId(), bmoProject.getId());
			ListIterator<BmObject> projectDetailList = pmProjectDetail.list(pmConn, filterByProject).listIterator();
			while (projectDetailList.hasNext()) {
				bmoProjectDetail = (BmoProjectDetail)projectDetailList.next();
				pmProjectDetail.delete(pmConn,  bmoProjectDetail, bmUpdateResult);
			}	

			// Eliminar encuestas
			pmConn.doUpdate("DELETE FROM customerpolls WHERE cupo_projectid = " + bmoProject.getId());

			// Eliminar orden del dia
			pmConn.doUpdate("DELETE FROM projectguidelines WHERE pjgi_projectid = " + bmoProject.getId());

			// Eliminar proyecto
			super.delete(pmConn, bmoProject, bmUpdateResult);

			// Eliminar pedidos
			if (!bmUpdateResult.hasErrors()) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				if (bmoProject.getOrderId().toInteger() > 0) {
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());

					// Eliminar ordenes de compra ligadas al pedido
					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					BmoRequisition bmoRequisition = new BmoRequisition();
					filterByProject.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(), bmoOrder.getId());
					ListIterator<BmObject> requisitionList = pmRequisition.list(pmConn, filterByProject).listIterator();
					while (requisitionList.hasNext()) {
						bmoRequisition = (BmoRequisition)requisitionList.next();
						pmRequisition.delete(pmConn,  bmoRequisition, bmUpdateResult);
					}

					pmOrder.delete(pmConn,  bmoOrder, bmUpdateResult);
				}
			}

			//			if (!bmUpdateResult.hasErrors()) {
			//				// Eliminar flujos
			//				PmWFlow pmWFlow = new PmWFlow(getSFParams());
			//				BmoWFlow bmoWFlow = new BmoWFlow();
			//				// Busca por PEDIDO
			//				filterByProject.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoProject.getOrderId().toInteger());			
			//				BmFilter filterWFlowCategory = new BmFilter();
			//				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoProject.getProgramCode());
			//				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			//				filterList.add(filterByProject);
			//				filterList.add(filterWFlowCategory);
			//				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
			//				while (wFlowList.hasNext()) {
			//					bmoWFlow = (BmoWFlow)wFlowList.next();
			//					pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
			//				}
			//			}

		} else {
			String status = "Autorizado";
			if (bmoProject.getStatus().toChar() == BmoProject.STATUS_CANCEL)
				status = "Cancelado";
			else if (bmoProject.getStatus().toChar() == BmoProject.STATUS_FINISHED)
				status = "Finalizado";
			
			bmUpdateResult.addError(bmoProject.getStatus().getName(), "No se puede eliminar, el Proyecto está " + status);
		}

		return bmUpdateResult;
	}
	
//project copy
	
	public BmUpdateResult copyProject(BmoProject bmoProject, int idCustomer, BmUpdateResult bmUpdateResult)
			throws SFException {
		
		BmoCustomer bmoCustomer = new BmoCustomer();
		BmoOrder bmoOrderOld = new BmoOrder();
		try {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			
			BmoProject newBmoProject = new BmoProject();
			PmProject pmProject = new PmProject(getSFParams());
			
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			bmoCustomer = (BmoCustomer) pmCustomer.get(idCustomer);			
			PmOrder pmOrder = new PmOrder(getSFParams());
			bmoOrderOld = (BmoOrder) pmOrder.get(bmoProject.getOrderId().toInteger());

			newBmoProject.getStatus().setValue(BmoProject.STATUS_REVISION);
			newBmoProject.getName().setValue(bmoProject.getName().toString());
			newBmoProject.getDescription().setValue(bmoProject.getDescription().toString() + "-COPIA_"+bmoProject.getCode().toString());
			newBmoProject.getComments().setValue(bmoProject.getComments().toString());
			newBmoProject.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			newBmoProject.getEndDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			newBmoProject.getCustomerId().setValue(bmoCustomer.getId());
			newBmoProject.getDateCreate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			newBmoProject.getCompanyId().setValue(bmoProject.getCompanyId().toString());
			newBmoProject.getOrderTypeId().setValue(bmoProject.getOrderTypeId().toInteger());
			newBmoProject.getWFlowTypeId().setValue(bmoProject.getWFlowTypeId().toString());
			newBmoProject.getWFlowId().setValue(bmoProject.getWFlowId().toString());
			newBmoProject.getHomeAddress().setValue(bmoProject.getHomeAddress().toString());
			newBmoProject.getUserId().setValue(bmoProject.getUserId().toInteger());
			newBmoProject.getCurrencyId().setValue(bmoProject.getCurrencyId().toInteger());
			newBmoProject.getCurrencyParity().setValue(bmoProject.getCurrencyParity().toString());
			newBmoProject.getWarehouseManagerId().setValue(bmoProject.getWarehouseManagerId().toInteger());
			newBmoProject.getMarketId().setValue(bmoProject.getMarketId().toString());
			newBmoProject.getVenueId().setValue(bmoProject.getVenueId().toString());
			newBmoProject.getCustomerAddressId().setValue(bmoProject.getCustomerAddressId().toInteger());
			newBmoProject.getGeventId().setValue(bmoProject.getGeventId().toString());
			newBmoProject.getGuests().setValue(bmoProject.getGuests().toString());
			
			pmProject.save(pmConn, newBmoProject, bmUpdateResult);
			BmoOrder bmoOrder = (BmoOrder) pmOrder.get(newBmoProject.getOrderId().toInteger());
			pmOrder.copyOrderItemsFromOrder(pmConn, bmoOrderOld.getId(), bmoOrder, bmUpdateResult);

			bmUpdateResult.setBmObject(pmProject.get(newBmoProject.getId()));
			pmConn.close();
		} catch (SFException e) {
			throw new SFException("PmProject-copyProject() ERROR: " + e.toString());
		}
		return bmUpdateResult;
	}
	
	//crear TX Inventario salida (productos consumibles)
	public void consumableActions(BmoOrder bmoOrder,SFParams sfParams) throws SFException  {
		
	
		PmConn pmConn2 = new PmConn(sfParams);
		String sql = "";
		PmConn pmConn = new PmConn(sfParams);	
		pmConn.open();
		if (bmoOrder.getId() > 0) {		
			
			sql = "SELECT * FROM orderdeliveryitems " + 
					"	LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " + 
					"	LEFT JOIN orders ON (odly_orderid = orde_orderid) " + 
					"	LEFT JOIN products ON (odyi_productid = prod_productid) " + 
					"	LEFT JOIN projects ON (proj_orderid = orde_orderid) " + 
					"   LEFT JOIN orderitems ON (ordi_orderitemid = odyi_orderitemid) " +
					"	WHERE  prod_consumable = 1 AND  " + 
					"	(proj_orderid = "+bmoOrder.getId()+" OR orde_originreneworderid = "+bmoOrder.getId()+") " + 
					"	and odly_type = '"+BmoOrderDelivery.TYPE_RETURN+"'";
					//"' AND odyi_quantity > 0";
			if(!getSFParams().isProduction()) 
				printDevLog("orderDeliveryItems SQL- " +sql);
			
			pmConn.doFetch(sql);
			
			
			while(pmConn.next()) {
				BmUpdateResult bmUpdateResult;
				BmoWhMovement bmoWhMovement = new BmoWhMovement();
				BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
				PmWhMovement pmWhMovement = new PmWhMovement(sfParams);
				PmWhMovItem pmWhMovItem = new PmWhMovItem(sfParams);
				
				bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_OUT_ADJUST);
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);
				bmoWhMovement.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
				bmoWhMovement.getDescription().setValue(bmoOrder.getName().toString());
				bmoWhMovement.getDatemov().setValue(SFServerUtil.nowToString(sfParams, sfParams.getDateFormat()));
			
				bmUpdateResult =  pmWhMovement.save(bmoWhMovement, new BmUpdateResult());
			
				bmoWhMovement = (BmoWhMovement)pmWhMovement.get(bmUpdateResult.getBmObject().getId());
				String serial = "";
				String sql2 = " SELECT whtr_serial FROM whtracks "+
					          " LEFT JOIN whstocks ON (whst_whtrackid = whtr_whtrackid) "+
						      " WHERE whst_whsectionid = "+pmConn.getString("odyi_fromwhsectionid")+
						      " AND whst_productid = " + pmConn.getString("odyi_productid");
				pmConn2.open();
				pmConn2.doFetch(sql2);
				if(pmConn2.next()) {
					serial = pmConn2.getString("whtr_serial");
				}
				int WhMovement = bmoWhMovement.getId();
				double quantity = pmConn.getDouble("ordi_quantity");
				quantity = quantity -(pmConn.getDouble("odyi_quantitybalance"))-pmConn.getDouble("odyi_quantityreturned");
				bmoWhMovItem.getQuantity().setValue(quantity);
				bmoWhMovItem.getFromWhSectionId().setValue(pmConn.getInt("odyi_fromwhsectionid"));
				bmoWhMovItem.getProductId().setValue(pmConn.getInt("odyi_productid"));
				bmoWhMovItem.getSerial().setValue(serial);
				bmoWhMovItem.getWhMovementId().setValue(WhMovement);
				bmoWhMovItem.getAmount().setValue(0);
				pmWhMovItem.save(bmoWhMovItem, bmUpdateResult);
				if(!getSFParams().isProduction()) 
					printDevLog("MIS RESULTADOS  "+bmUpdateResult.errorsToString());
				
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
				pmWhMovement.save(bmoWhMovement, new BmUpdateResult());
				
				pmConn2.close();
			}
//			if(pmConn.next()) {
//				
//				bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_OUT_ADJUST);
//				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);
//				bmoWhMovement.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
//				bmoWhMovement.getDescription().setValue(bmoOrder.getName().toString());
//				bmoWhMovement.getDatemov().setValue(SFServerUtil.nowToString(sfParams, sfParams.getDateFormat()));
//				
//				bmUpdateResult =  pmWhMovement.save(bmoWhMovement, new BmUpdateResult());
//				
//				bmoWhMovement = (BmoWhMovement)pmWhMovement.get(bmUpdateResult.getBmObject().getId());
//				
//				int WhMovement = bmoWhMovement.getId();
//				
//				pmConn.doFetch(sql);
//				while(pmConn.next()) {
//					double quantity = pmConn.getDouble("ordi_quantity");
//					
//					quantity = quantity -(pmConn.getDouble("odyi_quantitybalance"))-pmConn.getDouble("odyi_quantityreturned");
//					printDevLog("CANTIDAD "+ quantity);
//					bmoWhMovItem.getQuantity().setValue(quantity);
//					bmoWhMovItem.getFromWhSectionId().setValue(pmConn.getInt("odyi_fromwhsectionid"));
//					bmoWhMovItem.getProductId().setValue(pmConn.getInt("odyi_productid"));
//					bmoWhMovItem.getSerial().setValue(pmConn.getString("odyi_serial"));
//					bmoWhMovItem.getWhMovementId().setValue(WhMovement);
//					bmoWhMovItem.getAmount().setValue(0);
//					pmWhMovItem.save(bmoWhMovItem, bmUpdateResult);
//					printDevLog(bmUpdateResult.errorsToString());
//					
//				}
//				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
//				pmWhMovement.save(bmoWhMovement, new BmUpdateResult());
//			}
		}
		pmConn.close();
	}
	public boolean projectOrderExists1(PmConn pmConn,int orderId) throws SFPmException {
		pmConn.doFetch("SELECT proj_orderid FROM " + formatKind("projects") + " WHERE proj_orderid= " + orderId);
		return pmConn.next();
	}
	private String getSumTotal(int userId) throws SFPmException {
		String sql = "SELECT sum( proj_total) as total FROM projects WHERE (  ( proj_status = '" + BmoProject.STATUS_REVISION + "'  )  )   ";
		if (userId > 0)sql += "  AND proj_userid = " + userId;
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
