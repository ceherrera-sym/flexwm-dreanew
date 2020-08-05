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
import com.flexwm.shared.fi.BmoRaccountAssignmentParent;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmRaccountAssignmentParent extends PmObject {
	BmoRaccountAssignmentParent bmoRaccountAssignmentParent;
	// IMPORTATE: SI SE VA A MODIFICA ESTE ARCHIVO SE DEBE CAMBIAR EN PmRaccountAssignment.java TAMBIEN
	// Este archivo es para crear un join con diferente destino, ya que no se pueden agregar dos join de una misma tabla
	
	public PmRaccountAssignmentParent(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountAssignmentParent = new BmoRaccountAssignmentParent();
		setBmObject(bmoRaccountAssignmentParent);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRaccountAssignmentParent.getForeignRaccountId(), bmoRaccountAssignmentParent.getBmoForeignRaccount()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getPaymentTypeId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoPaymentType()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getCurrencyId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCurrency()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getCompanyId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCompany()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getRaccountTypeId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoRaccountType()),				
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getCustomerId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getSalesmanId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getAreaId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getLocationId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getTerritoryId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getReqPayTypeId(), bmoRaccountAssignmentParent.getBmoForeignRaccount().getBmoCustomer().getBmoReqPayType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRaccountAssignmentParent= (BmoRaccountAssignmentParent)autoPopulate(pmConn, new BmoRaccountAssignmentParent());

		//BmoRaccount
		BmoRaccount bmoRaccount = new BmoRaccount();
		int RaccountId = pmConn.getInt(bmoRaccount.getIdFieldName());
		if (RaccountId > 0) bmoRaccountAssignmentParent.setBmoForeignRaccount((BmoRaccount) new PmRaccount(getSFParams()).populate(pmConn));
		else bmoRaccountAssignmentParent.setBmoForeignRaccount(bmoRaccount);

		return bmoRaccountAssignmentParent;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)bmObject;
//
		// Obten la CxC Origen
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getRaccountId().toInteger());

		// Obten la cuenta por pagar destino
		BmoRaccount bmoForeignRaccount = new BmoRaccount();
		if (bmoRaccountAssignmentParent.getForeignRaccountId().toInteger() > 0)		
			bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getForeignRaccountId().toInteger());

		// Si la CxC origen ya está autorizada, no se puede hacer movimientos
		if (bmoRaccountAssignmentParent.getId() > 0 && bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la CxC Destino - está Autorizada.");
		} else {
			// Datos de la cuenta destino
			if (bmoRaccountAssignmentParent.getForeignRaccountId().toInteger() > 0) {
				bmoRaccountAssignmentParent.getInvoiceno().setValue(bmoForeignRaccount.getInvoiceno().toString());
				bmoRaccountAssignmentParent.getCode().setValue(bmoForeignRaccount.getCode().toString());
				super.save(pmConn, bmObject, bmUpdateResult);
			}

			// Primero agrega el ultimo valor			
			super.save(pmConn, bmObject, bmUpdateResult);

			// Revisa que los montos no excedan
			this.checkAmounts(pmConn, bmoRaccount, bmoForeignRaccount, bmoRaccountAssignmentParent, bmUpdateResult);

			// Recalcula el subtotal del ORIGEN de la aplicación						
			pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);

			// Recalcula el subtotal del DESTINO de la aplicación
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Revisa montos
	private void checkAmounts(PmConn pmConn, BmoRaccount bmoRaccount, BmoRaccount bmoForeignRaccount, BmoRaccountAssignmentParent bmoRaccountAssignmentParent , BmUpdateResult bmUpdateResult) throws SFException {
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
		if ((bmoForeignRaccount.getBalance().toDouble() - bmoRaccountAssignmentParent.getAmount().toDouble()) < 0)
			bmUpdateResult.addError(bmoRaccountAssignmentParent.getAmount().getName(), "El Saldo de la CxC Destino no puede ser menor a 0.");
	}

	// Almacena devolucion
	public BmUpdateResult saveDevolutionCxC(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)bmObject;

		// Obten la cuenta por pagar origen
		PmRaccount pmRaccount = new PmRaccount(getSFParams());		
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getRaccountId().toInteger());

		// Obten la cuenta por pagar destino
		BmoRaccount bmoForeignRaccount = new BmoRaccount();
		if (bmoRaccountAssignmentParent.getForeignRaccountId().toInteger() > 0)		
			bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getForeignRaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos
		if (bmoRaccountAssignmentParent.getId() > 0 && bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Pagar - está Autorizada.");
		} else {

			if (!(bmoRaccountAssignmentParent.getAmount().toDouble() > 0)) 
				bmUpdateResult.addError(bmoRaccountAssignmentParent.getAmount().getName(), "El monto debe ser mayor a $ 0.00.");


			// Datos de la cuenta destino
			if (bmoRaccountAssignmentParent.getForeignRaccountId().toInteger() > 0) {

				bmoRaccountAssignmentParent.getInvoiceno().setValue(bmoForeignRaccount.getInvoiceno().toString());
				bmoRaccountAssignmentParent.getCode().setValue(bmoForeignRaccount.getCode().toString());

				super.save(pmConn, bmoRaccount, bmUpdateResult);
			}

			//Actualizar los datos en el concepto de banco
			//updateBankMovConcepts(pmConn, bmoRaccountAssignmentParent, bmUpdateResult);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmoRaccountAssignmentParent, bmUpdateResult);

			// Recalcula el subtotal del DESTINO de la aplicación
			pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);

			// Recalcula el subtotal del ORIGEN de la aplicación
			if (bmoRaccountAssignmentParent.getForeignRaccountId().toInteger() > 0)			
				pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)bmObject;

		// Obtener la Cuenta por Cobrar
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getRaccountId().toInteger());

		BmoRaccount bmoForeignRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoRaccountAssignmentParent.getForeignRaccountId().toInteger());

		//elimina el item
		super.delete(pmConn, bmObject, bmUpdateResult);

		// Recalcula el subtotal del Origen
		pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);

		// Recalcula el subtotal del Destino
		pmRaccount.updateBalance(pmConn, bmoForeignRaccount, bmUpdateResult);

		return bmUpdateResult;
	}

}
