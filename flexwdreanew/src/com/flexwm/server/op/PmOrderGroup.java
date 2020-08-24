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

import java.util.Iterator;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductKitItem;


public class PmOrderGroup extends PmObject {
	BmoOrderGroup bmoOrderGroup;

	public PmOrderGroup(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderGroup = new BmoOrderGroup();
		setBmObject(bmoOrderGroup);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderGroup = (BmoOrderGroup)autoPopulate(pmConn, new BmoOrderGroup());
		return bmoOrderGroup;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderGroup = (BmoOrderGroup)bmObject;

		// Obten la cotización
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderGroup.getOrderId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el pedido - está Autorizada.");
		} else {

			//Si existe una CxC ligada al grupo, no se puede modificar
			if (orderGroupHasRaccAuthorized(pmConn, bmoOrderGroup, bmUpdateResult))
				bmUpdateResult.addMsg("Existe una CxC autorizada ligada al grupo.");

			// Si no esta asignado el indice, agrega el siguiente
			if (!(bmoOrderGroup.getIndex().toInteger() > 0)) {
				bmoOrderGroup.getIndex().setValue(nextIndex(pmConn, bmoOrderGroup));
			}
			//Calcular total de kit
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) && bmoOrderGroup.getIsKit().toBoolean()) {
				bmoOrderGroup.getTotal().setValue(bmoOrderGroup.getDays().toDouble() * bmoOrderGroup.getAmount().toDouble());
				updateItems(pmConn,bmoOrderGroup, bmUpdateResult);
			}
			//calcular descuento y fee
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				if(bmoOrderGroup.getIsKit().toBoolean()) {
					bmoOrderGroup.getFeeProduction().setValue(bmoOrderGroup.getTotal().toDouble() * (bmoOrderGroup.getFeeProductionRate().toDouble()/100));
					bmoOrderGroup.getCommissionAmount().setValue((bmoOrderGroup.getTotal().toDouble() + bmoOrderGroup.getFeeProduction().toDouble())* (bmoOrderGroup.getCommissionRate().toDouble()/100));					
				} else {
					bmoOrderGroup.getFeeProduction().setValue(bmoOrderGroup.getAmount().toDouble() * (bmoOrderGroup.getFeeProductionRate().toDouble()/100));
					bmoOrderGroup.getCommissionAmount().setValue((bmoOrderGroup.getAmount().toDouble() + bmoOrderGroup.getFeeProduction().toDouble()) * (bmoOrderGroup.getCommissionRate().toDouble()/100));				
				}

			}
			super.save(pmConn, bmoOrderGroup, bmUpdateResult);
			
			// Recalcular el pedido completo
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
			
			//Acciones Drea
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				
				if (!bmoOrderGroup.getIsKit().toBoolean())
					updateDiscountitems(pmConn, bmoOrderGroup, bmUpdateResult);
			}
			// Aplicar si esta activo en el tipo de pedido
			if (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0) {
				// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
			}
		}
		return bmUpdateResult;
	}
	//Dar guardar a los items para recalcular valores
	public void updateDiscountitems(PmConn pmConn, BmoOrderGroup bmoOrderGroup,BmUpdateResult bmUpdateResult) throws SFException {
		BmoOrderItem bmoOrderItem = new BmoOrderItem();
		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
		BmFilter bmFilter = new BmFilter();

		bmFilter.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId(), bmoOrderGroup.getId());
		Iterator<BmObject> iterator = pmOrderItem.list(bmFilter).iterator();
		while (iterator.hasNext()) {
			BmoOrderItem nextBmoOrderItem = (BmoOrderItem)iterator.next();
			if (!bmoOrderGroup.getDiscountApplies().toBoolean())
				nextBmoOrderItem.getDiscountApplies().setValue(bmoOrderGroup.getDiscountApplies().toBoolean());

			pmOrderItem.save(pmConn, nextBmoOrderItem, bmUpdateResult);
		}

	}
	public void updateItems(PmConn pmConn, BmoOrderGroup bmoOrderGroup, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "SELECT ordi_orderitemid FROM orderitems  WHERE ordi_ordergroupid =  " + bmoOrderGroup.getId();
		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			BmoOrderItem nextBmoOrderItem = (BmoOrderItem)pmOrderItem.get(pmConn.getInt("ordi_orderitemid"));
			
			nextBmoOrderItem.getDays().setValue(bmoOrderGroup.getDays().toDouble());
			
			pmOrderItem.saveSimple(pmConn, nextBmoOrderItem, bmUpdateResult);			
		}		
	}
	// Sube el grupo un nivel
	private void changeIndex(PmConn pmConn, BmoOrderGroup bmoOrderGroup, String direction) throws SFException {
		String sql = "";
		int replaceOrderGroupId = 0, replaceIndex = 0;
	
		if (direction.equalsIgnoreCase(BmoFlexConfig.UP)) {
			printDevLog("Iniciando subir indice");
	
			// Ubica si hay algun grupo antes 
			sql = " SELECT ordg_ordergroupid, ordg_index FROM ordergroups "
					+ " WHERE ordg_index < " + bmoOrderGroup.getIndex().toInteger()
					+ " AND ordg_orderid = " + bmoOrderGroup.getOrderId().toInteger()
					+ " ORDER BY ordg_index DESC"; 
			pmConn.doFetch(sql);
	
			printDevLog(sql);
	
			if (pmConn.next()) {
				printDevLog("Encontro otro registro anterior");
	
				// Si hay otro, reuperar indice del grupo a sustituir
				replaceOrderGroupId = pmConn.getInt("ordg_ordergroupid");
				replaceIndex = pmConn.getInt("ordg_index");
	
				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE ordergroups SET ordg_index = " + bmoOrderGroup.getIndex().toInteger()
						+ " WHERE ordg_ordergroupid = " + replaceOrderGroupId);
	
				pmConn.doUpdate("UPDATE ordergroups SET ordg_index = " + replaceIndex
						+ " WHERE ordg_ordergroupid = " + bmoOrderGroup.getId());
			}
		} else if (direction.equalsIgnoreCase(BmoFlexConfig.DOWN)) {
			printDevLog("Iniciando bajar indice");
	
			// Ubica si hay algun grupo antes 
			sql = " SELECT ordg_ordergroupid, ordg_index FROM ordergroups "
					+ " WHERE ordg_index > " + bmoOrderGroup.getIndex().toInteger()
					+ " AND ordg_orderid = " + bmoOrderGroup.getOrderId().toInteger()
					+ " ORDER BY ordg_index ASC"; 
			pmConn.doFetch(sql);
	
			printDevLog(sql);
	
			if (pmConn.next()) {
				printDevLog("Encontro otro registro posterior");
	
				// Si hay otro, reuperar indice del grupo a sustituir
				replaceOrderGroupId = pmConn.getInt("ordg_ordergroupid");
				replaceIndex = pmConn.getInt("ordg_index");
	
				// Cambia el indice del anterior
				pmConn.doUpdate("UPDATE ordergroups SET ordg_index = " + bmoOrderGroup.getIndex().toInteger()
						+ " WHERE ordg_ordergroupid = " + replaceOrderGroupId);
	
				pmConn.doUpdate("UPDATE ordergroups SET ordg_index = " + replaceIndex
						+ " WHERE ordg_ordergroupid = " + bmoOrderGroup.getId());
			}
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoOrderGroup = (BmoOrderGroup)bmObject;

		// Obten la cotización
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderGroup.getOrderId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			if (action.equals(BmoOrderGroup.ACTION_PRODUCTKIT)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();
					//Total para drea
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
						bmoOrderGroup.getTotal().setValue(bmoOrderGroup.getDays().toDouble() * bmoOrderGroup.getAmount().toDouble());
					// Crear el grupo de la cotización
					super.save(pmConn, bmoOrderGroup, bmUpdateResult);
					
					bmoOrderGroup.setId(bmUpdateResult.getId());
					
					// Buscar el ultimo index y asignar al nuevo grupo
					int index = 0;
					PmConn pmConnGetIndex = new PmConn(getSFParams());
					pmConnGetIndex.open();
					pmConnGetIndex.doFetch("SELECT " + bmoOrderGroup.getIndex().getName() + " FROM ordergroups WHERE ordg_orderid = " + bmoOrder.getId() + " ORDER BY " + bmoOrderGroup.getIndex().getName()  + " DESC");
											
					if(pmConnGetIndex.next()) {
						index = pmConnGetIndex.getInt(bmoOrderGroup.getIndex().getName()) + 1;
					}
					pmConnGetIndex.close();
					bmoOrderGroup.getIndex().setValue(index);

					// Agregar todos los items del kit a este grupo de cotizaciones
					int productKitId = Integer.parseInt(value);
					addKit(pmConn, bmoOrderGroup, productKitId, bmUpdateResult);

					// Aprovechar para recalcular el grupo y la cotizacion, puede ser redundante...
					calculateAmount(pmConn, bmoOrder, bmoOrderGroup, bmUpdateResult);

					if (!bmUpdateResult.hasErrors()) {
						pmConn.commit();
					}
				} catch (SFPmException e) {
					bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
				} finally {
					pmConn.close();
				}
			} else if  (action.equals(BmoOrderGroup.ACTION_CHANGEINDEX)) {
				PmConn pmConn = new PmConn(getSFParams());
				try {
					pmConn.open();
					pmConn.disableAutoCommit();

					printDevLog("Iniciando accion change index");

					// Revisa si debe subir de indice
					changeIndex(pmConn, bmoOrderGroup, value);

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

	// Agrega Kit al Pedido
	private void addKit(PmConn pmConn, BmoOrderGroup bmoOrderGroup, int productKitId, BmUpdateResult bmUpdateResult) throws SFException {
		PmProductKitItem pmProductKitItem = new PmProductKitItem(getSFParams());
		BmoProductKitItem bmoProductKitItem = new BmoProductKitItem();

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderGroup.getOrderId().toInteger());

		PmProductPrice pmProductPrice = new PmProductPrice(getSFParams());

		BmFilter kitItemsByKitFilter = new BmFilter();
		kitItemsByKitFilter.setValueFilter(bmoProductKitItem.getKind(), bmoProductKitItem.getProductKitId(), productKitId);
		Iterator<BmObject> productKitItemIterator = pmProductKitItem.list(pmConn, kitItemsByKitFilter).iterator();

		// Identificar la secuencia inicial
		PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());

		// Lista de items del kit
		int index = 1;
		while (productKitItemIterator.hasNext()) {
			bmoProductKitItem = (BmoProductKitItem)productKitItemIterator.next();

			BmoOrderItem bmoOrderItem = new BmoOrderItem();
			bmoOrderItem.getOrderGroupId().setValue(bmoOrderGroup.getId());
			bmoOrderItem.getName().setValue(bmoProductKitItem.getBmoProduct().getName().toString());
			bmoOrderItem.getQuantity().setValue(bmoProductKitItem.getQuantity().toDouble());
			bmoOrderItem.getDays().setValue(bmoProductKitItem.getDays().toDouble());
			bmoOrderItem.getProductId().setValue(bmoProductKitItem.getProductId().toInteger());
			bmoOrderItem.getIndex().setValue(index);
			// Tomar Partida presp. y Departamento del Producto, sino, del Pedido
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

				PmProductCompany pmProductCompany = new PmProductCompany(getSFParams());
				BmoProductCompany bmoProductCompany = new BmoProductCompany();
				// Si existe empresa del producto, verificar si se cargo la partida y el depto. si no traer del producto
				if (pmProductCompany.productInCompany(pmConn, bmoOrder.getCompanyId().toInteger(), bmoOrderItem.getProductId().toInteger())) {
					bmoProductCompany = (BmoProductCompany)pmProductCompany.get(pmConn, 
									(pmProductCompany.getProductCompanySpecific("" + bmoOrderItem.getProductId().toInteger(), 
											bmoOrder.getCompanyId().toInteger(), bmUpdateResult)).getId() );
					
					if (bmoProductCompany.getBudgetItemId().toInteger() > 0) {
						bmoOrderItem.getBudgetItemId().setValue(bmoProductCompany.getBudgetItemId().toInteger());
					} else {
						// Si el producto tiene partida asignarla, sino; la de la cotizacion
						if (bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger() > 0)
							bmoOrderItem.getBudgetItemId().setValue(bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger());
						else {
							if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
								bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "Seleccione una Partida Presp. en la Oportunidad.");
							else
								bmoOrderItem.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						}
					}
					
					if (bmoProductCompany.getAreaId().toInteger() > 0) {
						bmoOrderItem.getAreaId().setValue(bmoProductCompany.getAreaId().toInteger());
					} else {
						// Si el producto tiene Departamento asignarlo, sino; de la Cotizacion
						if (bmoProductKitItem.getBmoProduct().getAreaId().toInteger() > 0)
							bmoOrderItem.getAreaId().setValue(bmoProductKitItem.getBmoProduct().getAreaId().toInteger());
						else {
							//					if (!(bmoQuote.getAreaId().toInteger() > 0))
							//						bmUpdateResult.addError(bmoQuote.getAreaId().getName(), "Seleccione un Departamento en la Oportunidad");
							//					else
							bmoOrderItem.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
						}
					}
				} else {
				
					// Si el producto tiene partida asignarla, sino; la de la cotizacion
					if (bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger() > 0)
						bmoOrderItem.getBudgetItemId().setValue(bmoProductKitItem.getBmoProduct().getBudgetItemId().toInteger());
					else {
						if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
							bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "Seleccione una Partida Presp. en la Oportunidad.");
						else
							bmoOrderItem.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
					}
	
					// Si el producto tiene Departamento asignarlo, sino; de la Cotizacion
					if (bmoProductKitItem.getBmoProduct().getAreaId().toInteger() > 0)
						bmoOrderItem.getAreaId().setValue(bmoProductKitItem.getBmoProduct().getAreaId().toInteger());
					else {
						//					if (!(bmoQuote.getAreaId().toInteger() > 0))
						//						bmUpdateResult.addError(bmoQuote.getAreaId().getName(), "Seleccione un Departamento en la Oportunidad");
						//					else
						bmoOrderItem.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
					}
				}
			}
			// Obtiene precio vigente del producto
			bmoOrderItem.getPrice().setValue(pmProductPrice.getCurrentPrice(pmConn, bmoProductKitItem.getProductId().toInteger(),
					bmoOrder.getCurrencyId().toInteger(), bmoOrder.getOrderTypeId().toInteger(), bmoOrder.getWFlowTypeId().toInteger(), 
					bmoOrder.getMarketId().toInteger(), bmoOrder.getCompanyId().toInteger()));

			double amount = bmoProductKitItem.getQuantity().toDouble() * bmoOrderItem.getPrice().toDouble();
			bmoOrderItem.getAmount().setValue(amount);

			// Almacenar nuevo item de la cotizacion
			if (!bmUpdateResult.hasErrors())
				pmOrderItem.saveSimple(pmConn, bmoOrderItem, bmUpdateResult);
			index++;
		}
	}

	// Regresa el indice maximo mas 1
	private int nextIndex(PmConn pmConn, BmoOrderGroup bmoOrderGroup) throws SFException {
		int index = 1;
		String sql = " SELECT MAX(ordg_index) as maxindex FROM ordergroups " +
				" WHERE ordg_orderid = " + bmoOrderGroup.getOrderId(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			return pmConn.getInt("maxindex") + 1 ;
		else 
			return index;
	}

	public boolean orderGroupHasRaccAuthorized(PmConn pmConn, BmoOrderGroup bmoOrderGroup, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		sql = " SELECT * FROM raccounts " +
				" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
				" WHERE racc_ordergroupid = " + bmoOrderGroup.getId() +
				" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'"; 
		pmConn.doFetch(sql);
		if (pmConn.next()) return true;
		else return false;
	}

	public boolean orderGroupHasRacc(PmConn pmConn, BmoOrderGroup bmoOrderGroup, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		sql = " SELECT * FROM raccounts " +
				" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
				" WHERE racc_ordergroupid = " + bmoOrderGroup.getId() +
				" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";		       
		pmConn.doFetch(sql);
		if (pmConn.next()) return true;
		else return false;
	}

	public void calculateAmount(PmConn pmConn, BmoOrder bmoOrder, BmoOrderGroup bmoOrderGroup, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoOrderGroup = bmoOrderGroup;

		// Si no es KIT, recacular mediante items
		if (!bmoOrderGroup.getIsKit().toBoolean()) {
			String sql = "SELECT sum(ordi_amount) FROM orderitems "
					+ " WHERE ordi_ordergroupid = " + bmoOrderGroup.getId();
			pmConn.doFetch(sql);

			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			double amount = -1;
			if (pmConn.next()) amount = pmConn.getDouble(1);

			bmoOrderGroup.getAmount().setValue(amount);
			
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//				pmConn.doFetch("SELECT sum(ordi_discount) AS dicount,sum(qoit_feeproduction) AS feeSum,sum(qoit_comission) sumCom ,sum(qoit_price) sumPrice FROM quoteitems "
//						+ " WHERE qoit_quotegroupid = " + bmoOrderGroup.getId());
//				double discount = 0;
//				double price = 0;
//				if (pmConn.next()) {
//					discount = pmConn.getDouble("dicount");
//					price = pmConn.getDouble("sumPrice");
//				}
//				bmoOrderGroup.getDiscount().setValue(discount);
//				bmoOrderGroup.getPrice().setValue(price);
//			}
		}

		super.save(pmConn, bmoOrderGroup, bmUpdateResult);

		// Recalcular la cotizacion completa
		PmOrder pmOrder = new PmOrder(getSFParams());
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
	}

	// Revisa si el pedido tiene grupos de items
	public boolean orderHasGroups(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "SELECT count(ordg_ordergroupid) as c FROM ordergroups "
				+ " WHERE ordg_orderid = " + bmoOrder.getId();
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			if ((pmConn.getInt("c") > 0))
				return true;
			else 
				return false;
		} else {
			return false;
		}
	}

	@Override
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderGroup = (BmoOrderGroup)bmObject;

		// Obten la cotización
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderGroup.getOrderId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el pedido - está Autorizada.");
		} else {
			PmConn pmConn = new PmConn(getSFParams());
			try {
				pmConn.open();
				pmConn.disableAutoCommit();

				// Eliminar items del grupo de cotización
				PmOrderItem pmOrderItem = new PmOrderItem(getSFParams());
				pmOrderItem.deleteByOrderGroup(pmConn, bmoOrderGroup.getId());

				//Validar que no exista una CxC ligada
				if (orderGroupHasRaccAuthorized(pmConn, bmoOrderGroup, bmUpdateResult)) { 
					bmUpdateResult.addMsg("Existe una CxC autorizada ligada al grupo.");
				} else {					
					if (orderGroupHasRacc(pmConn, bmoOrderGroup, bmUpdateResult))  {
						//Eliminar la CxC ligada al grupo si no esta autorizada
						BmoRaccount bmoRaccount = new BmoRaccount();
						PmRaccount pmRaccount = new PmRaccount(getSFParams());
						bmoRaccount = (BmoRaccount)pmRaccount.getBy(pmConn, bmoOrderGroup.getId(), bmoRaccount.getOrderGroupId().getName());

						pmRaccount.delete(pmConn, bmoRaccount, bmUpdateResult);
					}
				}

				// Actualizar montos
				calculateAmount(pmConn, bmoOrder, bmoOrderGroup, bmUpdateResult);

				// Eliminar grupo de cotización
				bmUpdateResult = super.delete(pmConn, bmoOrderGroup, bmUpdateResult);

				// Recalcular la cotizacion completa
				pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
				
				// Aplicar si esta activo en el tipo de pedido
				if (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0) {
					// Se asegura que se generen en automatico todas las CxC segun monto del Pedido
					PmRaccount pmRaccount = new PmRaccount(getSFParams());
					pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
				}

				// Recalcula Entrega de Pedido, si ya se han creado envios de pedido
				PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
				if (pmOrderDelivery.getOrderHasTypeDelivery(pmConn, bmoOrder.getId()))
					pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) pmConn.commit();

			} catch (SFPmException e) {
				bmUpdateResult.addError(bmObject.getProgramCode(), "ERROR: " + e.toString());
			} finally {
				pmConn.close();
			}
		}

		return bmUpdateResult;
	}
