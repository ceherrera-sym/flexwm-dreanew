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
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


/**
 * @author smuniz
 *
 */

public class PmCustomerServiceType extends PmObject{
	BmoCustomerServiceType bmoCustomerServiceType;

	public PmCustomerServiceType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCustomerServiceType = new BmoCustomerServiceType();
		setBmObject(bmoCustomerServiceType); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerServiceType.getUserId(), bmoCustomerServiceType.getBmoUser()),
				new PmJoin(bmoCustomerServiceType.getBmoUser().getAreaId(), bmoCustomerServiceType.getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomerServiceType.getBmoUser().getLocationId(), bmoCustomerServiceType.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoOrderComplaintType());
		bmoCustomerServiceType = (BmoCustomerServiceType)autoPopulate(pmConn, new BmoCustomerServiceType());	

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoCustomerServiceType.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoCustomerServiceType.setBmoUser(bmoUser);

		return bmoCustomerServiceType;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerServiceType bmoCustomerServiceType = (BmoCustomerServiceType)bmObject;

		super.save(pmConn, bmoCustomerServiceType, bmUpdateResult);
		return bmUpdateResult;
	}
}

