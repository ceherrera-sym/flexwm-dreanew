/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.op.PmRequisitionType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoContractConceptItem;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoEstimationItem;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmContractEstimation extends PmObject{
	BmoContractEstimation bmoContractEstimation;

	public PmContractEstimation(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoContractEstimation = new BmoContractEstimation();
		setBmObject(bmoContractEstimation); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoContractEstimation.getWorkContractId(), bmoContractEstimation.getBmoWorkContract()),			
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getWFlowId(), bmoContractEstimation.getBmoWorkContract().getBmoWFlow()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getWFlowTypeId(), bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getWFlowPhaseId(), bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getWFlowFunnelId(), bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoContractEstimation.getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getWorkId(), bmoContractEstimation.getBmoWorkContract().getBmoWork()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBudgetItemId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getCurrencyId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoCurrency()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getUserId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoWork().getDevelopmentPhaseId(), bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getSupplierId(), bmoContractEstimation.getBmoWorkContract().getBmoSupplier()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getSupplierCategoryId(), bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoContractEstimation.getBmoWorkContract().getCompanyId(), bmoContractEstimation.getBmoWorkContract().getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoContractEstimation = (BmoContractEstimation)autoPopulate(pmConn, new BmoContractEstimation());		

		//BmoWorkContract
		BmoWorkContract bmoWorkContract = new BmoWorkContract();
		int companyId = pmConn.getInt(bmoWorkContract.getIdFieldName());
		if (companyId > 0) bmoContractEstimation.setBmoWorkContract((BmoWorkContract) new PmWorkContract(getSFParams()).populate(pmConn));
		else bmoContractEstimation.setBmoWorkContract(bmoWorkContract);

		return bmoContractEstimation;

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoContractEstimation.getId() > 0)) {

			//Validar que la ultima estimacion este autorizada
			if(this.lastIsAutorized(pmConn, bmoContractEstimation, bmUpdateResult))
				bmUpdateResult.addError(bmoContractEstimation.getCode().getName(), "La estimación anterior no esta autorizada.");

			//Validar que no existe un consecutivo igual
			if (this.consecutiveCheck(pmConn, bmoContractEstimation, bmUpdateResult)) {
				bmUpdateResult.addError(bmoContractEstimation.getConsecutive().getName(), "Ya existe el consecutivo: " + bmoContractEstimation.getConsecutive().toInteger());
			}

			bmoContractEstimation.getPaymentStatus().setValue(BmoContractEstimation.PAYMENTSTATUS_PENDING);

			super.save(pmConn, bmoContractEstimation, bmUpdateResult);
			bmoContractEstimation.setId(bmUpdateResult.getId());
			bmoContractEstimation.getCode().setValue(bmoContractEstimation.getCodeFormat());


			//Crear los items de las estimaciones
			createItemsEstimations(pmConn, bmoContractEstimation, bmUpdateResult);

		}

		//Crear la OC en automatico si la estimación se autoriza
		if (bmoContractEstimation.getStatus().equals(BmoContractEstimation.STATUS_AUTHORIZED)) {
			createRequisition(pmConn, bmoContractEstimation, bmUpdateResult);
		} else if (bmoContractEstimation.getStatus().equals(BmoContractEstimation.STATUS_PENDING)) {
			//Validar no exista OC ligada a la estimacion
			checkEstimationInRequisition(pmConn, bmoContractEstimation, bmUpdateResult);
		}

		super.save(pmConn, bmoContractEstimation, bmUpdateResult);

		this.updateTotal(pmConn, bmoContractEstimation, bmUpdateResult);

		super.save(pmConn, bmoContractEstimation, bmUpdateResult);

		return bmUpdateResult;
	}

	public boolean lastIsAutorized(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result = false;

		sql = " SELECT * FROM contractestimations " +
				" WHERE coes_workcontractid = " + bmoContractEstimation.getWorkContractId().toInteger() +
				" AND coes_status = '" + BmoContractEstimation.STATUS_PENDING + "'" +
				" ORDER BY coes_contractestimationid ASC ";
		pmConn.doFetch(sql);
		if (pmConn.next()) { 
			result = true;        
		}

		return result;
	}

	public boolean consecutiveCheck(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		int consecutive = 0;
		String sql = "";
		sql = " SELECT COUNT(coes_consecutive) AS consecutive  FROM contractestimations " +
				" WHERE coes_workcontractid = " + bmoContractEstimation.getWorkContractId().toInteger() +
				" AND coes_consecutive = " + bmoContractEstimation.getConsecutive().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			consecutive = pmConn.getInt("consecutive");
			if (consecutive > 0) result = true;
		}

		return result;

	}

	//Crear la OC si la estimación esta autorizada
	public void createRequisition(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {

		//Validar que no exista la estimacion en una OC
		checkEstimationInRequisition(pmConn, bmoContractEstimation, bmUpdateResult);

		//Obtener el Tipo de OC
		BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
		PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
		bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.getBy(pmConn, "" + BmoRequisitionType.TYPE_CONTRACTESTIMATION, bmoRequisitionType.getType().getName());

		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisitionNew = new BmoRequisition();
		bmoRequisitionNew.getName().setValue("Est-" + bmoContractEstimation.getConsecutive().toInteger() + " " + bmoContractEstimation.getBmoWorkContract().getName().toString());
		bmoRequisitionNew.getContractEstimationId().setValue(bmoContractEstimation.getId());
		bmoRequisitionNew.getBudgetItemId().setValue(bmoContractEstimation.getBmoWorkContract().getBudgetItemId().toInteger());
		bmoRequisitionNew.getSupplierId().setValue(bmoContractEstimation.getBmoWorkContract().getSupplierId().toInteger());
		bmoRequisitionNew.getCompanyId().setValue(bmoContractEstimation.getBmoWorkContract().getCompanyId().toInteger());		
		bmoRequisitionNew.getRequisitionTypeId().setValue(bmoRequisitionType.getId());
		bmoRequisitionNew.getAreaId().setValue(getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger());		
		bmoRequisitionNew.getRequestedBy().setValue(getSFParams().getLoginInfo().getUserId());		
		bmoRequisitionNew.getRequestDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoRequisitionNew.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoRequisitionNew.getPaymentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		// TODO: Agregar moneda a la estimacion
		// Moneda sistema
		BmoCurrency bmoCurrency = new BmoCurrency();
		PmCurrency pmCurrency = new PmCurrency(getSFParams());
		bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		bmoRequisitionNew.getCurrencyId().setValue(bmoCurrency.getId());
		bmoRequisitionNew.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
		
		//Autorizar la OC en Edicion
		bmoRequisitionNew.getStatus().setValue(BmoRequisition.STATUS_EDITION);
		bmoRequisitionNew.getTaxApplies().setValue(0);

		pmRequisition.save(pmConn, bmoRequisitionNew, bmUpdateResult);


	}

	//Validar que no exista OC ligada a la estimación
	public void checkEstimationInRequisition(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {
		int countReqi = 0;
		String sql = " SELECT COUNT(*) AS countReqi FROM requisitions " +
				" WHERE reqi_contractestimationid = " + bmoContractEstimation.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) countReqi = pmConn.getInt("countReqi");

		if (countReqi > 0)	bmUpdateResult.addError(bmoContractEstimation.getStatus().getName(), "Existe un OC ligada a la estimación");
	}

	//Create los items de las estimaciones
	public void createItemsEstimations(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {

		PmWorkContract pmWorkContract = new PmWorkContract(getSFParams());
		BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn, bmoContractEstimation.getWorkContractId().toInteger());

		BmoContractConceptItem bmoContractConceptItem = new BmoContractConceptItem();

		PmEstimationItem pmEstimationItem = new PmEstimationItem(getSFParams());

		//Crear los items
		BmFilter filterContractConceptItem = new BmFilter();
		filterContractConceptItem.setValueFilter(bmoContractConceptItem.getKind(), bmoContractConceptItem.getWorkContractId(), bmoContractEstimation.getWorkContractId().toInteger());
		Iterator<BmObject> listContractConceptItem = new PmContractConceptItem(getSFParams()).list(pmConn, filterContractConceptItem).iterator();			

		while (listContractConceptItem.hasNext()) {
			bmoContractConceptItem = (BmoContractConceptItem)listContractConceptItem.next();			

			//Crear los item en la estimacion			
			BmoEstimationItem bmoEstimationItem = new BmoEstimationItem();

			bmoEstimationItem.getContractEstimationId().setValue(bmoContractEstimation.getId());
			bmoEstimationItem.getContractConceptItemId().setValue(bmoContractConceptItem.getId());
			//Obtener la cantidad anterior de la estimacion
			bmoEstimationItem.getQuantityLast().setValue(pmEstimationItem.getAcumulated(pmConn, bmoContractConceptItem, bmUpdateResult));
			bmoEstimationItem.getQuantityReceipt().setValue(bmoEstimationItem.getQuantityLast().toDouble());
			bmoEstimationItem.getQuantityTotal().setValue(bmoContractConceptItem.getQuantity().toDouble() * bmoWorkContract.getQuantity().toDouble());			
			bmoEstimationItem.getPrice().setValue(bmoContractConceptItem.getPrice().toDouble());

			pmEstimationItem.saveSimple(pmConn,bmoEstimationItem, bmUpdateResult);

		}
	}


	public void updateTotal(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {
		DecimalFormat decimal = new DecimalFormat("0.00");
		//Actualizar el total de la estimacion del contrato
		double sumEstimationItem = Double.parseDouble(decimal.format(sumEstimationItem(pmConn, bmoContractEstimation, bmUpdateResult)));



		//Obtener el contrato de obra
		PmWorkContract pmWorkContract = new PmWorkContract(getSFParams());
		BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn, bmoContractEstimation.getWorkContractId().toInteger());


		if (bmoWorkContract.getTotalReal().toDouble() > 0) {
			if ((sumEstimationItem - bmoContractEstimation.getOthersExpenses().toDouble()) > bmoWorkContract.getTotalReal().toDouble()) {
				bmUpdateResult.addError(bmoWorkContract.getTotalReal().getName(), "La suma de las estimaciones: " + (sumEstimationItem - bmoContractEstimation.getOthersExpenses().toDouble()) + 
						" es mayor al total del contrato " + bmoWorkContract.getTotalReal().toDouble());
			}
		} else {	
			if ((sumEstimationItem - bmoContractEstimation.getOthersExpenses().toDouble()) > bmoWorkContract.getTotal().toDouble()) {
				bmUpdateResult.addError(bmoWorkContract.getTotal().getName(), "La suma de las estimaciones: " + (sumEstimationItem - bmoContractEstimation.getOthersExpenses().toDouble()) + 
						" es mayor al total del contrato " + bmoWorkContract.getTotal().toDouble());
			}
		}
		bmoContractEstimation.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(sumEstimationItem));

		double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
		double tax = 0;

		if (bmoWorkContract.getHasTax().toBoolean()) tax  = sumEstimationItem * taxRate;

		bmoContractEstimation.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));

		bmoContractEstimation.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoContractEstimation.getAmount().toDouble() + tax));

		//Calcular el amortizado del anticipo
		double amortizado = 0;
		if (bmoWorkContract.getPercentDownPayment().toDouble() > 0) {
			amortizado = bmoContractEstimation.getSubTotal().toDouble() * (bmoWorkContract.getPercentDownPayment().toDouble()/100);
		}

		bmoContractEstimation.getDownPayment().setValue(SFServerUtil.roundCurrencyDecimals(amortizado));

		//Calcular el amortizado del del fondo de garantia
		double warrantyFund = 0;
		if (bmoWorkContract.getPercentGuaranteeFund().toDouble() > 0) {
			warrantyFund = bmoContractEstimation.getAmount().toDouble() * (bmoWorkContract.getPercentGuaranteeFund().toDouble()/100);			
		}

		bmoContractEstimation.getWarrantyFund().setValue(SFServerUtil.roundCurrencyDecimals(warrantyFund));



		double total = bmoContractEstimation.getSubTotal().toDouble() - (bmoContractEstimation.getWarrantyFund().toDouble() + bmoContractEstimation.getDownPayment().toDouble()) - bmoContractEstimation.getOthersExpenses().toDouble();

		bmoContractEstimation.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));

		super.save(pmConn, bmoContractEstimation, bmUpdateResult);

		//Actualizar el estatus de pago del contrato de obra
		pmWorkContract.updatePaymentStatus(pmConn, bmoWorkContract, bmUpdateResult);
	}

	//Obtener los importes de la estimacion
	private double sumEstimationItem(PmConn pmConn, BmoContractEstimation bmoContractEstimation, BmUpdateResult bmUpdateResult) throws SFException {		
		double sumImport = 0;

		String sql = " SELECT SUM(esti_subtotal) AS sumimport FROM estimationitems " +		              
				" WHERE esti_contractestimationid = " + bmoContractEstimation.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) sumImport = pmConn.getDouble("sumimport");		

		return sumImport;		
	}

	public void deleteEstimationItems(PmConn pmConn, int contractEstimationId, BmUpdateResult bmUpdateResult) throws SFException {		
		pmConn.doUpdate("DELETE FROM estimationitems WHERE esti_contractestimationid = " + contractEstimationId);			
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoContractEstimation = (BmoContractEstimation) bmObject;

		//Eliminar los EstimationItems
		deleteEstimationItems(pmConn, bmoContractEstimation.getId(), bmUpdateResult);

		super.delete(pmConn, bmObject, bmUpdateResult);

		return bmUpdateResult;
	}

}

