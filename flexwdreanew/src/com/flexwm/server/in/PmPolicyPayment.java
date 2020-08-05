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
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.in.BmoPolicyPayment;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

public class PmPolicyPayment extends PmObject {
	BmoPolicyPayment bmoPolicyPayment = new BmoPolicyPayment();
	
	public PmPolicyPayment(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoPolicyPayment);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPolicyPayment.getPolicyId(), bmoPolicyPayment.getBmoPolicy()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getInsuranceId(), bmoPolicyPayment.getBmoPolicy().getBmoInsurance()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getInsuranceCategoryId(), bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getGoalId(), bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getCurrencyId(), bmoPolicyPayment.getBmoPolicy().getBmoInsurance().getBmoCurrency()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getCustomerId(), bmoPolicyPayment.getBmoPolicy().getBmoCustomer()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getUserId(), bmoPolicyPayment.getBmoPolicy().getBmoUser()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoUser().getAreaId(), bmoPolicyPayment.getBmoPolicy().getBmoUser().getBmoArea()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoUser().getLocationId(), bmoPolicyPayment.getBmoPolicy().getBmoUser().getBmoLocation()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getWFlowTypeId(), bmoPolicyPayment.getBmoPolicy().getBmoWFlowType()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoWFlowType().getWFlowCategoryId(), bmoPolicyPayment.getBmoPolicy().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getWFlowId(), bmoPolicyPayment.getBmoPolicy().getBmoWFlow()),
				new PmJoin(bmoPolicyPayment.getBmoPolicy().getBmoWFlow().getWFlowPhaseId(), bmoPolicyPayment.getBmoPolicy().getBmoWFlow().getBmoWFlowPhase())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPolicyPayment bmoPolicyPayment = (BmoPolicyPayment)autoPopulate(pmConn, new BmoPolicyPayment());
		
		// BmoPolicy
		BmoPolicy bmoPolicy = new BmoPolicy();
		int policyId = (int)pmConn.getInt(bmoPolicy.getIdFieldName());
		if (policyId > 0) bmoPolicyPayment.setBmoPolicy((BmoPolicy) new PmPolicy(getSFParams()).populate(pmConn));
		else bmoPolicyPayment.setBmoPolicy(bmoPolicy);
		
		return bmoPolicyPayment;
	}
	
	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoPolicyPayment bmoPolicyPayment = (BmoPolicyPayment)bmObject;
		
		PmConn pmConn = new PmConn(getSFParams());
		
		try {
			pmConn.open();
			pmConn.disableAutoCommit();
			
			// Almacenar el movimiento
			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);
			
			// Actualiza poliza
			PmPolicy pmPolicy = new PmPolicy(getSFParams());
			BmoPolicy bmoPolicy = (BmoPolicy)pmPolicy.get(bmoPolicyPayment.getPolicyId().toInteger());
			pmPolicy.save(pmConn, bmoPolicy, bmUpdateResult);
			
			// Volver a guardar el movimiento
			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);
			
			if (!bmUpdateResult.hasErrors()) pmConn.commit();
			
		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
		
		return bmUpdateResult;
	}
}