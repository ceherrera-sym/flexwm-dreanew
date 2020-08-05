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

import com.flexwm.server.FlexUtil;
import com.flexwm.server.cr.PmOrderCredit;
import com.flexwm.shared.cr.BmoOrderCredit;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderDeliveryItem;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmOrderDeliveryItem extends PmObject {
	BmoOrderDeliveryItem bmoOrderDeliveryItem;

	public PmOrderDeliveryItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		setBmObject(bmoOrderDeliveryItem);




setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderDeliveryItem.getOrderItemId(), bmoOrderDeliveryItem.getBmoOrderItem()),
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getOrderGroupId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoOrderGroup()),
				
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getProductId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct()),
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getProductFamilyId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoProductFamily()),
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getProductGroupId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoProductGroup()),
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getUnitId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoUnit()),
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getAreaId() , bmoOrderDeliveryItem.getBmoOrderItem().getBmoArea()),
//				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBudgetItemId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem()),
				
				new PmJoin(bmoOrderDeliveryItem.getProductId(), bmoOrderDeliveryItem.getBmoProduct()),
				new PmJoin(bmoOrderDeliveryItem.getBmoProduct().getProductFamilyId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoOrderDeliveryItem.getBmoProduct().getProductGroupId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoOrderDeliveryItem.getBmoProduct().getUnitId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoProduct().getBmoUnit()),
				
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getAreaId() , bmoOrderDeliveryItem.getBmoOrderItem().getBmoArea()),
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBudgetItemId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem()),
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getBudgetId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getBudgetItemTypeId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getCurrencyId(), bmoOrderDeliveryItem.getBmoOrderItem().getBmoBudgetItem().getBmoCurrency()),
				new PmJoin(bmoOrderDeliveryItem.getFromWhSectionId(), bmoOrderDeliveryItem.getBmoFromWhSection()),
				new PmJoin(bmoOrderDeliveryItem.getBmoFromWhSection().getWarehouseId(), bmoOrderDeliveryItem.getBmoFromWhSection().getBmoWarehouse()),
				new PmJoin(bmoOrderDeliveryItem.getBmoFromWhSection().getBmoWarehouse().getCompanyId(), bmoOrderDeliveryItem.getBmoFromWhSection().getBmoWarehouse().getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderDeliveryItem = (BmoOrderDeliveryItem)autoPopulate(pmConn, new BmoOrderDeliveryItem());

		// BmoOrderItem
		BmoOrderItem bmoOrderItem = new BmoOrderItem();
		int orderItemId = pmConn.getInt(bmoOrderItem.getIdFieldName());
		if (orderItemId > 0) bmoOrderDeliveryItem.setBmoOrderItem((BmoOrderItem) new PmOrderItem(getSFParams()).populate(pmConn));
		else bmoOrderDeliveryItem.setBmoOrderItem(bmoOrderItem);

		// BmoWhSection
		BmoWhSection bmoWhSection = new BmoWhSection();
		int whSectionId = pmConn.getInt(bmoWhSection.getIdFieldName());
		if (whSectionId > 0) bmoOrderDeliveryItem.setBmoFromWhSection((BmoWhSection) new PmWhSection(getSFParams()).populate(pmConn));
		else bmoOrderDeliveryItem.setBmoFromWhSection(bmoWhSection);
		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoOrderDeliveryItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoOrderDeliveryItem.setBmoProduct(bmoProduct);

		return bmoOrderDeliveryItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;
		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		boolean newRecord = true;
		if (bmoOrderDeliveryItem.getId() > 0)
			newRecord = false;

		// Obten el envio del pedido
		PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
		if (bmoOrderDeliveryItem.getOrderDeliveryId().toInteger() > 0)
			bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoOrderDeliveryItem.getOrderDeliveryId().toInteger());
		else bmUpdateResult.addMsg("<b>No se encontro el Código de Barras.</b>");

		// Si el envío ya está autorizada, no se puede hacer movimientos
		if (bmoOrderDelivery.getStatus().toChar() != BmoOrderDelivery.STATUS_REVISION) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Envío - ya está Autorizado." + bmoOrderDelivery.getId());
		} else {	
			// Se almacena la informacion base para proceder con modificaciones adicionales
			super.save(pmConn, bmObject, bmUpdateResult);

			// Revisar que no exceda la cantidad del item
			if (!checkQuantityItem(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult))
				bmUpdateResult.addMsg("La cantidad enviada excede al Pedido.");

			// Revisar que si se esta asignando cantidad mayor a 0
			if (bmoOrderDeliveryItem.getQuantity().toDouble() > 0) {

				if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {
						// Revisar que este asignada seccion origen
						if (!(bmoOrderDeliveryItem.getFromWhSectionId().toInteger() > 0))
							bmUpdateResult.addMsg("Debe asignar primero la Sección de Almacén Origen.");
					}
					// Revisar que este asignada seccion destino si es de tipo renta
					if (!(bmoOrderDeliveryItem.getToWhSectionId().toInteger() > 0))
						bmUpdateResult.addMsg("Debe asignar primero la Sección de Almacén Destino.");
				}
				else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
					// Revisar que este asignada seccion destino si es de tipo venta y es devolucion
					if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
						if (!(bmoOrderDeliveryItem.getToWhSectionId().toInteger() > 0))
							bmUpdateResult.addMsg("Debe asignar primero la Sección de Almacén Destino.");
					}
				} else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
					// Revisar que este asignada seccion destino si es de tipo venta y es devolucion
					if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
						if (!(bmoOrderDeliveryItem.getToWhSectionId().toInteger() > 0))
							bmUpdateResult.addMsg("Debe asignar primero la Sección de Almacén Destino.");
					}
				}
			}

			// Actualiza estatus de saldo a todos los items relacionados
			updateQuantityBalance(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Actualiza estatus de devoluciones a todos los items relacionados
			updateQuantityReturned(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Calcula el valor del item
			double amount = bmoOrderDeliveryItem.getPrice().toDouble() * bmoOrderDeliveryItem.getQuantity().toDouble();
			//Si los dias son mayores a 0 multiplicar por los dias
			if (bmoOrderDeliveryItem.getDays().toDouble() > 0)
				amount = amount * bmoOrderDeliveryItem.getDays().toDouble();
			bmoOrderDeliveryItem.getAmount().setValue(amount);

			// Antes de las siguientes afectaciones almacena cambios
			bmUpdateResult = super.save(pmConn, bmObject, bmUpdateResult);			

			// Actualiza cantidad total enviada del item en el pedido
			updateOrderQuantityDelivered(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Recalcula el subtotal
			pmOrderDelivery.updateBalance(pmConn, bmoOrderDelivery, bmUpdateResult);

			// Asigna almacenes origen y/o destino en caso de ser necesario
			if (bmoOrderDeliveryItem.getQuantity().toDouble() > 0) {

				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
					// Si es de tipo RENTA, obtiene el almacen origen
					if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						// Establecer seccion origen
						bmoOrderDeliveryItem.getFromWhSectionId().setValue(getOrderWhSection(pmConn, bmoOrderDelivery, bmUpdateResult));

						// Forzar el destino de donde salio
						// Realmente no es necesario, ya que lo trae cuando hace la busqueda del Codigo de Barras
						// int toWhSection = getFromWhSectionDelivery(pmConn, bmoOrderDeliveryItem.getSerial().toString(), bmoOrderDelivery.getOrderId().toInteger(), bmoOrderDeliveryItem.getProductId().toInteger());
						// bmoOrderDeliveryItem.getToWhSectionId().setValue(toWhSection);
					} else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						// Si es de tipo VENTA, no hay almacen origen

						// Asigna el destino que aplique
						bmoOrderDeliveryItem.getToWhSectionId().setValue(getOrderDeliveryItemFromWhSection(pmConn, bmoOrderDeliveryItem.getOrderItemId().toInteger(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult));

						// Validar que el # Serie / Lote ingresado corresponda a un # de Serie que haya tenido salida en este Pedido
						// Se revisa que del pedido de esta devolución, haya algun orderdeliveryitem que tenga el # de serie de este item
						// Si hay varios, suma los de salida menos los de entrada, si es positivo el resultado significa que aun hay afuera y
						// entonces si deja continuar

						if (!checkSerialReturn(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult)) {
							bmUpdateResult.addMsg("El #Serial/Lote no fue encontrado en el Envío de Pedido.");
						}
					} else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
						// Asigna el destino que aplique
						bmoOrderDeliveryItem.getToWhSectionId().setValue(getOrderDeliveryItemFromWhSection(pmConn, bmoOrderDeliveryItem.getOrderItemId().toInteger(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult));

						if (!checkSerialReturn(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult)) {
							bmUpdateResult.addMsg("El #Serial/Lote no fue encontrado en el Envío de Pedido.");
						}
					}
				}

			}

			// Afectaciones a almacenes, si no es nuevo registro
			if (!newRecord)	 {
				if (!bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {					
					updateWhMovItem(pmConn, bmoOrderDeliveryItem, bmUpdateResult);					
				}	
			}
			// Aplica solo para items nuevo y que sea complementarios
			if(newRecord && bmoOrderDeliveryItem.getBmoProduct().getType().equals(BmoProduct.TYPE_COMPLEMENTARY)) {
				if (!bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {	
					updateWhMovItem(pmConn, bmoOrderDeliveryItem, bmUpdateResult);
				}
			}
			
			// Si la cantidad es menor a uno borra serial
			if (!(bmoOrderDeliveryItem.getQuantity().toDouble() > 0)) {
				bmoOrderDeliveryItem.getSerial().setValue("");
				//				bmoOrderDeliveryItem.getFromWhSectionId().setValue("");
				//				bmoOrderDeliveryItem.getToWhSectionId().setValue("");

				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY))
					bmoOrderDeliveryItem.getFromWhSectionId().setValue("");
				else {
					// Es de tipo devolucion, reasigna valores default

					// Si es de tipo RENTA
					if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						// Establecer seccion origen
						//bmoOrderDeliveryItem.getFromWhSectionId().setValue(getOrderWhSection(pmConn, bmoOrderDelivery, bmUpdateResult));
						//bmoOrderDeliveryItem.getFromWhSectionId().setValue("");

						// Asigna el destino
						bmoOrderDeliveryItem.getToWhSectionId().setValue(getOrderDeliveryItemFromWhSection(pmConn, bmoOrderDeliveryItem.getOrderItemId().toInteger(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult));
					} else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						// Si es de tipo VENTA, no hay almacen origen
						// Asigna el destino
						bmoOrderDeliveryItem.getToWhSectionId().setValue(getOrderDeliveryItemFromWhSection(pmConn, bmoOrderDeliveryItem.getOrderItemId().toInteger(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult));
					} else if (bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
						// Si es de tipo VENTA, no hay almacen origen
						// Asigna el destino
						bmoOrderDeliveryItem.getToWhSectionId().setValue(getOrderDeliveryItemFromWhSection(pmConn, bmoOrderDeliveryItem.getOrderItemId().toInteger(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult));
					}
				}
			}

			// Realiza la ultima modificacion
			bmUpdateResult = super.save(pmConn, bmoOrderDeliveryItem, bmUpdateResult);

		}

		return bmUpdateResult;
	}

	private boolean checkSerialReturn(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException {                    
		boolean result = false, quantityOutExist = false;
		String sql = "";
		double quantityOut = 0, quantityOutWhtr = 0;

		// Buscar en envios de salida(envio) de este pedido los items con el serial introducido
		sql = " SELECT * FROM orderdeliveryitems " + 
				" LEFT JOIN orderdeliveries ON (odly_orderdeliveryid = odyi_orderdeliveryid) " + 
				" LEFT JOIN orders ON (orde_orderid = odly_orderid) " + 
				" WHERE odyi_serial LIKE '" + bmoOrderDeliveryItem.getSerial().toString() + "'" + 
				" AND odly_type = '" +BmoOrderDelivery.TYPE_DELIVERY + "'" + 
				" AND odyi_quantity > 0 " + 
				" AND odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
		//" AND odly_status = '" + BmoOrderDelivery.STATUS_AUTHORIZED + "'";

		pmConn.doFetch(sql);
		if (pmConn.next()) quantityOutExist = true;

		//System.out.println("A: "+sql);
		//System.out.println("quantityOutExist: "+quantityOutExist);

		// Si existe alguna salida, revisa cantidades
		if (quantityOutExist) {
			// Sumar las salidas de este serial de los Envios
			sql = " SELECT SUM(odyi_quantity) AS quantityOut FROM orderdeliveryitems " + 
					" LEFT JOIN orderdeliveries ON (odly_orderdeliveryid = odyi_orderdeliveryid) " + 
					" LEFT JOIN orders ON (orde_orderid = odly_orderid) " + 
					" WHERE odyi_serial LIKE '" + bmoOrderDeliveryItem.getSerial().toString() + "'" + 
					" AND odly_type = '" +BmoOrderDelivery.TYPE_DELIVERY + "'" + 
					" AND odyi_quantity > 0 " + 
					" AND odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			//" AND odly_status = '" + BmoOrderDelivery.STATUS_AUTHORIZED + "'";

			pmConn.doFetch(sql);
			if (pmConn.next()) quantityOut = pmConn.getDouble("quantityOut");

			//System.out.println("B: "+sql);
			//System.out.println("quantityOut: "+quantityOut);

			// Sumar cantidad de salida en Raestros
			sql = " SELECT SUM(whtr_inquantity) AS inQuantity, SUM(whtr_outquantity) AS quantityOutWhtr " +
					" FROM whtracks " +
					" WHERE whtr_serial LIKE '" + bmoOrderDeliveryItem.getSerial().toString() + "'" + 
					" AND whtr_outquantity > 0 ";

			pmConn.doFetch(sql);
			if (pmConn.next()) quantityOutWhtr = pmConn.getDouble("quantityOutWhtr");

			//System.out.println("C : "+sql);
			//System.out.println("quantityOutWhtr: "+quantityOutWhtr);

			BmoProduct bmoProduct = new BmoProduct();
			PmProduct pmProduct = new PmProduct(getSFParams());
			bmoProduct = (BmoProduct)pmProduct.get(bmoOrderDeliveryItem.getProductId().toInteger());

			// Si el Producto es serial, las salida y la salida de rastreo es igual
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL)) {
				//System.out.println("SERIAL");
				if(quantityOut == quantityOutWhtr) result = true;
			} else if (bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
				//System.out.println("BATCH");
				// Revisa que no se pase de la cantidad a ingresar
				if (bmoOrderDeliveryItem.getQuantity().toDouble() > quantityOut)
					bmUpdateResult.addMsg("La cantidad de entrada es mayor a la salida.");
				else if (bmoOrderDeliveryItem.getQuantity().toDouble() == 0)
					bmUpdateResult.addMsg("La cantidad a devolver no debe ser cero.");
				else result = true;
				//if(quantityOut >= quantityOutWhtr) result = true;

			} else if (bmoProduct.getTrack().equals(BmoProduct.TRACK_NONE)) {
				//System.out.println("TRACK: NONE");
			}
		}
		return result;
	}

	public void createItemsFromOrder(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmUpdateResult bmUpdateResult) throws SFException {

		//Obtener el tipo de pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderDelivery.getOrderId().toInteger());

		// Crear los items
		BmoOrderItem bmoOrderItem = new BmoOrderItem();
		BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();
		BmFilter filterOrderItem = new BmFilter();
		Iterator<BmObject> listOrderItem;

		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			filterOrderItem.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getBmoOrderGroup().getOrderId(), bmoOrderDelivery.getOrderId().toInteger());
			listOrderItem = new PmOrderItem(getSFParams()).list(filterOrderItem).iterator();
		} else {
			filterOrderItem.setValueFilter(bmoOrderCredit.getKind(), bmoOrderCredit.getOrderId(), bmoOrderDelivery.getOrderId().toInteger());
			listOrderItem = new PmOrderCredit(getSFParams()).list(filterOrderItem).iterator();
		}

		// Si es de tipo devolucion, obtener seccion origen
		int fromWhSectionId = 0, toWhSectionId = 0;
		if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {			
			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				// Establece seccion destino
				toWhSectionId = bmoOrderDelivery.getToWhSectionId().toInteger();
			}	
		} else {			
			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					// Establece seccion origen
					PmWhSection pmWhSection = new PmWhSection(getSFParams());
					BmoWhSection bmoWhSection = new BmoWhSection();
					bmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn, bmoOrderDelivery.getOrderId().toInteger(), bmoWhSection.getOrderId().getName());
					fromWhSectionId = bmoWhSection.getId();
				}
			}

			// Establece seccion destino
			//toWhSectionId = getOrderDeliveryFromWhSection(pmConn, bmoOrderDelivery, bmUpdateResult);
		}


		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

			while (listOrderItem.hasNext()) {
				bmoOrderItem = (BmoOrderItem)listOrderItem.next();

				// Solo crea los items si afectan inventario
				if (bmoOrderItem.getBmoProduct().getInventory().toBoolean()) {

					// Asigna seccion destino de cada item desde el envio, solo si es de tipo devolución
					if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) 
						toWhSectionId = getOrderDeliveryItemFromWhSection(pmConn, bmoOrderItem.getId(), bmoOrderDelivery.getOrderId().toInteger(), bmUpdateResult);

					// Revisar que tipo de product
					if (bmoOrderItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
						// (FORZAR de decimal a entero SOLO PARA #SERIAL)
						for (int i = 0; i < (int)bmoOrderItem.getQuantity().toDouble(); i++) {
							//Crear un nuevo item en el envío
							BmoOrderDeliveryItem bmoOrderDeliveryItemNew = new BmoOrderDeliveryItem();

							bmoOrderDeliveryItemNew.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());
							bmoOrderDeliveryItemNew.getOrderItemId().setValue(bmoOrderItem.getId());
							bmoOrderDeliveryItemNew.getProductId().setValue(bmoOrderItem.getProductId().toInteger());


							bmoOrderDeliveryItemNew.getQuantity().setValue(0);


							bmoOrderDeliveryItemNew.getDays().setValue(bmoOrderItem.getDays().toDouble());
							bmoOrderDeliveryItemNew.getName().setValue(bmoOrderItem.getName().toString());				
							bmoOrderDeliveryItemNew.getPrice().setValue(bmoOrderItem.getPrice().toDouble());
							bmoOrderDeliveryItemNew.getAmount().setValue(0);

							// Actualiza estatus de saldo a todos los items relacionados
							updateQuantityBalance(pmConn, bmoOrderDelivery, bmoOrderDeliveryItemNew, bmUpdateResult);

							// Actualiza estatus de devoluciones a todos los items relacionados
							updateQuantityReturned(pmConn, bmoOrderDelivery, bmoOrderDeliveryItemNew, bmUpdateResult);

							// Establece seccion destino
							bmoOrderDeliveryItemNew.getToWhSectionId().setValue(toWhSectionId);

							// Asigna seccion origen si es de devolución
							if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
								bmoOrderDeliveryItemNew.getFromWhSectionId().setValue(fromWhSectionId);

								// Establece seccion destino
								bmoOrderDeliveryItemNew.getToWhSectionId().setValue(toWhSectionId);
							}

							super.save(pmConn, bmoOrderDeliveryItemNew, bmUpdateResult);


						}	
					} else {
						// Crear un nuevo item en el envío
						BmoOrderDeliveryItem bmoOrderDeliveryItemNew = new BmoOrderDeliveryItem();

						bmoOrderDeliveryItemNew.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());
						bmoOrderDeliveryItemNew.getOrderItemId().setValue(bmoOrderItem.getId());
						bmoOrderDeliveryItemNew.getProductId().setValue(bmoOrderItem.getProductId().toInteger());

						//Recibir en automático
						if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {
							if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY))
								bmoOrderDeliveryItemNew.getQuantity().setValue(bmoOrderItem.getQuantity().toDouble());
							else
								bmoOrderDeliveryItemNew.getQuantity().setValue(0);
						} else 
							bmoOrderDeliveryItemNew.getQuantity().setValue(0);

						bmoOrderDeliveryItemNew.getDays().setValue(bmoOrderItem.getDays().toDouble());
						bmoOrderDeliveryItemNew.getName().setValue(bmoOrderItem.getName().toString());				
						bmoOrderDeliveryItemNew.getPrice().setValue(bmoOrderItem.getPrice().toDouble());
						bmoOrderDeliveryItemNew.getAmount().setValue(0);

						// Actualiza estatus de saldo a todos los items relacionados
						updateQuantityBalance(pmConn, bmoOrderDelivery, bmoOrderDeliveryItemNew, bmUpdateResult);

						// Actualiza estatus de devoluciones a todos los items relacionados
						updateQuantityReturned(pmConn, bmoOrderDelivery, bmoOrderDeliveryItemNew, bmUpdateResult);

						// Establece seccion destino
						bmoOrderDeliveryItemNew.getToWhSectionId().setValue(toWhSectionId);

						// Asigna seccion origen si es de devolución
						if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
							bmoOrderDeliveryItemNew.getFromWhSectionId().setValue(fromWhSectionId);

							// Establece seccion destino
							bmoOrderDeliveryItemNew.getToWhSectionId().setValue(toWhSectionId);
						}

						super.save(pmConn, bmoOrderDeliveryItemNew, bmUpdateResult);
					}
				}
			}
		} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

			while (listOrderItem.hasNext()) {

				bmoOrderCredit = (BmoOrderCredit)listOrderItem.next();

				BmoOrderDeliveryItem bmoOrderDeliveryItemNew = new BmoOrderDeliveryItem();

				bmoOrderDeliveryItemNew.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());
				bmoOrderDeliveryItemNew.getOrderCreditId().setValue(bmoOrderCredit.getId());				
				bmoOrderDeliveryItemNew.getName().setValue(bmoOrderCredit.getName().toString());
				bmoOrderDeliveryItemNew.getPrice().setValue(bmoOrderCredit.getPrice().toDouble());
				bmoOrderDeliveryItemNew.getAmount().setValue(bmoOrderCredit.getPrice().toDouble());
				bmoOrderDeliveryItemNew.getQuantity().setValue(bmoOrderCredit.getQuantity().toInteger());

				super.save(pmConn, bmoOrderDeliveryItemNew, bmUpdateResult);

			}	
		}
	}


	//Obtener el número de serie
	public void setSerialTrackByProduct(PmConn pmConn, BmoOrderDeliveryItem bmoOrderDeliveryItem, int companyId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		String serialTrack = "";
		int whSectionId = 0;
		//		int quantity = 0;
		String track = "";
		//Obtener el serial del primer producto que este en el almacen
		sql = " SELECT whtr_serial, whst_whsectionid, whst_quantity, prod_track FROM whstocks " +			  
				" LEFT JOIN whsections ON (whst_whsectionid = whse_whsectionid) " +
				" LEFT JOIN products ON (whst_productid = prod_productid) " +
				" LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid)" +
				" LEFT JOIN whtracks ON (whst_whtrackid = whtr_whtrackid) " +
				" WHERE ware_companyid = " + companyId +			  			  
				" AND whtr_productid = " + bmoOrderDeliveryItem.getProductId().toInteger() + 
				" AND whst_quantity > 0 ";
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			serialTrack = pmConn.getString("whtracks", "whtr_serial");
			whSectionId = pmConn.getInt("whst_whsectionid");
			//			quantity = pmConn.getDouble("whst_quantity");
			track = pmConn.getString("products", "prod_track");
		} 

		if (track.equals("" + BmoProduct.TRACK_SERIAL)) {		
			bmoOrderDeliveryItem.getQuantity().setValue(1);
		} else {		
			bmoOrderDeliveryItem.getQuantity().setValue(bmoOrderDeliveryItem.getBmoOrderItem().getQuantity().toDouble());
		}	

		bmoOrderDeliveryItem.getSerial().setValue(serialTrack);
		bmoOrderDeliveryItem.getFromWhSectionId().setValue(whSectionId);

		super.save(pmConn, bmoOrderDeliveryItem, bmUpdateResult);

		this.save(pmConn, bmoOrderDeliveryItem, bmUpdateResult);

	}

	private void updateWhMovItem(PmConn pmConn, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException {
		PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());
		BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();

		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		BmoWhMovement bmoWhMovement = new BmoWhMovement();
		bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoOrderDeliveryItem.getOrderDeliveryId().toInteger(), bmoWhMovement.getOrderDeliveryId().getName());

		// Si proviene de producto, continuar
		if (bmoOrderDeliveryItem.getProductId().toInteger() > 0) {
			PmProduct pmProduct = new PmProduct(getSFParams());
			BmoProduct bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoOrderDeliveryItem.getProductId().toInteger());

			// Si afecta inventario, continuar
			if (bmoProduct.getInventory().toBoolean()) {				
				// Si no existe el item de movimiento de almacen, crearlo
				if (!pmWhMovItem.orderDeliveryItemWhMovItemExists(pmConn, bmoOrderDeliveryItem.getId())) {					
					bmoWhMovItem.getWhMovementId().setValue(bmoWhMovement.getId());
					bmoWhMovItem.getOrderDeliveryItemId().setValue(bmoOrderDeliveryItem.getId());
					bmoWhMovItem.getProductId().setValue(bmoOrderDeliveryItem.getProductId().toInteger());
					bmoWhMovItem.getQuantity().setValue(bmoOrderDeliveryItem.getQuantity().toDouble());
					bmoWhMovItem.getAmount().setValue(bmoOrderDeliveryItem.getAmount().toDouble());
					bmoWhMovItem.getSerial().setValue(bmoOrderDeliveryItem.getSerial().toString());
					bmoWhMovItem.getFromWhSectionId().setValue(bmoOrderDeliveryItem.getFromWhSectionId().toInteger());
					bmoWhMovItem.getToWhSectionId().setValue(bmoOrderDeliveryItem.getToWhSectionId().toInteger());
				} else {					
					// Si existen, actualizalo					
					bmoWhMovItem = (BmoWhMovItem)pmWhMovItem.getBy(pmConn, bmoOrderDeliveryItem.getId(), bmoWhMovItem.getOrderDeliveryItemId().getName());
					bmoWhMovItem.getQuantity().setValue(bmoOrderDeliveryItem.getQuantity().toDouble());
					bmoWhMovItem.getSerial().setValue(bmoOrderDeliveryItem.getSerial().toString());
					bmoWhMovItem.getAmount().setValue(bmoOrderDeliveryItem.getAmount().toDouble());
					bmoWhMovItem.getFromWhSectionId().setValue(bmoOrderDeliveryItem.getFromWhSectionId().toInteger());
					bmoWhMovItem.getToWhSectionId().setValue(bmoOrderDeliveryItem.getToWhSectionId().toInteger());
				}

				// Si la cantidad es menor a 1 borrarlo
				if (!(bmoOrderDeliveryItem.getQuantity().toDouble() > 0)) {
					if (bmoWhMovItem.getId() > 0) {
						printDevLog("movItemID: "+ bmoWhMovItem.getId());
						pmWhMovItem.delete(pmConn, bmoWhMovItem, bmUpdateResult);
					} else {
						printDevLog("No existe un ID a eliminar.");
						bmUpdateResult.addError(bmoOrderDeliveryItem.getQuantity().getName(), "La Cantidad no puede estar vacía.");
					}
				} else {					
					pmWhMovItem.save(pmConn, bmoWhMovItem, bmUpdateResult);					
				}	
			}
		}
	}

	// Obtner la cantidad de producto enviado
	public boolean checkQuantityItem(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException {                    
		String sql= "";
		boolean result = true; 
		double quantityOrderItem = 0;
		double quantityItemDelivered = 0;

		// Obtiene cantidad de un producto
		sql = " SELECT SUM(ordi_quantity) AS quantity FROM orderitems " +
				" LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) " + 
				" WHERE ordg_orderid = " + bmoOrderDelivery.getOrderId().toInteger() +
				" AND ordi_productid = " + bmoOrderDeliveryItem.getProductId().toInteger();
		//" AND ordi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();
		pmConn.doFetch(sql);
		//		System.out.println("q:: " +sql);
		if (pmConn.next()) quantityOrderItem = pmConn.getDouble("quantity");

		// Obtiene suma de enviados
		sql = " SELECT SUM(odyi_quantity) AS delivered FROM orderdeliveryitems " +
				" LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " +
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() +
				" AND odyi_productid = " + bmoOrderDeliveryItem.getProductId().toInteger() +
				//" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger() + 
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'";
		pmConn.doFetch(sql);
		//		System.out.println("d:: " +sql);
		if (pmConn.next()) quantityItemDelivered = pmConn.getDouble("delivered");

		//		System.out.println("quantityOrderItem: " +quantityOrderItem);
		//		System.out.println("quantityItemDelivered: " +quantityItemDelivered);

		if (quantityItemDelivered > quantityOrderItem) result = false;
		if (bmoOrderDeliveryItem.getBmoProduct().getType().equals(BmoProduct.TYPE_COMPLEMENTARY))
			return true;
		else
			return result;
	}

	// Obtiene la seccion de almacen de donde se envió un item
	private int getOrderWhSection(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmUpdateResult bmUpdateResult) throws SFException {                    
		String sql= "";
		int orderWhSectionId = 0;
		PmWhSection pmWhSection = new PmWhSection(getSFParams());

		// Obtiene cantidad de un producto
		sql = " SELECT whse_whsectionid FROM whsections " +
				" LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid) " + 
				" WHERE whse_orderid = " + bmoOrderDelivery.getOrderId().toInteger();

		sql += ((pmWhSection.getDisclosureFilters().length() > 0) ? " AND " + pmWhSection.getDisclosureFilters() : "");

		pmConn.doFetch(sql);
		if (pmConn.next()) orderWhSectionId = pmConn.getInt("whse_whsectionid");

		return orderWhSectionId;
	}

	// Obtiene la seccion de almacen de donde se envió un item
	private int getOrderDeliveryItemFromWhSection(PmConn pmConn, int orderItemId, int orderId, BmUpdateResult bmUpdateResult) throws SFException {                    
		String sql= "";
		int deliveryFromWhSectionId = 0;

		// Obtiene cantidad de un producto
		sql = " SELECT odyi_fromwhsectionid FROM orderdeliveryitems " +
				" LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " +
				" WHERE odly_orderid = " + orderId +
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'" + 
				" AND odyi_orderitemid = " + orderItemId +
				" ORDER BY odly_orderdeliveryid DESC";
		pmConn.doFetch(sql);
		if (pmConn.next()) deliveryFromWhSectionId = pmConn.getInt("odyi_fromwhsectionid");

		printDevLog("sql_getOrderDeliveryItemFromWhSection: "+sql);
		return deliveryFromWhSectionId;
	}

	// Actualizar cantidad enviada
	private void updateOrderQuantityDelivered(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";
		double quantityDelivered = 0;

		sql = " SELECT SUM(odyi_quantity) AS quantity FROM orderdeliveryitems " +
				" LEFT JOIN orderdeliveries on (odyi_orderdeliveryid = odly_orderdeliveryid)" +
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger() +
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'";

		pmConn.doFetch(sql);
		if (pmConn.next()) quantityDelivered = pmConn.getDouble("quantity");	

		sql = " UPDATE orderitems SET ordi_quantitydelivered = " + quantityDelivered + 
				" WHERE ordi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();

		pmConn.doUpdate(sql);
	}

	// Actualizar pendiente de enviar
	private void updateQuantityBalance(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";

		double quantityDelivered = 0;
		double quantityTotal = 0;
		double balance = 0;

		// Cantidad del pedido
		sql = " SELECT ordi_quantity FROM orderitems " +
				" LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) " + 
				" WHERE ordg_orderid = " + bmoOrderDelivery.getOrderId().toInteger() +
				" AND ordi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityTotal = pmConn.getDouble("ordi_quantity");

		// Cantidad entregada
		sql = " SELECT SUM(odyi_quantity) AS quantity FROM orderdeliveryitems " +
				" LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid)" +
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger() + 
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityDelivered = pmConn.getDouble("quantity");

		balance = quantityTotal - quantityDelivered;

		sql = " UPDATE orderdeliveryitems " + 
				" LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid)" +
				" SET odyi_quantitybalance = " + balance + 
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();
		pmConn.doUpdate(sql);

		sql = " UPDATE orderitems SET ordi_quantitybalance = " + balance + 
				" WHERE ordi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();

		pmConn.doUpdate(sql);

		// Actualiza el valor del registro actual
		bmoOrderDeliveryItem.getQuantityBalance().setValue(FlexUtil.roundDouble(balance, 4));
	}

	// Actualiza la cantidad de devuelta en todos los items
	private void updateQuantityReturned(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmoOrderDeliveryItem bmoOrderDeliveryItem, BmUpdateResult bmUpdateResult) throws SFException{                    
		String sql="";
		double quantityReturned = 0;

		sql = " SELECT SUM(odyi_quantity) AS quantity FROM orderdeliveryitems " +
				" left join orderdeliveries on (odyi_orderdeliveryid = odly_orderdeliveryid)" +
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() +
				" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger() + 
				" AND odly_type = '" + BmoOrderDelivery.TYPE_RETURN + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) quantityReturned = pmConn.getDouble("quantity");

		sql = " UPDATE orderdeliveryitems " + 
				" LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid)" +
				" SET odyi_quantityreturned = " + quantityReturned + 
				" WHERE odly_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();
		pmConn.doUpdate(sql);

		sql = " UPDATE orderitems " +
				" LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid)" +
				" SET ordi_quantityreturned = " + quantityReturned + 
				" WHERE ordg_orderid = " + bmoOrderDelivery.getOrderId().toInteger() + 
				" AND ordi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger();

		pmConn.doUpdate(sql);

		// Actualiza el valor del registro actual
		bmoOrderDeliveryItem.getQuantityReturned().setValue(FlexUtil.roundDouble(quantityReturned));
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;
		int orderDeliveryId = bmoOrderDeliveryItem.getOrderDeliveryId().toInteger();

		PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
		BmoOrderDelivery bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoOrderDeliveryItem.getOrderDeliveryId().toInteger());
	

		// Accion de encontrar el codigo de barras
		if (action.equals(BmoOrderDeliveryItem.ACTION_SEARCHBARCODE)) {
			String barcode = value;
			PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
			BmoWhTrack bmoWhTrack = pmWhTrack.searchSerialByBarcode(pmConn, barcode);			
			
			
			// Busca primero dentro de los requisition receipt items, la clave de producto
			bmoOrderDeliveryItem = searchProductByCode(pmConn, orderDeliveryId, barcode);
			
			// Si no fue encontrado, buscar como numero de serie
			if (!(bmoOrderDeliveryItem.getId() > 0)) {

				// Busca el número de serie
//				PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
//				BmoWhTrack bmoWhTrack = pmWhTrack.searchSerialByBarcode(pmConn, barcode);

				if (bmoWhTrack.getId() > 0) {
					// Busca nuevamente dentro de los order delivery items, la clave de producto
					bmoOrderDeliveryItem = searchProductByBarCode(pmConn, orderDeliveryId, bmoWhTrack.getBmoProduct().getCode().toString(), barcode);

					// Busca ubicacion en secciones de almacen si es de tipo envio, y no esta asignada la cantidad
					if (bmoOrderDeliveryItem.getId() > 0 
							&& bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)
							&& bmoOrderDeliveryItem.getQuantity().toDouble() < 1) {

						int fromWhSectionId = getWhStockSectionBySerialDelivery(pmConn, barcode);

						if (fromWhSectionId > 0) 
							bmoOrderDeliveryItem.getFromWhSectionId().setValue(fromWhSectionId);
						else 
							bmUpdateResult.addMsg("El Item no fue encontrado en un Almacén Válido.");
					}
					// Devolucion; Busca nuevamente dentro de los order delivery items de Tipo Envío,
					// para regresar el origen, es decir, traer el origen(donde salio) y colocarlo en el destino correcto
					else if (bmoOrderDeliveryItem.getId() > 0
							&& bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {

						int fromWhSectionId = getFromWhSectionDelivery(pmConn, barcode, bmoOrderDelivery.getOrderId().toInteger(), bmoWhTrack.getProductId().toInteger());
						//System.out.println("fromWhSectionId:: "+fromWhSectionId);
						bmoOrderDeliveryItem.getToWhSectionId().setValue(fromWhSectionId);
					}
				} 
				// hay mas de 1 linea con el mismo producto
				else {
					
				}
			}
			// Si el rastreo vine de un Producto Complementario
			if((bmoWhTrack.getBmoProduct().getType().equals(BmoProduct.TYPE_COMPLEMENTARY) ||
					bmoWhTrack.getBmoProduct().getType().equals(BmoProduct.TYPE_COMPLEMENTARY_QUOTABLE) ||
					bmoWhTrack.getBmoProduct().getConsumable().toBoolean()) 
					&& (!(bmoOrderDeliveryItem.getId() > 0))) {
				
				int fromWhSectionId = 0;
				// si es devolucion buscar en los items de envio el almacen origen
				if (!(bmoOrderDeliveryItem.getId() > 0)
						&& bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
					fromWhSectionId = getFromWhSectionDelivery(pmConn, barcode, bmoOrderDelivery.getOrderId().toInteger(), bmoWhTrack.getProductId().toInteger());
				}else {
					//Obtener almacen origen si es envio
				    fromWhSectionId = getWhStockSectionBySerialDelivery(pmConn, barcode);				    
				}
				
					//Buscar si ya exixte el complementario en el envio
//					bmoOrderDeliveryItem = searchComplementaryItem(pmConn,bmoOrderDelivery.getId(),barcode);
					//si no existe el comlementario dentro de el envio
//					if (!(bmoOrderDeliveryItem.getId() > 0)) {
//						Si no existe en el envio crea el item de envio		
												
							
							bmoOrderDeliveryItem.getName().setValue(bmoWhTrack.getBmoProduct().getName().toString());
							bmoOrderDeliveryItem.getProductId().setValue(bmoWhTrack.getBmoProduct().getId());
							bmoOrderDeliveryItem.getPrice().setValue(0);
							bmoOrderDeliveryItem.getOrderDeliveryId().setValue(orderDeliveryId);
							bmoOrderDeliveryItem.getQuantity().setValue(1);
							bmoOrderDeliveryItem.getQuantityBalance().setValue(0);
							bmoOrderDeliveryItem.getQuantityReturned().setValue(0);
							bmoOrderDeliveryItem.getSerial().setValue(bmoWhTrack.getSerial().toString());									
							bmoOrderDeliveryItem.getBmoProduct().getType().setValue(BmoProduct.TYPE_COMPLEMENTARY);		
							bmoOrderDeliveryItem.getBmoProduct().getName().setValue(bmoWhTrack.getBmoProduct().getName().toString());
							bmoOrderDeliveryItem.getBmoProduct().getTrack().setValue(bmoWhTrack.getBmoProduct().getTrack().toChar());
							//traer almacen destino
							int toWhSectionId = getToWhSection(pmConn, bmoOrderDelivery.getOrderId().toInteger());
							//si es de tipo devolucion invierte los almacenes
							if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
								if(fromWhSectionId > 0) {
									bmoOrderDeliveryItem.getToWhSectionId().setValue(fromWhSectionId);
								}else {
									bmUpdateResult.addMsg("El Item no fue encontrado en un Almacén Válido.");
								}
								bmoOrderDeliveryItem.getFromWhSectionId().setValue(toWhSectionId);
							}else {
								//si es envio deja los almacenes como son
								bmoOrderDeliveryItem.getToWhSectionId().setValue(toWhSectionId);
								if(fromWhSectionId > 0) {
									bmoOrderDeliveryItem.getFromWhSectionId().setValue(fromWhSectionId);
								}else {
									bmUpdateResult.addMsg("El Item no fue encontrado en un Almacén Válido.");
								}
							}
							bmUpdateResult.setBmObject(bmoOrderDeliveryItem);
//						
					// si ya existe el item en el envio	
//					}else if(bmoOrderDeliveryItem.getId() > 0) {
//						//si es de rastreo lote y ya existe en el envio regresa el item
//						if(bmoWhTrack.getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)){
//							bmoOrderDeliveryItem.getQuantity().setValue(bmoOrderDeliveryItem.getQuantity().toDouble());
//							bmUpdateResult.setBmObject(bmoOrderDeliveryItem);
//							// si es rastreo Serie y ya existe en el pedido manda mensaje
//						}else if(bmoWhTrack.getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL)) {
//							bmUpdateResult.setBmObject(bmoOrderDeliveryItem);
//						}
//						
//						
//					}
					
				
			}else {

				if (bmoOrderDeliveryItem.getId() > 0) {

					bmUpdateResult.setBmObject(bmoOrderDeliveryItem);

				} else {

					// No encontró en ningun lugar la clave / #serie / #lote; busca en cajas de productos
					PmWhBox pmWhBox = new PmWhBox(getSFParams());
					BmoWhBox bmoWhBox = pmWhBox.searchBoxByCode(pmConn, barcode);

					// Fue encontrada la caja
					if (bmoWhBox.getId() > 0) {

						// Actualiza los items de la caja
						pmOrderDelivery.updateItemsFromWhBox(bmoOrderDelivery, "" + bmoWhBox.getId(), bmUpdateResult);

						// Asigna el objeto de la caja
						bmUpdateResult.setBmObject(bmoWhBox);

					} else {
						// No se encontro nada
						bmUpdateResult.addMsg("El Código de Barras No fue Localizado.");
					}
				}
			}	
		
		}

		return bmUpdateResult;
	}
	
	// Busca por clave de producto en los item
	public int searchProductByCodeCount(PmConn pmConn, int orderDeliveryId, String prodcode) throws SFException {
		String sql = "";
		int countOdyi = 0;

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT COUNT(odyi_orderdeliveryitemid) AS countOdyi FROM orderdeliveryitems " +
				" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
				" LEFT JOIN products ON (odyi_productid = prod_productid) " + 
				" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
				" AND prod_code LIKE '" + prodcode + "' " +
				" ORDER BY odyi_serial ASC"; 

		printDevLog("sql_searchProductByCodeCount: "+ sql);
		pmConn.doFetch(sql);

		if (pmConn.next())
			countOdyi = pmConn.getInt("countOdyi");

		return countOdyi;
	}

	// Busca por clave de producto en los item
	public BmoOrderDeliveryItem searchProductByCode(PmConn pmConn, int orderDeliveryId, String prodcode) throws SFException {
		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		String sql = "";
		
		// Contar cuantos items hay de un producto, si solo es uno lo devuelve
		if ((searchProductByCodeCount(pmConn, orderDeliveryId, prodcode) == 1)) {

			// Busca la clave del producto dentro de los items del recibo de orden de compra
			sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
					" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
					" LEFT JOIN products ON (odyi_productid = prod_productid) " + 
					" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
					" AND prod_code LIKE '" + prodcode + "' " +
					" ORDER BY odyi_serial ASC"; 
	
			printDevLog("sql_searchProductByCode: "+sql);
	
			pmConn.doFetch(sql);
	
			if (pmConn.next()) {
				int orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");
				printDevLog("Como si encontro el item, procede a obtener el objeto de negocios, con el id: " + orderDeliveryItemId);
				bmoOrderDeliveryItem = (BmoOrderDeliveryItem)this.get(pmConn, orderDeliveryItemId);	
			}
		} else {
			// Si hay mas items con el mismo producto, devuelve el siguiente(el que no tiene cantidad asignada[o que todo esta pendiente])
			bmoOrderDeliveryItem = searchProductByCodeUnassigned(pmConn, orderDeliveryId, prodcode);
		}

		return bmoOrderDeliveryItem;
	}
	//Buscar si ya existe el complementario
