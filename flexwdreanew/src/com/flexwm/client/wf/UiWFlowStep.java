/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import java.sql.Types;
import java.util.ArrayList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiCheckBoxClickHandler;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiWFlowStep extends UiList {
	BmoWFlowStep bmoWFlowStep;
	BmoWFlow bmoWFlow;
	IWFlowStepAction wFlowStepAction;
	boolean finishedTask = false;

	public UiWFlowStep(UiParams uiParams, BmoWFlow bmoWFlow) {
		super(uiParams, new BmoWFlowStep());
		bmoWFlowStep = (BmoWFlowStep)getBmObject();
		this.bmoWFlow = bmoWFlow;
	}

	public UiWFlowStep(UiParams uiParams, BmoWFlow bmoWFlow, IWFlowStepAction wFlowStepAction) {
		super(uiParams, new BmoWFlowStep());
		bmoWFlowStep = (BmoWFlowStep)getBmObject();
		this.bmoWFlow = bmoWFlow;
		this.wFlowStepAction = wFlowStepAction;
	}

	public UiWFlowStep(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		this.bmoWFlowStep = (BmoWFlowStep)getBmObject();
	}
	
	public UiWFlowStep(UiParams uiParams, Panel defaultPanel, BmoWFlow bmoWFlow) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		this.bmoWFlowStep = (BmoWFlowStep)getBmObject();
		this.bmoWFlow = bmoWFlow;
	}

	public UiWFlowStep(UiParams uiParams) {
		super(uiParams, new BmoWFlowStep());
		this.bmoWFlowStep = (BmoWFlowStep)getBmObject();
	}

	@Override
	public void postShow() {
		// Muestra filtros si es modulo independiente
//		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).getFilterList();
//		addDateRangeFilterListBox(bmoWFlowStep.getRemindDate());
		if (isMaster()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory()), bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory());
			addFilterSuggestBox(new UiSuggestBox(new BmoWFlowType()), new BmoWFlowType(), bmoWFlowStep.getBmoWFlow().getWFlowTypeId());
			
			if (!isMobile()) {
				addFilterListBox(new UiListBox(getUiParams(), new BmoProfile()), bmoWFlowStep.getBmoProfile());
				addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoWFlowStep.getUserCreateId());
				addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoWFlowStep.getUserId());
