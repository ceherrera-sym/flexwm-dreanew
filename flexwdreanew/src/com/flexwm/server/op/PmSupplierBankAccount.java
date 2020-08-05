/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoSupplierBankAccount;


public class PmSupplierBankAccount extends PmObject {
	BmoSupplierBankAccount bmoSupplierBankAccount;

	public PmSupplierBankAccount(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierBankAccount = new BmoSupplierBankAccount();
		setBmObject(bmoSupplierBankAccount);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplierBankAccount.getCurrencyId(), bmoSupplierBankAccount.getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoSupplierBankAccount());
	}
}
