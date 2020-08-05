/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.in.BmoGoal;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceCategory;
import com.flexwm.shared.in.BmoInsuranceCoverage;
import com.flexwm.shared.in.BmoInsuranceDiscount;
import com.flexwm.shared.in.BmoInsuranceFund;
import com.flexwm.shared.in.BmoInsuranceValuable;
import com.flexwm.shared.in.BmoPolicy;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsurance extends PmObject {
	BmoInsurance bmoInsurance = new BmoInsurance();
	
	public PmInsurance(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsurance);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoInsurance.getInsuranceCategoryId(), bmoInsurance.getBmoInsuranceCategory()),
				new PmJoin(bmoInsurance.getGoalId(), bmoInsurance.getBmoGoal()),
				new PmJoin(bmoInsurance.getCurrencyId(), bmoInsurance.getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoInsurance bmoInsurance = (BmoInsurance)autoPopulate(pmConn, new BmoInsurance());

		// BmoInsuranceCategory
		BmoInsuranceCategory bmoInsuranceCategory = new BmoInsuranceCategory();
		int InsuranceCategoryId = (int)pmConn.getInt(bmoInsuranceCategory.getIdFieldName());
		if (InsuranceCategoryId > 0) bmoInsurance.setBmoInsuranceCategory((BmoInsuranceCategory) new PmInsuranceCategory(getSFParams()).populate(pmConn));
		else bmoInsurance.setBmoInsuranceCategory(bmoInsuranceCategory);

		// BmoGoal
		BmoGoal bmoGoal = new BmoGoal();
		int GoalId = (int)pmConn.getInt(bmoGoal.getIdFieldName());
		if (GoalId > 0) bmoInsurance.setBmoGoal((BmoGoal) new PmGoal(getSFParams()).populate(pmConn));
		else bmoInsurance.setBmoGoal(bmoGoal);
		
		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int CurrencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
		if (CurrencyId > 0) bmoInsurance.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoInsurance.setBmoCurrency(bmoCurrency);
		
		return bmoInsurance;
	}
	
	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) pmConn.commit();

		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoInsurance bmoInsurance = (BmoInsurance)bmObject;
		
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoInsurance.getId() > 0)) {
			super.save(pmConn, bmoInsurance, bmUpdateResult);
			bmoInsurance.setId(bmUpdateResult.getId());
			
			// Establecer clave
			bmoInsurance.getCode().setValue(bmoInsurance.getCodeFormat());
		}
		
		super.save(pmConn, bmoInsurance, bmUpdateResult);
		
		return bmUpdateResult;
	}
	
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException{
		
		if (action.equalsIgnoreCase(BmoInsurance.ACTION_COPY)) copyInsurance((BmoInsurance)bmObject, bmUpdateResult, value);
		
		return bmUpdateResult;
	}
	
	// Crear un nuevo producto de seguro a partir de otro
	public void copyInsurance(BmoInsurance bmoFromInsurance, BmUpdateResult bmUpdateResult, String value) throws SFException{
		BmoInsurance bmoToInsurance = new BmoInsurance();
		int toInsuranceId;
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();
			
			//Obtener informacion de la moneda nueva
			PmCurrency pmCurrency = new PmCurrency(getSFParams());
			BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(Integer.parseInt(value));

			// Copiar datos directos
			bmoToInsurance.getName().setValue(bmoFromInsurance.getName().toString() + "_" + bmoCurrency.getCode().toString());
			bmoToInsurance.getDescription().setValue(bmoFromInsurance.getDescription().toString() + ", " + bmoCurrency.getName().toString());
			bmoToInsurance.getComments().setValue(bmoFromInsurance.getComments().toString());
			bmoToInsurance.getMaxUnlimited().setValue(bmoFromInsurance.getMaxUnlimited().toString());
			bmoToInsurance.getMaxAmount().setValue(bmoFromInsurance.getMaxAmount().toString());
			bmoToInsurance.getMinUnlimited().setValue(bmoFromInsurance.getMinUnlimited().toString());
			bmoToInsurance.getMinAmount().setValue(bmoFromInsurance.getMinAmount().toString());
			bmoToInsurance.getMaxAge().setValue(bmoFromInsurance.getMaxAge().toString());
			bmoToInsurance.getMinAge().setValue(bmoFromInsurance.getMinAge().toString());
			bmoToInsurance.getPayYears().setValue(bmoFromInsurance.getPayYears().toString());
			bmoToInsurance.getInsuranceCategoryId().setValue(bmoFromInsurance.getInsuranceCategoryId().toString());
			bmoToInsurance.getGoalId().setValue(bmoFromInsurance.getGoalId().toString());
			
			// La moneda se asigna del valor de la accion
			if (value.equals(bmoFromInsurance.getCurrencyId().toString())) 
				bmUpdateResult.addError(bmoToInsurance.getCode().getName(), "La Moneda debe ser distinta al Producto Original.");
			bmoToInsurance.getCurrencyId().setValue(value);
			
			// Se almacena el nuevo producto y se obtiene el ID
			bmUpdateResult = this.save(pmConn, bmoToInsurance, bmUpdateResult);
			toInsuranceId = bmUpdateResult.getId();
			
			// Si no hay errores continuar
			if (!bmUpdateResult.hasErrors()){
				// Crear coberturas
				copyCoverages(pmConn, bmUpdateResult, toInsuranceId, bmoFromInsurance);
	
				// Crear descuentos
				copyDiscounts(pmConn, bmUpdateResult, toInsuranceId, bmoFromInsurance);
				
				// Crear valores
				copyValuables(pmConn, bmUpdateResult, toInsuranceId, bmoFromInsurance);
				
				// Crear fondos
				copyFunds(pmConn, bmUpdateResult, toInsuranceId, bmoFromInsurance);
			}
			
			
			// Reasignar el id creado del nuevo producto de seguro
			bmUpdateResult.setId(toInsuranceId);
			
			if (!bmUpdateResult.hasErrors()) pmConn.commit();
		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
	}
	
	// Copiar coberturas del producto de seguro
	private void copyCoverages(PmConn pmConn, BmUpdateResult bmUpdateResult, int toInsuranceId, BmoInsurance bmoFromInsurance) throws SFException {
		BmoInsuranceCoverage bmoInsuranceCoverage = new BmoInsuranceCoverage();
		PmInsuranceCoverage pmInsuranceCoverage = new PmInsuranceCoverage(getSFParams());
		BmFilter filterInsurance = new BmFilter();
		filterInsurance.setValueFilter(bmoInsuranceCoverage.getKind(), bmoInsuranceCoverage.getInsuranceId(), bmoFromInsurance.getId());
		
		ArrayList<BmObject> insuranceCoverageList = pmInsuranceCoverage.list(pmConn, filterInsurance); 
		Iterator<BmObject> insuranceCoverageListIterator = insuranceCoverageList.iterator();
		
		// Copiar cada cobertura
		while (insuranceCoverageListIterator.hasNext()) {
			BmoInsuranceCoverage bmoToInsuranceCoverage = new BmoInsuranceCoverage();
			BmoInsuranceCoverage bmoFromInsuranceCoverage = (BmoInsuranceCoverage)insuranceCoverageListIterator.next();
			
			bmoToInsuranceCoverage.getInsuranceId().setValue(toInsuranceId);
			bmoToInsuranceCoverage.getCoverageId().setValue(bmoFromInsuranceCoverage.getCoverageId().toInteger());
			
			pmInsuranceCoverage.save(pmConn, bmoToInsuranceCoverage, bmUpdateResult);
		}
	}
	
	// Copiar descuentos del producto de seguro
	private void copyDiscounts(PmConn pmConn, BmUpdateResult bmUpdateResult, int toInsuranceId, BmoInsurance bmoFromInsurance) throws SFException {
		BmoInsuranceDiscount bmoInsuranceDiscount = new BmoInsuranceDiscount();
		PmInsuranceDiscount pmInsuranceDiscount = new PmInsuranceDiscount(getSFParams());
		BmFilter filterInsurance = new BmFilter();
		filterInsurance.setValueFilter(bmoInsuranceDiscount.getKind(), bmoInsuranceDiscount.getInsuranceId(), bmoFromInsurance.getId());
		
		ArrayList<BmObject> insuranceDiscountList = pmInsuranceDiscount.list(pmConn, filterInsurance); 
		Iterator<BmObject> insuranceDiscountListIterator = insuranceDiscountList.iterator();
		
		// Copiar cada cobertura
		while (insuranceDiscountListIterator.hasNext()) {
			BmoInsuranceDiscount bmoToInsuranceDiscount = new BmoInsuranceDiscount();
			BmoInsuranceDiscount bmoFromInsuranceDiscount = (BmoInsuranceDiscount)insuranceDiscountListIterator.next();
			
			bmoToInsuranceDiscount.getInsuranceId().setValue(toInsuranceId);
			bmoToInsuranceDiscount.getDiscountId().setValue(bmoFromInsuranceDiscount.getDiscountId().toInteger());
			
			pmInsuranceDiscount.save(pmConn, bmoToInsuranceDiscount, bmUpdateResult);
		}
	}
	
	// Copiar valores del producto de seguro
	private void copyValuables(PmConn pmConn, BmUpdateResult bmUpdateResult, int toInsuranceId, BmoInsurance bmoFromInsurance) throws SFException {
		BmoInsuranceValuable bmoInsuranceValuable = new BmoInsuranceValuable();
		PmInsuranceValuable pmInsuranceValuable = new PmInsuranceValuable(getSFParams());
		BmFilter filterInsurance = new BmFilter();
		filterInsurance.setValueFilter(bmoInsuranceValuable.getKind(), bmoInsuranceValuable.getInsuranceId(), bmoFromInsurance.getId());
		
		ArrayList<BmObject> insuranceValuableList = pmInsuranceValuable.list(pmConn, filterInsurance); 
		Iterator<BmObject> insuranceValuableListIterator = insuranceValuableList.iterator();
		
		// Copiar cada cobertura
		while (insuranceValuableListIterator.hasNext()) {
			BmoInsuranceValuable bmoToInsuranceValuable = new BmoInsuranceValuable();
			BmoInsuranceValuable bmoFromInsuranceValuable = (BmoInsuranceValuable)insuranceValuableListIterator.next();
			
			bmoToInsuranceValuable.getInsuranceId().setValue(toInsuranceId);
			bmoToInsuranceValuable.getValuableId().setValue(bmoFromInsuranceValuable.getValuableId().toInteger());
			
			pmInsuranceValuable.save(pmConn, bmoToInsuranceValuable, bmUpdateResult);
		}
	}
	
	// Copiar fondos del producto de seguro
	private void copyFunds(PmConn pmConn, BmUpdateResult bmUpdateResult, int toInsuranceId, BmoInsurance bmoFromInsurance) throws SFException {
		BmoInsuranceFund bmoInsuranceFund = new BmoInsuranceFund();
		PmInsuranceFund pmInsuranceFund = new PmInsuranceFund(getSFParams());
		BmFilter filterInsurance = new BmFilter();
		filterInsurance.setValueFilter(bmoInsuranceFund.getKind(), bmoInsuranceFund.getInsuranceId(), bmoFromInsurance.getId());
		
		ArrayList<BmObject> insuranceFundList = pmInsuranceFund.list(pmConn, filterInsurance); 
		Iterator<BmObject> insuranceFundListIterator = insuranceFundList.iterator();
		
		// Copiar cada cobertura
		while (insuranceFundListIterator.hasNext()) {
			BmoInsuranceFund bmoToInsuranceFund = new BmoInsuranceFund();
			BmoInsuranceFund bmoFromInsuranceFund = (BmoInsuranceFund)insuranceFundListIterator.next();
			
			bmoToInsuranceFund.getInsuranceId().setValue(toInsuranceId);
			bmoToInsuranceFund.getFundId().setValue(bmoFromInsuranceFund.getFundId().toInteger());
			
			pmInsuranceFund.save(pmConn, bmoToInsuranceFund, bmUpdateResult);
		}
	}
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoInsurance = (BmoInsurance)bmObject;

		// Revisar si hay polizas ligadas
		PmPolicy pmPolicy = new PmPolicy(getSFParams());
		BmoPolicy bmoPolicy = new BmoPolicy();
		BmFilter filterByInsurance = new BmFilter();
		filterByInsurance.setValueFilter(bmoPolicy.getKind(), bmoPolicy.getInsuranceId(), bmoInsurance.getId());
		if (pmPolicy.list(pmConn, filterByInsurance).size() > 0)
			bmUpdateResult.addMsg("No se puede eliminar el Producto -  tiene Pólizas Ligadas.");

		if (!bmUpdateResult.hasErrors()) {
			pmConn.doUpdate("DELETE FROM insurancecoverages WHERE incv_insuranceid = " + bmoInsurance.getId());
			pmConn.doUpdate("DELETE FROM insurancediscounts WHERE indi_insuranceid = " + bmoInsurance.getId());
			pmConn.doUpdate("DELETE FROM insurancevaluables WHERE inva_insuranceid = " + bmoInsurance.getId());
			pmConn.doUpdate("DELETE FROM insurancefunds WHERE infu_insuranceid = " + bmoInsurance.getId());

			super.delete(pmConn, bmoInsurance, bmUpdateResult);
		}

		return bmUpdateResult;
	}
}




