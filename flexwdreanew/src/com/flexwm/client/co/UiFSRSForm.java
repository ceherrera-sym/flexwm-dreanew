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

import com.flexwm.shared.co.BmoFSRS;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author smuniz
 *
 */

public class UiFSRSForm extends UiForm{
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox daysYearTextBox = new TextBox();
	TextBox daySevenTextBox = new TextBox();
	TextBox holidaysTextBox = new TextBox(); 
	TextBox vacationsTextBox = new TextBox(); 
	TextBox celebrationsTextBox = new TextBox();
	TextBox diseaseTextBox = new TextBox(); 
	TextBox badWheatherTextBox = new TextBox(); 
	TextBox dailyQuotaTextBox = new TextBox(); 
	TextBox vacationalPremiumTextBox = new TextBox(); 
	TextBox christmasGiftTextBox = new TextBox(); 
	TextBox paidDaysTextBox = new TextBox(); 
	TextBox daysNonToiledTextBox = new TextBox(); 
	TextBox toiledDaysTextBox = new TextBox(); 
	TextBox tDaysNonToiledTextBox = new TextBox(); 
	TextBox tToiledDaysTextBox = new TextBox(); 
	TextBox minRiskWorkTextBox = new TextBox(); 
	TextBox minDiseaseMaternityTextBox = new TextBox(); 
	TextBox minDisabilityOldnessTextBox = new TextBox(); 
	TextBox maxRiskWorkTextBox = new TextBox(); 
	TextBox maxDiseaseMaternityTextBox = new TextBox(); 
	TextBox maxDisabilityOldnessTextBox = new TextBox(); 
	TextBox tImssMinTextBox = new TextBox(); 
	TextBox tImssMaxTextBox = new TextBox(); 
	TextBox childGuardTextBox = new TextBox(); 
	TextBox infonavitTextBox = new TextBox(); 
	TextBox isptTextBox = new TextBox(); 
	TextBox othersTextBox = new TextBox(); 
	TextBox others1TextBox = new TextBox(); 
	TextBox sarTextBox = new TextBox(); 
	TextBox minLftTextBox = new TextBox(); 
	TextBox minImssTextBox = new TextBox(); 
	TextBox minChildGuardTextBox = new TextBox(); 
	TextBox minInfonavitTextBox = new TextBox(); 
	TextBox minIsptTextBox = new TextBox(); 
	TextBox minOthersDaysYearTextBox = new TextBox(); 
	TextBox minOthersPaidDaysTextBox = new TextBox(); 
	TextBox minSarTextBox = new TextBox(); 
	TextBox maxLftTextBox = new TextBox(); 
	TextBox maxImssTextBox = new TextBox(); 
	TextBox maxChildGuardTextBox = new TextBox(); 
	TextBox maxInfonavitTextBox = new TextBox(); 
	TextBox maxIsptTextBox = new TextBox(); 
	TextBox maxOthersDaysYearTextBox = new TextBox(); 
	TextBox maxOtherPaidDaysTextBox = new TextBox(); 
	TextBox maxSarTextBox = new TextBox(); 
	TextBox tFsrsMinTextBox = new TextBox(); 
	TextBox tFsrsMaxTextBox = new TextBox();
		
	BmoFSRS bmoFSRS;
	
