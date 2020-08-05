/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderCommissionAmount;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;


public class PmOrderCommissionAmount extends PmObject{
	BmoOrderCommissionAmount bmOrderCommissionAmount;


	public PmOrderCommissionAmount(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmOrderCommissionAmount = new BmoOrderCommissionAmount();
		setBmObject(bmOrderCommissionAmount); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmOrderCommissionAmount.getProfileId(), bmOrderCommissionAmount.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmOrderCommissionAmount = (BmoOrderCommissionAmount)autoPopulate(pmConn, new BmoOrderCommissionAmount());	

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		int userId = (int)pmConn.getInt(bmoProfile.getIdFieldName());
		if (userId > 0) bmOrderCommissionAmount.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmOrderCommissionAmount.setBmoProfile(bmoProfile);


		return bmOrderCommissionAmount;
	}


	// Obtiene los anticipos que se hayan realizado de una comisión
	public double getCommissionPreviousPayments(PmConn pmConn, BmoOrder bmoOrder, BmoOrderCommissionAmount bmoOrderCommissionAmount) throws SFException {
		double previousAmount = 0;

		pmConn.doFetch("SELECT SUM(reqi_total) as s FROM requisitions "
				+ "	WHERE reqi_orderid = " + bmoOrder.getId() 
				+ " AND reqi_ordercommissionamountid IN "
				+ "		(SELECT orca_ordercommissionamountid FROM ordercommissionamounts WHERE orca_parentid = " + bmoOrderCommissionAmount.getId() +  ") ");
		if (pmConn.next()) {
			previousAmount = pmConn.getDouble("s");
		}

		return previousAmount;
	}
}

