///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//
//import com.flexwm.server.op.PmOrder;
//import com.flexwm.server.op.PmOrderGroup;
//import com.flexwm.server.op.PmOrderItem;
//import com.flexwm.server.wf.PmWFlow;
//import com.flexwm.server.wf.PmWFlowLog;
//import com.flexwm.server.wf.PmWFlowType;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.flexwm.shared.op.BmoOrder;
//import com.flexwm.shared.op.BmoOrderGroup;
//import com.flexwm.shared.op.BmoOrderItem;
//import com.flexwm.shared.wf.BmoWFlow;
//import com.flexwm.shared.wf.BmoWFlowLog;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.SFServerUtil;
//import com.symgae.server.sf.PmUser;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class PmProjectStep extends PmObject {
//	BmoProjectStep bmoProjectStep;
//
//	public PmProjectStep(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoProjectStep = new BmoProjectStep();
//		setBmObject(bmoProjectStep);
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoProjectStep.getCustomerId(),bmoProjectStep.getBmoCustomer()),
//				new PmJoin(bmoProjectStep.getBmoCustomer().getTerritoryId(), bmoProjectStep.getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoProjectStep.getBmoCustomer().getReqPayTypeId(), bmoProjectStep.getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoProjectStep.getUserId(), bmoProjectStep.getBmoUser()),
//				new PmJoin(bmoProjectStep.getUserId(), bmoProjectStep.getBmoUser().getBmoArea()),
//				new PmJoin(bmoProjectStep.getBmoUser().getLocationId(), bmoProjectStep.getBmoUser().getBmoLocation()),
//				new PmJoin(bmoProjectStep.getWFlowId(), bmoProjectStep.getBmoWFlow()),
//				new PmJoin(bmoProjectStep.getBmoWFlow().getWFlowTypeId(), bmoProjectStep.getBmoWFlow().getBmoWFlowType()),
//				new PmJoin(bmoProjectStep.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoProjectStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
//				new PmJoin(bmoProjectStep.getBmoWFlow().getWFlowPhaseId(), bmoProjectStep.getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoProjectStep.getBmoWFlow().getWFlowFunnelId(), bmoProjectStep.getBmoWFlow().getBmoWFlowFunnel())			
//				)));
//	}
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		BmoProjectStep bmoStepProject = (BmoProjectStep) autoPopulate(pmConn, new BmoProjectStep());
//		// BmoCustomer
//		BmoCustomer bmoCustomer = new BmoCustomer();
//		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
//		if (customerId > 0) bmoStepProject.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
//		else bmoStepProject.setBmoCustomer(bmoCustomer);
//		
//		BmoUser bmoUser = new BmoUser();
//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
//		if (userId > 0) bmoStepProject.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
//		else bmoStepProject.setBmoUser(bmoUser);
//		
//		BmoWFlow bmoWFlow = new BmoWFlow();
//		int wflowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
//		if (wflowId > 0) bmoStepProject.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
//		else bmoStepProject.setBmoWFlow(bmoWFlow);
//		
//		return bmoStepProject;
//
//	}
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
//		BmoProjectStep bmoStepProject = (BmoProjectStep)bmObject;	
//		BmoProjectStep bmoProjectStepPrev = new BmoProjectStep();
//		boolean isNew = false;
//		// Se almacena de forma preliminar para asignar Clave
//		if (!(bmoStepProject.getId() > 0)) {			
//			isNew = true;
//			bmoStepProject.getStatus().setValue(BmoProjectStep.STATUS_REVISION);
//			super.save(pmConn, bmoStepProject, bmUpdateResult);
//
//			bmoStepProject.setId(bmUpdateResult.getId());			
//		}else {
//			bmoProjectStepPrev = (BmoProjectStep)this.get(bmoStepProject.getId());
//		}
//		if (bmoStepProject.getCode().toString().equals("")) bmoStepProject.getCode().setValue(bmoStepProject.getCodeFormat());
//		//		
//		super.save(pmConn, bmoStepProject, bmUpdateResult);
//		
//		//Cambio de etatus
//		if(!bmoStepProject.getStatus().equals(bmoProjectStepPrev.getStatus())) {
//			if(bmoStepProject.getStatus().equals(BmoProjectStep.STATUS_CANCELLED) || bmoStepProject.getStatus().equals(BmoProjectStep.STATUS_FINISHED)) {
//				//desactivar tareas de proyecto
//				updateActivities(pmConn,bmUpdateResult,bmoProjectStepPrev,BmoProjectActivities.STATUS_INACTIVE);
//			}else if(bmoStepProject.getStatus().equals(BmoProjectStep.STATUS_AUTHORIZED) && 
//					(bmoProjectStepPrev.getStatus().equals(BmoProjectStep.STATUS_CANCELLED) || bmoProjectStepPrev.getStatus().equals(BmoProjectStep.STATUS_FINISHED))) {
//				//Activar actividades
//				updateActivities(pmConn,bmUpdateResult,bmoProjectStepPrev,BmoProjectActivities.STATUS_ACTIVE);
//			}else if(bmoStepProject.getStatus().equals(BmoProjectStep.STATUS_REVISION) && 
//					(bmoProjectStepPrev.getStatus().equals(BmoProjectStep.STATUS_CANCELLED) || 
//							bmoProjectStepPrev.getStatus().equals(BmoProjectStep.STATUS_FINISHED)
//							|| bmoProjectStepPrev.getStatus().equals(BmoProjectStep.STATUS_AUTHORIZED))) {
//				if(!bmUpdateResult.hasErrors());
//					updateActivities(pmConn,bmUpdateResult,bmoProjectStepPrev,BmoProjectActivities.STATUS_ACTIVE);
//			}
//		}
//
//		if (!bmUpdateResult.hasErrors()) {
//			
//			// Si no esta asignado el tipo de wflow, toma el primero
//			if (!(bmoStepProject.getWFlowTypeId().toInteger() > 0)) {
//				int wFlowTypeId = new PmWFlowType(getSFParams()).getFirstWFlowTypeId(pmConn, bmoStepProject.getProgramCode());
//				if (wFlowTypeId > 0) {
//					bmoStepProject.getWFlowTypeId().setValue(wFlowTypeId);
//				} else {
//					bmUpdateResult.addError(bmoStepProject.getCode().getName(), "Debe crearse Tipo de WFlow para Proyecto.");
//				}
//			}
//			char wflowstatus = BmoWFlow.STATUS_ACTIVE;
//		
//			// Crea el WFlow y asigna el ID recien creado
//			PmWFlow pmWFlow = new PmWFlow(getSFParams());
//			bmoStepProject.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoStepProject.getWFlowTypeId().toInteger(), bmoStepProject.getWFlowId().toInteger(), 
//					bmoStepProject.getProgramCode(), bmoStepProject.getId(), bmoStepProject.getUserCreateId().toInteger(), bmoStepProject.getCompanyId().toInteger(), -1,
//					bmoStepProject.getCode().toString(), bmoStepProject.getName().toString(), bmoStepProject.getDescription().toString(), 
//					bmoStepProject.getLockStart().toString(), bmoStepProject.getLockEnd().toString(),wflowstatus , bmUpdateResult).getId());
//
//		}	
//		//Agregar creacion de proyecto a la Bitacora
//		if(isNew) {
//			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
//			pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoStepProject.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, "Se Creo el Proyecto", "");
//		}
//		super.save(pmConn, bmoStepProject, bmUpdateResult);
//	
//		return bmUpdateResult;
//	}	
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoProjectStep = (BmoProjectStep)bmObject;
//		//Validar que no tenga tareas avanzadas
//		if(getProgresProjectActities(pmConn, bmoProjectStep.getId())) 
//			bmUpdateResult.addMsg("No se puede eliminar, tiene tareas avanzadas");
//		//Validar que no este Autorizado
//		if(bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_AUTHORIZED))
//			bmUpdateResult.addError(bmoProjectStep.getStatus().getName(),"No se puede eliminar, esta Autorizado");
//		if(bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_CANCELLED))
//			bmUpdateResult.addError(bmoProjectStep.getStatus().getName(),"No se puede eliminar, esta Cancelado");
//		if(bmoProjectStep.getStatus().equals(BmoProjectStep.STATUS_FINISHED))
//			bmUpdateResult.addError(bmoProjectStep.getStatus().getName(),"No se puede eliminar, esta Terminado");
//		if(!bmUpdateResult.hasErrors()) {
//			deleteActivities(pmConn, bmoProjectStep.getId());
//			//Gaurdar registro de la eliminación del proyecto en el pedido
//			addDatalog(pmConn,bmUpdateResult,bmoProjectStep,bmoProjectStep.getOrderId().toInteger());
//		}
//		super.delete(pmConn, bmObject, bmUpdateResult);
//		
//		
//		return bmUpdateResult;
//	}
//	//Creando el projecto apartir de un pedido
//	public BmUpdateResult createProject(PmConn pmConn,BmoOrder bmoOrder,BmUpdateResult bmUpdateResult) throws SFException {
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();
//		PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//		pmConn.open();
//		//Pasar los datos del pedido al nuevo proyecto
//		bmoProjectStep.getName().setValue(bmoOrder.getName().toString());
//		bmoProjectStep.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
//		bmoProjectStep.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
//		bmoProjectStep.getUserId().setValue(bmoOrder.getUserId().toInteger());
//		bmoProjectStep.getOrderId().setValue(bmoOrder.getId());
//		bmoProjectStep.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
//		bmoProjectStep.getDescription().setValue(bmoOrder.getDescription().toString());
//		bmoProjectStep.getLockStart().setValue(bmoOrder.getLockStart().toString());
//		bmoProjectStep.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
//		bmoProjectStep.getDefaultBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
//		bmoProjectStep.getDefaultAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
//		bmoProjectStep.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
//		bmoProjectStep.getStatus().setValue(BmoProjectStep.STATUS_REVISION);
//		
//		pmProjectStep.save(pmConn, bmoProjectStep, bmUpdateResult);
//		
//		
//		bmoProjectStep = (BmoProjectStep)bmUpdateResult.getBmObject();
//		BmFilter orderGroupsBmFilter = new BmFilter();
//		BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
//		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
//		BmoOrderItem bmoOrderItem = new BmoOrderItem();
//		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
//		//Crear las actividades del proyecto tomando los items del pedido si estan marcadas para crear proyecto
//		PmProjectActivities pmProjectActivities = new PmProjectActivities(getSFParams());
//		orderGroupsBmFilter.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), bmoProjectStep.getOrderId().toInteger());
//		BmFilter orderItemBmFilter = new BmFilter();		
//		Iterator<BmObject> orderGroupIterator = pmOrderGroup.list(orderGroupsBmFilter).iterator();
//		//Revizar los grupos de el pedido para obtener posterior los item
//		while (orderGroupIterator.hasNext()) {
//			bmoOrderGroup = (BmoOrderGroup)orderGroupIterator.next();
//			orderItemBmFilter.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId(), bmoOrderGroup.getId());
//			Iterator<BmObject> orderItemIterator = pmOrderItem.list(orderItemBmFilter).iterator();
//			//obtener items del grupo y cear actividades
//			while(orderItemIterator.hasNext()) {
//				bmoOrderItem = (BmoOrderItem)orderItemIterator.next();
//				BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//				if(bmoOrderItem.getCreateProject().toBoolean()) {
//					bmoProjectActivities.getName().setValue(bmoOrderItem.getName().toString());
//					bmoProjectActivities.getStartDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
//					bmoProjectActivities.getProgress().setValue(0);
//					bmoProjectActivities.getStepProjectId().setValue(bmoProjectStep.getId());
//					bmoProjectActivities.getWFlowId().setValue(bmoProjectStep.getWFlowId().toInteger());
//					bmoProjectActivities.getWFlowTypeId().setValue(bmoProjectStep.getWFlowTypeId().toInteger());
//					
//					pmProjectActivities.save(pmConn, bmoProjectActivities, bmUpdateResult);			
//					
//				}				
//			}		
//		}
//		
//		pmConn.close();		
//		
//		return bmUpdateResult;
//	}
//	//
//	private void deleteActivities(PmConn pmConn,int projectStep) throws SFException {
//		
//		PmProjectActivities pmProjectActivities = new PmProjectActivities(getSFParams());
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//		BmFilter bmFilter = new BmFilter();		
//		bmFilter.setValueFilter(bmoProjectActivities.getKind(),bmoProjectActivities.getStepProjectId(),""+projectStep);
//		Iterator<BmObject> Iterator = pmProjectActivities.list(bmFilter).iterator();
//		while(Iterator.hasNext()) {
//			bmoProjectActivities = (BmoProjectActivities)Iterator.next();
//			
//			super.delete(pmConn,bmoProjectActivities, new BmUpdateResult());
//			
//		}
//		
//	}
//	//aplicar status a actividades
//	public BmUpdateResult updateActivities(PmConn pmConn,BmUpdateResult bmUpdateResult,BmoProjectStep bmoProjectStep,char status) throws SFException {
//		BmFilter bmFilter = new BmFilter();
//		PmProjectActivities pmProjectActivities = new PmProjectActivities(getSFParams());
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//		bmFilter.setValueFilter(bmoProjectActivities.getKind(), bmoProjectActivities.getStepProjectId(), bmoProjectStep.getId());
//		Iterator<BmObject> Iterator = pmProjectActivities.list(bmFilter).iterator();
//		
//		while (Iterator.hasNext()) {
//			bmoProjectActivities = (BmoProjectActivities)Iterator.next();			
//			bmoProjectActivities.getStatus().setValue(status);
//			super.saveSimple(pmConn, bmoProjectActivities, bmUpdateResult);
//			printDevLog(bmUpdateResult.errorsToString());
//		}
//		
//		return bmUpdateResult;
//		
//	}
//	//Bitacora al eliminar 
//	public void addDatalog(PmConn pmConn,BmUpdateResult bmUpdateResult,BmoProjectStep bmoProjectStep,int orderId) throws SFException {
//		PmOrder pmOrder = new PmOrder(getSFParams());
//		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(orderId);
//		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
//		pmWFlowLog.addDataLog( bmUpdateResult,bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//				"Eliminación del Proyecto " + bmoProjectStep.getCode() , "");
//	}
//	//Revisa si hay tareas de proyecto avanzadas
//	public boolean getProgresProjectActities(PmConn pmConn,int projectStepid) throws SFPmException {
//		boolean result = false;
//		String sql = "SELECT prac_projectactivitiesid FROM projectactivities WHERE prac_stepprojectid = " + projectStepid + " AND prac_progress > 0";
//		PmConn pmConn2 = new PmConn(getSFParams());
//		pmConn2.open();
//		pmConn.doFetch(sql);
//		if(pmConn.next()) {
//			result = true;
//		}
//		pmConn2.close();
//		return result;		
//	}
//}
