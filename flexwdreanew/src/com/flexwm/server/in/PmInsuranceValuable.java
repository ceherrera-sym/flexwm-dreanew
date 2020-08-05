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
import com.flexwm.shared.in.BmoInsuranceValuable;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmInsuranceValuable extends PmObject {
	BmoInsuranceValuable bmoInsuranceValuable = new BmoInsuranceValuable();
	
	public PmInsuranceValuable(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoInsuranceValuable);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoInsuranceValuable.getInsuranceId(), bmoInsuranceValuable.getBmoInsurance()),
				new PmJoin(bmoInsuranceValuable.getBmoInsurance().getInsuranceCategoryId(), bmoInsuranceValuable.getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoInsuranceValuable.getBmoInsurance().getGoalId(), bmoInsuranceValuable.getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoInsuranceValuable.getBmoInsurance().getCurrencyId(), bmoInsuranceValuable.getBmoInsurance().getBmoCurrency())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoInsuranceValuable bmoInsuranceValuable = (BmoInsuranceValuable)autoPopulate(pmConn, new BmoInsuranceValuable());

		// BmoInsurance
		BmoInsurance bmoInsurance = new BmoInsurance();
		int InsuranceId = (int)pmConn.getInt(bmoInsurance.getIdFieldName());
		if (InsuranceId > 0) bmoInsuranceValuable.setBmoInsurance((BmoInsurance) new PmInsurance(getSFParams()).populate(pmConn));
		else bmoInsuranceValuable.setBmoInsurance(bmoInsurance);
		
		return bmoInsuranceValuable;
	}
}