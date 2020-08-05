//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.flexwm.server.FlexUtil;
//import com.flexwm.server.op.PmOrderType;
//import com.flexwm.server.wf.PmWFlow;
//import com.flexwm.server.wf.PmWFlowLog;
//import com.flexwm.server.wf.PmWFlowType;
//import com.flexwm.shared.BmoFlexConfig;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoOpportunity;
//import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.op.BmoOrderType;
//import com.flexwm.shared.wf.BmoWFlow;
//import com.flexwm.shared.wf.BmoWFlowType;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.sf.PmUser;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.shared.sf.BmoCompany;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class PmRFQU extends PmObject {
//	BmoRFQU bmoRFQU;
//	PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
//
//	public PmRFQU(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoRFQU = new BmoRFQU();
//		setBmObject(bmoRFQU);
//
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoRFQU.getUserSale(), bmoRFQU.getBmoUser()),
//				new PmJoin(bmoRFQU.getBmoUser().getAreaId(), bmoRFQU.getBmoUser().getBmoArea()),
//				new PmJoin(bmoRFQU.getBmoUser().getLocationId(), bmoRFQU.getBmoUser().getBmoLocation()),
//				new PmJoin(bmoRFQU.getOrderTypeId(), bmoRFQU.getBmoOrderType()),
//				new PmJoin(bmoRFQU.getCustomerId(), bmoRFQU.getBmoCustomer()),
//				new PmJoin(bmoRFQU.getBmoCustomer().getReqPayTypeId(),bmoRFQU.getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoRFQU.getBmoCustomer().getTerritoryId(), bmoRFQU.getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoRFQU.getwFlowTypeId(), bmoRFQU.getBmoWFlowType()),
//				new PmJoin(bmoRFQU.getBmoWFlowType().getWFlowCategoryId(), bmoRFQU.getBmoWFlowType().getBmoWFlowCategory()),
//				new PmJoin(bmoRFQU.getwFlowId(), bmoRFQU.getBmoWFlow()),
//				new PmJoin(bmoRFQU.getBmoWFlow().getWFlowPhaseId(), bmoRFQU.getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoRFQU.getBmoWFlow().getWFlowFunnelId(), bmoRFQU.getBmoWFlow().getBmoWFlowFunnel())
//				))); 
//
//	}
//	@Override
//	public String getDisclosureFilters() {
//		String filters = "";
//		int loggedUserId = getSFParams().getLoginInfo().getUserId();
//
//		// Filtro por asignacion de Oportunidads 
//		if (getSFParams().restrictData(bmoRFQU.getProgramCode())) {
//
//			filters = "( rfqu_userid IN (" +
//					" SELECT user_userid FROM users " +
//					" WHERE " + 
//					" user_userid = " + loggedUserId +
//					" OR user_userid IN ( " +
//					" SELECT u2.user_userid FROM users u1 " +
//					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
//					" WHERE u1.user_userid = " + loggedUserId +
//					" ) " +
//					" OR user_userid IN ( " +
//					" SELECT u3.user_userid FROM users u1 " +
//					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
//					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
//					" WHERE u1.user_userid = " + loggedUserId +
//					" ) " +
//					" OR user_userid IN ( " +
//					" SELECT u4.user_userid FROM users u1 " +
//					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
//					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
//					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
//					" WHERE u1.user_userid = " + loggedUserId +
//					" ) " +
//					" OR user_userid IN ( " +
//					" SELECT u5.user_userid FROM users u1 " +
//					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
//					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
//					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
//					" LEFT JOIN users u5 ON (u5.user_parentid = u4.user_userid) " +
//					" WHERE u1.user_userid = " + loggedUserId +
//					" ) " + 
//					" ) " +
//					" OR " +
//					" ( " +
//					" rfqu_rfquid IN ( " +
//					" SELECT wflw_callerid FROM wflowusers  " +
//					" LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid) " +
//					" WHERE wflu_userid = " + loggedUserId +
//					" AND wflw_callercode = 'RFQU' " + 
//					"   ) " +
//					" ) " +
//					" ) ";
//		}
//			// Filtro de oportunidades de empresas del usuario
//			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
//				if (filters.length() > 0) filters += " AND ";
//				filters += "( rfqu_companyid IN (" +
//						" SELECT uscp_companyid FROM usercompanies " +
//						" WHERE " + 
//						" uscp_userid = " + loggedUserId + " )"
//						+ ") ";			
//			}
//
//			// Filtro de empresa seleccionada
//			if (getSFParams().getSelectedCompanyId() > 0) {
//				if (filters.length() > 0) filters += " AND ";
//				filters += " rfqu_companyid = " + getSFParams().getSelectedCompanyId();
//			}
//
//			
//			return filters;
//			}	
//	
//
//
//
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		BmoRFQU bmoRFQU = (BmoRFQU) autoPopulate(pmConn, new BmoRFQU());
//
//
//		// BmoOrderType
//		BmoOrderType bmoOrderType = new BmoOrderType();
//		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
//		if (orderTypeId > 0) bmoRFQU.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
//		else bmoRFQU.setBmoOrderType(bmoOrderType);
//		// BmoWFlowType
//		BmoWFlowType bmoWFlowType = new BmoWFlowType();
//		int wFlowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
//		if (wFlowTypeId > 0) bmoRFQU.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
//		else bmoRFQU.setBmoWFlowType(bmoWFlowType);
//
//		// BmoUser
//		BmoUser bmoUser = new BmoUser();
//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
//		if (userId > 0) bmoRFQU.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
//		else bmoRFQU.setBmoUser(bmoUser);
//
//		// BmoCustomer
//		BmoCustomer bmoCustomer = new BmoCustomer();
//		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
//		if (customerId > 0) bmoRFQU.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
//		else bmoRFQU.setBmoCustomer(bmoCustomer);
//
//		// BmoWFlow
//		BmoWFlow bmoWFlow = new BmoWFlow();
//		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
//		if (wFlowId > 0) bmoRFQU.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
//		else bmoRFQU.setBmoWFlow(bmoWFlow);
//
//		return bmoRFQU;
//	}
//
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		BmoRFQU bmoRFQUPrev = (BmoRFQU)bmObject;	
//		PmRFQU pmRFQUPrev = new PmRFQU(getSFParams());
//		BmoRFQU bmoRFQU = (BmoRFQU)bmObject;
//		PmWFlow pmWFlow = new PmWFlow(getSFParams());
//		BmoWFlow bmoWFlow = new BmoWFlow();
//		// Se almacena de forma preliminar para asignar ID
//		if (!(bmoRFQU.getId() > 0)) {
//			
//			super.save(pmConn, bmoRFQU, bmUpdateResult);
//			bmoRFQU.setId(bmUpdateResult.getId());	
//			//La clave debe tener 4 digitos
//			String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoRFQU.CODE_PREFIX);			
//			bmoRFQU.getCodeRFQU().setValue(code);
//		}else {
//			bmoRFQUPrev = (BmoRFQU)pmRFQUPrev.get(bmoRFQU.getId());
//		}
//		// Actualizar WFlow
//		if (!bmUpdateResult.hasErrors()) {
//			// Asigna el estatus al flujo
//			char wFlowStatus = BmoWFlow.STATUS_ACTIVE;
//			if (bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_CANCELED) 
//					|| bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_COMPLET)
//					|| bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_NOTSTARTING))
//				wFlowStatus = BmoWFlow.STATUS_INACTIVE;
//
//			bmoWFlow = pmWFlow.updateWFlow(pmConn, bmoRFQU.getwFlowTypeId().toInteger(), bmoRFQU.getwFlowId().toInteger(), 
//					bmoRFQU.getProgramCode(), bmoRFQU.getId(), bmoRFQU.getUserSale().toInteger(), bmoRFQU.getCompanyId().toInteger(), bmoRFQU.getCustomerId().toInteger(),
//					bmoRFQU.getCodeRFQU().toString(), bmoRFQU.getAffair().toString(), bmoRFQU.getObjectiveRFQU().toString(), 
//					bmoRFQU.getDateRFQU().toString(), bmoRFQU.getDateRFQU().toString(), wFlowStatus, bmUpdateResult);
//
//			bmoRFQU.getwFlowId().setValue(bmoWFlow.getId());
//			
//			super.save(pmConn, bmoRFQU, bmUpdateResult);
//			updateEstimation(pmConn, bmoRFQU, bmUpdateResult);
//
//		}		
//		
//		super.save(pmConn, bmoRFQU, bmUpdateResult);
//
//		if((bmoRFQUPrev.getStatusRFQU().toChar()!= bmoRFQU.getStatusRFQU().toChar())) {
//			if(bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_COMPLET)) {
//
//				winRFQU(pmConn, bmoRFQU, bmoWFlow, pmWFlowLog, bmUpdateResult);
//			} else {
//				if(!(bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_COMPLET)))
//			if(existOppo(pmConn, bmoRFQUPrev)) {
//				bmUpdateResult.addError(bmoRFQU.getStatusRFQU().getName(),"No se puede cambiar el Estatus, El RFQU tiene una Oportunidad");
//			}
//			}
//		}
//		super.save(pmConn, bmoRFQU, bmUpdateResult);
//
//		return bmUpdateResult;
//
//	}
//
//	//delete 
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoRFQU= (BmoRFQU)bmObject;
//			String status = ""+bmoRFQU.getStatusRFQU().getSelectedOption().getLabel();
//			if (bmoRFQU.getStatusRFQU().toChar() == BmoRFQU.STATUS_PROCESSING || bmoRFQU.getStatusRFQU().toChar() == BmoRFQU.STATUS_COMPLET)
//				bmUpdateResult.addError(bmoRFQU.getStatusRFQU().getName(), "No se puede eliminar el RFQ - está " + status + ".");
//				
//			if(!bmUpdateResult.hasErrors()) {
//				if (bmoRFQU.getStatusRFQU().toChar() == BmoRFQU.STATUS_NOTSTARTING || bmoRFQU.getStatusRFQU().toChar() == BmoRFQU.STATUS_PROCESSING) 
//					{
//					pmConn.doUpdate("DELETE  FROM estimationrfqitems WHERE esrf_estimationgroupid IN (SELECT esgp_estimationgroupid FROM estimationgroups "
//							+ " WHERE esgp_estimationid IN (SELECT ests_estimationid FROM estimations " + 
//							" WHERE ests_rfquid =  "	+ bmoRFQU.getId() + "))");
//
//					pmConn.doUpdate("DELETE FROM estimationgroups "  
//							+  " WHERE esgp_estimationid IN (SELECT ests_estimationid FROM estimations "  
//							+  " WHERE ests_rfquid = "+ bmoRFQU.getId()+")");
//					pmConn.doUpdate("DELETE FROM estimations WHERE ests_rfquid = " + bmoRFQU.getId());
//					}
//				super.delete(pmConn, bmoRFQU, bmUpdateResult);
//
//				}
//		return bmUpdateResult;
//	}
//	
//
//	// Ganar RFQU
//	private void winRFQU(PmConn pmConn, BmoRFQU bmoRFQU, BmoWFlow bmoWFlow, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
//		if (!bmUpdateResult.hasErrors()) {
//			// Revisar si esta habilitado el crear los efectos de la oportunidad
//			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOpportunityEffect().toBoolean()){
//
//				// Revisar que este asignado el tipo de proyecto
//				if (bmoRFQU.getForeignWFlowTypeId().toInteger() > 0)  {
//
//					// Obtener Estimacion
//					PmEstimation pmEstimation = new PmEstimation(getSFParams());
//					BmoEstimation bmoEstimation = new BmoEstimation();
//					bmoEstimation = (BmoEstimation)pmEstimation.get(pmConn, bmoRFQU.getEstimationId().toInteger());
//						if (!bmoEstimation.getStatus().equals('A'))
//							bmUpdateResult.addError(bmoRFQU.getStatusRFQU().getName(), "Debe Completada la Estimación del RFQU.");
//						else 
//
//							if (bmoRFQU.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
//								winSale(pmConn, bmoRFQU, pmWFlowLog, bmUpdateResult);
//							}
//					
//				} else {
//					// No se asigno el tipo de proyecto a crear, enviar error
//					bmUpdateResult.addError(bmoRFQU.getForeignWFlowTypeId().getName(), "Debe asigarse el tipo de RFQU a crear.");
//				}
//			}
//		}
//	}
//
//
//	// Se gana oportunidad de tipo venta
//	private void winSale(PmConn pmConn, BmoRFQU bmoRFQU, PmWFlowLog pmWFlowLog, BmUpdateResult bmUpdateResult) throws SFException {
//		// Revisar que no haya errores, crear rfqu
//		if (!bmUpdateResult.hasErrors()) {
//			PmOpportunity  pmOpportunity= new PmOpportunity(getSFParams());
//			pmOpportunity.createFromRFQU(pmConn, bmoRFQU, bmUpdateResult);
//		} 
//	}
//
//
//	// update estimation 
//	private void updateEstimation(PmConn pmConn, BmoRFQU bmoRFQU, BmUpdateResult bmUpdateResult) throws SFException {
//		// Crear estimación si estan habilitadas por sistema
//		PmEstimation pmEstimation = new PmEstimation(getSFParams());
//		BmoEstimation bmoEstimation = new BmoEstimation();
//		
//		
//		if (!(bmoRFQU.getEstimationId().toInteger() > 0)) {
//
//			bmoEstimation.getwFlowId().setValue(bmoRFQU.getwFlowId().toInteger());
//
//			bmoEstimation.getCode().setValue(bmoRFQU.getCodeRFQU().toString());
//			bmoEstimation.getName().setValue(bmoRFQU.getAffair().toString());
//			bmoEstimation.getOrderTypeId().setValue(bmoRFQU.getOrderTypeId().toInteger());
//			bmoEstimation.getCompanyId().setValue(bmoRFQU.getCompanyId().toInteger());
//			bmoEstimation.getUserId().setValue(bmoRFQU.getUserSale().toInteger());
//			bmoEstimation.getCustomerId().setValue(bmoRFQU.getCustomerId().toInteger());
//			bmoEstimation.getCurrencyId().setValue(1);
//			bmoEstimation.getStatus().setValue(bmoRFQU.getStatusRFQU().toString());
//			bmoEstimation.getStartDate().setValue(bmoRFQU.getDateRFQU().toString());
//			bmoEstimation.getRfquId().setValue(bmoRFQU.getId());
//
//			pmEstimation.save(pmConn, bmoEstimation, bmUpdateResult);
//
//		} else {
//			bmoEstimation = (BmoEstimation)pmEstimation.get(pmConn, bmoRFQU.getEstimationId().toInteger());
//
//			// Actualiza datos de la estimación
//
//			bmoEstimation.getwFlowId().setValue(bmoRFQU.getwFlowId().toInteger());
//			bmoEstimation.getCode().setValue(bmoRFQU.getCodeRFQU().toString());
//			bmoEstimation.getName().setValue(bmoRFQU.getAffair().toString());
//			bmoEstimation.getOrderTypeId().setValue(bmoRFQU.getOrderTypeId().toInteger());
//			bmoEstimation.getCompanyId().setValue(bmoRFQU.getCompanyId().toInteger());
//			bmoEstimation.getUserId().setValue(bmoRFQU.getUserSale().toInteger());
//			bmoEstimation.getCustomerId().setValue(bmoRFQU.getCustomerId().toInteger());
//			bmoEstimation.getCurrencyId().setValue(1);
//			bmoEstimation.getStartDate().setValue(bmoRFQU.getDateRFQU().toString());
//			bmoEstimation.getRfquId().setValue(bmoRFQU.getId());
//			pmEstimation.saveSimple(pmConn, bmoEstimation, bmUpdateResult);
//
//		}
//		bmoRFQU.getEstimationId().setValue(bmoEstimation.getId());
//
//	}
//
//	@Override
//	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
//		// Actualiza datos de la cotización
//		bmoRFQU = (BmoRFQU)this.get(bmObject.getId());
//
//		// Obtiene el ID del modulo de effecto effecto
//		if (action.equals(BmoRFQU.ACTION_GETEFFECT)) {
//
//				PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
//				BmoOpportunity bmoOpportunity = new BmoOpportunity();
//				bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(bmoRFQU.getId(), bmoOpportunity.getRfquId().getName());
//				bmUpdateResult.setBmObject(bmoOpportunity);
//			
//			
//			
//		}  
//		return bmUpdateResult;
//	}
//	
//	//comprobar que no exista una oporutnidad con el mismo id de un rfqu
//	private boolean existOppo(PmConn pmConn,BmoRFQU bmoRFQU) throws SFPmException {
//		String sql = "SELECT * FROM opportunities where oppo_rfquid = " + bmoRFQU.getId();
//		pmConn.doFetch(sql);
//		pmConn.beforeFirst();
//		if(pmConn.next()) {
//			return true;	
//
//		}else {
//			return false;
//		}
//	}
//	
//
//}
