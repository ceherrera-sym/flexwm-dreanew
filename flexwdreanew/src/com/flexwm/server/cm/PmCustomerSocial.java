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

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmSocial;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoSocial;
import com.flexwm.shared.cm.BmoCustomerSocial;


public class PmCustomerSocial extends PmObject {
	BmoCustomerSocial bmoCustomerSocial;

	public PmCustomerSocial(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerSocial = new BmoCustomerSocial();
		setBmObject(bmoCustomerSocial);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerSocial.getSocialId(), bmoCustomerSocial.getBmoSocial())
				)));	
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCustomerSocial = (BmoCustomerSocial) autoPopulate(pmConn, new BmoCustomerSocial());

		// BmoCustomerSocial
		BmoSocial bmoSocial = new BmoSocial();
		int socialId = (int)pmConn.getInt(bmoSocial.getIdFieldName());
		if (socialId > 0) bmoCustomerSocial.setBmoSocial((BmoSocial) new PmSocial(getSFParams()).populate(pmConn));
		else bmoCustomerSocial.setBmoSocial(bmoSocial);

		return bmoCustomerSocial;
	}
}
