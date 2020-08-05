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

import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertySaleDetail;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiPropertySaleDetailForm extends UiFormDialog {

	UiSuggestBox notaryUiSuggestBox = new UiSuggestBox(new BmoSupplier());
	TextBox writingNumberTextBox = new TextBox();
	TextBox creditModalityTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextArea descriptionLogTextArea = new TextArea();
	String propertySaleId;
	int propertySaleDetailId;
	String logSection = "Bitácora";
	
	BmoPropertySaleDetail bmoPropertySaleDetail;
	BmoPropertySale bmoPropertySale;

	public UiPropertySaleDetailForm(UiParams uiParams, int id, BmoPropertySale bmoPropertySale) {
		super(uiParams, new BmoPropertySaleDetail(), id);
		bmoPropertySaleDetail = (BmoPropertySaleDetail)getBmObject();
		this.bmoPropertySale = bmoPropertySale;
		super.foreignId = bmoPropertySale.getId();
		this.propertySaleId = "" + foreignId;
		this.propertySaleDetailId = id;
		super.foreignField = bmoPropertySaleDetail.getPropertySaleId().getName();
	}

	@Override
	public void populateFields() {
		bmoPropertySaleDetail = (BmoPropertySaleDetail)getBmObject();
		int row = 0;
		formFlexTable.addField(row++, 0, notaryUiSuggestBox, bmoPropertySaleDetail.getNotary());
		formFlexTable.addField(row++, 0, writingNumberTextBox, bmoPropertySaleDetail.getWrintingNumber());
		formFlexTable.addField(row++, 0, creditModalityTextBox, bmoPropertySaleDetail.getCreditModality());
		formFlexTable.addField(row++, 0, creditModalityTextBox, bmoPropertySaleDetail.getCreditModality());
		formFlexTable.addField(row++, 0, descriptionTextArea, bmoPropertySaleDetail.getDescription());
		formFlexTable.addSectionLabel(row++, 0, logSection, 2);
		formFlexTable.addField(row++, 0, descriptionLogTextArea, bmoPropertySaleDetail.getDescriptionLog());
		descriptionLogTextArea.setEnabled(false);
		descriptionLogTextArea.setHeight("100px");
		
		formFlexTable.hideSection(logSection);
	}
	
	@Override
	public void postShow() {
		deleteButton.setVisible(false);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoPropertySaleDetail.setId(id);
		bmoPropertySaleDetail.getPropertySaleId().setValue(propertySaleId);
		bmoPropertySaleDetail.getNotary().setValue(notaryUiSuggestBox.getSelectedId());
		bmoPropertySaleDetail.getWrintingNumber().setValue(writingNumberTextBox.getText());
		bmoPropertySaleDetail.getCreditModality().setValue(creditModalityTextBox.getText());
		bmoPropertySaleDetail.getDescription().setValue(descriptionTextArea.getText());
		bmoPropertySaleDetail.getDescriptionLog().setValue(descriptionLogTextArea.getText());
		return bmoPropertySaleDetail;
	}

	@Override
	public void close() {
		dialogClose();
	}
}
