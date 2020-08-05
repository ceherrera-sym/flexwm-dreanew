package com.flexwm.client.op;

import com.flexwm.shared.op.BmoOrderCommission;
import com.flexwm.shared.op.BmoOrderCommissionAmount;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoReqPayType;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiOrderCommission extends UiList {

	public UiOrderCommission(UiParams uiParams) {
		super(uiParams, new BmoOrderCommission());
	}

	@Override
	public void create() {
		UiOrderCommissionForm uiOrderCommissionForm = new UiOrderCommissionForm(getUiParams(), 0);
		uiOrderCommissionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiOrderCommissionForm uiOrderCommissionForm = new UiOrderCommissionForm(getUiParams(), bmObject.getId());
		uiOrderCommissionForm.show();
	}

	public class UiOrderCommissionForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoOrderCommission bmoOrderCommission;
		UiListBox typeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox reqPayTypeListBox = new UiListBox(getUiParams(), new BmoReqPayType());
		OrderCommissionUpdater orderCommissionUpdater = new OrderCommissionUpdater();

		String itemsSection = "Items";
		public UiOrderCommissionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOrderCommission(), id);
		}

		@Override
		public void populateFields(){
			bmoOrderCommission = (BmoOrderCommission)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoOrderCommission.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoOrderCommission.getDescription());
			formFlexTable.addField(3, 0, typeListBox, bmoOrderCommission.getOrderTypeId());
			formFlexTable.addField(4, 0, reqPayTypeListBox, bmoOrderCommission.getReqPayTypeId());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(5, 0, itemsSection, 2);
				BmoOrderCommissionAmount bmoOrderCommissionAmount = new BmoOrderCommissionAmount();
				FlowPanel orderCommissionAmountFP = new FlowPanel();
				BmFilter filterBudgetItems = new BmFilter();
				filterBudgetItems.setValueFilter(bmoOrderCommissionAmount.getKind(), bmoOrderCommissionAmount.getOrderCommissionId(), bmoOrderCommission.getId());
				getUiParams().setForceFilter(bmoOrderCommissionAmount.getProgramCode(), filterBudgetItems);
				UiOrderCommissionAmount uiOrderCommissionAmount = new UiOrderCommissionAmount(getUiParams(), orderCommissionAmountFP, bmoOrderCommission, bmoOrderCommission.getId(), orderCommissionUpdater);
				setUiType(bmoOrderCommissionAmount.getProgramCode(), UiParams.MINIMALIST);
				uiOrderCommissionAmount.show();
				formFlexTable.addPanel(6, 0, orderCommissionAmountFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderCommission.setId(id);
			bmoOrderCommission.getName().setValue(nameTextBox.getText());
			bmoOrderCommission.getDescription().setValue(descriptionTextArea.getText());
			bmoOrderCommission.getOrderTypeId().setValue(typeListBox.getSelectedId());
			bmoOrderCommission.getReqPayTypeId().setValue(reqPayTypeListBox.getSelectedId());
			return bmoOrderCommission;
		}

		@Override
		public void close() {
			list();
		}

		public class OrderCommissionUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}

}
