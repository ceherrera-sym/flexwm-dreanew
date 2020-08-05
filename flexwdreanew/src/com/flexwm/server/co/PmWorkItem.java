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
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkItem;
import com.flexwm.shared.co.BmoUnitPrice;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWorkItem extends PmObject {
	BmoWorkItem bmoWorkItem;

	public PmWorkItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWorkItem = new BmoWorkItem();
		setBmObject(bmoWorkItem);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(

				new PmJoin(bmoWorkItem.getUnitPriceId(), bmoWorkItem.getBmoUnitPrice()),
				new PmJoin(bmoWorkItem.getBmoUnitPrice().getUnitId(), bmoWorkItem.getBmoUnitPrice().getBmoUnit()),			
				new PmJoin(bmoWorkItem.getBmoUnitPrice().getCurrencyId(), bmoWorkItem.getBmoUnitPrice().getBmoCurrency()),
				new PmJoin(bmoWorkItem.getWorkId(), bmoWorkItem.getBmoWork()),
				new PmJoin(bmoWorkItem.getBmoWork().getBudgetItemId(), bmoWorkItem.getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoWorkItem.getBmoWork().getBmoBudgetItem().getBudgetId(), bmoWorkItem.getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoWorkItem.getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoWorkItem.getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoWorkItem.getBmoWork().getCompanyId(), bmoWorkItem.getBmoWork().getBmoCompany()),			
				new PmJoin(bmoWorkItem.getBmoWork().getDevelopmentPhaseId(), bmoWorkItem.getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoWorkItem.getBmoWork().getUserId(), bmoWorkItem.getBmoWork().getBmoUser()),
				new PmJoin(bmoWorkItem.getBmoWork().getBmoUser().getAreaId(), bmoWorkItem.getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoWorkItem.getBmoWork().getBmoUser().getLocationId(), bmoWorkItem.getBmoWork().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWorkItem = (BmoWorkItem)autoPopulate(pmConn, new BmoWorkItem());

		//Works
		BmoWork bmoWork = new BmoWork();
		int workId = (int)pmConn.getInt(bmoWork.getIdFieldName());
		if (workId > 0) bmoWorkItem.setBmoWork((BmoWork) new PmWork(getSFParams()).populate(pmConn));
		else bmoWorkItem.setBmoWork(bmoWork);

		//Precios Unitarios
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int unitPriceId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		if (unitPriceId > 0) bmoWorkItem.setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoWorkItem.setBmoUnitPrice(bmoUnitPrice);

		return bmoWorkItem;
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		PmConn pmConn = new PmConn(getSFParams());
		bmoWorkItem = (BmoWorkItem)bmObject;
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Obten el Work
			PmWork pmWork = new PmWork(getSFParams());
			BmoWork bmoWork = (BmoWork)pmWork.get(pmConn, bmoWorkItem.getWorkId().toInteger());

			if (bmoWorkItem.getUnitPriceId().toInteger() > 0) {
				// Obtener el precio unitario  y su precio vigente
				PmUnitPrice pmUnitPrice = new PmUnitPrice(getSFParams());
				BmoUnitPrice bmoUnitPrice = (BmoUnitPrice)pmUnitPrice.get(pmConn, bmoWorkItem.getUnitPriceId().toInteger());

				//Obtener el P.U.
				//Tomar el precio del presupuesto de obra
				double price = bmoWorkItem.getPrice().toDouble();

				//Si el precio del presupuesto de obra es 0, tomar del precio unitario
				if (!(price > 0)) price = bmoUnitPrice.getTotal().toDouble();

				double indirects = 0;

				if (bmoWork.getIndirects().toDouble() > 0) {
					indirects = price * (bmoWork.getIndirects().toDouble() / 100);
				}

				bmoWorkItem.getCode().setValue(bmoUnitPrice.getCode().toString());
				bmoWorkItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(price + indirects));
			}	

			// Calcula el valor del item
			bmoWorkItem.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(
					bmoWorkItem.getPrice().toDouble() * 
					bmoWorkItem.getQuantity().toDouble())
					);


			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);



			// Recalcula el subtotal
			pmWork.updateAmount(pmConn, bmoWork, bmUpdateResult);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

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
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		bmoWorkItem = (BmoWorkItem)bmObject;

		// Obten la obra
		PmWork pmWork = new PmWork(getSFParams());
		BmoWork bmoWork = (BmoWork)pmWork.get(bmoWorkItem.getWorkId().toInteger());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();


			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);			

			pmWork.updateAmount(pmConn, bmoWork, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) pmConn.commit();				


		} catch (SFPmException e) {
			System.out.println("ERROR-" + e.toString());
		} finally {
			pmConn.close();
		}	

		return bmUpdateResult;
	}

}
