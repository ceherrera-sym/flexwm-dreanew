package com.flexwm.client.op;

import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.op.BmoRequisitionType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiRequisitionType extends UiList {

	public UiRequisitionType(UiParams uiParams) {
		super(uiParams, new BmoRequisitionType());
	}

	@Override
	public void create() {
		UiRequisitionTypeForm uiRequisitionTypeForm = new UiRequisitionTypeForm(getUiParams(), 0);
		uiRequisitionTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRequisitionTypeForm uiRequisitionTypeForm = new UiRequisitionTypeForm(getUiParams(), bmObject.getId());
		uiRequisitionTypeForm.show();
	}

	public class UiRequisitionTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		CheckBox createReceiptCheckBox = new CheckBox();
		CheckBox stockCheckBox = new CheckBox();
		CheckBox enableSendCheckBox = new CheckBox();
		CheckBox createPaccountCheckBox = new CheckBox();
		CheckBox viewformatChechBox = new CheckBox();
		CheckBox outFormatCheckBox = new CheckBox();
		UiListBox devolutionBankmovTypeIdUiListBox = new UiListBox(getUiParams(), new BmoBankMovType());
		UiListBox paymentBankmovTypeIdListBox = new UiListBox(getUiParams(), new BmoBankMovType());
		BmoRequisitionType bmoRequisitionType;
		String generalSection = "Datos Generales";

		public UiRequisitionTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRequisitionType(), id);
		}

		@Override
		public void populateFields() {
			bmoRequisitionType = (BmoRequisitionType)getBmObject();
			formFlexTable.addSectionLabel(0, 0, generalSection, 2);
			formFlexTable.addField(1, 0, nameTextBox, bmoRequisitionType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoRequisitionType.getDescription());
			formFlexTable.addField(3, 0, typeListBox, bmoRequisitionType.getType());
			formFlexTable.addField(4, 0, createReceiptCheckBox, bmoRequisitionType.getCreateReceipt());
			formFlexTable.addField(5, 0, createPaccountCheckBox, bmoRequisitionType.getCreatePaccount());
			formFlexTable.addField(6, 0, stockCheckBox, bmoRequisitionType.getStock());
			formFlexTable.addField(7, 0, enableSendCheckBox, bmoRequisitionType.getEnableSend());
			formFlexTable.addField(8, 0, viewformatChechBox, bmoRequisitionType.getViewFormat());
			formFlexTable.addField(9, 0, devolutionBankmovTypeIdUiListBox, bmoRequisitionType.getDevolutionBankmovTypeId());
			formFlexTable.addField(10, 0, paymentBankmovTypeIdListBox, bmoRequisitionType.getPaymentBankmovTypeId());
			formFlexTable.addField(11, 0, outFormatCheckBox,bmoRequisitionType.getOutFormat());
			statusEffect();
		}

		@Override	
		public void formListChange(ChangeEvent event) {

			if (event.getSource() == typeListBox) {
				statusEffect();
			}
		}

		private void statusEffect() {
			if (typeListBox.getSelectedCode().toString().equals("" + BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
				formFlexTable.showField(bmoRequisitionType.getDevolutionBankmovTypeId());
				formFlexTable.showField(bmoRequisitionType.getPaymentBankmovTypeId());
			} else {
				formFlexTable.hideField(bmoRequisitionType.getDevolutionBankmovTypeId());
				formFlexTable.hideField(bmoRequisitionType.getPaymentBankmovTypeId());
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRequisitionType.setId(id);
			bmoRequisitionType.getName().setValue(nameTextBox.getText());
			bmoRequisitionType.getDescription().setValue(descriptionTextArea.getText());
			bmoRequisitionType.getType().setValue(typeListBox.getSelectedCode());
			bmoRequisitionType.getCreateReceipt().setValue(createReceiptCheckBox.getValue());
			bmoRequisitionType.getCreatePaccount().setValue(createPaccountCheckBox.getValue());
			bmoRequisitionType.getStock().setValue(stockCheckBox.getValue());
			bmoRequisitionType.getEnableSend().setValue(enableSendCheckBox.getValue());
			bmoRequisitionType.getViewFormat().setValue(viewformatChechBox.getValue());
			bmoRequisitionType.getDevolutionBankmovTypeId().setValue(devolutionBankmovTypeIdUiListBox.getSelectedId());
			bmoRequisitionType.getPaymentBankmovTypeId().setValue(paymentBankmovTypeIdListBox.getSelectedId());
			bmoRequisitionType.getOutFormat().setValue(outFormatCheckBox.getValue());
			return bmoRequisitionType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
