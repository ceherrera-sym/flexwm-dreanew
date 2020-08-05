/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ar;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerDetail;
import com.flexwm.client.co.UiProperty;
import com.flexwm.client.op.UiOrderList;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.co.BmoProperty;
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


public class UiPropertyRentalDetail extends UiDetail {
	BmoPropertyRental bmoPropertyRental;
	BmoProperty bmoProperty;

	BmoOrder bmoOrder = new BmoOrder();
	//BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
	BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();

	public UiPropertyRentalDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoPropertyRental(), id);
		bmoPropertyRental = (BmoPropertyRental)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoPropertyRental = (BmoPropertyRental)getBmObject();
		this.bmoProperty =  bmoPropertyRental.getBmoProperty();
		
		addDetailImage(bmoPropertyRental.getBmoCustomer().getLogo());
		if (isMobile())
			addTitleLabel(" " + bmoPropertyRental.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoPropertyRental) 
					+ ": " + bmoPropertyRental.getCode());
		addLabel(bmoPropertyRental.getName());
		addLabel(bmoPropertyRental.getBmoProperty().getCode());
		addLabel(bmoPropertyRental.getStartDate());
		addLabel(bmoPropertyRental.getEndDate());
		addLabel(bmoPropertyRental.getCurrentIncome());
		addLabel(bmoPropertyRental.getRentIncrease());
		addLabel(bmoPropertyRental.getStatus());		

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoPropertyRental.getTags());
		addDetailPanel(tagPanel);

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoPropertyRental.getBmoCustomer()) 
				+ ": " + bmoPropertyRental.getBmoCustomer().getCode());
		addLabel(bmoPropertyRental.getBmoCustomer().getDisplayName());
		addLabel(bmoPropertyRental.getBmoCustomer().getPhone());
		addLabel(bmoPropertyRental.getBmoCustomer().getEmail());
		addEmptyLabel();


		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoPropertyRental.getBmoUser()) 
				+ ": " + bmoPropertyRental.getBmoUser().getCode());
		addLabel(bmoPropertyRental.getBmoUser().getEmail());
		addLabel(bmoPropertyRental.getBmoCustomer().getPhone());
		
		// Obten info del pedido
		getBmoOrder();
	}

	public void getBmoOrder() {

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + caught.toString());
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
				getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoPropertyRental.getOrderId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
		}
	}

	public void setLinks(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;

		addActionLabel("Inicio", bmoPropertyRental.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar Contrato
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showPropertyRentalForm();
			}
		});


		// Cliente
		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiParams().getUiTemplate().hideEastPanel();
				UiCustomer uiCustomer = new UiCustomer(getUiParams());
				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
				uiCustomer.edit(bmoPropertyRental.getBmoCustomer());
			}
		});


		// Tareas
		//		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				showSteps();
		//			}
		//		});

		// Pedido Contrato
		//		addActionLabel(new BmoOrder(), new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				showOrder();
		//			}
		//		});



		// Cuentas x Cobrar
		//		addActionLabel(new BmoRaccount(), new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				showRaccount();
		//			}
		//		});

		// Ordenes de compra
		addActionLabel(new BmoRequisition(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showRequisition(bmoProperty);
			}
		});
		
		// Inmueble
		addActionLabel(getSFParams().getProgramTitle(new BmoProperty().getProgramCode()), new BmoProperty().getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showProperty();
			}
		});

		// Pedidos Extras
		addActionLabel("Contratos Renovados",new BmoOrder().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrders();
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

		// Panel default inicial
		showStart();
	}

	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoPropertyRental.getCustomerId().toInteger());
			uiCustomerDetail.show();
		} else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		}
	}

	@Override
	public void close() {
		UiPropertyRental uiPropertyRental = new UiPropertyRental(getUiParams());
		setUiType(UiParams.MASTER);
		uiPropertyRental.show();
	}

	private void reset() {
		UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), bmoPropertyRental.getId());
		uiPropertyRentalDetail.show();
	}

	private void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertyRentalStart uiPropertyRentalStart = new UiPropertyRentalStart(getUiParams(), bmObjectProgramId, bmoPropertyRental);
		uiPropertyRentalStart.show();
	}

	private void showPropertyRentalForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiPropertyRental uiPropertyRentalForm = new UiPropertyRental(getUiParams());
		uiPropertyRentalForm.edit(bmoPropertyRental);
	}

	public void showOrders() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderList uiOrderList = new UiOrderList(getUiParams(),bmoOrder);
		//BmoOrder bmoOrder = new BmoOrder();

		BmFilter bmFilterPropertyRent = new BmFilter();
		bmFilterPropertyRent.setInFilter(bmoPropertyRental.getKind(),
										 bmoOrder.getOriginRenewOrderId().getName(), 
										 bmoPropertyRental.getOrderId().getName(),
										 bmoPropertyRental.getIdFieldName(),
										  ""+bmoPropertyRental.getId());
		
		//bmFilter.setValueFilter(bmoOrder.getKind(), bmoOrder.getOriginRenewOrderId().getName(), bmoOrder.getId());
		getUiParams().getUiProgramParams(bmoOrder.getProgramCode()).setForceFilter(bmFilterPropertyRent);
		setUiType(new BmoOrder().getProgramCode(), UiParams.SLAVE);
		uiOrderList.show();
	}

	//	private void showProjectGuidelines(BmoProjectGuideline bmoProjectGuideline) {
	//		getUiParams().getUiTemplate().hideEastPanel();
	//		UiProjectGuidelinesForm uiProjectGuidelinesForm = new UiProjectGuidelinesForm(getUiParams(), bmoProjectGuideline.getId(), bmoProject);
	//		setUiType(new BmoProjectGuideline().getProgramCode(), UiParams.SLAVE);
	//		uiProjectGuidelinesForm.show();
	//	}

	//	private void showProjectDetail(BmoProjectDetail bmoProjectDetail) {
	//		getUiParams().getUiTemplate().hideEastPanel();
	//		UiProjectDetailForm uiProjectDetailForm = new UiProjectDetailForm(getUiParams(), bmoProjectDetail.getId(), bmoProject);
	//		setUiType(new BmoProjectDetail().getProgramCode(), UiParams.SLAVE);
	//		uiProjectDetailForm.show();
	//	}
	//	
	//
	//	private void showOrder() {
	//		getUiParams().getUiTemplate().hideEastPanel();
	//		UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoPropertyRental.getOrderId().toInteger());
	//		setUiType(new BmoOrder().getProgramCode(), UiParams.SINGLESLAVE);
	//		uiOrder.show();
	//	}
	//	
	//
		private void showRequisition(BmoProperty bmoProperty) {
			getUiParams().getUiTemplate().hideEastPanel();
			UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOrder, bmoProperty);
			BmoRequisition bmoRequisition = new BmoRequisition();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getPropertyId().getName(), bmoPropertyRental.getPropertyId().toInteger());
			getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
			setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
			uiRequisitionList.show();
		}
		
		public void showProperty() {
			getUiParams().getUiTemplate().hideEastPanel();
			UiProperty uiPropertyForm = new UiProperty(getUiParams());
			setUiType(new BmoProperty().getProgramCode(), UiParams.SINGLESLAVE);
			uiPropertyForm.edit(bmoPropertyRental.getBmoProperty());
		}

	//	
//		private void showRaccount() {
//			getUiParams().getUiTemplate().hideEastPanel();
//			UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
//			BmoRaccount bmoRaccount = new BmoRaccount();
//			BmFilter bmFilter = new BmFilter();
//			bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoPropertyRental.getOrderId().toInteger());
//			getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
//			setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
//			uiRaccountList.show();
//		}
	//		
	//	private void showSteps() {
	//		getUiParams().getUiTemplate().hideEastPanel();
	//
	//		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();
	//
	//		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoPropertyRental.getBmoWFlow(), wFlowStepAction);
	//
	//		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
	//		BmFilter bmFilter = new BmFilter();
	//		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
	//				bmoWFlowStep.getWFlowId().getName(), 
	//				bmoWFlowStep.getWFlowId().getLabel(), 
	//				BmFilter.EQUALS, 
	//				bmoPropertyRental.getWFlowId().toInteger(), 
	//				bmoPropertyRental.getBmoWFlow().getName().toString());
	//		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
	//		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
	//		uiWFlowStepList.show();
	//	}

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
}