//				addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowStep.getType()), bmoWFlowStep, bmoWFlowStep.getType());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowStep.getStatus()), bmoWFlowStep, bmoWFlowStep.getStatus(), ""+BmoWFlowStep.STATUS_EXPIRED);
				addNumberRangeFilterListBox(bmoWFlowStep.getProgress());
				newImage.setVisible(false);
			}
		}else {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowStep.getStatus()), bmoWFlowStep, bmoWFlowStep.getStatus(), "" );
			addDateRangeFilterListBox(bmoWFlowStep.getRemindDate());

			getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).getFilterList();
			
		}
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).getFilterList();
		if (bmoWFlow == null)
			newImage.setVisible(false);
	}

	@Override
	public void create() {
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), 0, bmoWFlow, wFlowStepAction);
		uiWFlowStepForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), bmObject.getId(), bmoWFlowStep.getBmoWFlow(), wFlowStepAction);
		uiWFlowStepForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), bmObject.getId(), bmoWFlowStep.getBmoWFlow(), wFlowStepAction);
		uiWFlowStepForm.show();
	}

	@Override
	public void displayList() {		
		int col = 0;
		
		addColumnHeader(col++, "Abrir");
		addColumnHeader(col++, bmoWFlowStep.getName());
		if (!isMobile()) {
			addColumnHeader(col++, bmoWFlowStep.getType());
			addColumnHeader(col++, "Crear Nueva");
			addColumnHeader(col++, bmoWFlowStep.getBmoWFlow().getName());
			addColumnHeader(col++, bmoWFlowStep.getBmoProfile().getName());
			addColumnHeader(col++, bmoWFlowStep.getBmoUser().getCode());
			if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
				addColumnHeader(col++, bmoWFlowStep.getWFlowFunnelId());
			addColumnHeader(col++, bmoWFlowStep.getStartdate());
			
//			addColumnHeader(col++, bmoWFlowStep.getEnddate());
//			addColumnHeader(col++, bmoWFlowStep.getEnabled());
			
			addColumnHeader(col++, bmoWFlowStep.getFile());		
			addColumnHeader(col++, bmoWFlowStep.getRemindDate());		
			addColumnHeader(col++, bmoWFlowStep.getFileLink());	
		}
		addColumnHeader(col++, "Finalizar");
		addColumnHeader(col++, bmoWFlowStep.getProgress());	
		if (!isMobile()) { 
			addColumnHeader(col++, bmoWFlowStep.getPending());
			addColumnHeader(col++, bmoWFlowStep.getStatus());
//			addColumnHeader(col++, bmoWFlowStep.getEnabled());
		}

		int row = 1;
		while (iterator.hasNext()) {
			BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)iterator.next(); 
			col = 0;

			CheckBox wFlowStepCheckBox = new CheckBox("");
			
			// Si tiene acceso total, o pertenece al grupo de la tarea permitir apertura
			if (getUiParams().getSFParams().isAllData(bmoWFlowStep.getProgramCode()) 
					|| getUiParams().getSFParams().userInProfile(bmoWFlowStep.getProfileId().toString())) {

				if (bmoWFlowStep.getEnabled().toBoolean()) {
					Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/whst_open.png"));
					clickImage.setStyleName("listCellLink");
					clickImage.setTitle("Tarea Habilitada.");
					clickImage.addClickHandler(rowClickHandler);
					listFlexTable.setWidget(row, col++, clickImage);
					listFlexTable.getCellFormatter().addStyleName(row, 0, "listFirstColumn");
				} else {
					Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/whst_paused.png"));
					clickImage.setTitle("Tarea No Habilitada.");
					listFlexTable.setWidget(row, 0, clickImage);
					listFlexTable.getCellFormatter().addStyleName(row, col++, "listFirstColumn");		
					wFlowStepCheckBox.setEnabled(false);
				}
				
			} else {
				Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/whst_blocked.png"));
				clickImage.setTitle("La tarea pertenece a otro Grupo.");
				listFlexTable.setWidget(row, 0, clickImage);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listFirstColumn");		
				wFlowStepCheckBox.setEnabled(false);
			}


			BmField displayCode = new BmField("displaycode", "", "Clave Tarea", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
			try {
				displayCode.setValue(bmoWFlowStep.getBmoWFlowPhase().getSequence() + 
						"." + bmoWFlowStep.getSequence() + 
						"-" + bmoWFlowStep.getName());
			} catch (BmException e) {
				System.out.println(this.getClass().getName() + "-getDisplayFieldList(): " + e.toString());
			}

			listFlexTable.addListCell(row, col++, getBmObject(), displayCode);

			if (!isMobile()) {
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getType());
				
				// Imagen de crear nueva tarea
				Image newImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/show.png"));
				newImage.setStyleName("formLabelImage");
				newImage.addClickHandler(new UiClickHandler(bmoWFlowStep) {
					public void onClick(ClickEvent event) {
						createLinkedWFlowStep(bmObject);
					}
				});
				listFlexTable.setWidget(row, col, newImage);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellImage");

				// Agregar nombre flujo
				BmField displayWFlow = new BmField("displaywflow", "", "Nombre Flujo", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
				try {
					displayWFlow.setValue(bmoWFlowStep.getBmoWFlow().getCode().toString() + " " + bmoWFlowStep.getBmoWFlow().getName().toString());
				} catch (BmException e) {
					System.out.println(this.getClass().getName() + "-getDisplayFieldList(): " + e.toString());
				}
				listFlexTable.addListCell(row, col++, getBmObject(), displayWFlow);
	
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoProfile().getName());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoUser().getCode());
				if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
					listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoWFlowFunnel().getName());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getStartdate());
