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
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductLink;
import com.flexwm.shared.op.BmoRequisitionItem;


public class PmOrderItem extends PmObject {
	BmoOrderItem bmoOrderItem;

	public PmOrderItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderItem = new BmoOrderItem();
		setBmObject(bmoOrderItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderItem.getOrderGroupId(), bmoOrderItem.getBmoOrderGroup()),
				new PmJoin(bmoOrderItem.getProductId(), bmoOrderItem.getBmoProduct()),
				new PmJoin(bmoOrderItem.getBmoProduct().getProductFamilyId(), bmoOrderItem.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoOrderItem.getBmoProduct().getProductGroupId(), bmoOrderItem.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoOrderItem.getBmoProduct().getUnitId(), bmoOrderItem.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoOrderItem.getAreaId() , bmoOrderItem.getBmoArea()),
				new PmJoin(bmoOrderItem.getBudgetItemId(), bmoOrderItem.getBmoBudgetItem()),
				new PmJoin(bmoOrderItem.getBmoBudgetItem().getBudgetId(), bmoOrderItem.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoOrderItem.getBmoBudgetItem().getBudgetItemTypeId(), bmoOrderItem.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoOrderItem.getBmoBudgetItem().getCurrencyId(), bmoOrderItem.getBmoBudgetItem().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderItem = (BmoOrderItem)autoPopulate(pmConn, new BmoOrderItem());

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoOrderItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoOrderItem.setBmoProduct(bmoProduct);

		// BmoOrder
		BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
		int orderId = (int)pmConn.getInt(bmoOrderGroup.getIdFieldName());
		if (orderId > 0) bmoOrderItem.setBmoOrderGroup((BmoOrderGroup) new PmOrderGroup(getSFParams()).populate(pmConn));
		else bmoOrderItem.setBmoOrderGroup(bmoOrderGroup);

		// BmoArea
		BmoArea bmoArea = new BmoArea();
		int areaId = pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoOrderItem.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoOrderItem.setBmoArea(bmoArea);

		// BmoBudgetItem
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoOrderItem.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoOrderItem.setBmoBudgetItem(bmoBudgetItem);

		return bmoOrderItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderItem = (BmoOrderItem)bmObject;
		
		boolean newRecord = false;
		if (!(bmoOrderItem.getId() > 0)) 
			newRecord = true;

		// Obten el grupo de pedido y el pedido
		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		BmoOrderGroup bmoOrderGroup =  new BmoOrderGroup();
		if (bmoOrderItem.getOrderGroupId().toInteger() > 0)
			bmoOrderGroup = (BmoOrderGroup)pmOrderGroup.get(pmConn, bmoOrderItem.getOrderGroupId().toInteger());
		else 
			bmUpdateResult.addError(bmoOrderItem.getOrderGroupId().getName(), "No está seleccionado el " + bmoOrderItem.getOrderGroupId().getLabel() + " del item");

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderGroup.getOrderId().toInteger());

		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		BmoOrderType bmoOrderType = (BmoOrderType)pmOrderType.get(pmConn, bmoOrder.getOrderTypeId().toInteger());

		PmProduct pmProduct = new PmProduct(getSFParams());
		BmoProduct bmoProduct = new BmoProduct();
		//No permite cambios en DRea en un kit
		if (bmoOrderGroup.getIsKit().toBoolean() && bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (bmoOrderItem.getId() > 0) {
				bmUpdateResult.addMsg("No se puede modificar el ítem , es un Kit");
			}
		}

		// Obten el registro del producto y su precio vigente
		if (bmoOrderItem.getProductId().toInteger() > 0) {
			bmoProduct = (BmoProduct)pmProduct.get(bmoOrderItem.getProductId().toInteger());

			// Si es nuevo registro, toma el precio del producto
			if (newRecord) {
				if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMNAME))
					bmoOrderItem.getName().setValue(bmoProduct.getName().toString());

