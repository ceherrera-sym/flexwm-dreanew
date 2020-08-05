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

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.co.BmoContractConceptItem;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoEstimationItem;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmEstimationItem extends PmObject{
	BmoEstimationItem bmoEstimationItem;


	public PmEstimationItem(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoEstimationItem = new BmoEstimationItem();
		setBmObject(bmoEstimationItem); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoEstimationItem.getContractEstimationId(), bmoEstimationItem.getBmoContractEstimation()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getWorkContractId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract()),
				new PmJoin( bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getWFlowId(),  bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow()),
				new PmJoin( bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getWFlowTypeId(),  bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getBmoWFlowType()),
				new PmJoin( bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getWFlowPhaseId(),  bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin( bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getWFlowFunnelId(),  bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin( bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(),  bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getWorkId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork()),				
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getUserId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getDevelopmentPhaseId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBudgetItemId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getSupplierId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoSupplier()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoSupplier().getSupplierCategoryId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getCompanyId(), bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getBmoCompany()),
				new PmJoin(bmoEstimationItem.getContractConceptItemId(), bmoEstimationItem.getBmoContractConceptItem()),
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getWorkItemId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem()),				
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getUnitPriceId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice()),
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getUnitId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getBmoUnit()),				
				new PmJoin(bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getCurrencyId(), bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getBmoCurrency())
				)));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoEstimationItem());

		bmoEstimationItem = (BmoEstimationItem)autoPopulate(pmConn, new BmoEstimationItem());		

		//BmoContractConcept
		BmoContractConceptItem bmoContractConceptItem = new BmoContractConceptItem();
		int contractConceptItemId = pmConn.getInt(bmoContractConceptItem.getIdFieldName());
		if (contractConceptItemId > 0) bmoEstimationItem.setBmoContractConceptItem((BmoContractConceptItem) new PmContractConceptItem(getSFParams()).populate(pmConn));
		else bmoEstimationItem.setBmoContractConceptItem(bmoContractConceptItem);

		//BmoContractEstimation
		BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
		int contractEstimationId = pmConn.getInt(bmoContractEstimation.getIdFieldName());
		if (contractEstimationId > 0) bmoEstimationItem.setBmoContractEstimation((BmoContractEstimation) new PmContractEstimation(getSFParams()).populate(pmConn));
		else bmoEstimationItem.setBmoContractEstimation(bmoContractEstimation);

		return bmoEstimationItem;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoEstimationItem bmoEstimationItem = (BmoEstimationItem)bmObject;

		PmContractEstimation pmContractEstimation = new PmContractEstimation(getSFParams());
		BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(pmConn, bmoEstimationItem.getContractEstimationId().toInteger());


		//Calcular el importe de la estimacion y Calcular el total de lo recibido
		if (bmoEstimationItem.getQuantity().toDouble() > 0 && bmoEstimationItem.getPrice().toDouble() > 0) {

			//Validar que no exceda el total
			/*if ((bmoEstimationItem.getQuantity().toDouble() + bmoEstimationItem.getQuantityReceipt().toDouble()) > bmoEstimationItem.getQuantityTotal().toDouble()) {
				<bmUpdateResult.addError(bmoContractEstimation.getCode().getName(), "No puede estimar mas del total.");
			}*/	

			bmoEstimationItem.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoEstimationItem.getPrice().toDouble() * bmoEstimationItem.getQuantity().toDouble()));
			bmoEstimationItem.getQuantityReceipt().setValue(bmoEstimationItem.getQuantity().toDouble() + bmoEstimationItem.getQuantityLast().toDouble());
		} else {
			bmoEstimationItem.getQuantityReceipt().setValue(bmoEstimationItem.getQuantityLast().toDouble());
			bmoEstimationItem.getSubTotal().setValue(0);			
			bmoEstimationItem.getQuantity().setValue(0);
		}

		super.save(pmConn, bmoEstimationItem, bmUpdateResult);

		//Actualizar el importe
		pmContractEstimation.updateTotal(pmConn, bmoContractEstimation, bmUpdateResult);

		return bmUpdateResult;
	}

	public double getAcumulated(PmConn pmConn, BmoContractConceptItem bmoContractConceptItem, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double result = 0;

		sql = " select sum(esti_quantity) from estimationitems " +
				" where esti_contractestimationid in " +
				" (select coes_contractestimationid from contractestimations where coes_workcontractid = " + bmoContractConceptItem.getWorkContractId().toInteger() + ") " +
				" and esti_contractconceptitemid = " + bmoContractConceptItem.getId();	       
		pmConn.doFetch(sql);

		if (pmConn.next()) result = pmConn.getDouble(1);

		return result;
	}




	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoEstimationItem = (BmoEstimationItem) bmObject;

		// Eliminar el item de la estimacion
		super.delete(pmConn, bmObject, bmUpdateResult);

		//Actualizar el acumulado

		return bmUpdateResult;
	}

}

