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

import com.flexwm.client.co.UiPropertySaleDetail;
import com.flexwm.client.dash.UiComercialProjectsDash;
import com.flexwm.client.dash.UiConsultancyDash;
import com.flexwm.client.dash.UiOrderSaleDash;
import com.flexwm.client.dash.UiPropertySaleDash;
import com.flexwm.client.op.UiConsultancyDetail;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoRateType;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
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
import com.flexwm.client.wf.IWFlowStepAction;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.client.wf.UiWFlowTimeTrackCalendar;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;


public class UiOpportunityDetail extends UiDetail {
	protected BmoOpportunity bmoOpportunity;

	BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
	BmoVenue bmoVenue = new BmoVenue();
	BmoMarket bmoMarket = new BmoMarket();
	BmoRateType bmoRateType = new BmoRateType();
	private int effectRpcAttempt = 0;
	private int bmoMarketRpcAttempt = 0;
	private int bmoRateTypeRpcAttempt = 0;
	private int bmoOpportunityDetailRpcAttempt = 0;

	public UiOpportunityDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoOpportunity(), id);
		bmoOpportunity = (BmoOpportunity)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoOpportunity = (BmoOpportunity)getBmObject();

		getBmoOpportunityDetail();

		addDetailImage(bmoOpportunity.getBmoCustomer().getLogo());
		if (isMobile())
			addTitleLabel(bmoOpportunity.getCode());
		else
			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoOpportunity) 
					+ ": " + bmoOpportunity.getCode());
		addLabel(bmoOpportunity.getName());
		addLabel(bmoOpportunity.getBmoOrderType().getName());	
		addLabel(bmoOpportunity.getBmoWFlow().getBmoWFlowType().getName());
		addLabel(bmoOpportunity.getBmoWFlow().getBmoWFlowPhase().getName());
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
				|| bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			addLabel(bmoOpportunity.getStartDate());
			addLabel(bmoOpportunity.getEndDate());
		} else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			addLabel(bmoOpportunity.getExpireDate());			
		} else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			addLabel(bmoOpportunity.getStartDate());
			addLabel(bmoOpportunity.getEndDate());
			addLabel(bmoOpportunity.getCustomField1());
		}
		addLabel(bmoOpportunity.getBmoWFlow().getProgress());

		if(bmoOpportunity.getMarketId().toInteger() > 0) 
			getBmoMarket();
		else {
			if (bmoOpportunity.getBmoCustomer().getRateTypeId().toInteger() > 0)
				getBmoRateType();
			else 
				getInfo();
		}
	}
	public void getInfo(){		
		if(!(bmoOpportunity.getMarketId().toInteger() > 0)) 
			addLabel(bmoOpportunity.getMarketId());
		if(bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE))
			addLabel(bmoOpportunity.getAmount());
		addLabel(bmoOpportunity.getStatus());
		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoOpportunity.getTags());
		addDetailPanel(tagPanel);	
		addEmptyLabel();
		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoOpportunity.getBmoCustomer()) 
				+ ": " + bmoOpportunity.getBmoCustomer().getCode());
		addLabel(bmoOpportunity.getBmoCustomer().getDisplayName());

		if (getSFParams().hasRead(bmoRateType.getProgramCode())) {
			addLabel(bmoRateType.getName());
			addLabel(bmoOpportunity.getBmoCustomer().getRate());
		}

		addLabel(bmoOpportunity.getBmoCustomer().getPhone());
		addLabel(bmoOpportunity.getBmoCustomer().getEmail());
		//		addEmptyLabel();
		//		
		//		addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoOpportunity.getBmoUser()) 
		//				+ ": " + bmoOpportunity.getBmoUser().getCode());
		//		addLabel(bmoOpportunity.getBmoUser().getEmail());
		//		addLabel(bmoOpportunity.getBmoCustomer().getPhone());

		addActionLabel("Inicio", bmoOpportunity.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar oportunidad
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOpportunityForm();
			}
		});

		// Mostrar datos adicionales si es de tipo renta
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			// Datos adicionales
			addActionLabel(new BmoOpportunityDetail(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().getUiTemplate().hideEastPanel();
					UiOpportunityDetailForm uiOpportunityDetailForm = new UiOpportunityDetailForm(getUiParams(), bmoOpportunityDetail.getId(), bmoOpportunity);
					setUiType(new BmoOpportunityDetail().getProgramCode(), UiParams.SINGLESLAVE);
					uiOpportunityDetailForm.show();
				}
			});
		}

		// Cliente
		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiParams().getUiTemplate().hideEastPanel();
				UiCustomer uiCustomer = new UiCustomer(getUiParams());
				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
				uiCustomer.edit(bmoOpportunity.getBmoCustomer());
			}
		});

		// Tareas
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});

		// Rastreo de Tiempo
		addActionLabel(new BmoWFlowTimeTrack(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTimeTracks();
			}
		});
		addActionLabel(new BmoRequisition(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showRequisition();
				
			}
		});

		// Cotizacion 
		addActionLabel(new BmoQuote(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showQuote();
			}
		});

		//addSeparatorRow();

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

		// Abrir Efecto si esta ganada oportunidad
		if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)) {
			addActionLabel(getEffectText(), getEffectCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					confirmOpenEffect();
				}
			});
		}

