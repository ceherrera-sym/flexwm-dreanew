/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cr;

import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.cr.BmoOrderCredit;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmOrderCredit extends PmObject {
	BmoOrderCredit bmoOrderCredit;

	public PmOrderCredit(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderCredit = new BmoOrderCredit();
		setBmObject(bmoOrderCredit);

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoOrderCredit());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoOrderCredit = (BmoOrderCredit)bmObject;



		if (!(bmoOrderCredit.getId() > 0)) {
			super.save(pmConn, bmoOrderCredit, bmUpdateResult);

		}

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderCredit.getOrderId().toInteger());
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		/*		
		// Obten el Credito
		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCredit = new BmoCredit();
		bmoCredit = (BmoCredit)pmCredit.getBy(pmConn, bmoOrderCredit.getOrderId().toInteger(), bmoCredit.getOrderId().getName());

		//Obtener el Plazo
		PmTerm pmTerm = new PmTerm(getSFParams());
		BmoTerm bmoTerm = (BmoTerm)pmTerm.get(pmConn, bmoCredit.getTermId().toInteger());

		// Asigna valores
		bmoOrderCredit.getName().setValue(bmoTerm.getName().toString());
		bmoOrderCredit.getDescription().setValue(bmoTerm.getDescription().toString());

		// Si no estan asignados los valores default los asigna
		if (!(bmoOrderCredit.getPrice().toDouble() > 0)) {
			bmoOrderCredit.getPrice().setValue(bmoCredit.getAmount().toDouble());			
			bmoOrderCredit.getInterest().setValue(bmoCredit.getAmount().toDouble() * (bmoTerm.getInterest().toDouble() / 100));
			bmoOrderCredit.getQuantity().setValue(1);
		}

		// Calcula el valor del item
		bmoOrderCredit.getAmount().setValue((bmoOrderCredit.getPrice().toDouble() + bmoOrderCredit.getInterest().toDouble()) *
				bmoOrderCredit.getQuantity().toDouble());

		// Si es nuevo, elimina los existentes
		if (!(bmoOrderCredit.getId() > 0))
			deleteOrderCredits(pmConn, bmoOrderCredit.getOrderId().toInteger());
		 */


		// Recalcula el subtotal
		//pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		// Actualiza paquete en la venta
		//bmoSessionSale.getSessionTypePackageId().setValue(bmoSessionTypePackage.getId());
		//pmSessionSale.saveSimple(pmConn, bmoSessionSale, bmUpdateResult);

		// Lo ultimo que hace es almacenarlo
		super.save(pmConn, bmoOrderCredit, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult create(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {

		//Crea la linea de Credito
		PmOrderCredit pmOrderCredit = new PmOrderCredit(getSFParams());
		BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();

		//Obtener el Plazo
		PmCreditType pmTerm = new PmCreditType(getSFParams());
		BmoCreditType bmoTerm = (BmoCreditType)pmTerm.get(pmConn, bmoCredit.getCreditTypeId().toInteger());

		// Asigna valores
		bmoOrderCredit.getOrderId().setValue(bmoCredit.getOrderId().toInteger());
		bmoOrderCredit.getName().setValue(bmoTerm.getName().toString());
		bmoOrderCredit.getDescription().setValue(bmoTerm.getDescription().toString());

		// Si no estan asignados los valores default los asigna
		if (!(bmoOrderCredit.getPrice().toDouble() > 0)) {
			bmoOrderCredit.getPrice().setValue(bmoCredit.getAmount().toDouble());			
			bmoOrderCredit.getInterest().setValue(bmoCredit.getAmount().toDouble() * (bmoTerm.getInterest().toDouble() / 100));
			bmoOrderCredit.getQuantity().setValue(1);
		}

		// Calcula el valor del item
		bmoOrderCredit.getAmount().setValue((bmoOrderCredit.getPrice().toDouble() + bmoOrderCredit.getInterest().toDouble()) *
				bmoOrderCredit.getQuantity().toDouble());

		pmOrderCredit.save(pmConn, bmoOrderCredit, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult changeOrderCredit(PmConn pmConn, BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {

		//Crea la linea de Credito
		PmOrderCredit pmOrderCredit = new PmOrderCredit(getSFParams());
		BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();		
		bmoOrderCredit = (BmoOrderCredit)pmOrderCredit.getBy(pmConn, bmoCredit.getOrderId().toInteger(), bmoOrderCredit.getOrderId().getName());

		//Obtener el Plazo
		PmCreditType pmTerm = new PmCreditType(getSFParams());
		BmoCreditType bmoTerm = (BmoCreditType)pmTerm.get(pmConn, bmoCredit.getCreditTypeId().toInteger());

		//bmoOrderCredit.getName().setValue(bmoCredit.getBmoCreditType().getName().toString());
		bmoOrderCredit.getPrice().setValue(bmoCredit.getAmount().toDouble());			
		bmoOrderCredit.getInterest().setValue(bmoCredit.getAmount().toDouble() * (bmoTerm.getInterest().toDouble() / 100));
		bmoOrderCredit.getQuantity().setValue(1);
		bmoOrderCredit.getName().setValue(bmoTerm.getName().toString());
		bmoOrderCredit.getDescription().setValue(bmoTerm.getDescription().toString());
		

		// Calcula el valor del item
		bmoOrderCredit.getAmount().setValue((bmoOrderCredit.getPrice().toDouble() + bmoOrderCredit.getInterest().toDouble()) *
				bmoOrderCredit.getQuantity().toDouble());

		pmOrderCredit.save(pmConn, bmoOrderCredit, bmUpdateResult);


		return bmUpdateResult;
	}

	// Elimina paquetes anteriores asociados al Pedido
//	private void deleteOrderCredits(PmConn pmConn, int orderId) throws SFException {
//		pmConn.doUpdate("DELETE FROM " + bmoOrderCredit.getKind() + " WHERE orsp_orderid = " + orderId);
//	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderCredit = (BmoOrderCredit)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderCredit.getOrderId().toInteger());

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
