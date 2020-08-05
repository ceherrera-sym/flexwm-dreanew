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

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerDetail;
import com.flexwm.client.cm.UiCustomerView;
import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.client.dash.UiPropertySaleDash;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiCustomerService;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertySaleDetail;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiTagBox;
import com.symgae.client.ui.UiTemplate;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiPropertySaleDetail extends UiDetail {

	protected BmoPropertySale bmoPropertySale;

	BmoOrder bmoOrder = new BmoOrder();

	BmoPropertySaleDetail bmoPropertySaleDetail = new BmoPropertySaleDetail();

	public UiPropertySaleDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoPropertySale(), id);
		bmoPropertySale = (BmoPropertySale)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoPropertySale = (BmoPropertySale)getBmObject();
		addDetailImage(bmoPropertySale.getBmoCustomer().getLogo());
		addTitleLabel(bmoPropertySale.getBmoProperty().getCode());
		addLabel(bmoPropertySale.getCode());
		addLabel(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getName());
		addLabel(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName());
		addLabel(bmoPropertySale.getBmoWFlowType().getBmoWFlowCategory().getName());
		addLabel(bmoPropertySale.getBmoWFlowType().getName());
		addLabel(bmoPropertySale.getStartDate());
		addLabel(bmoPropertySale.getType());
		addLabel(bmoPropertySale.getStatus());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoPropertySale.getTags());
		addDetailPanel(tagPanel);

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoPropertySale.getBmoCustomer()) 
				+ ": " + bmoPropertySale.getBmoCustomer().getCode());
		addLabel(bmoPropertySale.getBmoCustomer().getDisplayName());
		addLabel(bmoPropertySale.getBmoCustomer().getPhone());
		addLabel(bmoPropertySale.getBmoCustomer().getEmail());
		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoPropertySale.getBmoSalesUser()) 
				+ ": " + bmoPropertySale.getBmoSalesUser().getCode());
		addLabel(bmoPropertySale.getBmoSalesUser().getEmail());
		addLabel(bmoPropertySale.getBmoSalesUser().getPhone());

		// Obten info del pedido
		getBmoOrder();

	}

	public void getBmoOrder() {

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
				setLinks(new BmoOrder());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				setLinks((BmoOrder)result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoPropertySale.getOrderId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	public void setLinks(BmoOrder bmoOrder) {

		this.bmoOrder = bmoOrder;
		
		addActionLabel("Inicio", bmoPropertySale.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar forma
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showPropertySaleForm();
			}
		});

		// Mostrar datos adicionales si es de tipo inmueble
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			// Datos adicionales
			addActionLabel(new BmoPropertySaleDetail(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// llamada asyncrono
					getBmoPropertySaleDetail();
				}
			});
		}

		// Editar cliente
		addActionLabel("Cliente", new BmoCustomer().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showClient();
			}
		});

		// Tareas
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});

		// Pedido Venta Inm.
		addActionLabel(new BmoOrder(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrder();
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
		if(getUiParams().getSFParams().hasRead(new BmoRequisition().getProgramCode()))
			addActionLabel(new BmoRequisition(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showRequisition();
				}
			});

		// Quejas de los pedidos
		addActionLabel(new BmoCustomerService(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCustomerService();
			}
		});

		// Inmueble
		addActionLabel(new BmoProperty(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showProperty();
			}
		});

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
					close();
				}
			});
		}

		// Abrir oportunidad
		if (bmoPropertySale.getOpportunityId().toInteger() > 0) {
			addActionLabel("Ir a Oportunidad", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					showOpportunity();
				}
			});
		}

		// Preparar panel del este
		//showPropertySaleWestPanel();

		// Panel default inicial
		showStart();
	}

	public void getBmoPropertySaleDetail() {

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-getBmoPropertySaleDetail() ERROR: " + caught.toString());
				showPropertySaleWestPanel(bmoPropertySaleDetail);
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();
				bmoPropertySaleDetail = ((BmoPropertySaleDetail)result);
				showPropertySaleDetail(bmoPropertySaleDetail);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().getBy(bmoPropertySaleDetail.getPmClass(), bmoPropertySale.getId(), bmoPropertySaleDetail.getPropertySaleId().getName(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBmoPropertySaleDetail() ERROR: " + e.toString());
		}
	}

	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoPropertySale.getCustomerId().toInteger());
			uiCustomerDetail.show();
		} else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		}
	}

	@Override
	public void close() {
		UiPropertySale uiPropertySaleList = new UiPropertySale(getUiParams());
		setUiType(UiParams.MASTER);
		uiPropertySaleList.show();
	}

	public void returnToDashboard() {
		UiPropertySaleDash  uiPropertySaleDash = new UiPropertySaleDash(getUiParams());
		uiPropertySaleDash.show();
	}

	public void reset() {
		UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), bmoPropertySale.getId());
		uiPropertySaleDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertySaleStart uiPropertySaleStart = new UiPropertySaleStart(getUiParams(), bmObjectProgramId, bmoPropertySale);
		uiPropertySaleStart.show();
	}
	
	public void showPropertySaleDetail(BmoPropertySaleDetail bmoPropertySaleDetail) {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertySaleDetailForm uiPropertySaleDetailForm = new UiPropertySaleDetailForm(getUiParams(), bmoPropertySaleDetail.getId(), bmoPropertySale);
		setUiType(new BmoPropertySaleDetail().getProgramCode(), UiParams.SLAVE);
		uiPropertySaleDetailForm.show();
	}

	private void showOpportunity() {
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoPropertySale.getOpportunityId().toInteger());
		uiOpportunityDetail.show();
	}

	public void showPropertySaleForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertySale uiPropertySaleForm = new UiPropertySale(getUiParams());
		uiPropertySaleForm.edit(bmoPropertySale);
	}

	public void showClient() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomer uiCustomer = new UiCustomer(getUiParams());
		setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
		uiCustomer.edit(bmoPropertySale.getBmoCustomer());
	}

	public void showOrder() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertySaleStart uiPropertySaleStart = new UiPropertySaleStart(getUiParams(), bmObjectProgramId, bmoPropertySale);
		uiPropertySaleStart.show();
		
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoPropertySale.getOrderId().toInteger());
		setUiType(new BmoOrder().getProgramCode(), UiParams.SINGLESLAVE);
		uiOrder.show();
	}

	public void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoPropertySale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
		uiRaccountList.show();
	}

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoPropertySale.getBmoWFlow(), new PropertySaleWFlowStepAction());

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoPropertySale.getWFlowId().toInteger(), 
				bmoPropertySale.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);

		uiWFlowStepList.show();
	}

	public void showRequisition() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOrder);
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId().getName(), bmoPropertySale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
		uiRequisitionList.show();
	}

	public void showCustomerService() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomerService uiCustomerServiceList = new UiCustomerService(getUiParams());
		BmoCustomerService bmoCustomerService = new BmoCustomerService();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoCustomerService.getKind(), bmoCustomerService.getOrderId().getName(), bmoPropertySale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoCustomerService.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoCustomerService().getProgramCode(), UiParams.SLAVE);
		uiCustomerServiceList.show();
	}

	public void showProperty() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProperty uiPropertyForm = new UiProperty(getUiParams());
		setUiType(new BmoProperty().getProgramCode(), UiParams.SLAVE);
		uiPropertyForm.edit(bmoPropertySale.getBmoProperty());
	}

	public void showPropertySaleWestPanel(BmoPropertySaleDetail bmoPropertySaleDetail) {
		this.bmoPropertySaleDetail = bmoPropertySaleDetail;
		// Datos del cliente
		FlowPanel customerVP = new FlowPanel();
		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");

		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoPropertySale.getCustomerId().toInteger(), bmoPropertySale.getId());
		uiCustomerView.show();

		discPanel.add(customerVP);
		discPanel.setOpen(false);
		detailLabelTable.addDetailPanel(discPanel);
	}

	// Accion de una tarea
	public class PropertySaleWFlowStepAction implements IWFlowStepAction {

		@Override
		public void action() {
			reset();
		}
	}
}
