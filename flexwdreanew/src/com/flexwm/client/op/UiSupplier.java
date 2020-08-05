/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCity;


/**
 * @author jhernandez
 *
 */

public class UiSupplier extends UiList {
	BmoSupplier bmoSupplier;

	public UiSupplier(UiParams uiParams) {
		super(uiParams, new BmoSupplier());
		bmoSupplier = (BmoSupplier)getBmObject();
	}

	@Override
	public void create() {
		UiSupplierForm uiSupplierForm = new UiSupplierForm(getUiParams(), 0);
		uiSupplierForm.show();
	}

	@Override
	public void postShow() {
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoSupplier.getType()), bmoSupplier, bmoSupplier.getType());	
		addFilterListBox(new UiListBox(getUiParams(), new BmoSupplierCategory()), bmoSupplier.getBmoSupplierCategory());
	}

	@Override
	public void open(BmObject bmObject) {
		UiSupplierForm uiSupplierForm = new UiSupplierForm(getUiParams(), bmObject.getId());
		uiSupplierForm.show();
	}

	public class UiSupplierForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea legalNameTextBox = new TextArea();
		UiListBox supplierTypeListBox = new UiListBox(getUiParams());
		TextArea descriptionTextArea = new TextArea();
		TextBox officePhone1TextBox = new TextBox();
		TextBox emailTextBox = new TextBox();
		TextBox rfcTextBox = new TextBox();
		TextBox legalRepTextBox = new TextBox();
		TextBox imssTextBox = new TextBox();	
		UiListBox fiscalTypeListBox = new UiListBox(getUiParams());	
		TextBox clabeTextBox = new TextBox();
		TextBox lawerNameTextBox = new TextBox();
		TextBox lawerNumberTextBox = new TextBox();
		TextBox lawerDeedTextBox  = new TextBox();
		UiDateBox lawerDeedDateDateBox = new UiDateBox();
		UiSuggestBox cityDeedSuggestBox = new UiSuggestBox(new BmoCity());
		UiListBox supplierCategoryListBox = new UiListBox(getUiParams(), new BmoSupplierCategory());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		UiListBox paymentTypeListBox = new UiListBox(getUiParams());
		//Autorizar el Envio de Correo
		CheckBox enableSendEmailCheckBox = new CheckBox();
		BmoSupplier bmoSupplier;

		String generalSection = "Datos Generales";
		String legalInformationSection = "Información Legal";
		String extraSection = "Datos Adicionales";

		public UiSupplierForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSupplier(), id);
		}

		@Override
		public void populateFields() {
			bmoSupplier = (BmoSupplier)getBmObject();

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoSupplier.getCurrencyId().toInteger() > 0)) {
				try {
					bmoSupplier.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				} catch (BmException e) {
					showSystemMessage("No se puede asignar moneda : " + e.toString());
				}
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 4);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoSupplier.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoSupplier.getName());
			formFlexTable.addField(4, 0, legalNameTextBox, bmoSupplier.getLegalName());
			formFlexTable.addField(5, 0, descriptionTextArea, bmoSupplier.getDescription());
			formFlexTable.addField(6, 0, supplierTypeListBox, bmoSupplier.getType());
			formFlexTable.addField(7, 0, supplierCategoryListBox, bmoSupplier.getSupplierCategoryId());
			formFlexTable.addField(8, 0, rfcTextBox, bmoSupplier.getRfc());
			formFlexTable.addField(9, 0, legalRepTextBox, bmoSupplier.getLegalRep());
			formFlexTable.addField(10, 0, imssTextBox, bmoSupplier.getImss());		
			formFlexTable.addField(11, 0, fiscalTypeListBox, bmoSupplier.getFiscalType());		
			formFlexTable.addField(12, 0, currencyListBox, bmoSupplier.getCurrencyId());
			formFlexTable.addField(13, 0, officePhone1TextBox, bmoSupplier.getOfficePhone1());		
			formFlexTable.addField(14, 0, emailTextBox, bmoSupplier.getEmail());
			formFlexTable.addField(15, 0, enableSendEmailCheckBox, bmoSupplier.getSendEmail());

			formFlexTable.addSectionLabel(16, 0, legalInformationSection, 4);
			formFlexTable.addField(17, 0, lawerNumberTextBox, bmoSupplier.getLawyerNumber());
			formFlexTable.addField(18, 0, lawerNameTextBox, bmoSupplier.getLawyerName());		
			formFlexTable.addField(19, 0, lawerDeedTextBox, bmoSupplier.getLawyerDeed());
			formFlexTable.addField(20, 0, lawerDeedDateDateBox, bmoSupplier.getLawyerDeedDate());
			formFlexTable.addField(21, 0, cityDeedSuggestBox, bmoSupplier.getCityDeedId());
		
			

			if (!newRecord) {
				formFlexTable.addSectionLabel(22, 0, extraSection, 4);
				if (bmoSupplier.getType().equals(BmoSupplier.TYPE_BROKER)) 
					formFlexTable.addField(23, 0, new UiSupplierUserLabelList(getUiParams(), id));
				formFlexTable.addField(24, 0, new UiSupplierEmailLabeList(getUiParams(), id));
				formFlexTable.addField(25, 0, new UiSupplierPhoneLabelList(getUiParams(), id));
				formFlexTable.addField(26, 0, new UiSupplierAddressLabelList(getUiParams(), id));
				formFlexTable.addField(27, 0, new UiSupplierContactLabelList(getUiParams(), id));
				formFlexTable.addField(28, 0, new UiSupplierBankAccountLabelList(getUiParams(), id));
				formFlexTable.addField(29, 0, new UiSupplierPaymentTypeLabelList(getUiParams(), id));
				formFlexTable.addField(30, 0, new UiSupplierCompanyLabelList(getUiParams(), id));
			}

