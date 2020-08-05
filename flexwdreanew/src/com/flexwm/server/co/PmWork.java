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
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkItem;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class PmWork extends PmObject{
	BmoWork bmoWork;


	public PmWork(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoWork = new BmoWork();
		setBmObject(bmoWork); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWork.getBudgetItemId(), bmoWork.getBmoBudgetItem()),
				new PmJoin(bmoWork.getBmoBudgetItem().getBudgetId(), bmoWork.getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoWork.getBmoBudgetItem().getCurrencyId(), bmoWork.getBmoBudgetItem().getBmoCurrency()),
				new PmJoin(bmoWork.getBmoBudgetItem().getBudgetItemTypeId(), bmoWork.getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoWork.getCompanyId(), bmoWork.getBmoCompany()),
				new PmJoin(bmoWork.getDevelopmentPhaseId(), bmoWork.getBmoDevelopmentPhase()),
				new PmJoin(bmoWork.getUserId(), bmoWork.getBmoUser()),
				new PmJoin(bmoWork.getBmoUser().getAreaId(), bmoWork.getBmoUser().getBmoArea()),
				new PmJoin(bmoWork.getBmoUser().getLocationId(), bmoWork.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoWork());		
		bmoWork = (BmoWork)autoPopulate(pmConn, new BmoWork());		

		//BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoWork.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoWork.setBmoCompany(bmoCompany);

		//BmoDevelopment
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		int developmentPhaseId = pmConn.getInt(bmoDevelopmentPhase.getIdFieldName());
		if (developmentPhaseId > 0) bmoWork.setBmoDevelopmentPhase((BmoDevelopmentPhase) new PmDevelopmentPhase(getSFParams()).populate(pmConn));
		else bmoWork.setBmoDevelopmentPhase(bmoDevelopmentPhase);

		//BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoWork.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoWork.setBmoUser(bmoUser);

		//Presupuesto
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		int budgetItemId = pmConn.getInt(bmoBudgetItem.getIdFieldName());
		if (budgetItemId > 0) bmoWork.setBmoBudgetItem((BmoBudgetItem) new PmBudgetItem(getSFParams()).populate(pmConn));
		else bmoWork.setBmoBudgetItem(bmoBudgetItem);

		return bmoWork;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoWork bmoWork = (BmoWork)bmObject;

		//Validar si existe un catalago maestro
		/*if (bmoWork.getIsMaster().toBoolean() && masterWorkActive(pmConn, bmUpdateResult)) {
			bmUpdateResult.addError(bmoWork.getCode().getName(), "Ya existe un catalago maestro");
		}*/

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoWork.getId() > 0)) {

			bmoWork.getStatus().setValue(BmoWork.STATUS_REVISION);



			super.save(pmConn, bmoWork, bmUpdateResult);

			//Obtener el Id
			bmoWork.setId(bmUpdateResult.getId());

			// Establecer clave si no esta asignada
			if (bmoWork.getCode().toString().equals("")) bmoWork.getCode().setValue(bmoWork.getCodeFormat());


		}

		//Obtener la Empresa desde la etapa
		if (bmoWork.getDevelopmentPhaseId().toInteger() > 0) {

			PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(getSFParams());
			BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)pmDevelopmentPhase.get(pmConn, bmoWork.getDevelopmentPhaseId().toInteger()); 

			BmoDevelopment bmoDevelopment = new BmoDevelopment();
			PmDevelopment pmDevelopment = new PmDevelopment(getSFParams());
			bmoDevelopment = (BmoDevelopment)pmDevelopment.get(pmConn, bmoDevelopmentPhase.getDevelopmentId().toInteger());

			bmoWork.getCompanyId().setValue(bmoDevelopment.getCompanyId().toInteger());
		}	

		super.save(pmConn, bmoWork, bmUpdateResult);

		if (bmoWork.getIndirects().toDouble() > 0) {
			//Recalcular los PU en los items de los conceptos
			this.setIndirectInWorkItem(pmConn, bmoWork, bmUpdateResult);
		} else {
			this.removeIndirectInWorkItem(pmConn, bmoWork, bmUpdateResult);
		}

		//Actualizar el monto de la obra
		updateAmount(pmConn, bmoWork, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult saveRecalculate(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWork = (BmoWork)bmObject;

		// Es un registro existente, calcular montos y guardar
		if (bmObject.getId() > 0) {
			// Calcular montos
			setAmounts(pmConn, bmoWork, bmUpdateResult);	
		} else {
			// Es un registro nuevo, guardar una vez para recuperar ID, luego guardar de nuevo con la clave asignada
			super.save(pmConn, bmoWork, bmUpdateResult);			
			int workId = bmUpdateResult.getId();
			bmoWork.setId(workId);
			bmoWork.getCode().setValue(bmoWork.getCodeFormat());
		}

		return super.save(pmConn, bmoWork, bmUpdateResult);		
	}

	public void updateAmount(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWork = (BmoWork) bmObject;


		//Sumar los precios unitarios ligados a la obra
		sumUnitPrices(pmConn, bmoWork, bmUpdateResult);


		super.save(pmConn, bmoWork, bmUpdateResult);		

	}

	private void sumUnitPrices(PmConn pmConn, BmoWork bmoWork, BmUpdateResult bmUpdateResult) throws SFException {
		double amount = 0;

		pmConn.doFetch("SELECT sum(wkit_amount) FROM workitems WHERE wkit_workid = " + bmoWork.getId());

		if (pmConn.next()) amount = pmConn.getDouble(1);


		// Calcular montos
		if (amount == 0) {
			//bmoWork.getSubTotal().setValue(0);
			//bmoWork.getIndirectsAmount().setValue(0);
			bmoWork.getTotal().setValue(0);			
		} else {			
			//bmoWork.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			//bmoWork.getIndirectsAmount().setValue(0);			

			bmoWork.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));

		}

	}

	private void setAmounts(PmConn pmConn, BmoWork bmoWork, BmUpdateResult bmUpdateResult) throws SFException {
		double amount = 0;

		pmConn.doFetch("SELECT sum(unpr_total) FROM unitprices WHERE unpr_workid = " + bmoWork.getId());

		if (pmConn.next()) amount = pmConn.getDouble(1);


		// Calcular montos
		if (amount == 0) {			
			bmoWork.getTotal().setValue(0);			
		} else {
			bmoWork.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
		}		
	}

	private void setIndirectInWorkItem(PmConn pmConn, BmoWork bmoWork, BmUpdateResult bmUpdateResult) throws SFException {		
		//Recalcular los items		
		BmoWorkItem bmoWorkItem = new BmoWorkItem();		


		BmFilter filterWorkItems = new BmFilter();
		filterWorkItems.setValueFilter(bmoWorkItem.getKind(), bmoWorkItem.getWorkId(), "" + bmoWork.getId());
		Iterator<BmObject> listWorkItems = new PmWorkItem(getSFParams()).list(pmConn, filterWorkItems).iterator();

		while(listWorkItems.hasNext()) {
			bmoWorkItem = (BmoWorkItem)listWorkItems.next();

			double indirects = bmoWorkItem.getPrice().toDouble() * (bmoWork.getIndirects().toDouble() / 100);
			bmoWorkItem.getPrice().setValue(bmoWorkItem.getPrice().toDouble() + indirects );

			bmoWorkItem.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(
					(bmoWorkItem.getPrice().toDouble() + indirects)  * bmoWorkItem.getQuantity().toDouble()));

			super.save(pmConn, bmoWorkItem, bmUpdateResult);

			this.updateAmount(pmConn, bmoWork, bmUpdateResult);
		}
	}


	private void removeIndirectInWorkItem(PmConn pmConn, BmoWork bmoWork, BmUpdateResult bmUpdateResult) throws SFException {
		//Recalcular los items		
		BmoWorkItem bmoWorkItem = new BmoWorkItem();

		BmFilter filterWorkItems = new BmFilter();
		filterWorkItems.setValueFilter(bmoWorkItem.getKind(), bmoWorkItem.getWorkId(), "" + bmoWork.getId());
		Iterator<BmObject> listWorkItems = new PmWorkItem(getSFParams()).list(pmConn, filterWorkItems).iterator();

		while(listWorkItems.hasNext()) {
			bmoWorkItem = (BmoWorkItem)listWorkItems.next();

			double price = bmoWorkItem.getPrice().toDouble();
			bmoWorkItem.getPrice().setValue(price);

			bmoWorkItem.getAmount().setValue(price *	bmoWorkItem.getQuantity().toDouble());

			super.save(pmConn, bmoWorkItem, bmUpdateResult);

			this.updateAmount(pmConn, bmoWork, bmUpdateResult);
		}
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {		
		bmoWork = (BmoWork)this.get(pmConn, bmObject.getId());

		//Copiar una obra
		if (action.equals(BmoWork.ACTION_COPYWORK)) {
			int fromWorkId = Integer.parseInt(value);
			copyWorkItemsFromWork(pmConn, fromWorkId, bmoWork, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Copiar los items de otra cotización
	public void copyWorkItemsFromWork(PmConn pmConn, int fromWorkId, BmoWork toBmoWork, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener cotización base
		//		PmWork pmWork = new PmWork(getSFParams());
		//		BmoWork fromBmoWork = new BmoWork();
		//		fromBmoWork = (BmoWork)pmWork.get(pmConn, fromWorkId);

		// Filtro de items de la cotización por grupo de cotización
		PmWorkItem pmWorkItem = new PmWorkItem(getSFParams());
		BmoWorkItem fromBmoWorkItem = new BmoWorkItem();
		BmFilter byWorkItemFilter = new BmFilter();
		byWorkItemFilter.setValueFilter(fromBmoWorkItem.getKind(), fromBmoWorkItem.getWorkId().getName(), fromWorkId);

		// Crear los grupos del pedido
		Iterator<BmObject> WorkItemIterator = pmWorkItem.list(pmConn, byWorkItemFilter).iterator();
		while (WorkItemIterator.hasNext()) {
			fromBmoWorkItem = (BmoWorkItem)WorkItemIterator.next();

			BmoWorkItem toBmoWorkItem = new BmoWorkItem();
			toBmoWorkItem.getCode().setValue(fromBmoWorkItem.getCode().toString());			
			toBmoWorkItem.getUnitPriceId().setValue(fromBmoWorkItem.getUnitPriceId().toString());
			toBmoWorkItem.getQuantity().setValue(fromBmoWorkItem.getQuantity().toString());
			toBmoWorkItem.getPrice().setValue(fromBmoWorkItem.getPrice().toString());			
			toBmoWorkItem.getAmount().setValue(fromBmoWorkItem.getAmount().toDouble());			
			toBmoWorkItem.getWorkId().setValue(toBmoWork.getId());

			pmWorkItem.saveSimple(pmConn, toBmoWorkItem, bmUpdateResult);
			// Obten el ultimo ID generado, que es el del grupo de pedido
			//			int toWorkItemId = bmUpdateResult.getId();			
		}

		this.updateAmount(pmConn, toBmoWork, bmUpdateResult);
	}

	//Validar si existe un catalago maestro
	/*private boolean masterWorkActive(PmConn pmConn, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";

		sql = " SELECT COUNT(work_workid) AS master FROM works " +
		      " WHERE work_ismaster = 1 ";
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			if (pmConn.getInt("master") > 0) {
				result = true;
			}
		}

		return result;

	}*/

	//Obtener el catalago maestro
	public BmObject getMasterCatalog(PmConn pmConn) throws SFException {
		BmoWork bmoWork = new BmoWork();

		BmFilter filterMasterWork = new BmFilter();
		filterMasterWork.setValueFilter(bmoWork.getKind(), bmoWork.getIsMaster(), "1");
		Iterator<BmObject> listWork = new PmWork(getSFParams()).list(pmConn, filterMasterWork).iterator();

		if(listWork.hasNext()) {
			bmoWork = (BmoWork)listWork.next();
		}

		return bmoWork;
	}
}