//		if (bmoOpportunity.getRfquId().toInteger()>0) {
//			addActionLabel("Ir al RFQ", new BmoOpportunity().getProgramCode(), new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					openRFQUEffect();
//				}
//			});
//		}
		// Preparar panel del oeste
		//showOpportunityWestPanel(bmoOpportunityDetail);

		// Panel default inicial
		showStart();
	}
//private void openRFQUEffect() {
//			UiRFQDetail uiRFQDetail = new UiRFQDetail(getUiParams(), bmoOpportunity.getRfquId().toInteger());
//		setUiType(new BmoProject().getProgramCode(), UiParams.DETAILFORM);
//		uiRFQDetail.show();
//}
	
	// Regresa al modulo de apertura
	public void openCallerProgram() {
		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoOpportunity.getCustomerId().toInteger());
			uiCustomerDetail.show();
		} else {
			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
		}
	}

	@Override
	public void close() {
		UiOpportunity uiOpportunityList = new UiOpportunity(getUiParams());
		setUiType(UiParams.MASTER);
		uiOpportunityList.show();
	}

	public void reset() {
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOpportunity.getId());
		uiOpportunityDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiOpportunityStart uiOpportunityStart = new UiOpportunityStart(getUiParams(), bmObjectProgramId, bmoOpportunity);
		uiOpportunityStart.show();
	}

	public void returnToDashboard() {
		// Es de tipo Renta
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			UiComercialProjectsDash uiComercialDash = new UiComercialProjectsDash(getUiParams());
			uiComercialDash.show();
		}
		// Es de tipo Venta o Consultoria
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			UiOrderSaleDash uiOrderSaleDash = new UiOrderSaleDash(getUiParams());
			uiOrderSaleDash.show();
		}
		// Es de tipo Inmueble
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			UiPropertySaleDash uiPropertySaleDash = new UiPropertySaleDash(getUiParams());
			uiPropertySaleDash.show();
		}
		// Es de tipo Consultoria
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			UiConsultancyDash uiConsultancyDash = new UiConsultancyDash(getUiParams());
			uiConsultancyDash.show();
		} 
	}

	public void showOpportunityForm(){
		getUiParams().getUiTemplate().hideEastPanel();
		UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
		setUiType(bmoOpportunity.getProgramCode(), UiParams.MASTER);
		uiOpportunity.edit(bmoOpportunity);
	}

	public void showQuote() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiQuoteForm uiQuoteForm = new UiQuoteForm(getUiParams(), bmoOpportunity.getQuoteId().toInteger(), bmoOpportunity);
		setUiType(new BmoQuote().getProgramCode(), UiParams.SINGLESLAVE);
		uiQuoteForm.show();
	}
	private void showRequisition() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiRequisition uiRequisitionList = new UiRequisition(getUiParams(), bmoOpportunity);
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOportunityId().getName(), bmoOpportunity.getId());
		getUiParams().getUiProgramParams(bmoRequisition.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoRequisition().getProgramCode(), UiParams.SLAVE);
		uiRequisitionList.show();
	}

	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();

		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoOpportunity.getBmoWFlow(), wFlowStepAction);
 
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoOpportunity.getWFlowId().toInteger(), 
				bmoOpportunity.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

	public void showTimeTracks() {
		getUiParams().getUiTemplate().hideEastPanel();

		UiWFlowTimeTrackCalendar uiWFlowTimeTrackCalendar = new UiWFlowTimeTrackCalendar(getUiParams(), bmoOpportunity.getBmoWFlow());

		BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowTimeTrack.getKind(), 
				bmoWFlowTimeTrack.getWFlowId().getName(), 
				bmoWFlowTimeTrack.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoOpportunity.getWFlowId().toInteger(), 
				bmoOpportunity.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowTimeTrack.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.SLAVE);
		uiWFlowTimeTrackCalendar.show();
	}

	public void showOpportunityWestPanel(BmoOpportunityDetail bmoOpportunityDetail) {
		this.bmoOpportunityDetail = bmoOpportunityDetail;

		// Datos del cliente
		FlowPanel customerVP = new FlowPanel();
		customerVP.setWidth((UiTemplate.WESTSIZE - 60) + "px");
		DisclosurePanel discPanel = new DisclosurePanel("Datos Cliente");
		//		discPanel.setStyleName("detailText");
		discPanel.setWidth((UiTemplate.WESTSIZE - 60) + "px");

		UiCustomerView uiCustomerView = new UiCustomerView(getUiParams(), customerVP, bmoOpportunity.getCustomerId().toInteger(), bmoOpportunity.getId());
		uiCustomerView.show();
	}

	// Establece texto del boton
	public String getEffectText() {
		// Es de tipo Renta
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			return "Ir a Proyecto";
		}
		// Es de tipo Venta 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			return "Ir a Pedido";
		}	
		// Es de tipo Servicio 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			return "Ir a Venta";
		}	
		// Es de tipo Inmueble
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			return "Ir a Venta";
		} 
		else return "";
	}

	// Establece texto del boton
	public String getEffectCode() {
		// Es de tipo Renta
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			return new BmoProject().getProgramCode();
		}
		// Es de tipo Venta 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			return new BmoOrder().getProgramCode();
		}	
		// Es de tipo Servicio 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			return new BmoConsultancy().getProgramCode();
		}
		// Es de tipo Inmueble
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			return new BmoPropertySale().getProgramCode();
		} 
		else return "";
	}

	// Prepara apertura de effecto
	public void confirmOpenEffect() {

		// Es de tipo Renta
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			getEffect();
		}
		// Es de tipo Venta 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			getEffect();
		}	
		// Es de tipo Servicio 
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			getEffect();
		}	
		// Es de tipo Inmueble
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			getEffect();
		}
	}

	// Abre efecto de la oportunidad
	protected void openOpportunityEffect(BmUpdateResult result) {
		// Es de tipo Renta - abre Proyecto
		if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), result.getBmObject().getId());
			setUiType(new BmoProject().getProgramCode(), UiParams.DETAILFORM);
			uiProjectDetail.show();
		}
		// Es de tipo Venta - abre Pedido
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), result.getBmObject().getId());
			setUiType(new BmoOrder().getProgramCode(), UiParams.DETAILFORM);
			uiOrderDetail.show();
		}
		// Es de tipo Servicio - abre Pedido
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), result.getBmObject().getId());
			setUiType(new BmoOrder().getProgramCode(), UiParams.DETAILFORM);
			uiConsultancyDetail.show();
		}
		// Es de tipo Inmueble - abre Venta de Inmueble
		else if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), result.getBmObject().getId());
			setUiType(new BmoPropertySale().getProgramCode(), UiParams.DETAILFORM);
			uiPropertySaleDetail.show();
		} 
		else showSystemMessage("No fue encontrado el Efecto.");
	}

	// Accion de una tarea
	public class SFComponentWFlowStepAction implements IWFlowStepAction {
		@Override
		public void action() {
			reset();
		}
	}

	// Obtiene liga del effecto, primer intento
	public void getEffect() {
		getEffect(0);
	}

	// Obtiene liga del effecto
	public void getEffect(int effectRpcAttempt) {
		if (effectRpcAttempt < 5) {
			setEffectRpcAttempt(effectRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getEffectRpcAttempt() < 5)
						getEffect(getEffectRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getEffect() ERROR: " + caught.toString());
					}
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setEffectRpcAttempt(0);
					openOpportunityEffect(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOpportunity.getPmClass(), bmoOpportunity, BmoOpportunity.ACTION_GETEFFECT, "" + bmoOpportunity.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
			}
		}
	}
	// Obtener mercado, primero intento
	public void getBmoMarket() {
		getBmoMarket(0);
	}

	// Obtener mercado
	public void getBmoMarket(int bmoMarketRpcAttempt ) {
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
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoMarketRpcAttempt(0);
					bmoMarket = (BmoMarket)result;
					addLabel(bmoMarket.getName());
					if (bmoOpportunity.getBmoCustomer().getRateTypeId().toInteger() > 0)
						getBmoRateType();
					else 
						getInfo();
				}
			};

			// Llamada al servicio RPC
			try {
				//			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoMarket.getPmClass(), bmoOpportunity.getMarketId().toInteger(), callback);
				//			}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getbmoMarket ERROR: " + e.toString());
			}
		}
	}

	// Tipos de Tarifa, primer intento
	public void getBmoRateType() {
		getBmoRateType(0);
	}

	// Tipos de Tarifa
	public void getBmoRateType(int bmoRateTypeRpcAttempt ) {
		if (bmoRateTypeRpcAttempt < 5) {
			setBmoRateTypeRpcAttempt(bmoRateTypeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoRateTypeRpcAttempt() < 5)
						getBmoRateType(getBmoRateTypeRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoRateType() ERROR: " + caught.toString());
					}
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoRateTypeRpcAttempt(0);
					bmoRateType = (BmoRateType)result;
					getInfo();
				}
			};

			// Llamada al servicio RPC
			try {

				//			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoRateType.getPmClass(), bmoOpportunity.getBmoCustomer().getRateTypeId().toInteger(), callback);
				//			}
				//			else
				//				showSystemMessage("qqqq");
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoRateType() ERROR: " + e.toString());
			}
		}
	}

	//	public void getBmoVenueId() {
	//		// Establece eventos ante respuesta de servicio
	//		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
	//			public void onFailure(Throwable caught) {
	//				stopLoading();
	//				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
	//				else showErrorMessage(this.getClass().getName() + "-getBmoReqPayType() ERROR: " + caught.toString());
	//				
	//			}
	//
	//			public void onSuccess(BmObject result) {
	//				stopLoading();				
	//				bmoVenue = (BmoVenue )result;
	//				addLabel(bmoVenue.getName());
	//				addLabel(bmoVenue.getStreet());
	//				addLabel(bmoVenue.getNumber());
	//				addLabel(bmoVenue.getNeighborhood());
	//				addLabel(bmoVenue.getName());
	//				addLabel(bmoVenue.getBmoCity().getBmoState().getCode());
	//				
	//			}
	//		};
	//
	//		// Llamada al servicio RPC
	//		try {
	//			if (!isLoading()) {
	//				startLoading();
	//				getUiParams().getBmObjectServiceAsync().get(bmoVenue.getPmClass(),bmoOpportunity.getVenueId().toInteger(), callback);
	//			}
	//		} catch (SFException e) {
	//			stopLoading();
	//			showErrorMessage(this.getClass().getName() + "-getBmoReqPayType ERROR: " + e.toString());
	//		}
	//		
	//	}

	// Obtener detalle de la oportunidad, primer intento
	public void getBmoOpportunityDetail() {
		getBmoOpportunityDetail(0);
	}

	// Obtener detalle de la oportunidad
	public void getBmoOpportunityDetail(int bmoOpportunityDetailRpcAttempt) {
		if (bmoOpportunityDetailRpcAttempt < 5) {
			setBmoOpportunityDetailRpcAttempt(bmoOpportunityDetailRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoOpportunityDetailRpcAttempt() < 5)
						getBmoOpportunityDetail(getBmoOpportunityDetailRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-getBmoOpportunityDetail() ERROR: " + caught.toString());
						//setLinks(new BmoOpportunityDetail());
						showOpportunityWestPanel(bmoOpportunityDetail);
					}
				}

				@Override
				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoOpportunityDetailRpcAttempt(0);
					bmoOpportunityDetail = ((BmoOpportunityDetail)result);				
					showOpportunityWestPanel(bmoOpportunityDetail);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().getBy(bmoOpportunityDetail.getPmClass(), bmoOpportunity.getId(), bmoOpportunityDetail.getOpportunityId().getName(), callback);
					//getUiParams().getBmObjectServiceAsync().get(bmoOpportunityDetail.getPmClass(), bmoOpportunity.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOpportunityDetail() ERROR: " + e.toString());
			}
		}
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

	public int getBmoRateTypeRpcAttempt() {
		return bmoRateTypeRpcAttempt;
	}

	public void setBmoRateTypeRpcAttempt(int bmoRateTypeRpcAttempt) {
		this.bmoRateTypeRpcAttempt = bmoRateTypeRpcAttempt;
	}

	public int getBmoOpportunityDetailRpcAttempt() {
		return bmoOpportunityDetailRpcAttempt;
	}

	public void setBmoOpportunityDetailRpcAttempt(int bmoOpportunityDetailRpcAttempt) {
		this.bmoOpportunityDetailRpcAttempt = bmoOpportunityDetailRpcAttempt;
	}
}
