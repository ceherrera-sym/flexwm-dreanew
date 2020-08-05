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

import java.sql.Types;
import java.util.Date;

import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomer.UiCustomerForm;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiSessionCalendar extends UiCalendar {
	BmoSession bmoSession;

	UiListBox filterSessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
	UiListBox filterUserListBox = new UiListBox(getUiParams(), new BmoUser());

	// Dialogo de agregar sesion
	DialogBox sessionDialogBox;

	public UiSessionCalendar(UiParams uiParams) {
		super(uiParams, new BmoSession());
		this.bmoSession = (BmoSession)getBmObject();
		dateFieldName = bmoSession.getStartDate().getName();

		// Forza a desplegar calendario como MASTER
		setUiType(bmoSession.getProgramCode(), UiParams.MASTER);

		// Limpia filtros de listas
		getUiParams().resetUiProgramParams(bmoSession.getProgramCode());

	}

	@Override
	public void postShow() {
		// Filtro por ubicaciones
		addFilterListBox(new UiListBox(getUiParams(), new BmoLocation()), new BmoLocation());

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
		filterUserListBox.addFilter(filterSalesmen);
		addFilterListBox(filterUserListBox, new BmoUser());

		// Filtro por tipos de sesiones
		addFilterListBox(filterSessionTypeListBox, new BmoSessionType());

		//No mostrar nuevo registro en el calendario
		newImage.setVisible(false);
	}

	@Override
	public void create() {
		UiSession uiSessionForm = new UiSession(getUiParams());
		uiSessionForm.show();
	}

	@Override
	protected void open(BmObject bmObject) {
		bmoSession = (BmoSession)bmObject;
		UiSession uiSessionForm = new UiSession(getUiParams());
		uiSessionForm.edit(bmoSession);
	}	

	@Override
	protected void moveAppointment(UiAppointment uiAppointment) {
		if (Window.confirm("Esta seguro que desea mover la Sesión?")) {
			bmoSession = (BmoSession)uiAppointment.getBmObject();
			try {
				bmoSession.getStartDate().setValue(GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()));
				bmoSession.getEndDate().setValue(GwtUtil.dateToString(uiAppointment.getEnd(), getSFParams().getDateTimeFormat()));				
				save(bmoSession);

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-moveAppointment() ERROR: " + e.toString());
			}
		}
	}

	@Override
	protected void emptySelection(TimeBlockClickEvent<Date> event) {
		addSession(event.getTarget());
	}

	// Agrega sesion 
	public void addSession(Date startDate) {
		sessionDialogBox = new DialogBox(true);
		sessionDialogBox.setGlassEnabled(true);
		sessionDialogBox.setText("Seleccionar Cliente | " +GwtUtil.dateToString(startDate, getSFParams().getDateFormat()));

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		sessionDialogBox.setWidget(vp);

		UiSessionSelectorForm sessionSelectorForm = new UiSessionSelectorForm(getUiParams(), vp, startDate);
		sessionSelectorForm.show();

		sessionDialogBox.center();
		sessionDialogBox.show();
	}


	@Override
	public void displayList() {	
		calendar.suspendLayout();
		while (iterator.hasNext()) {
			BmoSession bmoSession = (BmoSession)iterator.next();
			UiAppointment appt = new UiAppointment(bmoSession);
			appt.setStart(new Date(bmoSession.getStartDate().toMilliseconds(getSFParams())));
			appt.setEnd(new Date(bmoSession.getEndDate().toMilliseconds(getSFParams())));
			appt.setTitle(bmoSession.getBmoUser().getCode().toString() +
					" - " + bmoSession.getBmoSessionType().getName().toString() +
					" - " + bmoSession.getStartDate().toString()					
					);
			if (bmoSession.getReservations().toInteger() < bmoSession.getBmoSessionType().getCapacity().toInteger()) appt.setStyle(AppointmentStyle.GREEN);
			else if (bmoSession.getReservations().toInteger() == bmoSession.getBmoSessionType().getCapacity().toInteger()) appt.setStyle(AppointmentStyle.RED);
			addAppointment(appt);
		}
		calendar.scrollToHour(8);
		calendar.resumeLayout();
	}

	public void reset() {
		super.list();
	}

	// Agrega una sesion
	private class UiSessionSelectorForm extends Ui {		
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

		//Sesiones		
		//UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());

		//Dialogo de Session
		DialogBox createSessionDialogBox;

		//Dialogo Crear Venta Sesión
		DialogBox createSessionSaleDialogBox;

		private BmField customerField;

		// Número de sesiones disponibles
		private Label noSessionNoAttended = new Label();
		private Label showSingUp = new Label();

		private BmoSession bmoSession = new BmoSession();		
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private Date startDate;
		private String startDateString = "";
		// Opciones
		private Button createSessionSaleButton = new Button("VENTA SESION");
		private Button createSessionCustButton = new Button("ASIGNAR CLASE");
		private Button createSaleProductButton = new Button("VTA.PRODUCTO");

		UiCustomerForm uiCustomerForm = new UiCustomer(getUiParams()).getUiCustomerForm();

		public UiSessionSelectorForm(UiParams uiParams, Panel defaultPanel, Date startDate) {
			super(uiParams, defaultPanel);
			this.startDate = startDate;
			this.startDateString = GwtUtil.dateToString(startDate, getSFParams().getDateFormat());
			
			customerField = new BmField("customerField", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					suggestAction(uiSuggestBox);						
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			createSessionSaleButton.setStyleName("formSaveButton");
			createSessionSaleButton.setVisible(false);
			createSessionSaleButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {					
					addSessionSale(customerSuggestBox.getSelectedId());
				}
			});

			createSessionCustButton.setStyleName("formSaveButton");
			createSessionCustButton.setVisible(false);
			createSessionCustButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {					
					addSessionCust(customerSuggestBox.getSelectedId());
				}
			});

			createSaleProductButton.setStyleName("formSaveButton");			
			createSaleProductButton.setVisible(false);
			createSaleProductButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {					
					createNewOrder();
				}
			});

			buttonPanel.add(createSessionCustButton);
			buttonPanel.add(createSessionSaleButton);
			buttonPanel.add(createSaleProductButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {			
			formTable.addField(2, 0, customerSuggestBox, customerField, uiCustomerForm, new BmoCustomer());
			formTable.addLabelField(3, 0, "Disponibles", noSessionNoAttended);

			formTable.addButtonPanel(buttonPanel);
		}

		public void suggestAction(UiSuggestBox uiSuggestBox) {			
			if (uiSuggestBox == customerSuggestBox) {				
				if (customerSuggestBox.getSelectedId() > 0) {					
					showSessionNotAttended();	
				} else {
					noSessionNoAttended.setText("0");					
					formTable.addFieldEmpty(4, 0);
					formTable.addFieldEmpty(5, 0);
					formTable.addFieldEmpty(6, 0);
					formTable.addFieldEmpty(7, 0);
				}
			}
		}

		// Muestra sesiones disponibles
		public void showSessionNotAttended() {			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {					
					showErrorMessage(this.getClass().getName() + "-sessionAttended() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					if (result.getMsg().equals("") || result.getMsg().trim().equals("0")) {
						formTable.addButtonCell(5, 0, createSessionCustButton);			
						formTable.addButtonCell(6, 0, createSessionSaleButton);
						formTable.addButtonCell(7, 0, createSaleProductButton);
						createSessionCustButton.setVisible(false);
						createSessionSaleButton.setVisible(true);
						createSaleProductButton.setVisible(true);
					} else {
						formTable.addButtonCell(5, 0, createSessionCustButton);			
						formTable.addButtonCell(6, 0, createSessionSaleButton);
						formTable.addButtonCell(7, 0, createSaleProductButton);
						createSessionCustButton.setVisible(true);
						createSessionSaleButton.setVisible(true);
						createSaleProductButton.setVisible(true);

						noSessionNoAttended.setText(result.getMsg());
					}

					showSingUp();
				}
			};
			try {
				if (!isLoading()) {					
					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_SESSNOTATTENDED, "" + customerSuggestBox.getSelectedId() + "|" + startDateString, callback);
				}
			} catch (SFException e) {				
				showErrorMessage(this.getClass().getName() + "-sessionAttended() ERROR: " + e.toString());
			}
		}

		// Muestra vigencia del pedido
		public void showSingUp() {			
			int custId = customerSuggestBox.getSelectedId();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-showSingUp() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();					
					if (!result.getMsg().equals("")) {
						showSingUp.setText(result.getMsg());
						formTable.addLabelCell(4, 0, "Vigente hasta:" + showSingUp.getText());
					} else {
						formTable.addLabelCell(4, 0, "Vigente hasta: No Disponible");
					}
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_CHECKSINGUP, "" + custId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-showSingUp() ERROR: " + e.toString());
			}
		}

		// Crear Venta de Sesión
		public void addSessionSale(int customerId) {	
			createSessionSaleDialogBox = new DialogBox(true);
			createSessionSaleDialogBox.setGlassEnabled(true);
			createSessionSaleDialogBox.setText("Crear Venta de Sesiones| " + GwtUtil.dateToString(startDate, getSFParams().getDateFormat()));

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("500px", "300px");

			createSessionSaleDialogBox.setWidget(vp);

			UiSessionSaleSelectorForm sessionSaleSelectorForm = new UiSessionSaleSelectorForm(getUiParams(), vp, customerId);
			sessionSaleSelectorForm.show();

			createSessionSaleDialogBox.center();
			createSessionSaleDialogBox.show();
		}

		// Crear Venta de Sesión
		public void addSessionCust(int customerId) {	
			createSessionDialogBox = new DialogBox(true);
			createSessionDialogBox.setGlassEnabled(true);
			createSessionDialogBox.setText("Crear Cita | " + GwtUtil.dateToString(startDate, getSFParams().getDateFormat()));

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "200px");

			createSessionDialogBox.setWidget(vp);

			UiSessionCustomerForm uiSessionCustomerForm = new UiSessionCustomerForm(getUiParams(), vp, customerId, startDate);
			uiSessionCustomerForm.show();

			createSessionDialogBox.center();
			createSessionDialogBox.show();
		}

		public void newOrder(int id) {
			sessionDialogBox.hide();
			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), id);
			uiOrderDetail.show();
		}

		// Crear un pedido de venta
		public void createNewOrder() {		
			BmoOrder bmoOrder = new BmoOrder();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {					
					showErrorMessage(this.getClass().getName() + "-createNewOrder() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {					
					if (!result.hasErrors()) {						
						newOrder(result.getId());
					} else {
						showErrorMessage(result.errorsToString());
					}
				}
			};
			try {
				if (!isLoading()) {					
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_CREATENEWORDER, "" + customerSuggestBox.getSelectedId(), callback);
				}
			} catch (SFException e) {				
				showErrorMessage(this.getClass().getName() + "-createNewOrder() ERROR: " + e.toString());
			}
		}

		// Clase para generar nueva venta de sesión
		private class UiSessionSaleSelectorForm extends Ui {
			private int customerId;
			private int newSessionSaleId;
			int programId;
			
			private NumberFormat numberFormat = NumberFormat.getFormat("####,###.##");
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
			private BmoSessionSale bmoSessionSale = new BmoSessionSale();
			private BmoSession bmoSession = new BmoSession();
			private Button saveButton = new Button("CREAR");
			private Button cancelButton = new Button("REGRESAR");
			private HorizontalPanel buttonPanel = new HorizontalPanel();

			// Venta de Session
			TextBox nameTextBox = new TextBox();
			UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
			UiListBox sessionTypePackageListBox = new UiListBox(getUiParams(), new BmoSessionTypePackage());
			UiListBox companiesListBox = new UiListBox(getUiParams(), new BmoCompany());
			UiListBox wFlowTypeListBox;
			UiTextBox noSessionTextBox = new UiTextBox();
			UiTextBox costSessionTextBox = new UiTextBox();
			UiDateBox startDateSessionSaleBox = new UiDateBox();
			UiDateBox inscriptionDateSessionSaleBox = new UiDateBox();

			private BmField nameField;
			private BmField startDateSessionSaleField;			
			private BmField orderTypeField;			
			private BmField sessionTypePackageField;
			private BmField noSessionField;
			private BmField costSessionField;	
			private BmField inscriptionDateSessionField;
			
			// Datos asignacion de sesiones
			UiListBox userListBox;
			UiDateTimeBox sessionStartDateBox = new UiDateTimeBox();
			CheckBox seriesMondayCheckBox = new CheckBox();
			CheckBox seriesTuesdayCheckBox = new CheckBox();
			CheckBox seriesWednesdayCheckBox = new CheckBox();
			CheckBox seriesThursdayCheckBox = new CheckBox();
			CheckBox seriesFridayCheckBox = new CheckBox();
			CheckBox seriesSaturdayCheckBox = new CheckBox();
			CheckBox seriesSundayCheckBox = new CheckBox();
			HorizontalPanel seriesDaysPanel = new HorizontalPanel();


			public UiSessionSaleSelectorForm(UiParams uiParams, Panel defaultPanel, int custId) {
				super(uiParams, defaultPanel);
				customerId = custId;
				
				noSessionTextBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent arg0) {
						calculateCostSession();
					}
				});

				// Acciones cambio de fecha
				ValueChangeHandler<Date> dateChangeHandler = new ValueChangeHandler<Date>() {
					@Override
					public void onValueChange(ValueChangeEvent<Date> event) {
						formDateChange(event);
					}
				};
				startDateSessionSaleBox.addValueChangeHandler(dateChangeHandler);

				cancelButton.setStyleName("formCloseButton");
				cancelButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cancelSessionSale();
					}
				});
				cancelButton.setVisible(true);

				saveButton.setStyleName("formSaveButton");

				saveButton.setVisible(false);
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						createSessionSale();
					}
				});
				if (getSFParams().hasWrite(bmoSession.getProgramCode())) 
					saveButton.setVisible(true);

				buttonPanel.add(saveButton);
				buttonPanel.add(cancelButton);

				nameField = new BmField("namefield", "", "Nombre", 20, Types.VARCHAR, false, BmFieldType.STRING, false);
				customerField = new BmField("customerfield", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);				
				startDateSessionSaleField = new BmField("startDateSessionSaleField", "", "Inicio VS", 20, Types.DATE, false, BmFieldType.DATE, false);
				inscriptionDateSessionField = new BmField("inscriptionDateSessionField", "", "Inscripción", 20, Types.DATE, false, BmFieldType.DATE, false);
				sessionTypePackageField = new BmField("sessiontypepackagefield", "", "Paquete Sesiones", 8, Types.INTEGER, false, BmFieldType.ID, false);
				orderTypeField = new BmField("ordertypefield", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
				noSessionField = new BmField("nosessionfield", "", "No.Sesiones", 11, Types.INTEGER, false, BmFieldType.NUMBER, false);
				costSessionField = new BmField("costsessionfield", "", "Costo", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);				
				
				// Crear panel checkboxes dia serie repeticion
				Label l = new Label("Días:");
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
				
				// Filtar Tipo de Session
				BmoOrderType bmoOrderType = new BmoOrderType();
				BmFilter filterOrderType = new BmFilter();
				filterOrderType.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_SESSION);
				orderTypeListBox.addFilter(filterOrderType);

				// Filtrado por tipo se sesion
				sessionTypePackageListBox.addChangeHandler(new ChangeHandler() {					
					@Override
					public void onChange(ChangeEvent arg0) {
						showSessionAndCost();
					}
				});

				try {
					programId = getSFParams().getProgramId(bmoSessionSale.getProgramCode());
				} catch (SFException e) {
					showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
				}

				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
				wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);
				
				// Filtrar por instructores
				BmoUser bmoUser = new BmoUser();
				userListBox = new UiListBox(getUiParams(), new BmoUser());
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterInstructors = new BmFilter();
				int instructorGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				filterInstructors.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + instructorGroupId);	
				userListBox.addFilter(filterInstructors);
				
				// Filtrar por instructores activos
				BmFilter filterSalesmenActive = new BmFilter();
				filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
				userListBox.addFilter(filterSalesmenActive);

				defaultPanel.add(formTable);
			}	

			@Override
			public void show() {	
				getInscriptionDate();
				
				try {
					startDateSessionSaleField.setValue(GwtUtil.dateToString(startDate, getSFParams().getDateFormat()));
					bmoSession.getStartDate().setValue(GwtUtil.dateToString(startDate, getSFParams().getDateFormat()));
				} catch (BmException e) {
					showErrorMessage("Error al asignar la fecha de inicio");
				}

				formTable.addField(1, 0, nameTextBox, nameField);
				formTable.addField(2, 0, orderTypeListBox, orderTypeField);
				formTable.addField(3, 0, wFlowTypeListBox, bmoSessionSale.getWFlowTypeId());
				formTable.addField(4, 0, companiesListBox, bmoSessionSale.getCompanyId());
				formTable.addField(5, 0, inscriptionDateSessionSaleBox, inscriptionDateSessionField);
				formTable.addField(6, 0, startDateSessionSaleBox, startDateSessionSaleField);
				formTable.addField(7, 0, sessionTypePackageListBox, sessionTypePackageField);								
				formTable.addField(8, 0, noSessionTextBox, noSessionField);	
				formTable.addField(9, 0, costSessionTextBox, costSessionField);
				
				formTable.addLabelCell(10, 0, "Asignar Sesiones:");
				formTable.addField(11, 0, userListBox, bmoSession.getUserId());
				formTable.addField(12, 0, sessionStartDateBox, bmoSession.getStartDate());
				formTable.addPanel(13, 0, seriesDaysPanel);

				formTable.addButtonPanel(buttonPanel);
				
				statusEffect();
			}
			
			// Mostrar la fecha de inscripcion
			public void getInscriptionDate() {			
				//int custId = customerSuggestBox.getSelectedId();
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-getInscriptionDate() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						
						if (!result.getMsg().equals(""))
						{							
							inscriptionDateSessionSaleBox.getTextBox().setValue(result.getMsg());
							try {
								
								inscriptionDateSessionField.setValue(result.getMsg());
								if(result.getMsg().length() <= 1 )
									inscriptionDateSessionSaleBox.setEnabled(true);
								else {
									inscriptionDateSessionSaleBox.setEnabled(false);
								}
							} catch (BmException e) {
								e.printStackTrace();
							}
						} 
						
					}
				};

				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_GETSINGUP, "" + customerId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getInscriptionDate() ERROR: " + e.toString());
				}
			}

			public void statusEffect() {
				//wFlowTypeListBox.setEnabled(false);
				//orderTypeListBox.setEnabled(false);				
				noSessionTextBox.setEnabled(true);	
				formTable.hideField(orderTypeField);
				formTable.hideField(bmoSessionSale.getWFlowTypeId());

			}

			public void formDateChange(ValueChangeEvent<Date> event) {
				if (event.getSource() == startDateSessionSaleBox) {
					sessionStartDateBox.setValue(startDateSessionSaleBox.getValue());
					showSessionAndCost();
				}
			}

			// Mostrar el costo y las sesiones disponibles
			private void showSessionAndCost() {			
				if (sessionTypePackageListBox.getSelectedIndex() > 0) {
					BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
	
					try {
						costSessionField.setValue(bmoSessionTypePackage.getSalePrice().toDouble());
						costSessionTextBox.setText("" + bmoSessionTypePackage.getSalePrice().toDouble());
					} catch (BmException e) {
						showErrorMessage("Error al obterner el Costo del Paquete");				
					}
	
					//formTable.addField(9, 0, costSessionTextBox, costSessionField);
					showSessionByPackage();
				}
			}

			// Calcular el costo
			private void calculateCostSession() {	
				BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
				/**
				 * Calcular el monto en costo del paquete / 30 días
				 * Multiplicar por el número de clases
				 */
				double cost = bmoSessionTypePackage.getSalePrice().toDouble();
				costSessionTextBox.setText("" + numberFormat.format(cost));
			}

			// Mostrar sesion por paquete
			private void showSessionByPackage() {
				BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();

				String values = "";
				values = sessionTypePackageListBox.getSelectedId() + "|" + startDateSessionSaleBox.getTextBox().getValue();
				
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {					
						showErrorMessage(this.getClass().getName() + "-showSessionByPackage() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();					
						try {						
							noSessionField.setValue(result.getMsg());
							noSessionTextBox.setText(result.getMsg());
						} catch (BmException e) {
							showErrorMessage("Error al calcular las clases del mes " + e.toString());
						}

						//formTable.addField(8, 0, noSessionTextBox, noSessionField);
						
						showCostByPackage();
					}
				};
				try {
					if (!isLoading()) {	
						getUiParams().getBmObjectServiceAsync().action(bmoSessionTypePackage.getPmClass(), bmoSessionTypePackage, BmoSessionTypePackage.ACTION_NOSESSION, values, callback);
					}
				} catch (SFException e) {				
					showErrorMessage(this.getClass().getName() + "-showSessionByPackage() ERROR: " + e.toString());
				}
			}

			// Muestra el precio segun el paquete elegido
			private void showCostByPackage() {
				BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();

				String values = "";
				values = sessionTypePackageListBox.getSelectedId() + "|" + startDateSessionSaleBox.getTextBox().getValue();

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {					
						showErrorMessage(this.getClass().getName() + "-showCostByPackage() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();					
						costSessionTextBox.setText("" + numberFormat.format(Double.parseDouble(result.getMsg())));
					}
				};
				try {
					if (!isLoading()) {					
						getUiParams().getBmObjectServiceAsync().action(bmoSessionTypePackage.getPmClass(), bmoSessionTypePackage, BmoSessionTypePackage.ACTION_COSTSESSION, values, callback);
					}
				} catch (SFException e) {				
					showErrorMessage(this.getClass().getName() + "-showCostByPackage() ERROR: " + e.toString());
				}
			}

			// Cancelar venta de sesión
			public void cancelSessionSale() {
				createSessionSaleDialogBox.hide();
			}

			// Genera la venta de sesion
			public void createSessionSale() {			
				String values = "";

				if (!nameTextBox.getText().equals("")) {
					values = customerId + "|" + nameTextBox.getText() + "|" + startDateSessionSaleBox.getTextBox().getText() + "|" +  
							inscriptionDateSessionSaleBox.getTextBox().getText()  + "|" + wFlowTypeListBox.getSelectedId() + "|" + 
							sessionTypePackageListBox.getSelectedId() + "|" + orderTypeListBox.getSelectedId() + "|" +
							noSessionTextBox.getText() + "|" + costSessionTextBox.getText().replaceAll(",", "") + "|" + companiesListBox.getSelectedId();

					
					// Asigna los valores de la Sesion
					try {
						BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
						bmoSession.getStartDate().setValue(sessionStartDateBox.getDateTime());
						bmoSession.getUserId().setValue(userListBox.getSelectedId());
						bmoSession.getSessionTypeId().setValue(bmoSessionTypePackage.getSessionTypeId().toInteger());
						bmoSession.getSeriesMonday().setValue(seriesMondayCheckBox.getValue());
						bmoSession.getSeriesTuesday().setValue(seriesTuesdayCheckBox.getValue());
						bmoSession.getSeriesWednesday().setValue(seriesWednesdayCheckBox.getValue());
						bmoSession.getSeriesThursday().setValue(seriesThursdayCheckBox.getValue());
						bmoSession.getSeriesFriday().setValue(seriesFridayCheckBox.getValue());
						bmoSession.getSeriesSaturday().setValue(seriesSaturdayCheckBox.getValue());
						bmoSession.getSeriesSunday().setValue(seriesSundayCheckBox.getValue());
					} catch (BmException e1) {
						showSystemMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e1.toString());
					}
					
					AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
						public void onFailure(Throwable caught) {
							stopLoading();
							showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + caught.toString());
						}

						public void onSuccess(BmUpdateResult result) {
							stopLoading();
							if (!result.hasErrors()) {
								newSessionSaleId = result.getId();

								saveButton.setVisible(false);
								sessionDialogBox.hide();
								createSessionSaleDialogBox.hide();
								UiSessionSaleDetail uiSessionSaleDetail = new UiSessionSaleDetail(getUiParams(), newSessionSaleId);
								uiSessionSaleDetail.show();

							} else {
								showErrorMessage("Error al Crear la Venta de Sesión." + result.errorsToString());
							}
						}
					};

					try {
						if (!isLoading()) {
							startLoading();
							getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_NEWSESSIONSALE, values, callback);
						}
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e.toString());
					}
				} else {
					showErrorMessage("Capturar el Nombre.");
				}
			}
		}

		// Agregar cita a cliente
		private class UiSessionCustomerForm extends Ui {			
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

			//Sessiones
			UiDateTimeBox startDateBox = new UiDateTimeBox();
			DateBox startDateSessionSaleBox = new DateBox();
			CheckBox assistCheckBox = new CheckBox();		
			UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());			

			//Venta de Session			
			UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
			UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
			UiListBox sessionTypePackageListBox = new UiListBox(getUiParams(), new BmoSessionTypePackage());
			UiTextBox noSessionTextBox = new UiTextBox();
			UiTextBox costSessionTextBox = new UiTextBox();
			UiListBox sessionSaleListBox = new UiListBox(getUiParams(), new BmoSessionSale());

			// Número de sesiones disponibles
			private Label noSessionNoAttended = new Label();

			private BmField startDateField;			
			private BmField userField;			
			private BmField assistField;
			private BmField sessionTypePackageField;
			private BmField sessionSaleId;

			private BmoSession bmoSession = new BmoSession();
			private Button saveButton = new Button("GUARDAR");
			private Button cancelButton = new Button("REGRESAR");
			private HorizontalPanel buttonPanel = new HorizontalPanel();
			private String startDateString;
			private int customerId;

			public UiSessionCustomerForm(UiParams uiParams, Panel defaultPanel, int custId, Date date) {
				super(uiParams, defaultPanel);
				this.customerId = custId;
				this.startDateString = GwtUtil.dateToString(date, getSFParams().getDateFormat());

				sessionSaleId = new BmField("sessionSaleId", "", "Venta Sesion", 20, Types.INTEGER, false, BmFieldType.ID, false);

				startDateField = new BmField("startDateField", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
				userField = new BmField("userField", "", "Instrucctor", 11, Types.INTEGER, false, BmFieldType.ID, false);
				customerField = new BmField("customerField", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);
				assistField = new BmField("assistField", "1", "Asistio", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);				
				sessionTypePackageField = new BmField("sessiontypepackagefield", "", "Paquete Sesiones", 8, Types.INTEGER, false, BmFieldType.ID, false);

				//Filtar Tipo de Session
				BmoOrderType bmoOrderType = new BmoOrderType();
				BmFilter filterOrderType = new BmFilter();
				filterOrderType.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_SESSION);
				orderTypeListBox.addFilter(filterOrderType);

				sessionTypeListBox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						updateSessionTypePackage();					
					}
				});

				// Manejo de acciones de suggest box
				UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
					@Override
					public void onSelect(UiSuggestBox uiSuggestBox) {
						suggestAction(uiSuggestBox);						
					}
				};
				formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

				saveButton.setStyleName("formSaveButton");
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						addCustomerSession();
					}
				});
				saveButton.setVisible(false);
				if (getSFParams().hasWrite(bmoSession.getProgramCode())) 
					saveButton.setVisible(true);

				cancelButton.setStyleName("formCloseButton");
				cancelButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cancelSessionCustoSale();
					}
				});
				cancelButton.setVisible(true);

				buttonPanel.add(saveButton);
				buttonPanel.add(cancelButton);

				defaultPanel.add(formTable);
			}

			@Override
			public void show() {

				showSessionNotAttended(customerId);
				try {
					if (!filterSessionTypeListBox.getSelectedId().equals("")) {
						bmoSession.getSessionTypeId().setValue(filterSessionTypeListBox.getSelectedId());
						bmoSession.setBmoSessionType((BmoSessionType)filterSessionTypeListBox.getSelectedBmObject());
					}

					if (!filterUserListBox.getSelectedId().equals("")) {
						bmoSession.getUserId().setValue(filterUserListBox.getSelectedId());
					}

					startDateField.setValue(GwtUtil.dateToString(startDate, getSFParams().getDateTimeFormat()));
					
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
				}

				setUserFilters();
				setSessionSaleFilters(startDateString);	
				formTable.addField(1, 0, sessionSaleListBox, sessionSaleId);				
				formTable.addField(2, 0, userSuggestBox, userField);
				formTable.addLabelField(3, 0, "Disponibles", noSessionNoAttended);			
				formTable.addField(4, 0, startDateBox, startDateField);
				formTable.addField(5, 0, assistCheckBox, assistField);
				formTable.addButtonPanel(buttonPanel);

				statusEffect();
			}

			public void cancelSessionCustoSale() {
				createSessionDialogBox.hide();
			}

			public void updateSessionTypePackage() {

				// Filtrar por paquetes disponibles
				sessionTypePackageListBox = new UiListBox(getUiParams(), new BmoSessionTypePackage());

				//Filtrar por tipos de pedidos de 
				BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
				BmFilter filterEnabled = new BmFilter();
				filterEnabled.setValueFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getEnabled(), "1");			
				sessionTypePackageListBox.addFilter(filterEnabled);

				BmFilter filterBySessionType = new BmFilter();
				filterBySessionType.setValueFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getSessionTypeId(), sessionTypeListBox.getSelectedId());			
				sessionTypePackageListBox.addFilter(filterBySessionType);

				formTable.addField(6, 0, sessionTypePackageListBox, sessionTypePackageField);
			}

			//Asignar filtros de usuario
			private void setUserFilters() {
				BmoUser bmoUser = new BmoUser();
				// Filtrar por instructores activos
				BmFilter filterSalesmenActive = new BmFilter();
				filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
				userSuggestBox.addFilter(filterSalesmenActive);

				// Filtrar por instructores
				userSuggestBox = new UiSuggestBox(new BmoUser());				
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterSalesmen = new BmFilter();
				int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesGroupId);
				userSuggestBox.addFilter(filterSalesmen);
			}

			// Asignar filtros 
			@SuppressWarnings("deprecation")
			private void setSessionSaleFilters(String startDateString)  {
				sessionSaleListBox.clear();
				sessionSaleListBox.clearFilters();

				BmoSessionSale bmoSessionSale = new BmoSessionSale();
				// Filtrar por cliente
				BmFilter filterCustomerId = new BmFilter();
				filterCustomerId.setValueFilter(bmoSessionSale.getKind(), bmoSessionSale.getCustomerId(), customerId);
				sessionSaleListBox .addFilter(filterCustomerId);
				
				// Seccionar fecha
				int year = Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(startDate));
				int month = Integer.parseInt(DateTimeFormat.getFormat("M").format(startDate));
				int day = Integer.parseInt(DateTimeFormat.getFormat("d").format(startDate));
				//showSystemMessage("fechaString: "+startDateString+" | anio: "+year + " mes: "+month +" dia: "+day);
				
				// Obtener fecha seleccionada, se crea otra para su posterior modificacion +1mes
				Date endDate = new Date(year + "-" + month + "-" + day + " 00:00");
				// Sumar 1 mes a la fecha seleccionada
				CalendarUtil.addMonthsToDate(endDate, 1);
				
				// Primer dia del mes seleccionado
				Date firstdateMonthNow = new Date(year + "-" + month + "-" + 01 + " 00:00");
				// Restar 1 mes a la fecha seleccionada
				Date firstdateMonthPast = new Date(year + "-" + (month - 1) + "-" + 01 + " 00:00");
								
				// Convertir de nuevo a cadena de texto
				String firstdateMonthNow2 = GwtUtil.dateToString(firstdateMonthNow, getSFParams().getDateFormat());
				String firstdateMonthPast2 = GwtUtil.dateToString(firstdateMonthPast, getSFParams().getDateFormat());
				//String endDate2 = GwtUtil.dateToString(endDate, getSFParams().getDateFormat());
				/*
				showSystemMessage("+1 mes de la fecha seleccionada:"+ endDate2
						+" | primer dia del mes seleccionado: "+firstdateMonthNow2 
						+" | primer dia del mes anterior: "+firstdateMonthPast2 );
			 	*/
				// Traer dias entre fecha seleccionada y la fecha con +1mes
				int daysDiff = CalendarUtil.getDaysBetween(startDate, endDate);

				// Agregar dias a la fecha del mes actual
				CalendarUtil.addDaysToDate(firstdateMonthNow, daysDiff);

				// Convertir de nuevo a cadena de texto la fecha que se la agregarons los dias
				firstdateMonthNow2 = GwtUtil.dateToString(firstdateMonthNow, getSFParams().getDateFormat());				

				//showSystemMessage("daysDiff: "+daysDiff+" | ultimo dia del mes actual: "+ firstdateMonthNow2);
				
				// Filtro del fecha del primer dia del mes anterior
				BmFilter filterFirstdateMonthPast = new BmFilter();
				filterFirstdateMonthPast.setValueOperatorFilter(bmoSessionSale.getKind(), bmoSessionSale.getStartDate(), BmFilter.MAJOREQUAL, firstdateMonthPast2);
				sessionSaleListBox .addFilter(filterFirstdateMonthPast);
				
				// Filtro de fecha fin ed mes actual, se le pone < por tema de que manda 1 dia mas
				BmFilter filterLastdateMonth = new BmFilter();
				filterLastdateMonth.setValueOperatorFilter(bmoSessionSale.getKind(), bmoSessionSale.getStartDate(), BmFilter.MINOR, firstdateMonthNow2);
				sessionSaleListBox .addFilter(filterLastdateMonth);
			}

			public void statusEffect() {
				startDateSessionSaleBox.setEnabled(false);
				sessionTypeListBox.setEnabled(false);
				sessionTypePackageListBox.setEnabled(false);
				orderTypeListBox.setEnabled(false);
				noSessionTextBox.setEnabled(false);
				costSessionTextBox.setEnabled(false);
			}


			public void showSessionNotAttended(int customerId) {
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {					
						showErrorMessage(this.getClass().getName() + "-showSessionNotAttended() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {						
						noSessionNoAttended.setText(result.getMsg());
					}
				};
				try {
					if (!isLoading()) {					
						getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_SESSNOTATTENDED, "" + customerId + "|" + startDateString, callback);
					}
				} catch (SFException e) {				
					showErrorMessage(this.getClass().getName() + "-showSessionNotAttended() ERROR: " + e.toString());
				}
			}

			public void addCustomerSession() {
				String values = "";
				values = customerId + "|" + userSuggestBox.getSelectedId() + "|" +
						startDateBox.getDateTime() + "|" + assistCheckBox.getValue() + "|" + sessionSaleListBox.getSelectedId();
				
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (startDateBox.getDateTime().equals("")) 
							showErrorMessage("Debe seleccionar una Fecha Inicio.");
						else
							showErrorMessage(this.getClass().getName() + "-addCustomerSession() onFailure ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						if (!result.hasErrors()) {						
							showSystemMessage("Cita Creada con Exito");
							createSessionDialogBox.hide();
						}
						sessionDialogBox.hide();
						processUpdateResult(result);
					}
				};

				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_NEWSESSIONCUST, values, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-addCustomerSession() ERROR: " + e.toString());
				}

			}

			public void processUpdateResult(BmUpdateResult bmUpdateResult) {
				if (bmUpdateResult.hasErrors()) 
					showSystemMessage("Error al crear Sesión: " + bmUpdateResult.errorsToString());
				else {				
					sessionDialogBox.hide();
					reset();
				}
			}
		}
	}



	// Agrega una sesion
	/*private class UiSessionSelectorForm extends Ui {
		private BProgramServiceAsync bmObjectServiceAsync = GWT.create(BProgramService.class);
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();
		TextBox reservationsTextBox = new TextBox();
		CheckBox availableCheckBox = new CheckBox();
		UiListBox userListBox = new UiListBox(getUiParams(), new BmoUser());
		UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
		private BmoSession bmoSession = new BmoSession();
		private Button saveButton = new Button("Guardar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private Date startDate;
		public UiSessionSelectorForm(UiParams uiParams, Panel defaultPanel, Date startDate) {
			super(uiParams, defaultPanel);
			this.startDate = startDate;
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
			startDateBox.getTimeListBox().addChangeHandler(listChangeHandler);
			// Filtrar por instructores
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterInstructors = new BmFilter();
			int instructorGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterInstructors.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + instructorGroupId);	
			userListBox.addFilter(filterInstructors);
			// Filtrar por instructores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userListBox.addFilter(filterSalesmenActive);
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
			try {
				if (!filterSessionTypeListBox.getSelectedId().equals("")) {
					bmoSession.getSessionTypeId().setValue(filterSessionTypeListBox.getSelectedId());
					bmoSession.setBmoSessionType((BmoSessionType)filterSessionTypeListBox.getSelectedBmObject());
				}
				if (!filterUserListBox.getSelectedId().equals("")) {
					bmoSession.getUserId().setValue(filterUserListBox.getSelectedId());
				}
				bmoSession.getStartDate().setValue(GwtUtil.dateToString(startDate, getSFParams().getDateTimeFormat()));
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
			}
			formTable.addListBoxField(1, 0, sessionTypeListBox, bmoSession.getSessionTypeId());
			formTable.addListBoxField(2, 0, userListBox, bmoSession.getUserId());
			formTable.addTextAreaField(3, 0, descriptionTextArea, bmoSession.getDescription());
			formTable.addTextBoxReadOnlyField(4, 0, reservationsTextBox, bmoSession.getReservations());
			formTable.addCheckBoxField(5, 0, availableCheckBox, bmoSession.getAvailable());
			availableCheckBox.setEnabled(false);
			formTable.addDateTimeBoxField(6, 0, startDateBox, bmoSession.getStartDate());
			formTable.addDateTimeBoxField(7, 0, endDateBox, bmoSession.getEndDate());
			endDateBox.setEnabled(false);
			formTable.addButtonPanel(buttonPanel);
			statusEffect();
		}
		private void statusEffect() {
			calculateEndDate();
		}
		public void formListChange(ChangeEvent event) {
			calculateEndDate();
		}
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateBox)
				calculateEndDate();
		}
		private void calculateEndDate() {		
			// Calcular fecha de fin
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
		public void prepareSave() {
			try {
				bmoSession = new BmoSession();
				bmoSession.getDescription().setValue(descriptionTextArea.getText());
				bmoSession.getStartDate().setValue(startDateBox.getDateTime());
				bmoSession.getEndDate().setValue(endDateBox.getDateTime());		
				bmoSession.getReservations().setValue(reservationsTextBox.getText());
				bmoSession.getAvailable().setValue(availableCheckBox.getValue());
				bmoSession.getUserId().setValue(userListBox.getSelectedId());
				bmoSession.getSessionTypeId().setValue(sessionTypeListBox.getSelectedId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
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
					bmObjectServiceAsync.save(bmoSession.getPmClass(), bmoSession, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al crear Sesión: " + bmUpdateResult.errorsToString());
			else {
				sessionDialogBox.hide();
				reset();
			}
		}
	}*/
}