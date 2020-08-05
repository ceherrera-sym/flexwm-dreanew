/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoExternalSales;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmExternalSales extends PmObject {
	BmoExternalSales bmoExternalSales;

	public PmExternalSales(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoExternalSales = new BmoExternalSales();
		setBmObject(bmoExternalSales);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoExternalSales.getCustomerId(),bmoExternalSales.getBmoCustomer()),
				new PmJoin(bmoExternalSales.getBmoCustomer().getTerritoryId(), bmoExternalSales.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoExternalSales.getBmoCustomer().getSalesmanId(), bmoExternalSales.getBmoUser()),
				new PmJoin(bmoExternalSales.getBmoCustomer().getReqPayTypeId(), bmoExternalSales.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoExternalSales.getProductId(), bmoExternalSales.getBmoProduct()),
				new PmJoin(bmoExternalSales.getBmoProduct().getProductGroupId(), bmoExternalSales.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoExternalSales.getBmoProduct().getProductFamilyId(), bmoExternalSales.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoExternalSales.getBmoProduct().getUnitId(), bmoExternalSales.getBmoProduct().getBmoUnit()),
				new PmJoin(bmoExternalSales.getBmoUser().getAreaId(), bmoExternalSales.getBmoUser().getBmoArea()),
				new PmJoin(bmoExternalSales.getBmoUser().getLocationId(), bmoExternalSales.getBmoUser().getBmoLocation())					
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {

		return autoPopulate(pmConn, new BmoExternalSales());

	}
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoExternalSales bmoExternalSales = (BmoExternalSales)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoExternalSales.getId() > 0)) {
			super.save(pmConn, bmoExternalSales, bmUpdateResult);
			bmoExternalSales.setId(bmUpdateResult.getId());

			// Establecer clave si no esta asignada
			if (bmoExternalSales.getCode().toString().equals("")) bmoExternalSales.getCode().setValue(bmoExternalSales.getCodeFormat());
		}




		super.save(pmConn, bmoExternalSales, bmUpdateResult);

		return bmUpdateResult;
	}
}
