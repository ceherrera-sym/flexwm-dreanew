package com.flexwm.server.op;

import com.flexwm.shared.op.BmoOrderBlockDate;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmOrderBlockDate extends PmObject {
	BmoOrderBlockDate bmoOrderBlockDate;
	
	public PmOrderBlockDate(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderBlockDate = new BmoOrderBlockDate();
		setBmObject(bmoOrderBlockDate);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoOrderBlockDate());
	}
	
	public boolean orderInBlockedDates(PmConn pmConn, String startDate, String endDate, BmUpdateResult bmUpdateResult) throws SFException {
		
		// Revisar asignaciones en las fechas establecidas 
		String blockedSql = "SELECT * FROM orderblockdates "
				+ " WHERE ("
				+ "		('" + startDate + "' BETWEEN orbl_startdate AND orbl_enddate)"
				+ "		OR"
				+ "		('" + endDate + "' BETWEEN orbl_startdate AND orbl_enddate) "
				+ "		) "
				+ "		OR ("
				+ "		'" + startDate + "' < orbl_startdate AND  '" + endDate + "' > orbl_enddate "
				+ "		)";
		pmConn.doFetch(blockedSql);
		
		return pmConn.next();
	}
	
}
