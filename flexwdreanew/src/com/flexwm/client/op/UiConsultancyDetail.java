/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerDetail;
import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.client.dash.UiOrderSaleDash;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiCustomerService;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiTagBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiConsultancyDetail extends UiDetail {
	protected BmoConsultancy bmoConsultancy;
	protected BmoOrder bmoOrder = new BmoOrder();

	BmoMarket bmoMarket = new BmoMarket();
	private int bmoOrderRpcAttempt = 0;

	public UiConsultancyDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoConsultancy(), id);
		bmoConsultancy = (BmoConsultancy)getBmObject();		
	}

	@Override
	public void populateLabels() {
		bmoConsultancy = (BmoConsultancy)getBmObject();
		addDetailImage(bmoConsultancy.getBmoCustomer().getLogo());
		if (isMobile())
			addTitleLabel(bmoConsultancy.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoConsultancy) 
					+ ": " + bmoConsultancy.getCode());
		addLabel(bmoConsultancy.getName());
		addLabel(bmoConsultancy.getBmoOrderType().getName());	
		addLabel(bmoConsultancy.getBmoWFlow().getBmoWFlowType().getName());
		addLabel(bmoConsultancy.getBmoWFlow().getBmoWFlowPhase().getName());
		addLabel(bmoConsultancy.getStartDate());
		addLabel(bmoConsultancy.getBmoWFlow().getProgress());
		addLabel(bmoConsultancy.getStatus());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoConsultancy.getTags());
		addDetailPanel(tagPanel);	

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoConsultancy.getBmoCustomer()) 
				+ ": " + bmoConsultancy.getBmoCustomer().getCode());
		addLabel(bmoConsultancy.getBmoCustomer().getDisplayName());
		addLabel(bmoConsultancy.getBmoCustomer().getPhone());
		addLabel(bmoConsultancy.getBmoCustomer().getEmail());
		addEmptyLabel();

		getBmoOrder();

	}
	public void setLinks(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;

		// Inicio
		addActionLabel("Inicio", bmoConsultancy.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});
		
		// Pedido
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrder();
			}
		});

		// Editar consultoria
		addActionLabel("Detalle", "cons", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConsultancyForm();
			}
		});

		// Editar Cliente
		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiParams().getUiTemplate().hideEastPanel();
				UiCustomer uiCustomer = new UiCustomer(getUiParams());
				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
				uiCustomer.edit(bmoConsultancy.getBmoCustomer());
			}
		});

		// Tareas
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});

		// Cuentas x Cobrar
		addActionLabel(new BmoRaccount(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showRaccount();
			}
		});

		// Ordenes de compra
		addActionLabel(new BmoRequisition(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showRequisition();
			}
		});

		// Envio Pedido
		addActionLabel(new BmoOrderDelivery(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrderDelivery();
			}
		});

		// Quejas de los pedidos
		addActionLabel(new BmoCustomerService(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCustomerService();
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
					openCallerProgram();
				}
			});
			addActionLabel("Ir a Listado", "close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().setCallerProgramCode(new BmoConsultancy().getProgramCode());;
					close();
				}
			});
		}

		// Abrir oportunidad
		if (bmoConsultancy.getOpportunityId().toInteger() > 0) {
			addActionLabel("Ir a Oportunidad", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showOpportunity();
				}
			});
		}

		// Preparar panel-tablero inicial
		showStart();

	}

	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoConsultancy.getCustomerId().toInteger());
			uiCustomerDetail.show();
		} else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		}
	}

	@Override
	public void close() {
		UiConsultancy uiConsultancy = new UiConsultancy(getUiParams());
		setUiType(UiParams.MASTER);
		uiConsultancy.show();
	}

	public void returnToDashboard() {
		UiOrderSaleDash uiOrderSaleDash = new UiOrderSaleDash(getUiParams());
		uiOrderSaleDash.show();
	}

	public void reset() {
		UiConsultancyDetail uiOrderDetail = new UiConsultancyDetail(getUiParams(), bmoConsultancy.getId());
		uiOrderDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();		
		UiConsultancyStart uiConsultancyStart = new UiConsultancyStart(getUiParams(), bmObjectProgramId, bmoConsultancy);
		uiConsultancyStart.show();
	}

	private void showConsultancyForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiConsultancy uiConsultancy = new UiConsultancy(getUiParams());
		uiConsultancy.edit(bmoConsultancy);
	}

	// Mostrar oportunidad
	private void showOpportunity() {
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoConsultancy.getOpportunityId().toInteger());
		uiOpportunityDetail.show();
	}

	// Mostrar O.C.
	public void showRequisition() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOrder);
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId().getName(), bmoConsultancy.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
		uiRequisitionList.show();
	}

	// Mostrar Pedido
	public void showOrder() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoConsultancy.getOrderId().toInteger());
		setUiType(new BmoOrder().getProgramCode(), UiParams.SLAVE);
		uiOrder.show();
	}

	// Mostrar CxC
	public void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoConsultancy.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
		uiRaccountList.show();
	}

	//Envio de pedidos
	public void showOrderDelivery(){
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderDelivery uiOrderDelivery = new UiOrderDelivery(getUiParams(), bmoOrder);
		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoOrderDelivery.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoOrderDelivery().getProgramCode(), UiParams.SLAVE);
		uiOrderDelivery.show();
	}

	// Mostrar Atencion a Clientes
	public void showCustomerService() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomerService uiCustomerServiceList = new UiCustomerService(getUiParams());
		BmoCustomerService bmoCustomerService = new BmoCustomerService();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoCustomerService.getKind(), bmoCustomerService.getOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoCustomerService.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoCustomerService().getProgramCode(), UiParams.SLAVE);
		uiCustomerServiceList.show();
	}

	// Mostrar Tareas
	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();

		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoConsultancy.getBmoWFlow(), wFlowStepAction);

		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoConsultancy.getWFlowId().toInteger(), 
				bmoConsultancy.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

	// Accion de una tarea
	public class SFComponentWFlowStepAction implements IWFlowStepAction {
		@Override
		public void action() {
			reset();
		}
	}

	// Obtener pedido, primer intento
	public void getBmoOrder() {
		getBmoOrder(0);
	}

	// Obtener pedido
	public void getBmoOrder(int bmoOrderRpcAttempt ) {
		if (bmoOrderRpcAttempt < 5) {
			setBmoOrderRpcAttempt(bmoOrderRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoOrderRpcAttempt() < 5)
						getBmoOrder(getBmoOrderRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + caught.toString());
						setLinks(new BmoOrder());
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoOrderRpcAttempt(0);
					setLinks((BmoOrder)result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoConsultancy.getOrderId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
			}
		}
	}

	// Variables para llamadas RPC
	public int getBmoOrderRpcAttempt() {
		return bmoOrderRpcAttempt;
	}

	public void setBmoOrderRpcAttempt(int bmoOrderRpcAttempt) {
		this.bmoOrderRpcAttempt = bmoOrderRpcAttempt;
	}

}
