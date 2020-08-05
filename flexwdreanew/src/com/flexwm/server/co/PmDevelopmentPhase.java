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
import java.util.Iterator;
import java.util.ListIterator;
import com.flexwm.server.fi.PmBudget;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoBudget;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

import com.flexwm.shared.wf.BmoWFlow;


/**
 * @author smuniz
 *
 */

public class PmDevelopmentPhase extends PmObject{
	BmoDevelopmentPhase bmoDevelopmentPhase;

	public PmDevelopmentPhase(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoDevelopmentPhase = new BmoDevelopmentPhase();
		setBmObject(bmoDevelopmentPhase);			
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de empresas del usuario en Desarrollos
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters += 	" ( dvph_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " ) "
					+ " ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " dvph_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoDevelopmentPhase());				
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)bmObject;
		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoDevelopmentPhase.getId() > 0)) {
			super.save(pmConn, bmoDevelopmentPhase, bmUpdateResult);
			newRecord = true;
		}

		//Crear el presupuesto
		if (!(bmoDevelopmentPhase.getBudgetId().toInteger() > 0)) {
			createBudget(pmConn, bmoDevelopmentPhase, bmUpdateResult);
		}

		// Crea el WFlow y asigna el ID recien creado
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		bmoDevelopmentPhase.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoDevelopmentPhase.getWFlowTypeId().toInteger(), bmoDevelopmentPhase.getWFlowId().toInteger(), 
				bmoDevelopmentPhase.getProgramCode(), bmoDevelopmentPhase.getId(), bmoDevelopmentPhase.getUserId().toInteger(), bmoDevelopmentPhase.getCompanyId().toInteger(), -1,
				bmoDevelopmentPhase.getCode().toString(), bmoDevelopmentPhase.getName().toString(), bmoDevelopmentPhase.getDescription().toString(), 
				bmoDevelopmentPhase.getStartDate().toString(), bmoDevelopmentPhase.getEndDate().toString(), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId());


		super.save(pmConn, bmoDevelopmentPhase, bmUpdateResult);

		if(!newRecord){
			//Actualizar clave de la etapa en viviendas SOLO en caso de que la clave de la etapa haya sido cambiada
			BmoDevelopmentPhase bmoDevelopmentPhasePrev = new BmoDevelopmentPhase();
			PmDevelopmentPhase pmDevelopmentPhasePrev = new PmDevelopmentPhase(getSFParams());
			bmoDevelopmentPhasePrev = (BmoDevelopmentPhase)pmDevelopmentPhasePrev.get(bmoDevelopmentPhase.getId());

			if(!(bmoDevelopmentPhase.getCode().toString().equals(bmoDevelopmentPhasePrev.getCode().toString())))
				recalculateProperties(pmConn, bmoDevelopmentPhase, bmUpdateResult);

			super.save(pmConn, bmoDevelopmentPhase, bmUpdateResult);
		}


		return bmUpdateResult;
	}

	public void recalculateProperties(PmConn pmConn, BmoDevelopmentPhase bmoDevelopmentPhase, BmUpdateResult bmUpdateResult) throws SFException {

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(getSFParams());     
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProperty.getKind(), bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId().getName(), bmoDevelopmentPhase.getId());
		Iterator<BmObject> propertyList = pmProperty.list(bmFilter).iterator();	    
		while (propertyList.hasNext()) { 
			bmoProperty = (BmoProperty)propertyList.next();
			pmProperty.save(pmConn, bmoProperty, bmUpdateResult);
		}
	}

	private void createBudget(PmConn pmConn, BmoDevelopmentPhase bmoDevelopmentPhase, BmUpdateResult bmUpdateResult) throws SFException {

		PmBudget pmBudget = new PmBudget(getSFParams());
		BmoBudget bmoBudget = new BmoBudget();

		//Si no existe el presupuesto crear uno nuevo

		
		bmoBudget.getName().setValue(bmoDevelopmentPhase.getName().toString());
		bmoBudget.getDescription().setValue("Presupuesto de la fase de desarrollo " + bmoDevelopmentPhase.getName().toString());			
		bmoBudget.getStatus().setValue(BmoBudget.STATUS_REVISION);
		bmoBudget.getUserId().setValue(bmoDevelopmentPhase.getUserId().toInteger());
		bmoBudget.getTotal().setValue(0);

		pmBudget.save(pmConn, bmoBudget, bmUpdateResult);	     

		bmoDevelopmentPhase.getBudgetId().setValue(bmUpdateResult.getId());
	}

	//Actualizar el numero de viviendas de la etapa
	public void updateNoProperties(PmConn pmConn, BmoDevelopmentPhase bmoDevelopmentPhase, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int noProperties = 0;
		sql = " SELECT COUNT(*) AS properties FROM properties " +
				" LEFT JOIN developmentblocks ON (prty_developmentblockid = dvbl_developmentblockid) " +	
				" WHERE dvbl_developmentphaseid = " + bmoDevelopmentPhase.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			noProperties = pmConn.getInt("properties");
		}

		bmoDevelopmentPhase.getNumberProperties().setValue(noProperties);

		super.save(pmConn, bmoDevelopmentPhase, bmUpdateResult);

	}

	public boolean existCodeDvph(PmConn pmConn, String code) throws SFException {
		String sql = "";
		boolean result = false;

		sql = " SELECT dvph_code FROM developmentphases " +
				" WHERE dvph_code LIKE '" + code + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) result = true;

		return result;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)bmObject;

		super.delete(pmConn, bmoDevelopmentPhase, bmUpdateResult);

		// Elimina centros de costos ligados
		pmConn.doUpdate("DELETE FROM venturecostcenters WHERE vecc_venturecostcenterid = " + bmoDevelopmentPhase.getVentureCostCenterId().toInteger());

		if (!bmUpdateResult.hasErrors()) {
			// Eliminar flujos
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = new BmoWFlow();
			BmFilter filterByDevelopmentPhase = new BmFilter();
			filterByDevelopmentPhase.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoDevelopmentPhase.getId());			
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoDevelopmentPhase.getProgramCode());
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterByDevelopmentPhase);
			filterList.add(filterWFlowCategory);
			ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
			while (wFlowList.hasNext()) {
				bmoWFlow = (BmoWFlow)wFlowList.next();
				pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}
}
