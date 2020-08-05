/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerView;
import com.flexwm.client.ac.UiOrderFormSession;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSessionReview;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
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
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiSessionSaleDetail extends UiDetail {

	protected BmoSessionSale bmoSessionSale;
	BmoOrder bmoOrder = new BmoOrder();

	public UiSessionSaleDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoSessionSale(), id);
		bmoSessionSale = (BmoSessionSale)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoSessionSale = (BmoSessionSale)getBmObject();
		addDetailImage(bmoSessionSale.getBmoCustomer().getLogo());
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoSessionSale) 
				+ ": " + bmoSessionSale.getCode());
		addLabel(bmoSessionSale.getCode());
		addLabel(bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getName());
		addLabel(bmoSessionSale.getBmoSessionTypePackage().getName());
		addLabel(bmoSessionSale.getStartDate());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoSessionSale.getTags());
		addDetailPanel(tagPanel);

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoSessionSale.getBmoCustomer()) 
				+ ": " + bmoSessionSale.getBmoCustomer().getCode());
		addLabel(bmoSessionSale.getBmoCustomer().getDisplayName());
		addLabel(bmoSessionSale.getBmoCustomer().getPhone());
		addLabel(bmoSessionSale.getBmoCustomer().getEmail());

		// Obten info del pedido
		getBmoOrder();

	}

	public void getBmoOrder() {


		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
				setLinks(new BmoOrder());
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();
				setLinks((BmoOrder)result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoSessionSale.getOrderId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	public void setLinks(BmoOrder bmoOrder) {

		this.bmoOrder = bmoOrder;

		addActionLabel("Inicio", bmoSessionSale.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar oportunidad
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSessionSaleForm();
			}
		});

		addActionLabel("Cliente", "cust", new ClickHandler() {
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

		// Pedido proyecto
		addActionLabel(new BmoOrder(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrderFormSession();
			}
		});

		// Cuentas x Cobrar
		addActionLabel(new BmoRaccount(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showRaccount();
			}
		});

		// Evaluaciones
		addActionLabel(new BmoSessionReview(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSessionReview();
			}
		});

		// Selector calendario
		/*addActionLabel("Selector Sesiones", new BmoSession().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSessionSelector();
			}
		});*/

		//		// Sesiones pedido
		//		westTable.addActionLabel("Lista Sesiones", new BmoOrderSession().getProgramCode(), new ClickHandler() {
		//			public void onClick(ClickEvent event) {
		//				showOrderSession();
		//			}
		//		});

		// Calendario sesiones
		addActionLabel("Cal. Sesiones", new BmoOrderSession().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrderSessionCalendar();
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
			addActionLabel("Regresar", "start", new ClickHandler() {
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

		// Preparar panel del este
		showSessionSaleWestPanel();

		// Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiSessionSale uiSessionSaleList = new UiSessionSale(getUiParams());
		setUiType(UiParams.MASTER);
		uiSessionSaleList.show();
	}

	public void reset() {
		UiSessionSaleDetail uiSessionSaleDetail = new UiSessionSaleDetail(getUiParams(), bmoSessionSale.getId());
		uiSessionSaleDetail.show();
	}

	public void showStart() {
		//showOrderFormSession();
		UiSessionSaleStart uiSessionSaleStart = new UiSessionSaleStart(getUiParams(), bmObjectProgramId, bmoSessionSale);
		uiSessionSaleStart.show();
	}

	public void showSessionSaleForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiSessionSale uiSessionSaleForm = new UiSessionSale(getUiParams());
		setUiType(new BmoSessionSale().getProgramCode(), UiParams.SLAVE);
		uiSessionSaleForm.edit(bmoSessionSale);
	}

	public void showClient() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomer uiCustomer = new UiCustomer(getUiParams());
		setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
		uiCustomer.edit(bmoSessionSale.getBmoCustomer());
	}

	public void showOrderFormSession() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderFormSession uiOrderFormSession = new UiOrderFormSession(getUiParams(), bmoOrder.getId());
		setUiType(new BmoOrder().getProgramCode(), UiParams.DETAILFORM);
		uiOrderFormSession.show();
	}

	public void showSessionReview() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiSessionReview uiSessionReviewList = new UiSessionReview(getUiParams(), bmoSessionSale);
		BmoSessionReview bmoSessionReview = new BmoSessionReview();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoSessionReview.getKind(), bmoSessionReview.getSessionSaleId().getName(), bmoSessionSale.getId());
		getUiParams().getUiProgramParams(bmoSessionReview.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoSessionReview().getProgramCode(), UiParams.SLAVE);
		uiSessionReviewList.show();
	}

	public void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoSessionSale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
		uiRaccountList.show();
	}

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoSessionSale.getBmoWFlow(), new SessionSaleWFlowStepAction());

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoSessionSale.getWFlowId().toInteger(), 
				bmoSessionSale.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);

		uiWFlowStepList.show();
	}

	// Sesiones del Pedido
	public void showOrderSession() {		
		UiOrderFormSession uiOrderFormSession = new UiOrderFormSession(getUiParams(), bmoSessionSale.getOrderId().toInteger());
		setUiType(UiParams.DETAILFORM);
		uiOrderFormSession.show();
		
		/*UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), bmoOrder.getId());
		setUiType(UiParams.DETAILFORM);
		uiOrderForm.show();*/
	}

	// Calendario de Sesiones del Pedido
	public void showOrderSessionCalendar() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoOrderSession bmoOrderSession = new BmoOrderSession();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getOrderId().getName(), bmoSessionSale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).setForceFilter(bmFilter);

		setUiType(bmoOrderSession.getProgramCode(), UiParams.SLAVE);

		UiOrderSessionCalendar uiOrderSessionCalendar = new UiOrderSessionCalendar(getUiParams());
		uiOrderSessionCalendar.show();
	}

	// Calendario selector de Sesiones
	public void showSessionSelector() {
		//		hideEastPanel();
		//		BmoSession bmoSession = new BmoSession();
		//		
		//		BmoOrderSession bmoOrderSession = new BmoOrderSession();
		//		BmFilter bmFilter = new BmFilter();
		//		bmFilter.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getOrderId().getName(), bmoSessionSale.getOrderId().toInteger());
		//		getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).setForceFilter(bmFilter);
		//		
		//		setUiType(bmoSession.getProgramCode(), UiParams.SLAVE);
		//		
		//		UiSessionSelectorCalendar uiSessionSelectorCalendar = new UiSessionSelectorCalendar(getUiParams(), bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger());
		//		uiSessionSelectorCalendar.show();

		getUiParams().getUiTemplate().hideEastPanel();
		UiSessionSaleSelector uiSessionSaleSelector = new UiSessionSaleSelector(getUiParams(), bmObjectProgramId, bmoSessionSale);
		uiSessionSaleSelector.show();
	}

	// Mostrar datos panel oeste
	public void showSessionSaleWestPanel() {
		// Datos del cliente
		FlowPanel customerVP = new FlowPanel();
		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");

		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoSessionSale.getCustomerId().toInteger(), bmoSessionSale.getId());
		uiCustomerView.show();

		discPanel.add(customerVP);
		discPanel.setOpen(false);
		detailLabelTable.addDetailPanel(discPanel);
	}

	// Accion de una tarea
	public class SessionSaleWFlowStepAction implements IWFlowStepAction {

		@Override
		public void action() {
			reset();
		}
	}
}
