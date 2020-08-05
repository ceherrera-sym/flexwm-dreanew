
package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoBudgetItemType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmBudgetItemType extends PmObject {
	BmoBudgetItemType bmoTypeCostCenter;

	public PmBudgetItemType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoTypeCostCenter = new BmoBudgetItemType();
		setBmObject(bmoTypeCostCenter);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoBudgetItemType());
	}

	//	@Override
	//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
	//		bmoTypeCostCenter = (BmoTypeCostCenter)bmObject;
	//		
	//		// Se almacena de forma preliminar para asignar ID y la Clave
	//		if (!(bmoTypeCostCenter.getId() > 0)) {
	//			super.save(pmConn, bmoTypeCostCenter, bmUpdateResult);
	//			bmoTypeCostCenter.setId(bmUpdateResult.getId());
	//			bmoTypeCostCenter.getCode().setValue(bmoTypeCostCenter.getCodeFormat());
	//		}
	//		
	//		// Modifica clave 
	//		if (bmoTypeCostCenter.getParentId().toInteger() > 0) {
	//			
	//		}
	//		
	//		super.save(pmConn, bmoTypeCostCenter, bmUpdateResult);
	//	
	//		return bmUpdateResult;
	//	}
}
