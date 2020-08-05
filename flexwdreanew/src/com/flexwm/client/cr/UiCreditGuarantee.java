/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cr;

import com.flexwm.client.cr.UiCredit.UiCreditForm.CreditUpdater;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiCreditGuarantee extends UiList {
	BmoCreditGuarantee bmoCreditGuarantee;	
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	BmoCredit bmoCredit;
	protected CreditUpdater creditUpdater;
	int creditId;	

	public UiCreditGuarantee(UiParams uiParams, Panel defaultPanel, BmoCredit bmoCredit, int id, CreditUpdater creditUpdater) {
		super(uiParams, defaultPanel, new BmoCreditGuarantee());
		creditId = id;
		bmoCreditGuarantee = new BmoCreditGuarantee();
		this.bmoCredit = bmoCredit;
		this.creditUpdater = creditUpdater;
	}	

	public UiCreditGuarantee(UiParams uiParams) {
		super(uiParams, new BmoCreditGuarantee());
		bmoCreditGuarantee = (BmoCreditGuarantee)getBmObject();
	}

	@Override
	public void create() {
		UiCreditGuaranteeForm uiCreditGuaranteeForm = new UiCreditGuaranteeForm(getUiParams(), 0);
		uiCreditGuaranteeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCreditGuaranteeForm uiCreditGuaranteeForm = new UiCreditGuaranteeForm(getUiParams(), bmObject.getId());
		uiCreditGuaranteeForm.show();
	}

	private class UiCreditGuaranteeForm extends UiFormDialog {

		public UiCreditGuaranteeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCreditGuarantee(), id);
		}

		@Override
		public void populateFields(){
			bmoCreditGuarantee = (BmoCreditGuarantee)getBmObject();

			if (creditId > 0)
				try {
					bmoCreditGuarantee.getCreditId().setValue(creditId);
				} catch (BmException e) {
					showSystemMessage("No se pudo asignar correctamente el Crédito: " + e.toString());
				}

			formFlexTable.addField(1, 0, customerSuggestBox, bmoCreditGuarantee.getCustomerId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCreditGuarantee = new BmoCreditGuarantee();
			bmoCreditGuarantee.setId(id);
			bmoCreditGuarantee.getCreditId().setValue(creditId);
			bmoCreditGuarantee.getCustomerId().setValue(customerSuggestBox.getSelectedId());		
			return bmoCreditGuarantee;
		}

		@Override
		public void close() {
			list();
			if (creditUpdater != null)
				creditUpdater.update();
		}
	}
}
