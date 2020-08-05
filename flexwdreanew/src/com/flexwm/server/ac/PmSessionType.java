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

import com.flexwm.server.fi.PmCurrency;
import com.flexwm.shared.ac.BmoSessionDiscipline;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmSessionType extends PmObject{
	BmoSessionType bmoSessionType;
	
	
	public PmSessionType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoSessionType = new BmoSessionType();
		setBmObject(bmoSessionType); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSessionType.getSessionDisciplineId(), bmoSessionType.getBmoSessionDiscipline()),
				new PmJoin(bmoSessionType.getCurrencyId(), bmoSessionType.getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSessionType = (BmoSessionType)autoPopulate(pmConn, new BmoSessionType());

		// BmoSessionDiscipline
		BmoSessionDiscipline bmoSessionDiscipline = new BmoSessionDiscipline();
		if (pmConn.getInt(bmoSessionDiscipline.getIdFieldName()) > 0) 
			bmoSessionType.setBmoSessionDiscipline((BmoSessionDiscipline) new PmSessionDiscipline(getSFParams()).populate(pmConn));
		else 
			bmoSessionType.setBmoSessionDiscipline(bmoSessionDiscipline);
		
		BmoCurrency bmoCurrency = new BmoCurrency();
		if (pmConn.getInt(bmoCurrency.getIdFieldName()) > 0) 
			bmoSessionType.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else 
			bmoSessionType.setBmoCurrency(bmoCurrency);
		
		return bmoSessionType;
	}
}

