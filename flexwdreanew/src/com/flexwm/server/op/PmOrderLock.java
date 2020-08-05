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
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderLock;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhSection;


public class PmOrderLock extends PmObject {
	BmoOrderLock bmoOrderLock;

	public PmOrderLock(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderLock = new BmoOrderLock();
		setBmObject(bmoOrderLock);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderLock.getOrderId(), bmoOrderLock.getBmoOrder()),
				new PmJoin(bmoOrderLock.getBmoOrder().getCustomerId(), bmoOrderLock.getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoOrderLock.getBmoOrder().getCurrencyId(), bmoOrderLock.getBmoOrder().getBmoCurrency()),
				new PmJoin(bmoOrderLock.getProductId(), bmoOrderLock.getBmoProduct()),
				new PmJoin(bmoOrderLock.getBmoProduct().getProductFamilyId(), bmoOrderLock.getBmoProduct().getBmoProductFamily()),				
				new PmJoin(bmoOrderLock.getBmoProduct().getProductGroupId(), bmoOrderLock.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoOrderLock.getBmoProduct().getUnitId(), bmoOrderLock.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoOrderLock.getRequisitionId(), bmoOrderLock.getBmoRequisition()),
				new PmJoin(bmoOrderLock.getBmoRequisition().getSupplierId(), bmoOrderLock.getBmoRequisition().getBmoSupplier()),	
				new PmJoin(bmoOrderLock.getBmoRequisition().getReqPayTypeId(), bmoOrderLock.getBmoRequisition().getBmoReqPayType()),	
				new PmJoin(bmoOrderLock.getWhSectionId(), bmoOrderLock.getBmoWhSection()),
				new PmJoin(bmoOrderLock.getBmoWhSection().getWarehouseId(), bmoOrderLock.getBmoWhSection().getBmoWarehouse()),
				new PmJoin(bmoOrderLock.getBmoWhSection().getBmoWarehouse().getCompanyId(), bmoOrderLock.getBmoWhSection().getBmoWarehouse().getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderLock = (BmoOrderLock)autoPopulate(pmConn, new BmoOrderLock());

		// BmoOrder
		BmoOrder bmoOrder = new BmoOrder();
		int orderId = (int)pmConn.getInt(bmoOrder.getIdFieldName());
		if (orderId > 0) bmoOrderLock.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else bmoOrderLock.setBmoOrder(bmoOrder);

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoOrderLock.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoOrderLock.setBmoProduct(bmoProduct);

		// BmoRequisition
		BmoRequisition bmoRequisition = new BmoRequisition();
		int requisitionId = (int)pmConn.getInt(bmoRequisition.getIdFieldName());
		if (requisitionId > 0) bmoOrderLock.setBmoRequisition((BmoRequisition) new PmRequisition(getSFParams()).populate(pmConn));
		else bmoOrderLock.setBmoRequisition(bmoRequisition);

		// BmoWhSection
		BmoWhSection bmoWhSection = new BmoWhSection();
		int whSectionId = (int)pmConn.getInt(bmoWhSection.getIdFieldName());
		if (whSectionId > 0) bmoOrderLock.setBmoWhSection((BmoWhSection) new PmWhSection(getSFParams()).populate(pmConn));
		else bmoOrderLock.setBmoWhSection(bmoWhSection);

		return bmoOrderLock;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderLock = (BmoOrderLock)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderLock.getOrderId().toInteger());

		// Primero guarda el dato
		super.save(pmConn, bmoOrderLock, bmUpdateResult);

		// Es de tipo orden de compra, validar disponibilidad
		if (bmoOrderLock.getType().equals(BmoOrderLock.TYPE_REQUISITION)) {
			// Revisar bloqueo
			lockRequisition(pmConn, bmoOrder, bmoOrderLock, bmUpdateResult);

		} else if (bmoOrderLock.getType().equals(BmoOrderLock.TYPE_WHSECTION)) {
			// Es de tipo almacen
			// Revisar bloqueo
			lockWhSection(pmConn, bmoOrder, bmoOrderLock, bmUpdateResult);
		}

		// Primero agrega el ultimo valor
		super.save(pmConn, bmoOrderLock, bmUpdateResult);

