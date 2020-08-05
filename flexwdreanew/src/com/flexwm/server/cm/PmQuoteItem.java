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
import com.symgae.server.sf.PmArea;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.op.PmProduct;
import com.flexwm.server.op.PmProductCompany;
import com.flexwm.server.op.PmProductPrice;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProduct;


public class PmQuoteItem extends PmObject {
	BmoQuoteItem bmoQuoteItem;

	public PmQuoteItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuoteItem = new BmoQuoteItem();
		setBmObject(bmoQuoteItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuoteItem.getQuoteGroupId(), bmoQuoteItem.getBmoQuoteGroup()),
				new PmJoin(bmoQuoteItem.getProductId(), bmoQuoteItem.getBmoProduct()),
				new PmJoin(bmoQuoteItem.getBmoProduct().getProductFamilyId(), bmoQuoteItem.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoQuoteItem.getBmoProduct().getProductGroupId(), bmoQuoteItem.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoQuoteItem.getBmoProduct().getUnitId(), bmoQuoteItem.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoQuoteItem.getAreaId() , bmoQuoteItem.getBmoArea()),
				new PmJoin(bmoQuoteItem.getBudgetItemId(), bmoQuoteItem.getBmoBudgetItem()),
				new PmJoin(bmoQuoteItem.getBmoBudgetItem().getBudgetId(), bmoQuoteItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoQuoteItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoQuoteItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoQuoteItem.getBmoBudgetItem().getCurrencyId(), bmoQuoteItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuoteItem = (BmoQuoteItem)autoPopulate(pmConn, new BmoQuoteItem());

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoQuoteItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoQuoteItem.setBmoProduct(bmoProduct);

		// BmoQuoteGroup
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		int quoteGroupId = (int)pmConn.getInt(bmoQuoteGroup.getIdFieldName());
		if (quoteGroupId > 0) bmoQuoteItem.setBmoQuoteGroup((BmoQuoteGroup) new PmQuoteGroup(getSFParams()).populate(pmConn));
		else bmoQuoteItem.setBmoQuoteGroup(bmoQuoteGroup);

		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoQuoteItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoQuoteItem.setBmoArea(bmoArea);

		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoQuoteItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoQuoteItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoQuoteItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuoteItem = (BmoQuoteItem)bmObject;

		// Operaciones con el Grupo de Items de la cotizacion
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		if (bmoQuoteItem.getQuoteGroupId().toInteger() > 0)
			bmoQuoteGroup = (BmoQuoteGroup)pmQuoteGroup.get(pmConn, bmoQuoteItem.getQuoteGroupId().toInteger());
		else 
			bmUpdateResult.addError(bmoQuoteItem.getQuoteGroupId().getName(), "No está seleccionado el " + bmoQuoteItem.getQuoteGroupId().getLabel() + " del item");

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());
		
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			// Si esta ligado a los productos, obten el registro del producto y su precio vigente
			if (bmoQuoteItem.getProductId().toInteger() > 0) {
				PmProduct pmProduct = new PmProduct(getSFParams());
				BmoProduct bmoProduct = (BmoProduct)pmProduct.get(bmoQuoteItem.getProductId().toInteger());

				// Si es nuevo registro, toma el precio del producto
				if (!(bmoQuoteItem.getId() > 0)) {
					if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMNAME))
						bmoQuoteItem.getName().setValue(bmoProduct.getName().toString());

					//					// Si es de tipo RENTA, obtener precios de la renta
					//					if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
					//						//bmoQuoteItem.getPrice().setValue(bmoProduct.getRentalPrice().toDouble());
					//						bmoQuoteItem.getBasePrice().setValue(bmoProduct.getRentalPrice().toDouble());
					//					} 
					//					// Si es diferente de RENTA, obtener los precios de venta
					//					else {
					//						//bmoQuoteItem.getPrice().setValue(bmoProduct.getSalePrice().toDouble());
					//						bmoQuoteItem.getBasePrice().setValue(bmoProduct.getSalePrice().toDouble());
					//					}

