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

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiWFlow extends UiList {

	public UiWFlow(UiParams uiParams) {
		super(uiParams, new BmoWFlow());
	}

	@Override
	public void create() {
		UiWFlowForm uiWFlowForm = new UiWFlowForm(getUiParams(), 0);
		uiWFlowForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowForm uiWFlowForm = new UiWFlowForm(getUiParams(), bmObject.getId());
		uiWFlowForm.show();		
	}

	public class UiWFlowForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		UiListBox wFlowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase());
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();	
		TextBox progressTextBox = new TextBox();
		TextBox googleEventIdTextBox = new TextBox();
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		BmoWFlow bmoWFlow;
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		

		public UiWFlowForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlow(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlow = (BmoWFlow)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWFlow.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlow.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlow.getDescription());
			formFlexTable.addField(4, 0, progressTextBox, bmoWFlow.getProgress());
			formFlexTable.addField(5, 0, wFlowTypeListBox, bmoWFlow.getWFlowTypeId());
			formFlexTable.addField(6, 0, wFlowPhaseListBox, bmoWFlow.getWFlowPhaseId());
			formFlexTable.addField(7, 0, startDateBox, bmoWFlow.getStartDate());
			formFlexTable.addField(8, 0, endDateBox, bmoWFlow.getEndDate());
			formFlexTable.addField(9, 0, userSuggestBox, bmoWFlow.getUserId());
			formFlexTable.addField(10, 0, googleEventIdTextBox, bmoWFlow.getGoogleEventId());
			formFlexTable.addField(11, 0, companyListBox, bmoWFlow.getCompanyId());

			if (!newRecord) {
				TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
				tabPanel.setSize("100%", "400px");
				formFlexTable.addPanel(6, 0, tabPanel, 4);

				//				FlowPanel sFlowStepPanel = new FlowPanel();
				//				sFlowStepPanel.setSize("100%", "100%");
				//				ScrollPanel sFlowStepScrollPanel = new ScrollPanel();
				//				sFlowStepScrollPanel.setSize("98%", "355px");
				//				sFlowStepScrollPanel.add(sFlowStepPanel);
				//				UiWFlowStepFormList uiWFlowStep = new UiWFlowStepFormList(getUiParams(), sFlowStepPanel, id);
				//				uiWFlowStep.show();
				//				tabPanel.add(sFlowStepScrollPanel, "Tareas Flujo");

				//				FlowPanel wFlowDocumentPanel = new FlowPanel();
				//				wFlowDocumentPanel.setSize("100%", "100%");
				//				ScrollPanel wFlowDocumentScrollPanel = new ScrollPanel();
				//				wFlowDocumentScrollPanel.setSize("98%", "355px");
				//				wFlowDocumentScrollPanel.add(wFlowDocumentPanel);
				//				UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentPanel, id);
				//				uiWFlowDocument.show();
				//				tabPanel.add(wFlowDocumentScrollPanel, "Documentos Flujo");

				FlowPanel wFlowUserPanel = new FlowPanel();
				wFlowUserPanel.setSize("100%", "100%");
				ScrollPanel wFlowUserScrollPanel = new ScrollPanel();
				wFlowUserScrollPanel.setSize("98%", "355px");
				wFlowUserScrollPanel.add(wFlowUserPanel);
				UiWFlowUser uiWFlowUser = new UiWFlowUser(getUiParams(), wFlowUserPanel, bmoWFlow);
				uiWFlowUser.show();
				tabPanel.add(wFlowUserScrollPanel, "Usuarios Flujo");
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlow.setId(id);
			bmoWFlow.getCode().setValue(codeTextBox.getText());
			bmoWFlow.getName().setValue(nameTextBox.getText());
			bmoWFlow.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlow.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoWFlow.getWFlowPhaseId().setValue(wFlowPhaseListBox.getSelectedId());
			bmoWFlow.getStartDate().setValue(startDateBox.getDateTime());
			bmoWFlow.getEndDate().setValue(endDateBox.getDateTime());
			bmoWFlow.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoWFlow.getGoogleEventId().setValue(googleEventIdTextBox.getText());
			bmoWFlow.getCompanyId().setValue(companyListBox.getSelectedId());
			return bmoWFlow;
		}

		@Override
		public void close() {
			UiWFlow uiWFlowList = new UiWFlow(getUiParams());
			uiWFlowList.show();
		}

		@Override
		public void saveNext() {
			if (id > 0) { 
				showFormMsg("Cambios almacenados exitosamente", "Cambios almacenados exitosamente.");
			} else {
				UiWFlowDetail uiWFlowDetail = new UiWFlowDetail(getUiParams(), getBmObject().getId());
				uiWFlowDetail.show();
			}		
		}
	}
}