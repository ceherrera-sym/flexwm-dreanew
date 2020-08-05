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
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;

import com.symgae.shared.sf.BmoUser;


public class PmPolicy extends PmObject {
	BmoPolicy bmoPolicy = new BmoPolicy();
	
	public PmPolicy(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoPolicy);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPolicy.getInsuranceId(), bmoPolicy.getBmoInsurance()),
				new PmJoin(bmoPolicy.getBmoInsurance().getInsuranceCategoryId(), bmoPolicy.getBmoInsurance().getBmoInsuranceCategory()),
				new PmJoin(bmoPolicy.getBmoInsurance().getGoalId(), bmoPolicy.getBmoInsurance().getBmoGoal()),
				new PmJoin(bmoPolicy.getBmoInsurance().getCurrencyId(), bmoPolicy.getBmoInsurance().getBmoCurrency()),
				new PmJoin(bmoPolicy.getCustomerId(), bmoPolicy.getBmoCustomer()),
				new PmJoin(bmoPolicy.getUserId(), bmoPolicy.getBmoUser()),
				new PmJoin(bmoPolicy.getBmoUser().getAreaId(), bmoPolicy.getBmoUser().getBmoArea()),
				new PmJoin(bmoPolicy.getBmoUser().getLocationId(), bmoPolicy.getBmoUser().getBmoLocation()),
				new PmJoin(bmoPolicy.getWFlowTypeId(), bmoPolicy.getBmoWFlowType()),
				new PmJoin(bmoPolicy.getBmoWFlowType().getWFlowCategoryId(), bmoPolicy.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoPolicy.getWFlowId(), bmoPolicy.getBmoWFlow()),
				new PmJoin(bmoPolicy.getBmoWFlow().getWFlowPhaseId(), bmoPolicy.getBmoWFlow().getBmoWFlowPhase())
				)));
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		
		if (getSFParams().restrictData(bmoPolicy.getProgramCode())) {
			int loggedUserId = getSFParams().getLoginInfo().getUserId();
			filters = "( poli_userid in (" +
					" select user_userid from users " +
					" where " + 
					" user_userid = " + loggedUserId +
					" or user_userid in ( " +
					" 	select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " + 
					") )";
		}				
		return filters;
	}	
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPolicy bmoPolicy = (BmoPolicy)autoPopulate(pmConn, new BmoPolicy());

		// BmoInsurance
		BmoInsurance bmoInsurance = new BmoInsurance();
		int insuranceId = (int)pmConn.getInt(bmoInsurance.getIdFieldName());
		if (insuranceId > 0) bmoPolicy.setBmoInsurance((BmoInsurance) new PmInsurance(getSFParams()).populate(pmConn));
		else bmoPolicy.setBmoInsurance(bmoInsurance);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoPolicy.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoPolicy.setBmoUser(bmoUser);
		
		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (customerId > 0) bmoPolicy.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoPolicy.setBmoCustomer(bmoCustomer);
		
		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int WFlowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (WFlowTypeId > 0) bmoPolicy.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoPolicy.setBmoWFlowType(bmoWFlowType);
		
		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		int wFlowId = (int)pmConn.getInt(bmoWFlow.getIdFieldName());
		if (wFlowId > 0) bmoPolicy.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoPolicy.setBmoWFlow(bmoWFlow);
		
		return bmoPolicy;
	}
	
	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		PmConn pmConn = new PmConn(getSFParams());
		
		try {
			pmConn.open();
			pmConn.disableAutoCommit();
			
			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);
			
			if (!bmUpdateResult.hasErrors()) pmConn.commit();
			
		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
		
		return bmUpdateResult;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoPolicy bmoPolicy = (BmoPolicy)bmObject;
		
		// Se almacena de forma preliminar para asignar ID
		if (!(bmoPolicy.getId() > 0)) {
			super.save(pmConn, bmoPolicy, bmUpdateResult);
			bmoPolicy.setId(bmUpdateResult.getId());
			
			// Establecer clave
			bmoPolicy.getCode().setValue(bmoPolicy.getCodeFormat());
		} else {
			// Actualizar status
			bmoPolicy.getStatus().setValue(getPolicyStatus(pmConn, bmoPolicy.getId()));
		}
		
		super.save(pmConn, bmoPolicy, bmUpdateResult);
		
		return bmUpdateResult;
	}
	
	public char getPolicyStatus(PmConn pmConn, int policyId) throws SFException {
		char status = BmoPolicy.STATUS_LATE;
		String sql = " SELECT COUNT(popa_status) FROM policypayments "
						+ " WHERE popa_policyid = " + policyId + ""
						+ " AND popa_status = '" + BmoPolicy.STATUS_LATE + "'";
		pmConn.doFetch(sql);
		pmConn.next();
		
		// Revisar si hay algun retraso, para fijar el estatus de la poliza como con retraso
		if (pmConn.getInt(1) > 0) status = BmoPolicy.STATUS_LATE;
		else {
			// Investigar si hay pagos pendientes
			sql = " SELECT COUNT(popa_status) FROM policypayments "
					+ " WHERE popa_policyid = " + policyId + ""
					+ " AND popa_status = '" + BmoPolicy.STATUS_PENDING + "'";
			pmConn.doFetch(sql);
			pmConn.next();
			if (pmConn.getInt(1) > 0) status = BmoPolicy.STATUS_PENDING;
			// No hay pagos pendientes o retrasados, por lo tanto esta pagado
			else status = BmoPolicy.STATUS_PAID;
		}
		
		System.out.println("El estatus es: " + status);
		
		return status;
	}
}
