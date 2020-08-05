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
import com.flexwm.server.co.PmDevelopmentPhase;
import com.flexwm.server.op.PmSupplier;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author smuniz
 *
 */

public class PmLoan extends PmObject{
	BmoLoan bmoLoan;

	public PmLoan(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoLoan = new BmoLoan();
		setBmObject(bmoLoan); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoLoan.getSupplierId(), bmoLoan.getBmoSupplier()),
				new PmJoin(bmoLoan.getBmoSupplier().getSupplierCategoryId(), bmoLoan.getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoLoan.getDevelopmentPhaseId(), bmoLoan.getBmoDevelopmentPhase())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de creditos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "( loan_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " loan_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {		
		bmoLoan = (BmoLoan) autoPopulate(pmConn, new BmoLoan());

		// BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		int supplierId = (int)pmConn.getInt(bmoSupplier.getIdFieldName());
		if (supplierId > 0) bmoLoan.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else bmoLoan.setBmoSupplier(bmoSupplier);

		// BmoDevelopmentPhase
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		int developmentPhaseId = (int)pmConn.getInt(bmoDevelopmentPhase.getIdFieldName());
		if (developmentPhaseId > 0) bmoLoan.setBmoDevelopmentPhase((BmoDevelopmentPhase) new PmDevelopmentPhase(getSFParams()).populate(pmConn));
		else bmoLoan.setBmoDevelopmentPhase(bmoDevelopmentPhase);

		return bmoLoan;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoLoan bmoLoan = (BmoLoan)bmObject;
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoLoan.getId() > 0)) {
			super.save(pmConn, bmoLoan, bmUpdateResult);
			bmoLoan.setId(bmUpdateResult.getId());
			bmoLoan.getCode().setValue(bmoLoan.getCodeFormat());
			bmoLoan.getDisbursed().setValue(bmoLoan.getAmount().toDouble() - bmoLoan.getCommissionPayment().toDouble() - bmoLoan.getBalance().toDouble());
		}

		super.save(pmConn, bmoLoan, bmUpdateResult);

		this.updateAmount(pmConn, bmoLoan, bmUpdateResult);
		this.updateAmountPayment(pmConn, bmoLoan, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoan bmoLoan= (BmoLoan)bmObject;
		PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());

		//Sumar las disposiciones del credito
		pmLoanDisbursement.sumLoanDisbursements(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar el Avance
		this.updateProgressLoan(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar Saldo por Disponer
		this.updateDisbursedLoan(pmConn, bmoLoan, bmUpdateResult);

		super.save(pmConn, bmoLoan, bmUpdateResult);
	}

	public void updateAmountPayment(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoLoan bmoLoan= (BmoLoan)bmObject;
		PmLoanPayment pmLoanPayment = new PmLoanPayment(getSFParams());

		//Sumar los pagos del credito
		pmLoanPayment.sumLoanPayments(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar la suma de los pagos al credito
		super.save(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar Monto Dispuesto
		this.updatePaymentsLoan(pmConn, bmoLoan, bmUpdateResult);

		//Actualizar Saldo Capital
		this.updateCapitalBalanceLoan(pmConn, bmoLoan, bmUpdateResult);

		// Solo en créditos revolventes toma Saldo Capital ((anticipos+ministraciones)-pago capital)
		if(bmoLoan.getRevolving().toBoolean()){
			bmoLoan.getDisbursed().setValue(bmoLoan.getCapitalBalance().toDouble());
		}

		super.save(pmConn, bmoLoan, bmUpdateResult);
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {		
		double result = 0;
		bmoLoan = (BmoLoan)bmObject;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			if (data.equals(BmoLoan.ACTION_TOTAL)) {
				PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
				result = pmLoanDisbursement.sumLoanDisbursements(pmConn, Integer.parseInt(value), bmUpdateResult);		

			} else if (data.equals(BmoLoan.ACTION_PAYMENTS)) {
				PmLoanPayment pmLoanPayment = new PmLoanPayment(getSFParams());
				result = pmLoanPayment.sumLoanPayments(pmConn, Integer.parseInt(value), bmUpdateResult);
			}
			bmUpdateResult.setMsg("" + result);
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "-action() " + e.toString());
		} finally {
			pmConn.close();			
		}

		return bmUpdateResult;
	}


	public void updateProgressLoan(PmConn pmConn, BmoLoan bmoLoan, BmUpdateResult bmUpdateResult) throws SFException {
		PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
		double sumProgressLodi = pmLoanDisbursement.sumProgressLoanDisbursement(pmConn, bmoLoan);
		bmoLoan.getProgress().setValue(sumProgressLodi);
		super.save(pmConn, bmoLoan, bmUpdateResult);
	}

	public void updateDisbursedLoan(PmConn pmConn, BmoLoan bmoLoan, BmUpdateResult bmUpdateResult) throws SFException {
		double disbursed = availableBalance(pmConn, bmoLoan);
		bmoLoan.getDisbursed().setValue(disbursed);
		super.save(pmConn, bmoLoan, bmUpdateResult);
	}


	// Obtener Saldo por disponer
	private double availableBalance(PmConn pmConn, BmoLoan bmoLoan) throws SFPmException {
		double saldoPorDisponer = 0;
		String sql = "SELECT ROUND((loan_amount - loan_commissionpayment - loan_balance), 2) as saldoPorDisponer " +
				" FROM loans WHERE loan_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) saldoPorDisponer = pmConn.getDouble("saldoPorDisponer");			

		return saldoPorDisponer;
	}

	// Obtener Monto dispuesto
	public void updatePaymentsLoan(PmConn pmConn, BmoLoan bmoLoan, BmUpdateResult bmUpdateResult) throws SFException {
		double disbursedAmount = disbursedAmount(pmConn, bmoLoan);
		bmoLoan.getDisbursedAmount().setValue(disbursedAmount);
		super.save(pmConn, bmoLoan, bmUpdateResult);
	}

	// Obtener Monto dispuesto
	private double disbursedAmount(PmConn pmConn, BmoLoan bmoLoan) throws SFPmException {
		double disbursedAmount= 0;
		String sql = "SELECT ROUND(((loan_commissionpayment + loan_balance) - loan_capitalpayment), 2) as disbursedAmount " +
				" FROM loans WHERE loan_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) disbursedAmount = pmConn.getDouble("disbursedAmount");			

		return disbursedAmount;
	}

	// Obtener Saldo Capital
	public void updateCapitalBalanceLoan(PmConn pmConn, BmoLoan bmoLoan, BmUpdateResult bmUpdateResult) throws SFException {
		double capitalBalance = updateCapitalBalanceLoan(pmConn, bmoLoan);
		bmoLoan.getCapitalBalance().setValue(capitalBalance);
		super.save(pmConn, bmoLoan, bmUpdateResult);
	}

	// Obtener Saldo Capital
	private double updateCapitalBalanceLoan(PmConn pmConn, BmoLoan bmoLoan) throws SFPmException {
		double capitalBalance= 0;
		String sql = "SELECT ROUND((loan_amount - loan_disbursedamount), 2) as capitalBalance " +
				" FROM loans WHERE loan_loanid = " + bmoLoan.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) capitalBalance = pmConn.getDouble("capitalBalance");			

		return capitalBalance;
	}
}