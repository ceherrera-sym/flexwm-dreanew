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
import com.flexwm.client.co.UiPropertySale;
import com.flexwm.client.co.UiPropertySaleActiveList;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.co.BmoPropertySale;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;


public class UiPropertySaleDash extends UiDashboard {

	private FlowPanel dashboard = new FlowPanel();

	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	BmoPropertySale bmoPropertySale = new BmoPropertySale();

	public UiPropertySaleDash(UiParams uiParams) {
		super(uiParams, "Tablero " + uiParams.getSFParams().getProgramTitle(new BmoPropertySale()) , GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/cops.png"));

		dashboard.setStyleName("dashboard");
		setShowUserInfo(true);
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
			uiWFlowStepPendingList.hideProgram();
			dashboard.add(tasksFP);
		}
		// Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart_mdm.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}

		// Oportunidades por deszarrollo
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			Frame opportunityChartFrame = new Frame();
			opportunityChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/cm/oppo_chart_mdm_desarrollo.jsp?user_userid=" + bmoUser.getId()));
			opportunityChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityChartFrame);
		}

		//Oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			// Oportunidades activas
			FlowPanel opportunityFP = new FlowPanel();
			UiOpportunityActiveList uiOpportunityActiveList = new UiOpportunityActiveList(getUiParams(), opportunityFP);
			getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MINIMALIST);
			uiOpportunityActiveList.show();
			//uiOpportunityActiveList.hideProgram();
			dashboard.add(opportunityFP);
		}

		// Ventas por desarrollo
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {	
			Frame opportunityTypeChartFrame = new Frame();
			opportunityTypeChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/co/prsa_chart_mdm_ventasDP.jsp?user_userid=" + bmoUser.getId()));
			opportunityTypeChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityTypeChartFrame);
		}

		// Ventas por fase
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode())) {
			Frame opportunityChartFrame = new Frame();
			opportunityChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/co/prsa_chart_mdm_ventasPF.jsp?user_userid=" + bmoUser.getId()));
			opportunityChartFrame.setStyleName("dashboardPanel");
			dashboard.add(opportunityChartFrame);
		}

		//Ventas de inmuebles
		if (getUiParams().getSFParams().hasRead(new BmoPropertySale().getProgramCode())) {
			// Ventas de Inmuebles activas
			FlowPanel propertySaleFP = new FlowPanel();
			UiPropertySaleActiveList uiPropertySaleActiveList = new UiPropertySaleActiveList(getUiParams(), propertySaleFP);
			getUiParams().setUiType(new BmoPropertySale().getProgramCode(), UiParams.MINIMALIST);
			uiPropertySaleActiveList.show();
			uiPropertySaleActiveList.hideProgram();
			dashboard.add(propertySaleFP);
		}

		addToDP(dashboard);
	}

	private void setWest() {

		// Inicio Tablero
		addActionLabel("Inicio", "cops", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiPropertySaleDash uiPropertySaleDash = new UiPropertySaleDash(getUiParams());
				uiPropertySaleDash.show();	  
			}
		});

		// Crear prospecto
		if (getUiParams().getSFParams().hasWrite((new BmoCustomer().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomer uiCustomer = new UiCustomer(getUiParams());
					setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
					uiCustomer.create();
				}
			});

		// Crear oportunidad
		if (getUiParams().getSFParams().hasWrite((new BmoOpportunity().getProgramCode())))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoOpportunity()), new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
					setUiType(new BmoOpportunity().getProgramCode(), UiParams.SLAVE);
					uiOpportunity.create();
				}
			});

		// Crear venta inmueble
		if (getUiParams().getSFParams().hasWrite((new BmoPropertySale().getProgramCode())))
			if (!getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_NOSALEDIRECT)) {
				addActionLabel("+ " + getSFParams().getProgramTitle(new BmoPropertySale()), new BmoPropertySale().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiPropertySale uiPropertySaleForm = new UiPropertySale(getUiParams());
						setUiType(new BmoPropertySale().getProgramCode(), UiParams.SLAVE);
						uiPropertySaleForm.create();
					}
				});
			}
	}
}