					// Obtiene el precio vigente
					PmProductPrice pmProductPrice = new PmProductPrice(getSFParams());
					bmoQuoteItem.getBasePrice().setValue(pmProductPrice.getCurrentPrice(pmConn, bmoQuote.getStartDate().toString(),
							bmoProduct.getId(), bmoQuote.getCurrencyId().toInteger(), bmoQuote.getOrderTypeId().toInteger(), bmoOpportunity.getForeignWFlowTypeId().toInteger(),
							bmoQuote.getMarketId().toInteger(), bmoQuote.getCompanyId().toInteger()));

				} /*
					// Se comenta por:
					// Quien tenia permiso y cambiaba el precio del item, ej: 100,
					// y quien no tiene permiso, y cambiaba los DIAS y la CANTIDAD, lo restablecia, o sea, quitaba el 100 de quien si tenia permiso
					else {
						// Restablece el precio de catalogo si no tiene permisos
						if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
							if ((bmoQuoteItem.getPrice().toDouble() > 0 && 
									bmoQuoteItem.getPrice().toDouble() < bmoQuoteItem.getBasePrice().toDouble())) {
								bmoQuoteItem.getPrice().setValue(bmoQuoteItem.getBasePrice().toDouble());
							}
						}
					}
				 */

				// Validar que el producto sea de la empresa de la cotizacion,
				// o que el producto no tenga empresa
				PmProductCompany pmProductCompany = new PmProductCompany(getSFParams());
				boolean productCompany = pmProductCompany.compareProductCompany(pmConn, 
						bmoQuoteItem.getProductId().toInteger(), 
						bmoQuote.getCompanyId().toInteger());
				if (!productCompany)
					bmUpdateResult.addMsg("El Producto no pertecene a la Empresa de la Oportunidad");
				
				
				// Validar que la cantidad no sea fraccion si el producto es SERIE
				// Si es Sin Rastreo, verificar por la Unidad del producto
				if (!pmProduct.applyFraction(bmoProduct, bmoQuoteItem.getQuantity().toDouble()))
					bmUpdateResult.addError(bmoQuoteItem.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");

			} else {

				// Si no tiene permisos para agregar items sin producto, manda error
				if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_NOPRODUCTITEM))
					bmUpdateResult.addMsg("No cuenta con Permisos para agregar Items sin Producto Ligado: debe Seleccionar un Producto.");

				bmoQuoteItem.getBasePrice().setValue(0);
			}

			// Revisa que el precio no sea menor a 0
			if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
				if (bmoQuoteItem.getPrice().toDouble() < 0)
					bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");
			}	

			// Revisa que los dias no sea menor a 0
			if (bmoQuoteItem.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");
			else if (bmoQuoteItem.getDays().toDouble() == 0)
				bmoQuoteItem.getDays().setValue(1);

			// Revisa que la cantidad no sea menor a 0
			if (bmoQuoteItem.getQuantity().toDouble() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

			// Calcula el valor del item
			double amount = bmoQuoteItem.getPrice().toDouble() * bmoQuoteItem.getQuantity().toDouble();
			//Si los dias son mayores a 0 multiplicar por los dias
			if (bmoQuoteItem.getDays().toDouble() > 0)
				amount = amount * bmoQuoteItem.getDays().toDouble();
			bmoQuoteItem.getAmount().setValue(amount);
			
			// Si no esta asignado el indice, agrega el siguiente
			if (!(bmoQuoteItem.getIndex().toInteger() > 0)) {
				bmoQuoteItem.getIndex().setValue(nextIndex(pmConn, bmoQuoteItem));
			}

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			pmQuoteGroup.calculateAmount(pmConn, bmoQuoteGroup, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	public BmUpdateResult simpleSave(PmConn pmConn, BmoQuoteItem bmoQuoteItem, BmUpdateResult bmUpdateResult) throws SFException{
		return super.save(pmConn, bmoQuoteItem, bmUpdateResult);
	}
	
	// Regresa el indice maximo mas 1
	private int nextIndex(PmConn pmConn, BmoQuoteItem bmoQuoteItem) throws SFException {
		int index = 1;
		String sql = " SELECT MAX(qoit_index) as maxindex FROM quoteitems " +
				" WHERE qoit_quotegroupid = " + bmoQuoteItem.getQuoteGroupId(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			return pmConn.getInt("maxindex") + 1 ;
		else 
			return index;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoQuoteItem = (BmoQuoteItem)bmObject;

		// Obten el grupo de la cotizacion
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
		BmoQuoteGroup bmoQuoteGroup = (BmoQuoteGroup)pmQuoteGroup.get(bmoQuoteItem.getQuoteGroupId().toInteger());

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			if (action.equals(BmoQuoteItem.ACTION_CHANGEINDEX)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();

					printDevLog("Iniciando accion change index");

					// Revisa si debe subir de indice
					changeIndex(pmConn, bmoQuoteItem, value);

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
	private void changeIndex(PmConn pmConn, BmoQuoteItem bmoQuoteItem, String direction) throws SFException {
		String sql = "";
		int replaceQuoteItemId = 0, replaceIndex = 0;

		if (direction.equalsIgnoreCase(BmoFlexConfig.UP)) {
			printDevLog("Iniciando subir indice");

			// Ubica si hay algun item antes 
			sql = " SELECT qoit_quoteitemid, qoit_index FROM quoteitems "
					+ " WHERE qoit_index < " + bmoQuoteItem.getIndex().toInteger()
					+ " AND qoit_quotegroupid = " + bmoQuoteItem.getQuoteGroupId().toInteger()
					+ " ORDER BY qoit_index DESC"; 
			pmConn.doFetch(sql);

			printDevLog(sql);

			if (pmConn.next()) {
				printDevLog("Encontro otro registro anterior");

				// Si hay otro, reuperar indice del grupo a sustituir
				replaceQuoteItemId = pmConn.getInt("qoit_quoteitemid");
				replaceIndex = pmConn.getInt("qoit_index");

				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE quoteitems SET qoit_index = " + bmoQuoteItem.getIndex().toInteger()
						+ " WHERE qoit_quoteitemid = " + replaceQuoteItemId);

				pmConn.doUpdate("UPDATE quoteitems SET qoit_index = " + replaceIndex
						+ " WHERE qoit_quoteitemid = " + bmoQuoteItem.getId());
			}
		} else if (direction.equalsIgnoreCase(BmoFlexConfig.DOWN)) {
			printDevLog("Iniciando bajar indice");

			// Ubica si hay algun item antes 
			sql = " SELECT qoit_quoteitemid, qoit_index FROM quoteitems "
					+ " WHERE qoit_index > " + bmoQuoteItem.getIndex().toInteger()
					+ " AND qoit_quotegroupid = " + bmoQuoteItem.getQuoteGroupId().toInteger()
					+ " ORDER BY qoit_index ASC"; 
			pmConn.doFetch(sql);

			printDevLog(sql);

			if (pmConn.next()) {
				printDevLog("Encontro otro registro posterior");

				// Si hay otro, reuperar indice del grupo a sustituir
				replaceQuoteItemId = pmConn.getInt("qoit_quoteitemid");
				replaceIndex = pmConn.getInt("qoit_index");

				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE quoteitems SET qoit_index = " + bmoQuoteItem.getIndex().toInteger()
						+ " WHERE qoit_quoteitemid = " + replaceQuoteItemId);

				pmConn.doUpdate("UPDATE quoteitems SET qoit_index = " + replaceIndex
						+ " WHERE qoit_quoteitemid = " + bmoQuoteItem.getId());
			}
		}
	}

	@Override
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		bmoQuoteItem = (BmoQuoteItem)bmObject;
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Operaciones con el Grupo de Items de la cotizacion
			PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
			BmoQuoteGroup bmoQuoteGroup = (BmoQuoteGroup)pmQuoteGroup.get(pmConn, bmoQuoteItem.getQuoteGroupId().toInteger());

			// Obten la cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteGroup.getQuoteId().toInteger());

			// Si la cotización ya está autorizada, no se puede hacer movimientos
			if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
				bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
			} else {

				// Primero elimina el item
				super.delete(pmConn, bmObject, bmUpdateResult);

				// Recalcula el subtotal tomando en cuenta el item eliminado
				pmQuoteGroup.calculateAmount(pmConn, bmoQuoteGroup, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) pmConn.commit();
			}
		} catch (SFPmException e) {
			System.out.println(e.toString());
		} finally {
			pmConn.close();
		}	
		return bmUpdateResult;
	}

	public void deleteByQuoteGroup(PmConn pmConn, int quoteGroupId) throws SFPmException {
		pmConn.doUpdate("DELETE FROM " + bmoQuoteItem.getKind() + " WHERE " + 
				bmoQuoteItem.getQuoteGroupId().getName() + " = " + quoteGroupId);
	}

	public int getSequence(PmConn pmConn, int quoteItemId, int quoteGroupId) {
		int sequence = 1;
		try {
			String sql = "SELECT COUNT(qoit_quoteitemid) FROM quoteitems " + 
					" WHERE qoit_quoteitemid <> " + quoteItemId + 
					" AND qoit_quotegroupid = " + quoteGroupId;
			pmConn.doFetch(sql);
			if (pmConn.next()) sequence = pmConn.getInt(1) + 1;
		} catch (SFPmException e) {
			System.out.println("PmQuoteItem-getSequence() ERROR: " + e.toString());
		}
		return sequence;
	}

}
