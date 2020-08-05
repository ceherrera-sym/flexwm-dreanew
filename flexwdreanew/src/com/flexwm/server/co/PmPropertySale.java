/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmLoseMotive;
import com.flexwm.server.cm.PmOpportunity;
import com.flexwm.server.cm.PmQuote;
import com.flexwm.server.cm.PmQuotePropertyModelExtra;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuotePropertyModelExtra;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.co.BmoOrderPropertyModelExtra;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertySaleDetail;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.sf.BmoCompany;

import com.symgae.shared.sf.BmoUser;


public class PmPropertySale extends PmObject {
	BmoPropertySale bmoPropertySale;
	BmoOrder bmoOrder;

	public PmPropertySale(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPropertySale = new BmoPropertySale();
		setBmObject(bmoPropertySale);

		bmoOrder = new BmoOrder();

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertySale.getSalesUserId(), bmoPropertySale.getBmoSalesUser()),
				new PmJoin(bmoPropertySale.getCustomerId(), bmoPropertySale.getBmoCustomer()),
				new PmJoin(bmoPropertySale.getBmoCustomer().getTerritoryId(), bmoPropertySale.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoPropertySale.getBmoCustomer().getReqPayTypeId(), bmoPropertySale.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoPropertySale.getPropertyId(), bmoPropertySale.getBmoProperty()),
				new PmJoin(bmoPropertySale.getBmoProperty().getDevelopmentBlockId(), bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase()),
				new PmJoin(bmoPropertySale.getBmoSalesUser().getAreaId(), bmoPropertySale.getBmoSalesUser().getBmoArea()),
				new PmJoin(bmoPropertySale.getBmoSalesUser().getLocationId(), bmoPropertySale.getBmoSalesUser().getBmoLocation()),
				new PmJoin(bmoPropertySale.getWFlowTypeId(), bmoPropertySale.getBmoWFlowType()),
				new PmJoin(bmoPropertySale.getBmoWFlowType().getWFlowCategoryId(), bmoPropertySale.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoPropertySale.getWFlowId(), bmoPropertySale.getBmoWFlow()),
				new PmJoin(bmoPropertySale.getBmoWFlow().getWFlowPhaseId(), bmoPropertySale.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoPropertySale.getBmoWFlow().getWFlowFunnelId(), bmoPropertySale.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		if (getSFParams().restrictData(bmoPropertySale.getProgramCode())) {

			// Filtro por asignacion de venta propiedads
			filters = "( prsa_salesuserid in (" +
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
					" prsa_wflowid IN ( " +
					" SELECT wflw_wflowid FROM wflowusers  " +
					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					" AND (wflw_callercode = '" + bmoPropertySale.getProgramCode().toString() +  
					"' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + 
					"   ) " +
					" ) " +
					" ) ";
		}		

		// Filtro de ventas de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( prsa_companyid in (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " ) " +
					" ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " prsa_companyid = " + getSFParams().getSelectedCompanyId();
		}
		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPropertySale bmoPropertySale = (BmoPropertySale) autoPopulate(pmConn, new BmoPropertySale());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoPropertySale.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoPropertySale.setBmoCustomer(bmoCustomer);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int UserId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (UserId > 0) bmoPropertySale.setBmoSalesUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoPropertySale.setBmoSalesUser(bmoUser);

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int PropertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (PropertyId > 0) bmoPropertySale.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoPropertySale.setBmoProperty(bmoProperty);

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoPropertySale.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoPropertySale.setBmoWFlowType(bmoWFlowType);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoPropertySale.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoPropertySale.setBmoWFlow(bmoWFlow);

		return bmoPropertySale;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPropertySale = (BmoPropertySale)bmObject;	
		boolean newRecord = false;
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";
		
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoPropertySale.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoPropertySale, bmUpdateResult);
			bmoPropertySale.setId(bmUpdateResult.getId());
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoPropertySale.getCompanyId().toInteger(),
					bmoPropertySale.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoPropertySale.CODE_PREFIX
					);
			bmoPropertySale.getCode().setValue(code);
