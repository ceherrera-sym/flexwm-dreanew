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
import java.util.Calendar;
import java.util.StringTokenizer;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.ar.PmPropertyRental;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.op.BmoOrder;


public class PmOrderPropertyTax extends PmObject {
	BmoOrderPropertyTax bmoOrderPropertyTax;

	public PmOrderPropertyTax(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderPropertyTax = new BmoOrderPropertyTax();
		setBmObject(bmoOrderPropertyTax);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderPropertyTax.getPropertyId(), bmoOrderPropertyTax.getBmoProperty()),
				new PmJoin(bmoOrderPropertyTax.getBmoProperty().getDevelopmentBlockId(), bmoOrderPropertyTax.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoOrderPropertyTax.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoOrderPropertyTax.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderPropertyTax = (BmoOrderPropertyTax)autoPopulate(pmConn, new BmoOrderPropertyTax());

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoOrderPropertyTax.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoOrderPropertyTax.setBmoProperty(bmoProperty);

		return bmoOrderPropertyTax;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoOrderPropertyTax = (BmoOrderPropertyTax)bmObject;
		bmoOrderPropertyTax.getAmount().setValue(bmoOrderPropertyTax.getQuantity().toInteger() * bmoOrderPropertyTax.getPrice().toDouble());
		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderPropertyTax.getOrderId().toInteger());

		// Calcula el valor del item
		//bmoOrderPropertyTax.getAmount().setValue(bmoOrderPropertyTax.getPrice().toDouble());

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

		super.save(pmConn, bmoOrderPropertyTax, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
		
		//Guardar el precio en el contrato
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		bmoPropertyRental = (BmoPropertyRental) pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());
		bmoPropertyRental.getCurrentIncome().setValue(bmoOrderPropertyTax.getPrice().toDouble());
		pmPropertyRental.saveSimple(bmoPropertyRental, bmUpdateResult);
		
		return bmUpdateResult;
	}

	public BmUpdateResult create(PmConn pmConn, BmoOrderPropertyTax bmoOrderPropertyTax, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {	
		this.bmoOrderPropertyTax = bmoOrderPropertyTax;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());

		// Calcula el valor del item
		bmoOrderPropertyTax.getAmount().setValue(bmoOrderPropertyTax.getQuantity().toInteger()*
													bmoOrderPropertyTax.getPrice().toDouble());
		super.save(pmConn, bmoOrderPropertyTax, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}


	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderPropertyTax = (BmoOrderPropertyTax)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderPropertyTax.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmoOrderPropertyTax, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		double currentIconme = 0;
		String rentalScheduleDate= "", rentIncrease = "";
		
		// Extraer datos
		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {
			currentIconme = SFServerUtil.stringToDouble(tabs.nextToken());
			rentalScheduleDate = tabs.nextToken();
			rentIncrease = tabs.nextToken();
		}
					
		if (action.equals(BmoOrderPropertyTax.ACTION_CHANGEPRICE)) {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();

			BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
			PmPropertyRental  pmPropertyRental = new PmPropertyRental(getSFParams());

			bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.get(bmObject.getId());
			bmoPropertyRental.getCurrentIncome().setValue(currentIconme);
			bmoPropertyRental.getRentalScheduleDate().setValue(rentalScheduleDate);
			bmoPropertyRental.getRentIncrease().setValue(rentIncrease);

			pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);

			BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
			bmoOrderPropertyTax = (BmoOrderPropertyTax) this.getBy(pmConn, bmoPropertyRental.getPropertyId().toInteger(), bmoOrderPropertyTax.getPropertyId().getName());

			// Calcular meses entre fechas
			rentalScheduleDate = bmoPropertyRental.getRentalScheduleDate().toString();
			rentIncrease = bmoPropertyRental.getRentIncrease() .toString();

			Calendar dateStart = Calendar.getInstance();
			Calendar dateEnd= Calendar.getInstance();

			dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalScheduleDate));
			dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentIncrease));

			int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);

			bmoOrderPropertyTax.getPrice().setValue(bmoPropertyRental.getCurrentIncome().toDouble() * diffMonth);

			BmoOrder bmoOrder = new BmoOrder();
			PmOrder  pmOrder = new PmOrder(getSFParams());
			bmoOrder = (BmoOrder)pmOrder.get(bmoPropertyRental.getOrderId().toInteger());

			this.create(pmConn, bmoOrderPropertyTax,  bmoOrder, bmUpdateResult);
			pmConn.close();
		} 
		return bmUpdateResult;
	}
}
