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

import com.flexwm.client.op.UiCustomerService.UiCustomerServiceForm.CustomerServiceUpdater;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoCustomerServiceFollowup;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiCustomerServiceFollowup extends UiList {
	TextArea descriptionTextArea = new TextArea();
	UiDateTimeBox  followupUpdateDateBox = new UiDateTimeBox();
	int customerServiceId;
	BmoCustomerService bmoCustomerService;
	BmoCustomerServiceFollowup bmoCustomerServiceFollowup;
	protected CustomerServiceUpdater customerServiceUpdater;

	public UiCustomerServiceFollowup(UiParams uiParams, Panel defaultPanel, BmoCustomerService bmoCustomerService, int id, CustomerServiceUpdater customerServiceUpdater) {

		super(uiParams, defaultPanel, new BmoCustomerServiceFollowup());
		bmoCustomerServiceFollowup = (BmoCustomerServiceFollowup)getBmObject();
		customerServiceId = id;
		this.bmoCustomerService = bmoCustomerService;
		this.customerServiceUpdater = customerServiceUpdater;
	}

	@Override
	public void create() {
		UiCustomerServiceFollowupForm uiCustomerServiceFollowupForm = new UiCustomerServiceFollowupForm(getUiParams(), 0);
		uiCustomerServiceFollowupForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCustomerServiceFollowupForm uiCustomerServiceFollowupForm = new UiCustomerServiceFollowupForm(getUiParams(), bmObject.getId());
		uiCustomerServiceFollowupForm.show();
	}

	private class UiCustomerServiceFollowupForm extends UiFormDialog {

		public UiCustomerServiceFollowupForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomerServiceFollowup(), id);
		}

		@Override
		public void populateFields() {
			bmoCustomerServiceFollowup = (BmoCustomerServiceFollowup)getBmObject();
			formFlexTable.addField(1, 0, descriptionTextArea, bmoCustomerServiceFollowup.getDescription());
			formFlexTable.addField(2, 0, followupUpdateDateBox, bmoCustomerServiceFollowup.getFollowupDate());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomerServiceFollowup = new BmoCustomerServiceFollowup();
			bmoCustomerServiceFollowup.setId(id);
			bmoCustomerServiceFollowup.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
			bmoCustomerServiceFollowup.getDescription().setValue(descriptionTextArea.getText());
			bmoCustomerServiceFollowup.getFollowupDate().setValue(followupUpdateDateBox.getDateTime());

			if (customerServiceId > 0) bmoCustomerServiceFollowup.getCustomerServiceId().setValue(customerServiceId);
			return bmoCustomerServiceFollowup;
		}

		@Override
		public void close() {
			list();

			if (customerServiceUpdater != null)
				customerServiceUpdater.update();
		}
	}
}
