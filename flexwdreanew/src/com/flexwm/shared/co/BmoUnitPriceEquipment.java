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

import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

/**
 * @author smuniz
 *
 */
public class BmoUnitPriceEquipment extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField unitPriceId, acquisitionValue, tiresValue, specialsValue, netValue, rescueFactor, rescueValue, interestRate, insurance, 
	maintenance, life, specialsLife, yearWork, potency, fuelType, fuelOther, fuelPrice,	oilPrice, tiresLife, fuel, oil, wageTab, fsr, wagePrice, workTurn;
	
	private BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
	public static final char FUEL_GAS = 'G';
	public static final char FUEL_DIESEL = 'D';
	
	public BmoUnitPriceEquipment(){
		super("com.flexwm.server.co.PmUnitPriceEquipment", "unitpriceequipments", "unitpriceequipmentid", "UPEQ", "Precios Unitarios de Equipos-");
		
		//Campo de Datos
		unitPriceId = setField("unitpriceid", "", "Insumos", 8, Types.INTEGER, false, BmFieldType.ID, false);
		acquisitionValue = setField("acquisitionvalue", "", "Valor de Adquisición", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tiresValue = setField("tiresvalue", "", "Valor de Llantas", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		specialsValue = setField("specialsvalue", "", "Valor de Piezas Especiales", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		netValue = setField("netvalue", "", "Valor Neto", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		rescueFactor = setField("rescuefactor", "", "Factor de Rescate", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		rescueValue = setField("rescuevalue", "", "Valor de Rescate", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		interestRate = setField("interestrate", "", "Tasa de Interés", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		insurance = setField("insurance", "", "Prima de Seguro", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		maintenance = setField("maintenance", "", "Factor de Mantenimiento", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		life = setField("life", "", "Vida Económica", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		specialsLife = setField("specialslife", "", "Vida Ecomonica Piezas Especiales", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		yearWork = setField("yearwork", "", "Tiempo Trabajado Por Año", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		potency = setField("potency", "", "Potencia", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		fuelType = setField("fueltype", "", "Tipo de Combustible", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		fuelType.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(FUEL_GAS, "Gasolina", "./icons/upeq_fuelType_fuelGas.png"),
				new BmFieldOption(FUEL_DIESEL, "Diesel", "./icons/upeq_fuelType_fuelDiesel.png")
				)));
		fuelOther = setField("fuelother", "", "Otro Cumbustible", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		fuelPrice = setField("fuelprice", "", "Valor Combustible", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		oilPrice = setField("oilprice", "", "Precio Aceite", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		tiresLife = setField("tireslife", "", "Vida Ecominica de Llantas", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		fuel = setField("fuel", "", "Cantidad de Combustible", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		oil = setField("oil", "", "Cantidad de Aceite", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		wageTab = setField("wagetab", "", "Salario Tabulado", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		fsr = setField("fsr", "", "Factor de Salario Real", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		wagePrice = setField("wageprice", "", "Salario Real de Operacion", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		workTurn = setField("workturn", "", "Horas Efectivas por Turno de Trabajo", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);

	}


	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoUnitPrice.getCode(),
				getAcquisitionValue(),
				getNetValue(),
				getFuelType(),
				getFuelPrice(),
				getFuel(),
				getOil()
				));
		
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getFuelType()), 
				new BmSearchField(bmoUnitPrice.getCode()), 
				new BmSearchField(getFuelOther())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}


	/**
	 * @return the unitPriceId
	 */
	public BmField getUnitPriceId() {
		return unitPriceId;
	}


	/**
	 * @param unitPriceId the unitPriceId to set
	 */
	public void setUnitPriceId(BmField unitPriceId) {
		this.unitPriceId = unitPriceId;
	}


	/**
	 * @return the acquisitionValue
	 */
	public BmField getAcquisitionValue() {
		return acquisitionValue;
	}


	/**
	 * @param acquisitionValue the acquisitionValue to set
	 */
	public void setAcquisitionValue(BmField acquisitionValue) {
		this.acquisitionValue = acquisitionValue;
	}


	/**
	 * @return the tiresValue
	 */
	public BmField getTiresValue() {
		return tiresValue;
	}


	/**
	 * @param tiresValue the tiresValue to set
	 */
	public void setTiresValue(BmField tiresValue) {
		this.tiresValue = tiresValue;
	}


	/**
	 * @return the specialsValue
	 */
	public BmField getSpecialsValue() {
		return specialsValue;
	}


	/**
	 * @param specialsValue the specialsValue to set
	 */
	public void setSpecialsValue(BmField specialsValue) {
		this.specialsValue = specialsValue;
	}


	/**
	 * @return the netValue
	 */
	public BmField getNetValue() {
		return netValue;
	}


	/**
	 * @param netValue the netValue to set
	 */
	public void setNetValue(BmField netValue) {
		this.netValue = netValue;
	}


	/**
	 * @return the rescueFactor
	 */
	public BmField getRescueFactor() {
		return rescueFactor;
	}


	/**
	 * @param rescueFactor the rescueFactor to set
	 */
	public void setRescueFactor(BmField rescueFactor) {
		this.rescueFactor = rescueFactor;
	}


	/**
	 * @return the rescueValue
	 */
	public BmField getRescueValue() {
		return rescueValue;
	}


	/**
	 * @param rescueValue the rescueValue to set
	 */
	public void setRescueValue(BmField rescueValue) {
		this.rescueValue = rescueValue;
	}


	/**
	 * @return the interestRate
	 */
	public BmField getInterestRate() {
		return interestRate;
	}


	/**
	 * @param interestRate the interestRate to set
	 */
	public void setInterestRate(BmField interestRate) {
		this.interestRate = interestRate;
	}


	/**
	 * @return the insurance
	 */
	public BmField getInsurance() {
		return insurance;
	}


	/**
	 * @param insurance the insurance to set
	 */
	public void setInsurance(BmField insurance) {
		this.insurance = insurance;
	}


	/**
	 * @return the maintenance
	 */
	public BmField getMaintenance() {
		return maintenance;
	}


	/**
	 * @param maintenance the maintenance to set
	 */
	public void setMaintenance(BmField maintenance) {
		this.maintenance = maintenance;
	}


	/**
	 * @return the life
	 */
	public BmField getLife() {
		return life;
	}


	/**
	 * @param life the life to set
	 */
	public void setLife(BmField life) {
		this.life = life;
	}


	/**
	 * @return the specialsLife
	 */
	public BmField getSpecialsLife() {
		return specialsLife;
	}


	/**
	 * @param specialsLife the specialsLife to set
	 */
	public void setSpecialsLife(BmField specialsLife) {
		this.specialsLife = specialsLife;
	}


	/**
	 * @return the yearWork
	 */
	public BmField getYearWork() {
		return yearWork;
	}


	/**
	 * @param yearWork the yearWork to set
	 */
	public void setYearWork(BmField yearWork) {
		this.yearWork = yearWork;
	}


	/**
	 * @return the potency
	 */
	public BmField getPotency() {
		return potency;
	}


	/**
	 * @param potency the potency to set
	 */
	public void setPotency(BmField potency) {
		this.potency = potency;
	}


	/**
	 * @return the fuelType
	 */
	public BmField getFuelType() {
		return fuelType;
	}


	/**
	 * @param fuelType the fuelType to set
	 */
	public void setFuelType(BmField fuelType) {
		this.fuelType = fuelType;
	}


	/**
	 * @return the fuelOther
	 */
	public BmField getFuelOther() {
		return fuelOther;
	}


	/**
	 * @param fuelOther the fuelOther to set
	 */
	public void setFuelOther(BmField fuelOther) {
		this.fuelOther = fuelOther;
	}


	/**
	 * @return the fuelPrice
	 */
	public BmField getFuelPrice() {
		return fuelPrice;
	}


	/**
	 * @param fuelPrice the fuelPrice to set
	 */
	public void setFuelPrice(BmField fuelPrice) {
		this.fuelPrice = fuelPrice;
	}


	/**
	 * @return the oilPrice
	 */
	public BmField getOilPrice() {
		return oilPrice;
	}


	/**
	 * @param oilPrice the oilPrice to set
	 */
	public void setOilPrice(BmField oilPrice) {
		this.oilPrice = oilPrice;
	}


	/**
	 * @return the tiresLife
	 */
	public BmField getTiresLife() {
		return tiresLife;
	}


	/**
	 * @param tiresLife the tiresLife to set
	 */
	public void setTiresLife(BmField tiresLife) {
		this.tiresLife = tiresLife;
	}


	/**
	 * @return the fuel
	 */
	public BmField getFuel() {
		return fuel;
	}


	/**
	 * @param fuel the fuel to set
	 */
	public void setFuel(BmField fuel) {
		this.fuel = fuel;
	}


	/**
	 * @return the oil
	 */
	public BmField getOil() {
		return oil;
	}


	/**
	 * @param oil the oil to set
	 */
	public void setOil(BmField oil) {
		this.oil = oil;
	}


	/**
	 * @return the wageTab
	 */
	public BmField getWageTab() {
		return wageTab;
	}


	/**
	 * @param wageTab the wageTab to set
	 */
	public void setWageTab(BmField wageTab) {
		this.wageTab = wageTab;
	}


	/**
	 * @return the fsr
	 */
	public BmField getFsr() {
		return fsr;
	}


	/**
	 * @param fsr the fsr to set
	 */
	public void setFsr(BmField fsr) {
		this.fsr = fsr;
	}


	/**
	 * @return the wagePrice
	 */
	public BmField getWagePrice() {
		return wagePrice;
	}


	/**
	 * @param wagePrice the wagePrice to set
	 */
	public void setWagePrice(BmField wagePrice) {
		this.wagePrice = wagePrice;
	}


	/**
	 * @return the workTurn
	 */
	public BmField getWorkTurn() {
		return workTurn;
	}


	/**
	 * @param workTurn the workTurn to set
	 */
	public void setWorkTurn(BmField workTurn) {
		this.workTurn = workTurn;
	}


	/**
	 * @return the bmoUnitPrice
	 */
	public BmoUnitPrice getBmoUnitPrice() {
		return bmoUnitPrice;
	}


	/**
	 * @param bmoUnitPrice the bmoUnitPrice to set
	 */
	public void setBmoUnitPrice(BmoUnitPrice bmoUnitPrice) {
		this.bmoUnitPrice = bmoUnitPrice;
	}
	
	
}
