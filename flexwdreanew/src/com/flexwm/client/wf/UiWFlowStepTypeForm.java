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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowValidation;

public class UiWFlowStepTypeForm extends UiForm {
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

	BmoWFlowStepType bmoWFlowStepType;
	String wFlowTypeId;
	
	public UiWFlowStepTypeForm(UiParams uiParams, int id) {
		super(uiParams, new BmoWFlowStepType(), id);
		bmoWFlowStepType = (BmoWFlowStepType)getBmObject();
		
		BmFilter bmFilterPhase = getUiParams().getUiProgramParams(new BmoWFlowPhase().getProgramCode()).getForceFilter();
		wFlowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), bmFilterPhase);
	}
	
	@Override
	public void populateFields(){
		bmoWFlowStepType = (BmoWFlowStepType)getBmObject();
		
		// Obten las variables del filtro forzado
		BmFilter forceFilter = getUiParams().getUiProgramParams(bmoWFlowStepType.getProgramCode()).getForceFilter();
		wFlowTypeId = forceFilter.getValue();
		
		formFlexTable.addLabelField(1, 0, forceFilter.getFieldLabel(), forceFilter.getValueLabel());
		formFlexTable.addField(1, 2, sequenceTextBox, bmoWFlowStepType.getSequence());
		formFlexTable.addField(2, 2, nameTextBox, bmoWFlowStepType.getName());
		
		formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowStepType.getDescription());
		formFlexTable.addField(3, 2, wFlowFunnelListBox, bmoWFlowStepType.getWFlowFunnelId());
		
		formFlexTable.addField(4, 0, daysRemindTextBox, bmoWFlowStepType.getDaysRemind());
		formFlexTable.addField(4, 2, emailRemindersCheckBox, bmoWFlowStepType.getEmailReminders());
		
		formFlexTable.addField(5, 0, wFlowValidationListBox, bmoWFlowStepType.getWFlowValidationId());
		formFlexTable.addField(5, 2, wFlowActionListBox, bmoWFlowStepType.getWFlowActionId());
		
		formFlexTable.addField(6, 0, profileListBox, bmoWFlowStepType.getProfileId());
		formFlexTable.addField(6, 2, wFlowPhaseListBox, bmoWFlowStepType.getWFlowPhaseId());
		
		if (!newRecord) {
			TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
			tabPanel.setSize("100%", "300px");
			
			FlowPanel stepDepPanel = new FlowPanel();
			stepDepPanel.setSize("100%", "100%");
			ScrollPanel stepDepScrollPanel = new ScrollPanel();
			stepDepScrollPanel.setSize("98%", "250px");
			stepDepScrollPanel.add(stepDepPanel);

			UiWFlowStepTypeDep uiWFlowStepTypeDep = new UiWFlowStepTypeDep(getUiParams(), stepDepPanel, bmoWFlowStepType);
			uiWFlowStepTypeDep.show();
			tabPanel.add(stepDepScrollPanel, "Dependencias de los Pasos");
			
			formFlexTable.addPanel(7, 0, tabPanel, 4);
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
		
		return bmoWFlowStepType;
	}
	
	@Override
	public void close() {
		UiWFlowStepType uiWFlowStepTypeList = new UiWFlowStepType(getUiParams());
		uiWFlowStepTypeList.show();
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