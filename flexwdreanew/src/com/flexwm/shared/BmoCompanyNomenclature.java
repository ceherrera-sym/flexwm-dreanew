package com.flexwm.shared;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.BmField;


public class BmoCompanyNomenclature extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private BmField acronym,companyId,programId,consecutive,codeFormatDigits;

	private BmoCompany bmoCompany = new BmoCompany();
	private BmoProgram bmoProgram = new BmoProgram();

	public BmoCompanyNomenclature() {
		super("com.flexwm.server.PmCompanyNomenclature", "companynomenclatures", "companynomenclaturesid", "CONO", "Nomenclatura Programas por Empresa");
		acronym = setField("acronym", "", "Clave", 5, Types.VARCHAR, false, BmFieldType.STRING, false);
		companyId = setField("companyid", "", "Empresa", 11, Types.INTEGER, false, BmFieldType.ID, false);
		programId = setField("programid", "", "Programa", 11, Types.INTEGER, false, BmFieldType.ID, false);
		consecutive = setField("consecutive", "0", "Consecutivo", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		codeFormatDigits = setField("codeformatdigits", "0", "Número de ceros", 2, Types.INTEGER, true, BmFieldType.NUMBER, false);
		//		programValueCode = setField("programcode", "", "Codigo de Programa", 4, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName(),
				getBmoProgram().getName(),
				getAcronym(),
				getConsecutive()
				));
	}
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName(),
				getBmoProgram().getName(),
				getAcronym()
				));
	}
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName(),
				getBmoProgram().getName(),
				getAcronym()
				));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCompanyId()),
				new BmSearchField(getProgramId()),
				new BmSearchField(getAcronym())

				));
	}
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}


	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmoProgram getBmoProgram() {
		return bmoProgram;
	}

	public void setBmoProgram(BmoProgram bmoProgram) {
		this.bmoProgram = bmoProgram;
	}

	public BmField getAcronym() {
		return acronym;
	}

	public void setAcronym(BmField acronym) {
		this.acronym = acronym;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getProgramId() {
		return programId;
	}

	public void setProgramId(BmField programId) {
		this.programId = programId;
	}

	public BmField getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(BmField consecutive) {
		this.consecutive = consecutive;
	}

	public BmField getCodeFormatDigits() {
		return codeFormatDigits;
	}

	public void setCodeFormatDigits(BmField codeFormatDigits) {
		this.codeFormatDigits = codeFormatDigits;
	}
}