		return bmUpdateResult;
	}

	// Bloquear las ordenes de compra
	private void lockRequisition(PmConn pmConn, BmoOrder bmoOrder, BmoOrderLock bmoOrderLock, BmUpdateResult bmUpdateResult) throws SFException {

		// Sumar cantidad del producto en la orden de compra
		String requisitionQuantitySql = "SELECT SUM(rqit_quantity) FROM requisitionitems "
				+ " LEFT JOIN requisitions ON (rqit_requisitionid = reqi_requisitionid) "
				+ " WHERE rqit_productid = " + bmoOrderLock.getProductId().toInteger()
				+ " AND reqi_requisitionid = " + bmoOrderLock.getRequisitionId().toInteger();
		pmConn.doFetch(requisitionQuantitySql);
		pmConn.next();
		int requisitionQuantity = pmConn.getInt(1);

		// Sumar las cantidades bloqueadas de esa orden de compra
		String lockedSql = "SELECT SUM(orlk_quantity) FROM orderlocks "
				+ " LEFT JOIN orders ON (orlk_orderid = orde_orderid) "
				+ " LEFT JOIN requisitions ON (orlk_requisitionid = reqi_requisitionid) "
				+ " WHERE orlk_productid = " + bmoOrderLock.getProductId().toInteger()
				+ " AND reqi_requisitionid = " + bmoOrderLock.getRequisitionId().toInteger()
				+ " AND ( "
				+ " ('" + bmoOrder.getLockStart().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				+ " OR  ('" + bmoOrder.getLockEnd().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				+ " OR  (orde_lockstart BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				+ " OR  (orde_lockend BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				+ " ) ";
		pmConn.doFetch(lockedSql);
		pmConn.next();
		int lockedQuantity = pmConn.getInt(1);

		if (lockedQuantity > requisitionQuantity)
			bmUpdateResult.addError(bmoOrderLock.getRequisitionId().getName(), "La Orden de Compra no tiene suficientes items disponibles: "
					+ "Total Orden de Compra: " + requisitionQuantity
					+ ", Total Bloqueo: " + lockedQuantity);
	}

	// Bloquear secciones de almacen
	private void lockWhSection(PmConn pmConn, BmoOrder bmoOrder, BmoOrderLock bmoOrderLock, BmUpdateResult bmUpdateResult) throws SFException {

		// Recuperar existencias en almacen
		String stockSql = "SELECT SUM(whst_quantity) FROM whstocks "
				+ " WHERE whst_whsectionid = " + bmoOrderLock.getWhSectionId().toInteger()
				+ " AND whst_productid = " + bmoOrderLock.getProductId().toInteger();
		pmConn.doFetch(stockSql);
		pmConn.next();
		double stockQuantity = pmConn.getDouble(1);
		if (!getSFParams().isProduction()) System.out.println("PmOrderItem-isLocked(): stockSQL: " + stockSql);

		// Sumar las cantidades bloqueadas de esa orden de compra
		String lockedSql = "SELECT SUM(orlk_quantity) FROM orderlocks "
				+ " LEFT JOIN orders ON (orlk_orderid = orde_orderid) "
				+ " LEFT JOIN whsections ON (orlk_whsectionid = whse_whsectionid) "
				+ " WHERE orlk_productid = " + bmoOrderLock.getProductId().toInteger()
				+ " AND whse_whsectionid = " + bmoOrderLock.getWhSectionId().toInteger()
				+ " AND ( "
				+ " ('" + bmoOrder.getLockStart().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				+ " OR  ('" + bmoOrder.getLockEnd().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				+ " OR  (orde_lockstart BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				+ " OR  (orde_lockend BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				+ " ) ";
		pmConn.doFetch(lockedSql);
		pmConn.next();
		int lockedQuantity = pmConn.getInt(1);

		if (lockedQuantity > stockQuantity)
			bmUpdateResult.addError(bmoOrderLock.getWhSectionId().getName(), "La Sección de Almacén no tiene suficientes items disponibles: "
					+ "Total S. Almacén: " + stockQuantity
					+ ", Total Bloqueo: " + lockedQuantity);
	}

}
