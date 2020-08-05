package com.flexwm.client.op;
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
//import com.flexwm.shared.cm.BmoServiceOrder;
//import com.flexwm.shared.cm.BmoServiceOrderReportTime;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.symgae.client.ui.UiDetail;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmFilter;
//
//
//public class UiServiceOrderDetail extends UiDetail {
//	protected BmoServiceOrder bmoServiceOrder;
//
//	public UiServiceOrderDetail(UiParams uiParams, int id) {
//		super(uiParams, new BmoServiceOrder(), id);
//		bmoServiceOrder = (BmoServiceOrder)getBmObject();		
//	}
//
//	@Override
//	public void populateLabels() {
//		bmoServiceOrder = (BmoServiceOrder)getBmObject();
////		addDetailImage(bmoServiceOrder.getBmoCustomer().getLogo());
//		if (isMobile())
//			addTitleLabel(bmoServiceOrder.getCode());
//		else
//			addTitleLabel(getUiParams().getSFParams().getProgramFormTitle(bmoServiceOrder) 
//					+ ": " + bmoServiceOrder.getCode());
//		addLabel(bmoServiceOrder.getCode());
//		addLabel(bmoServiceOrder.getActivity());
//
//		getLinks();
//
//	}
//	public void getLinks() {
//		
//		addEmptyLabel();
//
//		// Inicio
//		addActionLabel("Inicio", bmoServiceOrder.getProgramCode(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showServiceOrderForm();
//				//reset();
//			}
//		});
//
//		// Editar Reporte de Timempo
//		addActionLabel(new BmoServiceOrderReportTime(), new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				showServiceOrderReportTime();
//			}
//		});
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
//					//openCallerProgram();
//				}
//			});
////			if(getUiParams().getCallerProgramCode() != bmoServiceOrder.getProgramCode())
////			{
////				addActionLabel("Ir a Listado", "close", new ClickHandler() {
////					@Override
////					public void onClick(ClickEvent event) {
////						getUiParams().setCallerProgramCode(new BmoOrder().getProgramCode());;
////						close();
////					}
////				});
////			}
//		}
//
//		// Abrir Proyecto CAMBIAR EL ICONOOOOOOOOOOOOOOOOOOOO
//		if (bmoServiceOrder.getProjectStepId().toInteger() > 0) {
//			addActionLabel("Ir a Proyecto", bmoServiceOrder.getProgramCode(), new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					showProjectStep();
//				}
//			});
//		} else if (bmoServiceOrder.getRfquId().toInteger() > 0) {
//			addActionLabel("Ir a Proyecto", bmoServiceOrder.getProgramCode(), new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					showProjectStep();
//				}
//			});
//		}
//		
//		// Panel default inicial		
//		showServiceOrderForm();
//	}
//
//
//	@Override
//	public void close() {
//		UiServiceOrder uiServiceOrderList = new UiServiceOrder(getUiParams());
//		setUiType(UiParams.MASTER);
//		uiServiceOrderList.show();
//	}
//
//	public void reset() {
//		UiServiceOrderDetail uiOrderDetail = new UiServiceOrderDetail(getUiParams(), bmoServiceOrder.getId());
//		uiOrderDetail.show();
//	}
//
//	private void showServiceOrderForm() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		UiServiceOrderForm uiServiceOrderForm = new UiServiceOrderForm(getUiParams(), bmoServiceOrder.getId());
//		setUiType(UiParams.SLAVE);
//		uiServiceOrderForm.show();
//	}
//	
//	private void showServiceOrderReportTime() {
//		getUiParams().getUiTemplate().hideEastPanel();
//		BmoServiceOrderReportTime bmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//		BmFilter bmFilterByServiceOrder = new BmFilter();
//		bmFilterByServiceOrder.setValueFilter(bmoServiceOrderReportTime.getKind(), bmoServiceOrderReportTime.getServiceOrderId(), bmoServiceOrder.getId());
//		getUiParams().getUiProgramParams(new BmoServiceOrderReportTime().getProgramCode()).setForceFilter(bmFilterByServiceOrder);
//		UiServiceOrderReportTime uiServiceOrderReportTime = new UiServiceOrderReportTime(getUiParams(), bmoServiceOrder);
//		setUiType(new BmoServiceOrderReportTime().getProgramCode(), UiParams.SLAVE);
//		uiServiceOrderReportTime.show();
//	}
//	private void showProjectStep() {
////		UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), bmoServiceOrder.getProjectStepId().toInteger());
////		uiProjectStepDetail.show();
//	}
//
//}
