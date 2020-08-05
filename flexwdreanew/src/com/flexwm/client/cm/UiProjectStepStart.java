//package com.flexwm.client.cm;
//
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.symgae.client.ui.Ui;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmFilter;
//
//public class UiProjectStepStart extends Ui {
//	private FlexTable flexTable = new FlexTable();
//	private BmoProjectStep bmoProjectStep;
//	
//	public UiProjectStepStart(UiParams uiParams, int programId, BmoProjectStep bmoProjectStep){
//		super(uiParams, new BmoProjectStep());
//		this.bmoProjectStep = bmoProjectStep;		
//		flexTable.setSize("100%", "100%");
//	}
//	public void show() {
//		clearDP();
//		getUiParams().getUiTemplate().hideProgramButtonPanel();
//		getUiParams().getUiTemplate().hideProgramExtrasPanel();
//		getUiParams().getUiTemplate().hideEastPanel();		
//		//Actividades
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();		
//		FlowPanel projectActivitiesPanel = new 	FlowPanel();
//		BmFilter projActivitiesFilter = new BmFilter();
//		projActivitiesFilter.setValueFilter(bmoProjectActivities.getKind(), bmoProjectActivities.getStepProjectId(), bmoProjectStep.getId());		
//		getUiParams().setForceFilter(bmoProjectActivities.getProgramCode(), projActivitiesFilter);
//		UiProjectActivities uiProjectActivities = new UiProjectActivities(getUiParams(),bmoProjectStep.getId(),bmoProjectStep.getWFlowTypeId().toInteger(),bmoProjectStep.getWFlowId().toInteger());
//		setUiType(bmoProjectActivities.getProgramCode(),UiParams.MINIMALIST);
//		uiProjectActivities.show();
//		flexTable.setWidget(0, 0, projectActivitiesPanel);
//	
//
//		addToDP(flexTable);
//	}
//}
