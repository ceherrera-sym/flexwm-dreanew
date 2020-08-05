/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


/**
 * @author smuniz
 *
 */

public class PmPropertyModelExtra extends PmObject{
	BmoPropertyModelExtra bmoPropertyModelExtra;

	public PmPropertyModelExtra(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPropertyModelExtra = new BmoPropertyModelExtra();
		setBmObject(bmoPropertyModelExtra); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertyModelExtra.getPropertyModelId(), bmoPropertyModelExtra.getBmoPropertyModel()),
				new PmJoin(bmoPropertyModelExtra.getBmoPropertyModel().getDevelopmentId(), bmoPropertyModelExtra.getBmoPropertyModel().getBmoDevelopment()),
				new PmJoin(bmoPropertyModelExtra.getBmoPropertyModel().getPropertyTypeId(), bmoPropertyModelExtra.getBmoPropertyModel().getBmoPropertyType()),
				new PmJoin(bmoPropertyModelExtra.getBmoPropertyModel().getBmoPropertyType().getOrderTypeId(),bmoPropertyModelExtra.getBmoPropertyModel().getBmoPropertyType().getBmoOrderType())
				)));
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		// Filtro de modelos(desarrollos) de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( deve_companyid IN ("
					+ "		SELECT uscp_companyid FROM usercompanies "
					+ " 	WHERE " 
					+ " 	uscp_userid = " + loggedUserId + " )"
					+ " ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " deve_companyid = " + getSFParams().getSelectedCompanyId();
		}
		
		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoPropertyModelExtra = (BmoPropertyModelExtra)autoPopulate(pmConn, new BmoPropertyModelExtra());

		//BmoPropertyModel
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		int propertyModelId = (int)pmConn.getInt(bmoPropertyModel.getIdFieldName());
		if (propertyModelId > 0) bmoPropertyModelExtra.setBmoPropertyModel((BmoPropertyModel) new PmPropertyModel(getSFParams()).populate(pmConn));
		else bmoPropertyModelExtra.setBmoPropertyModel(bmoPropertyModel);

		return bmoPropertyModelExtra;
	}

}

