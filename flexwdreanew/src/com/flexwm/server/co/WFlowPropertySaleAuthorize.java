package com.flexwm.server.co;

import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowPropertySaleAuthorize implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se autoriza la venta de inmueble dependiendo avance de la tarea
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		bmoPropertySale = (BmoPropertySale)pmPropertySale.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoPropertySale.getOrderId().getName());

		// Si la venta de inmueble no esta autorizado y la tarea esta al 100, cambia status
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoPropertySale.getStatus().toChar() != BmoPropertySale.STATUS_AUTHORIZED) {
			bmoPropertySale.getStatus().setValue(BmoPropertySale.STATUS_AUTHORIZED);
			pmPropertySale.save(pmConn, bmoPropertySale, bmUpdateResult);			
		}
		// Si el avance es menor a 100 y la venta de inmueble esta autorizado, regresarlo
		else if (bmoWFlowStep.getProgress().toInteger() < 100 && bmoPropertySale.getStatus().toChar() == BmoPropertySale.STATUS_AUTHORIZED) {
			bmoPropertySale.getStatus().setValue(BmoPropertySale.STATUS_REVISION);
			pmPropertySale.saveSimple(pmConn, bmoPropertySale, bmUpdateResult);	
		}
	}
}
