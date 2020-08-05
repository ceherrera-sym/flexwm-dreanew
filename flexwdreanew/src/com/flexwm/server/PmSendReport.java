/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.BmoSendReport;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;

public class PmSendReport extends PmObject {
	BmoSendReport bmoSendReport;
	
	public PmSendReport(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSendReport = new BmoSendReport();
		setBmObject(bmoSendReport);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSendReport.getProfileId(), bmoSendReport.getBmoProfile())				
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSendReport = (BmoSendReport)autoPopulate(pmConn, new BmoSendReport());
		
		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) bmoSendReport.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoSendReport.setBmoProfile(bmoProfile);
		
		return bmoSendReport;
	}
		
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoSendReport = (BmoSendReport)bmObject;
		
		super.save(pmConn, bmoSendReport, bmUpdateResult);
		
		return bmUpdateResult;
	}
}
