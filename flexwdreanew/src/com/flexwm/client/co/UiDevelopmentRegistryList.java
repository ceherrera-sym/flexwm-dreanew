/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoDevelopmentRegistry;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiDevelopmentRegistryList extends UiList {
	BmoDevelopmentRegistry bmoDevelopmentRegistry;

	public UiDevelopmentRegistryList(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentRegistry());
		bmoDevelopmentRegistry = (BmoDevelopmentRegistry)getBmObject();
	}

	@Override
	public void create() {
		UiDevelopmentRegistryForm uiDevelopmentRegistryForm = new UiDevelopmentRegistryForm(getUiParams(), 0);
		uiDevelopmentRegistryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopmentRegistry = (BmoDevelopmentRegistry)bmObject;
		UiDevelopmentRegistryForm uiDevelopmentRegistryForm = new UiDevelopmentRegistryForm(getUiParams(), bmoDevelopmentRegistry.getId());
		uiDevelopmentRegistryForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiDevelopmentRegistryForm uiDevelopmentRegistryForm = new UiDevelopmentRegistryForm(getUiParams(), bmObject.getId());
		uiDevelopmentRegistryForm.show();
	}

	public class UiDevelopmentRegistryForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox legalNumberTextBox = new TextBox();
		UiDateBox legalNumberDateBox = new UiDateBox();
		TextBox accountTextBox = new TextBox();
		UiDateBox registryDateBox = new UiDateBox();
		UiDateBox dueDateWorkDateBox = new UiDateBox();
		UiDateBox dueDateSaleDateBox = new UiDateBox();
		UiListBox developmentPhaseListBox = new UiListBox(getUiParams(), new BmoDevelopmentPhase());
		UiSuggestBox supplierIdSuggestBox = new UiSuggestBox(new BmoSupplier());
		TextBox bankaccountIdTextBox = new TextBox();

		BmoDevelopmentRegistry bmoDevelopmentRegistry;

		public UiDevelopmentRegistryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDevelopmentRegistry(), id); 
		}

		@Override
		public void populateFields() {
			bmoDevelopmentRegistry = (BmoDevelopmentRegistry)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoDevelopmentRegistry.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoDevelopmentRegistry.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoDevelopmentRegistry.getDescription());
			formFlexTable.addField(4, 0, legalNumberTextBox, bmoDevelopmentRegistry.getLegalNumber());
			formFlexTable.addField(5, 0, legalNumberDateBox, bmoDevelopmentRegistry.getLegalNumberDate());
			formFlexTable.addField(6, 0, accountTextBox, bmoDevelopmentRegistry.getAccount());
			formFlexTable.addField(7, 0, registryDateBox, bmoDevelopmentRegistry.getRegistryDate());
			formFlexTable.addField(8, 0, dueDateWorkDateBox, bmoDevelopmentRegistry.getDuedateWork());
			formFlexTable.addField(9, 0, dueDateSaleDateBox, bmoDevelopmentRegistry.getDuedateSale());
			formFlexTable.addField(10, 0, developmentPhaseListBox, bmoDevelopmentRegistry.getDevelopmentPhaseId());
			formFlexTable.addField(11, 0, supplierIdSuggestBox, bmoDevelopmentRegistry.getSupplierId());
			formFlexTable.addField(12, 0, bankaccountIdTextBox, bmoDevelopmentRegistry.getBankAccountId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDevelopmentRegistry.setId(id);
			bmoDevelopmentRegistry.getCode().setValue(codeTextBox.getText());
			bmoDevelopmentRegistry.getName().setValue(nameTextBox.getText());
			bmoDevelopmentRegistry.getDescription().setValue(descriptionTextArea.getText());
			bmoDevelopmentRegistry.getLegalNumber().setValue(legalNumberTextBox.getText());
			bmoDevelopmentRegistry.getLegalNumberDate().setValue(legalNumberDateBox.getTextBox().getText());
			bmoDevelopmentRegistry.getAccount().setValue(accountTextBox.getText());
			bmoDevelopmentRegistry.getRegistryDate().setValue(registryDateBox.getTextBox().getText());
			bmoDevelopmentRegistry.getDuedateWork().setValue(dueDateWorkDateBox.getTextBox().getText());
			bmoDevelopmentRegistry.getDuedateSale().setValue(dueDateSaleDateBox.getTextBox().getText());
			bmoDevelopmentRegistry.getDevelopmentPhaseId().setValue(developmentPhaseListBox.getSelectedId());
			bmoDevelopmentRegistry.getSupplierId().setValue(supplierIdSuggestBox.getSelectedId());
			bmoDevelopmentRegistry.getBankAccountId().setValue(bankaccountIdTextBox.getText());
			return bmoDevelopmentRegistry;
		}

		@Override
		public void close() {
			list();
		}
	}
}
