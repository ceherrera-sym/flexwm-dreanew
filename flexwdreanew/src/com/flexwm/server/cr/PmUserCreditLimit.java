/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cr;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cr.BmoUserCreditLimit;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class PmUserCreditLimit extends PmObject{
	BmoUserCreditLimit bmoUserCreditLimit;


	public PmUserCreditLimit(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoUserCreditLimit = new BmoUserCreditLimit();
		setBmObject(bmoUserCreditLimit);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoUserCreditLimit.getUserId(), bmoUserCreditLimit.getBmoUser()),
				new PmJoin(bmoUserCreditLimit.getBmoUser().getAreaId(), bmoUserCreditLimit.getBmoUser().getBmoArea()),
				new PmJoin(bmoUserCreditLimit.getBmoUser().getLocationId(), bmoUserCreditLimit.getBmoUser().getBmoLocation())
				)));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoUserCreditLimit = (BmoUserCreditLimit)autoPopulate(pmConn, new BmoUserCreditLimit());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int UserId = pmConn.getInt(bmoUser.getIdFieldName());
		if (UserId > 0) bmoUserCreditLimit.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoUserCreditLimit.setBmoUser(bmoUser);

		return bmoUserCreditLimit;

	}
}

