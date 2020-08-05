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
import com.symgae.server.sf.PmUser;
import com.flexwm.server.wf.PmWFlowUserBlockDate;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowUserBlockDate;


public class PmWFlowUserBlockDate extends PmObject {
	BmoWFlowUserBlockDate bmoWFlowUserBlockDate;

	public PmWFlowUserBlockDate(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowUserBlockDate = new BmoWFlowUserBlockDate();
		setBmObject(bmoWFlowUserBlockDate);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowUserBlockDate.getUserId(), bmoWFlowUserBlockDate.getBmoUser()),
				new PmJoin(bmoWFlowUserBlockDate.getBmoUser().getLocationId(), bmoWFlowUserBlockDate.getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowUserBlockDate.getBmoUser().getAreaId(), bmoWFlowUserBlockDate.getBmoUser().getBmoArea())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowUserBlockDate = (BmoWFlowUserBlockDate)autoPopulate(pmConn, new BmoWFlowUserBlockDate());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoWFlowUserBlockDate.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoWFlowUserBlockDate.setBmoUser(bmoUser);

		return bmoWFlowUserBlockDate;
	}
	
	public boolean userInBlockedDates(PmConn pmConn, int userId, String startDate, String endDate, BmUpdateResult bmUpdateResult) throws SFException {
		
		// Revisar asignaciones en las fechas establecidas 
		String blockedSql = "SELECT * FROM wflowuserblockdates "
				+ " WHERE "
				+ " wfub_userid = " + userId
				+ "	AND	("
					+ "		('" + startDate + "' BETWEEN wfub_startdate AND wfub_enddate)"
					+ "		OR"
					+ "		('" + endDate + "' BETWEEN wfub_startdate AND wfub_enddate) "
					+ "		) "
					+ "		OR ("
					+ "		'" + startDate + "' < wfub_startdate AND  '" + endDate + "' > wfub_enddate "
				+ "		)";
		pmConn.doFetch(blockedSql);
		
		return pmConn.next();
	}
}
