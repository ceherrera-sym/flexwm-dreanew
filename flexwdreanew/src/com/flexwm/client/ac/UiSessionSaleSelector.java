package com.flexwm.client.ac;

import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiSessionSaleSelector extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoSessionSale bmoSessionSale;
	Label sessionSaleLabel;
	
	UiOrderSessionList uiOrderSessionList;
	UiSessionSelectorCalendar uiSessionSelectorCalendar;
	
	
	public UiSessionSaleSelector(UiParams uiParams, int programId, BmoSessionSale bmoSessionSale){
		super(uiParams, new BmoSessionSale());
		this.bmoSessionSale = bmoSessionSale;
				
		sessionSaleLabel = new Label("Selector de Sesiones");
		sessionSaleLabel.setStyleName("programSubtitle");
		
		flexTable.setSize("100%", "100%");
	}
	
	@Override
	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();
				
		// Vista Calendario Sesiones
		DecoratorPanel sessionCalendarDP = new DecoratorPanel();
		sessionCalendarDP.setSize("100%", "500px");
		FlowPanel sessionCalendarFP = new FlowPanel();
		sessionCalendarDP.setWidget(sessionCalendarFP);
		
		BmoSession bmoSession = new BmoSession();
		BmoOrderSession bmoOrderSession = new BmoOrderSession();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getOrderId().getName(), bmoSessionSale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoSession.getProgramCode(), UiParams.MINIMALIST);
		
		// Accion calendario sesiones
		UiActionHandler uiSessionCalendarActionHandler = new UiActionHandler() {
			@Override
			public void action() {
				resetOrderSessions();
			}
		};
		
		uiSessionSelectorCalendar = new UiSessionSelectorCalendar(getUiParams(), sessionCalendarFP, bmoSessionSale, bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType(), uiSessionCalendarActionHandler);
		uiSessionSelectorCalendar.show();
		
		// Lista sesiones del pedido
		DecoratorPanel orderSessionDP = new DecoratorPanel();
		orderSessionDP.setSize("100%", "500px");
		FlowPanel orderSessionFP = new FlowPanel();
		ScrollPanel orderSessionSP = new ScrollPanel();
		orderSessionSP.setSize("100%", "500px");
		orderSessionSP.add(orderSessionFP);
		orderSessionDP.setWidget(orderSessionSP);
		
		BmFilter filterByOrder = new BmFilter();
		filterByOrder.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getOrderId().getName(), bmoSessionSale.getOrderId().toInteger());
		getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).setForceFilter(filterByOrder);
		setUiType(bmoOrderSession.getProgramCode(), UiParams.MINIMALIST);
		
		// Accion pedidos de la sesion
		UiActionHandler uiOrderSessionActionHandler = new UiActionHandler() {
			@Override
			public void action() {
				resetSessionCalendar();
			}
		};
		
		uiOrderSessionList = new UiOrderSessionList(getUiParams(), orderSessionFP, bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger(), bmoSessionSale.getOrderId().toInteger(), uiOrderSessionActionHandler);
		uiOrderSessionList.show();
	
		HorizontalPanel row1HP = new HorizontalPanel();
		row1HP.setSize("100%", "500px");
		row1HP.add(sessionCalendarDP);
		//row1HP.add(new HTML("<pre> </pre>"));
		
		flexTable.setWidget(1, 0, row1HP);
		
		HorizontalPanel row2HP = new HorizontalPanel();
		row2HP.setSize("100%", "500px");
		//row1HP.add(new HTML("<pre> </pre>"));
		row2HP.add(orderSessionDP);
		
		flexTable.setWidget(2, 0, row2HP);

		
		addToDP(sessionSaleLabel);
		addToDP(flexTable);
	}
	
	
	public void resetSessionCalendar() {
		uiSessionSelectorCalendar.reset();
	}
	
	public void resetOrderSessions() {
		uiOrderSessionList.show();
	}
	
}
