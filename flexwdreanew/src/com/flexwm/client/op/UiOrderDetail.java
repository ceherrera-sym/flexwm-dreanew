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

import com.flexwm.client.ac.UiOrderFormSession;
import com.flexwm.client.ar.UiPropertyRentalDetail;
import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerDetail;
import com.flexwm.client.cm.UiCustomerView;
import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.client.cm.UiProjectDetail;
//import com.flexwm.client.cm.UiProjectStepDetail;
import com.flexwm.client.op.UiOrderDetailForm;
import com.flexwm.client.dash.UiOrderSaleDash;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiCustomerService;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
//import com.flexwm.shared.cm.BmoProjectStep;
import com.flexwm.shared.op.BmoOrderDetail;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoCustomerService;
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
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiOrderDetail extends UiDetail {
	protected BmoOrder bmoOrder;
	private BmoOrderType bmoOrderTypeDefault = new BmoOrderType();
	BmoOrderDetail bmoOrderDetail = new BmoOrderDetail();
	BmoMarket bmoMarket = new BmoMarket();
	BmoProject bmoProject = new BmoProject();
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
	private int bmoMarketRpcAttempt = 0;
	private int bmoOrderDetailRpcAttempt = 0;
	private int bmoPropertyRentalDetailRpcAttempt = 0;
	private int bmoProjectDetailRpcAttempt  = 0;
	private int showFormWhenOrderTypeRpcAttempt = 0;
	private int  defaultOrderTypeIdRpc = 0;
	private int effectRpcAttempt = 0;

	public UiOrderDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoOrder(), id);
		bmoOrder = (BmoOrder)getBmObject();		
	}

	@Override
	public void populateLabels() {
		bmoOrder = (BmoOrder)getBmObject();
		addDetailImage(bmoOrder.getBmoCustomer().getLogo());
		if (isMobile())
			addTitleLabel(bmoOrder.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoOrder) 
					+ ": " + bmoOrder.getCode());
		addLabel(bmoOrder.getName());
		addLabel(bmoOrder.getBmoOrderType().getName());	
		addLabel(bmoOrder.getBmoWFlow().getBmoWFlowType().getName());
		addLabel(bmoOrder.getBmoWFlow().getBmoWFlowPhase().getName());
		addLabel(bmoOrder.getLockStart());
		addLabel(bmoOrder.getBmoWFlow().getProgress());

		if(bmoOrder.getMarketId().toInteger() > 0) 
			getBmoMarket();
		else
			getInfo();

	}
	public void getInfo(){
		if(!(bmoOrder.getMarketId().toInteger() > 0)) 
			addLabel(bmoOrder.getMarketId());
		addLabel(bmoOrder.getAmount());
		addLabel(bmoOrder.getStatus());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoOrder.getTags());
		addDetailPanel(tagPanel);	

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoOrder.getBmoCustomer()) 
				+ ": " + bmoOrder.getBmoCustomer().getCode());
		addLabel(bmoOrder.getBmoCustomer().getDisplayName());
		addLabel(bmoOrder.getBmoCustomer().getPhone());
		addLabel(bmoOrder.getBmoCustomer().getEmail());
		addEmptyLabel();

		// Inicio
		if (bmoOrder.getBmoOrderType().getType().toChar() != BmoOrderType.TYPE_LEASE) {
			addActionLabel("Inicio", bmoOrder.getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					reset();
				}
			});
		}

		// Editar Pedido
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrderForm();
			}
		});

		// Detalle pedido
		addActionLabel(new BmoOrderDetail(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getBmoOrderDetail();
			}
		});

		// Editar Cliente
		if (bmoOrder.getBmoOrderType().getType().toChar() != BmoOrderType.TYPE_LEASE) {
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().getUiTemplate().hideEastPanel();
					UiCustomer uiCustomer = new UiCustomer(getUiParams());
					setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
					uiCustomer.edit(bmoOrder.getBmoCustomer());
				}
			});
		}

		// Tareas
		if (bmoOrder.getBmoOrderType().getType().toChar() != BmoOrderType.TYPE_LEASE) {
			addActionLabel(new BmoWFlowStep(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showSteps();
				}
			});
		}

		// Cuentas x Cobrar
		addActionLabel(new BmoRaccount(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showRaccount();
			}
		});

		if (bmoOrder.getBmoOrderType().getType().toChar() != BmoOrderType.TYPE_LEASE) {
			// Ordenes de compra
			addActionLabel(new BmoRequisition(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showRequisition();
				}
			});
		}

		// Envio Pedido
		if (!(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE)) {
			addActionLabel(new BmoOrderDelivery(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showOrderDelivery();
				}
			});
		}
		// Quejas de los pedidos
		addActionLabel(new BmoCustomerService(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCustomerService();
			}
		});

		// Bitacora de flujo
		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
			addActionLabel(new BmoWFlowLog(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showWFlowLog();
				}
			});
		}

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
			if(getUiParams().getCallerProgramCode() != new BmoPropertyRental().getProgramCode())
			{
				addActionLabel("Ir a Listado", "close", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						getUiParams().setCallerProgramCode(new BmoOrder().getProgramCode());;
						close();
					}
				});
			}
		}

		// Abrir oportunidad
		if (bmoOrder.getOpportunityId().toInteger() > 0) {
			addActionLabel("Ir a Oportunidad", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showOpportunity();
				}
			});
		}
		//Proyecto vis
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();		
//		if(getUiParams().getSFParams().hasRead(bmoProjectStep.getProgramCode())) {
//			if(bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
//				if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//					addActionLabel("Ir al Proyecto",new BmoProjectStep().getProgramCode(), new ClickHandler() {				
//						@Override
//						public void onClick(ClickEvent event) {
//							confirmOpenEffect();					
//						}
//					});
//				}
//		}
//		if()

		// Preparar panel del este
		//showOrderWestPanel();
		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
			showOrderForm();
		} else {
			// Panel default inicial
			showStart();
		}
	}

	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoOrder.getCustomerId().toInteger());
			uiCustomerDetail.show();
		}
		else if(getUiParams().getCallerProgramCode() == new BmoProject().getProgramCode())
		{
			getBmoProjectDetail();
		}
		else if(getUiParams().getCallerProgramCode() == new BmoPropertyRental().getProgramCode())
		{
			getBmoPropertyRentalDetail();
		}
		else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());

		}
	}

	@Override
	public void close() {
		UiOrderList uiOrderList = new UiOrderList(getUiParams());
		setUiType(UiParams.MASTER);
		uiOrderList.show();
	}

	public void returnToDashboard() {
		UiOrderSaleDash uiOrderSaleDash = new UiOrderSaleDash(getUiParams());
		uiOrderSaleDash.show();
	}

	public void reset() {
		UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), bmoOrder.getId());
		uiOrderDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();		
		UiOrderStart uiOrderStart = new UiOrderStart(getUiParams(), bmObjectProgramId, bmoOrder);
		uiOrderStart.show();
	}

	public void showOrderForm() {
		showFormWhenOrderType(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toInteger());
		/*getUiParams().getUiTemplate().hideEastPanel();
		UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), bmoOrder.getId());
		setUiType(UiParams.DETAILFORM);
		uiOrderForm.show();*/
	}

	private void showOpportunity() {
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOrder.getOpportunityId().toInteger());
		uiOpportunityDetail.show();
	}

	public void showRequisition() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOrder);
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
		uiRequisitionList.show();
	}

	public void showOrder() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderList uiOrderList = new UiOrderList(getUiParams());
		//BmoOrder bmoOrder = new BmoOrder();

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrder.getKind(), bmoOrder.getOriginRenewOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoOrder.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoOrder().getProgramCode(), UiParams.SLAVE);
		uiOrderList.show();
	}

	public void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoOrder.getId());
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

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();

		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoOrder.getBmoWFlow(), wFlowStepAction);

		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoOrder.getWFlowId().toInteger(), 
				bmoOrder.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

	public void showWFlowLog() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), bmoOrder.getWFlowId().toInteger());
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId().getName(), bmoOrder.getWFlowId().toInteger());
		getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoWFlowLog().getProgramCode(), UiParams.SLAVE);
		uiWFlowLog.show();
	}

	public void showOrderWestPanel() {
		// Datos del cliente
		FlowPanel customerVP = new FlowPanel();
		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");

		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoOrder.getCustomerId().toInteger(), bmoOrder.getId());
		uiCustomerView.show();

		discPanel.add(customerVP);
		discPanel.setOpen(false);
		detailLabelTable.addDetailPanel(discPanel);
	}

	// Accion de una tarea
	public class SFComponentWFlowStepAction implements IWFlowStepAction {
		@Override
		public void action() {
			reset();
		}
	}

	// Obtiene el tipo de pedido en el modulo de configuración, primer intento
	public void showFormWhenOrderType(int defaultOrderTypeIdRpc) {
		showFormWhenOrderType(defaultOrderTypeIdRpc, 0);
	}
	// Obtiene el tipo de pedido en el modulo de configuración
	public void showFormWhenOrderType(int defaultOrderTypeIdRpc, int showFormWhenOrderTypeRpcAttempt) {
		if (showFormWhenOrderTypeRpcAttempt < 5) {
			setDefaultOrderTypeIdRpc(defaultOrderTypeIdRpc);
			setShowFormWhenOrderTypeRpcAttempt(showFormWhenOrderTypeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					if (getShowFormWhenOrderTypeRpcAttempt() < 5)
						showFormWhenOrderType(getDefaultOrderTypeIdRpc(), getShowFormWhenOrderTypeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-showFormWhenOrderType() ERROR: " + caught.toString());
				}

				public void onSuccess(BmObject result) {
					setShowFormWhenOrderTypeRpcAttempt(0);
					setBmObject((BmObject)result);
					bmoOrderTypeDefault = (BmoOrderType)getBmObject();
					//Si el pedido por default no es session mostrar el detalle
					if (bmoOrderTypeDefault == null) {
						getUiParams().getUiTemplate().hideEastPanel();
						UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), bmoOrder.getId());
						setUiType(UiParams.DETAILFORM);
						uiOrderForm.show();
					} else {
						if (bmoOrderTypeDefault.getType().equals(BmoOrderType.TYPE_SESSION)) {
							getUiParams().getUiTemplate().hideEastPanel();
							UiOrderFormSession uiOrderFormSession = new UiOrderFormSession(getUiParams(), bmoOrder.getId());
							setUiType(new BmoOrder().getProgramCode(), UiParams.DETAILFORM);
							uiOrderFormSession.show();			
						} else {
							getUiParams().getUiTemplate().hideEastPanel();						
							UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), bmoOrder.getId());
							setUiType(UiParams.DETAILFORM);
							uiOrderForm.show();
						}
					}	
				}
			};

			// Llamada al servicio RPC
			try {
				getUiParams().getBmObjectServiceAsync().get(bmoOrderTypeDefault.getPmClass(), defaultOrderTypeIdRpc, callback);
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-showFormWhenOrderType() ERROR: " + e.toString());
			}
		}
	}

	// Primer intento
	public void getBmoProjectDetail() {
		getBmoProjectDetail(0);
	}

	public void getBmoProjectDetail(int bmoProjectDetailRpcAttempt) {
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
						else showErrorMessage(this.getClass().getName() + "-getBmoProject ERROR: " + caught.toString());
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoProjectDetailRpcAttempt(0);
					bmoProject= ((BmoProject)result);
					UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProject.getId());
					uiProjectDetail.show();
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoProject.getPmClass(), bmoOrder.getOriginRenewOrderId().toInteger(), bmoProject.getOrderId().getName(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoProjectGuidelines() ERROR: " + e.toString());
			}
		}
	}
	// Primer inteto
	public void getBmoPropertyRentalDetail() {
		getBmoPropertyRentalDetail(0);
	}
	public void getBmoPropertyRentalDetail(int bmoPropertyRentalDetailRpcAttempt) {
		if (bmoPropertyRentalDetailRpcAttempt < 5) {
			setBmoPropertyRentalDetailRpcAttempt(bmoPropertyRentalDetailRpcAttempt + 1);
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoPropertyRentalDetailRpcAttempt() < 5)
						getBmoPropertyRentalDetail(getBmoPropertyRentalDetailRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoPropertyRentalDetail ERROR: " + caught.toString());
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoPropertyRentalDetailRpcAttempt(0);
					bmoPropertyRental= ((BmoPropertyRental)result);
					UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), bmoPropertyRental.getId());
					uiPropertyRentalDetail.show();
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoPropertyRental.getPmClass(), bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoPropertyRentalDetail() ERROR: " + e.toString());
			}
		}
	}

	public void addExtraOrder() {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-creatExtraOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();

				if (!result.hasErrors()) {	

					showSystemMessage("Se creo el pedido Extra");
					UiOrderDetail uiOrderForm = new UiOrderDetail(getUiParams(),((BmoOrder)result.getBmObject()).getId());
					uiOrderForm.show();

					//					showSystemMessage("Pedido Registrado!");
					//					getUiParams().getUiTemplate().hideEastPanel();
					//					UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoProject.getOrderId().toInteger());
					//					setUiType(new BmoOrder().getProgramCode(), UiParams.SINGLESLAVE);
					//					uiOrder.show();

					//paccountXMLDialogBox.hide();
					//list();
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
			showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e.toString());
		}
	}
	// Primer intento
	public void getBmoOrderDetail() {
		getBmoOrderDetail(0);
	}

	public void getBmoOrderDetail(int bmoOrderDetailRpcAttempt) {
		if (bmoOrderDetailRpcAttempt < 5) {
			setBmoOrderDetailRpcAttempt(bmoOrderDetailRpcAttempt + 1);
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoOrderDetailRpcAttempt() < 5)
						getBmoOrderDetail(getBmoOrderDetailRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoOrderDetail() ERROR: " + caught.toString());
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoOrderDetailRpcAttempt(0);
					bmoOrderDetail = ((BmoOrderDetail)result);
					showOrderDetail(bmoOrderDetail);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoOrderDetail.getPmClass(), bmoOrder.getId(), bmoOrderDetail.getOrderId().getName(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOrderDetail() ERROR: " + e.toString());
			}
		}
	}
	// Primer intento
	public void getBmoMarket() {
		getBmoMarket(0);
	}

	public void getBmoMarket(int bmoMarketRpcAttempt) {
		if (bmoMarketRpcAttempt < 5) {
			setBmoMarketRpcAttempt(bmoMarketRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoMarketRpcAttempt() < 5)
						getBmoMarket(getBmoMarketRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoMarket() ERROR: " + caught.toString());
						getInfo();
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoMarketRpcAttempt(0);
					bmoMarket = (BmoMarket )result;
					addLabel(bmoMarket.getName());				
					getInfo();
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoMarket.getPmClass(), bmoOrder.getMarketId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getbmoMarket ERROR: " + e.toString());
			}
		}
	}
