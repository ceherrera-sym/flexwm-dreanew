/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmSessionTypeExtra extends PmObject{
	BmoSessionTypeExtra bmoSessionTypeExtra;
	
	
	public PmSessionTypeExtra(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSessionTypeExtra = new BmoSessionTypeExtra();
		setBmObject(bmoSessionTypeExtra); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSessionTypeExtra.getSessionTypeId(), bmoSessionTypeExtra.getBmoSessionType()),
				new PmJoin(bmoSessionTypeExtra.getBmoSessionType().getSessionDisciplineId(), bmoSessionTypeExtra.getBmoSessionType().getBmoSessionDiscipline()),
				new PmJoin(bmoSessionTypeExtra.getBmoSessionType().getCurrencyId(), bmoSessionTypeExtra.getBmoSessionType().getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSessionTypeExtra = (BmoSessionTypeExtra)autoPopulate(pmConn, new BmoSessionTypeExtra());

		// BmoSessionType
		BmoSessionType bmoSessionType = new BmoSessionType();
		if (pmConn.getInt(bmoSessionType.getIdFieldName()) > 0) 
			bmoSessionTypeExtra.setBmoSessionType((BmoSessionType) new PmSessionType(getSFParams()).populate(pmConn));
		else 
			bmoSessionTypeExtra.setBmoSessionType(bmoSessionType);
		
		return bmoSessionTypeExtra;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSessionTypeExtra bmoSessionTypeExtra = (BmoSessionTypeExtra)bmObject;
		
		if (SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
				bmoSessionTypeExtra.getEndDate().toString(), bmoSessionTypeExtra.getStartDate().toString())) {
			bmUpdateResult.addError(bmoSessionTypeExtra.getEndDate().getName(), 
					"No puede ser Anterior a " + bmoSessionTypeExtra.getStartDate().getLabel());
		}
		
		super.save(pmConn, bmoSessionTypeExtra, bmUpdateResult);
		return bmUpdateResult;
	}
}

