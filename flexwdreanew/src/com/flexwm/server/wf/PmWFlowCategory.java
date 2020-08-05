/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ListIterator;

import com.symgae.server.PmObject;
import com.flexwm.server.wf.PmWFlowCategoryProfile;
import com.flexwm.server.wf.PmWFlowPhase;
import com.flexwm.server.wf.PmWFlowType;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;


public class PmWFlowCategory extends PmObject {
	BmoWFlowCategory bmoWFlowCategory;

	public PmWFlowCategory(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowCategory = new BmoWFlowCategory();
		setBmObject(bmoWFlowCategory);
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";

		//Solo para G100
		if(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) {
			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
				filters ="(" + 
						"		(" + 
						"			wfca_wflowcategoryid IN  " + 
						"            ( " + 
						"				SELECT wfcc_wflowcategoryid FROM wflowcategorycompanies WHERE wfcc_companyid IN " +
						"					(" +
						" 						SELECT uscp_companyid FROM usercompanies where uscp_userid = " + getSFParams().getLoginInfo().getUserId() +
						" 					) " + 
						"			) " + 
						"        ) " + 
						//					"        OR " + 
						//					"        ( " + 
						//					"			NOT EXISTS (SELECT wfcc_companyid FROM wflowcategorycompanies  " + 
						//					"							WHERE wfca_wflowcategoryid =  wfcc_wflowcategoryid) " + 
						//					"		) " + 
						"	)";
			}


			if (getSFParams().getSelectedCompanyId() > 0) {
				if (filters.length() > 0) filters += " AND ";

				filters += " ( "
						+ "		( wfca_wflowcategoryid IN "
						+ "			("
						+ " 		SELECT wfcc_wflowcategoryid FROM wflowcategorycompanies "
						+ " 		WHERE wfcc_companyid = " + getSFParams().getSelectedCompanyId()
						+ "			)"
						+ "		)"
						//					+ " OR "
						//					+ "		( "
						//					+ "			NOT EXISTS (SELECT wfcc_companyid FROM wflowcategorycompanies "
						//					+ "			WHERE wfca_wflowcategoryid = wfcc_wflowcategoryid)"
						//					+ "		)"
						+ ")";
			}

			//		filters += "		( "
			//				+ "			NOT EXISTS (SELECT wfcc_companyid FROM wflowcategorycompany "
			//				+ "			WHERE wfca_wflowcategoryid = wfcc_wflowcategoryid)"
			//				+ "		)";
		}
		return filters;

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoWFlowCategory());
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowCategory = (BmoWFlowCategory)bmObject;

		// Eliminar tipos de flujos	
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterByWFlowCategory = new BmFilter();
		filterByWFlowCategory.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getWFlowCategoryId(), bmoWFlowCategory.getId());
		ListIterator<BmObject> wFlowTypeList = pmWFlowType.list(pmConn, filterByWFlowCategory).listIterator();
		while (wFlowTypeList.hasNext()) {
			BmoWFlowType bmoCurrentWFlowCategoryDep = (BmoWFlowType)wFlowTypeList.next();
			pmWFlowType.delete(pmConn, bmoCurrentWFlowCategoryDep, bmUpdateResult);
		}

		// Eliminar grupos ligados
		PmWFlowCategoryProfile pmWFlowGroup = new PmWFlowCategoryProfile(getSFParams());
		BmoWFlowCategoryProfile bmoWFlowGroup = new BmoWFlowCategoryProfile();
		filterByWFlowCategory = new BmFilter();
		filterByWFlowCategory.setValueFilter(bmoWFlowGroup.getKind(), bmoWFlowGroup.getWFlowCategoryId(), bmoWFlowCategory.getId());
		ListIterator<BmObject> wFlowGroupList = pmWFlowGroup.list(pmConn, filterByWFlowCategory).listIterator();
		while (wFlowGroupList.hasNext()) {
			BmoWFlowCategoryProfile bmoCurrentWFlowGroup = (BmoWFlowCategoryProfile)wFlowGroupList.next();
			pmWFlowGroup.delete(pmConn, bmoCurrentWFlowGroup, bmUpdateResult);
		}

		// Eliminar fases ligados
		PmWFlowPhase pmWFlowPhase = new PmWFlowPhase(getSFParams());
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		filterByWFlowCategory = new BmFilter();
		filterByWFlowCategory.setValueFilter(bmoWFlowPhase.getKind(), bmoWFlowPhase.getWFlowCategoryId(), bmoWFlowCategory.getId());
		ListIterator<BmObject> wFlowPhaseList = pmWFlowPhase.list(pmConn, filterByWFlowCategory).listIterator();
		while (wFlowPhaseList.hasNext()) {
			BmoWFlowPhase bmoCurrentWFlowPhase = (BmoWFlowPhase)wFlowPhaseList.next();
			pmWFlowPhase.delete(pmConn, bmoCurrentWFlowPhase, bmUpdateResult);
		}

		// Eliminar categoria
		super.delete(pmConn, bmoWFlowCategory, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoWFlowCategory bmoWFlowCategory = (BmoWFlowCategory)bmObject;		
		boolean isNewRecord = false;
		int companyId = 0;
		if(!(bmoWFlowCategory.getId() > 0))
			isNewRecord = true;
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) && isNewRecord) {
			if (bmoWFlowCategory.getCompanyId().toInteger() > 0) {
				companyId = bmoWFlowCategory.getCompanyId().toInteger();
			} else {
				bmUpdateResult.addError(bmoWFlowCategory.getCompanyId().getName(), "Seleccione una empresa");
			}


		}
		super.save(pmConn, bmoWFlowCategory, bmUpdateResult);

		if (!bmUpdateResult.hasErrors() && isNewRecord && (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {
			BmoWFlowCategoryCompany bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();

			bmoWFlowCategoryCompany.getWflowCategoryId().setValue(bmUpdateResult.getId());
			bmoWFlowCategoryCompany.getCompanyId().setValue(companyId);

			new PmWFlowCategoryCompany(getSFParams()).save(bmoWFlowCategoryCompany, new BmUpdateResult());
		}
		return bmUpdateResult;
	}
}
