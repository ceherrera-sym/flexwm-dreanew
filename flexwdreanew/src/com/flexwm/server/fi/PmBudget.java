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
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author jhernandez
 *
 */

public class PmBudget extends PmObject{
	BmoBudget bmoBudget;


	public PmBudget(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoBudget = new BmoBudget();
		setBmObject(bmoBudget);
		
		// Lista de joins
				setJoinList(new ArrayList<PmJoin>(Arrays.asList(
						new PmJoin(bmoBudget.getCurrencyId(), bmoBudget.getBmoCurrency())
						)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de presupuestos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "( budg_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " budg_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {		
		bmoBudget = (BmoBudget)autoPopulate(pmConn, new BmoBudget());
		
		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0)
			bmoBudget.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoBudget.setBmoCurrency(bmoCurrency);

		return bmoBudget;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoBudget bmoBudget = (BmoBudget)bmObject;
		

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoBudget.getId() > 0)) {

			bmoBudget.getStatus().setValue(BmoBudget.STATUS_REVISION);

			super.save(pmConn, bmoBudget, bmUpdateResult);

			//Obtener el Id
			bmoBudget.setId(bmUpdateResult.getId());
		

		}


		super.save(pmConn, bmoBudget, bmUpdateResult);

		this.updateBalance(pmConn, bmoBudget, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBudget bmoBudget = (BmoBudget)bmObject;

		//Sumar los item de los conceptos
		sumBudgetItems(pmConn, bmoBudget, bmUpdateResult);

		super.save(pmConn, bmoBudget, bmUpdateResult);

	}

	public void updateBalance(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBudget bmoBudget = (BmoBudget)bmObject;		

		sumBudgetItems(pmConn, bmoBudget, bmUpdateResult);

		super.save(pmConn, bmoBudget, bmUpdateResult);

	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {		
		double result = 0;
		bmoBudget = (BmoBudget)bmObject;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			result = sumBudgetItems(pmConn, Integer.parseInt(value), bmUpdateResult);		
			bmUpdateResult.setMsg("" + result);
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "-action() " + e.toString());
		} finally {
			pmConn.close();			
		}

		return bmUpdateResult;
	}

	public void sumBudgetItems(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoBudget = (BmoBudget)bmObject;

		double total = 0, payments = 0;// provisioned = 0;
		double totalWithdraw = 0, paymentWithdraw = 0, provisionedWithdraw = 0;
		double totalDeposit = 0, paymentDeposit = 0, provisionedDeposit = 0;

		String sql = "";
		sql = " SELECT sum(bgit_payments) FROM budgetitems " +				       
				" WHERE bgit_budgetid = " + bmoBudget.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) payments = pmConn.getDouble(1);

//		sql = " SELECT sum(bgit_provisioned) FROM budgetitems " +				       
//				" WHERE bgit_budgetid = " + bmoBudget.getId();
//		pmConn.doFetch(sql);
//		if (pmConn.next()) provisioned = pmConn.getDouble(1);

		sql = " SELECT sum(bgit_amount) FROM budgetitems " +				       
				" WHERE bgit_budgetid = " + bmoBudget.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) total = pmConn.getDouble(1);

		//Egresos
		sql = " SELECT sum(bgit_provisioned) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) provisionedWithdraw = pmConn.getDouble(1);

		sql = " SELECT sum(bgit_payments) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) paymentWithdraw = pmConn.getDouble(1);

		sql = " SELECT sum(bgit_amount) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) totalWithdraw = pmConn.getDouble(1);

		//Ingresos
		sql = " SELECT sum(bgit_provisioned) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_DEPOSIT + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) provisionedDeposit = pmConn.getDouble(1);

		sql = " SELECT sum(bgit_payments) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_DEPOSIT + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) paymentDeposit = pmConn.getDouble(1);

		sql = " SELECT sum(bgit_amount) FROM budgetitems " +
				" LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" WHERE bgit_budgetid = " + bmoBudget.getId() +
				" AND bgty_type = '" + BmoBudgetItemType.TYPE_DEPOSIT + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) totalDeposit = pmConn.getDouble(1);

		// Calcular montos
		if (total == 0) {
			bmoBudget.getProvisioned().setValue(0);
			bmoBudget.getPayments().setValue(0);
			bmoBudget.getTotal().setValue(0);	
			bmoBudget.getBalance().setValue(total-payments);

			//Egresos
			bmoBudget.getProvisionedWithdraw().setValue(0);
			bmoBudget.getPaymentWithdraw().setValue(0);
			bmoBudget.getTotalWithdraw().setValue(0);
			bmoBudget.getBalanceWithdraw().setValue(totalWithdraw - paymentWithdraw);

			//Ingresos
			bmoBudget.getProvisionedWithdraw().setValue(0);
			bmoBudget.getPaymentWithdraw().setValue(0);
			bmoBudget.getTotalWithdraw().setValue(0);
			bmoBudget.getBalanceWithdraw().setValue(totalDeposit - paymentDeposit);

		} else {

			//Egresos
			bmoBudget.getProvisionedWithdraw().setValue(SFServerUtil.roundCurrencyDecimals(provisionedWithdraw));
			bmoBudget.getPaymentWithdraw().setValue(SFServerUtil.roundCurrencyDecimals(paymentWithdraw));
			bmoBudget.getTotalWithdraw().setValue(SFServerUtil.roundCurrencyDecimals(totalWithdraw));
			bmoBudget.getBalanceWithdraw().setValue(SFServerUtil.roundCurrencyDecimals(totalWithdraw - paymentWithdraw));

			//Ingresos
			bmoBudget.getProvisionedDeposit().setValue(SFServerUtil.roundCurrencyDecimals(provisionedDeposit));
			bmoBudget.getPaymentDeposit().setValue(SFServerUtil.roundCurrencyDecimals(paymentDeposit));
			bmoBudget.getTotalDeposit().setValue(SFServerUtil.roundCurrencyDecimals(totalDeposit));
			bmoBudget.getBalanceDeposit().setValue(SFServerUtil.roundCurrencyDecimals(totalDeposit - paymentDeposit));

			//Balances
			bmoBudget.getProvisioned().setValue(SFServerUtil.roundCurrencyDecimals(provisionedDeposit - provisionedWithdraw));
			bmoBudget.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(paymentDeposit - paymentWithdraw));
			bmoBudget.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(totalDeposit - totalWithdraw));
			bmoBudget.getBalance().setValue(SFServerUtil.roundCurrencyDecimals((totalDeposit - paymentDeposit) - (totalWithdraw - paymentWithdraw)));
		}

		super.save(pmConn, bmoBudget, bmUpdateResult);

	}

	public double sumBudgetItems(PmConn pmConn, int budgetId, BmUpdateResult bmUpdateResult) throws SFException {		

		double total = 0;

		pmConn.doFetch("SELECT sum(bgit_amount) FROM budgetitems "
				+ " WHERE bgit_budgetid = " + budgetId);

		if (pmConn.next()) total = pmConn.getDouble(1);

		return total;


	}


}

