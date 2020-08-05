package com.flexwm.client.rpt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoUserTimeClock;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiGERPReports extends UiDashboard {

	public UiGERPReports(UiParams uiParams){
		super(uiParams, "Reportes Gestión", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/gerp.png"));
	}

	@Override
	public void populate() {

		// Reporte de Tareas de Flujos
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoWFlowStep()), new BmoWFlowStep().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiWFlowStepReport uiWFlowStepReport = new UiWFlowStepReport(getUiParams());
					uiWFlowStepReport.show();
				}
			});

		// Reporte de Usuarios
		if (getUiParams().getSFParams().hasRead(new BmoUser().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoUser()), new BmoUser().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiUsersReport uiUsersReport = new UiUsersReport(getUiParams());
					uiUsersReport.show();
				}
			});

		// Reporte de Reloj checador
		//		if (getUiParams().getSFParams().hasRead(new BmoUserTimeClock().getProgramCode()))
		if (getUiParams().getSFParams().hasRead("RUTC"))
			addActionLabel(getSFParams().getProgramTitle(new BmoUserTimeClock()), new BmoUserTimeClock().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiUserTimeClockReport uiUserTimeClockReport = new UiUserTimeClockReport(getUiParams());
					uiUserTimeClockReport.show();
				}
			});

		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())){
			UiWFlowStepReport uiWFlowStepReport = new UiWFlowStepReport(getUiParams());
			uiWFlowStepReport.show();
		}
	}
}
