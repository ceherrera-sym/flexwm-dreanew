package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoPaccountType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmPaccountType extends PmObject {
	BmoPaccountType bmoPaccountType;

	public PmPaccountType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPaccountType = new BmoPaccountType();
		setBmObject(bmoPaccountType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPaccountType());
	}

	// Obtiene algun tipo de cuenta que se para ordenes de compra, visibles y de cargo
	public int getRequisitionPaccountType(PmConn pmConn) throws SFException {
		int paccountTypeId = 0;

		pmConn.doFetch("SELECT pact_paccounttypeid FROM paccounttypes "
				+ " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'"
				+ " AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "'");
		if (pmConn.next())
			paccountTypeId = pmConn.getInt("pact_paccounttypeid");

		return paccountTypeId;
	}

	// Obtiene el tipo de CxP de O.C., devolucion
	public int getRequisitionReturnPaccountType(PmConn pmConn) throws SFException {
		int paccountTypeId = 0;

		pmConn.doFetch("SELECT pact_paccounttypeid FROM paccounttypes "
				+ " WHERE pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'"
				+ " AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "'");
		if (pmConn.next())
			paccountTypeId = pmConn.getInt("pact_paccounttypeid");

		return paccountTypeId;
	}

}
