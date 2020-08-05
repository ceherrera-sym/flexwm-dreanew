package com.flexwm.server.fi;

import com.flexwm.shared.fi.BmoRateOrFee;
import com.flexwm.shared.fi.BmoTax;
import com.symgae.server.PmObject;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.shared.fi.BmoFactorType;
import com.symgae.shared.sf.BmoCompany;

public class PmRateOrFee extends PmObject {
	BmoRateOrFee bmoRateOrFee;
	public PmRateOrFee(SFParams sFParams) throws SFPmException {
		super(sFParams);		
		bmoRateOrFee = new BmoRateOrFee();
		setBmObject(bmoRateOrFee);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRateOrFee.getTaxId(), bmoRateOrFee.getBmoTax()),
				new PmJoin(bmoRateOrFee.getFactorTypeId(), bmoRateOrFee.getBmoFactorType()),
				new PmJoin(bmoRateOrFee.getCompanyId(), bmoRateOrFee.getBmoCompany())				
				))); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoRateOrFee bmoRateOrFee = (BmoRateOrFee) autoPopulate(pmConn, new BmoRateOrFee());

		// BmoTax
		BmoTax bmoTax = new BmoTax();
		int taxId = (int)pmConn.getInt(bmoTax.getIdFieldName());
		if (taxId > 0) bmoRateOrFee.setBmoTax((BmoTax) new PmTax(getSFParams()).populate(pmConn));
		else bmoRateOrFee.setBmoTax(bmoTax);

		BmoFactorType bmoFactorType = new BmoFactorType();
		int factorTypeId = (int)pmConn.getInt(bmoFactorType.getIdFieldName());
		if (factorTypeId > 0) bmoRateOrFee.setBmoFactorType((BmoFactorType) new PmFactorType(getSFParams()).populate(pmConn));
		else bmoRateOrFee.setBmoFactorType(bmoFactorType);

		BmoCompany bmoCompany = new BmoCompany();
		int companyId = (int)pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoRateOrFee.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoRateOrFee.setBmoCompany(bmoCompany);


		return bmoRateOrFee;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoRateOrFee bmoRateOrFee = (BmoRateOrFee)bmObject;
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		boolean newRecord = false;
		String code = "";

		if (!(bmoRateOrFee.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoRateOrFee, bmUpdateResult);
			bmoRateOrFee.setId(bmUpdateResult.getId());

			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoRateOrFee.getCompanyId().toInteger(),
					bmoRateOrFee.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoRateOrFee.CODE_PREFIX
					);
			bmoRateOrFee.getCode().setValue(code);
		}
		// Actualizar id de claves del programa por empresa
		if (newRecord && !bmUpdateResult.hasErrors()) {
			pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoRateOrFee.getCompanyId().toInteger(), 
					bmoRateOrFee.getProgramCode().toString());
		}
		super.save(pmConn, bmoRateOrFee, bmUpdateResult);
		
		return bmUpdateResult;

	}



}
