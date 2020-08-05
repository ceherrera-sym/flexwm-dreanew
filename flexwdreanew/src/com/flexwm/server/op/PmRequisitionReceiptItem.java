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
import java.util.Iterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmArea;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionReceiptItem;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;


public class PmRequisitionReceiptItem extends PmObject {
	BmoRequisitionReceiptItem bmoRequisitionReceiptItem;

	public PmRequisitionReceiptItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
		setBmObject(bmoRequisitionReceiptItem);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRequisitionReceiptItem.getRequisitionItemId(), bmoRequisitionReceiptItem.getBmoRequisitionItem()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoRequisitionItem().getProductId(),bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getProductFamilyId(), bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getProductGroupId(), bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getUnitId(), bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getBmoUnit()),
				new PmJoin(bmoRequisitionReceiptItem.getAreaId() , bmoRequisitionReceiptItem.getBmoArea()),
				new PmJoin(bmoRequisitionReceiptItem.getBudgetItemId(), bmoRequisitionReceiptItem.getBmoBudgetItem()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoBudgetItem().getBudgetId(), bmoRequisitionReceiptItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoRequisitionReceiptItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoRequisitionReceiptItem.getBmoBudgetItem().getCurrencyId(), bmoRequisitionReceiptItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)autoPopulate(pmConn, new BmoRequisitionReceiptItem());

		// BmoRequisitionItem
		BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
		int requisitionItemId = (int)pmConn.getInt(bmoRequisitionItem.getIdFieldName());
		if (requisitionItemId > 0) bmoRequisitionReceiptItem.setBmoRequisitionItem((BmoRequisitionItem) new PmRequisitionItem(getSFParams()).populate(pmConn));
		else bmoRequisitionReceiptItem.setBmoRequisitionItem(bmoRequisitionItem);
		
		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoRequisitionReceiptItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoRequisitionReceiptItem.setBmoArea(bmoArea);
		
		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoRequisitionReceiptItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoRequisitionReceiptItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoRequisitionReceiptItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;

		// Obten el recibo de la OC
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoRequisitionReceiptItem.getRequisitionReceiptId().toInteger());

		// Si el recibo ya está autorizada, no se puede hacer movimientos
		if (bmoRequisitionReceipt.getStatus().toChar() != BmoRequisitionReceipt.STATUS_REVISION) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Recibo - ya está Autorizado.");
		} else {	
			// Se almacena la informacion base para proceder con modificaciones adicionales
			super.save(pmConn, bmoRequisitionReceiptItem, bmUpdateResult);

			// Revisar que no exceda la cantidad del item, si no tiene permiso especial
			if (bmoRequisitionReceipt.getType().equals(BmoRequisitionReceipt.TYPE_RECEIPT)) {
				if (!getSFParams().hasSpecialAccess(BmoRequisitionReceipt.CHANGE_ITEMRECEIPTS)) {
					if (!checkQuantityItem(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult))
						bmUpdateResult.addError(bmoRequisitionReceiptItem.getQuantity().getName(), "La cantidad Recibida Excede a la Órden de Compra.");
				} 
			} else {
				// Se esta haciendo devolucion, revisar que no se haya devuelto mas de lo ingresado
				if (!checkQuantityReturnedItem(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult))
					bmUpdateResult.addError(bmoRequisitionReceiptItem.getQuantity().getName(), "La cantidad a Devolver Excede a lo Recibido.");
			}

			// Revisa que la cantidad no sea menor a 0
			if (bmoRequisitionReceiptItem.getQuantity().toDouble() < 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser menor a 0.");	

			// Actualiza estatus de saldo a todos los items relacionados
			updateQuantityBalance(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

			// Actualiza estatus de devoluciones a todos los items relacionados
			updateQuantityReturned(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

			// Formatear a 4 digitos para bd
			bmoRequisitionReceiptItem.getPrice().setValue(FlexUtil.roundDouble(bmoRequisitionReceiptItem.getPrice().toDouble(),4));
			
			// Calcula el valor del item
			double amount = FlexUtil.roundDouble(bmoRequisitionReceiptItem.getPrice().toDouble(),4)
					* bmoRequisitionReceiptItem.getQuantity().toDouble();

			//Si los dias son mayores a 0 multiplicar por los dias
			if (bmoRequisitionReceiptItem.getDays().toDouble() > 0)
				amount = amount * bmoRequisitionReceiptItem.getDays().toDouble();

			bmoRequisitionReceiptItem.getAmount().setValue(FlexUtil.roundDouble(amount, 4));

			// Primero agrega el ultimo valor
			bmUpdateResult = super.save(pmConn, bmoRequisitionReceiptItem, bmUpdateResult);

			// Actualiza cantidad recibida por item
			updateQuantityReceived(pmConn, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger(), bmUpdateResult);

			// Recalcula el subtotal de la orden de copmra
			pmRequisitionReceipt.updateBalance(pmConn, bmoRequisitionReceipt, bmUpdateResult);

			// Afectaciones a almacenes, si no es nuevo registro
			if (bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() > 0)
				updateWhMovItem(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

			// Si la cantidad es menor a un borra serial
			if (!(bmoRequisitionReceiptItem.getQuantity().toDouble() > 0))
				bmoRequisitionReceiptItem.getSerial().setValue("");

			// Realiza la ultima modificacion
			bmUpdateResult = super.save(pmConn, bmObject, bmUpdateResult);
		} 

		return bmUpdateResult;
	}

	public void createItemsFromRequisition(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear los items
		BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();

		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());

		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

		if (bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_AUTHORIZED) {

			PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
			BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(pmConn, bmoRequisition.getRequisitionTypeId().toInteger());

			BmFilter filterRequisitionItem = new BmFilter();
			filterRequisitionItem.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisitionReceipt.getRequisitionId().toInteger());
			Iterator<BmObject> listRequisitionItem = new PmRequisitionItem(getSFParams()).list(pmConn, filterRequisitionItem).iterator();			

			while (listRequisitionItem.hasNext()) {
				bmoRequisitionItem = (BmoRequisitionItem)listRequisitionItem.next();

				// Validar si tiene numero de rastreo			
				if (bmoRequisitionItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {

					// Cuando es de tipo serial, crear un numero de items = a la cantidad recibida (FORZAR de decimal a entero)
					for (int i = 0; i < (int)bmoRequisitionItem.getQuantity().toDouble(); i++) {
						//Crear un nuevo item en el recibo
						BmoRequisitionReceiptItem bmoRequisitionReceiptItemNew = new BmoRequisitionReceiptItem();
						//Crear Items
						bmoRequisitionReceiptItemNew.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
						bmoRequisitionReceiptItemNew.getRequisitionItemId().setValue(bmoRequisitionItem.getId());
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//							if (!(bmoRequisition.getBudgetItemId().toInteger() > 0))
//								bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "El Items de la O.C. no tiene asignada una Partida Presupuestal");
							bmoRequisitionReceiptItemNew.getBudgetItemId().setValue(bmoRequisitionItem.getBudgetItemId().toInteger());
							bmoRequisitionReceiptItemNew.getAreaId().setValue(bmoRequisitionItem.getAreaId().toInteger());
						}
						bmoRequisitionReceiptItemNew.getQuantity().setValue(0);
						bmoRequisitionReceiptItemNew.getDays().setValue(bmoRequisitionItem.getDays().toDouble());
						bmoRequisitionReceiptItemNew.getName().setValue(bmoRequisitionItem.getName().toString());				
						bmoRequisitionReceiptItemNew.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());
						bmoRequisitionReceiptItemNew.getAmount().setValue(0);

						// Actualiza estatus de saldo a todos los items relacionados
						updateQuantityBalance(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);

						// Actualiza estatus de devoluciones a todos los items relacionados
						updateQuantityReturned(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);

						super.save(pmConn, bmoRequisitionReceiptItemNew, bmUpdateResult);
					}
				} else {
					// Crear un nuevo item en el recibo
					BmoRequisitionReceiptItem bmoRequisitionReceiptItemNew = new BmoRequisitionReceiptItem();
					bmoRequisitionReceiptItemNew.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
					bmoRequisitionReceiptItemNew.getRequisitionItemId().setValue(bmoRequisitionItem.getId());
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//						if (!(bmoRequisition.getBudgetItemId().toInteger() > 0))
//							bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "El Items de la O.C. no tiene asignada una Partida Presupuestal");
						bmoRequisitionReceiptItemNew.getBudgetItemId().setValue(bmoRequisitionItem.getBudgetItemId().toInteger());
						bmoRequisitionReceiptItemNew.getAreaId().setValue(bmoRequisitionItem.getAreaId().toInteger());
					}

					bmoRequisitionReceiptItemNew.getDays().setValue(bmoRequisitionItem.getDays().toDouble());
					bmoRequisitionReceiptItemNew.getName().setValue(bmoRequisitionItem.getName().toString());				
					bmoRequisitionReceiptItemNew.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());

					if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
						bmoRequisitionReceiptItemNew.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());					
						double amount = bmoRequisitionItem.getPrice().toDouble() * bmoRequisitionItem.getQuantity().toDouble();
						if (bmoRequisitionItem.getDays().toDouble() > 0)
							amount = bmoRequisitionItem.getPrice().toDouble() * bmoRequisitionItem.getDays().toDouble() * bmoRequisitionItem.getQuantity().toDouble();					
						bmoRequisitionReceiptItemNew.getAmount().setValue(amount);
					} else {
						bmoRequisitionReceiptItemNew.getQuantity().setValue(0);					
						bmoRequisitionReceiptItemNew.getAmount().setValue(0);
					}

					// Actualiza estatus de saldo a todos los items relacionados
					updateQuantityBalance(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);

					// Actualiza estatus de devoluciones a todos los items relacionados
					updateQuantityReturned(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);

					super.save(pmConn,bmoRequisitionReceiptItemNew, bmUpdateResult);		

				}
			}

			if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
				pmRequisitionReceipt.updateBalance(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}
		} else {
			bmUpdateResult.addError(bmoRequisitionReceipt.getRequisitionId().getName(), "La Orden de Compra no está Autorizada");
		}
	}

	private void updateWhMovItem(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem, BmUpdateResult bmUpdateResult) throws SFException {
		// Obtener item de la orden de compra
		PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
		BmoRequisitionItem bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger());

		// Revisar que el item afecte inventarios
		if (bmoRequisitionItem.getBmoProduct().getInventory().toBoolean()) {
			PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());
			BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();

			PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
			BmoWhMovement bmoWhMovement = new BmoWhMovement();
			bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoRequisitionReceiptItem.getRequisitionReceiptId().toInteger(), bmoWhMovement.getRequisitionReceiptId().getName());

			// Si no existe el item de movimiento de almacen, crearlo
			if (!pmWhMovItem.requisitionReceiptItemWhMovItemExists(pmConn, bmoRequisitionReceiptItem.getId())) {
				bmoWhMovItem.getWhMovementId().setValue(bmoWhMovement.getId());
				bmoWhMovItem.getRequisitionReceiptItemId().setValue(bmoRequisitionReceiptItem.getId());
				bmoWhMovItem.getProductId().setValue(bmoRequisitionItem.getProductId().toInteger());
				bmoWhMovItem.getQuantity().setValue(bmoRequisitionReceiptItem.getQuantity().toDouble());
				bmoWhMovItem.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
				bmoWhMovItem.getSerial().setValue(bmoRequisitionReceiptItem.getSerial().toString());
				if (bmoRequisitionReceipt.getType().toChar() == BmoRequisitionReceipt.TYPE_RECEIPT)
					bmoWhMovItem.getToWhSectionId().setValue(bmoRequisitionReceipt.getWhSectionId().toInteger());
				else
					bmoWhMovItem.getFromWhSectionId().setValue(bmoRequisitionReceipt.getWhSectionId().toInteger());

			} else {
				// Si existen, actualizalo
				bmoWhMovItem = (BmoWhMovItem)pmWhMovItem.getBy(pmConn, bmoRequisitionReceiptItem.getId(), bmoWhMovItem.getRequisitionReceiptItemId().getName());
				bmoWhMovItem.getQuantity().setValue(bmoRequisitionReceiptItem.getQuantity().toDouble());
				bmoWhMovItem.getSerial().setValue(bmoRequisitionReceiptItem.getSerial().toString());
				bmoWhMovItem.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
				if (bmoRequisitionReceipt.getType().toChar() == BmoRequisitionReceipt.TYPE_RECEIPT)
					bmoWhMovItem.getToWhSectionId().setValue(bmoRequisitionReceipt.getWhSectionId().toInteger());
				else
					bmoWhMovItem.getFromWhSectionId().setValue(bmoRequisitionReceipt.getWhSectionId().toInteger());			
			}
			// Si la cantidad es menor a 1 borrarlo
			if (!(bmoRequisitionReceiptItem.getQuantity().toDouble() > 0)) {
				if (bmoWhMovItem.getId() > 0)
					pmWhMovItem.delete(pmConn, bmoWhMovItem, bmUpdateResult);
			} else 
				pmWhMovItem.save(pmConn, bmoWhMovItem, bmUpdateResult);
		}
	}

	// Obtener la cantidad de producto obtenido
	private boolean checkQuantityItem(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem, BmUpdateResult bmUpdateResult) throws SFException {                    
		String sql="";
		boolean result = true; 
		int quantityItemOC = 0;
		int quantityItemRE = 0;

		sql = " SELECT rqit_quantity FROM requisitionitems " +
				" WHERE rqit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityItemOC = pmConn.getInt("rqit_quantity");

		sql = " SELECT SUM(reit_quantity)  AS quantityre FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) " + 
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() + 
				" AND rerc_type = '" + BmoRequisitionReceipt.TYPE_RECEIPT + "'";
				pmConn.doFetch(sql);			
				if (pmConn.next()) quantityItemRE = pmConn.getInt("quantityre");

				if (quantityItemRE > quantityItemOC) result = false;

				System.out.println("cantidad itemoc = " + quantityItemOC + ", cantidad recibida: " + quantityItemRE);

				return result;
	}

	// Revisa la cantidad a devolver
	private boolean checkQuantityReturnedItem(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem, BmUpdateResult bmUpdateResult) throws SFException {                    
		String sql="";
		boolean result = true; 
		int quantityReceived = 0;
		int quantityReturned = 0;

		sql = " SELECT SUM(reit_quantity) AS q FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) " +
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() + 
				" AND rerc_type = '" + BmoRequisitionReceipt.TYPE_RECEIPT + "'";
		pmConn.doFetch(sql);			
		if (pmConn.next()) quantityReceived = pmConn.getInt("q");

		sql = " SELECT SUM(reit_quantity)  AS q FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) " +
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() + 
				" AND rerc_type = '" + BmoRequisitionReceipt.TYPE_RETURN + "'";
		pmConn.doFetch(sql);			
		if (pmConn.next()) quantityReturned = pmConn.getInt("q");

		if (quantityReturned > quantityReceived) result = false;

		return result;
	}

	// Obtener la cantidad de producto recibido
	public void updateQuantityBalance(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";
		double quantityReceived = 0;
		double quantityTotal = 0;
		double balance = 0;

		// Obtiene total items de la oc
		sql = " SELECT rqit_quantity FROM requisitionitems " +
				" WHERE rqit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityTotal = pmConn.getDouble("rqit_quantity");

		// Obtiene total recibido
		sql = " SELECT SUM(reit_quantity) AS quantity FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid =rerc_requisitionreceiptid)" +
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() +
				" AND rerc_type <> '" + BmoRequisitionReceipt.TYPE_RETURN + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityReceived = pmConn.getDouble("quantity");	

		balance = quantityTotal - quantityReceived;

		// Actualiza pendientes en los items del recibo
		sql = " UPDATE requisitionreceiptitems SET reit_quantitybalance = " + balance + 
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger();
		pmConn.doUpdate(sql);

		// Actualiza pendientes en la orden de compra
		sql = " UPDATE requisitionitems SET rqit_quantitybalance = " + balance + 
				" WHERE rqit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger();
		pmConn.doUpdate(sql);

		// Obtener el Saldo del item
		bmoRequisitionReceiptItem.getQuantityBalance().setValue(balance);
	}

	// Actualiza la cantidad de devuelta en todos los items
	public void updateQuantityReturned(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";
		double quantityReturned = 0;

		sql = " SELECT SUM(reit_quantity) AS quantity FROM requisitionreceiptitems " +
				" left join requisitionreceipts on (reit_requisitionreceiptid =rerc_requisitionreceiptid)" +
				" WHERE rerc_requisitionid = " + bmoRequisitionReceipt.getRequisitionId().toInteger() +
				" AND reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() + 
				" AND rerc_type = '" + BmoRequisitionReceipt.TYPE_RETURN + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityReturned = pmConn.getDouble("quantity");	

		sql = " UPDATE requisitionreceiptitems SET reit_quantityreturned = " + quantityReturned + 
				" WHERE reit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger();
		pmConn.doUpdate(sql);

		sql = " UPDATE requisitionitems SET rqit_quantityreturned = " + quantityReturned + 
				" WHERE rqit_requisitionitemid = " + bmoRequisitionReceiptItem.getRequisitionItemId().toInteger();
		pmConn.doUpdate(sql);

		// Asignar el valor
		bmoRequisitionReceiptItem.getQuantityReturned().setValue(quantityReturned);
	}

	// Actualizar cantidad recibida
	public void updateQuantityReceived(PmConn pmConn, int requisitionItemId, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";
		double quantityReceived = 0;

		sql = " SELECT SUM(reit_quantity) AS quantity FROM requisitionreceiptitems " +
				" left join requisitionreceipts on (reit_requisitionreceiptid = rerc_requisitionreceiptid)" +
				" WHERE reit_requisitionitemid = " + requisitionItemId + 
				" AND rerc_type = '" + BmoRequisitionReceipt.TYPE_RECEIPT + "'";

		pmConn.doFetch(sql);
		if (pmConn.next()) quantityReceived = pmConn.getDouble("quantity");	

		sql = " UPDATE requisitionitems SET rqit_quantityreceipt = " + quantityReceived + 
				" WHERE rqit_requisitionitemid = " + requisitionItemId;

		pmConn.doUpdate(sql);
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;
		int requisitionReceiptId = bmoRequisitionReceiptItem.getRequisitionReceiptId().toInteger();

		// Accion de encontrar el codigo de barras
		if (action.equals(BmoRequisitionReceiptItem.ACTION_SEARCHBARCODE)) {
			String barcode = value;

			// Busca primero por numero de serie
			bmoRequisitionReceiptItem = searchItemSerialByBarcode(pmConn, requisitionReceiptId, barcode);
			if (bmoRequisitionReceiptItem.getId() > 0) {
				bmUpdateResult.setBmObject(bmoRequisitionReceiptItem);
				return bmUpdateResult;
			} else {

				// Busca por clave de producto y serial vacio
				bmoRequisitionReceiptItem = searchProductCodeEmptySerialByBarcode(pmConn, requisitionReceiptId, barcode);
				if (bmoRequisitionReceiptItem.getId() > 0) {
					bmUpdateResult.setBmObject(bmoRequisitionReceiptItem);
					return bmUpdateResult;
				} else {
					// Ahora busca por producto
					PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
					BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, requisitionReceiptId);

					// Busca primero dentro de los requisition receipt items, la clave de producto
					bmoRequisitionReceiptItem = searchProductCodeByBarcode(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger(), requisitionReceiptId, barcode, bmUpdateResult);	

					if (bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() > 0) {
						bmUpdateResult.setBmObject(bmoRequisitionReceiptItem);
					} else {
						bmUpdateResult.addMsg("No fue encontrado el Código de Barras dentro de los Items.");
					}
				}
			}
		}

		return bmUpdateResult;
	}

	// Busca por serie en item de producto
	private BmoRequisitionReceiptItem searchItemSerialByBarcode(PmConn pmConn, int requisitionReceiptId, String barcode) throws SFException {
		bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
		String sql = "";

		// Busca el # serie dentro de los items del recibo de orden de compra
		sql = "SELECT reit_requisitionreceiptitemid FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionitems ON (reit_requisitionitemid = rqit_requisitionitemid) " + 
				" LEFT JOIN products ON (rqit_productid = prod_productid) " + 
				" WHERE reit_requisitionreceiptid = " + requisitionReceiptId + 
				" AND reit_serial LIKE '" + barcode + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			int requisitionReceiptItemId = pmConn.getInt("reit_requisitionreceiptitemid");
			bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)this.get(pmConn, requisitionReceiptItemId);	
		}

		return bmoRequisitionReceiptItem;
	}

	// Busca por clave producto, numero de serie vacio en item de producto
	private BmoRequisitionReceiptItem searchProductCodeEmptySerialByBarcode(PmConn pmConn, int requisitionReceiptId, String barcode) throws SFException {
		bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
		String sql = "";

		// Busca el # serie dentro de los items del recibo de orden de compra
		sql = "SELECT reit_requisitionreceiptitemid FROM requisitionreceiptitems " +
				" LEFT JOIN requisitionitems ON (reit_requisitionitemid = rqit_requisitionitemid) " + 
				" LEFT JOIN products ON (rqit_productid = prod_productid) " + 
				" WHERE reit_requisitionreceiptid = " + requisitionReceiptId + 
				" AND prod_code LIKE '" + barcode + "'" + 
				" AND reit_serial IS NULL";

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			int requisitionReceiptItemId = pmConn.getInt("reit_requisitionreceiptitemid");
			bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)this.get(pmConn, requisitionReceiptItemId);	
		}

		return bmoRequisitionReceiptItem;
	}

	// Busca por clave de producto
	private BmoRequisitionReceiptItem searchProductCodeByBarcode(PmConn pmConn, int requisitionId, int requisitionReceiptId, String barcode, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT rqit_requisitionitemid FROM requisitionitems " +
				" LEFT JOIN products ON (rqit_productid = prod_productid) " + 
				" WHERE rqit_requisitionid = " + requisitionId + 
				" AND prod_code LIKE '" + barcode + "'";

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
			BmoRequisitionItem bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn, pmConn.getInt("rqit_requisitionitemid"));

			bmoRequisitionReceiptItem.setBmoRequisitionItem(bmoRequisitionItem);
			bmoRequisitionReceiptItem.getRequisitionItemId().setValue(bmoRequisitionItem.getId());
			bmoRequisitionReceiptItem.getRequisitionReceiptId().setValue(requisitionReceiptId);
			bmoRequisitionReceiptItem.getName().setValue(bmoRequisitionItem.getBmoProduct().getName().toString());
			bmoRequisitionReceiptItem.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());			
			this.save(pmConn, bmoRequisitionReceiptItem, bmUpdateResult);
		}

		return bmoRequisitionReceiptItem;
	}

	// Elimina todos los items de un envío
	public void deleteItems(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear los items
		BmFilter filterRequisitionReceiptItem = new BmFilter();
		filterRequisitionReceiptItem.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
		Iterator<BmObject> listRequisitionReceiptItem = this.list(pmConn, filterRequisitionReceiptItem).iterator();			

		while (listRequisitionReceiptItem.hasNext()) {
			bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();

			this.delete(pmConn, bmoRequisitionReceiptItem, bmUpdateResult);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;

		// Obtener el Recibo
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());			
		BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoRequisitionReceiptItem.getRequisitionReceiptId().toInteger());

		// Si el recibo ya está autorizado, no se puede hacer movimientos			
		if (bmoRequisitionReceipt.getStatus().toChar() != BmoRequisitionReceipt.STATUS_REVISION) {				
			bmUpdateResult.addMsg("No se pueden Eliminar Items del Recibo de Órden de Compra - está Autorizado.");
		} else {
			// Elimina el movimiento de almacen ligado
			PmRequisition pmRequisition = new PmRequisition(getSFParams());
			if (pmRequisition.requisitionAffectsInventory(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())) {
				PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());
				BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
				if (pmWhMovItem.requisitionReceiptItemWhMovItemExists(pmConn, bmoRequisitionReceiptItem.getId())) {
					bmoWhMovItem = (BmoWhMovItem)pmWhMovItem.getBy(pmConn, bmoRequisitionReceiptItem.getId(), bmoWhMovItem.getRequisitionReceiptItemId().getName());
					pmWhMovItem.delete(pmConn, bmoWhMovItem, bmUpdateResult);
				}
			}

			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualiza estatus de saldo a todos los items relacionados
			updateQuantityBalance(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

			// Actualiza estatus de devoluciones a todos los items relacionados
			updateQuantityReturned(pmConn, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

			// Actualiza cantidad recibida por item
			updateQuantityReceived(pmConn, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger(), bmUpdateResult);

			// Recalcula el subtotal
			pmRequisitionReceipt.updateBalance(pmConn, bmoRequisitionReceipt, bmUpdateResult);
		}

		return bmUpdateResult;
	}
}
