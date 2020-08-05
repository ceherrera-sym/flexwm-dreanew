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
import com.flexwm.shared.co.BmoConceptGroup;
import com.flexwm.shared.co.BmoContractConceptGroup;
import com.flexwm.shared.co.BmoWorkContract;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

/**
 * @author smuniz
 *
 */

public class PmContractConceptGroup extends PmObject{
	BmoContractConceptGroup bmoContractConceptGroup;
	
	
	public PmContractConceptGroup(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoContractConceptGroup = new BmoContractConceptGroup();
		setBmObject(bmoContractConceptGroup); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoContractConceptGroup.getWorkContractId(), bmoContractConceptGroup.getBmoWorkContract()),
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getWorkId(), bmoContractConceptGroup.getBmoWorkContract().getBmoWork()),				
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getUserId(), bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoContractConceptGroup.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getSupplierId(), bmoContractConceptGroup.getBmoWorkContract().getBmoSupplier()),
				new PmJoin(bmoContractConceptGroup.getBmoWorkContract().getCompanyId(), bmoContractConceptGroup.getBmoWorkContract().getBmoCompany()),
				new PmJoin(bmoContractConceptGroup.getConceptGroupId(), bmoContractConceptGroup.getBmoConceptGroup())
				)));
		
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoContractConceptGroup = (BmoContractConceptGroup)autoPopulate(pmConn, new BmoContractConceptGroup());		
		
		//BmoWorkContract
		BmoWorkContract bmoWorkContract = new BmoWorkContract();
		int workContractId = (int)pmConn.getInt(bmoWorkContract.getIdFieldName());
		if (workContractId > 0) bmoContractConceptGroup.setBmoWorkContract((BmoWorkContract) new PmWorkContract(getSFParams()).populate(pmConn));
		else bmoContractConceptGroup.setBmoWorkContract(bmoWorkContract);
		
		//BmoConceptGroup
		BmoConceptGroup bmoConceptGroup = new BmoConceptGroup();
		int conceptGroupId = (int)pmConn.getInt(bmoConceptGroup.getIdFieldName());
		if (conceptGroupId > 0) bmoContractConceptGroup.setBmoConceptGroup((BmoConceptGroup) new PmConceptGroup(getSFParams()).populate(pmConn));
		else bmoContractConceptGroup.setBmoConceptGroup(bmoConceptGroup);
				
		return bmoContractConceptGroup;
	}
	
	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoContractConceptGroup bmoContractConceptGroup = (BmoContractConceptGroup)bmObject;

		super.save(pmConn, bmoContractConceptGroup, bmUpdateResult);
		return bmUpdateResult;
	}
}

