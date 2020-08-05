package com.flexwm.client.dash;

import com.flexwm.client.fi.UiBankMovement;
import com.flexwm.client.fi.UiBankMovementPendingList;
import com.flexwm.client.fi.UiPaccount;
import com.flexwm.client.fi.UiPaccountPendingList;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.fi.UiRaccountPendingList;
import com.flexwm.client.fi.UiRequisitionsPendingList;
import com.flexwm.client.op.UiOrderActiveWeekList;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.google.gwt.user.client.ui.Frame;


public class UiFinanceDash extends UiDashboard {
	protected FlowPanel dashboard = new FlowPanel();

	public UiFinanceDash(UiParams uiParams) {
		super(uiParams, "Tablero de Finanzas", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/fims.png"));
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
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())){
			FlowPanel tasksFP = new FlowPanel();
			UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), tasksFP, bmoUser);
			getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
			uiWFlowStepPendingList.show();
			dashboard.add(tasksFP);
		}
		// Facturas autorizadas---
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {	
			Frame invoiceChartFrame = new Frame();
			invoiceChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/fi/invoice_revision_chart.jsp?user_userid=" + bmoUser.getId()));
			invoiceChartFrame.setStyleName("dashboardPanel");
			dashboard.add(invoiceChartFrame);
		}
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {	
			Frame invoiceChartFrame = new Frame();
			invoiceChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/fi/racc_auto_revi_chart.jsp?user_userid=" + bmoUser.getId()));
			invoiceChartFrame.setStyleName("dashboardPanel");
			dashboard.add(invoiceChartFrame);
		}
		//OC
		if (getUiParams().getSFParams().hasRead(new BmoRequisition().getProgramCode())){
			if(getUiParams().getSFParams().hasRead("ODDR")) {						
				FlowPanel requisitionsFP = new FlowPanel();
				UiRequisitionsPendingList uiRequisitionsPendingList = new UiRequisitionsPendingList(getUiParams(), requisitionsFP);
				getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MINIMALIST);
				uiRequisitionsPendingList.show();
				dashboard.add(requisitionsFP);
			}
		}

		// CxP pendientes
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {
			FlowPanel paccountFP = new FlowPanel();
			UiPaccountPendingList uiPaccountPendingList = new UiPaccountPendingList(getUiParams(), paccountFP);
			getUiParams().setUiType(new BmoPaccount().getProgramCode(), UiParams.MINIMALIST);
			uiPaccountPendingList.show();
			dashboard.add(paccountFP);
		}
		// Facturas autorizadas---
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {	
			Frame invoiceChartFrame = new Frame();

			invoiceChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/fi/bkac_chart_saldo.jsp?user_userid=" + bmoUser.getId()));

			invoiceChartFrame.setStyleName("dashboardPanel");

			dashboard.add(invoiceChartFrame);
		}
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {	
			Frame invoiceChartFrame = new Frame();
			invoiceChartFrame.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "rpt/fi/racc_yearmonth_chart.jsp?user_userid=" + bmoUser.getId()));
			invoiceChartFrame.setStyleName("dashboardPanel");
			dashboard.add(invoiceChartFrame);
		}
		// Cuentas x Cobrar pendientes
		if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode())) {
			FlowPanel raccountFP = new FlowPanel();
			UiRaccountPendingList uiRaccountActiveList = new UiRaccountPendingList(getUiParams(), raccountFP);
			getUiParams().setUiType(new BmoRaccount().getProgramCode(), UiParams.MINIMALIST);
			uiRaccountActiveList.show();
			dashboard.add(raccountFP);
		}
		// Mov pendientes
		if (getUiParams().getSFParams().hasRead(new BmoBankMovement().getProgramCode())) {
			FlowPanel bankMovementFP = new FlowPanel();
			UiBankMovementPendingList uiBankMovementPendingList = new UiBankMovementPendingList(getUiParams(), bankMovementFP);
			getUiParams().setUiType(new BmoBankMovement().getProgramCode(), UiParams.MINIMALIST);
			uiBankMovementPendingList.show();
			dashboard.add(bankMovementFP);
		}
		
		// Pedidos activos
		if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())) {
			FlowPanel orderFP = new FlowPanel();
			UiOrderActiveWeekList uiOrderActiveWeekList = new UiOrderActiveWeekList(getUiParams(), orderFP, bmoUser);
			getUiParams().setUiType(new BmoOrder().getProgramCode(), UiParams.MINIMALIST);
			uiOrderActiveWeekList.show();
			dashboard.add(orderFP);
		}

		addToDP(dashboard);


	}
	private void setWest() {

		// Inicio Tablero
		addActionLabel("Inicio", "fims", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiFinanceDash uiFinanceDash = new UiFinanceDash(getUiParams());
				uiFinanceDash.show();	  
			}
		});

		//Crear OC
		if (getUiParams().getSFParams().hasWrite(new BmoRequisition().getProgramCode())) {
			if(getUiParams().getSFParams().hasRead("ODDR")) {	
				addActionLabel("+ " + getSFParams().getProgramTitle(new BmoRequisition().getProgramCode()), new BmoRequisition().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiRequisition uiRequisition = new UiRequisition(getUiParams());
						setUiType(new BmoRequisition().getProgramCode(), UiParams.MASTER);
						uiRequisition.create();
					}
				});
			}

		}
		// Crear cuenta x pagar
		if (getUiParams().getSFParams().hasWrite(new BmoPaccount().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoPaccount().getProgramCode()), new BmoPaccount().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
					setUiType(new BmoPaccount().getProgramCode(), UiParams.MASTER);
					uiPaccountForm.create();
				}
			});
		// Crear cuenta x cobrar
		if (getUiParams().getSFParams().hasWrite(new BmoRaccount().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoRaccount().getProgramCode()), new BmoRaccount().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiRaccount uiRaccountForm = new UiRaccount(getUiParams());
					setUiType(new BmoRaccount().getProgramCode(), UiParams.MASTER);
					uiRaccountForm.create();
				}
			});
		// Crear MB
		if (getUiParams().getSFParams().hasWrite(new BmoBankMovement().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoBankMovement().getProgramCode()), new BmoBankMovement().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiBankMovement uiBankMovementForm = new UiBankMovement(getUiParams());
					setUiType(new BmoBankMovement().getProgramCode(), UiParams.MASTER);
					uiBankMovementForm.create();
				}
			});
	}

}
