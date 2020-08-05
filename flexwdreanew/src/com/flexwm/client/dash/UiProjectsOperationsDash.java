package com.flexwm.client.dash;

import com.flexwm.client.cm.UiProjectActiveList;
import com.flexwm.client.cm.UiProjectCalendar;
import com.flexwm.client.op.UiProduct;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionPendingList;
import com.flexwm.client.op.UiWhMovement;
import com.flexwm.client.wf.UiWFlowStepPendingList;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiProjectsOperationsDash extends UiDashboard {

	private FlowPanel dashboard = new FlowPanel();

	public UiProjectsOperationsDash(UiParams uiParams) {
		super(uiParams, "Tablero " + uiParams.getSFParams().getProgramTitle(new BmoProject()), GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/pjda.png"));

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

		// Proyectos activos
		if (getUiParams().getSFParams().hasRead(new BmoProject().getProgramCode())) {
			FlowPanel projectFP = new FlowPanel();
			UiProjectActiveList uiProjectActiveList = new UiProjectActiveList(getUiParams(), projectFP);
			getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MINIMALIST);
			uiProjectActiveList.show();
			dashboard.add(projectFP);
		}

		// Calendario 
		if (getUiParams().getSFParams().hasRead("PRCA")) {
			FlowPanel calendarFP = new FlowPanel();
			UiProjectCalendar uiProjectCalendar = new UiProjectCalendar(getUiParams(), calendarFP);
			getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MINIMALIST);
			uiProjectCalendar.show();
			dashboard.add(calendarFP);
		}
		addToDP(dashboard);

		setWest();
	}

	private void setWest() {

		// Ver calendario proyectos
		if (getUiParams().getSFParams().hasRead("PRCA"))
			addActionLabel(getSFParams().getProgramTitle("PRCA"), "prca", new ClickHandler() {
				public void onClick(ClickEvent event) {
					setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
					new UiProjectCalendar(getUiParams()).show();
				}
			});

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

	//	private void setEast() {
	//		labelPanel.setWidth((UiParams.EASTSIZE - 10) + "px");
	//		labelDecoratorPanel.setWidget(labelPanel);
	//		labelDecoratorPanel.addStyleName("detailPanel");
	//
	//		getEastPanel().add(new HTML("<pre> </pre>"));
	//		getEastPanel().add(labelDecoratorPanel);
	//
	//		//Mostrar datos en la barra lateral derecha
	//		if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSysMessage().toString().equals("")) {
	//			labelPanel.add(detailLabelTitle("Mensaje sistema:"));
	//			labelPanel.add(detailText(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSysMessage().toString()));
	//		}
	//
	//		// Gráfica de oportunnidades por tipo
	//		FlowPanel oppoChartFP = new FlowPanel();
	//		oppoChartFP.setWidth((UiParams.EASTSIZE - 10) + "px");
	//		UiChartBar uiOppoChart = new UiChartBar(getUiParams(), oppoChartFP, "Oportunidades por Categoría", 
	//				" select wfca_code as Tipo, count(wflw_wflowid) as Oportunidades from opportunities " +
	//						" left join wflows on (oppo_wflowid = wflw_wflowid) " +
	//						" left join wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) " +
	//						" left join wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid) "
	//						+ " group by wfca_name ");
	//		uiOppoChart.setSize(UiParams.EASTSIZE - 10, 160);
	//		uiOppoChart.show();
	//		DecoratorPanel oppoChartDP = new DecoratorPanel();
	//		oppoChartDP.addStyleName("detailPanel");
	//		oppoChartDP.setWidth((UiParams.EASTSIZE - 10) + "px");
	//		oppoChartDP.setWidget(oppoChartFP);
	//		getUiParams().getEastPanel().add(oppoChartDP);
	//
	//		// Gráfica de proyectos por tipo
	//		FlowPanel chartFP = new FlowPanel();
	//		chartFP.setWidth((UiParams.EASTSIZE - 10) + "px");
	//		UiChartBar uiChart = new UiChartBar(getUiParams(), chartFP, "Proyectos por Tipo", 
	//				"select wfty_code as Tipo, count(wflw_wflowid) as Proyectos from projects " +
	//						" left join wflows on (proj_wflowid = wflw_wflowid) " +
	//				" left join wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) group by wfty_name");
	//		uiChart.setSize(UiParams.EASTSIZE - 10, 160);
	//		uiChart.show();
	//		DecoratorPanel chartDP = new DecoratorPanel();
	//		chartDP.addStyleName("detailPanel");
	//		chartDP.setWidth((UiParams.EASTSIZE - 10) + "px");
	//		chartDP.setWidget(chartFP);
	//		getUiParams().getEastPanel().add(chartDP);
	//
	//	}

}
