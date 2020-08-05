/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.in;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.server.cm.PmCustomer;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.in.BmoPolicyRecipient;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmPolicyRecipient extends PmObject {
	BmoPolicyRecipient bmoPolicyRecipient = new BmoPolicyRecipient();
	
	public PmPolicyRecipient(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoPolicyRecipient);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPolicyRecipient.getCustomerId(), bmoPolicyRecipient.getBmoCustomer())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPolicyRecipient bmoPolicyRecipient = (BmoPolicyRecipient)autoPopulate(pmConn, new BmoPolicyRecipient());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (customerId > 0) bmoPolicyRecipient.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoPolicyRecipient.setBmoCustomer(bmoCustomer);
		
		return bmoPolicyRecipient;
	}
}