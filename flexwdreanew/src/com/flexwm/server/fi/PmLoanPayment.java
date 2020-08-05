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

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.co.PmProperty;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoLoanPayment;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
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

public class PmLoanPayment extends PmObject{
	BmoLoanPayment bmoLoanPayment;


	public PmLoanPayment(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoLoanPayment = new BmoLoanPayment();
		setBmObject(bmoLoanPayment); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoLoanPayment.getPropertyId(), bmoLoanPayment.getBmoProperty()),
				new PmJoin(bmoLoanPayment.getBmoProperty().getDevelopmentBlockId(), bmoLoanPayment.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoLoanPayment.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoLoanPayment.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase())	
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoLoanPayment = (BmoLoanPayment) autoPopulate(pmConn, new BmoLoanPayment());

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoLoanPayment.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoLoanPayment.setBmoProperty(bmoProperty);

		return bmoLoanPayment;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoLoanPayment bmoLoanPayment = (BmoLoanPayment)bmObject;

		super.save(pmConn, bmoLoanPayment, bmUpdateResult);

		//Actualizar el total
		updateAmount(pmConn, bmObject, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoanPayment bmoLoanPayment = (BmoLoanPayment)bmObject;

		PmLoan pmLoan = new PmLoan(getSFParams());
		BmoLoan bmoLoan = (BmoLoan)pmLoan.get(pmConn, bmoLoanPayment.getLoanId().toInteger());

		//Actualizar el monto crédito 
		pmLoan.updateAmountPayment(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar Saldo Capital
		pmLoan.updatePaymentsLoan(pmConn, bmoLoan, bmUpdateResult);
	}

	//Obtener suma de los pagos
	public void sumLoanPayments(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoan bmoLoan = (BmoLoan)bmObject;

		double total = 0;
		String sql ="SELECT ROUND(SUM(lopa_amount), 2) FROM loanpayments  WHERE lopa_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);

		if (pmConn.next()) total = (pmConn.getDouble(1));

		// Calcular montos
		if (total == 0)
			bmoLoan.getCapitalPayment().setValue(0);			
		else
			bmoLoan.getCapitalPayment().setValue(total);
	}

	//Obtener suma de los pagos
	public double sumLoanPayments(PmConn pmConn, int loanId, BmUpdateResult bmUpdateResult) throws SFException {		
		double total = 0;
		String sql ="SELECT sum(lopa_amount) FROM loanpayments WHERE lopa_loanid = " + loanId;
		pmConn.doFetch(sql);
		if (pmConn.next()) total = (pmConn.getDouble(1));

		return total;		
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoLoanPayment = (BmoLoanPayment) bmObject;

		// Validaciones 
		//if (bmoLoanDisbursement.getBmoLoan().getStatus().toChar() == BmoLoan.STATUS_AUTHORIZED) {
		//bmUpdateResult.addMsg("No se puede Eliminar: El crédito está Autorizado.");
		//} else {						
		// Elimina 
		super.delete(pmConn, bmObject, bmUpdateResult);
		//}

		updateAmount(pmConn, bmoLoanPayment, bmUpdateResult);

		return bmUpdateResult;
	}
}

