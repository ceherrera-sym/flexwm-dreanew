//package com.flexwm.client.op;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectStep;
////import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.op.BmoServiceOrder;
//import com.flexwm.shared.op.BmoServiceOrderReportTime;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.dom.client.KeyUpEvent;
//import com.google.gwt.event.dom.client.KeyUpHandler;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.regexp.shared.MatchResult;
//import com.google.gwt.regexp.shared.RegExp;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.symgae.client.sf.UiFileLabelList;
//import com.symgae.client.ui.UiDateBox;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.fields.UiTextBox;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.sf.BmoCompany;
//import com.symgae.shared.sf.BmoFile;
//import com.symgae.shared.sf.BmoProfile;
//import com.symgae.shared.sf.BmoProfileUser;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class UiServiceOrder extends UiList {
//	BmoServiceOrder bmoServiceOrder;
//
//	public UiServiceOrder(UiParams uiParams) {
//		super(uiParams, new BmoServiceOrder());
//		bmoServiceOrder =  (BmoServiceOrder)getBmObject();
//	}
//
//	@Override
//	public void create() {
//		UiServiceOrderForm uiServiceOrderForm = new UiServiceOrderForm(getUiParams(), 0);
//		uiServiceOrderForm.show();
//	}
//
//	@Override
//	public void open(BmObject bmObject) {
//		UiServiceOrderForm uiServiceOrderForm = new UiServiceOrderForm(getUiParams(), bmObject.getId());
//		uiServiceOrderForm.show();
//	}
//
//	@Override
//	public void postShow() {
//	}
//
//	public class UiServiceOrderForm extends UiFormDialog {
//		TextBox codeTextBox = new TextBox();
//		UiSuggestBox profileUiSuggestBox = new UiSuggestBox(new BmoProfile());
//		UiSuggestBox userUiSuggestBox = new UiSuggestBox(new BmoUser());
//		TextArea activityTextArea = new TextArea();
//		TextArea commitsTextArea = new TextArea();
//		UiListBox typeListBox = new UiListBox(getUiParams());
//		UiListBox statusListBox = new UiListBox(getUiParams());
//		UiDateBox startDateBox = new UiDateBox();
//		UiDateBox endDateBox = new UiDateBox();
//		TextBox estimateTimeTextBox = new TextBox();
//		TextBox realTimeTextBox = new TextBox();
////		TextBox costPerHourTextBox = new TextBox();
////		UiSuggestBox rfquUiSuggestBox = new UiSuggestBox(new BmoRFQU());
//		UiSuggestBox projectIdUiSuggestBox = new UiSuggestBox(new BmoProjectStep());
//		UiListBox projectActivitiesIdUiListBox = new UiListBox(getUiParams(), new BmoProjectActivities());
//		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
//
//		String generalSection = "Datos Generales";
//		ServiceOrderUpdater serviceOrderUpdater = new ServiceOrderUpdater();
//		// Variables RPC
//		BmoServiceOrder bmoServiceOrder;
//		private int userRateRpcAttempt = 0;
//		private int userIdRPC;
//		private UiFileLabelList uiFileLabelList;
//		protected HorizontalPanel programEnabledPanelFiles = new HorizontalPanel();
//		private boolean enableFiles = true;
//
//		// ***** REPORTE DE HORAS *****
//		BmoServiceOrderReportTime bmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//		private FlowPanel timerButtonPanel = new FlowPanel();
//		int zeros = 2;
//		String reportTimeSection = "Reporte de Horas";
//		// Cronometro
//		Timer t;
//		TextArea RTcommentsTextArea = new TextArea();
//		private Button timerStartButton = new Button("INICIAR");
//		private Button timerSaveButton = new Button("ACTUALIZAR");
//		private Button timerStopButton = new Button("DETENER");
//		private Button timerCancelButton = new Button("CANCELAR");
//		UiTextBox timerUiTextBox = new UiTextBox();
//		String userCode = "";
//		private int lastReportTimeRpcAttempt = 0;
//		private String serviceOrderIdRPC;
//		private boolean isLoadingFiles = false;
//
//		public UiServiceOrderForm(UiParams uiParams, int id) {
//			super(uiParams, new BmoServiceOrder(), id);
//
//			// Filtrar por vendedores activos
//			BmoUser bmoUser = new BmoUser();
//			BmFilter filterSalesmenActive = new BmFilter();
//			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
//			userUiSuggestBox.addFilter(filterSalesmenActive);
//			initiaze();
//			setLoadingFiles(false);
//		}
//
//		private void initiaze() {
//
//			// INICIAR
//			timerStartButton.setStyleName("startTimeButton");
//			timerStartButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					//										t = new Timer() {
//					//											@Override
//					//											public void run() {
//					actionTimer(BmoServiceOrderReportTime.TYPE_START);
//					//											}
//					//										};
//					//										t.run();
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
//			// MANUAL; ACTUALIZAR TIMER/COMENTARIO
//			timerSaveButton.setStyleName("formSaveButton");
//			timerSaveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					//					showSystemMessage("hasRPT: "+bmoServiceOrder.getHasReportTime() + "|"+bmoServiceOrderReportTime.getType());
//					if (!(bmoServiceOrder.getHasReportTime().toInteger() > 0)) 
//						actionTimer(BmoServiceOrderReportTime.TYPE_MANUAL);
//					else if (bmoServiceOrderReportTime.getType().equals(BmoServiceOrderReportTime.TYPE_START))
//						actionTimer(BmoServiceOrderReportTime.TYPE_START);
//					else if (bmoServiceOrderReportTime.getType().equals(BmoServiceOrderReportTime.TYPE_STOP))
//						actionTimer(BmoServiceOrderReportTime.TYPE_MANUAL);
//					else if (bmoServiceOrderReportTime.getType().equals(BmoServiceOrderReportTime.TYPE_MANUAL))
//						actionTimer(BmoServiceOrderReportTime.TYPE_MANUAL);
//				}
//			});
//
//			timerCancelButton.setStyleName("formSaveButton");
//			timerCancelButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					statusEffectReportTime();
//				}
//			});
//
//			timerUiTextBox.addKeyUpHandler(new KeyUpHandler() {
//				@Override
//				public void onKeyUp(KeyUpEvent event) {
//					// Si es nuevo apenas se va a inicar el cronometro
//					//					if (!(bmoServiceOrder.getHasReportTime().toInteger() > 0)) {
//					//						if (getValidateTimerTextBox(timerUiTextBox.getText())) {
//					//							timerSaveButton.setVisible(true);
//					//							timerSaveButton.setText("GUARDAR");
//					//							timerStartButton.setVisible(false);
//					//							timerCancelButton.setVisible(false);
//					//							timerUiTextBox.removeStyleName("dateBoxFormatError");
//					//						} else {
//					//							timerSaveButton.setVisible(false);
//					//	//						timerStartButton.setVisible(false);
//					//							timerCancelButton.setVisible(true);
//					//							timerUiTextBox.addStyleName("dateBoxFormatError");
//					//						}
//					//					} else {
//					if (getValidateTimerTextBox(timerUiTextBox.getText())) {
//						timerSaveButton.setVisible(true);
//						timerStartButton.setVisible(false);
//						timerCancelButton.setVisible(false);
//						timerUiTextBox.removeStyleName("dateBoxFormatError");
//					} else {
//						timerSaveButton.setVisible(false);
//						timerStartButton.setVisible(false);
//						timerCancelButton.setVisible(true);
//						timerUiTextBox.addStyleName("dateBoxFormatError");
//						timerUiTextBox.getElement().setPropertyString("border", "1px solid #E82634;");
//					}
//					//					}
//				}
//			});
//
//			// Al cambiar Timer
//			ValueChangeHandler<String> timerHandler = new ValueChangeHandler<String>() {
//				@Override
//				public void onValueChange(ValueChangeEvent<String> event) {
//					if (!timerUiTextBox.getText().equals("")) 
//						timerUiTextBox.setText(getManualConvertTime(timerUiTextBox.getText()));					
//				}
//			};
//			timerUiTextBox.addValueChangeHandler(timerHandler);
//
//			// Agregar botones al panel
//			timerButtonPanel.addStyleName("timerPanel");
//			timerButtonPanel.add(timerUiTextBox);
//			timerButtonPanel.add(timerStartButton);
//			timerButtonPanel.add(timerSaveButton);
//			timerButtonPanel.add(timerStopButton);
//			timerButtonPanel.add(timerCancelButton);
//
//			// Diseño de timer horas:minutos 
//			timerUiTextBox.setStyleName("textBoxTimer");
//			timerUiTextBox.setMaxLength(5);
//			timerUiTextBox.getElement().setPropertyString("placeholder", "00:00");
//		}
//
//		@Override
//		public void populateFields() {
//			bmoServiceOrder = (BmoServiceOrder)getBmObject();
//			// Modifica titulo
//			if (!newRecord)
//				formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoServiceOrder.getCode());
//
//			int col = 0;
//			formFlexTable.addSectionLabel(0, 0, generalSection, 2);
//			formFlexTable.addField(1, col, codeTextBox, bmoServiceOrder.getCode());
//			formFlexTable.addField(2, col, typeListBox, bmoServiceOrder.getType());
////			formFlexTable.addField(3, col, rfquUiSuggestBox, bmoServiceOrder.getRfquId());
//			setProjectAcitivitiesFilterListBox(bmoServiceOrder.getBmoProjectActivities().getStepProjectId().toInteger());
//			formFlexTable.addField(4, col, projectIdUiSuggestBox, bmoServiceOrder.getBmoProjectActivities().getStepProjectId());
//			formFlexTable.addField(5, col, projectActivitiesIdUiListBox, bmoServiceOrder.getProjectActivityId()); 
//			/* #COMENTADO
//			formFlexTable.addField(6, col, profileUiSuggestBox, bmoServiceOrder.getProfileId());
//			*/
//			formFlexTable.addField(7, col, userUiSuggestBox, bmoServiceOrder.getUserId());
//			formFlexTable.addField(8, col, activityTextArea, bmoServiceOrder.getActivity());
//			formFlexTable.addField(9, col, estimateTimeTextBox, bmoServiceOrder.getEstimateTime());
//			formFlexTable.addField(10, col, realTimeTextBox, bmoServiceOrder.getRealTime());
////			formFlexTable.addField(11, col, costPerHourTextBox, bmoServiceOrder.getCostPerHour());
//			formFlexTable.addField(12, col, startDateBox, bmoServiceOrder.getStartDate());
//			formFlexTable.addField(13, col, endDateBox, bmoServiceOrder.getEndDate());
//			/* #COMENTADO
//			formFlexTable.addField(14, col, companyListBox, bmoServiceOrder.getCompanyId());
//			formFlexTable.addField(15, col, statusListBox, bmoServiceOrder.getStatus());
//			 */
//			if (!newRecord) {
//				//				showSystemMessage("carga:"+bmoServiceOrder.getRealTime());
//				getLastReportTime("" + bmoServiceOrder.getId());
//			} else 
//				statusEffect();
//		}
//
//		public void populateFieldsReportTime(BmoServiceOrderReportTime bmoServiceOrderReportTime) {
//			formFlexTable.addSectionLabel(16, 0, reportTimeSection, 2);
//			formFlexTable.addField(17, 0, RTcommentsTextArea, bmoServiceOrderReportTime.getComments());
//			formFlexTable.addPanel(18, 0, timerButtonPanel);
//			
//			//	BmoServiceOrderReportTime bmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//			FlowPanel reportTimeFP = new FlowPanel();
//			BmFilter filterBySro = new BmFilter();
//			filterBySro.setValueFilter(bmoServiceOrderReportTime.getKind(), bmoServiceOrderReportTime.getServiceOrderId(), bmoServiceOrder.getId());
//			getUiParams().setForceFilter(bmoServiceOrderReportTime.getProgramCode(), filterBySro);
//			UiServiceOrderReportTime uiServiceOrderReportTime = new UiServiceOrderReportTime(getUiParams(), reportTimeFP, bmoServiceOrder, false, serviceOrderUpdater);
//			setUiType(bmoServiceOrderReportTime.getProgramCode(), UiParams.MINIMALIST);
//			uiServiceOrderReportTime.show();
//			formFlexTable.addPanel(20, 0, reportTimeFP, 2);
//
//			statusEffect();
//			statusEffectReportTime();
//
//			formFlexTable.getCellFormatter().removeStyleName(18, 0, "formPanel");
//			formFlexTable.getCellFormatter().removeStyleName(20, 0, "formPanel");
//
//			// Habilitar archivos del programa
//			if (!isLoadingFiles()) {
//				setProgramEnabled();
//			}
//		}
//
//		private void statusEffect() {
//			codeTextBox.setEnabled(false);
//			typeListBox.setEnabled(false);
////			rfquUiSuggestBox.setEnabled(false);
//			projectIdUiSuggestBox.setEnabled(false);
//			projectActivitiesIdUiListBox.setEnabled(false);
//			userUiSuggestBox.setEnabled(false);
//			estimateTimeTextBox.setEnabled(false);
//			realTimeTextBox.setEnabled(false);
//			startDateBox.setEnabled(false);
//			endDateBox.setEnabled(false);
//			activityTextArea.setEnabled(false);
//			statusListBox.setEnabled(false);
//			profileUiSuggestBox.setEnabled(false);
//			companyListBox.setEnabled(false);
////			costPerHourTextBox.setEnabled(false);
//			if (newRecord)
//				typeListBox.setEnabled(true);
//			else 
//				statusListBox.setEnabled(true);
//
////			showSystemMessage("tipo_"+typeListBox.getSelectedCode());
//			if (typeListBox.getSelectedCode().equals("" + BmoServiceOrder.TYPE_SERVICE)) {
//				formFlexTable.showField(projectIdUiSuggestBox);
//				formFlexTable.showField(bmoServiceOrder.getProjectActivityId());
//				formFlexTable.hideField(bmoServiceOrder.getRfquId());
////				rfquUiSuggestBox.setEnabled(false);
//				profileUiSuggestBox.setEnabled(false);
//				if (newRecord) {
//					projectIdUiSuggestBox.setEnabled(true);
//					projectActivitiesIdUiListBox.setEnabled(true);
//				}
//			} else if (typeListBox.getSelectedCode().equals("" + BmoServiceOrder.TYPE_DEMO)) {
//				formFlexTable.hideField(bmoServiceOrder.getProjectActivityId());
//				formFlexTable.hideField(projectIdUiSuggestBox);
//				formFlexTable.showField(bmoServiceOrder.getRfquId());
////				rfquUiSuggestBox.setEnabled(true);
//				userUiSuggestBox.setEnabled(true);
//				profileUiSuggestBox.setEnabled(true);
//				estimateTimeTextBox.setEnabled(true);
//				startDateBox.setEnabled(true);
//				endDateBox.setEnabled(true);
//				activityTextArea.setEnabled(true);
////				costPerHourTextBox.setEnabled(true);
//				projectActivitiesIdUiListBox.setEnabled(false);
//				projectIdUiSuggestBox.setEnabled(false);
//
//			} else {
//				if (newRecord) {
//					formFlexTable.hideField(bmoServiceOrder.getRfquId());
//					formFlexTable.hideField(bmoServiceOrder.getProjectActivityId());
//					formFlexTable.hideField(projectIdUiSuggestBox);
//				}
//			}
//		}
//
//		private void statusEffectReportTime() {
//			timerUiTextBox.removeStyleName("dateBoxFormatError");
//			if ((bmoServiceOrderReportTime.getId() > 0)) {
//				if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_START) {
//					
//					// Si eres el usuario que esta trabajando el la activadad muestra botono, si no los oculta
//					/* #COMENTADO
//					if (bmoServiceOrderReportTime.getUserId().toInteger() == getSFParams().getLoginInfo().getUserId()) {
//						timerStopButton.setVisible(true);
//						timerStartButton.setVisible(false);
//						timerSaveButton.setVisible(true);
//						timerCancelButton.setVisible(false);
//						timerUiTextBox.setVisible(false);
//					} else {
//					*/
//						timerStopButton.setVisible(false);
//						timerStartButton.setVisible(false);
//						timerSaveButton.setVisible(false);
//						timerCancelButton.setVisible(false);
//						timerUiTextBox.setVisible(false);
//						formFlexTable.addLabelField(19, 0, "Iniciado por:", userCode);
//						/* #COMENTADO } */
//					statusListBox.setEnabled(false);
//					
//				} else if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_STOP) {
//					timerStartButton.setVisible(true);
//					timerSaveButton.setVisible(true);
//					timerStopButton.setVisible(false);
//					timerCancelButton.setVisible(false);
//					timerUiTextBox.setVisible(true);
//					// Asignar horas reales al cronometro
//					setDataTimer(getHour(bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toDouble()),
//							getMinute(bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toDouble()));
//
//				} else if (bmoServiceOrderReportTime.getType().toChar() == BmoServiceOrderReportTime.TYPE_MANUAL) {
//					timerStartButton.setVisible(true);
//					timerSaveButton.setVisible(true);
//					timerStopButton.setVisible(false);
//					timerCancelButton.setVisible(false);
//					timerUiTextBox.setVisible(true);
//
//					// Asignar horas reales al cronometro
//					setDataTimer(getHour(bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toDouble()),
//							getMinute(bmoServiceOrderReportTime.getBmoServiceOrder().getRealTime().toDouble()));
//				}
//			} else {
//				timerStartButton.setVisible(true);
//				timerStopButton.setVisible(false);
//				timerSaveButton.setVisible(false);
//				timerCancelButton.setVisible(false);
//				timerUiTextBox.setVisible(true);
//				timerUiTextBox.setText("");
//			}
//			/* #COMENTADO
//			// Si esta inactivo ocultar
//			if (bmoServiceOrder.getStatus().equals(BmoServiceOrder.STATUS_INACTIVE)) {
//				timerStartButton.setVisible(false);
//				timerSaveButton.setVisible(false);
//				timerStopButton.setVisible(false);
//				timerCancelButton.setVisible(false);
//			}
//			*/
//		}
//
//		@Override
//		public void formListChange(ChangeEvent event) {
//			if (event.getSource() == typeListBox) {
//				clearFields(); // Limpiar texto al cambiar tipo
//				statusEffect();
//			} else if (event.getSource() == projectActivitiesIdUiListBox) {
//				BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//				bmoProjectActivities = (BmoProjectActivities)projectActivitiesIdUiListBox.getSelectedBmObject();
//				if (bmoProjectActivities != null) {
//					profileUiSuggestBox.setValue(bmoProjectActivities.getBmoProfile().listBoxFieldsToString());
//					profileUiSuggestBox.setSelectedId(bmoProjectActivities.getProfileId().toInteger());
//					
//					userUiSuggestBox.setValue(bmoProjectActivities.getBmoUser().listBoxFieldsToString());
//					userUiSuggestBox.setSelectedId(bmoProjectActivities.getUserId().toInteger());
////					getUserRate("" + bmoProjectActivities.getUserId().toInteger());
//					activityTextArea.setText(bmoProjectActivities.getName().toString());
//					estimateTimeTextBox.setText(bmoProjectActivities.getEstimatedHours().toString());
////					Date startDate = DateFormat .getFormat(getSFParams().getDateFormat()).parse(bmoProjectActivities.getStartDate().toString());
////					Date endDate = DateFormat.getFormat(getSFParams().getDateFormat()).parse(bmoProjectActivities.getEndDate().toString());
//					startDateBox.getTextBox().setValue(bmoProjectActivities.getStartDate().toString().substring(0, 10));
//					endDateBox.getTextBox().setValue(bmoProjectActivities.getEndDate().toString().substring(0, 10));
//					
//				} else {
////					clearFields(); // Limpiar texto al cambiar tipo
////					profileUiSuggestBox.clear();
////					userUiSuggestBox.clear();
////					activityTextArea.setText("");
////					estimateTimeTextBox.setText("");
////					startDateBox.setValue(null);
////					endDateBox.setValue(null);
//				}
//			} else if (event.getSource() == statusListBox) {
//				update("¿Desea cambiar el Estatus" 
//						+ " de "+ getSFParams().getProgramTitle(bmoServiceOrder.getProgramCode().toString())
//						+ "?");
//			}
//		}
//
//		// Cambios en los SuggestBox
//		@Override
//		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
//
//			if (uiSuggestBox == projectIdUiSuggestBox) {
//				populateProjectAcitivities(projectIdUiSuggestBox.getSelectedId());
//				BmoProjectStep bmoProjectStep = new BmoProjectStep();
//				bmoProjectStep = (BmoProjectStep)projectIdUiSuggestBox.getSelectedBmObject();
//				if (bmoProjectStep != null) {
//					populateCompany(bmoProjectStep.getCompanyId().toInteger());
////					showSystemMessage("pj_comp_"+bmoProjectStep.getCompanyId());
////					companyListBox.setSelectedId(bmoProjectStep.getCompanyId().toString());
//				} else {
//					clearFields(); // Limpiar texto al cambiar tipo
//					profileUiSuggestBox.clear();
//					userUiSuggestBox.clear();
//				}
////				companyListBox.setSelectedId(((BmoProjectStep)projectIdUiSuggestBox.getSelectedBmObject()).getCompanyId().toString() );
//			} else if (uiSuggestBox == userUiSuggestBox) {
////				getUserRate("" + userUiSuggestBox.getSelectedId());
//			} 
////			else if (uiSuggestBox == rfquUiSuggestBox) {
////				BmoRFQU bmoRFQU = new BmoRFQU();
////				bmoRFQU = (BmoRFQU)rfquUiSuggestBox.getSelectedBmObject();
////				if (bmoRFQU != null) {
////					populateCompany(bmoRFQU.getCompanyId().toInteger());
////
//////					showSystemMessage("rfq_comp_"+bmoRFQU.getCompanyId());
//////					companyListBox.setSelectedId(bmoRFQU.getCompanyId().toString());
////				} else {
////					clearFields(); // Limpiar texto al cambiar tipo
////				}
//////				companyListBox.setSelectedId(((BmoRFQU)rfquUiSuggestBox.getSelectedBmObject()).getCompanyId().toString() );
////
//////				if (bmoRFQU != null) {
//////					userUiSuggestBox.setValue(bmoRFQU.getBmoUser().listBoxFieldsToString());
//////					userUiSuggestBox.setSelectedId(bmoRFQU.getUserSale().toInteger());
//////					getUserRate("" + bmoRFQU.getUserSale().toInteger());
//////				} else {
//////					userUiSuggestBox.clear();
//////					costPerHourTextBox.setText("0");
//////				}
////			}
//			else if (uiSuggestBox == profileUiSuggestBox) {
//				populateResponsible(profileUiSuggestBox.getSelectedId());
//			} 
//		}
//
//		// Limpiar todos los campos
//		public void clearFields() {
////			rfquUiSuggestBox.setValue("");
////			rfquUiSuggestBox.setSelectedId(0);
//			projectIdUiSuggestBox.setValue("");
//			projectIdUiSuggestBox.setSelectedId(0);
//			projectActivitiesIdUiListBox.clear();
//			profileUiSuggestBox.setValue("");
//			profileUiSuggestBox.setSelectedId(0);
//			userUiSuggestBox.setValue("");
//			userUiSuggestBox.setSelectedId(0);
//			activityTextArea.setText("");
//			estimateTimeTextBox.setText("");
//			realTimeTextBox.setText("");
//			startDateBox.setValue(null);
//			endDateBox.setValue(null);
//			companyListBox.clear();
//		}
//
//		// Llena combo actividades del proyectos
//		private void populateProjectAcitivities(int projectStepId) {
//			projectActivitiesIdUiListBox.clear();
//			projectActivitiesIdUiListBox.clearFilters();
//			setProjectAcitivitiesFilterListBox(projectStepId);
//			projectActivitiesIdUiListBox.populate(bmoServiceOrder.getProjectActivityId());
//		}
//
//		// Asigna filtros de las actividades
//		private void setProjectAcitivitiesFilterListBox(int projectStepId) {
//			BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//			if (projectStepId > 0) {
//				BmFilter bmFilterByProject = new BmFilter();
//				bmFilterByProject.setValueFilter(bmoProjectActivities.getKind(), bmoProjectActivities.getStepProjectId(), projectStepId);
//				projectActivitiesIdUiListBox.addFilter(bmFilterByProject);
//
//			} else {
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setValueFilter(bmoProjectActivities.getKind(), bmoProjectActivities.getIdField(), bmoProjectActivities.getStepProjectId().toInteger());
//				projectActivitiesIdUiListBox.addFilter(bmFilter);
//			}
//		}
//		
//		// Llena combo actividades del proyectos
//		private void populateResponsible(int profileId) {
//			userUiSuggestBox.clear();
//			setResponsibleSuggestBox(profileId);
////			userUiSuggestBox.populate(bmoServiceOrder.getProfileId());
//		}
//
//		// Asigna filtros de las actividades
//		private void setResponsibleSuggestBox(int profileId) {
//			
//			BmoUser bmoUser = new BmoUser();
//
//			if (profileId > 0) {
//				BmoProfileUser bmoProfileUser = new BmoProfileUser();
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setInFilter(bmoProfileUser.getKind(), 
//						bmoUser.getIdFieldName(),
//						bmoProfileUser.getUserId().getName(),
//						bmoProfileUser.getProfileId().getName(),
//						""+ profileUiSuggestBox.getSelectedId());
//				userUiSuggestBox.addFilter(bmFilter);
//
//			} else {
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setValueFilter(bmoUser.getKind(), bmoUser.getIdField(), -1);
//				userUiSuggestBox.addFilter(bmFilter);
//			}
//		}
//		
//		private void populateCompany(int companyId) {
//			companyListBox.clear();
//			companyListBox.clearFilters();
//			if (companyId > 0)
//				setCompanyListBoxFilters(companyId);
//			companyListBox.populate("" + companyId);
//		}
//
//		private void setCompanyListBoxFilters(int companyId) {
//			BmoCompany bmoCompany = new BmoCompany();
//			BmFilter bmFilterByCompany = new BmFilter();
//			bmFilterByCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
//			companyListBox.addBmFilter(bmFilterByCompany);		
//		}
//
//		@Override
//		public void postShow() {
//			
//		}
//
//		// Revisar si estan habilitados el mostrar formatos y/o archivos
//		private void setProgramEnabled() {
//			// Si es registro creado, mostrar formatos y archivos
//			if (id > 0) {
//
//				int row = formFlexTable.getRowCount();
//				int col = 0;
//				String sectionName = "";
//
//				sectionName = getSFParams().getProgramTitle(new BmoFile().getProgramCode());
//
//				// Si hay archivos o formatos habilitados, mostrar seccion
//				if (!sectionName.equals(""))
//					formFlexTable.addSectionLabel(row++, col, sectionName, 2);
//
//				setProgramFiles(row++, col);
//
//				// Ocultar seccion si hay archivos o formatos habilitados, 
//				if (!sectionName.equals(""))
//					formFlexTable.hideSection(sectionName);
//
//			} else {
//				programEnabledPanelFiles.setVisible(false);
//			}
//
//			// Volver a habilitar botones
//			setButtons();
//		}
//
//		private void setProgramFiles(int row, int col) {
//			uiFileLabelList = new UiFileLabelList(getUiParams(), getBmObjectBmoProgram().getId(), getBmObject(), enableFiles);
//			formFlexTable.addField(row, col, uiFileLabelList);
//			setLoadingFiles(true);
//		}
//
//		// Habilita archivos
//		public void enableFiles() {
//			uiFileLabelList.enable();
//		}
//
//		// Deshabilita archivos
//		public void disableFiles() {
//			uiFileLabelList.disable();
//		}
//
//		// Estan cargados los archivos
//		public boolean isLoadingFiles() {
//			return isLoadingFiles;
//		}
//
//		// Asigna si estan cargados los archivos
//		public void setLoadingFiles(boolean isLoadingFiles) {
//			this.isLoadingFiles = isLoadingFiles;
//		}
//
//		@Override
//		public BmObject populateBObject() throws BmException {
//			bmoServiceOrder.setId(id);
//			bmoServiceOrder.getCode().setValue(codeTextBox.getText());
//			bmoServiceOrder.getType().setValue(typeListBox.getSelectedCode());
//			bmoServiceOrder.getProjectActivityId().setValue(projectActivitiesIdUiListBox.getSelectedId());
//			/* #COMENTADO
//			bmoServiceOrder.getProfileId().setValue(profileUiSuggestBox.getSelectedId());
//			*/
//			bmoServiceOrder.getUserId().setValue(userUiSuggestBox.getSelectedId());
////			bmoServiceOrder.getRfquId().setValue(rfquUiSuggestBox.getSelectedId());
//			bmoServiceOrder.getActivity().setValue(activityTextArea.getText());
//			bmoServiceOrder.getStartDate().setValue(startDateBox.getTextBox().getText());
//			bmoServiceOrder.getEndDate().setValue(endDateBox.getTextBox().getText());
//			bmoServiceOrder.getEstimateTime().setValue(estimateTimeTextBox.getText());
//			bmoServiceOrder.getRealTime().setValue(realTimeTextBox.getText());
////			bmoServiceOrder.getCostPerHour().setValue(costPerHourTextBox.getText());
//			/* #COMENTADO
//			bmoServiceOrder.getStatus().setValue(statusListBox.getSelectedCode());
//			bmoServiceOrder.getCompanyId().setValue(companyListBox.getSelectedId());
//			*/
//			return bmoServiceOrder;
//		}
//
//		// # Metodos Reporte de horas(Conversiones)
//		// Obtener la hora(primera parte) [Ej: de 2.78 obtiene 2]
//		public int partHr(double number) {
//			int partHr = 0;
//			BigDecimal bd = new BigDecimal(String.valueOf(number));
//			BigDecimal intPartHr = new BigDecimal(bd.toBigInteger());
//			partHr = Integer.parseInt("" + intPartHr);
//			return partHr;
//		}
//
//		// Obtener el minuto(segunda parte) [ej: de 2.78 obtiene .78]
//		public double partMin(double number) {
//			double partMin = 0;
//			BigDecimal bd = new BigDecimal(String.valueOf(number));
//			BigDecimal intPartMin = bd.remainder(BigDecimal.ONE);
//			partMin = Double.parseDouble("" + intPartMin);
//			return partMin;
//		}
//
//		// Obtener datos en formato de cronometro
//		private String setDataTimerFormat(int hr, int min) {
//			return setFormatDigits(hr) + ":" + setFormatDigits(min);
//		}
//
//		// Asignar datos al cronometro
//		private void setDataTimer(int hr, int min) {
//			timerUiTextBox.setText(setDataTimerFormat(hr, min));
//		}
//
//		// Obtener la hora
//		private int getHour(double number) {
//			return partHr(number);
//		}
//
//		// Obtener el minuto
//		private int getMinute(double number) {
//			// Obtener decimal(min) de la hora(numero)
//			double min = partMin(number);
//			// Convertir minutos(numero decimal) a minutos
//			return (int)roundDecimal(convertMinuteDecimalToMinute(min), 0);
//		}
//
//		private boolean getValidateTimerTextBox(String timerTextBox) {
//			boolean validate = true;
//			if (!timerTextBox.equals("")) {
//
//				RegExp regExp = RegExp.compile("[^.:0-9]");
//				MatchResult matcher = regExp.exec(timerTextBox);
//				boolean matchFound = matcher != null;
//				if (matchFound) validate = false;
//
//
////				if ((timerTextBox.indexOf(".") >= 0)) {
////					validate = false;
////				}
//
//			} else validate = false;
//			return validate;
//		}
//
//		// Obtener conversion de timer(NUMERO) a HORA
//		private String getManualConvertTime(String timerTextBox) {
//			String time = ""; 
//			int min = 0, hr = 0;
////			boolean num = false, text = false;
//			
////			if () num = true;
////			showSystemMessage("A_"+);
//			// Validar si los valores introducidos es numero/fraccion
//			if (isInteger(timerTextBox) || isDouble(timerTextBox)
//					|| timerTextBox.equalsIgnoreCase(".")) {
//
//				// En caso de que solo se haya puesto un punto, asignar como cero
//				if (timerTextBox.equalsIgnoreCase(".")) { timerTextBox = "0"; }
//
//				// Pasar de numero a hora			
//				hr = getHour(Double.parseDouble(timerTextBox));
//				min = getMinute(Double.parseDouble(timerTextBox));
//				time = setDataTimerFormat(hr, min);
//
//			} else {
//				
//				
//				// Dar formato a la hora introducida
//				String[] parts = timerTextBox.split(":");
//				String sHr = "", sMin= "";
////					showSystemMessage("parts:"+parts);
//					sHr = parts[0];
////					showSystemMessage("sHr:"+sHr);
//					sMin = parts[1];
////					showSystemMessage("sMin:"+sMin);
//				
//				
////				if (!(timerTextBox.indexOf(".") > 0)) {
////					showSystemMessage("bb"+timerTextBox.indexOf("."));
////					sHr = "0";
////					sMin = "0";
////				}
//
//
//				if (sHr == null || sHr.equals("")) sHr = "0";
//				if (sMin == null || sMin.equals("")) sMin = "0";
//
//				time = setDataTimerFormat(Integer.parseInt(sHr), Integer.parseInt(sMin));
//			}
//			return time;
//		}
//
//		// Obtener conversion de timer(HORA) a NUMERO
//		private double getManualConvertHour(String timerTextBox) {
//			double timeHr = 0, hr = 0, min = 0;
//			// Pasar de hora a numero
//			String[] parts = timerTextBox.split(":");
//			String sHr = parts[0];
//			String sMin = parts[1];
//			min =  convertMinuteToHours(Double.parseDouble(sMin));
//			hr = Double.parseDouble(sHr);
//			timeHr = hr + min;
//
//			return timeHr;
//		}
//
//		// Convertir Minutos a Horas
//		public double convertMinuteToHours(double minutes) {
//			return minutes / 60;
//		}
//
//		// Convertir Minutos(numero decimal) a Minutos
//		public double convertMinuteDecimalToMinute(double minutesDecimal) {
//			return minutesDecimal * 60;
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
//		public String setFormatDigits(int id) {
//			String s = "" + id;
//			int nowId = s.length();
//			for (int i = nowId; i < this.zeros; i++) {
//				if (s.length() < this.zeros)
//					s = "0" + s;
//			}
//			return s;
//		}
//
//		// Validar si es numero entero
//		public boolean isInteger(String value) {
//			try { 
//				Integer.parseInt(value); 
//			} catch(NumberFormatException e) { 
//				return false; 
//			} catch(NullPointerException e) {
//				return false;
//			}
//			return true;
//		}
//
//		// Validar si el valor es numero decimal
//		public boolean isDouble(String value) {
//			boolean res = true;
//			try {
//				Double.parseDouble(value);
//			} catch(NumberFormatException e) { 
//				return false; 
//			} catch(NullPointerException e) {
//				return false;
//			}
//			return res;
//		}
//
//		// Obtiene el ultimo registro del reporte de horas, primer intento
//		public void getLastReportTime(String serviceOrderIdRPC) {
//			getLastReportTime(serviceOrderIdRPC, 0);
//		}
//
//		// Obtiene el ultimo registro del reporte de horas
//		public void getLastReportTime(String serviceOrderIdRPC, int lastReportTimeRpcAttempt) {
//			if (lastReportTimeRpcAttempt < 5) {
//				setServiceOrderIdRPC(serviceOrderIdRPC);
//				setLastReportTimeRpcAttempt(lastReportTimeRpcAttempt + 1);
//
//				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						stopLoading();
//						if (getLastReportTimeRpcAttempt() < 5) 
//							getLastReportTime(getServiceOrderIdRPC(), getLastReportTimeRpcAttempt());
//						else 
//							showErrorMessage(this.getClass().getName() + "-getLastReportTime() ERROR: " + caught.toString());
//					}
//
//					@Override
//					public void onSuccess(BmUpdateResult result) {
//						stopLoading();
//						setLastReportTimeRpcAttempt(0);
//						if (result.hasErrors())
//							showErrorMessage("Error al obtener Reporte de Horas.");
//						else {
//							bmoServiceOrderReportTime = (BmoServiceOrderReportTime)result.getBmObject();
//							/* #COMENTADO
//							if (bmoServiceOrderReportTime.getUserId().toInteger() > 0)
//								getBmoUserRPH(bmoServiceOrderReportTime.getUserId().toInteger());
//							else
//							*/
//								populateFieldsReportTime(bmoServiceOrderReportTime);
//						}
//					}
//				};
//
//				try {	
//					if (!isLoading()) {				
//						startLoading();
//						getUiParams().getBmObjectServiceAsync().action(bmoServiceOrderReportTime.getPmClass(), bmoServiceOrderReportTime, BmoServiceOrderReportTime.ACTION_GETLASTRECORD, "" + serviceOrderIdRPC, callback);
//					}
//				} catch (SFException e) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-getLastReportTime() ERROR: " + e.toString());
//				}
//			}
//		}
//
//
//		// Obtiene Usuario, primer intento
//		public void getBmoUserRPH(int userIdRPC) {
//			getBmoUserRPH(userIdRPC, 0);
//		}
//
//		public void getBmoUserRPH(int userIdRPC, int userRateRpcAttempt) {
//			BmoUser bmoUser = new BmoUser();
//
//			if (userRateRpcAttempt < 5) {
//				setUserIdRPC(userIdRPC);
//				setUserRateRpcAttempt(userRateRpcAttempt + 1);
//
//				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//					@Override
//					public void onFailure(Throwable caught) {
//
//						stopLoading();
//						if (getUserRateRpcAttempt() < 5)
//							getBmoUserRPH(getUserIdRPC(), getUserRateRpcAttempt());
//						else 
//							showErrorMessage(this.getClass().getName() + "-getBmoUserRPH() ERROR: " + caught.toString());
//					}
//
//					@Override
//					public void onSuccess(BmObject bmObject) {
//						stopLoading();
//						setUserRateRpcAttempt(0);
//						if (!(bmObject.getId() > 0))
//							showErrorMessage("Error al obtener Usuario.");
//						else {
//							BmoUser bmoUser = (BmoUser)bmObject;
//							userCode = bmoUser.getCode().toString();
//							populateFieldsReportTime(bmoServiceOrderReportTime);
//						}
//					}
//				};
//
//				try {	
//					if (!isLoading()) {				
//						startLoading();
//						getUiParams().getBmObjectServiceAsync().get(bmoUser.getPmClass(), userIdRPC, callback);
//					}
//				} catch (SFException e) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-getBmoUserRPH() ERROR: " + e.toString());
//				}
//			}
//		}
//
//		// Asignar datos de acuerdo a la accion
//		private void actionTimer(char action) {
//			try {
//				String comments =  RTcommentsTextArea.getText();
//				if (comments.equals(""))
//					showSystemMessage("Debe asignar un comentario.");
//				else  {
//
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
//						bmoServiceOrderReportTime.getRealTime().setValue(getManualConvertHour(timerUiTextBox.getText()));
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
//				this.updateForm(id);
//			}
//		}
//
//		@Override
//		public void saveNext() {
//			// Recargar listado
//			showList();
//			UiServiceOrderForm uiServiceOrderForm = new UiServiceOrderForm(getUiParams(), getBmObject().getId());
//			uiServiceOrderForm.show();
//		}
//
//		@Override
//		public void close() {
//			list();
//		}
//
//		public void updateForm(int id) {
//			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-updateForm() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmObject result) {
//					stopLoading();				
//					setRealHours((BmoServiceOrder)result);
//					getLastReportTime("" + id);
//				}
//			};
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().get(bmoServiceOrder.getPmClass(), id, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-updateForm(): ERROR " + e.toString());
//			}
//		}
//
//		private void setRealHours(BmoServiceOrder bmoServiceOrder) {
//			this.bmoServiceOrder = bmoServiceOrder;
//			realTimeTextBox.setText("" + bmoServiceOrder.getRealTime().toDouble());
//		}
//
//		// Volver a cargar
//		public void reset() {
//			// Esto es por si se habilita el reporte de horas...
//			// ya que al eliminar un registro, no actualiza bien el cronometro
//			dialogClose();
//			open(getBmObject());
//			//			get(id); // Actualizar solo la forma
//		}
//
//		protected class ServiceOrderUpdater {
//			public void update() {
//				stopLoading();
//				reset();
//			}	
//		}
//
//		// Variables para llamadas RPC
//		public int getUserRateRpcAttempt() {
//			return userRateRpcAttempt;
//		}
//
//		public void setUserRateRpcAttempt(int userRateRpcAttempt) {
//			this.userRateRpcAttempt = userRateRpcAttempt;
//		}
//
//		public int getUserIdRPC() {
//			return userIdRPC;
//		}
//
//		public void setUserIdRPC(int userIdRPC) {
//			this.userIdRPC = userIdRPC;
//		}
//
//		// Reporte de horas
//		public int getLastReportTimeRpcAttempt() {
//			return lastReportTimeRpcAttempt;
//		}
//
//		public void setLastReportTimeRpcAttempt(int lastReportTimeRpcAttempt) {
//			this.lastReportTimeRpcAttempt = lastReportTimeRpcAttempt;
//		}
//
//		public String getServiceOrderIdRPC() {
//			return serviceOrderIdRPC;
//		}
//
//		public void setServiceOrderIdRPC(String serviceOrderIdRPC) {
//			this.serviceOrderIdRPC = serviceOrderIdRPC;
//		}
//	}
//}
