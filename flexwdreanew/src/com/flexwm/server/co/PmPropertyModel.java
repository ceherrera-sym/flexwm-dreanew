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
import java.util.Iterator;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.flexwm.shared.co.BmoPropertyType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


/**
 * @author smuniz
 *
 */

public class PmPropertyModel extends PmObject{
	BmoPropertyModel bmoPropertyModel;


	public PmPropertyModel(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPropertyModel = new BmoPropertyModel();
		setBmObject(bmoPropertyModel); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertyModel.getDevelopmentId(), bmoPropertyModel.getBmoDevelopment()),
				new PmJoin(bmoPropertyModel.getPropertyTypeId(), bmoPropertyModel.getBmoPropertyType()),
				new PmJoin(bmoPropertyModel.getBmoPropertyType().getOrderTypeId(),bmoPropertyModel.getBmoPropertyType().getBmoOrderType())
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
		bmoPropertyModel = (BmoPropertyModel)autoPopulate(pmConn, new BmoPropertyModel());		

		//BmoDevelopment
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		int developmentId = (int)pmConn.getInt(bmoDevelopment.getIdFieldName());
		if (developmentId > 0) bmoPropertyModel.setBmoDevelopment((BmoDevelopment) new PmDevelopment(getSFParams()).populate(pmConn));
		else bmoPropertyModel.setBmoDevelopment(bmoDevelopment);
		
		//BmoPropertyType
		BmoPropertyType bmoPropertyType = new BmoPropertyType();
		int propertyTypeid = (int) pmConn.getInt(bmoPropertyType.getIdFieldName());
		if (propertyTypeid > 0)
			bmoPropertyModel.setBmoPropertyType((BmoPropertyType) new PmPropertyType(getSFParams()).populate(pmConn));
		else
			bmoPropertyModel.setBmoPropertyType(bmoPropertyType);

		return bmoPropertyModel;
	}

	public BmoPropertyModelPrice getCurrentPrice(PmConn pmConn, BmoPropertyModel bmoPropertyModel, BmUpdateResult bmUpdateResult) throws SFException {
		// Obten el precio del modelo
		PmPropertyModelPrice pmPropertyModelPrice = new PmPropertyModelPrice(getSFParams());
		BmoPropertyModelPrice bmoPropertyModelPrice = new BmoPropertyModelPrice();

		int propertyModelPriceId = 0;
		String sql = "SELECT * FROM propertymodelprices "
				+ " WHERE prmp_propertymodelid = " + bmoPropertyModel.getId() + " "
				+ " AND prmp_startdate <= '" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' "
				+ " ORDER BY prmp_startdate DESC";
		pmConn.doFetch(sql);
		if (pmConn.next()) propertyModelPriceId = pmConn.getInt(1);

		if (propertyModelPriceId > 0) {
			bmoPropertyModelPrice = (BmoPropertyModelPrice)pmPropertyModelPrice.get(pmConn, propertyModelPriceId);
		} else {
			bmUpdateResult.addError(bmoPropertyModel.getCode().getName(), "No esta asignado el Precio al Modelo del Inmueble.");
		}	

		return bmoPropertyModelPrice;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)bmObject;
		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoPropertyModel.getId() > 0)) {
			newRecord = true;
			// Establecer clave si no esta asignada
			if (bmoPropertyModel.getCode().toString().equals("")) bmoPropertyModel.getCode().setValue(bmoPropertyModel.getCodeFormat());
		}

		super.save(pmConn, bmoPropertyModel, bmUpdateResult);

		recalculateProperties(pmConn, bmoPropertyModel, bmUpdateResult);

		if(!newRecord) {
			// Actualizar clave del modelo de vivienda SOLO en caso de que la clave del modelo haya sido cambiada
			BmoPropertyModel bmoPropertyModelPrev = new BmoPropertyModel();
			PmPropertyModel pmPropertyModelPrev = new PmPropertyModel(getSFParams());
			bmoPropertyModelPrev = (BmoPropertyModel)pmPropertyModelPrev.get(bmoPropertyModel.getId());

			if(!(bmoPropertyModel.getCode().toString().equals(bmoPropertyModelPrev.getCode().toString())))
				recalculateProperties(pmConn, bmoPropertyModel, bmUpdateResult);
		}
		super.save(pmConn, bmoPropertyModel, bmUpdateResult);

		return bmUpdateResult;
	}


	public void recalculateProperties(PmConn pmConn, BmoPropertyModel bmoPropertyModel, BmUpdateResult bmUpdateResult) throws SFException {

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(getSFParams());     
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProperty.getKind(), bmoProperty.getPropertyModelId(), bmoPropertyModel.getId());
		Iterator<BmObject> propertyList = pmProperty.list(bmFilter).iterator();	    
		while (propertyList.hasNext()) { 
			bmoProperty = (BmoProperty)propertyList.next();
			pmProperty.save(pmConn, bmoProperty, bmUpdateResult);
		}
	}

	public boolean existCodePtym(PmConn pmConn,String code) throws SFException {
		String sql = "";
		boolean result = false;		

		sql = " SELECT ptym_code FROM propertymodels " +
				" WHERE ptym_code LIKE '" + code + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) result = true;

		return result;
	}
}

