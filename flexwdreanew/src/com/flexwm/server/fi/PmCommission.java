/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.op.PmProductGroup;
import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.op.BmoProductGroup;
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


/**
 * @author jhernandez
 *
 */

public class PmCommission extends PmObject{
	BmoCommission bmoCommission;


	public PmCommission(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCommission = new BmoCommission();
		setBmObject(bmoCommission);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCommission.getProfileId(), bmoCommission.getBmoProfile()),
				new PmJoin(bmoCommission.getProductGroupId(), bmoCommission.getBmoProductGroup())
				)));


	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {		
		bmoCommission = (BmoCommission) autoPopulate(pmConn, new BmoCommission());

		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoCommission.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoCommission.setBmoProfile(bmoProfile);

		BmoProductGroup bmoProductGroup = new BmoProductGroup();
		int productGroupId = pmConn.getInt(bmoProductGroup.getIdFieldName());
		if (productGroupId > 0) bmoCommission.setBmoProductGroup((BmoProductGroup) new PmProductGroup(getSFParams()).populate(pmConn));
		else bmoCommission.setBmoProductGroup(bmoProductGroup);

		return bmoCommission;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCommission bmoCommission = (BmoCommission)bmObject;

		if (!(bmoCommission.getId() > 0)) {

			super.save(pmConn, bmoCommission, bmUpdateResult);

		}

		super.save(pmConn, bmoCommission, bmUpdateResult);

		return bmUpdateResult;
	}

	//El grupo del producto maneja comision
	public boolean hasCommissionByPRGP(PmConn pmConn,BmoProductGroup bmoProductGroup, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		sql = " SELECT * FROM commissions " +
				" WHERE comi_productgroupid = " + bmoProductGroup.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return true;
		}

		return false;
	}

	public boolean hasCommissionByGRUP(PmConn pmConn,BmoProfile bmoProfile, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		sql = " SELECT * FROM commissions " +
				" WHERE comi_profileid = " + bmoProfile.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			return true;
		}

		return false;
	}



	public double getPercentageCommissionByGRUP(PmConn pmConn,BmoProfile bmoProfile, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double percentage = 0;
		sql = " SELECT * FROM commissions " +
				" WHERE comi_profileid = " + bmoProfile.getId();		
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			percentage = pmConn.getDouble("comi_percentage");
		}

		return percentage;
	}

	private double getPercentageByGRUP(int profileId, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		double percentage = 0;
		try {
			pmConn.open();
			PmProfile pmProfile = new PmProfile(getSFParams());
			BmoProfile bmoProfile = (BmoProfile)pmProfile.get(profileId);

			percentage = getPercentageCommissionByGRUP(pmConn, bmoProfile, bmUpdateResult);

		} catch (Exception e) {
            System.out.println(this.getClass().getName() + "-getPercentageByGroup() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return percentage;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		if (action.equals(BmoCommission.ACTION_GETCOMMISSION)) {
			bmUpdateResult.addMsg("" + getPercentageByGRUP(Integer.parseInt(value), bmUpdateResult));
		}

		return bmUpdateResult;
	}
}

