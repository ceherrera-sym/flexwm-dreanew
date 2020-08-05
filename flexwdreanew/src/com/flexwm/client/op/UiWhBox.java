package com.flexwm.client.op;

import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhBoxTrack;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiWhBox extends UiList {
	BmoWhBox bmoWhBox;

	public UiWhBox(UiParams uiParams) {
		super(uiParams, new BmoWhBox());
	}

	@Override
	public void create() {
		UiWhBoxForm uiWhBoxForm = new UiWhBoxForm(getUiParams(), 0);
		uiWhBoxForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWhBox = (BmoWhBox)bmObject;
		UiWhBoxForm uiWhBoxForm = new UiWhBoxForm(getUiParams(), bmoWhBox.getId());
		uiWhBoxForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWhBoxForm uiWhBoxForm = new UiWhBoxForm(getUiParams(), bmObject.getId());
		uiWhBoxForm.show();
	}

	public class UiWhBoxForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoWhBox bmoWhBox;
		String itemSection = "Rastreo Ligado";
		WhBoxUpdater whBoxUpdater = new WhBoxUpdater();

		public UiWhBoxForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhBox(), id);
		}

		@Override
		public void populateFields() {
			bmoWhBox = (BmoWhBox)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWhBox.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWhBox.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWhBox.getDescription());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(4, 0, itemSection, 2);
				BmoWhBoxTrack bmoWhBoxTrack = new BmoWhBoxTrack();
				FlowPanel budgetItemFP = new FlowPanel();
				BmFilter filterBudgetItems = new BmFilter();
				filterBudgetItems.setValueFilter(bmoWhBoxTrack.getKind(), bmoWhBoxTrack.getWhBoxId(), bmoWhBox.getId());
				getUiParams().setForceFilter(bmoWhBoxTrack.getProgramCode(), filterBudgetItems);
				UiWhBoxTrack uiWhBoxTrack = new UiWhBoxTrack(getUiParams(), budgetItemFP, bmoWhBox, bmoWhBox.getId(), whBoxUpdater);
				setUiType(bmoWhBoxTrack.getProgramCode(), UiParams.MINIMALIST);
				uiWhBoxTrack.show();
				formFlexTable.addPanel(5, 0, budgetItemFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWhBox.setId(id);
			bmoWhBox.getCode().setValue(codeTextBox.getText());
			bmoWhBox.getName().setValue(nameTextBox.getText());
			bmoWhBox.getDescription().setValue(descriptionTextArea.getText());
			return bmoWhBox;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiWhBoxForm uiWhBoxForm = new UiWhBoxForm(getUiParams(), getBmObject().getId());
				uiWhBoxForm.show();
			} else {
				UiWhBox uiWhBoxList = new UiWhBox(getUiParams());
				uiWhBoxList.show();
			}		
		}

		public class WhBoxUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
