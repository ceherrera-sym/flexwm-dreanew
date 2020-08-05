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

import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceDiscount;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsuranceDiscount extends PmObject {
	BmoInsuranceDiscount bmoInsuranceDiscount = new BmoInsuranceDiscount();
	
	public PmInsuranceDiscount(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsuranceDiscount);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoInsuranceDiscount.getInsuranceId(), bmoInsuranceDiscount.getBmoInsurance()),
				new PmJoin(bmoInsuranceDiscount.getBmoInsurance().getInsuranceCategoryId(), bmoInsuranceDiscount.getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoInsuranceDiscount.getBmoInsurance().getGoalId(), bmoInsuranceDiscount.getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoInsuranceDiscount.getBmoInsurance().getCurrencyId(), bmoInsuranceDiscount.getBmoInsurance().getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoInsuranceDiscount bmoInsuranceDiscount = (BmoInsuranceDiscount)autoPopulate(pmConn, new BmoInsuranceDiscount());

		// BmoInsurance
		BmoInsurance bmoInsurance = new BmoInsurance();
		int InsuranceId = (int)pmConn.getInt(bmoInsurance.getIdFieldName());
		if (InsuranceId > 0) bmoInsuranceDiscount.setBmoInsurance((BmoInsurance) new PmInsurance(getSFParams()).populate(pmConn));
		else bmoInsuranceDiscount.setBmoInsurance(bmoInsurance);
		
		return bmoInsuranceDiscount;
	}
}