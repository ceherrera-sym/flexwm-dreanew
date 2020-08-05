package com.flexwm.server.cm;

import com.flexwm.server.wf.IWFlowValidate;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WflowOrderReturnedVal implements IWFlowValidate {

	@Override
	public void validate(SFParams sFParams, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se obtiene el proyecto
		//		PmProject pmProject = new PmProject(sFParams);
		//		BmoProject bmoProject = (BmoProject)pmProject.get(bmoWFlowStep.getBmoWFlow().getCallerId().toInteger());
		//		
		//		// Obtener el pedido del proyecto
		//		PmOrder pmOrder = new PmOrder(sFParams);
		//		BmoOrder bmoOrder = new BmoOrder();
		//		bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());
		//		
		//		// Obtener la sección de almacén del pedido
		//		PmWhSection pmWhSection = new PmWhSection(sFParams);
		//		BmoWhSection bmoWhSection = new BmoWhSection();
		//		bmoWhSection = (BmoWhSection)pmWhSection.getBy(bmoOrder.getId(), bmoWhSection.getOrderId().getName());
		//		
		//		// Obtener el stock en el almacen del pedido
		//		PmWhStock pmWhStock = new PmWhStock(sFParams);
		//		int stockQuantity = pmWhStock.stockQuantityInWhSection(bmoWhSection.getId());
		//		
		//		// Si el pedido no esta autorizado y la tarea esta al 100, cambia status
		//		if (bmoWFlowStep.getProgress().toInteger() == 100 && stockQuantity > 0) {
		//			bmUpdateResult.addError(bmoWFlowStep.getCode().getName(), "No se ha devuelto el Pedido Completo.");		
		//		}	
	}

}
