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

import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiActionHandler;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;


public class UiOrderSessionList extends UiList {
	BmoSessionSale bmoSessionSale = new BmoSessionSale();
	BmoOrderSession bmoOrderSession;
	private int sessionTypeId;
	//private int orderId;

	UiActionHandler uiActionHandler;

	public UiOrderSessionList(UiParams uiParams, int sessionTypeId) {
		super(uiParams, new BmoOrderSession());
		this.sessionTypeId = sessionTypeId;
	}

	public UiOrderSessionList(UiParams uiParams, Panel defaultPanel, int sessionTypeId) {
		super(uiParams, defaultPanel, new BmoOrderSession());
		this.sessionTypeId = sessionTypeId;		
	}

	public UiOrderSessionList(UiParams uiParams, Panel defaultPanel, int sessionTypeId, int orderId,  UiActionHandler uiActionHandler) {
		super(uiParams, defaultPanel, new BmoOrderSession());
		this.sessionTypeId = sessionTypeId;
		//this.orderId = orderId;		
		this.uiActionHandler = uiActionHandler;
	}

	@Override
	public void create() {
		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), 0, sessionTypeId);
		uiOrderSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), bmObject.getId(), sessionTypeId);
		uiOrderSessionForm.show();
	}

	@Override
	public void displayList() {
		int col = 0;
		//getOrderSessionNo();
		// Si es minimalista, despliega unicamente algunos campos		
		if (isMinimalist()) {			
			col = 0;
			listFlexTable.addListTitleCell(0, col++, "Instructor");
			listFlexTable.addListTitleCell(0, col++, "Inicio");
			listFlexTable.addListTitleCell(0, col++, "Eliminar");

			int row = 1;
			while (iterator.hasNext()) {
				col = 0;
				BmoOrderSession bmoOrderSession = (BmoOrderSession)iterator.next(); 


				listFlexTable.addListCell(row, col++, getBmObject(), bmoOrderSession.getBmoSession().getBmoUser().getCode());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoOrderSession.getBmoSession().getStartDate());

				Image deleteImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/delete.png"));
				deleteImage.addClickHandler(new UiClickHandler(bmoOrderSession) {

					@Override
					public void onClick(ClickEvent arg0) {
						deleteOrderSession(bmObject);
					}
				});
				listFlexTable.setWidget(row, col, deleteImage);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellLink");

				listFlexTable.formatRow(row);
				row++;
			}

		} else {
			super.displayList();
		}
	}

//	public void getOrderSessionNo() {
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				showErrorMessage(this.getClass().getName() + "-getOrderSessionNo() ERROR: " + caught.toString());
//			}
//
//			@Override
//			public void onSuccess(BmUpdateResult result) {
//				int col = 0;
//				listFlexTable.addListTitleCell(0, col++, "Apart/Pend:");
//				listFlexTable.addListTitleCell(0, col++, "" + result.getMsg());
//				listFlexTable.addListTitleCell(0, col++, "");					
//			}
//		};
//
//		try {
//			if (!isLoading()) {
//				getUiParams().getBmObjectServiceAsync().action(bmoSessionSale.getPmClass(), bmoSessionSale, BmoSessionSale.ACTION_COUNTORSS, "" + orderId, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-getOrderSessionNo() ERROR: " + e.toString());
//		}
//	} 

	/*
	public void getOrderSessionNo(String orderId) {		
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {				
				showErrorMessage(this.getClass().getName() + "-getOrderSessionNo() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {				

			}
		};

		try {	
			if (!isLoading()) {
				showSystemMessage("Entra");
				getUiParams().getBmObjectServiceAsync().action(bmoSessionSale.getPmClass(), bmoSessionSale, BmoSessionSale.ACTION_COUNTORDERSESSION, "" + orderId, callback);

			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getOrderSessionNo() ERROR: " + e.toString());
		}
	}	*/	

	public void deleteOrderSession(BmObject bmObject) {
		delete(bmObject);
		uiActionHandler.action();
	}

	// Al recibir la confirmacion de eliminar, actualiza selector calendario
	@Override
	public void deleteNext() {
		uiActionHandler.action();
	}

	public class UiOrderSessionForm extends UiFormDialog {
		UiSuggestBox sessionSuggestBox = new UiSuggestBox(new BmoSession());
		BmoOrderSession bmoOrderSession;
		BmoSession bmoSession = new BmoSession();
		int sessionTypeId;
		String orderId;

		public UiOrderSessionForm(UiParams uiParams, int id, int sessionTypeId) {
			super(uiParams, new BmoOrderSession(), id);
			bmoOrderSession = (BmoOrderSession)getBmObject();

			orderId = getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).getForceFilter().getValue();
			this.sessionTypeId = sessionTypeId;

			// Filtros suggestbox sesiones para no duplicar sesiones en un pedido
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setNotInFilter(bmoOrderSession.getKind(), bmoSession.getIdFieldName(), 
					bmoOrderSession.getSessionId().getName(), bmoOrderSession.getOrderId().getName(), orderId);
			sessionSuggestBox.addFilter(filterByOrder);

			// Filtrar sesiones del tipo de venta
			BmFilter filterBySessionType = new BmFilter();
			filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(), sessionTypeId);
			sessionSuggestBox.addFilter(filterBySessionType);

			// Filtrar sesiones disponibles
			BmFilter filterByAvailability = new BmFilter();
			filterByAvailability.setValueFilter(bmoSession.getKind(), bmoSession.getAvailable(), 1);
			sessionSuggestBox.addFilter(filterByAvailability);
		}

		// Creador desde seleccionador de sesion
		public UiOrderSessionForm(UiParams uiParams, int id, int sessionTypeId, int sessionId) {
			super(uiParams, new BmoOrderSession(), id);
			bmoOrderSession = (BmoOrderSession)getBmObject();

			orderId = getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).getForceFilter().getValue();
			this.sessionTypeId = sessionTypeId;

			// Filtros suggestbox sesiones para no duplicar sesiones en un pedido
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setNotInFilter(bmoOrderSession.getKind(), bmoSession.getIdFieldName(), 
					bmoOrderSession.getSessionId().getName(), bmoOrderSession.getOrderId().getName(), orderId);
			sessionSuggestBox.addFilter(filterByOrder);

			// Filtrar sesiones del tipo de venta
			BmFilter filterBySessionType = new BmFilter();
			filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(), sessionTypeId);
			sessionSuggestBox.addFilter(filterBySessionType);

			// Filtrar sesiones disponibles
			BmFilter filterByAvailability = new BmFilter();
			filterByAvailability.setValueFilter(bmoSession.getKind(), bmoSession.getAvailable(), 1);
			sessionSuggestBox.addFilter(filterByAvailability);

			try {
				bmoOrderSession.getSessionId().setValue(sessionId);
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "(): " + e.toString());
			}
		}

		@Override
		public void populateFields() {
			bmoOrderSession = (BmoOrderSession)getBmObject();
			formFlexTable.addField(1, 0, sessionSuggestBox, bmoOrderSession.getSessionId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderSession.setId(id);
			bmoOrderSession.getSessionId().setValue(sessionSuggestBox.getSelectedId());
			bmoOrderSession.getOrderId().setValue(orderId);		

			return bmoOrderSession;
		}

		@Override
		public void close() {
			list();
		}
	}

}