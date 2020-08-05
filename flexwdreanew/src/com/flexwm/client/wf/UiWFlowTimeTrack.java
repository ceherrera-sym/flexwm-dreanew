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

import java.util.Date;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;


public class UiWFlowTimeTrack extends UiList {
	BmoWFlowTimeTrack bmoWFlowTimeTrack;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiWFlowTimeTrack(UiParams uiParams) {
		super(uiParams, new BmoWFlowTimeTrack());
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
	}
	
	public UiWFlowTimeTrack(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowTimeTrack());
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
	}

	public UiWFlowTimeTrack(UiParams uiParams, Panel defaultPanel, BmoWFlow bmoWFlow) {
		super(uiParams, defaultPanel, new BmoWFlowTimeTrack());
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
		this.bmoWFlow = bmoWFlow;
	}

	@Override
	public void postShow() {

	}


	@Override
	public void create() {
		UiWFlowTimeTrackForm uiWFlowTimeTrackForm = new UiWFlowTimeTrackForm(getUiParams(), 0);
		uiWFlowTimeTrackForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)bmObject;
		UiWFlowTimeTrackForm uiWFlowTimeTrackForm = new UiWFlowTimeTrackForm(getUiParams(), bmoWFlowTimeTrack.getId());
		uiWFlowTimeTrackForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWFlowTimeTrackForm uiWFlowTimeTrackForm = new UiWFlowTimeTrackForm(getUiParams(), bmObject.getId());
		uiWFlowTimeTrackForm.show();
	}

	private class UiWFlowTimeTrackForm extends UiFormDialog { 
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
		UiTextBox minutesTextBox = new UiTextBox();
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiSuggestBox wFlowSuggestBox = new UiSuggestBox(new BmoWFlow());
		UiListBox wFlowStepListBox = new UiListBox(getUiParams(), new BmoWFlowStep());

		public UiWFlowTimeTrackForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowTimeTrack(), id);
			bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
		}

		@Override
		public void populateFields() {
			bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
			try {
				if (newRecord) {
					if (bmoWFlow.getId() > 0)
						bmoWFlowTimeTrack.getWFlowId().setValue(bmoWFlow.getId());

					bmoWFlowTimeTrack.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
					bmoWFlowTimeTrack.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
					bmoWFlowTimeTrack.getEndDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
				}

				// Filtrar tipos de flujo
				BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
				BmFilter filterByWFlow = new BmFilter();
				filterByWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlow.getId());
				wFlowStepListBox.addFilter(filterByWFlow);

			} catch (BmException e) {
				showSystemMessage("Error al Asignar Valor: " + e.toString());
			}

			formFlexTable.addField(1, 0, wFlowSuggestBox, bmoWFlowTimeTrack.getWFlowId());
			formFlexTable.addField(2, 0, wFlowStepListBox, bmoWFlowTimeTrack.getWFlowStepId());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowTimeTrack.getDescription());
			formFlexTable.addField(4, 0, startDateTimeBox, bmoWFlowTimeTrack.getStartDate());
			formFlexTable.addField(5, 0, minutesTextBox, bmoWFlowTimeTrack.getMinutes());
			formFlexTable.addField(6, 0, userSuggestBox, bmoWFlowTimeTrack.getUserId());

			if (bmoWFlow.getId() > 0)
				wFlowSuggestBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowTimeTrack.setId(id);
			bmoWFlowTimeTrack.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowTimeTrack.getStartDate().setValue(startDateTimeBox.getDateTime());
			bmoWFlowTimeTrack.getMinutes().setValue(minutesTextBox.getText());
			bmoWFlowTimeTrack.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoWFlowTimeTrack.getWFlowId().setValue(wFlowSuggestBox.getSelectedId());
			bmoWFlowTimeTrack.getWFlowStepId().setValue(wFlowStepListBox.getSelectedId());
			return bmoWFlowTimeTrack;
		}

		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Si se cambia el Flujo, recarga la lista de Tareas
			if (uiSuggestBox == wFlowSuggestBox && 
					wFlowSuggestBox.getSelectedId() > 0) {
				populateWFlowSteps(wFlowSuggestBox.getSelectedId()); 
			}
		} 

		private void populateWFlowSteps(int wFlowId) {
			BmFilter filterByWFlow = new BmFilter();
			wFlowStepListBox.clear();
			filterByWFlow.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getIdFieldName(), wFlowId);
			wFlowStepListBox.addFilter(filterByWFlow);
			wFlowStepListBox.populate(bmoWFlowTimeTrack.getWFlowStepId());
		}

		@Override
		public void close() {
			list();
		}
	}
}