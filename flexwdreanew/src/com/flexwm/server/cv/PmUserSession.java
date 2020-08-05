/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cv;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cv.BmoTrainingSession;
import com.flexwm.shared.cv.BmoUserSession;
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

public class PmUserSession extends PmObject{
	BmoUserSession bmoUserSession;


	public PmUserSession(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoUserSession = new BmoUserSession();
		setBmObject(bmoUserSession); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoUserSession.getTrainingSessionId(), bmoUserSession.getBmoTrainingSession()),
				new PmJoin(bmoUserSession.getBmoTrainingSession().getCourseId(), bmoUserSession.getBmoTrainingSession().getBmoCourse()),
				new PmJoin(bmoUserSession.getBmoTrainingSession().getBmoCourse().getProgramId(), bmoUserSession.getBmoTrainingSession().getBmoCourse().getBmoCourseProgram()),
				new PmJoin(bmoUserSession.getUserId(), bmoUserSession.getBmoUser()),
				new PmJoin(bmoUserSession.getBmoUser().getAreaId(), bmoUserSession.getBmoUser().getBmoArea()),
				new PmJoin(bmoUserSession.getBmoUser().getLocationId(), bmoUserSession.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoUserSession = (BmoUserSession) autoPopulate(pmConn, new BmoUserSession());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoUserSession.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoUserSession.setBmoUser(bmoUser);	

		// BmoTrainingSession
		BmoTrainingSession bmoTrainingSession = new BmoTrainingSession();
		int trainingSessionId = (int)pmConn.getInt(bmoTrainingSession.getIdFieldName());
		if (trainingSessionId > 0) bmoUserSession.setBmoTrainingSession((BmoTrainingSession) new PmTrainingSession(getSFParams()).populate(pmConn));
		else bmoUserSession.setBmoTrainingSession(bmoTrainingSession);

		return bmoUserSession;

	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoUserSession bmoUserSession = (BmoUserSession)bmObject;

		// Validaciones
		if (!(bmoUserSession.getUserId().toInteger() > 0)) {
			bmUpdateResult.addError(bmoUserSession.getUserId().getName(), "Seleccionar un usuario.");
		}

		super.save(pmConn, bmoUserSession, bmUpdateResult);

		return bmUpdateResult;
	}
}

