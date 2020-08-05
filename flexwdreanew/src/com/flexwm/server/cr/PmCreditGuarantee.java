/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cr;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.flexwm.shared.cr.BmoCreditType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmCreditGuarantee extends PmObject{
	BmoCreditGuarantee bmoCreditGuarantee;
	BmoCredit bmoCredit;


	public PmCreditGuarantee(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCreditGuarantee = new BmoCreditGuarantee();
		setBmObject(bmoCreditGuarantee);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCreditGuarantee.getCustomerId(), bmoCreditGuarantee.getBmoCustomer()),
				new PmJoin(bmoCreditGuarantee.getBmoCustomer().getSalesmanId(), bmoCreditGuarantee.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoCreditGuarantee.getBmoCustomer().getBmoUser().getAreaId(), bmoCreditGuarantee.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoCreditGuarantee.getBmoCustomer().getBmoUser().getLocationId(), bmoCreditGuarantee.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoCreditGuarantee.getBmoCustomer().getTerritoryId(), bmoCreditGuarantee.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoCreditGuarantee.getBmoCustomer().getReqPayTypeId(), bmoCreditGuarantee.getBmoCustomer().getBmoReqPayType())
				)));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCreditGuarantee = (BmoCreditGuarantee)autoPopulate(pmConn, new BmoCreditGuarantee());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int CustomerId = pmConn.getInt(bmoCustomer.getIdFieldName());
		if (CustomerId > 0) bmoCreditGuarantee.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoCreditGuarantee.setBmoCustomer(bmoCustomer);

		return bmoCreditGuarantee;

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCreditGuarantee bmoCreditGuarantee = (BmoCreditGuarantee)bmObject;

		if (!(bmoCreditGuarantee.getId() > 0)) {			
			super.save(pmConn, bmoCreditGuarantee, bmUpdateResult);
			bmoCreditGuarantee.setId(bmUpdateResult.getId());

			if(!guaranteeCount(pmConn, bmoCreditGuarantee, bmUpdateResult)) 
				bmUpdateResult.addError(bmoCreditGuarantee.getCustomerId().getName(), "Excede el número de avales para el crédito");

			//Validar que el aval no este activo
			if (guaranteeNow(pmConn, bmoCreditGuarantee, bmUpdateResult))
				bmUpdateResult.addError(
						bmoCreditGuarantee.getCustomerId().getName(), "El aval se activo en el crédito " +
								guaranteeNowCredit(pmConn, bmoCreditGuarantee, bmUpdateResult)		
						);



		}	
		if ((creditCustomer(pmConn, bmoCreditGuarantee.getCreditId().toString()) == bmoCreditGuarantee.getCustomerId().toInteger()))
			bmUpdateResult.addMsg("El Cliente no puede ser aval de su propio Credito");

		super.save(pmConn, bmoCreditGuarantee, bmUpdateResult);

		return bmUpdateResult; 
	}	

	public BmUpdateResult createGuarantee(PmConn pmConn, BmoCredit bmoCredit, int guaranteeId, BmUpdateResult bmUpdateResult) throws SFException {

		bmoCreditGuarantee = new BmoCreditGuarantee();
		
		bmoCreditGuarantee.getCreditId().setValue(bmoCredit.getId());
		bmoCreditGuarantee.getCustomerId().setValue(guaranteeId);
		

		this.save(pmConn, bmoCreditGuarantee, bmUpdateResult);

		return bmUpdateResult;

	}


	public boolean guaranteeNow(PmConn pmConn, BmoCreditGuarantee bmoCreditGuarantee, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;

		sql = " SELECT COUNT(crgu_customerid) AS guaranteeNow FROM creditguarantees " +
				" LEFT JOIN credits ON (crgu_creditid = cred_creditid) " +
				" WHERE crgu_customerid = " + bmoCreditGuarantee.getCustomerId().toInteger() +
				" AND (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')" +
				" AND cred_creditid <> " + bmoCreditGuarantee.getCreditId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("guaranteeNow");
		}

		if (items > 0) result = true;

		return result;
	}

	public String guaranteeNowCredit(PmConn pmConn, BmoCreditGuarantee bmoCreditGuarantee, BmUpdateResult bmUpdateResult) throws SFException {		
		String sql = "";
		String credits = "";

		sql = " SELECT * FROM creditguarantees " +
				" LEFT JOIN credits ON (crgu_creditid = cred_creditid) " +
				" LEFT JOIN customers ON (cred_customerid = cust_customerid) " +	
				" WHERE crgu_customerid = " + bmoCreditGuarantee.getCustomerId().toInteger() +
				" AND (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')" +
				" AND cred_creditid <> " + bmoCreditGuarantee.getCreditId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			credits = pmConn.getString("cred_code") + "-" + pmConn.getString("cust_displayname") ;
		}

		return credits;
	}

	public boolean guaranteeCount(PmConn pmConn, BmoCreditGuarantee bmoCreditGuarantee, BmUpdateResult bmUpdateResult) throws SFException {		
		String sql = "";
		int items  = 0;

		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCredit = (BmoCredit)pmCredit.get(pmConn, bmoCreditGuarantee.getCreditId().toInteger());

		PmCreditType pmCreditType = new PmCreditType(getSFParams());
		BmoCreditType bmoCreditType = (BmoCreditType)pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());		

		sql = " SELECT count(*) FROM creditguarantees " +
				" LEFT JOIN credits ON (crgu_creditid = cred_creditid) " +			  	
				" WHERE cred_creditid = " + bmoCreditGuarantee.getCreditId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt(1);
		}

		if (items > bmoCreditType.getGuarantees().toInteger()) return false; 
		else return true;
	}
	public int creditCustomer(PmConn pmConn, String creditId) throws SFException {
		String sql = "SELECT cred_customerid FROM credits  WHERE cred_creditid = " + creditId;
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			return pmConn.getInt("cred_customerid");
		}
		return 0;
		
	}
	

}

