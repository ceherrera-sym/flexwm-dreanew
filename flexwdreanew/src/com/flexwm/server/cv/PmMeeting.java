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
import java.util.ListIterator;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.cv.BmoMeeting;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlow;


public class PmMeeting extends PmObject {
	BmoMeeting bmoMeeting;


	public PmMeeting(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoMeeting = new BmoMeeting();
		setBmObject(bmoMeeting);	

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoMeeting.getWFlowId(), bmoMeeting.getBmoWFlow()),
				new PmJoin(bmoMeeting.getBmoWFlow().getWFlowPhaseId(), bmoMeeting.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoMeeting.getWFlowTypeId(), bmoMeeting.getBmoWFlowType()),
				new PmJoin(bmoMeeting.getBmoWFlowType().getWFlowCategoryId(), bmoMeeting.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoMeeting.getBmoWFlow().getWFlowFunnelId(), bmoMeeting.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return (BmoMeeting)autoPopulate(pmConn, new BmoMeeting());				
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoMeeting bmoMeeting = (BmoMeeting)bmObject;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoMeeting.getId() > 0)) {
			super.save(pmConn, bmoMeeting, bmUpdateResult);
		}

		// Crea el WFlow y asigna el ID recien creado
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		bmoMeeting.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoMeeting.getWFlowTypeId().toInteger(), bmoMeeting.getWFlowId().toInteger(), 
				bmoMeeting.getProgramCode(), bmoMeeting.getId(), bmoMeeting.getUserId().toInteger(), -1, -1,
				bmoMeeting.getCode().toString(), bmoMeeting.getName().toString(), bmoMeeting.getDescription().toString(), 
				bmoMeeting.getStartdate().toString(), bmoMeeting.getEnddate().toString(), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId());


		super.save(pmConn, bmoMeeting, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoMeeting bmoMeeting = (BmoMeeting)bmObject;

		super.delete(pmConn, bmoMeeting, bmUpdateResult);

		if (!bmUpdateResult.hasErrors()) {
			// Eliminar flujos
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = new BmoWFlow();
			BmFilter filterByMeeting = new BmFilter();
			filterByMeeting.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoMeeting.getId());			
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoMeeting.getProgramCode());
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterByMeeting);
			filterList.add(filterWFlowCategory);
			ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
			while (wFlowList.hasNext()) {
				bmoWFlow = (BmoWFlow)wFlowList.next();
				pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}
}

