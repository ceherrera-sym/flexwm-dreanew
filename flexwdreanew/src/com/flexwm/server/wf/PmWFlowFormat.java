package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowFormatCompany;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

public class PmWFlowFormat extends PmObject{
	BmoWFlowFormat bmoWFlowFormat;
	public PmWFlowFormat(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoWFlowFormat = new BmoWFlowFormat();		
		setBmObject(bmoWFlowFormat);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowFormat.getWflowTypeId(), bmoWFlowFormat.getBmoWFlowType()),
				new PmJoin(bmoWFlowFormat.getBmoWFlowType().getWFlowCategoryId(), bmoWFlowFormat.getBmoWFlowType().getBmoWFlowCategory())
				
				)));

	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro  de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = " ( "
					+ "		( wfft_wflowformatid IN "
					+ "			(" 
					+ " 			SELECT wffc_wflowformatid FROM wflowformatcompanies " 
					+ " 			WHERE wffc_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + loggedUserId
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT wffc_wflowformatcompanyid FROM wflowformatcompanies "
					+ "			WHERE wffc_wflowformatid = wfft_wflowformatid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";

			filters += " ( "
					+ "		( wfft_wflowformatid IN "
					+ "			("
					+ " 		SELECT wffc_wflowformatid FROM wflowformatcompanies "
					+ " 		WHERE wffc_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT wffc_wflowformatcompanyid FROM wflowformatcompanies "
					+ "			WHERE wffc_wflowformatid = wfft_wflowformatid)"
					+ "		)"
					+ ")";
		}


		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowFormat = (BmoWFlowFormat)autoPopulate(pmConn, new BmoWFlowFormat());

		BmoWFlowType bmoWFlowType = new BmoWFlowType();	
			
			if ((int)pmConn.getInt(bmoWFlowType.getIdFieldName()) > 0) 
				bmoWFlowFormat.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
			else 
				bmoWFlowFormat.setBmoWFlowType(bmoWFlowType);		

		return bmoWFlowFormat;
	}


	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoWFlowFormat = (BmoWFlowFormat)bmObject;
		// Acción de validar la publicació de un formato
		if (action.equals(BmoWFlowFormat.ACTION_PUBLISHVALIDATE)) {
			if (!bmoWFlowFormat.getPublishValidateClass().toString().equals("")){
				try {
					System.out.println("Entro a PmFormat, a abrir la acción.");
					IFWFlowFormatPublishValidator formatPublishValidator = (IFWFlowFormatPublishValidator) Class.forName(bmoWFlowFormat.getPublishValidateClass().toString()).newInstance();
					bmUpdateResult = formatPublishValidator.canPublish(getSFParams(), bmoWFlowFormat, value, bmUpdateResult);
				} catch (InstantiationException e) {
					throw new SFException("PmFormat-action(): ERROR " + e.toString());
				} catch (IllegalAccessException e) {
					throw new SFException("PmFormat-action(): ERROR " + e.toString());
				} catch (ClassNotFoundException e) {
					throw new SFException("PmFormat-action(): ERROR " + e.toString());
				}
			}
		}
		return bmUpdateResult;
	}
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoWFlowFormat bmoWFlowFormat = (BmoWFlowFormat)bmObject;
		boolean isNewRecord = false;
		int companyId = 0;
		

		if(!(bmoWFlowFormat.getId() > 0))
		isNewRecord = true;
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) && isNewRecord) {
			
			if (bmoWFlowFormat.getCompanyId().toInteger() > 0) {
				companyId = bmoWFlowFormat.getCompanyId().toInteger();
			} else {
				bmUpdateResult.addError(bmoWFlowFormat.getCompanyId().getName(), "Seleccione una empresa");
			}
			
		}
		
		super.save(pmConn, bmoWFlowFormat, bmUpdateResult);
		
		if (!bmUpdateResult.hasErrors() && isNewRecord && (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {
			BmoWFlowFormatCompany bmoWFlowFormatCompany = new BmoWFlowFormatCompany();
			
			bmoWFlowFormatCompany.getWflowformatId().setValue(bmUpdateResult.getId());
			bmoWFlowFormatCompany.getCompanyId().setValue(companyId);
			  
			new PmWFlowFormatCompany(getSFParams()).save(bmoWFlowFormatCompany, new BmUpdateResult());
		}
		// TODO Auto-generated method stub
		return bmUpdateResult;
	}

}
