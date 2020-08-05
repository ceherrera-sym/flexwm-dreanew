//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectActivitiesDep;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//
//public class PmProjectActivitiesDep extends PmObject{
//	BmoProjectActivitiesDep bmoProjectActivitiesDep;
//	
//	public PmProjectActivitiesDep(SFParams sFParams) throws SFPmException {
//		super(sFParams);
//		bmoProjectActivitiesDep = new BmoProjectActivitiesDep();
//		setBmObject(bmoProjectActivitiesDep);
//		
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoProjectActivitiesDep.getProjectActivitieId(), bmoProjectActivitiesDep.getBmoProjectActivities()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getProfileId(), bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProfile()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getUserId(), bmoProjectActivitiesDep.getBmoProjectActivities().getBmoUser()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoUser().getAreaId(), 
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoUser().getBmoArea()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoUser().getLocationId(), 
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoUser().getBmoLocation()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getStepProjectId(), bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getCustomerId(), 
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getTerritoryId(), 
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getReqPayTypeId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getWFlowId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowTypeId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowPhaseId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowFunnelId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowFunnel()),
//				new PmJoin(bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(),
//						bmoProjectActivitiesDep.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory())
//				)));
//	}
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		BmoProjectActivitiesDep bmoProjectActivitiesDep = (BmoProjectActivitiesDep) autoPopulate(pmConn, new BmoProjectActivitiesDep());
//		
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();		
//		int wFlowStepId = (int)pmConn.getInt(bmoProjectActivities.getIdFieldName());
//		if (wFlowStepId > 0) bmoProjectActivitiesDep.setBmoProjectActivities((BmoProjectActivities)new PmProjectActivities(getSFParams()).populate(pmConn));
//		else bmoProjectActivitiesDep.setBmoProjectActivities(bmoProjectActivities);
//		
//		return bmoProjectActivitiesDep;
//	}
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		BmoProjectActivitiesDep bmoProjectActivitiesDep = (BmoProjectActivitiesDep)bmObject;
//		
//		super.save(pmConn, bmoProjectActivitiesDep, bmUpdateResult);
//		if(!bmUpdateResult.hasErrors()) {			
//			updateDependecies(pmConn,bmoProjectActivitiesDep.getChildProjectActivityId().toInteger());
////			PmProjectActivities pmProjectActivities = new PmProjectActivities(getSFParams());
////			pmProjectActivities.updateDependecies(pmConn, bmoProjectActivitiesDep.getChildProjectActivityId().toInteger());
//		}
//		return bmUpdateResult;	
//		
//	}
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoProjectActivitiesDep = (BmoProjectActivitiesDep)bmObject;		
//		
//		super.delete(pmConn, bmoProjectActivitiesDep, bmUpdateResult);
//		
//		updateDependecies(pmConn, bmoProjectActivitiesDep.getChildProjectActivityId().toInteger());
//		
//		return bmUpdateResult;
//	}
//	//actualizar campo de dependencia de tareas
//	public void updateDependecies(PmConn pmConn,int childProjectActivityId) throws SFException {
//		BmUpdateResult bmUpdateResult = new BmUpdateResult();
//		boolean isAdvanced = true;
//		String sql = "SELECT psdp_projectactivitieid FROM projectactivitiesdeps WHERE psdp_childprojectactivityid = " 
//				+ childProjectActivityId +" ORDER BY psdp_projectactivitieid ASC";
//		String result = "";
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//		BmoProjectActivities bmoProjectActivitiesChild = new BmoProjectActivities();
//		PmProjectActivities pmProjectActivities = new PmProjectActivities(getSFParams());
//		pmConn.doFetch(sql);
//		while(pmConn.next()) {			
//			bmoProjectActivities = (BmoProjectActivities)pmProjectActivities.get(pmConn.getInt("psdp_projectactivitieid"));
//			result += bmoProjectActivities.getNumber() + " ,";
//			if(isAdvanced) {
//				if(!(bmoProjectActivities.getProgress().toInteger() == 100))
//					isAdvanced = false;
//			}
//		}
//		bmoProjectActivitiesChild = (BmoProjectActivities)pmProjectActivities.get(childProjectActivityId);
//		bmoProjectActivitiesChild.getDependencies().setValue(result);
//	
//		pmProjectActivities.saveSimple(pmConn, bmoProjectActivitiesChild,bmUpdateResult);
//		if(!isAdvanced) {
//			sql = "UPDATE projectactivities SET prac_status ='" + BmoProjectActivities.STATUS_INACTIVE + "' WHERE prac_projectactivitiesid = " + childProjectActivityId;
//			pmConn.doUpdate(sql);
//		}
//	}
//	
//
//}
