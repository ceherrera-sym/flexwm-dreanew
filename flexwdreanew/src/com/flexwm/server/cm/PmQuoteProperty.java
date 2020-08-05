/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

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
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.co.PmPropertyModel;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteProperty;


public class PmQuoteProperty extends PmObject {
	BmoQuoteProperty bmoQuoteProperty;

	public PmQuoteProperty(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuoteProperty = new BmoQuoteProperty();
		setBmObject(bmoQuoteProperty);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuoteProperty.getPropertyId(), bmoQuoteProperty.getBmoProperty()),
				new PmJoin(bmoQuoteProperty.getBmoProperty().getDevelopmentBlockId(), bmoQuoteProperty.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoQuoteProperty.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoQuoteProperty.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuoteProperty = (BmoQuoteProperty)autoPopulate(pmConn, new BmoQuoteProperty());

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoQuoteProperty.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoQuoteProperty.setBmoProperty(bmoProperty);

		return bmoQuoteProperty;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoQuoteProperty = (BmoQuoteProperty)bmObject;

		// Obten la cotizacion
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoQuoteProperty.getQuoteId().toInteger());
		
		// Obtener la oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());

		// Obten la propiedad
		PmProperty pmProperty = new PmProperty(getSFParams());
		BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoQuoteProperty.getPropertyId().toInteger());

		// Revisar que no exista otro inmueble en la cotizacion
		checkQuoteProperties(pmConn, bmoQuote, bmUpdateResult);
		
		// Actualizar partida presupuestal en la OP/COT
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			// Obtener partida a traves de la vivienda seleccionada
			int budgetId = 0, budgetItemId = -1;
			budgetId = bmoProperty.getBmoDevelopmentBlock().getBmoDevelopmentPhase().getBudgetId().toInteger();
			printDevLog("Existe idPrespuesto en Etapa -> " +budgetId);
			if (budgetId > 0) {
				// Obtener la partida de ingresos
				String sql = " SELECT bgit_budgetitemid FROM budgetitems " +
						" WHERE bgit_budgetid = " + budgetId + 
						" AND bgit_budgetitemtypeid = " + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger();
				pmConn.doFetch(sql);
				if (pmConn.next()) budgetItemId = pmConn.getInt("bgit_budgetitemid");
				
				if (budgetItemId > 0) {
					bmoQuote.getBudgetItemId().setValue(budgetItemId);
					bmoOpportunity.getBudgetItemId().setValue(budgetItemId);
					pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);
				} else
					bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), 
							"No se encontró una Partida Presp. de Ingresos (sobre la Etapa de Des.) de la vivienda seleccionada");
			} else bmUpdateResult.addError(bmoOpportunity.getBudgetItemId().getName(), 
					"No se encontró un Presupuesto en la Etapa de Desarrollo");
		}

		// Si es nuevo, crealo
		if (!(bmoQuoteProperty.getId() > 0)) {
			create(pmConn, bmoQuoteProperty, bmoQuote, bmUpdateResult);
		} else {

			// Calcula el valor del item
			bmoQuoteProperty.getAmount().setValue(bmoQuoteProperty.getPrice().toDouble() 
					+ bmoQuoteProperty.getExtraLand().toDouble()
					+ bmoQuoteProperty.getExtraConstruction().toDouble()
					+ bmoQuoteProperty.getExtraOther().toDouble());

			super.save(pmConn, bmoQuoteProperty, bmUpdateResult);

			// Modelo de la vivienda
			PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
			BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmConn, bmoProperty.getPropertyModelId().toInteger());
						
			// Actualiza nombre, modelo y empresa de la cotización
			bmoQuote.getName().setValue(bmoPropertyModel.getBmoDevelopment().getCode().toString() + " - " + bmoPropertyModel.getName().toString());
			bmoQuote.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());
			// Recalcula el subtotal
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

			// Actualiza nombre, modelo y empresa de la oportunidad
			bmoOpportunity.getPropertyModelId().setValue(bmoPropertyModel.getId());
			bmoOpportunity.getName().setValue(bmoPropertyModel.getBmoDevelopment().getCode().toString()
					+ " - " + bmoPropertyModel.getName().toString());
			bmoOpportunity.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());
			pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);

			// Guarda por ultima vez
			super.save(pmConn, bmoQuoteProperty, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	public BmUpdateResult create(PmConn pmConn, BmoQuoteProperty bmoQuoteProperty, BmoQuote bmoQuote, BmUpdateResult bmUpdateResult) throws SFException {	
		this.bmoQuoteProperty = bmoQuoteProperty;

		// Obten el pedido
		PmQuote pmQuote = new PmQuote(getSFParams());

		// Revisar que no exista otro inmueble en la cotizacion
		checkQuoteProperties(pmConn, bmoQuote, bmUpdateResult);

		// Obten la propiedad
		PmProperty pmProperty = new PmProperty(getSFParams());
		BmoProperty bmoProperty = (BmoProperty)pmProperty.get(pmConn, bmoQuoteProperty.getPropertyId().toInteger());

		// Obten el modelo
		PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
		BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmConn, bmoProperty.getPropertyModelId().toInteger());

		// Obten precio vigente
		BmoPropertyModelPrice bmoPropertyModelPrice = pmPropertyModel.getCurrentPrice(pmConn, bmoPropertyModel, bmUpdateResult);

		// Asigna precio
		bmoQuoteProperty.getPrice().setValue(bmoPropertyModelPrice.getPrice().toDouble());

		// Asigna valor de terreno excedente
		bmoQuoteProperty.getExtraLand().setValue(
				(bmoProperty.getLandSize().toDouble() - bmoPropertyModel.getLandSize().toDouble())
				* bmoPropertyModelPrice.getMeterPrice().toDouble());	

		// Asigna valor de construccion excedente
		bmoQuoteProperty.getExtraConstruction().setValue(
				(bmoProperty.getConstructionSize().toDouble() - bmoPropertyModel.getConstructionSize().toDouble())
				* bmoPropertyModelPrice.getConstructionMeterPrice().toDouble());	

		// Asigna valores extras
		bmoQuoteProperty.getExtraOther().setValue(
				bmoProperty.getExtraPrice().toDouble() + 
				((bmoProperty.getPublicLandSize().toDouble() - bmoPropertyModel.getPublicLandSize().toDouble())
						* bmoPropertyModelPrice.getPublicMeterPrice().toDouble()));	

		// Calcula el valor del item
		bmoQuoteProperty.getAmount().setValue(bmoQuoteProperty.getPrice().toDouble() 
				+ bmoQuoteProperty.getExtraLand().toDouble()
				+ bmoQuoteProperty.getExtraConstruction().toDouble()
				+ bmoQuoteProperty.getExtraOther().toDouble());

		super.save(pmConn, bmoQuoteProperty, bmUpdateResult);

		// Actualiza nombre, modelo y empresa de la cotización
		bmoQuote.getName().setValue(bmoPropertyModel.getBmoDevelopment().getCode().toString() + " - " + bmoPropertyModel.getName().toString());
		bmoQuote.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());
		// Recalcula el subtotal
		pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

		// Actualiza nombre, modelo y empresa de la oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());
		bmoOpportunity.getPropertyModelId().setValue(bmoPropertyModel.getId());
		bmoOpportunity.getName().setValue(bmoPropertyModel.getBmoDevelopment().getCode().toString()
				+ " - " + bmoPropertyModel.getName().toString());
		bmoOpportunity.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());
		pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);
		
		// Actualiza nombre, modelo y empresa del flujo
		BmoWFlow bmoWFlow = new BmoWFlow();
		PmWFlow pmWFlow  = new PmWFlow(getSFParams());
		bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoOpportunity.getWFlowId().toInteger());
		bmoWFlow.getName().setValue(bmoOpportunity.getName().toString());
		bmoWFlow.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
		pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

		// Almacena al final
		super.save(pmConn, bmoQuoteProperty, bmUpdateResult);

		return bmUpdateResult;
	}

	public void checkQuoteProperties(PmConn pmConn, BmoQuote bmoQuote, BmUpdateResult bmUpdateResult) throws SFException {
		pmConn.doFetch("SELECT * FROM quoteproperties WHERE qupy_quoteid = " + bmoQuote.getId());
		if (pmConn.next())
			bmUpdateResult.addError(bmoQuote.getName().getName(), "No se puede asignar otro Inmueble: ya tiene asignado uno.");
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuoteProperty = (BmoQuoteProperty)bmObject;

		// Obten el pedido
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteProperty.getQuoteId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Elimina todos los extras de la cotización
			pmConn.doUpdate("DELETE FROM quotepropertymodelextras WHERE qupx_quoteid = " + bmoQuote.getId());

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

		}
		return bmUpdateResult;
	}
}
