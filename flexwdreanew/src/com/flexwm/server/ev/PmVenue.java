/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmCity;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCity;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.shared.ev.BmoVenue;


public class PmVenue extends PmObject {
	BmoVenue bmoVenue;

	public PmVenue(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoVenue = new BmoVenue();
		setBmObject(bmoVenue);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoVenue.getCityId(), bmoVenue.getBmoCity()),
				new PmJoin(bmoVenue.getBmoCity().getStateId(), bmoVenue.getBmoCity().getBmoState()),
				new PmJoin(bmoVenue.getBmoCity().getBmoState().getCountryId(), bmoVenue.getBmoCity().getBmoState().getBmoCountry())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoVenue = (BmoVenue)autoPopulate(pmConn, new BmoVenue());

		// BmoCity
		BmoCity bmoCity = new BmoCity();
		int cityId = (int)pmConn.getInt(bmoCity.getIdFieldName());
		if (cityId > 0) bmoVenue.setBmoCity((BmoCity) new PmCity(getSFParams()).populate(pmConn));
		else bmoVenue.setBmoCity(bmoCity);

		return bmoVenue;
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			bmUpdateResult = this.save(pmConn, bmObject, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) pmConn.commit();			

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoVenue = (BmoVenue)bmObject;
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		boolean newRecord = false;
		int companyId = 0;

		// Es un registro nuevo, recuperar ID y clave
		if (!(bmoVenue.getId() > 0)) {
			String code = "";
			newRecord = true;
			// Es un registro nuevo, guardar una vez para recuperar ID, luego guardar de nuevo con la clave asignada
			super.save(pmConn, bmoVenue, bmUpdateResult);
			bmoVenue.setId(bmUpdateResult.getId());
			companyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();
			
			if (!(companyId > 0))
				companyId = getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger();				
			if (getSFParams().getSelectedCompanyId() > 0)
				companyId = getSFParams().getSelectedCompanyId();
			// Establecer clave si no esta asignada
			if (bmoVenue.getCode().toString().equals("")) {
					code = pmCompanyNomenclature.getCodeCustom(pmConn,
							companyId,
							bmoVenue.getProgramCode().toString(),
							bmUpdateResult.getId(),
							BmoVenue.CODE_PREFIX
							);
					bmoVenue.getCode().setValue(code);
			}
		}

		// Validar si es domicilio personal o no
		if (!(bmoVenue.getHomeAddress().toInteger() > 0)) {
			if (bmoVenue.getStreet().toString().equals(""))
				bmUpdateResult.addError(bmoVenue.getStreet().getName(), "Debe seleccionar la Calle.");
			
			if (bmoVenue.getNumber().equals(""))
				bmUpdateResult.addError(bmoVenue.getNumber().getName(), "Debe seleccionar el Número.");

			if (bmoVenue.getNeighborhood().toString().equals("") )
				bmUpdateResult.addError(bmoVenue.getNeighborhood().getName(), "Debe seleccionar la Colonia.");
			
			if (bmoVenue.getZip().toString().equals(""))
				bmUpdateResult.addError(bmoVenue.getZip().getName(), "Debe seleccionar el Código Postal.");
			
			if (!(bmoVenue.getCityId().toInteger() > 0))
				bmUpdateResult.addError(bmoVenue.getCityId().getName(), "Debe seleccionar la Ciudad.");
		}


		// Actualizar id de claves del programa por empresa
		if (newRecord && !bmUpdateResult.hasErrors()) {
			pmCompanyNomenclature.updateConsecutiveByCompany(pmConn,companyId, 
					bmoVenue.getProgramCode().toString());
		}
		// Almacena el movimiento
		if (!bmUpdateResult.hasErrors()) { 
			super.save(pmConn, bmoVenue, bmUpdateResult);
		}

		return bmUpdateResult;
	}
	// Peticion de Drea (Solo un domicilio particaular por todo el sistema)
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)
			throws SFException {
		if (action.equalsIgnoreCase(BmoVenue.ACTION_EXITSHOMEADDRESS)) {
			BmFilter homeAdresFilter = new BmFilter();
			
			homeAdresFilter.setValueFilter(bmoVenue.getKind(), bmoVenue.getHomeAddress(), 1);
			Iterator<BmObject> objectIterator = this.list(homeAdresFilter).iterator();
			
			if (objectIterator.hasNext()) {
				bmUpdateResult.setId(1);
			}
		}
		
		return bmUpdateResult;
	}
}
