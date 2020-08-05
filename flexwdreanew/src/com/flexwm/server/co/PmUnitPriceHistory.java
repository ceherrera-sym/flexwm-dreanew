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
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceHistory;

public class PmUnitPriceHistory extends PmObject {
	BmoUnitPriceHistory bmoUnitPriceHistory;
	
	public PmUnitPriceHistory(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoUnitPriceHistory = new BmoUnitPriceHistory();
		setBmObject(bmoUnitPriceHistory);
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoUnitPriceHistory.getUnitPriceId(), bmoUnitPriceHistory.getBmoUnitPrice()),				
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getWorkId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork()),
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getCompanyId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoCompany()),				
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getUnitId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoUnit()),
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getCurrencyId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoCurrency()),
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getUserId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoUser()),
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoUser().getAreaId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoUser().getLocationId(), bmoUnitPriceHistory.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoLocation())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoUnitPriceHistory = (BmoUnitPriceHistory)autoPopulate(pmConn, new BmoUnitPriceHistory());		
		
		//BmoUnitPrice
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int unitPriceId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		if (unitPriceId > 0) bmoUnitPriceHistory.setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoUnitPriceHistory.setBmoUnitPrice(bmoUnitPrice);
		
		return bmoUnitPriceHistory;
	}
	
	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoUnitPriceHistory bmoUnitPriceHistory = (BmoUnitPriceHistory)bmObject;
		/*
		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoUnitPriceHistory.getId() > 0)) {			
			// Establecer clave si no esta asignada
			if (bmoUnitPriceHistory.getCode().toString().equals("")) bmoUnitPriceHistory.getCode().setValue(bmoUnitPriceHistory.getCodeFormat());
		}
		*/
		super.save(pmConn, bmoUnitPriceHistory, bmUpdateResult);
		return bmUpdateResult;
	}
}
