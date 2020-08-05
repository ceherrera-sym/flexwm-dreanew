/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.co.BmoConceptGroup;
import com.flexwm.shared.co.BmoConceptHeading;
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

public class PmConceptHeading extends PmObject{
	BmoConceptHeading bmoConceptHeading;

	public PmConceptHeading(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoConceptHeading = new BmoConceptHeading();
		setBmObject(bmoConceptHeading); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoConceptHeading.getConceptGroupId(), bmoConceptHeading.getBmoConceptGroup())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoConceptHeading = (BmoConceptHeading)autoPopulate(pmConn, new BmoConceptHeading());

		//BmoConceptGroup
		BmoConceptGroup bmoConceptGroup = new BmoConceptGroup();
		int conceptGroupId = (int)pmConn.getInt(bmoConceptGroup.getIdFieldName());
		if (conceptGroupId > 0) bmoConceptHeading.setBmoConceptGroup((BmoConceptGroup) new PmConceptGroup(getSFParams()).populate(pmConn));
		else bmoConceptHeading.setBmoConceptGroup(bmoConceptGroup);
		return bmoConceptHeading;

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoConceptHeading bmoConceptHeading = (BmoConceptHeading)bmObject;

		super.save(pmConn, bmoConceptHeading, bmUpdateResult);

		return bmUpdateResult;
	}
}

