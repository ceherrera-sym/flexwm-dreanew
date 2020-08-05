/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.flexwm.client.fi.UiBudget;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoDevelopmentRegistry;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoBudget;
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


public class UiDevelopmentPhaseDetail extends UiDetail {

	protected BmoDevelopmentPhase bmoDevelopmentPhase;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiDevelopmentPhaseDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoDevelopmentPhase(), id);
		bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();
		if (isMobile())
			addTitleLabel(bmoDevelopmentPhase.getCode());
		else 
			addTitleLabel(bmoDevelopmentPhase.getName());
		addLabel(bmoDevelopmentPhase.getCode());
		addLabel(bmoDevelopmentPhase.getStartDate());
		addLabel(bmoDevelopmentPhase.getEndDate());
		addLabel(bmoDevelopmentPhase.getIsActive());

		// Inicio
		addActionLabel("Inicio", bmoDevelopmentPhase.getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar forma
		addActionLabel("Editar", "edit", new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDevelopmentPhaseForm();
			}
		});

		// Paquetes

		addActionLabel(new BmoDevelopmentRegistry(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDevelopmentRegistry();
			}
		});

		// Manzanas
		addActionLabel(new BmoDevelopmentBlock(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDevelopmentBlocks();
			}
		});

		// Inmuebles
		addActionLabel(new BmoProperty(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showProperties();
			}
		});

		if (bmoDevelopmentPhase.getBudgetId().toInteger() > 0) {
			// Presupuesto
			addActionLabel(new BmoBudget(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					showBudget();
				}
			});
		}	

		// Tareas
		getBmoWFlow();

		// Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiDevelopmentPhase uiDevelopmentPhaseList = new UiDevelopmentPhase(getUiParams());
		setUiType(UiParams.MASTER);
		uiDevelopmentPhaseList.show();
	}

	public void reset() {
		UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), bmoDevelopmentPhase.getId());
		uiDevelopmentPhaseDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiDevelopmentPhaseStart uiDevelopmentPhaseStart = new UiDevelopmentPhaseStart(getUiParams(), bmObjectProgramId, bmoDevelopmentPhase);
		uiDevelopmentPhaseStart.show();
	}

	public void showDevelopmentPhaseForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiDevelopmentPhase uiDevelopmentPhaseForm = new UiDevelopmentPhase(getUiParams());
		uiDevelopmentPhaseForm.edit(bmoDevelopmentPhase);
	}

	public void showDevelopmentRegistry() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiDevelopmentRegistryList uiDevelopmentRegistryList = new UiDevelopmentRegistryList(getUiParams());
		BmoDevelopmentRegistry bmoDevelopmentRegistry = new BmoDevelopmentRegistry();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoDevelopmentRegistry.getKind(), bmoDevelopmentRegistry.getDevelopmentPhaseId().getName(), bmoDevelopmentPhase.getId());
		getUiParams().getUiProgramParams(bmoDevelopmentRegistry.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoDevelopmentRegistry().getProgramCode(), UiParams.SLAVE);
		uiDevelopmentRegistryList.show();
	}

	public void showDevelopmentBlocks() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiDevelopmentBlock uiDevelopmentBlockList = new UiDevelopmentBlock(getUiParams());
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoDevelopmentBlock.getKind(), bmoDevelopmentBlock.getDevelopmentPhaseId().getName(), bmoDevelopmentPhase.getId());
		getUiParams().getUiProgramParams(bmoDevelopmentBlock.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoDevelopmentBlock().getProgramCode(), UiParams.SLAVE);
		uiDevelopmentBlockList.show();
	}

	public void showProperties() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProperty uiPropertyList = new UiProperty(getUiParams());
		BmoProperty bmoProperty = new BmoProperty();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProperty.getKind(), bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId().getName(), bmoDevelopmentPhase.getId());
		getUiParams().getUiProgramParams(bmoProperty.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoProperty().getProgramCode(), UiParams.SLAVE);
		uiPropertyList.show();
	}

	public void showBudget() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiBudget uiBudget = new UiBudget(getUiParams());
		setUiType(new BmoBudget().getProgramCode(), UiParams.SLAVE);
		uiBudget.edit(bmoDevelopmentPhase.getBudgetId().toInteger());
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
				getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), bmoDevelopmentPhase.getWFlowId().toInteger(), callback);
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

		showReturnLinks();
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
				bmoDevelopmentPhase.getWFlowId().toInteger(), 
				bmoWFlow.getName().toString());

		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

	public void showReturnLinks() {
		//westTable.addSeparatorRow();

		// Si otro modulo hizo la llamada
		if (getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
			addActionLabel("Regresar", "close", new ClickHandler() {
				public void onClick(ClickEvent event) {
					close();
				}
			});
		} else {
			addActionLabel("Regresar", getUiParams().getCallerProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
				}
			});

			addActionLabel("Ir a Listado", "close", new ClickHandler() {
				public void onClick(ClickEvent event) {
					close();
				}
			});
		}

		// Si otro modulo hizo la llamada
		if (!getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
			addActionLabel("Ir a Tablero", "start", new ClickHandler() {
				public void onClick(ClickEvent event) {
					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
				}
			});
		}
	}
}
