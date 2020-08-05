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
import java.util.Iterator;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.op.PmSupplier;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoContractConceptItem;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkItem;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


/**
 * @author jhernandez
 *
 */

public class PmWorkContract extends PmObject{
	BmoWorkContract bmoWorkContract;

	public PmWorkContract(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoWorkContract = new BmoWorkContract();
		setBmObject(bmoWorkContract); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWorkContract.getWorkId(), bmoWorkContract.getBmoWork()),
				new PmJoin(bmoWorkContract.getBmoWork().getBudgetItemId(), bmoWorkContract.getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoWorkContract.getBmoWork().getBmoBudgetItem().getCurrencyId(), bmoWorkContract.getBmoWork().getBmoBudgetItem().getBmoCurrency()),
				new PmJoin(bmoWorkContract.getBmoWork().getBmoBudgetItem().getBudgetId(), bmoWorkContract.getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoWorkContract.getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoWorkContract.getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoWorkContract.getBmoWork().getDevelopmentPhaseId(), bmoWorkContract.getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoWorkContract.getBmoWork().getUserId(), bmoWorkContract.getBmoWork().getBmoUser()),
				new PmJoin(bmoWorkContract.getBmoWork().getBmoUser().getAreaId(), bmoWorkContract.getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoWorkContract.getBmoWork().getBmoUser().getLocationId(), bmoWorkContract.getBmoWork().getBmoUser().getBmoLocation()),				
				new PmJoin(bmoWorkContract.getSupplierId(), bmoWorkContract.getBmoSupplier()),
				new PmJoin(bmoWorkContract.getBmoSupplier().getSupplierCategoryId(), bmoWorkContract.getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoWorkContract.getCompanyId(), bmoWorkContract.getBmoCompany()),
				new PmJoin(bmoWorkContract.getWFlowId(), bmoWorkContract.getBmoWFlow()),
				new PmJoin(bmoWorkContract.getBmoWFlow().getWFlowTypeId(), bmoWorkContract.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWorkContract.getBmoWFlow().getWFlowPhaseId(), bmoWorkContract.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWorkContract.getBmoWFlow().getWFlowFunnelId(), bmoWorkContract.getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoWorkContract.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(),bmoWorkContract.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoWorkContract());
		bmoWorkContract = (BmoWorkContract)autoPopulate(pmConn, new BmoWorkContract());

		//BmoWork
		BmoWork bmoWork = new BmoWork();
		int workId = pmConn.getInt(bmoWork.getIdFieldName());
		if (workId > 0) bmoWorkContract.setBmoWork((BmoWork) new PmWork(getSFParams()).populate(pmConn));
		else bmoWorkContract.setBmoWork(bmoWork);

		//BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		int supplierId = pmConn.getInt(bmoSupplier.getIdFieldName());
		if (supplierId > 0) bmoWorkContract.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else bmoWorkContract.setBmoSupplier(bmoSupplier);

		//BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoWorkContract.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoWorkContract.setBmoCompany(bmoCompany);
		
