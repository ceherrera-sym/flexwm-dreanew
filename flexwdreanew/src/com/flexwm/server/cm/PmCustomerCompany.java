/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.cm.BmoCustomerStatus;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.op.BmoOrder;


public class PmCustomerCompany extends PmObject {
	BmoCustomerCompany bmoCustomerCompany;

	public PmCustomerCompany(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerCompany = new BmoCustomerCompany();
		setBmObject(bmoCustomerCompany);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerCompany.getCompanyId(), bmoCustomerCompany.getBmoCompany()), 
				new PmJoin(bmoCustomerCompany.getCustomerId(), bmoCustomerCompany.getBmoCustomer()),
				new PmJoin(bmoCustomerCompany.getBmoCustomer().getSalesmanId(), bmoCustomerCompany.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoCustomerCompany.getBmoCustomer().getBmoUser().getAreaId(), bmoCustomerCompany.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomerCompany.getBmoCustomer().getBmoUser().getLocationId(), bmoCustomerCompany.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoCustomerCompany.getBmoCustomer().getTerritoryId(), bmoCustomerCompany.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoCustomerCompany.getBmoCustomer().getReqPayTypeId(), bmoCustomerCompany.getBmoCustomer().getBmoReqPayType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCustomerCompany bmoCustomerCompany = (BmoCustomerCompany)autoPopulate(pmConn, new BmoCustomerCompany());

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		if ((int)pmConn.getInt(bmoCustomer.getIdFieldName()) > 0) 
			bmoCustomerCompany.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else 
			bmoCustomerCompany.setBmoCustomer(bmoCustomer);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if ((int)pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoCustomerCompany.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoCustomerCompany.setBmoCompany(bmoCompany);

		return bmoCustomerCompany;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserCompanyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();

		// Filtro de productos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		cucp_companyid IN "
					+ "			( "
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ "			) "
					+ " 	OR cucp_companyid = " + loggedUserCompanyId
					+ " )";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		cucp_companyid IN "
					+ "			("
					+ "				SELECT uscp_companyid FROM usercompanies"
					+ "				WHERE uscp_userid = " + loggedUserId
					+ " 	OR cucp_companyid = " + loggedUserCompanyId
					+ "			)"
					+ " )";
		}

		return filters;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerCompany bmoCustomerCompany = (BmoCustomerCompany)bmObject;
		super.save(pmConn, bmoCustomerCompany, bmUpdateResult);
		
		//Al insertar una empresa al cliente se inserta un status de cliente por empresa
		BmoCustomerStatus bmoCustomerStatus = new BmoCustomerStatus();
		PmCustomerStatus pmCustomerStatus = new PmCustomerStatus(getSFParams());
		//boolean hasStatusCompany =false;
		
		BmFilter filterByCust = new BmFilter();
		filterByCust.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCustomerId(), bmoCustomerCompany.getCustomerId().toInteger());			
		BmFilter filterByCompany = new BmFilter();
		filterByCompany.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCompanyId(), bmoCustomerCompany.getCompanyId().toInteger());
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		filterList.add(filterByCust);
		filterList.add(filterByCompany);
		Iterator<BmObject> customerStatusIterator = pmCustomerStatus.list(filterList).iterator();
		// Si no existe el estatus de la empresa lo crea 
		if (!(customerStatusIterator.hasNext())) {
			bmoCustomerStatus.getCustomerId().setValue(bmoCustomerCompany.getCustomerId().toInteger());
			bmoCustomerStatus.getCompanyId().setValue(bmoCustomerCompany.getCompanyId().toInteger());
			pmCustomerStatus.save(pmConn,bmoCustomerStatus, bmUpdateResult);
			super.save(pmConn, bmoCustomerCompany, bmUpdateResult);
		}
		return bmUpdateResult;
	}
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCustomerCompany = (BmoCustomerCompany) bmObject;
		bmoCustomerCompany = (BmoCustomerCompany) this.get(bmoCustomerCompany.getId());
		BmoCustomerStatus bmoCustomerStatus = new BmoCustomerStatus();
		PmCustomerStatus pmCustomerStatus = new PmCustomerStatus(getSFParams());
		
		BmFilter filterByCust = new BmFilter();
		filterByCust.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCustomerId(), bmoCustomerCompany.getCustomerId().toInteger());			
		BmFilter filterByCompany = new BmFilter();
		filterByCompany.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCompanyId(), bmoCustomerCompany.getCompanyId().toInteger());
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		filterList.add(filterByCust);
		filterList.add(filterByCompany);
		Iterator<BmObject> customerStatusIterator = pmCustomerStatus.list(filterList).iterator();

		if (customerStatusIterator.hasNext()) {
			bmoCustomerStatus = (BmoCustomerStatus) customerStatusIterator.next();
			
			BmoOrder bmoOrder = new BmoOrder();
			PmOrder pmOrder= new PmOrder(getSFParams());
			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			PmOpportunity pmOpportunity= new PmOpportunity(getSFParams());
			
			//Busca si tiene pedidos u oportunidades, si existen no elimina el estatus de la empresa 
			boolean hasWflow = false;
			filterByCust.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), bmoCustomerCompany.getCustomerId().toInteger());			
			filterByCompany.setValueFilter(bmoOrder.getKind(), bmoOrder.getCompanyId(), bmoCustomerCompany.getCompanyId().toInteger());
			ArrayList<BmFilter> filterOrderList = new ArrayList<BmFilter>();
			filterOrderList.add(filterByCust);
			filterOrderList.add(filterByCompany);
			Iterator<BmObject> orderIterator = pmOrder.list(filterOrderList).iterator();

			if (orderIterator.hasNext()) {
				hasWflow = true;
			}
			else {
				filterByCust.setValueFilter(bmoOpportunity.getKind(), bmoOpportunity.getCustomerId(), bmoCustomerCompany.getCustomerId().toInteger());			
				filterByCompany.setValueFilter(bmoOpportunity.getKind(), bmoOpportunity.getCompanyId(), bmoCustomerCompany.getCompanyId().toInteger());
				ArrayList<BmFilter> filterOpportunityList = new ArrayList<BmFilter>();
				filterOpportunityList.add(filterByCust);
				filterOpportunityList.add(filterByCompany);
				Iterator<BmObject> opportunityIterator = pmOpportunity.list(filterOpportunityList).iterator();
				if (opportunityIterator.hasNext()) {
					hasWflow = true;
				}
			}
			// Si no tiene flujos elimina el estatus de la empresa
			if(!hasWflow)
				pmCustomerStatus.delete(pmConn,bmoCustomerStatus, bmUpdateResult);
		}		
		super.delete(pmConn, bmoCustomerCompany, bmUpdateResult);
		return bmUpdateResult;
	}
	// Determina si el usuario pertenece a la empresa
	public boolean customerInCompany(PmConn pmConn, int companyId, int customerId) throws SFException {
		pmConn.doFetch("SELECT * FROM customercompanies WHERE cucp_customerid = " + customerId + ""
				+ " AND cucp_companyid = " + companyId);
		return pmConn.next();
	}
}
