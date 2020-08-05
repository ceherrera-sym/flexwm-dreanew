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
import com.flexwm.shared.co.BmoContractProperty;
import com.flexwm.shared.co.BmoProperty;
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

public class PmContractProperty extends PmObject{
	BmoContractProperty bmoContractProperty;
	
	
	public PmContractProperty(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoContractProperty = new BmoContractProperty();
		setBmObject(bmoContractProperty); 
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoContractProperty.getWorkContractid(), bmoContractProperty.getBmoWorkContract()),
				new PmJoin(bmoContractProperty.getBmoWorkContract().getCompanyId(), bmoContractProperty.getBmoWorkContract().getBmoCompany()),
				new PmJoin(bmoContractProperty.getBmoWorkContract().getSupplierId(), bmoContractProperty.getBmoWorkContract().getBmoSupplier()),				
				new PmJoin(bmoContractProperty.getBmoWorkContract().getWorkId(), bmoContractProperty.getBmoWorkContract().getBmoWork()),				
				new PmJoin(bmoContractProperty.getBmoWorkContract().getBmoWork().getUserId(), bmoContractProperty.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				
				new PmJoin(bmoContractProperty.getPropertyId(), bmoContractProperty.getBmoProperty())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoContractProperty());
		bmoContractProperty = (BmoContractProperty)autoPopulate(pmConn, new BmoContractProperty());
		
		//BmoContractProperty
		BmoWorkContract bmoWorkContract = new BmoWorkContract();
		int workContractId = (int)pmConn.getInt(bmoWorkContract.getIdFieldName());
		if (workContractId > 0) bmoContractProperty.setBmoWorkContract((BmoWorkContract) new PmWorkContract(getSFParams()).populate(pmConn));
		else bmoContractProperty.setBmoWorkContract(bmoWorkContract);
		
		//BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoContractProperty.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoContractProperty.setBmoProperty(bmoProperty);

		return bmoContractProperty;
		
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoContractProperty bmoContractProperty = (BmoContractProperty)bmObject;
		super.save(pmConn, bmoContractProperty, bmUpdateResult);

		return bmUpdateResult;
	}
}

