/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiCustomerService;
import com.flexwm.client.op.UiOrderDelivery;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.op.UiOrderList;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectDetail;
import com.flexwm.shared.cm.BmoProjectGuideline;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoCustomerService;
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
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiProjectDetail extends UiDetail {
	protected BmoProject bmoProject;

	BmoOrder bmoOrder = new BmoOrder();
	BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
	BmoProjectGuideline bmoProjectGuideline = new BmoProjectGuideline();
	BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
	
	private int bmoOrderRpcAttempt = 0;
	private int bmoProjectDetailRpcAttempt = 0;
	private int bmoProjectGuidelinesRpcAttempt = 0;
	private int bmoCustomerAddressRpcAttempt = 0;
	
	public UiProjectDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoProject(), id);
		bmoProject = (BmoProject)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoProject = (BmoProject)getBmObject();
		addDetailImage(bmoProject.getBmoCustomer().getLogo());
		if (isMobile())
			addTitleLabel(" " + bmoProject.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoProject) 
					+ ": " + bmoProject.getCode());
		addLabel(bmoProject.getName());
		addLabel(bmoProject.getBmoOrderType().getName());	
		addLabel(bmoProject.getBmoWFlow().getBmoWFlowType().getName());
		addLabel(bmoProject.getBmoWFlow().getBmoWFlowPhase().getName());
		addLabel(bmoProject.getStartDate());
		addLabel(bmoProject.getBmoWFlow().getProgress());
		addLabel(bmoProject.getOpportunityId());
		addLabel(bmoProject.getStatus());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoProject.getTags());
		addDetailPanel(tagPanel);

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoProject.getBmoCustomer()) 
				+ ": " + bmoProject.getBmoCustomer().getCode());
		addLabel(bmoProject.getBmoCustomer().getDisplayName());
		addLabel(bmoProject.getBmoCustomer().getPhone());
		addLabel(bmoProject.getBmoCustomer().getEmail());
		addEmptyLabel();
		
		
		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoProject.getBmoUser()) 
				+ ": " + bmoProject.getBmoUser().getCode());
			addLabel(bmoProject.getBmoUser().getEmail());
			addLabel(bmoProject.getBmoCustomer().getPhone());

		// Obten info del pedido
		
		addTitleLabel("Lugar del evento:");
		if (bmoProject.getVenueId().toInteger() > 0) {
			addLabel(bmoProject.getBmoVenue().getName());
			addLabel(bmoProject.getBmoVenue().getStreet());
			addLabel(bmoProject.getBmoVenue().getNumber());
			addLabel(bmoProject.getBmoVenue().getNeighborhood());
			addLabel(bmoProject.getBmoVenue().getBmoCity().getName());
			addLabel(bmoProject.getBmoVenue().getBmoCity().getBmoState().getCode());
			getBmoOrder();
		} else {
			getBmoCustomerAddress();
		}
	}
	
	// Obtener direccion del cliente, primer intento
	public void getBmoCustomerAddress() {
		getBmoCustomerAddress(0);
	}
	// Obtener direccion del cliente
	public void getBmoCustomerAddress(int bmoCustomerAddressRpcAttempt) {
		if (bmoCustomerAddressRpcAttempt < 5) {
			setBmoCustomerAddressRpcAttempt(bmoCustomerAddressRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoCustomerAddressRpcAttempt() < 5)
						getBmoCustomerAddress(getBmoCustomerAddressRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoCustomerAddress() ERROR: " + caught.toString());
						getBmoOrder();
					}
				}
	
				public void onSuccess(BmObject result) {
					stopLoading();	
					setBmoCustomerAddressRpcAttempt(0);
					bmoCustomerAddress = (BmoCustomerAddress )result;
					addLabel(bmoCustomerAddress.getStreet());	
					addLabel(bmoCustomerAddress.getNeighborhood());
					getBmoOrder();
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoCustomerAddress.getPmClass(), bmoProject.getCustomerAddressId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoCustomerAddress ERROR: " + e.toString());
			}
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
					getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoProject.getOrderId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
			}
		}
	}

	// Obtener order del dia
	public void getBmoProjectGuidelines() {
		getBmoProjectGuidelines(0);
	}
	public void getBmoProjectGuidelines(int bmoProjectGuidelinesRpcAttempt) {
		if (bmoProjectGuidelinesRpcAttempt < 5) {
			setBmoProjectGuidelinesRpcAttempt(bmoProjectGuidelinesRpcAttempt + 1);
			
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoProjectGuidelinesRpcAttempt() < 5)
						getBmoProjectGuidelines(getBmoProjectGuidelinesRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoProjectGuidelines() ERROR: " + caught.toString());
					}
				}
	
				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoProjectGuidelinesRpcAttempt(0);
					bmoProjectGuideline = ((BmoProjectGuideline)result);
					showProjectGuidelines(bmoProjectGuideline);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoProjectGuideline.getPmClass(), bmoProject.getId(), bmoProjectGuideline.getProjectId().getName(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoProjectGuidelines() ERROR: " + e.toString());
			}
		}
	}
	
	// Obtener detalle de proyecto, primer intento
	public void getBmoProjectDetail() {
		getBmoProjectDetail(0);
	}
	// Obtener detalle de proyecto
	public void getBmoProjectDetail(int bmoProjectDetailRpcAttempt ) {
		if (bmoProjectDetailRpcAttempt < 5) {
			setBmoProjectDetailRpcAttempt(bmoProjectDetailRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoProjectDetailRpcAttempt() < 5)
						getBmoProjectDetail(getBmoProjectDetailRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoProjectDetail() ERROR: " + caught.toString());
					}
				}
	
				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoProjectDetailRpcAttempt(0);
					bmoProjectDetail = ((BmoProjectDetail)result);
					showProjectDetail(bmoProjectDetail);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoProjectDetail.getPmClass(), bmoProject.getId(), bmoProjectDetail.getProjectId().getName(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoProjectDetail() ERROR: " + e.toString());
			}
		}
	}

	public void setLinks(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
		
		addActionLabel("Inicio", bmoProject.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar oportunidad
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showProjectForm();
			}
		});

		// Orden del dia
		addActionLabel(new BmoProjectGuideline(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getBmoProjectGuidelines();
			}
		});

		// Detalle proyecto
		addActionLabel(new BmoProjectDetail(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getBmoProjectDetail();
			}
		});

		// Cliente
		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiParams().getUiTemplate().hideEastPanel();
				UiCustomer uiCustomer = new UiCustomer(getUiParams());
				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
				uiCustomer.edit(bmoProject.getBmoCustomer());
			}
		});


		// Tareas
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});

		// Pedido proyecto
		addActionLabel(new BmoOrder(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrder();
			}
		});
		
		// Envio Pedido
		addActionLabel(new BmoOrderDelivery(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrderDelivery();
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
		
		// Pedidos Extras
		if (bmoProject.getBmoOrderType().getEnableExtraOrder().toBoolean())
			addActionLabel("Extras",new BmoOrder().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showOrderExtra();
				}
			});
		