//			formFlexTable.hideSection(generalSection);
			formFlexTable.hideSection(legalInformationSection);
//			formFlexTable.hideSection(extraSection);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSupplier.setId(id);
			bmoSupplier.getCode().setValue(codeTextBox.getText());
			bmoSupplier.getName().setValue(nameTextBox.getText());
			bmoSupplier.getLegalName().setValue(legalNameTextBox.getText());
			bmoSupplier.getDescription().setValue(descriptionTextArea.getText());
			bmoSupplier.getSupplierCategoryId().setValue(supplierCategoryListBox.getSelectedId());
			bmoSupplier.getType().setValue(supplierTypeListBox.getSelectedCode());
			bmoSupplier.getOfficePhone1().setValue(officePhone1TextBox.getText());
			bmoSupplier.getEmail().setValue(emailTextBox.getText());
			bmoSupplier.getRfc().setValue(rfcTextBox.getText());
			bmoSupplier.getLegalRep().setValue(legalRepTextBox.getText());
			bmoSupplier.getImss().setValue(imssTextBox.getText());		
			bmoSupplier.getFiscalType().setValue(fiscalTypeListBox.getSelectedCode());		
			bmoSupplier.getLawyerNumber().setValue(lawerNumberTextBox.getText());
			bmoSupplier.getLawyerName().setValue(lawerNameTextBox.getText());
			bmoSupplier.getLawyerDeed().setValue(lawerDeedTextBox.getText());
			bmoSupplier.getLawyerDeedDate().setValue(lawerDeedDateDateBox.getTextBox().getText());
			bmoSupplier.getCityDeedId().setValue(cityDeedSuggestBox.getSelectedId());
			bmoSupplier.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoSupplier.getSendEmail().setValue(enableSendEmailCheckBox.getValue());
			return bmoSupplier;
		}

		@Override
		public void close() {
			list();
		}	

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiSupplierForm uiSupplierForm = new UiSupplierForm(getUiParams(), getBmObject().getId());
				uiSupplierForm.show();
			} else {
				list();
			}		
		}
	}
}

