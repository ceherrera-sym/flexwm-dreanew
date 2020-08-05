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
import com.flexwm.shared.cv.BmoPositionSkill;
import com.flexwm.shared.cv.BmoSkill;
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

public class PmPositionSkill extends PmObject {
	BmoPositionSkill bmoPositionSkill;


	public PmPositionSkill(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPositionSkill = new BmoPositionSkill();
		setBmObject(bmoPositionSkill); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPositionSkill.getPositionId(), bmoPositionSkill.getBmoPosition()),
				new PmJoin(bmoPositionSkill.getSkillId(), bmoPositionSkill.getBmoSkill())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoPositionSkill());
		bmoPositionSkill = (BmoPositionSkill)autoPopulate(pmConn, new BmoPositionSkill());		

		//BmoSkill
		BmoSkill bmoSkill = new BmoSkill();
		int skillId = (int)pmConn.getInt(bmoSkill.getIdFieldName());
		if (skillId > 0) bmoPositionSkill.setBmoSkill((BmoSkill) new PmSkill(getSFParams()).populate(pmConn));
		else bmoPositionSkill.setBmoSkill(bmoSkill);

		return bmoPositionSkill;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoPositionSkill bmoPositionSkill = (BmoPositionSkill)bmObject;

		super.save(pmConn, bmoPositionSkill, bmUpdateResult);

		return bmUpdateResult;
	}
}
