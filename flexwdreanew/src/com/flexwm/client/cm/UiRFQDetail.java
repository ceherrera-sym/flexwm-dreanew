///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.client.cm;
//
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoMarket;
//import com.flexwm.shared.cm.BmoOpportunity;
//import com.flexwm.shared.cm.BmoProject;
//import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.cm.BmoRateType;
//import com.flexwm.shared.ev.BmoVenue;
//import com.flexwm.shared.op.BmoOrderType;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.rpc.StatusCodeException;
//import com.symgae.client.ui.UiDetail;
//import com.symgae.client.ui.UiParams;
//import com.flexwm.client.wf.IWFlowStepAction;
//import com.flexwm.client.wf.UiWFlowStep;
//import com.flexwm.client.wf.UiWFlowTimeTrackCalendar;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.flexwm.shared.wf.BmoWFlowStep;
//import com.flexwm.shared.wf.BmoWFlowTimeTrack;
//
//
//public class UiRFQDetail extends UiDetail {
//	protected BmoRFQU bmoRFQU;
//	
//	
//	BmoVenue bmoVenue = new BmoVenue();
//	BmoMarket bmoMarket = new BmoMarket();
//	BmoRateType bmoRateType = new BmoRateType();
//	private int effectRpcAttempt = 0;
//	private int bmoMarketRpcAttempt = 0;
//	private int bmoRateTypeRpcAttempt = 0;
//	private int bmoOpportunityDetailRpcAttempt = 0;
//
//	public UiRFQDetail(UiParams uiParams, int id) {
//		super(uiParams, new BmoRFQU(), id);
//		bmoRFQU = (BmoRFQU)getBmObject();
//	}
//
//	@Override
//	public void populateLabels() {
//		bmoRFQU = (BmoRFQU)getBmObject();
//
//		if (isMobile())
//			addTitleLabel(bmoRFQU.getCodeRFQU());
//		else
//			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoRFQU) 
//					+ ": " + bmoRFQU.getCodeRFQU());
//		addLabel(bmoRFQU.getAffair());
//		addLabel(bmoRFQU.getBmoCustomer().getCode());	
//		addLabel(bmoRFQU.getBmoOrderType().getName());	
//		addLabel(bmoRFQU.getBmoWFlow().getBmoWFlowType().getName());
//		addLabel(bmoRFQU.getDateRFQU());
//		addLabel(bmoRFQU.getBmoWFlow().getProgress());
//		getInfo();
//	}
//	public void getInfo(){		
//
//		// Editar oportunidad
//		addActionLabel("RFQ", "edit", new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showRFQForm();
//			}
//		});
//	
//
//		// Cliente
//		addActionLabel(getSFParams().getProgramTitle(new BmoCustomer().getProgramCode()), new BmoCustomer().getProgramCode(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				getUiParams().getUiTemplate().hideEastPanel();
//				UiCustomer uiCustomer = new UiCustomer(getUiParams());
//				setUiType(new BmoCustomer().getProgramCode(), UiParams.SLAVE);
//				uiCustomer.edit(bmoRFQU.getBmoCustomer());
//			}
//		});
//
//		// Tareas
//		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
//			@Override
//				public void onClick(ClickEvent event) {
//				showSteps();
//			}
//		});
//
//		// Rastreo de Tiempo
////		addActionLabel(new BmoWFlowTimeTrack(), new ClickHandler() {
////			@Override
////			public void onClick(ClickEvent event) {
////				showTimeTracks();
////			}
////		});
//
//		// EStimacion
//		addActionLabel(new BmoEstimation(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showEstimation();
//			}
//		});
//
//		//addSeparatorRow();
//
//		// Si otro modulo hizo la llamada
//		if (getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
//			addActionLabel("Regresar", "close", new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					close();
//				}
//			});
//		} else {
//			addActionLabel("Regresar", getUiParams().getCallerProgramCode(), new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					openCallerProgram();
//				}
//			});
//
//			addActionLabel("Ir a Listado", "close", new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					close();
//				}
//			});
//		}
//		// Abrir Efecto si esta ganado el rfq
//		if (bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_COMPLET)) {
//			addActionLabel("Ir a Oportunidad", new BmoOpportunity().getProgramCode(), new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					getEffect();
//				}
//			});
//		}
//
//
//		// Panel default inicial
//		showRFQForm();
//	}
//	
//	
//
//	
//	// Regresa al modulo de apertura
//	public void openCallerProgram() {
//		if (getUiParams().getCallerProgramCode() == new BmoCustomer().getProgramCode()) {
//			UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoRFQU.getCustomerId().toInteger());
//			uiCustomerDetail.show();
//		} else {
//			getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
//		}
//	}
//
//	@Override
//	public void close() {
//		UiRFQU uiRFQUList = new UiRFQU(getUiParams());
//		setUiType(UiParams.MASTER);
//		uiRFQUList.show();
//	}
//	public void showEstimation() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiEstimationForm uiEstimationForm = new UiEstimationForm(getUiParams(),  bmoRFQU.getEstimationId().toInteger());
//		setUiType(new BmoEstimation().getProgramCode(), UiParams.SLAVE);
//		uiEstimationForm.show();
//	}
//
//	public void reset() {
//		UiRFQDetail uiRFQUDetail = new UiRFQDetail(getUiParams(), bmoRFQU.getId());
//		uiRFQUDetail.show();
//	}
//
//	public void showStart() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiRFQUStart uiRFQUStart = new UiRFQUStart(getUiParams(), bmObjectProgramId,bmoRFQU);
//		uiRFQUStart.show();
//	}
//
//
//	public void showRFQForm(){
//		bmoRFQU= (BmoRFQU)getBmObject();
//		UiRFQUForm uiRFQUForm = new UiRFQUForm(getUiParams(), bmoRFQU.getId());
//		uiRFQUForm.show();
//	}
//
////	public void showQuote() {
////		getUiParams().getUiTemplate().hideEastPanel();
////		UiQuoteForm uiQuoteForm = new UiQuoteForm(getUiParams(),  bmoRFQU.getQuoteId().toInteger());
////		setUiType(new BmoQuote().getProgramCode(), UiParams.SINGLESLAVE);
////		uiQuoteForm.show();
////	}
//
//	public void showSteps() {
//		getUiParams().getUiTemplate().hideEastPanel();
//
//		SFComponentWFlowStepAction wFlowStepAction = new SFComponentWFlowStepAction();
//
//		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoRFQU.getBmoWFlow(), wFlowStepAction);
// 
//		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
//		BmFilter bmFilter = new BmFilter();
//		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
//				bmoWFlowStep.getWFlowId().getName(), 
//				bmoWFlowStep.getWFlowId().getLabel(), 
//				BmFilter.EQUALS, 
//				bmoRFQU.getwFlowId().toInteger(), 
//				bmoRFQU.getBmoWFlow().getName().toString());
//		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
//		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
//		uiWFlowStepList.show();
//	}
//
//	public void showTimeTracks() {
//		getUiParams().getUiTemplate().hideEastPanel();
//
//		UiWFlowTimeTrackCalendar uiWFlowTimeTrackCalendar = new UiWFlowTimeTrackCalendar(getUiParams(), bmoRFQU.getBmoWFlow());
//
//		BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
//		BmFilter bmFilter = new BmFilter();
//		bmFilter.setValueLabelFilter(bmoWFlowTimeTrack.getKind(), 
//				bmoWFlowTimeTrack.getWFlowId().getName(), 
//				bmoWFlowTimeTrack.getWFlowId().getLabel(), 
//				BmFilter.EQUALS, 
//				bmoRFQU.getwFlowId().toInteger(), 
//				bmoRFQU.getBmoWFlow().getName().toString());
//		getUiParams().getUiProgramParams(bmoWFlowTimeTrack.getProgramCode()).setForceFilter(bmFilter);
//		setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.SLAVE);
//		uiWFlowTimeTrackCalendar.show();
//	}
//
//	// Establece texto del boton
//	public String getEffectText() {
//		// Es de tipo Renta
//		if (bmoRFQU.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//			return "Ir a Proyecto";
//		}
//
//		else return "";
//	}
//	// Accion de una tarea
//	public class SFComponentWFlowStepAction implements IWFlowStepAction {
//		@Override
//		public void action() {
//			reset();
//		}
//	}
//
//	// Abre efecto de la rfqu
//	protected void openRFQUEffect(BmUpdateResult result) {
//		// Es de tipo Renta - abre Proyecto
//			UiOpportunityDetail  uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), result.getBmObject().getId());
//			setUiType(new BmoProject().getProgramCode(), UiParams.DETAILFORM);
//			uiOpportunityDetail.show();
//		}
//	
//	// Obtiene liga del effecto, primer intento
//		public void getEffect() {
//			getEffect(0);
//		}
//
//		// Obtiene liga del effecto
//		public void getEffect(int effectRpcAttempt) {
//			if (effectRpcAttempt < 5) {
//				setEffectRpcAttempt(effectRpcAttempt + 1);
//
//				// Establece eventos ante respuesta de servicio
//				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						stopLoading();
//						if (getEffectRpcAttempt() < 5)
//							getEffect(getEffectRpcAttempt());
//						else {
//							if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
//							else showErrorMessage(this.getClass().getName() + "-getEffect() ERROR: " + caught.toString());
//						}
//					}
//
//					@Override
//					public void onSuccess(BmUpdateResult result) {
//						stopLoading();
//						setEffectRpcAttempt(0);
//						openRFQUEffect(result);
//					}
//				};
//
//				// Llamada al servicio RPC
//				try {
//					if (!isLoading()) {
//						startLoading();
//						getUiParams().getBmObjectServiceAsync().action(bmoRFQU.getPmClass(), bmoRFQU, BmoRFQU.ACTION_GETEFFECT, "" + bmoRFQU.getId(), callback);
//					}
//				} catch (SFException e) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
//				}
//			}
//		}
//
//	// Variables para llamadas RPC
//	public int getEffectRpcAttempt() {
//		return effectRpcAttempt;
//	}
//
//	public void setEffectRpcAttempt(int effectRpcAttempt) {
//		this.effectRpcAttempt = effectRpcAttempt;
//	}
//
//	public int getBmoMarketRpcAttempt() {
//		return bmoMarketRpcAttempt;
//	}
//
//	public void setBmoMarketRpcAttempt(int bmoMarketRpcAttempt) {
//		this.bmoMarketRpcAttempt = bmoMarketRpcAttempt;
//	}
//
//	public int getBmoRateTypeRpcAttempt() {
//		return bmoRateTypeRpcAttempt;
//	}
//
//	public void setBmoRateTypeRpcAttempt(int bmoRateTypeRpcAttempt) {
//		this.bmoRateTypeRpcAttempt = bmoRateTypeRpcAttempt;
//	}
//
//	public int getBmoOpportunityDetailRpcAttempt() {
//		return bmoOpportunityDetailRpcAttempt;
//	}
//
//	public void setBmoOpportunityDetailRpcAttempt(int bmoOpportunityDetailRpcAttempt) {
//		this.bmoOpportunityDetailRpcAttempt = bmoOpportunityDetailRpcAttempt;
//	}
//}
