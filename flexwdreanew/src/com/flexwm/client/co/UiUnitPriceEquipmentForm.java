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

import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceEquipment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author smuniz
 *
 */


public class UiUnitPriceEquipmentForm extends UiForm{
	TextBox acquisitionValueTextBox = new TextBox();
	TextBox tiresValueTextBox = new TextBox();
	TextBox specialsValueTextBox = new TextBox();
	TextBox netValueTextBox = new TextBox();
	TextBox rescueFactorTextBox = new TextBox();
	TextBox rescueValueTextBox = new TextBox();
	TextBox interestRateTextBox = new TextBox();
	TextBox insuranceTextBox = new TextBox();
	TextBox maintenanceTextBox = new TextBox();
	TextBox lifeTextBox = new TextBox();
	TextBox specialsLifeTextBox = new TextBox();
	TextBox yearWorkTextBox = new TextBox();
	TextBox potencyTextBox = new TextBox();
	UiListBox fuelTypeTextBox = new UiListBox(getUiParams());
	TextArea fuelOtherTextArea = new TextArea();
	TextBox fuelPriceTextBox = new TextBox();
	TextBox oilPriceTextBox = new TextBox();
	TextBox tiresLifeTextBox = new TextBox();
	TextBox fuelTextBox = new TextBox();
	TextBox oilTextBox = new TextBox();
	TextBox wageTabTextBox = new TextBox();
	TextBox fsrTextBox = new TextBox();
	TextBox wagePriceTextBox = new TextBox();
	TextBox workTurnTextBox = new TextBox();
	UiSuggestBox unitPriceSuggestBox = new UiSuggestBox(new BmoUnitPrice());
	
	BmoUnitPriceEquipment bmoUnitPriceEquipment;
	
