/**
 * 
 */
package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankAccountType;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
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

public class PmBankAccount extends PmObject {
	BmoBankAccount bmoBankAccount;

	public PmBankAccount(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBankAccount = new BmoBankAccount();
		setBmObject(bmoBankAccount);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(new PmJoin(bmoBankAccount.getCompanyId(), bmoBankAccount.getBmoCompany()),
				new PmJoin(bmoBankAccount.getCurrencyId(),bmoBankAccount.getBmoCurrency()),
				new	PmJoin(bmoBankAccount.getBankAccountTypeId(), bmoBankAccount.getBmoBankAccountType()))));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asginacion de cuentas de bancos
		if (getSFParams().restrictData(bmoBankAccount.getProgramCode())) {

			filters = "( bkac_responsibleid in (" +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" or user_userid in ( " +
					" 	select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" ) ";
		}

		// Filtro de cuentas de banco de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( bkac_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " bkac_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoBankAccount bmoBankAccount = (BmoBankAccount) autoPopulate(pmConn, new BmoBankAccount());

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoBankAccount.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoBankAccount.setBmoCompany(bmoCompany);

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoBankAccount.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoBankAccount.setBmoCurrency(bmoCurrency);
		
		//BmoBankAccount
		BmoBankAccountType bmoBankAccountType = new BmoBankAccountType();
		int bankAccountTypeId = pmConn.getInt(bmoBankAccountType.getIdFieldName());
		if (bankAccountTypeId > 0) bmoBankAccount.setBmoBankAccountType((BmoBankAccountType) new PmBankAccountType(getSFParams()).populate(pmConn));
		else bmoBankAccount.setBmoBankAccountType(bmoBankAccountType);

		return bmoBankAccount;	
	}

	//Calcular el Saldo
	public void updateBalance(PmConn pmConn, BmoBankAccount bmoBankAccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double sumDeposit = 0, sumWithdraw = 0;

		//Obtener los Abonos
		sql = "SELECT SUM(bkmv_deposit) AS deposit FROM bankmovements " +
				"WHERE bkmv_bankaccountid = " + bmoBankAccount.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) sumDeposit = pmConn.getDouble(1);


		//Obtener los Cargos		
		sql = "SELECT SUM(bkmv_withdraw) AS withdraw FROM bankmovements " +
				"WHERE bkmv_bankaccountid = " + bmoBankAccount.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) sumWithdraw = pmConn.getDouble(1);


		bmoBankAccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(sumDeposit - sumWithdraw));

		super.save(pmConn, bmoBankAccount, bmUpdateResult);

	}

	// Comparar sumatoria de cargo/abono vs saldo cuenta de banco
	public void batchUpdateBalance() throws SFException {

		BmoBankAccount bmoBankAccount = new BmoBankAccount();
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		PmConn pmConnSQL = new PmConn(getSFParams());
		pmConnSQL.open();
		PmConn pmConnSQL2 = new PmConn(getSFParams());
		pmConnSQL2.open();
		String sql = "", list = "";
		boolean bankAccountIsOk = true;
		double balanceSumAmount = 0, balanceBkca = 0, sumWithdraw = 0, sumDeposit = 0;

		try {
			System.out.println("Revisión de Saldo de Cuentas de Banco - " 
					+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat() + "\n"));

			// Cuentas de banco
			pmConnSQL.doFetch("SELECT bkac_bankaccountid FROM " + formatKind("bankaccounts" + " ORDER BY bkac_bankaccountid ASC;"));
			while (pmConnSQL.next()) {

				// Obtener Cuenta de Banco
				bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConnSQL.getInt("bkac_bankaccountid"));
				balanceBkca = bmoBankAccount.getBalance().toDouble();

				// Obtener Suma de Cargos		
				sql = " SELECT SUM(bkmv_withdraw) AS sumWithdraw "
						+ " FROM " + formatKind("bankmovements")
						+ " LEFT JOIN " + formatKind("bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
						+ " LEFT JOIN " + formatKind("bankmovtypes") + " ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) "
						+ " WHERE bkac_bankaccountid = " + bmoBankAccount.getId()
						+ " AND  bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' "
						+ " ORDER BY bkmv_duedate, bkmv_bankmovementid;";

				printDevLog("sql_sumWithdraw: "+sql);
				pmConnSQL2.doFetch(sql);
				if (pmConnSQL2.next()) sumWithdraw = pmConnSQL2.getDouble("sumWithdraw");

				// Obtener Suma de Abonos		
				sql = " SELECT SUM(bkmv_deposit) AS sumDeposit"
						+ " FROM " + formatKind("bankmovements")
						+ " LEFT JOIN " + formatKind("bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
						+ " LEFT JOIN " + formatKind("bankmovtypes") + " ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) "
						+ " WHERE bkac_bankaccountid = " + bmoBankAccount.getId()
						+ " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "' "
						+ " ORDER BY bkmv_duedate, bkmv_bankmovementid;";

				printDevLog("sql_sumDeposit: "+sql);
				pmConnSQL2.doFetch(sql);
				if (pmConnSQL2.next()) sumDeposit = pmConnSQL2.getDouble("sumDeposit");

				sumDeposit = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(sumDeposit));
				sumWithdraw = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(sumWithdraw));

				balanceSumAmount = sumDeposit - sumWithdraw;
				balanceSumAmount = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(balanceSumAmount));

				balanceBkca = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(balanceBkca));

				System.out.println("\nCta: "+bmoBankAccount.getName().toString());
				System.out.println("Saldo SUM: "+balanceSumAmount);
				System.out.println("Saldo CTA: "+balanceBkca);

				// cambiar a no igual
				if (balanceSumAmount != balanceBkca) {
					bankAccountIsOk = false;
					System.out.println("saldo cta vs sum MB: DIFERENTE");
					bmoBankAccount.getBalance().setValue(balanceSumAmount);
					this.saveSimple(pmConn, bmoBankAccount, bmUpdateResult);

					list += "\n El saldo de la cuenta de banco "+ bmoBankAccount.getName().toString() + ""
							+ " NO coincide con la sumatoria de Movimientos Bancarios. \n"
							+ " Balance Cta. Banco: " + SFServerUtil.formatCurrency(balanceBkca) + " \n"
							+ " Balance Sumatoria MB: " + SFServerUtil.formatCurrency(balanceSumAmount) + " \n\n";
				}
			}

			if (!bankAccountIsOk) {
				String errors = "";
				if (bmUpdateResult.hasErrors()) errors = " - Error: " + bmUpdateResult.errorsToString();

				throw new SFException(list + errors);
			}

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + " - batchUpdateBalance(): " + e.toString());
		} finally {
			pmConn.close();
			pmConnSQL.close();
			pmConnSQL2.close();
		}
	}
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		if (action.equals(BmoBankAccount.ACTION_GETBALANCE)) {
			bmUpdateResult.addMsg("" + getBalanceBankAccount(Integer.parseInt(value)));
		}
		return bmUpdateResult;
	}

	// Calcular el saldo de la Cuenta de banco
	public double getBalanceBankAccount(int bankAccountId) throws SFException {
		double balance = 0, sumWithdraw = 0, sumDeposit = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		try {
			// Obtener Suma de Cargos/Abono	
			String sql = " SELECT SUM(bkmv_withdraw) AS sumWithdraw, SUM(bkmv_deposit) AS sumDeposit "
					+ " FROM " + formatKind("bankmovements")
					+ " LEFT JOIN " + formatKind("bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
					+ " WHERE bkac_bankaccountid = " + bankAccountId
					+ " ORDER BY bkmv_duedate, bkmv_bankmovementid;";

//			printDevLog("sql_getBalanceBankAccount: "+sql);
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				sumWithdraw = pmConn.getDouble("sumWithdraw");
				sumDeposit = pmConn.getDouble("sumDeposit");
			}
//			printDevLog("sumDeposit:"+sumDeposit + " | sumWithdraw:"+sumWithdraw);
			balance = sumDeposit - sumWithdraw;
//			printDevLog("balance::"+balance);

		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-getBalanceBankAccount() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return balance;
	}
}