//				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getEnddate());
//				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getEnabled());

				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getFile());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getRemindDate());				
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getFileLink());
			}
			// Auto completar
			if (bmoWFlowStep.getEnabled().toBoolean()) {
				// Boton de completar tarea
				wFlowStepCheckBox.setWidth("10px");
				wFlowStepCheckBox.setStyleName("squaredFour");
				if (bmoWFlowStep.getProgress().toInteger() == 100) {
					wFlowStepCheckBox.setValue(true);
					wFlowStepCheckBox.setTitle("Reiniciar Tarea");
				} else {
					wFlowStepCheckBox.setValue(false);
					wFlowStepCheckBox.setTitle("Completar Tarea");
				}
				wFlowStepCheckBox.addClickHandler(new UiCheckBoxClickHandler(wFlowStepCheckBox, bmoWFlowStep) {
					public void onClick(ClickEvent event) {
						if (bmoWFlow.getStatus().equals(BmoWFlow.STATUS_ACTIVE)) {
							if (checkBox.getValue()) {
								if (Window.confirm("¿Desea Completar la Tarea?"))
									checkBoxClick(checkBox.getValue(), this.bmObject);
								else
									// Si no confirma regresar al check original
									checkBox.setValue(false);
							} else {
								if (Window.confirm("¿Desea Reiniciar la Tarea?"))
									checkBoxClick(checkBox.getValue(), this.bmObject);
								else
									// Si no confirma regresar al check original
									checkBox.setValue(true);
							}
						} else {
							// Si no esta activo el Flujo regresar al check original
							if (checkBox.getValue()) {
								checkBox.setValue(false);
							} else {
								checkBox.setValue(true);
							}
							showSystemMessage("El WFlow está Inactivo");
						}
					}
				});
				listFlexTable.setWidget(row, col, wFlowStepCheckBox);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellCheckBox");
			} else 
				col++;
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getProgress());
			
			if (!isMobile()) {
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getPending());
				listFlexTable.addListCell(row, col++, getBmObject(),bmoWFlowStep.getStatus());
