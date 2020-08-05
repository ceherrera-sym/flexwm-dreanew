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

import com.flexwm.shared.co.BmoConcept;
import com.flexwm.shared.co.BmoContractConcept;
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

public class PmContractConcept extends PmObject{
	BmoContractConcept bmoContractConcept;
	
	
	public PmContractConcept(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoContractConcept = new BmoContractConcept();
		setBmObject(bmoContractConcept); 
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				/*new PmJoin(bmoContractConcept.getConceptId(), bmoContractConcept.getBmoConcept()),
				new PmJoin(bmoContractConcept.getBmoConcept().getConceptHeadingId(), bmoContractConcept.getBmoConcept().getBmoConceptHeading()),
				new PmJoin(bmoContractConcept.getBmoConcept().getUnitPriceId(), bmoContractConcept.getBmoConcept().getBmoUnitPrice()),
				new PmJoin(bmoContractConcept.getBmoConcept().getBmoUnitPrice().getUnitId(), bmoContractConcept.getBmoConcept().getBmoUnitPrice().getBmoUnit()),
				new PmJoin(bmoContractConcept.getBmoConcept().getBmoUnitPrice().getCurrencyId(), bmoContractConcept.getBmoConcept().getBmoUnitPrice().getBmoCurrency()),*/
				
				new PmJoin(bmoContractConcept.getWorkContractid(), bmoContractConcept.getBmoWorkContract()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getWorkId(), bmoContractConcept.getBmoWorkContract().getBmoWork()),				
				new PmJoin(bmoContractConcept.getBmoWorkContract().getBmoWork().getUserId(), bmoContractConcept.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoContractConcept.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoContractConcept.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getWorktype(), bmoContractConcept.getBmoWorkContract().getBmoConceptGroup()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getSupplierId(), bmoContractConcept.getBmoWorkContract().getBmoSupplier()),
				new PmJoin(bmoContractConcept.getBmoWorkContract().getCompanyId(), bmoContractConcept.getBmoWorkContract().getBmoCompany())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoContractConcept());
		bmoContractConcept = (BmoContractConcept)autoPopulate(pmConn, new BmoContractConcept());
		
		//BmoUnitPrice
		/*BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int unitPricetId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		/*if (unitPricetId > 0) bmoContractConcept.getBmoConcept().setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoContractConcept.getBmoConcept().setBmoUnitPrice(bmoUnitPrice);*/
		
		//BmoConcept
		BmoConcept bmoConcept = new BmoConcept();
		int conceptId = (int)pmConn.getInt(bmoConcept.getIdFieldName());
		if (conceptId > 0) bmoContractConcept.setBmoConcept((BmoConcept) new PmConcept(getSFParams()).populate(pmConn));
		else bmoContractConcept.setBmoConcept(bmoConcept);

		return bmoContractConcept;
		
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoContractConcept bmoContractConcept = (BmoContractConcept)bmObject;
		super.save(pmConn, bmoContractConcept, bmUpdateResult);

		return bmUpdateResult;
	}
	
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoContractConcept bmoContractConcept = (BmoContractConcept) bmObject;
		
		super.delete(pmConn, bmoContractConcept, bmUpdateResult);
		
		return bmUpdateResult;
	}
}

