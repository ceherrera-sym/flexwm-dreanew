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

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCity;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentType;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiDevelopment extends UiList {
	BmoDevelopment bmoDevelopment;

	public UiDevelopment(UiParams uiParams) {
		super(uiParams, new BmoDevelopment());
		bmoDevelopment = (BmoDevelopment)getBmObject();
	}

	@Override
	public void postShow() {
		//		addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopmentType()), new BmoDevelopmentType(), bmoDevelopment.getDevelopmentTypeId());
	}

	@Override
	public void create() {
		UiDevelopmentForm uiDevelopmentForm = new UiDevelopmentForm(getUiParams(), 0);
		uiDevelopmentForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopment = (BmoDevelopment)bmObject;
		UiDevelopmentForm uiDevelopmentForm = new UiDevelopmentForm(getUiParams(), bmoDevelopment.getId());
		uiDevelopmentForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiDevelopmentForm uiDevelopmentForm = new UiDevelopmentForm(getUiParams(), bmObject.getId());
		uiDevelopmentForm.show();
	}

	public class UiDevelopmentForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox zipTextBox = new TextBox();
		TextArea addressTextArea = new TextArea();
		TextBox landSizeTextBox = new TextBox();
		TextBox saticTextBox = new TextBox();
		CheckBox isActivateCheckBox = new CheckBox();
		UiListBox developmentTypeListBox = new UiListBox(getUiParams(), new BmoDevelopmentType());
		UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());
		UiFileUploadBox logoFileUpload = new UiFileUploadBox(getUiParams());
		UiFileUploadBox blueprintFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiSuggestBox municipalAgencySuggestBox = new UiSuggestBox(new BmoSupplier());
		UiSuggestBox municipalAgencyWaterSuggestBox = new UiSuggestBox(new BmoSupplier());

		BmoDevelopment bmoDevelopment;

		public UiDevelopmentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDevelopment(), id); 
		}

		@Override
		public void populateFields() {
			bmoDevelopment = (BmoDevelopment)getBmObject();

			formFlexTable.addField(1, 0, codeTextBox, bmoDevelopment.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoDevelopment.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoDevelopment.getDescription());
			formFlexTable.addField(4, 0, zipTextBox, bmoDevelopment.getZip());
			formFlexTable.addField(5, 0, addressTextArea, bmoDevelopment.getAddress());
			formFlexTable.addField(6, 0, landSizeTextBox, bmoDevelopment.getLandSize());
			formFlexTable.addField(7, 0, isActivateCheckBox, bmoDevelopment.getIsActivate());		
			formFlexTable.addField(8, 0, developmentTypeListBox, bmoDevelopment.getDevelopmentTypeId());
			formFlexTable.addField(9, 0, citySuggestBox, bmoDevelopment.getCityId());
			formFlexTable.addField(10, 0, saticTextBox, bmoDevelopment.getSatic());
			if (!newRecord) {
				formFlexTable.addField(11, 0, logoFileUpload, bmoDevelopment.getLogo());	
				formFlexTable.addField(12, 0, blueprintFileUpload, bmoDevelopment.getBlueprint());	
			}
			formFlexTable.addField(13, 0, companyListBox, bmoDevelopment.getCompanyId());
			formFlexTable.addField(14, 0, municipalAgencySuggestBox, bmoDevelopment.getMunicipalAgency());
			formFlexTable.addField(15, 0, municipalAgencyWaterSuggestBox, bmoDevelopment.getMunicipalAgencyWater());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDevelopment.setId(id);
			bmoDevelopment.getCode().setValue(codeTextBox.getText());
			bmoDevelopment.getName().setValue(nameTextBox.getText());
			bmoDevelopment.getDescription().setValue(descriptionTextArea.getText());
			bmoDevelopment.getZip().setValue(zipTextBox.getText());
			bmoDevelopment.getAddress().setValue(addressTextArea.getText());
			bmoDevelopment.getLandSize().setValue(landSizeTextBox.getText());
			bmoDevelopment.getIsActivate().setValue(isActivateCheckBox.getValue());
			bmoDevelopment.getDevelopmentTypeId().setValue(developmentTypeListBox.getSelectedId());
			bmoDevelopment.getCityId().setValue(citySuggestBox.getSelectedId());
			bmoDevelopment.getSatic().setValue(saticTextBox.getText());
			bmoDevelopment.getLogo().setValue(logoFileUpload.getBlobKey());
			bmoDevelopment.getBlueprint().setValue(blueprintFileUpload.getBlobKey());
			bmoDevelopment.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoDevelopment.getMunicipalAgency().setValue(municipalAgencySuggestBox.getSelectedId());
			bmoDevelopment.getMunicipalAgencyWater().setValue(municipalAgencyWaterSuggestBox.getSelectedId());

			return bmoDevelopment;
		}

		@Override
		public void close() {
			list();
		}
	}
}
