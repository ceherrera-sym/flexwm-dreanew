package com.flexwm.server.op;

import com.flexwm.shared.op.BmoRequisitionType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmRequisitionType extends PmObject {
	BmoRequisitionType bmoRequisitionType;

	public PmRequisitionType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRequisitionType = new BmoRequisitionType();
		setBmObject(bmoRequisitionType);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoRequisitionType());
	}

	// Obtiene la orden de compra segun pedido y comision
	public int getRequisitionTypeIdCommission(PmConn pmConn) throws SFException {
		int requisitionTypeId = -1;
		pmConn.doFetch("SELECT rqtp_requisitiontypeid FROM requisitiontypes " +
				" WHERE rqtp_type = '" + BmoRequisitionType.TYPE_COMMISION + "'");				
		if (pmConn.next())
			requisitionTypeId = pmConn.getInt("rqtp_requisitiontypeid");

		return requisitionTypeId;
	}

}
