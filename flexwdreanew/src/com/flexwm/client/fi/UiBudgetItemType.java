/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoBudgetItemType;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiBudgetItemType extends UiList {

	BmoBudgetItemType bmoTypeCostCenter;

	public UiBudgetItemType(UiParams uiParams) {
		super(uiParams, new BmoBudgetItemType());
		bmoTypeCostCenter = (BmoBudgetItemType)getBmObject();
	}

	@Override
	public void create() {
		UiBudgetItemTypeForm uiTypeCostCenterForm = new UiBudgetItemTypeForm(getUiParams(), 0);
		uiTypeCostCenterForm.show();
	}

	@Override
	public void postShow(){
		addFilterSuggestBox(new UiSuggestBox(new BmoBudgetItemType()), new BmoBudgetItemType(), bmoTypeCostCenter.getParentId());
	}

	@Override
	public void open(BmObject bmObject) {
		UiBudgetItemTypeForm uiTypeCostCenterForm = new UiBudgetItemTypeForm(getUiParams(), bmObject.getId());
		uiTypeCostCenterForm.show();
	}

	public class UiBudgetItemTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();	
		TextArea descriptionTextArea = new TextArea();
		CheckBox isGroupCheckBox = new CheckBox();
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiSuggestBox parentSuggestBox; 

		BmoBudgetItemType bmoTypeCostCenter;

		public UiBudgetItemTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBudgetItemType(), id);
			bmoTypeCostCenter = (BmoBudgetItemType)getBmObject();

			// Agregar filtros al tipo de flujo
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoTypeCostCenter.getKind(), bmoTypeCostCenter.getIsGroup(), 1);
			parentSuggestBox = new UiSuggestBox(bmoTypeCostCenter, bmFilter);
		}

		@Override
		public void populateFields() {
			bmoTypeCostCenter = (BmoBudgetItemType)getBmObject();
			formFlexTable.addField(2, 0, nameTextBox, bmoTypeCostCenter.getName());		
			formFlexTable.addField(3, 0, descriptionTextArea, bmoTypeCostCenter.getDescription());
			formFlexTable.addField(4, 0, typeListBox, bmoTypeCostCenter.getType());
			formFlexTable.addField(5, 0, parentSuggestBox, bmoTypeCostCenter.getParentId());
			formFlexTable.addField(6, 0, isGroupCheckBox, bmoTypeCostCenter.getIsGroup());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoTypeCostCenter.setId(id);
			bmoTypeCostCenter.getName().setValue(nameTextBox.getText());		
			bmoTypeCostCenter.getDescription().setValue(descriptionTextArea.getText());
			bmoTypeCostCenter.getType().setValue(typeListBox.getSelectedCode());
			bmoTypeCostCenter.getIsGroup().setValue(isGroupCheckBox.getValue());
			bmoTypeCostCenter.getParentId().setValue(parentSuggestBox.getSelectedId());

			return bmoTypeCostCenter;
		}

		@Override
		public void close() {
			list();
		}
	}
}
