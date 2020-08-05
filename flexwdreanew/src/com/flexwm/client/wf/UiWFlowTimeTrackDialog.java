package com.flexwm.client.wf;

import java.util.Date;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiWFlowTimeTrackDialog extends Ui {
	HorizontalPanel hp = new HorizontalPanel();
	Image timeClockImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/ustc.png"));
	Button saveTimeClockButton = new Button("GUARDAR");
	HorizontalPanel buttonPanel = new HorizontalPanel();
	DialogBox timeClockDialogBox;

	TextArea descriptionTextArea = new TextArea();
	UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
	UiDateTimeBox endDateTimeBox = new UiDateTimeBox();
	UiSuggestBox wFlowSuggestBox = new UiSuggestBox(new BmoWFlow());
	UiListBox wFlowStepListBox = new UiListBox(getUiParams(), new BmoWFlowStep());

	BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
	BmoWFlow bmoWFlow = new BmoWFlow();
	Panel containerPanel;
	int userId;

	public UiWFlowTimeTrackDialog(UiParams uiParams, Panel defaultPanel) {
		super(uiParams);
		this.containerPanel = defaultPanel;
		userId = uiParams.getSFParams().getLoginInfo().getUserId();

		timeClockImage.setTitle(getSFParams().getProgramTitle(bmoWFlowTimeTrack));
		timeClockImage.addStyleName("bottomImage");
		timeClockImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				prepareTimeClockDialog();
			}
		});

		// Botones
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		saveTimeClockButton.setStyleName("formCloseButton");
		saveTimeClockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveTimeClock();
			}
		});
		buttonPanel.add(saveTimeClockButton);

		prepareImage();
	}

	public void prepareImage() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-prepareTimeClockDialog() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				show((BmoWFlowTimeTrack)result.getBmObject());
			}
		};

		try {	
			startLoading();
			getUiParams().getBmObjectServiceAsync().action(bmoWFlowTimeTrack.getPmClass(), bmoWFlowTimeTrack, BmoWFlowTimeTrack.ACTION_LASTWFLOWTRACK, "" + userId, callback);
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-prepareTimeClockDialog() ERROR: " + e.toString());
		}
	}

	public void show(BmoWFlowTimeTrack bmoWFlowTimeTrack) {

		if (bmoWFlowTimeTrack.getId() > 0) {
			timeClockImage.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/ustc_out.png"));
			timeClockImage.setTitle("Finalizar");
		} else {
			timeClockImage.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/ustc_in.png"));
			timeClockImage.setTitle("Iniciar");
		}

		hp.add(timeClockImage);
		containerPanel.add(hp);
	}

	public void prepareTimeClockDialog() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-prepareTimeClockDialog() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				showTimeClockDialog((BmoWFlowTimeTrack)result.getBmObject());
			}
		};

		try {	
			startLoading();
			getUiParams().getBmObjectServiceAsync().action(bmoWFlowTimeTrack.getPmClass(), bmoWFlowTimeTrack, BmoWFlowTimeTrack.ACTION_LASTWFLOWTRACK, "" + userId, callback);
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-prepareTimeClockDialog() ERROR: " + e.toString());
		}
	}


	public void showTimeClockDialog(BmoWFlowTimeTrack bmoWFlowTimeTrack) {
		this.bmoWFlowTimeTrack = bmoWFlowTimeTrack;
		String titleSuffix = ": "; 


		if (bmoWFlowTimeTrack.getId() > 0) {
			saveTimeClockButton.setText("FINALIZAR");
			titleSuffix += "Finalizar";
		} else { 
			saveTimeClockButton.setText("INICIAR");
			titleSuffix += "Iniciar";
		}


		timeClockDialogBox = new DialogBox(true);
		timeClockDialogBox.setGlassEnabled(true);
		timeClockDialogBox.setText("Rastreo Tiempo" + titleSuffix);
		timeClockDialogBox.setSize("400px", "230px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("100%", "100%");

		UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
		// Manejo de acciones de suggest box
		UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
			@Override
			public void onSelect(UiSuggestBox uiSuggestBox) {
				formSuggestionSelectionChange(uiSuggestBox);
			}
		};
		formFlexTable.setUiSuggestBoxAction(uiSuggestBoxAction);

		try {
			if (bmoWFlowTimeTrack.getId() > 0) {
				bmoWFlowTimeTrack.getEndDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
			} else {
				bmoWFlowTimeTrack.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
				bmoWFlowTimeTrack.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
				bmoWFlowTimeTrack.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
			}

			// Filtrar tipos de flujo
			BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
			BmFilter filterByWFlow = new BmFilter();
			filterByWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlowTimeTrack.getWFlowId().toInteger());
			wFlowStepListBox.addFilter(filterByWFlow);

			startDateTimeBox.setEnabled(false);
			endDateTimeBox.setEnabled(false);

		} catch (BmException e) {
			showSystemMessage("Error al Asignar Valor: " + e.toString());
		}

		formFlexTable.addField(1, 0, wFlowSuggestBox, bmoWFlowTimeTrack.getWFlowId());
		formFlexTable.addField(2, 0, wFlowStepListBox, bmoWFlowTimeTrack.getWFlowStepId());
		formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowTimeTrack.getDescription());
		formFlexTable.addField(4, 0, startDateTimeBox, bmoWFlowTimeTrack.getStartDate());
		formFlexTable.addField(5, 0, endDateTimeBox, bmoWFlowTimeTrack.getEndDate());
		formFlexTable.addButtonPanel(buttonPanel);

		vp.add(new HTML("&nbsp"));
		vp.add(formFlexTable);

		timeClockDialogBox.add(vp);
		timeClockDialogBox.center();
		timeClockDialogBox.show();
	}

	private void saveTimeClock() {
		try {
			bmoWFlowTimeTrack.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowTimeTrack.getStartDate().setValue(startDateTimeBox.getDateTime());
			bmoWFlowTimeTrack.getEndDate().setValue(endDateTimeBox.getDateTime());
			bmoWFlowTimeTrack.getWFlowId().setValue(wFlowSuggestBox.getSelectedId());
			bmoWFlowTimeTrack.getWFlowStepId().setValue(wFlowStepListBox.getSelectedId());
		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-saveTimeClock() ERROR: " + e.toString());
		}
		save();
	}

	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
		// Si se cambia el Flujo, recarga la lista de Tareas
		if (uiSuggestBox == wFlowSuggestBox && wFlowSuggestBox.getSelectedId() > 0) {
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

	public void save() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processUpdateResult(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoWFlowTimeTrack.getPmClass(), bmoWFlowTimeTrack, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
		}
	}

	public void processUpdateResult(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
		else {
			timeClockDialogBox.hide();
			if (bmoWFlowTimeTrack.getEndDate().toString().length() > 0) {
				timeClockImage.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/ustc_in.png"));
				timeClockImage.setTitle("Iniciar");
			} else {
				timeClockImage.setUrl(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/ustc_out.png"));
				timeClockImage.setTitle("Finalizar");
			}
		}
	}
}
