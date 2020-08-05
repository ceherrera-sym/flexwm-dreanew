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
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiSession extends UiList {
	
	BmoSession bmoSession;

	public UiSession(UiParams uiParams) {
		super(uiParams, new BmoSession());
		bmoSession = (BmoSession)getBmObject();
	}

	public UiSession(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoSession());
		bmoSession = (BmoSession)getBmObject();
	}

	@Override
	public void postShow() {

		// Filtro por ubicaciones
		if (!isMobile()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoLocation()), new BmoLocation());
		}
		// Filtrar por instructores
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + salesGroupId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoUser(), filterSalesmen), new BmoUser());

		addFilterListBox(new UiListBox(getUiParams(), new BmoSessionType()), new BmoSessionType());
		addDateRangeFilterListBox(bmoSession.getStartDate());
	}

	@Override
	public void create() {
		UiSessionForm uiSessionForm = new UiSessionForm(getUiParams(), 0);
		uiSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSessionForm uiSessionForm = new UiSessionForm(getUiParams(), bmObject.getId());
		uiSessionForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {
		UiSessionForm uiSessionForm = new UiSessionForm(getUiParams(), bmObject.getId());
		uiSessionForm.show();
	}

	public class UiSessionForm extends UiFormDialog {
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();
		TextBox reservationsTextBox = new TextBox();
		CheckBox availableCheckBox = new CheckBox();
		UiSuggestBox userSuggestBox;
		UiListBox locationListBox = new UiListBox(getUiParams(), new BmoLocation());
		UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		
		
		CheckBox isSeriesCheckBox = new CheckBox();
		UiDateBox seriesStartDateBox = new UiDateBox();
		UiDateBox seriesEndDateBox = new UiDateBox();
		CheckBox seriesApplyAllCheckBox = new CheckBox();
		CheckBox seriesMondayCheckBox = new CheckBox();
		CheckBox seriesTuesdayCheckBox = new CheckBox();
		CheckBox seriesWednesdayCheckBox = new CheckBox();
		CheckBox seriesThursdayCheckBox = new CheckBox();
		CheckBox seriesFridayCheckBox = new CheckBox();
		CheckBox seriesSaturdayCheckBox = new CheckBox();
		CheckBox seriesSundayCheckBox = new CheckBox();
		HorizontalPanel seriesDaysPanel = new HorizontalPanel();

		BmoSession bmoSession;

		String generalSection = "Datos Generales";
		String itemSection = "Asistentes";
		String repeatSeriesSection = "Repetir en Serie";

		SessionUpdater sessionUpdater = new SessionUpdater();

		public UiSessionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSession(), id);

			// Filtrar por instructores
			userSuggestBox = new UiSuggestBox(new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterInstructors = new BmFilter();
			int instructorGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterInstructors.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + instructorGroupId);	
			userSuggestBox.addFilter(filterInstructors);

			// Filtrar por instructores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);

			// Crear panel checkboxes dia serie repeticion
			Label l = new Label("Repetir en Días:");
			l.setStyleName("formLabelField");
			seriesDaysPanel.add(l);
			seriesDaysPanel.add(seriesMondayCheckBox);
			seriesDaysPanel.add(new Label("Lun"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesTuesdayCheckBox);
			seriesDaysPanel.add(new Label("Mar"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesWednesdayCheckBox);
			seriesDaysPanel.add(new Label("Mie"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesThursdayCheckBox);
			seriesDaysPanel.add(new Label("Jue"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesFridayCheckBox);
			seriesDaysPanel.add(new Label("Vie"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesSaturdayCheckBox);
			seriesDaysPanel.add(new Label("Sab"));
			seriesDaysPanel.add(new HTML("&nbsp;&nbsp;"));

			seriesDaysPanel.add(seriesSundayCheckBox);
			seriesDaysPanel.add(new Label("Dom"));
		}

		@Override
		public void populateFields() {
			bmoSession = (BmoSession)getBmObject();

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, sessionTypeListBox, bmoSession.getSessionTypeId());
			formFlexTable.addField(3, 0, userSuggestBox, bmoSession.getUserId());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoSession.getDescription());
			formFlexTable.addFieldReadOnly(5, 0, reservationsTextBox, bmoSession.getReservations());
			formFlexTable.addField(6, 0, availableCheckBox, bmoSession.getAvailable());
			availableCheckBox.setEnabled(false);
			formFlexTable.addField(7, 0, startDateBox, bmoSession.getStartDate());
			formFlexTable.addField(8, 0, endDateBox, bmoSession.getEndDate());
			formFlexTable.addField(9, 0, companyListBox, bmoSession.getCompanyId());
			
			endDateBox.setEnabled(false);

			formFlexTable.addSectionLabel(10, 0, "Repetir en Serie", 2);
			formFlexTable.addField(11, 0, isSeriesCheckBox, bmoSession.getIsSeries());
			formFlexTable.addField(12, 0, seriesStartDateBox, bmoSession.getSeriesStart());
			formFlexTable.addField(13, 0, seriesEndDateBox, bmoSession.getSeriesEnd());	
			setSeriesDaysValues();
			formFlexTable.addPanel(14, 0, seriesDaysPanel);
			formFlexTable.addField(15, 0, seriesApplyAllCheckBox, bmoSession.getSeriesApplyAll());

			if (!newRecord) {

				//				TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
				//				tabPanel.setSize("100%", "400px");
				//				formFlexTable.addPanel(13, 0, tabPanel);
				//
				//				FlowPanel customerSessionPanel = new FlowPanel();
				//				customerSessionPanel.setSize("100%", "100%");
				//				ScrollPanel customerSessionScrollPanel = new ScrollPanel();
				//				customerSessionScrollPanel.setSize("98%", "355px");
				//				customerSessionScrollPanel.add(customerSessionPanel);
				//				UiOrderSession uiCustomerSession = new UiOrderSession(getUiParams(), customerSessionPanel, id);
				//				uiCustomerSession.show();
				//				tabPanel.add(customerSessionScrollPanel, "Asistentes");


				// Items
				formFlexTable.addSectionLabel(16, 0, itemSection, 2);
				BmoOrderSession bmoOrderSession = new BmoOrderSession();
				FlowPanel orderSessionFP = new FlowPanel();
				BmFilter filterOrderSession = new BmFilter();
				filterOrderSession.setValueFilter(bmoOrderSession.getKind(), bmoOrderSession.getSessionId(), bmoSession.getId());
				getUiParams().setForceFilter(bmoOrderSession.getProgramCode(), filterOrderSession);
				UiOrderSession uiOrderSession = new UiOrderSession(getUiParams(), orderSessionFP, bmoSession, bmoSession.getId(), sessionUpdater);
				setUiType(bmoOrderSession.getProgramCode(), UiParams.MINIMALIST);
				uiOrderSession.show();
				formFlexTable.addPanel(17, 0, orderSessionFP, 2);
			}

			statusEffect();
		}

		private void setSeriesDaysValues() {
			seriesMondayCheckBox.setValue(bmoSession.getSeriesMonday().toBoolean());
			seriesTuesdayCheckBox.setValue(bmoSession.getSeriesTuesday().toBoolean());
			seriesWednesdayCheckBox.setValue(bmoSession.getSeriesWednesday().toBoolean());
			seriesThursdayCheckBox.setValue(bmoSession.getSeriesThursday().toBoolean());
			seriesFridayCheckBox.setValue(bmoSession.getSeriesFriday().toBoolean());
			seriesSaturdayCheckBox.setValue(bmoSession.getSeriesSaturday().toBoolean());
			seriesSundayCheckBox.setValue(bmoSession.getSeriesSunday().toBoolean());
		}

		private void statusEffect() {

			if (!newRecord) {
				isSeriesCheckBox.setEnabled(false);
				seriesStartDateBox.setEnabled(false);
				seriesEndDateBox.setEnabled(false);

				seriesMondayCheckBox.setEnabled(false);
				seriesTuesdayCheckBox.setEnabled(false);
				seriesWednesdayCheckBox.setEnabled(false);
				seriesThursdayCheckBox.setEnabled(false);
				seriesFridayCheckBox.setEnabled(false);
				seriesSaturdayCheckBox.setEnabled(false);
				seriesSundayCheckBox.setEnabled(false);
			}

			if (isSeriesCheckBox.getValue()) {
				Date startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateTimeFormat()).parse(startDateBox.getDateTime());
				seriesStartDateBox.setValue(startDate);
			}
		}

		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			if (event.getSource() == isSeriesCheckBox)
				statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			calculateEndDate();
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateBox) {
				calculateEndDate();
				statusEffect();
			}
		}

		private void calculateEndDate() {		
			// Calcular fecha de pago
			BmoSessionType bmoSessionType = (BmoSessionType)sessionTypeListBox.getSelectedBmObject();
			if (bmoSessionType == null && bmoSession.getSessionTypeId().toInteger() > 0) {
				bmoSessionType = bmoSession.getBmoSessionType();
			}
			if (bmoSessionType != null) {
				if (!startDateBox.getTextBox().getValue().equals("")) {
					Date startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateTimeFormat()).parse(startDateBox.getDateTime());
					Date endDate = new Date(startDate.getTime() + (bmoSessionType.getDuration().toInteger() * 60000));

					endDateBox.setDateTime(GwtUtil.dateToString(endDate, getSFParams().getDateTimeFormat()));
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSession.setId(id);
			bmoSession.getDescription().setValue(descriptionTextArea.getText());
			bmoSession.getStartDate().setValue(startDateBox.getDateTime());
			bmoSession.getEndDate().setValue(endDateBox.getDateTime());		
			bmoSession.getReservations().setValue(reservationsTextBox.getText());
			bmoSession.getAvailable().setValue(availableCheckBox.getValue());
			bmoSession.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoSession.getSessionTypeId().setValue(sessionTypeListBox.getSelectedId());

			bmoSession.getIsSeries().setValue(isSeriesCheckBox.getValue());
			bmoSession.getSeriesStart().setValue(seriesStartDateBox.getTextBox().getText());
			bmoSession.getSeriesEnd().setValue(seriesEndDateBox.getTextBox().getText());
			bmoSession.getSeriesApplyAll().setValue(seriesApplyAllCheckBox.getValue());
			bmoSession.getSeriesMonday().setValue(seriesMondayCheckBox.getValue());
			bmoSession.getSeriesTuesday().setValue(seriesTuesdayCheckBox.getValue());
			bmoSession.getSeriesWednesday().setValue(seriesWednesdayCheckBox.getValue());
			bmoSession.getSeriesThursday().setValue(seriesThursdayCheckBox.getValue());
			bmoSession.getSeriesFriday().setValue(seriesFridayCheckBox.getValue());
			bmoSession.getSeriesSaturday().setValue(seriesSaturdayCheckBox.getValue());
			bmoSession.getSeriesSunday().setValue(seriesSundayCheckBox.getValue());
			bmoSession.getCompanyId().setValue(companyListBox.getSelectedId());
			
			return bmoSession;
		}

		@Override
		public void close() {
			list();
		}

		public class SessionUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}