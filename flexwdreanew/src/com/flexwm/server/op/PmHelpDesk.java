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

import com.flexwm.shared.op.BmoHelpDesk;
import com.flexwm.shared.op.BmoHelpDeskType;
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

public class PmHelpDesk extends PmObject{
	BmoHelpDesk bmoHelpDesk;
	
	
	public PmHelpDesk(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoHelpDesk = new BmoHelpDesk();
		setBmObject(bmoHelpDesk); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoHelpDesk.getUserId(), bmoHelpDesk.getBmoUser()),
				new PmJoin(bmoHelpDesk.getBmoUser().getAreaId(), bmoHelpDesk.getBmoUser().getBmoArea()),
				new PmJoin(bmoHelpDesk.getBmoUser().getLocationId(), bmoHelpDesk.getBmoUser().getBmoLocation()),
				new PmJoin(bmoHelpDesk.getHelpDeskTypeId() , bmoHelpDesk.getBmoHelpDeskType()),
				new PmJoin(bmoHelpDesk.getEquipmentId() , bmoHelpDesk.getBmoEquipment())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoHelpDeskType());
		BmoHelpDesk bmoHelpDesk = (BmoHelpDesk)autoPopulate(pmConn, new BmoHelpDesk());

		// BmoUser
		BmoHelpDeskType bmoHelpDeskType = new BmoHelpDeskType();
		int helpDeskTypeId = (int)pmConn.getInt(bmoHelpDeskType.getIdFieldName());
		if (helpDeskTypeId > 0) bmoHelpDesk.setBmoHelpDeskType((BmoHelpDeskType) new PmHelpDeskType(getSFParams()).populate(pmConn));
		else bmoHelpDesk.setBmoHelpDeskType(bmoHelpDeskType);	
		
		return bmoHelpDesk;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoHelpDesk bmoHelpDesk = (BmoHelpDesk)bmObject;

		super.save(pmConn, bmoHelpDesk, bmUpdateResult);

		return bmUpdateResult;
	}
}

