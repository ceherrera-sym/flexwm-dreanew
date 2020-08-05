/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.op.BmoOrder;


public class PmOrderSessionTypePackage extends PmObject {
	BmoOrderSessionTypePackage bmoOrderSessionTypePackage;

	public PmOrderSessionTypePackage(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
		setBmObject(bmoOrderSessionTypePackage);

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return (BmoOrderSessionTypePackage)autoPopulate(pmConn, new BmoOrderSessionTypePackage());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderSessionTypePackage.getOrderId().toInteger());
		
		// Obten el paquete
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoOrderSessionTypePackage.getSessionTypePackageId().toInteger());
		
		// Obten la venta de sesion
		PmSessionSale pmSessionSale = new PmSessionSale(getSFParams());
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		bmoSessionSale = (BmoSessionSale)pmSessionSale.getBy(pmConn, bmoOrderSessionTypePackage.getOrderId().toInteger(), bmoSessionSale.getOrderId().getName());
		
		// Asigna valores
		bmoOrderSessionTypePackage.getName().setValue(bmoSessionTypePackage.getName().toString());
		bmoOrderSessionTypePackage.getDescription().setValue(bmoSessionTypePackage.getDescription().toString());
		
		// Si no estan asignados los valores default los asigna
		if (!(bmoOrderSessionTypePackage.getPrice().toDouble() > 0)) {
			bmoOrderSessionTypePackage.getPrice().setValue(bmoSessionTypePackage.getSalePrice().toDouble());						
			bmoOrderSessionTypePackage.getQuantity().setValue(1);	
		}
		
		// Calcula el valor del item
		bmoOrderSessionTypePackage.getAmount().setValue(bmoOrderSessionTypePackage.getPrice().toDouble() *
				bmoOrderSessionTypePackage.getQuantity().toDouble());
		
		// Si es nuevo, elimina los existentes
		if (!(bmoOrderSessionTypePackage.getId() > 0))
			deleteOrderSessionTypePackages(pmConn, bmoOrderSessionTypePackage.getOrderId().toInteger());

		super.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
		
		// Actualiza paquete en la venta
		bmoSessionSale.getSessionTypePackageId().setValue(bmoSessionTypePackage.getId());
		pmSessionSale.saveSimple(pmConn, bmoSessionSale, bmUpdateResult);
		
		// Lo ultimo que hace es almacenarlo
		super.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult create(PmConn pmConn, BmoOrderSessionTypePackage bmoOrderSessionTypePackage, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {	
		this.bmoOrderSessionTypePackage = bmoOrderSessionTypePackage;
		
		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		
		// Obten el paquetes
		PmSessionTypePackage pmSessionTypePackage = new PmSessionTypePackage(getSFParams());
		BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)pmSessionTypePackage.get(pmConn, bmoOrderSessionTypePackage.getSessionTypePackageId().toInteger());
		
		// Asigna cantidad y precio
		bmoOrderSessionTypePackage.getQuantity().setValue(1);
		bmoOrderSessionTypePackage.getPrice().setValue(bmoSessionTypePackage.getSalePrice().toDouble());
		
		// Calcula el valor del item
		bmoOrderSessionTypePackage.getAmount().setValue(bmoOrderSessionTypePackage.getPrice().toDouble() 
				* bmoOrderSessionTypePackage.getQuantity().toInteger());

		super.save(pmConn, bmoOrderSessionTypePackage, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}
	
	// Elimina paquetes anteriores asociados al Pedido
	private void deleteOrderSessionTypePackages(PmConn pmConn, int orderId) throws SFException {
		pmConn.doUpdate("DELETE FROM " + bmoOrderSessionTypePackage.getKind() + " WHERE orsp_orderid = " + orderId);
	}
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderSessionTypePackage.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
			
		}
		return bmUpdateResult;
	}
}
