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
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiActionHandler;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiSessionSelectorCalendar extends UiCalendar {
	BmoSession bmoSession;
	BmoSessionSale bmoSessionSale;
	BmoOrderSession bmoOrderSession = new BmoOrderSession();
	String orderId;
	BmoSessionType bmoSessionType;
	UiActionHandler uiActionHandler;
	UiListBox userFilterListBox;

	// Dialogo de agregar sesion
	DialogBox sessionDialogBox;

	// Dialogo de agregar pedido sesion
	DialogBox orderSessionDialogBox;

	public UiSessionSelectorCalendar(UiParams uiParams, BmoSessionType bmoSessionType) {
		super(uiParams, new BmoSession());
		this.bmoSession = (BmoSession) getBmObject();
		this.bmoSessionType = bmoSessionType;

		dateFieldName = bmoSession.getStartDate().getName();
		orderId = getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).getForceFilter().getValue();
	}

	public UiSessionSelectorCalendar(UiParams uiParams, Panel defaultPanel, BmoSessionSale bmoSessionSale ,BmoSessionType bmoSessionType,
			UiActionHandler uiActionHandler) {
		super(uiParams, defaultPanel, new BmoSession());
		this.bmoSession = (BmoSession) getBmObject();
		this.bmoSessionType = bmoSessionType;
		this.bmoSessionSale = bmoSessionSale;
		this.uiActionHandler = uiActionHandler;

		dateFieldName = bmoSession.getStartDate().getName();
		orderId = getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).getForceFilter().getValue();
	}

	@Override
	public void postShow() {
		// Filtros suggestbox sesiones para no mostrar y duplicar sesiones en un
		// pedido
		BmFilter filterByOrder = new BmFilter();
		filterByOrder.setNotInFilter(bmoOrderSession.getKind(), bmoSession.getIdFieldName(),
				bmoOrderSession.getSessionId().getName(), bmoOrderSession.getOrderId().getName(), orderId);
		//getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterByOrder);

		// Filtrar sesiones del tipo de venta
		BmFilter filterBySessionType = new BmFilter();
		filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(), bmoSessionType.getId());
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterBySessionType);

		// Filtrar sesiones disponibles
		BmFilter filterByAvailability = new BmFilter();
		filterByAvailability.setValueFilter(bmoSession.getKind(),bmoSession.getAvailable(), "1");
		//getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterByAvailability);

		// Filtrar por instructores
		userFilterListBox = new UiListBox(getUiParams(), new BmoUser());
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig) getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId()
				.toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), bmoUser.getIdFieldName(), bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(), "" + salesGroupId);
		userFilterListBox.addFilter(filterSalesmen);

		// Filtrar por instructores activos
		BmFilter filterSalesmenActive = new BmFilter();
		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		userFilterListBox.addFilter(filterSalesmenActive);

		addFilterListBox(userFilterListBox, bmoSession.getBmoUser());
	}

	@Override
	public void create() {
		UiSession uiSessionForm = new UiSession(getUiParams());		
		uiSessionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoSession = (BmoSession) bmObject;

		if (bmoSession.getAvailable().toBoolean())
			addOrderSession(bmoSession);
		else
			showSystemMessage("La Sesión seleccionada no está Disponible.");
	}

	@Override
	protected void moveAppointment(UiAppointment uiAppointment) {

		if (Window.confirm("Esta seguro que desea mover la Sesión?")) {
			bmoSession = (BmoSession) uiAppointment.getBmObject();
			try {
				bmoSession.getStartDate()
				.setValue(GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()));
				bmoSession.getEndDate()
				.setValue(GwtUtil.dateToString(uiAppointment.getEnd(), getSFParams().getDateTimeFormat()));

				save(bmoSession);

				// Cambia datos del appointment
				uiAppointment.setTitle(bmoSession.getBmoUser().getCode().toString() + " - "
						+ GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()) + " - "
						+ bmoSession.getBmoSessionType().getName().toString());
				displayList();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-moveAppointment() ERROR: " + e.toString());
			}
		} else {
			reset();
		}
	}

	@Override
	protected void emptySelection(TimeBlockClickEvent<Date> event) {
		if (!bmoSessionSale.getSessionDemo().toBoolean())
			addSession(event.getTarget());
		else 
			showSystemMessage("Solo puede existir una clase de prueba");
	}

	@Override
	public void displayList() {
		calendar.suspendLayout();
		while (iterator.hasNext()) {		
			BmoSession bmoSession = (BmoSession) iterator.next();
			UiAppointment appt = new UiAppointment(bmoSession);
			appt.setStart(new Date(bmoSession.getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoSession.getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoSession.getBmoUser().getCode().toString() + " - " + bmoSession.getStartDate().toString()
					+ " - " + bmoSession.getBmoSessionType().getName().toString());

			if (bmoSession.getAvailable().toBoolean())
				appt.setStyle(AppointmentStyle.GREEN);
			else
				appt.setStyle(AppointmentStyle.RED);
			addAppointment(appt);
		}
		calendar.scrollToHour(8);
		calendar.resumeLayout();
	}

	// Agrega sesion
	public void addSession(Date startDate) {
		sessionDialogBox = new DialogBox(true);
		sessionDialogBox.setGlassEnabled(true);
		sessionDialogBox.setText("Crear Sesión");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "320px");

		sessionDialogBox.setWidget(vp);

		UiSessionSelectorForm sessionSelectorForm = new UiSessionSelectorForm(getUiParams(), vp, startDate, orderId);
		sessionSelectorForm.show();

		sessionDialogBox.center();
		sessionDialogBox.show();
	}

	// Agrega sesion al pedido
	public void addOrderSession(BmoSession bmoSession) {
		orderSessionDialogBox = new DialogBox(true);
		orderSessionDialogBox.setGlassEnabled(true);
		orderSessionDialogBox.setText("Asignar Sesión");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "100px");

		orderSessionDialogBox.setWidget(vp);

		UiOrderSessionSelectorForm orderSessionSelectorForm = new UiOrderSessionSelectorForm(getUiParams(), vp,
				bmoSession);
		orderSessionSelectorForm.show();

		orderSessionDialogBox.center();
		orderSessionDialogBox.show();
	}

	// Reset de la lista de sesiones del pedido
	public void reset() {
		super.list();
		uiActionHandler.action();
	}

	// Obtiene objeto del servicio
	public void getSessionSale(int id){

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getSessionSale() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				setBmObject(result);					
			}
		};

		// Llamada al servicio RPC
		try {
			getUiParams().getBmObjectServiceAsync().get(getBmObject().getPmClass(), id, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getSessionSale() ERROR: " + e.toString());
		}
	}

	// Agrega una sesion
	private class UiSessionSelectorForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();
		TextBox reservationsTextBox = new TextBox();
		CheckBox availableCheckBox = new CheckBox();
		UiSuggestBox userSuggestBox;
		UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
		// AutoAsignar
		CheckBox autoAssignCheckBox = new CheckBox();

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

		private BmoSession bmoSession = new BmoSession();
		private Button saveButton = new Button("Guardar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private Date startDate;
		private String orderId;

		public UiSessionSelectorForm(UiParams uiParams, Panel defaultPanel, Date startDate, String orderId) {
			super(uiParams, defaultPanel);
			this.startDate = startDate;
			this.orderId = orderId;

			// Acciones lista
			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			sessionTypeListBox.addChangeHandler(listChangeHandler);

			// Acciones cambio de fecha
			ValueChangeHandler<Date> dateChangeHandler = new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					formDateChange(event);
				}
			};
			startDateBox.addValueChangeHandler(dateChangeHandler);
			startDateBox.getHourListBox().addChangeHandler(listChangeHandler);
			startDateBox.getMinuteListBox().addChangeHandler(listChangeHandler);

			//En Serie
			isSeriesCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					isSeries();
				}
			});

			// Filtrar por instructores
			userSuggestBox = new UiSuggestBox(new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterInstructors = new BmFilter();
			int instructorGroupId = ((BmoFlexConfig) getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();

			if (bmoSessionSale.getBmoSessionTypePackage().getProfileId().toInteger() > 0)
				instructorGroupId = bmoSessionSale.getBmoSessionTypePackage().getProfileId().toInteger();


			filterInstructors.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + instructorGroupId);
			//userSuggestBox.addFilter(filterInstructors);

			// Filtrar por instructores activos
			/*BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);*/

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoSession.getProgramCode()))
				saveButton.setVisible(true);

			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterInstructors = new BmFilter();
			int instructorGroupId = ((BmoFlexConfig) getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();

			if (bmoSessionSale.getBmoSessionTypePackage().getProfileId().toInteger() > 0)
				instructorGroupId = bmoSessionSale.getBmoSessionTypePackage().getProfileId().toInteger();
			filterInstructors.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + instructorGroupId);
			userSuggestBox.addFilter(filterInstructors);

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

			try {
				bmoSession.getSessionTypeId().setValue(bmoSessionType.getId());
				bmoSession.setBmoSessionType(bmoSessionType);

				bmoSession.getStartDate().setValue(GwtUtil.dateToString(startDate, getSFParams().getDateTimeFormat()));
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
			}

			formTable.addField(1, 0, sessionTypeListBox, bmoSession.getSessionTypeId());
			sessionTypeListBox.setEnabled(false);

			formTable.addField(2, 0, userSuggestBox, bmoSession.getUserId());

			formTable.addField(3, 0, descriptionTextArea, bmoSession.getDescription());

			formTable.addFieldReadOnly(4, 0, reservationsTextBox, bmoSession.getReservations());
			formTable.addField(5, 0, availableCheckBox, bmoSession.getAvailable());
			availableCheckBox.setEnabled(false);

			formTable.addField(6, 0, startDateBox, bmoSession.getStartDate());
			formTable.addField(7, 0, endDateBox, bmoSession.getEndDate());
			endDateBox.setEnabled(false);

			formTable.addField(8, 0, autoAssignCheckBox, bmoSession.getAutoAssign());

			formTable.addField(9, 0, isSeriesCheckBox, bmoSession.getIsSeries());

			formTable.addField(10, 0, seriesStartDateBox, bmoSession.getSeriesStart());
			formTable.addField(11, 0, seriesEndDateBox, bmoSession.getSeriesEnd());

			//setSeriesDaysValues();
			formTable.addPanel(12, 0, seriesDaysPanel);

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		private void statusEffect() {
			seriesStartDateBox.setEnabled(false);
			seriesEndDateBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			setSeriesDaysDiseabled();

			if (!(bmoSession.getId() > 0)) {
				userSuggestBox.setEnabled(true);
			}

			if (isSeriesCheckBox.getValue()) {
				Date startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateTimeFormat()).parse(startDateBox.getDateTime());
				seriesStartDateBox.setValue(startDate);
			}
			calculateEndDate();
		}

		public void formListChange(ChangeEvent event) {
			checkDates(bmoSessionSale.getStartDate().toString(), startDateBox.getTextBox().getText());
			calculateEndDate();
		}

		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateBox) {	
				checkDates(bmoSessionSale.getStartDate().toString(), startDateBox.getTextBox().getText());
				calculateEndDate();
			}	
		}

		public void isSeries() {			
			if(isSeriesCheckBox.getValue().toString().equals("true")) {
				seriesStartDateBox.setEnabled(false);
				seriesEndDateBox.setEnabled(true);				
				setSeriesDaysEnabled();
				seriesStartDateBox.getTextBox().setText(startDateBox.getTextBox().getText().substring(0,10));

				// Poner ultimo dia de mes
				lastDayMonth();
			} else {
				seriesStartDateBox.getTextBox().setText("");
				seriesEndDateBox.getTextBox().setText("");
				seriesStartDateBox.setEnabled(false);
				seriesEndDateBox.setEnabled(false);
				setSeriesDaysDiseabled();
			}

		}

		// Poner ultimo dia de mes
		@SuppressWarnings("deprecation")
		private void lastDayMonth() {
			if (seriesEndDateBox.getTextBox().getValue().equals("")) {
				Date date = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).parse(seriesStartDateBox.getTextBox().getText());
				date.setDate(1);
				date.setMonth(date.getMonth()+1);
				CalendarUtil.addDaysToDate(date, -1);
				seriesEndDateBox.getDatePicker().setValue(date);
				seriesEndDateBox.getTextBox().setValue(GwtUtil.dateToString(date, getSFParams().getDateFormat()));
			}
		}

		private void calculateEndDate() {
			// Calcular fecha de pago
			BmoSessionType bmoSessionType = (BmoSessionType) sessionTypeListBox.getSelectedBmObject();
			if (bmoSessionType == null && bmoSession.getSessionTypeId().toInteger() > 0) {
				bmoSessionType = bmoSession.getBmoSessionType();
			}
			if (bmoSessionType != null) {
				if (!startDateBox.getTextBox().getValue().equals("")) {
					Date startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateTimeFormat())
							.parse(startDateBox.getDateTime());
					Date endDate = new Date(startDate.getTime() + (bmoSessionType.getDuration().toInteger() * 60000));

					endDateBox.setDateTime(GwtUtil.dateToString(endDate, getSFParams().getDateTimeFormat()));
				}
			}
		}

		public void prepareSave() {
			try {
				bmoSession = new BmoSession();
				bmoSession.getDescription().setValue(descriptionTextArea.getText());
				bmoSession.getStartDate().setValue(startDateBox.getDateTime());
				bmoSession.getEndDate().setValue(endDateBox.getDateTime());
				bmoSession.getReservations().setValue(reservationsTextBox.getText());
				bmoSession.getAvailable().setValue(availableCheckBox.getValue());
				bmoSession.getUserId().setValue(userSuggestBox.getSelectedId());
				bmoSession.getSessionTypeId().setValue(sessionTypeListBox.getSelectedId());
				bmoSession.getAutoAssign().setValue(autoAssignCheckBox.getValue());

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


				//Validar las fechas
				//checkDates(bmoSessionSale.getStartDate().toString(), startDateBox.getTextBox().getText());



				if (bmoSession.getIsSeries().toBoolean()) {
					//checkSeries(bmoSession, bmoSessionSale.getO);
					//Obtener las sesiones por semana
					int noSession = bmoSessionSale.getBmoSessionTypePackage().getSessions().toInteger();
					int days = 0;

					if (bmoSession.getSeriesSunday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesMonday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesTuesday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesWednesday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesThursday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesFriday().toBoolean()) days += 1; 
					if (bmoSession.getSeriesSaturday().toBoolean()) days += 1;

					if (days > noSession) {
						showErrorMessage("El número de sesiones es mayor a las del paquete");
					} else {
						save();
					}

				} else {				
					save();
				}

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}


		private void setSeriesDaysEnabled() {
			seriesMondayCheckBox.setEnabled(true);
			seriesTuesdayCheckBox.setEnabled(true);
			seriesWednesdayCheckBox.setEnabled(true);
			seriesThursdayCheckBox.setEnabled(true);
			seriesFridayCheckBox.setEnabled(true);
			seriesSaturdayCheckBox.setEnabled(true);
			seriesSundayCheckBox.setEnabled(true);
		}

		private void setSeriesDaysDiseabled() {
			seriesMondayCheckBox.setEnabled(false);
			seriesTuesdayCheckBox.setEnabled(false);
			seriesWednesdayCheckBox.setEnabled(false);
			seriesThursdayCheckBox.setEnabled(false);
			seriesFridayCheckBox.setEnabled(false);
			seriesSaturdayCheckBox.setEnabled(false);
			seriesSundayCheckBox.setEnabled(false);
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					//AutoAssignar					
					if (bmoSession.getAutoAssign().toBoolean()) {
						int sessionId = result.getId();					
						autoassign(sessionId, orderId);						
					}

					processUpdateResult(result);


				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoSession.getPmClass(), bmoSession, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		public void autoassign(int sessionId, String orderId) {			
			String actionValues = sessionId + "|" + orderId;

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-autoassign() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showErrorMessage("Existen Errores " + result.errorsToString());
					} else {
						showSystemMessage("La Sesión Asignada con Exito");
					}
					processUpdateResult(result);
				}
			};

			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_AUTOASSIGN, "" + actionValues, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-autoassign() ERROR: " + e.toString());
			}
		}

		public void checkDates(String saleDate, String sessDate) {			
			String actionValues = saleDate + "|" + sessDate;

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(this.getClass().getName() + "-getOrdeBalance() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					if (result.hasErrors()) {
						stopLoading();
						showErrorMessage("Existen Errores " + result.errorsToString());
						sessionDialogBox.hide();
						reset();
					} 					
				}
			};

			try {	
				if (!isLoading()) {					
					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_CHECKDATES, "" + actionValues, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-checkDate() ERROR: " + e.toString());
			}
		}

		//		private void getReservations(String saleDate, String sessDate) {			
		//			String actionValues = saleDate + "|" + sessDate;
		//			
		//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
		//				@Override
		//				public void onFailure(Throwable caught) {
		//					showErrorMessage(this.getClass().getName() + "-getOrdeBalance() ERROR: " + caught.toString());
		//				}
		//
		//				@Override
		//				public void onSuccess(BmUpdateResult result) {
		//					if (result.hasErrors()) {
		//						stopLoading();
		//						showErrorMessage("Existen Errores " + result.errorsToString());
		//						sessionDialogBox.hide();
		//						reset();
		//					} 					
		//				}
		//			};
		//			
		//			try {	
		//				if (!isLoading()) {					
		//					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_CHECKDATES, "" + actionValues, callback);
		//				}
		//			} catch (SFException e) {
		//				stopLoading();
		//				showErrorMessage(this.getClass().getName() + "-checkDate() ERROR: " + e.toString());
		//			}
		//		}


		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors())
				showSystemMessage("Error al crear Sesión: " + bmUpdateResult.errorsToString());
			else {
				sessionDialogBox.hide();
				reset();
			}
		}
	}

	// Agrega un pedido de sesion
	private class UiOrderSessionSelectorForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiSuggestBox sessionSuggestBox = new UiSuggestBox(new BmoSession());
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		private int sessionTypeId;

		private BmoOrderSession bmoOrderSession = new BmoOrderSession();
		private Button assignButton = new Button("ASIGNAR");
		private Button saveButton = new Button("GUARDAR");
		private Button deleteButton = new Button("ELIMINAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();

		CheckBox seriesMondayCheckBox = new CheckBox();
		CheckBox seriesTuesdayCheckBox = new CheckBox();
		CheckBox seriesWednesdayCheckBox = new CheckBox();
		CheckBox seriesThursdayCheckBox = new CheckBox();
		CheckBox seriesFridayCheckBox = new CheckBox();
		CheckBox seriesSaturdayCheckBox = new CheckBox();
		CheckBox seriesSundayCheckBox = new CheckBox();

		UiSuggestBox userSuggestBox;

		private CheckBox seriesApplyAllCheckBox = new CheckBox();
		HorizontalPanel seriesDaysPanel = new HorizontalPanel();

		private BmoSession bmoSession = new BmoSession();

		public UiOrderSessionSelectorForm(UiParams uiParams, Panel defaultPanel, BmoSession bmoSession) {
			super(uiParams, defaultPanel);
			this.bmoOrderSession = new BmoOrderSession();
			this.bmoSession = bmoSession;
			sessionTypeId = bmoSession.getSessionTypeId().toInteger();
			// Filtros suggestbox sesiones para no duplicar sesiones en un
			// pedido
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setNotInFilter(bmoOrderSession.getKind(), bmoSession.getIdFieldName(),
					bmoOrderSession.getSessionId().getName(), bmoOrderSession.getOrderId().getName(), orderId);
			sessionSuggestBox.addFilter(filterByOrder);

			// Filtrar sesiones del tipo de venta
			BmFilter filterBySessionType = new BmFilter();
			filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(),
					bmoSessionType.getId());
			sessionSuggestBox.addFilter(filterBySessionType);

			// Filtrar sesiones disponibles
			BmFilter filterByAvailability = new BmFilter();
			filterByAvailability.setValueFilter(bmoSession.getKind(), bmoSession.getAvailable(), "1");
			sessionSuggestBox.addFilter(filterByAvailability);

			// Filtrar por vendedores
			userSuggestBox = new UiSuggestBox(new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig) getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId()
					.toInteger();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(), bmoProfileUser.getProfileId().getName(), "" + salesGroupId);

			userSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);

			try {
				bmoOrderSession.getSessionId().setValue(bmoSession.getId());
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "(): " + e.toString());
			}

			assignButton.setStyleName("formSaveButton");
			assignButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareAssign();
				}
			});
			if (!getSFParams().hasWrite(bmoOrderSession.getProgramCode()))
				assignButton.setVisible(false);
			buttonPanel.add(assignButton);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			if (!getSFParams().hasWrite(bmoOrderSession.getProgramCode()))
				saveButton.setVisible(false);
			buttonPanel.add(saveButton);

			deleteButton.setStyleName("formDeleteButton");
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareDelete();
				}
			});
			if (!getSFParams().hasDelete(bmoOrderSession.getProgramCode()))
				deleteButton.setVisible(false);

			buttonPanel.add(deleteButton);

			defaultPanel.add(formTable);

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
		public void show() {
			formTable.addLabelField(1, 0, bmoSession.getBmoSessionType().getName());
			formTable.addField(2, 0, userSuggestBox, bmoSession.getUserId());			
			formTable.addLabelField(3, 0, bmoSession.getReservations());
			formTable.addField(4, 0, descriptionTextArea, bmoSession.getDescription());
			formTable.addField(5, 0, startDateBox, bmoSession.getStartDate());

			if (bmoSession.getReservations().toInteger() > 0)
				startDateBox.setEnabled(false);
			formTable.addButtonPanel(buttonPanel);

		}

		public void prepareSave() {
			try {
				bmoSession.getDescription().setValue(descriptionTextArea.getText());
				bmoSession.getStartDate().setValue(startDateBox.getDateTime());
				// bmoSession.getEndDate().setValue(endDateBox.getDateTime());
				// bmoSession.getReservations().setValue(reservationsTextBox.getText());
				// bmoSession.getAvailable().setValue(availableCheckBox.getValue());
				bmoSession.getUserId().setValue(userSuggestBox.getSelectedId());
				bmoSession.getSessionTypeId().setValue(sessionTypeId);

				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void prepareAssign() {
			try {
				bmoOrderSession = new BmoOrderSession();
				bmoOrderSession.getSessionId().setValue(bmoSession.getId());
				bmoOrderSession.getOrderId().setValue(orderId);

				bmoOrderSession.getSeriesApplyAll().setValue(seriesApplyAllCheckBox.getValue());
				bmoOrderSession.getSeriesMonday().setValue(seriesMondayCheckBox.getValue());
				bmoOrderSession.getSeriesTuesday().setValue(seriesTuesdayCheckBox.getValue());
				bmoOrderSession.getSeriesWednesday().setValue(seriesWednesdayCheckBox.getValue());
				bmoOrderSession.getSeriesThursday().setValue(seriesThursdayCheckBox.getValue());
				bmoOrderSession.getSeriesFriday().setValue(seriesFridayCheckBox.getValue());
				bmoOrderSession.getSeriesSaturday().setValue(seriesSaturdayCheckBox.getValue());
				bmoOrderSession.getSeriesSunday().setValue(seriesSundayCheckBox.getValue());

				assign();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void prepareDelete() {
			delete();
		}

		public void assign() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-assign() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderSession.getPmClass(), bmoOrderSession, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-assign() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoSession.getPmClass(), bmoSession, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		public void delete() {

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().delete(bmoSession.getPmClass(), bmoSession, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + e.toString());
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors())
				showSystemMessage("Error al asignar Sesión: " + bmUpdateResult.errorsToString());
			else {
				orderSessionDialogBox.hide();
				reset();
			}
		}
	}
}