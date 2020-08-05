/**
 * 
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoSupplierUser;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


public class PmSupplierUser extends PmObject {
	BmoSupplierUser bmoSupplierUser;

	public PmSupplierUser(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierUser = new BmoSupplierUser();
		setBmObject(bmoSupplierUser);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplierUser.getUserId(), bmoSupplierUser.getBmoUser()),
				new PmJoin(bmoSupplierUser.getBmoUser().getAreaId(), bmoSupplierUser.getBmoUser().getBmoArea()),
				new PmJoin(bmoSupplierUser.getBmoUser().getLocationId(), bmoSupplierUser.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSupplierUser = (BmoSupplierUser) autoPopulate(pmConn, new BmoSupplierUser());		

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoSupplierUser.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoSupplierUser.setBmoUser(bmoUser);

		return bmoSupplierUser;
	}

	// Obtiene el usuario ligado al proveedor
	public int getSupplierIdByLinkedUser(PmConn pmConn, BmoUser bmoUser, BmUpdateResult bmUpdateResult) throws SFException {
		bmoSupplierUser = new BmoSupplierUser();
		try {
			bmoSupplierUser = (BmoSupplierUser)getBy(pmConn, bmoUser.getId(), bmoSupplierUser.getUserId().getName());			
		} catch (SFException e) {
			bmUpdateResult.addMsg("El Usuario " + bmoUser.getCode().toString() + " no tiene Proveedor Vinculado.");
		}
		return bmoSupplierUser.getSupplierId().toInteger();
	}
}
