package com.flexwm.client.dash;

import com.flexwm.client.co.UiDevelopmentPhaseActiveList;
import com.flexwm.client.fi.UiLoan;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiLoanDash extends UiDashboard {
	private FlowPanel dashboard = new FlowPanel();

	public UiLoanDash(UiParams uiParams){
		super(uiParams, "Tablero Fondeo", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/fifo.png"));

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

		// Prestamos
		if (getUiParams().getSFParams().hasRead(new BmoLoan().getProgramCode())) {
			FlowPanel loanFP = new FlowPanel();
			UiLoan uiLoanPendingList = new UiLoan(getUiParams(), loanFP);
			getUiParams().setUiType(new BmoLoan().getProgramCode(), UiParams.MINIMALIST);
			uiLoanPendingList.show();
			dashboard.add(loanFP);
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
		addActionLabel("Inicio", "fifo", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiLoanDash uiLoanDash = new UiLoanDash(getUiParams());
				uiLoanDash.show();	  
			}
		});
				
		// Ver creditos
		if (getUiParams().getSFParams().hasWrite(new BmoLoan().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoLoan().getProgramCode()), new BmoLoan().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiLoan uiLoanForm = new UiLoan(getUiParams());
					setUiType(new BmoLoan().getProgramCode(), UiParams.SLAVE);
					uiLoanForm.create();
				}
			});
	}
}
