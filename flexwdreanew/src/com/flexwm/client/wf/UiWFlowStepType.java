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
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;
import com.flexwm.shared.wf.BmoWFlowValidation;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;

public class UiWFlowStepType extends UiList {

	public UiWFlowStepType(UiParams uiParams) {
		super(uiParams, new BmoWFlowStepType());
	}

	@Override
	public void create() {
		UiWFlowStepTypeForm uiWFlowStepTypeForm = new UiWFlowStepTypeForm(getUiParams(), 0);
		uiWFlowStepTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowStepTypeForm uiWFlowStepTypeForm = new UiWFlowStepTypeForm(getUiParams(), bmObject.getId());
		uiWFlowStepTypeForm.show();
	}

	public class UiWFlowStepTypeForm extends UiFormDialog {
		BmoWFlowStepType bmoWFlowStepType;
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox sequenceTextBox = new TextBox();
		TextBox validateClassTextBox = new TextBox();
		TextBox actionClassTextBox = new TextBox();
		TextBox daysRemindTextBox = new TextBox();
		CheckBox emailRemindersCheckBox = new CheckBox();
		UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		UiListBox wFlowPhaseListBox;
		UiListBox wFlowParentStepTypeListBox = new UiListBox(getUiParams(), new BmoWFlowStepType());
		UiListBox wFlowValidationListBox = new UiListBox(getUiParams(), new BmoWFlowValidation());
		UiListBox wFlowActionListBox = new UiListBox(getUiParams(), new BmoWFlowAction());
		UiListBox wFlowFunnelListBox = new UiListBox(getUiParams(), new BmoWFlowFunnel());
		
		TextBox hoursTextBox = new TextBox();
		CheckBox billableCheckBox = new CheckBox();
		TextBox rateTextBox = new TextBox();
		
		String wFlowTypeId;
		BmFilter forceFilter;

		public UiWFlowStepTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowStepType(), id);
			bmoWFlowStepType = (BmoWFlowStepType)getBmObject();

			// Obten las variables del filtro forzado
			forceFilter = getUiParams().getUiProgramParams(bmoWFlowStepType.getProgramCode()).getForceFilter();
			wFlowTypeId = forceFilter.getValue();
			
			BmFilter bmFilterPhase = getUiParams().getUiProgramParams(new BmoWFlowPhase().getProgramCode()).getForceFilter();
			wFlowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), bmFilterPhase);
		}

		@Override
		public void populateFields(){
			bmoWFlowStepType = (BmoWFlowStepType)getBmObject();

			formFlexTable.addLabelField(1, 0, forceFilter.getFieldLabel(), forceFilter.getValueLabel());
			formFlexTable.addField(2, 0, sequenceTextBox, bmoWFlowStepType.getSequence());
			formFlexTable.addField(3, 0, nameTextBox, bmoWFlowStepType.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWFlowStepType.getDescription());
			formFlexTable.addField(5, 0, wFlowFunnelListBox, bmoWFlowStepType.getWFlowFunnelId());
			formFlexTable.addField(6, 0, daysRemindTextBox, bmoWFlowStepType.getDaysRemind());
			formFlexTable.addField(7, 0, emailRemindersCheckBox, bmoWFlowStepType.getEmailReminders());
			formFlexTable.addField(8, 0, wFlowValidationListBox, bmoWFlowStepType.getWFlowValidationId());
			formFlexTable.addField(9, 0, wFlowActionListBox, bmoWFlowStepType.getWFlowActionId());
			formFlexTable.addField(10, 0, profileListBox, bmoWFlowStepType.getProfileId());
			formFlexTable.addField(11, 0, wFlowPhaseListBox, bmoWFlowStepType.getWFlowPhaseId());
			formFlexTable.addField(12, 0, hoursTextBox, bmoWFlowStepType.getHours());
			formFlexTable.addField(13, 0, billableCheckBox, bmoWFlowStepType.getBillable());
			formFlexTable.addField(14, 0, rateTextBox, bmoWFlowStepType.getRate());

			if (!newRecord) {
				// Fases de Flujos
				BmoWFlowStepTypeDep bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
				FlowPanel wFlowStepTypeDepFP = new FlowPanel();
				UiWFlowStepTypeDep uiWFlowStepTypeDep = new UiWFlowStepTypeDep(getUiParams(), wFlowStepTypeDepFP, bmoWFlowStepType);
				setUiType(bmoWFlowStepTypeDep.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowStepTypeDep.show();
				formFlexTable.addPanel(15, 0, wFlowStepTypeDepFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowStepType.setId(id);
			bmoWFlowStepType.getName().setValue(nameTextBox.getText());
			bmoWFlowStepType.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowStepType.getWFlowValidationId().setValue(wFlowValidationListBox.getSelectedId());
			bmoWFlowStepType.getWFlowActionId().setValue(wFlowActionListBox.getSelectedId());
			bmoWFlowStepType.getDaysRemind().setValue(daysRemindTextBox.getText());
			bmoWFlowStepType.getEmailReminders().setValue(emailRemindersCheckBox.getValue());
			bmoWFlowStepType.getSequence().setValue(sequenceTextBox.getText());
			bmoWFlowStepType.getWFlowFunnelId().setValue(wFlowFunnelListBox.getSelectedId());
			bmoWFlowStepType.getProfileId().setValue(profileListBox.getSelectedId());
			bmoWFlowStepType.getWFlowTypeId().setValue(wFlowTypeId);
			bmoWFlowStepType.getWFlowPhaseId().setValue(wFlowPhaseListBox.getSelectedId());
			bmoWFlowStepType.getHours().setValue(hoursTextBox.getText());
			bmoWFlowStepType.getBillable().setValue(billableCheckBox.getValue());
			bmoWFlowStepType.getRate().setValue(rateTextBox.getText());
			return bmoWFlowStepType;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiWFlowStepTypeForm wFlowStepTypeForm = new UiWFlowStepTypeForm(getUiParams(), getBmObject().getId());
				wFlowStepTypeForm.show();
			} else {
				close();
			}		
		}
	}
}