//	public BmoOrderDeliveryItem searchComplementaryItem(PmConn pmConn,int ordeDeliveryId,String barCode) throws SFException {
//		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
//		String sql = "";
//		
//		sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems WHERE odyi_serial like '"+barCode+"' AND odyi_orderdeliveryid = " +ordeDeliveryId;
//		
//		System.out.println("Busacr Complementarios  " + sql);
//		
//		pmConn.doFetch(sql);
//		
//		if (pmConn.next()) {
//			int orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");
//			System.out.println("ITEM ENCONTRADO  "+ orderDeliveryItemId );
//			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)this.get(pmConn, orderDeliveryItemId);	
//		}
//		return bmoOrderDeliveryItem;
//	}

	public BmoOrderDeliveryItem searchProductByCodeUnassigned(PmConn pmConn, int orderDeliveryId, String prodcode) throws SFException {
		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
				" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
				" LEFT JOIN products ON (odyi_productid = prod_productid) " + 
				" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
				" AND prod_code LIKE '" + prodcode + "' " +
				" AND NOT odyi_quantity > 0 " + 
				" ORDER BY odyi_serial ASC"; 

		printDevLog("sql_searchProductByCodeUnassigned: " + sql);

		pmConn.doFetch(sql);

		if (pmConn.next()) {
			int orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");
			printDevLog("Como si encontro el item, procede a obtener el objeto de negocios, con el id: " + orderDeliveryItemId);
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)this.get(pmConn, orderDeliveryItemId);	
		}

		return bmoOrderDeliveryItem;
	}

	// Busca por clave de producto y que no sea el mismo numero de serie
	//	public BmoOrderDeliveryItem searchProductByCodeDiffSerial(PmConn pmConn, int orderDeliveryId, String prodcode, String serial) throws SFException {
	//		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
	//		String sql = "";
	//
	//		// Busca la clave del producto dentro de los items del recibo de orden de compra
	//		sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
	//				" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
	//				" LEFT JOIN products ON (odyi_productid = prod_productid) " + 
	//				" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
	//				" AND prod_code LIKE '" + prodcode + "' " +
	//				" AND odyi_serial IS NULL " +
	//				//" AND odyi_serial NOT LIKE '" + serial + "' " +
	//				" ORDER BY odyi_serial ASC"; 
	//
	//		System.out.println(sql);
	//
	//		pmConn.doFetch(sql);
	//
	//		if (pmConn.next()) {
	//			int orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");
	//			System.out.println("Como si encontro el item, procede a obtener el objeto de negocios, con el id: " + orderDeliveryItemId);
	//			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)this.get(pmConn, orderDeliveryItemId);	
	//		}
	//
	//		return bmoOrderDeliveryItem;
	//	}

	// Busca productos por numero de serie
	private BmoOrderDeliveryItem searchProductByBarCode(PmConn pmConn, int orderDeliveryId, String prodcode, String barcode) throws SFException {
		bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
				" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
				" LEFT JOIN products ON (odyi_productid = prod_productid) " + 
				" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
				" AND prod_code LIKE '" + prodcode + "'" +
				" AND (odyi_serial = '" + barcode + "' OR odyi_serial IS NULL)"; 
		printDevLog("sql_searchProductByBarCode: " + sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			int orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)this.get(pmConn, orderDeliveryItemId);	
		}

		return bmoOrderDeliveryItem;
	}

	// Busca si existe un item por numero de serie
	public boolean serialExists(PmConn pmConn, int orderDeliveryId, String serial) throws SFException {
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
				" LEFT JOIN orderitems ON (odyi_orderitemid = ordi_orderitemid) " + 
				" WHERE odyi_orderdeliveryid = " + orderDeliveryId + 
				" AND odyi_serial = '" + serial + "' " +
				" AND NOT odyi_quantity > 0 ";
		
		printDevLog("sql_serialExists: "+sql);
		pmConn.doFetch(sql);

		return pmConn.next();
	}

	// Validacion de que exista el #serie/lote con cantidad mayor a 0 en almacen tipo normal
	public boolean serialExistsWarehouseNormal(PmConn pmConn, String serial) throws SFException {
		String sql = "";
		PmWhStock pmWhStock = new PmWhStock(getSFParams());

		sql = " SELECT * FROM whstocks " +
				" LEFT JOIN whtracks ON (whst_whtrackid = whtr_whtrackid) " + 
				" LEFT JOIN whsections ON (whst_whsectionid = whse_whsectionid) " + 
				" LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid) " + 
				" WHERE whtr_serial LIKE '" + serial + "'" + 
				" AND whtr_inquantity > 0 " +
				" AND ware_type = '" + BmoWarehouse.TYPE_NORMAL + "'";

		sql += ((pmWhStock.getDisclosureFilters().length() > 0) ? " AND " + pmWhStock.getDisclosureFilters() : "");


		pmConn.doFetch(sql);
		return pmConn.next();
	}

	// Obtiene la seccion de almacen origen por numero de serie, siendo almacen normal
	private int getWhStockSectionBySerialDelivery(PmConn pmConn, String serial) throws SFException {
		String sql = "";
		PmWhStock pmWhStock = new PmWhStock(getSFParams());

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = " SELECT whst_whsectionid FROM whstocks " +
				" LEFT JOIN whtracks ON (whst_whtrackid = whtr_whtrackid) " + 
				" LEFT JOIN whsections ON (whst_whsectionid = whse_whsectionid) " + 
				" LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid) " + 
				" WHERE whtr_serial LIKE '" + serial + "'" + 
				" AND ware_type = '" + BmoWarehouse.TYPE_NORMAL + "'";

		sql += ((pmWhStock.getDisclosureFilters().length() > 0) ? " AND " + pmWhStock.getDisclosureFilters() : "");

		printDevLog("sql_getWhStockSectionBySerialDelivery: " + sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return pmConn.getInt("whst_whsectionid");
		} else {
			return 0;
		}
	}

	// Obtiene la seccion de almacen origen por numero de serie del Envío de Pedido de tipo Envío
	private int getFromWhSectionDelivery(PmConn pmConn, String serial, int orderId, int productId) throws SFException {
		String sql = "";

		sql = " SELECT odyi_fromwhsectionid FROM orderdeliveryitems " +
				" LEFT JOIN orderdeliveries ON (odly_orderdeliveryid = odyi_orderdeliveryid) " +
				" LEFT JOIN products ON (odyi_productid = prod_productid) " +
				" WHERE odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY  + "'" +
				" AND odly_orderid = " + orderId + 
				" AND prod_productid = " + productId +
				" AND odyi_serial = '" + serial + "'";

		printDevLog("sql_getFromWhSectionDelivery: "+sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return pmConn.getInt("odyi_fromwhsectionid");
		} else {
			return -1;
		}
	}
	//Almacen destino Complementarios
	private int getToWhSection(PmConn pmConn, int orderid) throws SFException {
		String sql = "";

		sql = " SELECT whse_whsectionid FROM whsections WHERE whse_orderid = "+ orderid;

		
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return pmConn.getInt("whse_whsectionid");
		} else {
			return -1;
		}
	}
	// Obtiene la seccion de almacen origen por numero de serie del Envío de Pedido de tipo Envío
	//		private int getToWhSectionDelivery(PmConn pmConn, String serial, int orderId, int productId) throws SFException {
	//			String sql = "";
	//
	//			sql = " SELECT odyi_towhsectionid FROM orderdeliveryitems " +
	//					" LEFT JOIN orderdeliveries ON (odly_orderdeliveryid = odyi_orderdeliveryid) " +
	//					" LEFT JOIN products ON (odyi_productid = prod_productid) " +
	//					" WHERE odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY  + "'" +
	//					" AND odly_orderid = " + orderId + 
	//					" AND prod_productid = " + productId +
	//					" AND odyi_serial = '" + serial + "'";
	//
	//			System.out.println("sql_getToWhSectionDelivery "+sql);
	//			pmConn.doFetch(sql);
	//			if (pmConn.next()) {
	//				return pmConn.getInt("odyi_fromwhsectionid");
	//			} else {
	//				return -1;
	//			}
	//		}

	// Elimina todos los items de un envío
	public void deleteItems(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmUpdateResult bmUpdateResult) throws SFException {
		// Crear los items
		BmFilter filterOrderDeliveryItem = new BmFilter();
		filterOrderDeliveryItem.setValueFilter(bmoOrderDeliveryItem.getKind(), bmoOrderDeliveryItem.getOrderDeliveryId(), bmoOrderDelivery.getId());
		Iterator<BmObject> listOrderDeliveryItem = this.list(pmConn, filterOrderDeliveryItem).iterator();			

		while (listOrderDeliveryItem.hasNext()) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)listOrderDeliveryItem.next();

			this.delete(pmConn, bmoOrderDeliveryItem, bmUpdateResult);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;

		// Obtener el Envío
		PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());			
		BmoOrderDelivery bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoOrderDeliveryItem.getOrderDeliveryId().toInteger());

		// Si el recibo ya está autorizado, no se puede hacer movimientos			
		if (!bmoOrderDelivery.getStatus().equals(BmoOrderDelivery.STATUS_REVISION)) {				
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Envío - ya está Autorizado.");
		} else {
			// Es Envio, se devolvio parcial o total, y hay cantidad en item, NO eliminar
			if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY) 
					&& bmoOrderDeliveryItem.getQuantityReturned().toDouble() > 0
					&& bmoOrderDeliveryItem.getQuantity().toDouble() > 0)
				bmUpdateResult.addMsg("<b>El Producto con #Serie/lote | " + bmoOrderDeliveryItem.getSerial() + " | se ha devuelto Parcialmente ó Totalmente. No se puede eliminar.</b>" );
			
			// Buscar si existe movimiento de almacen relacionado, en caso de encontrarlo lo elimina
			PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());
			BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
			try {
				bmoWhMovItem = (BmoWhMovItem)pmWhMovItem.getBy(pmConn, bmoOrderDeliveryItem.getId(), bmoWhMovItem.getOrderDeliveryItemId().getName());
				pmWhMovItem.delete(pmConn, bmoWhMovItem, bmUpdateResult);
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-delete() Error esperado: " + e.toString());	
			}

			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualiza estatus de saldo a todos los items relacionados
			updateQuantityBalance(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Actualiza estatus de devoluciones a todos los items relacionados
			updateQuantityReturned(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Actualiza cantidad total enviada del item en el pedido
			updateOrderQuantityDelivered(pmConn, bmoOrderDelivery, bmoOrderDeliveryItem, bmUpdateResult);

			// Recalcula el subtotal
			pmOrderDelivery.updateBalance(pmConn, bmoOrderDelivery, bmUpdateResult);
		}

		return bmUpdateResult;
	}
}
