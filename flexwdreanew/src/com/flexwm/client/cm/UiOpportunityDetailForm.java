/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.ev.BmoVenue;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.GwtUtil;


public class UiOpportunityDetailForm extends UiFormDialog {

	TextArea descriptionTextArea = new TextArea();
	TextBox guestsTextBox = new TextBox();

	TextBox depositTextBox = new TextBox();
	TextBox extraHourTextBox = new TextBox();
	UiDateBox paymentDateBox = new UiDateBox();
	UiListBox customerAddressListBox;
	UiListBox customerEmailListBox;
	UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
	TextBox downPaymentTextBox = new TextBox();

	CheckBox showInContractCheckBox = new CheckBox();
	BmoOpportunity bmoOpportunity;
	BmoOpportunityDetail bmoOpportunityDetail;

	public UiOpportunityDetailForm(UiParams uiParams, int id, BmoOpportunity bmoOpportunity) {
		super(uiParams, new BmoOpportunityDetail(), id);
		bmoOpportunityDetail = (BmoOpportunityDetail)getBmObject();
		super.foreignId = bmoOpportunity.getId();
		this.bmoOpportunity = bmoOpportunity;
		super.foreignField = bmoOpportunityDetail.getOpportunityId().getName();
		setUiType(UiParams.SINGLESLAVE);
	}

	@Override
	public void populateFields() {
		bmoOpportunityDetail = (BmoOpportunityDetail)getBmObject();

		if (bmoOpportunityDetail.getExtraHour().toString().equals("")) {
			try {
				bmoOpportunityDetail.getExtraHour().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getExtraHour().toString());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields() ERROR:  Error al asignar valor default a Hora Extra: " + e.toString());
			}
		}

		//Asigna fecha límite de pago automaticamente 15 días antes de la fecha del evento 
		if (bmoOpportunityDetail.getPaymentDate().toString().equals("")) {
			Date dueDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).parse(bmoOpportunity.getStartDate().toString().substring(0, 10));
			CalendarUtil.addDaysToDate(dueDate, -15);
			paymentDateBox.getDatePicker().setValue(dueDate);
			paymentDateBox.getTextBox().setValue(GwtUtil.dateToString(dueDate, getSFParams().getDateFormat()));
		}

		// Preparar lista de direcciones de clientes
		BmFilter customerAddressFilter = new BmFilter();
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		customerAddressFilter.setValueFilter(
				bmoCustomerAddress.getKind(),
				bmoCustomerAddress.getCustomerId().getName(),
				bmoOpportunity.getCustomerId().toInteger()
				);
		customerAddressListBox = new UiListBox(getUiParams(), new BmoCustomerAddress(), customerAddressFilter);

		// Preparar lista de emails de clientes
		BmFilter customerEmailFilter = new BmFilter();
		BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
		customerEmailFilter.setValueFilter(
				bmoCustomerEmail.getKind(),
				bmoCustomerEmail.getCustomerId().getName(),
				bmoOpportunity.getCustomerId().toInteger()
				);
		customerEmailListBox = new UiListBox(getUiParams(), new BmoCustomerEmail(), customerEmailFilter);

		formFlexTable.addField(1, 0, guestsTextBox, bmoOpportunityDetail.getGuests());
		formFlexTable.addField(2, 0, depositTextBox, bmoOpportunityDetail.getDeposit());
		formFlexTable.addField(3, 0, extraHourTextBox, bmoOpportunityDetail.getExtraHour());
		formFlexTable.addField(4, 0, downPaymentTextBox, bmoOpportunityDetail.getDownPayment());
		formFlexTable.addField(5, 0, descriptionTextArea, bmoOpportunityDetail.getDescription());
		formFlexTable.addField(6, 0, showInContractCheckBox, bmoOpportunityDetail.getShowInContract());
		formFlexTable.addField(7, 0, paymentDateBox, bmoOpportunityDetail.getPaymentDate());
		formFlexTable.addField(8, 0, customerEmailListBox, bmoOpportunityDetail.getCustomerEmailId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoOpportunityDetail.setId(id);
		bmoOpportunityDetail.getOpportunityId().setValue(bmoOpportunity.getId());
		bmoOpportunityDetail.getPaymentDate().setValue(paymentDateBox.getTextBox().getText());
		bmoOpportunityDetail.getDescription().setValue(descriptionTextArea.getText());
		bmoOpportunityDetail.getGuests().setValue(guestsTextBox.getText());
		bmoOpportunityDetail.getDeposit().setValue(depositTextBox.getText());
		bmoOpportunityDetail.getExtraHour().setValue(extraHourTextBox.getText());
		bmoOpportunityDetail.getCustomerEmailId().setValue(customerEmailListBox.getSelectedId());
		bmoOpportunityDetail.getShowInContract().setValue(showInContractCheckBox.getValue());
		bmoOpportunityDetail.getDownPayment().setValue(downPaymentTextBox.getText());

		return bmoOpportunityDetail;
	}
	
	@Override
	public void postShow() {
		deleteButton.setVisible(false);
	}

	@Override
	public void close() {
		dialogClose();
	}
}
