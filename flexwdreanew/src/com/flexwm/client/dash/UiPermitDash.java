package com.flexwm.client.dash;

import com.flexwm.client.co.UiDevelopmentPhaseActiveList;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionPendingList;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiPermitDash extends UiDashboard {

	private FlowPanel dashboard = new FlowPanel();

	public UiPermitDash(UiParams uiParams) {
		super(uiParams, "Tablero Trámites", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/cotr.png"));

		dashboard.setStyleName("dashboard");
	}

	@Override
	public void populate() {
		clearDP();

		// Tareas activas
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())) {
			FlowPanel tasksFP = new FlowPanel();
			UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), tasksFP);
			getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
			uiWFlowStepPendingList.show();
			dashboard.add(tasksFP);
		}

		// Ordenes de Compra Activas
		if (getUiParams().getSFParams().hasRead(new BmoRequisition().getProgramCode())) {
			FlowPanel requisitionFP = new FlowPanel();
			UiRequisitionPendingList uiRequisitionPendingList = new UiRequisitionPendingList(getUiParams(), requisitionFP);
			getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MINIMALIST);
			uiRequisitionPendingList.show();
			dashboard.add(requisitionFP);
		}

		// Fases de Desarrollos activos
		if (getUiParams().getSFParams().hasRead(new BmoDevelopmentPhase().getProgramCode())) {
			FlowPanel developmentPhaseFP = new FlowPanel();
			UiDevelopmentPhaseActiveList uiDevelopmentPhaseActiveList = new UiDevelopmentPhaseActiveList(getUiParams(), developmentPhaseFP);
			getUiParams().setUiType(new BmoDevelopmentPhase().getProgramCode(), UiParams.MINIMALIST);
			uiDevelopmentPhaseActiveList.show();
			dashboard.add(developmentPhaseFP);
		}

		addToDP(dashboard);

		setWest();
	}

	private void setWest() {
		
		// Inicio Tablero
		addActionLabel("Inicio", "cotr", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiPermitDash uiPermitDash = new UiPermitDash(getUiParams());
				uiPermitDash.show();	  
			}
		});
				
		// Crear oc
		if (getUiParams().getSFParams().hasWrite(new BmoRequisition().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoRequisition().getProgramCode()), new BmoRequisition().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiRequisition uiRequisitionForm = new UiRequisition(getUiParams());
					setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
					uiRequisitionForm.create();
				}
			});
	}
}
