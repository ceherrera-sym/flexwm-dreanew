/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiDevelopmentBlock extends UiList {
	BmoDevelopmentBlock bmoDevelopmentBlock;

	public UiDevelopmentBlock(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentBlock());
		bmoDevelopmentBlock = (BmoDevelopmentBlock)getBmObject();
	}

	@Override
	public void postShow() {
		if (isMaster())
			addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopmentPhase()), bmoDevelopmentBlock.getBmoDevelopmentPhase());
	}

	@Override
	public void create() {
		UiDevelopmentBlockForm uiDevelopmentBlockForm = new UiDevelopmentBlockForm(getUiParams(), 0);
		uiDevelopmentBlockForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopmentBlock = (BmoDevelopmentBlock)bmObject;
		UiDevelopmentBlockForm uiDevelopmentBlockForm = new UiDevelopmentBlockForm(getUiParams(), bmoDevelopmentBlock.getId());
		uiDevelopmentBlockForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiDevelopmentBlockForm uiDevelopmentBlockForm = new UiDevelopmentBlockForm(getUiParams(), bmObject.getId());
		uiDevelopmentBlockForm.show();
	}

	public class UiDevelopmentBlockForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		CheckBox isOpenCheckBox = new CheckBox();
		UiDateBox startDateDateBox = new UiDateBox();
		UiDateBox endDateDateBox = new UiDateBox();
		UiDateBox readyDateDateBox = new UiDateBox();
		TextBox sectionTextBox = new TextBox();
		CheckBox isTemporallyCheckBox = new CheckBox();
		CheckBox statusProcessCheckBox = new CheckBox();
		TextBox processPercentageTextBox = new TextBox();
		TextArea habitabilityHistoryTextArea = new TextArea();
		UiSuggestBox developmentPhaseSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
		UiFileUploadBox blueprintFileUpload = new UiFileUploadBox(getUiParams());

		// Actualización masiva
		BmoProperty bmoProperty = new BmoProperty();
		Button batchUpdateButton = new Button("Actualizar");
		CheckBox habitabilityCheckBox = new CheckBox();
		UiDateBox finishDateDateBox = new UiDateBox();
		TextBox progressTextBox = new TextBox();

		String progressSection = "Avances";
		String updateMassiveSection = "Actualización Masiva Inmuebles";

		BmoDevelopmentBlock bmoDevelopmentBlock;

		public UiDevelopmentBlockForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDevelopmentBlock(), id); 

			// Botón de actualizar masivamente inmuebles
			batchUpdateButton.setStyleName("formCloseButton");
			batchUpdateButton.setVisible(true);
			batchUpdateButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea actualizar los Inmuebles?")) prepareBatchUpdate();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoDevelopmentBlock = (BmoDevelopmentBlock)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoDevelopmentBlock.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoDevelopmentBlock.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoDevelopmentBlock.getDescription());
			if (isSlave()) {
				// Es derivado de un pedido, no es necesario mostrarlo
				int orderId = Integer.parseInt(getUiParams().getUiProgramParams(bmoDevelopmentBlock.getProgramCode()).getForceFilter().getValue());
				developmentPhaseSuggestBox.setSelectedId(orderId);
			} else {
				formFlexTable.addField(4, 0, developmentPhaseSuggestBox, bmoDevelopmentBlock.getDevelopmentPhaseId());			
			}

			formFlexTable.addField(5, 0, sectionTextBox, bmoDevelopmentBlock.getSection());	
			formFlexTable.addField(6, 0, statusProcessCheckBox, bmoDevelopmentBlock.getStatusProcess());
			formFlexTable.addField(7, 0, isOpenCheckBox, bmoDevelopmentBlock.getIsOpen());
			formFlexTable.addField(8, 0, isTemporallyCheckBox, bmoDevelopmentBlock.getIsTemporally());
			formFlexTable.addField(9, 0, blueprintFileUpload, bmoDevelopmentBlock.getBlueprint());	

			formFlexTable.addSectionLabel(10, 0, progressSection, 2);
			formFlexTable.addField(11, 0, startDateDateBox, bmoDevelopmentBlock.getStartDate());
			formFlexTable.addField(12, 0, endDateDateBox, bmoDevelopmentBlock.getEndDate());
			formFlexTable.addField(13, 0,readyDateDateBox, bmoDevelopmentBlock.getReadyDate());	
			formFlexTable.addField(14, 0, processPercentageTextBox, bmoDevelopmentBlock.getProcessPercentage());
			formFlexTable.addField(15, 0, habitabilityHistoryTextArea, bmoDevelopmentBlock.getHabitabilityHistory());

			formFlexTable.addSectionLabel(16, 0, updateMassiveSection, 2);
			formFlexTable.addField(17, 0, progressTextBox, bmoProperty.getProgress());
			formFlexTable.addField(18, 0, finishDateDateBox, bmoProperty.getFinishDate()); 
			formFlexTable.addField(19, 0, habitabilityCheckBox, bmoProperty.getHabitability());
			formFlexTable.addButtonCell(20, 0, batchUpdateButton);

			if (!newRecord)
				formFlexTable.hideSection(updateMassiveSection);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDevelopmentBlock.setId(id);
			bmoDevelopmentBlock.getCode().setValue(codeTextBox.getText());
			bmoDevelopmentBlock.getName().setValue(nameTextBox.getText());
			bmoDevelopmentBlock.getDescription().setValue(descriptionTextArea.getText());
			bmoDevelopmentBlock.getIsOpen().setValue(isOpenCheckBox.getValue());
			bmoDevelopmentBlock.getStartDate().setValue(startDateDateBox.getTextBox().getText()); 
			bmoDevelopmentBlock.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoDevelopmentBlock.getReadyDate().setValue(readyDateDateBox.getTextBox().getText());
			bmoDevelopmentBlock.getSection().setValue(sectionTextBox.getText());
			bmoDevelopmentBlock.getIsTemporally().setValue(isTemporallyCheckBox.getValue());
			bmoDevelopmentBlock.getStatusProcess().setValue(statusProcessCheckBox.getValue());
			bmoDevelopmentBlock.getProcessPercentage().setValue(processPercentageTextBox.getText());
			bmoDevelopmentBlock.getHabitabilityHistory().setValue(habitabilityHistoryTextArea.getText());
			bmoDevelopmentBlock.getDevelopmentPhaseId().setValue(developmentPhaseSuggestBox.getSelectedId()); 
			bmoDevelopmentBlock.getBlueprint().setValue(blueprintFileUpload.getBlobKey());

			return bmoDevelopmentBlock;
		}

		@Override
		public void close() {
			list();
		}

		private void prepareBatchUpdate() {
			try {
				bmoProperty = new BmoProperty();
				bmoProperty.getProgress().setValue(progressTextBox.getText());
				bmoProperty.getHabitability().setValue(habitabilityCheckBox.getValue());
				bmoProperty.getFinishDate().setValue(finishDateDateBox.getTextBox().getText());

				batchUpdateAction();

			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-prepareBatchUpdate(): " + e.toString());
			}
		}

		private void batchUpdateAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-batchUpdateAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) 
						showSystemMessage("Error al Actualizar Inmuebles: " + result.errorsToString());
					else {
						showSystemMessage("Actualización Exitosa.");
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoDevelopmentBlock.getPmClass(), bmoProperty, BmoDevelopmentBlock.ACTION_BATCHUPDATE, "" + bmoDevelopmentBlock.getId(), callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-copyAction() ERROR: " + e.toString());
			}
		}
	}
}
