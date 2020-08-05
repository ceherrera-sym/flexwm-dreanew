//package com.flexwm.client.op;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Iterator;
//import com.flexwm.client.op.UiServiceOrder.UiServiceOrderForm.ServiceOrderUpdater;
//import com.flexwm.shared.op.BmoServiceOrder;
//import com.flexwm.shared.op.BmoServiceOrderReportTime;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.rpc.StatusCodeException;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.Panel;
//import com.google.gwt.user.client.ui.ScrollPanel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.symgae.client.ui.UiClickHandler;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.fields.UiTextBox;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.SFException;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class UiServiceOrderReportTime extends UiList {
//	BmoServiceOrderReportTime bmoServiceOrderReportTime;
//	BmoServiceOrder bmoServiceOrder;
//	protected ServiceOrderUpdater serviceOrderUpdater;		
//	Image multipleDeleteButton = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "./icons/delete.png"));
//	Image backwardButton = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "./icons/backward.png"));
//	int totalCols = 0;
//	boolean enableActionBar = true;
//
//
//	public UiServiceOrderReportTime(UiParams uiParams) {
//		super(uiParams, new BmoServiceOrderReportTime());
//		bmoServiceOrderReportTime = (BmoServiceOrderReportTime)getBmObject();
//		initialize();
//	}
//
//	public UiServiceOrderReportTime(UiParams uiParams, Panel defaultPanel, BmoServiceOrder bmoServiceOrder, boolean advanceList, ServiceOrderUpdater serviceOrderUpdater) {
//		super(uiParams, defaultPanel, new BmoServiceOrderReportTime());
//		bmoServiceOrderReportTime = (BmoServiceOrderReportTime)getBmObject();
//		this.bmoServiceOrder = bmoServiceOrder;
//		this.serviceOrderUpdater = serviceOrderUpdater;
//		initialize();
//	}
//
//	public void initialize() {
//
////		listFlexTable = new UiListFlexTable(getUiParams(), uiListUpdater, rowClickHandler);
//
////		listActionCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
////			@Override
////			public void onValueChange(ValueChangeEvent<Boolean> event) {
////				listFlexTable.listCheckBoxTooggle(event.getValue());
////				toggleActionPanel();
////			}
////		});
//
//		multipleDeleteButton.setTitle("Eliminar Registros.");
//		multipleDeleteButton.setStyleName("listAllImage");
//		multipleDeleteButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (!isLoading()) {
//					if (Window.confirm("Esta seguro que desea Eliminar los registros seleccionados?"))
//						listDelete();
//				}
//			}
//		});
//	}
//	
//
//	// Muestra los botones de accion 
////	public void toggleActionPanel() {
////		// Lista de tipo columnas
////		if (listFlexTable.hasSelectedListCheckBox()) {
////			listFlexTable.removeRow(0);
////			listFlexTable.insertRow(0);
////			prepareRowHeader();
////			listFlexTable.setWidget(0, 2, actionPanel);
////
////			actionPanel.setVisible(true);
////		} else {
////			listFlexTable.removeRow(0);
////			listFlexTable.insertRow(0);
////			prepareColumnHeader();
////		}
////	}
//
//	// Agrega el header de columnas 
////	private void prepareRowHeader() {
////		listFlexTable.setWidget(0, 0, listActionCheckBox);
////		listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");
////		listFlexTable.addListTitleCell(0, 1, getSFParams().getProgramTitle(getBmObject()));
////		listFlexTable.addListTitleCell(0, 2, " ");
////		listFlexTable.getCellFormatter().addStyleName(0, 2, "listLastColumn");
////		listFlexTable.getFlexCellFormatter().setColSpan(0, 2, totalCols);
////	}
//
//	// Agrega el header de columnas 
//	private void prepareColumnHeader() {
//		int col = 0;
////		if (enableActionBar) {
////			listFlexTable.setWidget(0, 0, listActionCheckBox);
////			listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");
////			listFlexTable.getCellFormatter().addStyleName(0, col++, "listHeaderFirstColumn");
////		}
//
//		addColumnHeader(col++, bmoServiceOrderReportTime.getComments());
////		if (!isMobile()) 
////			addColumnHeader(col++, bmoServiceOrderReportTime.getUserId());
//		if (!isMobile()) 
//			addColumnHeader(col++, bmoServiceOrderReportTime.getType());
//		addColumnHeader(col++, "Avanzar");
//		if (!isMobile()) 
//			addColumnHeader(col++, bmoServiceOrderReportTime.getDateAndTime());
//
//		addColumnHeader(col++, bmoServiceOrderReportTime.getRealTime());
//	}
//
//	@Override
//	public void displayList() {
////		listActionCheckBox.setValue(false);
////		listFlexTable.resetListCheckBoxList();
//		int row = 1,col = 0;
//
//		// Prepara encabezado de la lista
//		prepareColumnHeader();
//		while (iterator.hasNext()) {
//			col = 0;
//			BmoServiceOrderReportTime bmoServiceOrderReportTime = (BmoServiceOrderReportTime)iterator.next(); 
//
////			if (enableActionBar) {
////				CheckBox checkBox = new CheckBox();
////				checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
////					@Override
////					public void onValueChange(ValueChangeEvent<Boolean> event) {
////						toggleActionPanel();
////					}
////				});
////				UiListCheckBox uiListCheckBox = new UiListCheckBox(getBmObject(), checkBox);
////				listFlexTable.addListCheckBox(uiListCheckBox);
////				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
////				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
////				listFlexTable.getCellFormatter().addStyleName(row, 0, "listFirstColumn");
////				col++;
////			}
//
//			String linkValue = bmoServiceOrderReportTime.getComments().getValue();
//			if (linkValue == null || linkValue.equalsIgnoreCase(""))
//				linkValue = "Editar";
//			Label linkLabel = new Label(linkValue);
//			linkLabel.addClickHandler(rowClickHandler);
//			linkLabel.setStyleName("listCellLink");
//			listFlexTable.setWidget(row, col++, linkLabel);
//
////			listFlexTable.addListCell(row, col++, getBmObject(), bmoServiceOrderReportTime.getUserId());
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoServiceOrderReportTime.getType());
//			
//			// Imagen para avanzar
//			String iconTime = "", title = "";
//			try {
//				// 
//				if (bmoServiceOrderReportTime.getType().toString().equals("" + BmoServiceOrderReportTime.TYPE_START)) {
//					iconTime = "./icons/srrt_type_stop.png";
//					title = "Detener";
//						bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_STOP);
//				} else if (bmoServiceOrderReportTime.getType().toString().equals("" + BmoServiceOrderReportTime.TYPE_STOP)) {
//					iconTime = "./icons/srrt_type_play.png";
//					title = "Iniciar";
//					bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_START);
//				} else if (bmoServiceOrderReportTime.getType().toString().equals("" + BmoServiceOrderReportTime.TYPE_MANUAL)) {
//					iconTime = "./icons/srrt_type_play.png"; 
//					title = "Inicar";
//					bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_START);
//				}
//			} catch (BmException e) {
//				showSystemMessage("No se pudo obtener el tipo de Reporte.");
//			}
//			
//			// Bloquear
//			if (bmoServiceOrderReportTime.getChildId().toInteger() > 0) {
//				iconTime = "./icons/status_expired.png";
//				title = "Ya tiene avance";
//			}
//			
//			Image newImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), iconTime));
//			newImage.setStyleName("formLabelImage");
//			newImage.setTitle(title);
//			newImage.addClickHandler(new UiClickHandler(bmoServiceOrderReportTime) {
//				public void onClick(ClickEvent event) {
//					if (!(bmoServiceOrderReportTime.getChildId().toInteger() > 0)) {
//						advanceServiceOrderReportTime(bmObject, bmoServiceOrderReportTime.getType().toChar());
//					}
//				}
//			});
//			listFlexTable.setWidget(row, col, newImage);
//			listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellImage");
//			
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoServiceOrderReportTime.getDateAndTime());
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoServiceOrderReportTime.getRealTime());
//
//			if (totalCols < col) totalCols = col;
//			listFlexTable.formatRow(row);
//			row++;
//		}
//	}
//
//	@Override
//	public void create() {
//		UiServiceOrderReportTimeForm uiServiceOrderReportTimeForm = new UiServiceOrderReportTimeForm(getUiParams(), 0, false);
//		uiServiceOrderReportTimeForm.show();
//	}
//
//	@Override
//	public void open(BmObject bmObject) {
//		UiServiceOrderReportTimeForm uiServiceOrderReportTimeForm = new UiServiceOrderReportTimeForm(getUiParams(), bmObject.getId(), false);
//		uiServiceOrderReportTimeForm.show();
//	}
//
//	@Override
//	public void postShow() {
//		// Deshabilitar boton porque este no entra al delete del objeto
//		deleteImage.setVisible(false); 
//		// Mostrar boton para borrar registros(este boton entra al delete de la clase)
//		if (getUiParams().getSFParams().hasDelete(getBmObject().getProgramCode()))	{
//			actionItems.add(multipleDeleteButton);
//		}
//
//		if (bmoServiceOrder.getHasReportTime().toInteger() > 0) {
//			newImage.setVisible(false);
//		}
//	}
//
//	// Borrar las seleccionadas
//	public void listDelete() {
//		boolean errors = false;
//		if (listFlexTable.getListCheckBoxSelectedBmObjectList().size() > 1) {
//			showSystemMessage ("<b>Solo puede seleccionar un Registro.</b>");
//		} else {
//			String values = "";
//			Iterator<BmObject> listSelected = listFlexTable.getListCheckBoxSelectedBmObjectList().iterator();
//			if (listSelected.hasNext()) {
//				BmoServiceOrderReportTime bmoServiceOrderReportTime = (BmoServiceOrderReportTime)listSelected.next();
//				showSystemMessage ("registro_ID : "+bmoServiceOrderReportTime.getId());
//
//				if (bmoServiceOrderReportTime.getChildId().toInteger() > 0 && !(bmoServiceOrderReportTime.getId() > 0)) {
//					errors = true;
//					showErrorMessage("<b>No se puede borrar este registro, está ligado a un registro posterior.</b>");
//				} else
//					values += bmoServiceOrderReportTime.getId() + "|";
//			}
//			// Mandar a borrar
//			if (!errors) listDelete(values);
//		}
//	}
//
//	// Accion de eliminar registros
//	public void listDelete(String values) {
//		// Llamada al servicio RPC
//		try {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {
//					} else
//						showErrorMessage("Error al pagar los registros: " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					if (result.hasErrors())
//						showErrorMessage(result.errorsToString());
//					else {
//						 // Actualizar todo el registro del SRO
//						if (serviceOrderUpdater != null)
//							serviceOrderUpdater.update();
//					}
//				}
//			};
//
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().action(bmoServiceOrderReportTime.getPmClass(), bmoServiceOrderReportTime,
//						BmoServiceOrderReportTime.ACTION_MULTIPLEDELETE, values, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-listDelete() ERROR: " + e.toString());
//		}
//	}
//	
//	private void advanceServiceOrderReportTime(BmObject bmObject, char type) {
//		bmoServiceOrderReportTime = (BmoServiceOrderReportTime)bmObject;
//		UiServiceOrderReportTimeForm uiServiceOrderReportTimeForm = new UiServiceOrderReportTimeForm(getUiParams(), 
//					bmoServiceOrderReportTime.getId(), bmoServiceOrderReportTime.getComments().toString(), true);
//		uiServiceOrderReportTimeForm.actionTimer(type);
//		// Actualizar todo el registro del SRO
////		if (serviceOrderUpdater != null)
////			serviceOrderUpdater.update();
//		
////		uiServiceOrderReportTimeForm.updateFormSRO();
//	}
//
//	public class UiServiceOrderReportTimeForm extends UiFormDialog {
//
//		UiTextBox noSroUiTextBox = new UiTextBox();
//		UiSuggestBox userSROUiSuggestBox = new UiSuggestBox(new BmoUser());
//		UiListBox typeSROUiListBox= new UiListBox(getUiParams());
//		UiListBox typeUiListBox= new UiListBox(getUiParams());
//		UiTextBox activityUiTextBox = new UiTextBox();
//		UiTextBox projectStepRfquTextBox = new UiTextBox();
//		TextArea commentsTextArea = new TextArea();
//		UiSuggestBox userIdUiSuggestBox = new UiSuggestBox(new BmoUser());
//
//		UiTextBox hoursUiTextBox = new UiTextBox();
//		UiTextBox minutesUiTextBox = new UiTextBox();
//		Label hourFormatLabel= new Label("HH");
//		Label colonFormatLabel= new Label(":");
//		Label colonTimerLabel= new Label(":");
//		Label minuteFormatLabel= new Label("mm");
//		BmoServiceOrderReportTime bmoServiceOrderReportTime;
//
//		HorizontalPanel timerManualHP = new HorizontalPanel();
//		HorizontalPanel timerManualLabelsHP = new HorizontalPanel();
//
//		//		private int saveRpcAttempt = 0;	
//		String sroSection = "Datos SRO";
//		String detailSection = "Detalle";
//		String comments = "";
//
//		boolean validateFieldTimerHr = true;
//		boolean validateFieldTimerMin = true;
//		boolean viewFromList = false; 
//
//		// Cronometro
//		//		Timer t;
//		private FlowPanel timerButtonPanel = new FlowPanel();
//		private Button timerStartButton = new Button("INICIAR");
//		private Button timerStopButton = new Button("DETENER");
//
//		private Button timerManualButton = new Button("MANUAL");
//		private Button timerSaveButton = new Button("GUARDAR");
//		private Button timerCancelButton = new Button("CANCELAR");
//
//		public UiServiceOrderReportTimeForm(UiParams uiParams, int id, boolean viewFromList) {
//			super(uiParams, new BmoServiceOrderReportTime(), id);
//			bmoServiceOrderReportTime = (BmoServiceOrderReportTime)getBmObject();
//			this.viewFromList = viewFromList;
//			initiaze();
//		}
//		
//		public UiServiceOrderReportTimeForm(UiParams uiParams, int id, String comments, boolean viewFromList) {
//			super(uiParams, new BmoServiceOrderReportTime(), id);
//			bmoServiceOrderReportTime = (BmoServiceOrderReportTime)getBmObject();
//			this.viewFromList = viewFromList;
//			this.comments = comments;
//			initiaze();
//		}
//
//		// EJEMPLO de TIMER
//		//		  public void onClick(ClickEvent event) {
//		//		    // Create a new timer that calls Window.alert().
//		//		    Timer t = new Timer() {
//		//		      @Override
//		//		      public void run() {
//		//		        Window.alert("Nifty, eh?");
//		//		      }
//		//		    };
//		//
//		//		    // Schedule the timer to run once in 5 seconds.
//		//		    t.schedule(5000);
//		//		  }
//
//		private void initiaze() {
//
//			// INICIAR
//			timerStartButton.setStyleName("startTimeButton");
//			timerStartButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					//					t = new Timer() {
//					//						@Override
//					//						public void run() {
//					actionTimer(BmoServiceOrderReportTime.TYPE_START);
//					//						}
//					//					};
//					//					t.run();
//				}
//			});
//
//			// DETENER
//			timerStopButton.setStyleName("stopTimerButton");
//			timerStopButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					//					t = new Timer() {
//					//						@Override
//					//						public void run() {
//					//							t.cancel();
//					actionTimer(BmoServiceOrderReportTime.TYPE_STOP);
//					//						}
//					//					};
//					//					t.run();
//				}
//			});
//
//			// GUARDAR MANUAL
//			timerSaveButton.setStyleName("formSaveButton");
//			timerSaveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					actionTimer(BmoServiceOrderReportTime.TYPE_MANUAL);
//				}
//			});
//
//			// MANUAL
//			timerManualButton.setStyleName("formSaveButton");
//			timerManualButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					timerStartButton.setVisible(false);
//					timerStopButton.setVisible(false);
//					timerManualButton.setVisible(false);
//					timerCancelButton.setVisible(true);
//					timerSaveButton.setVisible(false);
//
//					hoursUiTextBox.setVisible(true);
//					colonTimerLabel.setVisible(true);
//					minutesUiTextBox.setVisible(true);
//					backwardButton.setVisible(false);
//
//					timerManualLabelsHP.setVisible(true);
//					timerManualHP.setVisible(true);
//					// Asignar hora y minuto de las horas reales
//					setManualTime();
//				}
//			});
//
//			timerCancelButton.setStyleName("formSaveButton");
//			timerCancelButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					statusEffect();
//				}
//			});
//
//			// Al cambiar Hora
//			ValueChangeHandler<String> hoursDateHandler = new ValueChangeHandler<String>() {
//				@Override
//				public void onValueChange(ValueChangeEvent<String> event) {
//					if (validateFieldTimer(hoursUiTextBox)) {
//						timerSaveButton.setVisible(true);
//						backwardButton.setVisible(true);
//					} else {
//						timerSaveButton.setVisible(false);
//						backwardButton.setVisible(false);
//					}
//				}
//			};
//			hoursUiTextBox.addValueChangeHandler(hoursDateHandler);
//
//			// Al cambiar Minuto
//			ValueChangeHandler<String> minutesDateHandler = new ValueChangeHandler<String>() {
//				@Override
//				public void onValueChange(ValueChangeEvent<String> event) {
//					if (validateFieldTimer(minutesUiTextBox)) {
//						timerSaveButton.setVisible(true);
//						backwardButton.setVisible(true);
//					} else { 
//						timerSaveButton.setVisible(false);
//						backwardButton.setVisible(false);
//					}
//				}
//			};
//			minutesUiTextBox.addValueChangeHandler(minutesDateHandler);
//
//			// MANUAL
//			backwardButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					// Ocultar boton de guardar/refrescar
//					timerSaveButton.setVisible(false);
//					backwardButton.setVisible(false);
//					// Regresar hora y minuto de las horas reales
//					hoursUiTextBox.removeStyleName("dateBoxFormatError");
//					minutesUiTextBox.removeStyleName("dateBoxFormatError");
//					setManualTime();
//				}
//			});
//
//			// Agregar botones al panel
//			timerButtonPanel.add(timerStartButton);
//			timerButtonPanel.add(timerSaveButton);
//			timerButtonPanel.add(timerStopButton);
//			timerButtonPanel.add(timerManualButton);
//			timerButtonPanel.add(timerCancelButton);
//
//			// Diseño de label/caja horas:minutos 
//			hoursUiTextBox.setName("hour");
//			minutesUiTextBox.setName("minute");
//			backwardButton.setTitle("Regresar cambios.");
//			backwardButton.getElement().getStyle().setProperty("padding", "10px");
//
//			hoursUiTextBox.setMaxLength(2);
//			hoursUiTextBox.setText("00");
//			hoursUiTextBox.setStyleName("textBoxTimerHour");
//
//			minutesUiTextBox.setMaxLength(2);
//			minutesUiTextBox.setText("00");
//			minutesUiTextBox.setStyleName("textBoxTimerHour");
//
//			// Dar formato a los labels
//			hourFormatLabel.setStyleName("formatTimeLabel");
//			colonFormatLabel.setStyleName("formatTimeLabel");
//			colonTimerLabel.setStyleName("labelColonTimer");
//			minuteFormatLabel.setStyleName("formatTimeLabel");
//			// Añadir labels al panel
//			timerManualLabelsHP.add(hourFormatLabel);
//			timerManualLabelsHP.add(colonFormatLabel);
//			timerManualLabelsHP.add(minuteFormatLabel);
//			timerManualLabelsHP.setStyleName("formatTimeLabel");
//			// Añadir los widgets al panel 
//			timerManualHP.add(hoursUiTextBox);
//			timerManualHP.add(colonTimerLabel);
//			timerManualHP.add(minutesUiTextBox);
//			timerManualHP.add(backwardButton);
//			
//		}
//
//		@Override
//		public void populateFields() {
//			// Sobrescribir el tamaño de la ventana(dialog)
//			ScrollPanel scrollPanel = new ScrollPanel();
//			scrollPanel.setSize("400px", "400px");
//			scrollPanel.setWidget(formPanel);
//			formDialogBox.setWidget(scrollPanel);
//			formDialogBox.center();
//
//			bmoServiceOrderReportTime = (BmoServiceOrderReportTime)getBmObject();
//			
//			if (!viewFromList) {
//			
//				// Modifica titulo
//				if (!newRecord)
//					formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoServiceOrder.getCode());
//	
//				formFlexTable.addSectionLabel(0, 0, sroSection, 2);
//				formFlexTable.addField(1, 0, noSroUiTextBox, bmoServiceOrder.getCode());
//				formFlexTable.addField(2, 0, userSROUiSuggestBox, bmoServiceOrder.getUserId());
//				formFlexTable.addField(3, 0, typeSROUiListBox, bmoServiceOrder.getType());
//				formFlexTable.addField(4, 0, activityUiTextBox, bmoServiceOrder.getActivity());
//				if (bmoServiceOrder.getProjectActivityId().toInteger() > 0)
//					formFlexTable.addField(5, 0, projectStepRfquTextBox, bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getCode());
////				else if (bmoServiceOrder.getRfquId().toInteger() > 0)
////					formFlexTable.addField(5, 0, projectStepRfquTextBox, bmoServiceOrder.getBmoRFQU().getCodeRFQU());
//	
//				formFlexTable.addSectionLabel(6, 0, detailSection, 2);
//			}
////			formFlexTable.addField(7, 0, typeUiListBox, bmoServiceOrderReportTime.getType());
//			formFlexTable.addField(8, 0, commentsTextArea, bmoServiceOrderReportTime.getComments());
//			//			formFlexTable.addField(9, 0, dateAndTimeUiDateTimeBox, bmoServiceOrderReportTime.getDateAndTime());
//
//			formFlexTable.addPanel(10, 1, timerManualLabelsHP, 2);
//			formFlexTable.addPanel(11, 1, timerManualHP, 2);
//			formFlexTable.addPanel(12, 1, timerButtonPanel);
//			formFlexTable.getCellFormatter().removeStyleName(10, 1, "formPanel");
//			formFlexTable.getCellFormatter().removeStyleName(11, 1, "formPanel");
//			formFlexTable.getCellFormatter().removeStyleName(12, 1, "formPanel");
//
//			statusEffect();
//		}
//
//		@Override
//		public BmObject populateBObject() throws BmException {
//			bmoServiceOrderReportTime.setId(id);
//			bmoServiceOrderReportTime.getServiceOrderId().setValue(bmoServiceOrder.getId());
////			bmoServiceOrderReportTime.getType().setValue(typeUiListBox.getSelectedCode());
//			bmoServiceOrderReportTime.getComments().setValue(commentsTextArea.getText());
//			//			bmoServiceOrderReportTime.getDateAndTime().setValue(dateAndTimeUiDateTimeBox.getDateTime());
//
//			return bmoServiceOrderReportTime;
//		}
//
//		private void statusEffect() {
//
//			noSroUiTextBox.setEnabled(false);
//			userSROUiSuggestBox.setEnabled(false);
//			typeSROUiListBox.setEnabled(false);
//			typeUiListBox.setEnabled(false);
//			activityUiTextBox.setEnabled(false);
//			noSroUiTextBox.setEnabled(false);
//			projectStepRfquTextBox.setEnabled(false);
//			formFlexTable.hideSection(sroSection);
//
//			colonTimerLabel.setVisible(false);
//			hoursUiTextBox.setVisible(false);
//			minutesUiTextBox.setVisible(false);
//			timerManualLabelsHP.setVisible(false);
//			timerManualHP.setVisible(false);
//
//			if (!newRecord) {
//				if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_START) {
//					timerStopButton.setVisible(true);
//					timerStartButton.setVisible(false);
//					timerSaveButton.setVisible(false);
//					timerManualButton.setVisible(false);
//					timerCancelButton.setVisible(false);
//				} else if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_STOP) {
//					timerStartButton.setVisible(true);
//					timerSaveButton.setVisible(false);
//					timerStopButton.setVisible(false);
//					timerManualButton.setVisible(true);
//					timerCancelButton.setVisible(false);
//				} else if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_MANUAL) {
//					timerStartButton.setVisible(true);
//					timerSaveButton.setVisible(false);
//					timerStopButton.setVisible(false);
//					timerManualButton.setVisible(true);
//					timerCancelButton.setVisible(false);
//				}
//			} else {
//				timerStartButton.setVisible(true);
//				timerStopButton.setVisible(false);
//				timerSaveButton.setVisible(false);
//				timerManualButton.setVisible(false);
//				timerCancelButton.setVisible(false);
//			}
//		}
//
//		// Validaciones en el Timer manual (widgets de textBox)
//		public boolean validateFieldTimer(TextBox textBox) {
//			boolean validate = true;
//			// Si no hay nada, poner en ceros
//			if (textBox.getText().trim().equals("")) {
//				textBox.setText("00");
//			} else {
//				if (!isNumeric(textBox.getText())) {
//					validate = false;
//					if (textBox.getName().equals("hour")) 
//						showSystemMessage("Error de formato: la hora no es un número.");
//					else
//						showSystemMessage("Error de formato: el minuto no es un número.");
//
//				} else {
//					//					if (textBox.getName().equals("hour")) {
//					//						// Validar horario que no este fuera del rango del dia
//					//						if (Integer.parseInt(textBox.getText()) > 23
//					//								||  Integer.parseInt(textBox.getText()) < 0) {
//					//							validate = false;
//					//							showSystemMessage("Hora indefinida.");
//					//						}
//					//					} else if (textBox.getName().equals("minute")) {
//					//						// Validar horario que no este fuera del rango del dia
//					//						if (Integer.parseInt(textBox.getText()) > 59
//					//								||  Integer.parseInt(textBox.getText()) < 0) {
//					//							validate = false;
//					//							showSystemMessage("Minuto indefinido.");
//					//						}
//					//					}
//				}
//			}
//
//			// Si no hay error, formatear hora, si hay error colorear hora
//			if (validate) {
//				textBox.setText(codeFormatDigits(Integer.parseInt(textBox.getText()), 2));
//				textBox.setStyleName("textBoxTimerHour");
//
//				// Validar caja de texto de hora/minuto
//				if (textBox.getName().equals("hour")) {
//					// Validar que no sea la misma hora y min
//					if ((Integer.parseInt(textBox.getText()) == getHourNow()
//							&& Integer.parseInt(minutesUiTextBox.getText()) == getMinuteNow())) {
//						validate = false;
//						//						setValidateFieldTimerHr(false);
//					} 
//					//					else setValidateFieldTimerHr(true);
//				} else if (textBox.getName().equals("minute")) {
//					// Validar que no sea el mismo minuto					
//					if (Integer.parseInt(textBox.getText()) == getMinuteNow()
//							&& Integer.parseInt(hoursUiTextBox.getText()) == getHourNow()) {
//						validate = false;
//						//						setValidateFieldTimeMin(false);
//					} 
//					//					else setValidateFieldTimeMin(true);
//				}
//
//			} else {
//				textBox.addStyleName("dateBoxFormatError");
//			}
//
//			return validate;
//		}
//
//		// Validar si es numero entero
//		private boolean isNumeric(String cadena){
//			try {
//				Integer.parseInt(cadena);
//				return true;
//			} catch (NumberFormatException nfe){
//				return false;
//			}
//		}
//
//		// Aplicar a los widgets hora y minuto
//		public void setManualTime() {
//			hoursUiTextBox.setText("" + codeFormatDigits(getHourNow(), 2));
//			minutesUiTextBox.setText("" + codeFormatDigits(getMinuteNow(), 2));
//		}
//
//		public int partInteger(double number) {
//			int partInteger = 0;
//			BigDecimal bd = new BigDecimal(String.valueOf(number));
//			BigDecimal intPart = new BigDecimal(bd.toBigInteger());
//			partInteger = Integer.parseInt("" + intPart);
//			return partInteger;
//		}
//
//		// Obtener horas de las horas reales del sro
//		private int getHourNow() {
//			return partInteger(bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toDouble());
//		}
//
//		// Obtener minutos de las horas reales del sro
//		private int getMinuteNow() {
//			String realTime = bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toString();
//			// Obtener decimal(min) de la hora
//			double min = Double.parseDouble(realTime.substring(realTime.toString().indexOf('.')));
//			// Convertir horas a minutos
//			return (int)roundDecimal((min * 60), 0);
//		}
//
//		// Redondea un valor
//		public double roundDecimal(double value, int places) {
//			if (places < 0) throw new IllegalArgumentException();
//			BigDecimal bd = new BigDecimal("" + value);
//			bd = bd.setScale(places, RoundingMode.HALF_UP);
//			return bd.doubleValue();
//		}
//
//		// Asigna ceros a la izquierda
//		public String codeFormatDigits(int id, int zeros) {
//			String s = "" + id;
//			int nowId = s.length();
//			for (int i = nowId; i < zeros; i++) {
//				if (s.length() < zeros)
//					s = "0" + s;
//			}
//			return s;
//		}
//
//		// Asignar datos de acuerdo a la accion
//		private void actionTimer(char action) {
//			try {
//				String comments = "";
//				if (viewFromList) comments = this.comments;
//				else comments = commentsTextArea.getText();
//
//				if (comments.equals(""))
//					showSystemMessage("Debe asignar un comentario.");
//				else  {
//					bmoServiceOrderReportTime.getIdField().setValue(0);
//					bmoServiceOrderReportTime.getServiceOrderId().setValue(bmoServiceOrder.getId());
//
//					// INICIAR
//					if (action == BmoServiceOrderReportTime.TYPE_START) {
//						bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_START);
//						bmoServiceOrderReportTime.getRealTime().setValue("");
//						bmoServiceOrderReportTime.getComments().setValue(comments);
//					}
//					// DETENER
//					else if (action == BmoServiceOrderReportTime.TYPE_STOP) {
//						bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_STOP);
//						bmoServiceOrderReportTime.getComments().setValue(comments);
//					}
//					// MANUAL
//					else if (action == BmoServiceOrderReportTime.TYPE_MANUAL) {
//						// Asignar hora
//						double hr = Double.parseDouble(hoursUiTextBox.getText());
//						double min = (Double.parseDouble(minutesUiTextBox.getText()) / 60);
//						bmoServiceOrderReportTime.getRealTime().setValue(hr + min);
//						bmoServiceOrderReportTime.getType().setValue(BmoServiceOrderReportTime.TYPE_MANUAL);
//						bmoServiceOrderReportTime.getComments().setValue(comments);
//					}
//
//					// Mandar a guardar fechas
//					saveRPC(); 
//				}
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-actionTimer() ERROR: " + e);
//				e.printStackTrace();
//			}
//		}
//
//		public void saveRPC() {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-saveRPC() ERROR: " + caught.toString());
//				}
//				@Override
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					processSave(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoServiceOrderReportTime.getPmClass(), bmoServiceOrderReportTime, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveRPC() ERROR: " + e.toString());
//			}
//		}
//
//		public void processSave(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) {
//				showErrorMessage(this.getClass().getName() + "-processSave() ERROR: " + bmUpdateResult.errorsToString());
//			} else {
//				// Si avanzaste de la lista, actualizar toda la forma
//				if (viewFromList) {
//					updateFormSRO();
//				} else {
//					dialogClose(); // Cerrar dialogo
//					updateFormSRO(); // Actualizar forma sro
//					open(bmUpdateResult.getBmObject()); // Volver a abrir
//				}
//			}
//		}
//
//		@Override
//		public void postShow() {
//			saveButton.setVisible(false);
//			// Ocultar botones(Eliminar y Timer) de eliminar si el registro tiene 
//			if (bmoServiceOrderReportTime.getChildId().toInteger() > 0) {
//				deleteButton.setVisible(false);
//				timerButtonPanel.setVisible(false);
//			}
//		}
//
//		@Override
//		public void saveNext() {
//			if (newRecord) {
//				// Recargar listado
//				//				updateFormSRO();
//				// Volver a abrir
//				open(getBmObject());			
//			} else list();
//		}
//
//		@Override
//		public void close() {
//			if (deleted) updateFormSRO();
//			else showList();
//			//			updateForm();
//		}
//
//		private void updateFormSRO () {
//			if (serviceOrderUpdater != null) {
//				serviceOrderUpdater.update();
//			}
//		}
//		
//		public boolean isValidateFieldTimerMin() {
//			return validateFieldTimerMin;
//		}
//		public void setValidateFieldTimeMin(boolean validateFieldTimerMin) {
//			this.validateFieldTimerMin = validateFieldTimerMin;
//		}
//
//		public boolean isValidateFieldTimerHr() {
//			return validateFieldTimerHr;
//		}
//
//		public void setValidateFieldTimerHr(boolean validateFieldTimerHr) {
//			this.validateFieldTimerHr = validateFieldTimerHr;
//		}
//	}
//}
