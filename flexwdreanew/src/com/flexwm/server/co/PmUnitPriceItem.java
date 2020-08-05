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
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceItem;
import com.flexwm.shared.co.BmoWork;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmUnitPriceItem extends PmObject {
	BmoUnitPriceItem bmoUnitPriceItem;

	public PmUnitPriceItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoUnitPriceItem = new BmoUnitPriceItem();
		setBmObject(bmoUnitPriceItem);
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoUnitPriceItem.getUnitPriceId(), bmoUnitPriceItem.getBmoUnitPrice()),			
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getUnitId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoUnit()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getWorkId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getCurrencyId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoCurrency()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getCompanyId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoCompany()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBudgetItemId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoBudgetItem().getBudgetId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getDevelopmentPhaseId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getUserId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoUser()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoUser().getAreaId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoUser().getLocationId(), bmoUnitPriceItem.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoUnitPriceItem = (BmoUnitPriceItem)autoPopulate(pmConn, new BmoUnitPriceItem());

		//BmoUser
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int unitPriceId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		if (unitPriceId > 0) bmoUnitPriceItem.setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoUnitPriceItem.setBmoUnitPrice(bmoUnitPrice);

		return bmoUnitPriceItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoUnitPriceItem bmoUnitPriceItem = (BmoUnitPriceItem)bmObject;

		//Obtener el Precio Unitario
		PmUnitPrice pmUnitPriceParent = new PmUnitPrice(getSFParams());
		BmoUnitPrice bmoUnitPriceParent = (BmoUnitPrice)pmUnitPriceParent.get(pmConn, bmoUnitPriceItem.getUnitPriceParentId().toInteger());

		PmWork pmWork = new PmWork(getSFParams());

		// Obten la obra		
		BmoWork bmoWork = (BmoWork)pmWork.get(pmConn,bmoUnitPriceParent.getWorkId().toInteger());

		// Si la Obra ya está autorizada, no se puede hacer movimientos
		if (!bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
			bmUpdateResult.addMsg("No se puede realizar movimientos - Su estatus no esta en Revisión.");
		} else {

			if (!(bmoUnitPriceItem.getId() > 0)) {
				//Obtener el costo del precio unitario
				PmUnitPrice pmUnitPrice = new PmUnitPrice(getSFParams());
				BmoUnitPrice bmoUnitPrice = (BmoUnitPrice)pmUnitPrice.get(pmConn, bmoUnitPriceItem.getUnitPriceId().toInteger());

				bmoUnitPriceItem.getCode().setValue(bmoUnitPrice.getCode().toString());
				bmoUnitPriceItem.getAmount().setValue(bmoUnitPrice.getTotal().toDouble());
			}

			bmoUnitPriceItem.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPriceItem.getAmount().toDouble() * 
					bmoUnitPriceItem.getQuantity().toDouble()));

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

			//Calcular la cantidad del precio unitario
			pmUnitPriceParent.updateAmount(pmConn, bmoUnitPriceParent, bmUpdateResult);
		}	

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoUnitPriceItem = (BmoUnitPriceItem)bmObject;

		//Obtener el Precio Unitario				
		PmUnitPrice pmUnitPriceParent = new PmUnitPrice(getSFParams());
		BmoUnitPrice bmoUnitPriceParent = (BmoUnitPrice)pmUnitPriceParent.get(pmConn,bmoUnitPriceItem.getUnitPriceParentId().toInteger());

		//elimina el item
		super.delete(pmConn, bmObject, bmUpdateResult);

		//Calcular la cantidad del precio unitario
		pmUnitPriceParent.updateAmount(pmConn, bmoUnitPriceParent, bmUpdateResult);


		return bmUpdateResult;
	}

}
