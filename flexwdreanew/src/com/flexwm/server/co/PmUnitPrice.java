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
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.op.PmUnit;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoUnit;
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceHistory;
import com.flexwm.shared.co.BmoUnitPriceItem;
import com.flexwm.shared.co.BmoWork;


public class PmUnitPrice extends PmObject {
	BmoUnitPrice bmoUnitPrice;

	public PmUnitPrice(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoUnitPrice = new BmoUnitPrice();
		setBmObject(bmoUnitPrice);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(								
				new PmJoin(bmoUnitPrice.getWorkId(), bmoUnitPrice.getBmoWork()),
				new PmJoin(bmoUnitPrice.getBmoWork().getCompanyId(), bmoUnitPrice.getBmoWork().getBmoCompany()),
				new PmJoin(bmoUnitPrice.getBmoWork().getDevelopmentPhaseId(), bmoUnitPrice.getBmoWork().getBmoDevelopmentPhase()),
				new PmJoin(bmoUnitPrice.getBmoWork().getBudgetItemId(), bmoUnitPrice.getBmoWork().getBmoBudgetItem()),
				new PmJoin(bmoUnitPrice.getBmoWork().getBmoBudgetItem().getBudgetId(), bmoUnitPrice.getBmoWork().getBmoBudgetItem().getBmoBudget()),
				new PmJoin(bmoUnitPrice.getBmoWork().getBmoBudgetItem().getBudgetItemTypeId(), bmoUnitPrice.getBmoWork().getBmoBudgetItem().getBmoBudgetItemType()),
				new PmJoin(bmoUnitPrice.getUnitId(), bmoUnitPrice.getBmoUnit()),
				new PmJoin(bmoUnitPrice.getCurrencyId(), bmoUnitPrice.getBmoCurrency()),
				new PmJoin(bmoUnitPrice.getBmoWork().getUserId(), bmoUnitPrice.getBmoWork().getBmoUser()),
				new PmJoin(bmoUnitPrice.getBmoWork().getBmoUser().getAreaId(), bmoUnitPrice.getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoUnitPrice.getBmoWork().getBmoUser().getLocationId(), bmoUnitPrice.getBmoWork().getBmoUser().getBmoLocation())
				)));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoUnitPrice());
		bmoUnitPrice = (BmoUnitPrice)autoPopulate(pmConn, new BmoUnitPrice());		

		//BmoWork
		BmoWork bmoWork = new BmoWork();
		int workId = (int)pmConn.getInt(bmoWork.getIdFieldName());
		if (workId > 0) bmoUnitPrice.setBmoWork((BmoWork) new PmWork(getSFParams()).populate(pmConn));
		else bmoUnitPrice.setBmoWork(bmoWork);

		//BmoUnit
		BmoUnit bmoUnit = new BmoUnit();
		int unitId = (int)pmConn.getInt(bmoUnit.getIdFieldName());
		if (unitId > 0) bmoUnitPrice.setBmoUnit((BmoUnit) new PmUnit(getSFParams()).populate(pmConn));
		else bmoUnitPrice.setBmoUnit(bmoUnit);

		//BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoUnitPrice.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoUnitPrice.setBmoCurrency(bmoCurrency);

		return bmoUnitPrice;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoUnitPrice bmoUnitPrice = (BmoUnitPrice)bmObject;

		//Si la categoria es de tipo basico, pedir el precio
		if (bmoUnitPrice.getCategory().toChar() == BmoUnitPrice.CATEGORY_SUPPLIES) {
			if(!(bmoUnitPrice.getTotal().toDouble() > 0)) {
				bmUpdateResult.addError(bmoUnitPrice.getTotal().getName(), "Capture el precio");
			}

			bmoUnitPrice.getSubTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPrice.getTotal().toDouble()));
		}

		// Obtener el catalogo maestro
		BmoWork bmoMasterWork = new BmoWork();
		PmWork pmWork = new PmWork(getSFParams());		
		bmoMasterWork = (BmoWork)pmWork.getMasterCatalog(pmConn);


		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoUnitPrice.getId() > 0)) {

			if (bmoUnitPrice.getWorkId().toInteger() == bmoMasterWork.getId()) {
				//Validar que la clave no exista
				if (isNewCode(pmConn, bmoUnitPrice.getCode().toString(), bmoMasterWork.getId(), bmUpdateResult)) {
					bmUpdateResult.addError(bmoUnitPrice.getCode().getName(), "Ya existe esta clave en el catalago maestro");
				}
			}	

			/*
			// Si no es un registro del catalogo maestro, actualizar el catalogo
			if (bmoUnitPrice.getWorkId().toInteger() != bmoMasterWork.getId()) {		

					//Crear el precio unitario en el catalago maestro				
					BmoUnitPrice bmoUnitPriceNew = new BmoUnitPrice();
					bmoUnitPriceNew.getWorkId().setValue(bmoMasterWork.getId());
					bmoUnitPriceNew.getCode().setValue(bmoUnitPrice.getCode().toString());
					bmoUnitPriceNew.getName().setValue(bmoUnitPrice.getName().toString());
					bmoUnitPriceNew.getDescription().setValue(bmoUnitPrice.getDescription().toString());
					bmoUnitPriceNew.getType().setValue(bmoUnitPrice.getType().toChar());
					bmoUnitPriceNew.getCategory().setValue(bmoUnitPrice.getCategory().toChar());
					bmoUnitPriceNew.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPrice.getTotal().toDouble()));
					bmoUnitPriceNew.getUnitId().setValue(bmoUnitPrice.getUnitId().toInteger());					

					super.save(pmConn, bmoUnitPriceNew, bmUpdateResult);

			}*/

			//Obtener el Id
			super.save(pmConn, bmoUnitPrice, bmUpdateResult);

			bmoUnitPrice.setId(bmUpdateResult.getId());

			//Actualizar el total de los precios unitarios
			//this.updateAmount(pmConn, bmObject, bmUpdateResult);

			super.save(pmConn, bmoUnitPrice, bmUpdateResult);

			if (bmoUnitPrice.getWorkId().toInteger() == bmoMasterWork.getId()) {
				//Obtener la clave anterior del precio unitario
				String oldCode =  "";

				oldCode = getOldCodeUnitPrice(pmConn, bmoUnitPrice.getId());


				// Si es una modificacion, ya no va a permitir borrar
				// porque ya existe en el historial cambios de precios
				if (bmoUnitPrice.getId() > 0 && !(bmoUnitPrice.getDeleteHistory().toString().equals("t"))){
					bmoUnitPrice.getDeleteHistory().setValue("f"); 
				} else {
					bmoUnitPrice.getDeleteHistory().setValue("t");
				}

				// Actualizar el historial del precio unitario
				PmUnitPriceHistory pmUnitPriceHistory = new PmUnitPriceHistory(getSFParams());
				BmoUnitPriceHistory bmoUnitPriceHistory = new BmoUnitPriceHistory();

				bmoUnitPriceHistory.getUnitPriceId().setValue(bmoUnitPrice.getId());
				bmoUnitPriceHistory.getPrice().setValue(bmoUnitPrice.getTotal().toDouble());
				bmoUnitPriceHistory.getDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoUnitPriceHistory.getComments().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams()
						.getDateFormat()) + ", userId " + getSFParams().getLoginInfo().getUserId() +  ", clave anterior: " + oldCode + 
						", nueva clave: " + bmoUnitPrice.getCode().toString());

				pmUnitPriceHistory.save(pmConn, bmoUnitPriceHistory, bmUpdateResult);

			}
		}
        if(bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_AUTHORIZED)) {
        	bmUpdateResult.addMsg("La obra esta autorizada, no se pueden realizar cambios");
        }
		super.save(pmConn, bmoUnitPrice, bmUpdateResult);

		//Actualizar el total de los precios unitarios
		updateAmount(pmConn, bmoUnitPrice, bmUpdateResult);

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmoUnitPrice bmoUnitPrice, BmUpdateResult bmUpdateResult) throws SFException {
		//Sumar los items si es de tipo complex
		if (!bmoUnitPrice.getCategory().equals(BmoUnitPrice.CATEGORY_SUPPLIES)) {
			sumUnitPriceItems(pmConn, bmoUnitPrice, bmUpdateResult);

			//Agregar los indirectos
			if (bmoUnitPrice.getIndirects().toDouble() > 0) {
				bmoUnitPrice.getTotalIndirects().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPrice.getSubTotal().toDouble() * (bmoUnitPrice.getIndirects().toDouble() / 100)));
				bmoUnitPrice.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPrice.getSubTotal().toDouble() + bmoUnitPrice.getTotalIndirects().toDouble()));
			} else {				
				bmoUnitPrice.getTotalIndirects().setValue(0);
				bmoUnitPrice.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoUnitPrice.getSubTotal().toDouble()));
			}	
		}

		// Actualiza datos ligados recursivamente
		updateRecursive(pmConn, bmoUnitPrice, bmUpdateResult);

		super.save(pmConn, bmoUnitPrice, bmUpdateResult);		
	}	

	public void updateRecursive(PmConn pmConn, BmoUnitPrice bmoUnitPrice, BmUpdateResult bmUpdateResult) throws SFException {

		// Genera lista de items de precios unitarios ligados
		BmoUnitPriceItem bmoUnitPriceItem = new BmoUnitPriceItem();
		PmUnitPriceItem pmUnitPriceItem = new PmUnitPriceItem(getSFParams());
		BmFilter linkedItemsFilter = new BmFilter();
		linkedItemsFilter.setValueFilter(bmoUnitPriceItem.getKind(), bmoUnitPriceItem.getUnitPriceId(), bmoUnitPrice.getId());
		Iterator<BmObject> linkedUnitPriceItems = pmUnitPriceItem.list(pmConn, linkedItemsFilter).iterator();

		while (linkedUnitPriceItems.hasNext()) {
			BmoUnitPriceItem bmoCurrentUnitPriceItem = (BmoUnitPriceItem)linkedUnitPriceItems.next();

			bmoCurrentUnitPriceItem.getAmount().setValue(bmoUnitPrice.getTotal().toDouble());
			bmoCurrentUnitPriceItem.getTotal().setValue(bmoUnitPrice.getTotal().toDouble() * 
					bmoCurrentUnitPriceItem.getQuantity().toDouble());

			pmUnitPriceItem.save(pmConn, bmoCurrentUnitPriceItem, bmUpdateResult);
		}		
	}

	public BmUpdateResult saveRecalculate(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoUnitPrice = (BmoUnitPrice)bmObject;

		// Es un registro existente, calcular montos y guardar
		if (bmObject.getId() > 0) {
			// Calcular montos
			setAmounts(pmConn, bmoUnitPrice, bmUpdateResult);	
		} else {
			// Es un registro nuevo, guardar una vez para recuperar ID, luego guardar de nuevo con la clave asignada
			super.save(pmConn, bmoUnitPrice, bmUpdateResult);			
			int unitPriceId = bmUpdateResult.getId();
			bmoUnitPrice.setId(unitPriceId);
			bmoUnitPrice.getCode().setValue(bmoUnitPrice.getCodeFormat());
		}

		super.save(pmConn, bmoUnitPrice, bmUpdateResult);

		if (bmoUnitPrice.getWorkId().toInteger() > 0) {
			PmWork pmWork = new PmWork(getSFParams());
			BmoWork bmoWork = new BmoWork();
			bmoWork = (BmoWork)pmWork.get(pmConn, bmoUnitPrice.getWorkId().toInteger());
			pmWork.saveRecalculate(pmConn, bmoWork, bmUpdateResult);
		}

		return super.save(pmConn, bmoUnitPrice, bmUpdateResult);		
	}

	private void sumUnitPriceItems(PmConn pmConn, BmoUnitPrice bmoUnitPrice, BmUpdateResult bmUpdateResult) throws SFException {
		double total = 0;

		pmConn.doFetch("SELECT sum(unpi_total) FROM unitpriceitems "
				+ " WHERE unpi_unitpriceparentid = " + bmoUnitPrice.getId());

		if (pmConn.next()) total = pmConn.getDouble(1);

		// Calcular montos
		if (total == 0) {			
			bmoUnitPrice.getSubTotal().setValue(0);			
		} else {
			bmoUnitPrice.getSubTotal().setValue(total);
		}
	}

	private void setAmounts(PmConn pmConn, BmoUnitPrice bmoUnitPrice, BmUpdateResult bmUpdateResult) throws SFException {
		double total = 0;

		pmConn.doFetch("SELECT sum(unpi_total) FROM unitpriceitems "
				+ " WHERE unpi_unitpriceparentid = " + bmoUnitPrice.getId());

		if (pmConn.next()) total = pmConn.getDouble(1);

		// Calcular montos
		if (total == 0) {			
			bmoUnitPrice.getTotal().setValue(0);			
		} else {
			bmoUnitPrice.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));
		}
	}

	//Validar si existe la clave en el catalago maestro
	private boolean isNewCode(PmConn pmConn, String newCode, int masterWorkId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";

		sql = " SELECT count(unpr_code) as codes FROM unitprices " +
				" WHERE unpr_code = '" + newCode + "'" + 
				" AND unpr_workid = " + masterWorkId;
		pmConn.doFetch(sql);

		if (pmConn.next()) {		
			if (pmConn.getInt("codes") > 0) {
				result = true;			
			}
		}
		return result;
	}

	public boolean existParent(PmConn pmConn, int unitPriceId) throws SFException {
		String sql = "";
		boolean flag = false;
		sql =  " SELECT * FROM complexunitprices " + 
				" WHERE coup_parentunitpriceid = " + unitPriceId;

		pmConn.doFetch(sql);
		if (pmConn.next()){
			flag = true;
		}
		return flag;
	}

	public double getCalculatedUnitPrice(PmConn pmConn, int unitPriceId) throws SFException {
		String sql = "";

		sql =  " SELECT SUM(coup_quantity * unpr_total) AS price FROM complexunitprices " +
				" INNER JOIN unitprices ON (coup_childunitpriceid = unpr_unitpriceid) " +
				" where coup_parentunitpriceid = " + unitPriceId;
		pmConn.doFetch(sql);
		double price = 0;
		if (pmConn.next()) {
			price = pmConn.getDouble("price");
		}

		return price;
	}

	public void clearUnitPrice(PmConn pmConn, int unitPriceId) throws SFException {
		String sql = "";
		sql = "DELETE FROM complexunitprices WHERE coup_parentunitpriceid = " + unitPriceId;
		pmConn.doUpdate(sql);
	}  

	public String getOldCodeUnitPrice(PmConn pmConn, int unitPriceId) throws SFException {
		String sql = "";
		String oldCode = "";
		sql = " SELECT unpr_code FROM unitprices " +
				" WHERE unpr_unitpriceid = " + unitPriceId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			oldCode = pmConn.getString("unitprices", "unpr_code");
		}

		return oldCode;
	}  

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoUnitPrice = (BmoUnitPrice) bmObject;

		// Validaciones 
		if (bmoUnitPrice.getBmoWork().getStatus().toChar() == BmoWork.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("El P.U.no se puede Eliminar: La obra está Autorizada.");
		} else {						
			// Elimina la CxP
			super.delete(pmConn, bmObject, bmUpdateResult);
		}

		return bmUpdateResult;
	}

}
