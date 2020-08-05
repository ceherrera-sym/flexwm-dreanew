//package com.flexwm.client.cm;
//
//
//import com.flexwm.client.op.UiOrderDetail;
//import com.flexwm.client.wf.UiWFlowLog;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoOpportunity;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.flexwm.shared.wf.BmoWFlowLog;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.symgae.client.ui.UiDetail;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmFilter;
//
//public class UiProjectStepDetail extends UiDetail{
//	protected BmoProjectStep bmoProjectStep;
//	
//	public UiProjectStepDetail(UiParams uiParams, int id) {
//		super(uiParams, new BmoProjectStep(), id);
//		bmoProjectStep = (BmoProjectStep)getBmObject();
//	}
//
//	@Override
//	public void populateLabels() {
//		bmoProjectStep = (BmoProjectStep)getBmObject();
//		addTitleLabel(bmoProjectStep.getCode());
//		addLabel(bmoProjectStep.getName());		
//		addTitleLabel("Cliente " + bmoProjectStep.getBmoCustomer().getCode());
//		addLabel(bmoProjectStep.getBmoCustomer().getDisplayName());
//		addLabel(bmoProjectStep.getLockStart());
//		addLabel(bmoProjectStep.getLockEnd());
//		addLabel(bmoProjectStep.getStatus());
//		
//		
//		addActionLabel("Inicio", bmoProjectStep.getProgramCode(), new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				reset();
//			}
//		});
//		addActionLabel("Editar", "edit", new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				showStepProjectForm();
//			}
//		});
//		// Cliente
//		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				getUiParams().getUiTemplate().hideEastPanel();
//				UiCustomer uiCustomer = new UiCustomer(getUiParams());
//				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
//				uiCustomer.edit(bmoProjectStep.getBmoCustomer());
//			}
//		});
//
//		addActionLabel("Ir al Pedido", new BmoOpportunity().getProgramCode(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showOrder();
//			}
//		});			
//		addActionLabel("Bitacora",new BmoWFlowLog().getProgramCode(), new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				showLog();
//			}
//		});
//		addActionLabel("Ir a Listado", "close", new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				close();
//			}
//		});
//		showStart();
//	}
//	public void showLog() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams());
//		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
//		BmFilter bmFilter = new BmFilter();
//		bmFilter.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoProjectStep.getWFlowId().toInteger());
//		getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).setForceFilter(bmFilter);
//		setUiType(new BmoWFlowLog().getProgramCode(), UiParams.SLAVE);
//		uiWFlowLog.show();
//	}
//	
//	public void reset() {
//		
//		UiProjectStepDetail uiStepProjectDetail = new UiProjectStepDetail(getUiParams(), bmoProjectStep.getId());
//		uiStepProjectDetail.show();
//	}
//	public void showStepProjectForm() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiProjectStep uiProjectStep = new UiProjectStep(getUiParams());
//		uiProjectStep.edit(bmoProjectStep);
//	}
//
//	public void showOrder() {
//		UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(),bmoProjectStep.getOrderId().toInteger());
//		uiOrderDetail.show();
//	}
//	@Override
//	public void close() {
//		UiProjectStep uiStepProject = new UiProjectStep(getUiParams());
//		setUiType(UiParams.MASTER);
//		uiStepProject.show();
//	}
//	public void showStart() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiProjectStepStart uiStepProjectStart = new UiProjectStepStart(getUiParams(), bmObjectProgramId, bmoProjectStep);
//		uiStepProjectStart.show();
//	}
//
//
//}
