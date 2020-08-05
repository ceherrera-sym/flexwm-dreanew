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

import com.flexwm.shared.cv.BmoCourseProgram;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmArea;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;


/**
 * @author smuniz
 *
 */

public class PmCourseProgram extends PmObject {
	BmoCourseProgram bmoCourseProgram;

	public PmCourseProgram(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCourseProgram = new BmoCourseProgram();
		setBmObject(bmoCourseProgram); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCourseProgram.getAreaId(), bmoCourseProgram.getBmoArea())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoProgram());
		BmoCourseProgram bmoCourseProgram = (BmoCourseProgram)autoPopulate(pmConn, new BmoCourseProgram());

		// BmoUser
		BmoArea bmoArea = new BmoArea();
		int areaId = (int)pmConn.getInt(bmoArea.getIdFieldName());
		if (areaId > 0) bmoCourseProgram.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else bmoCourseProgram.setBmoArea(bmoArea);	

		return bmoCourseProgram;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCourseProgram bmoCourseProgram = (BmoCourseProgram)bmObject;

		super.save(pmConn, bmoCourseProgram, bmUpdateResult);

		return bmUpdateResult;
	}
}

