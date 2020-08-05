package com.flexwm.client.dash;

import com.flexwm.client.op.UiConsultancyActiveList;
import com.flexwm.client.op.UiProduct;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionPendingList;
import com.flexwm.client.op.UiWhMovement;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhMovement;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;


public class UiConsultancyOperationsDash extends UiDashboard {

	private FlowPanel dashboard = new FlowPanel();

	public UiConsultancyOperationsDash(UiParams uiParams) {
		super(uiParams, "Tablero de Operaciones", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/opdc.png"));

		setShowUserInfo(true);
		dashboard.setStyleName("dashboard");
	}

	@Override
	public void populate() {
		clearDP();
		addToDP(dashboard);	
		setWest();
	}

	public void setDashboard(BmoUser bmoUser) {
		dashboard.clear();
		// Tareas activas
		if (getUiParams().getSFParams().hasRead(new BmoWFlowStep().getProgramCode())) {
			FlowPanel tasksFP = new FlowPanel();
			UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), tasksFP, bmoUser);
			getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
			uiWFlowStepPendingList.show();
			dashboard.add(tasksFP);
		}

		// Ordenes de Compra Activas
		if (getUiParams().getSFParams().hasRead(new BmoRequisition().getProgramCode())) {
			FlowPanel requisitionFP = new FlowPanel();
			UiRequisitionPendingList uiRequisitionPendingList = new UiRequisitionPendingList(getUiParams(), requisitionFP, bmoUser);
			getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MINIMALIST);
			uiRequisitionPendingList.show();
			dashboard.add(requisitionFP);
		}

		// Pedidos activos
		if (getUiParams().getSFParams().hasRead(new BmoConsultancy().getProgramCode())) {
			FlowPanel orderFP = new FlowPanel();
			UiConsultancyActiveList uiConsultancyActiveList = new UiConsultancyActiveList(getUiParams(), orderFP, bmoUser);
			getUiParams().setUiType(new BmoConsultancy().getProgramCode(), UiParams.MINIMALIST);
			uiConsultancyActiveList.show();
			dashboard.add(orderFP);
		}

		addToDP(dashboard);
	}

	private void setWest() {

		// Crear OC
		if (getUiParams().getSFParams().hasWrite(new BmoRequisition().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoRequisition().getProgramCode()), new BmoRequisition().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiRequisition uiRequisitionForm = new UiRequisition(getUiParams());
					setUiType(new BmoRequisition().getProgramCode(), UiParams.MASTER);
					uiRequisitionForm.create();
				}
			});

		// Crear Mov. Almacén
		if (getUiParams().getSFParams().hasWrite(new BmoWhMovement().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoWhMovement().getProgramCode()), new BmoWhMovement().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiWhMovement uiWhMovementForm = new UiWhMovement(getUiParams());
					setUiType(new BmoWhMovement().getProgramCode(), UiParams.MASTER);
					uiWhMovementForm.create();
				}
			});

		// Crear Producto
		if (getUiParams().getSFParams().hasWrite(new BmoProduct().getProgramCode()))
			addActionLabel("+ " + getSFParams().getProgramTitle(new BmoProduct().getProgramCode()), new BmoProduct().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiProduct uiProductForm = new UiProduct(getUiParams());
					setUiType(new BmoProduct().getProgramCode(), UiParams.MASTER);
					uiProductForm.create();
				}
			});
	}
}
