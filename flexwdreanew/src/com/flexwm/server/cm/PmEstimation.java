///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.PmConn;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.server.sf.PmUser;
//import com.flexwm.server.fi.PmCurrency;
//import com.flexwm.server.op.PmOrderType;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.op.BmoOrderType;
//import com.symgae.shared.sf.BmoCompany;
//
//import com.symgae.shared.sf.BmoUser;
//
//
//
//public class PmEstimation extends PmObject {
//	BmoEstimation bmoEstimation;
//
//	public PmEstimation(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoEstimation = new BmoEstimation();
//		setBmObject(bmoEstimation);
//
//		// Lista de joins
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoEstimation.getCurrencyId(), bmoEstimation.getBmoCurrency()),
//				new PmJoin(bmoEstimation.getOrderTypeId(), bmoEstimation.getBmoOrderType()),
//				new PmJoin(bmoEstimation.getUserId(), bmoEstimation.getBmoUser()),
//				new PmJoin(bmoEstimation.getBmoUser().getAreaId(), bmoEstimation.getBmoUser().getBmoArea()),
//				new PmJoin(bmoEstimation.getBmoUser().getLocationId(), bmoEstimation.getBmoUser().getBmoLocation()),
//				new PmJoin(bmoEstimation.getCustomerId(), bmoEstimation.getBmoCustomer()),
//				new PmJoin(bmoEstimation.getBmoCustomer().getTerritoryId(), bmoEstimation.getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoEstimation.getBmoCustomer().getReqPayTypeId(), bmoEstimation.getBmoCustomer().getBmoReqPayType())
//				)));
//	}
//
//	@Override
//	public String getDisclosureFilters() {
//		String filters = "";
//		int loggedUserId = getSFParams().getLoginInfo().getUserId();
//
//		// Filtro por asignacion de cotizaciones 
//		if (getSFParams().restrictData(bmoEstimation.getProgramCode())) {
//
//			filters = "( ests_userid in (" +
//					" SELECT user_userid FROM users " +
//					" WHERE " + 
//					" user_userid = " + loggedUserId +
//					" or user_userid in ( " +
//					" 	select u2.user_userid from users u1 " +
//					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
//					" where u1.user_userid = " + loggedUserId +
//					" ) " +
//					" or user_userid in ( " +
//					" select u3.user_userid from users u1 " +
//					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
//					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
//					" where u1.user_userid = " + loggedUserId +
//					" ) " +
//					" or user_userid in ( " +
//					" select u4.user_userid from users u1 " +
//					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
//					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
//					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
//					" where u1.user_userid = " + loggedUserId +
//					" ) " +
//					" or user_userid in ( " +
//					" select u5.user_userid from users u1 " +
//					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
//					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
//					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
//					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
//					" where u1.user_userid = " + loggedUserId +
//					" ) " + 
//					" ) " +
//					" ) ";
//		}
//
//		// Filtro de oportunidades de empresas del usuario
//		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
//			if (filters.length() > 0) filters += " AND ";
//			filters += " ( ests_companyid in (" +
//					" select uscp_companyid from usercompanies " +
//					" where " + 
//					" uscp_userid = " + loggedUserId + " )"
//					+ ") ";			
//		}
//
//		// Filtro de cotizaciones seleccionada
//		if (getSFParams().getSelectedCompanyId() > 0) {
//			if (filters.length() > 0) filters += " AND ";
//			filters += " ests_companyid = " + getSFParams().getSelectedCompanyId();
//		}
//
//		return filters;
//	}	
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		bmoEstimation = (BmoEstimation)autoPopulate(pmConn, new BmoEstimation());
//
//		// BmoCurrency
//		BmoCurrency bmoCurrency = new BmoCurrency();
//		int currencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
//		if (currencyId > 0) bmoEstimation.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
//		else bmoEstimation.setBmoCurrency(bmoCurrency);
//
//		// BmoOrderType
//		BmoOrderType bmoOrderType = new BmoOrderType();
//		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
//		if (orderTypeId > 0) bmoEstimation.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
//		else bmoEstimation.setBmoOrderType(bmoOrderType);
//
//		// BmoCustomer
//		BmoCustomer bmoCustomer = new BmoCustomer();
//		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
//		if (customerId > 0) bmoEstimation.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
//		else bmoEstimation.setBmoCustomer(bmoCustomer);
//
//		// BmoUser
//		BmoUser bmoUser = new BmoUser();
//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
//		if (userId > 0) bmoEstimation.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
//		else bmoEstimation.setBmoUser(bmoUser);
//
//		return bmoEstimation;
//	}
//	
//
//	
//
//}
//	