//package com.flexwm.shared.op;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.flexwm.shared.cm.BmoProjectActivities;
////import com.flexwm.shared.cm.BmoRFQU;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldOption;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//import com.symgae.shared.sf.BmoUser;
//
//public class BmoServiceOrder extends BmObject implements Serializable {
//	private static final long serialVersionUID = 1L;
//	private BmField code, activity, type, startDate, endDate, estimateTime, realTime, costPerHour,
//					rfquId, projectActivityId, userId, hasReportTime;
//	
//	public static final char TYPE_SERVICE = 'S';
//	public static final char TYPE_DEMO = 'D';
//	
//	public static String CODE_PREFIX = "SRO-";
//	public static String ACTION_GETUSERRATE  = "GETUSERRATE";
//
//	BmoUser bmoUser = new BmoUser();
////	BmoRFQU bmoRFQU = new BmoRFQU();
//	BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//	
//	public BmoServiceOrder() {
//		super("com.flexwm.server.op.PmServiceOrder", "serviceorders", "serviceorderid", "SROR", "SRO");
//		code = setField("code", "", "No", 10, Types.VARCHAR, true, BmFieldType.STRING, true);
//		activity = setField("activity", "", "Actividad", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
//		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(TYPE_SERVICE, "Servicio", "./icons/sror_type_service.png"),
//				new BmFieldOption(TYPE_DEMO, "Demo", "./icons/sror_type_demo.png")
//				)));
//		startDate = setField("startdate", "", "Fecha Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		endDate = setField("enddate", "", "Fecha Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		estimateTime = setField("estimatetime", "", "H. Estimada", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
//		realTime = setField("realtime", "", "H. Real", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
//		costPerHour = setField("costperhour", "", "Costo x Hora", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
//		rfquId = setField("rfquid", "", "RFQ", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		projectActivityId = setField("projectactivitiesid", "", "Actividad de Proyecto", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		userId = setField("userid", "", "Recurso Tarea", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		hasReportTime = setField("hasreporttime", "0", "Tiene Registros Rep. Hrs.?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
//	}
//
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCode(),
//				getActivity(),
//				getType(),
//				getBmoUser().getCode(),
//				getStartDate(),
//				getEndDate(),
//				getEstimateTime(),
//				getRealTime(),
//				getCostPerHour()
//				));
//	}
//
//	@Override
//	public ArrayList<BmField> getListBoxFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getCode(),
//				getActivity()
//				));
//	}
//
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getCode().getName(), getCode().getLabel()), 
//				new BmSearchField(getActivity().getName(), getActivity().getLabel())));
//	}
//
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
//	}
//
//	public BmField getCode() {
//		return code;
//	}
//
//	public void setCode(BmField code) {
//		this.code = code;
//	}
//	
//	public BmField getActivity() {
//		return activity;
//	}
//
//	public void setActivity(BmField activity) {
//		this.activity = activity;
//	}
//
//	public BmField getType() {
//		return type;
//	}
//
//	public void setType(BmField type) {
//		this.type = type;
//	}
//
//	public BmField getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(BmField startDate) {
//		this.startDate = startDate;
//	}
//
//	public BmField getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(BmField endDate) {
//		this.endDate = endDate;
//	}
//
//	public BmField getEstimateTime() {
//		return estimateTime;
//	}
//
//	public void setEstimateTime(BmField estimateTime) {
//		this.estimateTime = estimateTime;
//	}
//
//	public BmField getRealTime() {
//		return realTime;
//	}
//
//	public void setRealTime(BmField realTime) {
//		this.realTime = realTime;
//	}
//
//	public BmField getCostPerHour() {
//		return costPerHour;
//	}
//
//	public void setCostPerHour(BmField costPerHour) {
//		this.costPerHour = costPerHour;
//	}
//
//	public BmField getProjectActivityId() {
//		return projectActivityId;
//	}
//
//	public void setProjectActivityId(BmField projectActivityId) {
//		this.projectActivityId = projectActivityId;
//	}
//	
//	public BmoProjectActivities getBmoProjectActivities() {
//		return bmoProjectActivities;
//	}
//
//	public void setBmoProjectActivities(BmoProjectActivities bmoProjectActivities) {
//		this.bmoProjectActivities = bmoProjectActivities;
//	}
//		
//	public BmField getUserId() {
//		return userId;
//	}
//
//	public void setUserId(BmField userId) {
//		this.userId = userId;
//	}
//	
//	public BmoUser getBmoUser() {
//		return bmoUser;
//	}
//
//	public void setBmoUser(BmoUser bmoUser) {
//		this.bmoUser = bmoUser;
//	}
//
//	public BmField getRfquId() {
//		return rfquId;
//	}
//
//	public void setRfquId(BmField rfquId) {
//		this.rfquId = rfquId;
//	}
//	
////	public BmoRFQU getBmoRFQU() {
////		return bmoRFQU;
////	}
////
////	public void setBmoRFQU(BmoRFQU bmoRFQU) {
////		this.bmoRFQU = bmoRFQU;
////	}
//	
//	public BmField getHasReportTime() {
//		return hasReportTime;
//	}
//
//	public void setHasReportTime(BmField hasReportTime) {
//		this.hasReportTime = hasReportTime;
//	}
//
//}
