/**
 * 
 */
package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoRaccountType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmBankMovType extends PmObject {
	BmoBankMovType bmoBankMovType;

	public PmBankMovType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBankMovType = new BmoBankMovType();
		setBmObject(bmoBankMovType);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoBankMovType.getPaccountTypeId(), bmoBankMovType.getBmoPaccountType()),
				new PmJoin(bmoBankMovType.getRaccountTypeId(), bmoBankMovType.getBmoRaccountType()))));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoBankMovType bmoBankMovType = (BmoBankMovType) autoPopulate(pmConn, new BmoBankMovType());

		//BmoPaccountType
		BmoPaccountType bmoPaccountType = new BmoPaccountType();
		int paccountTypeId = (int)pmConn.getInt(bmoPaccountType.getIdFieldName());
		if (paccountTypeId > 0) bmoBankMovType.setBmoPaccountType((BmoPaccountType) new PmPaccountType(getSFParams()).populate(pmConn));
		else bmoBankMovType.setBmoPaccountType(bmoPaccountType);

		//BmoPaccountType
		BmoRaccountType bmoRaccountType = new BmoRaccountType();
		int raccountTypeId = (int)pmConn.getInt(bmoRaccountType.getIdFieldName());
		if (raccountTypeId > 0) bmoBankMovType.setBmoRaccountType((BmoRaccountType) new PmRaccountType(getSFParams()).populate(pmConn));
		else bmoBankMovType.setBmoRaccountType(bmoRaccountType);

		return bmoBankMovType;
	}
}