//				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getEnabled());
			}

			listFlexTable.formatRow(row);
			row++;
		}
	}

	// Guarda tarea
	private void checkBoxClick(boolean check, BmObject bmObject) {
		finishedTask = check;
		get(bmObject.getId());
	}

	@Override
	public void getNext(BmObject bmObject) {
		try {
			bmoWFlowStep = (BmoWFlowStep)bmObject;
			if (finishedTask) {
				bmoWFlowStep.getProgress().setValue(100);
				bmoWFlowStep.getComments().setValue("Click en Avance.");
			} else {
				bmoWFlowStep.getProgress().setValue(0);
				bmoWFlowStep.getComments().setValue("Click en Avance.");
			}

			save(bmoWFlowStep);

		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-checkBoxClick() ERROR: " + e.toString());
		}
	}	

	private void createLinkedWFlowStep(BmObject bmObject) {
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), bmObject.getId(), bmoWFlowStep.getBmoWFlow(), wFlowStepAction);
		uiWFlowStepForm.setNewLinked(true);
		uiWFlowStepForm.show();
	}
	
	public class UiWFlowStepForm extends UiFormDialog {
		protected TextBox nameTextBox = new TextBox();
		protected TextArea commentsTextArea = new TextArea();
		protected TextArea descriptionTextArea = new TextArea();
		protected TextArea commentLogTextArea = new TextArea();
		protected TextBox sequenceTextBox = new TextBox();
		protected UiListBox wFlowFunnelListBox = new UiListBox(getUiParams(), new BmoWFlowFunnel());
		protected TextBox validateClassTextBox = new TextBox();
		protected CheckBox emailRemindersCheckBox = new CheckBox();
		protected CheckBox emailReminderCommentsCheckBox = new CheckBox();
		protected CheckBox pendingCheckBox = new CheckBox();
		protected UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		protected UiListBox userListBox = new UiListBox(getUiParams(), new BmoUser());
		protected UiDateTimeBox endDateDateTimeBox = new UiDateTimeBox();

		protected UiListBox wFlowPhaseListBox;
		protected UiListBox progressListBox = new UiListBox(getUiParams());
		protected UiDateBox remindDateBox = new UiDateBox();
		protected UiFileUploadBox fileUploadBox = new UiFileUploadBox(getUiParams());
		protected TextBox fileLinkTextBox = new TextBox();
		protected BmoWFlowStep bmoWFlowStep;
		protected BmoWFlow bmoWFlow;
		protected IWFlowStepAction wFlowStepAction;

		protected String generalSection = "Datos Generales";
		protected String progressSection = "Actualizar Avances";
		protected String logSection = "Bitácora de Avances";
		
		protected boolean newLinked = false;

		public boolean isNewLinked() {
			return newLinked;
		}

		public void setNewLinked(boolean newLinked) {
			this.newLinked = newLinked;
		}

		public UiWFlowStepForm(UiParams uiParams, int id, BmoWFlow bmoWFlow) {
			super(uiParams, new BmoWFlowStep(), id);
			this.bmoWFlow = bmoWFlow;

			initialize();
		}

		public UiWFlowStepForm(UiParams uiParams, int id, BmoWFlow bmoWFlow, IWFlowStepAction wFlowStepAction) {
			super(uiParams, new BmoWFlowStep(), id);
			this.bmoWFlow = bmoWFlow;

			this.wFlowStepAction = wFlowStepAction;
			initialize();
		}

		private void initialize() {
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter wFlowCategoryFilter = new BmFilter();
			wFlowCategoryFilter.setValueFilter(bmoWFlowPhase.getKind(),
					bmoWFlowPhase.getWFlowCategoryId().getName(),
					bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getId()
					);
			wFlowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), wFlowCategoryFilter);

		}

		@Override
		public void populateFields(){
			bmoWFlowStep = (BmoWFlowStep)getBmObject();

			// Asigna valores default si es nueva ligada
			if (isNewLinked()) {
				try {
					id = 0;
					bmoWFlowStep.setId(0);
					bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_USER);
					bmoWFlowStep.getName().setValue("");
					bmoWFlowStep.getSequence().setValue(bmoWFlowStep.getSequence().toInteger() + 1);
					bmoWFlowStep.getProgress().setValue(0);
					bmoWFlowStep.getStartdate().setValue("");
					bmoWFlowStep.getEnddate().setValue("");
					bmoWFlowStep.getCommentLog().setValue("");
					bmoWFlowStep.getWFlowActionId().setValue("");
					bmoWFlowStep.getWFlowValidationId().setValue("");
				} catch (BmException e) {
					showSystemMessage("Error al asignar valor: " + e.toString());
				}
			}
			
			formFlexTable.addSectionLabel(1, 0, generalSection, 2);

			if (!newRecord) {
				formFlexTable.addLabelField(2, 0, bmoWFlowStep.getBmoWFlow().getCode());
				formFlexTable.addLabelField(3, 0, bmoWFlowStep.getBmoWFlow().getName());
			}

			if (bmoWFlowStep.getType().toChar() != BmoWFlowStep.TYPE_WFLOW) {
				// Tarea creada por el usuario
				formFlexTable.addField(4, 0, wFlowPhaseListBox, bmoWFlowStep.getWFlowPhaseId());
				formFlexTable.addField(5, 0, sequenceTextBox, bmoWFlowStep.getSequence());
				formFlexTable.addField(6, 0, nameTextBox, bmoWFlowStep.getName());
				formFlexTable.addField(7, 0, descriptionTextArea, bmoWFlowStep.getDescription());
				if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
					formFlexTable.addField(8, 0, wFlowFunnelListBox, bmoWFlowStep.getWFlowFunnelId());
				formFlexTable.addField(9, 0, profileListBox, bmoWFlowStep.getProfileId());
			} else {
				// Tarea creada por engine WFLOW
				formFlexTable.addField(4, 0, wFlowPhaseListBox, bmoWFlowStep.getWFlowPhaseId());
				wFlowPhaseListBox.setEnabled(false);
				formFlexTable.addFieldReadOnly(5, 0, sequenceTextBox, bmoWFlowStep.getSequence());			
				formFlexTable.addFieldReadOnly(6, 0, nameTextBox, bmoWFlowStep.getName());
				if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
					formFlexTable.addField(7, 0, wFlowFunnelListBox, bmoWFlowStep.getWFlowFunnelId());
				formFlexTable.addField(8, 0, descriptionTextArea, bmoWFlowStep.getDescription());
				formFlexTable.addField(9, 0, profileListBox, bmoWFlowStep.getProfileId());
				profileListBox.setEnabled(false);
				wFlowFunnelListBox.setEnabled(false);
				deleteButton.setVisible(false);
			}
			showUserList();
			formFlexTable.addField(11, 0, emailRemindersCheckBox, bmoWFlowStep.getEmailReminders());
			formFlexTable.addField(12, 0, fileUploadBox, bmoWFlowStep.getFile());
			formFlexTable.addField(13, 0, fileLinkTextBox, bmoWFlowStep.getFileLink());

			formFlexTable.addSectionLabel(14, 0, progressSection, 2);
			formFlexTable.addField(15, 0, progressListBox, bmoWFlowStep.getProgress());
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableEmailReminderComments().toBoolean()) {
				formFlexTable.addField(16, 0, emailReminderCommentsCheckBox, bmoWFlowStep.getEmailReminderComments());
			}
			formFlexTable.addField(17, 0, commentsTextArea, bmoWFlowStep.getComments());
			formFlexTable.addField(18, 0, remindDateBox, bmoWFlowStep.getRemindDate());
			formFlexTable.addField(19, 0, pendingCheckBox, bmoWFlowStep.getPending());
			if (getSFParams().hasSpecialAccess(BmoWFlowStep.ACCESS_CHANGESENDDATE))
				formFlexTable.addField(20, 0, endDateDateTimeBox, bmoWFlowStep.getEnddate());
			
			formFlexTable.addSectionLabel(21, 0, logSection, 2);
			formFlexTable.addField(22, 0, commentLogTextArea, bmoWFlowStep.getCommentLog());
			commentLogTextArea.setEnabled(false);
			commentLogTextArea.setHeight("250px");

			formFlexTable.hideSection(logSection);
			populateProgressListBox();	
			
			statusEffect();
			
		}
		private void statusEffect() {
			if (bmoWFlowStep.getType().equals(BmoWFlowStep.TYPE_WFLOW)){
				descriptionTextArea.setEnabled(false);
			}
		}
		@Override
		public void postShow() {
			if (bmoWFlowStep.getBmoWFlow() != null) {
				if (bmoWFlowStep.getBmoWFlow().getStatus().equals(BmoWFlow.STATUS_INACTIVE)) {
					saveButton.setVisible(false);
					deleteButton.setVisible(false);
				}
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == profileListBox) {
				showUserList();
			}
		}

		private void showUserList() {
			BmoUser bmoUser = new BmoUser();		
			if (!profileListBox.getSelectedId().equals("0")) {
				// Actualizar lista de usuarios del grupo

				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter userByGroup = new BmFilter();
				userByGroup.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(), 
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + profileListBox.getSelectedId()
						);

				BmFilter activeUser = new BmFilter();
				activeUser.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);

				ArrayList<BmFilter> filters = new ArrayList<BmFilter>();
				filters.add(userByGroup);
				filters.add(activeUser);

				userListBox = new UiListBox(getUiParams(), bmoUser, filters);	

				formFlexTable.addField(10, 0, userListBox, bmoWFlowStep.getUserId());
			} else {
				formFlexTable.addFieldEmpty(10, 0);
			}
		}

		@Override
		public boolean validate(BmObject bmObject) throws BmException {
			//throw new BoException("error de objeto de negocio.");
			// Validaciones especiales, en caso de error enviar una excepcion de objecto de negocio
			bmoWFlowStep = (BmoWFlowStep)bmObject;

			if (!bmoWFlowStep.getEnabled().toBoolean()) {
				showFormMsg("La tarea aún no está habilitada.", "La tarea aún no está habilitada.");
				return false;
			}

			// Confirma si desea efectuar accion
			if (bmoWFlowStep.getWFlowActionId().toInteger() > 0 && bmoWFlowStep.getProgress().toInteger() == 100) {
				if (Window.confirm("Se va a Activar la Acción: " + bmoWFlowStep.getBmoWFlowAction().getName().toString() + " \n(" + bmoWFlowStep.getBmoWFlowAction().getDescription().toString() + ")."))
					return true;
				else {
					showFormMsg("No se aceptó Ejecutar la Acción.", "No se aceptó Ejecutar la Acción: " + bmoWFlowStep.getBmoWFlowAction().getName().toString());
					return false;	
				}
			}
			
			if (bmoWFlowStep.getEmailReminderComments().toBoolean()) {
				if (bmoWFlowStep.getComments().toString().equals("")) {
					showErrorMessage("Debe agregar un comentario para notificar a los Usuarios de Flujo(Colaboradores).");
					return false;
				}
			}

			return true;
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowStep.setId(id);

			if (bmoWFlowStep.getType().toChar() != BmoWFlowStep.TYPE_WFLOW) {

				bmoWFlowStep.getWFlowId().setValue(bmoWFlow.getId());
				bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_USER);
				bmoWFlowStep.getEnabled().setValue(true);
				bmoWFlowStep.getSequence().setValue(sequenceTextBox.getText());
				if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
					bmoWFlowStep.getWFlowFunnelId().setValue(wFlowFunnelListBox.getSelectedId());
				bmoWFlowStep.getName().setValue(nameTextBox.getText());
				bmoWFlowStep.getDescription().setValue(descriptionTextArea.getText());
				bmoWFlowStep.getWFlowPhaseId().setValue(wFlowPhaseListBox.getSelectedId());
				bmoWFlowStep.getProfileId().setValue(profileListBox.getSelectedId());
				bmoWFlowStep.getCommentLog().setValue(commentLogTextArea.getText());
			} else {
				bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_WFLOW);
			}

			bmoWFlowStep.getEmailReminders().setValue(emailRemindersCheckBox.getValue());
			bmoWFlowStep.getUserId().setValue(userListBox.getSelectedId());

			bmoWFlowStep.getRemindDate().setValue(remindDateBox.getTextBox().getText());
			bmoWFlowStep.getComments().setValue(commentsTextArea.getText());
			bmoWFlowStep.getProgress().setValue(progressListBox.getSelectedCode());
			bmoWFlowStep.getPending().setValue(pendingCheckBox.getValue());
			bmoWFlowStep.getFile().setValue(fileUploadBox.getBlobKey());
			bmoWFlowStep.getFileLink().setValue(fileLinkTextBox.getText());
			if (getSFParams().hasSpecialAccess(BmoWFlowStep.ACCESS_CHANGESENDDATE))
				bmoWFlowStep.getEnddate().setValue(endDateDateTimeBox.getDateTime());
			
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableEmailReminderComments().toBoolean()) {
				bmoWFlowStep.getEmailReminderComments().setValue(emailReminderCommentsCheckBox.getValue());
			}
			return bmoWFlowStep;
		}

		@Override
		public void saveNext() {
			// Realiza accion si es 0 o 100 el avance, y si la accion no es nula
			if (bmoWFlowStep.getWFlowActionId().toInteger() > 0) {
				if (bmoWFlowStep.getProgress().toInteger() == 0 
						|| bmoWFlowStep.getProgress().toInteger() == 100) {
					if (wFlowStepAction != null)
						wFlowStepAction.action();
					else
						close();
				} else
					close();
			} else
				close();
		}

		@Override
		public void close() {
			if (isMinimalist()) {
				//				UiWFlowStepPendingList uiWFlowStepPendingList = new UiWFlowStepPendingList(getUiParams(), getDP());		
				//				uiWFlowStepPendingList.show();			
			} else {
				UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoWFlow, wFlowStepAction);		
				uiWFlowStepList.show();
			}
		}

		public void populateProgressListBox() {
			progressListBox.addItem("0%", "0");
			progressListBox.addItem("25%", "25");
			progressListBox.addItem("50%", "50");
			progressListBox.addItem("75%", "75");
			progressListBox.addItem("100%", "100");

			if (bmoWFlowStep.getProgress().toInteger() == 0) progressListBox.setSelectedIndex(0);
			else if (bmoWFlowStep.getProgress().toInteger() == 25) progressListBox.setSelectedIndex(1);
			else if (bmoWFlowStep.getProgress().toInteger() == 50) progressListBox.setSelectedIndex(2);
			else if (bmoWFlowStep.getProgress().toInteger() == 75) progressListBox.setSelectedIndex(3);
			else if (bmoWFlowStep.getProgress().toInteger() == 100) progressListBox.setSelectedIndex(4);
			else progressListBox.setSelectedIndex(0);
		}
	}
}