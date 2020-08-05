/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.cv.BmoActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiActivityDetail extends UiDetail {

	protected BmoActivity bmoActivity;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiActivityDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoActivity(), id);
		bmoActivity = (BmoActivity)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoActivity = (BmoActivity)getBmObject();
		addLabel(bmoActivity.getCode());
		addLabel(bmoActivity.getName());
		addLabel(bmoActivity.getStartdate());
		addLabel(bmoActivity.getEnddate());

		// Inicio
		addActionLabel("Inicio", bmoActivity.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar forma
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showActivityForm();
			}
		});

		// Tareas
		getBmoWFlow();

		// Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiActivity uiActivityList = new UiActivity(getUiParams());
		setUiType(UiParams.MASTER);
		uiActivityList.show();
	}

	public void reset() {
		UiActivityDetail uiActivityDetail = new UiActivityDetail(getUiParams(), bmoActivity.getId());
		uiActivityDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiActivityStart uiActivityStart = new UiActivityStart(getUiParams(), bmObjectProgramId, bmoActivity);
		uiActivityStart.show();
	}

	public void showActivityForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiActivity uiActivityForm = new UiActivity(getUiParams());
		uiActivityForm.edit(bmoActivity);
	}

	public void getBmoWFlow() {


		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				setStepsLink((BmoWFlow)result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), bmoActivity.getWFlowId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	private void setStepsLink(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;

		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});

		// Si otro modulo hizo la llamada
		if (getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
			addActionLabel("Regresar", "close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					close();
				}
			});
		} else {
			addActionLabel("Regresar", getUiParams().getCallerProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
				}
			});

			addActionLabel("Ir a Listado", "close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					close();
				}
			});
		}


		// Si otro modulo hizo la llamada
		//		if (!getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
		//			westTable.addActionLabel("Ir a Tablero", "start", new ClickHandler() {
		//				public void onClick(ClickEvent event) {
		//					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		//				}
		//			});
		//		}
	}

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoWFlow);

		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoActivity.getWFlowId().toInteger(), 
				bmoWFlow.getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}
}
