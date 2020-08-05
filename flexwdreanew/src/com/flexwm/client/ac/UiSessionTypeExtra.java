/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.flexwm.client.ac.UiSessionType.UiSessionTypeForm.SessionTypeUpdater;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiSessionTypeExtra extends UiList {
	BmoSessionTypeExtra bmoSessionTypeExtra;
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox priceTextBox = new TextBox();
	CheckBox fixedPriceCheckBox = new CheckBox();
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiListBox sessionTypeListBox = new UiListBox(getUiParams(), new BmoSessionType());
	protected SessionTypeUpdater sessionTypeUpdater;
	BmoSessionType bmoSessionType;
	int sessionTypeId;

	public UiSessionTypeExtra(UiParams uiParams, Panel defaultPanel, BmoSessionType bmoSessionType, int id, SessionTypeUpdater sessionTypeUpdater) {
		super(uiParams, defaultPanel, new BmoSessionTypeExtra());
		sessionTypeId = id;
		bmoSessionTypeExtra = new BmoSessionTypeExtra();
		this.bmoSessionType = bmoSessionType;
		this.sessionTypeUpdater = sessionTypeUpdater;
	}

	public UiSessionTypeExtra(UiParams uiParams) {
		super(uiParams, new BmoSessionTypeExtra());
		bmoSessionTypeExtra = (BmoSessionTypeExtra)getBmObject();
	}

	@Override
	public void create() {
		UiSessionTypeExtraForm UiSessionTypeExtraForm = new UiSessionTypeExtraForm(getUiParams(), 0);
		UiSessionTypeExtraForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSessionTypeExtraForm UiSessionTypeExtraForm = new UiSessionTypeExtraForm(getUiParams(), bmObject.getId());
		UiSessionTypeExtraForm.show();
	}

	private class UiSessionTypeExtraForm extends UiFormDialog {

		public UiSessionTypeExtraForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionTypeExtra(), id);
		}

		@Override
		public void populateFields() {
			bmoSessionTypeExtra = (BmoSessionTypeExtra)getBmObject();
			if (!(sessionTypeId > 0))
				formFlexTable.addField(1, 0, sessionTypeListBox, bmoSessionTypeExtra.getSessionTypeId());
			formFlexTable.addField(2, 0, nameTextBox, bmoSessionTypeExtra.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoSessionTypeExtra.getDescription());
			formFlexTable.addField(4, 0, priceTextBox, bmoSessionTypeExtra.getPrice());
			formFlexTable.addField(5, 0, fixedPriceCheckBox, bmoSessionTypeExtra.getFixedPrice());
			formFlexTable.addField(6, 0, startDateBox, bmoSessionTypeExtra.getStartDate());
			formFlexTable.addField(7, 0, endDateBox, bmoSessionTypeExtra.getEndDate());	
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSessionTypeExtra = new BmoSessionTypeExtra();
			bmoSessionTypeExtra.setId(id);
			bmoSessionTypeExtra.getName().setValue(nameTextBox.getText());
			bmoSessionTypeExtra.getDescription().setValue(descriptionTextArea.getText());
			bmoSessionTypeExtra.getPrice().setValue(priceTextBox.getText());
			bmoSessionTypeExtra.getFixedPrice().setValue(fixedPriceCheckBox.getValue());
			bmoSessionTypeExtra.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoSessionTypeExtra.getEndDate().setValue(endDateBox.getTextBox().getText());
			if (sessionTypeId > 0)
				bmoSessionTypeExtra.getSessionTypeId().setValue(sessionTypeId);
			else
				bmoSessionTypeExtra.getSessionTypeId().setValue(sessionTypeListBox.getSelectedId());

			return bmoSessionTypeExtra;
		}

		@Override
		public void close() {
			list();
			if (sessionTypeUpdater != null)
				sessionTypeUpdater.update();
		}
	}
}
