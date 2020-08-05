
package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.co.PmWorkContract;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author jhernandez
 *
 */

public class PmWorkContractProperty extends PmObject {
	BmoWorkContractProperty bmoWorkContractProperty;

	public PmWorkContractProperty(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWorkContractProperty = new BmoWorkContractProperty();
		setBmObject(bmoWorkContractProperty);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWorkContractProperty.getWorkContractId(), bmoWorkContractProperty.getBmoWorkContract()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getWFlowId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getWFlowTypeId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), 
						bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getWFlowFunnelId(), 
						bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getWFlowPhaseId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getWorkId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBudgetItemId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getCurrencyId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoBudgetItem().getBmoCurrency()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getCompanyId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoCompany()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getUserId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoUser()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getAreaId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getLocationId(), bmoWorkContractProperty.getBmoWorkContract().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getSupplierId(), bmoWorkContractProperty.getBmoWorkContract().getBmoSupplier()),
				new PmJoin(bmoWorkContractProperty.getBmoWorkContract().getBmoSupplier().getSupplierCategoryId(), bmoWorkContractProperty.getBmoWorkContract().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoWorkContractProperty.getPropertyId(), bmoWorkContractProperty.getBmoProperty()),
				new PmJoin(bmoWorkContractProperty.getBmoProperty().getDevelopmentBlockId(), bmoWorkContractProperty.getBmoProperty().getBmoDevelopmentBlock()),
				new PmJoin(bmoWorkContractProperty.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoWorkContractProperty.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase())				
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWorkContractProperty bmoWorkContractProperty = (BmoWorkContractProperty)autoPopulate(pmConn, new BmoWorkContractProperty());

		// BmoProperty
		BmoProperty bmoProperty = new BmoProperty();
		int propertyId = (int)pmConn.getInt(bmoProperty.getIdFieldName());
		if (propertyId > 0) bmoWorkContractProperty.setBmoProperty((BmoProperty) new PmProperty(getSFParams()).populate(pmConn));
		else bmoWorkContractProperty.setBmoProperty(bmoProperty);

		// BmoWorkContract
		BmoWorkContract bmoWorkContract = new BmoWorkContract();
		int workContractId = (int)pmConn.getInt(bmoWorkContract.getIdFieldName());
		if (workContractId > 0) bmoWorkContractProperty.setBmoWorkContract((BmoWorkContract) new PmWorkContract(getSFParams()).populate(pmConn));
		else bmoWorkContractProperty.setBmoWorkContract(bmoWorkContract);

		return bmoWorkContractProperty;
	}

	// Determina si la vivienda pertenece al contrato
	public boolean userInWorkContract(PmConn pmConn, int workContractId, int propertyId) throws SFException {
		pmConn.doFetch("SELECT * FROM workcontractproperties WHERE wcpr_propertyid = " + propertyId + ""
				+ " AND wcpr_workcontractid = " + workContractId);
		return pmConn.next();
	}

	// Si el contrato solo tiene una vivienda, lo regresa
	public int onlyPropertyInWorkContract(PmConn pmConn, int workContractId) throws SFException {
		int propertyId = -1;
		int count = 0;

		pmConn.doFetch("SELECT COUNT(*) as c FROM workcontractproperties WHERE wcpr_workcontractid = " + workContractId);
		pmConn.next();
		count = pmConn.getInt("c");

		if (count == 1) {
			pmConn.doFetch("SELECT * FROM workcontractproperties WHERE wcpr_workcontractid = " + workContractId);
			if (pmConn.next())
				propertyId = pmConn.getInt("wcpr_propertyid");
		}

		return propertyId;
	}
}
