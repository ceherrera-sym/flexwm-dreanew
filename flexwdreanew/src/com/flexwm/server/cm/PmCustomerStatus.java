package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import com.flexwm.shared.cm.BmoCustomerStatus;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



public class PmCustomerStatus extends PmObject {
	BmoCustomerStatus bmoCustomerStatus;

	public PmCustomerStatus(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCustomerStatus = new BmoCustomerStatus();
		setBmObject(bmoCustomerStatus);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerStatus.getCompanyId(),bmoCustomerStatus.getBmoCompany())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCustomerStatus bmoCustomerStatus = (BmoCustomerStatus) autoPopulate(pmConn, new BmoCustomerStatus());

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		if (pmConn.getInt(bmoCompany.getIdFieldName()) > 0) 
			bmoCustomerStatus.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else 
			bmoCustomerStatus.setBmoCompany(bmoCompany);

		return bmoCustomerStatus;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";

		// Filtro de clientes de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		( csta_companyid IN "
					+ "			(" 
					+ " 			SELECT cucp_companyid FROM customercompanies " 
					+ " 			WHERE cucp_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + getSFParams().getLoginInfo().getUserId()
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT cucp_customercompanyid FROM customercompanies "
					+ "			WHERE cucp_customerid = csta_customerid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		( csta_companyid IN "
					+ "			("
					+ " 		SELECT cucp_companyid FROM customercompanies "
					+ " 		WHERE cucp_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT cucp_customercompanyid FROM customercompanies "
					+ "			WHERE cucp_customerid = csta_customerid)"
					+ "		)"
					+ ")";
		}
		
		printDevLog("Filtros: "+filters);

		return filters;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerStatus bmoCustomerStatus = (BmoCustomerStatus)bmObject;
		//Si es nuevo  
		if (!(bmoCustomerStatus.getId() > 0)) {
			// Se busca si ya exite el status de cliente con la misma empresa y cliente si no existe lo inserta pero si existe manda mnj de error 
			// Filtro customer
			BmFilter filterCustomer = new BmFilter();
			filterCustomer.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCustomerId(), bmoCustomerStatus.getCustomerId().toInteger());
			// Filtro empresa
			BmFilter filterCompany = new BmFilter();
			filterCompany.setValueFilter(bmoCustomerStatus.getKind(),  bmoCustomerStatus.getCompanyId(),bmoCustomerStatus.getCompanyId().toInteger());

			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterCustomer);
			filterList.add(filterCompany);

			ListIterator<BmObject> customerStatusList = this.list(pmConn, filterList).listIterator();
			if (!customerStatusList.hasNext()) {
				this.saveSimple(pmConn, bmoCustomerStatus, bmUpdateResult);
			}
			else {
				bmUpdateResult.addError(bmoCustomerStatus.getCompanyId().getName(), "El registro ya existe.");
			}
		}

		return bmUpdateResult;
	}


}
