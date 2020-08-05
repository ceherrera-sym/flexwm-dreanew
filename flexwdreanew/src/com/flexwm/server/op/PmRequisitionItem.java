/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmArea;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.op.PmProduct;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionType;


public class PmRequisitionItem extends PmObject {
	BmoRequisitionItem bmoRequisitionItem;

	public PmRequisitionItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRequisitionItem = new BmoRequisitionItem();
		setBmObject(bmoRequisitionItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRequisitionItem.getProductId(), bmoRequisitionItem.getBmoProduct()),
				new PmJoin(bmoRequisitionItem.getBmoProduct().getProductFamilyId(), bmoRequisitionItem.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoRequisitionItem.getBmoProduct().getProductGroupId(), bmoRequisitionItem.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoRequisitionItem.getBmoProduct().getUnitId(), bmoRequisitionItem.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoRequisitionItem.getAreaId() , bmoRequisitionItem.getBmoArea()),
				new PmJoin(bmoRequisitionItem.getBudgetItemId(), bmoRequisitionItem.getBmoBudgetItem()),
				new PmJoin(bmoRequisitionItem.getBmoBudgetItem().getBudgetId(), bmoRequisitionItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoRequisitionItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoRequisitionItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoRequisitionItem.getBmoBudgetItem().getCurrencyId(), bmoRequisitionItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRequisitionItem = (BmoRequisitionItem)autoPopulate(pmConn, new BmoRequisitionItem());

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoRequisitionItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoRequisitionItem.setBmoProduct(bmoProduct);
		
		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoRequisitionItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoRequisitionItem.setBmoArea(bmoArea);
		
		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoRequisitionItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoRequisitionItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoRequisitionItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRequisitionItem = (BmoRequisitionItem)bmObject;
		// Formatear a 4 digitos
		bmoRequisitionItem.getQuantity().setValue(FlexUtil.roundDouble(bmoRequisitionItem.getQuantity().toDouble(), 4));
		bmoRequisitionItem.getDays().setValue(FlexUtil.roundDouble(bmoRequisitionItem.getDays().toDouble(), 4));
		bmoRequisitionItem.getPrice().setValue(FlexUtil.roundDouble(bmoRequisitionItem.getPrice().toDouble(), 4));
		bmoRequisitionItem.getAmount().setValue(FlexUtil.roundDouble(bmoRequisitionItem.getAmount().toDouble(), 4));

		// Obten la orden de compra
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionItem.getRequisitionId().toInteger());
//		BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
//		// Obten el tipo de oc
//		if (bmoRequisition.getId() > 0) {
//			PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
//			bmoRequisitionType = (BmoRequisitionType) pmRequisitionType.get(pmConn,bmoRequisition.getRequisitionTypeId().toInteger());
//		}
		//Tipo de Orden de compra
		//		PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
		//		BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(pmConn, bmoRequisition.getRequisitionTypeId().toInteger());

		
		// agregar que pueda cambiar items con el permisos especial
		boolean validateAccesSpecial = false;
		if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)
				&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQTRAVEXP))
			validateAccesSpecial = true;
			
		if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_SERVICE)
				&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQCHSERVICE))
			validateAccesSpecial = true;
		
		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_AUTHORIZED
				&& !validateAccesSpecial
//				&& !(bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)
//				&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQTRAVEXP))
				) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Órden de Compra - está Autorizada.");
		} else {
			//si tipo de OC es diferente a estimacion valida si la cantidad en 
//			if(!(bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION))) {
//				if((bmoRequisitionItem.getQuantity().toDouble() % 1) != 0 ) {
//					bmUpdateResult.addMsg("Error: La cantidad debe ser un número entero");
//				}					
//			}
			// Si esta asignado producto, buscar la informacion
			if (bmoRequisitionItem.getProductId().toInteger() > 0) {
				// Obten el registro del producto y su precio vigente
				PmProduct pmProduct = new PmProduct(getSFParams());
				BmoProduct bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoRequisitionItem.getProductId().toInteger());

				bmoRequisitionItem.getName().setValue(bmoProduct.getName().toString());
				
				// Revisa que la cantidad sea numero entero si el producto afecta almacen
//				if (bmoProduct.getInventory().toInteger() > 0) {
//					if (bmoRequisitionItem.getQuantity().toDouble() % 1 != 0) 
//						bmUpdateResult.addError(bmoRequisitionItem.getQuantity().getName(), "<b>La Cantidad del producto debe ser número entero.</b>");
//				}
			}
			
			// Validacion Partidas Presupuestales
//			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//				if (!(bmoRequisitionItem.getBudgetItemId().toInteger() > 0)) {
//					bmUpdateResult.addError(bmoRequisitionItem.getBudgetItemId().getName(), "Seleccione una Partida.");
//				}
//				
//				if (!(bmoRequisitionItem.getAreaId().toInteger() > 0)) {
//					bmUpdateResult.addError(bmoRequisitionItem.getAreaId().getName(), "Seleccione un Departamento");
//				}
//			}

			// Revisa que el precio no sea menor a 0
			if (bmoRequisitionItem.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");

			// Revisa que los dias no sea menor a 0
			if (bmoRequisitionItem.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");

			// Revisa que la cantidad no sea menor a 0
			if (bmoRequisitionItem.getQuantity().toDouble() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");
			
			// Formatear a 4 digitos para bd
			bmoRequisitionItem.getPrice().setValue(FlexUtil.roundDouble(bmoRequisitionItem.getPrice().toDouble(), 4));
			
			// Establecer monto
			double amount = bmoRequisitionItem.getPrice().toDouble() * bmoRequisitionItem.getQuantity().toDouble();
			if (bmoRequisitionItem.getDays().toDouble() > 0)
				amount = bmoRequisitionItem.getPrice().toDouble() * bmoRequisitionItem.getDays().toDouble() * bmoRequisitionItem.getQuantity().toDouble();

			// Calcula el valor del item
			//bmoRequisitionItem.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			bmoRequisitionItem.getAmount().setValue(FlexUtil.roundDouble(amount, 4));

			// Primero agrega el ultimo valor
			if (!bmUpdateResult.hasErrors()) {
				super.save(pmConn, bmoRequisitionItem, bmUpdateResult);

				// Recalcula el subtotal
				pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);
				
				if (validateAccesSpecial) {
					pmRequisition.updatePayments(pmConn, bmoRequisition, bmUpdateResult);
					pmRequisition.createWFloLogRequisition(pmConn, bmoRequisition, "Modificó(*)", bmUpdateResult);
				}
				
				super.save(pmConn, bmoRequisitionItem, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}

	public void deleteItemsByRequisition(PmConn pmConn, BmoRequisition bmoRequisition) throws SFException{
		pmConn.doUpdate("DELETE FROM requisitionitems WHERE rqit_requisitionid = " + bmoRequisition.getId());
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionItem = (BmoRequisitionItem)bmObject;

		// Primero elimina el item
		super.delete(pmConn, bmoRequisitionItem, bmUpdateResult);

		// Recalcula el subtotal
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = new BmoRequisition();
		bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionItem.getRequisitionId().toInteger());
		pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);
		//pmRequisition.save(pmConn, bmoRequisition, bmUpdateResult);



		return bmUpdateResult;
	}
}
