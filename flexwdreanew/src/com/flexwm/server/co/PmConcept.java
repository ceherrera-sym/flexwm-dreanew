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
import com.flexwm.server.fi.PmBudgetItemType;
import com.flexwm.shared.co.BmoConcept;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmConcept extends PmObject{
	BmoConcept bmoConcept;


	public PmConcept(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoConcept = new BmoConcept();
		setBmObject(bmoConcept); 

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoConcept.getTypeCostCenterId(), bmoConcept.getBmoTypeCostCenter()),
				new PmJoin(bmoConcept.getWorkId(), bmoConcept.getBmoWork()),
				new PmJoin(bmoConcept.getBmoWork().getCompanyId(), bmoConcept.getBmoWork().getBmoCompany()),
				new PmJoin(bmoConcept.getBmoWork().getDevelopmentPhaseId(), bmoConcept.getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoConcept.getBmoWork().getUserId(), bmoConcept.getBmoWork().getBmoUser()),
				new PmJoin(bmoConcept.getBmoWork().getBmoUser().getAreaId(), bmoConcept.getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoConcept.getBmoWork().getBmoUser().getLocationId(), bmoConcept.getBmoWork().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoConcept = (BmoConcept)autoPopulate(pmConn, new BmoConcept());

		//BmoWork
		BmoWork bmoWork = new BmoWork();
		int workId = (int)pmConn.getInt(bmoWork.getIdFieldName());
		if (workId > 0) bmoConcept.setBmoWork((BmoWork) new PmWork(getSFParams()).populate(pmConn));
		else bmoConcept.setBmoWork(bmoWork);

		//BmoTypeCostCenter
		BmoBudgetItemType bmoTypeCostCenter = new BmoBudgetItemType();
		int typeCostCenterId = (int)pmConn.getInt(bmoTypeCostCenter.getIdFieldName());
		if (typeCostCenterId > 0) bmoConcept.setBmoTypeCostCenter((BmoBudgetItemType) new PmBudgetItemType(getSFParams()).populate(pmConn));
		else bmoConcept.setBmoTypeCostCenter(bmoTypeCostCenter);


		return bmoConcept;

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoConcept bmoConcept = (BmoConcept)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoConcept.getId() > 0)) {

			super.save(pmConn, bmoConcept, bmUpdateResult);

			//Obtener el Id
			bmoConcept.setId(bmUpdateResult.getId());

			// Establecer clave si no esta asignada
			if (bmoConcept.getCode().toString().equals("")) bmoConcept.getCode().setValue(bmoConcept.getCodeFormat());

		}

		super.save(pmConn, bmoConcept, bmUpdateResult);

		this.updateAmount(pmConn, bmoConcept, bmUpdateResult);

		super.save(pmConn, bmoConcept, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoConcept bmoConcept = (BmoConcept)bmObject;

		PmWork pmWork = new PmWork(getSFParams());
		BmoWork bmoWork = (BmoWork)pmWork.get(pmConn, bmoConcept.getWorkId().toInteger());

		//Sumar los item de los conceptos
		sumConcepts(pmConn, bmoConcept, bmUpdateResult);

		super.save(pmConn, bmoConcept, bmUpdateResult);

		//Actualizar la Obra
		pmWork.updateAmount(pmConn, bmoWork, bmUpdateResult);

	}

	public void sumConcepts(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoConcept = (BmoConcept)bmObject;

		double amount = 0;

		pmConn.doFetch("SELECT sum(cnci_amount) FROM conceptitems "
				+ " WHERE cnci_conceptid = " + bmoConcept.getId());

		if (pmConn.next()) amount = pmConn.getDouble(1);


		// Calcular montos
		if (amount == 0) {			
			bmoConcept.getSubTotal().setValue(0);
			bmoConcept.getTotal().setValue(0);			
		} else {
			bmoConcept.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));

			if (bmoConcept.getQuantity().toDouble() > 0) {
				amount = bmoConcept.getQuantity().toDouble() * bmoConcept.getSubTotal().toDouble();
			}

			bmoConcept.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));

		}
	}
}

