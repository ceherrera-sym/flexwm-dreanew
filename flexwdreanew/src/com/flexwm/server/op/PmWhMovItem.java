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
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWhMovItem extends PmObject {
	BmoWhMovItem bmoWhMovItem;

	public PmWhMovItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhMovItem = new BmoWhMovItem();
		setBmObject(bmoWhMovItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhMovItem.getProductId(), bmoWhMovItem.getBmoProduct()),
				new PmJoin(bmoWhMovItem.getBmoProduct().getProductFamilyId(), bmoWhMovItem.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoWhMovItem.getBmoProduct().getProductGroupId(), bmoWhMovItem.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoWhMovItem.getBmoProduct().getUnitId(), bmoWhMovItem.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoWhMovItem.getFromWhSectionId(), bmoWhMovItem.getBmoFromWhSection()),
				new PmJoin(bmoWhMovItem.getBmoFromWhSection().getWarehouseId(), bmoWhMovItem.getBmoFromWhSection().getBmoWarehouse()),
				new PmJoin(bmoWhMovItem.getBmoFromWhSection().getBmoWarehouse().getCompanyId(), bmoWhMovItem.getBmoFromWhSection().getBmoWarehouse().getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWhMovItem = (BmoWhMovItem) autoPopulate(pmConn, new BmoWhMovItem());		

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productIdId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productIdId > 0) bmoWhMovItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoWhMovItem.setBmoProduct(bmoProduct);

		// BmoWhSection
		BmoWhSection bmoWhSection = new BmoWhSection();
		int whSectionId = (int)pmConn.getInt(bmoWhSection.getIdFieldName());
		if (whSectionId > 0) bmoWhMovItem.setBmoFromWhSection((BmoWhSection) new PmWhSection(getSFParams()).populate(pmConn));
		else bmoWhMovItem.setBmoFromWhSection(bmoWhSection);

		return bmoWhMovItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWhMovItem = (BmoWhMovItem)bmObject;

		// Obten datos del producto, y asignarlo al movimiento
		PmProduct pmProduct = new PmProduct(getSFParams());
		BmoProduct bmoProduct = new BmoProduct();
		
		if (!(bmoWhMovItem.getProductId().toInteger() > 0))
			bmUpdateResult.addError(bmoWhMovItem.getProductId().getName(), "<b>Debe seleccionar un Producto.</b>");

		bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoWhMovItem.getProductId().toInteger());
		bmoWhMovItem.setBmoProduct(bmoProduct);

		// Obten datos del movimiento
		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		BmoWhMovement bmoWhMovement = new BmoWhMovement();
		bmoWhMovement = (BmoWhMovement)pmWhMovement.get(pmConn, bmoWhMovItem.getWhMovementId().toInteger());

		// Si el movimiento ya está autorizada, no se puede hacer movimientos
		if (bmoWhMovement.getStatus().toChar() == BmoWhMovement.STATUS_AUTHORIZED)
			bmUpdateResult.addMsg("No se pueden realizar Cambios - El Movimiento ya está Autorizado.");
		else {
			// Si afecta inventario, hacer los movimientos 
			if (bmoProduct.getInventory().toBoolean()) {

				// Si es modificacion, obtener la cantidad anterior para las afectaciones de inventario
				double prevQuantity = 0;
				if (bmoWhMovItem.getId() > 0) {
					PmWhMovItem prevBmoWhMovItem = new PmWhMovItem(getSFParams());
					try {
						prevQuantity = ((BmoWhMovItem)prevBmoWhMovItem.get(bmoWhMovItem.getId())).getQuantity().toDouble();						
					} catch (SFException e) {
						System.out.println(this.getClass().getName() + "-save(): Error Esperado: " + e.toString());
					}
				}

				// Validar que la cantidad no sea fraccion si el producto es SERIE
				// Si es Sin Rastreo, verificar por la Unidad del producto
				if (!pmProduct.applyFraction(bmoProduct, bmoWhMovItem.getQuantity().toDouble()))
					bmUpdateResult.addError(bmoWhMovItem.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");

				// Revisa que la cantidad no sea menor a 0
				if (bmoWhMovItem.getQuantity().toDouble() <= 0)
					bmUpdateResult.addMsg("La Cantidad no puede ser menor/igual a 0.");					

				// Almacena el item del movimiento para establecer el ID
				super.save(pmConn, bmoWhMovItem, bmUpdateResult);

				bmoWhMovItem.setId(bmUpdateResult.getId());

				// Ingreso de productos
				if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN)
					movementIn(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);			

				// Devolución de ingreso de productos
				if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_DEV)
					movementOut(pmConn, bmoWhMovement, bmoProduct, prevQuantity, bmUpdateResult);	

				// Ajuste de entrada
				else if (bmoWhMovement.getType().equals(BmoWhMovement.TYPE_IN_ADJUST))
					movementIn(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);	

				// Salida de productos
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT))
					movementOut(pmConn, bmoWhMovement, bmoProduct, prevQuantity, bmUpdateResult);

				// Devolucion de salida
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_DEV))
					movementInDev(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);

				// Salida Ajuste
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_ADJUST))
					movementOut(pmConn, bmoWhMovement, bmoProduct, prevQuantity, bmUpdateResult);

				// Transferencia de productos
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_TRANSFER))
					movementTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);

				// Salida por Renta
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_OUT))
					movementTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);

				// Entrada por Renta
				else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_IN))
					movementTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, prevQuantity, bmUpdateResult);

			} 
		}

		// No hay errores, seguir actualizando datos
		if (!bmUpdateResult.hasErrors()) {
			// Establecer el valor del item
			bmoWhMovItem.getAmount().setValue(bmoWhMovItem.getQuantity().toDouble() * bmoProduct.getCost().toDouble());

			// Almacena el item del movimiento
			super.save(pmConn, bmoWhMovItem, bmUpdateResult);

			// Actualiza valor del movimiento completo
			pmWhMovement.updateAmount(pmConn, bmoWhMovement, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Entrada
	private void movementIn(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, double lastQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Es de tipo de Rastreo
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) 
				|| bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {

			// Validar que este asignado el numero de serie si la cantidad es mayor a 0
			if ((!(bmoWhMovItem.getSerial().toString().length() > 0))
					&& (bmoWhMovItem.getQuantity().toDouble() > 0)) 
				bmUpdateResult.addMsg("Debe establecerse el # Serie/Lote para este tipo de Producto.");

			try {
				// Obtener el registro de rastreo
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getId(), bmoWhTrack.getWhMovItemId().getName());

			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-movementIn() Error aceptable, no se Encuentra el Rastreo de producto: " + e.toString());
			}

			// Corregir cantidad de items si es numero de serie
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) && quantity > 0) 
				quantity = 1;

			pmWhTrack.updateWhTrack(pmConn, 
					bmoWhMovement, 
					bmoWhMovItem, 
					bmoWhTrack, 
					quantity, 
					0, 
					bmUpdateResult);
		}

		// Se suma la cantidad al inventario
		double modQuantity = quantity - lastQuantity;

		// Actualiza inventario
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				modQuantity,
				bmoProduct.getId(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getToWhSectionId().toInteger(),
				bmoWhTrack.getId());

		// Si ya existia, y ahora es cero, lo elimina
		if (bmoWhMovItem.getQuantity().toDouble() == 0 
				&& lastQuantity > 0)
			pmWhTrack.delete(pmConn, bmoWhTrack, bmUpdateResult);
	}

	// Entrada por devolucion
	private void movementInDev(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, double lastQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		double modQuantity = quantity - lastQuantity;
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Es de tipo de Rastreo
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) 
				|| bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {

			// Validar que este asignado el numero de serie si la cantidad es mayor a 0
			if ((!(bmoWhMovItem.getSerial().toString().length() > 0))
					&& (bmoWhMovItem.getQuantity().toDouble() > 0)) 
				bmUpdateResult.addMsg("Debe establecerse el # Serie/Lote para este tipo de Producto.");

			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-movementIn() Error aceptable, no se Encuentra el Rastreo de producto: " + e.toString());
			}

			// Corregir cantidad de items si es numero de serie
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) && quantity > 0) 
				quantity = 1;

			pmWhTrack.updateWhTrack(pmConn, 
					bmoWhMovement, 
					bmoWhMovItem, 
					bmoWhTrack, 
					bmoWhTrack.getInQuantity().toDouble() + modQuantity, 
					bmoWhTrack.getOutQuantity().toDouble() - modQuantity, 
					//quantity, 
					//0, 
					bmUpdateResult);
		}

		// Se suma la cantidad al inventario
		//double modQuantity = quantity - lastQuantity;

		// 
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				modQuantity,
				bmoProduct.getId(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getToWhSectionId().toInteger(),
				bmoWhTrack.getId());

		// Si ya existia, y ahora es cero, lo elimina
		//		if (bmoWhMovItem.getQuantity().toDouble() == 0 
		//				&& lastQuantity > 0)
		//			pmWhTrack.delete(pmConn, bmoWhTrack, bmUpdateResult);
	}

	// Salida
	private void movementOut(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoProduct bmoProduct, double lastQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		double modQuantity = quantity - lastQuantity;

		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Afecta inventarios, si hay movimiento
		if (quantity != lastQuantity) {
			// Tiene rastreo
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
				// Validar que este asignado el numero de serie
				if (!(bmoWhMovItem.getSerial().toString().length() > 0)) 
					bmUpdateResult.addMsg("Debe establecerse el Num. de Serie para este tipo de Item.");

				try {
					bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
				} catch (SFException e) {
					System.out.println(this.getClass().getName() + "-movementIn() Error aceptable, no se encuentra el rastreo de producto: " + e.toString());
				}

				// Corregir cantidad de items si es numero de serie
				if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) && quantity > 0) 
					quantity = 1;

				pmWhTrack.updateWhTrack(pmConn, 
						bmoWhMovement, 
						bmoWhMovItem, 
						bmoWhTrack, 
						bmoWhTrack.getInQuantity().toDouble() - modQuantity, 
						bmoWhTrack.getOutQuantity().toDouble() + modQuantity, 
						bmUpdateResult);
			}

			// Revisar que exista el inventario suficiente para la salida
			checkExistence(pmConn, bmoWhMovItem, bmoWhMovement, bmoWhMovItem.getFromWhSectionId().toInteger(), bmoWhTrack.getId(), lastQuantity, bmUpdateResult);

			// Actualiza inventario
			pmWhStock.updateStock(pmConn, 
					bmUpdateResult, 
					-modQuantity,
					bmoProduct.getId(),
					bmoProduct.getCost().toDouble(),
					bmoWhMovItem.getFromWhSectionId().toInteger(),
					bmoWhTrack.getId());
		}
	}

	// Transferencia
	private void movementTransfer(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, double lastQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		double modQuantity = quantity - lastQuantity;
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Tipo de producto con rastreo a nivel de número de serie
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
			} catch (SFException e) {
				bmUpdateResult.addMsg("No existe el número de Serie / Lote.");
			}
		}

		// Revisar que exista el inventario suficiente para la salida para transferencia
		checkExistence(pmConn, bmoWhMovItem, bmoWhMovement, bmoWhMovItem.getFromWhSectionId().toInteger(), bmoWhTrack.getId(), lastQuantity, bmUpdateResult);

		// Afecta inventarios, si hay movimiento
		if (quantity != lastQuantity) {

			// Restar al almacen origen
			pmWhStock.updateStock(pmConn, 
					bmUpdateResult, 
					-modQuantity,
					bmoWhMovItem.getProductId().toInteger(),
					bmoProduct.getCost().toDouble(),
					bmoWhMovItem.getFromWhSectionId().toInteger(),
					bmoWhTrack.getId());

			// Sumar al almacen destino
			// Si es de tipo Renta, obtiene la Seccion destino de cada Item, en caso Contrario, del Destino del Movimiento
			quantity = bmoWhMovItem.getQuantity().toDouble();
			pmWhStock.updateStock(pmConn, 
					bmUpdateResult, 
					modQuantity,
					bmoWhMovItem.getProductId().toInteger(),
					bmoProduct.getCost().toDouble(),
					((bmoWhMovement.getType().equals(BmoWhMovement.TYPE_RENTAL_IN))
							? bmoWhMovItem.getToWhSectionId().toInteger() : bmoWhMovement.getToWhSectionId().toInteger())
					,
					bmoWhTrack.getId());
		}
	}

	// Revisar existencia
	public void checkExistence(PmConn pmConn, BmoWhMovItem bmoWhMovItem, BmoWhMovement bmoWhMovement, int whSectionId, int whTrackId, double lastQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		double existence = 0;
		PmWhStock pmWhStock = new PmWhStock(getSFParams());

		// Revisa cantidad del producto en el pedido total
		String sql = "SELECT SUM(whst_quantity) FROM whstocks " +
				" LEFT JOIN whsections ON (whst_whsectionid = whse_whsectionid) " + 
				" LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid) " + 
				" WHERE whst_whsectionid = " + whSectionId +
				" AND whst_productid = " + bmoWhMovItem.getProductId().toInteger();

		// Si es de rastreo, validar 
		if (whTrackId > 0)
			sql += " AND whst_whtrackid = " + whTrackId;

		sql += ((pmWhStock.getDisclosureFilters().length() > 0) ? " AND " + pmWhStock.getDisclosureFilters() : "");

		pmConn.doFetch(sql);
		if (pmConn.next()) existence = pmConn.getDouble(1);
		
		existence += lastQuantity;

		if (bmoWhMovItem.getQuantity().toDouble() > existence) {
			bmUpdateResult.addMsg(" No hay suficiente inventario. "
					+ " Salida: " + bmoWhMovItem.getQuantity().toDouble()
					+ " Existencia: " + existence);
			bmUpdateResult.addError(bmoWhMovItem.getQuantity().getName(),	
					" No puede ser mayor a: " + existence);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {

		// Recupera todos los datos del registro a borrar
		bmoWhMovItem = (BmoWhMovItem)this.get(pmConn, bmObject.getId());

		// Obten datos del producto
		PmProduct pmProduct = new PmProduct(getSFParams());
		BmoProduct bmoProduct = new BmoProduct();
		bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoWhMovItem.getProductId().toInteger());

		// Obten datos del movimiento
		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		BmoWhMovement bmoWhMovement = new BmoWhMovement();
		bmoWhMovement = (BmoWhMovement)pmWhMovement.get(pmConn, bmoWhMovItem.getWhMovementId().toInteger());

		// Si el movimiento ya está autorizada, no se puede hacer movimientos
		if (bmoWhMovement.getStatus().toChar() == BmoWhMovement.STATUS_AUTHORIZED)
			bmUpdateResult.addMsg("No se pueden realizar Cambios - El Movimiento ya está Autorizado.");
		else {
			// Se elimina movimiento de Entrada
			if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN)
				deleteIn(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de Entrada devolución
			else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_DEV)
				deleteOut(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de Entrada Ajuste
			else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_ADJUST)
				deleteIn(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de Salida
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT))
				deleteOut(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de devolucion de salida
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_DEV)) 
				deleteInDev(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de Salida por ajuste
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_ADJUST))
				deleteOut(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de transferencia
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_TRANSFER))
				deleteTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de salida por renta
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_OUT))
				deleteTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Se elimina movimiento de entrada de renta
			else if ((bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_IN))
				deleteTransfer(pmConn, bmoWhMovement, bmoWhMovItem, bmoProduct, bmUpdateResult);

			// Elimina el item del movimiento
			super.delete(pmConn, bmoWhMovItem, bmUpdateResult);

			// Actualiza valor del movimiento completo
			pmWhMovement.updateAmount(pmConn, bmoWhMovement, bmUpdateResult);

		}

		return bmUpdateResult;
	}

	// Elimina Entrada
	private void deleteIn(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = -bmoWhMovItem.getQuantity().toDouble();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Es de tipo de Rastreo
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getId(), bmoWhTrack.getWhMovItemId().getName());
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-deleteIn() " + e.toString());
			}
		}

		// Se modifica la cantidad al inventario
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				quantity,
				bmoWhMovItem.getProductId().toInteger(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getToWhSectionId().toInteger(),
				bmoWhTrack.getId());

		// Si existe rastreo, lo elimina
		if (bmoWhTrack.getId() > 0)
			pmWhTrack.delete(pmConn, bmoWhTrack, bmUpdateResult);
	}

	// Elimina Entrada de Devolucion
	private void deleteInDev(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = -bmoWhMovItem.getQuantity().toDouble();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Es de tipo de Rastreo
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-deleteIn() " + e.toString());
			}

			// Actualiza rastreo
			pmWhTrack.updateWhTrack(pmConn, 
					bmoWhMovement, 
					bmoWhMovItem, 
					bmoWhTrack, 
					bmoWhTrack.getInQuantity().toDouble() - bmoWhMovItem.getQuantity().toDouble(), 
					bmoWhTrack.getOutQuantity().toDouble() + bmoWhMovItem.getQuantity().toDouble(), 
					bmUpdateResult);
		}

		// Se modifica la cantidad al inventario
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				quantity,
				bmoWhMovItem.getProductId().toInteger(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getToWhSectionId().toInteger(),
				bmoWhTrack.getId());

		// Si existe rastreo, lo elimina
		//		if (bmoWhTrack.getId() > 0)
		//			pmWhTrack.delete(pmConn, bmoWhTrack, bmUpdateResult);
	}

	// Elimina Salida
	private void deleteOut(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Tiene rastreo
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
			// Validar que este asignado el numero de serie
			if (!(bmoWhMovItem.getSerial().toString().length() > 0)) 
				bmUpdateResult.addMsg("Debe establecerse el Num. de Serie para este tipo de Item.");

			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-movementIn() Error aceptable, no se encuentra el rastreo de producto: " + e.toString());
			}

			// Corregir cantidad de items si es numero de serie
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) && quantity > 0) 
				quantity = 1;

			pmWhTrack.updateWhTrack(pmConn, 
					bmoWhMovement, 
					bmoWhMovItem, 
					bmoWhTrack, 
					bmoWhTrack.getInQuantity().toDouble() + bmoWhMovItem.getQuantity().toDouble(), 
					bmoWhTrack.getOutQuantity().toDouble() - bmoWhMovItem.getQuantity().toDouble(), 
					bmUpdateResult);
		}

		// Actualiza inventario
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				quantity,
				bmoProduct.getId(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getFromWhSectionId().toInteger(),
				bmoWhTrack.getId());
	}

	// Elimina transferencia
	private void deleteTransfer(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoProduct bmoProduct, BmUpdateResult bmUpdateResult) throws SFException {
		double quantity = bmoWhMovItem.getQuantity().toDouble();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = new BmoWhTrack();

		// Revisar tipo de rastreo, generar acciones relacionadas
		if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL) || bmoProduct.getTrack().equals(BmoProduct.TRACK_BATCH)) {
			try {
				bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(pmConn, bmoWhMovItem.getSerial().toString(), bmoWhTrack.getSerial().getName());
			} catch (SFException e) {
				bmUpdateResult.addMsg("No existe el número de Serie / Lote.");
			}
		}

		// Se ejecuta 2 veces la actualizacion en los distintos almacenes
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				quantity,
				bmoWhMovItem.getProductId().toInteger(),
				bmoProduct.getCost().toDouble(),
				bmoWhMovItem.getFromWhSectionId().toInteger(),
				bmoWhTrack.getId());

		// Disminuir al almacen destino
		quantity = -bmoWhMovItem.getQuantity().toDouble();
		pmWhStock.updateStock(pmConn, 
				bmUpdateResult, 
				quantity,
				bmoWhMovItem.getProductId().toInteger(),
				bmoProduct.getCost().toDouble(),
				((bmoWhMovement.getType().equals(BmoWhMovement.TYPE_RENTAL_IN))
						? bmoWhMovItem.getToWhSectionId().toInteger() : bmoWhMovement.getToWhSectionId().toInteger()),
				//bmoWhMovement.getToWhSectionId().toInteger(),
				bmoWhTrack.getId());
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		BmoWhMovement bmoWhMovement = (BmoWhMovement)bmObject;

		// Revisar cantidad de items apartados
		if (action.equals(BmoWhMovItem.ACTION_SEARCHBARCODE)) {
			bmUpdateResult.setBmObject(getBarcodeObject(bmoWhMovement, value));
		} 

		return bmUpdateResult;
	}

	// Revisar si existe la el movimiento de la ROC
	public boolean requisitionReceiptItemWhMovItemExists(PmConn pmConn, int requisitionReceiptItemId) throws SFPmException {
		pmConn.doFetch("SELECT whmi_whmovitemid FROM whmovitems WHERE whmi_requisitionreceiptitemid = " + requisitionReceiptItemId);
		return pmConn.next();
	}

	// Revisar si existe la el movimiento del envio del pedido
	public boolean orderDeliveryItemWhMovItemExists(PmConn pmConn, int orderDeliveryItemId) throws SFPmException {
		pmConn.doFetch("SELECT whmi_whmovitemid FROM whmovitems WHERE whmi_orderdeliveryitemid = " + orderDeliveryItemId);
		return pmConn.next();
	}

	// Obtiene objeto de codigo de barras
	private BmObject getBarcodeObject(BmoWhMovement bmoWhMovement, String code) {
		BmObject bmObject = new BmObject();

		// Buscar el codigo de barras en los productos
		try {
			PmProduct pmProduct = new PmProduct(getSFParams());
			BmoProduct bmoProduct = new BmoProduct();
			bmoProduct = (BmoProduct)pmProduct.getBy(code, bmoProduct.getCode().getName());
			return bmoProduct;
		} catch (SFException e) {
			// El producto no se encontro, seguir buscando
		}

		// Buscar el codigo de barras en los rastreos por numero de serie
		try {
			PmWhBox pmWhBox = new PmWhBox(getSFParams());
			BmoWhBox bmoWhBox = new BmoWhBox();
			bmoWhBox = (BmoWhBox)pmWhBox.getBy(code, bmoWhBox.getCode().getName());
			return bmoWhBox;
		} catch (SFException e) {
			// El producto no se encontro, seguir buscando
		}

		// Buscar el codigo de barras en las cajas de productos
		try {
			PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
			BmoWhTrack bmoWhTrack = new BmoWhTrack();
			bmoWhTrack = (BmoWhTrack)pmWhTrack.getBy(code, bmoWhTrack.getSerial().getName());
			return bmoWhTrack;
		} catch (SFException e) {
			// El producto no se encontro, seguir buscando
		}

		return bmObject;
	}
}
