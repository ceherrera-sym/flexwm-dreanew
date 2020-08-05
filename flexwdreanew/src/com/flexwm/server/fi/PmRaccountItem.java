/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 *  
 * 
 */

package com.flexwm.server.fi;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.sf.PmArea;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountItem;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;


public class PmRaccountItem extends PmObject {
	BmoRaccountItem bmoRaccountItem;

	public PmRaccountItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountItem = new BmoRaccountItem();
		setBmObject(bmoRaccountItem);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRaccountItem.getAreaId() , bmoRaccountItem.getBmoArea()),
				new PmJoin(bmoRaccountItem.getBudgetItemId(), bmoRaccountItem.getBmoBudgetItem()),
				new PmJoin(bmoRaccountItem.getBmoBudgetItem().getBudgetId(), bmoRaccountItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoRaccountItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoRaccountItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoRaccountItem.getBmoBudgetItem().getCurrencyId(), bmoRaccountItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRaccountItem = (BmoRaccountItem)autoPopulate(pmConn, new BmoRaccountItem());

		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoRaccountItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoRaccountItem.setBmoArea(bmoArea);

		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoRaccountItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoRaccountItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoRaccountItem;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRaccountItem = (BmoRaccountItem)bmObject;

		if (!(bmoRaccountItem.getId() > 0)) {
			if (bmoRaccountItem.getName().equals("")) 
				bmUpdateResult.addError(bmoRaccountItem.getName().getName(), "El Nombre No Puede Estar Vacio.");

			if (!(bmoRaccountItem.getQuantity().toDouble() > 0)) 
				bmUpdateResult.addError(bmoRaccountItem.getQuantity().getName(), "La Cantidad Debe ser Mayor a 0.");

			if (!(bmoRaccountItem.getPrice().toDouble() > 0)) 
				bmUpdateResult.addError(bmoRaccountItem.getPrice().getName(), "El Precio Debe ser Mayor a $ 0.00.");

			//			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			//				if (!(bmoRaccountItem.getBudgetItemId().toInteger() > 0)) {
			//					bmUpdateResult.addError(bmoRaccountItem.getBudgetItemId().getName(), "Seleccione una Partida.");
			//				}
			//				
			//				if (!(bmoRaccountItem.getAreaId().toInteger() > 0)) {
			//					bmUpdateResult.addError(bmoRaccountItem.getAreaId().getName(), "Seleccione un Departamento");
			//				}
			//			}
		}	

		// Obten la cuenta por pagar
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountItem.getRaccountId().toInteger());

		// Si la CxC ya está autorizada, no se puede hacer movimientos
		if (bmoRaccountItem.getId() > 0 && bmoRaccount.getStatus().toChar() != BmoRaccount.STATUS_REVISION) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Cobrar - está Autorizada.");
		} else {
			
			// Formatear a 4 digitos para bd
			bmoRaccountItem.getPrice().setValue(FlexUtil.roundDouble(bmoRaccountItem.getPrice().toDouble(), 4));
			
			// Calcula el valor del item
			bmoRaccountItem.getAmount().setValue(FlexUtil.roundDouble(
					bmoRaccountItem.getPrice().toDouble() * bmoRaccountItem.getQuantity().toDouble()));

			// Primero agrega el ultimo valor
			super.save(pmConn, bmoRaccountItem, bmUpdateResult);

			// Recalcula el subtotal
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
		}

		// Valida que la suma de CxC menos las Notas de Credito no sea al Total del Pedido
		if (bmoRaccount.getOrderId().toInteger() > 0) {
			// Si es Nota de credito
			if (bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_CREDITNOTE) {
				// Si no tiene permisos para ingresar mas notas de credito que el valor del pedido
				if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_OVERDRAFTCREDITNOTES)) {
					PmOrder pmOrder = new PmOrder(getSFParams());
					if (!pmOrder.validateOrderVsRaccounts(pmConn, bmoRaccount.getOrderId().toInteger()))
						bmUpdateResult.addError(bmoRaccountItem.getAmount().getName(), "No tiene permisos para Ingresar Notas de Credito que disminuyan Monto Cobranza < Valor Pedido (" + BmoOrder.ACCESS_OVERDRAFTCREDITNOTES + ").");
				}
			}
		}

		// Primero agrega el ultimo valor
		super.save(pmConn, bmoRaccountItem, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRaccountItem = (BmoRaccountItem)bmObject;

		// Obtener la Cuenta por Pagar
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountItem.getRaccountId().toInteger());

		PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
		BmoRaccountType bmoRaccountType = (BmoRaccountType)pmRaccountType.get(pmConn, bmoRaccount.getRaccountTypeId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos			
		if (bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {				
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Cobrar - está Autorizada.");
		} else {

			//Validar que existan aplicaciones
			if (bmoRaccountType.getType().equals(BmoRaccountType.TYPE_DEPOSIT) 
					&& (bmoRaccount.getBalance().toDouble() < bmoRaccount.getTotal().toDouble())) {
				bmUpdateResult.addMsg("No se puede eliminar movimientos sobre la cuenta por cobrar - existen aplicaciones");				
			}

			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
		}	
		return bmUpdateResult;
	}
}
