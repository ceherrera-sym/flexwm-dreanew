/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.cv.BmoMeeting;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiMeeting extends UiList {
	BmoMeeting bmoMeeting;

	public UiMeeting(UiParams uiParams) {
		super(uiParams, new BmoMeeting());
		bmoMeeting = (BmoMeeting)getBmObject();
	}

	@Override
	public void create() {
		UiMeetingForm uiMeetingForm = new UiMeetingForm(getUiParams(), 0);
		uiMeetingForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoMeeting = (BmoMeeting)bmObject;
		UiMeetingDetail uiMeetingDetail = new UiMeetingDetail(getUiParams(), bmoMeeting.getId());
		uiMeetingDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiMeetingForm uiMeetingForm = new UiMeetingForm(getUiParams(), bmObject.getId());
		uiMeetingForm.show();
	}

	public class UiMeetingForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateDateBox = new UiDateTimeBox();
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
		UiListBox wFlowTypeListBox;

		BmoMeeting bmoMeeting;
		int programId;

		public UiMeetingForm(UiParams uiParams, int id) {
			super(uiParams, new BmoMeeting(), id);
			bmoMeeting = (BmoMeeting)getBmObject();
			initialize();
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoMeeting.getProgramCode());
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por usuarios activos
			BmoUser bmoUser = new BmoUser();
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterUserActive);
		}

		@Override
		public void populateFields(){
			bmoMeeting = (BmoMeeting)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoMeeting.getCode());
			formFlexTable.addField(2, 0, wFlowTypeListBox, bmoMeeting.getWFlowTypeId());
			formFlexTable.addField(3, 0, nameTextBox, bmoMeeting.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoMeeting.getDescription());
			formFlexTable.addField(5, 0, startDateDateBox, bmoMeeting.getStartdate());
			formFlexTable.addField(6, 0, endDateDateBox, bmoMeeting.getEnddate());
			formFlexTable.addField(7, 0, userSuggestBox, bmoMeeting.getUserId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoMeeting.setId(id);
			bmoMeeting.getCode().setValue(codeTextBox.getText());
			bmoMeeting.getName().setValue(nameTextBox.getText());
			bmoMeeting.getDescription().setValue(descriptionTextArea.getText());
			bmoMeeting.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoMeeting.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoMeeting.getStartdate().setValue(startDateDateBox.getDateTime());
			bmoMeeting.getEnddate().setValue(endDateDateBox.getDateTime());

			return bmoMeeting;
		}

		@Override
		public void close() {
			//list();
		}

		@Override
		public void saveNext() {
			if (bmoMeeting.getWFlowTypeId().toInteger() > 0) {
				UiMeetingDetail uiMeetingDetail = new UiMeetingDetail(getUiParams(), id);
				uiMeetingDetail.show();
			} else {
				UiMeeting uiMeetingList = new UiMeeting(getUiParams());
				uiMeetingList.show();
			}
		}
	}
}
