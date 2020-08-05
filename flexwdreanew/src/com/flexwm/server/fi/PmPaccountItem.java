/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 *  
 * 
 */

package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.op.PmRequisitionReceiptItem;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountItem;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoRequisitionReceiptItem;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmArea;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;


public class PmPaccountItem extends PmObject {
	BmoPaccountItem bmoPaccountItem;

	public PmPaccountItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPaccountItem = new BmoPaccountItem();
		setBmObject(bmoPaccountItem);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPaccountItem.getAreaId() , bmoPaccountItem.getBmoArea()),
				new PmJoin(bmoPaccountItem.getBudgetItemId(), bmoPaccountItem.getBmoBudgetItem()),
				new PmJoin(bmoPaccountItem.getBmoBudgetItem().getBudgetId(), bmoPaccountItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoPaccountItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoPaccountItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoPaccountItem.getBmoBudgetItem().getCurrencyId(), bmoPaccountItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoPaccountItem = (BmoPaccountItem)autoPopulate(pmConn, new BmoPaccountItem());
		
		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoPaccountItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoPaccountItem.setBmoArea(bmoArea);
		
		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoPaccountItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoPaccountItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoPaccountItem;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	};

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoPaccountItem = (BmoPaccountItem)bmObject;	

		if (!(bmoPaccountItem.getId() > 0)) {
			if (bmoPaccountItem.getName().equals("")) {
				bmUpdateResult.addError(bmoPaccountItem.getName().getName(), "El Nombre No Puede Estar Vacio.");
			}

			if (!(bmoPaccountItem.getQuantity().toDouble() > 0)) {
				bmUpdateResult.addError(bmoPaccountItem.getQuantity().getName(), "La Cantidad Debe ser Mayor a 0.");
			}

			if (!(bmoPaccountItem.getPrice().toDouble() > 0)) {
				bmUpdateResult.addError(bmoPaccountItem.getPrice().getName(), "El Precio Debe ser Mayor a $ 0.00.");
			}
			
//			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//				if (!(bmoPaccountItem.getBudgetItemId().toInteger() > 0)) {
//					bmUpdateResult.addError(bmoPaccountItem.getBudgetItemId().getName(), "Seleccione una Partida.");
//				}
//				
//				if (!(bmoPaccountItem.getAreaId().toInteger() > 0)) {
//					bmUpdateResult.addError(bmoPaccountItem.getAreaId().getName(), "Seleccione un Departamento");
//				}
//			}
		}	

		// Obten la cuenta por pagar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountItem.getPaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos
		if (bmoPaccountItem.getId() > 0 && bmoPaccount.getStatus().toChar() != BmoPaccount.STATUS_REVISION) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Pagar - está Autorizada.");
		} else {

			// Formatear a 4 digitos para bd
			bmoPaccountItem.getPrice().setValue(FlexUtil.roundDouble(bmoPaccountItem.getPrice().toDouble()));
			// Calcula el valor del item
			bmoPaccountItem.getAmount().setValue(FlexUtil.roundDouble(
					(bmoPaccountItem.getPrice().toDouble() * 
					bmoPaccountItem.getQuantity().toDouble())-bmoPaccountItem.getDiscount().toDouble(), 4)
					);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}
	
	public BmUpdateResult saveXML(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoPaccountItem = (BmoPaccountItem)bmObject;	

		if (!(bmoPaccountItem.getId() > 0)) {
			if (bmoPaccountItem.getName().equals("")) {
				bmUpdateResult.addError(bmoPaccountItem.getName().getName(), "El Nombre No Puede Estar Vacio.");
			}

//			if (!(bmoPaccountItem.getQuantity().toInteger() > 0)) {
//				bmUpdateResult.addError(bmoPaccountItem.getQuantity().getName(), "La Cantidad Debe ser Mayor a 0.");
//			}
//
//			if (!(bmoPaccountItem.getPrice().toDouble() > 0)) {
//				bmUpdateResult.addError(bmoPaccountItem.getPrice().getName(), "El Precio Debe ser Mayor a $ 0.00.");
//			}
		}	

		// Obten la cuenta por pagar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountItem.getPaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos
		if (bmoPaccountItem.getId() > 0 && bmoPaccount.getStatus().toChar() != BmoPaccount.STATUS_REVISION) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Pagar - está Autorizada.");
		} else {

			bmoPaccountItem.getPrice().setValue(FlexUtil.roundDouble(bmoPaccountItem.getPrice().toDouble(), 6));
			// Calcula el valor del item
			bmoPaccountItem.getAmount().setValue((bmoPaccountItem.getPrice().toDouble() * 
					bmoPaccountItem.getQuantity().toDouble())-bmoPaccountItem.getDiscount().toDouble());

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			//pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}


	// Crea los items del recibo de orden de compra
	public void createItemsFromRequisitionReceipt(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {

		// Validar que no existan items creados
		if (!itemsFromRequisitionReceiptExist(pmConn, bmoPaccount.getId(), bmUpdateResult)) {
			// Crear los items
			BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
			BmFilter filterRequisitionReceiptItem = new BmFilter();

//			PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
//			BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
//
//			PmRequisition pmRequisition = new PmRequisition(getSFParams());
//			BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

			// Actuliza montos
//			double amount = 0, quantityRerc = 0 , quantityReqi = 0;
//			String sql = " SELECT sum(reit_amount) as amount, sum(reit_quantity) as quantity FROM requisitionreceiptitems " +
//					" WHERE reit_requisitionreceiptid = " + bmoRequisitionReceipt.getId() + 
//					" AND reit_quantity > 0 ";		
//			pmConn.doFetch(sql);
//			if (pmConn.next())  { 
//				amount = pmConn.getDouble("amount");
//				quantityRerc = pmConn.getDouble("quantity");
//			}
//
//			//Obtener la cantidad de items en la OC
//			sql = " SELECT sum(rqit_quantity) as quantity FROM requisitionitems " +
//					" WHERE rqit_requisitionid = " + bmoRequisition.getId() + 
//					" AND rqit_quantity > 0 ";		
//			pmConn.doFetch(sql);
//			if (pmConn.next())  {
//				quantityReqi = pmConn.getDouble("quantity");
//			}

			//Descuentos en OC
//			double discount = 0;

//			if (quantityRerc > 0) {
//				discount = (bmoRequisition.getDiscount().toDouble() / quantityReqi) * quantityRerc;
//			}

			//RetencionesDescuentos en OC
//			double holdBack = 0;

//			if (quantityRerc > 0) {
//				holdBack = (bmoRequisition.getHoldBack().toDouble() / quantityReqi) * quantityRerc;
//			}

//			double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
//			double tax = 0;

			filterRequisitionReceiptItem.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoPaccount.getRequisitionReceiptId().toInteger());
			Iterator<BmObject> listRequisitionReceiptItem = new PmRequisitionReceiptItem(getSFParams()).list(pmConn, filterRequisitionReceiptItem).iterator();			

			while (listRequisitionReceiptItem.hasNext()) {
				bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();

				//Crear un nuevo item en CXP			
				BmoPaccountItem bmoPaccountItemNew = new BmoPaccountItem();
				bmoPaccountItemNew.getPaccountId().setValue(bmoPaccount.getId());
				bmoPaccountItemNew.getRequisitionReceiptItemId().setValue(bmoRequisitionReceiptItem.getId());
				bmoPaccountItemNew.getQuantity().setValue(bmoRequisitionReceiptItem.getQuantity().toDouble());
				bmoPaccountItemNew.getName().setValue(bmoRequisitionReceiptItem.getName().toString());
				bmoPaccountItemNew.getPrice().setValue(bmoRequisitionReceiptItem.getPrice().toDouble());
				bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());

				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//					if (!(bmoRequisitionReceiptItem.getBudgetItemId().toInteger() > 0))
//						bmUpdateResult.addError(bmoRequisitionReceiptItem.getBudgetItemId().getName(), "El Item del R.O.C. no tiene asignada una Partida Presupuestal");
					bmoPaccountItemNew.getBudgetItemId().setValue(bmoRequisitionReceiptItem.getBudgetItemId().toInteger());
					bmoPaccountItemNew.getAreaId().setValue(bmoRequisitionReceiptItem.getAreaId().toInteger());
				}
				//Aplicar el iva a la cantidad del item
				/*if (bmoRequisitionReceipt.getTax().toDouble() > 0) {
					tax = bmoRequisitionReceiptItem.getAmount().toDouble() * taxRate;
					bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
				} else {
					bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
				}*/


				/*if (bmoRequisition.getHoldBack().toDouble() > 0) {
					bmoPaccountItemNew.getAmount().setValue(bmoPaccountItemNew.getAmount().toDouble() - holdBack);
				}*/

				super.save(pmConn, bmoPaccountItemNew, bmUpdateResult);
			}
		}	
	}

	// Valida que existan ya items en la cuenta por pagar
	public boolean itemsFromRequisitionReceiptExist(PmConn pmConn, int paccountId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		int items = 0;
		String sql = "";

		sql = "SELECT count(pait_paccountitemid) AS items FROM paccountitems " +
				"WHERE pait_paccountid = " + paccountId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("items");
		}

		if (items > 0) result = true;

		return result;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccountItem = (BmoPaccountItem)bmObject;

		// Obtener la Cuenta por Pagar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(bmoPaccountItem.getPaccountId().toInteger());

		PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
		BmoPaccountType bmoPaccountType = (BmoPaccountType)pmPaccountType.get(bmoPaccount.getPaccountTypeId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos			
		if (bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_AUTHORIZED) {				
			bmUpdateResult.addMsg("No se puede eliminar movimientos sobre la cuenta por pagar - ya está Autorizada.");
		} else {
			// Validar que existan aplicaciones
			if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_DEPOSIT) 
					&& (bmoPaccount.getTotal().toDouble() < bmoPaccount.getTotal().toDouble())) {
				bmUpdateResult.addMsg("No se puede eliminar movimientos sobre la cuenta por pagar - existen aplicaciones");				
			}

			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	public BmUpdateResult deleteRequisitionAdvance(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccountItem = (BmoPaccountItem)bmObject;

		//elimina el item
		super.delete(pmConn, bmoPaccountItem, bmUpdateResult);

		return bmUpdateResult;
	}


}
