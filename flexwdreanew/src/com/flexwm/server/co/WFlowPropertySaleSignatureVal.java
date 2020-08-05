package com.flexwm.server.co;

import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertySaleDetail;
import com.flexwm.server.wf.IWFlowValidate;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowPropertySaleSignatureVal implements IWFlowValidate {

	@Override
	public void validate(SFParams sFParams, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Se obtiene la venta
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		bmoPropertySale = (BmoPropertySale)pmPropertySale.getBy(bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoPropertySale.getOrderId().getName());

		// Obtener el detalle de la Venta
		PmPropertySaleDetail pmPropertySaleDetail = new PmPropertySaleDetail(sFParams);
		BmoPropertySaleDetail bmoPropertySaleDetail = new BmoPropertySaleDetail();
		bmoPropertySaleDetail = (BmoPropertySaleDetail)pmPropertySaleDetail.getBy(bmoPropertySale.getId(),
				bmoPropertySaleDetail.getPropertySaleId().getName());		

		// Si la tarea esta al 100 y no se ha metido el Notario Ni el Num. Escritura en el detalle de la venta, rechaza avance
		if ((bmoWFlowStep.getProgress().toInteger() == 100)
				&& (!(bmoPropertySaleDetail.getNotary().toInteger() > 0)
						|| bmoPropertySaleDetail.getWrintingNumber().toString().equals(""))) {
			bmUpdateResult.addError(bmoWFlowStep.getName().getName(), "No se ha colocado el Notario o Núm. de Escritura en el Detalle de la Venta");		
		}	
	}

}
