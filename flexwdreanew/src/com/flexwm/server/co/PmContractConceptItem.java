/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 *  
 * 
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.co.BmoWorkItem;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoContractConceptItem;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmContractConceptItem extends PmObject {
	BmoContractConceptItem bmoContractConceptItem;

	public PmContractConceptItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoContractConceptItem = new BmoContractConceptItem();
		setBmObject(bmoContractConceptItem);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoContractConceptItem.getWorkContractId(), bmoContractConceptItem.getBmoWorkContract()),			
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getWorkId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork()),
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getBmoWork().getDevelopmentPhaseId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase()),			
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getCompanyId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoCompany()),
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getBmoWork().getUserId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoContractConceptItem.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoContractConceptItem.getWorkItemId(), bmoContractConceptItem.getBmoWorkItem()),
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBudgetItemId(), bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBmoBudgetItem().getBudgetId(), bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoContractConceptItem.getBmoWorkItem().getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getUnitPriceId(), bmoContractConceptItem.getBmoWorkItem().getBmoUnitPrice()),			
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getBmoUnitPrice().getUnitId(), bmoContractConceptItem.getBmoWorkItem().getBmoUnitPrice().getBmoUnit()),			
				new PmJoin(bmoContractConceptItem.getBmoWorkItem().getBmoUnitPrice().getCurrencyId(), bmoContractConceptItem.getBmoWorkItem().getBmoUnitPrice().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoContractConceptItem = (BmoContractConceptItem)autoPopulate(pmConn, new BmoContractConceptItem());

		//Conceptos items
		BmoWorkItem bmoWorkItem = new BmoWorkItem();
		int workItemId = (int)pmConn.getInt(bmoWorkItem.getIdFieldName());
		if (workItemId > 0) bmoContractConceptItem.setBmoWorkItem((BmoWorkItem) new PmWorkItem(getSFParams()).populate(pmConn));
		else bmoContractConceptItem.setBmoWorkItem(bmoWorkItem);

		return bmoContractConceptItem;
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		PmConn pmConn = new PmConn(getSFParams());
		bmoContractConceptItem = (BmoContractConceptItem)bmObject;
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Obtener el Contrato de obra
			PmWorkContract pmWorkContract = new PmWorkContract(getSFParams());			
			BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn, bmoContractConceptItem.getWorkContractId().toInteger());

			/*//Obtener el item de la Obra			
			if  (!(bmoContractConceptItem.getId() > 0)) {

				//PmConcept pmConceptItem = new PmConcept(getSFParams());
				BmoConcept bmoConceptItem = (BmoConcept)pmConceptItem.get(pmConn, bmoContractConceptItem.getConceptItemId().toInteger());

				bmoContractConceptItem.getPrice().setValue(bmoConceptItem.getTotal().toDouble());
			}*/

			bmoContractConceptItem.getAmount().setValue(bmoContractConceptItem.getPrice().toDouble() * bmoContractConceptItem.getQuantity().toDouble());

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);



			// Recalcula el subtotal
			pmWorkContract.updateAmount(pmConn, bmoWorkContract, bmUpdateResult);


			if (!bmUpdateResult.hasErrors()) pmConn.commit();

		} catch (SFPmException e) {
			System.out.println(e.toString());
			bmUpdateResult.addMsg(e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoContractConceptItem = (BmoContractConceptItem)bmObject;		

		// Obtener el Contrato de obra
		PmWorkContract pmWorkContract = new PmWorkContract(getSFParams());			
		BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn, bmoContractConceptItem.getWorkContractId().toInteger());

		//elimina el item
		super.delete(pmConn, bmObject, bmUpdateResult);				

		if (!bmUpdateResult.hasErrors()) pmConn.commit();				

		// Recalcula el subtotal
		pmWorkContract.updateAmount(pmConn, bmoWorkContract, bmUpdateResult);



		return bmUpdateResult;
	}





}
