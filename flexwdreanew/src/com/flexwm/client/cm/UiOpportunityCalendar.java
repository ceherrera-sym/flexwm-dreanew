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

import java.util.Date;

import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiOpportunityCalendar extends UiCalendar {
	BmoOpportunity bmoOpportunity;
	BmoVenue bmoVenue;
	
	public UiOpportunityCalendar(UiParams uiParams) {
		super(uiParams, new BmoOpportunity());
		this.bmoOpportunity = (BmoOpportunity)getBmObject();
		dateFieldName = bmoOpportunity.getStartDate().getName();
	}
	
	@Override
	public void postShow() {
		// Filtrar tipos de Flujos por Categoria Oportunidad
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoOpportunity.getBmoWFlowType());
		
		// Filtrar fases por Categoría Oportunidad
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(),
				"" + bmObjectProgramId);
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoOpportunity.getBmoWFlow().getBmoWFlowPhase());
		
		// Filtrar por vendedores
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
								bmoUser.getIdFieldName(),
								bmoProfileUser.getUserId().getName(),
								bmoProfileUser.getProfileId().getName(),
								"" + salesGroupId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoUser(), filterSalesmen), bmoOpportunity.getBmoUser());
		
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoOpportunity.getStatus()), bmoOpportunity, bmoOpportunity.getStatus());	
	}
		
	@Override
	public void create() {
		UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
		uiOpportunity.create();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoOpportunity = (BmoOpportunity)bmObject;
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
			UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOpportunity.getId());
			uiOpportunityDetail.show();
		} else {
			UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
			uiOpportunity.edit(bmoOpportunity);
		}
	}	
	
	@Override
	protected void moveAppointment(UiAppointment uiAppointment) {
		bmoOpportunity = (BmoOpportunity)uiAppointment.getBmObject();
		try {
			bmoOpportunity.getStartDate().setValue(GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()));
			bmoOpportunity.getEndDate().setValue(GwtUtil.dateToString(uiAppointment.getEnd(), getSFParams().getDateTimeFormat()));
			save(bmoOpportunity);
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-moveAppointment() ERROR: " + e.toString());
		}
	}
	
	@Override
	public void displayList() {	
		calendar.suspendLayout();
		while (iterator.hasNext()) {
		    BmoOpportunity bmoOpportunity = (BmoOpportunity)iterator.next();
			UiAppointment appt = new UiAppointment(bmoOpportunity);
			appt.setStart(new Date(bmoOpportunity.getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoOpportunity.getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoOpportunity.getCode().toString() + " - " + bmoOpportunity.getName().toString() + " (" + bmoOpportunity.getBmoVenue().getName().toString() + ") " 
				+ bmoOpportunity.getBmoUser().getCode().toString() + " "
				+ bmoOpportunity.getStartDate().toString());
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_REVISION || bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_HOLD ) appt.setStyle(AppointmentStyle.YELLOW);
			else if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST) appt.setStyle(AppointmentStyle.GREY);
			else appt.setStyle(AppointmentStyle.GREEN);
			addAppointment(appt);
		}
		calendar.scrollToHour(8);
		calendar.resumeLayout();
	}
}