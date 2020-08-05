/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import com.flexwm.shared.op.BmoConsultancy;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.DateTimeConstants;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;

@SuppressWarnings("deprecation")
public class UiConsultancyActiveWeekList extends UiList {
	BmoConsultancy bmoConsultancy;
	UiSuggestBox userSuggestBox;
	protected BmFilter userFilter = new BmFilter();

	BmoUser bmoUser = new BmoUser();

	public UiConsultancyActiveWeekList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoConsultancy());
		bmoConsultancy = (BmoConsultancy)getBmObject();	

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");
	}

	public UiConsultancyActiveWeekList(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoConsultancy());
		bmoConsultancy = (BmoConsultancy)getBmObject();	
		this.bmoUser = bmoUser;		

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}
	
	public String getFirstDayOfTheWeek() {
		String firstDayOfTheWeek = "";
		Date today = new Date();
		// Dia de la semana inicia en domingo
		DateTimeConstants constants = LocaleInfo.getCurrentLocale().getDateTimeConstants();
		int firstDay = Integer.parseInt(constants.firstDayOfTheWeek()) - 1;
		int offset = firstDay - today.getDay();
		today.setDate(today.getDate() + offset);
		firstDayOfTheWeek = GwtUtil.dateToString(today, getSFParams().getDateFormat());
		return firstDayOfTheWeek;
	}
	
	public String getLastDayOfTheWeek() {
		String firstDayOfTheWeek = "";
		Date today = new Date();
		DateTimeConstants constants = LocaleInfo.getCurrentLocale().getDateTimeConstants();
		// Dia de la semana inicia en domingo
		int firstDay = Integer.parseInt(constants.firstDayOfTheWeek()) - 1;
		int offset = firstDay - today.getDay();
		// Sumar 6 dias para finalice el dia sabado
		today.setDate((today.getDate() + offset) + 6);
		firstDayOfTheWeek = GwtUtil.dateToString(today, getSFParams().getDateFormat());

		return firstDayOfTheWeek;
	}
	
	// Ordenar por Fecha desc
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(bmoConsultancy.getKind(), bmoConsultancy.getStartDate(), BmOrder.DESC)));
	}

	@Override
	public void postShow() {
		
		// Filtro de Pedidos de la semana
		BmFilter orderByWeek = new BmFilter();
		orderByWeek.setRangeFilter(bmoConsultancy.getKind(), 
				bmoConsultancy.getStartDate().getName(),
				bmoConsultancy.getStartDate().getLabel(), 
				getFirstDayOfTheWeek(), 
				getLastDayOfTheWeek());
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(orderByWeek);
		
		// Reasigna campos ordenamiento
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setOrderList(getOrderFields());
				
		// Filtro de Pedidos En Revision
//		BmFilter revisionFilter = new BmFilter();
//		revisionFilter.setValueFilter(bmoOrder.getKind(), 
//				bmoOrder.getStatus(), 
//				"" + BmoOpportunity.STATUS_REVISION);
//		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(revisionFilter);

		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
					bmoConsultancy.getUserId().getName(), 
					bmoConsultancy.getUserId().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}
	}

	@Override
	public void create() {
		UiConsultancy uiConsultancy = new UiConsultancy(getUiParams());
		uiConsultancy.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoConsultancy = (BmoConsultancy)bmObject;
		getUiParams().setUiType(new BmoConsultancy().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoConsultancy.getWFlowTypeId().toInteger() > 0) {
			UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), bmoConsultancy.getId());
			uiConsultancyDetail.show();
		}
	}
}