//		addActionLabel("+ Ped Ext.", new BmoOpportunity().getProgramCode(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				addExtraOrder();
//			}
//		});

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
					close();
				}
			});
		}

		// Abrir oportunidad
		if (bmoProject.getOpportunityId().toInteger() > 0) {
			addActionLabel("Ir a Oportunidad", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showOpportunity();
				}
			});
		}

		// Preparar panel del este
		//showProjectWestPanel();

		// Panel default inicial
		showStart();
	}

	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoProject.getCustomerId().toInteger());
			uiCustomerDetail.show();
		} else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		}
	}

	@Override
	public void close() {
		UiProject uiProjectList = new UiProject(getUiParams());
		setUiType(UiParams.MASTER);
		uiProjectList.show();
	}

	private void reset() {
		UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProject.getId());
		uiProjectDetail.show();
	}

	private void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProjectStart uiProjectStart = new UiProjectStart(getUiParams(), bmObjectProgramId, bmoProject);
		uiProjectStart.show();
	}

	private void showProjectForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProject uiProjectForm = new UiProject(getUiParams());
		uiProjectForm.edit(bmoProject);
	}
	
	private void showProjectGuidelines(BmoProjectGuideline bmoProjectGuideline) {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProjectGuidelinesForm uiProjectGuidelinesForm = new UiProjectGuidelinesForm(getUiParams(), bmoProjectGuideline.getId(), bmoProject);
		setUiType(new BmoProjectGuideline().getProgramCode(), UiParams.SLAVE);
		uiProjectGuidelinesForm.show();
	}
	
	private void showProjectDetail(BmoProjectDetail bmoProjectDetail) {
		getUiParams().getUiTemplate().hideEastPanel();
		UiProjectDetailForm uiProjectDetailForm = new UiProjectDetailForm(getUiParams(), bmoProjectDetail.getId(), bmoProject);
		setUiType(new BmoProjectDetail().getProgramCode(), UiParams.SLAVE);
		uiProjectDetailForm.show();
	}
	
	private void showOpportunity() {
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoProject.getOpportunityId().toInteger());
		uiOpportunityDetail.show();
	}

	private void showOrder() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoProject.getOrderId().toInteger());
		setUiType(new BmoOrder().getProgramCode(), UiParams.SINGLESLAVE);
		uiOrder.show();
	}
	
	private void showOrderDelivery() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderDelivery uiOrderDeliveryList = new UiOrderDelivery(getUiParams(), bmoProject);
		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getOrderId().getName(), bmoProject.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoOrderDelivery.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoOrderDelivery().getProgramCode(), UiParams.SLAVE);
		uiOrderDeliveryList.show();
	}

	private void showRequisition() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOrder);
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId().getName(), bmoProject.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
		uiRequisitionList.show();
	}
	
	public void showOrderExtra() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderList uiOrderList = new UiOrderList(getUiParams(),bmoOrder);
		//BmoOrder bmoOrder = new BmoOrder();
		
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrder.getKind(), bmoOrder.getOriginRenewOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoOrder.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoOrder().getProgramCode(), UiParams.SLAVE);
		uiOrderList.show();
	}

	private void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoProject.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
		uiRaccountList.show();
	}
	
	

	private void showCustomerService() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomerService uiCustomerServiceList = new UiCustomerService(getUiParams());
		BmoCustomerService bmoCustomerService = new BmoCustomerService();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoCustomerService.getKind(), bmoCustomerService.getOrderId().getName(), bmoProject.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoCustomerService.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoCustomerService().getProgramCode(), UiParams.SLAVE);
		uiCustomerServiceList.show();
	}
	
	public void addExtraOrder() {
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addExtraOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();

				if (!result.hasErrors()) {						
					showSystemMessage("Se creo el pedido Extra");
					UiOrderDetail uiOrderForm = new UiOrderDetail(getUiParams(),((BmoOrder)result.getBmObject()).getId());
					uiOrderForm.show();		
				} 
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
						BmoOrder.ACTION_EXTRAORDER,"",callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-addExtraOrder() ERROR: " + e.toString());
		}
	}
	
	

	private void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();

		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoProject.getBmoWFlow(), wFlowStepAction);

		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoProject.getWFlowId().toInteger(), 
				bmoProject.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

