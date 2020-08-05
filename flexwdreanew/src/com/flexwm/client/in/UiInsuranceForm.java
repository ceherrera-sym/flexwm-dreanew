/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;

import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.in.BmoGoal;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceCategory;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiInsuranceForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	CheckBox maxUnlimitedCheckBox = new CheckBox();
	TextBox maxAmountTextBox = new TextBox();
	CheckBox minUnlimitedCheckBox = new CheckBox();
	TextBox minAmountTextBox = new TextBox();
	TextBox maxAgeTextBox = new TextBox();
	TextBox minAgeTextBox = new TextBox();
	TextBox payYearsTextBox = new TextBox();
	TextArea commentsTextArea = new TextArea();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox goalListBox = new UiListBox(getUiParams(), new BmoGoal());
	UiListBox productCategoryListBox = new UiListBox(getUiParams(), new BmoInsuranceCategory());
	UiListBox newCurrencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	Button copyButton = new Button("Copiar");
	
	BmoInsurance bmoInsurance;
	boolean copyProcessing = false;
	
	public UiInsuranceForm(UiParams uiParams, int id) {
		super(uiParams, new BmoInsurance(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoInsurance = (BmoInsurance)getBmObject();
		formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoInsurance.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoInsurance.getName());
		
		formFlexTable.addField(2, 0, productCategoryListBox, bmoInsurance.getInsuranceCategoryId());
		formFlexTable.addField(2, 2, goalListBox, bmoInsurance.getGoalId());
		
		formFlexTable.addField(3, 0, currencyListBox, bmoInsurance.getCurrencyId());
		formFlexTable.addField(3, 2, payYearsTextBox, bmoInsurance.getPayYears());
		
		formFlexTable.addField(4, 0, descriptionTextArea, bmoInsurance.getDescription());
		formFlexTable.addField(4, 2, commentsTextArea, bmoInsurance.getComments());

		formFlexTable.addField(5, 0, minAgeTextBox, bmoInsurance.getMinAge());
		formFlexTable.addField(5, 2, maxAgeTextBox, bmoInsurance.getMaxAge());

		formFlexTable.addField(6, 0, minAmountTextBox, bmoInsurance.getMinAmount());
		formFlexTable.addField(6, 2, minUnlimitedCheckBox, bmoInsurance.getMinUnlimited());
		
		formFlexTable.addField(7, 0, maxAmountTextBox, bmoInsurance.getMaxAmount());
		formFlexTable.addField(7, 2, maxUnlimitedCheckBox, bmoInsurance.getMaxUnlimited());

		if (!newRecord) {
			TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
			tabPanel.setSize("100%", "300px");
			
			// Coberturas
			FlowPanel productCoveragePanel = new FlowPanel();
			productCoveragePanel.setSize("100%", "100%");
			ScrollPanel productCoverageScrollPanel = new ScrollPanel();
			productCoverageScrollPanel.setSize("98%", "250px");
			productCoverageScrollPanel.add(productCoveragePanel);
			UiInsuranceCoverageCheckBoxList uiInsuranceCoverageCheckBoxList = new UiInsuranceCoverageCheckBoxList(getUiParams(), productCoveragePanel, bmoInsurance);
			uiInsuranceCoverageCheckBoxList.show();
			tabPanel.add(productCoverageScrollPanel, "Coberturas");
			
			// Descuentos
			FlowPanel productDiscountPanel = new FlowPanel();
			productDiscountPanel.setSize("100%", "100%");
			ScrollPanel productDiscountScrollPanel = new ScrollPanel();
			productDiscountScrollPanel.setSize("98%", "250px");
			productDiscountScrollPanel.add(productDiscountPanel);
			UiInsuranceDiscountCheckBoxList uiInsuranceDiscountCheckBoxList = new UiInsuranceDiscountCheckBoxList(getUiParams(), productDiscountPanel, bmoInsurance);
			uiInsuranceDiscountCheckBoxList.show();
			tabPanel.add(productDiscountScrollPanel, "Descuentos");
			
			// Valores
			FlowPanel productValuablePanel = new FlowPanel();
			productValuablePanel.setSize("100%", "100%");
			ScrollPanel productValuableScrollPanel = new ScrollPanel();
			productValuableScrollPanel.setSize("98%", "250px");
			productValuableScrollPanel.add(productValuablePanel);
			UiInsuranceValuableCheckBoxList uiInsuranceValuableCheckBoxList = new UiInsuranceValuableCheckBoxList(getUiParams(), productValuablePanel, bmoInsurance);
			uiInsuranceValuableCheckBoxList.show();
			tabPanel.add(productValuableScrollPanel, "Valores");
			
			// Fondos
			FlowPanel productFundPanel = new FlowPanel();
			productFundPanel.setSize("100%", "100%");
			ScrollPanel productFundScrollPanel = new ScrollPanel();
			productFundScrollPanel.setSize("98%", "250px");
			productFundScrollPanel.add(productFundPanel);
			UiInsuranceFundCheckBoxList uiInsuranceFundCheckBoxList = new UiInsuranceFundCheckBoxList(getUiParams(), productFundPanel, bmoInsurance);
			uiInsuranceFundCheckBoxList.show();
			tabPanel.add(productFundScrollPanel, "Fondos");
			
			formFlexTable.addPanel(8, 0, tabPanel);
		}
		
		
		if (!newRecord) {
			formFlexTable.addSectionLabel(9, 0, "Copiar Producto", 2);
			
			// Botón de imprimir
			copyButton.setStyleName("formCloseButton");
			copyButton.setVisible(true);
			copyButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareCopy();
				}
			});
			
			formFlexTable.addField(10, 0, newCurrencyListBox, bmoInsurance.getCurrencyId());
			formFlexTable.addButtonCell(10, 2, copyButton);		
		}
		
		checkBoxEffects();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoInsurance.setId(id);
		bmoInsurance.getCode().setValue(codeTextBox.getText());
		bmoInsurance.getName().setValue(nameTextBox.getText());
		bmoInsurance.getDescription().setValue(descriptionTextArea.getText());
		
		bmoInsurance.getPayYears().setValue(payYearsTextBox.getText());
		
		bmoInsurance.getMaxAmount().setValue(maxAmountTextBox.getText());
		bmoInsurance.getMaxUnlimited().setValue(maxUnlimitedCheckBox.getValue());
		
		bmoInsurance.getMinAmount().setValue(minAmountTextBox.getText());
		bmoInsurance.getMinUnlimited().setValue(minUnlimitedCheckBox.getValue());
		
		bmoInsurance.getMaxAge().setValue(maxAgeTextBox.getText());
		bmoInsurance.getMinAge().setValue(minAgeTextBox.getText());
		
		bmoInsurance.getComments().setValue(commentsTextArea.getText());
		
		bmoInsurance.getGoalId().setValue(goalListBox.getSelectedId());
		bmoInsurance.getCurrencyId().setValue(currencyListBox.getSelectedId());
		bmoInsurance.getInsuranceCategoryId().setValue(productCategoryListBox.getSelectedId());
		
		return bmoInsurance;
	}
	
	@Override
	public void close() {
		UiInsuranceList uiInsuranceList = new UiInsuranceList(getUiParams());
		uiInsuranceList.show();	
	}
	
	@Override
	public void saveNext() {
		if (newRecord) { 
			UiInsuranceForm uiInsuranceForm = new UiInsuranceForm(getUiParams(), getBmObject().getId());
			uiInsuranceForm.show();
		} else {
			UiInsuranceList uiInsuranceList = new UiInsuranceList(getUiParams());
			uiInsuranceList.show();
		}		
	}
	
	@Override
	public void formBooleanChange(ValueChangeEvent<Boolean> event) {
		checkBoxEffects();
	}
	
	private void checkBoxEffects(){
		if (maxUnlimitedCheckBox.getValue()) {
			maxAmountTextBox.setText("");
			maxAmountTextBox.setEnabled(false);
		} else {
			maxAmountTextBox.setEnabled(true);
		}
		
		if (minUnlimitedCheckBox.getValue()) {
			minAmountTextBox.setText("");
			minAmountTextBox.setEnabled(false);
		} else {
			minAmountTextBox.setEnabled(true);
		}
	}
	
	private void prepareCopy(){
		String newCurrencyId = "" + newCurrencyListBox.getSelectedId();
		copyAction(newCurrencyId);
	}
	
	private void copyAction(String newCurrencyId) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				copyProcessing = false;
				showErrorMessage(this.getClass().getName() + "-copyAction() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				copyProcessing = false;
				if (result.hasErrors()) showFormMsg("Error al copiar Producto.", "El Producto no se pudo Copiar: " + result.errorsToString());
				else {
					showFormMsg("Producto Copiado Exitosamente.", "Producto Copiado Exitosamente");
					close();
				}
			}
		};

		// Llamada al servicio RPC
		try {
			if (!copyProcessing) {
				copyProcessing = true;
				getUiParams().getBmObjectServiceAsync().action(bmoInsurance.getPmClass(), bmoInsurance, BmoInsurance.ACTION_COPY, newCurrencyId, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-copyAction() ERROR: " + e.toString());
		}
	}
}
