package com.flexwm.server;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.BmoCompanyNomenclature;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmProgram;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProgram;


public class PmCompanyNomenclature extends PmObject {
	BmoCompanyNomenclature bmoCompanyNomenclature;

	public PmCompanyNomenclature(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoCompanyNomenclature = new BmoCompanyNomenclature();
		setBmObject(bmoCompanyNomenclature);
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCompanyNomenclature.getCompanyId(), bmoCompanyNomenclature.getBmoCompany()),
				new PmJoin(bmoCompanyNomenclature.getProgramId(), bmoCompanyNomenclature.getBmoProgram())		,
				new PmJoin(bmoCompanyNomenclature.getBmoProgram().getMenuId(), bmoCompanyNomenclature.getBmoProgram().getBmoMenu())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCompanyNomenclature bmoCompanyConsecutive = (BmoCompanyNomenclature)autoPopulate(pmConn, new BmoCompanyNomenclature());
		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoCompanyConsecutive.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoCompanyConsecutive.setBmoCompany(bmoCompany);

		//Program
		BmoProgram bmoProgram = new BmoProgram();
		int programId = pmConn.getInt(bmoProgram.getIdFieldName());
		if (programId > 0) bmoCompanyConsecutive.setBmoProgram((BmoProgram) new PmProgram(getSFParams()).populate(pmConn));
		else bmoCompanyConsecutive.setBmoProgram(bmoProgram);

		return bmoCompanyConsecutive;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCompanyNomenclature = (BmoCompanyNomenclature)bmObject;
//		boolean newRecord = false;
		if (!(bmoCompanyNomenclature.getId() > 0)) {
//			newRecord = true;
		}

		if (existNomenclature(pmConn, bmoCompanyNomenclature.getCompanyId().toInteger(), bmoCompanyNomenclature.getProgramCode().toString()))
			bmUpdateResult.addError(bmoCompanyNomenclature.getProgramId().getName(), "Ya existe el " 
					+ bmoCompanyNomenclature.getProgramId().getLabel() + " en la "
					+ bmoCompanyNomenclature.getCompanyId().getLabel() + ".");
		
		super.save(pmConn, bmoCompanyNomenclature, bmUpdateResult);

		return bmUpdateResult;

	}

	//Asigna la clave a los programas
	public String getCodeCustom(PmConn pmConn, int companyId, String programCode, int id, String codePrefix) throws SFException {
		String code = "";
		
		// Generar clave personalizada si la hay en el catalogo, si no retorna la de por defecto
		if (existNomenclature(pmConn, companyId, programCode )) {

			BmoCompanyNomenclature bmoCompanyConsecutive = new BmoCompanyNomenclature();
			// Obtener datos del nomenclatura encontrada
			bmoCompanyConsecutive = getConoByCompanyProgram(pmConn, companyId, programCode);

			printDevLog("--- Crear clave del programa por empresa ---");
			code = FlexUtil.codeFormatDigits(
					bmoCompanyConsecutive.getConsecutive().toInteger() + 1,
					bmoCompanyConsecutive.getCodeFormatDigits().toInteger(),
					bmoCompanyConsecutive.getAcronym().toString() + "-");

			printDevLog("clave_personalizada:"+code);	
		} else {
			code = codePrefix + id;
//			code = FlexUtil.codeFormatDigits(id, 4, codePrefix);
		}
		
		return code;
	}

	private BmoCompanyNomenclature getConoByCompanyProgram(PmConn pmConn, int companyId, String programCode) throws SFException {
		BmoCompanyNomenclature bmoCompanyNomenclature = new BmoCompanyNomenclature();
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());

		String sql = "SELECT cono_companynomenclaturesid "
				+ " FROM " + formatKind("companynomenclatures") 
				+ " LEFT JOIN programs ON (prog_programid = cono_programid) "
				+ " WHERE prog_code = '"+ programCode +"' "
				+ " AND cono_companyid = " + companyId;

		printDevLog("getByCompanyProgram_SQL:"+sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bmoCompanyNomenclature = (BmoCompanyNomenclature)pmCompanyNomenclature.get(pmConn, pmConn.getInt("cono_companynomenclaturesid"));
		}

		return bmoCompanyNomenclature;
	}
	// Actualizar +1 la clave por programa y empresa
	public void updateConsecutiveByCompany(PmConn pmConn, int companyId, String programCode) throws SFException {
		BmoCompanyNomenclature bmoCompanyNomenclature = new BmoCompanyNomenclature();
		// Valida que exista el programa en el catalogo
		if (existNomenclature(pmConn, companyId, programCode )) {
			bmoCompanyNomenclature = getConoByCompanyProgram(pmConn, companyId, programCode);
			pmConn.doUpdate("UPDATE " + formatKind("companynomenclatures") 
			+ " SET cono_consecutive = " + (bmoCompanyNomenclature.getConsecutive().toInteger() + 1)
			+ " WHERE cono_companynomenclaturesid = " + bmoCompanyNomenclature.getId());
		}
	}

	public boolean existNomenclature(PmConn pmConn, int companyId, String programCode) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoCompanyNomenclature.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoCompanyNomenclature.getKind() 
				+ " LEFT JOIN programs ON (prog_programid = cono_programid) " 
				+ " WHERE " + bmoCompanyNomenclature.getCompanyId().getName() + " = " + companyId 
				+ " AND prog_code = '" + programCode + "' ";
		pmConn.doFetch(sql);

		int countExist = 0;
		if(pmConn.next()) countExist = pmConn.getInt("countExist");
			printDevLog("countExist:"+countExist);
			
		if (countExist > 0)
			return true;
		else
			return false;

	}
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCompanyNomenclature = (BmoCompanyNomenclature)bmObject;

		if (bmoCompanyNomenclature.getConsecutive().toInteger() > 0)
			bmUpdateResult.addError(bmoCompanyNomenclature.getCompanyId().getName(), "No se puede borrar el registro, la clave personalizada está en uso.");
		//		PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());

//		pmConn.doUpdate("DELETE FROM " + formatKind("serviceorderreporttimes") 
//		+ " WHERE srrt_serviceorderid = " + bmoCompanyNomenclature.getId());

		// YA NO APLICA
		//		if (pmServiceOrderReportTime.hasServiceOrderReportTime(pmConn, bmoServiceOrder.getId()))
		//			bmUpdateResult.addError(bmoServiceOrder.getCode().getName(), 
		//					"<b>Existen Registros ligados del programa " + getSFParams().getProgramTitle(new BmoServiceOrderReportTime()) + ".</b>");

		// Eliminar SRO
		super.delete(pmConn, bmoCompanyNomenclature, bmUpdateResult);

		return bmUpdateResult;
	}

}