//		private void showProjectWestPanel() {
	//		// Datos del cliente
	//		FlowPanel customerVP = new FlowPanel();
	//		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
	//		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
	//		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");
	//
	//		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoProject.getCustomerId().toInteger(), bmoProject.getId());
	//		uiCustomerView.show();
	//
	//		discPanel.add(customerVP);
	//		discPanel.setOpen(false);
	//		detailLabelTable.addDetailPanel(discPanel);
	//
	//		// Datos del lugar
//			if (bmoProject.getVenueId().toInteger() > 0) {
//				FlowPanel venueVP = new FlowPanel();
//				venueVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
//	
//				UiVenueView uiVenueView = new UiVenueView(getUiParams(), venueVP, bmoProject.getVenueId().toInteger());
//				uiVenueView.show();
//	
//				DisclosurePanel venueDiscP = new DisclosurePanel("Datos Lugar");
//				venueDiscP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
//				venueDiscP.add(venueVP);
//				detailLabelTable.addDetailPanel(venueDiscP);
//			}
//		}

	// Accion de una tarea
	public class SFComponentWFlowStepAction implements IWFlowStepAction {

		@Override
		public void action() {
			reset();
		}
	}
	
	// Variables para llamadas RPC
	public int getBmoOrderRpcAttempt() {
		return bmoOrderRpcAttempt;
	}

	public void setBmoOrderRpcAttempt(int bmoOrderRpcAttempt) {
		this.bmoOrderRpcAttempt = bmoOrderRpcAttempt;
	}

	public int getBmoProjectDetailRpcAttempt() {
		return bmoProjectDetailRpcAttempt;
	}

	public void setBmoProjectDetailRpcAttempt(int bmoProjectDetailRpcAttempt) {
		this.bmoProjectDetailRpcAttempt = bmoProjectDetailRpcAttempt;
	}

	public int getBmoProjectGuidelinesRpcAttempt() {
		return bmoProjectGuidelinesRpcAttempt;
	}

	public void setBmoProjectGuidelinesRpcAttempt(int bmoProjectGuidelinesRpcAttempt) {
		this.bmoProjectGuidelinesRpcAttempt = bmoProjectGuidelinesRpcAttempt;
	}

	public int getBmoCustomerAddressRpcAttempt() {
		return bmoCustomerAddressRpcAttempt;
	}

	public void setBmoCustomerAddressRpcAttempt(int bmoCustomerAddressRpcAttempt) {
		this.bmoCustomerAddressRpcAttempt = bmoCustomerAddressRpcAttempt;
	}

}
