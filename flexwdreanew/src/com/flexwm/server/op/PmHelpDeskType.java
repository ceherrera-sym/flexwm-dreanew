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

import com.flexwm.shared.op.BmoHelpDeskType;
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

public class PmHelpDeskType extends PmObject{
	BmoHelpDeskType bmoHelpDeskType;
	
	
	public PmHelpDeskType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoHelpDeskType = new BmoHelpDeskType();
		setBmObject(bmoHelpDeskType); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoHelpDeskType.getUserId(), bmoHelpDeskType.getBmoUser()),
				new PmJoin(bmoHelpDeskType.getBmoUser().getAreaId(), bmoHelpDeskType.getBmoUser().getBmoArea()),
				new PmJoin(bmoHelpDeskType.getBmoUser().getLocationId(), bmoHelpDeskType.getBmoUser().getBmoLocation())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoHelpDeskType());
		BmoHelpDeskType bmoHelpDeskType = (BmoHelpDeskType)autoPopulate(pmConn, new BmoHelpDeskType());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoHelpDeskType.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoHelpDeskType.setBmoUser(bmoUser);	
		
		return bmoHelpDeskType;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoHelpDeskType bmoHelpDeskType = (BmoHelpDeskType)bmObject;

		super.save(pmConn, bmoHelpDeskType, bmUpdateResult);

		return bmUpdateResult;
	}
}

