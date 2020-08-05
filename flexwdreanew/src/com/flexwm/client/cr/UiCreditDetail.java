/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cr;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerView;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiOrderForm;
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
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
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiCreditDetail extends UiDetail {

	protected BmoCredit bmoCredit;	

	BmoOrder bmoOrder = new BmoOrder();

	public UiCreditDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoCredit(), id);
		bmoCredit = (BmoCredit)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoCredit = (BmoCredit)getBmObject();
		
		if (isMobile())
			addTitleLabel(bmoCredit.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoCredit) 
					+ ": " + bmoCredit.getCode());
		addLabel(bmoCredit.getCode());		
		addLabel(bmoCredit.getStartDate());
		addLabel(bmoCredit.getBmoCustomer().getDisplayName());
		addLabel(bmoCredit.getStatus());
		addLabel(bmoCredit.getPaymentStatus());

		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoCredit.getTags());
		addDetailPanel(tagPanel);

		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoCredit.getBmoCustomer()) 
				+ ": " + bmoCredit.getBmoCustomer().getCode());
		addLabel(bmoCredit.getBmoCustomer().getDisplayName());
		addLabel(bmoCredit.getBmoCustomer().getPhone());
		addLabel(bmoCredit.getBmoCustomer().getEmail());
		
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
				getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), bmoCredit.getOrderId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	public void setLinks(BmoOrder bmoOrder) {

		this.bmoOrder = bmoOrder;

		addActionLabel("Inicio", bmoCredit.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar forma
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCreditForm();
			}
		});

		addActionLabel("Cliente", "cust", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showClient();
			}
		});

		// Pedido Credito
		addActionLabel(new BmoOrder(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOrder();
			}
		});

		// Cuentas x Cobrar
		addActionLabel(new BmoRaccount(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showRaccount();
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
		showCreditWestPanel();

		// Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiCredit uiCreditList = new UiCredit(getUiParams());
		setUiType(UiParams.MASTER);
		uiCreditList.show();
	}

	public void reset() {
		UiCreditDetail uiCreditDetail = new UiCreditDetail(getUiParams(), bmoCredit.getId());
		uiCreditDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCreditStart uiCreditStart = new UiCreditStart(getUiParams(), bmObjectProgramId, bmoCredit);
		uiCreditStart.show();
	}

	public void showCreditForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCredit uiCreditForm = new UiCredit(getUiParams());
		uiCreditForm.edit(bmoCredit);
	}

	public void showClient() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomer uiCustomer = new UiCustomer(getUiParams());
		setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
		uiCustomer.edit(bmoCredit.getBmoCustomer());
	}

	public void showOrder() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOrderForm uiOrder = new UiOrderForm(getUiParams(), bmoCredit.getOrderId().toInteger());
		setUiType(new BmoOrder().getProgramCode(), UiParams.SINGLESLAVE);
		uiOrder.show();
	}

	public void showRaccount() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRaccount uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoCredit.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoRaccount.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRaccount().getProgramCode(), UiParams.SLAVE);
		uiRaccountList.show();
	}

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoCredit.getBmoWFlow(), new CreditWFlowStepAction());

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoCredit.getWFlowId().toInteger(), 
				bmoCredit.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);

		uiWFlowStepList.show();
	}

	// Mostrar datos panel oeste
	public void showCreditWestPanel() {
		// Datos del cliente
		FlowPanel customerVP = new FlowPanel();
		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");

		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoCredit.getCustomerId().toInteger(), bmoCredit.getId());
		uiCustomerView.show();

		discPanel.add(customerVP);
		discPanel.setOpen(false);
		detailLabelTable.addDetailPanel(discPanel);
	}

	// Accion de una tarea
	public class CreditWFlowStepAction implements IWFlowStepAction {

		@Override
		public void action() {
			reset();
		}
	}
}
