package com.flexwm.client.op;

import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiWhSection extends UiList {
	BmoWhSection bmoWhSection;

	public UiWhSection(UiParams uiParams) {
		super(uiParams, new BmoWhSection());
		bmoWhSection = (BmoWhSection)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoWarehouse()), bmoWhSection.getBmoWarehouse());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWhSection.getBmoWarehouse().getType()), bmoWhSection.getBmoWarehouse(), bmoWhSection.getBmoWarehouse().getType());
	}

	@Override
	public void create() {
		UiWhSectionForm uiWhSectionForm = new UiWhSectionForm(getUiParams(), 0);
		uiWhSectionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWhSection = (BmoWhSection)bmObject;
		UiWhSectionForm uiWhSectionForm = new UiWhSectionForm(getUiParams(), bmoWhSection.getId());
		uiWhSectionForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWhSectionForm uiWhSectionForm = new UiWhSectionForm(getUiParams(), bmObject.getId());
		uiWhSectionForm.show();
	}

	public class UiWhSectionForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox warehouseListBox = new UiListBox(getUiParams(), new BmoWarehouse());
		BmoWhSection bmoWhSection;

		public UiWhSectionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhSection(), id);
		}

		@Override
		public void populateFields(){
			bmoWhSection = (BmoWhSection)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoWhSection.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoWhSection.getDescription());
			formFlexTable.addField(3, 0, warehouseListBox, bmoWhSection.getWarehouseId());	
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWhSection.setId(id);
			bmoWhSection.getName().setValue(nameTextBox.getText());
			bmoWhSection.getDescription().setValue(descriptionTextArea.getText());
			bmoWhSection.getWarehouseId().setValue(warehouseListBox.getSelectedId());
			return bmoWhSection;
		}

		@Override
		public void close() {
			list();
		}
	}
}
