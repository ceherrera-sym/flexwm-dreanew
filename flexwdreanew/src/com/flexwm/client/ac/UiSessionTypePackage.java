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
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;


public class UiSessionTypePackage extends UiList {
	BmoSessionTypePackage bmoSessionTypePackage;
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox sessionsTextBox = new TextBox();
	TextBox salePriceTextBox = new TextBox();
	CheckBox fixedPriceCheckBox = new CheckBox();
	CheckBox enabledCheckBox = new CheckBox();
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiSuggestBox profileSuggestBox = new UiSuggestBox(new BmoProfile());
	protected SessionTypeUpdater sessionTypeUpdater;
	BmoSessionType bmoSessionType;

	int sessionTypeId;

	public UiSessionTypePackage(UiParams uiParams, Panel defaultPanel, BmoSessionType bmoSessionType, int id, SessionTypeUpdater sessionTypeUpdater) {
		super(uiParams, defaultPanel, new BmoSessionTypePackage());
		sessionTypeId = id;
		bmoSessionTypePackage = new BmoSessionTypePackage();
		this.bmoSessionType = bmoSessionType;
		this.sessionTypeUpdater = sessionTypeUpdater;
	}

	@Override
	public void create() {
		UiSessionTypePackageForm uiSessionTypePackageForm = new UiSessionTypePackageForm(getUiParams(), 0);
		uiSessionTypePackageForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSessionTypePackageForm uiSessionTypePackageForm = new UiSessionTypePackageForm(getUiParams(), bmObject.getId());
		uiSessionTypePackageForm.show();
	}

	private class UiSessionTypePackageForm extends UiFormDialog {

		public UiSessionTypePackageForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionTypePackage(), id);
		}

		@Override
		public void populateFields() {
			bmoSessionTypePackage = (BmoSessionTypePackage)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoSessionTypePackage.getName());
			formFlexTable.addField(2, 0, typeListBox, bmoSessionTypePackage.getType());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoSessionTypePackage.getDescription());
			formFlexTable.addField(4, 0, sessionsTextBox, bmoSessionTypePackage.getSessions());
			formFlexTable.addField(5, 0, salePriceTextBox, bmoSessionTypePackage.getSalePrice());
			formFlexTable.addField(6, 0, startDateBox, bmoSessionTypePackage.getStartDate());
			formFlexTable.addField(7, 0, endDateBox, bmoSessionTypePackage.getEndDate());
			formFlexTable.addField(8, 0, enabledCheckBox, bmoSessionTypePackage.getEnabled());
			formFlexTable.addField(9, 0, profileSuggestBox, bmoSessionTypePackage.getProfileId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSessionTypePackage = new BmoSessionTypePackage();
			bmoSessionTypePackage.setId(id);
			bmoSessionTypePackage.getName().setValue(nameTextBox.getText());
			bmoSessionTypePackage.getDescription().setValue(descriptionTextArea.getText());
			bmoSessionTypePackage.getType().setValue(typeListBox.getSelectedCode());
			bmoSessionTypePackage.getSessions().setValue(sessionsTextBox.getText());
			bmoSessionTypePackage.getSalePrice().setValue(salePriceTextBox.getText());
			bmoSessionTypePackage.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoSessionTypePackage.getEndDate().setValue(endDateBox.getTextBox().getText());
			bmoSessionTypePackage.getSessionTypeId().setValue(sessionTypeId);
			bmoSessionTypePackage.getEnabled().setValue(enabledCheckBox.getValue());
			bmoSessionTypePackage.getProfileId().setValue(profileSuggestBox.getSelectedId());

			return bmoSessionTypePackage;
		}

		@Override
		public void close() {
			list();

			if (sessionTypeUpdater != null)
				sessionTypeUpdater.update();
		}
	}
}