//			bmoPropertySale.getCode().setValue(bmoPropertySale.getCodeFormat());
			bmoPropertySale.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoPropertySale.getType().setValue(BmoPropertySale.TYPE_NEW);
		}

		// Actualizacion de la clave de la venta
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoPropertySale.getWFlowTypeId().toInteger());

		PmProperty pmProperty = new PmProperty(getSFParams());
		BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoPropertySale.getPropertyId().toInteger());
		bmoPropertySale.setBmoProperty(bmoProperty);

		// Datos del cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoPropertySale.getCustomerId().toInteger());
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());
		
		
		// Asigna vendedor, desde el cliente si no viene de alguna oportunidad
		if (newRecord) {
			if(!(bmoPropertySale.getOpportunityId().toInteger() > 0) && !(bmoPropertySale.getSalesUserId().toInteger() > 0))
				bmoPropertySale.getSalesUserId().setValue(bmoCustomer.getSalesmanId().toInteger());
			
			// Revisa que la propiedad sea vendible
			validateProperty(bmoProperty, bmUpdateResult);
			
		}

		// Otras validaciones una vez asignado el tipo de venta propiedad
		if (!bmUpdateResult.hasErrors()) {
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			// Modificar pedido si ya existe
			if (bmoPropertySale.getOrderId().toInteger() > 0) {

				// Actualiza info del pedido, porque es existente
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());
				bmoOrder.getName().setValue(bmoWFlowType.getName().toString());
				bmoOrder.getCustomerId().setValue(bmoPropertySale.getCustomerId().toInteger());
				bmoOrder.getUserId().setValue(bmoPropertySale.getSalesUserId().toInteger());

				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);

			} else {
				// No existe, se crea
				createOrderFromPropertySale(pmConn, pmOrder, bmoOrder, bmoPropertySale, bmUpdateResult);
			}

			// Asignar el id del pedido
			bmoPropertySale.getOrderId().setValue(bmoOrder.getId());

			// Asignar el wflowid del pedido
			bmoPropertySale.getWFlowId().setValue(bmoOrder.getWFlowId().toInteger());
		}

		if(!newRecord){
			// En caso de establecer como cancelada la venta, forzar ingreso del motivo
			if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_CANCELLED) {
				if (!(bmoPropertySale.getLoseMotiveId().toInteger() > 0)){
					bmUpdateResult.addError(bmoPropertySale.getLoseMotiveId().getName(), "Debe asigarse el Motivo de Cancelación.");
				}else{
					// Si ya está cancelada y con motivo, manda error para no reemplazar la fecha de cancelación
					if(bmoPropertySale.getLoseMotiveId().toInteger() > 0 && bmoPropertySale.getCancellDate().toString().length() > 0)
						bmUpdateResult.addError(bmoPropertySale.getStatus().getName(), "La Venta NO se puede cancelar dos veces.");
					else
						bmoPropertySale.getCancellDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				}
			}else if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_REVISION || bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_AUTHORIZED) {
				bmoPropertySale.getCancellDate().setValue("");
				bmoPropertySale.getLoseMotiveId().setValue("");
				bmoPropertySale.getLoseComments().setValue("");

				// Si la venta estaba Cancelada y se pone en Revisión o Autorizada, valida que el inmueble aún este disponible
				PmPropertySale pmPropertySalePrev = new PmPropertySale(getSFParams());
				BmoPropertySale bmoPropertySalePrev = (BmoPropertySale)pmPropertySalePrev.get(bmoPropertySale.getId());
				if (bmoPropertySalePrev.getStatus().toChar() == BmoPropertySale.STATUS_CANCELLED) {
					// Revisa que la propiedad sea vendible
					validateProperty(bmoProperty, bmUpdateResult);
				}
			}

			// Se almacena la Venta
			if (!bmUpdateResult.hasErrors()) {
				// Crear bitacoras 
				if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_REVISION)
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(),	BmoWFlowLog.TYPE_OTHER, "La Venta se guardó como En Revisión.");
				else if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_AUTHORIZED)
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(),	BmoWFlowLog.TYPE_OTHER, "La Venta se guardó como Autorizada.");
				else if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_CANCELLED){
					String motives = "";
					if (bmoPropertySale.getLoseMotiveId().toInteger() > 0){
						PmLoseMotive pmLoseMotive = new PmLoseMotive(getSFParams());
						BmoLoseMotive bmoLoseMotive = (BmoLoseMotive)pmLoseMotive.get(bmoPropertySale.getLoseMotiveId().toInteger());
						motives = bmoLoseMotive.getName().toString() + " | "+ bmoPropertySale.getLoseComments().toString();
					}
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(),	BmoWFlowLog.TYPE_OTHER, "La Venta se guardó como Cancelada. Motivo: " + motives);

					//Cancelar pedido
					PmOrder pmOrder = new PmOrder(getSFParams());
					BmoOrder bmoOrder = new BmoOrder();
					bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());
					if (bmoOrder.getWFlowId().toInteger() > 0){
						bmoOrder.getStatus().setValue(BmoOrder.STATUS_CANCELLED);
						pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
					}
				}

				// Guarda la Venta
				super.save(pmConn, bmoPropertySale, bmUpdateResult);

				// Forzar el ID del resultado que sea de la Ventay no de otra clase
				bmUpdateResult.setId(bmoPropertySale.getId());

				//Actualiza estatus del cliente
				pmCustomer.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			}
		}

		// Crear detalle de venta si no existe
		PmPropertySaleDetail pmPropertySaleDetail = new PmPropertySaleDetail(getSFParams());
		if (!pmPropertySaleDetail.propertySaleDetailExists(pmConn, bmoPropertySale.getId())) {
			BmoPropertySaleDetail bmoPropertySaleDetail = new BmoPropertySaleDetail();

			bmoPropertySaleDetail.getPropertySaleId().setValue(bmoPropertySale.getId());
			pmPropertySaleDetail.saveSimple(pmConn,  bmoPropertySaleDetail, bmUpdateResult);
		}

		// Validaciones adicionales
		if (!bmUpdateResult.hasErrors()) {

			// Guarda el venta propiedad
			super.save(pmConn, bmoPropertySale, bmUpdateResult);

			// Actualiza estatus propiedad
			pmProperty.updateAvailability(pmConn, bmoProperty, bmUpdateResult);
			if(bmoPropertyModel.getBmoPropertyType().getCopyTag().toBoolean()) {
				bmoPropertySale.getTags().setValue(bmoPropertySale.getBmoProperty().getTags().toString());
			}
			
			// Actualizar id de claves del programa por empresa
			if (newRecord && !bmUpdateResult.hasErrors()) {
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoPropertySale.getCompanyId().toInteger(), 
						bmoPropertySale.getProgramCode().toString());
			}
			
			// Guarda el venta propiedad
			super.save(pmConn, bmoPropertySale, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	private void validateProperty(BmoProperty bmoProperty, BmUpdateResult bmUpdateResult) {
		// Revisa que la propiedad este en una manzana abierta
		if (!bmoProperty.getBmoDevelopmentBlock().getIsOpen().toBoolean())
			bmUpdateResult.addError(bmoPropertySale.getPropertyId().getName(), "El Inmueble no esta en una manzana/torre Abierta.");

		// Revisa que la propiedad este en una manzana abierta
		if (!bmoProperty.getCansell().toBoolean())
			bmUpdateResult.addError(bmoPropertySale.getPropertyId().getName(), "El Inmueble no se puede Vender.");

		// Revisa que la propiedad este disponible
		if (!bmoProperty.getAvailable().toBoolean())
			bmUpdateResult.addError(bmoPropertySale.getPropertyId().getName(), "El Inmueble ya esta Vendido.");
	}

	public BmUpdateResult createFromOpportunity(PmConn pmConn, BmoOpportunity bmoOpportunity, int propertyId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		bmoPropertySale.getDescription().setValue(bmoOpportunity.getDescription().toString());
		bmoPropertySale.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
		bmoPropertySale.getEndDate().setValue("");		
		bmoPropertySale.getCustomerId().setValue(bmoOpportunity.getCustomerId().toString());
		bmoPropertySale.getSalesUserId().setValue(bmoOpportunity.getUserId().toString());
		bmoPropertySale.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toString());
		bmoPropertySale.getTags().setValue(bmoOpportunity.getTags().toString());
		bmoPropertySale.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoPropertySale.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
		bmoPropertySale.getPropertyId().setValue(propertyId);
		bmoPropertySale.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());

		return this.save(pmConn, bmoPropertySale, bmUpdateResult);
	}

	private void createOrderFromPropertySale(PmConn pmConn, PmOrder pmOrder, BmoOrder bmoOrder, BmoPropertySale bmoPropertySale, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear pedido a partir de venta propiedad
		bmoOrder.getCode().setValue(bmoPropertySale.getCode().toString());
		bmoOrder.getName().setValue(bmoPropertySale.getCode().toString());
		bmoOrder.getDescription().setValue(bmoPropertySale.getDescription().toString());
		bmoOrder.getAmount().setValue(0);
		bmoOrder.getDiscount().setValue(0);
		bmoOrder.getTaxApplies().setValue(false);
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getLockStatus().setValue(BmoOrder.LOCKSTATUS_LOCKED);
		bmoOrder.getLockStart().setValue(bmoPropertySale.getStartDate().toString());
		bmoOrder.getLockEnd().setValue(bmoPropertySale.getEndDate().toString());
		bmoOrder.getStatus().setValue(BmoOrder.STATUS_REVISION);
		bmoOrder.getCustomerId().setValue(bmoPropertySale.getCustomerId().toInteger());
		bmoOrder.getOrderTypeId().setValue(bmoPropertySale.getOrderTypeId().toInteger());
		bmoOrder.getUserId().setValue(bmoPropertySale.getSalesUserId().toInteger());
		bmoOrder.getWFlowTypeId().setValue(bmoPropertySale.getWFlowTypeId().toInteger());
		bmoOrder.getOrderCommissionId().setValue(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getOrderCommissionId().toInteger());
		bmoOrder.getCompanyId().setValue(bmoPropertySale.getCompanyId().toInteger());

		// Obtener la moneda de la oportunidad si existe, en caso contrario de la configuracion
		if (bmoPropertySale.getOpportunityId().toInteger() > 0) {
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(pmConn, bmoPropertySale.getOpportunityId().toInteger());
			bmoOrder.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
			bmoOrder.getOrderTypeId().setValue(bmoOpportunity.getOrderTypeId().toInteger());
			bmoOrder.getOpportunityId().setValue(bmoOpportunity.getId());
			bmoOrder.getMarketId().setValue(bmoOpportunity.getMarketId().toInteger());

			// Obtener cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = new BmoQuote();
			bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOpportunity.getQuoteId().toInteger());

			bmoOrder.getTax().setValue(bmoQuote.getTax().toDouble());
			bmoOrder.getTaxApplies().setValue(bmoQuote.getTaxApplies().toString());

			bmoOrder.getComments().setValue(bmoQuote.getComments().toString());
			bmoOrder.getCoverageParity().setValue(bmoQuote.getCoverageParity().toBoolean());
			bmoOrder.getCustomerContactId().setValue(bmoOpportunity.getCustomerContactId().toString());
			bmoOrder.getCustomerRequisition().setValue(bmoQuote.getCustomerRequisition().toString());

			
			// Si esta habilitado el Control presp. pasar la partida  de la Oportunidad/Cot
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				if (!(bmoOpportunity.getBudgetItemId().toInteger() > 0))
					bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), "Seleccione una Partida en la Oportunidad");
				else {
					bmoOrder.getDefaultBudgetItemId().setValue(bmoQuote.getBudgetItemId().toInteger());
					bmoOrder.getDefaultAreaId().setValue(bmoQuote.getAreaId().toInteger());
				}
			}
			
		} else {
			bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				// Obtener partida a traves de la vivienda seleccionada
				int budgetId = 0, budgetItemId = -1;
				budgetId = bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getBudgetId().toInteger();
				printDevLog("Existe idPrespuesto en Etapa -> " +budgetId);
				if (budgetId > 0) {
					// Obtener la partida de ingresos
					String sql = " SELECT bgit_budgetitemid FROM budgetitems " +
							" WHERE bgit_budgetid = " + budgetId + 
							" AND bgit_budgetitemtypeid = " + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger();
					pmConn.doFetch(sql);
					if (pmConn.next()) budgetItemId = pmConn.getInt("bgit_budgetitemid");

					if (budgetItemId > 0) {
						bmoOrder.getDefaultBudgetItemId().setValue(budgetItemId);
						// el dpto. lo toma del tipo de pedido?
						PmOrderType pmOrderType = new PmOrderType(getSFParams());
						BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoPropertySale.getOrderTypeId().toInteger());
						bmoOrder.getDefaultAreaId().setValue(bmoOrderType.getDefaultAreaId().toInteger());
					} else
						bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), 
								"No se encontró una Partida Presp. de Ingresos (sobre la Etapa de Des.) de la vivienda seleccionada");
				} else bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), 
						"No se encontró un Presupuesto en la Etapa de Desarrollo");
			}
		}

		if (!bmUpdateResult.hasErrors()) {
			pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
		}

		if (!bmUpdateResult.hasErrors()) {
			// Crear el registro del inmueble vendido
			PmOrderProperty pmOrderProperty = new PmOrderProperty(getSFParams());
			BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
			bmoOrderProperty.getOrderId().setValue(bmoOrder.getId());
			bmoOrderProperty.getPropertyId().setValue(bmoPropertySale.getPropertyId().toInteger());
			pmOrderProperty.create(pmConn, bmoOrderProperty, bmoOrder, bmUpdateResult);
		}
		// Si el venta propiedad proviene de una oportunidad
		if (bmoPropertySale.getOpportunityId().toInteger() > 0) {
			if (!bmUpdateResult.hasErrors()) {
				// Obtener y crear los extras de la cotizacion
				createOrderPropertyModelExtrasFromOpportunity(pmConn, bmoOrder.getId(), bmoPropertySale, bmUpdateResult);
			}
			if (!bmUpdateResult.hasErrors()) {
				// Obtener los usuarios de WFlow de la oportunidad
				pmOrder.createWFlowUsersFromOpportunity(pmConn, bmoOrder, bmUpdateResult);
			}
			if (!bmUpdateResult.hasErrors()) {
				// Obtener los documentos de WFlow de la oportunidad
				pmOrder.createWFlowDocumentsFromOpportunity(pmConn, bmoOrder, bmUpdateResult);
			}
			
			new PmOpportunity(getSFParams()).updateRequisitions(pmConn, bmoPropertySale.getOpportunityId().toInteger(), bmoOrder, bmUpdateResult);
			// Si la notificacion esta activada en el Tipo de Pedido
			// notificar a los usuarios del WFlow del pedido
			if (!bmUpdateResult.hasErrors()) {
				if (bmoOrder.getBmoOrderType().getEmailRemindersOrderStart().toBoolean())
					pmOrder.sendMailReminderOrderStart(pmConn, bmoOrder);
			}
		}
	}

	private void createOrderPropertyModelExtrasFromOpportunity(PmConn pmConn, int orderId, BmoPropertySale bmoPropertySale, BmUpdateResult bmUpdateResult) throws SFException {
		// Obtener los documentos de WFlow de la oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(bmoPropertySale.getOpportunityId().toInteger());

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);
		
		// Lista de recursos
		PmOrderPropertyModelExtra pmOrderPropertyModelExtra = new PmOrderPropertyModelExtra(getSFParams());
		PmQuotePropertyModelExtra pmQuotePropertyModelExtra = new PmQuotePropertyModelExtra(getSFParams());
		BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
		BmFilter byQuoteFilter = new BmFilter();
		byQuoteFilter.setValueFilter(bmoQuotePropertyModelExtra.getKind(), bmoQuotePropertyModelExtra.getQuoteId().getName(), bmoOpportunity.getQuoteId().toInteger());
		Iterator<BmObject> quotePropertyModelExtraIterator = pmQuotePropertyModelExtra.list(byQuoteFilter).iterator();
		while (quotePropertyModelExtraIterator.hasNext()) {
			bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)quotePropertyModelExtraIterator.next();
			BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
			bmoOrderPropertyModelExtra.getQuantity().setValue(bmoQuotePropertyModelExtra.getQuantity().toInteger());
			bmoOrderPropertyModelExtra.getPrice().setValue(bmoQuotePropertyModelExtra.getPrice().toDouble());
			bmoOrderPropertyModelExtra.getAmount().setValue(bmoQuotePropertyModelExtra.getAmount().toDouble());
			bmoOrderPropertyModelExtra.getComments().setValue(bmoQuotePropertyModelExtra.getComments().toString());
			bmoOrderPropertyModelExtra.getPropertyModelExtraId().setValue(bmoQuotePropertyModelExtra.getPropertyModelExtraId().toInteger());
			bmoOrderPropertyModelExtra.getOrderId().setValue(orderId);

			pmOrderPropertyModelExtra.saveSimple(pmConn, bmoOrderPropertyModelExtra, bmUpdateResult);			
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
		}
	}

	public BmoPropertySale getByWFlowId(int wFlowId) throws SFException {
		return (BmoPropertySale)super.getBy(wFlowId, bmoPropertySale.getWFlowId().getName());
	}

	// Revisar si existe la orden de algún venta propiedad
	public boolean propertySaleOrderExists(PmConn pmConn, int orderId) throws SFPmException {
		pmConn.doFetch("SELECT orde_orderid FROM orders WHERE orde_orderid = " + orderId);
		return pmConn.next();
	}

	// Existe la venta ligada a la oportunidad?
	public boolean propertySaleOpportunityExists(PmConn pmConn, int opportunityId) throws SFPmException {
		pmConn.doFetch("SELECT prsa_opportunityid FROM propertysales WHERE prsa_opportunityid = " + opportunityId);
		return pmConn.next();
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoPropertySale = (BmoPropertySale)bmObject;

		if (action.equalsIgnoreCase(BmoPropertySale.ACTION_RELOCATE)) 
			relocate(bmoPropertySale, value, bmUpdateResult);
		else if (action.equalsIgnoreCase(BmoPropertySale.ACTION_CHANGEWFLOWTYPE)) {
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoPropertySale.getWFlowId().toInteger());

			PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());

			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());


			try {
				int newWFlowTypeId = Integer.parseInt(value);			

				pmWFlow.changeWFlowType(pmConn, bmoWFlow, newWFlowTypeId, bmUpdateResult);

				bmoPropertySale.getWFlowTypeId().setValue(newWFlowTypeId);
				super.save(pmConn, bmoPropertySale, bmUpdateResult);

				bmoOrder.getWFlowTypeId().setValue(newWFlowTypeId);
				super.save(pmConn, bmoOrder, bmUpdateResult);

				// Venta con nuevo Tipo de credito
				BmoPropertySale newBmoPropertySale  = (BmoPropertySale)pmPropertySale.get(pmConn, bmoPropertySale.getId());

				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
						"Cambio de Tipo de Crédito." +
								" Anterior: " + bmoPropertySale.getBmoWFlowType().getName().toString() + 
								" - Nuevo: " + newBmoPropertySale.getBmoWFlowType().getName().toString());


			} catch (NumberFormatException e) {
				throw new SFException(this.getClass() + "-changeWFlowType(): " + e.toString());
			}
		}

		return bmUpdateResult;
	}

	// Cambia la propiedad de una venta
	private void relocate(BmoPropertySale bmoPropertySale, String propertyId, BmUpdateResult bmUpdateResult) throws SFException {

		PmConn pmConn = new PmConn(getSFParams());
		PmRaccount pmRaccount = new PmRaccount(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Actualiza el inmueble
			if (propertyId.equals(""))
				bmUpdateResult.addError(bmoPropertySale.getPropertyId().getName(), "Debe seleccionarse el nuevo Inmueble.");
			else { 
				bmoPropertySale.getType().setValue(BmoPropertySale.TYPE_RELOCATION);

				// Obtener datos del inmueble anterior
				PmProperty pmProperty = new PmProperty(getSFParams());
				BmoProperty prevBmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoPropertySale.getPropertyId().toInteger());

				// Obtener datos del nuevo inmueble				
				bmoPropertySale.getPropertyId().setValue(propertyId);
				BmoProperty newBmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoPropertySale.getPropertyId().toInteger());

				// Obtener datos del pedido
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());

				// Si esta autorizado el pedido, no permitir la reubicacion
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
					bmUpdateResult.addError(bmoPropertySale.getPropertyId().getName(), "No se puede reubicar la Venta. El Pedido está Autorizado.");
				else {

					// Elimina los registros ligados al pedido de inmuebles
					pmOrder.deleteOrderProperty(pmConn, bmoOrder, bmUpdateResult);
					pmOrder.deleteOrderPropertyModelExtra(pmConn, bmoOrder, bmUpdateResult);

					// Asignar la empresa del nuevo inmueble al pedido
					bmoOrder.getCompanyId().setValue(newBmoProperty.getCompanyId().toInteger());
					
					// Crear el registro del inmueble vendido
					PmOrderProperty pmOrderProperty = new PmOrderProperty(getSFParams());
					BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
					bmoOrderProperty.getOrderId().setValue(bmoOrder.getId());
					bmoOrderProperty.getPropertyId().setValue(bmoPropertySale.getPropertyId().toInteger());
					pmOrderProperty.create(pmConn, bmoOrderProperty, bmoOrder, bmUpdateResult);

					// Validaciones de la nueva propiedad
					validateProperty(newBmoProperty, bmUpdateResult);

					// Agregar bitacora del cambio
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
							" Se reubico la Venta. Inmueble Anterior: " + prevBmoProperty.getCode().toString() +
							"  -  Inmueble Nuevo: " + newBmoProperty.getCode().toString());

					// Asignar la empresa del nuevo inmueble a la venta
					bmoPropertySale.getCompanyId().setValue(newBmoProperty.getCompanyId().toInteger());

					// Obtener partida presupuestal de la venta de inmueble anterior
					int budgetItemId = 0;
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
					}

					// Guardar venta
					super.save(pmConn, bmoPropertySale, bmUpdateResult);

					// Cambiar partida presupuestal de las cxc 
					// Y Revisar saldo de cuentas x cobrar para ajuste
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						// Actualizar la partida presupuestal de la NUEVA VIVIENDA en el Pedido
							int newBudgetItemId = -1;
							newBudgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
							if (newBudgetItemId > 0) {
								bmoOrder.getDefaultBudgetItemId().setValue(newBudgetItemId);
								// Guardar partida en pedido
								pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
							} else {
								bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), 
										"No se encontró una Partida Presp. de Ingresos (sobre la Etapa de Des.) de la vivienda seleccionada");
							}
	
						// Cambiar partida de cxc
						pmRaccount.changeBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
						
						// Actualizar partida presupuestal de la venta de inmueble anterior
						PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
						BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, budgetItemId);

						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					} else {
						// Revisar saldo de cuentas x cobrar
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnsureProcessCxC().toBoolean()) {
							// Si tiene cxc ajustarlas
							if (pmRaccount.orderHasRaccount(pmConn, bmoOrder, bmUpdateResult) > 0)
								pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
						}
					}

					// Actualiza estatus propiedad
					pmProperty.updateAvailability(pmConn, prevBmoProperty, bmUpdateResult);
					pmProperty.updateAvailability(pmConn, newBmoProperty, bmUpdateResult);

					super.save(pmConn, bmoPropertySale, bmUpdateResult);
				}
			}	

			if (!bmUpdateResult.hasErrors()) pmConn.commit();

		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}

	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPropertySale = (BmoPropertySale)bmObject;
		BmFilter filterByPropertySale = new BmFilter();

		// Revisar si esta en revision para proceder a eliminar
		if (bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_REVISION) {

			// Eliminar datos adicionales
			PmPropertySaleDetail pmPropertySaleDetail = new PmPropertySaleDetail(getSFParams());
			BmoPropertySaleDetail bmoPropertySaleDetail = new BmoPropertySaleDetail();
			BmFilter filterByPropertySaleId = new BmFilter();
			filterByPropertySaleId.setValueFilter(bmoPropertySaleDetail.getKind(), bmoPropertySaleDetail.getPropertySaleId(), bmoPropertySale.getId());
			ListIterator<BmObject> propertySaleDetailList = pmPropertySaleDetail.list(pmConn, filterByPropertySaleId).listIterator();
			while (propertySaleDetailList.hasNext()) {
				bmoPropertySaleDetail = (BmoPropertySaleDetail)propertySaleDetailList.next();
				pmPropertySaleDetail.delete(pmConn,  bmoPropertySaleDetail, bmUpdateResult);
			}

			super.delete(pmConn, bmoPropertySale, bmUpdateResult);

			// Eliminar pedidos
			if (!bmUpdateResult.hasErrors()) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				if (bmoPropertySale.getOrderId().toInteger() > 0) {
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoPropertySale.getOrderId().toInteger());

					// Eliminar ordenes de compra ligadas al pedido
					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					BmoRequisition bmoRequisition = new BmoRequisition();
					filterByPropertySale.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(), bmoOrder.getId());
					ListIterator<BmObject> requisitionList = pmRequisition.list(pmConn, filterByPropertySale).listIterator();
					while (requisitionList.hasNext()) {
						bmoRequisition = (BmoRequisition)requisitionList.next();
						pmRequisition.delete(pmConn,  bmoRequisition, bmUpdateResult);
					}

					pmOrder.delete(pmConn,  bmoOrder, bmUpdateResult);
				}

				// Actualiza estatus propiedad
				PmProperty pmProperty = new PmProperty(getSFParams());
				BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoPropertySale.getPropertyId().toInteger());
				pmProperty.updateAvailability(pmConn, bmoProperty, bmUpdateResult);

				//				// Eliminar flujos
				//				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				//				BmoWFlow bmoWFlow = new BmoWFlow();
				//				// Busca el WFlow por PEDIDO
				//				filterByPropertySale.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoPropertySale.getOrderId().toInteger());			
				//				BmFilter filterWFlowCategory = new BmFilter();
				//				filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoPropertySale.getProgramCode());
				//				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				//				filterList.add(filterByPropertySale);
				//				filterList.add(filterWFlowCategory);
				//				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
				//				while (wFlowList.hasNext()) {
				//					bmoWFlow = (BmoWFlow)wFlowList.next();
				//					pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
				//				}
			}

		} else {
			bmUpdateResult.addError(bmoPropertySale.getStatus().getName(), "No se puede eliminar la Venta - no está En Revisión.");
		}

		return bmUpdateResult;
	}

}