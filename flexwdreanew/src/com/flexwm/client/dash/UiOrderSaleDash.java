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
import com.flexwm.client.cm.UiOpportunityActiveList;
import com.flexwm.client.op.UiOrderActiveList;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;


public class UiOrderSaleDash extends UiDashboard {
	private FlowPanel dashboard = new FlowPanel();

	public UiOrderSaleDash(UiParams uiParams) {
		super(uiParams, "Tablero Comercial", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/crmo.png"));

		setShowUserInfo(true);
		dashboard.setStyleName("dashboard");
	}

	@Override
	public void populate() {
		clearDP();
		addToDP(dashboard);
		setActions();
	}

	@Override
	public void setDashboard(BmoUser bmoUser) {
		dashboard.clear();
		
		// Tareas activas
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())) {
			FlowPanel tasksFP = new FlowPanel();
			UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), tasksFP, bmoUser);
			getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
			uiWFlowStepPendingList.show();
			uiWFlowStepPendingList.hideProgram();
			dashboard.add(tasksFP);
		}

		// Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_funnelchart.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}
		
		// Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			Frame opportunityChartFrame = new Frame();
			opportunityChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart.jsp?user_userid=" + bmoUser.getId()));
			opportunityChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityChartFrame);
		}
		
		// Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			// Oportunidades activos
			FlowPanel opportunityFP = new FlowPanel();
			UiOpportunityActiveList uiOpportunityActiveList = new UiOpportunityActiveList(getUiParams(), opportunityFP, bmoUser);
			getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MINIMALIST);
			uiOpportunityActiveList.show();
			uiOpportunityActiveList.hideProgram();
			dashboard.add(opportunityFP);
		}

		/*
		// Oportunidades por funnel
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {			
			Frame opportunityFunnelChart = new Frame();
			opportunityFunnelChart.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_funnel_chart.jsp?user_userid=" + bmoUser.getId()));
			opportunityFunnelChart.setStyleName("dashboardPanel");
			dashboard.add(opportunityFunnelChart);
		}
		*/

		// Por Tipo de pedidos
		if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())) {
			Frame orderTypeChartFrame = new Frame();
			orderTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/op/orde_chart.jsp?user_userid=" + bmoUser.getId()));
			orderTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(orderTypeChartFrame);
		}
		
		// Venta-Pedidos
		if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())) {
			Frame orderChartFrame = new Frame();
			orderChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/op/orde_salescompchart.jsp?user_userid=" + bmoUser.getId()));
			orderChartFrame.setStyleName("dashboardPanel");
			dashboard.add(orderChartFrame);
		}
		
		// Venta-Pedidos
		if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())) {
			// Pedidos activos
			FlowPanel orderFP = new FlowPanel();
			UiOrderActiveList uiOrderActiveList = new UiOrderActiveList(getUiParams(), orderFP, bmoUser);
			getUiParams().setUiType(new BmoOrder().getProgramCode(), UiParams.MINIMALIST);
			uiOrderActiveList.show();
			uiOrderActiveList.hideProgram();
			dashboard.add(orderFP);
		}	
	}

	private void setActions() {
		// Inicio Tablero
		addActionLabel("Inicio", "crmo", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiOrderSaleDash uiOrderSaleDash = new UiOrderSaleDash(getUiParams());
				uiOrderSaleDash.show();	  
			}
		});

		// Crear prospecto
		if (getUiParams().getSFParams().hasWrite((new BmoCustomer().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomer uiCustomer = new UiCustomer(getUiParams());
					uiCustomer.create();
				}
			});

		// Crear oportunidad
		if (getUiParams().getSFParams().hasWrite((new BmoOpportunity().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoOpportunity().getProgramCode()), new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
					uiOpportunity.create();
				}
			});

		// Crear pedido
		if (getUiParams().getSFParams().hasWrite((new BmoOrder().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoOrder().getProgramCode()), new BmoOrder().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), 0);
					setUiType(new BmoOrder().getProgramCode(), UiParams.MASTER);
					uiOrderForm.show();
				}
			});
	}

}
