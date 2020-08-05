/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxClickHandler;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.client.cm.UiProjectDetail;
import com.flexwm.client.co.UiDevelopmentPhaseDetail;
import com.flexwm.client.co.UiPropertySaleDetail;
import com.flexwm.client.cv.UiMeetingDetail;
import com.flexwm.client.op.UiConsultancyDetail;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.cv.BmoMeeting;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiWFlowStepPendingList extends UiList {
	protected BmoWFlowStep bmoWFlowStep;
	protected UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	protected BmoUser bmoUser = new BmoUser();
	protected ValueChangeHandler<String> suggestionTextChangeHandler;
	protected BmFilter userFilter = new BmFilter();
	protected UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	boolean finishedTask = false;

	public UiWFlowStepPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		bmoWFlowStep = (BmoWFlowStep)getBmObject();

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}

	public UiWFlowStepPendingList(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		bmoWFlowStep = (BmoWFlowStep)getBmObject();
		this.bmoUser = bmoUser;

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();

	}

	@Override
	public void postShow() {		
		// Reasigna campos ordenamiento
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setOrderList(getBmObject().getOrderFields());

		//		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowStep.getStatus()), bmoWFlowStep, bmoWFlowStep.getStatus(), "" );
		//		addDateRangeFilterListBox(bmoWFlowStep.getRemindDate());
		// Filtro de tareas no terminadas
		// Filtrar por tareas no terminadas al 100%
		BmFilter pendingStepsFilter = new BmFilter();
		pendingStepsFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getProgress().getName(),
				bmoWFlowStep.getProgress().getName(),
				BmFilter.MINOR,
				100,
				"< 100%"
				);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(pendingStepsFilter);

		// Filtro de tareas activas
		BmFilter activeFilter = new BmFilter();
		activeFilter.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getEnabled(), "1");
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(activeFilter);

		// Filtro de wflows activas
		BmFilter activeWFlowFilter = new BmFilter();
		activeWFlowFilter.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getBmoWFlow().getStatus(), "" + BmoWFlow.STATUS_ACTIVE);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(activeWFlowFilter);

		// Te muestra el usuario seleccionado
		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
					bmoWFlowStep.getUserId().getName(), 
					bmoWFlowStep.getUserId().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		} 
		// No hay usuario seleccionado, filtra por el loggeado
		else {
			userFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
					bmoWFlowStep.getUserId().getName(), 
					bmoWFlowStep.getUserId().getLabel(), 
					BmFilter.EQUALS,
					getSFParams().getLoginInfo().getUserId(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}

		newImage.setVisible(false);
	}

	@Override
	public void displayList() {
		int col = 0;

		//		listFlexTable.addListTitleCell(0, col++, "WFlow");
		//		if (!isMobile())
		//			listFlexTable.addListTitleCell(0, col++, "Nombre WFlow");
		//		listFlexTable.addListTitleCell(0, col++, "Tarea");
		//		if (!isMobile()) {
		//			listFlexTable.addListTitleCell(0, col++, "Grupo");
		//			listFlexTable.addListTitleCell(0, col++, "Usuario");
		//			if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
		//				listFlexTable.addListTitleCell(0, col++, "Funnel");
		//			listFlexTable.addListTitleCell(0, col++, "Inicio");
		//			listFlexTable.addListTitleCell(0, col++, "Tipo");
		//			listFlexTable.addListTitleCell(0, col++, "Finalizar");
		//		}
		//		listFlexTable.addListTitleCell(0, col++, "Avance");

		addColumnHeader(col++, "WFlow");
		if (!isMobile())
			addColumnHeader(col++, "Nombre WFlow");
		addColumnHeader(col++, "Tarea");
		if (!isMobile()) {
			addColumnHeader(col++, bmoWFlowStep.getType());
			addColumnHeader(col++, bmoWFlowStep.getBmoProfile().getName());
			addColumnHeader(col++, bmoWFlowStep.getBmoUser().getCode());
			addColumnHeader(col++, bmoWFlowStep.getStartdate());
			if(getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
				addColumnHeader(col++, bmoWFlowStep.getBmoWFlowFunnel().getName());
			addColumnHeader(col++, bmoWFlowStep.getRemindDate());

		}
		addColumnHeader(col++, "Finalizar");
		addColumnHeader(col++, bmoWFlowStep.getProgress());	
		addColumnHeader(col++, bmoWFlowStep.getStatus());

		int row = 1;
		while (iterator.hasNext()) {
			col = 0;
			BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)iterator.next(); 

			// Abre el flujo
			Label wFlowLabel = new Label(bmoWFlowStep.getBmoWFlow().getCode().toString());
			wFlowLabel.addClickHandler(new UiClickHandler(bmoWFlowStep) {
				@Override
				public void onClick(ClickEvent arg0) {
					openWFlowCaller(bmObject);
				}
			});
			wFlowLabel.setStyleName("listCellLink");
			listFlexTable.setWidget(row, col++, wFlowLabel);			

			if (!isMobile()) 
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoWFlow().getName());

			Label wFlowStepLabel = new Label(bmoWFlowStep.getBmoWFlowPhase().getSequence() + "." + bmoWFlowStep.getSequence() + " - " + bmoWFlowStep.getName());
			wFlowStepLabel.addClickHandler(rowClickHandler);
			wFlowStepLabel.setStyleName("listCellLink");
			listFlexTable.setWidget(row, col++, wFlowStepLabel);	

			if (!isMobile()) {
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getType());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoProfile().getName());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoUser().getCode());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getStartdate());
				if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
					listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoWFlowFunnel().getName());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getRemindDate());

				// Boton de completar tarea
				CheckBox checkBox = new CheckBox("");
				checkBox.setWidth("10px");
				checkBox.setStyleName("squaredFour");
				if (bmoWFlowStep.getProgress().toInteger() == 100) {
					checkBox.setValue(true);
					checkBox.setTitle("Reiniciar Tarea");
				} else {
					checkBox.setValue(false);
					checkBox.setTitle("Completar Tarea");
				}
				checkBox.addClickHandler(new UiCheckBoxClickHandler(checkBox, bmoWFlowStep) {
					public void onClick(ClickEvent event) {
						if (checkBox.getValue()) {
							if (Window.confirm("¿Desea Completar la Tarea?"))
								checkBoxClick(checkBox.getValue(), this.bmObject);
							else
								// Si no confirma regresar al check original
								checkBox.setValue(false);
						} else {
							if (Window.confirm("¿Desea Reiniciar la Tarea?"))
								checkBoxClick(checkBox.getValue(), this.bmObject);
							else
								// Si no confirma regresar al check original
								checkBox.setValue(true);
						}
					}
				});
				listFlexTable.setWidget(row, col, checkBox);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellCheckBox");
			}
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getProgress());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getStatus());

			listFlexTable.formatRow(row);
			row++;
		}
	}

	@Override
	public void open(BmObject bmObject) {		
		bmoWFlowStep = (BmoWFlowStep)bmObject;

		UiWFlowStep uiWFlowStep = new UiWFlowStep(getUiParams(), bmoWFlowStep.getBmoWFlow());
		getUiParams().setUiType(bmoWFlowStep.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowStep.open(bmoWFlowStep);
	}

	// Abre el contenedor del WFlow
	protected void openWFlowCaller(BmObject bmObject) {
		BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)bmObject;
		int programId = 0, callerId = 0, projectProgramId = 0, opportunityProgramId = 0, 
				developmentPhaseProgramId = 0, propertySaleProgramId = 0, meetingId = 0, 
				orderProgramId = 0, consultancyProgramId = 0;

		// Obtiene IDs base
		programId = bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getProgramId().toInteger();
		callerId = bmoWFlowStep.getBmoWFlow().getCallerId().toInteger();

		// Obtiene ID de oportunidad
		try {
			opportunityProgramId = getSFParams().getProgramId(new BmoOpportunity().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			// showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Obtiene ID de pedido
		try {
			orderProgramId = getSFParams().getProgramId(new BmoOrder().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			// showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}
		
		// Obtiene ID de Ventas de consultoría
		try {
			consultancyProgramId = getSFParams().getProgramId(new BmoConsultancy().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			// showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Obtiene ID de proyecto
		try {
			projectProgramId = getSFParams().getProgramId(new BmoProject().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			//showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Obtiene ID de fase de desarrollo
		try {
			developmentPhaseProgramId = getSFParams().getProgramId(new BmoDevelopmentPhase().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			//showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Obtiene ID de venta de inmueble
		try {
			propertySaleProgramId = getSFParams().getProgramId(new BmoPropertySale().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			//showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Obtiene ID junta
		try {
			meetingId = getSFParams().getProgramId(new BmoMeeting().getProgramCode());
		} catch (SFException e) {
			// Error de modulo no encontrato, no hace nada
			//showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Es de tipo proyecto
		if (programId == projectProgramId) {
			getProjectFromOrder(callerId);
		} 
		// Es de tipo oportunidad
		else if (programId == opportunityProgramId) {
			UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), callerId);
			uiOpportunityDetail.show();
		} 
		// Es de tipo pedido
		else if (programId == orderProgramId) {
			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), callerId);
			uiOrderDetail.show();
		}
		// Es de tipo consultoria
		else if (programId == consultancyProgramId) {
			getConsultancyFromOrder(callerId);
		}
		// Es de tipo Etapa de Desarrollo
		else if (programId == developmentPhaseProgramId) {
			UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), callerId);
			uiDevelopmentPhaseDetail.show();
		} 
		// Es de tipo venta de inmnueble
		else if (programId == propertySaleProgramId) {
			getPropertySaleFromOrder(callerId);
		}
		// Es de tipo junta
		else if (programId == meetingId) {
			UiMeetingDetail uiMeetingDetail = new UiMeetingDetail(getUiParams(), callerId);
			uiMeetingDetail.show();
		}
	}

	// Obtiene el ID del proyecto desde el ID del pedido
	private void getProjectFromOrder(int orderId) {
		BmoProject bmoProject = new BmoProject();

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();

				openProjectDetail(result.getId());
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().getBy(bmoProject.getPmClass(), orderId, bmoProject.getOrderId().getName(), callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + e.toString());
		}
	}

	// Abre el detalle del proyecto
	public void openProjectDetail(int projectId) {
		UiProjectDetail uiProjectDashboard = new UiProjectDetail(getUiParams(), projectId);
		uiProjectDashboard.show();
	}

	// Obtiene el ID de la venta de inmueble desde el ID del pedido
	private void getPropertySaleFromOrder(int orderId) {
		BmoPropertySale bmoPropertySale = new BmoPropertySale();

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();

				openPropertySaleDetail(result.getId());
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().getBy(bmoPropertySale.getPmClass(), orderId, bmoPropertySale.getOrderId().getName(), callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + e.toString());
		}
	}

	// Abre el detalle de la venta de inmueble
	public void openPropertySaleDetail(int propertySaleId) {
		UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), propertySaleId);
		uiPropertySaleDetail.show();
	}
	
	// Obtiene el ID de la venta de inmueble desde el ID del pedido
	private void getConsultancyFromOrder(int orderId) {
		BmoConsultancy bmoConsultancy = new BmoConsultancy();

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getConsultancyFromOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				openConsultancyDetail(result.getId());
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().getBy(bmoConsultancy.getPmClass(), orderId, bmoConsultancy.getOrderId().getName(), callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getConsultancyFromOrder() ERROR: " + e.toString());
		}
	}

	// Abre el detalle de la venta de inmueble
	public void openConsultancyDetail(int consultancyId) {
		UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), consultancyId);
		uiConsultancyDetail.show();
	}

	// Guarda tarea
	private void checkBoxClick(boolean check, BmObject bmObject) {
		finishedTask = check;
		get(bmObject.getId());
	}

	@Override
	public void getNext(BmObject bmObject) {
		try {
			bmoWFlowStep = (BmoWFlowStep)bmObject;
			if (finishedTask) {
				bmoWFlowStep.getProgress().setValue(100);
				bmoWFlowStep.getComments().setValue("Click en Avance.");
			} else {
				bmoWFlowStep.getProgress().setValue(0);
				bmoWFlowStep.getComments().setValue("Click en Avance.");
			}

			save(bmoWFlowStep);

		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-checkBoxClick() ERROR: " + e.toString());
		}
	}	
}