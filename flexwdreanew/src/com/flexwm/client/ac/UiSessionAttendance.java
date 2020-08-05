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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.cm.BmoCustomer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiSessionAttendance extends UiList {
	BmoSessionSale bmoSessionSale;
	BmoOrderSession bmoOrderSession = new BmoOrderSession();
	int attempt = 0;

	// Listado de OrderSession
	ArrayList<BmObject> orderSessionList;

	BmFilter sessionSaleFilter;
	Date startDate, endDate;

	// Dialogo de asistencia a sesion
	protected DialogBox orderSessionDialogBox;

	public UiSessionAttendance(UiParams uiParams) {
		super(uiParams, new BmoSessionSale());
		bmoSessionSale = (BmoSessionSale)getBmObject();
	}

	@Override
	public void postShow() {
		// Filtrar por Instructores
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + salesGroupId);	
		addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoSessionSale.getSalesUserId());

		addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoSessionSale.getCustomerId());

		addDateRangeFilterListBox(bmoSessionSale.getStartDate(), 
				GwtUtil.firstDayOfMonthToString(getUiParams().getSFParams().getDateFormat(), 0), 
				GwtUtil.firstDayOfMonthToString(getUiParams().getSFParams().getDateFormat(), +1));
	}

	// Muestra lista
	@Override
	public void displayList() {
		getOrderSessions(0);
	}

	// Obtiene la lista de sesiones desde la posicion 0
	public void getOrderSessions() {
		getOrderSessions(0);
	}

	// Obtiene lista de sesiones / pedido
	public void getOrderSessions(int attempt) {
		if (attempt < 5) {
			setAttempt(attempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getAttempt() < 5) {
						getOrderSessions(getAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-getOrderSessions()", caught);
					}
				}
				public void onSuccess(ArrayList<BmObject> result) {
					stopLoading();
					setAttempt(0);
					try {
						setOrderSessionData(result);
					} catch (BmException e) {
						e.printStackTrace();
					}
				}
			};
			try {

				// Prepara filtros
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

				// Filtra por rango de fechas
				sessionSaleFilter = getUiParams().getUiProgramParams(bmoSessionSale.getProgramCode()).searchFilter(bmoSessionSale.getKind(), bmoSessionSale.getStartDate().getName());

				BmFilter dateRangeFilter = new BmFilter();
				dateRangeFilter.setRangeFilter(bmoOrderSession.getKind(),
						bmoOrderSession.getBmoSession().getStartDate().getName(), 
						bmoOrderSession.getBmoSession().getStartDate().getLabel(), 
						sessionSaleFilter.getMinValue(), 
						sessionSaleFilter.getMaxValue());
				//dateRangeFilter.setKind(bmoOrderSession.getKind());
				//dateRangeFilter.setField(bmoOrderSession.getBmoSession().getStartDate().getName());
				filterList.add(dateRangeFilter);

				// Prepara ordenamiento
				ArrayList<BmOrder> orderList = new ArrayList<BmOrder>();
				orderList.add(new BmOrder(bmoOrderSession.getKind(), bmoOrderSession.getBmoSession().getStartDate(), BmOrder.ASC));

				// Busca el listado
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().list(bmoOrderSession.getPmClass(), 
							filterList, 
							bmoOrderSession.getSearchFields(), 
							"", 
							orderList, 
							-1,
							-1,
							callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-getOrderSessions()", e);
			}
		} 
	}

	// Prepara informacion de sesiones / pedido
	public void setOrderSessionData(ArrayList<BmObject> result) throws BmException {
		this.orderSessionList = result;
		finalRender();
	}

	// Muestra la informacion completa
	@SuppressWarnings("deprecation")
	public void finalRender() throws BmException {
		int col = 0;

		// Asigna fechas de la vista
		startDate = getDate(sessionSaleFilter.getMinValue().substring(0, 10), getUiParams().getSFParams().getDateFormat());
		endDate = getDate(sessionSaleFilter.getMaxValue().substring(0, 10), getUiParams().getSFParams().getDateFormat());
		Date currentDate = getDate(sessionSaleFilter.getMinValue().substring(0, 10), getUiParams().getSFParams().getDateFormat());

		// Header principal
		listFlexTable.addListTitleCell(0, 0, "Sesiones");
		listFlexTable.getFlexCellFormatter().setColSpan(0, 0, 6);

		listFlexTable.addListTitleCell(0, 1, "Mes / Día");
		listFlexTable.getFlexCellFormatter().setColSpan(0, 1, 33);

		// Header secundario
		listFlexTable.addListTitleCell(1, 0, "Clave");
		listFlexTable.addListTitleCell(1, 1, "Cliente");
		listFlexTable.addListTitleCell(1, 2, "Tipo");
		listFlexTable.addListTitleCell(1, 3, "Duracion");
		listFlexTable.addListTitleCell(1, 4, "Sesiones Máx.");
		listFlexTable.addListTitleCell(1, 5, "Ses. Disponibles");

		int i = 8;
		while (currentDate.before(endDate)) {
			HTML dayLabel = new HTML();

			String dayName = "";
			if (currentDate.getDay() == 1) dayName = "L";
			else if (currentDate.getDay() == 2) dayName = "M";
			else if (currentDate.getDay() == 3) dayName = "M";
			else if (currentDate.getDay() == 4) dayName = "J";
			else if (currentDate.getDay() == 5) dayName = "V";
			else if (currentDate.getDay() == 6) dayName = "S";
			else if (currentDate.getDay() == 0) dayName = "D";

			dayLabel.setHTML("<b>" + dayName + "</b><br>" 
					+ "&nbsp;" + DateTimeFormat.getFormat( "MM" ).format(currentDate)
					+ "<br>" 
					+ "&nbsp;" + DateTimeFormat.getFormat( "dd" ).format(currentDate));

			listFlexTable.setWidget(1, i, dayLabel);
			listFlexTable.getCellFormatter().addStyleName(1, i, "listHeaderCell");

			CalendarUtil.addDaysToDate(currentDate, 1);
			i++;
		}

		int row = 2;
		BmField availableField;
		availableField = new BmField("availableField", "", "Disponible", 11, Types.INTEGER, false, BmFieldType.ID, false);

		while (iterator.hasNext()) {
			col = 0;
			BmoSessionSale bmoSessionSale = (BmoSessionSale)iterator.next(); 
			availableField.setValue(bmoSessionSale.getMaxSessions().toInteger() - bmoSessionSale.getNoSession().toInteger());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoSessionSale.getBmoCustomer().getCode());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoSessionSale.getBmoCustomer().getDisplayName());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoSessionSale.getBmoSessionTypePackage().getName());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getDuration());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoSessionSale.getMaxSessions());
			listFlexTable.addListCell(row, col++, getBmObject(), availableField);

			renderDays(row, bmoSessionSale, endDate);

			listFlexTable.formatRow(row);
			row++;
		}
	}

	// Despliega días
	private void renderDays(int row, BmoSessionSale bmoSessionSale, Date endDate) {
		int i = 8;
		Date currentDate = getDate(sessionSaleFilter.getMinValue().substring(0, 10), getUiParams().getSFParams().getDateFormat());

		while (currentDate.before(endDate)) {
			listFlexTable.addListCell(row, i, setAttendanceLabel(row, i, bmoSessionSale, currentDate));	
			CalendarUtil.addDaysToDate(currentDate, 1);
			i++;
		}
	}

	// Busca si tiene sesion en el dia x
	private Label setAttendanceLabel(int row, int col, BmoSessionSale bmoSessionSale, Date currentDate) {
		Label l = new Label("-");

		// Recorre las sesiones de pedido
		Iterator<BmObject> orderSessionIterator = orderSessionList.iterator();
		while (orderSessionIterator.hasNext()) {
			BmoOrderSession nextBmoOrderSession = (BmoOrderSession)orderSessionIterator.next();

			// Si hay pedido
			if (nextBmoOrderSession.getOrderId().toInteger() == bmoSessionSale.getOrderId().toInteger()) {

				// Busca si hay fecha
				Date sessionDate = getDate(nextBmoOrderSession.getBmoSession().getStartDate().toString().substring(0,  10), getUiParams().getSFParams().getDateFormat());

				if (sessionDate.compareTo(currentDate) == 0) {
					prepareOrderSessionLabel(nextBmoOrderSession, l);

					l.addClickHandler(new UiOrderSessionClickHandler(row, col, bmoSessionSale, nextBmoOrderSession.getBmoSession(), nextBmoOrderSession, DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).format(currentDate), l) {
						@Override
						public void onClick(ClickEvent arg0) {
							addOrderSessionDialog(row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, label);
						}
					});
					return l;
				}
			} 
		}

		// No hay sesion de pedido en ese dia, mostrar boton para nueva
		l.setText("+");
		l.setTitle("Agregar Sesion el: " + DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).format(currentDate));
		l.setStyleName("listNewButton");
		l.addClickHandler(new UiOrderSessionClickHandler(row, col, bmoSessionSale, DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).format(currentDate), l) {
			@Override
			public void onClick(ClickEvent arg0) {
				addOrderSessionDialog(row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, label);
			}
		});
		return l;
	}

	// Asigna el estilo y texto al label de sesion de pedido
	private void prepareOrderSessionLabel(BmoOrderSession bmoOrderSession, Label l) {
		if (bmoOrderSession.getAttended().toBoolean()) {
			l.setText("@");						
		} else {
			l.setText("-");
		}

		if (bmoOrderSession.getType().equals(BmoOrderSession.TYPE_EXEMPT)) {
			l.setStyleName("listDisabledButton");
		} else if (bmoOrderSession.getType().equals(BmoOrderSession.TYPE_REPLACEMENT)) {
			l.setText("R");
			l.setStyleName("listWarningButton");
		} else {
			l.setStyleName("listEnabledButton");	
		}

		l.setTitle("Sesión: " + bmoOrderSession.getBmoSession().getStartDate().toString()
				+ ", Instructor: " + bmoOrderSession.getBmoSession().getBmoUser().getCode().toString());
	}

	// Obtiene fecha
	private Date getDate(String dateString, String format) {
		Date result = null;
		try {
			DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(format);
			result = dateTimeFormat.parse(dateString);
		} catch (Exception e) {
			showSystemMessage(this.getClass().getName() + "-getDate() Error " + e.toString() + ", date: ->" + dateString + "<-, format: " + format);
		}
		return result;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	// Agregar asistencia a sesion
	public void addOrderSessionDialog(int row, int col, BmoSessionSale bmoSessionSale, BmoSession bmoSession, BmoOrderSession bmoOrderSession, String sessionDate, Label dayLabel) {
		orderSessionDialogBox = new DialogBox(true);
		orderSessionDialogBox.setGlassEnabled(true);
		orderSessionDialogBox.setText("Asistencia a Sesion " + sessionDate);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "150px");

		orderSessionDialogBox.setWidget(vp);

		UiOrderSessionForm quoteGroupForm = new UiOrderSessionForm(getUiParams(), vp, row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, dayLabel);
		quoteGroupForm.show();

		orderSessionDialogBox.center();
		orderSessionDialogBox.show();
	}

	// Dialogo asistencia de sesion
	private class UiOrderSessionForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private int row, col;
		private Label dayLabel;
		private BmoOrderSession bmoOrderSession;
		private BmoSessionSale bmoSessionSale;
		private String sessionDate;
		// Sesion de Pedido existente		
		private UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		private CheckBox attendedCheckBox = new CheckBox();
		private UiListBox typeListBox = new UiListBox(getUiParams());
		private TextArea descriptionTextArea = new TextArea();
		private TextArea descriptionTextArea2 = new TextArea();
		private UiListBox sessionListBox = new UiListBox(getUiParams(), new BmoSession());
		private FlowPanel buttonPanel = new FlowPanel();
		private Button saveOrderSessionButton = new Button("ASIGNAR");
		private Button deleteOrderSessionButton = new Button("ELIMINAR");

		// Nueva sesion
		private BmoSession bmoSession = new BmoSession();
		private BmoSessionType bmoSessionType = new BmoSessionType();
		private UiDateTimeBox startDateBox = new UiDateTimeBox();
		private UiListBox userListBox = new UiListBox(getUiParams(), new BmoUser());
		private UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
		private Button newSessionButton = new Button("CREAR");

		private int bmoSessionSaleRpcAttempt = 0;

		String orderSessionSection = "Sesión Existente";
		String newSessionSection = "Nueva Sesion?";
		String dataSessionSection = "Sesiones";

		public UiOrderSessionForm(UiParams uiParams, Panel defaultPanel, int row, int col, BmoSessionSale bmoSessionSale, BmoSession bmoSession, BmoOrderSession bmoOrderSession, String sessionDate, Label dayLabel) {
			super(uiParams, defaultPanel);
			this.row = row;
			this.col = col;
			this.bmoSessionSale = bmoSessionSale;
			
			this.bmoSession = bmoSession;
			this.bmoOrderSession = bmoOrderSession;
			this.sessionDate = sessionDate;
			this.dayLabel = dayLabel;

			defaultPanel.add(formTable);

			// Boton de asignar a sesion existente
			saveOrderSessionButton.setStyleName("formSaveButton");
			saveOrderSessionButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSaveOrderSession();
				}
			});
			saveOrderSessionButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrderSession.getProgramCode())) 
				saveOrderSessionButton.setVisible(true);

			// Boton de eliminar asignacion
			deleteOrderSessionButton.setStyleName("formDeleteButton");
			deleteOrderSessionButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (Window.confirm("Desea eliminar el registro?")) 
						deleteOrderSession();
				}
			});
			deleteOrderSessionButton.setVisible(false);
			if (getSFParams().hasDelete(bmoOrderSession.getProgramCode())) 
				deleteOrderSessionButton.setVisible(true);

			// Boton de crear nueva sesion
			newSessionButton.setStyleName("formSaveButton");
			newSessionButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareNewSessionAction();
				}
			});
			// Filtro de fechas de sesion
			BmFilter filterBySessionDate = new BmFilter();
			filterBySessionDate.setRangeFilter(bmoSession.getKind(), bmoSession.getStartDate().getName(), 
					bmoSession.getStartDate().getLabel(), 
					sessionDate + " 00:00:00", 
					sessionDate + " 23:59:59");
			sessionListBox.addFilter(filterBySessionDate);

			// Filtrar por Instructores
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesGroupId);	
			userListBox.addFilter(filterSalesmen);
			
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userListBox.addFilter(filterUserActive);

			// Filtro de tipos de Sesion
			BmFilter filterBySessionType = new BmFilter();
			filterBySessionType.setValueFilter(bmoSessionType.getKind(), bmoSessionType.getIdField(), bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger());
		}
		
		public void show(){
			
			try {
				if (!(bmoSession.getId() > 0)) {
					bmoOrderSession.getOrderId().setValue(bmoSessionSale.getOrderId().toInteger());
					bmoOrderSession.getAttended().setValue(true);
					bmoSession.getStartDate().setValue(sessionDate);
					bmoSession.getSessionTypeId().setValue(bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger());
				} else {
					bmoOrderSession.getSessionId().setValue(bmoSession.getId());
				}

				if (bmoOrderSession.getId() > 0) {
					saveOrderSessionButton.setText("MODIFICAR");
				} else {
					deleteOrderSessionButton.setVisible(false);
				}
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
			}	
			
			// Traer datos actualizados
			getBmoSessionSale();
		}
		
		public void renderSessionAttendace(BmoSessionSale bmoSessionSale) {
			formTable.addSectionLabel(0, 0, dataSessionSection, 2);
			formTable.addLabelCell(1, 0, "# Sesiones Máximas: "+ bmoSessionSale.getMaxSessions().toInteger());
			formTable.addLabelCell(1, 1, "# Sesiones Asignadas: "+bmoSessionSale.getNoSession().toInteger() );
			formTable.addLabelCell(2, 0, "# Sesiones Disponibles: "+ (bmoSessionSale.getMaxSessions().toInteger()-bmoSessionSale.getNoSession().toInteger()));
			
			formTable.addSectionLabel(3, 0, orderSessionSection, 2);
			formTable.addField(4, 0, customerSuggestBox, bmoSessionSale.getCustomerId());
			formTable.addField(5, 0, sessionListBox, bmoOrderSession.getSessionId());			
			formTable.addField(6, 0, attendedCheckBox, bmoOrderSession.getAttended());
			
			formTable.addField(7, 0, typeListBox, bmoOrderSession.getType());
			formTable.addField(8, 0, descriptionTextArea, bmoOrderSession.getDescription());
			
			buttonPanel.add(saveOrderSessionButton);
			buttonPanel.add(deleteOrderSessionButton);
			formTable.addPanel(10, 1, buttonPanel);

			// Solo mostrar crear nueva sesion si no esta seleccionada una ya
			if (!(bmoSession.getId() > 0)) {
				formTable.addSectionLabel(11, 0, newSessionSection, 2);
				formTable.addField(12, 0, sessionTypeListBox, bmoSession.getSessionTypeId());
				formTable.addField(13, 0, userListBox, bmoSession.getUserId());
				formTable.addField(14, 0, startDateBox, bmoSession.getStartDate());
				formTable.addLabelField(15, 0, "Duración", bmoSessionSale.getBmoSessionTypePackage().getBmoSessionType().getDuration().toString());
				formTable.addField(16, 0, descriptionTextArea2, bmoOrderSession.getDescription());
				
				formTable.addButtonCell(17, 1, newSessionButton);
			}

			formTable.hideSection(newSessionSection);
			customerSuggestBox.setEnabled(false);
			sessionTypeListBox.setEnabled(false);
		}
		
		public void getBmoSessionSale() {
			getBmoSessionSale(0);
		}
		public void getBmoSessionSale(int getBmoSessionSaleRpcAttempt) {
			if (getBmoSessionSaleRpcAttempt < 5) {
				setBmoSessionSaleRpcAttempt(getBmoSessionSaleRpcAttempt + 1);
			}
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoSessionSaleRpcAttempt() < 5) getBmoSessionSale(getBmoSessionSaleRpcAttempt());
					//if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-getBmoSessionSale(), ERROR: " + caught.toString());
					
				}

				public void onSuccess(BmObject result) {
					stopLoading();	
					setBmoSessionSaleRpcAttempt(0);
					bmoSessionSale = (BmoSessionSale)result;
					renderSessionAttendace(bmoSessionSale);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoSessionSale.getPmClass(), bmoSessionSale.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoSessionSale(), ERROR: " + e.toString());
			}
		}

		// Almacena nuevo registro de order session
		public void prepareSaveOrderSession() {
			try {
				bmoOrderSession.getAttended().setValue(attendedCheckBox.getValue());
				bmoOrderSession.getType().setValue(typeListBox.getSelectedCode());
				
				bmoOrderSession.getDescription().setValue(descriptionTextArea.getText());
				bmoOrderSession.getSessionId().setValue(sessionListBox.getSelectedId());
				bmoOrderSession.getOrderId().setValue(bmoSessionSale.getOrderId().toInteger());
				saveOrderSession();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		// Almacena sesion de pedido
		public void saveOrderSession() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

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
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		// Procesa resultados de guardar sesion de pedido
		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderSessionDialogBox.hide();
				bmoOrderSession = (BmoOrderSession)bmUpdateResult.getBmObject();

				dayLabel = new Label();
				prepareOrderSessionLabel(bmoOrderSession, dayLabel);

				Date sessionDate = getDate(bmoOrderSession.getBmoSession().getStartDate().toString(), getUiParams().getSFParams().getDateTimeFormat());

				dayLabel.addClickHandler(new UiOrderSessionClickHandler(row, col, bmoSessionSale, bmoOrderSession.getBmoSession(), bmoOrderSession, GwtUtil.dateToString(sessionDate, getUiParams().getSFParams().getDateFormat()), dayLabel) {
					@Override
					public void onClick(ClickEvent arg0) {
						addOrderSessionDialog(row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, label);
					}
				});

				listFlexTable.setWidget(row, col, dayLabel);
			}
		}

		// Almacena sesion de pedido
		public void deleteOrderSession() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processDeleteResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().delete(bmoOrderSession.getPmClass(), bmoOrderSession, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + e.toString());
			}
		}

		// Procesa resultados de guardar sesion de pedido
		public void processDeleteResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showErrorMessage(this.getClass().getName() + "-processDeleteResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderSessionDialogBox.hide();

				Date sessionDate = getDate(bmoSession.getStartDate().toString(), getUiParams().getSFParams().getDateTimeFormat());

				// No hay sesion de pedido en ese dia, mostrar boton para nueva
				Label l = new Label();
				l.setText("+");
				l.setTitle("Agregar Sesion el: " + DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).format(sessionDate));
				l.setStyleName("listNewButton");
				l.addClickHandler(new UiOrderSessionClickHandler(row, col, bmoSessionSale, DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).format(sessionDate), l) {
					@Override
					public void onClick(ClickEvent arg0) {
						addOrderSessionDialog(row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, label);
					}
				});

				listFlexTable.setWidget(row, col, l);
			}
		}

		// Almacena nueva sesion
		public void prepareNewSessionAction() {
			try {
				bmoSession.getSessionTypeId().setValue(bmoSessionSale.getBmoSessionTypePackage().getSessionTypeId().toInteger());
				bmoSession.getStartDate().setValue(startDateBox.getDateTime());
				bmoSession.getUserId().setValue(userListBox.getSelectedId());
				
				newSessionAction();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareNewSessionAction() ERROR: " + e.toString());
			}
		}

		public void newSessionAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-newSessionAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processNewSessionActionUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					if(descriptionTextArea2.getText().equals(""))
						descriptionTextArea2.setText(" ");
					getUiParams().getBmObjectServiceAsync().action(bmoSession.getPmClass(), bmoSession, BmoSession.ACTION_NEWSESSIONWITHSALE, "" + bmoSessionSale.getId()+"|"+descriptionTextArea2.getText(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-newSessionAction() ERROR: " + e.toString());
			}
		}

		public void processNewSessionActionUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processNewSessionActionUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderSessionDialogBox.hide();
				bmoOrderSession = (BmoOrderSession)bmUpdateResult.getBmObject();

				dayLabel = new Label();
				prepareOrderSessionLabel(bmoOrderSession, dayLabel);

				Date sessionDate = getDate(bmoOrderSession.getBmoSession().getStartDate().toString(), getUiParams().getSFParams().getDateTimeFormat());

				dayLabel.addClickHandler(new UiOrderSessionClickHandler(row, col, bmoSessionSale, bmoOrderSession.getBmoSession(), bmoOrderSession, GwtUtil.dateToString(sessionDate, getUiParams().getSFParams().getDateFormat()), dayLabel) {
					@Override
					public void onClick(ClickEvent arg0) {
						addOrderSessionDialog(row, col, bmoSessionSale, bmoSession, bmoOrderSession, sessionDate, label);
					}
				});

				listFlexTable.setWidget(row, col, dayLabel);
			}
		}
		
		// Variables para llamadas RPC
		public int getBmoSessionSaleRpcAttempt() {
			return bmoSessionSaleRpcAttempt;
		}

		public void setBmoSessionSaleRpcAttempt(int bmoSessionSaleRpcAttempt) {
			this.bmoSessionSaleRpcAttempt = bmoSessionSaleRpcAttempt;
		}
	}
}

