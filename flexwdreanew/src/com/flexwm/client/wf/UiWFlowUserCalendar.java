/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.flexwm.shared.wf.BmoWFlowUserBlockDate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;


public class UiWFlowUserCalendar extends UiCalendar {
	public static String UIMODULECODE = "WUCA";
	BmoWFlowUser bmoWFlowUser;
	BmoWFlowUserBlockDate bmoWFlowUserBlockDate = new BmoWFlowUserBlockDate();
	UiListBox userUiListBox;

	public UiWFlowUserCalendar(UiParams uiParams) {
		super(uiParams, new BmoWFlowUser());
		this.bmoWFlowUser = (BmoWFlowUser)getBmObject();
		dateFieldName = bmoWFlowUser.getBmoWFlow().getStartDate().getName();
	}

	public UiWFlowUserCalendar(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowUser());
		this.bmoWFlowUser = (BmoWFlowUser)getBmObject();
		dateFieldName = bmoWFlowUser.getBmoWFlow().getStartDate().getName();
	}

	@Override
	public void postShow() {
		if (!isMobile()) {
			// Filtrar categorias de Flujos por Modulo Proyecto
//			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
//			BmFilter filterWFlowCategory = new BmFilter();
//			filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoWFlowUser.getBmoWFlowType().getBmoWFlowCategory());
//
//			// Filtrar tipos de Flujos por Categoria Proyecto
//			BmoWFlowType bmoWFlowType = new BmoWFlowType();
//			BmFilter filterWFlowType = new BmFilter();
//			filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoWFlowUser.getBmoWFlowType());
//
//			// Filtrar fases por Categoría Proyecto
//			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
//			BmFilter filterWFlowPhase = new BmFilter();
//			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
//					bmoWFlowPhase.getWFlowCategoryId().getName(), 
//					bmoWFlowCategory.getIdFieldName(),
//					bmoWFlowCategory.getProgramId().getName(),
//					"" + bmObjectProgramId);
//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoWFlowUser.getBmoWFlow().getBmoWFlowPhase());

			// Filtrar por vendedores
			userUiListBox = new UiListBox(getUiParams(), new BmoUser());
			BmoUser bmoUser = new BmoUser();
//			BmoProfileUser bmoProfileUser = new BmoProfileUser();
//			BmFilter filterSalesmen = new BmFilter();
//			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
//			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
//					bmoUser.getIdFieldName(),
//					bmoProfileUser.getUserId().getName(),
//					bmoProfileUser.getProfileId().getName(),
//					"" + salesGroupId);
//
//			userUiListBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userUiListBox.addFilter(filterUserActive);

			addFilterListBox(userUiListBox, bmoWFlowUser.getBmoUser());

			//addStaticFilterListBox(new UiListBox(bmoWFlowUser.getStatus()), bmoWFlowUser, bmoWFlowUser.getStatus());	
		}

	}

	@Override
	public void create() {
		//UiWFlowUser uiWFlowUserForm = new UiWFlowUser(getUiParams());
		//getUiParams().setUiType(new BmoWFlowUser().getProgramCode(), UiParams.MASTER);
		//uiWFlowUserForm.create();
	}

	@Override
	public void open(BmObject bmObject) {

//		if (bmObject instanceof BmoWFlowUser){
//			bmoWFlowUser = (BmoWFlowUser)bmObject;
//			// Si esta asignado el tipo de proyecto, envia directo al dashboard
//			if (bmoWFlowUser.getWFlowTypeId().toInteger() > 0) {
//				UiWFlowUserDetail uiWFlowUserDetail = new UiWFlowUserDetail(getUiParams(), bmoWFlowUser.getId());
//				uiWFlowUserDetail.show();
//			} else {
//				UiWFlowUser uiWFlowUserForm = new UiWFlowUser(getUiParams());
//				uiWFlowUserForm.edit(bmoWFlowUser);
//			}
//		} else if (bmObject instanceof BmoWFlowUserBlockDate) {
//			bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)bmObject;
//			showSystemMessage("La Fecha está Bloqueada: " + bmoWFlowUserBlockDate.getComments().toString());
//		}
	}	

	@Override
	public void displayList() {	
		listBlockDates();
	}


	private void listBlockDates() {
		try {
			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-listBlockDates() ERROR: " + caught.toString());	
				}
				public void onSuccess(ArrayList<BmObject> result) {
					stopLoading();
					completeList(result.iterator());
				}
			};

			// Si es de tipo lista de detalle, no se maneja paginado
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().list(bmoWFlowUserBlockDate.getPmClass(), 
						null, 
						null, 
						"", 
						bmoWFlowUserBlockDate.getOrderFields(), -1, -1, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-listBlockDates() ERROR: " + e.toString());
		}
	}

	public void completeList(Iterator<BmObject> blockIterator) {
		calendar.suspendLayout();

		// Primero agrega fechas bloqueadas
		while (blockIterator.hasNext()) {
			BmoWFlowUserBlockDate bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)blockIterator.next();

			// Solo los agrega si no estan cancelados
			UiAppointment appt = new UiAppointment(bmoWFlowUserBlockDate);
			appt.setStart(new Date(bmoWFlowUserBlockDate.getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoWFlowUserBlockDate.getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoWFlowUserBlockDate.getBmoUser().getCode().toString() 
					+ "(" + bmoWFlowUserBlockDate.getComments().toString() + ") ");
			appt.setStyle(AppointmentStyle.RED);

			addAppointment(appt);

		}

		// Posteriormente agrega usuarios
		while (iterator.hasNext()) {
			BmoWFlowUser bmoWFlowUser = (BmoWFlowUser)iterator.next();
	    
			UiAppointment appt = new UiAppointment(bmoWFlowUser);
			appt.setStart(new Date(bmoWFlowUser.getBmoWFlow().getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoWFlowUser.getBmoWFlow().getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoWFlowUser.getBmoUser().getCode().toString() 
					+ " (" + bmoWFlowUser.getBmoWFlow().getName().toString() + ")" 
					+ " " + bmoWFlowUser.getBmoWFlow().getStartDate().toString());

			if (bmoWFlowUser.getBmoWFlow().getStatus().equals(BmoWFlow.STATUS_INACTIVE)) appt.setStyle(AppointmentStyle.GREY);
			else if (bmoWFlowUser.getBmoWFlow().getStatus().equals(BmoWFlow.STATUS_ACTIVE)) appt.setStyle(AppointmentStyle.ORANGE);
			else appt.setStyle(AppointmentStyle.GREEN);

			addAppointment(appt);

		}

		calendar.scrollToHour(8);

		calendar.resumeLayout();
	}
}