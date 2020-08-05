///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.sf.PmArea;
//import com.symgae.server.PmConn;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.shared.sf.BmoArea;
//import com.flexwm.server.fi.PmBudgetItem;
//import com.flexwm.server.op.PmProduct;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoEstimationGroup;
//import com.flexwm.shared.cm.BmoEstimationRFQItem;
//import com.flexwm.shared.fi.BmoBudgetItem;
//import com.flexwm.shared.op.BmoProduct;
//
//
//public class PmEstimationRFQItem extends PmObject {
//	BmoEstimationRFQItem bmoEstimationRFQItem;
//	BmoEstimationGroup bmoEstimationGroupGroup;
//	public PmEstimationRFQItem(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoEstimationRFQItem = new BmoEstimationRFQItem();
//		setBmObject(bmoEstimationRFQItem);
//
//		// Lista de joins
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoEstimationRFQItem.getEstimationGroupId(), bmoEstimationRFQItem.getBmoEstimationGroup()),
//				new PmJoin(bmoEstimationRFQItem.getProductId(), bmoEstimationRFQItem.getBmoProduct()),
//				new PmJoin(bmoEstimationRFQItem.getBmoProduct().getProductFamilyId(), bmoEstimationRFQItem.getBmoProduct().getBmoProductFamily()),
//				new PmJoin(bmoEstimationRFQItem.getBmoProduct().getProductGroupId(), bmoEstimationRFQItem.getBmoProduct().getBmoProductGroup()),
//				new PmJoin(bmoEstimationRFQItem.getBmoProduct().getUnitId(), bmoEstimationRFQItem.getBmoProduct().getBmoUnit()),
//				new PmJoin(bmoEstimationRFQItem.getAreaId() , bmoEstimationRFQItem.getBmoArea()),
//				new PmJoin(bmoEstimationRFQItem.getBudgetItemId(), bmoEstimationRFQItem.getBmoBudgetItem()),
//				new PmJoin(bmoEstimationRFQItem.getBmoBudgetItem().getBudgetId(), bmoEstimationRFQItem.getBmoBudgetItem().getBmoBudget()),
//				new PmJoin(bmoEstimationRFQItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoEstimationRFQItem.getBmoBudgetItem().getBmoBudgetItemType()),
//				new PmJoin(bmoEstimationRFQItem.getBmoBudgetItem().getCurrencyId(), bmoEstimationRFQItem.getBmoBudgetItem().getBmoCurrency())
//				)));
//	}
//	
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		bmoEstimationRFQItem = (BmoEstimationRFQItem)autoPopulate(pmConn, new BmoEstimationRFQItem());
//
//		// BmoProduct
//		BmoProduct bmoProduct = new BmoProduct();
//		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
//		if (productId > 0) bmoEstimationRFQItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
//		else bmoEstimationRFQItem.setBmoProduct(bmoProduct);
//
//		// BmoEstimationGroup
//		BmoEstimationGroup bmoEstimationGroup = new BmoEstimationGroup();
//		int estimationGroupId = (int)pmConn.getInt(bmoEstimationGroup.getIdFieldName());
//		if (estimationGroupId > 0) bmoEstimationRFQItem.setBmoEstimationGroup((BmoEstimationGroup) new PmEstimationGroup(getSFParams()).populate(pmConn));
//		else bmoEstimationRFQItem.setBmoEstimationGroup(bmoEstimationGroup);
//		
//		// BmoArea
//		BmoArea bmoArea = new BmoArea();
//		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
//		if (areaId > 0) bmoEstimationRFQItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
//		else bmoEstimationRFQItem.setBmoArea(bmoArea);
//
//		// BmoBudgetItem
//		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
//		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
//		if (budgetItemId > 0) bmoEstimationRFQItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
//		else bmoEstimationRFQItem.setBmoBudgetItem(bmoBudgetItem);
//
//		
//		return bmoEstimationRFQItem;
//	}
//
//	
//	@Override	
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//			bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//			// Operaciones con el Grupo de Items de la cotizacion
//			PmEstimationGroup pmEstimationGroup = new PmEstimationGroup(getSFParams());
//			BmoEstimationGroup bmoEstimationGroup = new BmoEstimationGroup();
//			if (bmoEstimationRFQItem.getEstimationGroupId().toInteger() > 0)
//				bmoEstimationGroup = (BmoEstimationGroup)pmEstimationGroup.get(pmConn, bmoEstimationRFQItem.getEstimationGroupId().toInteger());
//			else 
//				bmUpdateResult.addError(bmoEstimationRFQItem.getEstimationGroupId().getName(), "No está seleccionado el " + bmoEstimationRFQItem.getEstimationGroupId().getLabel() + " del item");
//			
//			//hacer la suma de las cantidades
//			if(bmoEstimationRFQItem.getQuantity().toInteger()>0) {
//				
//			}
//			
//			// Obten la ESTIMACION
//			PmEstimation pmEstimation = new PmEstimation(getSFParams());
//			BmoEstimation bmoEstimation = (BmoEstimation)pmEstimation.get(bmoEstimationGroup.getEstimationId().toInteger());
//			printDevLog("despues de obetner EST ");
//
//			// Si la cotización ya está autorizada, no se puede hacer movimientos
//			if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_AUTHORIZED) {
//				bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
//			} else {
//				printDevLog("dentro de EST ");
//
//				// Si esta ligado a los productos, obten el registro del producto y su precio vigente
//				if (bmoEstimationRFQItem.getProductId().toInteger() > 0) {
//					PmProduct pmProduct = new PmProduct(getSFParams());
//					BmoProduct bmoProduct = (BmoProduct)pmProduct.get(bmoEstimationRFQItem.getProductId().toInteger());
//
//					// Si es nuevo registro, toma el precio del producto
//					if (!(bmoEstimationRFQItem.getId() > 0)) {
//							bmoEstimationRFQItem.getName().setValue(bmoProduct.getName().toString());
//					} 
//
//					// Validar que la cantidad no sea fraccion si el producto es SERIE
//					// Si es Sin Rastreo, verificar por la Unidad del producto
//					if (!pmProduct.applyFraction(bmoProduct, bmoEstimationRFQItem.getQuantity().toDouble()))
//						bmUpdateResult.addError(bmoEstimationRFQItem.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");
//
//				} 
//				
//			
//						 
//					if(!bmUpdateResult.hasErrors())
//					 super.save(pmConn, bmoEstimationRFQItem, bmUpdateResult);
//					 pmEstimationGroup.recaltulateQuantity(pmConn, bmoEstimationRFQItem.getEstimationGroupId().toInteger(), bmUpdateResult);
//					 if(!bmUpdateResult.hasErrors()) {
//						 super.save(pmConn, bmoEstimationRFQItem, bmUpdateResult);
//						 pmEstimationGroup.recaltulateQuantity(pmConn, bmoEstimationRFQItem.getEstimationGroupId().toInteger(), bmUpdateResult);
//
//					 }else {
//						 printDevLog("Error: "+bmUpdateResult.errorsToString());
//						 }
//				
//			}
//
//			return bmUpdateResult;
//		}
//
//	@Override
//	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
//		bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//
//		// Obten el grupo de la estimacion
//		PmEstimationGroup pmEstimationGroup = new PmEstimationGroup(getSFParams());
//		BmoEstimationGroup bmoEstimationGroup = (BmoEstimationGroup)pmEstimationGroup.get(bmoEstimationRFQItem.getEstimationGroupId().toInteger());
//
//		// Obten la cotización
//		PmEstimation pmEstimation = new PmEstimation(getSFParams());
//		BmoEstimation bmoEStimation = (BmoEstimation)pmEstimation.get(bmoEstimationGroup.getEstimationId().toInteger());
//
//		// Si la cotización ya está autorizada, no se puede hacer movimientos
//		if (bmoEStimation.getStatus().toChar() == BmoEstimation.STATUS_AUTHORIZED) {
//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Estimación - ya está Completada.");
//		} else {
//			if (action.equals(BmoEstimationRFQItem.ACTION_CHANGEINDEX)) {
//				PmConn pmConn = new PmConn(getSFParams());
//				try {
//					pmConn.open();
//					pmConn.disableAutoCommit();
//
//					if (!bmUpdateResult.hasErrors()) {
//						pmConn.commit();
//					}
//				} catch (SFException e) {
//					bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
//				} finally {
//					pmConn.close();
//				}
//			}
//		}
//		return bmUpdateResult;
//	}
//	
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//		// Operaciones con el Grupo de Items de la cotizacion
//		PmEstimationGroup pmEstimationGroup = new PmEstimationGroup(getSFParams());
//		//RESTAR LA CANTIDAD del item
//		if(!bmUpdateResult.hasErrors())
//		 super.delete(pmConn, bmoEstimationRFQItem, bmUpdateResult);
//			 pmEstimationGroup.recaltulateQuantity(pmConn, bmoEstimationRFQItem.getEstimationGroupId().toInteger(), bmUpdateResult);
//
//			 if(!bmUpdateResult.hasErrors()) {
//
//					 super.delete(pmConn, bmoEstimationRFQItem, bmUpdateResult);
//
//			 }else { printDevLog("Error: "+bmUpdateResult.errorsToString());	
//			 }
//	
//return bmUpdateResult;
//		
//	}
//	
//	
//	
//}