	public UiFSRSForm(UiParams uiParams, int id) {
		super(uiParams, new BmoFSRS(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoFSRS = (BmoFSRS)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoFSRS.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoFSRS.getName());
		formFlexTable.addField(1, 4, descriptionTextArea, bmoFSRS.getDescription());
		
		//POR LEY FEDERAL DEL TRABAJO 
		formFlexTable.addFieldEmpty(2, 0);
		formFlexTable.addFieldEmpty(2, 2);
		formFlexTable.addFieldEmpty(2, 4);
		formFlexTable.addFieldEmpty(3, 0);
		formFlexTable.addLabelCell(3, 2, "Por Ley Federal del Trabajo");
		formFlexTable.addFieldEmpty(3, 4);
		formFlexTable.addField(4, 0, daysYearTextBox, bmoFSRS.getDaysYear()); 
		formFlexTable.addField(4, 2, daySevenTextBox, bmoFSRS.getDaySeven()); 
		formFlexTable.addField(4, 4, holidaysTextBox, bmoFSRS.getHolidays()); 
		formFlexTable.addField(5, 0, vacationsTextBox, bmoFSRS.getVacations()); 
		formFlexTable.addField(5, 2, celebrationsTextBox, bmoFSRS.getCelebrations()); 
		formFlexTable.addField(5, 4, diseaseTextBox, bmoFSRS.getDisease()); 
		formFlexTable.addField(6, 0, badWheatherTextBox , bmoFSRS.getBadWheather()); 
		formFlexTable.addField(6, 2, dailyQuotaTextBox , bmoFSRS.getDailyQuota()); 
		formFlexTable.addField(6, 4, vacationalPremiumTextBox , bmoFSRS.getVacationalPremium()); 
		formFlexTable.addField(7, 0, christmasGiftTextBox , bmoFSRS.getChristmasGift()); 
		formFlexTable.addField(7, 2, paidDaysTextBox , bmoFSRS.getPaidDays()); 
		formFlexTable.addField(7, 4, daysNonToiledTextBox , bmoFSRS.getDaysNonToiled()); 
		formFlexTable.addField(8, 0, toiledDaysTextBox , bmoFSRS.getToiledDays()); 
		formFlexTable.addField(8, 2, tDaysNonToiledTextBox , bmoFSRS.gettDaysNonToiled()); 
		formFlexTable.addField(8, 4, tToiledDaysTextBox , bmoFSRS.gettToiledDays()); 
		
		//POR SEGURO SOCIAL 
		formFlexTable.addFieldEmpty(9, 0);
		formFlexTable.addFieldEmpty(9, 2);
		formFlexTable.addFieldEmpty(9, 4);
		formFlexTable.addFieldEmpty(10, 0);
		formFlexTable.addLabelCell(10, 2, "Por Seguro Social");
		formFlexTable.addFieldEmpty(10, 4);
		formFlexTable.addField(11, 0, minRiskWorkTextBox , bmoFSRS.getMinRiskWork()); 
		formFlexTable.addField(11, 2, minDiseaseMaternityTextBox , bmoFSRS.getMinDiseaseMaternity()); 
		formFlexTable.addField(11, 4, minDisabilityOldnessTextBox , bmoFSRS.getMinDisabilityOldness()); 
		formFlexTable.addField(12, 0, maxRiskWorkTextBox , bmoFSRS.getMaxRiskWork()); 
		formFlexTable.addField(12, 2, maxDiseaseMaternityTextBox , bmoFSRS.getMaxDiseaseMaternity()); 
		formFlexTable.addField(12, 4, maxDisabilityOldnessTextBox , bmoFSRS.getMaxDisabilityOldness()); 
		formFlexTable.addField(13, 0, tImssMinTextBox , bmoFSRS.gettImssMin()); 
		formFlexTable.addField(13, 2, tImssMaxTextBox , bmoFSRS.gettImssMax()); 
		formFlexTable.addFieldEmpty(13, 4);
		//---
		formFlexTable.addFieldEmpty(14, 0); 
		formFlexTable.addFieldEmpty(14, 2);
		formFlexTable.addFieldEmpty(14, 4);
		formFlexTable.addField(15, 0, childGuardTextBox , bmoFSRS.getChildGuard()); 
		formFlexTable.addField(15, 2, infonavitTextBox , bmoFSRS.getInfonavit()); 
		formFlexTable.addField(15, 4, isptTextBox , bmoFSRS.getIspt()); 
		formFlexTable.addField(16, 0, othersTextBox , bmoFSRS.getOthers()); 
		formFlexTable.addField(16, 2, others1TextBox , bmoFSRS.getOthers1()); 
		formFlexTable.addField(16, 4, sarTextBox , bmoFSRS.getSar()); 

		//INTEGRACION DEL FACTOR DE SALARIO REAL 
		formFlexTable.addFieldEmpty(17, 0); 
		formFlexTable.addFieldEmpty(17, 2);
		formFlexTable.addFieldEmpty(17, 4);
		formFlexTable.addFieldEmpty(18, 0);
		formFlexTable.addLabelCell(18, 2, "Integración Factor Salario Real");
		formFlexTable.addFieldEmpty(18, 4);
		formFlexTable.addField(19, 0, minLftTextBox , bmoFSRS.getMinLft()); 
		formFlexTable.addField(19, 2, minImssTextBox , bmoFSRS.getMinImss()); 
		formFlexTable.addField(19, 4, minChildGuardTextBox , bmoFSRS.getMinChildGuard()); 
		formFlexTable.addField(20, 0, minInfonavitTextBox , bmoFSRS.getMinInfonavit()); 
		formFlexTable.addField(20, 2, minIsptTextBox , bmoFSRS.getMinIspt()); 
		formFlexTable.addField(20, 4, minOthersDaysYearTextBox , bmoFSRS.getMinOthersDaysYear()); 
		formFlexTable.addField(21, 0, minOthersPaidDaysTextBox , bmoFSRS.getMinOthersPaidDays()); 
		formFlexTable.addField(21, 2, minSarTextBox , bmoFSRS.getMinSar()); 
		formFlexTable.addField(21, 4, maxLftTextBox , bmoFSRS.getMaxLft()); 
		formFlexTable.addField(22, 0, maxImssTextBox , bmoFSRS.getMaxImss()); 
		formFlexTable.addField(22, 2, maxChildGuardTextBox , bmoFSRS.getMaxChildGuard()); 
		formFlexTable.addField(22, 4, maxInfonavitTextBox , bmoFSRS.getMaxInfonavit()); 
		formFlexTable.addField(23, 0, maxIsptTextBox , bmoFSRS.getMaxIspt()); 
		formFlexTable.addField(23, 2, maxOthersDaysYearTextBox , bmoFSRS.getMaxOthersDaysYear()); 
		formFlexTable.addField(23, 4, maxOtherPaidDaysTextBox , bmoFSRS.getMaxOtherPaidDays()); 
		formFlexTable.addField(24, 0, maxSarTextBox , bmoFSRS.getMaxSar()); 
		formFlexTable.addField(24, 2, tFsrsMinTextBox , bmoFSRS.getTFsrsMin()); 
		formFlexTable.addField(24, 4, tFsrsMaxTextBox , bmoFSRS.getTFsrsMax());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoFSRS.setId(id);
		bmoFSRS.getCode().setValue(codeTextBox.getText());
		bmoFSRS.getName().setValue(nameTextBox.getText());
		bmoFSRS.getDescription().setValue(descriptionTextArea.getText());
		bmoFSRS.getDaysYear().setValue(daysYearTextBox.getText()); 
		bmoFSRS.getDaySeven().setValue(daySevenTextBox.getText());
		bmoFSRS.getHolidays() .setValue(holidaysTextBox.getText()); 
		bmoFSRS.getVacations().setValue(vacationsTextBox.getText()); 
		bmoFSRS.getCelebrations().setValue(celebrationsTextBox.getText()); 
		bmoFSRS.getDisease().setValue(diseaseTextBox.getText()); 
		bmoFSRS.getBadWheather().setValue(badWheatherTextBox.getText()); 
		bmoFSRS.getDailyQuota().setValue(dailyQuotaTextBox.getText()); 
		bmoFSRS.getVacationalPremium().setValue( vacationalPremiumTextBox.getText()); 
		bmoFSRS.getChristmasGift().setValue( christmasGiftTextBox.getText()); 
		bmoFSRS.getPaidDays().setValue(paidDaysTextBox.getText()); 
		bmoFSRS.getDaysNonToiled().setValue(daysNonToiledTextBox.getText()); 
		bmoFSRS.getToiledDays().setValue( toiledDaysTextBox.getText()); 
		bmoFSRS.gettDaysNonToiled().setValue(tDaysNonToiledTextBox.getText()); 
		bmoFSRS.gettToiledDays().setValue(tToiledDaysTextBox.getText()); 
		bmoFSRS.getMinRiskWork().setValue(minRiskWorkTextBox.getText()); 
		bmoFSRS.getMinDiseaseMaternity().setValue(minDiseaseMaternityTextBox.getText()); 
		bmoFSRS.getMinDisabilityOldness().setValue(minDisabilityOldnessTextBox.getText()); 
		bmoFSRS.getMaxRiskWork().setValue(maxRiskWorkTextBox.getText()); 
		bmoFSRS.getMaxDiseaseMaternity().setValue(maxDiseaseMaternityTextBox.getText()); 
		bmoFSRS.getMaxDisabilityOldness().setValue(maxDisabilityOldnessTextBox.getText()); 
		bmoFSRS.gettImssMin().setValue(tImssMinTextBox.getText()); 
		bmoFSRS.gettImssMax().setValue(tImssMaxTextBox.getText()); 
		bmoFSRS.getChildGuard().setValue(childGuardTextBox.getText()); 
		bmoFSRS.getInfonavit().setValue(infonavitTextBox.getText()); 
		bmoFSRS.getIspt().setValue(isptTextBox.getText()); 
		bmoFSRS.getOthers().setValue(othersTextBox.getText()); 
		bmoFSRS.getOthers1().setValue(others1TextBox.getText()); 
		bmoFSRS.getSar().setValue(sarTextBox.getText()); 
		bmoFSRS.getMinLft().setValue(minLftTextBox.getText()); 
		bmoFSRS.getMinImss().setValue(minImssTextBox.getText()); 
		bmoFSRS.getMinChildGuard().setValue(minChildGuardTextBox.getText()); 
		bmoFSRS.getMinInfonavit().setValue(minInfonavitTextBox.getText()); 
		bmoFSRS.getMinIspt().setValue(minIsptTextBox.getText()); 
		bmoFSRS.getMinOthersDaysYear().setValue(minOthersDaysYearTextBox.getText()); 
		bmoFSRS.getMinOthersPaidDays().setValue(minOthersPaidDaysTextBox.getText()); 
		bmoFSRS.getMinSar().setValue(minSarTextBox.getText()); 
		bmoFSRS.getMaxLft().setValue(maxLftTextBox.getText()); 
		bmoFSRS.getMaxImss().setValue(maxImssTextBox.getText()); 
		bmoFSRS.getMaxChildGuard().setValue(maxChildGuardTextBox.getText()); 
		bmoFSRS.getMaxInfonavit().setValue(maxInfonavitTextBox.getText()); 
		bmoFSRS.getMaxIspt().setValue(maxIsptTextBox.getText()); 
		bmoFSRS.getMaxOthersDaysYear().setValue(maxOthersDaysYearTextBox.getText()); 
		bmoFSRS.getMaxOtherPaidDays().setValue(maxOtherPaidDaysTextBox.getText()); 
		bmoFSRS.getMaxSar().setValue(maxSarTextBox.getText()); 
		bmoFSRS.getTFsrsMin().setValue(tFsrsMinTextBox.getText()); 
		bmoFSRS.getTFsrsMax().setValue(tFsrsMaxTextBox.getText());

		return bmoFSRS;
	}
	
	@Override
	public void close() {
		UiFSRSList uiFSRSList = new UiFSRSList(getUiParams());
		uiFSRSList.show();
	}
}


