/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;

import com.flexwm.shared.BmoSendReport;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;


public class UiSendReport extends UiList {
	BmoSendReport bmoSendReport;

	public UiSendReport(UiParams uiParams) {
		super(uiParams, new BmoSendReport());
		bmoSendReport = (BmoSendReport)getBmObject();
	}

	@Override
	public void create() {
		UiSendReportForm uiSendReportForm = new UiSendReportForm(getUiParams(), 0);
		uiSendReportForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSendReportForm uiSendReportForm = new UiSendReportForm(getUiParams(), bmObject.getId());
		uiSendReportForm.show();
	}

	public class UiSendReportForm extends UiFormDialog {	
		TextBox nameTextBox = new TextBox();	
		TextBox linkTextBox = new TextBox();
		UiSuggestBox groupSuggestBox = new UiSuggestBox(new BmoProfile());

		BmoSendReport bmoSendReport;

		public UiSendReportForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSendReport(), id);
			bmoSendReport = (BmoSendReport)getBmObject();
		}

		@Override
		public void populateFields(){
			bmoSendReport = (BmoSendReport)getBmObject();

			formFlexTable.addField(1, 0, nameTextBox, bmoSendReport.getName());
			formFlexTable.addField(2, 0, groupSuggestBox, bmoSendReport.getProfileId());
			formFlexTable.addField(3, 0, linkTextBox, bmoSendReport.getLink());		
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSendReport.setId(id);		
			bmoSendReport.getName().setValue(nameTextBox.getText());
			bmoSendReport.getProfileId().setValue(groupSuggestBox.getSelectedId());
			bmoSendReport.getLink().setValue(linkTextBox.getText());

			return bmoSendReport;
		}

		@Override
		public void close() {
			UiSendReport uiSendReportList = new UiSendReport(getUiParams());
			uiSendReportList.show();
		}	
	}
}


