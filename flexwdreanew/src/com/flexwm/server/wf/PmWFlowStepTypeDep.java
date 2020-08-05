/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.flexwm.server.wf.PmWFlowStepType;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;

public class PmWFlowStepTypeDep extends PmObject {
	BmoWFlowStepTypeDep bmoWFlowStepTypeDep;
	
	public PmWFlowStepTypeDep(SFParams sfParams) throws SFPmException {
		super(sfParams);
		
		bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
		setBmObject(bmoWFlowStepTypeDep);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowStepTypeDep.getWFlowStepTypeId(), bmoWFlowStepTypeDep.getBmoWFlowStepType()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowFunnelId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowFunnel()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getProfileId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoProfile()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowTypeId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowType()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowValidationId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowValidation()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowActionId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowAction()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowPhaseId(), bmoWFlowStepTypeDep.getBmoWFlowStepType().getBmoWFlowPhase())
				)));
	}
		
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowStepTypeDep bmoWFlowStepTypeDep = (BmoWFlowStepTypeDep)autoPopulate(pmConn, new BmoWFlowStepTypeDep());		
		
		// Parent Step Type
		BmoWFlowStepType bmoWFlowStepType = new BmoWFlowStepType();
		PmWFlowStepType pm = new PmWFlowStepType(getSFParams());
		int wFlowStepTypeId = (int)pmConn.getInt(bmoWFlowStepType.getIdFieldName());
		if (wFlowStepTypeId > 0) bmoWFlowStepTypeDep.setBmoWFlowStepType((BmoWFlowStepType) pm.populate(pmConn));
		else bmoWFlowStepTypeDep.setBmoWFlowStepType(bmoWFlowStepType);
		
		return bmoWFlowStepTypeDep;
	}
}
