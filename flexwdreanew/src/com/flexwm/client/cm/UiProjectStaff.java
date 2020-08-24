package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectStaff;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;

public class UiProjectStaff extends UiList {
	
	BmoProjectStaff bmoProjectStaff;
	BmoProject bmoProject;
	
	public UiProjectStaff(UiParams uiParams, Panel defaultPanel,BmoProject bmoProject) {
		super(uiParams, defaultPanel,new BmoProjectStaff());
		bmoProjectStaff = (BmoProjectStaff)getBmObject();
		this.bmoProject = bmoProject;
		
		
	}
	
	@Override
	public void create() {
		UiProjectStaffForm uiProjectStaffForm = new UiProjectStaffForm(getUiParams(), 0);
		uiProjectStaffForm.show();
	}
	@Override
	public void open(BmObject bmObject) {
		bmoProjectStaff = (BmoProjectStaff)bmObject;
		UiProjectStaffForm uiProjectStaffForm = new UiProjectStaffForm(getUiParams(), bmoProjectStaff.getId());
		uiProjectStaffForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {
		UiProjectStaffForm uiProjectStaffForm = new UiProjectStaffForm(getUiParams(), bmObject.getId());
		uiProjectStaffForm.show();
	}
	private class UiProjectStaffForm extends UiFormDialog {

		UiTextBox codeTextBox = new UiTextBox();
		TextArea notesTextArea = new TextArea();
		UiListBox profileUiList = new UiListBox(getUiParams(), new BmoProfile());
		UiListBox userUiList = new UiListBox(getUiParams());
		
		public UiProjectStaffForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProjectStaff(), id);
			bmoProjectStaff = (BmoProjectStaff)getBmObject();
			
			
		}
		
		@Override
		public void populateFields(){	
			bmoProjectStaff = (BmoProjectStaff)getBmObject();
			
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmoUser bmoUser = new BmoUser();				
			BmFilter userFilter = new BmFilter();
			
			userFilter.setInFilter(bmoProfileUser.getKind(), bmoUser.getIdFieldName(), 
					bmoProfileUser.getUserId().getName(), bmoProfileUser.getProfileId().getName(),
					"" + bmoProjectStaff.getProfileId().toInteger());
			userUiList = new UiListBox(getUiParams(),new BmoUser(),userFilter);
			int row = 0;
			formFlexTable.addField(row++, 0, codeTextBox, bmoProjectStaff.getCode());
			formFlexTable.addField(row++, 0, profileUiList, bmoProjectStaff.getProfileId());
			formFlexTable.addField(row++, 0, userUiList, bmoProjectStaff.getUserId());
			formFlexTable.addField(row++, 0, notesTextArea, bmoProjectStaff.getNotes());
			
			
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoProjectStaff.setId(id);
			bmoProjectStaff.getCode().setValue(codeTextBox.getText());
			bmoProjectStaff.getProfileId().setValue(profileUiList.getSelectedId());
			bmoProjectStaff.getUserId().setValue(userUiList.getSelectedId());
			bmoProjectStaff.getNotes().setValue(notesTextArea.getText());
			bmoProjectStaff.getProjectId().setValue(bmoProject.getId());
			return bmoProjectStaff;
		}
		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == profileUiList) {
				BmoProfile bmoProfile = (BmoProfile)profileUiList.getSelectedBmObject();
				
				if (bmoProfile == null) {
					populateUserItems(-1);
				} else {
					populateUserItems(bmoProfile.getId());
				}
			}
		}
		
		private void populateUserItems(int profileId) {
			userUiList.clear();						
			userUiList.clearFilters();
			setUsersItemsFilters(profileId);
			userUiList.populate(bmoProjectStaff.getUserId());
		}
		
		private void setUsersItemsFilters(int profileId) {
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmoUser bmoUser = new BmoUser();				
			BmFilter userFilter = new BmFilter();
			if (profileId > 0) {
				userFilter.setInFilter(bmoProfileUser.getKind(), bmoUser.getIdFieldName(), 
						bmoProfileUser.getUserId().getName(), bmoProfileUser.getProfileId().getName(),
						"" + profileId);
				
			}else {
				userFilter.setInFilter(bmoProfileUser.getKind(), bmoUser.getIdFieldName(), 
						bmoProfileUser.getUserId().getName(), bmoProfileUser.getProfileId().getName(),
						"-1");
			}
			userUiList.addBmFilter(userFilter);
			
			
		}
		@Override
		public void close() {
			list();
		}
		
	}
}