	public UiUnitPriceEquipmentForm(UiParams uiParams, int id) {
		super(uiParams, new BmoUnitPriceEquipment(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoUnitPriceEquipment = (BmoUnitPriceEquipment)getBmObject();
		
		formFlexTable.addField(1, 0, unitPriceSuggestBox, bmoUnitPriceEquipment.getUnitPriceId());
		formFlexTable.addField(1, 2, acquisitionValueTextBox, bmoUnitPriceEquipment.getAcquisitionValue());
		formFlexTable.addField(2, 0, tiresValueTextBox, bmoUnitPriceEquipment.getTiresValue());
		formFlexTable.addField(2, 2, specialsValueTextBox, bmoUnitPriceEquipment.getSpecialsValue());
		formFlexTable.addField(3, 0, netValueTextBox, bmoUnitPriceEquipment.getNetValue());
		formFlexTable.addField(3, 2, rescueFactorTextBox, bmoUnitPriceEquipment.getRescueFactor());
		formFlexTable.addField(4, 0, rescueValueTextBox, bmoUnitPriceEquipment.getRescueValue());
		formFlexTable.addField(4, 2, interestRateTextBox, bmoUnitPriceEquipment.getInterestRate());
		formFlexTable.addField(5, 0, insuranceTextBox, bmoUnitPriceEquipment.getInsurance());
		formFlexTable.addField(5, 2, maintenanceTextBox, bmoUnitPriceEquipment.getMaintenance());
		formFlexTable.addField(6, 0, lifeTextBox, bmoUnitPriceEquipment.getLife());
		formFlexTable.addField(6, 2, specialsLifeTextBox, bmoUnitPriceEquipment.getSpecialsLife());
		formFlexTable.addField(7, 0, yearWorkTextBox, bmoUnitPriceEquipment.getYearWork());
		formFlexTable.addField(7, 2, potencyTextBox, bmoUnitPriceEquipment.getPotency());
		formFlexTable.addField(8, 0, fuelTypeTextBox, bmoUnitPriceEquipment.getFuelType());
		formFlexTable.addField(8, 2, fuelOtherTextArea, bmoUnitPriceEquipment.getFuelOther());
		formFlexTable.addField(9, 0, fuelPriceTextBox, bmoUnitPriceEquipment.getFuelPrice());
		formFlexTable.addField(9, 2, oilPriceTextBox, bmoUnitPriceEquipment.getOilPrice());
		formFlexTable.addField(10, 0, tiresLifeTextBox, bmoUnitPriceEquipment.getTiresLife());
		formFlexTable.addField(10, 2, fuelTextBox, bmoUnitPriceEquipment.getFuel());
		formFlexTable.addField(11, 0, oilTextBox, bmoUnitPriceEquipment.getOil());
		formFlexTable.addField(11, 2, wageTabTextBox, bmoUnitPriceEquipment.getWageTab());
		formFlexTable.addField(12, 0, fsrTextBox, bmoUnitPriceEquipment.getFsr());
		formFlexTable.addField(12, 2, wagePriceTextBox, bmoUnitPriceEquipment.getWagePrice());
		formFlexTable.addField(13, 0, workTurnTextBox, bmoUnitPriceEquipment.getWorkTurn());
		
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoUnitPriceEquipment.setId(id);
		bmoUnitPriceEquipment.getUnitPriceId().setValue(unitPriceSuggestBox.getSelectedId());
		bmoUnitPriceEquipment.getAcquisitionValue().setValue(acquisitionValueTextBox.getText());
		bmoUnitPriceEquipment.getTiresValue().setValue(tiresValueTextBox.getText());
		bmoUnitPriceEquipment.getSpecialsValue().setValue(specialsValueTextBox.getText());
		bmoUnitPriceEquipment.getNetValue().setValue(netValueTextBox.getText());
		bmoUnitPriceEquipment.getRescueFactor().setValue(rescueFactorTextBox.getText());
		bmoUnitPriceEquipment.getRescueValue().setValue(rescueValueTextBox.getText());
		bmoUnitPriceEquipment.getInterestRate().setValue(interestRateTextBox.getText());
		bmoUnitPriceEquipment.getInsurance().setValue(insuranceTextBox.getText());
		bmoUnitPriceEquipment.getMaintenance().setValue(maintenanceTextBox.getText());
		bmoUnitPriceEquipment.getLife().setValue(lifeTextBox.getText());
		bmoUnitPriceEquipment.getSpecialsLife().setValue(specialsLifeTextBox.getText());
		bmoUnitPriceEquipment.getYearWork().setValue(yearWorkTextBox.getText());
		bmoUnitPriceEquipment.getPotency().setValue(potencyTextBox.getText());
		bmoUnitPriceEquipment.getFuelType().setValue(fuelTypeTextBox.getSelectedCode());
		bmoUnitPriceEquipment.getFuelOther().setValue(fuelOtherTextArea.getText());
		bmoUnitPriceEquipment.getFuelPrice().setValue(fuelPriceTextBox.getText());
		bmoUnitPriceEquipment.getOilPrice().setValue(oilPriceTextBox.getText());
		bmoUnitPriceEquipment.getTiresLife().setValue(tiresLifeTextBox.getText());
		bmoUnitPriceEquipment.getFuel().setValue(fuelTextBox.getText());
		bmoUnitPriceEquipment.getOil().setValue(oilTextBox.getText());
		bmoUnitPriceEquipment.getWageTab().setValue(wageTabTextBox.getText());
		bmoUnitPriceEquipment.getFsr().setValue(fsrTextBox.getText());
		bmoUnitPriceEquipment.getWagePrice().setValue(wagePriceTextBox.getText());
		bmoUnitPriceEquipment.getWorkTurn().setValue(workTurnTextBox.getText());
		
		return bmoUnitPriceEquipment;
	}
	
	@Override
	public void close() {
		UiUnitPriceEquipmentList uiUnitPriceEquipmentList = new UiUnitPriceEquipmentList(getUiParams());
		uiUnitPriceEquipmentList.show();
	}
}


