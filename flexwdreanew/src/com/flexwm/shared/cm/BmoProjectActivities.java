//package com.flexwm.shared.cm;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldOption;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//import com.symgae.shared.sf.BmoProfile;
//import com.symgae.shared.sf.BmoUser;
//
//public class BmoProjectActivities extends BmObject implements Serializable  {
//	private static final long serialVersionUID = 1L;
//	private BmField number,	name,profileId,userId,startDate,endDate,estimatedHours,realHours,progress,stepProjectId
//	,wFlowTypeId,wFlowId,dependencies,remindDate,status;
//	
//	private BmoProfile bmoProfile = new BmoProfile();
//	private BmoUser bmoUser = new BmoUser();
//	private BmoProjectStep bmoProjectStep = new BmoProjectStep();
//	
//	public static String ACTION_GETACTIVDEP = "ACTION_GETACTIVDEP";
//	
//	public static char STATUS_ACTIVE = 'A';
//	public static char STATUS_INACTIVE = 'N';
//	
//	public BmoProjectActivities() {
//		super("com.flexwm.server.cm.PmProjectActivities", "projectactivities", "projectactivitiesid", "PRAC", "Actividades de Proyecto");	
//		number = setField("number", "", "Numero", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
//		name = setField("name", "", "Nombre Actividad", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
//		profileId =setField("profileid", "", "Perfil", 20, Types.INTEGER, true, BmFieldType.ID, false);
//		userId = setField("userid", "", "Recurso", 8, Types.INTEGER, true, BmFieldType.ID, false);		
//		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
//		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		estimatedHours = setField("estimatedhours", "", "H. Est.", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
//		realHours  = setField("realhours", "", "H. Reales", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
//		progress = setField("progress", "", "Avance", 8, Types.INTEGER, false, BmFieldType.PERCENTAGE, false);
//		stepProjectId = setField("stepprojectid", "", "Proyecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		wFlowTypeId = setField("wflowtypeid", "", "Tipo WFlow", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		dependencies = setField("dependencies", "", "Predecesoras", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
//		remindDate = setField("reminddate", "", "Recordar el", 20, Types.DATE, true, BmFieldType.DATE, false);
//		status = setField("status", "" + STATUS_ACTIVE, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(STATUS_ACTIVE, "Activo", "../icons/boolean_true.png"),
//				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "../icons/boolean_false.png")
//				)));
//	}
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getNumber(),
//				getName(),
//				getBmoProfile().getName(),
//				getBmoUser().getCode(),
//				getDependencies(),
//				getStartDate(),
//				getEndDate(),
//				getEstimatedHours(),
//				getRealHours(),
//				getProgress(),
//				getStatus()
//				));
//	}
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(
//				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
//				));
//	}
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getName()), 
//				new BmSearchField(getBmoProfile().getName()),
//				new BmSearchField(getBmoUser().getCode()),
//				new BmSearchField(getBmoProjectStep().getCode()),
//				new BmSearchField(getBmoProjectStep().getName())
//				));
//	}
//	@Override
//	public ArrayList<BmField> getListBoxFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getNumber(),
//				getName(),
//				getBmoProjectStep().getCode()
//				));
//	}
//	
//
//	
//	public BmField getRemindDate() {
//		return remindDate;
//	}
//	public void setRemindDate(BmField remindDate) {
//		this.remindDate = remindDate;
//	}
//	public BmoProfile getBmoProfile() {
//		return bmoProfile;
//	}
//	public void setBmoProfile(BmoProfile bmoProfile) {
//		this.bmoProfile = bmoProfile;
//	}
//	public BmoUser getBmoUser() {
//		return bmoUser;
//	}
//	public void setBmoUser(BmoUser bmoUser) {
//		this.bmoUser = bmoUser;
//	}
//	public BmoProjectStep getBmoProjectStep() {
//		return bmoProjectStep;
//	}
//	public void setBmoProjectStep(BmoProjectStep bmoProjectStep) {
//		this.bmoProjectStep = bmoProjectStep;
//	}
//	public BmField getNumber() {
//		return number;
//	}
//	public void setNumber(BmField number) {
//		this.number = number;
//	}
//	public BmField getName() {
//		return name;
//	}
//	public void setName(BmField name) {
//		this.name = name;
//	}
//	public BmField getProfileId() {
//		return profileId;
//	}
//	public void setProfileId(BmField profileId) {
//		this.profileId = profileId;
//	}
//	public BmField getUserId() {
//		return userId;
//	}
//	public void setUserId(BmField userId) {
//		this.userId = userId;
//	}	
//	public BmField getStartDate() {
//		return startDate;
//	}
//	public void setStartDate(BmField startDate) {
//		this.startDate = startDate;
//	}
//	public BmField getEndDate() {
//		return endDate;
//	}
//	public void setEndDate(BmField endDate) {
//		this.endDate = endDate;
//	}
//	public BmField getEstimatedHours() {
//		return estimatedHours;
//	}
//	public void setEstimatedHours(BmField estimatedHours) {
//		this.estimatedHours = estimatedHours;
//	}
//	public BmField getRealHours() {
//		return realHours;
//	}
//	public void setRealHours(BmField realHours) {
//		this.realHours = realHours;
//	}
//	public BmField getProgress() {
//		return progress;
//	}
//	public void setProgress(BmField progress) {
//		this.progress = progress;
//	}
//	public BmField getStepProjectId() {
//		return stepProjectId;
//	}
//	public void setStepProjectId(BmField stepProjectId) {
//		this.stepProjectId = stepProjectId;
//	}
//	public BmField getWFlowTypeId() {
//		return wFlowTypeId;
//	}
//	public void setWFlowTypeId(BmField wFlowTypeId) {
//		this.wFlowTypeId = wFlowTypeId;
//	}
//	public BmField getWFlowId() {
//		return wFlowId;
//	}
//	public void setWFlowId(BmField wFlowId) {
//		this.wFlowId = wFlowId;
//	}
//	public BmField getDependencies() {
//		return dependencies;
//	}
//	public void setDependencies(BmField dependencies) {
//		this.dependencies = dependencies;
//	}
//	public BmField getStatus() {
//		return status;
//	}
//	public void setStatus(BmField status) {
//		this.status = status;
//	}	
//	
//	
//}
