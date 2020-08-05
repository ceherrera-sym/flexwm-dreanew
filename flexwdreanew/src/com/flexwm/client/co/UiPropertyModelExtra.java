/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.flexwm.client.co.UiPropertyModel.UiPropertyModelForm.PropertyModelUpdater;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiPropertyModelExtra extends UiList {

	BmoPropertyModelExtra bmoPropertyModelExtra;
	BmoPropertyModel bmoPropertyModel;
	int propertyModelId;
	protected PropertyModelUpdater propertyModelUpdater;

	public UiPropertyModelExtra(UiParams uiParams, Panel defaultPanel, BmoPropertyModel bmoPropertyModel, int propertyModelId, PropertyModelUpdater propertyModelUpdater) {
		super(uiParams, defaultPanel, new BmoPropertyModelExtra());
		bmoPropertyModelExtra = (BmoPropertyModelExtra)getBmObject();
		this.propertyModelId = propertyModelId;
		this.bmoPropertyModel = bmoPropertyModel;
		this.propertyModelUpdater = propertyModelUpdater;
	}

	public UiPropertyModelExtra(UiParams uiParams) {
		super(uiParams, new BmoPropertyModelExtra());
		bmoPropertyModelExtra = (BmoPropertyModelExtra)getBmObject();
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			if (getSFParams().hasRead((new BmoDevelopment()).getProgramCode().toString() )) 
				addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopment()), bmoPropertyModelExtra.getBmoPropertyModel().getBmoDevelopment());
			if (getSFParams().hasRead((new BmoPropertyModel()).getProgramCode().toString() )) 
				addFilterListBox(new UiListBox(getUiParams(), new BmoPropertyModel()), bmoPropertyModelExtra.getBmoPropertyModel());
		}
	}

	@Override
	public void create() {
		UiPropertyModelExtraForm uiPropertyModelExtraForm = new UiPropertyModelExtraForm(getUiParams(), 0);
		uiPropertyModelExtraForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPropertyModelExtraForm uiPropertyModelExtraForm = new UiPropertyModelExtraForm(getUiParams(), bmObject.getId());
		uiPropertyModelExtraForm.show();
	}

	private class UiPropertyModelExtraForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox priceTextBox = new TextBox();
		CheckBox fixedPriceCheckBox = new CheckBox();
		UiDateBox startDateBox = new UiDateBox();
		UiDateBox endDateBox = new UiDateBox();
		UiSuggestBox propertyModelSuggestBox = new UiSuggestBox(new BmoPropertyModel());
		UiFileUploadBox fileUpload = new UiFileUploadBox(getUiParams());

		public UiPropertyModelExtraForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertyModelExtra(), id);
		}

		@Override
		public void populateFields() {
			bmoPropertyModelExtra = (BmoPropertyModelExtra)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoPropertyModelExtra.getCode());
			if (!(propertyModelId > 0))
				formFlexTable.addField(2, 0, propertyModelSuggestBox, bmoPropertyModelExtra.getPropertyModelId());
			formFlexTable.addField(3, 0, nameTextBox, bmoPropertyModelExtra.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoPropertyModelExtra.getDescription());
			formFlexTable.addField(5, 0, priceTextBox, bmoPropertyModelExtra.getPrice());
			formFlexTable.addField(6, 0, fixedPriceCheckBox, bmoPropertyModelExtra.getFixedPrice());
			formFlexTable.addField(7, 0, startDateBox, bmoPropertyModelExtra.getStartDate());
			formFlexTable.addField(8, 0, endDateBox, bmoPropertyModelExtra.getEndDate());
			formFlexTable.addField(9, 0, fileUpload, bmoPropertyModelExtra.getFile());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertyModelExtra = new BmoPropertyModelExtra();
			bmoPropertyModelExtra.setId(id);
			bmoPropertyModelExtra.getCode().setValue(codeTextBox.getText());
			bmoPropertyModelExtra.getName().setValue(nameTextBox.getText());
			bmoPropertyModelExtra.getDescription().setValue(descriptionTextArea.getText());
			bmoPropertyModelExtra.getPrice().setValue(priceTextBox.getText());
			bmoPropertyModelExtra.getFixedPrice().setValue(fixedPriceCheckBox.getValue());
			bmoPropertyModelExtra.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoPropertyModelExtra.getEndDate().setValue(endDateBox.getTextBox().getText());
			if (propertyModelId > 0) 
				bmoPropertyModelExtra.getPropertyModelId().setValue(propertyModelId);
			else 
				bmoPropertyModelExtra.getPropertyModelId().setValue(propertyModelSuggestBox.getSelectedId());

			return bmoPropertyModelExtra;
		}

		@Override
		public void close() {
			list();
			if (propertyModelUpdater != null)
				propertyModelUpdater.update();
		}
	}
}
