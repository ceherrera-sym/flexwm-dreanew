/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.cm.BmoProjectGuideline;


public class PmProjectGuideline extends PmObject {
	BmoProjectGuideline bmoProjectGuideline;

	public PmProjectGuideline(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProjectGuideline = new BmoProjectGuideline();
		setBmObject(bmoProjectGuideline);
	}

	// Revisar si existe la orden de algún proyecto
	public boolean projectGuidelineExists(PmConn pmConn, int projectId) throws SFPmException {
		pmConn.doFetch("SELECT pjgi_projectguidelineid FROM projectguidelines WHERE pjgi_projectid = " + projectId);
		return pmConn.next();
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoProjectGuideline());		
	}
}
