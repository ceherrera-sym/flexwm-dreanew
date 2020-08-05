package com.flexwm.client.wf;

import java.util.Date;

import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiAppointment;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;


public class UiWFlowTimeTrackCalendar extends UiCalendar {
	BmoWFlowTimeTrack bmoWFlowTimeTrack;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiWFlowTimeTrackCalendar(UiParams uiParams) {
		super(uiParams, new BmoWFlowTimeTrack());
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
		initialize();
	}

	public UiWFlowTimeTrackCalendar(UiParams uiParams, BmoWFlow bmoWFlow) {
		super(uiParams, new BmoWFlowTimeTrack());
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();
		this.bmoWFlow = bmoWFlow;
		initialize();
	}

	private void initialize() {
		dateFieldName = bmoWFlowTimeTrack.getStartDate().getName();
	}
	
	@Override
	public void postShow() {
	
	}

	@Override
	public void displayList() {	
		calendar.suspendLayout();
		while (iterator.hasNext()) {
			BmoWFlowTimeTrack bmoWFlowTimeTrack = (BmoWFlowTimeTrack)iterator.next();
			if (bmoWFlowTimeTrack.getEndDate().toString().length() > 0) {
				UiAppointment appt = new UiAppointment(bmoWFlowTimeTrack);
				appt.setStart(new Date(bmoWFlowTimeTrack.getStartDate().toMilliseconds(getSFParams())));
				appt.setEnd(new Date(bmoWFlowTimeTrack.getEndDate().toMilliseconds(getSFParams())));
					
				// Dependiendo si esta dentro de un flujo muestra el titulo
				String title = "";
				if (bmoWFlow.getId() > 0) {
					title += bmoWFlowTimeTrack.getBmoUser().getCode().toString();
				} else { 
					title += bmoWFlowTimeTrack.getBmoWFlow().getName().toString();
					title += " - " + bmoWFlowTimeTrack.getBmoUser().getCode().toString();
				}
				title += " - " + bmoWFlowTimeTrack.getStartDate().toString();
				appt.setTitle(title);
				
				addAppointment(appt);
			}
		}
		calendar.scrollToHour(8);
		calendar.resumeLayout();
	}

	@Override
	protected void moveAppointment(UiAppointment uiAppointment) {
		if (Window.confirm("Esta seguro que desea mover el Registro?")) {
			bmoWFlowTimeTrack = (BmoWFlowTimeTrack)uiAppointment.getBmObject();
			try {
				bmoWFlowTimeTrack.getStartDate().setValue(GwtUtil.dateToString(uiAppointment.getStart(), getSFParams().getDateTimeFormat()));
				bmoWFlowTimeTrack.getEndDate().setValue(GwtUtil.dateToString(uiAppointment.getEnd(), getSFParams().getDateTimeFormat()));				
				save(bmoWFlowTimeTrack);

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-moveAppointment() ERROR: " + e.toString());
			}
		}
	}

	@Override
	protected void emptySelection(TimeBlockClickEvent<Date> event) {
		UiWFlowStepTrackForm uiWFlowStepTrackForm = new UiWFlowStepTrackForm(getUiParams(), 0, event.getTarget());
		uiWFlowStepTrackForm.show();
	}

	@Override
	public void create() {
		UiWFlowStepTrackForm uiWFlowStepTrackForm = new UiWFlowStepTrackForm(getUiParams(), 0);
		uiWFlowStepTrackForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowStepTrackForm uiWFlowStepTrackForm = new UiWFlowStepTrackForm(getUiParams(), bmObject.getId());
		uiWFlowStepTrackForm.show();	
	}


	public class UiWFlowStepTrackForm extends UiFormDialog {
		BmoWFlowTimeTrack bmoWFlowTimeTrack;
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox endDateTimeBox = new UiDateTimeBox();
		UiTextBox minutesTextBox = new UiTextBox();
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiSuggestBox wFlowSuggestBox = new UiSuggestBox(new BmoWFlow());
		UiListBox wFlowStepListBox = new UiListBox(getUiParams(), new BmoWFlowStep());
		Date createStartDate;

		public UiWFlowStepTrackForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowTimeTrack(), id);
			this.createStartDate = new Date();
		}

		public UiWFlowStepTrackForm(UiParams uiParams, int id, Date createStartDate) {
			super(uiParams, new BmoWFlowTimeTrack(), id);
			this.createStartDate = createStartDate;
		}

		@Override
		public void populateFields(){
			bmoWFlowTimeTrack = (BmoWFlowTimeTrack)getBmObject();

			try {
				if (newRecord) {
					if (bmoWFlow.getId() > 0)
						bmoWFlowTimeTrack.getWFlowId().setValue(bmoWFlow.getId());

					bmoWFlowTimeTrack.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
					bmoWFlowTimeTrack.getStartDate().setValue(GwtUtil.dateToString(createStartDate, getSFParams().getDateTimeFormat()));
					Date createEndDate = createStartDate;
					createEndDate.setTime(createStartDate.getTime() + 60 * 60000);
					bmoWFlowTimeTrack.getEndDate().setValue(GwtUtil.dateToString(createEndDate, getSFParams().getDateTimeFormat()));
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
			wFlowStepListBox.clearFilters();
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
