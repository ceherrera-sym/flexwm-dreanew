/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 *  
 * 
 */

package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountAssignment;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmRaccountAssignment extends PmObject {
	BmoRaccountAssignment bmoRaccountAssignment;
	// IMPORTATE: SI SE VA A MODIFICA ESTE ARCHIVO SE DEBE CAMBIAR EN PmRaccountAssignmentParent.java TAMBIEN, YA QUE ES UNA COPIA

	public PmRaccountAssignment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountAssignment = new BmoRaccountAssignment();
		setBmObject(bmoRaccountAssignment);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRaccountAssignment.getRaccountId(), bmoRaccountAssignment.getBmoForeignRaccount()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getPaymentTypeId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoPaymentType()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getCurrencyId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCurrency()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getCompanyId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCompany()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getRaccountTypeId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoRaccountType()),				
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getCustomerId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getSalesmanId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getAreaId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getLocationId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getTerritoryId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getReqPayTypeId(), bmoRaccountAssignment.getBmoForeignRaccount().getBmoCustomer().getBmoReqPayType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRaccountAssignment = (BmoRaccountAssignment)autoPopulate(pmConn, new BmoRaccountAssignment());

		//BmoRaccount
		BmoRaccount bmoRaccount = new BmoRaccount();
		int RaccountId = pmConn.getInt(bmoRaccount.getIdFieldName());
		if (RaccountId > 0) bmoRaccountAssignment.setBmoForeignRaccount((BmoRaccount) new PmRaccount(getSFParams()).populate(pmConn));
		else bmoRaccountAssignment.setBmoForeignRaccount(bmoRaccount);

		return bmoRaccountAssignment;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRaccountAssignment = (BmoRaccountAssignment)bmObject;

		// Obten la CxC Origen
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getRaccountId().toInteger());

		// Obten la cuenta por pagar destino
		BmoRaccount bmoForeignRaccount = new BmoRaccount();
		if (bmoRaccountAssignment.getForeignRaccountId().toInteger() > 0)		
			bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getForeignRaccountId().toInteger());

		// Si la CxC origen ya está autorizada, no se puede hacer movimientos
		if (bmoRaccountAssignment.getId() > 0 && bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la CxC Destino - está Autorizada.");
		} else {
			// Datos de la cuenta destino
			if (bmoRaccountAssignment.getForeignRaccountId().toInteger() > 0) {
				bmoRaccountAssignment.getInvoiceno().setValue(bmoForeignRaccount.getInvoiceno().toString());
				bmoRaccountAssignment.getCode().setValue(bmoForeignRaccount.getCode().toString());
				super.save(pmConn, bmObject, bmUpdateResult);
			}

			// Primero agrega el ultimo valor			
			super.save(pmConn, bmObject, bmUpdateResult);

			// Revisa que los montos no excedan
			this.checkAmounts(pmConn, bmoRaccount, bmoForeignRaccount, bmoRaccountAssignment, bmUpdateResult);

			// Recalcula el subtotal del ORIGEN de la aplicación						
			pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);

			// Recalcula el subtotal del DESTINO de la aplicación
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Revisa montos
	private void checkAmounts(PmConn pmConn, BmoRaccount bmoRaccount, BmoRaccount bmoForeignRaccount, BmoRaccountAssignment bmoRaccountAssignment , BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		// Primero valida que las asignaciones no sean mayores que la CxC
		double sumAssigned = 0;
		sql = " SELECT SUM(rass_amount) AS s FROM raccountassignments " +
				" WHERE rass_raccountid = " + bmoRaccount.getId();		
		pmConn.doFetch(sql); 
		if (pmConn.next()) 
			sumAssigned = pmConn.getDouble("s"); 

		// Validar que la suma de aplicaciones de pagos no sea mayor al amount del Raccount
		if (sumAssigned > bmoRaccount.getTotal().toDouble())
			bmUpdateResult.addMsg("La suma de Aplicaciones es Mayor al monto de la Nota de Crédito.");
		
		// Valida que no se pague mas de lo debido
		if ((bmoForeignRaccount.getBalance().toDouble() - bmoRaccountAssignment.getAmount().toDouble()) < 0)
			bmUpdateResult.addError(bmoRaccountAssignment.getAmount().getName(), "El Saldo de la CxC Destino no puede ser menor a 0.");
	}

	// Almacena devolucion
	public BmUpdateResult saveDevolutionCxC(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRaccountAssignment = (BmoRaccountAssignment)bmObject;

		// Obten la cuenta por pagar origen
		PmRaccount pmRaccount = new PmRaccount(getSFParams());		
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getRaccountId().toInteger());

		// Obten la cuenta por pagar destino
		BmoRaccount bmoForeignRaccount = new BmoRaccount();
		if (bmoRaccountAssignment.getForeignRaccountId().toInteger() > 0)		
			bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getForeignRaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos
		if (bmoRaccountAssignment.getId() > 0 && bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Pagar - está Autorizada.");
		} else {

			if (!(bmoRaccountAssignment.getAmount().toDouble() > 0)) 
				bmUpdateResult.addError(bmoRaccountAssignment.getAmount().getName(), "El monto debe ser mayor a $ 0.00.");


			// Datos de la cuenta destino
			if (bmoRaccountAssignment.getForeignRaccountId().toInteger() > 0) {

				bmoRaccountAssignment.getInvoiceno().setValue(bmoForeignRaccount.getInvoiceno().toString());
				bmoRaccountAssignment.getCode().setValue(bmoForeignRaccount.getCode().toString());

				super.save(pmConn, bmoRaccount, bmUpdateResult);
			}

			//Actualizar los datos en el concepto de banco
			//updateBankMovConcepts(pmConn, bmoRaccountAssignment, bmUpdateResult);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmoRaccountAssignment, bmUpdateResult);

			// Recalcula el subtotal del DESTINO de la aplicación
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);

			// Recalcula el subtotal del ORIGEN de la aplicación
			if (bmoRaccountAssignment.getForeignRaccountId().toInteger() > 0)			
				pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRaccountAssignment = (BmoRaccountAssignment)bmObject;

		// Obtener la Cuenta por Cobrar
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getRaccountId().toInteger());

		BmoRaccount bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignment.getForeignRaccountId().toInteger());

		//elimina el item
		super.delete(pmConn, bmObject, bmUpdateResult);

		// Recalcula el subtotal del Origen
		pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);

		// Recalcula el subtotal del Destino
		pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);

		return bmUpdateResult;
	}

}
