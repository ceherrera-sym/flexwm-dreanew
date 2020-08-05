/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;

import java.util.Date;

import com.flexwm.client.wf.UiWFlowStepActivity;
import com.flexwm.shared.cv.BmoActivity;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiActivity extends UiList {
	BmoActivity bmoActivity;

	public UiActivity(UiParams uiParams) {
		super(uiParams, new BmoActivity());
		bmoActivity = (BmoActivity)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoActivity.getUserId());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoActivity.getStatus()), bmoActivity, bmoActivity.getStatus(), "" + BmoActivity.STATUS_ACTIVE);
		addDateRangeFilterListBox(bmoActivity.getStartdate());
	}
	
	@Override
	public void create() {
		UiActivityForm uiActivityForm = new UiActivityForm(getUiParams(), 0);
		uiActivityForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoActivity = (BmoActivity)bmObject;
		UiActivityForm uiActivityForm = new UiActivityForm(getUiParams(), bmoActivity.getId());
		uiActivityForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiActivityForm uiActivityForm = new UiActivityForm(getUiParams(), bmObject.getId());
		uiActivityForm.show();
	}

	public class UiActivityForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateDateBox = new UiDateTimeBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
		UiListBox wFlowTypeListBox;
		protected UiDateBox remindDateBox = new UiDateBox();
		protected CheckBox emailRemindersCheckBox = new CheckBox();
		

		BmoActivity bmoActivity;
		int programId;
		
		public UiActivityForm(UiParams uiParams, int id) {
			super(uiParams, new BmoActivity(), id);
			bmoActivity = (BmoActivity)getBmObject();
			initialize();
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoActivity.getProgramCode());
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por usuarios activos
			BmoUser bmoUser = new BmoUser();
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterUserActive);
		}

		@Override
		public void populateFields(){
			bmoActivity = (BmoActivity)getBmObject();
			
			// Asignar datos predeterminados
			try {
				if (!(bmoActivity.getUserId().toInteger() > 0)) {
					bmoActivity.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
				}
				
				// Modifica titulo
				if (!newRecord)
					formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoActivity.getName().toString());
				else {
					bmoActivity.getStartdate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
				}
				
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}
			
			formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoActivity.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoActivity.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoActivity.getDescription());
			formFlexTable.addField(4, 0, userSuggestBox, bmoActivity.getUserId());
			formFlexTable.addField(5, 0, startDateDateBox, bmoActivity.getStartdate());
			formFlexTable.addField(6, 0, endDateDateBox, bmoActivity.getEnddate());
			formFlexTable.addField(7, 0, emailRemindersCheckBox, bmoActivity.getEmailReminders());
			formFlexTable.addField(8, 0, remindDateBox, bmoActivity.getRemindDate());
			
			
			
			
			formFlexTable.addField(9, 0, statusListBox, bmoActivity.getStatus());
			
			if (!newRecord) {	
				// WFlowSteps
				BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
				FlowPanel budgetItemFP = new FlowPanel();
				BmFilter filterWFlowSteps = new BmFilter();
				filterWFlowSteps.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoActivity.getWFlowId().toInteger());
				getUiParams().setForceFilter(bmoWFlowStep.getProgramCode(), filterWFlowSteps);
				UiWFlowStepActivity uiWFlowStepActivity = new UiWFlowStepActivity(getUiParams(), budgetItemFP, bmoActivity.getWFlowId().toInteger());
				setUiType(bmoWFlowStep.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowStepActivity.show();
				formFlexTable.addPanel(10, 0, budgetItemFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoActivity.setId(id);
			bmoActivity.getCode().setValue(codeTextBox.getText());
			bmoActivity.getName().setValue(nameTextBox.getText());
			bmoActivity.getDescription().setValue(descriptionTextArea.getText());
			bmoActivity.getStatus().setValue(statusListBox.getSelectedCode());
			bmoActivity.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoActivity.getStartdate().setValue(startDateDateBox.getDateTime());
			bmoActivity.getEnddate().setValue(endDateDateBox.getDateTime());
			bmoActivity.getEmailReminders().setValue(emailRemindersCheckBox.getValue());
			bmoActivity.getRemindDate().setValue(remindDateBox.getTextBox().getText());

			return bmoActivity;
		}

		@Override
		public void close() {
			list();
		}
		
		@Override
		public void saveNext() {
			list();
			if (newRecord) { 
				UiActivityForm uiActivityForm = new UiActivityForm(getUiParams(), getBmObject().getId());
				uiActivityForm.show();
			}	
		}
		
	}
}
