//package com.flexwm.client.cm;
//
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectActivitiesDep;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.TextBox;
//import com.symgae.client.ui.UiDateBox;
//import com.symgae.client.ui.UiDateTimeBox;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.sf.BmoProfile;
//import com.symgae.shared.sf.BmoProfileUser;
//import com.symgae.shared.sf.BmoUser;
//
//public class UiProjectActivities extends UiList {
//	private UiProjectActivitiesForm uiProjectActivitiesForm;
//	BmoProjectActivities bmoProjectActivities;
//	int stepProyectId,stepProyecWflowTypeId,stepProyecWflowId;
//	Image batchProjectActities;
//	
//	public UiProjectActivities(UiParams uiParams, int proyectId,int wflowTypeId,int wflowId) {
//		super(uiParams,new BmoProjectActivities());		
//		bmoProjectActivities = (BmoProjectActivities)getBmObject();
//		stepProyectId = proyectId;
//		stepProyecWflowTypeId = wflowTypeId;
//		stepProyecWflowId = wflowId;
//		
//		batchProjectActities = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/batch.png"));
//		batchProjectActities.setTitle("Importar Tareas desde Excel");
//		batchProjectActities.setStyleName("listSearchImage");
//		batchProjectActities.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				showBatch();
//			}
//		});
//		
//		
//	}
//	public void postShow() {
//		localButtonPanel.add(batchProjectActities);
//	}
//	public void showBatch() {
//		String url = "/batch/prac_batchProjectAct.jsp?&wflowId="+ stepProyecWflowId +"&wflowTypeId="+stepProyecWflowTypeId
//				+ "&projectStepId=" + stepProyectId;
//		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
//	}
//	
//	public UiProjectActivitiesForm getUiExternalSalesForm() {
//		uiProjectActivitiesForm = new UiProjectActivitiesForm(getUiParams(),0);
//		return uiProjectActivitiesForm;
//	}
//	@Override
//	public void open(BmObject bmObject) {
//		UiProjectActivitiesForm uiProjectActivitiesForm = new UiProjectActivitiesForm(getUiParams(), bmObject.getId());
//		uiProjectActivitiesForm.show();
//	}
//	@Override
//	public void create() {
//		UiProjectActivitiesForm uiProjectActivitiesForm = new UiProjectActivitiesForm(getUiParams(),0);
//		uiProjectActivitiesForm.show();		
//	}
//	//Listado Manual
//	@Override
//	public void displayList() {	
//		int col = 0;
//		//Cabecera
//		addColumnHeader(col++, "No.");
//		addColumnHeader(col++, "Actividad");
//		addColumnHeader(col++, "Perfil");
//		addColumnHeader(col++, "Recurso");
//		addColumnHeader(col++, "Predecesoras");
//		addColumnHeader(col++, "F. Inicio");
//		addColumnHeader(col++, "F. Final");
//		addColumnHeader(col++, "H. Est");
//		addColumnHeader(col++, "H. Reales");
//		addColumnHeader(col++, "Finalizar");
//		addColumnHeader(col++, "%Avance.");
//		
//		//Setear columnas
//		int row = 1;
//		while (iterator.hasNext()) {
//			BmoProjectActivities bmoProjectActivities = (BmoProjectActivities)iterator.next();
//			//Check para finalizar tareas/Reiniciar
//			CheckBox progresCheckBox = new CheckBox();
//
//
//			if (bmoProjectActivities.getProgress().toInteger() == 100)progresCheckBox.setValue(true);
//			col = 0;
//		
//			//Labels para Numero,Grupo y usuario
//			Label numberLabel = new Label(bmoProjectActivities.getNumber().toString());	
//			if(bmoProjectActivities.getStatus().equals(BmoProjectActivities.STATUS_ACTIVE)) {
//				numberLabel.addClickHandler(rowClickHandler);
//				numberLabel.setStyleName("listCellLink");
//			}else			
//				numberLabel.setStyleName("listCellCode");
//			
//			Label userLabel = new Label(bmoProjectActivities.getBmoUser().getCode().toString());
//			userLabel.setStyleName("listCellCode");
//			
//			Label groupLabel = new Label(bmoProjectActivities.getBmoProfile().getName().toString());
//			groupLabel.setStyleName("listCellCode");
//			
//			//Evento de el checkBox
//			progresCheckBox.addClickHandler(new ClickHandler() {				
//				@Override
//				public void onClick(ClickEvent event) {
//					if(progresCheckBox.getValue()) {
//						if (Window.confirm("¿Desea Completar la Tarea?")){
//							try {
//								bmoProjectActivities.getProgress().setValue(100);
//							} catch (BmException e) {								
//							}
//							save(bmoProjectActivities);
//						}else {
//							progresCheckBox.setValue(false);
//						}
//					}else {
//						if (Window.confirm("¿Desea Reiniciar la Tarea?")){
//							try {
//								bmoProjectActivities.getProgress().setValue(0);
//							} catch (BmException e) {							
//							}
//							save(bmoProjectActivities);
//						}else {
//							progresCheckBox.setValue(true);
//						}
//						
//					}					
//								
//				}
//			});
//			
//			listFlexTable.setWidget(row, col++, numberLabel);			
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getName());
//			listFlexTable.setWidget(row, col++, groupLabel);
//			listFlexTable.setWidget(row, col++,userLabel);
//			if(!bmoProjectActivities.getDependencies().toString().equals(""))
//				listFlexTable.addListCell(row, col++, getBmObject(),bmoProjectActivities.getDependencies());
//			else
//				listFlexTable.addListCell(row, col++, " ");
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getStartDate());
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getEndDate());
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getEstimatedHours());
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getRealHours());
//			if(bmoProjectActivities.getProgress().toInteger() == 100) {
//				progresCheckBox.setTitle("Reiniciar Tarea");
//			}else {
//				progresCheckBox.setTitle("Finalizar Tarea");
//			}
//			if(bmoProjectActivities.getStatus().equals(BmoProjectActivities.STATUS_ACTIVE))  {
//				listFlexTable.setWidget(row, col++, progresCheckBox);
//			}else {
//				listFlexTable.addListCell(row, col++, " ");
//			}
//			
//			listFlexTable.addListCell(row, col++, getBmObject(), bmoProjectActivities.getProgress());
//		
//			
//			listFlexTable.formatRow(row);
//			row++;
//		}
//	}
//	
//	public class UiProjectActivitiesForm extends UiFormDialog {
//		BmoProjectActivities bmoProjectActivities;
//		TextBox numberTextBox = new TextBox();
//		TextBox nameTextBox = new TextBox();
//		UiSuggestBox groupSuggestBox = new UiSuggestBox(new BmoProfile());
//		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
//		UiDateTimeBox startDateDateTimeBox = new UiDateTimeBox();
//		UiDateTimeBox endDateDateTimeBox = new UiDateTimeBox();
//		TextBox estimatedHoursTextBox = new TextBox();
//		TextBox realHoursTextBox = new TextBox();
//		UiListBox progressListBox = new UiListBox(getUiParams());		
//		UiDateBox remindDateBox = new UiDateBox();
//		public UiProjectActivitiesForm(UiParams uiParams, int id) {
//			super(uiParams,new BmoProjectActivities(),id);
//			bmoProjectActivities = (BmoProjectActivities)getBmObject();
//		}
//		@Override
//		public void populateFields(){
//			bmoProjectActivities = (BmoProjectActivities)getBmObject();
//			numberTextBox.setEnabled(false);		
//			realHoursTextBox.setEnabled(false);
//			formFlexTable.addField(0, 0, numberTextBox,bmoProjectActivities.getNumber());
//			formFlexTable.addField(1, 0, nameTextBox,bmoProjectActivities.getName());			
//			formFlexTable.addField(2, 0, groupSuggestBox,bmoProjectActivities.getProfileId());			
//			formFlexTable.addField(3, 0, userSuggestBox,bmoProjectActivities.getUserId());					
//			formFlexTable.addField(6, 0, startDateDateTimeBox,bmoProjectActivities.getStartDate());
//			formFlexTable.addField(7, 0, endDateDateTimeBox,bmoProjectActivities.getEndDate());
//			formFlexTable.addField(9, 0, estimatedHoursTextBox,bmoProjectActivities.getEstimatedHours());
//			formFlexTable.addField(10, 0, realHoursTextBox,bmoProjectActivities.getRealHours());
//			formFlexTable.addField(11, 0, progressListBox,bmoProjectActivities.getProgress());		
//			
//			if(!newRecord) {				
//				BmoProjectActivitiesDep bmoProjectActivitiesDep = new BmoProjectActivitiesDep();
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setValueFilter(bmoProjectActivitiesDep.getKind(), bmoProjectActivitiesDep.getChildProjectActivityId(), bmoProjectActivities.getId());
//				getUiParams().getUiProgramParams(bmoProjectActivitiesDep.getProgramCode()).setForceFilter(bmFilter);
//				FlowPanel wFlowStepTypeDepFP = new FlowPanel();
//				UiProjectActivitiesDep uiProjectActivitiesDep = new UiProjectActivitiesDep(getUiParams(), wFlowStepTypeDepFP, bmoProjectActivities,stepProyectId);
//				setUiType(bmoProjectActivitiesDep.getProgramCode(), UiParams.MINIMALIST);
//				uiProjectActivitiesDep.show();
//				formFlexTable.addPanel(15, 0, wFlowStepTypeDepFP, 2);
//			}
//			
//			populateProgressListBox();
//		}
//		
//		@Override
//		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
//			if(uiSuggestBox == groupSuggestBox) {
//				userSuggestBox.clear();
//				BmoProfileUser bmoProfileUser = new BmoProfileUser();
//				BmoUser bmoUser = new BmoUser();
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setInFilter(bmoProfileUser.getKind(), 
//						bmoUser.getIdFieldName(),
//						bmoProfileUser.getUserId().getName(),
//						bmoProfileUser.getProfileId().getName(),
//						""+ groupSuggestBox.getSelectedId());
//				userSuggestBox.addFilter(bmFilter);
//				
//			}
//		}
//		@Override
//		public BmObject populateBObject() throws BmException {
//			bmoProjectActivities.setId(id);
//			bmoProjectActivities.getName().setValue(nameTextBox.getText());
//			bmoProjectActivities.getProfileId().setValue(groupSuggestBox.getSelectedId());
//			bmoProjectActivities.getUserId().setValue(userSuggestBox.getSelectedId());
//			bmoProjectActivities.getStartDate().setValue(startDateDateTimeBox.getDateTime());
//			bmoProjectActivities.getEndDate().setValue(endDateDateTimeBox.getDateTime());
//			bmoProjectActivities.getEstimatedHours().setValue(estimatedHoursTextBox.getText());
//			bmoProjectActivities.getProgress().setValue(progressListBox.getSelectedCode());
//			bmoProjectActivities.getStepProjectId().setValue(stepProyectId);			
//			bmoProjectActivities.getRemindDate().setValue(remindDateBox.getTextBox().getText());
//			if(newRecord) {
//				bmoProjectActivities.getWFlowTypeId().setValue(stepProyecWflowTypeId);
//				bmoProjectActivities.getWFlowId().setValue(stepProyecWflowId);
//			}
//			return bmoProjectActivities;
//		}
//		public void populateProgressListBox() {
//			progressListBox.addItem("0%", "0");
//			progressListBox.addItem("25%", "25");
//			progressListBox.addItem("50%", "50");
//			progressListBox.addItem("75%", "75");
//			progressListBox.addItem("100%", "100");
//
//			if (bmoProjectActivities.getProgress().toInteger() == 0) progressListBox.setSelectedIndex(0);
//			else if (bmoProjectActivities.getProgress().toInteger() == 25) progressListBox.setSelectedIndex(1);
//			else if (bmoProjectActivities.getProgress().toInteger() == 50) progressListBox.setSelectedIndex(2);
//			else if (bmoProjectActivities.getProgress().toInteger() == 75) progressListBox.setSelectedIndex(3);
//			else if (bmoProjectActivities.getProgress().toInteger() == 100) progressListBox.setSelectedIndex(4);
//			else progressListBox.setSelectedIndex(0);
//		}
//		@Override
//		public void close() {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), stepProyectId)	;
//			uiProjectStepDetail.show();
//		}
//	
//		
//	}
//}
