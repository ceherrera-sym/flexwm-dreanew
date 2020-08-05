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
import java.util.ListIterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.flexwm.server.wf.PmWFlowAction;
import com.flexwm.server.wf.PmWFlowPhase;
import com.flexwm.server.wf.PmWFlowStepTypeDep;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.server.wf.PmWFlowValidation;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowValidation;


public class PmWFlowStepType extends PmObject {
	BmoWFlowStepType bmoWFlowStepType;
	
	public PmWFlowStepType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowStepType = new BmoWFlowStepType();
		setBmObject(bmoWFlowStepType);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowStepType.getProfileId(), bmoWFlowStepType.getBmoProfile()),
				new PmJoin(bmoWFlowStepType.getWFlowTypeId(), bmoWFlowStepType.getBmoWFlowType()),
				new PmJoin(bmoWFlowStepType.getBmoWFlowType().getWFlowCategoryId(), bmoWFlowStepType.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowStepType.getWFlowPhaseId(), bmoWFlowStepType.getBmoWFlowPhase()),
				new PmJoin(bmoWFlowStepType.getWFlowValidationId(), bmoWFlowStepType.getBmoWFlowValidation()),
				new PmJoin(bmoWFlowStepType.getWFlowActionId(), bmoWFlowStepType.getBmoWFlowAction()),
				new PmJoin(bmoWFlowStepType.getWFlowFunnelId(), bmoWFlowStepType.getBmoWFlowFunnel())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowStepType bmoWFlowStepType = (BmoWFlowStepType)autoPopulate(pmConn, new BmoWFlowStepType());		

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if ((int)pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoProfile(bmoProfile);
		
		// BmoWFlowStepTypeType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		if (pmConn.getInt(bmoWFlowType.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoWFlowType(bmoWFlowType);
		
		// BmoWFlowPhase
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		if (pmConn.getInt(bmoWFlowPhase.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoWFlowPhase((BmoWFlowPhase) new PmWFlowPhase(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoWFlowPhase(bmoWFlowPhase);
		
		// BmoWFlowValidation
		BmoWFlowValidation bmoWFlowValidation = new BmoWFlowValidation();
		if (pmConn.getInt(bmoWFlowValidation.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoWFlowValidation((BmoWFlowValidation) new PmWFlowValidation(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoWFlowValidation(bmoWFlowValidation);
		
		// BmoWFlowAction
		BmoWFlowAction bmoWFlowAction = new BmoWFlowAction();
		if (pmConn.getInt(bmoWFlowAction.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoWFlowAction((BmoWFlowAction) new PmWFlowAction(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoWFlowAction(bmoWFlowAction);
		
		// BmoWFlowFunnel
		BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();
		if (pmConn.getInt(bmoWFlowFunnel.getIdFieldName()) > 0) 
			bmoWFlowStepType.setBmoWFlowFunnel((BmoWFlowFunnel) new PmWFlowFunnel(getSFParams()).populate(pmConn));
		else 
			bmoWFlowStepType.setBmoWFlowFunnel(bmoWFlowFunnel);
		
		return bmoWFlowStepType;
	}
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowStepType = (BmoWFlowStepType)bmObject;
		
		// Eliminar dependencias de pasos padres	
		PmWFlowStepTypeDep pmWFlowStepTypeDep = new PmWFlowStepTypeDep(getSFParams());
		BmoWFlowStepTypeDep bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
		BmFilter filterByWFlowStepType = new BmFilter();
		filterByWFlowStepType.setValueFilter(bmoWFlowStepTypeDep.getKind(), bmoWFlowStepTypeDep.getWFlowStepTypeId(), bmoWFlowStepType.getId());
		ListIterator<BmObject> wFlowStepTypeDepList = pmWFlowStepTypeDep.list(pmConn, filterByWFlowStepType).listIterator();
		while (wFlowStepTypeDepList.hasNext()) {
			BmoWFlowStepTypeDep bmoCurrentWFlowStepTypeDep = (BmoWFlowStepTypeDep)wFlowStepTypeDepList.next();
			pmWFlowStepTypeDep.delete(pmConn, bmoCurrentWFlowStepTypeDep, bmUpdateResult);
		}
		
		// Eliminar dependencias de pasos hijos
		pmWFlowStepTypeDep = new PmWFlowStepTypeDep(getSFParams());
		bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
		filterByWFlowStepType = new BmFilter();
		filterByWFlowStepType.setValueFilter(bmoWFlowStepTypeDep.getKind(), bmoWFlowStepTypeDep.getChildStepTypeId(), bmoWFlowStepType.getId());
		wFlowStepTypeDepList = pmWFlowStepTypeDep.list(pmConn, filterByWFlowStepType).listIterator();
		while (wFlowStepTypeDepList.hasNext()) {
			BmoWFlowStepTypeDep bmoCurrentWFlowStepTypeDep = (BmoWFlowStepTypeDep)wFlowStepTypeDepList.next();
			pmWFlowStepTypeDep.delete(pmConn, bmoCurrentWFlowStepTypeDep, bmUpdateResult);
		}
		
		// Eliminar usuario
		super.delete(pmConn, bmoWFlowStepType, bmUpdateResult);
		
		return bmUpdateResult;
	}
}
