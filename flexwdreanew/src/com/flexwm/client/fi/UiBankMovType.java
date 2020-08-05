/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoRaccountType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiBankMovType extends UiList {
	public UiBankMovType(UiParams uiParams) {
		super(uiParams, new BmoBankMovType());
	}

	@Override
	public void create() {
		UiBankMovTypeForm uiBankMovTypeForm = new UiBankMovTypeForm(getUiParams(), 0);
		uiBankMovTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiBankMovTypeForm uiBankMovTypeForm = new UiBankMovTypeForm(getUiParams(), bmObject.getId());
		uiBankMovTypeForm.show();
	}

	public class UiBankMovTypeForm extends UiFormDialog {
		TextBox invoicenoTextBox = new TextBox();	
		TextBox nameTextBox = new TextBox();	
		TextArea descriptionTextArea = new TextArea();
		UiListBox visibleTextBox = new UiListBox(getUiParams()); 
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox subTypeListBox = new UiListBox(getUiParams());
		UiListBox categoryListBox = new UiListBox(getUiParams());
		UiListBox childListBox = new UiListBox(getUiParams(), new  BmoBankMovType());
		UiListBox paccountTypeListBox = new UiListBox(getUiParams(), new BmoPaccountType());
		UiListBox raccountTypeListBox = new UiListBox(getUiParams(), new BmoRaccountType());
		BmoBankMovType bmoBankMovType;

		public UiBankMovTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoBankMovType(), id);
		}

		@Override
		public void populateFields() {
			bmoBankMovType = (BmoBankMovType)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoBankMovType.getName());		
			formFlexTable.addField(2, 0, descriptionTextArea, bmoBankMovType.getDescription());
			formFlexTable.addField(3, 0, typeListBox, bmoBankMovType.getType());		
			formFlexTable.addField(4, 0, categoryListBox, bmoBankMovType.getCategory());
			formFlexTable.addField(5, 0, paccountTypeListBox, bmoBankMovType.getPaccountTypeId());
			formFlexTable.addField(6, 0, raccountTypeListBox, bmoBankMovType.getRaccountTypeId());
			formFlexTable.addField(7, 0, childListBox, bmoBankMovType.getBankMovTypeChildId());
			formFlexTable.addField(8, 0, visibleTextBox, bmoBankMovType.getVisible());
			// Ocultar segun tipo
			if (newRecord) {			
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(false);
			}
			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoBankMovType.setId(id);
			bmoBankMovType.getName().setValue(nameTextBox.getText());		
			bmoBankMovType.getDescription().setValue(descriptionTextArea.getText());
			bmoBankMovType.getType().setValue(typeListBox.getSelectedCode());		
			bmoBankMovType.getCategory().setValue(categoryListBox.getSelectedCode());		
			bmoBankMovType.getPaccountTypeId().setValue(paccountTypeListBox.getSelectedId());
			bmoBankMovType.getRaccountTypeId().setValue(raccountTypeListBox.getSelectedId());
			bmoBankMovType.getBankMovTypeChildId().setValue(childListBox.getSelectedId());
			bmoBankMovType.getVisible().setValue(visibleTextBox.getSelectedCode());

			return bmoBankMovType;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			statusEffect();
		}

		private void statusEffect() {
			paccountTypeListBox.setEnabled(false);
			raccountTypeListBox.setEnabled(false);
			childListBox.setEnabled(false);


			if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_CXP) 
					|| categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				paccountTypeListBox.setEnabled(true);
				raccountTypeListBox.setEnabled(false);
				childListBox.setEnabled(false);
			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_CXC)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(true);
				childListBox.setEnabled(false);
			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(true);
				childListBox.setEnabled(false);	

			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_DEPOSITFREE)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(false);
				childListBox.setEnabled(false);		 
			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_TRANSFER)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(false);
				if (typeListBox.getSelectedCode().equals("" + BmoBankMovType.TYPE_WITHDRAW)) {
					childListBox.setEnabled(true);
				}			
			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_DISPOSALFREE)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(false);
				childListBox.setEnabled(false);
			} else if (categoryListBox.getSelectedCode().equals("" + BmoBankMovType.CATEGORY_MULTIPLECXC)) {
				paccountTypeListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(true);
				childListBox.setEnabled(false);
			}
		}	
	}
}
