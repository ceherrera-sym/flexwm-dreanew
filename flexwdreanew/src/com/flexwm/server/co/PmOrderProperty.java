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
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.flexwm.shared.op.BmoOrder;


public class PmOrderProperty extends PmObject {
	BmoOrderProperty bmoOrderProperty;

	public PmOrderProperty(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderProperty = new BmoOrderProperty();
		setBmObject(bmoOrderProperty);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderProperty.getPropertyId(), bmoOrderProperty.getBmoProperty()),
				new PmJoin(bmoOrderProperty.getBmoProperty().getDevelopmentBlockId(), bmoOrderProperty.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoOrderProperty.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoOrderProperty.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderProperty = (BmoOrderProperty)autoPopulate(pmConn, new BmoOrderProperty());

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoOrderProperty.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoOrderProperty.setBmoProperty(bmoProperty);

		return bmoOrderProperty;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoOrderProperty = (BmoOrderProperty)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderProperty.getOrderId().toInteger());

		// Calcula el valor del item
		bmoOrderProperty.getAmount().setValue(bmoOrderProperty.getPrice().toDouble() 
				+ bmoOrderProperty.getExtraLand().toDouble()
				+ bmoOrderProperty.getExtraConstruction().toDouble()
				+ bmoOrderProperty.getExtraOther().toDouble());
		
		// Actualizar partida presupuestal en el Pedido
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			// Obtener partida a traves de la vivienda seleccionada
			int budgetItemId = -1;
			budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
			if (budgetItemId > 0)
				bmoOrder.getDefaultBudgetItemId().setValue(budgetItemId);
			else
				bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), 
						"No se encontró una Partida Presp. de Ingresos (sobre la Etapa de Des.) de la vivienda seleccionada");
		}

		super.save(pmConn, bmoOrderProperty, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult create(PmConn pmConn, BmoOrderProperty bmoOrderProperty, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {	
		this.bmoOrderProperty = bmoOrderProperty;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());

		// Obten la propiedad
		PmProperty pmProperty = new PmProperty(getSFParams());
		BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoOrderProperty.getPropertyId().toInteger());

		// Obten el modelo
		PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
		BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmConn, bmoProperty.getPropertyModelId().toInteger());

		// Obten precio vigente
		BmoPropertyModelPrice bmoPropertyModelPrice = pmPropertyModel.getCurrentPrice(pmConn, bmoPropertyModel, bmUpdateResult);

		// Asigna precio
		bmoOrderProperty.getPrice().setValue(bmoPropertyModelPrice.getPrice().toDouble());

		// Asigna valor de terreno excedente
		bmoOrderProperty.getExtraLand().setValue(
				(bmoProperty.getLandSize().toDouble() - bmoPropertyModel.getLandSize().toDouble())
				* bmoPropertyModelPrice.getMeterPrice().toDouble());	

		// Asigna valor de construccion excedente
		bmoOrderProperty.getExtraConstruction().setValue(
				(bmoProperty.getConstructionSize().toDouble() - bmoPropertyModel.getConstructionSize().toDouble())
				* bmoPropertyModelPrice.getConstructionMeterPrice().toDouble());	

		// Asigna valores extras
		bmoOrderProperty.getExtraOther().setValue(
				bmoProperty.getExtraPrice().toDouble() + 
				((bmoProperty.getPublicLandSize().toDouble() - bmoPropertyModel.getPublicLandSize().toDouble())
						* bmoPropertyModelPrice.getPublicMeterPrice().toDouble()));	

		// Calcula el valor del item
		bmoOrderProperty.getAmount().setValue(bmoOrderProperty.getPrice().toDouble() 
				+ bmoOrderProperty.getExtraLand().toDouble()
				+ bmoOrderProperty.getExtraConstruction().toDouble()
				+ bmoOrderProperty.getExtraOther().toDouble());

		super.save(pmConn, bmoOrderProperty, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}


	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderProperty = (BmoOrderProperty)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderProperty.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		}
		return bmUpdateResult;
	}
}
