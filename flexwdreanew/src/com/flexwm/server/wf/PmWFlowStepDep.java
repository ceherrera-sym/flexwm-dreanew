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
import com.flexwm.server.wf.PmWFlowStep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowStepDep;

public class PmWFlowStepDep extends PmObject {
	BmoWFlowStepDep bmoWFlowStepDep;
	
	public PmWFlowStepDep(SFParams sfParams) throws SFPmException {
		super(sfParams);
		
		bmoWFlowStepDep = new BmoWFlowStepDep();
		setBmObject(bmoWFlowStepDep);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowStepDep.getWFlowStepId(), bmoWFlowStepDep.getBmoWFlowStep()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowFunnelId(),bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowFunnel()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getProfileId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoProfile()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getUserId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoUser()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getBmoUser().getAreaId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoUser().getBmoArea()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getBmoUser().getLocationId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlow()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowType()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowValidationId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowValidation()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowActionId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowAction()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowType().getWFlowCategoryId(),bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowStepDep.getBmoWFlowStep().getWFlowPhaseId(), bmoWFlowStepDep.getBmoWFlowStep().getBmoWFlowPhase())
				)));
	}
		
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowStepDep bmoWFlowStepDep = (BmoWFlowStepDep)autoPopulate(pmConn, new BmoWFlowStepDep());		
		
		// Parent Step Type
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		PmWFlowStep pm = new PmWFlowStep(getSFParams());
		int wFlowStepId = (int)pmConn.getInt(bmoWFlowStep.getIdFieldName());
		if (wFlowStepId > 0) bmoWFlowStepDep.setBmoWFlowStep((BmoWFlowStep) pm.populate(pmConn));
		else bmoWFlowStepDep.setBmoWFlowStep(bmoWFlowStep);
		
		return bmoWFlowStepDep;
	}
	
	public int getIdByStepType(PmConn pmConn, int wFlowId, int wFlowTypeId, int wFlowPhaseId, String wFlowStepName) throws SFException {
		String sql = "select wfsp_wflowstepid from wflowsteps " +
					" left join wflows on (wfsp_wflowid = wflw_wflowid) " +
					" left join wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) " + 
					" left join wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid) " +
					" where wfsp_wflowid =  " + wFlowId +
					" and wfsp_name like ('" + wFlowStepName + "') " +
					" and wfph_wflowphaseid = " + wFlowPhaseId;
		pmConn.doFetch(sql);
		
		System.out.println("\n" + sql + "\n");
		
		if (pmConn.next()) return pmConn.getInt(1);
		else System.out.println("Error al encontrar registro de creacion de tipo de pasos en los flujos.");
		
		return -1;
	}
}
