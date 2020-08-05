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
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceEquipment;
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

public class PmUnitPriceEquipment extends PmObject{
	BmoUnitPriceEquipment bmoUnitPriceEquipement;
	
	
	public PmUnitPriceEquipment(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoUnitPriceEquipement = new BmoUnitPriceEquipment();
		setBmObject(bmoUnitPriceEquipement); 
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoUnitPriceEquipement.getUnitPriceId(), bmoUnitPriceEquipement.getBmoUnitPrice()),				
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getWorkId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork()),
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getCompanyId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoCompany()),				
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getUnitId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoUnit()),
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getCurrencyId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoCurrency()),
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getUserId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoUser()),
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoUser().getAreaId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoArea()),
				new PmJoin(bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoUser().getLocationId(), bmoUnitPriceEquipement.getBmoUnitPrice().getBmoWork().getBmoUser().getBmoLocation())
		)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoUnitPriceEquipement = (BmoUnitPriceEquipment)autoPopulate(pmConn, new BmoUnitPriceEquipment());		
		
		//BmoUnitPrice
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		int developmentTypeId = (int)pmConn.getInt(bmoUnitPrice.getIdFieldName());
		if (developmentTypeId > 0) bmoUnitPriceEquipement.setBmoUnitPrice((BmoUnitPrice) new PmUnitPrice(getSFParams()).populate(pmConn));
		else bmoUnitPriceEquipement.setBmoUnitPrice(bmoUnitPrice);
		
		return bmoUnitPriceEquipement;
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoUnitPriceEquipment bmoUnitPriceEquipement = (BmoUnitPriceEquipment)bmObject;
		/*
		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoDevelopment.getId() > 0)) {			
			// Establecer clave si no esta asignada
			if (bmoDevelopment.getCode().toString().equals("")) bmoDevelopment.getCode().setValue(bmoDevelopment.getCodeFormat());
		}*/

		super.save(pmConn, bmoUnitPriceEquipement, bmUpdateResult);
		return bmUpdateResult;
	}
}

