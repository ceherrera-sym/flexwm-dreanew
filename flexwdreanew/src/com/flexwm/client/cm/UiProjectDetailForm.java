/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectDetail;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiProjectDetailForm extends UiFormDialog {
	UiDateTimeBox deliveryDateBox = new UiDateTimeBox();
	UiDateTimeBox loadStartDateBox = new UiDateTimeBox();
	UiDateTimeBox prepDateBox = new UiDateTimeBox();
	UiDateTimeBox unloadStartDateBox = new UiDateTimeBox();
	UiDateTimeBox exitDateBox = new UiDateTimeBox();
	UiDateTimeBox returnDateBox = new UiDateTimeBox();

	Button copyDates = new Button("Copiar");
	String projectId;

	BmoProjectDetail bmoProjectDetail;
	BmoProject bmoProject;

	public UiProjectDetailForm(UiParams uiParams, int id, BmoProject bmoProject) {
		super(uiParams, new BmoProjectDetail(), id);
		bmoProjectDetail = (BmoProjectDetail)getBmObject();
		this.bmoProject = bmoProject;
		super.foreignId = bmoProject.getId();
		this.projectId = "" + foreignId;
		super.foreignField = bmoProjectDetail.getProjectId().getName();
	}

	//	public UiProjectDetailForm(UiParams uiParams, Panel defaultPanel, BmoProject bmoProject) {
	//		super(uiParams, defaultPanel, new BmoProjectDetail(), 0);
	//		bmoProjectDetail = (BmoProjectDetail)getBmObject();
	//		this.bmoProject = bmoProject;
	//		super.foreignId = bmoProject.getId();
	//		this.projectId = "" + foreignId;
	//		super.foreignField = bmoProjectDetail.getProjectId().getName();
	//		setUiType(UiParams.SINGLESLAVE);
	//	}

	@Override
	public void populateFields() {
		bmoProjectDetail = (BmoProjectDetail)getBmObject();

		copyDates.setStyleName("formCloseButton");
		copyDates.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				copyDates();
			}
		});

		formFlexTable.addField(1, 0, deliveryDateBox, bmoProjectDetail.getDeliveryDate());
		formFlexTable.addFieldEmpty(2, 0);
		formFlexTable.addButtonCell(2, 1, copyDates);

		formFlexTable.addField(3, 0, prepDateBox, bmoProjectDetail.getPrepDate());
		formFlexTable.addField(4, 0, exitDateBox, bmoProjectDetail.getExitDate());
		formFlexTable.addField(5, 0, loadStartDateBox, bmoProjectDetail.getLoadStartDate());
		formFlexTable.addField(6, 0, unloadStartDateBox, bmoProjectDetail.getUnloadStartDate());
		formFlexTable.addField(8, 0, returnDateBox, bmoProjectDetail.getReturnDate());
	}

	@Override
	public void postShow() {
		if (bmoProject.getStatus().toChar() != BmoProject.STATUS_REVISION) {
			copyDates.setEnabled(false);
			deliveryDateBox.setEnabled(false);
			prepDateBox.setEnabled(false);
			exitDateBox.setEnabled(false);
			loadStartDateBox.setEnabled(false);
			unloadStartDateBox.setEnabled(false);
			returnDateBox.setEnabled(false);
		}
		deleteButton.setVisible(false);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoProjectDetail.setId(id);
		bmoProjectDetail.getProjectId().setValue(projectId);
		bmoProjectDetail.getDeliveryDate().setValue(deliveryDateBox.getDateTime());
		bmoProjectDetail.getLoadStartDate().setValue(loadStartDateBox.getDateTime());
		bmoProjectDetail.getPrepDate().setValue(prepDateBox.getDateTime());
		bmoProjectDetail.getUnloadStartDate().setValue(unloadStartDateBox.getDateTime());
		bmoProjectDetail.getExitDate().setValue(exitDateBox.getDateTime());
		bmoProjectDetail.getReturnDate().setValue(returnDateBox.getDateTime());
		return bmoProjectDetail;
	}

	public void copyDates() {
		loadStartDateBox.setDateTime(deliveryDateBox.getDateTime());
		prepDateBox.setDateTime(deliveryDateBox.getDateTime());
		unloadStartDateBox.setDateTime(deliveryDateBox.getDateTime());
		exitDateBox.setDateTime(deliveryDateBox.getDateTime());
		returnDateBox.setDateTime(deliveryDateBox.getDateTime());
	}

	@Override
	public void saveNext() {
		UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProjectDetail.getProjectId().toInteger());
		uiProjectDetail.show();
	}

	@Override
	public void close() {
		dialogClose();
	}
}
