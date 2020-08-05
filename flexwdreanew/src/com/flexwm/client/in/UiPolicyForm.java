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

import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoUser;


public class UiPolicyForm extends UiForm {
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox codeTextBox = new TextBox();
	TextBox numberTextBox = new TextBox();
	DateBox startDateBox = new DateBox();
	DateBox endDateBox = new DateBox();
	UiListBox payFrequencyListBox = new UiListBox(getUiParams());	
	UiListBox payTypeListBox = new UiListBox(getUiParams());
	UiListBox wFlowTypeListBox;
	
	TextBox tcDigitsTextBox = new TextBox();
	TextBox tcExpiryTextBox = new TextBox();
	
	TextBox premiumTextBox = new TextBox();
	TextBox amountTextBox = new TextBox();
	TextBox maxAmountTextBox = new TextBox();
	TextBox minAmountTextBox = new TextBox();
	
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox insuredSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiListBox productListBox = new UiListBox(getUiParams(), new BmoInsurance());
	
	BmoPolicy bmoPolicy;
	String code = "";
	
	public UiPolicyForm(UiParams uiParams, int id) {
		super(uiParams, new BmoPolicy(), id);
		initialize();
	}
	
	public UiPolicyForm(UiParams uiParams, Panel defaultPanel, int id) {
		super(uiParams, defaultPanel, new BmoPolicy(), id);
		initialize();
	}
	
	private void initialize(){
		bmoPolicy = (BmoPolicy)getBmObject();
		
		// Agregar filtros al tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
		wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);
		
		code = bmoPolicy.getCode().toString();
	}
	
	@Override
	public void populateFields(){
		bmoPolicy = (BmoPolicy)getBmObject();
		formFlexTable.addField(1, 0, productListBox, bmoPolicy.getInsuranceId());		
		formFlexTable.addField(1, 2, wFlowTypeListBox, bmoPolicy.getWFlowTypeId());
		
		formFlexTable.addField(2, 0, numberTextBox, bmoPolicy.getNumber());
		formFlexTable.addField(2, 2, userSuggestBox, bmoPolicy.getUserId());		
		
		formFlexTable.addField(3, 0, customerSuggestBox, bmoPolicy.getCustomerId());	
		formFlexTable.addField(3, 2, insuredSuggestBox, bmoPolicy.getInsuredId());	
		
		formFlexTable.addField(4, 0, startDateBox, bmoPolicy.getStartDate());
		formFlexTable.addField(4, 2, endDateBox, bmoPolicy.getEndDate());
		
		formFlexTable.addField(5, 0, payTypeListBox, bmoPolicy.getPayType());
		formFlexTable.addField(5, 2, typeListBox, bmoPolicy.getType());
		
		formFlexTable.addField(6, 0, payFrequencyListBox, bmoPolicy.getPayFrequency());
		formFlexTable.addField(6, 2, tcDigitsTextBox, bmoPolicy.getTcDigits());
		
		formFlexTable.addField(7, 0, premiumTextBox, bmoPolicy.getPremium());
		formFlexTable.addField(7, 2, tcExpiryTextBox, bmoPolicy.getTcExpiryDate());
		
		if (!newRecord) {
			formFlexTable.addField(8, 0, amountTextBox, bmoPolicy.getAmount());
			formFlexTable.addFieldReadOnly(8, 2, maxAmountTextBox, bmoPolicy.getBmoInsurance().getMaxAmount());
			
			formFlexTable.addLabelField(9, 0, bmoPolicy.getStatus());
			formFlexTable.addFieldReadOnly(9, 2, minAmountTextBox, bmoPolicy.getBmoInsurance().getMinAmount());	
			
			TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
			tabPanel.setSize("100%", "300px");
			
			// Coverturas
			FlowPanel policyCoveragePanel = new FlowPanel();
			policyCoveragePanel.setSize("100%", "100%");
			ScrollPanel policyCoverageScrollPanel = new ScrollPanel();
			policyCoverageScrollPanel.setSize("98%", "250px");
			policyCoverageScrollPanel.add(policyCoveragePanel);
			UiPolicyCoverage uiPolicyCoverage = new UiPolicyCoverage(getUiParams(), policyCoveragePanel, bmoPolicy);
			uiPolicyCoverage.show();
			tabPanel.add(policyCoverageScrollPanel, "Coberturas");
			
			// Beneficiarios
			FlowPanel policyRecipientPanel = new FlowPanel();
			policyRecipientPanel.setSize("100%", "100%");
			ScrollPanel policyRecipientScrollPanel = new ScrollPanel();
			policyRecipientScrollPanel.setSize("98%", "250px");
			policyRecipientScrollPanel.add(policyRecipientPanel);
			UiPolicyRecipient uiPolicyRecipient = new UiPolicyRecipient(getUiParams(), policyRecipientPanel, bmoPolicy.getId());
			uiPolicyRecipient.show();
			tabPanel.add(policyRecipientScrollPanel, "Beneficiarios");
						
			formFlexTable.addPanel(10, 0, tabPanel, 4);
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoPolicy.setId(id);
		bmoPolicy.getType().setValue(typeListBox.getSelectedCode());
		bmoPolicy.getCode().setValue(codeTextBox.getText());
		bmoPolicy.getNumber().setValue(numberTextBox.getText());
		bmoPolicy.getStartDate().setValue(startDateBox.getTextBox().getText());
		bmoPolicy.getEndDate().setValue(endDateBox.getTextBox().getText());		
		bmoPolicy.getPayFrequency().setValue(payFrequencyListBox.getSelectedCode());
		bmoPolicy.getPayType().setValue(payTypeListBox.getSelectedCode());
		bmoPolicy.getTcDigits().setValue(tcDigitsTextBox.getText());
		bmoPolicy.getTcExpiryDate().setValue(tcExpiryTextBox.getText());
		bmoPolicy.getPremium().setValue(premiumTextBox.getText());
		bmoPolicy.getAmount().setValue(amountTextBox.getText());
		bmoPolicy.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoPolicy.getCustomerId().setValue(customerSuggestBox.getSelectedId());
		bmoPolicy.getInsuredId().setValue(insuredSuggestBox.getSelectedId());
		bmoPolicy.getInsuranceId().setValue(productListBox.getSelectedId());
		bmoPolicy.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
		return bmoPolicy;
	}
	
	@Override
	public void close() {
		UiPolicyList uiPolicyList = new UiPolicyList(getUiParams());
		uiPolicyList.show();	
	}
	
	@Override
	public void saveNext() {
		if (newRecord) { 
			UiPolicyDetail uiPolicyDetail = new UiPolicyDetail(getUiParams(), getBmObject().getId());
			uiPolicyDetail.show();
		} else {
			showFormMsg("Cambios almacenados exitosamente.", "Cambios almacenados exitosamente.");
		}		
	}
}
