/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoInvoice;
import com.flexwm.shared.fi.BmoInvoiceOrderDelivery;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmInvoiceOrderDelivery extends PmObject{
	BmoInvoiceOrderDelivery bmoInvoiceOrderDelivery;


	public PmInvoiceOrderDelivery(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoInvoiceOrderDelivery = new BmoInvoiceOrderDelivery();
		setBmObject(bmoInvoiceOrderDelivery); 

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return (BmoInvoiceOrderDelivery)autoPopulate(pmConn, new BmoInvoiceOrderDelivery());
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoInvoiceOrderDelivery bmoInvoiceOrderDelivery = (BmoInvoiceOrderDelivery)bmObject;

		super.save(pmConn, bmoInvoiceOrderDelivery, bmUpdateResult);

		PmInvoice pmInvoice = new PmInvoice(getSFParams());
		BmoInvoice bmoInvoice = (BmoInvoice)pmInvoice.get(pmConn, bmoInvoiceOrderDelivery.getInvoiceId().toInteger());

		//Actualizar la factura
		pmInvoice.updateBalance(pmConn, bmoInvoice, bmUpdateResult);

		return bmUpdateResult;
	}





	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoInvoiceOrderDelivery = (BmoInvoiceOrderDelivery) bmObject;

		PmInvoice pmInvoice = new PmInvoice(getSFParams());
		BmoInvoice bmoInvoice = (BmoInvoice)pmInvoice.get(pmConn, bmoInvoiceOrderDelivery.getInvoiceId().toInteger());

		if (bmoInvoice.getStatus().equals(BmoInvoice.STATUS_AUTHORIZED)) {
			bmUpdateResult.addError(bmoInvoice.getStatus().getName(), "No puede eliminar el item, La Factura esta autorizada");
		} else {
			// Elimina 
			super.delete(pmConn, bmObject, bmUpdateResult);

			//Actualizar la factura
			pmInvoice.updateBalance(pmConn, bmoInvoice, bmUpdateResult);
		}	


		return bmUpdateResult;
	}

}