				// Asigna precios y costos segun tipo de pedido
				if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
					bmoOrderItem.getBaseCost().setValue(bmoProduct.getRentalCost().toDouble());
				} else {
					bmoOrderItem.getBaseCost().setValue(bmoProduct.getCost().toDouble());
				}

				// Obtiene el precio vigente
				PmProductPrice pmProductPrice = new PmProductPrice(getSFParams());
				bmoOrderItem.getBasePrice().setValue(pmProductPrice.getCurrentPrice(pmConn, bmoOrder.getLockStart().toString(),
						bmoProduct.getId(), bmoOrder.getCurrencyId().toInteger(), bmoOrder.getOrderTypeId().toInteger(), bmoOrder.getWFlowTypeId().toInteger(),
						bmoOrder.getMarketId().toInteger(), bmoOrder.getCompanyId().toInteger()));
			}

			// Si es de tipo producto o subproducto y esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (bmoProduct.getType().toChar() == BmoProduct.TYPE_PRODUCT
					|| bmoProduct.getType().toChar() == BmoProduct.TYPE_SUBPRODUCT) {
				// Si afecta inventario, se revisa conflicto
				if (bmoProduct.getInventory().toBoolean()) {

					// Si esta habilitado el bloqueo de pedidos revisar
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
						// Primero agrega el ultimo valor
						super.save(pmConn, bmoOrderItem, bmUpdateResult);

						// Forzar revision de bloqueo
						if (isLocked(pmConn, bmoOrder, bmoOrderItem))
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
						else
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);	
					} else {
						bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
					}

				} else {
					// No afecta inventario, no hay bloqueo
					bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
				}
			} else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_CLASS) {
				bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);
			} else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED) {
				printDevLog("Producto Compuesto: " + bmoProduct.getCode());
				// Primero agrega el ultimo valor
				super.save(pmConn, bmoOrderItem, bmUpdateResult);
				// crear/actualizar subproductos
				createItemsComposed(pmConn, bmoOrderItem, bmoOrder, newRecord, bmUpdateResult);
			}

			// Validar que el producto sea de la empresa de la cotizacion,
			// o que el producto no tenga empresa
			PmProductCompany pmProductCompany = new PmProductCompany(getSFParams());
			boolean productCompany = pmProductCompany.compareProductCompany(pmConn, 
					bmoOrderItem.getProductId().toInteger(), 
					bmoOrder.getCompanyId().toInteger());
			if (!productCompany)
				bmUpdateResult.addMsg("El Producto no pertecene a la Empresa del Pedido");
			
			
			// Validar que la cantidad no sea fraccion si el producto es SERIE
			// Si es Sin Rastreo, verificar por la Unidad del producto
			if (!pmProduct.applyFraction(bmoProduct, bmoOrderItem.getQuantity().toDouble()))
				bmUpdateResult.addError(bmoOrderItem.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");

		} else {
			bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
			bmoOrderItem.getBasePrice().setValue(bmoOrderItem.getPrice().toDouble());
			bmoOrderItem.getBaseCost().setValue(0);
		}

		//Revisar si existe una CxC Ligada al grupo
		if (pmOrderGroup.orderGroupHasRaccAuthorized(pmConn, bmoOrderGroup, bmUpdateResult)) {
			bmUpdateResult.addMsg("Existe una CxC autorizada ligada al grupo.");
		}

		// Revisa que el precio no sea menor a 0
		if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {			
			if (bmoOrderItem.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");
		}	

		// Revisa que los dias no sea menor a 0
		if (bmoOrderItem.getDays().toDouble() < 0)
			bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");
		else if (bmoOrderItem.getDays().toDouble() == 0)
			bmoOrderItem.getDays().setValue(1);

		// Revisa que la cantidad no sea menor a 0
		if (bmoOrderItem.getQuantity().toDouble() <= 0)
			bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

		// Calcula el valor del item
		bmoOrderItem.getAmount().setValue(
				bmoOrderItem.getPrice().toDouble() * 
				bmoOrderItem.getQuantity().toDouble() * 
				bmoOrderItem.getDays().toDouble()
				);

		// Si no esta asignado el indice, agrega el siguiente
		if (!(bmoOrderItem.getIndex().toInteger() > 0)) {
			bmoOrderItem.getIndex().setValue(nextIndex(pmConn, bmoOrderItem));
		}

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			double discount = 0;
			//Descuento
			if (bmoOrderItem.getDiscountApplies().toBoolean()) {					
					discount = bmoOrderItem.getAmount().toDouble() * (bmoOrderGroup.getDiscountRate().toDouble()/100);
					bmoOrderItem.getDiscount().setValue(discount);
			} else {
				bmoOrderItem.getDiscount().setValue(0);
			}		
			bmoOrderItem.getAmount().setValue(bmoOrderItem.getAmount().toDouble() - discount );
		}
		
		// Primero agrega el ultimo valor
		super.save(pmConn, bmoOrderItem, bmUpdateResult);

		// Recalcula el subtotal
		pmOrderGroup.calculateAmount(pmConn, bmoOrder, bmoOrderGroup, bmUpdateResult);

		// Asigna estatus de bloqueo
		pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

		// Recalcula Entrega de Pedido, si ya se han creado envios de pedido
		// Si se llega a meter mas productos despues de una salida, estando en el evento, es decir;
		// cambian el pedido de autorizado a en revision para meter mas productos y vuelven a autorizar,
		// se recalcula cuando meten un item
		PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
		if (pmOrderDelivery.getOrderHasTypeDelivery(pmConn, bmoOrder.getId()))
			pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);
		
		// Aplicar si esta activo en el tipo de pedido
		if (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0) {
			// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Regresa el indice maximo mas 1
	private int nextIndex(PmConn pmConn, BmoOrderItem bmoOrderItem) throws SFException {
		int index = 1;
		String sql = " SELECT MAX(ordi_index) as maxindex FROM orderitems " +
				" WHERE ordi_ordergroupid = " + bmoOrderItem.getOrderGroupId(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			return pmConn.getInt("maxindex") + 1 ;
		else 
			return index;
	}

	// Obtener sub-productos del producto
	public BmUpdateResult createItemsComposed(PmConn pmConn, BmoOrderItem bmoOrderItem, BmoOrder bmoOrder, boolean newRecord, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener producto
		BmoProduct bmoProduct = new BmoProduct();
		PmProduct pmProduct = new PmProduct(getSFParams());
		bmoProduct = (BmoProduct)pmProduct.get(bmoOrderItem.getProductId().toInteger());

		// Crear subproductos(items) del producto compuesto
		if (newRecord) {
			// Validar que sea un producto compuesto
			if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED) {

				BmoProductLink bmoProductLink = new BmoProductLink();
				PmProductLink pmProductLink = new PmProductLink(getSFParams());
				PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());

				// Filtro por producto
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterByProduct = new BmFilter();

				filterByProduct.setValueFilter(bmoProductLink.getKind(), bmoProductLink.getProductId().getName(), bmoOrderItem.getProductId().toInteger());
				filterList.add(filterByProduct);

				// Traer los subproductos del producto compuesto
				Iterator<BmObject> listSubProduct = pmProductLink.list(pmConn, filterList).iterator();
				while (listSubProduct.hasNext()) {
					bmoProductLink = (BmoProductLink)listSubProduct.next();
					printDevLog("Clave subProducto: "+bmoProductLink.getBmoProductLinked().getCode());
					printDevLog("days: "+bmoOrderItem.getDays().toInteger());

					BmoOrderItem bmoOrderItemSubProduct = new BmoOrderItem();
					bmoOrderItemSubProduct.getProductId().setValue(bmoProductLink.getProductLinkedId().toInteger());
					bmoOrderItemSubProduct.getName().setValue(bmoProductLink.getBmoProductLinked().getName().toString());
					bmoOrderItemSubProduct.getDescription().setValue(bmoProductLink.getBmoProductLinked().getDescription().toString());
					bmoOrderItemSubProduct.getQuantity().setValue(bmoOrderItem.getQuantity().toDouble());
					bmoOrderItemSubProduct.getDays().setValue(bmoOrderItem.getDays().toInteger());
					// Validacion menor
					if (bmoOrderItemSubProduct.getDays().toInteger() < 0)
						bmoOrderItemSubProduct.getDays().setValue(1);						
					// Asigna precios y costos segun tipo de pedido
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						bmoOrderItem.getBaseCost().setValue(bmoProductLink.getBmoProductLinked().getRentalCost().toDouble());
					} else {
						bmoOrderItem.getBaseCost().setValue(bmoProductLink.getBmoProductLinked().getCost().toDouble());
					}
					bmoOrderItemSubProduct.getBasePrice().setValue(0);
					bmoOrderItemSubProduct.getPrice().setValue(0);
					bmoOrderItemSubProduct.getAmount().setValue(0);
					bmoOrderItemSubProduct.getOrderGroupId().setValue(bmoOrderItem.getOrderGroupId().toInteger());
					bmoOrderItemSubProduct.getOrderItemComposedId().setValue(bmoOrderItem.getId());
					bmoOrderItemSubProduct.getCommission().setValue(bmoProductLink.getBmoProductLinked().getCommision().toInteger());
					bmoOrderItemSubProduct.getIndex().setValue(bmoOrderItem.getIndex().toInteger());

					// Pasar datos de control presupuestal
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

						// Obtener datos de la empresa del Sub Producto
						BmoProductCompany bmoProductCompany = new BmoProductCompany();
						PmProductCompany pmProductCompany = new PmProductCompany(getSFParams());
						bmoProductCompany = (BmoProductCompany)(pmProductCompany.getProductCompanySpecific("" + bmoProductLink.getProductLinkedId().toInteger(), 
								bmoOrder.getCompanyId().toInteger(), 
								bmUpdateResult)).getBmObject();

						// Colocar partida/dpto. de la empresa del Sub producto
						if (bmoProductCompany.getId() > 0) {
							if (bmoProductCompany.getBudgetItemId().toInteger() > 0)
								bmoOrderItemSubProduct.getBudgetItemId().setValue(bmoProductCompany.getBudgetItemId().toInteger());
							if (bmoProductCompany.getAreaId().toInteger() > 0)
								bmoOrderItemSubProduct.getAreaId().setValue(bmoProductCompany.getAreaId().toInteger());
						} 

						// Colocar partida/dpto. del producto(no tiene empresas), si no tiene datos regresar por defecto a las del pedido
						else {
							// Tomar datos del producto, sino del pedido
							if (bmoProductLink.getBmoProductLinked().getBudgetItemId().toInteger() > 0)
								bmoOrderItemSubProduct.getBudgetItemId().setValue(bmoProductLink.getBmoProductLinked().getBudgetItemId().toInteger());
							else {
								if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0) 
									bmoOrderItemSubProduct.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
							}
							if (bmoProductLink.getBmoProductLinked().getAreaId().toInteger() > 0)
								bmoOrderItemSubProduct.getAreaId().setValue(bmoProductLink.getBmoProductLinked().getAreaId().toInteger());
							else {
								if (bmoOrder.getDefaultAreaId().toInteger() > 0)
									bmoOrderItemSubProduct.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
							}
						}
					}
					pmOrderItem.updateLockStatus(pmConn, bmoOrder, bmoOrderItemSubProduct, false, bmUpdateResult);
					super.save(pmConn, bmoOrderItemSubProduct, bmUpdateResult);
				}
			}
		} 
		// Actualizar los datos de los subproductos
		else {

			// Filtro por producto
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByProductComposed = new BmFilter();

			filterByProductComposed.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderItemComposedId().getName() , bmoOrderItem.getId());
			filterList.add(filterByProductComposed);

			// Traer los items del producto compuesto
			PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
			Iterator<BmObject> listItems = pmOrderItem.list(pmConn, filterList).iterator();
			while (listItems.hasNext()) {
				BmoOrderItem bmoOrderItemSubProduct = new BmoOrderItem();
				bmoOrderItemSubProduct = (BmoOrderItem)listItems.next();

				// Actualizar cantidad y dias para todos los subproductos del producto compuesto
				bmoOrderItemSubProduct.getQuantity().setValue(bmoOrderItem.getQuantity().toDouble());
				bmoOrderItemSubProduct.getDays().setValue(bmoOrderItem.getDays().toInteger());
				super.save(pmConn, bmoOrderItemSubProduct, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}

	// Solo actualiza estatus de bloqueo
	public BmUpdateResult updateLockStatus(PmConn pmConn, BmoOrder bmoOrder, BmoOrderItem bmoOrderItem, boolean forceCalculate, BmUpdateResult bmUpdateResult) throws SFException {

		// Obten el registro del producto y su precio vigente
		if (bmoOrderItem.getProductId().toInteger() > 0) {
			PmProduct pmProduct = new PmProduct(getSFParams());
			BmoProduct bmoProduct = (BmoProduct)pmProduct.get(bmoOrderItem.getProductId().toInteger());

			// Si es de tipo producto o subproducto y esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (bmoProduct.getType().toChar() == BmoProduct.TYPE_PRODUCT
					|| bmoProduct.getType().toChar() == BmoProduct.TYPE_SUBPRODUCT) {

				// Si afecta inventario, se revisa conflicto
				if (bmoProduct.getInventory().toBoolean()) {

					// Si esta habilitado el bloqueo de pedidos revisar
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
						// Primero agrega el ultimo valor
						super.save(pmConn, bmoOrderItem, bmUpdateResult);

						// Forzar revision de bloqueo
						if (isLocked(pmConn, bmoOrder, bmoOrderItem))
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
						else
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);	
					} else {
						bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
					}

				} else {
					// No afecta inventario, no hay conflicto
					bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
				}
			} else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_CLASS) 
				bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);
			else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED)
				bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
		} else {
			bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
		}

		super.save(pmConn, bmoOrderItem, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult createFromQuote(PmConn pmConn, BmObject bmObject, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderItem = (BmoOrderItem)bmObject;

		PmProduct pmProduct = new PmProduct(getSFParams());
		BmoProduct bmoProduct = new BmoProduct();

		// Asigna bloqueo abierto por default
		bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);

		// Obten el registro del producto y su precio vigente
		if (bmoOrderItem.getProductId().toInteger() > 0) {
			bmoProduct = (BmoProduct)pmProduct.get(bmoOrderItem.getProductId().toInteger());
			bmoOrderItem.getName().setValue(bmoProduct.getName().toString());

			// Si es de tipo producto y esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (bmoProduct.getType().toChar() == BmoProduct.TYPE_PRODUCT) {
				// Si afecta inventario, se revisa conflicto
				if (bmoProduct.getInventory().toBoolean()) {

					// Si esta habilitado el bloqueo de pedidos revisar
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
						// Primero agrega el ultimo valor
						super.save(pmConn, bmoOrderItem, bmUpdateResult);

						// Forzar revision de bloqueo
						if (isLocked(pmConn, bmoOrder, bmoOrderItem))
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
						else
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);	
					} else {
						bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
					}

				} else {
					// No afecta inventario, no hay conflicto
					bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
				}
			} else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_CLASS) 
				bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);
		}

		// Calcula el valor del item
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			bmoOrderItem.getAmount().setValue((
					bmoOrderItem.getPrice().toDouble() * 
					bmoOrderItem.getQuantity().toDouble() * 
					bmoOrderItem.getDays().toDouble()) - bmoOrderItem.getDiscount().toDouble()
					);
		} else {
			bmoOrderItem.getAmount().setValue(
					bmoOrderItem.getPrice().toDouble() * 
					bmoOrderItem.getQuantity().toDouble() * 
					bmoOrderItem.getDays().toDouble()
					);
		}

		// Primero agrega el ultimo valor
		super.save(pmConn, bmoOrderItem, bmUpdateResult);

		return bmUpdateResult;
	}

	// Revisa si el tipo de producto esta bloqueado
	public boolean isLocked(PmConn pmConn, BmoOrder bmoOrder, BmoOrderItem bmoOrderItem) throws SFException{		
		// Recuperar items bloqueados
		String lockedQuantitySql = "SELECT SUM(orlk_quantity) FROM orderlocks WHERE orlk_productid = " + bmoOrderItem.getProductId().toInteger();
		pmConn.doFetch(lockedQuantitySql);
		pmConn.next();
		int lockedQuantity = pmConn.getInt(1);
		if (!getSFParams().isProduction()) System.out.println("PmOrderItem-isLocked(): lockedQuantity: " + lockedQuantity);

		// Recuperar items requeridos
		String orderQuantitySql = "SELECT SUM(ordi_quantity) FROM orderitems "
				+ " LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) "
				+ " WHERE ordg_orderid = " + bmoOrder.getId()
				+ " AND ordi_productid = " + bmoOrderItem.getProductId().toInteger();
		pmConn.doFetch(orderQuantitySql);
		pmConn.next();
		double orderQuantity = pmConn.getDouble(1);
		if (!getSFParams().isProduction()) System.out.println("PmOrderItem-isLocked(): orderQuantity: " + orderQuantity);

		if (lockedQuantity >= orderQuantity)
			return true;
		else 
			return false;
	}

	// La OC cambio, cambia el item 
	public void updateFromRequsitionItem(PmConn pmConn, BmoOrder bmoOrder, BmoRequisitionItem bmoRequisitionItem, boolean forceCalculate, BmUpdateResult bmUpdateResult) {
		try {
			BmoOrderItem bmoOrderItem = new BmoOrderItem();
			bmoOrderItem = (BmoOrderItem)getBy(pmConn, bmoRequisitionItem.getProductId().toInteger(), bmoOrderItem.getProductId().getName());

			// Actualiza item
			updateLockStatus(pmConn, bmoOrder, bmoOrderItem, forceCalculate, bmUpdateResult);

		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-updateFromRequisitionItem() " + e.toString());
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoOrderItem = (BmoOrderItem)bmObject;

		// Obten el grupo de la cotización
		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		BmoOrderGroup bmoOrderGroup = (BmoOrderGroup)pmOrderGroup.get(bmoOrderItem.getOrderGroupId().toInteger());

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderGroup.getOrderId().toInteger());

		// Si la acciones es modificar items, lo lleva a cabo
		if (action.equals(BmoOrderItem.ACTION_CHANGEORDERITEM)) {
			PmConn pmConn = new PmConn(getSFParams());
			try {
				pmConn.open();
				pmConn.disableAutoCommit();

				// Modifica unicamente el id y nombre del item, no el precio
				int productId = Integer.parseInt(value);
				PmProduct pmProduct = new PmProduct(getSFParams());
				BmoProduct bmoProduct = (BmoProduct)pmProduct.get(productId);

				bmoOrderItem.getName().setValue(bmoProduct.getName().toString());
				bmoOrderItem.getProductId().setValue(productId);

				// Si es de tipo producto y esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
				if (bmoProduct.getType().toChar() == BmoProduct.TYPE_PRODUCT) {

					// Si afecta inventario, se revisa conflicto
					if (bmoProduct.getInventory().toBoolean()) {

						// Si esta habilitado el bloqueo de pedidos revisar
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
							// Primero agrega el ultimo valor
							super.save(pmConn, bmoOrderItem, bmUpdateResult);

							// Forzar revision de bloqueo
							if (isLocked(pmConn, bmoOrder, bmoOrderItem))
								bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
							else
								bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);	
						} else {
							bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
						}

					} else {
						// No afecta inventario, no hay bloqueo
						bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_OPEN);
					}
				} else if (bmoProduct.getType().toChar() == BmoProduct.TYPE_CLASS) 
					bmoOrderItem.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_CONFLICT);

				// Crear el grupo de la cotización
				super.saveSimple(pmConn, bmoOrderItem, bmUpdateResult);

				// Asigna estatus de bloqueo
				pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) {
					pmConn.commit();
				}
			} catch (SFPmException e) {
				bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
			} finally {
				pmConn.close();
			}
		} else {
			if (action.equals(BmoOrderItem.ACTION_CHANGEINDEX)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();

					printDevLog("Iniciando accion change index");

					// Revisa si debe subir de indice
					changeIndex(pmConn, bmoOrderItem, value);

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
	private void changeIndex(PmConn pmConn, BmoOrderItem bmoOrderItem, String direction) throws SFException {
		String sql = "";
		int replaceOrderItemId = 0, replaceIndex = 0;

		if (direction.equalsIgnoreCase(BmoFlexConfig.UP)) {
			printDevLog("Iniciando subir indice");

			// Ubica si hay algun item antes 
			sql = " SELECT ordi_orderitemid, ordi_index FROM orderitems "
					+ " WHERE ordi_index < " + bmoOrderItem.getIndex().toInteger()
					+ " AND ordi_ordergroupid = " + bmoOrderItem.getOrderGroupId().toInteger()
					+ " ORDER BY ordi_index DESC"; 
			pmConn.doFetch(sql);

			printDevLog(sql);

			if (pmConn.next()) {
				printDevLog("Encontro otro registro anterior");

				// Si hay otro, reuperar indice del grupo a sustituir
				replaceOrderItemId = pmConn.getInt("ordi_orderitemid");
				replaceIndex = pmConn.getInt("ordi_index");

				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE orderitems SET ordi_index = " + bmoOrderItem.getIndex().toInteger()
						+ " WHERE ordi_orderitemid = " + replaceOrderItemId);

				pmConn.doUpdate("UPDATE orderitems SET ordi_index = " + replaceIndex
						+ " WHERE ordi_orderitemid = " + bmoOrderItem.getId());
			}
		} else if (direction.equalsIgnoreCase(BmoFlexConfig.DOWN)) {
			printDevLog("Iniciando bajar indice");

			// Ubica si hay algun item antes 
			sql = " SELECT ordi_orderitemid, ordi_index FROM orderitems "
					+ " WHERE ordi_index > " + bmoOrderItem.getIndex().toInteger()
					+ " AND ordi_ordergroupid = " + bmoOrderItem.getOrderGroupId().toInteger()
					+ " ORDER BY ordi_index ASC"; 
			pmConn.doFetch(sql);

			printDevLog(sql);

			if (pmConn.next()) {
				printDevLog("Encontro otro registro posterior");

				// Si hay otro, reuperar indice del grupo a sustituir
				replaceOrderItemId = pmConn.getInt("ordi_orderitemid");
				replaceIndex = pmConn.getInt("ordi_index");

				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE orderitems SET ordi_index = " + bmoOrderItem.getIndex().toInteger()
						+ " WHERE ordi_orderitemid = " + replaceOrderItemId);

				pmConn.doUpdate("UPDATE orderitems SET ordi_index = " + replaceIndex
						+ " WHERE ordi_orderitemid = " + bmoOrderItem.getId());
			}
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderItem = (BmoOrderItem)bmObject;

		// Obten el grupo de pedido y el pedido
		PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
		BmoOrderGroup bmoOrderGroup = (BmoOrderGroup)pmOrderGroup.get(pmConn, bmoOrderItem.getOrderGroupId().toInteger());

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderGroup.getOrderId().toInteger());

		// Obtener el producto
		PmProduct pmProduct = new PmProduct(getSFParams()); 
		BmoProduct bmoProduct = new BmoProduct();
		if (bmoOrderItem.getProductId().toInteger() > 0)
			bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoOrderItem.getProductId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {

			// Quitar primero los items del producto compuesto
			if (bmoOrderItem.getProductId().toInteger() > 0) {
				if (bmoProduct.getType().toChar() == BmoProduct.TYPE_COMPOSED)
					deleteByProductComposed(pmConn, bmoOrderItem.getId());
			}

			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			//Revisar si existe una CxC Ligada al grupo
			if (pmOrderGroup.orderGroupHasRaccAuthorized(pmConn, bmoOrderGroup, bmUpdateResult)) {
				bmUpdateResult.addMsg("Existe una CxC autorizada ligada al grupo.");
			}

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrderGroup.calculateAmount(pmConn, bmoOrder, bmoOrderGroup, bmUpdateResult);
			
			// Aplicar si esta activo en el tipo de pedido
			if (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0) {
				// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
			} 
			// Asigna estatus de bloqueo
			pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

			// Recalcula Entrega de Pedido, si ya se han creado envios de pedido
			// Si se llega a meter mas productos despues de una salida, estando en el evento, es decir;
			// cambian el pedido de autorizado a en revision para meter mas productos y vuelven a autorizar,
			// se recalcula cuando borran un item
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
			if (pmOrderDelivery.getOrderHasTypeDelivery(pmConn, bmoOrder.getId()))
				pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);
		}
		return bmUpdateResult;
	}

	public void deleteByOrderGroup(PmConn pmConn, int orderGroupId) throws SFPmException {
		pmConn.doUpdate("DELETE FROM " + bmoOrderItem.getKind() + " WHERE " + 
				bmoOrderItem.getOrderGroupId().getName() + " = " + orderGroupId);
	}

	public void deleteByProductComposed(PmConn pmConn, int productComposedId) throws SFPmException {
		pmConn.doUpdate("DELETE FROM " + bmoOrderItem.getKind() + " WHERE " + 
				bmoOrderItem.getOrderItemComposedId().getName() + " = " + productComposedId);
	}
}
