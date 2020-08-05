package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.flexwm.server.wf.PmWFlowCategory;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowCategory;


public class PmOrderTypeWFlowCategory extends PmObject {
	BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory;		

	public PmOrderTypeWFlowCategory(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
		setBmObject(bmoOrderTypeWFlowCategory);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderTypeWFlowCategory.getWFlowCategoryId(), bmoOrderTypeWFlowCategory.getBmoWFlowCategory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = (BmoOrderTypeWFlowCategory) autoPopulate(pmConn, new BmoOrderTypeWFlowCategory());

		// BmoWFlowCategory
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		if (pmConn.getInt(bmoWFlowCategory.getIdFieldName()) > 0) 
			bmoOrderTypeWFlowCategory.setBmoWFlowCategory((BmoWFlowCategory) new PmWFlowCategory(getSFParams()).populate(pmConn));
		else 
			bmoOrderTypeWFlowCategory.setBmoWFlowCategory(bmoWFlowCategory);

		return bmoOrderTypeWFlowCategory;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoOrderTypeWFlowCategory = (BmoOrderTypeWFlowCategory)bmObject;
		
		// Validar de nuevo que no este la misma categoria
		if (existWFlowCategory(pmConn, bmoOrderTypeWFlowCategory.getWFlowCategoryId().toInteger(), bmoOrderTypeWFlowCategory.getOrderTypeId().toInteger() ))
			bmUpdateResult.addError(bmoOrderTypeWFlowCategory.getWFlowCategoryId().getLabel(), "La Categoría de Flujo ya está agregada.");

		super.save(pmConn, bmoOrderTypeWFlowCategory, bmUpdateResult);

		return bmUpdateResult;
	}

	public boolean existWFlowCategory(PmConn pmConn, int wFlowCategoryId, int orderTypeId) throws SFException {
		pmConn.doFetch(" SELECT * FROM " + formatKind("ordertypewflowcategories") 
		+ " WHERE ortw_wflowcategoryid = " + wFlowCategoryId
		+ " AND ortw_ordertypeid = " + orderTypeId);
		return pmConn.next();
	}
}
