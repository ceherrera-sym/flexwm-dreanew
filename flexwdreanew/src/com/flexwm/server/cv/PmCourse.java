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

public class PmCourse extends PmObject {
	BmoCourse bmoCourse;

	public PmCourse(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCourse = new BmoCourse();
		setBmObject(bmoCourse); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCourse.getProgramId(), bmoCourse.getBmoCourseProgram()),
				new PmJoin(bmoCourse.getBmoCourseProgram().getAreaId(), bmoCourse.getBmoCourseProgram().getBmoArea())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCourse());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCourse bmoCourse = (BmoCourse)bmObject;

		super.save(pmConn, bmoCourse, bmUpdateResult);

		return bmUpdateResult;
	}
}

