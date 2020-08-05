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

import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoLoanDisbursement;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmLoanDisbursement extends PmObject{
	BmoLoanDisbursement bmoLoanDisbursement;


	public PmLoanDisbursement(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoLoanDisbursement = new BmoLoanDisbursement();
		setBmObject(bmoLoanDisbursement); 

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return (BmoLoanDisbursement)autoPopulate(pmConn, new BmoLoanDisbursement());
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoLoanDisbursement bmoLoanDisbursement = (BmoLoanDisbursement)bmObject;

		/*// En cada disposicion deja un Balance-Saldo Ministrado
		PmLoan pmLoan = new PmLoan(getSFParams());
		double balanceTotal = pmLoan.sumLoanDisbursements(pmConn, bmoLoanDisbursement.getLoanId().toInteger(), bmUpdateResult);
		bmoLoanDisbursement.getBalance().setValue(balanceTotal + bmoLoanDisbursement.getAmount().toDouble());
		 */

		super.save(pmConn, bmoLoanDisbursement, bmUpdateResult);

		//Actualizar el total
		updateAmount(pmConn, bmObject, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoanDisbursement bmoLoanDisbursement = (BmoLoanDisbursement)bmObject;

		PmLoan pmLoan = new PmLoan(getSFParams());
		BmoLoan bmoLoan = (BmoLoan)pmLoan.get(pmConn, bmoLoanDisbursement.getLoanId().toInteger());

		// Actualizar el monto crédito 
		pmLoan.updateAmount(pmConn, bmoLoan, bmUpdateResult);
		
		// Actualizar pagos (Actualizar el Avance y Actualizar Saldo por Disponer)
		pmLoan.updateAmountPayment(pmConn, bmoLoan, bmUpdateResult);

		// Actualizar el Avance
//		pmLoan.updateProgressLoan(pmConn, bmoLoan, bmUpdateResult);

		// Actualizar Saldo por Disponer
//		pmLoan.updateDisbursedLoan(pmConn, bmoLoan, bmUpdateResult);
	}

	//Obtener suma de las disposiciones
	public void sumLoanDisbursements(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoan bmoLoan = (BmoLoan)bmObject;

		double total = 0;
		String sql ="SELECT ROUND(SUM(lodi_amount), 2) FROM loandisbursements  WHERE lodi_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);

		if (pmConn.next()) total = (pmConn.getDouble(1));

		// Calcular montos
		if (total == 0)
			bmoLoan.getBalance().setValue(0);			
		else
			bmoLoan.getBalance().setValue(total);
	}

	//Obtener suma de las disposiciones
	public double sumLoanDisbursements(PmConn pmConn, int loanId, BmUpdateResult bmUpdateResult) throws SFException {		
		double total = 0;
		String sql ="SELECT sum(lodi_amount) FROM loandisbursements WHERE lodi_loanid = " + loanId;
		pmConn.doFetch(sql);
		if (pmConn.next()) total = (pmConn.getDouble(1));

		return total;		
	}

	// Obtener suma de Progreso de las disposiciones
	public double sumProgressLoanDisbursement(PmConn pmConn, BmoLoan bmoLoan) throws SFPmException {
		double sumProgressLodi = 0;
		String sql = "SELECT SUM(lodi_progress) AS sumProgressLodi FROM loandisbursements " +
				" WHERE lodi_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);
		if (pmConn.next())
			sumProgressLodi = Math.round(pmConn.getDouble("sumProgressLodi")*100)/100.0d;

		return sumProgressLodi;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoLoanDisbursement = (BmoLoanDisbursement) bmObject;
		
		PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
		bmoLoanDisbursement = (BmoLoanDisbursement) pmLoanDisbursement.get(pmConn, bmoLoanDisbursement.getId());
		
		
		if(bmoLoanDisbursement.getBankMovConceptId().toInteger() > 0) {
			bmUpdateResult.addMsg("No se puede Eliminar: La disposición tiene un Movimiento de banco");
		}
		else {
			super.delete(pmConn, bmObject, bmUpdateResult);
			updateAmount(pmConn, bmoLoanDisbursement, bmUpdateResult);
		}

		
		return bmUpdateResult;
	}

}

