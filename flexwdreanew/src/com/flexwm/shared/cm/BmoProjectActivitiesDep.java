//package com.flexwm.shared.cm;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//
//public class BmoProjectActivitiesDep extends BmObject implements Serializable  {
//	private static final long serialVersionUID = 1L;
//	private BmField projectActivitieId,childProjectActivityId;
//	private BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//	
//	public BmoProjectActivitiesDep() {
//		super("com.flexwm.server.cm.PmProjectActivitiesDep", "projectactivitiesdeps", "projectstepdepid", "PSDP", "Depend. Actividades");
//		projectActivitieId = setField("projectactivitieid", "", "Actividad Raíz", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		childProjectActivityId = setField("childprojectactivityid", "", "Actividad Raíz", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		
//	}
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//			getBmoProjectActivities().getNumber(),
//			getBmoProjectActivities().getName()
//			));
//	}		
//
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(
//				new BmOrder(getKind(), getBmoProjectActivities().getNumber(), BmOrder.ASC)
//				));
//	}
//	public BmField getProjectActivitieId() {
//		return projectActivitieId;
//	}
//	public void setProjectActivitieId(BmField projectActivitieId) {
//		this.projectActivitieId = projectActivitieId;
//	}
//	public BmoProjectActivities getBmoProjectActivities() {
//		return bmoProjectActivities;
//	}
//	public void setBmoProjectActivities(BmoProjectActivities bmoProjectActivities) {
//		this.bmoProjectActivities = bmoProjectActivities;
//	}
//	public BmField getChildProjectActivityId() {
//		return childProjectActivityId;
//	}
//	public void setChildProjectActivityId(BmField childProjectActivityId) {
//		this.childProjectActivityId = childProjectActivityId;
//	}
//	
//	
//
//}
