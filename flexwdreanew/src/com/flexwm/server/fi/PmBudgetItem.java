/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoRequisition;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmBudgetItem extends PmObject {
	BmoBudgetItem bmoBudgetItem;

	public PmBudgetItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBudgetItem = new BmoBudgetItem();
		setBmObject(bmoBudgetItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoBudgetItem.getBudgetId(), bmoBudgetItem.getBmoBudget()),
				new PmJoin(bmoBudgetItem.getBudgetItemTypeId(), bmoBudgetItem.getBmoBudgetItemType()),
				new PmJoin(bmoBudgetItem.getCurrencyId(), bmoBudgetItem.getBmoCurrency())
				)));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoBudgetItem = (BmoBudgetItem)autoPopulate(pmConn, new BmoBudgetItem());		

		//BmoBudget
		BmoBudget bmoBudget = new BmoBudget();
		if (pmConn.getInt(bmoBudget.getIdFieldName()) > 0) bmoBudgetItem.setBmoBudget((BmoBudget) new PmBudget(getSFParams()).populate(pmConn));
		else bmoBudgetItem.setBmoBudget(bmoBudget);

		//BmoBudget
		BmoBudgetItemType bmoBudgetItemType = new BmoBudgetItemType();
		if (pmConn.getInt(bmoBudgetItemType.getIdFieldName()) > 0) bmoBudgetItem.setBmoBudgetItemType((BmoBudgetItemType) new PmBudgetItemType(getSFParams()).populate(pmConn));
		else bmoBudgetItem.setBmoBudgetItemType(bmoBudgetItemType);
		
		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0)
			bmoBudgetItem.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoBudgetItem.setBmoCurrency(bmoCurrency);

		return bmoBudgetItem;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)bmObject;

		super.save(pmConn, bmoBudgetItem, bmUpdateResult);

		//Actualizar el total
		//updateAmount(pmConn, bmoBudgetItem, bmUpdateResult);

		//Actualizar los saldos y pagos
		updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)bmObject;

		PmBudget pmBudget = new PmBudget(getSFParams());
		BmoBudget bmoBudget = (BmoBudget)pmBudget.get(pmConn, bmoBudgetItem.getBudgetId().toInteger());

		//Actualizar el Presupuesto 
		pmBudget.updateAmount(pmConn, bmoBudget, bmUpdateResult);
	}

	public void updateBalance(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)bmObject;


		double reqiSum = 0, raccSum = 0, bkmvSum = 0;
		String sql = "";

		//Obtener el tipo de partida presupuestal
		BmoBudgetItemType bmoTypeCostCenter = new BmoBudgetItemType();
		if (bmoBudgetItem.getBudgetItemTypeId().toInteger() > 0) {
			PmBudgetItemType pmTypeCostCenter = new PmBudgetItemType(getSFParams());
			bmoTypeCostCenter = (BmoBudgetItemType) pmTypeCostCenter.get(pmConn, bmoBudgetItem.getBudgetItemTypeId().toInteger());
		}	


		if (bmoTypeCostCenter.getId() > 0) {

			if (bmoTypeCostCenter.getType().equals(BmoBudgetItemType.TYPE_WITHDRAW)) {

				sql = " SELECT SUM(reqi_total) AS reqiTotal FROM requisitions " +
						" WHERE reqi_budgetitemid = " + bmoBudgetItem.getId() +
						" AND reqi_status <> '" + BmoRequisition.STATUS_CANCELLED + "'";
				pmConn.doFetch(sql);		
				if (pmConn.next()) reqiSum = pmConn.getDouble("reqiTotal");

				bmoBudgetItem.getProvisioned().setValue(SFServerUtil.roundCurrencyDecimals(reqiSum));

				//Calcular el saldo
				//Sumar las OC ligadas al item
				sql = " SELECT SUM(reqi_payments) AS reqiPayments FROM requisitions " +
						" WHERE reqi_budgetitemid = " + bmoBudgetItem.getId() +
						" AND reqi_status <> '" + BmoRequisition.STATUS_CANCELLED + "'";
				pmConn.doFetch(sql);		
				if (pmConn.next()) reqiSum = pmConn.getDouble("reqiPayments");


				if (!(reqiSum > 0)) {
					//Sumar las comisiones bancarias
					sql = " SELECT SUM(bkmc_amount) AS bkmcSum FROM bankmovconcepts " + 
							" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
							" WHERE bkmv_budgetitemid = " + bmoBudgetItem.getId() +
							" AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'";
					pmConn.doFetch(sql);		
					if (pmConn.next()) bkmvSum = pmConn.getDouble("bkmcSum");
				}		

				bmoBudgetItem.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(reqiSum + bkmvSum));							

				double amount = 0;				
				amount = bmoBudgetItem.getAmount().toDouble() - (reqiSum + bkmvSum);

				// Validar que no permita saldos negativos
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getNegativeBudget().toBoolean()) {
					if (!(amount > 0)) {
						//bmUpdateResult.addError(bmoBudgetItem.getAmount().getName(), "El saldo no puede ser negativo");
					}
				}

				bmoBudgetItem.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(amount));


			} else {			

				//Calcular el saldo
				//Sumar las CXC ligadas al item
				sql = " SELECT SUM(racc_total) AS raccSum FROM raccounts " +
						" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +					  	
						" WHERE racc_budgetitemid = " + bmoBudgetItem.getId() +
						" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
						" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";
				pmConn.doFetch(sql);		
				if (pmConn.next()) raccSum = pmConn.getDouble("raccSum");

				bmoBudgetItem.getProvisioned().setValue(SFServerUtil.roundCurrencyDecimals(raccSum));

				raccSum = 0;
				sql = " SELECT SUM(racc_total) AS raccSum FROM raccounts " +
						" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +					  	
						" WHERE racc_budgetitemid = " + bmoBudgetItem.getId() +
						" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
						" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
						" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'";
				pmConn.doFetch(sql);		
				if (pmConn.next()) raccSum = pmConn.getDouble("raccSum");

				bkmvSum = 0;
				sql = " SELECT SUM(bkmv_deposit) AS bkmvSum FROM bankmovements " +
						" WHERE bkmv_budgetitemid = " + bmoBudgetItem.getId() +				      
						" AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'";
				pmConn.doFetch(sql);				
				if (pmConn.next()) bkmvSum = pmConn.getDouble("bkmvSum");				

				bmoBudgetItem.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(raccSum + bkmvSum));

				double amount = 0;				
				amount = bmoBudgetItem.getAmount().toDouble() - (raccSum + bkmvSum);

				//Validar que no permita saldos negativos
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getNegativeBudget().toBoolean()) {
					if (!(amount > 0)) {
						// TODO: reestablecer control de partidas negativas
						//bmUpdateResult.addError(bmoBudgetItem.getAmount().getName(), "El saldo no puede ser negativo");
					}
				}

				bmoBudgetItem.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			}

		} else {
			bmUpdateResult.addError(bmoBudgetItem.getBalance().getName(), "La partida no tiene definido el tipo");
		}

		super.save(pmConn, bmoBudgetItem, bmUpdateResult);


		PmBudget pmBudget = new PmBudget(getSFParams());
		BmoBudget bmoBudget = (BmoBudget)pmBudget.get(pmConn, bmoBudgetItem.getBudgetId().toInteger());

		//Actualizar el Saldo 
		pmBudget.updateBalance(pmConn, bmoBudget, bmUpdateResult);		
	}


	public double getSumBudgetItems(PmConn pmConn, int value, BmUpdateResult bmUpdateResult) throws SFException {
		double total = 0;

		pmConn.doFetch("SELECT sum(bgit_amount) FROM budgetitems "
				+ " WHERE bgit_budgetid = " + value);

		if (pmConn.next()) total = pmConn.getDouble(1);
		return  total;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoBudgetItem = (BmoBudgetItem) bmObject;

		// Validaciones 
		if (bmoBudgetItem.getBmoBudget().getStatus().toChar() == BmoBudget.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede Eliminar: El presupuesto está Autorizado.");
		} else {						
			// Elimina la CxP
			super.delete(pmConn, bmObject, bmUpdateResult);
		}

		updateAmount(pmConn, bmoBudgetItem, bmUpdateResult);

		//Actualizar el Saldo 
		PmBudget pmBudget = new PmBudget(getSFParams());
		BmoBudget bmoBudget = (BmoBudget)pmBudget.get(pmConn, bmoBudgetItem.getBudgetId().toInteger());
		pmBudget.updateBalance(pmConn, bmoBudget, bmUpdateResult);

		//Actualizar los saldos y pagos
		//updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);

		return bmUpdateResult;
	}



}
