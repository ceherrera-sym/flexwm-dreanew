/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.client.op.UiOrderCommission.UiOrderCommissionForm.OrderCommissionUpdater;
import com.flexwm.shared.op.BmoOrderCommission;
import com.flexwm.shared.op.BmoOrderCommissionAmount;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;


public class UiOrderCommissionAmount extends UiList {
	TextBox nameTextBox = new TextBox();
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiListBox  profileListBox = new UiListBox(getUiParams(), new BmoProfile());
	TextBox valueTextBox = new TextBox();
	UiListBox triggerListBox = new UiListBox(getUiParams());
	CheckBox userAssignRequiredCheckBox = new CheckBox();
	CheckBox isPartialCheckBox = new CheckBox();
	UiListBox parentListBox;
	int orderCommissionId;
	BmoOrderCommissionAmount bmoOrderCommissionAmount;
	BmoOrderCommission bmoOrderCommission;
	protected OrderCommissionUpdater orderCommissionUpdater;

	public UiOrderCommissionAmount(UiParams uiParams, Panel defaultPanel, BmoOrderCommission bmoOrderCommission, int id, OrderCommissionUpdater orderCommissionUpdater) {

		super(uiParams, defaultPanel, new BmoOrderCommissionAmount());
		bmoOrderCommissionAmount = (BmoOrderCommissionAmount)getBmObject();
		this.orderCommissionId = id;
		this.bmoOrderCommissionAmount = new BmoOrderCommissionAmount();
		this.bmoOrderCommission = bmoOrderCommission;
		this.orderCommissionUpdater = orderCommissionUpdater;

		BmFilter filterParent = new BmFilter();
		filterParent.setValueFilter(bmoOrderCommissionAmount.getKind(), bmoOrderCommissionAmount.getOrderCommissionId(), orderCommissionId);
		parentListBox = new UiListBox(getUiParams(), new BmoOrderCommissionAmount(), filterParent);
	}

	@Override
	public void create() {
		UiOrderCommissionAmountForm uiOrderCommissionAmountForm = new UiOrderCommissionAmountForm(getUiParams(), 0);
		uiOrderCommissionAmountForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiOrderCommissionAmountForm uiOrderCommissionAmountForm = new UiOrderCommissionAmountForm(getUiParams(), bmObject.getId());
		uiOrderCommissionAmountForm.show();
	}

	private class UiOrderCommissionAmountForm extends UiFormDialog {

		public UiOrderCommissionAmountForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOrderCommissionAmount(), id);
		}

		@Override
		public void populateFields() {
			bmoOrderCommissionAmount = (BmoOrderCommissionAmount)getBmObject();

			formFlexTable.addField(1, 0, nameTextBox, bmoOrderCommissionAmount.getName());
			formFlexTable.addField(2, 0, profileListBox, bmoOrderCommissionAmount.getProfileId());
			formFlexTable.addField(3, 0, typeListBox, bmoOrderCommissionAmount.getType());
			formFlexTable.addField(4, 0, valueTextBox, bmoOrderCommissionAmount.getValue());
			formFlexTable.addField(5, 0, userAssignRequiredCheckBox, bmoOrderCommissionAmount.getUserAssignRequired());
			formFlexTable.addField(6, 0, triggerListBox, bmoOrderCommissionAmount.getTrigger());
			formFlexTable.addField(7, 0, isPartialCheckBox, bmoOrderCommissionAmount.getIsPartial());
			formFlexTable.addField(8, 0, parentListBox, bmoOrderCommissionAmount.getParentId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderCommissionAmount = new BmoOrderCommissionAmount();
			bmoOrderCommissionAmount.setId(id);
			bmoOrderCommissionAmount.getName().setValue(nameTextBox.getText());
			bmoOrderCommissionAmount.getValue().setValue(valueTextBox.getText());
			bmoOrderCommissionAmount.getProfileId().setValue(profileListBox.getSelectedId());
			bmoOrderCommissionAmount.getType().setValue(typeListBox.getSelectedCode());
			bmoOrderCommissionAmount.getUserAssignRequired().setValue(userAssignRequiredCheckBox.getValue());
			bmoOrderCommissionAmount.getIsPartial().setValue(isPartialCheckBox.getValue());
			bmoOrderCommissionAmount.getOrderCommissionId().setValue(orderCommissionId);
			bmoOrderCommissionAmount.getTrigger().setValue(triggerListBox.getSelectedCode());
			bmoOrderCommissionAmount.getParentId().setValue(parentListBox.getSelectedId());

			return bmoOrderCommissionAmount;
		}

		@Override
		public void close() {
			list();

			if (orderCommissionUpdater != null)
				orderCommissionUpdater.update();
		}
	}
}
