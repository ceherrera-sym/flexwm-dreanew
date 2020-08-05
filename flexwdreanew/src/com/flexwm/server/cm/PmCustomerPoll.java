
package com.flexwm.server.cm;


import com.flexwm.shared.cm.BmoCustomerPoll;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmCustomerPoll extends PmObject {
	BmoCustomerPoll bmoCustomerPoll;

	public PmCustomerPoll(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerPoll = new BmoCustomerPoll();
		setBmObject(bmoCustomerPoll);

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCustomerPoll());		
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) pmConn.commit();

		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}
}

