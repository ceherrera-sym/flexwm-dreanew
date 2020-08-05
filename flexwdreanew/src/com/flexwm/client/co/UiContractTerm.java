/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.flexwm.shared.co.BmoContractTerm;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


/**
 * @author pguerra
 *
 */

public class UiContractTerm extends UiList {
	BmoContractTerm bmoContractTerm;

	public UiContractTerm(UiParams uiParams) {
		super(uiParams, new BmoContractTerm());
		bmoContractTerm = (BmoContractTerm)getBmObject();
	}

	@Override
	public void postShow( ){
		//addFilterListBox(new UiListBox(getUiParams(), new bmoCreditTypeGroup()), bmoCreditType.getbmoCreditTypeGroup());
	}

	@Override
	public void create() {
		UiContractTermForm uiTermForm = new UiContractTermForm(getUiParams(), 0);
		uiTermForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiContractTermForm uiTermForm = new UiContractTermForm(getUiParams(), bmObject.getId());
		uiTermForm.show();
	}

	public class UiContractTermForm extends UiFormDialog {
		
		TextBox nameTextBox = new TextBox();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();
		TextBox deadLineTextBox = new TextBox();
		
		BmoContractTerm bmoContractTerm;

		public UiContractTermForm(UiParams uiParams, int id) {
			super(uiParams, new BmoContractTerm(), id); 
		}

		@Override
		public void populateFields() {
			bmoContractTerm = (BmoContractTerm)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoContractTerm.getName());
			formFlexTable.addField(2, 0, startDateBox, bmoContractTerm.getStartDate());
			formFlexTable.addField(3, 0, endDateBox, bmoContractTerm.getEndDate());
			formFlexTable.addField(4, 0, deadLineTextBox, bmoContractTerm.getDeadLine());
							
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoContractTerm.setId(id);
			bmoContractTerm.getName().setValue(nameTextBox.getText());
			bmoContractTerm.getStartDate().setValue(startDateBox.getDateTime());
			bmoContractTerm.getEndDate().setValue(endDateBox.getDateTime());
			bmoContractTerm.getDeadLine().setValue(deadLineTextBox.getText());
			
			return bmoContractTerm;
		}

		@Override
		public void close() {
			list();
		}
	}
}
