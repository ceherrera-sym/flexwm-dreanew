package com.flexwm.client.op;

import com.flexwm.shared.op.BmoOrderBlockDate;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;

public class UiOrderBlockDateForm extends UiForm {
	UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
	UiDateTimeBox endDateTimeBox = new UiDateTimeBox();	
	TextArea commentsTextArea = new TextArea();
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoOrderBlockDate bmoOrderBlockDate;
	
	public UiOrderBlockDateForm(UiParams uiParams, int id) {
		super(uiParams, new BmoOrderBlockDate(), id);
	}
	
	@Override
	public void populateFields(){
		bmoOrderBlockDate = (BmoOrderBlockDate)getBmObject();
		formFlexTable.addField(1, 0, startDateTimeBox, bmoOrderBlockDate.getStartDate());
		formFlexTable.addField(1, 2, endDateTimeBox, bmoOrderBlockDate.getEndDate());
		
		formFlexTable.addField(2, 0, commentsTextArea, bmoOrderBlockDate.getComments());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrderBlockDate.setId(id);
		bmoOrderBlockDate.getStartDate().setValue(startDateTimeBox.getDateTime());
		bmoOrderBlockDate.getEndDate().setValue(endDateTimeBox.getDateTime());
		bmoOrderBlockDate.getComments().setValue(commentsTextArea.getText());
		return bmoOrderBlockDate;
	}
	
	@Override
	public void close() {
		UiOrderBlockDateList uiOrderBlockDateList = new UiOrderBlockDateList(getUiParams());
		uiOrderBlockDateList.show();
	}
}