//	public void confirmOpenEffect() {
//
//		// Es de tipo Renta
//		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//			getEffect();
//		}
//		// Es de tipo Venta 
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
//			getEffect();
//		}	
//		// Es de tipo Consultoria 
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
//			getEffect();
//		}	
//		// Es de tipo Inmueble
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
//			getEffect();
//		}
//	}
//	public void getEffect() {
//		getEffect(0);
//	}
//	public void getEffect(int effectRpcAttempt) {
//		if (effectRpcAttempt < 5) {
//			setEffectRpcAttempt(effectRpcAttempt + 1);
//
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getEffectRpcAttempt() < 5)
//						getEffect(getEffectRpcAttempt());
//					else {
//						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
//						else showErrorMessage(this.getClass().getName() + "-getEffect() ERROR: " + caught.toString());
//					}
//				}
//
//				@Override
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					setEffectRpcAttempt(0);			
//					if(result.hasErrors())
//						showSystemMessage(result.errorsToString());
//					else
//						openOpportunityEffect(result);
//					
//						
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOpportunity.ACTION_GETEFFECT, "" + bmoOrder.getId(), callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
//			}
//		}
//	}
//	protected void openOpportunityEffect(BmUpdateResult result) {
//		// Es de tipo Renta - abre Proyecto
//		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), result.getBmObject().getId());			
//			uiProjectStepDetail.show();
//		}
//		// Es de tipo Venta - abre Pedido
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), result.getBmObject().getId());			
//			uiProjectStepDetail.show();
//		}
//		// Es de tipo Consultoria - abre Pedido
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), result.getBmObject().getId());			
//			uiProjectStepDetail.show();
//		}
//		// Es de tipo Inmueble - abre Venta de Inmueble
//		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), result.getBmObject().getId());			
//			uiProjectStepDetail.show();
//		} 
//		else showSystemMessage("No fue encontrado el Efecto.");
//	}

	private void showOrderDetail(BmoOrderDetail bmoOrderDetail) {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderDetailForm uiOrderDetailForm = new UiOrderDetailForm(getUiParams(), bmoOrderDetail.getId(), bmoOrder);
		setUiType(new BmoOrderDetail().getProgramCode(), UiParams.SLAVE);
		uiOrderDetailForm.show();
	}

	// Variables para llamadas RPC
	public int getEffectRpcAttempt() {
		return effectRpcAttempt;
	}

	public void setEffectRpcAttempt(int effectRpcAttempt) {
		this.effectRpcAttempt = effectRpcAttempt;
	}
	public int getBmoMarketRpcAttempt() {
		return bmoMarketRpcAttempt;
	}

	public void setBmoMarketRpcAttempt(int bmoMarketRpcAttempt) {
		this.bmoMarketRpcAttempt = bmoMarketRpcAttempt;
	}

	public int getBmoOrderDetailRpcAttempt() {
		return bmoOrderDetailRpcAttempt;
	}

	public void setBmoOrderDetailRpcAttempt(int bmoOrderDetailRpcAttempt) {
		this.bmoOrderDetailRpcAttempt = bmoOrderDetailRpcAttempt;
	}

	public int getBmoPropertyRentalDetailRpcAttempt() {
		return bmoPropertyRentalDetailRpcAttempt;
	}

	public void setBmoPropertyRentalDetailRpcAttempt(int bmoPropertyRentalDetailRpcAttempt) {
		this.bmoPropertyRentalDetailRpcAttempt = bmoPropertyRentalDetailRpcAttempt;
	}

	public int getBmoProjectDetailRpcAttempt() {
		return bmoProjectDetailRpcAttempt;
	}

	public void setBmoProjectDetailRpcAttempt(int bmoProjectDetailRpcAttempt) {
		this.bmoProjectDetailRpcAttempt = bmoProjectDetailRpcAttempt;
	}

	public int getShowFormWhenOrderTypeRpcAttempt() {
		return showFormWhenOrderTypeRpcAttempt;
	}

	public void setShowFormWhenOrderTypeRpcAttempt(int showFormWhenOrderTypeRpcAttempt) {
		this.showFormWhenOrderTypeRpcAttempt = showFormWhenOrderTypeRpcAttempt;
	}

	public int getDefaultOrderTypeIdRpc() {
		return defaultOrderTypeIdRpc;
	}

	public void setDefaultOrderTypeIdRpc(int defaultOrderTypeIdRpc) {
		this.defaultOrderTypeIdRpc = defaultOrderTypeIdRpc;
	}

}
