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

import java.util.Date;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiOrderSessionCalendar extends UiCalendar {
	BmoOrderSession bmoOrderSession;
	UiListBox userUiListBox;
	
	public UiOrderSessionCalendar(UiParams uiParams) {
		super(uiParams, new BmoOrderSession());
		this.bmoOrderSession = (BmoOrderSession)getBmObject();
		dateFieldName = bmoOrderSession.getBmoSession().getStartDate().getName();
	}
	
	@Override
	public void postShow() {
		// Filtrar por instructores
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
		
		// Filtrar por instructores activos
		BmFilter filterSalesmenActive = new BmFilter();
		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		userUiListBox.addFilter(filterSalesmenActive);
		
		addFilterListBox(userUiListBox, bmoOrderSession.getBmoSession().getBmoUser());
		
		newImage.setVisible(false);
	}
		
	@Override
	public void create() {
//		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), 0);
//		uiOrderSessionForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoOrderSession = (BmoOrderSession)bmObject;
		
		showSystemMessage("el modulo es: " + getBmObject().getProgramCode());

//		UiOrderSessionForm uiOrderSessionForm = new UiOrderSessionForm(getUiParams(), bmoOrderSession.getId());
//		uiOrderSessionForm.show();
	}	
	
	@Override
	protected void moveAppointment(UiAppointment uiAppointment) {
		bmoOrderSession = (BmoOrderSession)uiAppointment.getBmObject();
		try {
			bmoOrderSession.getBmoSession().getStartDate().setValue(GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()));
			bmoOrderSession.getBmoSession().getEndDate().setValue(GwtUtil.dateToString(uiAppointment.getEnd(), getSFParams().getDateTimeFormat()));
			save(bmoOrderSession);
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-moveAppointment() ERROR: " + e.toString());
		}
	}
	
	@Override
	public void displayList() {	
		calendar.suspendLayout();
		while (iterator.hasNext()) {
		    BmoOrderSession bmoOrderSession = (BmoOrderSession)iterator.next();
			UiAppointment appt = new UiAppointment(bmoOrderSession);
			appt.setStart(new Date(bmoOrderSession.getBmoSession().getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoOrderSession.getBmoSession().getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoOrderSession.getBmoSession().getBmoUser().getCode().toString() + 
					" - " + bmoOrderSession.getBmoSession().getStartDate().toString() +
					" - " +  bmoOrderSession.getBmoSession().getBmoSessionType().getName().toString());
		
			if (bmoOrderSession.getBmoSession().getReservations().toInteger() < bmoOrderSession.getBmoSession().getBmoSessionType().getCapacity().toInteger()) appt.setStyle(AppointmentStyle.GREEN);
			else if (bmoOrderSession.getBmoSession().getReservations().toInteger() == bmoOrderSession.getBmoSession().getBmoSessionType().getCapacity().toInteger()) appt.setStyle(AppointmentStyle.RED);
			addAppointment(appt);
		}
		calendar.scrollToHour(8);
		calendar.resumeLayout();
	}
}