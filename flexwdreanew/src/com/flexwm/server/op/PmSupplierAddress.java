/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmCity;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCity;
import com.flexwm.shared.op.BmoSupplierAddress;


public class PmSupplierAddress extends PmObject {
	BmoSupplierAddress bmoSupplierAddress;

	public PmSupplierAddress(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoSupplierAddress = new BmoSupplierAddress();
		setBmObject(bmoSupplierAddress);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSupplierAddress.getCityId(), bmoSupplierAddress.getBmoCity()),
				new PmJoin(bmoSupplierAddress.getBmoCity().getStateId(), bmoSupplierAddress.getBmoCity().getBmoState()),
				new PmJoin(bmoSupplierAddress.getBmoCity().getBmoState().getCountryId(), bmoSupplierAddress.getBmoCity().getBmoState().getBmoCountry())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSupplierAddress = (BmoSupplierAddress) autoPopulate(pmConn, new BmoSupplierAddress());

		// BmoCity
		BmoCity bmoCity = new BmoCity();
		int cityId = (int)pmConn.getInt(bmoCity.getIdFieldName());
		if (cityId > 0) bmoSupplierAddress.setBmoCity((BmoCity) new PmCity(getSFParams()).populate(pmConn));
		else bmoSupplierAddress.setBmoCity(bmoCity);

		return bmoSupplierAddress;
	}
}
