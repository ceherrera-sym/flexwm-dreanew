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

import java.util.Iterator;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.op.PmProductKitItem;
import com.flexwm.server.op.PmProductPrice;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.cm.BmoQuoteMainGroup;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProductKitItem;


public class PmQuoteGroup extends PmObject {
	BmoQuoteGroup bmoQuoteGroup;

	public PmQuoteGroup(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuoteGroup = new BmoQuoteGroup();
		setBmObject(bmoQuoteGroup);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuoteGroup = (BmoQuoteGroup)autoPopulate(pmConn, new BmoQuoteGroup());
		return bmoQuoteGroup;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuoteGroup = (BmoQuoteGroup)bmObject;		

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoQuoteGroup.getQuoteId().toInteger());
		//validar grupo maestro para Dreanew
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (bmoQuoteGroup.getMainGroupId().toInteger() <= 0 ) {
				bmUpdateResult.addError(bmoQuoteGroup.getMainGroupId().getName(), "Debe seleccionar un Grupo para el Sub-Grupo");
			}
		}

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la cotización - está Autorizada.");
		} else {

			// Si no esta asignado el indice, agrega el siguiente
			if (!(bmoQuoteGroup.getIndex().toInteger() > 0)) {
				bmoQuoteGroup.getIndex().setValue(nextIndex(pmConn, bmoQuoteGroup));
			}
			
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) && bmoQuoteGroup.getIsKit().toBoolean()) {
				bmoQuoteGroup.getTotal().setValue(bmoQuoteGroup.getDays().toDouble() * bmoQuoteGroup.getAmount().toDouble());
				updateItems(pmConn,bmoQuoteGroup, bmUpdateResult);
			}
			//calcular descuento y fee de produccion por grupo
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				if(bmoQuoteGroup.getIsKit().toBoolean()) {
					bmoQuoteGroup.getFeeProduction().setValue(bmoQuoteGroup.getTotal().toDouble() * (bmoQuoteGroup.getFeeProductionRate().toDouble()/100));
					bmoQuoteGroup.getCommissionAmount().setValue((bmoQuoteGroup.getTotal().toDouble() + bmoQuoteGroup.getFeeProduction().toDouble()) * (bmoQuoteGroup.getCommissionRate().toDouble()/100));
				} else {
					bmoQuoteGroup.getFeeProduction().setValue(bmoQuoteGroup.getAmount().toDouble() * (bmoQuoteGroup.getFeeProductionRate().toDouble()/100));
					bmoQuoteGroup.getCommissionAmount().setValue((bmoQuoteGroup.getAmount().toDouble() + bmoQuoteGroup.getFeeProduction().toDouble()) * (bmoQuoteGroup.getCommissionRate().toDouble()/100));
				}				
			}
		
			super.save(pmConn, bmoQuoteGroup, bmUpdateResult);
			
			//Acciones Drea
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				
				updateMainGroup(pmConn,bmoQuoteGroup.getMainGroupId().toInteger(),bmUpdateResult);
				if (!bmoQuoteGroup.getIsKit().toBoolean())
					updateDiscountitems(pmConn, bmoQuoteGroup, bmUpdateResult);
			}
			
			// Recalcular la cotizacion completa
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);
		}

		return bmUpdateResult;
	}
	//Dar guardar a los items para recalcular valores
	public void updateDiscountitems(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup,BmUpdateResult bmUpdateResult) throws SFException {
		BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
		PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
		BmFilter bmFilter = new BmFilter();
		
		bmFilter.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId(), bmoQuoteGroup.getId());
		Iterator<BmObject> iterator = pmQuoteItem.list(bmFilter).iterator();
		while (iterator.hasNext()) {
			BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)iterator.next();
			if (!bmoQuoteGroup.getDiscountApplies().toBoolean())
				nextBmoQuoteItem.getDiscountApplies().setValue(bmoQuoteGroup.getDiscountApplies().toBoolean());
			
			pmQuoteItem.save(pmConn, nextBmoQuoteItem, bmUpdateResult);
		}
		
	}
	
	public void updateMainGroup(PmConn pmConn, int mainGroupId, BmUpdateResult bmUpdateResult) throws SFException {
		PmQuoteMainGroup pmQuoteMainGroup = new PmQuoteMainGroup(getSFParams());
		BmoQuoteMainGroup bmoQuoteMainGroup = (BmoQuoteMainGroup)pmQuoteMainGroup.get(pmConn, mainGroupId);
		
		
		String sql = "SELECT qogr_amount,qogr_total,qogr_iskit,qogr_discount,qogr_comission,qogr_feeproduction,qogr_price,qogr_days"
				+ " from quotegroups where qogr_maingroupid = "  + mainGroupId;
		
		double total = 0;
		double discount = 0;
		double comission = 0;
		double fee = 0;
		double price = 0;
		
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			if (pmConn.getInt("qogr_iskit") > 0) {
				total += pmConn.getDouble("qogr_total");
				price += pmConn.getDouble("qogr_amount") * pmConn.getDouble("qogr_days");
			} else {
				total += pmConn.getDouble("qogr_amount");
				price += pmConn.getDouble("qogr_amount") + pmConn.getDouble("qogr_discount")  ;
			}
			
			discount += pmConn.getDouble("qogr_discount");
			comission +=  pmConn.getDouble("qogr_comission");
			fee += pmConn.getDouble("qogr_feeproduction");			
		}
		bmoQuoteMainGroup.getDiscount().setValue(discount);
		bmoQuoteMainGroup.getCommission().setValue(comission);
		bmoQuoteMainGroup.getProductionFee().setValue(fee);
		bmoQuoteMainGroup.getAmount().setValue(price);
		bmoQuoteMainGroup.getTotal().setValue(total);
		pmQuoteMainGroup.save(pmConn, bmoQuoteMainGroup, bmUpdateResult);
		
	}
	public void updateItems(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "SELECT qoit_quoteitemid FROM quoteitems WHERE qoit_quotegroupid = " + bmoQuoteGroup.getId();
		PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)pmQuoteItem.get(pmConn.getInt("qoit_quoteitemid"));
			
			nextBmoQuoteItem.getDays().setValue(bmoQuoteGroup.getDays().toDouble());
			
			pmQuoteItem.saveSimple(pmConn, nextBmoQuoteItem, bmUpdateResult);			
		}		
	}
	// Regresa el indice maximo mas 1
	private int nextIndex(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup) throws SFException {
		int index = 1;
		String sql = " SELECT MAX(qogr_index) as maxindex FROM quotegroups " +
				" WHERE qogr_quoteid = " + bmoQuoteGroup.getQuoteId(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			return pmConn.getInt("maxindex") + 1 ;
		else 
			return index;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoQuoteGroup = (BmoQuoteGroup)bmObject;

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			if (action.equals(BmoQuoteGroup.ACTION_PRODUCTKIT)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();
					//Total para drea
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
						bmoQuoteGroup.getTotal().setValue(bmoQuoteGroup.getDays().toDouble() * bmoQuoteGroup.getAmount().toDouble());
					// Crear el grupo de la cotización
					super.save(pmConn, bmoQuoteGroup, bmUpdateResult);
					bmoQuoteGroup.setId(bmUpdateResult.getId());
					
					// Buscar el ultimo index y asignar al nuevo grupo
					int index = 0;
					PmConn pmConnGetIndex = new PmConn(getSFParams());
					pmConnGetIndex.open();
					pmConnGetIndex.doFetch("SELECT " + bmoQuoteGroup.getIndex().getName() + " FROM quotegroups WHERE qogr_quoteid = " + bmoQuote.getId() + " ORDER BY " + bmoQuoteGroup.getIndex().getName()  + " DESC");
											
					if(pmConnGetIndex.next()) {
						index = pmConnGetIndex.getInt(bmoQuoteGroup.getIndex().getName()) + 1;
					}
					pmConnGetIndex.close();
					bmoQuoteGroup.getIndex().setValue(index);

					// Agregar todos los items del kit a este grupo de cotizaciones
					int productKitId = Integer.parseInt(value);
					addKit(pmConn, bmoQuoteGroup, productKitId, bmUpdateResult);
					
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						updateMainGroup(pmConn, bmoQuoteGroup.getMainGroupId().toInteger(), bmUpdateResult);
					}
					// Aprovechar para recalcular el grupo y la cotizacion, puede ser redundante...
					calculateAmount(pmConn, bmoQuoteGroup, bmUpdateResult);

					if (!bmUpdateResult.hasErrors()) {
						pmConn.commit();
					}
				} catch (SFException e) {
					bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
				} finally {
					pmConn.close();
				}
			} else if  (action.equals(BmoQuoteGroup.ACTION_CHANGEINDEX)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();

					printDevLog("Iniciando accion change index");
					
					// Revisa si debe subir de indice
					changeIndex(pmConn, bmoQuoteGroup, value);
					
					printDevLog("Ya termino accion, revisa si puede guardar cambios: " + bmUpdateResult.errorsToString());

					if (!bmUpdateResult.hasErrors()) {
						pmConn.commit();
					}
				} catch (SFException e) {
					bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
				} finally {
					pmConn.close();
				}
			}
		}
		return bmUpdateResult;
	}

	// Sube el grupo un nivel
	private void changeIndex(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup, String direction) throws SFException {
		String sql = "";
		int replaceQuoteGroupId = 0, replaceIndex = 0;
		
		if (direction.equalsIgnoreCase(BmoFlexConfig.UP)) {
			printDevLog("Iniciando subir indice");
			
			// Ubica si hay algun grupo antes 
			sql = " SELECT qogr_quotegroupid, qogr_index FROM quotegroups "
					+ " WHERE qogr_index < " + bmoQuoteGroup.getIndex().toInteger()
					+ " AND qogr_quoteid = " + bmoQuoteGroup.getQuoteId().toInteger()
					+ " ORDER BY qogr_index DESC"; 
			pmConn.doFetch(sql);
			
			printDevLog(sql);
			
			if (pmConn.next()) {
				printDevLog("Encontro otro registro anterior");
				
				// Si hay otro, reuperar indice del grupo a sustituir
				replaceQuoteGroupId = pmConn.getInt("qogr_quotegroupid");
				replaceIndex = pmConn.getInt("qogr_index");
				
				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE quotegroups SET qogr_index = " + bmoQuoteGroup.getIndex().toInteger()
						 + " WHERE qogr_quotegroupid = " + replaceQuoteGroupId);
				
				pmConn.doUpdate("UPDATE quotegroups SET qogr_index = " + replaceIndex
						 + " WHERE qogr_quotegroupid = " + bmoQuoteGroup.getId());
			}
		} else if (direction.equalsIgnoreCase(BmoFlexConfig.DOWN)) {
			printDevLog("Iniciando bajar indice");
			
			// Ubica si hay algun grupo antes 
			sql = " SELECT qogr_quotegroupid, qogr_index FROM quotegroups "
					+ " WHERE qogr_index > " + bmoQuoteGroup.getIndex().toInteger()
					+ " AND qogr_quoteid = " + bmoQuoteGroup.getQuoteId().toInteger()
					+ " ORDER BY qogr_index ASC"; 
			pmConn.doFetch(sql);
			
			printDevLog(sql);
			
			if (pmConn.next()) {
				printDevLog("Encontro otro registro posterior");
				
				// Si hay otro, reuperar indice del grupo a sustituir
				replaceQuoteGroupId = pmConn.getInt("qogr_quotegroupid");
				replaceIndex = pmConn.getInt("qogr_index");
				
				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE quotegroups SET qogr_index = " + bmoQuoteGroup.getIndex().toInteger()
						 + " WHERE qogr_quotegroupid = " + replaceQuoteGroupId);
				
				pmConn.doUpdate("UPDATE quotegroups SET qogr_index = " + replaceIndex
						 + " WHERE qogr_quotegroupid = " + bmoQuoteGroup.getId());
			}
		}
	}

	private void addKit(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup, int productKitId, BmUpdateResult bmUpdateResult) throws SFException {
		PmProductKitItem pmProductKitItem = new PmProductKitItem(getSFParams());
		BmoProductKitItem bmoProductKitItem = new BmoProductKitItem();

		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoQuoteGroup.getQuoteId().toInteger());
		
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());

		PmProductPrice pmProductPrice = new PmProductPrice(getSFParams());

		BmFilter kitItemsByKitFilter = new BmFilter();
		kitItemsByKitFilter.setValueFilter(bmoProductKitItem.getKind(), bmoProductKitItem.getProductKitId(), productKitId);
		Iterator<BmObject> productKitItemIterator = pmProductKitItem.list(pmConn, kitItemsByKitFilter).iterator();

		// Identificar la secuencia inicial
		PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());

		// Lista de items del kit
		int index = 1;
		while (productKitItemIterator.hasNext()) {
			bmoProductKitItem = (BmoProductKitItem)productKitItemIterator.next();

			BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
			bmoQuoteItem.getQuoteGroupId().setValue(bmoQuoteGroup.getId());
			bmoQuoteItem.getName().setValue(bmoProductKitItem.getBmoProduct().getName().toString());
			bmoQuoteItem.getQuantity().setValue(bmoProductKitItem.getQuantity().toDouble());
			bmoQuoteItem.getDays().setValue(bmoProductKitItem.getDays().toDouble());
			bmoQuoteItem.getProductId().setValue(bmoProductKitItem.getProductId().toInteger());
			bmoQuoteItem.getIndex().setValue(index);
			// Tomar Partida presp. y Departamento del Producto, sino, de la Cotizacion
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				// Si el producto tiene partida asignarla, sino; la de la cotizacion
				if (bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger() > 0)
					bmoQuoteItem.getBudgetItemId().setValue(bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger());
				else {
					if (!(bmoQuote.getBudgetItemId().toInteger() > 0))
						bmUpdateResult.addError(bmoQuote.getBudgetItemId().getName(), "Seleccione una Partida Presp. en la Oportunidad");
					else
						bmoQuoteItem.getBudgetItemId().setValue(bmoQuote.getBudgetItemId().toInteger());
				}

				// Si el producto tiene Departamento asignarlo, sino; de la Cotizacion
				if (bmoProductKitItem.getBmoProduct().getAreaId().toInteger() > 0)
					bmoQuoteItem.getAreaId().setValue(bmoProductKitItem.getBmoProduct().getAreaId().toInteger());
				else {
					//					if (!(bmoQuote.getAreaId().toInteger() > 0))
					//						bmUpdateResult.addError(bmoQuote.getAreaId().getName(), "Seleccione un Departamento en la Oportunidad");
					//					else
					bmoQuoteItem.getAreaId().setValue(bmoQuote.getAreaId().toInteger());
				}
			}
			// Obtiene precio vigente del producto
			bmoQuoteItem.getPrice().setValue(pmProductPrice.getCurrentPrice(pmConn, bmoProductKitItem.getProductId().toInteger(),
					bmoQuote.getCurrencyId().toInteger(), bmoQuote.getOrderTypeId().toInteger(), bmoOpportunity.getForeignWFlowTypeId().toInteger(),
					bmoQuote.getMarketId().toInteger(), bmoQuote.getCompanyId().toInteger()));

			double amount = bmoProductKitItem.getQuantity().toDouble() * bmoQuoteItem.getPrice().toDouble();
			bmoQuoteItem.getAmount().setValue(amount);

			// Almacenar nuevo item de la cotizacion
			if (!bmUpdateResult.hasErrors())
				pmQuoteItem.simpleSave(pmConn, bmoQuoteItem, bmUpdateResult);
			index++;
		}		
	}

	public void calculateAmount(PmConn pmConn, BmoQuoteGroup bmoQuoteGroup, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoQuoteGroup = bmoQuoteGroup;
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());

		// Si no es KIT, recacular mediante items
		if (!bmoQuoteGroup.getIsKit().toBoolean()) {
			pmConn.doFetch("SELECT sum(qoit_amount) FROM quoteitems "
					+ " WHERE qoit_quotegroupid = " + bmoQuoteGroup.getId());

			double amount = -1;
			if (pmConn.next()) amount = pmConn.getDouble(1);
			bmoQuoteGroup.getAmount().setValue(amount);
			//Descuento Drea
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				pmConn.doFetch("SELECT sum(qoit_discount) AS dicount,sum(qoit_feeproduction) AS feeSum,sum(qoit_comission) sumCom ,sum(qoit_price) sumPrice FROM quoteitems "
						+ " WHERE qoit_quotegroupid = " + bmoQuoteGroup.getId());
				double discount = 0;
				double price = 0;
				if (pmConn.next()) {
					discount = pmConn.getDouble("dicount");
					price = pmConn.getDouble("sumPrice");
				}
				bmoQuoteGroup.getDiscount().setValue(discount);
				bmoQuoteGroup.getPrice().setValue(price);
			}
		}		

		super.save(pmConn, bmoQuoteGroup, bmUpdateResult);	

		// Recalcular la cotizacion completa		
		pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);
	}
	
	
	@Override
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuoteGroup = (BmoQuoteGroup)bmObject;

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la cotización - está Autorizada.");
		} else {
			PmConn pmConn = new PmConn(getSFParams());
			try {
				pmConn.open();
				pmConn.disableAutoCommit();

				// Eliminar items del grupo de cotización
				PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
				pmQuoteItem.deleteByQuoteGroup(pmConn, bmoQuoteGroup.getId());

				// Actualizar montos
				calculateAmount(pmConn, bmoQuoteGroup, bmUpdateResult);

				// Eliminar grupo de cotización
				bmUpdateResult = super.delete(pmConn, bmoQuoteGroup, bmUpdateResult);
				if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					updateMainGroup(pmConn, bmoQuoteGroup.getMainGroupId().toInteger(), bmUpdateResult);
				}
				
				// Recalcular la cotizacion completa
				pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) pmConn.commit();

			} catch (SFPmException e) {
				bmUpdateResult.addError(bmObject.getProgramCode(), "ERROR: " + e.toString());
			} finally {
				pmConn.close();
			}
		}
		return bmUpdateResult;
	}
}
