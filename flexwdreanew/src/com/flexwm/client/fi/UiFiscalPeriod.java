/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoFiscalPeriod;
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
import com.symgae.shared.sf.BmoCompany;


/**
 * @author smuniz
 *
 */

public class UiFiscalPeriod extends UiList {
	BmoFiscalPeriod bmoFiscalPeriod;

	public UiFiscalPeriod(UiParams uiParams) {
		super(uiParams, new BmoFiscalPeriod());
		bmoFiscalPeriod = (BmoFiscalPeriod)getBmObject();
	}

	@Override
	public void postShow() {
		addDateRangeFilterListBox(bmoFiscalPeriod.getStartDate(), "", "");
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoFiscalPeriod.getStatus()), bmoFiscalPeriod, bmoFiscalPeriod.getStatus());
	}

	@Override
	public void create() {
		UiFiscalPeriodForm uiFiscalPeriodForm = new UiFiscalPeriodForm(getUiParams(), 0);
		uiFiscalPeriodForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiFiscalPeriodForm uiFiscalPeriodForm = new UiFiscalPeriodForm(getUiParams(), bmObject.getId());
		uiFiscalPeriodForm.show();
	}

	public class UiFiscalPeriodForm extends UiFormDialog {	
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();			
		UiDateBox startDateDateBox = new UiDateBox();
		UiDateBox endDateDateBox = new UiDateBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox companyIdSuggestBox = new UiSuggestBox(new BmoCompany());

		BmoFiscalPeriod bmoFiscalPeriod;

		public UiFiscalPeriodForm(UiParams uiParams, int id) {
			super(uiParams, new BmoFiscalPeriod(), id);
		}

		@Override
		public void populateFields() {
			bmoFiscalPeriod = (BmoFiscalPeriod)getBmObject();

			if (newRecord) {
				if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
					try {
						bmoFiscalPeriod.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					} catch (BmException e) {
						showSystemMessage(this.getClass().getName() + "-populateFields(): " + e.toString());
					}
			}

			formFlexTable.addField(1, 0, nameTextBox, bmoFiscalPeriod.getName());		
			formFlexTable.addField(2, 0, descriptionTextArea, bmoFiscalPeriod.getDescription());
			formFlexTable.addField(3, 0, startDateDateBox, bmoFiscalPeriod.getStartDate());
			formFlexTable.addField(4, 0, endDateDateBox, bmoFiscalPeriod.getEndDate());
			formFlexTable.addField(5, 0, statusListBox, bmoFiscalPeriod.getStatus());
			formFlexTable.addField(6, 0, companyIdSuggestBox, bmoFiscalPeriod.getCompanyId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoFiscalPeriod.setId(id);
			bmoFiscalPeriod.getName().setValue(nameTextBox.getText());		
			bmoFiscalPeriod.getDescription().setValue(descriptionTextArea.getText());
			bmoFiscalPeriod.getStartDate().setValue(startDateDateBox.getTextBox().getText());
			bmoFiscalPeriod.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoFiscalPeriod.getStatus().setValue(statusListBox.getSelectedCode());
			bmoFiscalPeriod.getCompanyId().setValue(companyIdSuggestBox.getSelectedId());
			return bmoFiscalPeriod;
		}

		@Override
		public void close() {
			list();
		}
	} 
}
