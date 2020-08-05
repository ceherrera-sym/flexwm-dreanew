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

import com.flexwm.shared.in.BmoCoverage;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiCoverageForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	CheckBox maxAmountAppliesCheckBox = new CheckBox();
	CheckBox maxUnlimitedCheckBox = new CheckBox();
	TextBox maxAmountTextBox = new TextBox();
	CheckBox minAmountAppliesCheckBox = new CheckBox();
	CheckBox minUnlimitedCheckBox = new CheckBox();
	TextBox minAmountTextBox = new TextBox();
	TextBox maxAgeTextBox = new TextBox();
	TextBox minAgeTextBox = new TextBox();
	TextArea restrictionsTextArea = new TextArea();
	TextArea conditionsTextArea = new TextArea();
	BmoCoverage bmoCoverage;
	
	public UiCoverageForm(UiParams uiParams, int id) {
		super(uiParams, new BmoCoverage(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoCoverage = (BmoCoverage)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoCoverage.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoCoverage.getName());
		
		formFlexTable.addField(2, 0, descriptionTextArea, bmoCoverage.getDescription());
		formFlexTable.addFieldEmpty(2, 2);

		formFlexTable.addSectionLabel(2, 0, "Monto Mínimo", 2);
		formFlexTable.addField(3, 1, minAmountAppliesCheckBox, bmoCoverage.getMinAmountApplies());
		
		formFlexTable.addField(4, 0, minAmountTextBox, bmoCoverage.getMinAmount());
		formFlexTable.addField(4, 2, minUnlimitedCheckBox, bmoCoverage.getMinUnlimited());
		
		formFlexTable.addSectionLabel(5, 0, "Monto Máximo", 2);
		formFlexTable.addField(5, 1, maxAmountAppliesCheckBox, bmoCoverage.getMaxAmountApplies());
		
		formFlexTable.addField(6, 0, maxAmountTextBox, bmoCoverage.getMaxAmount());
		formFlexTable.addField(6, 2, maxUnlimitedCheckBox, bmoCoverage.getMaxUnlimited());

		formFlexTable.addField(7, 0, minAgeTextBox, bmoCoverage.getMinAge());
		formFlexTable.addField(7, 2, maxAgeTextBox, bmoCoverage.getMaxAge());
		
		formFlexTable.addField(8, 0, restrictionsTextArea, bmoCoverage.getRestrictions());
		formFlexTable.addField(8, 2, conditionsTextArea, bmoCoverage.getConditions());
		
		checkBoxEffects();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoCoverage.setId(id);
		bmoCoverage.getCode().setValue(codeTextBox.getText());
		bmoCoverage.getName().setValue(nameTextBox.getText());
		bmoCoverage.getDescription().setValue(descriptionTextArea.getText());
		
		bmoCoverage.getMaxAmount().setValue(maxAmountTextBox.getText());
		bmoCoverage.getMaxAmountApplies().setValue(maxAmountAppliesCheckBox.getValue());
		bmoCoverage.getMaxUnlimited().setValue(maxUnlimitedCheckBox.getValue());
		
		bmoCoverage.getMinAmount().setValue(minAmountTextBox.getText());
		bmoCoverage.getMinAmountApplies().setValue(minAmountAppliesCheckBox.getValue());
		bmoCoverage.getMinUnlimited().setValue(minUnlimitedCheckBox.getValue());
		
		bmoCoverage.getMaxAge().setValue(maxAgeTextBox.getText());
		bmoCoverage.getMinAge().setValue(minAgeTextBox.getText());
		
		bmoCoverage.getRestrictions().setValue(restrictionsTextArea.getText());
		bmoCoverage.getConditions().setValue(conditionsTextArea.getText());
		
		return bmoCoverage;
	}
	
	@Override
	public void close() {
		UiCoverageList uiCoverageList = new UiCoverageList(getUiParams());
		uiCoverageList.show();
	}

	@Override
	public void formBooleanChange(ValueChangeEvent<Boolean> event) {
		checkBoxEffects();
	}
	
	private void checkBoxEffects(){
		// Efectos del monto mínimo
		if (!minAmountAppliesCheckBox.getValue()) {
			minAmountTextBox.setEnabled(true);
			minUnlimitedCheckBox.setEnabled(true);
			
			// Efectos de sin limite
			if (!minUnlimitedCheckBox.getValue()) {
				minAmountTextBox.setEnabled(true);
			} else {
				minAmountTextBox.setText("");
				minAmountTextBox.setEnabled(false);
			}
			
		} else {
			minAmountTextBox.setText("");
			minAmountTextBox.setEnabled(false);
			minUnlimitedCheckBox.setValue(false);
			minUnlimitedCheckBox.setEnabled(false);
		}
		
		// Efectos del monto máximo
		if (!maxAmountAppliesCheckBox.getValue()) {
			maxAmountTextBox.setEnabled(true);
			maxUnlimitedCheckBox.setEnabled(true);
			
			// Efectos de sin limite
			if (!maxUnlimitedCheckBox.getValue()) {
				maxAmountTextBox.setEnabled(true);
			} else {
				maxAmountTextBox.setText("");
				maxAmountTextBox.setEnabled(false);
			}
			
		} else {
			maxAmountTextBox.setText("");
			maxAmountTextBox.setEnabled(false);
			maxUnlimitedCheckBox.setValue(false);
			maxUnlimitedCheckBox.setEnabled(false);
		}
	}
}