//	//Desmarcar los check de los item para crear proyecto
//	public BmUpdateResult updateItemTrue(PmConn pmConn,BmoOrderGroup bmoOrderGroup,BmUpdateResult bmUpdateResult) throws SFException {
//		PmConn pmcoConnItems = new PmConn(getSFParams());
//		
//		
//		String sql = "SELECT ordi_orderitemid FROM orderitems WHERE ordi_ordergroupid = " + bmoOrderGroup.getId();		
//		
//		pmcoConnItems.open();
//		pmConn.doFetch(sql); 
//	
//		while(pmConn.next()) {
//			
//			String sqlItem = "UPDATE orderitems SET ordi_createproject = 1 WHERE ordi_orderitemid = " +pmConn.getInt("ordi_orderitemid");
//			printDevLog("updateItemTrue ()- " + sqlItem);
//			pmcoConnItems.doUpdate(sqlItem);		
//			
//		}
//		pmcoConnItems.close();
//		return bmUpdateResult;
//		
//	}
//	//Marcar los check de los item para crear proyecto
//	public BmUpdateResult updateItemFalse(PmConn pmConn,BmoOrderGroup bmoOrderGroup,BmUpdateResult bmUpdateResult) throws SFException {
//		PmConn pmcoConnItems = new PmConn(getSFParams());
//		
//		
//		String sql = "SELECT ordi_orderitemid FROM orderitems WHERE ordi_ordergroupid = " + bmoOrderGroup.getId();		
//		
//		pmcoConnItems.open();
//		pmConn.doFetch(sql); 
//	
//		while(pmConn.next()) {
//			
//			String sqlItem = "UPDATE orderitems SET ordi_createproject = 0 WHERE ordi_orderitemid = " +pmConn.getInt("ordi_orderitemid");
//			printDevLog("updateItemFalse ()- " + sqlItem);
//			pmcoConnItems.doUpdate(sqlItem);		
//			
//		}
//		pmcoConnItems.close();
//		return bmUpdateResult;
//		
//	}
}
