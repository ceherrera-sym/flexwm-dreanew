package com.flexwm.client.wf;

import java.util.ArrayList;

import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;

public class UiWFlowFormat extends UiList{
//	private UiWFlowFormatForm uIuFlowFormatForm;
	BmoWFlowFormat bmoWFlowFormat;
	BmoWFlowType bmoWFlowType;
	public UiWFlowFormat(UiParams uiParams, BmObject bmObject) {
		super(uiParams, bmObject);
		bmoWFlowType = (BmoWFlowType)bmObject;
	}
	public UiWFlowFormat(UiParams uiParams) {
		super(uiParams, new BmoWFlowFormat());
		
	}
	public UiWFlowFormat(UiParams uiParams,BmoWFlowType bmoWFlowType) {
		super(uiParams, new BmoWFlowFormat());
		this.bmoWFlowType = bmoWFlowType;
	}
	
	@Override
	public void create() {
		UiWFlowFormatForm uiWFlowFormatForm = new UiWFlowFormatForm(getUiParams(), 0);
		uiWFlowFormatForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiWFlowFormatForm uiWFlowFormatForm = new UiWFlowFormatForm(getUiParams(), bmObject.getId());
		uiWFlowFormatForm.show();
	}
	
	public class UiWFlowFormatForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox linkTextBox = new TextBox();
		CheckBox hasConsecutiveCheckBox = new CheckBox();
		CheckBox multiplePrintCheckBox = new CheckBox();
		TextBox sequenceTextBox = new TextBox();
		TextBox publishValidateClassTextBox = new TextBox();
		UiListBox companyListBox ;
		UiListBox wflowTypeListBox = new UiListBox(getUiParams(),new BmoWFlowType());		
		String wflowTypeId;
		
		public UiWFlowFormatForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowFormat(), id);
			
		
		}
		
		@Override
		public void populateFields(){
			bmoWFlowFormat = (BmoWFlowFormat)getBmObject();
			int row = 0;
			
			// Obten las variables del filtro forzado
			BmFilter forceFilter = getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).getForceFilter();
			formFlexTable.addLabelField(0, 0, forceFilter.getFieldLabel(), forceFilter.getValueLabel());
			wflowTypeId = forceFilter.getValue();
			
			BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setInFilter(bmoWFlowTypeCompany.getKind(), 
					bmoCompany.getIdFieldName(),
					bmoWFlowTypeCompany.getCompanyId().getName(),
					bmoWFlowTypeCompany.getWflowTypeId().getName(),
					wflowTypeId);
			companyListBox = new UiListBox(getUiParams(), new BmoCompany(),bmFilter);
			if(newRecord)
				formFlexTable.addField(row++, 0, companyListBox,bmoWFlowFormat.getCompanyId());
			formFlexTable.addField(row++, 0, codeTextBox, bmoWFlowFormat.getCode());
			formFlexTable.addField(row++, 0, nameTextBox, bmoWFlowFormat.getName());
			if(!newRecord)
				formFlexTable.addField(row++, 0, new UiWFlowFormatCompanyLabelList(getUiParams(),bmoWFlowFormat));
			formFlexTable.addField(row++, 0, descriptionTextArea, bmoWFlowFormat.getDescription());
			formFlexTable.addField(row++, 0, linkTextBox, bmoWFlowFormat.getLink());
			formFlexTable.addField(row++, 0, sequenceTextBox, bmoWFlowFormat.getSequence());
			formFlexTable.addField(row++, 0, publishValidateClassTextBox, bmoWFlowFormat.getPublishValidateClass());
			formFlexTable.addField(row++, 0, hasConsecutiveCheckBox, bmoWFlowFormat.getHasconsecutive());
			formFlexTable.addField(row++, 0, multiplePrintCheckBox, bmoWFlowFormat.getMultipleprint());

			getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).setFilterList(new ArrayList<BmFilter>());
		}
		
	
		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowFormat.setId(id);
			bmoWFlowFormat.getWflowTypeId().setValue(wflowTypeId);
			bmoWFlowFormat.getCode().setValue(codeTextBox.getText());
			bmoWFlowFormat.getName().setValue(nameTextBox.getText());
			bmoWFlowFormat.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowFormat.getHasconsecutive().setValue(hasConsecutiveCheckBox.getValue());
			bmoWFlowFormat.getMultipleprint().setValue(multiplePrintCheckBox.getValue());
			bmoWFlowFormat.getSequence().setValue(sequenceTextBox.getValue());
			bmoWFlowFormat.getLink().setValue(linkTextBox.getValue());
			bmoWFlowFormat.getPublishValidateClass().setValue(publishValidateClassTextBox.getText());
			bmoWFlowFormat.getCompanyId().setValue(companyListBox.getSelectedId());
			
			return bmoWFlowFormat;
		}
		
		@Override
		public void saveNext() {
			if(newRecord)
				open(bmoWFlowFormat);
			else 
				showList();
		}
				
		@Override
		public void close() {
			list();
		}
		
	}

}
