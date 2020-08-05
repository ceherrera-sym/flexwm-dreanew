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

import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;


public class PmWFlowTimeTrack extends PmObject {
	BmoWFlowTimeTrack bmoWFlowTimeTrack;

	public PmWFlowTimeTrack(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
		setBmObject(bmoWFlowTimeTrack);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowTimeTrack.getWFlowId(), bmoWFlowTimeTrack.getBmoWFlow()),
				new PmJoin(bmoWFlowTimeTrack.getUserId(), bmoWFlowTimeTrack.getBmoUser()),
				new PmJoin(bmoWFlowTimeTrack.getBmoUser().getLocationId(), bmoWFlowTimeTrack.getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowTimeTrack.getBmoUser().getAreaId(), bmoWFlowTimeTrack.getBmoUser().getBmoArea()),
				new PmJoin(bmoWFlowTimeTrack.getBmoWFlow().getWFlowTypeId(), bmoWFlowTimeTrack.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWFlowTimeTrack.getBmoWFlow().getWFlowPhaseId(), bmoWFlowTimeTrack.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWFlowTimeTrack.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowTimeTrack.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowTimeTrack.getBmoWFlow().getWFlowFunnelId(), bmoWFlowTimeTrack.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)autoPopulate(pmConn, new BmoWFlowTimeTrack());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if ((int)pmConn.getInt(bmoUser.getIdFieldName()) > 0) bmoWFlowTimeTrack.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoWFlowTimeTrack.setBmoUser(bmoUser);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if ((int)pmConn.getInt(bmoWFlow.getIdFieldName()) > 0) bmoWFlowTimeTrack.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoWFlowTimeTrack.setBmoWFlow(bmoWFlow);

		return bmoWFlowTimeTrack;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)bmObject;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoWFlowTimeTrack.getId() > 0)) {

		}

		// Validar que el usuario no tenga ya otra actividad en el mismo tiempo
		//		if (isDuplicated(pmConn, bmoWFlowTimeTrack))
		//			bmUpdateResult.addError(bmoWFlowTimeTrack.getStartDate().getName(), "El Horario ya esta asignado.");

		// Calcula los minutos
		if (bmoWFlowTimeTrack.getEndDate().toString().length() > 0) {
			Date startDate = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(), bmoWFlowTimeTrack.getStartDate().toString());
			Date endDate = SFServerUtil.stringToDate(getSFParams().getDateTimeFormat(), bmoWFlowTimeTrack.getEndDate().toString());
			bmoWFlowTimeTrack.getMinutes().setValue((int)((endDate.getTime()/60000) - (startDate.getTime()/60000)));
		}

		super.save(pmConn, bmoWFlowTimeTrack, bmUpdateResult);

		return bmUpdateResult;
	}

	// Revisa que no haya otro rastreo en el mismo tiempo
	//	private boolean isDuplicated(PmConn pmConn, BmoWFlowTimeTrack bmoWFlowTimeTrack) throws SFPmException{
	//		String sql = "SELECT wftt_wflowtimetrackid FROM wflowtimetracks "
	//				+ " WHERE "
	//				+ " wftt_wflowtimetrackid <> " + bmoWFlowTimeTrack.getId() 
	//				+ " AND wftt_userid = " + bmoWFlowTimeTrack.getUserId().toInteger()
	//				+ " AND ("
	//				+ "			('"+ bmoWFlowTimeTrack.getStartDate().toString() + "' > wftt_startdate "
	//				+ 			" AND '" + bmoWFlowTimeTrack.getStartDate().toString() + "' < wftt_enddate)"
	//				+ "		OR "
	//				+ "			('"+ bmoWFlowTimeTrack.getEndDate().toString() + "' > wftt_startdate "
	//				+ 			" AND '" + bmoWFlowTimeTrack.getEndDate().toString() + "' < wftt_enddate)"
	//				+ "		)";
	//
	//		pmConn.doFetch(sql);
	//
	//		return pmConn.next();	
	//	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		bmoWFlowTimeTrack = (BmoWFlowTimeTrack)bmObject;

		// Revisar cantidad de items apartados
		if (action.equals(BmoWFlowTimeTrack.ACTION_LASTWFLOWTRACK))
			bmUpdateResult.setBmObject(getDayLastUserWFlowTimeTrack(pmConn, value));

		return bmUpdateResult;
	}

	// Obtiene el tipo de registro siguiente
	private BmoWFlowTimeTrack getDayLastUserWFlowTimeTrack(PmConn pmConn, String userId) throws SFException {
		String sql = " SELECT * FROM wflowtimetracks "
				+ " WHERE wftt_userid = " + userId
				+ " AND YEAR(wftt_startdate) = " + Calendar.getInstance(TimeZone.getTimeZone(getSFParams().getBmoSFConfig().getDayTimeZone().toString())).get(Calendar.YEAR)
				+ " AND MONTH(wftt_startdate) = " + (Calendar.getInstance(TimeZone.getTimeZone(getSFParams().getBmoSFConfig().getDayTimeZone().toString())).get(Calendar.MONTH) + 1)
				+ " AND DAY(wftt_startdate) = " + Calendar.getInstance(TimeZone.getTimeZone(getSFParams().getBmoSFConfig().getDayTimeZone().toString())).get(Calendar.DAY_OF_MONTH) 
				+ " ORDER BY wftt_wflowtimetrackid DESC ";
		System.out.println(sql);

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			BmoWFlowTimeTrack lastBmoWFlowTimeTrack = (BmoWFlowTimeTrack)this.get(pmConn, pmConn.getInt("wftt_wflowtimetrackid"));
			if (!(lastBmoWFlowTimeTrack.getEndDate().toString().length() > 0))
				return lastBmoWFlowTimeTrack;
			else 
				return new BmoWFlowTimeTrack();
		} else
			return new BmoWFlowTimeTrack();
	}
}
