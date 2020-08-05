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

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


public class PmDevelopmentBlock extends PmObject{
	BmoDevelopmentBlock bmoDevelopmentBlock;


	public PmDevelopmentBlock(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoDevelopmentBlock = new BmoDevelopmentBlock();
		setBmObject(bmoDevelopmentBlock); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoDevelopmentBlock.getDevelopmentPhaseId(), bmoDevelopmentBlock.getBmoDevelopmentPhase())
				)));
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		
		if(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) {
			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
				filters = "  dvph_companyId IN " +
				"					(" +
				" 						SELECT uscp_companyid FROM usercompanies where uscp_userid = " + getSFParams().getLoginInfo().getUserId() +
				" 					) " ;
				
			}
			
			if (getSFParams().getSelectedCompanyId() > 0) {
				if( filters.length() > 0) filters += " AND ";
				filters += "( ( dvph_companyId = " + getSFParams().getSelectedCompanyId() + "  ) )";
			}
		}
		return filters;
	}
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoDevelopmentBlock = (BmoDevelopmentBlock)autoPopulate(pmConn, new BmoDevelopmentBlock());	

		// BmoDevelopmentPhaseBlock
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		int developmentId = (int)pmConn.getInt(bmoDevelopmentPhase.getIdFieldName());
		if (developmentId > 0) bmoDevelopmentBlock.setBmoDevelopmentPhase((BmoDevelopmentPhase) new PmDevelopmentPhase(getSFParams()).populate(pmConn));
		else bmoDevelopmentBlock.setBmoDevelopmentPhase(bmoDevelopmentPhase);

		return bmoDevelopmentBlock;
	}

	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoDevelopmentBlock = (BmoDevelopmentBlock)bmObject;

		super.save(pmConn, bmoDevelopmentBlock, bmUpdateResult);

		//Actualizar las viviendas
		recalculateProperties(pmConn, bmoDevelopmentBlock, bmUpdateResult);

		return bmUpdateResult;		
	}	


	public void recalculateProperties(PmConn pmConn, BmoDevelopmentBlock bmoDevelopmentBlock, BmUpdateResult bmUpdateResult) throws SFException {

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(getSFParams());     
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProperty.getKind(), bmoProperty.getDevelopmentBlockId().getName(), bmoDevelopmentBlock.getId());
		Iterator<BmObject> propertyList = pmProperty.list(bmFilter).iterator();	    
		while (propertyList.hasNext()) { 
			bmoProperty = (BmoProperty)propertyList.next();
			bmoProperty.getProgress().setValue(bmoDevelopmentBlock.getProcessPercentage().toInteger());
			bmoProperty.getFinishDate().setValue(bmoDevelopmentBlock.getReadyDate().toString());
			pmProperty.save(pmConn, bmoProperty, bmUpdateResult);
		}
	}

	public boolean existCodeDvbl(PmConn pmConn, String code) throws SFException {
		String sql = "";
		boolean result = false;	
		pmConn.open();
		sql = " SELECT dvbl_code FROM developmentblocks " +
				" WHERE dvbl_code LIKE '" + code + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) result = true;		
		return result;
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {

		if (action.equalsIgnoreCase(BmoDevelopmentBlock.ACTION_BATCHUPDATE)) 
			batchUpdate(pmConn, (BmoProperty)bmObject, value, bmUpdateResult);

		return bmUpdateResult;
	}

	// Cambia la propiedad de una venta
	private void batchUpdate(PmConn pmConn, BmoProperty bmoProperty, String developmentBlockId, BmUpdateResult bmUpdateResult) throws SFException {
		PmProperty pmProperty = new PmProperty(getSFParams());
		BmFilter filterByDevelopmentBlock = new BmFilter();
		filterByDevelopmentBlock.setValueFilter(bmoProperty.getKind(), bmoProperty.getDevelopmentBlockId(), developmentBlockId);
		Iterator<BmObject> propertyList = pmProperty.list(pmConn, filterByDevelopmentBlock).iterator();

		while (propertyList.hasNext() && !bmUpdateResult.hasErrors()) {
			BmoProperty bmoCurrentProperty = (BmoProperty)propertyList.next();

			if ((!bmoProperty.getProgress().toString().equals("")))
				bmoCurrentProperty.getProgress().setValue(bmoProperty.getProgress().toInteger());

			if ((!bmoProperty.getHabitability().toString().equals("")))
				bmoCurrentProperty.getHabitability().setValue(bmoProperty.getHabitability().toString());

			if ((!bmoProperty.getFinishDate().toString().equals("")))
				bmoCurrentProperty.getFinishDate().setValue(bmoProperty.getFinishDate().toString());

			super.save(pmConn, bmoCurrentProperty, bmUpdateResult);
		}
	}
}
