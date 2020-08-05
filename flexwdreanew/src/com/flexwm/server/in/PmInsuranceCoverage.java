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
import com.flexwm.shared.in.BmoInsuranceCoverage;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsuranceCoverage extends PmObject {
	BmoInsuranceCoverage bmoInsuranceCoverage = new BmoInsuranceCoverage();
	
	public PmInsuranceCoverage(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsuranceCoverage);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoInsuranceCoverage.getInsuranceId(), bmoInsuranceCoverage.getBmoInsurance()),
				new PmJoin(bmoInsuranceCoverage.getBmoInsurance().getInsuranceCategoryId(), bmoInsuranceCoverage.getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoInsuranceCoverage.getBmoInsurance().getGoalId(), bmoInsuranceCoverage.getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoInsuranceCoverage.getBmoInsurance().getCurrencyId(), bmoInsuranceCoverage.getBmoInsurance().getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoInsuranceCoverage bmoInsuranceCoverage = (BmoInsuranceCoverage)autoPopulate(pmConn, new BmoInsuranceCoverage());

		// BmoInsurance
		BmoInsurance bmoInsurance = new BmoInsurance();
		int InsuranceId = (int)pmConn.getInt(bmoInsurance.getIdFieldName());
		if (InsuranceId > 0) bmoInsuranceCoverage.setBmoInsurance((BmoInsurance) new PmInsurance(getSFParams()).populate(pmConn));
		else bmoInsuranceCoverage.setBmoInsurance(bmoInsurance);
		
		return bmoInsuranceCoverage;
	}
}