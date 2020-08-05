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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiCheckBoxClickHandler;
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
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;

import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowStepDep;


public class UiWFlowStepActivity extends UiList {
	BmoWFlowStep bmoWFlowStep;
	boolean finishedTask = false;
	int wFlowId;

	public UiWFlowStepActivity(UiParams uiParams, Panel defaultPanel, int wFlowId) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		this.bmoWFlowStep = (BmoWFlowStep)getBmObject();
		this.wFlowId = wFlowId;
	}

	@Override
	public void create() {
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), 0);
		uiWFlowStepForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), bmObject.getId());
		uiWFlowStepForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		UiWFlowStepForm uiWFlowStepForm = new UiWFlowStepForm(getUiParams(), bmObject.getId());
		uiWFlowStepForm.show();
	}

	@Override
	public void displayList() {		
		int col = 0;

		addColumnHeader(col++, "Abrir");
		addColumnHeader(col++, bmoWFlowStep.getName());
		if (!isMobile()) {
			addColumnHeader(col++, bmoWFlowStep.getBmoUser().getCode());
			addColumnHeader(col++, bmoWFlowStep.getStartdate());
			addColumnHeader(col++, bmoWFlowStep.getFile());			
			addColumnHeader(col++, bmoWFlowStep.getFileLink());	
		}
		addColumnHeader(col++, "Finalizar");
		addColumnHeader(col++, bmoWFlowStep.getProgress());	
		if (!isMobile()) { 
			addColumnHeader(col++, "Status");
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

				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoUser().getCode());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getStartdate());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getFile());
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

					}
				});
				listFlexTable.setWidget(row, col, wFlowStepCheckBox);
				listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellCheckBox");
			} else 
				col++;
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getProgress());

			if (!isMobile()) {
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getEnabled());
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


	public class UiWFlowStepForm extends UiFormDialog {
		protected TextBox nameTextBox = new TextBox();
		protected TextArea commentsTextArea = new TextArea();
		protected TextArea descriptionTextArea = new TextArea();
		protected TextBox validateClassTextBox = new TextBox();
		protected CheckBox emailRemindersCheckBox = new CheckBox();
		protected UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		protected UiDateTimeBox endDateDateTimeBox = new UiDateTimeBox();

		protected UiListBox progressListBox = new UiListBox(getUiParams());
		protected UiDateBox remindDateBox = new UiDateBox();
		protected UiFileUploadBox fileUploadBox = new UiFileUploadBox(getUiParams());
		protected TextBox fileLinkTextBox = new TextBox();
		protected BmoWFlowStep bmoWFlowStep;
		protected IWFlowStepAction wFlowStepAction;

		protected String generalSection = "Datos Generales";
		protected String progressSection = "Actualizar Avances";

		protected boolean newLinked = false;

		public boolean isNewLinked() {
			return newLinked;
		}

		public void setNewLinked(boolean newLinked) {
			this.newLinked = newLinked;
		}

		public UiWFlowStepForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowStep(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowStep = (BmoWFlowStep)getBmObject();

			// Asignar datos predeterminados
			try {
				if (!(bmoWFlowStep.getUserId().toInteger() > 0)) {
					bmoWFlowStep.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
				}
				
				// Modifica titulo
				if (!newRecord) {
					formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoWFlowStep.getName().toString());
				} else {
					bmoWFlowStep.getEmailReminders().setValue(true);
				}
					
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}

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

			if (bmoWFlowStep.getType().toChar() != BmoWFlowStep.TYPE_WFLOW) {
				// Tarea creada por el usuario
				formFlexTable.addField(6, 0, nameTextBox, bmoWFlowStep.getName());
				formFlexTable.addField(7, 0, descriptionTextArea, bmoWFlowStep.getDescription());
			} else {
				// Tarea creada por engine WFLOW
				formFlexTable.addFieldReadOnly(6, 0, nameTextBox, bmoWFlowStep.getName());
				formFlexTable.addField(8, 0, descriptionTextArea, bmoWFlowStep.getDescription());
				deleteButton.setVisible(false);
			}

			formFlexTable.addField(9, 0, userSuggestBox, bmoWFlowStep.getUserId());
			formFlexTable.addField(11, 0, emailRemindersCheckBox, bmoWFlowStep.getEmailReminders());
			formFlexTable.addField(12, 0, fileUploadBox, bmoWFlowStep.getFile());
			formFlexTable.addField(13, 0, fileLinkTextBox, bmoWFlowStep.getFileLink());

			formFlexTable.addSectionLabel(14, 0, progressSection, 2);
			formFlexTable.addField(15, 0, progressListBox, bmoWFlowStep.getProgress());
			formFlexTable.addField(16, 0, commentsTextArea, bmoWFlowStep.getComments());
			formFlexTable.addField(17, 0, remindDateBox, bmoWFlowStep.getRemindDate());
			if (getSFParams().hasSpecialAccess(BmoWFlowStep.ACCESS_CHANGESENDDATE))
				formFlexTable.addField(18, 0, endDateDateTimeBox, bmoWFlowStep.getEnddate());
			if (!newRecord) {
				// Fases de Flujos
				BmoWFlowStepDep bmoWFlowStepDep = new BmoWFlowStepDep();
				FlowPanel wFlowStepDepFP = new FlowPanel();
				UiWFlowStepDep uiWFlowStepDep = new UiWFlowStepDep(getUiParams(), wFlowStepDepFP, bmoWFlowStep);
				setUiType(bmoWFlowStepDep.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowStepDep.show();
				formFlexTable.addPanel(20, 0, wFlowStepDepFP, 2);
			}

			populateProgressListBox();	

			if (newRecord)
				formFlexTable.hideSection(progressSection);
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

			return true;
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowStep.setId(id);

			if (bmoWFlowStep.getType().toChar() != BmoWFlowStep.TYPE_WFLOW) {

				bmoWFlowStep.getWFlowId().setValue(wFlowId);
				bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_USER);
				bmoWFlowStep.getEnabled().setValue(true);
				bmoWFlowStep.getName().setValue(nameTextBox.getText());
				bmoWFlowStep.getDescription().setValue(descriptionTextArea.getText());
			} else {
				bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_WFLOW);
			}

			bmoWFlowStep.getEmailReminders().setValue(emailRemindersCheckBox.getValue());
			bmoWFlowStep.getUserId().setValue(userSuggestBox.getSelectedId());

			bmoWFlowStep.getRemindDate().setValue(remindDateBox.getTextBox().getText());
			bmoWFlowStep.getComments().setValue(commentsTextArea.getText());
			bmoWFlowStep.getProgress().setValue(progressListBox.getSelectedCode());
			bmoWFlowStep.getFile().setValue(fileUploadBox.getBlobKey());
			bmoWFlowStep.getFileLink().setValue(fileLinkTextBox.getText());
			if (getSFParams().hasSpecialAccess(BmoWFlowStep.ACCESS_CHANGESENDDATE))
				bmoWFlowStep.getEnddate().setValue(endDateDateTimeBox.getDateTime());
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
			list();
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