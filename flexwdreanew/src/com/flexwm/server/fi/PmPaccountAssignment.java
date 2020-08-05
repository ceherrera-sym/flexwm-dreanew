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
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountAssignment;
import com.flexwm.shared.fi.BmoPaccountItem;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmPaccountAssignment extends PmObject {
	BmoPaccountAssignment bmoPaccountAssignment;

	public PmPaccountAssignment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPaccountAssignment = new BmoPaccountAssignment();
		setBmObject(bmoPaccountAssignment);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPaccountAssignment.getForeignPaccountId(), bmoPaccountAssignment.getBmoForeignPaccount()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getCurrencyId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoCurrency()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getUserId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoUser()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getBmoUser().getAreaId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoUser().getBmoArea()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getBmoUser().getLocationId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoUser().getBmoLocation()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getCompanyId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoCompany()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getPaccountTypeId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoPaccountType()),					
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getSupplierId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoSupplier()),
				new PmJoin(bmoPaccountAssignment.getBmoForeignPaccount().getBmoSupplier().getSupplierCategoryId(), bmoPaccountAssignment.getBmoForeignPaccount().getBmoSupplier().getBmoSupplierCategory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoPaccountAssignment = (BmoPaccountAssignment)autoPopulate(pmConn, new BmoPaccountAssignment());

		//BmoPaccount
		BmoPaccount bmoPaccount = new BmoPaccount();
		int paccountId = pmConn.getInt(bmoPaccount.getIdFieldName());
		if (paccountId > 0) bmoPaccountAssignment.setBmoForeignPaccount((BmoPaccount) new PmPaccount(getSFParams()).populate(pmConn));
		else bmoPaccountAssignment.setBmoForeignPaccount(bmoPaccount);

		return bmoPaccountAssignment;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoPaccountAssignment = (BmoPaccountAssignment)bmObject;

		// Obten la cuenta por pagar origen
		PmPaccount pmPaccount = new PmPaccount(getSFParams());		
		BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountAssignment.getPaccountId().toInteger());

		// Obten la cuenta por pagar destino
		BmoPaccount bmoForeignPaccount = new BmoPaccount();
		if (bmoPaccountAssignment.getForeignPaccountId().toInteger() > 0)		
			bmoForeignPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountAssignment.getForeignPaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos
		if (bmoPaccountAssignment.getId() > 0 && bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cuenta por Pagar - está Autorizada.");
		} else {

			if (!(bmoPaccountAssignment.getAmount().toDouble() > 0)) 
				bmUpdateResult.addError(bmoPaccountAssignment.getAmount().getName(), "El monto debe ser mayor a $ 0.00.");

			// Datos de la cuenta destino
			if (bmoPaccountAssignment.getForeignPaccountId().toInteger() > 0) {
				bmoPaccountAssignment.getInvoiceno().setValue(bmoForeignPaccount.getInvoiceno().toString());
				bmoPaccountAssignment.getCode().setValue(bmoForeignPaccount.getCode().toString());

				super.save(pmConn, bmoPaccountAssignment, bmUpdateResult);
			}

			// Actualizar los datos en el concepto de banco
			updateBankMovConcepts(pmConn, bmoPaccountAssignment, bmUpdateResult);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmoPaccountAssignment, bmUpdateResult);

			// Revisa que los montos no excedan
			checkAmounts(pmConn, bmoPaccount, bmoForeignPaccount, bmoPaccountAssignment, bmUpdateResult);

			// Recalcula el subtotal del ORIGEN de la aplicación
			pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);

			// Recalcula el subtotal del DESTINO de la aplicación
			if (bmoPaccountAssignment.getForeignPaccountId().toInteger() > 0)			
				pmPaccount.updateBalance(pmConn, bmoForeignPaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Revisar montos
	private void checkAmounts(PmConn pmConn, BmoPaccount bmoPaccount, BmoPaccount bmoForeignPaccount, BmoPaccountAssignment bmoPaccountAssignment , BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		// Primero valida que las asignaciones no sean mayores que la CxP
		double sumAssigned = 0;
		sql = " SELECT SUM(pass_amount) AS s FROM paccountassignments " +
				" WHERE pass_paccountid = " + bmoPaccount.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			sumAssigned = pmConn.getDouble("s");

		// Validar que la suma de aplicaciones de pagos no sea mayor al total de la CxP
		if (sumAssigned > bmoPaccount.getTotal().toDouble())
			bmUpdateResult.addMsg("La suma de Aplicaciones es Mayor al monto de la Nota de Crédito.");

		// Validar que la aplicacion no rebase el saldo de la cta por pagar destino
		if ((bmoForeignPaccount.getTotal().toDouble() - bmoForeignPaccount.getPayments().toDouble()) > 0) {
			double diff = (bmoForeignPaccount.getTotal().toDouble() - bmoForeignPaccount.getPayments().toDouble());	
			diff = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(diff));
			if (bmoPaccountAssignment.getAmount().toDouble() > diff)
				bmUpdateResult.addMsg("La Cantidad Aplicada rebasa el Saldo de la CxP Destino.");
		}	
	}

	// Actualiza conceptos de bancos
	private void updateBankMovConcepts(PmConn pmConn, BmoPaccountAssignment bmoPaccountAssignment , BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
	
		//Obtener el Item de la Cuenta por Pagar
		BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
		PmPaccountItem pmPaccountItem = new PmPaccountItem(getSFParams());
		bmoPaccountItem = (BmoPaccountItem)pmPaccountItem.getBy(pmConn, bmoPaccountAssignment.getPaccountId().toString(), bmoPaccountItem.getPaccountId().getName());
	
		sql = "SELECT * FROM bankmovconcepts WHERE bkmc_depositpaccountitemid = " + bmoPaccountItem.getId();
		pmConn.doFetch(sql);		
		if (pmConn.next()) {
			//Ligar la cuenta por pagar de cargo al concepto de bancos
			sql = " UPDATE bankmovconcepts SET bkmc_paccountid = " + bmoPaccountAssignment.getForeignPaccountId().toInteger() +
					" WHERE bkmc_depositpaccountitemid = " + bmoPaccountItem.getId();
			pmConn.doUpdate(sql);
		}	
	
	}

	// Valida que existan ya items en la cuenta por pagar
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccountAssignment = (BmoPaccountAssignment)bmObject;

		// Obtener la Cuenta por Pagar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountAssignment.getPaccountId().toInteger());
		BmoPaccount bmoForeignPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoPaccountAssignment.getForeignPaccountId().toInteger());

		// Si la CXP ya está autorizada, no se puede hacer movimientos			
		if (bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_AUTHORIZED) {				
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la cuenta por pagar - ya está Autorizada." );
		} else {
			//elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal del Origen
			pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);

			// Recalcula el subtotal del Destino
			pmPaccount.updateBalance(pmConn, bmoForeignPaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}
}
