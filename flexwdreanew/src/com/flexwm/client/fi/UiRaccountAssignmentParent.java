/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountAssignmentParent;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;

public class UiRaccountAssignmentParent extends UiList {
	BmoRaccountAssignmentParent bmoRaccountAssignmentParent;
	BmoRaccount bmoRaccount = new BmoRaccount();
	
	
	public UiRaccountAssignmentParent(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount) {
		super(uiParams, defaultPanel, new BmoRaccountAssignmentParent());
		bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)getBmObject();
		this.bmoRaccount = bmoRaccount;
	}


	@Override
	public void postShow() {
		newImage.setVisible(false);
	}

	@Override
	public void create() {
		UiRaccountAssignmentParentForm uiCustomerStatusForm = new UiRaccountAssignmentParentForm(getUiParams(), 0);
		uiCustomerStatusForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRaccountAssignmentParentForm uiCustomerStatusForm = new UiRaccountAssignmentParentForm(getUiParams(), bmObject.getId());
		uiCustomerStatusForm.show();
	}

	 @Override
	 public void edit(BmObject bmObject) {
		 UiRaccountAssignmentParentForm uiCustomerStatusForm = new UiRaccountAssignmentParentForm(getUiParams(),
		 bmObject.getId());
		 uiCustomerStatusForm.show();
	 }

	public class UiRaccountAssignmentParentForm extends UiFormDialog {
		
		UiListBox raccountListBox = new UiListBox(getUiParams(), new BmoRaccount());
		BmoRaccountAssignmentParent bmoRaccountAssignmentParent;
		
		public UiRaccountAssignmentParentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRaccountAssignmentParent(), id);
			
		}
		
		@Override
		public void postShow() {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
		}

		@Override
		public void populateFields(){
			bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)getBmObject();

			formFlexTable.addField(1, 0, raccountListBox, bmoRaccountAssignmentParent.getRaccountId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRaccountAssignmentParent.setId(id);
			bmoRaccountAssignmentParent.getRaccountId().setValue(bmoRaccount.getId());
			//bmoCustomerStatus.getStatus().setValue(statusListBox.getSelectedCode());
			
			return bmoRaccountAssignmentParent;
		}

		@Override
		public void close() {
			list();
		}

		
	}
}


