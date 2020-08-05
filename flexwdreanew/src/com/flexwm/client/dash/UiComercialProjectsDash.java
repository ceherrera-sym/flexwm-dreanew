/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.dash;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiOpportunity;
import com.flexwm.client.cm.UiOpportunityActiveListDreaNew;
import com.flexwm.client.cm.UiOpportunityCalendar;
import com.flexwm.client.cm.UiProject;
import com.flexwm.client.cm.UiProjectActiveList;
import com.flexwm.client.cm.UiProjectCalendar;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;


public class UiComercialProjectsDash extends UiDashboard {

	protected FlowPanel dashboard = new FlowPanel();

	
	public UiComercialProjectsDash(UiParams uiParams){
		super(uiParams, "Tablero Comercial", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/crms.png"));

		setShowUserInfo(true);
		dashboard.setStyleName("dashboard");


	}

	@Override
	public void populate() {
		clearDP();
		addToDP(dashboard);
		setWest();
	}

	@Override
	public void setDashboard(BmoUser bmoUser) {
		dashboard.clear();
		// Tareas activas
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())){
			FlowPanel tasksFP = new FlowPanel();
			UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), tasksFP, bmoUser);
			getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
			uiWFlowStepPendingList.show();
			dashboard.add(tasksFP);
		}
		// OPortunidades por fase
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart_drea3.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}
		// Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart_drea1.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}
		

		// Oportunidades activas
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			FlowPanel opportunityFP = new FlowPanel();
			UiOpportunityActiveListDreaNew uiOpportunityActiveList = new UiOpportunityActiveListDreaNew(getUiParams(), opportunityFP, bmoUser);
			getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MINIMALIST);
			uiOpportunityActiveList.show();
			dashboard.add(opportunityFP);
		}		
		// Ventas por desarrollo
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart_drea2.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}
		
		// Ventas por fase
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			Frame opportunityChartFrame = new Frame();
			opportunityChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/proj_projchart_month.jsp?user_userid=" + bmoUser.getId()));
			opportunityChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityChartFrame);
		}
		
		// Proyectos activos
		if (getUiParams().getSFParams().hasRead(new BmoProject().getProgramCode())) {
			FlowPanel projectFP = new FlowPanel();
			UiProjectActiveList uiProjectActiveList = new UiProjectActiveList(getUiParams(), projectFP, bmoUser);
			getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MINIMALIST);
			uiProjectActiveList.show();
			dashboard.add(projectFP);
		}

		addToDP(dashboard);
	}

	private void setWest() {
		// Crear prospecto
		if (getUiParams().getSFParams().hasWrite((new BmoCustomer().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomer uiCustomer = new UiCustomer(getUiParams());
					uiCustomer.create();
				}
			});

		// Crear oportunidad
		if (getUiParams().getSFParams().hasWrite((new BmoOpportunity().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoOpportunity()), new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
					uiOpportunity.create();
				}
			});

		// Ver calendario oportunidades
		if (getUiParams().getSFParams().hasRead("OPCA"))
			addActionLabel(getSFParams().getProgramTitle("OPCA"), "opca", new ClickHandler() {
				public void onClick(ClickEvent event) {
					setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
					new UiOpportunityCalendar(getUiParams()).show();
				}
			});

		// Crear proyecto
		if (getUiParams().getSFParams().hasWrite((new BmoProject().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoProject()), new BmoProject().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiProject uiProjectForm = new UiProject(getUiParams());
					uiProjectForm.create();
				}
			});

		// Ver calendario proyectos
		if (getUiParams().getSFParams().hasRead("PRCA"))
			addActionLabel(getSFParams().getProgramTitle("PRCA"), "prca", new ClickHandler() {
				public void onClick(ClickEvent event) {
					setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
					new UiProjectCalendar(getUiParams()).show();
				}
			});
	}
}
