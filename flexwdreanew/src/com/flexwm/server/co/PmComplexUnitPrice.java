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
import com.flexwm.shared.co.BmoComplexUnitPrice;
import com.flexwm.shared.co.BmoUnitPrice;
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

public class PmComplexUnitPrice extends PmObject{
	BmoComplexUnitPrice bmoComplexUnitPrice;
	
	
	public PmComplexUnitPrice(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoComplexUnitPrice = new BmoComplexUnitPrice();
		setBmObject(bmoComplexUnitPrice); 
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoComplexUnitPrice.getParentUnitPriceId(), bmoComplexUnitPrice.getBmoUnitPrice()),				
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getWorkId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork()),
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getCompanyId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoCompany()),				
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getUserId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoUser()),
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoUser().getAreaId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoUser().getLocationId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoLocation()),
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getUnitId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoUnit()),
				new PmJoin(bmoComplexUnitPrice.getBmoUnitPrice().getCurrencyId(), bmoComplexUnitPrice.getBmoUnitPrice().getBmoCurrency())
				//new PmJoin(bmoComplexUnitPrice.getParentUnitPriceId(), bmoComplexUnitPrice.getBmoUnitPriceP()),
				//new PmJoin(bmoComplexUnitPrice.getChildUnitPriceId(), bmoComplexUnitPrice.getBmoUnitPriceCh())
		)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new BmoComplexUnitPrice());
		bmoComplexUnitPrice = (BmoComplexUnitPrice)autoPopulate(pmConn, new BmoComplexUnitPrice());		
				
		//BmoUnitPrice()
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int unitPriceId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		if (unitPriceId > 0) bmoComplexUnitPrice.setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoComplexUnitPrice.setBmoUnitPrice(bmoUnitPrice);

		return bmoComplexUnitPrice;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoComplexUnitPrice bmoComplexUnitPrice = (BmoComplexUnitPrice)bmObject;
		super.save(pmConn, bmoComplexUnitPrice, bmUpdateResult);

		return bmUpdateResult;
	}
}

