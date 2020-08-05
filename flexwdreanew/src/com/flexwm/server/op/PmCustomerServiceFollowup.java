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

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoCustomerServiceFollowup;
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
 * @author smuniz
 *
 */

public class PmCustomerServiceFollowup extends PmObject{
	BmoCustomerServiceFollowup bmoCustomerServiceFollowup;

	public PmCustomerServiceFollowup(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCustomerServiceFollowup = new BmoCustomerServiceFollowup();
		setBmObject(bmoCustomerServiceFollowup); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerServiceFollowup.getUserId(), bmoCustomerServiceFollowup.getBmoUser()),
				new PmJoin(bmoCustomerServiceFollowup.getBmoUser().getAreaId(), bmoCustomerServiceFollowup.getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomerServiceFollowup.getBmoUser().getLocationId(), bmoCustomerServiceFollowup.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCustomerServiceFollowup = (BmoCustomerServiceFollowup)autoPopulate(pmConn, new BmoCustomerServiceFollowup());	

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoCustomerServiceFollowup.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoCustomerServiceFollowup.setBmoUser(bmoUser);


		return bmoCustomerServiceFollowup;
	}
}
