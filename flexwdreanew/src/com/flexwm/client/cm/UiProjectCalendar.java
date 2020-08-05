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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoOrderBlockDate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiProjectCalendar extends UiCalendar {
	BmoProject bmoProject;
	BmoOrderBlockDate bmoOrderBlockDate = new BmoOrderBlockDate();
	UiListBox userUiListBox;

	public UiProjectCalendar(UiParams uiParams) {
		super(uiParams, new BmoProject());
		this.bmoProject = (BmoProject)getBmObject();
		dateFieldName = bmoProject.getStartDate().getName();
	}

	public UiProjectCalendar(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoProject());
		this.bmoProject = (BmoProject)getBmObject();
		dateFieldName = bmoProject.getStartDate().getName();
	}

	@Override
	public void postShow() {
		if (!isMobile()) {
			// Filtrar categorias de Flujos por Modulo Proyecto
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoProject.getBmoWFlowType().getBmoWFlowCategory());

			// Filtrar tipos de Flujos por Categoria Proyecto
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter filterWFlowType = new BmFilter();
			filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoProject.getBmoWFlowType());

			// Filtrar fases por Categoría Proyecto
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
					bmoWFlowPhase.getWFlowCategoryId().getName(), 
					bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(),
					"" + bmObjectProgramId);
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoProject.getBmoWFlow().getBmoWFlowPhase());

			// Filtrar por vendedores
			userUiListBox = new UiListBox(getUiParams(), new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesGroupId);

			userUiListBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userUiListBox.addFilter(filterSalesmenActive);

			addFilterListBox(userUiListBox, bmoProject.getBmoUser());

			addStaticFilterListBox(new UiListBox(getUiParams(), bmoProject.getStatus()), bmoProject, bmoProject.getStatus());	
		}

	}

	@Override
	public void create() {
		UiProject uiProjectForm = new UiProject(getUiParams());
		getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
		uiProjectForm.create();
	}

	@Override
	public void open(BmObject bmObject) {

		if (bmObject instanceof BmoProject){
			bmoProject = (BmoProject)bmObject;
			// Si esta asignado el tipo de proyecto, envia directo al dashboard
			if (bmoProject.getWFlowTypeId().toInteger() > 0) {
				UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProject.getId());
				uiProjectDetail.show();
			} else {
				UiProject uiProjectForm = new UiProject(getUiParams());
				uiProjectForm.edit(bmoProject);
			}
		} else if (bmObject instanceof BmoOrderBlockDate) {
			bmoOrderBlockDate = (BmoOrderBlockDate)bmObject;
			showSystemMessage("La Fecha está Bloqueada: " + bmoOrderBlockDate.getComments().toString());
		}
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
				getUiParams().getBmObjectServiceAsync().list(bmoOrderBlockDate.getPmClass(), 
						null, 
						null, 
						"", 
						bmoOrderBlockDate.getOrderFields(), -1, -1, callback);	
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
			BmoOrderBlockDate bmoOrderBlockDate = (BmoOrderBlockDate)blockIterator.next();

			// Solo los agrega si no estan cancelados
			UiAppointment appt = new UiAppointment(bmoOrderBlockDate);
			appt.setStart(new Date(bmoOrderBlockDate.getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoOrderBlockDate.getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoOrderBlockDate.getComments().toString());
			appt.setStyle(AppointmentStyle.RED);

			addAppointment(appt);

		}

		// Posteriormente agrega proyectos
		while (iterator.hasNext()) {
			BmoProject bmoProject = (BmoProject)iterator.next();

			// Solo los agrega si no estan cancelados
			if (!bmoProject.getStatus().equals(BmoProject.STATUS_CANCEL)) {  		    
				UiAppointment appt = new UiAppointment(bmoProject);
				appt.setStart(new Date(bmoProject.getStartDate().toMilliseconds(getSFParams())));
				appt.setEnd(new Date(bmoProject.getEndDate().toMilliseconds(getSFParams())));
				appt.setTitle(bmoProject.getCode().toString() + " - " + bmoProject.getName().toString()
						+ " (" + bmoProject.getBmoVenue().getName().toString() + ")" + " " + bmoProject.getBmoUser().getCode().toString()
						+ " " + bmoProject.getStartDate().toString());

				if (bmoProject.getStatus().equals(BmoProject.STATUS_FINISHED)) appt.setStyle(AppointmentStyle.GREY);
				else if (bmoProject.getStatus().equals(BmoProject.STATUS_REVISION)) appt.setStyle(AppointmentStyle.ORANGE);
				else appt.setStyle(AppointmentStyle.GREEN);

				addAppointment(appt);
			}

		}

		calendar.scrollToHour(8);

		calendar.resumeLayout();
	}
}