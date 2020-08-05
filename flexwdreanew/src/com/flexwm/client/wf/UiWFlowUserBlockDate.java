package com.flexwm.client.wf;

import com.flexwm.shared.wf.BmoWFlowUserBlockDate;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;


public class UiWFlowUserBlockDate extends UiList {

	BmoWFlowUserBlockDate bmoWFlowUserBlockDate;

	public UiWFlowUserBlockDate(UiParams uiParams) {
		super(uiParams, new BmoWFlowUserBlockDate());
		bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)getBmObject();
	}

	@Override
	public void create() {
		UiWFlowUserBlockDateForm uiWFlowUserBlockDateForm = new UiWFlowUserBlockDateForm(getUiParams(), 0);
		uiWFlowUserBlockDateForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowUserBlockDateForm uiWFlowUserBlockDateForm = new UiWFlowUserBlockDateForm(getUiParams(), bmObject.getId());
		uiWFlowUserBlockDateForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)bmObject;
		UiWFlowUserBlockDateForm uiWFlowUserBlockDateForm = new UiWFlowUserBlockDateForm(getUiParams(), bmoWFlowUserBlockDate.getId());
		uiWFlowUserBlockDateForm.show();
	}

	public class UiWFlowUserBlockDateForm extends UiFormDialog {
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox endDateTimeBox = new UiDateTimeBox();	
		TextArea commentsTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		BmoWFlowUserBlockDate bmoWFlowUserBlockDate;

		public UiWFlowUserBlockDateForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowUserBlockDate(), id);
		}

		@Override
		public void populateFields() {
			bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)getBmObject();
			formFlexTable.addField(1, 0, userSuggestBox, bmoWFlowUserBlockDate.getUserId());
			formFlexTable.addField(2, 0, startDateTimeBox, bmoWFlowUserBlockDate.getStartDate());
			formFlexTable.addField(3, 0, endDateTimeBox, bmoWFlowUserBlockDate.getEndDate());
			formFlexTable.addField(4, 0, commentsTextArea, bmoWFlowUserBlockDate.getComments());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowUserBlockDate.setId(id);
			bmoWFlowUserBlockDate.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoWFlowUserBlockDate.getStartDate().setValue(startDateTimeBox.getDateTime());
			bmoWFlowUserBlockDate.getEndDate().setValue(endDateTimeBox.getDateTime());
			bmoWFlowUserBlockDate.getComments().setValue(commentsTextArea.getText());
			return bmoWFlowUserBlockDate;
		}

		@Override
		public void close() {
			list();
		}
	}
}
