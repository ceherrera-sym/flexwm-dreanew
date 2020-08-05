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
import com.flexwm.shared.in.BmoInsuranceFund;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsuranceFund extends PmObject {
	BmoInsuranceFund bmoInsuranceFund = new BmoInsuranceFund();
	
	public PmInsuranceFund(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsuranceFund);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoInsuranceFund.getInsuranceId(), bmoInsuranceFund.getBmoInsurance()),
				new PmJoin(bmoInsuranceFund.getBmoInsurance().getInsuranceCategoryId(), bmoInsuranceFund.getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoInsuranceFund.getBmoInsurance().getGoalId(), bmoInsuranceFund.getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoInsuranceFund.getBmoInsurance().getCurrencyId(), bmoInsuranceFund.getBmoInsurance().getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoInsuranceFund bmoInsuranceFund = (BmoInsuranceFund)autoPopulate(pmConn, new BmoInsuranceFund());

		// BmoInsurance
		BmoInsurance bmoInsurance = new BmoInsurance();
		int InsuranceId = (int)pmConn.getInt(bmoInsurance.getIdFieldName());
		if (InsuranceId > 0) bmoInsuranceFund.setBmoInsurance((BmoInsurance) new PmInsurance(getSFParams()).populate(pmConn));
		else bmoInsuranceFund.setBmoInsurance(bmoInsurance);
		
		return bmoInsuranceFund;
	}
}