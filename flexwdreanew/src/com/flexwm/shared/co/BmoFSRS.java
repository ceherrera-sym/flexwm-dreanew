/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

/**
 * @author smuniz
 *
 */
public class BmoFSRS extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name,	description, daysYear, daySeven, holidays,
	vacations, celebrations, disease, badWheather, dailyQuota, vacationalPremium,
	christmasGift, paidDays, daysNonToiled, toiledDays, tDaysNonToiled, tToiledDays,
	minRiskWork, minDiseaseMaternity, minDisabilityOldness, maxRiskWork, maxDiseaseMaternity, maxDisabilityOldness,
	tImssMin, tImssMax, childGuard, infonavit, ispt, others, others1,
	sar, minLft, minImss, minChildGuard, minInfonavit, minIspt, minOthersDaysYear,
	minOthersPaidDays, minSar, maxLft, maxImss, maxChildGuard, maxInfonavit,
	maxIspt, maxOthersDaysYear, maxOtherPaidDays, maxSar, tFsrsMin, tFsrsMax;

	public static String CODE_PREFIX = "FS-";
		
	public BmoFSRS(){
		super("com.flexwm.server.co.PmFSRS", "fsrs", "fsrsid", "FSRS", "FSRS");
		
		//Campo de Datos		
		code = setField("code", "", "Código", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		//POR LEY FEDERAL DEL TRABAJO 
		daysYear = setField("daysyear", "", "Días por Año", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		daySeven = setField("dayseven", "", "Séptimo Día", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		holidays = setField("holidays", "", " Días Festivo", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		vacations = setField("vacations", "", "Vacaciones", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		celebrations = setField("celebrations", "", "Fiestas Costumbre", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		disease = setField("disease", "", "Enfermedad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		badWheather = setField("badwheather", "", "Mal Tiempo", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		dailyQuota = setField("dailyquota", "", "Cuota Diaria", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		vacationalPremium = setField("vacationalpremium", "", "Prima Vacacional", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		christmasGift = setField("christmasgift", "", "Aguinaldo", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		paidDays = setField("paiddays", "", "Días Pagados", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		daysNonToiled = setField("daysnontoiled", "", "Días no Laborados", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		toiledDays = setField("toileddays", "", "Días Por Año", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tDaysNonToiled = setField("tdaysnontoiled", "", "Total de Días Laborados", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tToiledDays = setField("ttoileddays", "", "Factor", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		//POR SEGURO SOCIAL 
		minRiskWork = setField("minriskwork", "", "Riesgos del Trabajo Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minDiseaseMaternity = setField("mindiseasematernity", "", "Enfermedad y Maternidad Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minDisabilityOldness = setField("mindisabilityoldness", "", "Invalidez, Vejez Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxRiskWork = setField("maxriskwork", "", "Riesgos del Trabajo Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxDiseaseMaternity = setField("maxdiseasematernity", "", "Enfermedad y Maternidad Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxDisabilityOldness = setField("maxdisabilityoldness", "", "Invalidez, Vejez Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tImssMin = setField("timssmin", "", "Suma Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tImssMax = setField("timssmax", "", "Suma Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		//---
		childGuard = setField("childguard", "", "Por Guarderias", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		infonavit = setField("infonavit", "", "Por Infonavit", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		ispt = setField("ispt", "", "Por Ispt", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		others = setField("others", "", "Por Otros Días por Año", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		others1 = setField("others1", "", "Por Otros Días de Pago", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		sar = setField("sar", "", "Por Sar", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		// INTEGRACION DEL FACTOR DE SALARIO REAL 
		minLft = setField("minlft", "", "Por Ley Federal del Trabajo Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minImss = setField("minimss", "", "Por Seguro Social Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minChildGuard = setField("minchildguard", "", "Por Guarderias Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minInfonavit = setField("mininfonavit", "", "Por Infonavit Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minIspt = setField("minispt", "", "Por Ispt Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minOthersDaysYear = setField("minothersdaysyear", "", "Por Días por Año Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minOthersPaidDays = setField("minotherspaiddays", "", "Por Días Pagados Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		minSar = setField("minsar", "", "Por Sar Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxLft = setField("maxlft", "", "Por Ley Federal del Trabajo Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxImss = setField("maximss", "", "Por Seguro Social Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxChildGuard = setField("maxchildguard", "", "Por Guarderias Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxInfonavit = setField("maxinfonavit", "", "Por Infonavit Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxIspt = setField("maxispt", "", "Por Ispt Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxOthersDaysYear = setField("maxothersdaysyear", "", "Por Días por Año Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxOtherPaidDays = setField("maxotherpaiddays", "", "Por Días Pagados Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maxSar = setField("maxsar", "", "Por Sar Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tFsrsMin = setField("tfsrsmin", "", "Factor Salario Real Min", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tFsrsMax = setField("tfsrsmax", "", "Factor Salario Real Max", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
	
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),				
				getDescription(),
				getTFsrsMin(),
				getTFsrsMax()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the daysYear
	 */
	public BmField getDaysYear() {
		return daysYear;
	}

	/**
	 * @param daysYear the daysYear to set
	 */
	public void setDaysYear(BmField daysYear) {
		this.daysYear = daysYear;
	}

	/**
	 * @return the daySeven
	 */
	public BmField getDaySeven() {
		return daySeven;
	}

	/**
	 * @param daySeven the daySeven to set
	 */
	public void setDaySeven(BmField daySeven) {
		this.daySeven = daySeven;
	}

	/**
	 * @return the holidays
	 */
	public BmField getHolidays() {
		return holidays;
	}

	/**
	 * @param holidays the holidays to set
	 */
	public void setHolidays(BmField holidays) {
		this.holidays = holidays;
	}

	/**
	 * @return the vacations
	 */
	public BmField getVacations() {
		return vacations;
	}

	/**
	 * @param vacations the vacations to set
	 */
	public void setVacations(BmField vacations) {
		this.vacations = vacations;
	}

	/**
	 * @return the celebrations
	 */
	public BmField getCelebrations() {
		return celebrations;
	}

	/**
	 * @param celebrations the celebrations to set
	 */
	public void setCelebrations(BmField celebrations) {
		this.celebrations = celebrations;
	}

	/**
	 * @return the disease
	 */
	public BmField getDisease() {
		return disease;
	}

	/**
	 * @param disease the disease to set
	 */
	public void setDisease(BmField disease) {
		this.disease = disease;
	}

	/**
	 * @return the badWheather
	 */
	public BmField getBadWheather() {
		return badWheather;
	}

	/**
	 * @param badWheather the badWheather to set
	 */
	public void setBadWheather(BmField badWheather) {
		this.badWheather = badWheather;
	}

	/**
	 * @return the dailyQuota
	 */
	public BmField getDailyQuota() {
		return dailyQuota;
	}

	/**
	 * @param dailyQuota the dailyQuota to set
	 */
	public void setDailyQuota(BmField dailyQuota) {
		this.dailyQuota = dailyQuota;
	}

	/**
	 * @return the vacationalPremium
	 */
	public BmField getVacationalPremium() {
		return vacationalPremium;
	}

	/**
	 * @param vacationalPremium the vacationalPremium to set
	 */
	public void setVacationalPremium(BmField vacationalPremium) {
		this.vacationalPremium = vacationalPremium;
	}

	/**
	 * @return the christmasGift
	 */
	public BmField getChristmasGift() {
		return christmasGift;
	}

	/**
	 * @param christmasGift the christmasGift to set
	 */
	public void setChristmasGift(BmField christmasGift) {
		this.christmasGift = christmasGift;
	}

	/**
	 * @return the paidDays
	 */
	public BmField getPaidDays() {
		return paidDays;
	}

	/**
	 * @param paidDays the paidDays to set
	 */
	public void setPaidDays(BmField paidDays) {
		this.paidDays = paidDays;
	}

	/**
	 * @return the daysNonToiled
	 */
	public BmField getDaysNonToiled() {
		return daysNonToiled;
	}

	/**
	 * @param daysNonToiled the daysNonToiled to set
	 */
	public void setDaysNonToiled(BmField daysNonToiled) {
		this.daysNonToiled = daysNonToiled;
	}

	/**
	 * @return the toiledDays
	 */
	public BmField getToiledDays() {
		return toiledDays;
	}

	/**
	 * @param toiledDays the toiledDays to set
	 */
	public void setToiledDays(BmField toiledDays) {
		this.toiledDays = toiledDays;
	}

	/**
	 * @return the tDaysNonToiled
	 */
	public BmField gettDaysNonToiled() {
		return tDaysNonToiled;
	}

	/**
	 * @param tDaysNonToiled the tDaysNonToiled to set
	 */
	public void settDaysNonToiled(BmField tDaysNonToiled) {
		this.tDaysNonToiled = tDaysNonToiled;
	}

	/**
	 * @return the tToiledDays
	 */
	public BmField gettToiledDays() {
		return tToiledDays;
	}

	/**
	 * @param tToiledDays the tToiledDays to set
	 */
	public void settToiledDays(BmField tToiledDays) {
		this.tToiledDays = tToiledDays;
	}

	/**
	 * @return the minRiskWork
	 */
	public BmField getMinRiskWork() {
		return minRiskWork;
	}

	/**
	 * @param minRiskWork the minRiskWork to set
	 */
	public void setMinRiskWork(BmField minRiskWork) {
		this.minRiskWork = minRiskWork;
	}

	/**
	 * @return the minDiseaseMaternity
	 */
	public BmField getMinDiseaseMaternity() {
		return minDiseaseMaternity;
	}

	/**
	 * @param minDiseaseMaternity the minDiseaseMaternity to set
	 */
	public void setMinDiseaseMaternity(BmField minDiseaseMaternity) {
		this.minDiseaseMaternity = minDiseaseMaternity;
	}

	/**
	 * @return the minDisabilityOldness
	 */
	public BmField getMinDisabilityOldness() {
		return minDisabilityOldness;
	}

	/**
	 * @param minDisabilityOldness the minDisabilityOldness to set
	 */
	public void setMinDisabilityOldness(BmField minDisabilityOldness) {
		this.minDisabilityOldness = minDisabilityOldness;
	}

	/**
	 * @return the maxRiskWork
	 */
	public BmField getMaxRiskWork() {
		return maxRiskWork;
	}

	/**
	 * @param maxRiskWork the maxRiskWork to set
	 */
	public void setMaxRiskWork(BmField maxRiskWork) {
		this.maxRiskWork = maxRiskWork;
	}

	/**
	 * @return the maxDiseaseMaternity
	 */
	public BmField getMaxDiseaseMaternity() {
		return maxDiseaseMaternity;
	}

	/**
	 * @param maxDiseaseMaternity the maxDiseaseMaternity to set
	 */
	public void setMaxDiseaseMaternity(BmField maxDiseaseMaternity) {
		this.maxDiseaseMaternity = maxDiseaseMaternity;
	}

	/**
	 * @return the maxDisabilityOldness
	 */
	public BmField getMaxDisabilityOldness() {
		return maxDisabilityOldness;
	}

	/**
	 * @param maxDisabilityOldness the maxDisabilityOldness to set
	 */
	public void setMaxDisabilityOldness(BmField maxDisabilityOldness) {
		this.maxDisabilityOldness = maxDisabilityOldness;
	}

	/**
	 * @return the tImssMin
	 */
	public BmField gettImssMin() {
		return tImssMin;
	}

	/**
	 * @param tImssMin the tImssMin to set
	 */
	public void settImssMin(BmField tImssMin) {
		this.tImssMin = tImssMin;
	}

	/**
	 * @return the tImssMax
	 */
	public BmField gettImssMax() {
		return tImssMax;
	}

	/**
	 * @param tImssMax the tImssMax to set
	 */
	public void settImssMax(BmField tImssMax) {
		this.tImssMax = tImssMax;
	}

	/**
	 * @return the childGuard
	 */
	public BmField getChildGuard() {
		return childGuard;
	}

	/**
	 * @param childGuard the childGuard to set
	 */
	public void setChildGuard(BmField childGuard) {
		this.childGuard = childGuard;
	}

	/**
	 * @return the infonavit
	 */
	public BmField getInfonavit() {
		return infonavit;
	}

	/**
	 * @param infonavit the infonavit to set
	 */
	public void setInfonavit(BmField infonavit) {
		this.infonavit = infonavit;
	}

	/**
	 * @return the ispt
	 */
	public BmField getIspt() {
		return ispt;
	}

	/**
	 * @param ispt the ispt to set
	 */
	public void setIspt(BmField ispt) {
		this.ispt = ispt;
	}

	/**
	 * @return the others
	 */
	public BmField getOthers() {
		return others;
	}

	/**
	 * @param others the others to set
	 */
	public void setOthers(BmField others) {
		this.others = others;
	}

	/**
	 * @return the others1
	 */
	public BmField getOthers1() {
		return others1;
	}

	/**
	 * @param others1 the others1 to set
	 */
	public void setOthers1(BmField others1) {
		this.others1 = others1;
	}

	/**
	 * @return the sar
	 */
	public BmField getSar() {
		return sar;
	}

	/**
	 * @param sar the sar to set
	 */
	public void setSar(BmField sar) {
		this.sar = sar;
	}

	/**
	 * @return the minLft
	 */
	public BmField getMinLft() {
		return minLft;
	}

	/**
	 * @param minLft the minLft to set
	 */
	public void setMinLft(BmField minLft) {
		this.minLft = minLft;
	}

	/**
	 * @return the minImss
	 */
	public BmField getMinImss() {
		return minImss;
	}

	/**
	 * @param minImss the minImss to set
	 */
	public void setMinImss(BmField minImss) {
		this.minImss = minImss;
	}

	/**
	 * @return the minChildGuard
	 */
	public BmField getMinChildGuard() {
		return minChildGuard;
	}

	/**
	 * @param minChildGuard the minChildGuard to set
	 */
	public void setMinChildGuard(BmField minChildGuard) {
		this.minChildGuard = minChildGuard;
	}

	/**
	 * @return the minInfonavit
	 */
	public BmField getMinInfonavit() {
		return minInfonavit;
	}

	/**
	 * @param minInfonavit the minInfonavit to set
	 */
	public void setMinInfonavit(BmField minInfonavit) {
		this.minInfonavit = minInfonavit;
	}

	/**
	 * @return the minIspt
	 */
	public BmField getMinIspt() {
		return minIspt;
	}

	/**
	 * @param minIspt the minIspt to set
	 */
	public void setMinIspt(BmField minIspt) {
		this.minIspt = minIspt;
	}

	/**
	 * @return the minOthersDaysYear
	 */
	public BmField getMinOthersDaysYear() {
		return minOthersDaysYear;
	}

	/**
	 * @param minOthersDaysYear the minOthersDaysYear to set
	 */
	public void setMinOthersDaysYear(BmField minOthersDaysYear) {
		this.minOthersDaysYear = minOthersDaysYear;
	}

	/**
	 * @return the minOthersPaidDays
	 */
	public BmField getMinOthersPaidDays() {
		return minOthersPaidDays;
	}

	/**
	 * @param minOthersPaidDays the minOthersPaidDays to set
	 */
	public void setMinOthersPaidDays(BmField minOthersPaidDays) {
		this.minOthersPaidDays = minOthersPaidDays;
	}

	/**
	 * @return the minSar
	 */
	public BmField getMinSar() {
		return minSar;
	}

	/**
	 * @param minSar the minSar to set
	 */
	public void setMinSar(BmField minSar) {
		this.minSar = minSar;
	}

	/**
	 * @return the maxLft
	 */
	public BmField getMaxLft() {
		return maxLft;
	}

	/**
	 * @param maxLft the maxLft to set
	 */
	public void setMaxLft(BmField maxLft) {
		this.maxLft = maxLft;
	}

	/**
	 * @return the maxImss
	 */
	public BmField getMaxImss() {
		return maxImss;
	}

	/**
	 * @param maxImss the maxImss to set
	 */
	public void setMaxImss(BmField maxImss) {
		this.maxImss = maxImss;
	}

	/**
	 * @return the maxChildGuard
	 */
	public BmField getMaxChildGuard() {
		return maxChildGuard;
	}

	/**
	 * @param maxChildGuard the maxChildGuard to set
	 */
	public void setMaxChildGuard(BmField maxChildGuard) {
		this.maxChildGuard = maxChildGuard;
	}

	/**
	 * @return the maxInfonavit
	 */
	public BmField getMaxInfonavit() {
		return maxInfonavit;
	}

	/**
	 * @param maxInfonavit the maxInfonavit to set
	 */
	public void setMaxInfonavit(BmField maxInfonavit) {
		this.maxInfonavit = maxInfonavit;
	}

	/**
	 * @return the maxIspt
	 */
	public BmField getMaxIspt() {
		return maxIspt;
	}

	/**
	 * @param maxIspt the maxIspt to set
	 */
	public void setMaxIspt(BmField maxIspt) {
		this.maxIspt = maxIspt;
	}

	/**
	 * @return the maxOthersDaysYear
	 */
	public BmField getMaxOthersDaysYear() {
		return maxOthersDaysYear;
	}

	/**
	 * @param maxOthersDaysYear the maxOthersDaysYear to set
	 */
	public void setMaxOthersDaysYear(BmField maxOthersDaysYear) {
		this.maxOthersDaysYear = maxOthersDaysYear;
	}

	/**
	 * @return the maxOtherPaidDays
	 */
	public BmField getMaxOtherPaidDays() {
		return maxOtherPaidDays;
	}

	/**
	 * @param maxOtherPaidDays the maxOtherPaidDays to set
	 */
	public void setMaxOtherPaidDays(BmField maxOtherPaidDays) {
		this.maxOtherPaidDays = maxOtherPaidDays;
	}

	/**
	 * @return the maxSar
	 */
	public BmField getMaxSar() {
		return maxSar;
	}

	/**
	 * @param maxSar the maxSar to set
	 */
	public void setMaxSar(BmField maxSar) {
		this.maxSar = maxSar;
	}

	/**
	 * @return the tFsrsMin
	 */
	public BmField getTFsrsMin() {
		return tFsrsMin;
	}

	/**
	 * @param tFsrsMin the tFsrsMin to set
	 */
	public void setTFsrsMin(BmField tFsrsMin) {
		this.tFsrsMin = tFsrsMin;
	}

	/**
	 * @return the tFsrsMax
	 */
	public BmField getTFsrsMax() {
		return tFsrsMax;
	}

	/**
	 * @param tFsrsMax the tFsrsMax to set
	 */
	public void setTFsrsMax(BmField tFsrsMax) {
		this.tFsrsMax = tFsrsMax;
	}

	

}
