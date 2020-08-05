/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.fi.BmoFiscalPeriod;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.SQLUtil;
import com.symgae.shared.sf.BmoCompany;



/**
 * @author smuniz
 *
 */

public class PmFiscalPeriod extends PmObject {
	BmoFiscalPeriod bmoFiscalPeriod;

	public PmFiscalPeriod(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoFiscalPeriod = new BmoFiscalPeriod();
		setBmObject(bmoFiscalPeriod);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(new PmJoin(bmoFiscalPeriod.getCompanyId(), bmoFiscalPeriod.getBmoCompany())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de cuentas de banco de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( fipe_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " fipe_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoFiscalPeriod bmoFiscalPeriod = (BmoFiscalPeriod) autoPopulate(pmConn, new BmoFiscalPeriod());

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoFiscalPeriod.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoFiscalPeriod.setBmoCompany(bmoCompany);

		return bmoFiscalPeriod;	
	}

	// Verificar si esta abierto en la fecha del documento
	public boolean isOpen(PmConn pmConn, String date, int companyId) throws SFPmException {
		boolean isOpen = false;
		int isOpenCount = 0;
		String sql = "";

		sql = "SELECT COUNT(*) AS isOpenCount FROM " + SQLUtil.formatKind(getSFParams(), "fiscalperiods")
		+ " WHERE fipe_status = '" + BmoFiscalPeriod.STATUS_OPEN + "' "
		+ " AND (fipe_companyid = " + companyId + " OR fipe_companyid IS NULL ) "
		+ " AND ( "
		+ " ('" + date + "' BETWEEN fipe_startdate AND fipe_enddate) " 
		+ " OR  (fipe_startdate BETWEEN '" + date + "' AND '" + date + "') " 
		+ " OR  (fipe_enddate BETWEEN '" + date + "' AND '" + date + "') " 
		+ " ); ";

		printDevLog("isOpenCount: "+sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) isOpenCount = pmConn.getInt("isOpenCount");

		// Si existe un registro, esta abierto
		if (isOpenCount > 0) isOpen = true;

		return isOpen;
	}
	
	// Verificar si esta cerrado en la fecha del documento
	public boolean isClosed(PmConn pmConn, String date, int companyId) throws SFPmException {
		boolean isClosed = false;
		int isClosedCount = 0;
		String sql = "SELECT COUNT(*) AS isClosedCount FROM " + SQLUtil.formatKind(getSFParams(), "fiscalperiods")
					+ " WHERE fipe_status = '" + BmoFiscalPeriod.STATUS_CLOSED + "' "
					+ " AND (fipe_companyid = " + companyId + " OR fipe_companyid IS NULL ) "
					+ " AND ( "
					+ " ('" + date + "' BETWEEN fipe_startdate AND fipe_enddate) " 
				 	+ " OR  (fipe_startdate BETWEEN '" + date + "' AND '" + date + "') " 
				 	+ " OR  (fipe_enddate BETWEEN '" + date + "' AND '" + date + "') " 
				 	+ " ); ";
		
		printDevLog("isClosedCount: "+sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) isClosedCount = pmConn.getInt("isClosedCount");
		
		// Si existe un registro, esta cerrado
		if (isClosedCount > 0) isClosed = true;
			
		return isClosed;
	}
}