		//BmoCompany
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wflowId = pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wflowId > 0) bmoWorkContract.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoWorkContract.setBmoCompany(bmoCompany);

		return bmoWorkContract;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoWorkContract bmoWorkContract = (BmoWorkContract)bmObject;
		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoWorkContract.getId() > 0)) {
			
			newRecord = true;
			PmWork pmWork = new PmWork(getSFParams());
			BmoWork bmoWork = (BmoWork)pmWork.get(pmConn, bmoWorkContract.getWorkId().toInteger());

			//Tomar la empresa desde la Obra
			if (bmoWork.getCompanyId().toInteger() > 0)
				bmoWorkContract.getCompanyId().setValue(bmoWork.getCompanyId().toInteger());

			super.save(pmConn, bmoWorkContract, bmUpdateResult);
			//Obtener el Id
			bmoWorkContract.setId(bmUpdateResult.getId());
			bmoWorkContract.getCode().setValue(bmoWorkContract.getCodeFormat());
			bmoWorkContract.getPaymentStatus().setValue(BmoWorkContract.PAYMENTSTATUS_PENDING);

			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoWorkContract.getBudgetItemId().toInteger() > 0 )) {
					bmUpdateResult.addError(bmoWorkContract.getBudgetItemId().getName(), "Seleccionar una partida");
				}
			}
			//Crear los items con el presupusto de conceptos
			createItemsFromWorks(pmConn, bmoWorkContract, bmUpdateResult);

			//El numero de paquetes deber ser 1
			bmoWorkContract.getQuantity().setValue(1);

		}
		
		if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_CLOSED)) {			
			//Obtener el total de las estimaciones
			if (sumAmountContractEstimations(pmConn, bmoWorkContract, bmUpdateResult) > 0) {
				bmoWorkContract.getTotalReal().setValue(SFServerUtil.roundCurrencyDecimals(sumAmountContractEstimations(pmConn, bmoWorkContract, bmUpdateResult)));
			} else {
				bmoWorkContract.getTotalReal().setValue(SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getTotal().toDouble()));
			}	
		} else {
			bmoWorkContract.getTotalReal().setValue(SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getTotal().toDouble()));
		}


		if (!(bmoWorkContract.getQuantity().toDouble() > 0))
			bmUpdateResult.addError(bmoWorkContract.getQuantity().getName(), "El número de paquetes no puede ser cero.");

		//Si existe un cambio en la partida actulizar las OC
		PmWorkContract pmWorkContractPrev = new PmWorkContract(getSFParams());
		BmoWorkContract bmoWorkContractPrev = (BmoWorkContract)pmWorkContractPrev.get(pmConn, bmoWorkContract.getId());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoWorkContract.getBudgetItemId().toInteger() != bmoWorkContractPrev.getBudgetItemId().toInteger()) {
				updateBudgetItem(pmConn, bmoWorkContract, bmUpdateResult);
			}
		}
		if (!bmUpdateResult.hasErrors()) {
			
			// Si no esta asignado el tipo de wflow, toma el primero
			if (!(bmoWorkContract.getWFlowTypeId().toInteger() > 0)) {
				int wFlowTypeId = new PmWFlowType(getSFParams()).getFirstWFlowTypeId(pmConn, bmoWorkContract.getProgramCode());
				if (wFlowTypeId > 0) {
					bmoWorkContract.getWFlowTypeId().setValue(wFlowTypeId);
				} else {
					bmUpdateResult.addError(bmoWorkContract.getCode().getName(), "Debe crearse Tipo de WFlow para Contrato de Obra.");
				}
			}
			char wflowstatus = BmoWFlow.STATUS_ACTIVE;
			if((bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_CLOSED) || (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_CANCEL)))){
				wflowstatus = BmoWFlow.STATUS_INACTIVE;
			}
					
			// Crea el WFlow y asigna el ID recien creado
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			bmoWorkContract.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoWorkContract.getWFlowTypeId().toInteger(), bmoWorkContract.getWFlowId().toInteger(), 
					bmoWorkContract.getProgramCode(), bmoWorkContract.getId(), bmoWorkContract.getUserCreateId().toInteger(), bmoWorkContract.getCompanyId().toInteger(), -1,
					bmoWorkContract.getCode().toString(), bmoWorkContract.getName().toString(), bmoWorkContract.getDescription().toString(), 
					bmoWorkContract.getStartDate().toString(), bmoWorkContract.getEndDate().toString(),wflowstatus , bmUpdateResult).getId());
			
			
			
		}
		
		if (newRecord) {
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addDataLog(pmConn,bmUpdateResult, bmoWorkContract.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, "Se creo el contrato ","");
		}
		

		super.save(pmConn, bmoWorkContract, bmUpdateResult);
		
		
		//Actualizar la cantidad del contrato
		updateAmount(pmConn, bmObject, bmUpdateResult);
		
		

		return bmUpdateResult;
		
	
	}

	//Actualizar la partidad del presupuesto
	public void updateBudgetItem(PmConn pmConn, BmoWorkContract bmoWorkContract, BmUpdateResult bmUpdateResult) throws SFException {

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			//Obtener las estimaciones ligadas al contrato
			BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getWorkContractId(), bmoWorkContract.getId());
			PmContractEstimation pmContractEstimation = new PmContractEstimation(getSFParams());
			Iterator<BmObject> contractEstimationIterator = pmContractEstimation.list(pmConn, bmFilter).iterator();
	
			BmoRequisition bmoRequisition = new BmoRequisition();
	
			PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	
			while (contractEstimationIterator.hasNext()) {
				bmoContractEstimation = (BmoContractEstimation)contractEstimationIterator.next();
	
				//Listar las OC ligadas a la estimacion
				BmFilter bmFilterReq = new BmFilter();
				bmFilterReq.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getContractEstimationId(), bmoContractEstimation.getId());
				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				Iterator<BmObject> requisitionIterator = pmRequisition.list(pmConn, bmFilterReq).iterator();
	
				while(requisitionIterator.hasNext()) {				
					bmoRequisition = (BmoRequisition)requisitionIterator.next();
	
					bmoRequisition.getBudgetItemId().setValue(bmoWorkContract.getBudgetItemId().toInteger());
					pmRequisition.saveSimple(pmConn, bmoRequisition, bmUpdateResult);
	
					//Actualizar la OC
					pmRequisition.updateBudgetItem(pmConn, bmoRequisition, bmUpdateResult);
	
					//Actualizar el presupuesto
					bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoWorkContract.getBudgetItemId().toInteger());				
					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		}
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWorkContract = (BmoWorkContract)bmObject;

		//Sumar los items de contrato
		sumWorkContractItems(pmConn, bmoWorkContract, bmUpdateResult);

		double amount = bmoWorkContract.getSubTotal().toDouble();

		//Multiplicar los paquetes
		double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
		double tax = 0;

		if (bmoWorkContract.getHasTax().toBoolean()) tax = amount * taxRate;

		double total = amount + tax;

		bmoWorkContract.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
		bmoWorkContract.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
		bmoWorkContract.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));

		//Obtener el total de las estimaciones			
		if (!(bmoWorkContract.getTotalReal().toDouble() > 0))
			bmoWorkContract.getTotalReal().setValue(bmoWorkContract.getTotal().toDouble());

		bmoWorkContract.getTotalReal().setValue(SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getTotalReal().toDouble()));

		super.save(pmConn, bmoWorkContract, bmUpdateResult);


		//Validar que las estimaciones esten en cero al cambiar el fondo de garantia y el anticipo
		if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
			if (bmoWorkContract.getPercentGuaranteeFund().toDouble() > 0 || bmoWorkContract.getPercentDownPayment().toDouble() > 0) {
				if (this.sumContractEstimations(pmConn, bmoWorkContract.getId(), bmUpdateResult) > 0)
					bmUpdateResult.addError(bmoWorkContract.getStatus().getName(), "Existen Estimaciones Capturadas."); 
			}
		}	


		//Calcular el fondo de garantia		
		if (bmoWorkContract.getPercentGuaranteeFund().toDouble() > 0) 
			bmoWorkContract.getGuaranteeFund().setValue(SFServerUtil.roundCurrencyDecimals((bmoWorkContract.getPercentGuaranteeFund().toDouble() / 100) * bmoWorkContract.getSubTotal().toDouble()));			 
		else
			bmoWorkContract.getGuaranteeFund().setValue(0);

		//Calcular el anticipo
		if (bmoWorkContract.getPercentDownPayment().toDouble() > 0)
			bmoWorkContract.getDownPayment().setValue(SFServerUtil.roundCurrencyDecimals((bmoWorkContract.getPercentDownPayment().toDouble() / 100) * bmoWorkContract.getTotal().toDouble()));
		else
			bmoWorkContract.getDownPayment().setValue(0);

		//Calcular la sansion
		//System.out.println("bmoWorkContract.getHasSanction() " + bmoWorkContract.getHasSanction().toBoolean());
		//System.out.println("bmoWorkContract.getDailySanction() " + bmoWorkContract.getDailySanction().toDouble());

		if (bmoWorkContract.getHasSanction().toBoolean()) 
			bmoWorkContract.getDailySanction().setValue(SFServerUtil.roundCurrencyDecimals((bmoWorkContract.getTotal().toDouble() * 0.1) / 12));
		else
			bmoWorkContract.getDailySanction().setValue(0);

		//System.out.println("bmoWorkContract.getHasSanction() " + bmoWorkContract.getHasSanction().toBoolean());
		//System.out.println("bmoWorkContract.getDailySanction() " + bmoWorkContract.getDailySanction().toDouble());

		super.save(pmConn, bmoWorkContract, bmUpdateResult);


	}


	public void updatePaymentStatus(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWorkContract = (BmoWorkContract)bmObject;
		String sql = "";
		double totalCoes = 0;
		//Sumar las estimaciones pagas
		sql = " SELECT SUM(coes_total) AS totalcoes FROM contractestimations " +
				" WHERE coes_status = '" + BmoContractEstimation.STATUS_AUTHORIZED  + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) totalCoes = pmConn.getDouble("totalcoes");
	
		if (bmoWorkContract.getTotal().toDouble() <= totalCoes)
			bmoWorkContract.getPaymentStatus().setValue(BmoContractEstimation.PAYMENTSTATUS_TOTAL);
		else
			bmoWorkContract.getPaymentStatus().setValue(BmoContractEstimation.PAYMENTSTATUS_PENDING);

		// Almacenar el cambio de estatus
		super.save(pmConn, bmoWorkContract, bmUpdateResult);

	}	


	private void sumWorkContractItems(PmConn pmConn, BmoWorkContract bmoWorkContract, BmUpdateResult bmUpdateResult) throws SFException {
		double amount = 0;

		pmConn.doFetch("SELECT sum(ccit_amount) FROM contractconceptitems "
				+ " WHERE ccit_workcontractid = " + bmoWorkContract.getId());

		if (pmConn.next()) amount = pmConn.getDouble(1);


		// Calcular montos
		if (amount == 0) {			
			bmoWorkContract.getSubTotal().setValue(0);
			bmoWorkContract.getTotal().setValue(0);
		} else {

			double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
			double tax = 0;

			if (bmoWorkContract.getHasTax().toBoolean()) tax = amount * taxRate;

			double total = amount + tax;

			bmoWorkContract.getTotalConcepts().setValue(SFServerUtil.roundCurrencyDecimals(amount));

			if (bmoWorkContract.getQuantity().toDouble() > 0)
				bmoWorkContract.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getQuantity().toDouble() * bmoWorkContract.getTotalConcepts().toDouble()));
			else
				bmoWorkContract.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));				

			bmoWorkContract.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));			
			bmoWorkContract.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));

		}		
	}

	public double sumContractEstimations(PmConn pmConn, int workContractId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		sql = "SELECT SUM(coes_total) as total FROM contractestimations WHERE coes_workcontractid = " + workContractId;
		pmConn.doFetch(sql);
		if (pmConn.next()) return pmConn.getDouble("total");
		else return 0;
	}

	public double sumAmountContractEstimations(PmConn pmConn, BmoWorkContract bmoWorkContract, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		if (bmoWorkContract.getTax().toDouble() > 0)
			sql = "SELECT SUM(coes_subtotal) as total FROM contractestimations WHERE coes_workcontractid = " + bmoWorkContract.getId();
		else
			sql = "SELECT SUM(coes_amount) as total FROM contractestimations WHERE coes_workcontractid = " + bmoWorkContract.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) return pmConn.getDouble("total");
		else return 0;
	}

	public void createItemsFromWorks(PmConn pmConn, BmoWorkContract bmoWorkContract, BmUpdateResult bmUpdateResult) throws SFException {

		//Obtener los conceptos de la obra

		PmWorkItem pmWorkItem = new PmWorkItem(getSFParams());
		BmoWorkItem bmoWorkItem = new BmoWorkItem();
		bmoWorkItem = (BmoWorkItem)pmWorkItem.getBy(bmoWorkContract.getWorkId().toString(), bmoWorkItem.getWorkId().getName());

		BmFilter filterWorkItem = new BmFilter();
		filterWorkItem.setValueFilter(bmoWorkItem.getKind(), bmoWorkItem.getWorkId(), bmoWorkContract.getWorkId().toInteger());
		Iterator<BmObject> listWorkItem = new PmWorkItem(getSFParams()).list(pmConn, filterWorkItem).iterator();			

		while (listWorkItem.hasNext()) {
			bmoWorkItem = (BmoWorkItem)listWorkItem.next();			

			//Crear nvo item
			BmoContractConceptItem bmoContractConceptItem = new BmoContractConceptItem();
			bmoContractConceptItem.getWorkItemId().setValue(bmoWorkItem.getId());
			bmoContractConceptItem.getCode().setValue(bmoWorkItem.getCode().toString());
			bmoContractConceptItem.getName().setValue(bmoWorkItem.getBmoUnitPrice().getName().toString());
			bmoContractConceptItem.getDescription().setValue(bmoWorkItem.getBmoUnitPrice().getDescription().toString());
			bmoContractConceptItem.getWorkContractId().setValue(bmoWorkContract.getId());
			bmoContractConceptItem.getQuantity().setValue(bmoWorkItem.getQuantity().toDouble());
			bmoContractConceptItem.getPrice().setValue(bmoWorkItem.getPrice().toDouble());
			bmoContractConceptItem.getAmount().setValue(bmoWorkItem.getAmount().toDouble());

			super.save(pmConn,bmoContractConceptItem, bmUpdateResult);			

		}
	}

	public void deleteItems(PmConn pmConn, int workContractId, BmUpdateResult bmUpdateResult) throws SFException {		
		pmConn.doUpdate("DELETE FROM contractconceptitems WHERE ccit_workcontractid = " + workContractId);			
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWorkContract = (BmoWorkContract) bmObject;

		if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_AUTHORIZED)) {
			bmUpdateResult.addError(bmoWorkContract.getCode().getName(), "El Contrato esta Autorizado.");
		} else {			
			//Eliminar los EstimationItems
			deleteItems(pmConn, bmoWorkContract.getId(), bmUpdateResult);

			super.delete(pmConn, bmObject, bmUpdateResult);

		}	

		return bmUpdateResult;
	}

}

