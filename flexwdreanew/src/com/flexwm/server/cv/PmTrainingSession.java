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
import com.flexwm.shared.cv.BmoCourse;
import com.flexwm.shared.cv.BmoCourseProgram;
import com.flexwm.shared.cv.BmoTrainingSession;
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

public class PmTrainingSession extends PmObject{
	BmoTrainingSession bmoTrainingSession;


	public PmTrainingSession(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoTrainingSession = new BmoTrainingSession();
		setBmObject(bmoTrainingSession); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoTrainingSession.getCourseId(), bmoTrainingSession.getBmoCourse()),
				new PmJoin(bmoTrainingSession.getBmoCourse().getProgramId(), bmoTrainingSession.getBmoCourse().getBmoCourseProgram()),
				new PmJoin(bmoTrainingSession.getBmoCourse().getBmoCourseProgram().getAreaId(), bmoTrainingSession.getBmoCourse().getBmoCourseProgram().getBmoArea())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoCourse());
		BmoTrainingSession bmoTrainingSession = (BmoTrainingSession)autoPopulate(pmConn, new BmoTrainingSession());

		// BmoCourse
		BmoCourse bmoCourse = new BmoCourse();
		int courseId = (int)pmConn.getInt(bmoCourse.getIdFieldName());
		if (courseId > 0) bmoTrainingSession.setBmoCourse((BmoCourse) new PmCourse(getSFParams()).populate(pmConn));
		else bmoTrainingSession.setBmoCourse(bmoCourse);	

		// BmoProgram
		BmoCourseProgram bmoCourseProgram = new BmoCourseProgram();
		int programId = (int)pmConn.getInt(bmoCourseProgram.getIdFieldName());
		if (programId > 0) bmoTrainingSession.getBmoCourse().setBmoCourseProgram((BmoCourseProgram) new PmCourseProgram(getSFParams()).populate(pmConn));
		else bmoTrainingSession.getBmoCourse().setBmoCourseProgram(bmoCourseProgram);

		return bmoTrainingSession;

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoTrainingSession bmoTrainingSession = (BmoTrainingSession)bmObject;

		super.save(pmConn, bmoTrainingSession, bmUpdateResult);

		return bmUpdateResult;
	}
}

