/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;


public class UiWFlowCategoryProfile extends UiList {
	BmoWFlowCategoryProfile bmoWFlowGroup;
	BmoWFlowCategory bmoWFlowCategory;

	public UiWFlowCategoryProfile(UiParams uiParams, Panel defaultPanel, BmoWFlowCategory bmoWFlowCategory) {
		super(uiParams, defaultPanel, new BmoWFlowCategoryProfile());
		bmoWFlowGroup = new BmoWFlowCategoryProfile();
		this.bmoWFlowCategory = bmoWFlowCategory;

		BmFilter filterByCategory = new BmFilter();
		filterByCategory.setValueLabelFilter(bmoWFlowGroup.getKind(), 
				bmoWFlowGroup.getWFlowCategoryId().getName(), 
				bmoWFlowGroup.getWFlowCategoryId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowCategory.getId(),
				bmoWFlowGroup.getWFlowCategoryId().getName());
		getUiParams().setForceFilter(bmoWFlowGroup.getProgramCode(), filterByCategory);	
	}
	
	@Override
	public void create() {
		UiWFlowGroupForm uiWFlowGroupForm = new UiWFlowGroupForm(getUiParams(), 0);
		uiWFlowGroupForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowGroupForm uiWFlowGroupForm = new UiWFlowGroupForm(getUiParams(), bmObject.getId());
		uiWFlowGroupForm.show();
	}

	
	public class UiWFlowGroupForm extends UiFormDialog {
		UiListBox groupListBox = new UiListBox(getUiParams(), new BmoProfile());
		CheckBox requiredCheckBox = new CheckBox();
		CheckBox autoDateCheckBox = new CheckBox();
		CheckBox autoProfileCheckBox = new CheckBox();

		public UiWFlowGroupForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowCategoryProfile(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowGroup = (BmoWFlowCategoryProfile)getBmObject();
			formFlexTable.addField(1, 0, groupListBox, bmoWFlowGroup.getProfileId());	
			formFlexTable.addField(2, 0, autoDateCheckBox, bmoWFlowGroup.getAutoDate());
			formFlexTable.addField(3, 0, requiredCheckBox, bmoWFlowGroup.getRequired());
			formFlexTable.addField(4, 0, autoProfileCheckBox, bmoWFlowGroup.getAutoProfile());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowGroup = new BmoWFlowCategoryProfile();
			bmoWFlowGroup.setId(id);
			bmoWFlowGroup.getProfileId().setValue(groupListBox.getSelectedId());
			bmoWFlowGroup.getAutoDate().setValue(autoDateCheckBox.getValue());
			bmoWFlowGroup.getRequired().setValue(requiredCheckBox.getValue());
			bmoWFlowGroup.getAutoProfile().setValue(autoProfileCheckBox.getValue());
			bmoWFlowGroup.getWFlowCategoryId().setValue(bmoWFlowCategory.getId());
			return bmoWFlowGroup;
		}
		
		@Override
		public void close() {
			list();
		}
	}
}